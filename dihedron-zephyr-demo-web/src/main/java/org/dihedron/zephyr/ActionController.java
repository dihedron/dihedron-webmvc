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
package org.dihedron.zephyr;

import org.dihedron.commons.properties.Properties;
import org.dihedron.commons.properties.PropertiesException;
import org.dihedron.commons.strings.Strings;
import org.dihedron.commons.url.URLFactory;
import org.dihedron.commons.variables.EnvironmentValueProvider;
import org.dihedron.commons.variables.SystemPropertyValueProvider;
import org.dihedron.commons.variables.Variables;
import org.dihedron.zephyr.exceptions.DeploymentException;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.plugins.Plugin;
import org.dihedron.zephyr.plugins.PluginManager;
import org.dihedron.zephyr.targets.TargetId;
import org.dihedron.zephyr.targets.registry.TargetFactory;
import org.dihedron.zephyr.targets.registry.TargetRegistry;
import org.dihedron.zephyr.webserver.WebServer;
import org.dihedron.zephyr.webserver.WebServerPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * This class implements the servlet filter interface and provides all the
 * routing and processing logic of the Zephyr framework. It was necessary to
 * create a filter instead of a servlet because despite being less powerful,
 * filters are more flexible in terms of URL mapping: URL mapping only allows to
 * specify a pattern for incoming request path infos which will go to the mapped
 * servlet; if you specify "/*", all requests will go through the controller,
 * but then you'll have to provide images, CSS files, scripts, and will not be
 * able to redirect or forward since the request will come back to you again. By
 * using a filter, you can decide whether the filter is going to handle the
 * request or it can let the processing be handled by another processor, such as
 * the JSP compiler; moreover you can modify the original request so that
 * automatic re-routing happens immediately.
 * 
 * NOTE: I will have to take into account multi-steps target handling: in that
 * case the filter will not forward to JSPs until all the target chain has been
 * exhausted.
 * 
 * @author Andrea Funto'
 */
public class ActionController implements Filter {

	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionController.class);

	/**
	 * The configuration and general information about the current filter.
	 */
	private FilterConfig filter = null;

	/**
	 * The action's configuration.
	 */
	private Properties configuration = null;

	/**
	 * The web-server specific plugin.
	 */
	private WebServer server = null;

	/**
	 * The registry of invocation targets.
	 */
	private TargetRegistry registry = null;

	/**
	 * The default package for stock portal- and application-server plugins.
	 */
	public static final String DEFAULT_CONTAINERS_CLASSPATH = "org.dihedron.zephyr.webserver";

	/**
	 * Constructor.
	 */
	public ActionController() {
		logger.trace("constructed!");
	}

	public void init(FilterConfig filter) throws ServletException {

		this.filter = filter;

		try {
			logger.info("   +------------------------------------------------------------+   ");
			logger.info("   |                                                            |   ");
			logger.info(String.format("   |      %1$-48s      |   ", Strings.centre(filter.getFilterName().toUpperCase(), 48)));
			logger.info("   |                                                            |   ");
			logger.info("   |              {} |   ", Strings.padLeft("zephyr ver. " + Zephyr.getVersion(), 45));
			logger.info("   +------------------------------------------------------------+   ");

			Zephyr.getVersion();
			Enumeration<?> names = filter.getInitParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				logger.trace("parameter '{}' := '{}'", name, filter.getInitParameter(name));
			}

			initialiseConfiguration();

			initialiseRuntimeEnvironment();

			initialiseTargetsRegistry();

		} finally {

		}
	}

	@Override
	public void destroy() {
		logger.info("zephyr filter for {} is down", filter.getFilterName());
	}

	private void initialiseConfiguration() {
		String value = Parameter.ACTIONS_CONFIGURATION.getValueFor(filter);
		if (Strings.isValid(value)) {

			// bind system properties or environment variables (if any) to actual values
			logger.trace("binding variables in actions' configuration property: '{}'", value);
			value = Variables.replaceVariables(value, new SystemPropertyValueProvider(), new EnvironmentValueProvider());

			logger.debug("loading actions' configuration from '{}'", value);
			InputStream stream = null;
			try {
				URL url = URLFactory.makeURL(value);
				if (url != null) {
					stream = url.openConnection().getInputStream();
					logger.trace("opened stream to actions configuration");
					configuration = new Properties();
					configuration.load(stream);
					configuration.lock();
					logger.trace("configuration read");
				}
			} catch (MalformedURLException e) {
				logger.error("invalid URL '{}' for actions configuration: check parameter '{}' in your web.xml", value,	Parameter.ACTIONS_CONFIGURATION.getName());
			} catch (IOException e) {
				logger.error("error reading from URL '{}', actions configuration will be unavailable", value);
			} catch (PropertiesException e) {
				logger.error("is you see this error, the code has attempted to fill a locked configuration map", e);
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						logger.error("error closing input stream", e);
					}
				}
			}
		}
	}

	/**
	 * Initialises the current runtime environment, with application server
	 * specific activities and tasks.
	 */
	private void initialiseRuntimeEnvironment() {

		// dump current environment
		Map<String, String> environment = System.getenv();
		StringBuilder buffer = new StringBuilder("runtime environment:\n");
		for (String key : environment.keySet()) {
			buffer.append("\t- '").append(key).append("' = '").append(environment.get(key)).append("'\n");
		}
		logger.trace(buffer.toString());

		logger.trace("initialising runtime environment...");

		String value = Parameter.WEB_CONTAINER_PLUGIN.getValueFor(filter);
		if (Strings.isValid(value)) {
			logger.trace("trying to load web container as per user's explicit request: '{}'", value);
			Plugin plugin = PluginManager.loadPlugin(value);
			if (plugin != null) {
				logger.trace("web container '{}' loaded", value);
				this.server = (WebServer) plugin;
			}
		}
		if (this.server == null) {
			// the server plug-in has not been initialised either because there
			// was no valid value in the WEB_CONTAINER_PLUGIN parameter or the
			// class probing/loading process failed, try with class path
			value = Parameter.WEB_CONTAINER_PACKAGES.getValueFor(filter);
			if (Strings.isValid(value)) {
				logger.trace("using user-provided class paths to load web container plugin: '{}'", value);
			} else {
				value = DEFAULT_CONTAINERS_CLASSPATH;
				logger.trace("using default classpath to load web container plugin: '{}'", value);
			}
			List<Plugin> plugins = PluginManager.loadPluginsInPath(WebServerPluginFactory.class, Strings.split(value, ",", true));
			switch (plugins.size()) {
			case 0:
				logger.warn("no web server plugin found, some functionalities might not be available through ActionContext");
				break;
			case 1:
				logger.trace("exactly one web server plugin found that supports the current environment");
				this.server = (WebServer) plugins.get(0);
				break;
			default:
				logger.warn("more than a single web server plugin found: we're picking the first one, but you may want to check your plugin probes' effectiveness");
				this.server = (WebServer) plugins.get(0);
				break;
			}
		}

		logger.trace("runtime initialisation done!");
	}

	/**
	 * Initialises the action registry with information taken from the actions
	 * found under the Java packages specified by means of the
	 * {@code zephyr:actions-packages} parameter in the {@code web.xml}.
	 * 
	 * @throws ZephyrException
	 */
	private void initialiseTargetsRegistry() throws ZephyrException {
		// get the actions configuration repository
		registry = new TargetRegistry();

		// set the root directory for HTML files and JSPs, for auto-configured
		// annotated actions
		registry.setJspPathInfo(Parameter.JSP_ROOT_PATH.getValueFor(filter), Parameter.JSP_PATH_PATTERN.getValueFor(filter));

		// pre-scan existing classes and methods in the default actions package
		TargetFactory loader = new TargetFactory();

		boolean generateValidationCode = false;
		String value = Parameter.ACTIONS_ENABLE_VALIDATION.getValueFor(filter);
		if (Strings.isValid(value) && value.equalsIgnoreCase("true")) {
			logger.info("enabling JSR-349 bean validation code generation");
			generateValidationCode = true;
		} else {
			logger.info("JSR-349 bean validation code generation will be disabled");
			generateValidationCode = false;
		}

		String parameter = Parameter.ACTIONS_JAVA_PACKAGES.getValueFor(filter);
		if (Strings.isValid(parameter)) {
			logger.trace("scanning for actions in packages: '{}'", parameter);
			String[] packages = Strings.split(parameter, ",", true);
			for (String pkg : packages) {
				loader.makeFromJavaPackage(registry, pkg, generateValidationCode);
			}
		} else {
			logger.error("no Java packages specified for actions: check parameter '{}'", Parameter.ACTIONS_JAVA_PACKAGES.getName());
			throw new DeploymentException("No Java package specified for actions: check parameter '" + Parameter.ACTIONS_JAVA_PACKAGES.getName() + "'");
		}
		logger.trace("actions configuration:\n{}", registry.toString());
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String contextPath = request.getContextPath();
		// NOTE: getPathInfo() does not work in filters, where the exact servlet
		// that will end up handling the rquest is not determined; the only
		// reliable
		// way seems to be by stripping the context path from the complete
		// request URI
		String pathInfo = request.getRequestURI().substring(contextPath.length() + 1); // strip the leading '/'
		String queryString = request.getQueryString();

		String uri = request.getRequestURI();

		logger.trace("servicing request for '{}' (query string: '{}', context path: '{}', request URI: '{}')...", pathInfo, queryString, contextPath, uri);

		if (TargetId.isValidTargetId(pathInfo)) {
			PrintWriter writer = new PrintWriter(response.getWriter());
			writer.println("<html><head><title>Zephyr</title></head>");
			writer.println("<body>");
			writer.println("<h1>would be invoking " + pathInfo + "...</h1>");
			writer.println("<br>");
			
			writer.println("<h1>list of configuration properties:</h1><br><ul>");
			for (Parameter parameter : Parameter.values()) {
				writer.println("<li><b>" + parameter.getName() + "</b>:" + parameter.getValueFor(filter) + "</li>");
			}
			writer.println("</ol>");
			writer.println("</body>");
			writer.println("</html>");
		} else {
			logger.trace("letting the application handle the request: this is no action");
			chain.doFilter(req, res);
		}
	}
}
