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

package org.dihedron.zephyr.plugins;

/**
 * @author Andrea Funto'
 */
public interface Plugin {

    /**
     * Returns the name of the <code>Plugin</code> instance.
     *
     * @return the name of the <code>Plugin</code> instance.
     */
    String getName();

    /**
     * Initialises the <code>Plugin</code> object, and gets it ready for
     * providing services. This method needs not be reentrant, as it will be
     * called only once per instance.
     *
     * @return <code>true</code> if the initialisation succeeded, <code>false</code>
     * otherwise.
     */
    boolean initialise();

    /**
     * Cleans up any resources that might have been created or allocated at
     * initialisation time.
     */
    void cleanup();
}
