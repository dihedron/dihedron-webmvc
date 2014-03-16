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

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import javassist.Modifier;

import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.renderers.Renderer;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class RendererRegistryLoader {
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(RendererRegistryLoader.class);

    /**
     * This method performs the automatic scanning of renderers at startup time,
     * to get any custom, user-provided renderers.
     */
    public void loadFromJavaPackage(RendererRegistry registry, String javaPackage) throws ZephyrException {

        if (Strings.isValid(javaPackage)) {
            logger.trace("looking for renderer classes in package '{}'", javaPackage);

            // use this approach because it seems to be consistently faster
            // than the much simpler new Reflections(javaPackage)
            Reflections reflections =
                    new Reflections(new ConfigurationBuilder()
                            .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(javaPackage)))
                            .setUrls(ClasspathHelper.forPackage(javaPackage))
                            .setScanners(new SubTypesScanner()));
            Set<Class<? extends Renderer>> renderers = reflections.getSubTypesOf(Renderer.class);
            for (Class<? extends Renderer> clazz : renderers) {
                logger.trace("analysing renderer class: '{}'...", clazz.getName());
                if (!Modifier.isAbstract(clazz.getModifiers())/* && clazz.isAnnotationPresent(Alias.class)*/) {
					try {						
						String id = (String)clazz.getMethod("getId").invoke(null);						
	                    logger.trace("... registering '{}' renderer: '{}'", id, clazz.getCanonicalName());
	                    registry.addRenderer(id, clazz);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
						logger.error("error retrieving id for renderers of class '" + clazz.getName() + "': does it extend AbstractRenderer or provide a static getId() method?", e);
					}
                } else {
                    logger.trace("... skipping renderer '{}'", clazz.getCanonicalName());
                }
            }
        }
    }
}
