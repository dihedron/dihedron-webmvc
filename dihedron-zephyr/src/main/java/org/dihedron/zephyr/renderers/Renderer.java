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

package org.dihedron.zephyr.renderers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.zephyr.exceptions.ZephyrException;


/**
 * The base interface for all renderers; concreete implementing classes MUST 
 * provide a no-args constructor or they will not be registered by the loader.
 *
 * @author Andrea Funto'
 */
public interface Renderer {
    
    /**
     * Returns whether the selection of this renderer can only represent the 
     * final step in an invocation: renderers belong to two classes: those 
     * that can be chained (such as the "chain" renderer) and those that can
     * only provide output to the user and thus represent the final step in the
     * processing of a request. 
     * 
     * @return
     *   whether the renderer can only render output or it can be chained, e.g.
     *   to forward contro to another target before performing the fihal rendering.
     */
    boolean isTerminal();

    /**
     * Renders the output to the client.
     *
     * @param request  
     *   the request object.
     * @param response 
     *   the response object.
     * @param data     
     *   the {@code Renderer}-specific data, e.g. the URL for the JSP renderer,
     *   the name of a java bean for the JSON and XML renderers, etc. Renderer
     *   data can be a JSON string if the renderer requires more complex or
     *   structured data to perform its work.  
     * @return
     *   {@code true} to signify that there is no further processing to be performed 
     *   by the invocation chain because this renderer did all there was to do, 
     *   including sending response data to the end user; {@code false} to let 
     *   the framework pass control over to the next filter in the chain and have
     *   it handle the actual response sending task.  
     * @throws IOException     
     *   if it cannot write to the output stream.
     * @throws ZephyrException 
     *   if any servlet-specific error occurs during the processing.
     */
    boolean render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, ZephyrException;
}
