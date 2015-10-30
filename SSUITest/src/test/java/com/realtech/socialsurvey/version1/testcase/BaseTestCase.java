package com.realtech.socialsurvey.version1.testcase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.utils.DriverFactory;


@Listeners ( { com.realtech.socialsurvey.version1.testcase.TestSetup.class })
public abstract class BaseTestCase
{
    private Log log = LogFactory.getLog( "UI" );
    public WebDriver driver;

    protected static int NUMBER_OF_TESTS_PASSED = 0;
    protected static int NUMBER_OF_TESTS_FAILED = 0;
    protected static int NUMBER_OF_TESTS_SKIPPED = 0;
    protected static StringBuilder FAILED_TEST_CASES = new StringBuilder();
    protected static StringBuilder SKIPPED_TEST_CASES = new StringBuilder();


    @BeforeMethod ( alwaysRun = true)
    public void setUp()
    {
        log.info( "\n\n-- Started Setting up" );

        driver = DriverFactory.getDriver( DriverFactory.FIREFOX_BROWSER );
        driver
            .manage()
            .timeouts()
            .implicitlyWait( Integer.parseInt( GlobalConstants.get( GlobalConstants.DRIVER_IMPLICIT_WAIT_TIME_DEFAULT ) ),
                TimeUnit.SECONDS );
        driver.manage().window().maximize();
        log.info( "-- Testing on: " + GlobalConstants.SERVER_URL );
        
        if(driver.getCurrentUrl() == null || driver.getCurrentUrl().isEmpty())
            driver.get( GlobalConstants.SERVER_URL );

        log.info( "-- Finished Setting up" );
    }


    @AfterMethod ( alwaysRun = true)
    public void tearDown( ITestResult result, ITestContext context )
    {
        log.info( "\n\n-- Started tear down" );
        try {

            if ( !result.isSuccess() ) {
                if ( result.getStatus() == ITestResult.SKIP ) {
                    NUMBER_OF_TESTS_SKIPPED++;
                    SKIPPED_TEST_CASES.append( "\t" ).append( result.getMethod().getMethodName() ).append( "\n" );
                } else {
                    NUMBER_OF_TESTS_FAILED++;
                    FAILED_TEST_CASES.append( "\t" ).append( result.getMethod().getMethodName() ).append( "\n" );
                    takeScreenshot( result.getMethod().getMethodName() );
                }
            } else {
                NUMBER_OF_TESTS_PASSED++;
            }

            /*if (driver instanceof FirefoxDriver) {
            	List<JavaScriptError> jsErrors = JavaScriptError
            			.readErrors(driver);
            	
            	StringBuilder javaScriptError = new StringBuilder();
            	if (!jsErrors.isEmpty()) {
            		for (int i = 0; i < jsErrors.size(); i++) {
            			javaScriptError.append("\nError : "
            					+ jsErrors.get(i).getErrorMessage() + "\t");
            			javaScriptError.append("At line no : "
            					+ jsErrors.get(i).getLineNumber() + "\t");
            			javaScriptError.append("Source : "
            					+ jsErrors.get(i).getSourceName() + "\t");
            			javaScriptError.append("Console : "
            					+ jsErrors.get(i).getConsole() + "\t");
            			javaScriptError.append("Error : "
            					+ jsErrors.get(i).toString() + "\t");
            		}
            		//System.err.println(javaScriptError.toString());
            	}
            }*/
        } finally {
            log.info( "-- Closing browser" );
            driver.close();
        }

        log.info( "-- Finished tear down" );

        StringBuilder sb = new StringBuilder();

        sb.append( "===============================================" );
        sb.append( "Test Case Execution Progress" );
        sb.append( "===============================================" );
        sb.append( "Executed: " ).append( NUMBER_OF_TESTS_PASSED + NUMBER_OF_TESTS_FAILED + NUMBER_OF_TESTS_SKIPPED )
            .append( "\nPassed: " ).append( NUMBER_OF_TESTS_PASSED ).append( "\nFailed: " ).append( NUMBER_OF_TESTS_FAILED )
            .append( "\nSkipped: " ).append( NUMBER_OF_TESTS_SKIPPED ).append( "\nFailed Test Cases:\n" )
            .append( FAILED_TEST_CASES.toString() ).append( "\nSkipped Test Cases:\n" ).append( SKIPPED_TEST_CASES.toString() );
        sb.append( "===============================================\n\n" );

        log.info( sb.toString() );
    }


    public void takeScreenshot( String testCaseName )
    {
        File scrFile = ( (TakesScreenshot) driver ).getScreenshotAs( OutputType.FILE );

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd HHmmssSSS" );
        String timeNow = sdf.format( Calendar.getInstance().getTime() );

        String filePath = null;

        if ( !GlobalConstants.get( GlobalConstants.STORE_SCREENSHOT_IN_USER_HOME ).equals( "false" ) ) {
            filePath = System.getProperty( "user.home" ) + File.separator + "Selenium" + File.separator
                + timeNow.substring( 0, timeNow.indexOf( " " ) ) + File.separator + testCaseName + "_" + System.currentTimeMillis() + ".png";
        } else {
            filePath = GlobalConstants.get( GlobalConstants.SCREENSHOT_FOLDER ) + File.separator + testCaseName + "_" + System.currentTimeMillis() + ".png";
        }

        // Now you can do whatever you need to do with it, for example copy somewhere
        try {
            FileUtils.copyFile( scrFile, new File( filePath ) );
        } catch ( IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}