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
package org.dihedron.zephyr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class DefineObjectsTag extends SimpleTagSupport {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DefineObjectsTag.class);
	
	public void doTag() throws JspException, IOException {
//		PageContext pageContext = (PageContext)getJspContext();
//		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
//		pageContext.setAttribute("context", ActionContext.getContext());
		
		logger.trace("should be defining framework objects here!");
	}
}
