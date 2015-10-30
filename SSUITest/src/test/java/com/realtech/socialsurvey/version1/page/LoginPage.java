package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class LoginPage extends BasePage
{

    public static final String LOGIN_PAGE_TITLE = "Login";

    public static final String INVALID_CREDENTIALS_MESSAGE = "INVALID EMAIL ADDRESS OR PASSWORD";

    public static final String USER_NAME_LOCATOR = "id=login-user-id";
    public static final String USER_PASSWORD_LOCATOR = "id=login-pwd";
    public static final String FORGOT_PASSWORD_LOCATOR = "link=Forgot Password";


    public LoginPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( LOGIN_PAGE_TITLE ) ) {
            Assert.fail( "This is not login page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }


    public DashboardPage loginUser( String userNameString, String passwordString )
    {

        WebElement userName = getElement( USER_NAME_LOCATOR );
        userName.clear();
        userName.sendKeys( userNameString );

        WebElement password = getElement( USER_PASSWORD_LOCATOR );
        password.clear();
        password.sendKeys( passwordString );

        //Press enter to login
        password.sendKeys( Keys.ENTER );


        waitForAjax();
        return new DashboardPage( driver );
    }


    public ForgotPasswordPage clickForgotPasswordLink()
    {
        navigateToForgotPasswordPage();
        waitForAjax();
        return new ForgotPasswordPage( driver );
    }


    public void navigateToForgotPasswordPage()
    {
        navigateToPage( FORGOT_PASSWORD_LOCATOR );
    }


}
