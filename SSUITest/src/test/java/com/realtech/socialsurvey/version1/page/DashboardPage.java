package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;


public class DashboardPage extends BasePage
{

    public static final String DASHBOARD_PAGE_TITLE = "Dashboard";
    public static final String POPUP_CLOSE_LOCATOR = "xpath=//*[contains(@class, 'wc-final-skip')]";
    public static final String SENDSURVEY_BUTTON_LOCATOR = "id=dsh-btn1";
    public static final String CONNECTTO_LINKEDIN_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Linkedin')]";
    public static final String CONNECTTO_FACEBOOK_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Facebook')]";
    public static final String CONNECTTO_GOOGLE_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Google+')]";
    public static final String CONNECTTO_YELP_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Yelp')]";
    public static final String CONNECTTO_ZILLOW_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Zillow')]";
    public static final String ENTER_LICENSE_DETAILS_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Enter license details')]";
    public static final String ENTER_HOBBIES_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Enter hobbies')]";
    public static final String ENTER_ACHIEVEMENTS_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Enter achievements')]";
    public static final String VIEW_ALL_INCOMOPLETED_SURVEY_LOCATOR = "xpath=//*[@id='incomplete-survey-header']/div[contains(@class, 'dash-sur-link')]";
    public static final String VIEW_AS_DROPDOWN_LOCATOR = "id=dashboard-sel";
    public static final String VIEW_AS_DROPDOWN_OPTION_LOCATOR = "xpath=//*[contains(text(), '@viewasname') and contains(@class, 'da-dd-item')]";

    //Header link locators
    public static final String EDIT_PROFILE_LOCATOR = "link=Edit Profile";
    public static final String EDIT_SURVEY_LOCATOR = "link=Edit Survey";
    public static final String SOCIAL_MONITOR_LOCATOR = "link=Social Monitor";
    public static final String MANAGE_TEAM_LOCATOR = "link=Manage Team";
    public static final String CONFIGURE_SETTINGS_LOCATOR = "link=Configure";
    public static final String HELP_LOCATOR = "link=Help";


    public DashboardPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( DASHBOARD_PAGE_TITLE ) ) {
            Assert.fail( "This is not dashboard page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }


    public void clickSendSurveyPopupClose()
    {
        WebElement closeBtn = getElement( POPUP_CLOSE_LOCATOR );
        closeBtn.click();
    }


    public void clickSendSurveyButtonDashboard()
    {
        WebElement sendSurveyBtn = getElement( SENDSURVEY_BUTTON_LOCATOR );
        sendSurveyBtn.click();
    }


    public void clickConnectToLinkedIn()
    {
        WebElement linkedInConnectBtn = getElement( CONNECTTO_LINKEDIN_BUTTON_LOCATOR );
        linkedInConnectBtn.click();
    }


    public void clickConnectToFacebook()
    {
        WebElement facebookConnectBtn = getElement( CONNECTTO_FACEBOOK_BUTTON_LOCATOR );
        facebookConnectBtn.click();
    }


    public void clickViewAllIncompleteSurvey()
    {
        WebElement incompleteSurveyBtn = getElement( VIEW_ALL_INCOMOPLETED_SURVEY_LOCATOR );
        incompleteSurveyBtn.click();
    }


    public void clickConnectToGoogle()
    {
        WebElement googlePlusBtn = getElement( CONNECTTO_GOOGLE_BUTTON_LOCATOR );
        googlePlusBtn.click();
    }


    public void clickConnectToYelp()
    {
        WebElement yelpBtn = getElement( CONNECTTO_YELP_BUTTON_LOCATOR );
        yelpBtn.click();
    }


    public void clickConnectToZillow()
    {
        WebElement yelpBtn = getElement( CONNECTTO_ZILLOW_BUTTON_LOCATOR );
        yelpBtn.click();
    }


    public void clickEnterLicenseDetails()
    {
        WebElement enterLicenseDetailsBtn = getElement( ENTER_LICENSE_DETAILS_BUTTON_LOCATOR );
        enterLicenseDetailsBtn.click();
    }


    public void clickEnterHobbies()
    {
        WebElement enterHobbiesBtn = getElement( ENTER_HOBBIES_BUTTON_LOCATOR );
        enterHobbiesBtn.click();
    }


    public void clickEnterAchievements()
    {
        WebElement enterAchievementssBtn = getElement( ENTER_ACHIEVEMENTS_BUTTON_LOCATOR );
        enterAchievementssBtn.click();
    }


    public void clickDashboardDropDown()
    {
        WebElement dashboardDropdownBtn = getElement( VIEW_AS_DROPDOWN_LOCATOR );
        dashboardDropdownBtn.click();
    }


    public void selectViewAsOption( String viewAsName )
    {
        WebElement viewAsOption = getElement( VIEW_AS_DROPDOWN_OPTION_LOCATOR.replace( "@viewasname", viewAsName ) );
        viewAsOption.click();
        waitForAjax();
    }


    public EditProfilePage clickEditProfilePage()
    {
        navigateToEditProfilePage();
        waitForAjax();
        return new EditProfilePage( driver );
    }


    public void navigateToEditProfilePage()
    {
        navigateToPage( EDIT_PROFILE_LOCATOR );
    }


    public EditSurveyPage clickEditSurveyPage()
    {
        navigateToEditSurveyPage();
        waitForAjax();
        return new EditSurveyPage( driver );
    }


    public void navigateToEditSurveyPage()
    {
        navigateToPage( EDIT_SURVEY_LOCATOR );
    }


    public SocialMonitorPage clickSocialMonitorPage()
    {
        navigateToSocialMonitorPage();
        waitForAjax();
        return new SocialMonitorPage( driver );
    }


    public void navigateToSocialMonitorPage()
    {
        navigateToPage( SOCIAL_MONITOR_LOCATOR );
    }


    public EditTeamPage clickManageTeamPage()
    {
        navigateToManageTeamPage();
        waitForAjax();
        return new EditTeamPage( driver );
    }


    public void navigateToManageTeamPage()
    {
        navigateToPage( MANAGE_TEAM_LOCATOR );
    }


    public SettingsPage clickConfigureSettingsPage()
    {
        navigateToConfigureSettingsPage();
        waitForAjax();
        return new SettingsPage( driver );
    }


    public void navigateToConfigureSettingsPage()
    {
        navigateToPage( CONFIGURE_SETTINGS_LOCATOR );
    }
    
    public HelpPage clickHelpPage()
    {
        navigateToHelpPage();
        waitForAjax();
        return new HelpPage( driver );
    }


    public void navigateToHelpPage()
    {
        navigateToPage( HELP_LOCATOR);
    }
}
