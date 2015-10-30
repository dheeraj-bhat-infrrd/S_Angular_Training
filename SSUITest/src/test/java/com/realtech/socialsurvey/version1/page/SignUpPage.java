package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SignUpPage extends BasePage
{

    public static final String SIGNUP_PAGE_TITLE = "Sign Up";
    public static final String EMAIL_ADDRESS_TAKEN = "This email address has already been taken.";
    
    
    public SignUpPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( SIGNUP_PAGE_TITLE ) ) {
            Assert.fail( "This is not sign up page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
