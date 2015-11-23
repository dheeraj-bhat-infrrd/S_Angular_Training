package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class HelpPage extends BasePage
{

    public static final String HELP_PAGE_TITLE = "Help";
    public HelpPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( HELP_PAGE_TITLE ) ) {
            Assert.fail( "This is not apps page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
