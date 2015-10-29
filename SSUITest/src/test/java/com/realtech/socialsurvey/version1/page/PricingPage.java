package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class PricingPage extends BasePage
{

    public static final String PRICING_PAGE_TITLE = "Pricing | SocialSurvey";
    public PricingPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( PRICING_PAGE_TITLE ) ) {
            Assert.fail( "This is not pricing page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
