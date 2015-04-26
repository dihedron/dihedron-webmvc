/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.targets.registry;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.annotations.Action;
import org.dihedron.webmvc.annotations.Invocable;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.targets.Target;
import org.dihedron.webmvc.targets.TargetId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class containing the set of information available for the pool of supported
 * targets; the information is stored in here by detecting an action's properties 
 * at deployment time; some information (regarding the results for those targets 
 * that let the framework infer the JSP to forward to based on the outcome) may
 * be added directly to the individual targets at runtime, as new outcomes are
 * detected that were not available at deployment time.
 *
 * @author Andrea Funto'
 */
public class TargetRegistry {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(TargetRegistry.class);
    
    /**
     * The default root directory for JSP URLs.
     */
    public static final String DEFAULT_JSP_ROOT_DIR = "";

    /**
     * The default pattern for JSP paths generation, for auto-configured targets.
     */
    public static final String DEFAULT_JSP_PATH_PATTERN = "${rootdir}/${action}/${method}/${result}.jsp";

    /**
     * The actual set of actions' targets.
     */
    private Map<TargetId, Target> store = new HashMap<>();

    /**
     * The root directory to be used to infer the path to result JSPs, based on 
     * the business method outcome when no explicit result is provided via the
     * {@code &at;Invocable} annotation.
     */
    private volatile String jspRootDir = DEFAULT_JSP_ROOT_DIR;

    /**
     * The pattern to be used for inferring JSP pages for auto-configured
     * results. For a thorough discussion on format and accepted variables
     * check out {@link org.dihedron.webmvc.Parameter.JSP_PATH_PATTERN}.
     */
    private volatile String jspPathPattern = DEFAULT_JSP_PATH_PATTERN;

    /**
     * Constructor.
     */
    public TargetRegistry() {
        logger.info("creating target registry");
    }

    /**
     * Sets the information used by the framework to self-configure when there
     * is no explicit URL associated with a JSP-rendered result. This information
     * is made up of two components:<ol>
     * <li> the HTML package (directory) to be used as the base directory
     * for self-configuring business methods lacking result URLs,</li>
     * <li>the pattern used to create JSP URLs; if not specified, URLs will be
     * inferred according to the the following pattern:
     * &lt;root directory&gt;/&lt;action name&gt;/&lt;method name&gt;/&lt;result&gt;.jsp
     * </ol>
     *
     * @param jspRootDir 
     *   the root directory to be used as the starting point for inferred URLs.
     * @param jspPathPattern 
     *   the pattern to be used as a mould for auto-configured targets' JSPs.
     */
    public void setJspPathInfo(String jspRootDir, String jspPathPattern) {
        if (Strings.isValid(jspRootDir)) {
            logger.debug("JSP root directory from configuration: '{}'", jspRootDir);
            this.jspRootDir = jspRootDir;
            if (this.jspRootDir.endsWith("/")) {
                int index = this.jspRootDir.lastIndexOf('/');
                this.jspRootDir = this.jspRootDir.substring(0, index);
            }
        }
        if (Strings.isValid(jspPathPattern)) {
            logger.debug("JSP path pattern from configuration: '{}'", jspPathPattern);
            this.jspPathPattern = jspPathPattern;
        }

        this.jspPathPattern = this.jspPathPattern.replaceAll("\\$\\{rootdir\\}", this.jspRootDir);

        logger.info("root directory for synthetic result URLs: '{}'", this.jspRootDir);
        logger.info("pattern for auto-configured targets' JSPs: '{}'", this.jspPathPattern);
    }

    /**
     * Registers a new target (as a {@code TargetId}, {@code Target} pair) into 
     * the registry.
     *
     * @param targetClass
     *   the user-provided class implementing the target's business logic.
     * @param targetMethod  
     *   the user-provided method implementing the target's business logic.   
     * @param stubFactoryMethod 
     *   the factory method that will instantiate the stub class implementing the 
     *   business method using synthetic code instead of reflection.
     * @param stubMethod
     *   the 
     * @param invocable     the method annotation, from which some information might be extracted.
     * @param interceptors  the name of the interceptor stack to be used for the given action.
     * @throws WebMVCException
     */
    public void addTarget(Class<?> targetClass, Method targetMethod, Method stubFactoryMethod, Method stubMethod,
                          Invocable invocable, String interceptors) throws WebMVCException {
        String actionName = Strings.isValid(targetClass.getAnnotation(Action.class).alias()) ? targetClass.getAnnotation(Action.class).alias() : targetClass.getSimpleName();
        logger.info("adding target '{}!{}' (proxy: '{}')", actionName, targetMethod.getName(), stubMethod.getName());
        TargetId id = new TargetId(targetClass, targetMethod);

        // instantiate the information object
        Target data = new Target(id);
        data.setActionClass(targetClass);
        data.setActionFactory(stubFactoryMethod);
        data.setActionMethod(targetMethod);
        data.setStubMethod(stubMethod);
//        data.setIdempotent(invocable.idempotent());
        data.setInterceptorsStackId(interceptors);
        data.setJspUrlPattern(jspPathPattern);
        data.addDeclaredResults(invocable);
        this.store.put(id, data);
    }

    /**
     * Retrieves the {@code Target} object corresponding to the given target
     * identifier.
     *
     * @param id the target identifier.
     * @return the {@code Target} object; if none found in the registry, an
     * exception is thrown.
     * @throws WebMVCException if no @{code Target} object could be found for the given id.
     */
    public Target getTarget(TargetId id) throws WebMVCException {
        if (!store.containsKey(id)) {
            logger.debug("repository does not contain info for target '{}'", id);
            throw new WebMVCException("Invalid target : '" + id.toString() + "'");
        }
        return store.get(id);
    }

    /**
     * Returns the @{code Target} corresponding to the action and method,
     * as expressed in the given target string.
     *
     * @param target a string representing the action, with or without the method being invoked
     *               on it; thus it can be the action name (in which case the default method
     *               "execute" is assumed) or the full target ("MyAction!myMethod").
     * @return the @{code Target} object corresponding to the given combination
     * of action name and method name, if found. If no @{code Target} can
     * be found an exception is thrown.
     * @throws WebMVCException if the string is not a valid target or no target can be found corresponding
     *                            to it.
     */
    public Target getTarget(String target) throws WebMVCException {
        return getTarget(new TargetId(target));
    }

    /**
     * Returns the @{code Target} corresponding to the action and method,
     * as expressed in the given target string.
     *
     * @param action a string representing the action (e.g. "MyAction").
     * @param method a string representing the method (e.g. "myMethod").
     * @return the @{code Target} object corresponding to the given combination
     * of action name and method name, if found. If no @{code Target} can
     * be found an exception is thrown.
     * @throws WebMVCException if the string is not a valid target or no target can be found corresponding
     *                            to it.
     */
    public Target getTarget(String action, String method) throws WebMVCException {
        return getTarget(new TargetId(action, method));
    }

    /**
     * Provides a string representation of the registry.
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder("");

        if (!store.isEmpty()) {
            for (Entry<TargetId, Target> entry : store.entrySet()) {
                buffer.append("----------- TARGET -----------\n");
                buffer.append(entry.getValue().toString());
            }
            buffer.append("------------------------------\n");
        }
        return buffer.toString();
    }
}
