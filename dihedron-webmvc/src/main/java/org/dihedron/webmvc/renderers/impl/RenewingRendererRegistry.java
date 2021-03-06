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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.renderers.Renderer;
import org.dihedron.webmvc.renderers.registry.RendererRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class RenewingRendererRegistry implements RendererRegistry {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(RenewingRendererRegistry.class);

	/**
	 * The map containing the classes of all registered renderers.
	 */
	private Map<String, Class<? extends Renderer>> renderers = new HashMap<>();

	/**
	 * Constructor.
	 */
	public RenewingRendererRegistry() {
		logger.info("instantiating renewing renderers registry...");
		// handle the special case of the "done" do-nothing, built-in renderer
		this.renderers.put(DoneRenderer.ID, DoneRenderer.class);
	}

	@Override
	public void addRenderer(String id, Class<? extends Renderer> clazz) {
		if (Strings.isValid(id) && clazz != null) {
			logger.info("registering renderer '{}' of class '{}'", id, clazz.getName());
			this.renderers.put(id, clazz);
		}
	}

	@Override
	public Renderer getRenderer(String id) throws WebMVCException {
		String classname = null;
		Renderer renderer = null;
		try {
			if (Strings.isValid(id)) {
				Class<? extends Renderer> clazz = this.renderers.get(id);
				renderer = clazz.newInstance();
			}
		} catch (InstantiationException e) {
			logger.error("error instantiating object of class '{}'", classname);
			throw new WebMVCException("Error instantiating renderer class '" + classname + "'", e);
		} catch (IllegalAccessException e) {
			logger.error("error accessing class '{}'", classname);
			throw new WebMVCException("Error accessing renderer class '" + classname + "'", e);
		}
		return renderer;
	}

	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("renderers: [\n");
		for (Entry<String, Class<? extends Renderer>> entry : renderers.entrySet()) {
			buffer.append("  name: '").append(entry.getKey()).append("', class: '").append(entry.getValue().getCanonicalName()).append("' },\n");
		}
		buffer.append("]\n");
		return buffer.toString();
	}
}
