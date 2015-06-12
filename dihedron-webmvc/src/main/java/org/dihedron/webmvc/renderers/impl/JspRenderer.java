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
 * The renderer that takes care of routing a request to the appropriate JSP page
 * for rendering; the WebMVC controller being a servlet filter, this simply
 * means changing the original request so that is is assigned to the proper JSP
 * before letting the filter chain proceed.
 * 
 * @author Andrea Funto'
 */
public class JspRenderer extends AbstractRenderer {

	public static final String ID = "jsp";

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(JspRenderer.class);

	/**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {
		try {
			String url = response.encodeURL(data);
			logger.trace("rendering URL '{}'", url);
			RequestDispatcher dispatcher = null;
//			if(url.startsWith("/")) {
//				 response.sendRedirect(url);
//			} else {
				dispatcher = request.getRequestDispatcher(url);
				if (dispatcher == null) {
					logger.error("'{}' is not a valid include path (jsp)", data);
				} else {
					dispatcher.forward(request, response);
				}
//			}	
		}
		catch(ServletException e) {
			logger.error("error re-routing and forwaring request to JSP '{}'", data);
			throw new WebMVCException("Error forwarding request to JSP '" + data + "' for rendering", e);
		}
	}
}
