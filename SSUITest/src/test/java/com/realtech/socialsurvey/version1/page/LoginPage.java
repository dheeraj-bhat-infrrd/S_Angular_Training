package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class LoginPage extends BasePage
{

    public static final String LOGIN_PAGE_TITLE = "Login";
    
    public static final String INVALID_CREDENTIALS_MESSAGE = "INVALID EMAIL ADDRESS OR PASSWORD";
    public static final String ERROR_LOCATOR = "id=err-nw-txt";


    public LoginPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( LOGIN_PAGE_TITLE ) ) {
            Assert.fail( "This is not login page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }


    public String getErrorMessage()
    {
        WebElement message = getElement( ERROR_LOCATOR );
        return message.getText();
    }
}
