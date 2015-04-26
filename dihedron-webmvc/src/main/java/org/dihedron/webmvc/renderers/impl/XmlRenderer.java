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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrea Funto'
 */
public class XmlRenderer extends BeanRenderer {

    /**
     * The renderer unique id.
     */
    public static final String ID = "xml";

    /**
     * The MIME type returned as content type by this renderer.
     */
    public static final String XML_MIME_TYPE = "text/xml";

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(XmlRenderer.class);

    /**
     * @see org.dihedron.webmvc.renderers.Renderer#render(javax.portlet.PortletRequest, javax.portlet.PortletResponse, java.lang.String)
     */
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, String data) throws IOException, WebMVCException {

        String bean = data;
        logger.trace("rendering bean '{}'", bean);
        Object object = getBean(request, bean);
        JAXBContext context;
        try {
            response.setContentType(XML_MIME_TYPE);
            context = JAXBContext.newInstance("org.dihedron.webmvc");
            Marshaller marshaller = context.createMarshaller();
            marshaller.marshal(object, getWriter(response));
        } catch (JAXBException e) {
            logger.error("error marshalling bean to XML", e);
            throw new WebMVCException("Error marshalling Java bean to XML", e);
        }
    }
}
