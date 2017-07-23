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
package org.openlvc.disops.server.influx;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.openlvc.disops.DisOpsException;
import org.openlvc.disops.configuration.Configuration;
import org.openlvc.disops.configuration.InfluxConfiguration;

/**
 * This class manages the lifecycle and configuration of the underyling InfluxDB instance
 * that supports metrics gathering and the associated elements of the TICK stack.
 * 
 *
 */
public class InfluxManager
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	private static final String INFLUX_METADATA_DIR = "INFLUXDB_META_DIR";

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private InfluxConfiguration influxConfiguration;
	private Logger logger;
	
	// Runtime Processes
	private Process processInfluxd;
	private StreamGobbler gobblerInfluxd;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public InfluxManager( Configuration configuration )
	{
		this.configuration = configuration;
		this.influxConfiguration = configuration.getInfluxConfiguration();
		
		this.logger = null; // set in startup()
		this.processInfluxd = null;
		this.gobblerInfluxd = null;
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Lifecycle Management Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////
	public void startup() throws DisOpsException
	{
		this.logger = this.configuration.getApplicationLogger();
		this.logger.info( "Starting Influxd process" );

		// Build up the command line for InfluxDB Daemon
		List<String> commandline = new ArrayList<String>();
		commandline.add( influxConfiguration.getBinaryInfluxd().getAbsolutePath() );
		//commandline.add( "-config" );
		//commandline.add( influxConfiguration.getInfluxConfigFile().getAbsolutePath() );
		commandline.add( "config" );
		
		// Set up the environment, overriding env-vars with any settings from our config
		ProcessBuilder builder = new ProcessBuilder( commandline );
		builder.environment().put( INFLUX_METADATA_DIR, influxConfiguration.getMetadataDirectoryPath() );
		
		builder.redirectOutput( new File("eldumpo.txt") );
		logger.info( builder.command() );
		
		testConnection();
		
		try
		{
			this.processInfluxd = builder.start();
			logger.info( "Influxd process started: "+this.processInfluxd );
		}
		catch( IOException ioex )
		{
			throw new DisOpsException( "Could not start InfluxDB Daemon: "+ioex.getMessage(), ioex );
		}
		
	}
	
	public void shutdown()
	{
		logger.info( "Shutting down influxd process [isAlive="+processInfluxd.isAlive()+"]" );
		if( this.processInfluxd.isAlive() )
			this.processInfluxd.destroy();
	}
	
	
	
	private void testConnection()
	{
		logger.error( "(A)" );
		InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "root", "root");
		influxDB.createDatabase( "test" );
		logger.error( "(B)" );
	}
	
	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	
	///////////////////////////////////////////////////////////////////////////////////////////
	private class StreamGobbler extends Thread
	{
		private InputStream istream;
		StreamGobbler( InputStream istream )
		{
			this.istream = istream;
		}
		
		public void run()
		{
			try
			{
				InputStreamReader reader = new InputStreamReader( istream );
				BufferedReader buffered = new BufferedReader( reader );
				String line = null;
				while( (line=buffered.readLine()) != null )
					System.out.println( line );
			}
			catch( IOException ioex )
			{
			}
		}
	}
	
}
