package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class EditProfilePage extends BasePage
{

    public static final String EDIT_PROFILE_PAGE_TITLE = "Profile Settings";
    public EditProfilePage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( EDIT_PROFILE_PAGE_TITLE ) ) {
            Assert.fail( "This is not edit profile page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }

}
