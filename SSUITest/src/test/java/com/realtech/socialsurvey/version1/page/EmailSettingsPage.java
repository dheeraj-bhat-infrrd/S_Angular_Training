package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class EmailSettingsPage extends BasePage
{

    public static final String EMAIL_SETTINGS_PAGE_TITLE = "Email Settings";
    public EmailSettingsPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( EMAIL_SETTINGS_PAGE_TITLE ) ) {
            Assert.fail( "This is not email settings page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
