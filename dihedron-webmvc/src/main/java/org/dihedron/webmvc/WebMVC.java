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
package org.dihedron.webmvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * A class to store general information about the library.
 *
 * @author Andrea Funto'
 */
public final class WebMVC {
	
	/**
	 * A string representing the WebMVC framework domain name (JMX).
	 */
	public static final String DIHEDRON_ZEPHYR_DOMAIN = "org.dihedron.webmvc";
		
    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(WebMVC.class);

    /**
     * A map containing the library properties, partially populated by the build
     * process using information in the project's POM (e.g. the library version).
     */
    private static final Properties properties = new Properties();

    /**
     * Initialises the library properties.
     */
    static {
//    	logger.trace("trying to open the zephyr properties stream");
    	try(InputStream stream = WebMVC.class.getClassLoader().getResourceAsStream("webmvc.properties")) {
//            logger.trace("zephyr properties stream acquired, loading properties (stream is {})", stream != null ? "valid" : "null");
            properties.load(stream);
//            logger.trace("zephyr properties loaded and ready");
        } catch (IOException e) {
            logger.error("error opening the zephyr properties file", e);
        }
    }
    
    /**
     * Returns the name of the framework.
     * 
     * @return
     *   the name of the framework.
     */
    public static String getName() {
    	return "WebMVC MVC";
    }

    /**
     * Returns the framework's version (as per the project's POM).
     *
     * @return the framework's version (as per the project's POM).
     */
    public static String getVersion() {
        return properties.getProperty("webmvc.version").trim();
    }

    /**
     * Returns the WebMVC framework's web site.
     *
     * @return 
     *   the WebMVC framework web site.
     */
    public static String getWebSite() {
        return properties.getProperty("webmvc.website").trim();
    }

    /**
     * Private constructor to prevent utility class instantiation.
     */
    private WebMVC() {
    }

    public static void main(String [] args) {
        WebMVC.getVersion();
    }
}
