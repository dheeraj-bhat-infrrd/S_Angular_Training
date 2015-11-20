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


    @Test ( groups = "userdashboard", testName = "TCSSUD-12")
    public void testUserDashboardConnectToYelpClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardConnectToYelpClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickConnectToYelp();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardConnectToYelpClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardConnectToYelpClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-13")
    public void testUserDashboardConnectToZillowClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardConnectToZillowClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickConnectToZillow();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardConnectToZillowClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardConnectToZillowClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-14")
    public void testUserDashboardEnterLicenseDetailsClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardEnterLicenseDetailsClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEnterLicenseDetails();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardEnterLicenseDetailsClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardEnterLicenseDetailsClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-15")
    public void testUserDashboardEnterHobbiesClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardEnterHobbiesClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEnterHobbies();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardEnterHobbiesClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardEnterHobbiesClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "userdashboard", testName = "TCSSUD-16")
    public void testUserDashboardEnterAchievementsClick()
    {
        LOG.trace( "\n\n*** Started Testing: testUserDashboardEnterAchievementsClick" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEnterAchievements();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserDashboardEnterAchievementsClick" );
            Assert.fail( "Exception Occurred While Testing: testUserDashboardEnterAchievementsClick: " + e.getMessage() );
        }
    }


    //Test the user header links

    /**
     *Test edit profile for all profile types 
     */
    @Test ( groups = "userheader", testName = "TCSSUD-17")
    public void testClickEditProfileIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEditProfileIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEditProfilePage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEditProfileIndividual" );
            Assert.fail( "Exception Occurred While Testing: testClickEditProfileIndividual: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-18")
    public void testClickEditProfileBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEditProfileBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEditProfilePage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEditProfileBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickEditProfileBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-19")
    public void testClickEditProfileRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEditProfileRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEditProfilePage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEditProfileRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickEditProfileRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-20")
    public void testClickEditProfileCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEditProfileCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEditProfilePage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEditProfileCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickEditProfileCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test Edit survey click for company admin
     */
    @Test ( groups = "userheader", testName = "TCSSUD-21")
    public void testClickEditSurveyCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEditSurveyCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEditSurveyPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEditSurveyCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickEditSurveyCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test social monitor click for company admin
     */
    @Test ( groups = "userheader", testName = "TCSSUD-22")
    public void testClickSocialMonitorCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickSocialMonitorCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickSocialMonitorPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickSocialMonitorCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickSocialMonitorCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test manage team click for all profile types
     */
    @Test ( groups = "userheader", testName = "TCSSUD-23")
    public void testClickManageTeamBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickManageTeamBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickManageTeamPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickManageTeamBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickManageTeamBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-24")
    public void testClickManageTeamRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickManageTeamRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickManageTeamPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickManageTeamRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickManageTeamRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-25")
    public void testClickManageTeamCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickSocialMonitorCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickManageTeamPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickSocialMonitorCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickSocialMonitorCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test help page link click for all profiles
     */
    @Test ( groups = "userheader", testName = "TCSSUD-26")
    public void testClickHelpIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testClickHelpIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickHelpPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickHelpIndividual" );
            Assert.fail( "Exception Occurred While Testing: testClickHelpIndividual: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-27")
    public void testClickHelpBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickHelpBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickHelpPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickHelpBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickHelpBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-28")
    public void testClickHelpRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickHelpRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickHelpPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickHelpRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickHelpRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-29")
    public void testClickHelpCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickHelpCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickHelpPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickHelpCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickHelpCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test complaint resolution click
     */
    @Test ( groups = "userheader", testName = "TCSSUD-30")
    public void testClickComplaintResolutionCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickComplaintResolutionCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickComplaintResolutionPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickComplaintResolutionCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickComplaintResolutionCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test update password page link click for all profiles
     */
    @Test ( groups = "userheader", testName = "TCSSUD-31")
    public void testClickUpdatePasswordIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testClickUpdatePasswordIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickUpdatePasswordPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickUpdatePasswordIndividual" );
            Assert.fail( "Exception Occurred While Testing: testClickUpdatePasswordIndividual: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-32")
    public void testClickUpdatePasswordBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickUpdatePasswordBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickUpdatePasswordPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickUpdatePasswordBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickUpdatePasswordBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-33")
    public void testClickUpdatePasswordRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickUpdatePasswordRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickUpdatePasswordPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickUpdatePasswordRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickUpdatePasswordRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-34")
    public void testClickUpdatePasswordCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickUpdatePasswordCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickUpdatePasswordPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickUpdatePasswordCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickUpdatePasswordCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test email settings click
     */
    @Test ( groups = "userheader", testName = "TCSSUD-35")
    public void testClickEmailSettingsCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickEmailSettingsCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickEmailSettingsPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickEmailSettingsCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickEmailSettingsCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test apps page link click for all profiles
     */
    @Test ( groups = "userheader", testName = "TCSSUD-36")
    public void testClickAppSettingsIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testClickAppSettingsIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickAppSettingsPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickAppSettingsIndividual" );
            Assert.fail( "Exception Occurred While Testing: testClickAppSettingsIndividual: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-37")
    public void testClickAppSettingsBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickAppSettingsBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickAppSettingsPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickAppSettingsBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickAppSettingsBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-38")
    public void testClickAppSettingsRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickAppSettingsRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickAppSettingsPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickAppSettingsRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickAppSettingsRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-39")
    public void testClickAppSettingsCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickAppSettingsCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickAppSettingsPage();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickAppSettingsCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickAppSettingsCompanyAdmin: " + e.getMessage() );
        }
    }


    /**
     * Test apps page link click for all profiles
     */
    @Test ( groups = "userheader", testName = "TCSSUD-40")
    public void testClickLogoutIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testClickLogoutIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickLogoutButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickLogoutIndividual" );
            Assert.fail( "Exception Occurred While Testing: testClickLogoutIndividual: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-41")
    public void testClickLogoutBranchAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickLogoutBranchAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.BRANCH_USER_NAME,
                GlobalConstants.BRANCH_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickLogoutButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickLogoutBranchAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickLogoutBranchAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-42")
    public void testClickLogoutRegionAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickLogoutRegionAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.REGION_USER_NAME,
                GlobalConstants.REGION_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickLogoutButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickLogoutRegionAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickLogoutRegionAdmin: " + e.getMessage() );
        }
    }


    @Test ( groups = "userheader", testName = "TCSSUD-43")
    public void testClickLogoutCompanyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testClickLogoutCompanyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickLogoutButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testClickLogoutCompanyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testClickLogoutCompanyAdmin: " + e.getMessage() );
        }
    }


    //Test send survey after login with individual login
    @Test ( groups = "userdashboard", testName = "TCSSUD-44")
    public void testSendSurveyIndividual()
    {
        LOG.trace( "\n\n*** Started Testing: testSendSurveyIndividual" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.INDIVIDUAL_USER_NAME,
                GlobalConstants.INDIVIDUAL_USER_PASSWORD );
            dashboardPage.fillSurveyDetails( 1, GlobalConstants.CUSTOMER_FIRST_NAME, GlobalConstants.CUSTOMER_LAST_NAME,
                GlobalConstants.CUSTOMER_EMAILID, null );
            dashboardPage.clickSendSurveyButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testSendSurveyIndividual" );
            Assert.fail( "Exception Occurred While Testing: testSendSurveyIndividual: " + e.getMessage() );
        }
    }


    //Test send survey after login with admin login
    @Test ( groups = "userdashboard", testName = "TCSSUD-45")
    public void testSendSurveyAdmin()
    {
        LOG.trace( "\n\n*** Started Testing: testSendSurveyAdmin" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.fillSurveyDetails( 1, GlobalConstants.CUSTOMER_FIRST_NAME, GlobalConstants.CUSTOMER_LAST_NAME,
                GlobalConstants.CUSTOMER_EMAILID, GlobalConstants.AGENT_NAME );
            dashboardPage.clickSendSurveyButton();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testSendSurveyAdmin" );
            Assert.fail( "Exception Occurred While Testing: testSendSurveyAdmin: " + e.getMessage() );
        }
    }


    //Test resend survey from dashboard
    @Test ( groups = "userdashboard", testName = "TCSSUD-46")
    public void testResendSurveyRequest()
    {
        LOG.trace( "\n\n*** Started Testing: testResendSurveyRequest" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickResendSurveyFromDashbaord( 1 );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testResendSurveyRequest" );
            Assert.fail( "Exception Occurred While Testing: testResendSurveyRequest: " + e.getMessage() );
        }
    }


    //Test send survey after login with admin login
    @Test ( groups = "userdashboard", testName = "TCSSUD-47")
    public void testRetakeSurveyRequest()
    {
        LOG.trace( "\n\n*** Started Testing: testRetakeSurveyRequest" );
        try {
            driver.get( GlobalConstants.SERVER_URL );
            HomePage homePage = new HomePage( driver );
            DashboardPage dashboardPage = homePage.loginUser( GlobalConstants.COMPANY_USER_NAME,
                GlobalConstants.COMPANY_USER_PASSWORD );
            dashboardPage.clickSendSurveyPopupClose();
            dashboardPage.clickRetakeSurveyFromDashbaord( 1 );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testRetakeSurveyRequest" );
            Assert.fail( "Exception Occurred While Testing: testRetakeSurveyRequest: " + e.getMessage() );
        }
    }
}
