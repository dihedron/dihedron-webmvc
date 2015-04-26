/*
 * Copyright (c) 2012-2015, Andrea Funto'. All rights reserved. See LICENSE for details.
 */ 
package org.dihedron.webmvc.classpath;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.dihedron.core.strings.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Part of this code was taken from http://stackoverflow.com/questions/520328/can-you-find-all-classes-in-a-package-using-reflection.
 * 
 * @author Andrea Funto'
 */
public class ClassPathScanner {
	/**
	 * The logger.
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClassPathScanner.class);

	/**
	 * The class loader to use to scan for classes.
	 */
	private ClassLoader classloader;
	
	/**
	 * Constructor; uses the current thread's class loader to look for classes.
	 */
	public ClassPathScanner() {
		this(Thread.currentThread().getContextClassLoader());		
	}
	
	/**
	 * Constructor.
	 * 
	 * @param classloader
	 *   uses the given class loader to look for classes.
	 */
	public ClassPathScanner(ClassLoader classloader) {
		this.classloader = classloader;
		logger.trace("classloader is of class '{}'", classloader.getClass().getName());
	}

	/**
	 * Retrieves the classes in the given JAR.
	 * 
	 * @param connection
	 *   the connection to the JAR.
	 * @param packageName
	 *   the package name to search for.
	 * @param recurse
	 *   whether sub-packages should be searched too.
	 * @return
	 *   the classes under the given package available in the given JAR.
	 * @throws ClassNotFoundException
	 *   if a file isn't loaded but still is in the JAR file.
	 * @throws IOException
	 *   if it can't correctly read from the JAR file.
	 */
	private List<Class<?>> getClassesInJar(JarURLConnection connection, String packageName, boolean recurse) throws ClassNotFoundException, IOException {
		
		List<Class<?>> classes = new ArrayList<Class<?>>();
		
		JarFile jarFile = connection.getJarFile();
		Enumeration<JarEntry> entries = jarFile.entries();		
	
		for (JarEntry jarEntry = null; entries.hasMoreElements() && ((jarEntry = entries.nextElement()) != null);) {
			String name = jarEntry.getName().replace("/", ".");
			//logger.trace("checking entry '{}' against package '{}' and suffix '.class'", name, packageName);			
			if (name.startsWith(packageName) && name.endsWith(".class") && !name.endsWith("package-info.class")) {
				name = name.substring(0, name.length() - ".class".length()).replace('/', '.');
				if(recurse || name.substring(0, name.lastIndexOf('.')).equals(packageName)) {
					classes.add(Class.forName(name));
					logger.trace("class '{}' is valid, added to list", name);
				}
			}
		}
		return classes;
	}	
	
	/**
	 * Retrieves the classes under the given directory.
	 * 
	 * @param directory
	 *   the directory to start with.
	 * @param packageName
	 *   the package name to search for; it's needed internally to create a
	 *   Class object.
	 * @param recurse
	 *   whether sub-directories should be searched.
	 * @return
	 *   the classes loaded from the given directory.
	 * @throws ClassNotFoundException
	 */
	private List<Class<?>> getClassesInDirectory(File directory, String packageName, boolean recurse) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>(); 
	    if (directory.exists() && directory.isDirectory()) {
	    	logger.trace("directory '{}' exists and is a valid directory");
	    	File tmpDirectory = null;
	        for (String filename : directory.list()) {
	            if (filename.endsWith(".class")) {
	            	if(!filename.endsWith("package-info.class")) {
	            		// skip package documentation classes
		                try {
		                    classes.add(Class.forName(packageName + '.' + filename.substring(0, filename.length() - ".class".length())));
		                } catch (NoClassDefFoundError e) {
		                    // do nothing; this class hasn't been found by the loader, and we don't care
		                }
	            	}
	            } else if (recurse && (tmpDirectory = new File(directory, filename)).isDirectory()) {
	                classes.addAll(getClassesInDirectory(tmpDirectory, packageName + "." + filename, recurse));
	            }
	        }
	    }
	    return classes;
	}	

	/**
	 * Attempts to list all the classes in the specified package as made available 
	 * through the scanner's class loader.
	 * 
	 * @param packageName
	 *   the name of the package to search for.
	 * @return 
	 *   a list of classes that exist within that package (and sub-packages).
	 * @throws ClassNotFoundException
	 *   if something went wrong.
	 */
	public List<Class<?>> getClassesForPackage(String packageName, boolean recurse) throws ClassNotFoundException {
		
		try {
			if(!Strings.isValid(packageName)) {
				logger.error("invalid package name");
				throw new ClassNotFoundException("Invalid package name.");
			}
			
			logger.info("looking for classes under package name '{}'", packageName);
			
			// get all the resources with the given package name
			Enumeration<URL> resources = classloader.getResources(packageName.replace('.', '/'));

			List<Class<?>> classes = new ArrayList<Class<?>>();
			for (URL url = null; resources.hasMoreElements() && ((url = resources.nextElement()) != null);) {
				logger.trace("cheking URL '{}' ({})", url.toExternalForm(), url.getPath());
				URLConnection connection = url.openConnection();

				if (connection instanceof JarURLConnection) {
					logger.trace("connection is of type JAR");
					classes.addAll(getClassesInJar((JarURLConnection) connection, packageName, recurse));
				} else if(
						connection.getClass().getName().equals("sun.net.www.protocol.file.FileURLConnection") ||
						connection.getClass().getName().equals("org.jboss.vfs.protocol.VirtualFileURLConnection")						
						) {
					logger.info("connection is of type Directory");

					try {
						classes.addAll(getClassesInDirectory(new File(URLDecoder.decode(url.getPath(), "UTF-8")), packageName, recurse));
                    } catch (final UnsupportedEncodingException e) {
                    	logger.error(packageName + " does not appear to be avalid package (unsupported encoding)", e);
                        throw new ClassNotFoundException(packageName + " does not appear to be a valid package (unsupported encoding)", e);
                    }					
				} else {
					logger.info("connection is of an unsupported type '{}'", connection.getClass().getName());
					throw new ClassNotFoundException(packageName + " (" + url.getPath() + ") does not appear to be a valid package");
				}
			}
			return classes;
		} catch (IOException e) {
			logger.error("IOException was thrown when trying to get all resources for " + packageName, e);
			throw new ClassNotFoundException("IOException was thrown when trying to get all resources for " + packageName, e);
		}
	}
}
