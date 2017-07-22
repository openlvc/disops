/*
 *   Copyright 2017 Open LVC Project.
 *
 *   This file is part of Open LVC DisOps.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.openlvc.disops.configuration;

import java.io.File;

import org.openlvc.disops.utils.OS;

public class InfluxConfiguration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	// InfluxDB Configuration File
	public static final String KEY_INFLUXD_CONFIG     = "influxdb.config";
	public static final String DEFAULT_INFLUXD_CONFIG = "${do.home}/etc/influxdb/influxdb.conf"; 
	
	// InfluxDB Metadata Directory
	public static final String KEY_METADATA_DIR     = "influxdb.meta.dir";
	public static final String DEFAULT_METADATA_DIR = "${do.data}/influxdb/meta";
	
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration rootConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	protected InfluxConfiguration( Configuration rootConfiguration )
	{
		this.rootConfiguration = rootConfiguration;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------
	///////////////////////////////////////////////
	/// InfluxDB Configuration Settings  //////////
	///////////////////////////////////////////////
	public File getInfluxConfigFile()
	{
		return new File( rootConfiguration.getProperty(KEY_INFLUXD_CONFIG,DEFAULT_INFLUXD_CONFIG) );
	}
	
	public void setInfluxConfigFile( String path )
	{
		rootConfiguration.setProperty( KEY_INFLUXD_CONFIG, path );
	}

	public File getMetadataDirectory()
	{
		return new File( getMetadataDirectoryPath() );
	}
	
	public String getMetadataDirectoryPath()
	{
		return rootConfiguration.getProperty( KEY_METADATA_DIR, DEFAULT_METADATA_DIR );
	}
	
	public void setMetadataDirectory( String path )
	{
		rootConfiguration.setProperty( KEY_METADATA_DIR, path );
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Executable Location Methods   //////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the path to the InfluxDB binaries directory.
	 * 
	 * @return The path to the influxdb directory based on currently OS
	 * @throws ConfigurationException If the OS type is not supported or the bindir can't be found
	 */
	public File getBinaryDirectory() throws ConfigurationException
	{
		// get the operating system path that we can use when determining where the binaries are
		String ostag = "win64";
		switch( OS.getSystemOS() )
		{
			case Windows:
				break;
			case Linux:
				ostag = "linux64";
				break;
			case MacOSX:
				ostag = "macosx";
				break;
			default:
				throw new ConfigurationException( "Unsupported Operating System: "+OS.getSystemOS() );
		}

		// get the root installation directory
		File homeDirectory = rootConfiguration.getHomeDirectory();
		
		// generate and check the path to the binaries
		File bindir = new File( homeDirectory, "lib/influxdb/"+ostag );
		if( bindir.exists() == false )
			throw new ConfigurationException( "Cannot locate InfluxDB binaries (expected path: "+bindir.getAbsolutePath()+")" );
		else
			return bindir;
	}

	/**
	 * @return the path to the influxd executable, adjusted for the current OS.
	 * @throws ConfigurationException If the current OS is not supported
	 */
	public File getBinaryInfluxd() throws ConfigurationException
	{
		switch( OS.getSystemOS() )
		{
			case Windows:
				return new File( getBinaryDirectory(), "influxd.exe" );
			default:
				throw new ConfigurationException( "Unsupported Operating System: "+OS.getSystemOS() );
		}
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
