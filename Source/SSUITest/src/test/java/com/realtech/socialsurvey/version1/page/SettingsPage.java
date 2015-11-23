package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SettingsPage extends BasePage
{

    public static final String SETTINGS_PAGE_TITLE = "Edit Settings";
    public SettingsPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( SETTINGS_PAGE_TITLE ) ) {
            Assert.fail( "This is not configure settings page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
