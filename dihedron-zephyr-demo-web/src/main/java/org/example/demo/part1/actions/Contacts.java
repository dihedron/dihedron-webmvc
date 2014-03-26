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

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;


/**
 * @author Andrea Funto'
 */
public class Contacts {

	/**
	 * The user's phone number.
	 */
	@Pattern(regexp="^\\d{2}-\\d{3}-\\d{5}$")
	private String phone;
	
	/**
	 * The user's email address.
	 */
	@Email
	private String email;
	
	/**
	 * Constructor.
	 */
	public Contacts() {
	}

	/**
	 * Returns the value of the field phone.
	 *
	 * @return 
	 *   the value of field phone.
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Sets the value of field phone.
	 *
	 * @param phone 
	 *   the new value for field phone.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * Returns the value of the field email.
	 *
	 * @return 
	 *   the value of field email.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the value of field email.
	 *
	 * @param email 
	 *   the new value for field email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
