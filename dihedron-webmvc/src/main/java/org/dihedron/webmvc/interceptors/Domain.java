/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.actions.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class representing the resources' domain.
 * 
 * @author Andrea Funto'
 */
public class Domain {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Domain.class);
	
	/**
	 * The domain identifier.
	 */
	private String id;
	
	/**
	 * The domain interceptors stack.
	 */
	private String stack;
	
	/**
	 * The pattern that all domain resources must comply with.
	 */
	private Regex pattern;
	
	/**
	 * The optional list of per-domain global results.
	 */
	private Map<String, Result> globalResults = Collections.synchronizedMap(new HashMap<String, Result>());		
	
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
	public String getStackId() {
		return stack;
	}
	
	/**
	 * Adds a global result to the domain.
	 * 
	 * @param result
	 *   the global result to be added.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public Domain addGlobalResult(Result result) {
		if(result != null) {
			globalResults.put(result.getId(), result);
		}
		return this;
	}
	
	/**
	 * Adds a global result to the domain, using the default renderer ("JSP").
	 * 
	 * @param id
	 *   the id of the result (e.g. "error", "success").
	 * @param data
	 *   the data to be used by the associated renderer.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public Domain addGlobalResult(String id, String data) {
		return addGlobalResult(new Result(id, Result.DEFAULT_RENDERER_ID, data));
	}

	/**
	 * Adds a global result to the domain.
	 * 
	 * @param id
	 *   the id of the result (e.g. "error", "success").
	 * @param rendererId
	 *   the id of the renderer that will serve the result.
	 * @param data
	 *   the data to be used by the associated renderer.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public Domain addGlobalResult(String id, String rendererId, String data) {
		if(Strings.areValid(id, data, rendererId)) {
			return addGlobalResult(new Result(id, rendererId, data));
		}
		return this;
	}
	
    /**
     * Returns the <code>Result</code> object corresponding to the given
     * result string, or null if none found.
     *
     * @param resultId 
     *   a result string (e.g. "success", "error").
     * @return 
     *   the <code>Result</code> object corresponding to the given result string.
     */
    public Result getGlobalResult(String resultId) {
        assert (Strings.isValid(resultId));        
        Result result = globalResults.get(resultId);
        logger.trace("result for id '{}' is {}", resultId, result != null ? "\n" + result : "unconfigured");
        return result;
    }	
	
	/**
	 * Returns whether the given resource belongs to this domain.
	 * 
	 * @param resource
	 *   the path to the requested resource.
	 * @return
	 *   whether the given resource belongs to this domain.
	 */
	public boolean protects(String resource) {
		return Strings.isValid(resource) && pattern.matches(resource); 
	}	

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("domain('").append(id).append("') {\n");
		buffer.append("  ").append("stack('").append(stack).append("')\n");
		buffer.append("  ").append("pattern('").append(pattern).append("')\n");
		// add optional global results
		if(globalResults != null && !globalResults.isEmpty()) {
			for(Result result : globalResults.values()) {
				buffer.append("  ").append("result('").append(result.getId()).append("') = '").append(result.getData()).append("' (type: '").append(result.getRendererId()).append("')\n");
			}				
		}		
		buffer.append("}");		
		return buffer.toString();
	}
}
