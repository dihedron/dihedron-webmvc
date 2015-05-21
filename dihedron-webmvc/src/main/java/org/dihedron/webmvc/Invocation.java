/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.InterceptorStack;

/**
 * The class representing a plain resource request; business logic actions are
 * invoked through {@code ActionInvocation}.
 *  
 * @author Andrea Funto'
 */
public abstract class Invocation {
			
	/**
	 * The {@code HttpServletRequest} object.
	 */
	protected HttpServletRequest request;
	
	/**
	 * The {@code HttpServletResponse} object.
	 */
	protected HttpServletResponse response;
	
	/**
	 * The stack of interceptors.
	 */
	protected InterceptorStack interceptors;
	
	/**
	 * Constructor.
	 * 
	 * @param interceptors
	 *   the {@code InterceptorStack} representing the set of interceptors 
	 * @param request
	 *   the {@code HttpServletRequest} object.
	 * @param response
	 *   the {@code HttpServletResponse} object.
	 */
	protected Invocation(InterceptorStack interceptors, HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.interceptors = interceptors;
	}
		
	/**
	 * Returns the current servlet request.
	 * 
	 * @return
	 *   the current servlet request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	
	/**
	 * Returns the current servlet response.
	 * 
	 * @return
	 *   the current servlet response.
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * Invokes the next interceptor in the stack.
	 * 
	 * @return
	 *   the interceptor result; if the interceptor is not intended to divert 
	 *   control flow, it should pass through whatever results from the nested 
	 *   interceptor call; changing this result with a different value results 
	 *   in a deviation of the workflow.  
	 * @throws WebMVCException
	 */
	public abstract String invoke() throws WebMVCException;
	
	/**
	 * Cleans up after the invocation has completed; this method must be called 
	 * after each invocation, no matter how it ends, whether in success or with 
	 * an exception; add it to a "finally" block around the action invocation.
	 */
	public abstract void cleanup();
}
