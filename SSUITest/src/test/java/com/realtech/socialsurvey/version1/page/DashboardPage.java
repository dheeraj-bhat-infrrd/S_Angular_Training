package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class DashboardPage extends BasePage
{

    public static final String DASHBOARD_PAGE_TITLE = "Dashboard";
    public DashboardPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( DASHBOARD_PAGE_TITLE ) ) {
            Assert.fail( "This is not dashboard page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
