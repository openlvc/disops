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
package org.openlvc.disops.utils;

public enum OS
{
	//----------------------------------------------------------
	//                        VALUES
	//----------------------------------------------------------
	Windows,
	Linux,
	MacOSX;

	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------
	/**
	 * Get the OS Family of the platform we are currently executing on.
	 */
	public static OS getSystemOS()
	{
		String os = System.getProperty( "os.name" );
		boolean windows = os.contains( "indows" );
		boolean linux   = os.contains( "inux" );
		if( windows )
			return Windows;
		else if( linux )
			return Linux;
		else
			return MacOSX;
	}
}
