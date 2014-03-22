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

package org.dihedron.zephyr.targets;

import org.dihedron.commons.regex.Regex;
import org.dihedron.commons.strings.Strings;
import org.dihedron.zephyr.annotations.Action;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * The class representing in an unique way an invocation target.
 * 
 * @author Andrea Funto'
 */
public class TargetId {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(TargetId.class);

	/**
	 * Checks whether the given string represents a valid target specification.
	 * 
	 * @param targetId
	 *   a string to be checked for compliance with target id specifications; 
	 *   valid target identifiers are in the form "MyAction!myMethod".
	 * @return 
	 *   whether the given string is a viable representation of a target id.
	 */
	public static boolean isValidTargetId(String targetId) {
		return Strings.isValid(targetId) && REGEX.matches(targetId);
	}

	/**
	 * The name of the action (a symbolic name for a Java class) on which the 
	 * business method resides.
	 */
	private String actionName;

	/**
	 * The name of the actual business method.
	 */
	private String methodName;

	/**
	 * Constructor.
	 * 
	 * @param targetId
	 *   a string representing the target identifier, with or without the method 
	 *   name; if no method is specified, the default method ("execute") is 
	 *   automatically selected. This parameter must not be null or blank.
	 * @throws ZephyrException
	 *   if the target id is blank or null.
	 */
	public TargetId(String targetId) throws ZephyrException {
		this.actionName = getActionName(targetId);
		this.methodName = getMethodName(targetId);
	}

	/**
	 * Constructor.
	 * 
	 * @param actionName
	 *   the name of the action, that is the symbolic or proper name of the class
	 *   on which the business method resides.
	 * @param methodName
	 *   the name of the business method.
	 * @throws ZephyrException
	 *   if the name of the action is null or blank.
	 */
	public TargetId(String actionName, String methodName) throws ZephyrException {
		if (!Strings.isValid(actionName)) {
			logger.error("invalid action name in target id creation");
			throw new ZephyrException("Invalid action name specified");
		}
		this.actionName = actionName.trim();
		this.methodName = Strings.isValid(methodName) ? methodName.trim() : DEFAULT_METHOD_NAME;
	}

	/**
	 * Constructor; the target identifier has two components:
	 * <ol>
	 * <li>The action name, which can be taken from the alias specified in the
	 * <code>@Action</code> annotation, if valid, or the simple name of the
	 * class implementing the target</li>
	 * <li>the name of the method, always taken the way it is</li>
	 * </ol>.
	 * 
	 * @param action
	 *   the action class; if its {@code &at;Action} annotation contains an alias,
	 *   that one is used as the action component of the target identifier,
	 *   otherwise the simple name of the class will be assumed.  
	 * @param method
	 *   the business method.
	 */
	public TargetId(Class<?> action, Method method) {
		Action annotation = action.getAnnotation(Action.class);
		if (Strings.isValid(annotation.alias())) {
			logger.trace("getting target 'action' component for '{}' from alias in annotation: '{}'", action.getSimpleName(), annotation.alias());
			this.actionName = annotation.alias();
		} else {
			logger.trace("getting target 'action' component from class name: '{}'", action.getSimpleName());
			this.actionName = action.getSimpleName();
		}
		this.methodName = method.getName();
	}

	/**
	 * Returns the symbolic name of the action.
	 * 
	 * @return 
	 *   the symbolic name of the action.
	 */
	public String getActionName() {
		return actionName;
	}

	/**
	 * Returns the name of the method.
	 * 
	 * @return 
	 *   the name of the business method.
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Returns the string corresponding to the current target identifier, as a
	 * concatenation of the action name, a bang ("!") and the name of the
	 * method, e.g. {@code MyAction!myMethod}.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return actionName + METHOD_SEPARATOR + methodName;
	}

	/**
	 * Returns the hash code of the current target identifier, to enable use as
	 * a unique key in a map.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * Returns {@code true} if and only if the other object is a
	 * {@code TargetId} and its string representation corresponds in all aspects
	 * to this object's.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		return (other instanceof TargetId && ((TargetId) other).toString().equals(this.toString()));
	}

	/**
	 * The character separating action and methodName names in the target id.
	 */
	public static final String METHOD_SEPARATOR = "!";

	/**
	 * The name of the default <code>ActionInfo</code> methodName.
	 */
	public static final String DEFAULT_METHOD_NAME = "execute";

	/**
	 * A Java regular expression matching a combination of action name and
	 * methodName name; the original expression is
	 * <code>^\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\s*!\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\s*$</code>
	 * and matches names of the form <code>MyAction!myMethod</code>, where the
	 * action identifier complies with the rules for Java class names and the
	 * methodName identifier complies with the best practices for Java methods
	 * (starting with a lowercase alphabetic character, followed by any
	 * alphanumeric character). It may also match the simple name of the action,
	 * an in that case the default method ("execute") is assumed. 
	 */
	private static final Regex REGEX = new Regex("^\\s*([A-Z]{1,}[a-zA-Z0-9]*)(?:\\s*!\\s*([a-z]{1,}[a-zA-Z0-9]{1,})){0,1}\\s*$");

	/**
	 * Given the target id specification in the &lt;action&gt;!&lt;methodName&gt;
	 * form (e.g. "MyAction!myMethod", where the methodName part is optional),
	 * returns the name of the action ("MyAction" in the above example).
	 * 
	 * @param targetId
	 *   the target id specification, including the method name or not.
	 * @return 
	 *   the part of the name representing the action name.
	 * @throws ZephyrException
	 *   if the target is a null or blank string.
	 */
	private static String getActionName(String targetId) throws ZephyrException {
		String actionName = null;
		if (Strings.isValid(targetId)) {
			if (targetId.contains(METHOD_SEPARATOR)) {
				actionName = targetId.substring(0, targetId.indexOf(METHOD_SEPARATOR)).trim();
			} else {
				actionName = targetId.trim();
			}
			logger.trace("action name: '{}'", actionName);
			return actionName;
		} else {
			throw new ZephyrException("Invalid target specified");
		}
	}

	/**
	 * Given the target specification in the &lt;action&gt;!&lt;methodName&gt;
	 * form (e.g. "MyAction!myMethod"), returns the name of the methodName
	 * ("myMethod" in the above example); if the name of the methodName is
	 * empty, it returns the default methodName ("execute").
	 * 
	 * @param targetId
	 *   the action target id specification; this may or may not include the
	 *   method name, and in the latter case the default method name ("execute")
	 *   is returned.
	 * @return 
	 *   the method name, or the default method name ("execute") if the target 
	 *   id does not contain an explicit indication of the method name.
	 */
	private static String getMethodName(String targetId) {
		String methodName = null;
		if (Strings.isValid(targetId) && targetId.contains(METHOD_SEPARATOR)) {
			methodName = targetId.substring(targetId.indexOf(METHOD_SEPARATOR) + 1).trim();
		}
		if (!Strings.isValid(methodName)) {
			methodName = DEFAULT_METHOD_NAME;
		}
		logger.trace("method name: '{}'", methodName);
		return methodName;
	}
}
