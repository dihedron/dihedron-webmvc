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
package org.example.demo.part1.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class User {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(User.class);

	/**
	 * The user's name.
	 */
	private String name;
	
	/**
	 * The user's family name.
	 */
	private String surname;
	
	private Contacts contacts = new Contacts();
	
	private Address address = new Address();
	
	/**
	 * Constructor.
	 */
	public User() {
	}
	
	
}
