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
package org.example.demo.part1.actions;


/**
 * @author Andrea Funto'
 */
public enum Sex {
	
	/**
	 * The user is a male.
	 */
	MALE,
	
	/**
	 * The user is a female.
	 */
	FEMALE;
	
	/**
	 * Converts the given string to the matching enumeration value, or returns null.
	 * 
	 * @param string
	 *   a string description of the sex.
	 * @return
	 *   a value from the enumeration, or null if none matches.
	 */
	public static Sex fromString(String string) {
		if(string != null) {
			for(Sex sex : Sex.values()) {
				if(sex.name().equalsIgnoreCase(string)) {
					return sex;
				}
			}
		}
		return null;
	}
}
