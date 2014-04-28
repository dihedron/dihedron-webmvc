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

import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.dihedron.zephyr.Zephyr;

/**
 * Prints out a hyperlink to the current Zephyr MVC framework website.
 * 
 * @author Andrea Funto'
 */
public class HyperLinkTag extends SimpleTagSupport {

	/**
	 * The default value of the target attribute; by default the hyperlink is
	 * opened in a new window/tab ("_blank");
	 */
	private static final String DEFAULT_TARGET = "_blank";
	
	/**
	 * The target of the generated hyperlink.
	 */
	private String target = DEFAULT_TARGET;
	
    /**
     * Sets the value of the "target" attribute.
     * 
     * @param target
     *   the name of the target window for the hyperlink.
     */
    public void setTarget(String target) {
        this.target = target;
    }
    
	/**
	 * Prints out a hyperlink to the current Zephyr MVC framework website.
	 * 
	 * @see javax.servlet.jsp.tagext.SimpleTagSupport#doTag()
	 */
	public void doTag() throws IOException { 		
		getJspContext().getOut().println("<a href=\"" + Zephyr.getWebSite() + "\" target=\"" + target + "\">Zephyr " + Zephyr.getVersion() + "</a>");
	}
}
