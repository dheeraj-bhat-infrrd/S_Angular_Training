package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;


public class LoginPage extends BasePage
{

    public static final String LOGIN_PAGE_TITLE = "Login";
    
    public static final String INVALID_CREDENTIALS_MESSAGE = "INVALID EMAIL ADDRESS OR PASSWORD";


    public LoginPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( LOGIN_PAGE_TITLE ) ) {
            Assert.fail( "This is not login page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
