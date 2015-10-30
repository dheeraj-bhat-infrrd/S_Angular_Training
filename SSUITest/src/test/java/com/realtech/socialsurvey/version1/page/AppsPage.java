package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class AppsPage extends BasePage
{

    public static final String APPS_PAGE_TITLE = "Apps";
    public AppsPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( APPS_PAGE_TITLE ) ) {
            Assert.fail( "This is not apps page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
