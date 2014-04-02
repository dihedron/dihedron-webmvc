/**
 * Copyright (c) 2014, Andrea Funto'. All rights reserved.
 * 
 * This file is part of the Zephyr framework ("Zephyr").
 *
 * Zephyr is free software: you can redistribute it and/or modify it under 
 * the terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version.
 *
 * Zephyr is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR 
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more 
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License 
 * along with Zephyr. If not, see <http://www.gnu.org/licenses/>.
 */
package org.dihedron.zephyr.protocol;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Andrea Funto'
 */
public class ConversationTest {


	/**
	 * Test method for {@link org.dihedron.zephyr.protocol.Conversation#getConversationId(java.lang.String)}.
	 */
	@Test
	public void testGetConversationId() {
		String id = Conversation.getConversationId("conversation:key");
		assertTrue(id != null);
		assertTrue(id.equals("conversation"));
	}
	
	/**
	 * Test method for {@link org.dihedron.zephyr.protocol.Conversation#getValueId(java.lang.String)}.
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
