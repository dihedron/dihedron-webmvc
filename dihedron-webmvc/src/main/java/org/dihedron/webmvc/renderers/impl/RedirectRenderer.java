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

package org.dihedron.webmvc.renderers.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@code}
 *
 * @author Andrea Funto'
 */
public class RedirectRenderer extends AbstractRenderer {

    /**
     * The renderer unique id.
     */
    public static final String ID = "redirect";

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RedirectRenderer.class);

    /**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {

        // sending redirect to the redirect JSP
        logger.trace("redirecting to URL: '{}'", data);
        response.sendRedirect(data);
    }
}
