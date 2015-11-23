package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class SocialMonitorPage extends BasePage
{

    public static final String SOCIAL_MONITOR_PAGE_TITLE = "Social Monitor";
    public SocialMonitorPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( SOCIAL_MONITOR_PAGE_TITLE ) ) {
            Assert.fail( "This is not social monitor page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
