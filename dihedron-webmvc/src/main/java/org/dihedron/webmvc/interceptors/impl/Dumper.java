/**
 * Copyright (c) 2012, 2013, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Strutlets framework ("Strutlets").
 *
 * Strutlets is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Strutlets is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Strutlets. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dihedron.webmvc.interceptors.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dihedron.core.reflection.Types;
import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.StringTokeniser;
import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.ActionContext;
import org.dihedron.webmvc.ActionInvocation;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.Interceptor;
import org.dihedron.webmvc.protocol.Conversation;
import org.dihedron.webmvc.protocol.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class Dumper extends Interceptor {

	/**
	 * The name of the parameter containing a regular expression that, if matched, 
	 * prevents the given parameter from the being output.
	 */
	public static final String EXCLUDE_PARAMETER = "exclude";
	
	/**
	 * The name of the parameter containing the list of scopes to be dumped.
	 */
	public static final String SCOPES_PARAMETER = "scopes";
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(Dumper.class);
	
	private static final int SECTION_HEADER_LENGTH = 64;
	
	private static final char SECTION_HEADER_PADDING = '=';
	private static final char CONVERSATION_HEADER_PADDING = '-';
	
	private static final String SECTION_FOOTER = "================================================================";

	private Regex regex = null;
	
	private List<Scope> scopes = new ArrayList<>();
	
	@Override
	public void initialise() {
		String exclude = getParameter(EXCLUDE_PARAMETER);
		logger.trace("excluding properties matching /{}/", regex);
		if(Strings.isValid(exclude)) {
			regex = new Regex(exclude);
		}
		
		
		String description = getParameter("scopes");
		if(description != null) {
			String [] tokens = new StringTokeniser(",").setTrimSpaces(true).setSkimEmpty(true).tokenise(description);
			for(String token : tokens) {
				Scope scope = Scope.fromString(token);
				if(scope != null) {
					scopes.add(scope);
				}
			}
		} else {			
			scopes.addAll(Arrays.asList(Scope.ALL));
		}
	}
	
	/**
	 * Dumps the various scopes before and after the action invocation. 
	 * 
	 * @param invocation
	 *   the current action invocation.
	 * @return
	 *   the result of the nested components' execution.
	 * @see 
	 *   org.dihedron.strutlets.interceptors.Interceptor#intercept(org.dihedron.strutlets.ActionInvocation)
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws WebMVCException {
		StringBuilder builder = new StringBuilder();
		for(Scope scope : scopes) {
			dumpValues(scope, builder);
		}
		builder.append(SECTION_FOOTER).append("\n");
		logger.debug("action context BEFORE execution:\n{}", builder);
		builder.setLength(0);
		String result = invocation.invoke();
		for(Scope scope : scopes) {
			dumpValues(scope, builder);
		}
		builder.append(SECTION_FOOTER).append("\n");
		logger.debug("action context AFTER execution:\n{}", builder);
		return result;		
	}
		
	/**
	 * Dumps any value available in the given scope to the provided buffer.
	 * 
	 * @param scope
	 *   the scope whose values are being dumped.
	 * @param builder
	 *   the buffer used for output accumulation.
	 */
	private void dumpValues(Scope scope, StringBuilder builder) throws WebMVCException {
		Set<String> names = ActionContext.getValueNames(scope);
		builder.append(Strings.centre(" " + scope.name() + " SCOPE ", SECTION_HEADER_LENGTH, SECTION_HEADER_PADDING)).append("\n");
		if(scope == Scope.CONVERSATION) {
			Map<String, List<String>> conversations = new HashMap<>();
			for(String name : names) {
				String id = Conversation.getConversationId(name);
				String key = Conversation.getValueId(name);
				List<String> keys = conversations.get(id);
				if(keys == null) {
					keys = new ArrayList<>();
					conversations.put(id, keys);
				}
				keys.add(key);
			}
			for(String conversation : conversations.keySet()) {
				builder.append(Strings.centre(" " + conversation + " ", SECTION_HEADER_LENGTH, CONVERSATION_HEADER_PADDING)).append("\n");
				for(String key : conversations.get(conversation)) {
					dumpValue(conversation + ":" + key, ActionContext.getValue(conversation + ":" + key, Scope.CONVERSATION), builder);
				}
			}
		} else {		
			if(names != null) {
				for(String name : names) {
					if(regex == null || !regex.matches(name)) { 
						Object value = ActionContext.getValue(name, scope);
						dumpValue(name, value, builder);
					}
				}
			}
		}
	}	
	
	private void dumpValue(String name, Object value, StringBuilder builder) {
		String string = null;
		if(value != null) {
			if(Types.isArray(value)) {
				string = "[" + Strings.join((Object[])value) + "]";
			} else {
				string = value.toString();
			}
		}
		builder.append("'").append(name).append("' = '").append(string).append("' (type: ").append(value != null ? value.getClass().getName() : "n.a.").append(")\n");		
	}
}
