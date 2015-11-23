package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class EditSurveyPage extends BasePage
{

    public static final String BUILD_SURVEY_PAGE_TITLE = "Build Survey";
    public EditSurveyPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( BUILD_SURVEY_PAGE_TITLE ) ) {
            Assert.fail( "This is not build survey page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
