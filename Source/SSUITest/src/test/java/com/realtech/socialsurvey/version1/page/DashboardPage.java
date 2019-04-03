package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;


public class DashboardPage extends BasePage
{

    public static final String DASHBOARD_PAGE_TITLE = "Dashboard";

    //Dashboard elements
    public static final String POPUP_CLOSE_LOCATOR = "xpath=//*[contains(@class, 'wc-final-skip')]";
    public static final String SENDSURVEY_BUTTON_LOCATOR = "id=dsh-btn1";
    public static final String CONNECTTO_LINKEDIN_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Linkedin')]";
    public static final String CONNECTTO_FACEBOOK_BUTTON_LOCATOR = "xpath=//div[contains(text(), 'Connect to Facebook')]";
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
    public static final String UPDATE_PASSWORD_LOCATOR = "id=change-password";
    public static final String COMPLAINT_RESOLUTION_LOCATOR = "xpath=//*[contains(text(), 'Complaint Resolution') and contains(@class, 'hdr-link-item-dropdown-item')]";
    public static final String EMAIL_SETTINGS_LOCATOR = "xpath=//*[contains(text(), 'Emails') and contains(@class, 'hdr-link-item-dropdown-item')]";
    public static final String APP_SETTINGS_LOCATOR = "xpath=//*[contains(text(), 'Apps') and contains(@class, 'hdr-link-item-dropdown-item')]";
    public static final String USER_HEADER_IMAGE = "id=hdr-usr-img";
    public static final String LOGOUT_BTN_LOCATOR = "id=user-logout";

    //send survey popup
    public static final String SURVEY_POPUP_CUSTOMER_FIRST_NAME_LOCATOR = "css=#wc-review-table-inner > div:nth-child(@RowNumber) > div.float-left > input.wc-review-fname";
    public static final String SURVEY_POPUP_CUSTOMER_LAST_NAME_LOCATOR = "css=#wc-review-table-inner > div:nth-child(@RowNumber) > div.float-left > input.wc-review-lname";
    public static final String SURVEY_POPUP_CUSTOMER_EMAIL_LOCATOR = "css=#wc-review-table-inner > div:nth-child(@RowNumber) > div.float-left > input.wc-review-email";
    public static final String SURVEY_POPUP_AGENT_LOCATOR = "css=#wc-review-table-inner > div:nth-child(@RowNumber) > div.float-left > input.wc-review-agentname";
    public static final String SEND_SURVEY_BTN = "id=wc-send-survey";

    //Survey dashboard
    public static final String RESEND_SURVEY_DASHBOARD = "css=#dsh-inc-srvey > div:nth-child(@RowNumber) > div.float-right.dash-lp-rt-img.cursor-pointer";
    public static final String RETAKESURVEY_DASHBOARD = "css=#review-details > div:nth-child(@RowNumber) > div.ppl-header-wrapper.clearfix > div.float-right.ppl-header-right > div.report-resend-icn-container.clearfix.float-right > div.restart-survey-mail-txt.report-txt";
    public static final String REPORTSURVEY_DASHBOARD = "css=#review-details > div:nth-child(@RowNumber) > div.ppl-header-wrapper.clearfix > div.float-right.ppl-header-right > div.report-resend-icn-container.clearfix.float-right > div.report-abuse-txt.report-txt";
    public static final String REPORTSURVEY_DASHBOARD_TEXTBOX = "id=report-abuse-txtbox";
    public static final String REPORTSURVEY_DASHBOARD_SUBMIT = "css=div.rpa-btn.rpa-report-btn";
    public static final String REPORTSURVEY_DASHBOARD_CANCEL = "css=div.rpa-btn.rpa-cancel-btn";
    public static final String DOWNLOAD_REPORT_LOCATOR = "id=dsh-dwnld-report-btn";
    public static final String START_DATE_REPORT_LOCATOR = "id=dsh-start-date";
    public static final String END_DATE_REPORT_LOCATOR = "id=dsh-end-date";
    public static final String REPORT_SELECT = "id=download-survey-reports";

    //Success messages
    public static final String SEND_SURVEY_SUCCESSFUL = "Survey request sent successfully!";
    public static final String RESEND_SURVEY_REQUEST_SUCCESSFUL = "Reminder Mail sent successfully to";
    public static final String RETAKE_SURVEY_REQUEST_SUCCESSFUL = "Mail sent to @CustomerFirstName to retake the survey for you.";
    public static final String REPORT_SURVEY_SUCCESSFUL = "Reported Successfully!";


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
        navigateToPage( HELP_LOCATOR );
    }


    public HelpPage clickUpdatePasswordPage()
    {
        clickUserIcon();
        navigateToUpdatePasswordPage();
        waitForAjax();
        return new HelpPage( driver );
    }


    public void navigateToUpdatePasswordPage()
    {
        navigateToPage( UPDATE_PASSWORD_LOCATOR );
    }


    public HomePage clickLogoutButton()
    {
        clickUserIcon();
        WebElement logoutBtn = getElement( LOGOUT_BTN_LOCATOR );
        logoutBtn.click();
        waitForAjax();
        return new HomePage( driver );
    }


    public ComplaintResolutionPage clickComplaintResolutionPage()
    {
        Actions actions = new Actions( driver );
        WebElement settingsIcn = getElement( CONFIGURE_SETTINGS_LOCATOR );
        actions.moveToElement( settingsIcn );

        WebElement compaintResolutionBtn = getElement( COMPLAINT_RESOLUTION_LOCATOR );
        actions.moveToElement( compaintResolutionBtn );
        actions.click().build().perform();

        waitForAjax();
        return new ComplaintResolutionPage( driver );
    }


    public EmailSettingsPage clickEmailSettingsPage()
    {
        Actions actions = new Actions( driver );
        WebElement settingsIcn = getElement( CONFIGURE_SETTINGS_LOCATOR );
        actions.moveToElement( settingsIcn );

        WebElement emailSettingsBtn = getElement( EMAIL_SETTINGS_LOCATOR );
        actions.moveToElement( emailSettingsBtn );
        actions.click().build().perform();

        waitForAjax();
        return new EmailSettingsPage( driver );
    }


    public AppsPage clickAppSettingsPage()
    {
        Actions actions = new Actions( driver );
        WebElement settingsIcn = getElement( CONFIGURE_SETTINGS_LOCATOR );
        actions.moveToElement( settingsIcn );

        WebElement appSettingsBtn = getElement( APP_SETTINGS_LOCATOR );
        actions.moveToElement( appSettingsBtn );
        actions.click().build().perform();

        waitForAjax();
        return new AppsPage( driver );
    }


    public void clickUserIcon()
    {
        WebElement userPicIcn = getElement( USER_HEADER_IMAGE );
        userPicIcn.click();
    }


    /**
     * Method to fill survey details on the survey popup
     * @param rowNumber
     * @param customerFirstName
     * @param customerLastName
     * @param customerEmailId
     * @param agentName - null if agent sending survey for him/her self
     */
    public void fillSurveyDetails( Integer rowNumber, String customerFirstName, String customerLastName,
        String customerEmailId, String agentName )
    {
        String rowStr = Integer.toString( rowNumber + 1 );

        //Check if agent name is passed, if passed select the first agent matching with the agent name
        if ( agentName != null ) {
            WebElement agentNameInput = getElement( SURVEY_POPUP_AGENT_LOCATOR.replace( "@RowNumber", rowStr ) );
            agentNameInput.clear();
            agentNameInput.sendKeys( agentName );

            //wait to load user list
            waitForAjax();

            //select the first element
            agentNameInput.sendKeys( Keys.ARROW_DOWN );
            agentNameInput.sendKeys( Keys.TAB );
        }

        WebElement custFirstNameInput = getElement( SURVEY_POPUP_CUSTOMER_FIRST_NAME_LOCATOR.replace( "@RowNumber", rowStr ) );
        custFirstNameInput.clear();
        custFirstNameInput.sendKeys( customerFirstName );

        WebElement custLastNameInput = getElement( SURVEY_POPUP_CUSTOMER_LAST_NAME_LOCATOR.replace( "@RowNumber", rowStr ) );
        custLastNameInput.clear();
        custLastNameInput.sendKeys( customerLastName );

        WebElement custEmailIdInput = getElement( SURVEY_POPUP_CUSTOMER_EMAIL_LOCATOR.replace( "@RowNumber", rowStr ) );
        custEmailIdInput.clear();
        custEmailIdInput.sendKeys( customerEmailId );
    }


    public void clickSendSurveyButton() throws Exception
    {
        WebElement sendSurveyBtn = getElement( SEND_SURVEY_BTN );
        sendSurveyBtn.click();

        //wait for the request to complete
        waitForAjax();

        if ( !getToastMessage().equalsIgnoreCase( SEND_SURVEY_SUCCESSFUL ) ) {
            throw new Exception( "Survey request fail. Reason : " + getToastMessage() );
        }
    }


    /**
     * Method to resend survey request from dashboard
     * 
     * @param rowNumber
     * @throws Exception 
     */
    public void clickResendSurveyFromDashbaord( int rowNumber ) throws Exception
    {
        WebElement resendBtn = getElement( RESEND_SURVEY_DASHBOARD.replace( "@RowNumber", Integer.toString( rowNumber ) ) );
        resendBtn.click();

        //wait for response
        waitForAjax();

        String customerName = resendBtn.getAttribute( "data-custname" );

        if ( !getToastMessage().equalsIgnoreCase( RESEND_SURVEY_REQUEST_SUCCESSFUL + " " + customerName ) ) {
            throw new Exception( "Resend survey request failed. Reason : " + getToastMessage() );
        }
    }


    public void clickRetakeSurveyFromDashbaord( int rowNumber ) throws Exception
    {
        WebElement retakeBtn = getElement( RETAKESURVEY_DASHBOARD.replace( "@RowNumber", Integer.toString( rowNumber ) ) );

        String customerFirstName = retakeBtn.findElement( By.xpath( ".." ) ).findElement( By.xpath( ".." ) )
            .findElement( By.xpath( ".." ) ).findElement( By.xpath( ".." ) ).getAttribute( "data-firstname" );

        retakeBtn.click();
        clickOverlayContinue();

        //wait for response
        waitForAjax();

        if ( !getToastMessage().equalsIgnoreCase(
            RETAKE_SURVEY_REQUEST_SUCCESSFUL.replace( "@CustomerFirstName", customerFirstName ) ) ) {
            throw new Exception( "Retake survey request failed. Reason : " + getToastMessage() );
        }
    }


    public void clickReportSurveyFromDashbaord( int rowNumber, String reportAbuseText ) throws Exception
    {
        WebElement retakeBtn = getElement( REPORTSURVEY_DASHBOARD.replace( "@RowNumber", Integer.toString( rowNumber ) ) );

        retakeBtn.click();

        WebElement reportAbuseTextBox = getElement( REPORTSURVEY_DASHBOARD_TEXTBOX );
        reportAbuseTextBox.clear();
        reportAbuseTextBox.sendKeys( reportAbuseText );

        WebElement reportAbuseSubmit = getElement( REPORTSURVEY_DASHBOARD_SUBMIT );
        reportAbuseSubmit.click();

        //wait for response
        waitForAjax();

        if ( !getToastMessage().equalsIgnoreCase( REPORT_SURVEY_SUCCESSFUL ) ) {
            throw new Exception( "Report survey request failed. Reason : " + getToastMessage() );
        }
    }


    public void clickDownloadReportButton( String startDateStr, String endDateStr )
    {

        WebElement startDate = getElement( START_DATE_REPORT_LOCATOR );
        startDate.clear();
        startDate.sendKeys( startDateStr );

        WebElement endDate = getElement( END_DATE_REPORT_LOCATOR );
        endDate.clear();
        endDate.sendKeys( startDateStr );

        WebElement downloadBtn = getElement( DOWNLOAD_REPORT_LOCATOR );
        downloadBtn.click();
    }


    public void downloadUserRankingReport( String startDateStr, String endDateStr )
    {
        Select select = new Select( getElement( REPORT_SELECT ) );
        select.selectByVisibleText( "User Ranking Report" );

        clickDownloadReportButton( startDateStr, endDateStr );
    }


    public void downloadSurveyResultsReport( String startDateStr, String endDateStr )
    {
        Select select = new Select( getElement( REPORT_SELECT ) );
        select.selectByVisibleText( "Survey Results Report" );

        clickDownloadReportButton( startDateStr, endDateStr );
    }


    public void downloadSocialMonitorReport( String startDateStr, String endDateStr )
    {
        Select select = new Select( getElement( REPORT_SELECT ) );
        select.selectByVisibleText( "Social Monitor Report" );

        clickDownloadReportButton( startDateStr, endDateStr );
    }


    public void downloadIncompleteSurveyReport( String startDateStr, String endDateStr )
    {
        Select select = new Select( getElement( REPORT_SELECT ) );
        select.selectByVisibleText( "Incomplete Survey Data" );

        clickDownloadReportButton( startDateStr, endDateStr );
    }
}
