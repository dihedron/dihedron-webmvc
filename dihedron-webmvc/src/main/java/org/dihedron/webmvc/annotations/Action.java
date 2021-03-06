/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.annotations;

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
     * The special return value to indicate that the complete rendering life cycle, 
     * <em>including the rendering</em>, has been completed and no further processing
     * is needed. An action should return this result when it has already taken 
     * care of serving the result data, e.g. when the action produces a binary 
     * stream and writes it directly to the output stream. Under these circumstances, 
     * the framework needs not route control to the view. There is no need to
     * indicate a <code>@Result</code> for this value: it is handled internally
     * by the framework as a "do-nothing" indication.  
     */
    public static final String DONE = "done";

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
     * The id of the domain this action belongs to; if empty, it means that the
     * domain resolution should undergo the ordinary process for all resources.
     *
     * @return 
     *   the (optional) id if the domain.
     */
    String domain() default "";
}