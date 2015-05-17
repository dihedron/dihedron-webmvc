/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.validation;

import org.dihedron.webmvc.ActionContext;

import javax.validation.ConstraintViolation;

import java.util.Set;


/**
 * The base interface to all validation callback handlers; implementors of this
 * class must comply with the following contract:<ol>
 * <li>no "business" exceptions should be thrown</li>
 * <li>if the method returns {@code null}, then the processing will continue as
 * usual</li>
 * <li>if the method returns a string, it will be taken as the result of the
 * method invocation, without the actual method being invoked; this can be used
 * to divert the ordinary flow of navigation into a path that e.g. shows the user
 * which values were not correct in the submitted form</li>
 * <li>implementors have access to the {@link ActionContext}, so they can store
 * values in any admitted scope before routing to the appropriate renderer</li>
 * </ol>.
 *
 * @author Andrea Funto'
 */
public interface ValidationHandler {

    /**
     * This method is invoked when the JSR-349 validator produces at least one
     * constraint violation on the parameters, right before the action's execution.
     * Based on the return value, it can prevent the action from being executed
     * at all.
     *
     * @param action     the name of the action class; this is the simple name of the class or
     *                   its alias if available.
     * @param method     the name of the method having caused the violations.
     * @param violations the set of violations.
     * @return
     * @{code null} to let the processing proceed as usual; a valid string to
     * divert execution flow into a different path (this value will be assumed
     * to be the action's result, without executing it).
     */
    String onParametersViolations(String action, String method, Set<ConstraintViolation<?>> violations);

    /**
     * This method is invoked when the JSR-349 validator produces at least one
     * constraint violation on the action method's return value. It is called
     * right after the actual action has executed, so it cannot be used to prevent
     * execution, but it can route output rendering to a different path if
     * necessary.
     *
     * @param action     the name of the action class; this is the simple name of the class or
     *                   its alias if available.
     * @param method     the name of the method having caused the violations.
     * @param violations the set of violations.
     * @return
     * @{code null} to let the processing proceed as usual; a valid string to
     * divert execution flow into a different path (this value will replace
     * the action's result).
     */
    String onResultViolations(String action, String method, Set<ConstraintViolation<?>> violations);

    /**
     * This method is invoked when the JSR-349 validator produces at least one
     * constraint violation on the given {@code &at;Model}-based method parameter.
     * If violations occur on multiple parameters, one call is performed for each
     * invalid parameter. This method can be used to prevent method execution and
     * directly diverting the execution to a different path.
     *
     * @param action     the name of the action class; this is the simple name of the class or
     *                   its alias if available.
     * @param method     the name of the method having caused the violations.
     * @param index      the index of the method parameter to which this violation belongs.
     * @param model      the class of the model object on which the error occurred,
     * @param violations the set of violations.
     * @return
     * @{code null} to let the processing proceed as usual; a valid string to
     * divert execution flow into a different path (this value will replace
     * the action's result).
     */
    String onModelViolations(String action, String method, int index, Class<?> model, Set<ConstraintViolation<?>> violations);
}
