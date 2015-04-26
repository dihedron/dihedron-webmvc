/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.renderers.impl;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.dihedron.webmvc.renderers.Renderer;

/**
 * Base class for all renderers.
 *
 * @author Andrea Funto'
 */
public abstract class AbstractRenderer implements Renderer { 

    /**
     * Returns the {@code PrintWriter} associated with the response object.
     *
     * @param response 
     *   the response object.
     * @return 
     *   the {@code PrintWriter} associated with the response object.
     * @throws IOException
     */
    protected PrintWriter getWriter(HttpServletResponse response) throws IOException {
        return response.getWriter();
    }
}
