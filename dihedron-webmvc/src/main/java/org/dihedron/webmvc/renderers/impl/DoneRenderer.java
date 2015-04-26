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

package org.dihedron.webmvc.renderers.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The renderer that handles the render phase for actions that performed all
 * their I/O on their own and returned the {@code Action.DONE} result code,
 * meaning that there is nothing left to do in the render phase.
 * 
 * @author Andrea Funto'
 */
public class DoneRenderer extends AbstractRenderer {

	public static final String ID = "done";

	/**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) {
		// plain do nothing here!
	}
}
