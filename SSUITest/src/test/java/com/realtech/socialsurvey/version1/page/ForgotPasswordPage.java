package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;


public class ForgotPasswordPage extends BasePage
{
    public static final String FORGOT_PASSWORD_PAGE_TITLE = "Enter your Registered Email Address";

    public ForgotPasswordPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( FORGOT_PASSWORD_PAGE_TITLE ) ) {
            Assert.fail( "This is not forgot password page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
