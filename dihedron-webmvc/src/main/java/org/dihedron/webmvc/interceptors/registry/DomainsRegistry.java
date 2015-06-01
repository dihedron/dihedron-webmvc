/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.dihedron.core.strings.Strings;
import org.dihedron.core.url.URLFactory;
import org.dihedron.core.xml.DOMReader;
import org.dihedron.patterns.activities.exceptions.InvalidArgumentException;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * @author Andrea Funto'
 */
public class DomainsRegistry {
	
//	/**
//	 * The internal DOM parser error handler.
//	 * 
//	 * @author Andrea Funto'
//	 */
//	public class ParserErrorHandler implements ErrorHandler {
//	    public void warning(SAXParseException e) throws SAXException {
//	        logger.warn(e.getMessage(), e);
//	    }
//
//	    public void error(SAXParseException e) throws SAXException {
//	        logger.error(e.getMessage(), e);
//	    }
//
//	    public void fatalError(SAXParseException e) throws SAXException {
//	        logger.error(e.getMessage(), e);
//	    }
//	}	

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DomainsRegistry.class);
	
	/**
	 * Whether the input XML file should be validated.
	 */
	private static final boolean VALIDATE_XML = false;
		
	/**
	 * The name of the domains configuration schema file.
	 */
	public static final String DOMAINS_CONFIG_XSD = "classpath:org/dihedron/webmvc/domains.xsd";
	
	/**
	 * The name of the file declaring the default domains.
	 */
	public static final String DEFAULT_DOMAINS_CONFIG_XML = "classpath:org/dihedron/webmvc/default-domains.xml";

	/**
	 * The name of the default domain ("default").
	 */
	public static final String DEFAULT_DOMAIN = "default";
	
	/**
	 * The list of registered domains.
	 */
	private List<Domain> domains = Collections.synchronizedList(new ArrayList<Domain>());
		
	/**
	 * Initialises the domains configuration by parsing the input configuration file
	 * as read from the given URL; URLs are interpreted using {@link URLFactory},
	 * so they can include classpath resources. If the XML input stream is not 
	 * valid, it returns without complaining in order to make domains registry 
	 * configuration optional.
	 * 
	 * @param specification
	 *   a string representing the XML resource to be loaded; it can be any web
	 *   URL, or the path of a resource on the classpath.
	 * @param interceptors
	 *   a reference to the interceptors registry, used to check that information on
	 *   stacks provided at domain level is consistent with what's really available 
	 *   in the registry.
	 * @throws WebMVCException
	 *   if the resource cannot be located or read.  
	 * @see URLFactory
	 */
	public void load(String specification, InterceptorsRegistry interceptors) throws WebMVCException {
		try {			
			logger.info("loading the domain configuration from '{}'", specification);
			DOMReader.loadDocument(specification, DOMAINS_CONFIG_XSD, new DomainsRegistryHandler(domains, interceptors), VALIDATE_XML); 
		} catch (InvalidArgumentException e) {
			logger.warn("no valid stream to read domains from, it may be OK (check if this is what you want)", e);
			// gracefully handle this condition by swallowing the error
		} catch(IOException | SAXException | ParserConfigurationException e) {
			logger.error("error loading domains", e);
			throw new WebMVCException("Error loading domains stacks", e);
		} catch (Exception e) {
			logger.error("error loading domains", e);
			throw new WebMVCException("Error loading parsing domains", e);
		}
	}	

	
	/**
	 * Looks up the matching domain for the given id. 
	 * 
	 * @param id
	 *   the id of the domain to look up.
	 * @return
	 *   the domain, if found; {@code null} otherwise.
	 */
	public Domain findDomainById(String id) {
		if(!Strings.isValid(id)) {
			logger.warn("invalid domain id, no matching domain");
			return null;
		}
		for(Domain domain : domains) {
			if(domain.getId().equals(id)) {
				return domain;
			}
		}
		return null;
	}
	
	/**
	 * Looks up the matching domain for the given resource; the first
	 * matching domain is returned. 
	 * 
	 * @param resource
	 *   the resource whose domain is to be identified.
	 * @return
	 *   the domain, if found; {@code null} otherwise.
	 */
	public Domain findDomainByResource(String resource) {
		if(!Strings.isValid(resource)) {
			logger.warn("invalid resource, no matching domain");
			return null;
		}
		//logger.warn("testing against resource '{}'", resource);
		for(Domain domain : domains) {
			if(domain.protects(resource)) {
				return domain;
			}
		}
		return null;
	}
		
	/**
	 * Returns a pretty printed, complex representation of the object as a string.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for(Domain domain : domains) {
			buffer.append("\n----------- DOMAINS -----------\n");
			buffer.append(domain.toString());
		}
		buffer.append("\n-------------------------------\n");
		return buffer.toString();
	}
}
