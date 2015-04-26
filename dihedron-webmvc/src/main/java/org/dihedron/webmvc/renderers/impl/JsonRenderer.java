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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author Andrea Funto'
 */
public class JsonRenderer extends BeanRenderer {

    /**
     * The renderer unique id.
     */
    public static final String ID = "json";

    /**
     * The MIME type returned as content type by this renderer.
     */
    public static final String JSON_MIME_TYPE = "application/json";

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(JsonRenderer.class);

	/**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {
        String bean = data;
        logger.trace("rendering bean '{}'", bean);
        Object object = getBean(request, bean);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json = mapper.writeValueAsString(object);
        logger.trace("json object is:\n{}", json);
        response.setContentType(JSON_MIME_TYPE);
        getWriter(response).print(json);
        getWriter(response).flush();
    }
}
