package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class RegistrationSuccessPage extends BasePage
{

    public static final String REGISTRATION_SUCCESS_PAGE_TITLE = "You're account has been registered.";
    
    public RegistrationSuccessPage( WebDriver driver )
    {
        super( driver );
        
        if (!getTitle().equals(REGISTRATION_SUCCESS_PAGE_TITLE)) {
            Assert.fail("This is not registration success page. Current page is: " + getTitle());
        }
        
        waitForAjax();
    }

}
