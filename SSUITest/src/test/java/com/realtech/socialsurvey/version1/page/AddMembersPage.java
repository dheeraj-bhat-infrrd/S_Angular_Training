package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class AddMembersPage extends BasePage
{

    public static final String ADD_MEMBERS_PAGE_TITLE = "Build Hierarchy";
    public AddMembersPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( ADD_MEMBERS_PAGE_TITLE ) ) {
            Assert.fail( "This is not add members page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
