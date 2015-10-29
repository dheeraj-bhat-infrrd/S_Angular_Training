package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class FeaturesPage extends BasePage
{

    public static final String FEATURES_PAGE_TITLE = "Amplify the Voice of Your Customer | SocialSurvey";
    public FeaturesPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( FEATURES_PAGE_TITLE ) ) {
            Assert.fail( "This is not features page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
