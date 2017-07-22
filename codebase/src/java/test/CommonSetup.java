/*
 *   Copyright 2013 MYPROJECT-VENDOR
 *
 *   This file is part of MYPROJECT.
 * 
 *   MYPORJECT is free software; you can redistribute and/or modify it under the
 *   terms of the Common Development and Distribution License (the "License").
 *   You may not use this file except in compliance with the License.
 *
 *   Obtain a copy of the License at http://opensource.org/licenses/CDDL-1.0
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   This Software is provided "AS IS" and "WITH ALL FAULTS".
 *   Use of this software is strictly AT YOUR OWN RISK!!!
 *
 */
import java.io.File;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * This class contains project-wide common test setup logic. Before
 * any TestNG suite is run, the methods here are called.
 */
public class CommonSetup
{
	//----------------------------------------------------------
	//                    STATIC VARIABLES
	//----------------------------------------------------------
	/** The root directory from which any files that need to be loaded should be prefixed */
	public static final String TEST_ROOT_DIR = System.getProperty( "test.root.dir" );

	// System properties that will control the log level used for testing
	private static final String TEST_LOG_LEVEL = System.getProperty( "test.loglevel", "OFF" );	
	private static final String FILE_LOG_LEVEL = System.getProperty( "test.fileLogLevel","no" );

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
	@BeforeSuite(alwaysRun=true)
	public static void commonBeforeSuiteSetup()
	{
		///////////////////////////////////////////////////////////////////////////////
		// set the global log level based off the level provided on the command line //
		///////////////////////////////////////////////////////////////////////////////
		// set the log level if it has been specified in the system properties
		String loglevel = System.getProperty( "test.loglevel", "OFF" );
		if( loglevel.equals("${test.loglevel}") )
				loglevel = "OFF";
		
		// set the global log level based off the given level above
		// FIXME Adjust our log level to match the one defined for the test
		//System.setProperty( PorticoConstants.PROPERTY_PORTICO_LOG_LEVEL, loglevel );

		/////////////////////////////////////////
		// project specific pre-test run setup //
		/////////////////////////////////////////
		// FIXME		
	}

	@AfterSuite(alwaysRun=true)
	public static void commonAfterSuiteCleanup()
	{

	}

	/**
	 * If file-based logging is turned on, this method will redirect output for
	 * each individual test into a separate log file so that you can easily locate
	 * the logging caused by one test method even if you have run multiples.
	 */
	public static void testStarting( String className, String methodName )
	{
		// FIXME Enable this if you want to use it

//		// don't set things up unless per-test log files are enabled
//		if( FILE_LOG_LEVEL.equals("${test.fileLogLevel}") || FILE_LOG_LEVEL.equals("no") )
//			return;
//
//		String testSuite = System.getProperty( "test.suite", "unknownSuite" );
//		String filename = "logs/"+testSuite+"/"+className+"/"+methodName+".log";
//		Log4jConfigurator.redirectFileOutput( filename, false );
	}
}

