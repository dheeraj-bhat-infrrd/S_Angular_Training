package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

public class SignUpPage extends BasePage
{

    private static final Logger LOG = LoggerFactory.getLogger( SignUpPage.class );
    
    public static final String SIGNUP_PAGE_TITLE = "Sign Up";
    public static final String EMAIL_ADDRESS_TAKEN = "This email address has already been taken.";
    public static final String FIRST_NAME_LOCATOR = "name=firstName";
    public static final String LAST_NAME_LOCATOR = "name=lastName";
    public static final String EMAIL_ID_LOCATOR = "name=emailId";
    
    
    public SignUpPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( SIGNUP_PAGE_TITLE ) ) {
            Assert.fail( "This is not sign up page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }
    
    public RegistrationSuccessPage registerUser( String firstNameString, String lastNameString, String emailIdString )
    {
        LOG.trace( "Register user with firstName : " + firstNameString + ", lastName : " + lastNameString + " &emailId : "
            + emailIdString );


        WebElement firstName = getElement( FIRST_NAME_LOCATOR );
        firstName.clear();
        firstName.sendKeys( firstNameString );

        WebElement lastName = getElement( LAST_NAME_LOCATOR );
        lastName.clear();
        lastName.sendKeys( lastNameString );

        WebElement emailId = getElement( EMAIL_ID_LOCATOR );
        emailId.clear();
        emailId.sendKeys( emailIdString );

        //press enter key
        emailId.sendKeys( Keys.ENTER );


        waitForAjax();
        return new RegistrationSuccessPage( driver );
    }

}
