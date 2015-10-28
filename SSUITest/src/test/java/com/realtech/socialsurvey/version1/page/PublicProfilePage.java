package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.CommonConstants;
import com.realtech.socialsurvey.utils.DriverFactory;

public class PublicProfilePage
{

    WebDriver driver = DriverFactory.getDriver(DriverFactory.FIREFOX_BROWSER);
    
    private static final Logger LOG = LoggerFactory.getLogger( PublicProfilePage.class );
    
    @Test
    public void loadPublicPage() {
        driver.get( CommonConstants.publicProfileUrl );
        
        LOG.info( "Opened public profile page" );
        
        //maximize the window
        driver.manage().window().maximize();
    }
    
}
