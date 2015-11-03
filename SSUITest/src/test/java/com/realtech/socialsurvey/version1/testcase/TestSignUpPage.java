package com.realtech.socialsurvey.version1.testcase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.version1.page.SignUpPage;

public class TestSignUpPage extends BaseTestCase
{

    private static final Logger LOG = LoggerFactory.getLogger( TestSignUpPage.class );
    
    @Test ( groups = "home", testName = "TCSSSUP-1")
    public void testUserRegister()
    {
        LOG.trace( "\n\n*** Started Testing: testUserRegister" );
        try {
            driver.get( GlobalConstants.SIGNUP_URL );
            SignUpPage signupPage = new SignUpPage( driver );
            signupPage.registerUser( GlobalConstants.REG_FIRST_NAME, GlobalConstants.REG_LAST_NAME, GlobalConstants.REG_EMAILID );
        } catch ( Exception e ) {
            e.printStackTrace( System.out );
            LOG.error( "*** Exception While Testing: testUserRegister" );
            Assert.fail( "Exception Occurred While Testing: testUserRegister: " + e.getMessage() );
        }
    }

}
