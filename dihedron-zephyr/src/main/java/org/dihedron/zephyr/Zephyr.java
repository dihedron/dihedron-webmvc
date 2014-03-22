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
package org.dihedron.zephyr;

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
public final class Zephyr {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(Zephyr.class);

    /**
     * A map containing the library properties, partially populated by the build
     * process using information in the project's POM (e.g. the library version).
     */
    private static final Properties properties = new Properties();

    /**
     * Initialises the library properties.
     */
    static {
        InputStream stream = null;
        try {
//            logger.trace("trying to open the zephyr properties stream");
            stream = Zephyr.class.getClassLoader().getResourceAsStream("zephyr.properties");
//            logger.trace("zephyr properties stream acquired, loading properties (stream is {})", stream != null ? "valid" : "null");
            properties.load(stream);
//            logger.trace("zephyr properties loaded and ready");
        } catch (IOException e) {
            logger.error("error opening the zephyr properties file", e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Returns the framework's version (as per the project's POM).
     *
     * @return the framework's version (as per the project's POM).
     */
    public static String getVersion() {
        return properties.getProperty("zephyr.version");
    }

    /**
     * Returns the Zephyr framework's web site.
     *
     * @return the Zephyr framework web site.
     */
    public static String getWebSite() {
        return properties.getProperty("zephyr.website");
    }

    /**
     * Private constructor to prevent utility class instantiation.
     */
    private Zephyr() {
    }

    public static void main(String [] args) {
        Zephyr.getVersion();
    }
}
