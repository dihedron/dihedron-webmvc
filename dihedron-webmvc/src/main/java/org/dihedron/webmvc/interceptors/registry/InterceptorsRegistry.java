/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors.registry;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.dihedron.core.strings.Strings;
import org.dihedron.core.url.URLFactory;
import org.dihedron.core.xml.DOMReader;
import org.dihedron.patterns.activities.exceptions.InvalidArgumentException;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.InterceptorStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author Andrea Funto'
 */
public class InterceptorsRegistry {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(InterceptorsRegistry.class);
	
	/**
	 * Whether the input XML file should be validated.
	 */
	private static final boolean VALIDATE_XML = false;
	
	/**
	 * The name of the interceptors configuration schema file.
	 */
	public static final String INTERCEPTORS_CONFIG_XSD = "classpath:org/dihedron/webmvc/interceptors.xsd";
	
	/**
	 * The name of the file declaring the default interceptor stack.
	 */
	public static final String DEFAULT_INTERCEPTORS_CONFIG_XML = "classpath:org/dihedron/webmvc/default-interceptors.xml";

	/**
	 * The name of the default interceptor stack ("default").
	 */
	public static final String DEFAULT_INTERCEPTOR_STACK = "default";
		
	/**
	 * The map of registered interceptor stacks.
	 */
	private Map<String, InterceptorStack> stacks = Collections.synchronizedMap(new HashMap<String, InterceptorStack>());
	
	/**
	 * Initialises the interceptors configuration by parsing the input configuration 
	 * file as read from the given URL; URLs are interpreted using {@link URLFactory},
	 * so they can include classpath resources. If the XML input stream is not valid, 
	 * it returns without complaining in order to make interceptors registry 
	 * configuration optional.
	 * 
	 * @param specification
	 *   a string representing the XML resource to be loaded; it can be any web
	 *   URL, or the path of a resource on the classpath.
	 * @throws WebMVCException
	 *   if the resource cannot be located or read.  
	 * @see URLFactory
	 */
	public void load(String specification) throws WebMVCException {
		try {			
			logger.info("loading the interceptors configuration from '{}'", specification);
			DOMReader.loadDocument(specification, INTERCEPTORS_CONFIG_XSD, new InterceptorsRegistryHandler(stacks), VALIDATE_XML); 
		} catch (InvalidArgumentException e) {
			logger.warn("no valid stream to read interceptors' stacks from, it may be OK (check if this is what you want)", e);
			// gracefully handle this condition by swallowing the error
		} catch(IOException | SAXException | ParserConfigurationException e) {
			logger.error("error loading interceptors' stacks", e);
			throw new WebMVCException("Error loading interceptors stacks", e);
		} catch (Exception e) {
			logger.error("error loading interceptors' stacks", e);
			throw new WebMVCException("Error loading parsing interceptors stacks", e);
		}
	}
	
	/**
	 * Returns whether a stack exists that corresponds to the given stack id.
	 * 
	 * @param id
	 *   the id of the stack being checked.
	 * @return
	 *   {@code true} if the stack exists, {@code false} otherwise.
	 */
	public boolean hasStack(String id) {
		return Strings.isValid(id) && stacks.containsKey(id);
	}
	
	/**
	 * Retrieves the stack corresponding to the given id.
	 *  
	 * @param id
	 *   the id of the stack to be retrieved.
	 * @return
	 *   the stack, or null if none found.
	 */
	public InterceptorStack getStack(String id) {
		return stacks.get(id);
	}
		
	/**
	 * Retrieves the stack corresponding to the given id.
	 *  
	 * @param stackId
	 *   the id of the stack to be retrieved.
	 * @return
	 *   the stack, or the default stack if not found.
	 */
	public InterceptorStack getStackOrDefault(String stackId) {
		InterceptorStack stack = null;
		if(Strings.isValid(stackId)) {
			stack = stacks.get(stackId);
		}
		if(stack == null) {
			stack = stacks.get(DEFAULT_INTERCEPTOR_STACK);
		}
		return stack;
	}
	
	/**
	 * Returns a pretty printed, complex representation of the object as a string.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(Entry<String, InterceptorStack> entry : stacks.entrySet()) {
			buffer.append("------------ STACK ------------\n");
			buffer.append(entry.getValue().toString());
		}
		buffer.append("-------------------------------\n");
		return buffer.toString();
	}
}
