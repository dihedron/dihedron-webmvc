/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dihedron.webmvc.validation.DefaultValidationHandler;
import org.dihedron.webmvc.validation.ValidationHandler;

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
    
    /**
     * The id of the domain this method belongs to; if empty, it means that it 
     * inherits the domain of its action, or the domain resolution should undergo 
     * the ordinary process for all resources.
     *
     * @return 
     *   the (optional) id if the domain.
     */
    String domain() default "";    

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