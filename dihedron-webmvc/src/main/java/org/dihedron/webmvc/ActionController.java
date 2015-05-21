/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dihedron.core.properties.Properties;
import org.dihedron.core.properties.PropertiesException;
import org.dihedron.core.strings.Strings;
import org.dihedron.core.url.URLFactory;
import org.dihedron.core.variables.EnvironmentValueProvider;
import org.dihedron.core.variables.SystemPropertyValueProvider;
import org.dihedron.core.variables.Variables;
import org.dihedron.webmvc.actions.ActionFactory;
import org.dihedron.webmvc.actions.Result;
import org.dihedron.webmvc.annotations.Action;
import org.dihedron.webmvc.exceptions.DeploymentException;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.InterceptorStack;
import org.dihedron.webmvc.interceptors.registry.InterceptorsRegistry;
import org.dihedron.webmvc.plugins.Plugin;
import org.dihedron.webmvc.plugins.PluginManager;
import org.dihedron.webmvc.renderers.Renderer;
import org.dihedron.webmvc.renderers.impl.CachingRendererRegistry;
import org.dihedron.webmvc.renderers.registry.RendererRegistry;
import org.dihedron.webmvc.renderers.registry.RendererRegistryLoader;
import org.dihedron.webmvc.targets.Target;
import org.dihedron.webmvc.targets.TargetId;
import org.dihedron.webmvc.targets.registry.TargetFactory;
import org.dihedron.webmvc.targets.registry.TargetRegistry;
import org.dihedron.webmvc.upload.FileUploadConfiguration;
import org.dihedron.webmvc.webserver.WebServer;
import org.dihedron.webmvc.webserver.WebServerPluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the servlet filter interface and provides all the
 * routing and processing logic of the WebMVC framework. It was necessary to
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
public class ActionController implements Filter, ActionControllerMBean {

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
	 * The configuration for file upload handling.
	 */
	private FileUploadConfiguration uploadInfo = null;

	/**
	 * The default package for stock portal- and application-server plugins.
	 */
	public static final String DEFAULT_CONTAINERS_CLASSPATH = "org.dihedron.webmvc.webserver";
	
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
			logger.info("   |              {} |   ", Strings.padLeft("webmvc ver. " + WebMVC.getVersion(), 45));
			logger.info("   +------------------------------------------------------------+   ");

			WebMVC.getVersion();
			Enumeration<?> names = filter.getInitParameterNames();
			while (names.hasMoreElements()) {
				String name = (String) names.nextElement();
				logger.trace("parameter '{}' := '{}'", name, filter.getInitParameter(name));
			}

			initialiseConfiguration();

			initialiseRuntimeEnvironment();

			initialiseInterceptorsRegistry();
			
			initialiseTargetsRegistry();

			initialiseRenderersRegistry();
			
			initialiseFileUploadConfiguration();
			
			initialiseJMXSupport();

		} finally {

		}
	}

	@Override
	public void destroy() {
		logger.info("webmvc filter for {} is down", filter.getFilterName());
		
		cleanupJMXSupport();
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		String contextPath = request.getContextPath();
		// NOTE: getPathInfo() does not work in filters, where the exact servlet that will 
		// end up handling the request is not determined; the only reliable way seems to be 
		// by stripping the context path from the complete request URI
		String uri = request.getRequestURI();
		String targetId = uri.substring(contextPath.length() + 1); // strip the leading '/'
		String queryString = request.getQueryString();

		logger.debug("servicing request for '{}' (query string: '{}', context path: '{}', request URI: '{}')...", targetId, queryString, contextPath, uri);

		try {
			ActionContext.bindContext(filter, request, response, configuration, server, uploadInfo);
			
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
			while(TargetId.isValidTargetId(targetId) && (result == null || result.getRendererId().equals("chain"))) {
				
				logger.trace("invoking target '{}'...", targetId);
				
				// check if there's configuration available for the given action
				Target target = registry.getTarget(targetId);
				
				logger.trace("target configuration:\n{}", target.toString());
				
				// instantiate the action
				Object action = ActionFactory.makeAction(target);
				if(action != null) {
					logger.trace("action instance '{}' ready", target.getActionClass().getSimpleName());
				} else {    			 	
					logger.error("could not create an action instance for target '{}'", target.getId());
					throw new WebMVCException("No action could be found for target '" + target.getId() + "'");
				}
				
				// get the stack for the given action
				InterceptorStack stack = interceptors.getStackOrDefault(target.getInterceptorStackId());
		    	    	
		    	// create and fire the action stack invocation				
				ActionInvocation invocation = null;
				try {
					logger.trace("invoking interceptors' stack...");
					invocation = new ActionInvocation(target, action, stack, request, response);
					invocationResult = invocation.invoke();
					if(invocationResult.equals(Action.DONE)) {
						logger.trace("action request performed view rendering too, request is complete");
						return;
					}					
					
					do {
						// look up the result amont thos already available (explicitly configured 
						// only at first, then auto-configured too as the target warms up)
						logger.trace("looking up result among target's ...");
						result = target.getResult(invocationResult);						
						if(result != null) break;

						// is not among the target's, check if there is a global result handler
						// at stack level
						logger.trace("looking up result among globals in stack '{}'...", stack.getId());
						result = stack.getGlobalResult(invocationResult);
						if(result != null) break;
										        
			            // nope, then try to auto-configure one in the target
						logger.trace("result '{}' is not present yet, auto-configuring...", invocationResult);
			            result = target.addUndeclaredResult(invocationResult);
			            if(result != null) break;
					
						// too bad, we're throwing an error because everyithing else failed
						logger.error("misconfiguration in registry: target '{}' and result '{}' have no valid processing information", target.getId(), invocationResult);
						throw new WebMVCException("No valid information found in registry for target '" + target.getId() + "', result '" + invocationResult + "', please check your actions");
						
					} while(false);
					
				} finally {
					logger.debug("... business logic invocation done!");
					invocation.cleanup();
				}
			} 
			
			if(result == null) {
				logger.trace("'{}' is no action, treating as resource...", uri);
				
				InterceptorStack stack = interceptors.getStackOrDefault("resource");
				ResourceInvocation invocation = null;
				try {
					invocation = new ResourceInvocation(uri, stack, request, response);
					invocationResult = invocation.invoke();
					result = stack.getGlobalResult(invocationResult);					
				} finally {
					logger.debug("... resource invocation done!");
					invocation.cleanup();
				}
			}
				
			if(result == null) {
				logger.trace("after interceptors application, letting the server handle the resource...");
				chain.doFilter(req, res);				
			} else {
				Renderer renderer = renderers.getRenderer(result.getRendererId());
				renderer.render(request, response, result.getData());
			}
		} finally {
			ActionContext.unbindContext();
		}
	}

	/**
	 * Reads the actions' configuration info from the URL specified in the web.xml.
	 */
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
				logger.error("if you see this error, the code has attempted to fill a locked configuration map", e);
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
	 * {@code webmvc:actions-packages} parameter in the {@code web.xml}.
	 * 
	 * @throws WebMVCException
	 */
	private void initialiseTargetsRegistry() throws WebMVCException {
		// get the actions configuration repository
		registry = new TargetRegistry();

		// set the root directory for HTML files and JSPs, for auto-configured
		// annotated actions
		registry.setJspPathInfo(Parameter.JSP_ROOT_PATH.getValueFor(filter), Parameter.JSP_PATH_PATTERN.getValueFor(filter));

		// pre-scan existing classes and methods in the default actions package
		TargetFactory loader = null;

		String value = Parameter.ACTIONS_ENABLE_VALIDATION.getValueFor(filter);
		if (Strings.isValid(value) && value.equalsIgnoreCase("true")) {
			loader = new TargetFactory(true);
		} else {
			loader = new TargetFactory(false);
		}

		String parameter = Parameter.ACTIONS_JAVA_PACKAGES.getValueFor(filter);
		if (Strings.isValid(parameter)) {
			logger.trace("scanning for actions in packages: '{}'", parameter);
			String[] packages = Strings.split(parameter, ",", true);
			for (String pkg : packages) {
				loader.makeFromJavaPackage(registry, interceptors, pkg);
			}
		} else {
			logger.error("no Java packages specified for actions: check parameter '{}'", Parameter.ACTIONS_JAVA_PACKAGES.getName());
			throw new DeploymentException("No Java package specified for actions: check parameter '" + Parameter.ACTIONS_JAVA_PACKAGES.getName() + "'");
		}
		logger.info("actions configuration:\n{}", registry.toString());
	}
	
    /**
     * Initialises the interceptors stack registry (factory) by loading the default 
     * stacks first and then any custom stacks provided in the initialisation 
     * parameters.
     * 
     * @throws WebMVCException
     */
    private void initialiseInterceptorsRegistry() throws WebMVCException {

		interceptors = new InterceptorsRegistry();
		
		// load the default interceptors stacks ("default" and others)
		logger.trace("loading default interceptors stacks: '{}'", InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		interceptors.load(InterceptorsRegistry.DEFAULT_INTERCEPTORS_CONFIG_XML);
		//logger.trace("pre-configured interceptors stacks:\n{}", interceptors.toString());
		
		// load the custom interceptors configuration
		String value = Parameter.INTERCEPTORS_DECLARATION.getValueFor(filter);
		if(Strings.isValid(value)) {			
    		logger.trace("loading interceptors' configuration from '{}'", value);
    		try {
    			interceptors.load(value);    			
    		} catch(WebMVCException e) {
    			logger.error("invalid URL '{}' for interceptors stacks: check parameter '{}' in your web.xml", value, Parameter.INTERCEPTORS_DECLARATION.getName());
    			throw e;
    		}
		} 
		
		logger.info("interceptors stacks:\n{}", interceptors.toString());
    }	

	/**
	 * Initialises the registry of view renderers.
	 * 
	 * @throws WebMVCException
	 */
	private void initialiseRenderersRegistry() throws WebMVCException {
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
	
	/**
	 * Initialises support for file uploads.
	 * 
	 * @throws WebMVCException
	 *   if it cannot create or access the uploaded files repository.
	 */
	private void initialiseFileUploadConfiguration() throws WebMVCException {
		
		this.uploadInfo = new FileUploadConfiguration();
		
		// initialise uploaded files repository		
		File repository = null;
		String value = Parameter.UPLOADED_FILES_DIRECTORY.getValueFor(filter);
		if(Strings.isValid(value)) {
			logger.info("using user-provided upload directory: '{}'", value);
			repository  = new File(value);
			if(!repository.exists()) {
				if(repository.mkdirs()) {
					logger.info("directory tree created under '{}'", repository.getAbsolutePath());
				} else {
					logger.error("cannot create directory tree for uploaded files: '{}'", repository.getAbsolutePath());
					throw new DeploymentException("Error creating file upload directory under path '" + repository.getAbsolutePath() + "'");
				}
			}
			
			if(!repository.isDirectory()) {
				logger.error("filesystem object {} is not a directory", repository.getAbsolutePath());
				throw new DeploymentException("Filesystem object at path '" + repository.getAbsolutePath() + "' is not a directory");
			}			
		} else {
			repository = (File)filter.getServletContext().getAttribute(ServletContext.TEMPDIR);
			logger.info("using application-server upload directory: '{}'", repository.getAbsolutePath());			
		}
				
		// check if directory is writable
		if(!repository.canWrite()) {
			logger.error("upload directory {} is not writable", repository.getAbsolutePath());
			throw new DeploymentException("Directory at path '" + repository.getAbsolutePath() + "' is not a writable");			
		}
		
		// remove all pre-existing files
		try {
			logger.trace("removing all existing files from directory '{}'...", repository.getAbsolutePath());
			DirectoryStream<Path> files = Files.newDirectoryStream(repository.toPath());
			for(Path file : files) {
				logger.trace("...removing '{}'", file);
				file.toFile().delete();
			}
		} catch(IOException e) {
			logger.warn("error deleting all files from upload directory", e);
		}
		
		logger.info("upload directory '{}' ready", repository.getAbsolutePath());
		
		this.uploadInfo.setRepository(repository);
		
		// initialise maximum uploadable file size per single file
		value = Parameter.UPLOADED_FILES_MAX_FILE_SIZE.getValueFor(filter);
		if(Strings.isValid(value)) {
			logger.trace("setting maximum uploadable size to {}", value);
			this.uploadInfo.setMaxUploadFileSize(Long.parseLong(value));
		} else {
			logger.trace("using default value for maximum uploadable file size: {}", FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_SINGLE);
			this.uploadInfo.setMaxUploadFileSize(FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_SINGLE);
		}
		
		// initialise maximum total uploadable file size
		value = Parameter.UPLOADED_FILES_MAX_REQUEST_SIZE.getValueFor(filter);
		if(Strings.isValid(value)) {
			logger.trace("setting maximum total uploadable size to {}", value);
			this.uploadInfo.setMaxUploadTotalSize(Long.parseLong(value));
		} else {
			logger.trace("using default value for maximum total uploadable size: {}", FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_TOTAL);
			this.uploadInfo.setMaxUploadTotalSize(FileUploadConfiguration.DEFAULT_MAX_UPLOADABLE_FILE_SIZE_TOTAL);
		}

		// initialise small file threshold
		value = Parameter.UPLOADED_SMALL_FILE_SIZE_THRESHOLD.getValueFor(filter);
		if(Strings.isValid(value)) {
			logger.trace("setting small files size threshold to {}", value);
			this.uploadInfo.setInMemorySizeThreshold(Integer.parseInt(value));
		} else {
			logger.trace("using default value for small files size threshold: {}", FileUploadConfiguration.DEFAULT_SMALL_FILE_SIZE_THRESHOLD);
			this.uploadInfo.setInMemorySizeThreshold(FileUploadConfiguration.DEFAULT_SMALL_FILE_SIZE_THRESHOLD);
		}
		logger.trace("done configuring file upload support");
	}
	
	// JMX SUPPORT
	
	private void initialiseJMXSupport() {
		ObjectName name = null;
		try {
			logger.info("registering WebMVC Controller JMX MBean...");
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			Hashtable<String, String> attributes = new Hashtable<>();
			attributes.put("type", "ActionController");
			attributes.put("instance", filter.getFilterName());
			attributes.put("version", WebMVC.getVersion());
	        name = new ObjectName(WebMVC.DIHEDRON_WEBMVC_DOMAIN, attributes);  
	        mbs.registerMBean(this, name);
	        logger.info("... WebMVC Controller JMX MBean successfully registered under name '{}'", name.getCanonicalName());
		} catch (MalformedObjectNameException e) {
			logger.error("invalid object name", e);
		} catch (InstanceAlreadyExistsException e) {
			logger.error("an instance with the given name ('" + name.getCanonicalName() + "') already esists in this JMX server", e);
		} catch (MBeanRegistrationException e) {
			logger.error("error registering '" + name.getCanonicalName() + "' MBean", e);
		} catch (NotCompliantMBeanException e) {
			logger.error("MBean '" + name.getCanonicalName() + "' is not compliant", e);
		}
	}
	
	private void cleanupJMXSupport() {
		ObjectName name = null;
		try {
			logger.info("unregistering WebMVC Controller JMX MBean...");
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			Hashtable<String, String> attributes = new Hashtable<>();
			attributes.put("type", "ActionController");
			attributes.put("instance", filter.getFilterName());
			attributes.put("version", WebMVC.getVersion());
	        name = new ObjectName(WebMVC.DIHEDRON_WEBMVC_DOMAIN, attributes);  
	        mbs.unregisterMBean(name);
	        logger.info("... WebMVC Controller JMX MBean with name '{}' successfully unregistered", name.getCanonicalName());
		} catch (MalformedObjectNameException e) {
			logger.error("invalid object name", e);
		} catch (MBeanRegistrationException e) {
			logger.error("error unregistering '" + name.getCanonicalName() + "' MBean", e);
		} catch (InstanceNotFoundException e) {
			logger.error("an instance with the given name ('" + name.getCanonicalName() + "') does not esist in this JMX server", e);
		}		
	}
	
	/**
	 * Returns the name of this instance.
	 * 
	 * @see org.dihedron.webmvc.ActionControllerMBean#getApplicationName()
	 */
	public String getApplicationName() {
		return filter != null ? filter.getFilterName() : "uninitialised";
	}

	/**
	 * Returns the name of the WebMVC framework.
	 * 
	 * @see org.dihedron.webmvc.ActionControllerMBean#getFrameworkName()
	 */
	public String getFrameworkName() {
		return WebMVC.getName();
	}
	
	/**
	 * Returns the current version of the framework.
	 * 
	 * @see org.dihedron.webmvc.ActionControllerMBean#getFrameworkVersion()
	 */
	public String getFrameworkVersion() {
		return WebMVC.getVersion();
	}

//	protected String invokeTarget(TargetId targetId, HttpServletRequest request, HttpServletRequest response) throws WebMVCException {
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
