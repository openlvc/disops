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
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openlvc.disops.DisOpsException;
import org.openlvc.disops.Utils;

/**
 * Class encapsulating configuration information that has been loaded from a
 * configuration file and the command line.
 * 
 * On consturction the command line will first be read and any properties specified
 * here will override those that come later. If one can be located, the configuration
 * file will then be read and loaded. Defaults are provided and will be used should
 * any configuration property not be defined on the command line or in any config file.
 * 
 * Configuration files are represented as flat key/value pair properties files. The
 * various statically defined strings represent the keys that will be looked for in
 * the configuration file.
 * 
 * Once loaded, the various setter properties can be used to further manipulate and
 * alter any settings in code.
 */
public class Configuration
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final String KEY_CONFIG_FILE     = "disops.configfile";
	public static final String KEY_LOG_LEVEL       = "disops.loglevel";
	public static final String KEY_LOG_FILE        = "disops.logfile";
	
	public static final String KEY_DATA_DIR        = "disops.data";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	protected Properties properties;
	private Log4jConfiguration loggingConfiguration;
	private Logger applicationLogger;
	private String configFile = "etc/disops.config";
	
	private File disopsHome;
	private File disopsData;
	
	// Child Configuration Objects
	private InfluxConfiguration influxConfiguration;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public Configuration( String[] args )
	{
		// main storage
		this.properties = new Properties();
		
		// logging configuration
		this.loggingConfiguration = new Log4jConfiguration( "disops" );
		this.loggingConfiguration.setConsoleOn( true );
		this.loggingConfiguration.setFileOn( false );
		this.loggingConfiguration.setLevel( "INFO" );
		this.applicationLogger = null; // lazy loaded via getApplicationLogger()

		this.disopsHome = null;
		this.disopsData = null;
		
		// Sub-Component Configuration
		this.influxConfiguration = new InfluxConfiguration( this );
		
		// TODO Split out into a "Configurator" object
		// see if the user specified a config file on the command line before we process it
		this.checkArgsForConfigFile( args );
		this.loadConfigFile();
		
		// pull out any command line args and use them to override all values
		this.applyCommandLine( args );
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Sub-Component Configurations    ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public InfluxConfiguration getInfluxConfiguration()
	{
		return this.influxConfiguration;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Configuration Outside Config File    ///////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Get the DisOps Data Directory. This is a writable place that we can put log files,
	 * snapshots and everything else.
	 */
	public File getDataDirectory()
	{
		// if it came in as a command line argument we will already have it loaded
		if( this.disopsData != null )
			return disopsData;

		// do we have a configured value already?
		File datadir = null;
		if( properties.containsKey(KEY_DATA_DIR) )
			datadir = new File( Utils.replaceTokens(properties.getProperty(KEY_DATA_DIR)) );
		else
			datadir = getOsDefaultDataDir();

		// if the directory does not exist, create it
		if( datadir.exists() == false )
			datadir.mkdirs();
		
		// check that it is a directory
		if( datadir.isDirectory() == false )
			throw new ConfigurationException( "DisOps Data Directory is not a directory: "+datadir );

		this.disopsData = datadir;
		return this.disopsData;
	}
	
	private File getOsDefaultDataDir()
	{
		String os = System.getProperty( "os.name" );
		boolean windows = os.contains( "indows" );
		String datadir = null;
		if( windows )
			datadir = System.getProperty("user.home")+"\\My Documents\\Open LVC DisOps";
		else
			datadir = System.getProperty("user.home")+"/.disops";

		return new File( datadir );
	}

	/**
	 * Set the data directory to the given path, throwing an exception if it already exists
	 * on the file system but isn't a directory, or cannot be written to.
	 */
	public void setDataDirectory( String path )
	{
		File directory = new File( path );
		if( directory.exists() == false )
			directory.mkdirs();
		
		if( directory.isDirectory() == false )
			throw new ConfigurationException( "Can't create data dir, path exists: "+path );
		
		if( directory.canWrite() )
			throw new ConfigurationException( "No permissions to write to data directory: "+path );
		
		this.disopsData = directory;
	}
	
	/**
	 * Get the directory where the DisOps Server is installed
	 */
	public File getHomeDirectory()
	{
		// if it came in as a command line argument we will already have it loaded
		if( this.disopsHome != null )
			return disopsHome;

		// we haven't loaded it yet, so we'll fall back on the following locations in order:
		//   1. Enviornment Variable: DISOPS_HOME
		//   2. Current working directory
		String envvar = System.getenv( "DISOPS_HOME" );
		if( envvar != null )
		{
			File file = new File( envvar );
			if( file.exists() && file.isDirectory() )
			{
				this.disopsHome = file;
				return file;
			}
		}
		
		this.disopsHome = new File( "./" );
		return this.disopsHome;
	}

	/**
	 * Set the location where the DisOps Server is installed
	 */
	public void setHomeDirectory( String path ) throws ConfigurationException
	{
		File file = new File( path );
		if( file.exists() && file.isDirectory() )
			this.disopsHome = file;
		else
			throw new ConfigurationException( "DisOps Server home directory not valid: "+path );
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public Log4jConfiguration getLogConfiguration()
	{
		return this.loggingConfiguration;
	}

	public void setLogLevel( String loglevel )
	{
		this.loggingConfiguration.setLevel( loglevel );
	}

	public void setLogFile( String logfile )
	{
		this.loggingConfiguration.setFile( logfile );
		this.loggingConfiguration.setFileOn( true );
	}
	
	/**
	 * Fetch the application logger. If it hasn't been create yet, activate the
	 * stored configuration, generate the logger and return it.
	 */
	public Logger getApplicationLogger()
	{
		if( this.applicationLogger != null )
			return applicationLogger;

		// activate the configuration and return the logger
		this.loggingConfiguration.activateConfiguration();
		this.applicationLogger = LogManager.getFormatterLogger( "disops" );
		return applicationLogger;
	}



	////////////////////////////////////////////////////////////////////////////////////////////
	/// Command Line Argument Methods   ////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/** If --config-file is in the args, load it into the local var for processing. We do
	    this separately so we can load the config file before the command line args, which
	    we do later so that they override all other values */
	private void checkArgsForConfigFile( String[] args )
	{
		for( int i = 0; i < args.length; i++ )
		{
			if( args[i].equalsIgnoreCase("--config-file") )
			{
				this.configFile = args[++i];
				return;
			}
		}
	}

	/**
	 * Apply the given command line args to override any defaults that we have
	 */
	private void applyCommandLine( String[] args ) throws DisOpsException
	{
		for( int i = 0; i < args.length; i++ )
		{
			String argument = args[i];
			if( argument.equalsIgnoreCase("--config-file") )
				this.configFile = args[++i];
			else if( argument.equalsIgnoreCase("--log-level") )
				this.loggingConfiguration.setLevel( args[++i] );
			else
				throw new DisOpsException( "Unknown argument: "+argument );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Configuration File Loading   ///////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	private void loadConfigFile()
	{
		//
		// Load the configuration file into a Properties object
		//
		Properties properties = new Properties();
		File configurationFile = new File( this.configFile );
		if( configurationFile.exists() )
		{
    		// configuration file exists, load the properties into it
    		try
    		{
    			properties.load( configurationFile.toURI().toURL().openStream() );
    		}
    		catch( Exception e )
    		{
    			throw new RuntimeException( "Problem parsing config file: "+e.getMessage(), e );
    		}
		}
		
		//
		// pull the logging configuration out of the properties
		//
		if( properties.containsKey(KEY_LOG_FILE) )
		{
			this.loggingConfiguration.setFile(properties.getProperty(KEY_LOG_FILE));
			this.loggingConfiguration.setFileOn( true );
		}

		if( properties.containsKey(KEY_LOG_LEVEL) )
			this.loggingConfiguration.setLevel( properties.getProperty(KEY_LOG_LEVEL) );
		
		//
		// Store the main property set in ours for later reference
		//
		properties.forEach( (key,value) -> this.properties.put(key,value) );
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Utility/Helper Methods   ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Return the value for the given key from our contained configuration properties.
	 * Use the default value if an entry cannot be found for the key.
	 * 
	 * Run the value through a token replacement for any of the supported value tokens:
	 * 
	 *   - ${do.home}  - Installation Directory
	 *   - ${do.data}  - Data Directory
	 * 
	 * @param key The key to look for 
	 * @param defaultValue The value to use if there is no key found
	 * @return The value for the key or the given default. If the value exists it will have all
	 *         common tokens exchanged for their configured values
	 */
	protected final String getProperty( String key, String defaultValue )
	{
		// get any configured value
		String value = properties.getProperty( key, defaultValue );
		if( value == null )
			return null;
		
		// replace any tokens
		if( value.contains("${do.data}") )
			value = value.replace( "${do.data}", getDataDirectory().getAbsolutePath() );
		if( value.contains("${do.home}") )
			value = value.replace( "${do.home}", getHomeDirectory().getAbsolutePath() );
		
		return value;
	}

	protected final void setProperty( String key, String value )
	{
		properties.setProperty( key, value );
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	public static void printHelp()
	{
		System.out.println( "DisOps Server - Monitoring and DevOps for OpenLVC DIS Networks" );
		System.out.println( "Usage: bin/disops-server [--args]" );
		System.out.println( "" );

		System.out.println( "  --config-file         string   (optional)  Location of the config file             (default: etc/disops.config)" );
		System.out.println( "  --log-level           string   (optional)  [OFF,FATAL,ERROR,WARN,INFO,DEBUG,TRACE] (default: INFO)" );
		System.out.println( "" );
	}

}
