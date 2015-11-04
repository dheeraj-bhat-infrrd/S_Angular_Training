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
    public static final String VIEW_ALL_INCOMOPLETED_SURVEY_LOCATOR="xpath=//*[@id='incomplete-survey-header']/div[contains(@class, 'dash-sur-link')]";
    public static final String VIEW_AS_DROPDOWN_LOCATOR="id=dashboard-sel";
    public static final String VIEW_AS_DROPDOWN_OPTION_LOCATOR="xpath=//*[contains(text(), '@viewasname') and contains(@class, 'da-dd-item')]";
    
    public DashboardPage( WebDriver driver )
    {
        super( driver );
        if ( !getTitle().equals( DASHBOARD_PAGE_TITLE ) ) {
            Assert.fail( "This is not dashboard page. Current page is: " + getTitle() );
        }

        waitForAjax();
    }
    
    public void clickSendSurveyPopupClose () {
        WebElement closeBtn = getElement( POPUP_CLOSE_LOCATOR );
        closeBtn.click();
    }
    
    public void clickSendSurveyButtonDashboard () {
        WebElement sendSurveyBtn = getElement( SENDSURVEY_BUTTON_LOCATOR );
        sendSurveyBtn.click();
    }
    
    public void clickConnectToLinkedIn() {
        WebElement linkedInConnectBtn = getElement( CONNECTTO_LINKEDIN_BUTTON_LOCATOR );
        linkedInConnectBtn.click();
    }
    
    public void clickConnectToFacebook() {
        WebElement facebookConnectBtn = getElement( CONNECTTO_LINKEDIN_BUTTON_LOCATOR );
        facebookConnectBtn.click();
    }
    
    public void clickViewAllIncompleteSurvey() {
        WebElement incompleteSurveyBtn = getElement( VIEW_ALL_INCOMOPLETED_SURVEY_LOCATOR );
        incompleteSurveyBtn.click();
    }
    public void clickConnectToGoogle(){
        WebElement googlePlusBtn = getElement( CONNECTTO_GOOGLE_BUTTON_LOCATOR );
        googlePlusBtn.click();
    }
    
    public void clickDashboardDropDown() {
        WebElement dashboardDropdownBtn = getElement( VIEW_AS_DROPDOWN_LOCATOR );
        dashboardDropdownBtn.click();
    }
    
    public void selectViewAsOption(String viewAsName) {
        WebElement viewAsOption = getElement( VIEW_AS_DROPDOWN_OPTION_LOCATOR.replace( "@viewasname", viewAsName ) );
        viewAsOption.click();
        waitForAjax();
    }

}
