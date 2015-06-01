/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.interceptors.registry;

import java.util.List;

import org.dihedron.core.strings.Strings;
import org.dihedron.core.xml.DOM;
import org.dihedron.core.xml.DOMHandler;
import org.dihedron.core.xml.DOMHandlerException;
import org.dihedron.webmvc.interceptors.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andrea Funto'
 */
public class DomainsRegistryHandler implements DOMHandler {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(DomainsRegistryHandler.class);

	/**
	 * A reference to the domains to be populated.
	 */
	private List<Domain> domains;
	
	/**
	 * A reference to the interceptors registry, used by this reader
	 * to check that any stack referenced by a new domain is valid,
	 * and bail out if not so.
	 */
	private InterceptorsRegistry interceptors;
	
	/**
	 * Constructor.
	 * 
	 * @param domains
	 *   the domain to be populated.
	 * @param interceptors
	 *   a reference to the interceptors registry, used to check that domain
	 *   and stack information is consistent across configuration files. 
	 */
	DomainsRegistryHandler(List<Domain> domains, InterceptorsRegistry interceptors) {
		this.domains = domains;
		this.interceptors = interceptors;
	}
	
	/**
	 * @see org.dihedron.core.xml.DOMHandler#onDocument(org.w3c.dom.Document)
	 */
	@Override
	public void onDocument(Document document) throws DOMHandlerException {
		
		for(Element e : DOM.getDescendantsByTagName(document, "domain")) {
			
			// domain and stack id cannot be null
			String domainId = e.getAttribute("id");
			String stackId = e.getAttribute("stack");			

			// the referenced stack may be non existing, though....
			if(!interceptors.hasStack(stackId)) {					
				logger.error("invalid stack '{}' specified in domain '{}'", domainId);
				throw new DOMHandlerException("Domain '" + domainId + "' references the invalid stack '" + stackId + "'");
			}
			
			// now check that the pattern is ok
			String pattern = DOM.getElementText(e);
			if(!Strings.isValid(pattern)) {
				logger.error("invalid resource pattern '{}' for domain '{}'", pattern, domainId);
				throw new DOMHandlerException("Invalid resource pattern for domain '" + domainId + "'");
			}
			logger.debug("adding domain '{}' (referencing stack '{}'), applied to '{}'", domainId, stackId, pattern);
			domains.add(new Domain(domainId, stackId, pattern));
		}
	}
}
