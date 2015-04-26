/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 


package org.dihedron.webmvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dihedron.webmvc.protocol.Scope;

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