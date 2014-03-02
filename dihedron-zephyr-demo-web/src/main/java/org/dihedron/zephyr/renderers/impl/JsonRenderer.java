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
@Alias(JsonRenderer.ID)
public abstract class JsonRenderer extends BeanRenderer {

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
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.enable(SerializationFeature.INDENT_OUTPUT);
//        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        String json = mapper.writeValueAsString(object);
//        logger.trace("json object is:\n{}", json);
//
//        if (response instanceof MimeResponse) {
//            // this works in both RENDER and RESOURCE (AJAX) phases
//            ((MimeResponse) response).setContentType(JSON_MIME_TYPE);
//        }
//        getWriter(response).print(json);
//        getWriter(response).flush();
//
//    }
}
