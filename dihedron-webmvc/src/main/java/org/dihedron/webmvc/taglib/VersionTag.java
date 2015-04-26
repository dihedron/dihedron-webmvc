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
package org.dihedron.webmvc.taglib;

import java.io.IOException;

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.dihedron.webmvc.WebMVC;

/**
 * Prints out the current version of the WebMVC MVC framework.
 * 
 * @author Andrea Funto'
 */
public class VersionTag extends SimpleTagSupport {

	/**
	 * Prints out the current version of the WebMVC MVC framework.
	 * 
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws IOException { 
		getJspContext().getOut().println(WebMVC.getVersion());
	}
}
