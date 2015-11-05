package com.realtech.socialsurvey.version1.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.version1.page.DashboardPage;
import com.realtech.socialsurvey.version1.page.HomePage;


public class TestUserDashboard extends BaseTestCase
{
    private static final Logger LOG = LoggerFactory.getLogger( TestUserDashboard.class );


    @Test ( groups = "userdashboard", testName = "TCSSUD-1")
    public void testUserDashboardPageCompanyAdminLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardPageCompanyAdminLogin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.COMPANY_USER_NAME, GlobalConstants.COMPANY_USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardPageCompanyAdminLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardPageCompanyAdminLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-2")
    public void testUserDashboardPageRegionAdminLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardPageRegionAdminLogin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.REGION_USER_NAME, GlobalConstants.REGION_USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardPageRegionAdminLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardPageRegionAdminLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-3")
    public void testUserDashboardPageBranchAdminLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardPageBranchAdminLogin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.BRANCH_USER_NAME, GlobalConstants.BRANCH_USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardPageBranchAdminLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardPageBranchAdminLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-4")
    public void testUserDashboardPageIndividualLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardPageIndividualLogin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME, GlobalConstants.INDIVIDUAL_USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardPageIndividualLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardPageIndividualLogin: " + e.getMessage() );
        }

    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-5")
    public void testUserDashboardSendSurveyPopupClose()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardSendSurveyPopupClose" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardPageIndividualLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardPageIndividualLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-6")
    public void testUserDashboardSendSurveyPopupClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardSendSurveyPopupClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickSendSurveyButtonDashboard();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardSendSurveyPopupClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardSendSurveyPopupClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-7")
    public void testUserDashboardConnectToLinkedInClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardConnectToLinkedInClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickConnectToLinkedIn();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardConnectToLinkedInClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardConnectToLinkedInClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-8")
    public void testUserDashboardConnectToFacebookClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardConnectToFacebookClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickConnectToFacebook();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardConnectToFacebookClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardConnectToFacebookClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-9")
    public void testUserDashboardViewAllIncompletedSurveyClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardViewAllIncompletedSurveyClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickViewAllIncompleteSurvey();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardViewAllIncompletedSurveyClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardViewAllIncompletedSurveyClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-10")
    public void testUserDashboardChangeViewAsDropdown()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardChangeViewAsDropdown" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickDashboardDropDown();
            dashboardPage.selectViewAsOption( "another region" );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardChangeViewAsDropdown" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardChangeViewAsDropdown: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-11")
    public void testUserDashboardConnectToGooglePlusClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardConnectToGooglePlusClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickConnectToGoogle();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardConnectToGooglePlusClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardConnectToConnectToGooglePlusClick: "
                + e.getMessage() );
        }
    }
}
