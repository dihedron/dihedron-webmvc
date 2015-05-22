/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors;

import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;

/**
 * A class representing the resources' domain.
 * 
 * @author Andrea Funto'
 */
public class Domain {
	
	/**
	 * The domain identifier.
	 */
	private String id;
	
	/**
	 * The domain stack.
	 */
	private String stack;
	
	/**
	 * The pattern that all domain resources must comply with.
	 */
	private Regex pattern;
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the domain id.
	 * @param stack
	 *   the stack that applies to this domain.
	 * @param pattern
	 *   the pattern with which domain resources must comply.
	 */
	public Domain(String id, String stack, String pattern) {
		this.id = id;
		this.stack = stack;
		this.pattern = new Regex(pattern);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the domain id.
	 * @param stack
	 *   the stack that applies to this domain.
	 * @param pattern
	 *   the pattern with which domain resources must comply.
	 */
	public Domain(String id, String stack, Regex pattern) {
		this.id = id;
		this.stack = stack;
		this.pattern = pattern;
	}
	
	/**
	 * Returns the domain id.
	 * 
	 * @return
	 *   the domain id.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Returns the id of the stack that applies to this domain's 
	 * resources.
	 * 
	 * @return
	 *   the id of the interceptors stack.
	 */
	public String getStack() {
		return stack;
	}
	
	/**
	 * Returns whether the given resource belongs to this domain.
	 * 
	 * @param resource
	 *   the path to the requested resource.
	 * @return
	 *   whether the given resource belongs to this domain.
	 */
	public boolean contains(String resource) {
		return Strings.isValid(resource) && pattern.matches(resource); 
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO:
		StringBuilder buffer = new StringBuilder();
		buffer.append("domain('").append(id).append("') {\n'");
		buffer.append("  ");
		
		
		return buffer.toString();
	}
}
