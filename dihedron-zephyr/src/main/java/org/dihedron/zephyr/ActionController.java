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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.commons.properties.Properties;
import org.dihedron.commons.properties.PropertiesException;
import org.dihedron.commons.strings.Strings;
import org.dihedron.commons.url.URLFactory;
import org.dihedron.commons.variables.EnvironmentValueProvider;
import org.dihedron.commons.variables.SystemPropertyValueProvider;
import org.dihedron.commons.variables.Variables;
import org.dihedron.zephyr.actions.ActionFactory;
import org.dihedron.zephyr.actions.Result;
import org.dihedron.zephyr.annotations.Action;
import org.dihedron.zephyr.exceptions.DeploymentException;
import org.dihedron.zephyr.exceptions.ZephyrException;
import org.dihedron.zephyr.interceptors.InterceptorStack;
import org.dihedron.zephyr.interceptors.registry.InterceptorsRegistry;
import org.dihedron.zephyr.plugins.Plugin;
import org.dihedron.zephyr.plugins.PluginManager;
import org.dihedron.zephyr.protocol.Scope;
import org.dihedron.zephyr.renderers.Renderer;
import org.dihedron.zephyr.renderers.impl.CachingRendererRegistry;
import org.dihedron.zephyr.renderers.registry.RendererRegistry;
import org.dihedron.zephyr.renderers.registry.RendererRegistryLoader;
import org.dihedron.zephyr.targets.Target;
import org.dihedron.zephyr.targets.TargetId;
import org.dihedron.zephyr.targets.registry.TargetFactory;
import org.dihedron.zephyr.targets.registry.TargetRegistry;
import org.dihedron.zephyr.webserver.WebServer;
import org.dihedron.zephyr.webserver.WebServerPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	 * The registry of interceptors' stacks.
	 */
	private InterceptorsRegistry interceptors;
	
	/**
	 * The registry of supported renderers.
	 */
	private RendererRegistry renderers;

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

			initialiseInterceptorsRegistry();

			initialiseRenderersRegistry();

		} finally {

		}
	}

	@Override
	public void destroy() {
		logger.info("zephyr filter for {} is down", filter.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String contextPath = request.getContextPath();
		// NOTE: getPathInfo() does not work in filters, where the exact servlet that will 
		// end up handling the rquest is not determined; the only reliable way seems to be 
		// by stripping the context path from the complete request URI
		String targetId = request.getRequestURI().substring(contextPath.length() + 1); // strip the leading '/'
		String queryString = request.getQueryString();

		String uri = request.getRequestURI();


		logger.trace("servicing request for '{}' (query string: '{}', context path: '{}', request URI: '{}')...", targetId, queryString, contextPath, uri);

		try {
			ActionContext.bindContext(filter, request, response, configuration, server);
			
			// TODO: test, remove!
//			ActionContext.setValue("conversation_A:key1", "value1a", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_A:key2", "value2a", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_A:key3", "value3a", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_A:key4", "value4a", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_B:key1", "value1b", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_B:key2", "value2b", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_B:key3", "value3b", Scope.CONVERSATION);
//			ActionContext.setValue("conversation_B:key4", "value4b", Scope.CONVERSATION);
			
			// TODO: end test
			
			String invocationResult = null;
			Result result = null;
			while(TargetId.isValidTargetId(targetId) && (result == null ||/* result.getRendererId().equals("auto") ||*/ result.getRendererId().equals("chain"))) {
				
				logger.info("invoking target '{}'...", targetId);
				
				// check if there's configuration available for the given action
				Target target = registry.getTarget(targetId);
				
				logger.trace("target configuration:\n{}", target.toString());
				
				// instantiate the action
				Object action = ActionFactory.makeAction(target);
				if(action != null) {
					logger.info("action instance '{}' ready", target.getActionClass().getSimpleName());
				} else {    			 	
					logger.error("could not create an action instance for target '{}'", target.getId());
					throw new ZephyrException("No action could be found for target '" + target.getId() + "'");
				}
				
				// get the stack for the given action
				InterceptorStack stack = interceptors.getStackOrDefault(target.getInterceptorStackId());
		    	    	
		    	// create and fire the action stack invocation				
				ActionInvocation invocation = null;
				try {
					logger.info("invoking interceptors' stack...");
					invocation = new ActionInvocation(target, action, stack, request, response);
					invocationResult = invocation.invoke();
					if(invocationResult.equals(Action.DONE)) {
						logger.trace("action request performed view rendering too, request is complete");
						return;
					}					
					result = target.getResult(invocationResult);
					logger.info("... invocation done!");
				} finally {
					invocation.cleanup();
				}
				
				if(result == null) {
					logger.error("misconfiguration in registry: target '{}' and result '{}' have no valid processing information", target.getId(), result);
					throw new ZephyrException("No valid information found in registry for target '" + target.getId() + "', result '" + result + "', please check your actions");
				}
			} 
			
			if(result == null) {
				logger.trace("letting the application handle the request: this is no action");
				chain.doFilter(req, res);				
			} else {
				Renderer renderer = renderers.getRenderer(result.getRendererId());
				renderer.render(request, response, result.getData());
			}
		} finally {
			ActionContext.unbindContext();
		}
	}

	private void initialiseConfiguration() {
		String value = Parameter.ACTIONS_CONFIGURATION.getValueFor(filter);
		if (Strings.isValid(value)) {

			// bind system properties or environment variables (if any) to
			// actual values
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
				logger.error("invalid URL '{}' for actions configuration: check parameter '{}' in your web.xml", value,
						Parameter.ACTIONS_CONFIGURATION.getName());
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
			throw new DeploymentException("No Java package specified for actions: check parameter '" + Parameter.ACTIONS_JAVA_PACKAGES.getName()
					+ "'");
		}
		logger.trace("actions configuration:\n{}", registry.toString());
	}
	
    /**
     * Initialises the interceptors stack registry (factory) by loading the default 
     * stacks first and then any custom stacks provided in the initialisation 
     * parameters.
     * 
     * @throws ZephyrException
     */
    private void initialiseInterceptorsRegistry() throws ZephyrException {

		interceptors = new InterceptorsRegistry();
		
		// load the default interceptors stacks ("default" and others)
		logger.info("loading default interceptors stacks: '{}'", InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		interceptors.loadFromClassPath(InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		logger.trace("pre-configured interceptors stacks:\n{}", interceptors.toString());
		
		// load the custom interceptors configuration
		String value = Parameter.INTERCEPTORS_DECLARATION.getValueFor(filter);
		if(Strings.isValid(value)) {			
    		logger.debug("loading interceptors' configuration from '{}'", value);
    		InputStream stream = null;
    		try {
	    		URL url = URLFactory.makeURL(value);
	    		if(url != null) {
	    			stream = url.openConnection().getInputStream();	    			
	    			interceptors.loadFromStream(stream);
	    			logger.trace("interceptors stacks:\n{}", interceptors.toString());
	    		}
    		} catch(MalformedURLException e) {
    			logger.error("invalid URL '{}' for interceptors stacks: check parameter '{}' in your web.xml", value, Parameter.INTERCEPTORS_DECLARATION.getName());
    		} catch (IOException e) {
    			logger.error("error reading from URL '{}', interceptors stacks will be unavailable", value);
			} finally  {
				if(stream != null) {
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
	 * Initialises the registry of view renderers.
	 * 
	 * @throws ZephyrException
	 */
	private void initialiseRenderersRegistry() throws ZephyrException {
		RendererRegistryLoader loader = new RendererRegistryLoader();
		renderers = new CachingRendererRegistry();
		// renderers = new RenewingRendererRegistry();
		loader.loadFromJavaPackage(renderers, RendererRegistry.DEFAULT_RENDERER_PACKAGE);

		String parameter = Parameter.RENDERERS_JAVA_PACKAGES.getValueFor(filter);
		if (Strings.isValid(parameter)) {
			logger.trace("scanning for renderers in packages: '{}'", parameter);
			String[] packages = Strings.split(parameter, ",", true);
			for (String pkg : packages) {
				loader.loadFromJavaPackage(renderers, pkg);
			}
		}
		logger.trace("renderers configuration:\n{}", renderers.toString());
	}

//	protected String invokeTarget(TargetId targetId, HttpServletRequest request, HttpServletRequest response) throws ZephyrException {
//
//		logger.info("invoking target '{}'", targetId);
//
//		// check if there's configuration available for the given action
//		Target target = registry.getTarget(targetId);
//
//		logger.trace("target configuration:\n{}", target.toString());
//
////		// instantiate the action
////		Object action = ActionFactory.makeAction(target);
////		if (action != null) {
////			logger.info("action instance '{}' ready", target.getActionClass().getSimpleName());
////		} else {
////			logger.error("could not create an action instance for target '{}'", targetId);
////			throw new StrutletsException("No action could be found for target '" + targetId + "'");
////		}
//
////		// get the stack for the given action
////		InterceptorStack stack = interceptors.getStackOrDefault(target.getInterceptorStackId());
////
////		// create and fire the action stack invocation
////		ActionInvocation invocation = null;
////		try {
////			invocation = new ActionInvocation(action, target, stack, request, response);
////			return invocation.invoke();
////		} finally {
////			invocation.cleanup();
////		}
//	}

}
