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

package org.dihedron.zephyr.renderers.registry;

import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.renderers.Renderer;

/**
 * @author Andrea Funto'
 */
public interface RendererRegistry {

    /**
     * The Java package where the default set of renderers is located.
     */
    static final String DEFAULT_RENDERER_PACKAGE = "org.dihedron.zephyr.renderers.impl";

    /**
     * Adds information about a {@code Renderer} type.
     *
     * @param id    
     *   the id of the renderer.
     * @param clazz 
     *   the class of the renderer.
     */
    void addRenderer(String id, Class<? extends Renderer> clazz) throws ZephyrException;

    /**
     * Returns an instance of {@code Renderer} for the given input type; implementing
     * classes may choose to create a new instance of renderer class for each
     * request, or to recycle instances since renderers are assumed to be stateless.
     *
     * @param id 
     *   the type of the renderer to return.
     * @return 
     *   the {@code Renderer} instance.
     * @throws ZephyrException
     */
    Renderer getRenderer(String id) throws ZephyrException;
}