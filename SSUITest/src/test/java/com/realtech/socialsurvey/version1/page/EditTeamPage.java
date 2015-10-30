package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class EditTeamPage extends BasePage
{

    public static final String EDIT_TEAM_PAGE_TITLE = "User Management";
    public EditTeamPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( EDIT_TEAM_PAGE_TITLE ) ) {
            Assert.fail( "This is not edit team page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
