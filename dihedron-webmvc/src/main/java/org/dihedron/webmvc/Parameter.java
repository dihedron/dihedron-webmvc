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

package org.dihedron.webmvc;

import javax.servlet.FilterConfig;

/**
 * The enumeration of supported initialisation parameters.
 *
 * @author Andrea Funto'
 */
public enum Parameter {

    /**
     * The parameter used to specify the comma-separated list of Java packages
     * where actions are to be located. Each of these packages will be scanned
     * for <code>@Action</code>-annotated classes.
     */
    ACTIONS_JAVA_PACKAGES("zephyr:actions-packages"),
    
    /**
     * A properties file used to initialise values for the actions managed by
     * this <code>ActionController</code>. The path to the file must be expressed
     * as an URL, according to one of the following formats:<ul>
     * <li>classpath:path/to/resource/on/classpath.properties</li>
     * <li>http://server:port/path/to/configuration.properties</li>
     * <li>file://path/to/configuration.properties</li>
     * </ul>.
     */
    ACTIONS_CONFIGURATION("zephyr:actions-configuration"),

    /**
     * The parameter used to specify if JSR-349 bean validation code should be
     * generated to validate input parameters and targets return values; JSR-349
     * support requires a valid implementation of the JavaBean Validation 1.1
     * specification (such as Hibernate Validator) to be available on the class
     * path.
     */
    ACTIONS_ENABLE_VALIDATION("zephyr:enable-validation"),

    /**
     * The parameter used to override the name of the interceptors stack
     * configuration XML file; by default it is called "interceptors-config.xml".
     */
    INTERCEPTORS_DECLARATION("zephyr:interceptors-declaration"),

    /**
     * The parameter used to override the default interceptors stack to be
     * used when invoking non-configured or non-fully-configured actions; by
     * default it is the "default" stack.
     */
    INTERCEPTORS_DEFAULT_STACK("zephyr:interceptors-default-stack"),

    /**
     * The comma-separated list of Java packages where custom renderer classes
     * are looked for, if non null.
     */
    RENDERERS_JAVA_PACKAGES("zephyr:renderers-packages"),
    
    /**
     * The directory in which temporary uploaded files will be stored.
     */
    UPLOADED_FILES_DIRECTORY("zephyr:upload-directory"),

    /**
     * The maximum size of uploaded files; files exceeding this size will not be 
     * accepted.
     */
    UPLOADED_FILES_MAX_FILE_SIZE("zephyr:upload-max-file-size"),

    /**
     * The maximum size of uploaded files; files exceeding this size will not be 
     * accepted.
     */
    UPLOADED_FILES_MAX_REQUEST_SIZE("zephyr:upload-max-request-size"),
    
    /**
     * The maximum size of uploaded files that will be kept in memory; files 
     * smaller than this threshold will be kept in memory, larger ones will be 
     * written out to disk (see {@link #UPLOADED_FILES_DIRECTORY}.
     */
    UPLOADED_SMALL_FILE_SIZE_THRESHOLD("zephyr:upload-small-file-threshold"),

    /**
     * The default page to be shown when an internal error occurs.
     */
    DEFAULT_ERROR_PAGE("zephyr:default-error-page"),

    /**
     * The parameter used to specify the root directory for JSP renderers.
     * This is used only when dealing with annotated actions and smart defaults,
     * to build the name of renderer JSPs based on the action's result.
     */
    JSP_ROOT_PATH("zephyr:jsp-root-path"),

    /**
     * The parameter used to specify the pattern to create the path to JSP
     * pages for auto-configured targets. Accepted variables include:<ul>
     * <li><b>${rootdir}</b>: the root directory, as specified via
     * parameter <code>zephyr:jsp-root-path</code>;</li>
     * <li><b>${action}</b>: the name of the action;<li>
     * <li><b>${method}</b>: the name of the method;<li>
     * <li><b>${result}</b>: the result id of the execution, e.g. "success";<li>
     * </ul>
     */
    JSP_PATH_PATTERN("zephyr:jsp-path-pattern"),

    /**
     * The parameter used to specify an optional set of packages to be scanned
     * for application-server-specific plugins.
     */
    WEB_CONTAINER_PACKAGES("zephyr:web-container-packages"),

    /**
     * The parameter used to specify an optional application-server-specific plugin,
     * which will be used by the framework to retrieve platform-specific data.
     */
    WEB_CONTAINER_PLUGIN("zephyr:web-container-plugin");

    /**
     * Constructor.
     *
     * @param name 
     *   the initialisation parameter name.
     */
    private Parameter(String name) {
        this.name = name;
    }

    /**
     * The string representing the name of the initialisation parameter.
     */
    private String name;

    /**
     * Returns the name of the initialisation parameter.
     *
     * @return 
     *   the name of the initialisation parameter.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the value of the input parameter for the given portlet, base on
     * the current name first, and on the legacy name if no valid value could be
     * found.
     *
     * @param filter 
     *   the servlet filter configuration from which the value will be taken.
     * @return 
     *   the value of the input parameter.
     */
    public String getValueFor(FilterConfig filter) {
        return filter.getInitParameter(name);
    }

    /**
     * Returns the parameter's name and value as a String.
     *
     * @param filter 
     *   the servlet filter configuration from which the value will be taken.
     * @return 
     *   the name and value of the input parameter.
     */
    public String toString(FilterConfig filter) {
        return "'" + getName() + "':='" + getValueFor(filter) + "'";
    }
}
