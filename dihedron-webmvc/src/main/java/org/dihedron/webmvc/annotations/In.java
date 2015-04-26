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
 * Annotation indicating that the field will contain data coming from the given
 * parameter name in the given scope(s).
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface In {

    /**
     * The name of the input parameter; it must be specified.
     *
     * @return 
     *   the name of the parameter; this must not be a null or blank string since
     *   there's no way to acquire a sensible default from the information available
     *   at runtime (e.g. there's no name of the field available through reflection).
     */
    String value();

    /**
     * The scopes in which the parameter should be looked up; by default, it is
     * looked up in all available scopes.
     *
     * @return 
     *   the set of scope to scan for the annotated parameter.
     */
    Scope[] from() default {
		Scope.FORM, Scope.REQUEST, Scope.SESSION, Scope.STICKY, Scope.APPLICATION, 
		Scope.CONFIGURATION, Scope.SYSTEM, Scope.ENVIRONMENT
	};
}