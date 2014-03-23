/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Torque framework ("Torque").
 *
 * Torque is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Torque is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Torque. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.zephyr.protocol;


import org.dihedron.commons.strings.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An enumeration representing the HTTP methods.
 * 
 * @author Andrea Funto'
 */
public enum HttpMethod {
		
	/**
	 * The HTTP GET method.
	 */
	GET,
	
	/**
	 * The HTTP POST method.
	 */
	POST,
	
	/**
	 * The HTTP PUT method.
	 */
	PUT, 
	
	/**
	 * The HTTP DELETE method.
	 */
	DELETE,
	
	/**
	 * The HTTP HEAD method.
	 */
	HEAD,
	
	/**
	 * The HTTP OPTIONS method.
	 */
	OPTIONS,
	
	/**
	 * The HTTP TRACE method.
	 */
	TRACE;

	/**
	 * Tries to map the given string to an enumeration value.
	 * 
	 * @param name
	 *   the name to be mapped to an enumeration value.
	 * @return
	 *   the enumeration value if one fits the name, null if none applies or the 
	 *   input string is null.
	 */
	public static HttpMethod fromString(String name) {
		if(Strings.isValid(name)) {
			String internal = name.trim();
			for(HttpMethod method : HttpMethod.values()) {
				if(method.name().equalsIgnoreCase(internal)) {
					logger.trace("name '{}' corresponds to HTTP method {}", name, method.name());
					return method;
				}
			}
		}
		logger.warn("no method found corresponding to name '{}'", name);
		return null;
	}
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(HttpMethod.class);
	
}
