/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Zephyr framework ("Zephyr").
 *
 * Zephyr is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Zephyr is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Zephyr. If not, see <http://www.gnu.org/licenses/>.
 */


package org.dihedron.zephyr.annotations;

import org.dihedron.zephyr.renderers.impl.JspRenderer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing the view handler that will
 * create the visual representation for the given result.
 * Results can be represented as free text strings, and
 * are mapped to the appropriate view handler by the action
 * controller, based on what's in these annotations.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Result {

    /**
     * The result string for which the mapping to the renderer is
     * being described.
     *
     * @return 
     *   the result string.
     */
    String value() default "success";

    /**
     * The type of renderer; this parameter can contain the name of a registered
     * renderer, e.g. "jsp", which must have been registered. A set of core
     * renderers are registered by default by the framework, while the user can
     * specify some additional ones which must implement the {@code Renderer}
     * interface and be located in a package, as per the portlet's initialisation.
     *
     * @return 
     *   the alias of the renderer.
     */
    String renderer() default JspRenderer.ID;

    /**
     * The data to be passed on to the renderer; in the case of a JSP renderer,
     * this data would be the URL of the JSP page to show, whereas in the case
     * of a JSON or XML renderer, this data would be the name of the variable or
     * of the parameter to be rendered as JSON or XML.
     *
     * @return 
     *   the data to be used by the renderer to provide a meaningful output; in
     *   the case of a JSP renderer, this would typically be the URL of the JSP
     *   to be included, whereas for JSON and XML it would be the name of the
     *   parameter containing the object to be JSON- or XML-encoded.
     */
    String data() default "";
}