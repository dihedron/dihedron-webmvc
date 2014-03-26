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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.dihedron.zephyr.renderers.Renderer;

/**
 * Base class for all renderers.
 *
 * @author Andrea Funto'
 */
public abstract class AbstractRenderer implements Renderer { 
		
	/**
	 * By default renderers are terminal.
	 * 
	 * @see org.dihedron.zephyr.renderers.Renderer#isTerminal()
	 */
	@Override
	public boolean isTerminal() {
		return true;
	}

    /**
     * Returns the {@code PrintWriter} associated with the response object.
     *
     * @param response 
     *   the response object.
     * @return 
     *   the {@code PrintWriter} associated with the response object.
     * @throws IOException
     */
    protected PrintWriter getWriter(HttpServletResponse response) throws IOException {
        return response.getWriter();
    }
}
