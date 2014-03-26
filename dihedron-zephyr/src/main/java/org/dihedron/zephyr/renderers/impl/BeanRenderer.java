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

import javax.servlet.http.HttpServletRequest;

import org.dihedron.zephyr.ActionContext;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.protocol.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public abstract class BeanRenderer extends AbstractRenderer {
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(BeanRenderer.class);

    /**
     * Tries to retrieve the bean from the parameters and the attributes.
     *
     * @param request 
     *   the request object.
     * @param data
     *   the name of the attribute holding the bean to be retrieved.
     * @return
     *   the bean, if found; null otherwise.
     * @throws ZephyrException 
     */
    protected Object getBean(HttpServletRequest request, String data) throws ZephyrException {
    	logger.trace("trying to retrieve bean '{}'...", data);
    	Object bean = ActionContext.findValue(data, Scope.ALL);
    	logger.trace("bean '{}' {} been found", data, bean != null ? "has" : "hasn't");
    	return bean;
    }
}
