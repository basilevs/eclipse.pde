package org.eclipse.pde.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

/**
 * Generic constants for this plug-in classes.
 */
public interface IPDECoreConstants {

	/** PDE Core plug-in id */
	public static final String PI_BOOT = "org.eclipse.core.boot";
	public static final String PI_BOOT_JAR_NAME = "boot.jar";
	public static final String PI_PDECORE = "org.eclipse.pde.core";
	public static final String PI_RUNTIME = "org.eclipse.core.runtime";
	public static final String PI_RUNTIME_JAR_NAME = "runtime.jar";

	/** properties file name */
	public final static String PROPERTIES_FILE = "build.properties";


	// command line arguments

	public static final String ARG_ARCH = "-buildARCH";
	public static final String ARG_CVS_PASSFILE_LOCATION = "-cvspassfile";
	public static final String ARG_DEV_ENTRIES = "-dev";
	public static final String ARG_DIRECTORY_LOCATION = "-directory";
	public static final String ARG_ELEMENTS = "-elements";
	public static final String ARG_INSTALL_LOCATION = "-install";
	public static final String ARG_NL = "-buildNL";
	public static final String ARG_NO_CHILDREN = "-nochildren";
	public static final String ARG_OS = "-buildOS";
	public static final String ARG_SCRIPT_NAME = "-scriptname";
	public static final String ARG_USAGE = "-?";
	public static final String ARG_WS = "-buildWS";
	

	// default values
	public final static String DEFAULT_BUILD_SCRIPT_FILENAME = "build.xml";
	public final static String DEFAULT_FEATURE_FILENAME_DESCRIPTOR = "feature.xml";
	public final static String DEFAULT_FEATURE_LOCATION = "install/features";
	public final static String DEFAULT_FETCH_SCRIPT_FILENAME = "fetch.xml";
	public final static String DEFAULT_FRAGMENT_LOCATION = "fragments";
	public final static String DEFAULT_PLUGIN_LOCATION = "plugins";
	public final static String DEFAULT_TEMPLATE_SCRIPT_FILENAME = "template.xml";


	// status constants	
	public final static int EXCEPTION_FEATURE_MISSING = 1;
	public final static int EXCEPTION_INSTALL_LOCATION_MISSING = 2;
	public final static int EXCEPTION_MALFORMED_URL = 3;
	public final static int EXCEPTION_MODEL_PARSE = 4;
	public final static int EXCEPTION_PLUGIN_MISSING = 5;
	public final static int EXCEPTION_READ_DIRECTORY = 6;
	public final static int EXCEPTION_WRITING_SCRIPT = 7;
	public final static int EXCEPTION_ELEMENT_MISSING = 8;
	public final static int EXCEPTION_ENTRY_MISSING = 9;
	public final static int WARNING_MISSING_SOURCE = 20;
}