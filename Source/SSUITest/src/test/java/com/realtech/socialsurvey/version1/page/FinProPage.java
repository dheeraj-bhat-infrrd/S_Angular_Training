package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class FinProPage extends BasePage
{
    
    public static final String FIND_PRO_PAGE_TITLE = "Search Professionals | SocialSurvey.me";

    public FinProPage( WebDriver driver )
    {
        super( driver );
        if (!getTitle().equals(FIND_PRO_PAGE_TITLE)) {
            Assert.fail("This is not find pro page. Current page is: " + getTitle());
        }
        
        waitForAjax();
    }

}
