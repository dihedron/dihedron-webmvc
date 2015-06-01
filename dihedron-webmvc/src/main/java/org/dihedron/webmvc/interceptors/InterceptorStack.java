/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc.interceptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.actions.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class InterceptorStack extends ArrayList<Interceptor> {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 1022726179711950362L;
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(InterceptorStack.class);
	
	/**
	 * The interceptor stack identifier.
	 */
	private String id;
	
	/**
	 * The optional list of per-stack global results.
	 */
	private Map<String, Result> globalResults = Collections.synchronizedMap(new HashMap<String, Result>());
	
	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the interceptor stack identifier.
	 */
	public InterceptorStack(String id) {
		this.id = id;
	}
	
	/**
	 * Returns the interceptor stack identifier.
	 * 
	 * @return
	 *   he interceptor stack identifier.
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Adds a global result to the interceptors stack.
	 * 
	 * @param result
	 *   the global result to be added.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public InterceptorStack addGlobalResult(Result result) {
		if(result != null) {
			globalResults.put(result.getId(), result);
		}
		return this;
	}
	
	/**
	 * Adds a global result to the interceptors stack, using the default 
	 * renderer ("JSP").
	 * 
	 * @param id
	 *   the id of the result (e.g. "error", "success").
	 * @param data
	 *   the data to be used by the associated renderer.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public InterceptorStack addGlobalResult(String id, String data) {
		return addGlobalResult(new Result(id, Result.DEFAULT_RENDERER_ID, data));
	}

	/**
	 * Adds a global result to the interceptors stack.
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
	public InterceptorStack addGlobalResult(String id, String rendererId, String data) {
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
	 * Provides a pretty-printed string representation of the object.
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("stack('").append(id).append("')").append(" {\n");
		if(!this.isEmpty()) {
			for(Interceptor interceptor : this ) {
				buffer.append("  interceptor('").append(interceptor.getId()).append("')");
				if(!interceptor.getParameters().isEmpty()) {
					buffer.append(" {\n");
					for(Entry<String, String> entry : interceptor.getParameters().entrySet()) {
						buffer.append("    parameter('").append(entry.getKey()).append("') = '").append(entry.getValue()).append("'\n");
					}
					buffer.append("  }");
				}
				buffer.append("\n");			
			}
			// add optional global results
			if(globalResults != null && !globalResults.isEmpty()) {
				for(Result result : globalResults.values()) {
					buffer.append("  result('").append(result.getId()).append("') = '").append(result.getData()).append("' (type: '").append(result.getRendererId()).append("')\n");
				}				
			}
		}
		buffer.append("}\n");
		return buffer.toString();
	}
}
