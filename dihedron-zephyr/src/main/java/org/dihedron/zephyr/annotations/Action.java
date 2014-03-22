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
 * Annotation used to annotate business-logic classes in order to specify how it
 * should be trated with respect to the interceptor's stack to be used with it,
 * and (optionally) how it should be addressed by remote clients; by default, 
 * the framework assumes that unless specified all actions go through the 
 * "default" interceptor stack, which is also the default of the annotation value,
 * and that they retain their class' "simple name", but you can use this annotation
 * to hide away internal names and provide a public alias.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Action {

    /**
     * The name of the default stack.
     */
    public static final String DEFAULT_INTERCEPTORS_STACK = "default";

    /**
     * The default return code for a successful execution.
     */
    public static final String SUCCESS = "success";

    /**
     * The default return code to request more input data.
     */
    public static final String INPUT = "input";

    /**
     * The default return code for a failed execution.
     */
    public static final String ERROR = "error";

    /**
     * An alternative name for the action, so that the user need not know the
     * name of the concrete class implementing the business logic.
     *
     * @return 
     *   the alias under which the developer wants the <code>Action</code> to be
     *   exposed to users; if left to the default, the <code>Action</code>'s name
     *   will be the name of the class.
     */
    String alias() default "";

    /**
     * The id of the interceptors' stack to be used with this action.
     *
     * @return 
     *   the id if the interceptors' stack.
     */
    String interceptors() default DEFAULT_INTERCEPTORS_STACK;
}