package com.realtech.socialsurvey.version1.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.version1.page.HomePage;
import com.realtech.socialsurvey.version1.page.LoginPage;


public class TestLoginPage extends BaseTestCase
{
    private static final Logger LOG = LoggerFactory.getLogger( TestLoginPage.class );


    @Test ( groups = "login", testName = "TCSSLP-1")
    public void testLoadLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testLogin" );
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            new LoginPage( driver );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testLogin" );
            Assert.fail( "Exception Occurred While Testing: testLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-2")
    public void testUserLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLogin" );
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            LoginPage loginPage = new LoginPage( driver );
            loginPage.loginUser( GlobalConstants.USER_NAME, GlobalConstants.USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-3")
    public void testLoginWithInvalidCredentials()
    {
        LOG.trace( "\n\n*** Started Testing: testLoginWithInvalidCredentials" );

        LoginPage loginPage = null;
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            loginPage = new LoginPage( driver );
            loginPage.loginUser( GlobalConstants.USER_NAME, GlobalConstants.USER_PASSWORD_INCORRECT );
        } catch ( Error e ) {
            //check if it ended up on login page
            if ( loginPage.getErrorMessage().equalsIgnoreCase( LoginPage.INVALID_CREDENTIALS_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testLoginWithInvalidCredentials" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testLoginWithInvalidCredentials: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-4")
    public void testUserLoginInvalidEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginInvalidEmailId" );
        LoginPage loginPage = null;
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            loginPage = new LoginPage( driver );
            loginPage.loginUser( GlobalConstants.EMAILID_INCORRECT, GlobalConstants.USER_PASSWORD );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginInvalidEmailId : " + e.getMessage() );

            if ( loginPage.getErrorMessage().equalsIgnoreCase( HomePage.INVALID_EMAILID_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginInvalidEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginInvalidEmailId: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-5")
    public void testUserLoginEmptyEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginEmptyEmailId" );
        LoginPage loginPage = null;
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            loginPage = new LoginPage( driver );
            loginPage.loginUser( "", GlobalConstants.USER_PASSWORD );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginEmptyEmailId : " + e.getMessage() );

            //check if it ended up on login page
            if ( loginPage != null && loginPage.getErrorMessage().equalsIgnoreCase( HomePage.EMPTY_EMAILID_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginEmptyEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginEmptyEmailId: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-6")
    public void testUserLoginEmptyPassword()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginEmptyPassword" );
        LoginPage loginPage = null;
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            loginPage = new LoginPage( driver );
            loginPage.loginUser( GlobalConstants.USER_NAME, "" );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginEmptyPassword : " + e.getMessage() );

            //check if it ended up on login page
            if ( loginPage != null && loginPage.getErrorMessage().equalsIgnoreCase( HomePage.EMPTY_PASSWORD_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginEmptyPassword" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginEmptyPassword: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "login", testName = "TCSSLP-7")
    public void testForgotPasswordLink()
    {
        LOG.trace( "\n\n*** Started Testing: testForgotPasswordLink" );
        LoginPage loginPage = null;
        try {
            driver.get( GlobalConstants.LOGIN_URL );
            loginPage = new LoginPage( driver );
            loginPage.clickForgotPasswordLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testForgotPasswordLink" );
            Assert.fail( "Exception Occurred While Testing: testForgotPasswordLink: " + e.getMessage() );
        }
    }

}
