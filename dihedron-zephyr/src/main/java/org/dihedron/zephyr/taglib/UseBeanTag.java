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
package org.dihedron.zephyr.taglib;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.ActionContext;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.protocol.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Andrea Funto'
 */
public class UseBeanTag extends TagSupport {

	
	/**
	 * The visibility of the new variable: the whole page or only the nested tags.
	 * 
	 * @author Andrea Funto'
	 */
	private enum Visibility {
		
		/**
		 * The variable will be visible from the definition point until the end
		 * of the current JSP page.
		 */
		PAGE("page"),
		
		/**
		 * The variable will only be visible from the start tag until the matching
		 * closing tag.
		 */
		NESTED("nested");
		
		/**
		 * Constructor.
		 *
		 * @param visibility
		 *   the visibility scope of the new variable.
		 */
		private Visibility(String visibility) {
			this.visibility = visibility;
		}
		
		/**
		 * Tries to convert a textual representation into the proper enumeration 
		 * constant.
		 * 
		 * @param text
		 *   the textual representation of the enumeration constant.
		 * @return
		 */
		public static Visibility fromString(String text) {
			if (text != null) {
				for (Visibility v : Visibility.values()) {
					if (text.equalsIgnoreCase(v.visibility)) {
						return v;
					}
				}
			}
			throw new IllegalArgumentException("No enumeration value matching '" + text + "'");
		}		
		
		
		@Override
		public String toString() {
			return visibility;
		}
		
		/**
		 * The visibility scope of the new variable, as a string.
		 */
		private String visibility;
	}
	
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = -8197748548084293389L;

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(UseBeanTag.class);
	
	/**
	 * The default visibility scope of the new variable.
	 */
	private static final Visibility DEFAULT_VISIBILITY = Visibility.PAGE;
	
	/**
	 * The name of the attribute to be made available to the page and EL. 
	 */
	private String name;
	
	/**
	 * The context in which the attribute/parameter is supposed to be available;
	 * it can have the following values:<ul>
	 * <li>{@code form}: the bean is supposed to be among the request parameters,</li>
	 * <li>{@code request}: the attribute is supposed to be in the request</li>
	 * <li>{@code session}: the attribute is supposed to be in the current user's 
	 * session</li>
	 * <li>{@code sticky}: the attribute is supposed to be available on the server
	 * as long as this is not restarted, whether or not the user logged off and on 
	 * again</li>
	 * <li>{@code application}: the attribute is supposed to be in the application
	 * scope, available to the all the servlet in the application and to all users,
	 * regardless of their sessions</li>
	 * <li>{@code configuration}: the attribute is supposed to be among the 
	 * configuration values</li>
	 * <li>{@code system}: the attribute is supposed to be among the system
	 * properties </li> 
	 * <li>{@code environment}: the attribute is supposed to be among the system
	 * environment variables</li>
	 * </ol>
	 */
	private Scope[] scopes = Scope.ALL;
		
	/**
	 * The name of the destination variable.
	 */
	private String var;
	
	/**
	 * The class (type) of the destination variable.
	 */
	private String type;
	
	/**
	 * The lexical scope of the declared variable; it can have the following values:<ul>
	 * <li>{@code nested}: the variable will be visible only between the start 
	 * and end tags,</li>
	 * <li>{@code page}; the variable will be available from this point until the
	 * end of the page.</li>
	 * </ul>
	 */
	@SuppressWarnings("unused")
	private Visibility visibility = DEFAULT_VISIBILITY;	
	
	/**
	 * Sets the name of the attribute to be made available to the page and EL.
	 * 
	 * @param name
	 *   the name of the attribute.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the context in which the attribute/parameter is supposed to be available
	 * and will be looked up.
	 * 
	 * @param context
	 *   the names of the scopes, as a comma-separated list of strings; supported 
	 *   values for the contexts include:<ul>
	 *   <li>render</li>: one of the render parameters;
	 *   <li>request</li>: the bean is among the request attributes;
	 *   <li>portlet</li>: the bean is among the portlet attributes;
	 *   <li>application</li>: the bean is among the application attributes;
	 * <ul>
	 */
	public void setScopes(String context) {
		if(Strings.isValid(context)) {
			Set<Scope> scopes = new LinkedHashSet<Scope>();
			String [] tokens = Strings.split(context, ",");
			for(String token : tokens) {
				scopes.add(Scope.fromString(token));
			}
			this.scopes = (Scope[])scopes.toArray(new Scope[scopes.size()]);
		} else {
			this.scopes = Scope.ALL;
		}		
	}
	
	/**
	 * Sets the name of the destination variable.
	 * 
	 * @param var
	 *   the name of the destination variable.
	 */
	public void setVar(String var) {
		this.var = var;
	}
	
	/**
	 * Sets the type of the destination variable.
	 * 
	 * @param type  
	 *   the type of the destination variable.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * Sets the visibility scope of the new variable.
	 * 
	 * @param visibility
	 *   the visibility scope of the new variable; accepted values include:<ul>
	 *   <li>{@code nested}: the variable will be visible only between the start 
	 *   and end tags,</li>
	 *   <li>{@code page}; the variable will be available from this point until the
	 *   end of the page.</li></ul>
	 */
	public void setVisibility(String visibility) {		
		this.visibility = Visibility.fromString(visibility);
	}	

	/**
	 * Retrieves the appropriate attribute from the given scope and stores it
	 * into the page context under the given variable name.
	 * 
	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
	 */
	@Override
	public int doStartTag() throws JspException {
		Object value = null;
		
		logger.trace("publishing value '{}' from scopes [{}] as variable '{}'", name, Strings.join((Object[])scopes), var);
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		
		for(Scope scope : scopes) {
			switch(scope) {
			case FORM:
				if(type.equals("java.lang.String")) {
					logger.trace("retrieving form parameter");
					value = request.getParameter(name);
				} else if(type.equals("java.lang.String[]")) {
					logger.trace("retrieving form parameters list");
					value = request.getParameterValues(name);
				}
				break;
			case REQUEST:
			case SESSION:
			case STICKY:
			case APPLICATION:
			case CONFIGURATION:
			case SYSTEM:
			case ENVIRONMENT:
				try {
					value = ActionContext.getValue(name, scope);
				} catch (ZephyrException e) {
					logger.error("error retrieving value '" + name + "' from scope '" + scope.name() + "'", e);
				}
				break;
			}
			
			if(value != null) {
				logger.trace("returning attribute '{}' with value '{}' from scope '{}' as variable '{}'", name, value, scope, var);
				pageContext.setAttribute(var, value);
				break;
			}
		}		
		return EVAL_BODY_INCLUDE;
	}
}
