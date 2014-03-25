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


/**
 * @author Andrea Funto'
 */
public class Address {

	/**
	 * The user's street.
	 */
	private String street;
	
	/**
	 * The user's street number.
	 */
	private String number;

	/**
	 * The user's ZIP code.
	 */
	private String zip;

	/**
	 * The user's town.
	 */
	private String town;
	
	/**
	 * Constructor.
	 */
	public Address() {
	}

	/**
	 * Returns the value of the field street.
	 *
	 * @return 
	 *   the value of field street.
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Sets the value of field street.
	 *
	 * @param street 
	 *   the new value for field street.
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * Returns the value of the field number.
	 *
	 * @return 
	 *   the value of field number.
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * Sets the value of field number.
	 *
	 * @param number 
	 *   the new value for field number.
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * Returns the value of the field zip.
	 *
	 * @return 
	 *   the value of field zip.
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * Sets the value of field zip.
	 *
	 * @param zip 
	 *   the new value for field zip.
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * Returns the value of the field town.
	 *
	 * @return 
	 *   the value of field town.
	 */
	public String getTown() {
		return town;
	}

	/**
	 * Sets the value of field town.
	 *
	 * @param town 
	 *   the new value for field town.
	 */
	public void setTown(String town) {
		this.town = town;
	}
}
