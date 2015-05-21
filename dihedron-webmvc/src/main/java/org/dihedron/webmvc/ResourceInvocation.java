/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.Interceptor;
import org.dihedron.webmvc.interceptors.InterceptorStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class representing a plain resource request; business logic actions are
 * invoked through {@code ActionInvocation}.
 *  
 * @author Andrea Funto'
 */
public class ResourceInvocation extends Invocation {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ResourceInvocation.class);
	
	/**
	 * The resource being requested.
	 */
	private String resource;
	
	/**
	 * The thread-specific store for the iterator on the list of interceptors.
	 */
	private ThreadLocal<Iterator<Interceptor>> iterator = new ThreadLocal<Iterator<Interceptor>>() {
		@Override protected Iterator<Interceptor> initialValue() {
			return null;
		}
	};
		
	/**
	 * Constructor.
	 * 
	 * @param resource
	 *   the resource being requested.
	 * @param interceptors
	 *   the {@code InterceptorStack} representing the set of interceptors 
	 * @param request
	 *   the {@code HttpServletRequest} object.
	 * @param response
	 *   the {@code HttpServletResponse} object.
	 */
	public ResourceInvocation(String resource, InterceptorStack interceptors, HttpServletRequest request, HttpServletResponse response) {
		super(interceptors, request, response);
		this.resource = resource;
		this.iterator.set(null);
	}
	
	/**
	 * Returns the information pertaining to the resource being requested.
	 * 
	 * @return
	 *   the information on the resource being requested.
	 */
	public String getResource() {
		return resource;
	}
	
	/**
	 * Invokes the next interceptor in the stack, or the action if this is the 
	 * last interceptor.
	 * 
	 * @return
	 *   the interceptor result; if the interceptor is not intended to divert 
	 *   control flow, it should pass through whatever results from the nested 
	 *   interceptor call; changing this result with a different value results 
	 *   in a deviation of the workflow.  
	 * @throws WebMVCException
	 */
	@Override
	public String invoke() throws WebMVCException {
		
		// invoke the interceptors stack
		if(iterator.get() == null) {
			iterator.set(interceptors.iterator());
		}
		if(iterator.get().hasNext()) {
			return iterator.get().next().intercept(this);
		}
		// now simply return the name of the resource
		return resource;
	}
	
	/**
	 * Cleans up after the invocation has completed, by unbinding data from the 
	 * thread-local storage; this method must be called after each invocation,
	 * no matter how it ends, whether in success or with an exception; add it to 
	 * a "finally" block around the action invocation.
	 */
	public void cleanup() {
		logger.trace("removing the interceptors iterator from the thread-local storage");
		iterator.remove();
	}
}
