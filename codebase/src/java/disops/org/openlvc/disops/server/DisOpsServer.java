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
package org.openlvc.disops.server;

import org.apache.logging.log4j.Logger;
import org.openlvc.disops.configuration.Configuration;
import org.openlvc.disops.server.influx.InfluxManager;

public class DisOpsServer
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private Configuration configuration;
	private InfluxManager influxManager;
	private Logger logger;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------
	public DisOpsServer( Configuration configuration )
	{
		this.configuration = configuration;
		this.influxManager = new InfluxManager( configuration );
		this.logger = this.configuration.getApplicationLogger();
	}

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public void startup()
	{
		this.logger.info( "Starting DisOps Server" );
		this.influxManager.startup();
	}
	
	public void shutdown()
	{
		this.logger.info( "Shutting down DisOps Server" );
		this.influxManager.shutdown();
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	/// Accessor and Mutator Methods   /////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
}
