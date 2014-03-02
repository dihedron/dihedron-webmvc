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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the field will contain data coming from the given
 * set of parameters, whose name is expressed as regular expression.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Model {

    /**
     * The pattern (regular expression) of the names of the input parameters; it
     * must be specified, and must contain exactly one (no more, no less) capturing
     * group; whatever is captured by the group is taken as the OGNL expression used
     * to store the request parameter value into the model object. You may use
     * this characteristic to filter out any unwanted parts in the name of the
     * parameter. As an example, you may specify a regular expression such as
     * <code>user\\:(.*)</code>, which will only capture the second part of
     * any string in the form "user:address.street" ("address.street" will be
     * captured) or "user:name" ("name" will be captured). In these examples,
     * this will result in <code>&lt;model-object&gt;.getAddress().setStreet()</code>
     * and <code>&lt;model-object&gt;.setName()</code> being called.
     *
     * @return the pattern of the regular expression used to select the parameters
     * whose values will be stored inside the field.
     */
    String value() default "^(?:[^\\:\\[\\]]+)\\:(.+)$";

    /**
     * The scope in which the parameters should be looked up; by default, they
     * are looked up in all available scopes.
     *
     * @return the scope of the parameter.
     */
    Scope[] from() default {Scope.FORM, Scope.REQUEST, Scope.PORTLET, Scope.APPLICATION, /*Scope.HTTP,*/Scope.CONFIGURATION};
}