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
package org.dihedron.zephyr.actions;

import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.renderers.impl.JspRenderer;

/**
 * @author Andrea Funto'
 */
public class Result {

	/**
	 * The default renderer to be used for results that do not have a renderer
	 * type specified explicitly in the annotations.
	 */
	public static final String DEFAULT_RENDERER_ID = JspRenderer.ID;

	/**
	 * The result identifier (e.g. "error", "success").
	 */
	private String id;

	/**
	 * The type of result; if not overridden the default is assumed to be "auto".
	 */
	private String rendererId = DEFAULT_RENDERER_ID;

	/**
	 * The data used by the renderer; this can be a URL, the name of a field in
	 * the action class or an attribute in the request, portlet o application
	 * scope, or anything else for custom renderers.
	 */
	private String data;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *   the result identifier (e.g. "success").
	 * @param rendererId
	 *   the renderer of the result; by default it will be the "auto" renderer.
	 * @param data
	 *   the data to be used by the renderer, e.g. the URL of the JSP or servlet 
	 *   providing the action's view (for "jsp" renderers), the name of a bean, etc.
	 */
	public Result(String id, String rendererId, String data) {
		this.id = id;
		this.rendererId = Strings.isValid(rendererId) ? rendererId : DEFAULT_RENDERER_ID;
		this.data = data;
	}

	/**
	 * Retrieves the result's identifier.
	 * 
	 * @return 
	 *   the result's identifier (e.g. "success").
	 */
	public String getId() {
		return id;
	}

	/**
	 * Retrieves the renderer of result (JSP, JSON...).
	 * 
	 * @return 
	 *   the renderer of the result (JSP, XML, JSON...).
	 */
	public String getRendererId() {
		return rendererId;
	}

	/**
	 * Returns the data used by the renderer to return a meaningful result, e.g
	 * the URL of the JSP or servlet that will provide the actions' view for
	 * "jsp" renderers, or the name of the attribute (the bean) to be rendered
	 * as JSON or XML.
	 * 
	 * @return 
	 *   the data to be passed on to the given renderer, e,g, the URL of the JSP 
	 *   or servlet that will provide the actions' view for "jsp" renderers.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Returns a pretty-printed string representation of the object.
	 * 
	 * @return 
	 *   a pretty-printed string representation of the object.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder("result {\n");
		builder.append("  id      ('").append(id).append("')\n");
		builder.append("  renderer('").append(rendererId).append("')\n");
		builder.append("  data    ('").append(data).append("')\n");
		builder.append("}\n");
		return builder.toString();
	}
}
