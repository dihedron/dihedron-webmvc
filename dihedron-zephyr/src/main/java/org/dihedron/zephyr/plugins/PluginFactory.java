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
 * The base interface of plugins: they must provide a factory method to create a
 * probe, which will be used to detect whether the given plugin is supported under
 * the current conditions; the probe must not statically link any resources which
 * might not be available at runtime (e.g. some application-server-specific JARs
 * and classes), it should resort to Reflection based methods to asses whether
 * the necessary runtime components would be available if the plugin were to be
 * used.
 * Plugins must provide another factory method to instantiate the actual
 * <code>pluggable</code> object, which is allowed to statically link classes of
 * since the runtime is supposed to be availble at this stage, after the probe
 * has been run.
 *
 * @author Andrea Funto'
 */
public interface PluginFactory {

    /**
     * Creates a new <code>Probe</code> object, which will employ Java Reflection
     * and other artifices to detect if the given <code>Plugin</code>
     * business object can be instantiated. The probe is a lightweight dependency
     * in that it does not require the availability of its supporting classes,
     * it will simply sniff for their availability, and only if so will the
     * <code>PluginManager</code> proceed to actual <code>Plugin</code>
     * instantiation.
     *
     * @return a <code>Probe</code> object instance.
     */
    Probe makeProbe();

    /**
     * Creates a new <code>Plugin</code> object.
     *
     * @return a new <code>Plugin</code> object.
     */
    Plugin makePlugin();
}
