/**
 * Copyright (c) 2013, Andrea Funto'. All rights reserved.
 *
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.webmvc.exceptions;

import javax.servlet.ServletException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Base exception of all WebMVC exceptions.
 *
 * @author Andrea Funto'
 */
public class WebMVCException extends ServletException {

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -8032261686411960912L;

    /**
     * Constructor.
     */
    public WebMVCException() {
    }

    /**
     * Constructor.
     *
     * @param message the exception message.
     */
    public WebMVCException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause the exception's root cause.
     */
    public WebMVCException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     *
     * @param message the exception message.
     * @param cause   the exception's root cause.
     */
    public WebMVCException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Prints the exception's stack trace to a String.
     */
    public String getStackTraceAsString() {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        this.printStackTrace(printWriter);
        return writer.toString();
    }
}
