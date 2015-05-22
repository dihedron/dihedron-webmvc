/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.interceptors.registry;

import java.util.List;
import java.util.Map;

import org.dihedron.core.strings.Strings;
import org.dihedron.core.xml.DOM;
import org.dihedron.core.xml.DOMHandler;
import org.dihedron.core.xml.DOMHandlerException;
import org.dihedron.webmvc.interceptors.Interceptor;
import org.dihedron.webmvc.interceptors.InterceptorStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Andrea Funto'
 */
public class InterceptorsRegistryHandler implements DOMHandler {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(InterceptorsRegistryHandler.class);

	/**
	 * A reference to the stacks to be populated.
	 */
	private Map<String, InterceptorStack> stacks;
	
	/**
	 * Constructor.
	 * 
	 * @param stacks
	 *   the stacks to be populated.
	 */
	InterceptorsRegistryHandler(Map<String, InterceptorStack> stacks) {
		this.stacks = stacks;
	}
	
	/**
	 * @see org.dihedron.core.xml.DOMHandler#onDocument(org.w3c.dom.Document)
	 */
	@Override
	public void onDocument(Document document) throws DOMHandlerException {
		
		try {
			for(Element s : DOM.getDescendantsByTagName(document, "stack")) {
				
				String stackId = s.getAttribute("id");
				InterceptorStack stack = new InterceptorStack(stackId);
				logger.trace("interceptor stack '{}' ", stack.getId()); 
				
				// load interceptors
				//for(Element i : DOM.getChildrenByTagName(s, "interceptor")) {
				for(Element i : DOM.getDescendantsByTagName(s, "interceptor")) {
					String interceptorId = i.getAttribute("id");
					String interceptorClass = i.getAttribute("class");
					Interceptor interceptor = (Interceptor)Class.forName(interceptorClass).newInstance();
					interceptor.setId(stackId, interceptorId);
					logger.trace(" + interceptor '{}' ", interceptorId);
					
					for(Element parameter : DOM.getChildrenByTagName(i, "parameter")) {
						//String key = DOM.getElementText(DOM.getFirstChildByTagName(parameter, "key"));							
						//String value = DOM.getElementText(DOM.getFirstChildByTagName(parameter, "value"));
						String key = parameter.getAttribute("key");
						String value = DOM.getElementText(parameter);
						interceptor.setParameter(key, value);
						logger.trace("   + parameter '{}' has value '{}'", key, value);
					}	
					
					interceptor.initialise();
					
					stack.add(interceptor);
				}
				
				// load global results
				List<Element> rs = DOM.getDescendantsByTagName(s, "result");
				if(rs != null && !rs.isEmpty()) {
					for(Element r : rs) {
						String resultId = r.getAttribute("value");
						String rendererId = r.getAttribute("renderer");
						String data = DOM.getElementText(r);
						if(Strings.isValid(rendererId)) {
							stack.addGlobalResult(resultId, rendererId, data);
						} else {
							stack.addGlobalResult(resultId, data);
						}
					}
				}
				
				stacks.put(stack.getId(), stack);
			}
			logger.info("configuration loaded");
		} catch(InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.error("error parsing interceptors registry configuration", e);
			throw new DOMHandlerException("error parsing interceptors registry configuration", e);					
		}
	}

}
