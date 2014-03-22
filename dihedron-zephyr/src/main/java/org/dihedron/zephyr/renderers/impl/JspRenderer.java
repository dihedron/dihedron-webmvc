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

package org.dihedron.zephyr.renderers.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.zephyr.exceptions.ZephyrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The renderer that takes care of routing a request to the appropriate JSP page
 * for rendering; the Zephyr controller being a servlet filter, this simply
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
	 * @see org.dihedron.zephyr.renderers.Renderer#render(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public boolean render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, ZephyrException {
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
			throw new ZephyrException("Error forwarding reuqest to JSP '" + data + "' for rendering", e);
		}
		return true;
	}
}
