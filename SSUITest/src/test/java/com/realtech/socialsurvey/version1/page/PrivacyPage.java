package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class PrivacyPage extends BasePage
{

    public static final String PRIVACY_PAGE_TITLE = "Privacy Policy | SocialSurvey";
    public PrivacyPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( PRIVACY_PAGE_TITLE ) ) {
            Assert.fail( "This is not privacy page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
