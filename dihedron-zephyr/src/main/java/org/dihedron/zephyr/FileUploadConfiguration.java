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

import java.io.File;

/**
 * A class containing all the information needed to configure the file upload
 * handling.
 * 
 * @author Andrea Funto'
 */
public class FileUploadConfiguration {

	/**
	 * The directory into which uploaded files exceeding the {@link inMemoryThreshold}. 
	 * will be stored.
	 */
	private File repository;
	
	/**
	 * The maximum size for uploaded files.
	 */
	private long maxUploadableSize;
	
	/**
	 * The size above discriminating files that will be held in memory from those
	 * that are written out to disk.
	 */
	private long inMemoryThresholdSize;

	/**
	 * Returns the File object representing the temporary uploaded files repository.
	 *
	 * @return 
	 *   the File object representing the temporary uploaded files repository.
	 */
	public File getRepository() {
		return repository;
	}

	/**
	 * Sets the File object representing the temporary uploaded files repository
	 *
	 * @param repository 
	 *   the new value for the File object representing the temporary uploaded 
	 *   files repository.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public FileUploadConfiguration setRepository(File repository) {
		this.repository = repository;
		return this;
	}

	/**
	 * Returns the value of the maximum uploadable file size.
	 *
	 * @return 
	 *   the value of the maximum uploadable file size.
	 */
	public long getMaxUploadableSize() {
		return maxUploadableSize;
	}

	/**
	 * Sets the value of the maximum uploadable file size.
	 *
	 * @param maxUploadSize 
	 *   the new value for the maximum uploadable file size.
	 * @return
	 *   the object itself, for method chaining.
	 */
	public FileUploadConfiguration setMaxUploadSize(long maxUploadSize) {
		this.maxUploadableSize = maxUploadSize;
		return this;
	}

	/**
	 * Returns the value of the maximum file size that will be kept in memory;
	 * file larger than this size will be written out to disk.
	 *
	 * @return 
	 *   the maximum size of files that will be kept in memory.
	 */
	public long getInMemoryThresholdSize() {
		return inMemoryThresholdSize;
	}

	/**
	 * Sets the value of the maximum size for files that will be kept in memory;
	 * files larger than this size will be written out to disk as temporary files
	 * inside the configured repository directory.
	 *
	 * @param inMemoryThresholdSize 
	 *   the new value for the maximum size of uploaded files for these to be
	 *   kept in memory.
	 * @return
	 *   the object itself, for method chaining. 
	 */
	public FileUploadConfiguration setInMemoryThreshold(long inMemoryThresholdSize) {
		this.inMemoryThresholdSize = inMemoryThresholdSize;
		return this;
	}
}
