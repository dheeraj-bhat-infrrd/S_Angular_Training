package com.realtech.socialsurvey.version1.testcase;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.version1.page.HomePage;


public class TestHomePage extends BaseTestCase
{
    @Test ( groups = "home", testName = "TCSS-1")
    public void testHomePage()
    {
        System.out.println( "\n\n*** Started Testing: testHomePage" );
        try {
            HomePage homePage = new HomePage( driver );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testHomePage" );
            Assert.fail( "Exception Occurred While Testing: testHomePage: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-2")
    public void testForgotPasswordClick()
    {
        System.out.println( "\n\n*** Started Testing: testForgotPasswordClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickForgotPasswordLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testForgotPasswordClick" );
            Assert.fail( "Exception Occurred While Testing: testForgotPasswordClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-3")
    public void testUserLogin()
    {
        System.out.println( "\n\n*** Started Testing: testUserLogin" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.USER_NAME, GlobalConstants.USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testUserLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-4", dependsOnMethods = "testUserLogin")
    public void testUserLogout()
    {
        System.out.println( "\n\n*** Started Testing: testUserLogout" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.logout();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testUserLogout" );
            Assert.fail( "Exception Occurred While Testing: testUserLogout: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-5")
    public void testUserRegister()
    {
        System.out.println( "\n\n*** Started Testing: testUserRegister" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME, GlobalConstants.REG_EMAILID );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testUserRegister" );
            Assert.fail( "Exception Occurred While Testing: testUserRegister: " + e.getMessage() );
        }
    }


    @Test ( groups = "prosearch", testName = "TCSS-6")
    public void testProSearch()
    {
        System.out.println( "\n\n*** Started Testing: testProSearch" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.findAPro( GlobalConstants.PRO_FIRST_NAME, GlobalConstants.PRO_LAST_NAME );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            System.out.println( "*** Exception While Testing: testProSearch" );
            Assert.fail( "Exception Occurred While Testing: testProSearch: " + e.getMessage() );
        }
    }
}
