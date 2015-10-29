package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TermsPage extends BasePage
{

    public static final String TERMS_PAGE_TITLE = "Terms of Use | SocialSurvey";
    public TermsPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( TERMS_PAGE_TITLE ) ) {
            Assert.fail( "This is not terms page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
