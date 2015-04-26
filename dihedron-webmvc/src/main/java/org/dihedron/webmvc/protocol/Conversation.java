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
package org.dihedron.webmvc.protocol;

import java.util.List;

import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;

/**
 * @author Andrea Funto'
 */
public class Conversation {
	
	/**
	 * The ID for unnamed conversations.
	 */
	private static final String DEFAULT_CONVERSATION_ID = "org.dihedron.webmvc.default_conversation";	

	/**
	 * Given a conversational value identifier (in the form {@code conversation:key}),
	 * it extracts and returns the id of the conversation ({@code conversation} in
	 * the example.
	 * 
	 * @param key
	 *   the conversational value identifier.
	 * @return
	 *   the id of the conversation.
	 */
	public static String getConversationId(String key) {
		if(Strings.isValid(key) && CONVERSATION_PATTERN.matches(key)) {
			List<String[]> matches = CONVERSATION_PATTERN.getAllMatches(key);
			String id = matches.get(0)[0];
			if(Strings.isValid(id)) {
				return id.trim();
			}
		}
		return DEFAULT_CONVERSATION_ID;
	}

	/**
	 * Given a conversational value identifier (in the form {@code conversation:key}),
	 * it extracts and returns the id of the value within the conversation 
	 * ({@code key} in the example.
	 * 
	 * @param key
	 *   the conversational value identifier.
	 * @return
	 *   the id of the value within the given conversation.
	 */
	public static String getValueId(String key) {
		if(Strings.isValid(key) && CONVERSATION_PATTERN.matches(key)) {
			List<String[]> matches = CONVERSATION_PATTERN.getAllMatches(key);
			String id = matches.get(0)[1];
			if(Strings.isValid(id)) {
				return id.trim();
			}
		}
		return null;
	}
	
	/**
	 * The regular expression used to pull apart conversation and value id's.
	 */
	private static final Regex CONVERSATION_PATTERN = new Regex("([^\\:]*)\\:(.*)");
}
