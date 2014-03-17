/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Zephyr framework ("Zephyr").
 *
 * Zephyr is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Zephyr is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Zephyr. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.zephyr.annotations;

/**
 * The possible scopes available for retrieving and storing values. These 
 * scopes mostly map those specified for request parameters and attributes in 
 * the Servlet specification. The Zephyr framework regards parameters and 
 * attributes in the same way, in order to abstract away from low-level
 * technicalities. 
 * 
 * @author Andrea Funto'
 */
public enum Scope {
	
	/**
	 * Values in this scope are mapped from the request parameters; they have
	 * the same life span as REQUEST scoped attributes, but are stored as request
	 * parameters and can therefore only be of String type. Moreover this scope 
	 * is read-only, so you cannot set, update or remove its values. You would 
	 * expect to find form values and request parameters inside this scope.
	 */
	FORM(true),
	
	/**
	 * An attribute set at this scope will be available as long as the request
	 * is valid, that is from the moment it is set (by the browser or by the
	 * server-side code) to the moment the request is fulfilled and the response
	 * sent back to the client. 
	 */
	REQUEST(false),
	
	/**
	 * An attribute set at this scope is available throughout the user's session,
	 * to which it is private. Thus, once set it will hang around as long as 
	 * the user's session is valid, and it will be available to the same
	 * servlet until the session is invalidated (it times out or the user closes 
	 * the browser window).
	 */
	SESSION(false),
	
	/**
	 * An attribute set at this scope (which does not map to any scope in the
	 * Servlet specification) will be available to the same user until it is 
	 * removed or the server restarted. This is achieved by setting an entry 
	 * in the application scope keyed by the remote user's identifier. It is
	 * called "sticky" because it sticks around even after the user's session
	 * is invalidated, yet it is not in the public domain (application scope, 
	 * for everyone to peep). 
	 * IMPORTANT: node that using this scope in lack of a reliable way of 
	 * ascertaining the remote user's identity can easily lead to data theft, 
	 * since it sould be sufficient to forge the "remote user" request header 
	 * to gain access to someone else's data in the server session. Moreover
	 * not being bound to a session, the server is not able to recollect
	 * resources when a user logs off, so storing relevan quantities of data
	 * in this scope does not scale and can easily lead to server resources 
	 * exhaustion.    
	 */
	STICKY(false),
	
	/**
	 * An attribute set in this scope is freely available for everyone to use:
	 * it is shared among sessions and servlets in the same application, so it
	 * can be renamed "public domain". 
	 * IMPORTANT: as for sticky attributes, data in this scope is never
	 * garbage-collected until the server is restarted, so it can lead to 
	 * server resources exhaustion. 
	 */
	APPLICATION(false),
	
	/**
	 * The scope mapping values in the controller's configuration; by providing 
	 * a properties file, configuration values can be injected and made available
	 * to actions in this scope. The scope is read-only and cannot be used to
	 * persist information.
	 */
	CONFIGURATION(true),
	
	/**
	 * The scope mapping properties defined as system properties in the JVM; this 
	 * scope is read-only and cannot be used to update the system properties.
	 */
	SYSTEM(true),
	
	/**
	 * The scope mapping system environment variables; this scope is read-only 
	 * and cannot be used to update system properties.  
	 */
	ENVIRONMENT(true);
	
	/**
	 * An array representing all scopes.
	 */
	public static final Scope[] ALL = {
		FORM, REQUEST, SESSION, STICKY, APPLICATION, CONFIGURATION, SYSTEM, ENVIRONMENT
	};
	
	/**
	 * Returns whether the scope is read-only and it cannot be used to store
	 * values, only to read them. This applies only to the FORM scope, which 
	 * does not allow setting any value.
	 * 
	 * @return
	 *   whether the scope allows values to be stored or not.
	 */
	public boolean isReadOnly() {
		return readOnly;
	}
	
	/**
	 * Constructor.
	 *
	 * @param readOnly
	 *   whether the scope is read-only.
	 */
	private Scope(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	/**
	 * Whether the scope is read-only and it cannot be used to store information,
	 * only to retrieve it.
	 */
	private boolean readOnly;    	
}
