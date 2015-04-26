/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.renderers.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.annotations.Alias;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Alias(StringRenderer.ID)
public class StringRenderer extends BeanRenderer {
	
	/**
	 * The renderer unique id.
	 */
	public static final String ID = "string";

	/**
	 * The MIME type returned as content type by this renderer.
	 */
	public static final String TEXT_MIME_TYPE = "text/plain";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(StringRenderer.class);
	
	/**
	 * @see org.dihedron.webmvc.renderers.Renderer#render(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.String)
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {
		
		String bean = data;
		logger.trace("rendering bean '{}'", bean);

		Object object = getBean(request, bean);
		String string = "";
		if(object != null) {
			string = object.toString();			
		}
		logger.trace("string is:\n{}", string);
		
		response.setContentType(TEXT_MIME_TYPE);
		getWriter(response).print(string);
        getWriter(response).flush();        
	}
}
