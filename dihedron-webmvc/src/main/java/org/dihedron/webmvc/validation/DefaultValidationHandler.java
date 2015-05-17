/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import java.util.Set;

/**
 * @author Andrea Funto'
 */
public class DefaultValidationHandler extends BaseValidationHandler {
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(DefaultValidationHandler.class);

    /**
     * @see org.dihedron.webmvc.validation.ValidationHandler#onParametersViolations(java.lang.String, java.lang.String, java.util.Set)
     */
    @Override
    public String onParametersViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            logger.warn("{}!{}: violation on parameter '{}' having value '{}': {}", action, method, violation.getPropertyPath().toString(), violation.getInvalidValue(), violation.getMessage());
            addParameterViolationMessage("violation on parameter '" 
            		+ violation.getPropertyPath().toString() + "' having value '" +
            		violation.getInvalidValue() + "': " + violation.getMessage());
        }
        return null;
    }

    /**
     * @see org.dihedron.webmvc.validation.ValidationHandler#onResultViolation(java.lang.String, java.lang.String, java.util.Set)
     */
    @Override
    public String onResultViolations(String action, String method, Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            logger.warn("{}!{}: violation on return value '{}': {}", action, method, violation.getInvalidValue(), violation.getMessage());
            addParameterViolationMessage("violation on return value '" +
            		violation.getInvalidValue() + "': " + violation.getMessage());

        }
        return null;
    }

    /**
     * @see org.dihedron.webmvc.validation.ValidationHandler#onModelViolations(java.lang.String, java.lang.String, int, java.lang.Class, java.util.Set)
     */
    @Override
    public String onModelViolations(String action, String method, int index, Class<?> model, Set<ConstraintViolation<?>> violations) {
        for (ConstraintViolation<?> violation : violations) {
            logger.warn("{}!{}: violation on model bean {}, property '{}' (no. {}), having value '{}': {} ({})", action, method, model.getSimpleName(), 
            		violation.getPropertyPath().toString(), index, violation.getInvalidValue(), violation.getMessage());
            addParameterViolationMessage("violation on model bean '" + model.getSimpleName() + "', property '" +
            		violation.getPropertyPath().toString() + " has invalid value " + 
            		violation.getInvalidValue() + "': " + violation.getMessage());
            
        }
        return null;
    }
}
