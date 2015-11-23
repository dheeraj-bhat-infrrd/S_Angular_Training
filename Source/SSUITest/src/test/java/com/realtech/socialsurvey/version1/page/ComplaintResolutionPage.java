package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class ComplaintResolutionPage extends BasePage
{

    public static final String COMPLAINT_RESOLUTION_PAGE_TITLE = "Complaint Resolution Settings";
    public ComplaintResolutionPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( COMPLAINT_RESOLUTION_PAGE_TITLE ) ) {
            Assert.fail( "This is not complaint resolution page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
