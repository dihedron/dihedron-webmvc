/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.actions;

import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.targets.Target;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class acts as a factory for actions.
 *  
 * @author Andrea Funto'
 */
public final class ActionFactory {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionFactory.class);
	
	/**
	 * Creates a new Action object, given the information about the request target.
	 *  
	 * @param target
	 *   information about the requested target (the business service).
	 * @return
	 *   an object that implements the requested chunk of business logic.
	 * @throws WebMVCException 
	 */
	public static Object makeAction(Target target) throws WebMVCException {
		Object action = null;
		if(target != null) {
			logger.trace("instantiating action of class '{}'...", target.getActionClass().getSimpleName());
			try {
				action = target.getActionFactory().invoke(null);
				logger.trace("... class '{}' instance ready!", target.getActionClass().getSimpleName());
			} catch (Exception e) {
				logger.error("error instantiating action for target '{}'", target);
				throw new WebMVCException("Error instantiating action", e);
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
