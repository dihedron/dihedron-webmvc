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


package org.dihedron.zephyr.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation representing the view handler that will
 * create the visual representation for the given result.
 * Results can be represented as free text strings, and
 * are mapped to the appropriate view handler by the action
 * controller, based on what's in these annotations.
 *
 * @author Andrea Funto'
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Conversation {
	
	public enum Type {
		/**
		 * Uses an existing conversation if available, otherwise establishes a new one.
		 */
		USES,
		
		/**
		 * Creates a new conversation; if one is already available it fails.
		 */
		INITIATES,
		
		/**
		 * Uses an existing conversation; if not available, it fails.
		 */
		REQUIRES,
		
		/**
		 * Closes an existing conversation, removing it.
		 */
		DESTROYS;		
	}

    /**
     * The name of the conversation in which the method may be involved.
     *
     * @return 
     *   the name of the conversation in which the method may be invoked.
     */
    String value() default "";
    
    /**
     * The type of involvement in a conversation.
     * 
     * @return
     *   type of involvement in a conversation.
     */
    Type type() default Type.USES;
}