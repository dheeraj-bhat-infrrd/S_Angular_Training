package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class UpdatePasswordPage extends BasePage
{

    public static final String UPDATE_PASSWORD_PAGE_TITLE = "Update Password";
    public UpdatePasswordPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( UPDATE_PASSWORD_PAGE_TITLE ) ) {
            Assert.fail( "This is not apps page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
