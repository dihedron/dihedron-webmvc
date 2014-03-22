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
 * Annotation indicating that the field will be filled with input data coming from
 * one of the given scopes, and it will also be used to store output data, mapped 
 * in the appropriate scope under the given parameter name.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface InOut {

    /**
     * The name of the output parameter which will receive the annotated field's
     * value; it must be specified.
     *
     * @return 
     *   the name of the output parameter.
     */
    String value();

    /**
     * The scope in which the parameter should be looked up; by default, it is
     * looked up in all available scopes.
     *
     * @return 
     *   the set of scopes to be scanned for the annotated parameter.
     */
    Scope[] from() default { Scope.FORM, Scope.REQUEST, Scope.SESSION, Scope.STICKY, Scope.APPLICATION, Scope.CONFIGURATION };

    /**
     * The scope into which the parameter should be stored; by default, it is
     * stored among the request attributes, so it is available throughout the rest
     * of the request processing and no more.
     * IMPORTANT: only read/write scopes can be specified here.
     *
     * @return 
     *   the scope into which to set the parameter.
     */
    Scope to() default Scope.REQUEST;
}