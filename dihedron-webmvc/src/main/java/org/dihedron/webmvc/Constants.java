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


/**
 * A class to hold common constants.
 * 
 * @author Andrea Funto'
 */
public class Constants {

	/**
	 * The number of milliseconds in a second.
	 */
	public static final int MILLISECONDS_PER_SECOND = 1000;
	
	/**
	 * The number of seconds in a minute.
	 */
	public static final int SECONDS_PER_MINUTE = 60;
	
	/**
	 * The number of minutes in a hour.
	 */
	public static final int MINUTES_PER_HOUR = 60;
	
	/**
	 * The number of hours in a day.
	 */
	public static final int HOURS_PER_DAY = 24;
	
	/**
	 * The number of milliseconds in a minute.
	 */
	public static final int MILLISECONDS_PER_MINUTE = SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
	
	/**
	 * The number of milliseconds in a hour.
	 */
	public static final int MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * SECONDS_PER_MINUTE * MILLISECONDS_PER_SECOND;
	
	/**
	 * The number of bytes corresponding to a kilobyte.
	 */
	public static final int KILOBYTE = 1024;
	
	/**
	 * The number of bytes corresponding to a megabyte.
	 */
	public static final int MEGABYTE = 1024 * KILOBYTE;

	/**
	 * The number of bytes corresponding to a gigabyte.
	 */
	public static final int GIGABYTE = 1024 * MEGABYTE;
		
	/**
	 * Private constructor, to prevent construction.
	 */
	private Constants() {
	}
}
