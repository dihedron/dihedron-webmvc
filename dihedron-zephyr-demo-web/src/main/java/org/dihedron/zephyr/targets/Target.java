/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.zephyr.targets;

import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.actions.Result;
import org.dihedron.zephyr.annotations.Invocable;
import org.dihedron.zephyr.renderers.impl.JspRenderer;
import org.dihedron.zephyr.targets.registry.TargetRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
     * The method that implements the target's business logic.
     */
    private Method factory;

    /**
     * The method that implements the target's business logic.
     */
    private Method method;

    /**
     * The static proxy method that collects parameters from the various scopes
     * before invoking the actual action's business method; this method's
     * implementation is provided as a stub by the framework, by inspecting the
     * action at bootstrap time and generating bytecode dynamically.
     */
    private Method proxy;

    /**
     * The pattern used to create JSP URLs.
     */
    private String jspUrlPattern = TargetRegistry.DEFAULT_HTML_PATH_PATTERN;

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
     * @param id a reference to the unique identifier of the target whose data are held
     *           by this instance.
     */
    public Target(TargetId id) {
        this.id = id;
    }

    /**
     * Returns the id of the target.
     *
     * @return the id of the target.
     */
    public TargetId getId() {
        return id;
    }

    /**
     * Returns the class object containing the executable code of this target's
     * business logic.
     *
     * @return the class object containing the executable code of this target's
     * business logic.
     */
    public Class<?> getActionClass() {
        return this.action;
    }

    /**
     * Sets the class object containing the executable code of this target's
     * business logic.
     *
     * @param action the class object containing the executable code of this target's
     *               business logic.
     * @return the object itself, for method chaining.
     */
    public Target setActionClass(Class<?> action) {
        this.action = action;
        return this;
    }

    /**
     * Returns the reference to the factory method capable of allocating and
     * instance of the AbstractAction class implementing this target.
     *
     * @return the reference to the containing action's factory method.
     */
    public Method getFactoryMethod() {
        return this.factory;
    }

    /**
     * Sets the reference to the factory method capable of allocating and
     * instance of the AbstractAction class implementing this target.
     *
     * @param method the reference to the containing action's factory method.
     * @return the object itself, for method chaining.
     */
    public Target setFactoryMethod(Method method) {
        this.factory = method;
        return this;
    }

    /**
     * Returns the reference to method implementing this target's business logic.
     *
     * @return the reference to the method implementing this target's business logic.
     */
    public Method getActionMethod() {
        return this.method;
    }

    /**
     * Sets the reference to the method implementing this target's business logic.
     *
     * @param method the reference to the method implementing this target's business logic.
     * @return the object itself, for method chaining.
     */
    public Target setActionMethod(Method method) {
        this.method = method;
        return this;
    }

    /**
     * Returns the static, framework-generated proxy method for the action's
     * business logic method.
     *
     * @return the static proxy method.
     */
    public Method getProxyMethod() {
        return this.proxy;
    }

    /**
     * Sets the reference to the static, framework-generated proxy method for
     * the action's business logic method.
     *
     * @param proxy the static proxy method.
     * @return the object itself, for metod chaining.
     */
    public Target setProxyMethod(Method proxy) {
        this.proxy = proxy;
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
        for (org.dihedron.zephyr.annotations.Result annotation : invocable.results()) {
            addDeclaredResult(annotation);
        }
        logger.trace("... done auto-configuring results of '{}'", id);
    }

    public void addDeclaredResult(org.dihedron.zephyr.annotations.Result annotation) {
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

    public void addUndeclaredResult(String value) {
        String id = value;
        String renderer = DEFAULT_RENDERER;
        String data = makeJspUrl(id);
        logger.trace("adding (auto-configured) result '{}' with data '{}'", id, data);
        Result result = new Result(id, renderer, data);
        this.results.put(id, result);
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
     * result string, or null if none found. If the target belongs to an auto-
     * configured action and no result could be found, the methodName attempts to
     * reconstruct the information and returns it, after having added it to the
     * set of valid results.
     *
     * @param rid a result string (e.g. "success", "error").
     * @return the <code>Result</code> object corresponding to the given result string.
     */
    public Result getResult(String rid) {
        assert (Strings.isValid(rid));
        Result result = results.get(rid);
        if (result == null) {
            logger.trace("result '{}' is not present yet, auto-configuring...", rid);
            addUndeclaredResult(rid);
        }
        return results.get(rid);
    }

    /**
     * Returns a pretty printed, complex representation of the object as a string.
     */
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("target('").append(id.toString()).append("') {\n");
        buffer.append("  action      ('").append(id.getActionName()).append("')\n");
        buffer.append("  method      ('").append(id.getMethodName()).append("')\n");
        buffer.append("  proxy       ('").append(proxy.getName()).append("')\n");
        buffer.append("  url pattern ('").append(this.getJspUrlPattern()).append("')\n");
        buffer.append("  stack       ('").append(interceptors).append("')\n");
        buffer.append("  javaclass   ('").append(action.getCanonicalName()).append("')\n");
        if (!results.isEmpty()) {
            buffer.append("  results {\n");
            for (Entry<String, Result> result : results.entrySet()) {
                buffer.append("    result  ('").append(result.getKey()).append("') { \n");
                buffer.append("      renderer ('").append(result.getValue().getRenderer()).append("')\n");
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
