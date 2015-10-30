/**
 *  Copyright (C) 2013, Intalio Inc.
 *
 *  The program(s) herein may be used and/or copied only with the
 *  written permission of Intalio Inc. or in accordance with the terms
 *  and conditions stipulated in the agreement/contract under which the
 *  program(s) have been supplied.
 *
 * @author Vimal Nair
 */
package com.realtech.socialsurvey.constants;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


public class GlobalConstants
{

    private static Properties prop = null;

    public static final String SERVER_IP = "server.ip";
    public static final String SERVER_PORT = "server.port";

    public static final String DEFAULT_BROWSER = "default.browser";
    public static final String UNIX_DEFAULT_BROWSER = "unix.default.browser";
    public static final String WINDOWS_DEFAULT_BROWSER = "windows.default.browser";
    public static final String IE_DRIVER_PATH = "driver.ie.path";
    public static final String CHROME_DRIVER_PATH = "driver.chrome.path";
    public static final String DRIVER_IMPLICIT_WAIT_TIME_DEFAULT = "driver.implicitwaittime.default";

    public static final String STORE_SCREENSHOT_IN_USER_HOME = "store.screenshot.in.user.home";
    public static final String SCREENSHOT_FOLDER = "screenshot.folder";
    
    //Default values
    public static final String USER_NAME = "shubham@raremile.com";
    public static final String EMAILID_INCORRECT = "shubham@rarem";
    public static final String USER_PASSWORD = "test12";
    public static final String USER_PASSWORD_INCORRECT = "blahblah";
    
    //Register New User
    public static final String REG_FIRST_NAME = "satish";
    public static final String REG_LAST_NAME = "patel";
    public static final String REG_EMAILID = "satish.patel@mailiantor.com";
    public static final String REG_EXISTING_EMAILID = "shubham@raremile.com";
    
    
    //Pro Name
    public static final String PRO_FIRST_NAME = "Sa";
    public static final String PRO_LAST_NAME = "";
    
    //Derived variable
    public static final String SERVER_URL = "http://" + GlobalConstants.get( SERVER_IP ) + ":"
        + GlobalConstants.get( SERVER_PORT );


    /**
     * This will load all the properties in the config.properties and will override it with values 
     * that are set as system environment variables.
     *      
     * @return
     */
    private static void loadProperties()
    {
        if ( prop == null ) {
            prop = new Properties();
            try {
                prop.load( GlobalConstants.class.getClassLoader().getResourceAsStream( "config.properties" ) );

                @SuppressWarnings ( "rawtypes") Enumeration keysEnum = prop.keys();
                for ( ; keysEnum.hasMoreElements(); ) {
                    String key = (String) keysEnum.nextElement();

                    String value = System.getenv( key );

                    if ( value != null ) {
                        prop.setProperty( key, value );
                    }
                }
            } catch ( IOException e ) {
                e.printStackTrace();
            }
            //prop.list(System.out);
        }
    }


    public static String get( String key )
    {

        loadProperties();
        return prop.getProperty( key );
    }
}