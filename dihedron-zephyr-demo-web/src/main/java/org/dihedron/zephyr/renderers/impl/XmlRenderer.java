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

import org.dihedron.zephyr.annotations.Alias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Andrea Funto'
 */
@Alias(XmlRenderer.ID)
public abstract class XmlRenderer extends BeanRenderer {

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

//    /**
//     * @see org.dihedron.zephyr.renderers.Renderer#getId()
//     */
//    @Override
//    public String getId() {
//        return ID;
//    }
//
//    /**
//     * @see org.dihedron.zephyr.renderers.Renderer#render(javax.portlet.PortletRequest, javax.portlet.PortletResponse, java.lang.String)
//     */
//    @Override
//    public void render(PortletRequest request, PortletResponse response, String data) throws IOException, PortletException {
//
//        String bean = data;
//        logger.trace("rendering bean '{}'", bean);
//
//        Object object = getBean(request, bean);
//        JAXBContext context;
//        try {
//            if (response instanceof MimeResponse) {
//                // this works in both RENDER and RESOURCE (AJAX) phases
//                ((MimeResponse) response).setContentType(XML_MIME_TYPE);
//            }
//            context = JAXBContext.newInstance("org.dihedron.strutlets");
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.marshal(object, getWriter(response));
//        } catch (JAXBException e) {
//            logger.error("error marshalling bean to XML", e);
//            throw new PortletException("Error marshalling Java bean to XML", e);
//        }
//    }
}
