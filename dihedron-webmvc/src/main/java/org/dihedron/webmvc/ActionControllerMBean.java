/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the WebMVC framework ("WebMVC").
 *
 * WebMVC is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * WebMVC is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with WebMVC. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.webmvc;


/**
 * The JMX MBean to enable manipulation of the framework's controller.
 * 
 * @author Andrea Funto'
 */
public interface ActionControllerMBean {
	
	/**
	 * Returns the name of this application instance.
	 */
	String getApplicationName();
	
	/**
	 * Returns the current name of the WebMVC MVC framework.
	 */
	String getFrameworkName();
	
	/**
	 * Returns the current version of the WebMVC MVC framework.
	 */
	String getFrameworkVersion();

}
