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

import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;

/**
 * This singleton provides a number of useful pieces of information about the current system.
 * <b>IMPORTANT:</b> If you want the time methods to work (startup time etc...) you must make at
 * least one call to this class (even if it is just to fetch the instance) at the start of your
 * program so that it will be initialized with all the values. The instance is only created when
 * the class is first initialized. Thus, if you get hold of this class half way through your
 * execution, your start time will be recorded as that point. You have been warned :) 
 */
public class SystemInformation
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	public static final SystemInformation LOCAL = new SystemInformation();
	
	//----------------------------------------------------------
	//                   INSTANCE VARIABLES
	//----------------------------------------------------------
	private String          platform;
	private String          os;
	private String          osVersion;
	private String          javaVersion;
	private String          javaVendor;
	private double          javaClassVersion;
	private int             cpuCount;
	private String          fileSeparator;
	private String          pathSeparator;
	private String          userName;
	private String          userHome;
	private String          launchDir;
	
	private long            startupTime;
	
	private String          ipAddress;

	//----------------------------------------------------------
	//                      CONSTRUCTORS
	//----------------------------------------------------------

	private SystemInformation()
	{
		this.platform         = System.getProperty("os.arch");
		this.os               = System.getProperty("os.name");
		this.osVersion        = System.getProperty( "os.version" );
		this.javaVersion      = System.getProperty("java.version");
		this.javaVendor       = System.getProperty("java.vendor");
		this.javaClassVersion = Double.parseDouble( System.getProperty("java.class.version") );
		this.cpuCount         = Runtime.getRuntime().availableProcessors();
		this.fileSeparator    = System.getProperty( "file.separator" );
		this.pathSeparator    = System.getProperty( "path.separator" );
		this.userName         = System.getProperty( "user.name" );
		this.userHome         = System.getProperty( "user.home" );
		this.launchDir        = System.getProperty( "user.dir" );
		
		this.startupTime      = System.currentTimeMillis();
		
		try
		{
			this.ipAddress = InetAddress.getLocalHost().getHostAddress();
		}
		catch( Exception e )
		{
			// give it a default value
			this.ipAddress = "error: " + e.getMessage();
		}
	}
	
	//----------------------------------------------------------
	//                    INSTANCE METHODS
	//----------------------------------------------------------

	public String getPlatform()
	{
		return this.platform;
	}
	
	public String getOS()
	{
		return this.os;
	}
	
	public String getOSVersion()
	{
		return this.osVersion;
	}
	
	public String getJavaVersion()
	{
		return this.javaVersion;
	}
	
	public String getJavaVendor()
	{
		return this.javaVendor;
	}
	
	public double getJavaClassVersion()
	{
		return this.javaClassVersion;
	}
	
	public int getCPUCount()
	{
		return this.cpuCount;
	}
	
	public String getFileSeparator()
	{
		return this.fileSeparator;
	}
	
	public String getPathSeparator()
	{
		return this.pathSeparator;
	}
	
	public String getUserName()
	{
		return this.userName;
	}
	
	public String getUserHome()
	{
		return this.userHome;
	}
	
	public String getLaunchDir()
	{
		return this.launchDir;
	}
	
	/**
	 * Gets the IP address of this computer, or, if there was a problem, a string with
	 * the error in it
	 */
	public String getIpAddress()
	{
		return this.ipAddress;
	}
	
	/**
	 * Get the startup time in the following format: month dayof, year - hh:mm:ss
	 * <p/>
	 * <b>NOTE:</b> This isn't really the startup time of the JVM, rather, it will be the time
	 * that this class was first accessed (and thus, the static SYSINFO var was first initialized).
	 */ 
	public String getStartupTime()
	{
		Date date = new Date( startupTime );
		String sDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format( date );
		String sTime = DateFormat.getTimeInstance(DateFormat.MEDIUM).format( date );
		return sDate + " - " + sTime;
	}
	
	/**
	 * Returns the raw startup time as obtained from System.currentTimeMillis() when this class
	 * was first loaded.
	 */
	public long getRawStartupTime()
	{
		return startupTime;
	}
	
	/** Get the current time in the following format: month dayof, year - hh:mm:ss */
	public String getCurrentTime()
	{
		Date date = new Date( System.currentTimeMillis() );
		String sDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format( date );
		String sTime = DateFormat.getTimeInstance(DateFormat.MEDIUM).format( date );
		return sDate + " - " + sTime;
	}
	
	/** Get the current uptime of the server - FIX THIS, DOESN'T WORK. NEVER CODE WHILE DRUNK */
	public String getUptime()
	{
		long difference = System.currentTimeMillis() - startupTime;

		// pre-declare everything
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		long days = 0;
		
		try
		{
			// get the seconds
			seconds = difference / 1000;
			
			// get the minutes
			minutes = seconds / 60;
			// get the remaining seconds
			seconds = seconds % minutes;
			
			// get the hours
			hours = minutes / 60;
			// get the remaining minutes
			minutes = minutes % hours;
			
			// get the days
			days = hours / 24;
			// get the remaining hours
			hours = hours % days;
		}
		catch( ArithmeticException ae )
		{
			// divide by 0 exception :D we've hit the end of the road here :)
			// this is just easier than having 1,000,000 if() blocks all up in this place
		}
		
		StringBuilder buf = new StringBuilder( "" );
		buf.append( days );
		buf.append( " days, " );
		buf.append( hours );
		buf.append( " hours, " );
		buf.append( minutes );
		buf.append( " minutes, " );
		buf.append( seconds );
		buf.append( " seconds" );
		return buf.toString();
	}
	
	/**
	 * Gets the raw uptime in milliseconds. From the time this class was first loaded, to now
	 */
	public long getRawUptime()
	{
		return System.currentTimeMillis() - this.startupTime;
	}

	//----------------------------------------------------------
	//                     STATIC METHODS
	//----------------------------------------------------------

}

