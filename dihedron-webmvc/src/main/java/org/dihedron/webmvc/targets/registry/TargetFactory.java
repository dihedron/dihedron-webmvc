/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.targets.registry;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.Parameter;
import org.dihedron.webmvc.annotations.Action;
import org.dihedron.webmvc.annotations.Invocable;
import org.dihedron.webmvc.aop.ActionProxy;
import org.dihedron.webmvc.aop.ActionProxyBuilder;
import org.dihedron.webmvc.exceptions.DeploymentException;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.registry.DomainsRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class responsible for loading the actions configuration ("targets"), either
 * from the XML file or from the classpath.
 *
 * @author Andrea Funto'
 */
public class TargetFactory {

    /**
     * The logger.
     */
    private static Logger logger = LoggerFactory.getLogger(TargetFactory.class);
    
    /**
     * The set of action classes that have already been instrumented; this avoids 
     * attempting to create a proxy class twice, resulting in an unrecoverable
     * error and a failure of the overall deployment should the user accidentally 
     * specify the same Java package more than once.
     */
    private Set<Class<?>> instrumentedActions = new HashSet<>();

    /**
     * The object that takes care of inspecting the action and creating proxy
     * class, static factory method and method proxies for each of its 
     * {@code @Invocable} methods.
     */
    private ActionProxyBuilder builder = null;
    
    /**
     * Constructor.
     *
     * @param doValidation
     *   whether the factory should emit JSR-349 validation code in the generated 
     *   proxies.
     */
    public TargetFactory(boolean doValidation) {
    	builder = new ActionProxyBuilder();
    	if(doValidation) {
    		logger.info("the builder will emit code supporting JSR-349 validation");
    		builder.withValidation();
    	} else {
    		logger.info("the builder won't emit code supporting JSR-349 validation");
    		builder.withoutValidation();
    	}
    }

    /**
     * This method performs the automatic scanning of actions at startup time,
     * to make access to actions faster later on. The targets map is pre-populated
     * with information coming from actions configured through annotations.
     *
     * @param registry     
     *   the repository where new targets will be stored.
     * @param domains
     *   the domains registry: this is used to verify that the domains requested by 
     *   the action and/or its methods do actually exist.  
     * @param javaPackage  
     *   the Java package to be scanned for actions.
     * @throws WebMVCException
     */
    public void makeFromJavaPackage(TargetRegistry registry, DomainsRegistry domains, String javaPackage) throws WebMVCException {
    	
        if (Strings.isValid(javaPackage)) {            
            if(!javaPackage.endsWith(".")) {
            	logger.trace("package name is not complete, adding final dot");
            	javaPackage = javaPackage + ".";
            }
            logger.trace("looking for action classes in package '{}'", javaPackage);

            // using this approach because it seems to be consistently faster
            // than the much simpler new Reflections(javaPackage)
            Reflections reflections =
                    new Reflections(new ConfigurationBuilder()
                            .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(javaPackage)))
                            .setUrls(ClasspathHelper.forPackage(javaPackage))
                            .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner()));
            Set<Class<?>> actions = reflections.getTypesAnnotatedWith(Action.class);
            for (Class<?> action : actions) {
            	if(!instrumentedActions.contains(action)) {
            		makeFromJavaClass(registry, domains, action);
            		instrumentedActions.add(action);
            	} else {
            		logger.warn("skipping class '{}' as it is already instrumented: check your configuration for duplicate packages in '{}'", action.getName(), Parameter.ACTIONS_JAVA_PACKAGES.getName());
            	}
            }
        }
    }

    /**
     * Scans the given class for annotated methods and adds them to the registry
     * as targets.
     *
     * @param registry     
     *   the repository where new targets will be stored.
     * @param domains
     *   the interceptors registry: this is used to verify that the interceptors 
     *   stack requested by the action does actually exist.  
     * @param actionClass  
     *   the action class to be scanned for annotated methods (targets).
     * @throws WebMVCException
     */
    public void makeFromJavaClass(TargetRegistry registry, DomainsRegistry domains, Class<?> actionClass) throws WebMVCException {
        logger.trace("analysing action class: '{}'...", actionClass.getName());

        // only add classes that are not abstract to the target registry
        if (!Modifier.isAbstract(actionClass.getModifiers())) {
            logger.trace("class '{}' is not abstract", actionClass.getSimpleName());

            // check that the given domain (id specified) is existing
            String domain = actionClass.getAnnotation(Action.class).domain();
            if(Strings.isValid(domain) && domains.findDomainById(domain) == null) {
            	throw new DeploymentException("Class '" + actionClass.getSimpleName() + "' specifies a non-existing domain: '" + domain + "': check annotation value");
            }

            // let the builder inspect the action and generate a factory method
            // and a set of proxy methods for valid @Invocable-annotated action methods
            // (possibly walking up the class hierarchy and discarding duplicates,
            // static and unannotated methods...)
            ActionProxy proxy = builder.build(actionClass).addActionFactoryMethod().addAllBusinessMethods().getActionProxy();

            // now loop through annotated methods and add them to the registry as targets
            Map<Method, Method> methods = proxy.getStubMethods();
            for (Method actionMethod : methods.keySet()) {
                if (actionMethod.isAnnotationPresent(Invocable.class)) {
                	Invocable invocable = actionMethod.getAnnotation(Invocable.class);
                	
                    // check that if a domain is specified in the method
                	// annotation, it is valid (existing) and then use it
                    if(Strings.isValid(invocable.domain())) {
                    	if(domains.findDomainById(invocable.domain()) == null) {                    
                    		throw new DeploymentException("Method '" + actionMethod.getName() + "' specifies a non-existing domain: '" + invocable.domain() + "': check annotation value");
                    	} else {
                    		domain = invocable.domain();
                    	}
                    }
                	
                    Method proxyMethod = methods.get(actionMethod);
                    logger.trace("... adding annotated method '{}' in class '{}' (proxy: '{}' in class '{}')", actionMethod.getName(), actionClass.getSimpleName(), proxyMethod.getName(), proxy.getProxyClass().getSimpleName());
                    
                    registry.addTarget(actionClass, actionMethod, proxy.getActionFactory(), proxyMethod, invocable, domain);
                } else {
                    logger.trace("... discarding unannotated method '{}' in class '{}'", actionMethod.getName(), actionClass.getSimpleName());
                }
            }
        } else {
            // if the input class is abstract, we skip it altogether: its methods
            // will be made available through its subclasses (if ever)
            logger.info("discarding abstract class '{}'", actionClass.getSimpleName());
        }
        logger.trace("... done analysing action class: '{}'!", actionClass.getName());
    }
}
