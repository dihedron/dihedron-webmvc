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

import javax.validation.constraints.Size;


/**
 * @author Andrea Funto'
 */
public class User {

	/**
	 * The user's name.
	 */
	@Size(min = 2, max = 20)
	private String name;
	
	/**
	 * The user's family name.
	 */
	@Size(min = 2, max = 20)
	private String surname;
	
	/**
	 * The user's sex.
	 */	
	private Sex sex;
	
	/**
	 * The user contact info.
	 */
	private Contacts contacts = new Contacts();
	
	/**
	 * The user's address.
	 */
	private Address address = new Address();
	
	/**
	 * the set of music genres the user likes.
	 */
	private String[] music;
	
	/**
	 * Constructor.
	 */
	public User() {
	}

	/**
	 * Returns the value of the field name.
	 *
	 * @return 
	 *   the value of field name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the value of field name.
	 *
	 * @param name 
	 *   the new value for field name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the value of the field surname.
	 *
	 * @return 
	 *   the value of field surname.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Sets the value of field surname.
	 *
	 * @param surname 
	 *   the new value for field surname.
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	/**
	 * Returns the value of the field sex.
	 *
	 * @return 
	 *   the value of field sex.
	 */
	public Sex getSex() {
		return sex;
	}

	/**
	 * Sets the value of field sex.
	 *
	 * @param sex 
	 *   the new value for field sex.
	 */	
	public void setSex(String sex) {
		this.sex = Sex.fromString(sex);
	}

	/**
	 * Returns the value of the field contacts.
	 *
	 * @return 
	 *   the value of field contacts.
	 */
	public Contacts getContacts() {
		return contacts;
	}

	/**
	 * Sets the value of field contacts.
	 *
	 * @param contacts 
	 *   the new value for field contacts.
	 */
	public void setContacts(Contacts contacts) {
		this.contacts = contacts;
	}

	/**
	 * Returns the value of the field address.
	 *
	 * @return 
	 *   the value of field address.
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * Sets the value of field address.
	 *
	 * @param address 
	 *   the new value for field address.
	 */
	public void setAddress(Address address) {
		this.address = address;
	}

	/**
	 * Returns the value of the field music.
	 *
	 * @return 
	 *   the value of field music.
	 */
	public String[] getMusic() {
		return music;
	}

	/**
	 * Sets the value of field music.
	 *
	 * @param music 
	 *   the new value for field music.
	 */
	public void setMusic(String[] music) {
		this.music = music;
	}
}
