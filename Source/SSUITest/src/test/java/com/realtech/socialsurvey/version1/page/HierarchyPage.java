package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class HierarchyPage extends BasePage
{

    public static final String HIERARCHY_PAGE_TITLE = "User Management";
    public HierarchyPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( HIERARCHY_PAGE_TITLE ) ) {
            Assert.fail( "This is not hierarchy page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
