/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 

package org.dihedron.webmvc;

import static org.dihedron.webmvc.Constants.MILLISECONDS_PER_SECOND;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dihedron.core.properties.Properties;
import org.dihedron.core.regex.Regex;
import org.dihedron.core.strings.Strings;
import org.dihedron.webmvc.exceptions.WebMVCException;
import org.dihedron.webmvc.interceptors.Interceptor;
import org.dihedron.webmvc.protocol.Conversation;
import org.dihedron.webmvc.protocol.HttpMethod;
import org.dihedron.webmvc.protocol.Scope;
import org.dihedron.webmvc.upload.FileUploadConfiguration;
import org.dihedron.webmvc.upload.UploadedFile;
import org.dihedron.webmvc.webserver.WebServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This object provides mediated access to the underlying JSR-286 features, such
 * as session, parameters, remote user information and the like. This interface
 * is a superset of all available functionalities; other classes may provide a
 * restricted view base on the current phase, to help developers discover bugs
 * at compile time instead of having to catch exceptions at run time.
 * 
 * @author Andrea Funto'
 */
public class ActionContext {
	
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ActionContext.class);

	/**
	 * The default encoding for file names in multipart/form-data requests.
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";
    
	/**
	 * The key under which conversation-scoped attributes are stored in the session.
	 */
	protected static final String CONVERSATION_SCOPED_ATTRIBUTES_KEY = "org.dihedron.webmvc.conversation_scoped_attributes";

	/**
	 * The key under which sticky-scoped attributes are stored in the application area.
	 */
	protected static final String STICKY_SCOPED_ATTRIBUTES_KEY = "org.dihedron.webmvc.sticky_scoped_attributes";
	
	/**
	 * The key under which interceptor data is stored in the session.
	 */
	protected static final String INTERCEPTOR_DATA_KEY = "org.dihedron.webmvc.interceptor_data";

	/**
	 * The per-thread instance.
	 */
	private static ThreadLocal<ActionContext> context = new ThreadLocal<ActionContext>() {
		@Override
		protected ActionContext initialValue() {
			logger.trace("creating action context instance for thread {}", Thread.currentThread().getId());
			return new ActionContext();
		}
	};

	/**
	 * A reference to the filter configuration, for access to filter-specific
	 * information.
	 */
	private FilterConfig filter;

	/**
	 * The servlet request object.
	 */
	private HttpServletRequest request;

	/**
	 * The servlet response object.
	 */
	private HttpServletResponse response;

	/**
	 * The actions' configuration; this map is read only and it's loaded at
	 * startup if the URL of a properties file is provided in the web.xml, among
	 * the WebMVC controller's initialisation parameters.
	 */
	private Properties configuration;

	/**
	 * A reference to the {@code WebServer} plug-in; this is useful if the
	 * actions need need to exploit web server specific APIs.
	 */
	private WebServer server = null;
	
	/**
	 * The encoding of uploaded file names.
	 */
	private String encoding = DEFAULT_ENCODING;
	
	/**
	 * A map containing names and information about all the form fields in a 
	 * multipart/form-data request; if the form is not multipart, the form values 
	 * can be retrieved directly from the request in the ordinary way (see 
	 * {@link HttpServletRequest#getParameter(String)} for details); if the 
	 * request is multipart, then the standard says that parameters will all be 
	 * mixed up in the request, and the context will extract them and place them 
	 * in this map. Thus, when this map is null, the request is not multipart 
	 * and values will be picked from the request, when not null, all parameters
	 * (be they form fields or uploaded files) will be available inside this map.
	 */
	private Map<String, FileItem> parts = null;

	/**
	 * Retrieves the per-thread instance.
	 * 
	 * @return the per-thread instance.
	 */
	private static ActionContext getContext() {
		return context.get();
	}

	/**
	 * Binds the thread-local object to the current invocation, by setting
	 * references to the various objects (web server plugin, request and
	 * response objects etc.) that will be made available to the business method
	 * through this context.
	 * 
	 * @param request
	 *   the servlet request object.
	 * @param response
	 *   the servlet response object.
	 * @param configuration
	 *   the configuration object, holding properties that may have been loaded at
	 *   startup if the proper initialisation parameter is specified in the
	 *   web.xml.
	 * @param server
	 *   a reference to the web server specific plugin.
	 * @throws WebMVCException 
	 */
	static void bindContext(FilterConfig filter, HttpServletRequest request, HttpServletResponse response, Properties configuration, WebServer server, FileUploadConfiguration uploadInfo) throws WebMVCException {
		logger.trace("initialising the action context for thread {}", Thread.currentThread().getId());
		getContext().filter = filter;
		getContext().request = request;
		getContext().response = response;
		getContext().configuration = configuration;
		getContext().server = server;
		
		// this is where we try to retrieve all files (if there are any that were 
		// uploaded) and store them as temporary files on disk; these objects will
		// be accessible as ordinary values under the "FORM" scope through a 
		// custom "filename-to-file" map, which will be clened up when the context
		// is unbound
		String encoding = request.getCharacterEncoding();
        getContext().encoding = Strings.isValid(encoding)? encoding : DEFAULT_ENCODING;
        logger.trace("request encoding is: '{}'", getContext().encoding);

        try {
        
	        // check that we have a file upload request
	        if(ServletFileUpload.isMultipartContent(request)) {  
        	
	        	getContext().parts = new HashMap<String, FileItem>();
	        	
	        	logger.trace("handling multipart/form-data request");
	        	
		        // create a factory for disk-based file items
		        DiskFileItemFactory factory = new DiskFileItemFactory();
		        
		        // register a tracker to perform automatic file cleanup
//		        FileCleaningTracker tracker = FileCleanerCleanup.getFileCleaningTracker(getContext().filter.getServletContext());
//		        factory.setFileCleaningTracker(tracker);
		
		        // configure the repository (to ensure a secure temporary location 
		        // is used and the size of the )
		        factory.setRepository(uploadInfo.getRepository());
		        factory.setSizeThreshold(uploadInfo.getInMemorySizeThreshold());
		        
		        // create a new file upload handler
		        ServletFileUpload upload = new ServletFileUpload(factory);
		        upload.setSizeMax(uploadInfo.getMaxUploadableTotalSize());
		        upload.setFileSizeMax(uploadInfo.getMaxUploadableFileSize());
		
		        // parse the request & process the uploaded items
		        List<FileItem> items = upload.parseRequest(request);
		        logger.trace("{} items in the multipart/form-data request", items.size());
		        for(FileItem item : items) {
		        	logger.trace("storing field '{}' (type: '{}') into parts map", item.getFieldName(), item.isFormField() ? "field" : "file"); 
		        	getContext().parts.put(item.getFieldName(), item);
		        }		        
	        } else {
	        	logger.trace("handling plain form request");
	        }
    	} catch(FileUploadException e) {
    		logger.warn("error handling uploaded file", e);
    		throw new WebMVCException("Error handling uploaded file", e);
    	}        
	}

	/**
	 * Cleans up the internal status of the {@code ActionContext} in order to
	 * avoid memory leaks due to persisting objects stored in the per-thread
	 * local storage; afterwards it removes the thread local entry altogether,
	 * so the application server does not complain about left-over data in TLS
	 * when re-deploying the application (see Tomcat memory leak detection
	 * feature).
	 */
	static void unbindContext() {
		logger.trace("removing action context for thread {}", Thread.currentThread().getId());
		getContext().filter = null;
		getContext().request = null;
		getContext().response = null;
		getContext().configuration = null;
		getContext().server = null;
		// remove all files if this is a multipart/form-data request, because
		// the file tracker does not seem to work as expected
		if(isMultiPartRequest()) {
			for(Entry<String, FileItem> entry : getContext().parts.entrySet()) {
				if(!entry.getValue().isFormField() && !entry.getValue().isInMemory()) {
					File file = ((DiskFileItem)entry.getValue()).getStoreLocation();
					try {
						logger.trace("removing uploaded file '{}' from disk...", file.getAbsolutePath());
						Files.delete(file.toPath());
						logger.trace("... file deleted");
					} catch(IOException e) {
						logger.trace("... error deleting file", e);
					}
				}
			}
		}		
		getContext().parts = null;
		
//		if(getContext().parts != null) {
//			for(Entry<String, FileItem> entry : getContext().parts.entrySet()) {
//				FileItem part = entry.getValue();
//				if(!part.isFormField() && !part.isInMemory()) {
//					logger.trace("releasing on-disk uploaded file '{}'", part.getFieldName());
//					
//				}
//			}
//		}
		context.remove();
	}
	
	/**
	 * Returns a reference to the servlet context.
	 * 
	 * @return
	 *   a reference to the servlet context.
	 */
	public static ServletContext getServletContext() {
		// TODO: need to check on this: getContext().request.getSession().getServletContext();
		return getContext().filter.getServletContext();
	}

	/**
	 * Returns a reference to the current application-server-specific plug-in,
	 * if available. If no plug-in was loaded, returns null.
	 * 
	 * @return 
	 *   a reference to the current application-server-specific plug-in.
	 */
	public static WebServer getApplicationServer() {
		return getContext().server;
	}

	/**
	 * Returns the name of the current WebMVC controller instance.
	 * 
	 * @return 
	 *   the current portlet's name.
	 */
	public static String getFilterName() {
		return getContext().filter.getFilterName();
	}

	/**
	 * Returns the value of the current WebMVC controller filter's
	 * initialisation parameter.
	 * 
	 * @param name
	 *   the name of the parameter.
	 * @return 
	 *   the value of the initialisation parameter for the current WebMVC
	 *   controller instance.
	 */
	public static String getFilterInitialisationParameter(String name) {
		return getContext().filter.getInitParameter(name);
	}

	/**
	 * Returns a string representing the authentication type.
	 * 
	 * @return 
	 *   a string representing the authentication type.
	 */
	public static String getAuthType() {
		return getContext().request.getAuthType();
	}

	/**
	 * Checks whether the client request came through a secured connection.
	 * 
	 * @return 
	 *   whether the client request came through a secured connection.
	 */
	public static boolean isSecure() {
		return getContext().request.isSecure();
	}

	/**
	 * Returns the name of the remote authenticated user.
	 * 
	 * @return 
	 *   the name of the remote authenticated user.
	 */
	public static String getRemoteUser() {
		return getContext().request.getRemoteUser();
	}

	/**
	 * Returns the user principal associated with the request.
	 * 
	 * @return 
	 *   the user principal.
	 */
	public static Principal getUserPrincipal() {
		return getContext().request.getUserPrincipal();
	}

	/**
	 * Checks whether the user has the given role.
	 * 
	 * @param role
	 *   the name of the role
	 * @return 
	 *   whether the user has the given role.
	 */
	public static boolean isUserInRole(String role) {
		return getContext().request.isUserInRole(role);
	}

	/**
	 * Returns the locale associated with the user's request.
	 * 
	 * @return 
	 *   the request locale.
	 */
	public static Locale getLocale() {
		return getContext().request.getLocale();
	}

	/**
	 * Returns an Enumeration of Locale objects indicating, in decreasing order
	 * starting with the preferred locale in which the portal will accept
	 * content for this request. The Locales may be based on the Accept-Language
	 * header of the client.
	 * 
	 * @return 
	 *   an Enumeration of Locales, in decreasing order, in which the
	 *   portal will accept content for this request
	 */
	public static Enumeration<Locale> getLocales() {
		return (Enumeration<Locale>) getContext().request.getLocales();
	}

	/**
	 * Returns the name of the current web server.
	 * 
	 * @return 
	 *   the name of the current web server.
	 */
	public static String getServerName() {
		return getContext().request.getServerName();
	}

	/**
	 * Returns the port of the current web server.
	 * 
	 * @return 
	 *   the port of the current web server.
	 */
	public static int getServerPort() {
		return getContext().request.getServerPort();
	}

	// /**
	// * Returns the current portlet mode.
	// *
	// * @return
	// * the current portlet mode.
	// */
	// public static PortletMode getPortletMode() {
	// return
	// PortletMode.fromString(getContext().request.getPortletMode().toString());
	// }
	//
	// /**
	// * Sets the current portlet mode; it is preferable not to use this method
	// * directly and let the framework set the portlet mode instead, by
	// * specifying it in the action's results settings.
	// *
	// * @param mode
	// * the new portlet mode.
	// * @throws PortletModeException
	// * if the new portlet mode is not supported by the current portal server
	// * runtime environment.
	// * @throws InvalidPhaseException
	// * if the operation is attempted while in the render phase.
	// */
	// @Deprecated
	// public static void setPortletMode(PortletMode mode) throws
	// PortletModeException, InvalidPhaseException {
	// if(isActionPhase() || isEventPhase()) {
	// if(getContext().request.isPortletModeAllowed(mode)) {
	// logger.trace("changing portlet mode to '{}'", mode);
	// ((StateAwareResponse)getContext().response).setPortletMode(mode);
	// } else {
	// logger.warn("unsupported portlet mode '{}'", mode);
	// }
	// } else {
	// logger.error("trying to change portlet mode in the render phase");
	// throw new
	// InvalidPhaseException("Portlet mode cannot be changed in the render phase.");
	// }
	// }
	//
	// /**
	// * Returns the current portlet window state.
	// *
	// * @return
	// * the current portlet window state.
	// */
	// public static WindowState getWindowState() {
	// return
	// WindowState.fromString(getContext().request.getWindowState().toString());
	// }
	//
	// /**
	// * Sets the current window state; it is preferable not to use this method
	// * directly and let the framework set the portlet window state instead, by
	// * specifying it in the action's results settings.
	// *
	// * @param state
	// * the new window state.
	// * @throws WindowStateException
	// * if the new window state is not supported by the current portal server
	// * runtime environment.
	// * @throws InvalidPhaseException
	// * if the operation is attempted in the render phase.
	// */
	// @Deprecated
	// public static void setWindowState(WindowState state) throws
	// WindowStateException, InvalidPhaseException {
	// if(isActionPhase() || isEventPhase()) {
	// if(getContext().request.isWindowStateAllowed(state)) {
	// logger.trace("changing window state to '{}'", state);
	// ((StateAwareResponse)getContext().response).setWindowState(state);
	// } else {
	// logger.warn("unsupported window state '{}'", state);
	// }
	// } else {
	// logger.error("trying to change window state in the render phase");
	// throw new
	// InvalidPhaseException("Windows state cannot be changed in the render phase.");
	// }
	// }
	//
	// /**
	// * Returns the portlet window ID. The portlet window ID is unique for this
	// * portlet window and is constant for the lifetime of the portlet window.
	// * This ID is the same that is used by the portlet container for scoping
	// * the portlet-scope session attributes.
	// *
	// * @return
	// * the portlet window ID.
	// */
	// public static String getPortletWindowId() {
	// if(getContext().request != null) {
	// return getContext().request.getWindowID();
	// }
	// return null;
	// }
	//
	/**
	 * Returns the session ID indicated in the client request. This session ID
	 * may not be a valid one, it may be an old one that has expired or has been
	 * invalidated. If the client request did not specify a session ID, this
	 * method returns null.
	 * 
	 * @return 
	 *   a String specifying the session ID, or null if the request did
	 *   not specify a session ID.
	 * @see isRequestedSessionIdValid()
	 */
	public static String getRequestedSessionId() {
		return getContext().request.getRequestedSessionId();
	}

	/**
	 * Checks whether the requested session ID is still valid.
	 * 
	 * @return 
	 *   true if this request has an id for a valid session in the current
	 *   session context; false otherwise.
	 */
	public static boolean isRequestedSessionIdValid() {
		return getContext().request.isRequestedSessionIdValid();
	}

	/**
	 * Returns whether the session id was presented by the client as a cookie.
	 * 
	 * @return 
	 *   whether the session id was presented by the client as a cookie.
	 */
	public static boolean isRequestedSessionIdFromCookie() {
		return getContext().request.isRequestedSessionIdFromCookie();
	}

	/**
	 * Returns whether the session id was presented by the client as a parameter
	 * in the request URL.
	 * 
	 * @return 
	 *   whether the session id was presented by the client as a parameter
	 *   in the request URL.
	 */
	public static boolean isRequestedSessionIdFromURL() {
		return getContext().request.isRequestedSessionIdFromURL();
	}

	/**
	 * Returns whether the session is still valid.
	 * 
	 * @return 
	 *   whether the session is still valid.
	 */
	public static boolean isSessionValid() {
		HttpSession session = getContext().request.getSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (elapsed < session.getMaxInactiveInterval() * MILLISECONDS_PER_SECOND);
	}

	/**
	 * Returns the number of seconds left before the session gets invalidated by
	 * the container.
	 * 
	 * @return 
	 *   the number of seconds left before the session gets invalidated by the 
	 *   container.
	 */
	public static long getSecondsToSessionInvalid() {
		HttpSession session = getContext().request.getSession();
		long elapsed = System.currentTimeMillis() - session.getLastAccessedTime();
		return (long) ((elapsed - session.getMaxInactiveInterval() * MILLISECONDS_PER_SECOND) / MILLISECONDS_PER_SECOND);
	}

	/**
	 * Returns the number of seconds since the last access to the session
	 * object.
	 * 
	 * @return 
	 *   the number of seconds since the last access to the session object.
	 */
	public static long getTimeOfLastAccessToSession() {
		return getContext().request.getSession().getLastAccessedTime();
	}

	/**
	 * Returns the maximum amount of inactivity seconds before the session is
	 * considered stale.
	 * 
	 * @return 
	 *   the maximum number of seconds before the session is considered stale.
	 */
	public static int getMaxInactiveSessionInterval() {
		return getContext().request.getSession().getMaxInactiveInterval();
	}

	/**
	 * Sets the session timeout duration in seconds.
	 * 
	 * @param time
	 *   the session timeout duration, in seconds.
	 */
	public static void setMaxInactiveSessionInterval(int time) {
		getContext().request.getSession().setMaxInactiveInterval(time);
	}
	
	/**
	 * Returns the part of this request's URL from the protocol name 
	 * up to the query string in the first line of the HTTP request.
	 * 
	 * @return
	 *   the request URI.
	 */
	public static String getRequestURI() {
		return getContext().request.getRequestURI();
	}
	
	/**
	 * Reconstructs the URL the client used to make the request.
	 * 
	 * @return
	 *   the URL the client used to make the request.
	 */
	public static StringBuffer getRequestURL() {
		return getContext().request.getRequestURL();
	}
	
	/**
	 *  Returns any extra path information associated with the URL the 
	 *  client sent when it made this request.
	 * 
	 * @return
	 *   any extra path information associated with the URL the
	 *   client sent when it made this request.
	 */
	public static String getPathInfo() {
		return getContext().request.getPathInfo();
	}
	
	/**
	 *  Returns any extra path information after the servlet name but 
	 *  before the query string, and translates it to a real path.
	 * 
	 * @return
	 *   any extra path information after the servlet name but 
	 *   before the query string, and translates it to a real path.
	 */
	public static String getPathTranslated() {
		return getContext().request.getPathTranslated();
	}
		
	/**
	 * Returns an array containing all of the Cookie properties. This method
	 * returns null if no cookies exist.
	 * 
	 * @return 
	 *   the array of cookie properties, or null if no cookies exist.
	 */
	public static Cookie[] getCookies() {
		return getContext().request.getCookies();
	}

	/**
	 * Adds a cookie to the client.
	 * 
	 * @param cookie
	 *   the cookie to be added to the client.
	 */
	public static void setCookie(Cookie cookie) {
		getContext().response.addCookie(cookie);
	}

	/**
	 * Returns the header with the given name.
	 * 
	 * @param name
	 *   the name of the header.
	 * @return 
	 *   the value of the header.
	 */
	public static Object getHeader(String name) {
		return getContext().request.getHeader(name);
	}

	// TODO: add other header-related methods...

	/**
	 * Encodes the given URL; ths URL is not prefixed with the current context
	 * path, and is therefore considered as absolute. An example of such URLs is
	 * <code>/MyApplication/myServlet</code>.
	 * 
	 * @param url
	 *   the absolute URL to be encoded.
	 * @return 
	 *   the URL, in encoded form.
	 */
	public static String encodeAbsoluteURL(String url) {
		String encoded = getContext().response.encodeURL(url);
		logger.trace("url '{}' encoded as '{}'", url, encoded);
		return encoded;
	}

	/**
	 * Encodes the given URL; the URL is prefixed with the current context path,
	 * and is therefore considered as relative to it. An example of such URLs is
	 * <code>/css/myStyleSheet.css</code>.
	 * 
	 * @param url
	 *   the relative URL to be encoded.
	 * @return 
	 *   the URL, in encoded form.
	 */
	public static String encodeRelativeURL(String url) {
		String unencoded = getContext().request.getContextPath() + url;
		String encoded = getContext().response.encodeURL(unencoded);
		logger.trace("url '{}' encoded as '{}'", unencoded, encoded);
		return encoded;
	}

	/**
	 * Redirects to a different URL, with no referrer URL unless it is specified
	 * in the URL itself.
	 * 
	 * @param url
	 *   the URL to redirect the browser to (via a 302 HTTP status response).
	 * @throws IOException
	 *   if the redirect operation fails.
	 */
	public static void sendRedirect(String url) throws IOException {
		getContext().response.sendRedirect(url);
	}

	/**
	 * Sends an error code to the client.
	 * 
	 * @param error
	 *   the error code (e.g. "401 Unauthorized").
	 * @throws IOException
	 *   if the redirect operation fails.
	 */
	public static void sendError(int error) throws IOException {
		getContext().response.sendError(error);
	}

	// /**
	// * Returns the resource bundle associated with the underlying portlet, for
	// * the given locale.
	// *
	// * @param locale
	// * the selected locale.
	// * @return
	// * the portlet's configured resource bundle.
	// */
	// public static ResourceBundle getResouceBundle(Locale locale) {
	// return getContext().filter.getResourceBundle(locale);
	// }
	
	/**
	 * Returns an enumeration value representing the current HTTP method.
	 * 
	 * @return
	 *   an enumeration value representing the current HTTP method.
	 */
	public static HttpMethod getHttpMethod() {
		return HttpMethod.fromString(getContext().request.getMethod());
	}
	
	/**
	 * Returns whether the request is a multipart/form-data request.
	 * 
	 * @return
	 *   whether the request is a multipart/form-data request.
	 */
	public static boolean isMultiPartRequest() {
		return getContext().parts != null;
	}

	/**
	 * Checks if the give scope contains a non-null value under the given name.
	 * 
	 * @param key
	 *   the name of the value.
	 * @param scope
	 *   the scope in which it should be located.
	 * @return 
	 *   whether the given scope contains the value.
	 * @throws WebMVCException
	 */
	public static boolean hasValue(String key, Scope scope) throws WebMVCException {
		boolean result = false;
		if (!Strings.isValid(key)) {
			logger.error("value name must be valid");
			throw new WebMVCException("Value name must be valid.");
		}
		switch (scope) {
		case FORM:
			if(isMultiPartRequest()) {
				result = getContext().parts.containsKey(key);
			} else {
				result = getContext().request.getParameterValues(key) != null;
			}
			break;
		case REQUEST:
			result = getContext().request.getAttribute(key) != null;
			break;
		case CONVERSATION:
			String conversationId = Conversation.getConversationId(key);
			String valueId = Conversation.getValueId(key);
			if(Strings.areValid(conversationId, valueId)) {
				logger.trace("checking existence of value '{}' in conversation '{}'", valueId, conversationId);
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> conversations = (Map<String, Map<String, Object>>) getValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, Scope.SESSION);
				if (conversations != null && conversations.get(conversationId) != null) {
					result = conversations.get(conversationId).containsKey(valueId);
				}
			}
			break;			
		case SESSION:
			result = getContext().request.getSession().getAttribute(key) != null;
			break;
		case STICKY:
			String user = getRemoteUser();
			if(Strings.isValid(user)) {
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> sticky = (Map<String, Map<String, Object>>) getValue(STICKY_SCOPED_ATTRIBUTES_KEY, Scope.APPLICATION);
				if (sticky != null && sticky.get(user) != null) {
					result = sticky.get(user).containsKey(key);
				}
			}
			break;
		case APPLICATION:
			result = getContext().filter.getServletContext().getAttribute(key) != null;
			break;
		case CONFIGURATION:
			if (getContext().configuration != null) {
				result = getContext().configuration.get(key) != null;
			}
			break;
		case SYSTEM:
			result = Strings.isValid(System.getProperty(key));
			break;
		case ENVIRONMENT:
			result = Strings.isValid(System.getenv(key));
			break;

		}
		logger.debug("scope '{}' {} value '{}'", scope.name(), key, result ? "contains" : "doesn't contain");
		return result;
	}

	/**
	 * Returns the value associated with the given name if present in the given
	 * scope; no difference is made between parameters and attributes, in order
	 * to provide a consistent high-level view of parameter and attribute
	 * passing.
	 * 
	 * @param key
	 *   the name of the value.
	 * @param scope
	 *   the scope in which the value should be looked up.
	 * @return 
	 *   the requested parameter or attribute value, or null if not found.
	 */
	public static Object getValue(String key, Scope scope) throws WebMVCException {
		if (!Strings.isValid(key)) {
			logger.error("value name must be valid");
			throw new WebMVCException("Value name must be valid.");
		}
		Object value = null;
		switch (scope) {
		case FORM:
			if(isMultiPartRequest()) {
				FileItem item = getContext().parts.get(key);
				if(item != null) {
					if(item.isFormField()) {
						value = item.getString();
					} else {
						value = new UploadedFile(item); 
					}
				}
			} else {
				value = getContext().request.getParameterValues(key);
			}
			break;
		case REQUEST:
			value = getContext().request.getAttribute(key);
			break;
		case CONVERSATION:
			String conversationId = Conversation.getConversationId(key);
			String valueId = Conversation.getValueId(key);
			if(Strings.areValid(conversationId, valueId)) {
//				logger.trace("retrieving value '{}' in conversation '{}'", valueId, conversationId);
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> conversations = (Map<String, Map<String, Object>>) getValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, Scope.SESSION);
				if (conversations != null && conversations.get(conversationId) != null) {
					value = conversations.get(conversationId).get(valueId);
				}
			}
			break;						
		case SESSION:
			value = getContext().request.getSession().getAttribute(key);
			break;
		case STICKY:
			String user = getRemoteUser();
			if(Strings.isValid(user)) {
				@SuppressWarnings("unchecked")
				Map<String, Map<String, Object>> sticky = (Map<String, Map<String, Object>>) getValue(STICKY_SCOPED_ATTRIBUTES_KEY, Scope.APPLICATION);
				if (sticky != null && sticky.get(user) != null) {
					value = sticky.get(user).get(key);
				}
			}
			break;
		case APPLICATION:
			value = getContext().filter.getServletContext().getAttribute(key);
			break;
		case CONFIGURATION:
			if (getContext().configuration != null) {
				value = getContext().configuration.get(key);
			}
			break;
		case SYSTEM:
			value = System.getProperty(key);
			break;
		case ENVIRONMENT:
			value = System.getenv(key);
			break;
		}
//		logger.trace("value '{}' in scope '{}' has value '{}' (class {})", key, scope.name(), value, value != null ? value.getClass().getSimpleName() : "n.a.");
		return value;
	}

	/**
	 * Returns a copy of the map of values at the given scope.
	 * 
	 * @param scope
	 *   the scope whose values are to be returned.
	 * @return 
	 *   a copy of the map of attributes/parameters at the requested scope.
	 * @throws WebMVCException
	 */
	public static Map<String, Object> getValues(Scope scope) throws WebMVCException {
		return getValues(scope, null);
	}

	/**
	 * Returns a copy of the map of values at the given scope, possibly applying
	 * a filter to value names according to the provided pattern.
	 * 
	 * @param scope
	 *   the scope whose values are to be returned.
	 * @param pattern
	 *   an optional regular expression to return only matching values.
	 * @return 
	 *   a copy of the map of attributes/parameters at the requested scope.
	 * @throws WebMVCException
	 */
	public static Map<String, Object> getValues(Scope scope, Regex pattern) throws WebMVCException {
		Set<String> names = getValueNames(scope);
		Map<String, Object> map = new HashMap<>();
		for (String name : names) {
			map.put(name, getValue(name, scope));
		}
		return map;
	}

	/**
	 * Sets the value associated with the given name into the given scope;
	 * despite making no difference between parameters and attributes from a
	 * theoretical standpoint, this method will actually perform a check to see
	 * if an attempt is being made to store a value in a read-only context, so
	 * make sure you do not try to set a value in FORM or CONFIGURATION scopes
	 * as this would result in an error at runtime.
	 * 
	 * @param key
	 *   the name of the value.
	 * @param value
	 *   the value to be stored.
	 * @param scope
	 *   the scope into which the value should be stored.
	 */
	@SuppressWarnings("unchecked")
	public static void setValue(String key, Object value, Scope scope) throws WebMVCException {
		if (!Strings.isValid(key)) {
			logger.error("value name must be valid");
			throw new WebMVCException("Value name must be valid.");
		}
		if (scope.isReadOnly()) {
			logger.error("trying to store value in read-only scope '{}'", scope.name());
			throw new WebMVCException("Trying to store value in read-only scope '" + scope.name() + "'.");
		}

		Map<String, Object> map = null;
		switch (scope) {
		case REQUEST:
			getContext().request.setAttribute(key, value);
			break;
		case CONVERSATION:
			String conversationId = Conversation.getConversationId(key);
			String valueId = Conversation.getValueId(key);
			if(Strings.areValid(conversationId, valueId)) {
				logger.trace("setting value '{}' in conversation '{}'", valueId, conversationId);
				synchronized(ActionContext.class) {
					Map<String, Map<String, Object>> conversations = (Map<String, Map<String, Object>>) getValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, Scope.SESSION);
					if(conversations == null)  {
						conversations = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
						setValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, conversations, Scope.SESSION);
					}
					map = conversations.get(conversationId); 
					if(map == null) {
						map = new HashMap<>();
						conversations.put(conversationId, map);
					}
					map.put(valueId, value);
				}
			}
			break;
		case SESSION:
			getContext().request.getSession().setAttribute(key, value);
			break;
		case STICKY:
			String user = getRemoteUser();
			if(Strings.isValid(user)) {
				user = user.trim();
				synchronized(ActionContext.class) {
					Map<String, Map<String, Object>> sticky = (Map<String, Map<String, Object>>) getValue(STICKY_SCOPED_ATTRIBUTES_KEY, Scope.APPLICATION);
					if(sticky == null)  {
						sticky = Collections.synchronizedMap(new HashMap<String, Map<String, Object>>());
						setValue(STICKY_SCOPED_ATTRIBUTES_KEY, sticky, Scope.APPLICATION);
					}
					map = sticky.get(user); 
					if(map == null) {
						map = new HashMap<>();
						sticky.put(user, map);
					}
					map.put(key, value);
				}
			}
			break;
		case APPLICATION:
			getContext().filter.getServletContext().setAttribute(key, value);
			break;
		default:
			logger.error("should never get here: is this a bug?");
		}
		logger.debug("value '{}' in scope '{}' set to value '{}' (class {})", key, scope.name(), value, value != null ? value.getClass().getSimpleName() : "n.a.");
	}

	/**
	 * Adds all the entries in the given map into the given scope, overriding
	 * any existing values with the same names.
	 * 
	 * @param values
	 *   a map of values.
	 * @param scope
	 *   the scope into which values should be added.
	 * @throws WebMVCException
	 */
	public static void setValues(Map<String, Object> values, Scope scope) throws WebMVCException {
		setValues(values, scope, true);
	}

	/**
	 * Adds all the entries in the given map into the given scope.
	 * 
	 * @param values
	 *   a map of values.
	 * @param scope
	 *   the scope into which values should be added.
	 * @param override
	 *   if {@code true} (the default), the value will always be added to scope,
	 *   thus overriding any existing value; if {@code false} the value will be
	 *   added only if not already present.
	 * @throws WebMVCException
	 */
	public static void setValues(Map<String, Object> values, Scope scope, boolean override) throws WebMVCException {
		if (values == null) {
			logger.error("input values map must not be null");
			throw new WebMVCException("Input value map must not be null.");
		}
		for (Entry<String, Object> entry : values.entrySet()) {
			if (override || !hasValue(entry.getKey(), scope)) {
				setValue(entry.getKey(), entry.getValue(), scope);
			}
		}
	}

	/**
	 * Removes the value associated with the given name from the given scope;
	 * despite making no difference between parameters and attributes from a
	 * theoretical standpoint, this method will actually perform a check to see
	 * if an attempt is being made to remove a value from a read-only context,
	 * so make sure you do not try to remove a value from FORM or CONFIGURATION
	 * scopes as this would result in an error at runtime.
	 * 
	 * @param key
	 *   the name of the value.
	 * @param scope
	 *   the scope from which the value should be removed.
	 */
	@SuppressWarnings("unchecked")
	public static void removeValue(String key, Scope scope) throws WebMVCException {

		if (!Strings.isValid(key)) {
			logger.error("value name must be valid");
			throw new WebMVCException("Value name must be valid.");
		}
		if (scope.isReadOnly()) {
			logger.error("trying to remove value from read-only scope '{}'", scope.name());
			throw new WebMVCException("Trying to remove value from read-only scope '" + scope.name() + "'.");
		}

		switch (scope) {
		case REQUEST:
			getContext().request.removeAttribute(key);
			break;
		case CONVERSATION:
			String conversationId = Conversation.getConversationId(key);
			String valueId = Conversation.getValueId(key);
			if(Strings.areValid(conversationId, valueId)) {
				logger.trace("retrieving value '{}' in conversation '{}'", valueId, conversationId);
				Map<String, Map<String, Object>> conversations = (Map<String, Map<String, Object>>) getValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, Scope.SESSION);
				if (conversations != null && conversations.get(conversationId) != null) {
					conversations.get(conversationId).remove(valueId);
				}
			}
			break;						
		case SESSION:
			getContext().request.getSession().removeAttribute(key);
			break;
		case STICKY:
			String user = getRemoteUser();
			if(Strings.isValid(user)) {
				Map<String, Map<String, Object>> sticky = (Map<String, Map<String, Object>>) getValue(STICKY_SCOPED_ATTRIBUTES_KEY, Scope.APPLICATION);
				if (sticky != null && sticky.get(user) != null) {
					sticky.get(user).remove(key);
				}
			}
			break;
		case APPLICATION:
			getContext().filter.getServletContext().removeAttribute(key);
			break;
		default:
			logger.error("should never get here: is this a bug?");
		}
		logger.debug("value '{}' removed from scope '{}'", key, scope.name());
	}

	/**
	 * Removes all values in the input set from the given scope.
	 * 
	 * @param names
	 *   a set of value names.
	 * @param scope
	 *   the scope from which the values should be removed.
	 * @throws WebMVCException
	 */
	public static void removeValues(Set<String> names, Scope scope) throws WebMVCException {
		if (names == null) {
			logger.error("set of value names must not be null");
			throw new WebMVCException("Set of value names must not be null.");
		}

		for (String name : names) {
			removeValue(name, scope);
		}
	}

	/**
	 * Removes all values whose name matches the given regular expression from
	 * the given scope.
	 * 
	 * @param pattern
	 *   a regular expression against which value names are matched.
	 * @param scope
	 *   the scope from which to remove values.
	 * @throws WebMVCException
	 */
	public static void removeValues(String pattern, Scope scope) throws WebMVCException {
		if (pattern == null) {
			logger.error("regular expression to match against value names must not be null");
			throw new WebMVCException("Regular expression to match against value names must not be null.");
		}
		Set<String> names = getValueNames(pattern,scope);
		removeValues(names, scope);
	}

	/**
	 * Removes all values from the given scope.
	 * 
	 * @param scope
	 *   the scope from which values must be cleared.
	 * @throws WebMVCException
	 */
	public static void clearValues(Scope scope) throws WebMVCException {
		switch(scope) {
		case CONVERSATION:
			removeValues(".*:.*", scope);
			break;
		default:
			removeValues(".*", scope);
			break;
		}		
	}

	/**
	 * Retrieves the names of attributes and parameters in the given scope.
	 * 
	 * @param scope
	 *   the scope whose value (attribute/parameter) names should be retrieved.
	 * @return 
	 *   the names of the attributes/parameters in the given scope.
	 * @throws WebMVCException 
	 */
	public static Set<String> getValueNames(Scope scope) throws WebMVCException {
		return getValueNames(null, scope);
	}

	/**
	 * Retrieves the names of attributes and parameters in the given scope,
	 * possibly filtering out those that do not match the given pattern (if
	 * provided).
	 * 
	 * @param pattern
	 *   an optional regular expression: only names matching it will be returned.
	 * @param scope
	 *   the scope whose value (attribute/parameter) names should be retrieved.
	 * @return 
	 *   the names of the attributes/parameters in the given scope.
	 * @throws WebMVCException 
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getValueNames(String pattern, Scope scope) throws WebMVCException {
		Set<String> names = new HashSet<>();
		Enumeration<?> enumeration = null;
		Regex regex = null;
		if(Strings.isValid(pattern)) {
			regex = new Regex(pattern);
		}		
		switch (scope) {
		case FORM:
			if(isMultiPartRequest()) {
				names.addAll(getContext().parts.keySet());
			} else {
				enumeration = getContext().request.getParameterNames();
				while (enumeration.hasMoreElements()) {
					String name = (String) enumeration.nextElement();
					if (regex == null || regex.matches(name)) {
						names.add(name);
					}
				}
			}
			break;
		case REQUEST:
			enumeration = getContext().request.getAttributeNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				if (regex == null || regex.matches(name)) {
					names.add(name);
				}
			}
			break;
		case CONVERSATION:			
			Regex conversation = Strings.isValid(Conversation.getConversationId(pattern)) ? new Regex(Conversation.getConversationId(pattern)) : new Regex(".*");  
			regex = Strings.isValid(Conversation.getValueId(pattern)) ? new Regex(Conversation.getValueId(pattern)) : new Regex(".*");
			Map<String, Map<String, Object>> conversations = (Map<String, Map<String, Object>>) getValue(CONVERSATION_SCOPED_ATTRIBUTES_KEY, Scope.SESSION);
			if (conversations != null) {
				for(String conversationId : conversations.keySet()) {
					if(conversation.matches(conversationId)) {
						for (String name : conversations.get(conversationId).keySet()) {
							if (regex.matches(name)) {
								names.add(conversationId + ":" + name);
							}
						}							
					}
				}
			}
			break;
		case SESSION:
			enumeration = getContext().request.getSession().getAttributeNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				if (regex == null || regex.matches(name)) {
					names.add(name);
				}
			}
			break;
		case STICKY:
			String user = getRemoteUser();
			if(Strings.isValid(user)) {
				Map<String, Map<String, Object>> sticky = (Map<String, Map<String, Object>>) getValue(STICKY_SCOPED_ATTRIBUTES_KEY, Scope.APPLICATION);
				if (sticky != null && sticky.get(user) != null) {
					for (String name : sticky.get(user).keySet()) {
						if (regex == null || regex.matches(name)) {
							names.add(name);
						}
					}
				}
			}
			break;
		case APPLICATION:
			enumeration = getContext().filter.getServletContext().getAttributeNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				if (regex == null || regex.matches(name)) {
					names.add(name);
				}
			}
			break;
		case CONFIGURATION:
			if (getContext().configuration != null) {
				for (String name : getContext().configuration.getKeys()) {
					if (regex == null || regex.matches(name)) {
						names.add(name);
					}
				}
			}
			break;
		case SYSTEM:
			enumeration = System.getProperties().keys();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				if (regex == null || regex.matches(name)) {
					names.add(name);
				}
			}
			break;
		case ENVIRONMENT:
			for (String name : System.getenv().keySet()) {
				if (regex == null || regex.matches(name)) {
					names.add(name);
				}
			}
			break;
		}
		return names;
	}

	/**
	 * Looks for a value in any of the provided scopes, in the given order.
	 * 
	 * @param name
	 *   the name of the parameter to look for.
	 * @param scopes
	 *   the ordered list of scopes to look into.
	 * @return 
	 *   the value, as soon as it is found; null otherwise.
	 * @throws WebMVCException
	 */
	public static Object findValue(String name, Scope... scopes) throws WebMVCException {
		Object value = null;
		for (Scope scope : scopes) {
			if (hasValue(name, scope)) {
				value = getValue(name, scope);
				break;
			}
		}
		return value;
	}

	
	/**
	 * Looks up any value whose name matches the given regular expression in the
	 * given set of scopes.
	 * 
	 * @param pattern
	 *   a pattern to match against value names, as a string.
	 * @param scopes
	 *   a set of scopes to look into.
	 * @return
	 *   a map containing all the values whose names match the given pattern in 
	 *   the given scopes.
	 * @throws WebMVCException
	 */
	public static Map<String, Object> matchValues(String pattern, Scope... scopes) throws WebMVCException {
		if (!Strings.isValid(pattern)) {
			logger.error("regular expression to match against value names must be a valid string");
			throw new WebMVCException("Regular expression to match against value names must be a valid string.");
		}
		return matchValues(new Regex(pattern), scopes);	
	}
	
	/**
	 * Looks up any value whose name matches the given regular expression in the
	 * given set of scopes.
	 * 
	 * @param pattern
	 *   a pattern to match against value names.
	 * @param scopes
	 *   a set of scopes to look into.
	 * @return
	 *   a map containing all the values whose names match the given pattern in 
	 *   the given scopes.
	 * @throws WebMVCException
	 */
	public static Map<String, Object> matchValues(Regex pattern, Scope... scopes) throws WebMVCException {
		if (pattern == null) {
			logger.error("regular expression to match against value names must not be null");
			throw new WebMVCException("Regular expression to match against value names must not be null.");
		}
		Map<String, Object> values = new HashMap<>();

		if (scopes != null && scopes.length > 0) {
			// visit the scopes in reverse order so that first scopes have
			// higher
			// priority in retrieving values than last ones
			for (int i = scopes.length - 1; i >= 0; i--) {
				values.putAll(getValues(scopes[i], pattern));
			}
		}
		return values;
	}

	/**
	 * Sets interceptor-specific data into the action context; this information
	 * is available through different calls and can be used to keep track of
	 * system status, such as number of calls for target, or number of accesses
	 * by the same user etc. This method should only be used by interceptors,
	 * and the associated data should not be tampered with, to avoid
	 * unpredictable behaviour.
	 * 
	 * @param interceptorId
	 *   the namepaced id of the interceptor this data belongs to (see
	 *   {@link Interceptor#getId()} for details).
	 * @param data
	 *   the data object.
	 * @throws WebMVCException
	 */
	public static void setInterceptorData(String interceptorId, Object data) throws WebMVCException {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) getValue(INTERCEPTOR_DATA_KEY, Scope.SESSION);
		if (map == null) {
			map = Collections.synchronizedMap(new HashMap<String, Object>());
			setValue(INTERCEPTOR_DATA_KEY, map, Scope.SESSION);
		}
		map.put(interceptorId, data);
	}

	/**
	 * Retrieves interceptor-specific data stored by the given interceptor. This
	 * method should only be used by interceptors, and the associated data
	 * should not be tampered with, to avoid unpredictable behaviour.
	 * 
	 * @param interceptorId
	 *   the namespace id of the interceptor owning the stored data.
	 * @return 
	 *   the data, or null if none found.
	 * @throws WebMVCException
	 */
	public static Object getInterceptorData(String interceptorId) throws WebMVCException {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) getValue(INTERCEPTOR_DATA_KEY, Scope.SESSION);
		Object data = null;
		if (map != null) {
			data = map.get(interceptorId);
		}
		return data;
	}

	/**
	 * Returns the data stored by the given interceptor. This method should only
	 * be used by interceptors, and the associated data should not be tampered
	 * with, to avoid unpredictable behaviour.
	 * 
	 * @param interceptorId
	 *   the namespace id of the interceptor requesting the data.
	 * @param clazz
	 *   the type of the data to be retrieved, so it can be automatically cast.
	 * @return 
	 *   the data, already cast to the given type, or null if nothing found.
	 * @throws WebMVCException
	 */
	public static <T> T getInterceptorData(String interceptorId, Class<? extends T> clazz) throws WebMVCException {
		Object data = getInterceptorData(interceptorId);
		return data != null ? clazz.cast(data) : null;
	}
	
	/**
	 * Returns the underlying request object.
	 * 
	 * @return 
	 *   the underlying request object.
	 */
	@Deprecated
	public static HttpServletRequest getRequest() {
		return getContext().request;
	}

	/**
	 * Returns the underlying response object.
	 * 
	 * @return 
	 *   the underlying response object.
	 */
	@Deprecated
	public static HttpServletResponse getResponse() {
		return getContext().response;
	}

	/**
	 * Returns the underlying session object.
	 * 
	 * @return 
	 *   the underlying session object.
	 */
	@Deprecated
	public static HttpSession getSession() {
		return getContext().request.getSession();
	}
}
