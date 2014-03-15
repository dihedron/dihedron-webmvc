/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.zephyr.renderers.impl;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.zephyr.annotations.Alias;
import org.dihedron.zephyr.exceptions.ZephyrException;
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
@Alias(ChainRenderer.ID)
public class ChainRenderer extends AbstractRenderer {

	public static final String ID = "chain";

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ChainRenderer.class);

	/**
	 * @see org.dihedron.zephyr.renderers.Renderer#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}
	
	/**
	 * The chain renderer is non-terminal: by specifying it we are asking the framework
	 * to forward control to yet another target before the actually rendering can occur.
	 * 
	 * @return
	 *   {@code true}, to indicate that this is not a process sink, and further processing
	 *   is neededbefore the actal rendering can take plavce
	 */
	@Override
	public boolean isTerminal() {
		return false;
	}

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
