/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.interceptors.impl;

import org.dihedron.webmvc.ActionContext;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.Interceptor;
import org.dihedron.webmvc.protocol.Scope;


/**
 * Base class for security-related interceptors.
 * 
 * @author Andrea Funto'
 */
public abstract class Security extends Interceptor {
	
	/**
	 * The name of the token in the session that indicates that a successful 
	 * authentication has been performed; it is up to the application to store 
	 * the token and remove it when the user logs off; this class provides facility 
	 * methods for both uses.
	 */
	public static final String SESSION_TOKEN = "webmvc:session-token";
	
	/**
	 * An empty array representing a logon token with no associated groups.
	 */
	private static final String[] NO_GROUPS = {};
	
	/**
	 * Adds a logon token at session scope containing a list of groups, or
	 * a standard empty list if no group is provided.
	 * 
	 * @param groups
	 *   an optional list of group names.
	 * @throws WebMVCException 
	 */
	public static void addSessionToken(String ... groups) throws WebMVCException {
		if(groups != null && groups.length > 0) {
			ActionContext.setValue(SESSION_TOKEN, groups, Scope.SESSION);
		} else {
			ActionContext.setValue(SESSION_TOKEN, NO_GROUPS, Scope.SESSION);
		}
	}	
	
	/**
	 * Removes the logon token from the session scope.
	 * 
	 * @throws WebMVCException
	 */
	public static void removeSessionToken() throws WebMVCException {
		ActionContext.removeValue(SESSION_TOKEN, Scope.SESSION);
	}
	
	/**
	 * Checks if the user is authenticated to the system.
	 * 
	 * @return
	 *   whether the user is authenticated to the system.
	 * @throws WebMVCException 
	 */
	public static boolean hasSessionToken() throws WebMVCException {
		return ActionContext.getValue(SESSION_TOKEN, Scope.SESSION) != null;
	}
	
	/**
	 * If the user is logged on, it returns the list of groups in the logon
	 * token (if available) and an empty array otherwise; if the user is not 
	 * logged on, {@code null} is returned instead.
	 * 
	 * @return
	 *   a list of groups names, or {@code null}.
	 * @throws WebMVCException
	 */
	public static String[] getGroups() throws WebMVCException {
		Object value = ActionContext.getValue(SESSION_TOKEN, Scope.SESSION);
		if(value != null) {
			return (String[])value;
		}
		return null;
	}
}
