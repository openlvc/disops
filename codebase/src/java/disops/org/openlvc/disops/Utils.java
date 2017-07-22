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
package org.openlvc.disops;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public class Utils
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final DateTimeFormatter LONG_DATE = DateTimeFormatter.ofPattern( "eeee d MMMM, u" );

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
	 * Returns `System.currentTimeMillis()` as seconds since the epoch
	 */
	public static int currentTimeSeconds()
	{
		return (int)TimeUnit.MILLISECONDS.toSeconds( System.currentTimeMillis() );
	}

	/**
	 * Force a sleep for the given amount of milliseconds and consume any exceptions
	 * generated from this so we can call this method in a single line without a big
	 * try/catch block every time we need it.
	 */
	public static void sleep( long millis )
	{
		try
		{
			Thread.sleep( millis );
		}
		catch( Exception e )
		{
			return;
		}
	}

	////////////////////////////////////////////////////////////////////////
	/// Date/Time Utils   //////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	public static String formatDateTimeString( long millisSinceEpoch )
	{
		Instant instant = Instant.ofEpochMilli( millisSinceEpoch );
		LocalDateTime datetime = LocalDateTime.ofInstant( instant, ZoneId.systemDefault() );
		return datetime.format( DateTimeFormatter.RFC_1123_DATE_TIME );
	}
	
	public static String formatDateTimeString( LocalDate localdate )
	{
		return localdate.atStartOfDay(ZoneId.systemDefault()).format( LONG_DATE );
	}

	public static String formatTimeBetweenNowAndThen( long millis )
	{
		return formatDuration( System.currentTimeMillis()-millis );
	}
	
	public static String formatDuration( long millisDuration )
	{
		if( millisDuration < 1000 )
		{
			return millisDuration+"ms";
		}
		else if( millisDuration < (1000*60) )
		{
			return (millisDuration/1000)+" seconds";
		}
		else if( millisDuration < (1000*60*60) )
		{
			int seconds = (int) (millisDuration / 1000) % 60 ;
			int minutes = (int) ((millisDuration / (1000*60)) % 60);
			return String.format( "%d minutes %d seconds", minutes, seconds );
		}
		else if( millisDuration < (1000*60*60*24) )
		{
			int seconds = (int) (millisDuration / 1000) % 60 ;
			int minutes = (int) ((millisDuration / (1000*60)) % 60);
			int hours   = (int) ((millisDuration / (1000*60*60)) % 24);
			return String.format( "%d hours %d minutes %d seconds", hours, minutes, seconds );
		}
		else
		{
			int minutes = (int) ((millisDuration / (1000*60)) % 60);
			int hours   = (int) ((millisDuration / (1000*60*60)) % 24);
			int days    = (int) ((millisDuration / (1000*60*60*24)));
			return String.format( "%d days %d hours %d minutes", days, hours, minutes );
		}
	}
		

	////////////////////////////////////////////////////////////////////////
	/// String Utils   /////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	public static String replaceTokens( String string )
	{
		String userhome = System.getProperty("user.home").replace("\\","\\\\");
		string = string.replace( "${user.home}", userhome );
		return string;
	}

	public static String bytesToString( long bytes )
	{
		int unit = 1000;
		if( bytes < unit )
			return bytes + " B";
		int exp = (int)(Math.log( bytes ) / Math.log( unit ));
		return String.format( "%.1f %sB", bytes/Math.pow(unit,exp), "kMGTPE".charAt(exp-1) );
	}

	////////////////////////////////////////////////////////////////////////
	/// JSON Helper Methods   //////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////
	public static double getFloatingPointFromJSON( Object value )
	{
		if( value instanceof Double )
			return ((Double)value).doubleValue();
		else if( value instanceof Long )
			return ((Long)value).doubleValue();
		else if( value instanceof String )
			return Double.parseDouble( (String)value );
		else
			throw new IllegalArgumentException( "Cannot convert type to double: "+value.getClass() );
	}

}
