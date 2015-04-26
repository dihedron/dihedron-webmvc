/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.renderers.registry;

import java.util.Set;

import javassist.Modifier;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.renderers.Renderer;
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
    public void loadFromJavaPackage(RendererRegistry registry, String javaPackage) throws WebMVCException {

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
                if (!Modifier.isAbstract(clazz.getModifiers())) {
					try {			
						String id = (String)clazz.getField("ID").get(null);						
	                    logger.trace("... registering '{}' renderer: '{}'", id, clazz.getCanonicalName());
	                    registry.addRenderer(id, clazz);
					} catch (IllegalAccessException | IllegalArgumentException | SecurityException | NoSuchFieldException e) {
						logger.error("error retrieving id for renderers of class '" + clazz.getName() + "': does it provide a static field 'ID' of type String?", e);
					}
                } else {
                    logger.trace("... skipping renderer '{}'", clazz.getCanonicalName());
                }
            }
        }
    }
}
