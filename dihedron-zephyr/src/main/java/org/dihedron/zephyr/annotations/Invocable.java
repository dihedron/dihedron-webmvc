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

import org.dihedron.zephyr.validation.DefaultValidationHandler;
import org.dihedron.zephyr.validation.ValidationHandler;

import java.lang.annotation.*;

/**
 * Annotation to be placed on methods that will be exposed as action, event,
 * render or resource targets.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface Invocable {

    /**
     * The array of expected results; each of them will map to the appropriate
     * renderer, and will be parameterised according to what is specified in the
     * <code>@Result</code> annotation.
     *
     * @return 
     *   the array of expected results.
     */
    Result[] results() default {};
    
//    Conversation conversation();

    /**
     * The optional implementation of the validator interface that will provide
     * the callbacks to handle JSR-349 constraint violations errors on properly
     * annotated input parameters or on the method itself; if left to the default
     * dummy class, validation errors will simply cause a warning message to be
     * printed. For the exact contract between validator and validation handler,
     * see {@link ValidationHandler}.
     */
    Class<? extends ValidationHandler> validator() default DefaultValidationHandler.class;
}