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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.renderers.Renderer;
import org.dihedron.zephyr.renderers.registry.RendererRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class CachingRendererRegistry implements RendererRegistry {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CachingRendererRegistry.class);


    /**
     * The map containing an instance of each registered renderers.
     */
    private Map<String, Renderer> renderers = new HashMap<>();
    
    /**
     * Constructor.
     */
    public CachingRendererRegistry() {
        logger.info("instantiating caching renderers registry...");
        // handle the special case of the "done" do-nothing renderer
        this.renderers.put(DoneRenderer.ID, new DoneRenderer());
    }
    
    @Override
    public void addRenderer(String id, Class<? extends Renderer> clazz) throws ZephyrException {
        if (Strings.isValid(id) && clazz != null) {
            try {
                logger.info("registering renderer '{}' of class '{}'", id, clazz.getName());
                Renderer renderer = clazz.newInstance();
                this.renderers.put(id, renderer);
            } catch (InstantiationException e) {
                logger.error("error instantiating object of class '{}'", clazz.getCanonicalName());
                throw new ZephyrException("Error instantiating renderer class '" + clazz.getCanonicalName() + "'", e);
            } catch (IllegalAccessException e) {
                logger.error("error accessing class '{}'", clazz.getCanonicalName());
                throw new ZephyrException("Error accessing renderer class '" + clazz.getCanonicalName() + "'", e);
            }
        }
    }

    @Override
    public Renderer getRenderer(String id) throws ZephyrException {
        Renderer renderer = null;
        if (Strings.isValid(id)) {
    		renderer = this.renderers.get(id);
        }
        return renderer;
    }

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("renderers: [\n");
        for (Entry<String, Renderer> entry : renderers.entrySet()) {
            buffer.append("  { name: '").append(entry.getKey()).append("', class: '").append(entry.getValue().getClass().getCanonicalName()).append("' },\n");
        }
        buffer.append("]\n");
        return buffer.toString();
    }
}
