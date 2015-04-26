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

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The renderer that takes care of informing the framework that upon this kind
 * of result, control should be handled ovber to another targets; this renderer
 * is not capable of rendering any output, it will throw an exception if it is 
 * forced to do so (since it would be a bug).
 * 
 * @author Andrea Funto'
 */
public class ChainRenderer extends AbstractRenderer {

	public static final String ID = "chain";

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ChainRenderer.class);

	/**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {
		try {
			RequestDispatcher dispatcher = request.getRequestDispatcher(response.encodeURL(data));
	
			if (dispatcher == null) {
				logger.error("'{}' is not a valid include path (jsp)", data);
			} else {
				dispatcher.forward(request, response);
			}
		}
		catch(ServletException e) {
			logger.error("error re-routing and forwaring request to JSP '{}'", data);
			throw new WebMVCException("Error forwarding reuqest to JSP '" + data + "' for rendering", e);
		}
	}
}
