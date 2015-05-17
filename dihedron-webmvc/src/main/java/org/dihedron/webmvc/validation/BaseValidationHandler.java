/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.validation;

import java.util.ArrayList;
import java.util.List;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.ActionContext;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.protocol.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public abstract class BaseValidationHandler implements ValidationHandler {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(BaseValidationHandler.class);
	
	/**
	 * The key (under the request scope) where validation errors might be stored.
	 */
	private static final String VALIDATION_ERRORS = "webmvc:validation-errors";
	
	/**
	 * Adds a parameter violation message to the request scope.
	 * 
	 * @param message
	 *   the message to be added.
	 */
	protected void addParameterViolationMessage(String message) {
		addErrorMessage(message);
	}

	/**
	 * Adds a result violation message to the request scope.
	 * 
	 * @param message
	 *   the message to be added.
	 */
	protected void addResultViolationMessage(String message) {
		addErrorMessage(message);
	}
	
	/**
	 * Adds a model violation message to the request scope.
	 * 
	 * @param message
	 *   the message to be added.
	 */
	protected void addModelViolationMessage(String message) {
		addErrorMessage(message);
	}
	
	/**
	 * Adds an error message to the request scope.
	 * 
	 * @param message
	 *   the message to be added.
	 */
	@SuppressWarnings("unchecked")
	private void addErrorMessage(String message) {
		if(Strings.isValid(message)) {
			try {
				List<String> errors = null;
				Object value = ActionContext.getValue(VALIDATION_ERRORS, Scope.REQUEST);
				errors = value != null ? (List<String>)value : new ArrayList<String>();
				errors.add(message);
				ActionContext.setValue(VALIDATION_ERRORS, errors, Scope.REQUEST);
			} catch(WebMVCException e) {
				logger.error("error storing error message into rquest scope");
			}
		}
	}
}
