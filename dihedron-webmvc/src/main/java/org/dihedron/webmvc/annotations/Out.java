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
 * Annotation indicating that the field will contain output data, mapped in the
 * given read/write scope under the given parameter name.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Out {

    /**
     * The name of the output parameter which will receive the annotated field's
     * value; it must be specified.
     *
     * @return 
     *   the name of the output parameter.
     */
    String value();

    /**
     * The scope into which the parameter should be stored; by default, it is
     * stored among the request attributes, in order to have it available throughout
     * the request life cycle and no more.
     * IMPORTANT: only read/write scopes can be specified here.
     *
     * @return 
     *   the scope into which to set the parameter.
     */
    Scope to() default Scope.REQUEST;
}