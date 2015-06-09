/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors.impl;

import org.dihedron.webmvc.Invocation;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if a valid session token is available in the session scope;
 * if so, it propagates control down the interceptors stack, otherwise
 * an error result is returned. 
 * 
 * @author Andrea Funto'
 */
public class Authentication extends Security {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Authentication.class);
	
	/**
	 * The result returned by the interceptor when the user is not 
	 * authenticated.
	 */
	public static final String UNAUTHENTICATED = "unauthenticated";
				
	/**
	 * Checks if the authentication token is available in the session. 
	 * 
	 * @param invocation
	 *   the current action invocation.
	 * @return
	 *   the result of the nested components' execution.
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(Invocation invocation) throws WebMVCException {
		if(Security.hasSessionToken()) { 
			// propagate the invocation
			logger.trace("user is authenticated, propagating request...");
			return invocation.invoke();
		}
		logger.warn("an attempt was made to access a protected resource without authenticating first, rejecting...");
		return UNAUTHENTICATED;
	}
}
