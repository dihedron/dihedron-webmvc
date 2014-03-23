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
 * along with Torque. If not, see <http://www.gnu.org/licenses/>.
 */
package org.example.demo.part1.actions;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.dihedron.zephyr.ActionContext;
import org.dihedron.zephyr.annotations.Action;
import org.dihedron.zephyr.annotations.In;
import org.dihedron.zephyr.annotations.Invocable;
import org.dihedron.zephyr.annotations.Out;
import org.dihedron.zephyr.annotations.Result;
import org.dihedron.zephyr.aop.$;
import org.dihedron.zephyr.protocol.Scope;
import org.dihedron.zephyr.renderers.impl.JspRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
@Action(alias="TestAction")
public class MyPrivateActionImpl {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(MyPrivateActionImpl.class);

	@Invocable(
		results =  {
			@Result(value=Action.SUCCESS, renderer=JspRenderer.ID, data="index.jsp")
		}
	)
	public String onSimpleFormSubmission(
			@In(value="message", from=Scope.FORM) String message,
			@Out(value="echoGET", to=Scope.REQUEST) $<String> echoGet,
			@Out(value="echoPOST", to=Scope.REQUEST) $<String> echoPost
		) {
		logger.trace("business method invoked with inbound message: '{}'", message);
		switch(ActionContext.getHttpMethod()) {
		case GET:
			if(message!= null) {
				echoGet.set(new StringBuilder(message).reverse().toString());
				echoPost.reset(true);
			} 
			break;
		case POST:
			if(message!= null) {
				echoPost.set(new StringBuilder(message).reverse().toString());
				echoGet.reset(true);
			}
			break;	
		default:
			logger.error("unsupported method: {}", ActionContext.getHttpMethod().name());
		}
		return Action.SUCCESS;
	}
	
	@Invocable(
		results =  {
			@Result(value=Action.SUCCESS, renderer=JspRenderer.ID, data="index.jsp")
		}
	)
	public String onComplexFormSubmission(
			@In(value="name", from=Scope.FORM) @Min(3) @Max(20) String name,
			@In(value="surname", from=Scope.FORM) @Min(3) @Max(20) String surname,
			@In(value="phone", from=Scope.FORM) String phone,
			@In(value="email", from=Scope.FORM) String email,
			@In(value="street", from=Scope.FORM) String street,
			@In(value="number", from=Scope.FORM) String number,
			@In(value="zip", from=Scope.FORM) String zip,
			@In(value="town", from=Scope.FORM) String town,
			@Out(value="user", to=Scope.REQUEST) $<String> user
	) {
		
		StringBuilder buffer = new StringBuilder();
		buffer.append("{\n");
		buffer.append("\t'name': '").append(name).append("',\n");
		buffer.append("\t'surname': '").append(surname).append("',\n");
		buffer.append("\t'phone': '").append(phone).append("',\n");
		buffer.append("\t'email': '").append(email).append("',\n");
		buffer.append("\t'street': '").append(street).append("',\n");
		buffer.append("\t'number': '").append(number).append("',\n");
		buffer.append("\t'zip': '").append(zip).append("',\n");
		buffer.append("\t'town': '").append(town).append("'\n");
		buffer.append("}\n");
		user.set(buffer.toString());
		
		return Action.SUCCESS;
	}
	
}
