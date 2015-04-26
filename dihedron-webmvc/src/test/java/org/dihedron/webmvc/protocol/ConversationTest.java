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

import static org.junit.Assert.assertTrue;

import org.dihedron.webmvc.protocol.Conversation;
import org.junit.Test;

/**
 * @author Andrea Funto'
 */
public class ConversationTest {


	/**
	 * Test method for {@link org.dihedron.webmvc.protocol.Conversation#getConversationId(java.lang.String)}.
	 */
	@Test
	public void testGetConversationId() {
		String id = Conversation.getConversationId("conversation:key");
		assertTrue(id != null);
		assertTrue(id.equals("conversation"));
	}
	
	/**
	 * Test method for {@link org.dihedron.webmvc.protocol.Conversation#getValueId(java.lang.String)}.
	 */
	@Test
	public void testGetValueId() {
		String id = Conversation.getValueId("conversation:key");
		assertTrue(id != null);
		assertTrue(id.equals("key"));
		
		id = Conversation.getValueId("conversation:whatever:contains.dots!and?other#strange@characters");
		assertTrue(id != null);
		assertTrue(id.equals("whatever:contains.dots!and?other#strange@characters"));
	}
	
}
