package com.realtech.socialsurvey.version1.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.version1.page.HomePage;
import com.realtech.socialsurvey.version1.page.LoginPage;
import com.realtech.socialsurvey.version1.page.SignUpPage;


public class TestHomePage extends BaseTestCase
{
    private static final Logger LOG = LoggerFactory.getLogger( TestHomePage.class );


    @Test ( groups = "home", testName = "TCSS-1")
    public void testHomePage()
    {
        LOG.trace( "\n\n*** Started Testing: testHomePage" );
        try {
            new HomePage( driver );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testHomePage" );
            Assert.fail( "Exception Occurred While Testing: testHomePage: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-2")
    public void testForgotPasswordClick()
    {
        LOG.trace( "\n\n*** Started Testing: testForgotPasswordClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickForgotPasswordLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testForgotPasswordClick" );
            Assert.fail( "Exception Occurred While Testing: testForgotPasswordClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-3")
    public void testUserLogin()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLogin" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.USER_NAME, GlobalConstants.USER_PASSWORD );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserLogin" );
            Assert.fail( "Exception Occurred While Testing: testUserLogin: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-4")
    public void testUserLoginInvalidCreditentials()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginInvalid" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.USER_NAME, GlobalConstants.USER_PASSWORD_INCORRECT );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginInvalid : " + e.getMessage() );

            //check if it ended up on login page
            LoginPage loginPage = new LoginPage( driver );
            if ( loginPage.getErrorMessage().equalsIgnoreCase( LoginPage.INVALID_CREDENTIALS_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginInvalid" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginInvalid: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-5", dependsOnMethods = "testUserLogin")
    public void testUserLogout()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLogout" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.logout();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserLogout" );
            Assert.fail( "Exception Occurred While Testing: testUserLogout: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-6")
    public void testUserRegister()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegister" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME, GlobalConstants.REG_EMAILID );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserRegister" );
            Assert.fail( "Exception Occurred While Testing: testUserRegister: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-7")
    public void testProSearch()
    {
        LOG.trace( "\n\n*** Started Testing: testProSearch" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.findAPro( GlobalConstants.PRO_FIRST_NAME, GlobalConstants.PRO_LAST_NAME );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testProSearch" );
            Assert.fail( "Exception Occurred While Testing: testProSearch: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-8")
    public void testPricingLinkClick()
    {
        LOG.trace( "\n\n*** Started Testing: testPricingLinkClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickPricingLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testPricingLinkClick" );
            Assert.fail( "Exception Occurred While Testing: testPricingLinkClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-9")
    public void testPrivacyLinkClick()
    {
        LOG.trace( "\n\n*** Started Testing: testPrivacyClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickPrivacyLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testPrivacyClick" );
            Assert.fail( "Exception Occurred While Testing: testPrivacyClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-10")
    public void testFeaturesLinkClick()
    {
        LOG.trace( "\n\n*** Started Testing: testFeaturesLinkClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickFeaturesLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testFeaturesLinkClick" );
            Assert.fail( "Exception Occurred While Testing: testFeaturesLinkClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-11")
    public void testTermsLinkClick()
    {
        LOG.trace( "\n\n*** Started Testing: testTermsLinkClick" );
        try {
            HomePage homePage = new HomePage( driver );
            homePage.clickTermsLink();
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testTermsLinkClick" );
            Assert.fail( "Exception Occurred While Testing: testTermsLinkClick: " + e.getMessage() );
        }
    }


    @Test ( groups = "home", testName = "TCSS-12")
    public void testUserLoginInvalidEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginInvalidEmailId" );
        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.EMAILID_INCORRECT, GlobalConstants.USER_PASSWORD );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginInvalidEmailId : " + e.getMessage() );

            //check if it ended up on login page
            if ( homePage != null && homePage.getToastMessage().equalsIgnoreCase( HomePage.INVALID_EMAILID_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginInvalidEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginInvalidEmailId: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-13")
    public void testUserLoginEmptyEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginEmptyEmailId" );
        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.loginUser( "", GlobalConstants.USER_PASSWORD );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginEmptyEmailId : " + e.getMessage() );

            //check if it ended up on login page
            if ( homePage != null && homePage.getToastMessage().equalsIgnoreCase( HomePage.EMPTY_EMAILID_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginEmptyEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginEmptyEmailId: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-14")
    public void testUserLoginEmptyPassword()
    {
        LOG.trace( "\n\n*** Started Testing: testUserLoginEmptyPassword" );
        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.loginUser( GlobalConstants.USER_NAME, "" );
        } catch ( Error e ) {
            LOG.trace( "*** Login failed: testUserLoginEmptyPassword : " + e.getMessage() );

            //check if it ended up on login page
            if ( homePage != null && homePage.getToastMessage().equalsIgnoreCase( HomePage.EMPTY_PASSWORD_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserLoginEmptyPassword" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserLoginEmptyPassword: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-15")
    public void testUserRegisterWithExistingEmail()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegisterWithExistingEmail" );

        try {
            HomePage homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME,
                GlobalConstants.REG_EXISTING_EMAILID );
        } catch ( Error e ) {
            LOG.trace( "*** Registration failed: testUserRegisterWithExistingEmail : " + e.getMessage() );

            //check if it ended up on login page
            SignUpPage signUpPage = new SignUpPage( driver );
            if ( signUpPage.getErrorMessage().equalsIgnoreCase( SignUpPage.EMAIL_ADDRESS_TAKEN ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserRegisterWithExistingEmail" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserRegisterWithExistingEmail: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-16")
    public void testUserRegisterWithEmptyFirstName()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegisterWithEmptyFirstName" );

        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.registerUser( "", GlobalConstants.REG_LAST_NAME, GlobalConstants.REG_EMAILID );
        } catch ( Error e ) {
            LOG.trace( "*** Registration failed: testUserRegisterWithEmptyFirstName : " + e.getMessage() );

            //check the validation message
            if ( homePage != null && homePage.getHomeRegErrorMessage().equalsIgnoreCase( HomePage.REG_EMPTY_FIRST_NAME_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserRegisterWithEmptyFirstName" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserRegisterWithEmptyFirstName: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-17")
    public void testUserRegisterWithEmptyLastName()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegisterWithEmptyLastName" );

        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, "", GlobalConstants.REG_EMAILID );
        } catch ( Error e ) {
            LOG.trace( "*** Registration failed: testUserRegisterWithEmptyLastName : " + e.getMessage() );

            //check the validation message
            if ( homePage != null && homePage.getHomeRegErrorMessage().equalsIgnoreCase( HomePage.REG_EMPTY_LAST_NAME_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserRegisterWithEmptyLastName" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserRegisterWithEmptyLastName: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-18")
    public void testUserRegisterWithEmptyEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegisterWithEmptyEmailId" );

        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME, "" );
        } catch ( Error e ) {
            LOG.trace( "*** Registration failed: testUserRegisterWithEmptyEmailId : " + e.getMessage() );

            //check the validation message
            if ( homePage != null && homePage.getHomeRegErrorMessage().equalsIgnoreCase( HomePage.REG_EMPTY_EMAIL_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserRegisterWithEmptyEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserRegisterWithEmptyEmailId: " + e.getMessage() );
            }
        }
    }


    @Test ( groups = "home", testName = "TCSS-19")
    public void testUserRegisterWithInvalidEmailId()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegisterWithInvalidEmailId" );

        HomePage homePage = null;
        try {
            homePage = new HomePage( driver );
            homePage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME,
                GlobalConstants.EMAILID_INCORRECT );
        } catch ( Error e ) {
            LOG.trace( "*** Registration failed: testUserRegisterWithInvalidEmailId : " + e.getMessage() );

            //check the validation message
            if ( homePage != null && homePage.getHomeRegErrorMessage().equalsIgnoreCase( HomePage.REG_INVALID_EMAIL_MESSAGE ) ) {
                LOG.trace( "\n\n*** Test case passed: testUserRegisterWithInvalidEmailId" );
            } else {
                Assert.fail( "Exception Occurred While Testing: testUserRegisterWithInvalidEmailId: " + e.getMessage() );
            }
        }
    }
}
