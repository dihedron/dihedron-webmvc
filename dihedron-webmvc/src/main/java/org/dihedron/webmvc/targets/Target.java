/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 *
 * This file is part of the WebMVC framework ("WebMVC").
 *
 * WebMVC is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * WebMVC is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with WebMVC. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.webmvc.targets;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.actions.Result;
import org.dihedron.webmvc.annotations.Invocable;
import org.dihedron.webmvc.renderers.impl.JspRenderer;
import org.dihedron.webmvc.targets.registry.TargetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Target {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Target.class);

    /**
     * A reference to the unique identifier of the target whose additional data
     * are stored in this object.
     */
    private TargetId id;

    /**
     * The class object of the Action class containing the executable code (the
     * method) implementing the target's business logic.
     */
    private Class<?> action;

    /**
     * The method that implements the target's business logic; this method is not 
     * directly invoked, it will be invoked by the proxy method instead, through
     * generated code.
     */
    private Method method;
    
    /**
     * The method that creates an instance of the proxy class capable of invoking 
     * the business method after having performed all the necessary parameters
     * unmarshalling and injection. 
     */
    private Method actionFactory;

    /**
     * The static proxy method that collects parameters from the various scopes
     * before invoking the actual action's business method; this method's
     * implementation is provided as a stub by the framework, by inspecting the
     * action at bootstrap time and generating bytecode dynamically.
     */
    private Method stubMethod;

    /**
     * The pattern used to create JSP URLs.
     */
    private String jspUrlPattern = TargetRegistry.DEFAULT_JSP_PATH_PATTERN;

    /**
     * The name of the interceptor stack to be used with this action.
     */
    private String interceptors;

    /**
     * The map of expected results.
     */
    private Map<String, Result> results = Collections.synchronizedMap(new HashMap<String, Result>());

    /**
     * Constructor.
     *
     * @param id 
     *   a reference to the unique identifier of the target whose data are held
     *   by this instance.
     */
    public Target(TargetId id) {
        this.id = id;
    }

    /**
     * Returns the id of the target.
     *
     * @return 
     *   the id of the target.
     */
    public TargetId getId() {
        return id;
    }

    /**
     * Returns the class object containing the executable code of this target's
     * business logic.
     *
     * @return 
     *   the class object containing the executable code of this target's
     *   business logic.
     */
    public Class<?> getActionClass() {
        return this.action;
    }

    /**
     * Sets the class object containing the executable code of this target's
     * business logic.
     *
     * @param action 
     *   the class object containing the executable code of this target's
     *   business logic.
     * @return 
     *   the object itself, for method chaining.
     */
    public Target setActionClass(Class<?> action) {
        this.action = action;
        return this;
    }
    
    /**
     * Returns the reference to method implementing this target's business logic;
     * this method will never be invoked directly, the framework will always go
     * through the stub that provides parameters marshalling/unmarshalling, 
     * validation in a non-reflective fashion. 
     *
     * @return 
     *   the reference to the user-provided method implementing this target's 
     *   business logic.
     */
    public Method getActionMethod() {
        return this.method;
    }

    /**
     * Sets the reference to the user-provided method implementing this target's 
     * business logic.
     *
     * @param method 
     *   the reference to the method implementing this target's business logic.
     * @return 
     *   the object itself, for method chaining.
     */
    public Target setActionMethod(Method method) {
        this.method = method;
        return this;
    }    

    /**
     * Returns the reference to the factory method capable of allocating an
     * instance of the concrete user-provided class implementing the actual 
     * business logic.
     *
     * @return 
     *   the reference to the factory method that's used to instantiate the
     *   stub class.
     */
    public Method getActionFactory() {
        return this.actionFactory;
    }

    /**
     * Sets the reference to the factory method capable of allocating an
     * instance of the stub class that will in turn invoke the actual business
     * logic method after having performed its unmarshalling and validation, and 
     * to perform final validation and marshalling once the business method is 
     * done.
     *
     * @param actionFactory 
     *   the reference to the stub factory method.
     * @return 
     *   the object itself, for method chaining.
     */
    public Target setActionFactory(Method actionFactory) {
        this.actionFactory = actionFactory;
        return this;
    }

    /**
     * Returns the static, framework-generated stub method for the action's
     * business logic method.
     *
     * @return 
     *   the static stub method.
     */
    public Method getStubMethod() {
        return this.stubMethod;
    }

    /**
     * Sets the reference to the static, framework-generated proxy method for
     * the action's business logic method.
     *
     * @param stubMethod the static proxy method.
     * @return the object itself, for metod chaining.
     */
    public Target setStubMethod(Method stubMethod) {
        this.stubMethod = stubMethod;
        return this;
    }

    /**
     * Returns the pattern used to create JSP URLs for JSP-rendered results
     * that have not been declared in the annotation.
     *
     * @return the URL pattern used for auto-configured JSP-rendered results.
     */
    public String getJspUrlPattern() {
        return this.jspUrlPattern;
    }

    /**
     * Sets the value of the HTML views path pattern for automagic actions.
     *
     * @param pattern the pattern to be used to JSP path reconstruction at runtime.
     * @return the object itself, for method chaining.
     */
    public Target setJspUrlPattern(String pattern) {
        if (Strings.isValid(pattern)) {
            this.jspUrlPattern = Strings.trim(pattern);
        }
        logger.trace("target '{}' has URL pattern '{}'", id, this.jspUrlPattern);
        return this;
    }

    /**
     * Retrieves the id of the interceptors stack.
     *
     * @return the name of the interceptors stack.
     */
    public String getInterceptorStackId() {
        return interceptors;
    }

    /**
     * Sets the name of the interceptors stack.
     *
     * @param interceptors the name of the interceptors stack.
     * @return the object itself, for method chaining.
     */
    public Target setInterceptorsStackId(String interceptors) {
        if (Strings.isValid(interceptors)) {
            this.interceptors = interceptors;
        }
        logger.trace("target '{}' has interceptors stack '{}'", id, this.interceptors);
        return this;
    }

    public void addDeclaredResults(Invocable invocable) {
        logger.trace("auto-configuring results of '{}'...", id);
        for (org.dihedron.webmvc.annotations.Result annotation : invocable.results()) {
            addDeclaredResult(annotation);
        }
        logger.trace("... done auto-configuring results of '{}'", id);
    }

    public void addDeclaredResult(org.dihedron.webmvc.annotations.Result annotation) {
        String id = annotation.value();
        String renderer = annotation.renderer();
        String data = annotation.data();
        if (!Strings.isValid(data) && renderer.equalsIgnoreCase(JspRenderer.ID)) {
            data = makeJspUrl(id);
            logger.trace("adding result '{}' with (auto-configured) data '{}'", id, data);
        } else {
            logger.trace("adding result '{}' with data '{}'", id, data);
        }
        Result result = new Result(id, renderer, data);
        this.results.put(id, result);

    }

    public Result addUndeclaredResult(String value) {
        String id = value;
        String renderer = DEFAULT_RENDERER;
        String data = makeJspUrl(id);
        logger.trace("adding (auto-configured) result '{}' with data '{}'", id, data);
        Result result = new Result(id, renderer, data);
        this.results.put(id, result);
        return result;
    }

    /**
     * Returns the map of result identifiers and <code>Result</code> objects.
     *
     * @return the map of result identifiers and <code>Result</code> objects.
     */
    public Map<String, Result> getResults() {
        return results;
    }

    /**
     * Returns the <code>Result</code> object corresponding to the given
     * result string, or null if none found.
     *
     * @param resultId 
     *   a result string (e.g. "success", "error").
     * @return 
     *   the <code>Result</code> object corresponding to the given result string, 
     *   or {@code null} if it cannot be found.
     */
    public Result getResult(String resultId) {
        assert (Strings.isValid(resultId));
        logger.trace("retrieving result for id '{}'", resultId);
        return results.get(resultId);
//        if (result == null) {
//            logger.trace("result '{}' is not present yet, auto-configuring...", resultId);
//            addUndeclaredResult(resultId);
//        }
//        return results.get(resultId);
    }

    /**
     * Returns a pretty printed, complex representation of the object as a string.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("target('").append(id.toString()).append("') {\n");
        buffer.append("  action      ('").append(id.getActionName()).append("')\n");
        buffer.append("  method      ('").append(id.getMethodName()).append("')\n");
        buffer.append("  factory     ('").append(actionFactory.getName()).append("')\n");
        buffer.append("  stub        ('").append(stubMethod.getName()).append("')\n");
        buffer.append("  url pattern ('").append(this.getJspUrlPattern()).append("')\n");
        buffer.append("  stack       ('").append(interceptors).append("')\n");
        buffer.append("  javaclass   ('").append(action.getCanonicalName()).append("')\n");
        if (!results.isEmpty()) {
            buffer.append("  results {\n");
            for (Entry<String, Result> result : results.entrySet()) {
                buffer.append("    result  ('").append(result.getKey()).append("') { \n");
                buffer.append("      renderer ('").append(result.getValue().getRendererId()).append("')\n");
                buffer.append("      data     ('").append(result.getValue().getData()).append("')\n");
                buffer.append("    }\n");
            }
            buffer.append("  }\n");
        }
        buffer.append("}\n");
        return buffer.toString();
    }

    /**
     * Creates the path to a JSP for a given target and result combination, according
     * to the input pattern and starting from the given root HTML files directory.
     * Both parameter can be overridden via the initialisation configuration parameters.
     *
     * @param result the target's result.
     * @param mode   the new portlet mode.
     * @param state  the new window state.
     * @return the URL of the JSP-renderered page for the given result.
     */
    private String makeJspUrl(String result) {
        String path = this.jspUrlPattern
                .replaceAll("\\$\\{action\\}", id.getActionName())
                .replaceAll("\\$\\{method\\}", id.getMethodName())
                .replaceAll("\\$\\{result\\}", result);
        logger.debug("path for target: '{}', result: '{}' is '{}'", id, result, path);
        return path;
    }

    /**
     * The default renderer, to be used when no renderer is specified.
     */
    private static final String DEFAULT_RENDERER = JspRenderer.ID;
}
