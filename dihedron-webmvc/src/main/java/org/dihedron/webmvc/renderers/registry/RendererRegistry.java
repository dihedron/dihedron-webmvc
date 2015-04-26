/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.renderers.registry;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.renderers.Renderer;

/**
 * @author Andrea Funto'
 */
public interface RendererRegistry {

    /**
     * The Java package where the default set of renderers is located.
     */
    static final String DEFAULT_RENDERER_PACKAGE = "org.dihedron.webmvc.renderers.impl";

    /**
     * Adds information about a {@code Renderer} type.
     *
     * @param id    
     *   the id of the renderer.
     * @param clazz 
     *   the class of the renderer.
     */
    void addRenderer(String id, Class<? extends Renderer> clazz) throws WebMVCException;

    /**
     * Returns an instance of {@code Renderer} for the given input type; implementing
     * classes may choose to create a new instance of renderer class for each
     * request, or to recycle instances since renderers are assumed to be stateless.
     *
     * @param id 
     *   the type of the renderer to return.
     * @return 
     *   the {@code Renderer} instance.
     * @throws WebMVCException
     */
    Renderer getRenderer(String id) throws WebMVCException;
}