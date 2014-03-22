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

package org.dihedron.zephyr.actions;

import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public final class ActionFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	
	/**
	 * 
	 * @param target
	 * @return
	 * @throws ZephyrException 
	 */
	public static Object makeAction(Target target) throws ZephyrException {
		Object action = null;
		if(target != null) {
			logger.trace("instantiating action of class '{}'...", target.getActionClass().getSimpleName());
			try {
				action = target.getActionFactory().invoke(null);
				logger.trace("... class '{}' instance ready!", target.getActionClass().getSimpleName());
			} catch (Exception e) {
				logger.error("error instantiating action for target '{}'", target);
				throw new ZephyrException("Error instantiating action", e);
			}
		}
		return action;
	}
	
	/**
	 * Private constructor to prevent utility class instantiation. 
	 */
	private ActionFactory() {
	}
}
