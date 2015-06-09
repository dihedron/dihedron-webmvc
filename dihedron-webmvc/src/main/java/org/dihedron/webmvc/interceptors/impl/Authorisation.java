/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors.impl;

import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.Invocation;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Checks if the session token contains the appropriate group(s), as 
 * specified via a regular expression. Regular expressions allow multiple
 * groups to be checked at once, so that any one will do. If you want to 
 * check that the used has more than one group (not any of them), you can
 * create an interceptor stack with a cascaded series of interceptors of 
 * this type, each testing its group(s): as soon as any of them fails to
 * detect the required group name, the error code is returned. Thus:<ol>
 * <li>to check for any of a set of groups, do 'admin|sudoer'</li>
 * <li>to check for all of a set of groups, create multiple interceptors</li>
 * </ol>.
 * 
 * @author Andrea Funto'
 */
public class Authorisation extends Security {
	
	/**
	 * The result returned by the interceptor when the user is not 
	 * authorised.
	 */
	public static final String UNAUTHORISED = "unauthorised";
	
	/**
	 * A regular expression against which each of the groups in the user
	 * profile is matched; if any one matches the regular expression, the
	 * user is authorised. Regular expressions provide an easy way of
	 * expressing OR conditions (e.g. 'admin|superadmin').
	 */
	public static final String GROUPS_PARAMETER = "groups"; 
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Authorisation.class);
	
	/**
	 * The pattern against which group names will be matched.
	 */
	private Regex pattern = null;
	
	/**
	 * Initialises the interceptor.
	 * 
	 * @see org.dihedron.webmvc.interceptors.Interceptor#initialise()
	 */
	@Override
	public void initialise() {
		String groups = getParameter(GROUPS_PARAMETER);
		if(Strings.isValid(groups)) {
			logger.trace("testing existence of groups matching /{}/ in user profile", groups);
			pattern = new Regex(groups);
		} else {
			logger.error("the key under which the authentication token is to be checked was not specified: check that this interceptor defines the 'token' parameter");
		}
	}
	
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
		String [] groups = Security.getGroups();
		if(groups != null && groups.length > 0) {
			for(String group : groups) {
				if(pattern.matches(group)) {
					return invocation.invoke();
				}
			}
		}
		// no matching group: the user is not authorised!
		return UNAUTHORISED;
	}
}
