package com.realtech.socialsurvey.version1.page;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.realtech.socialsurvey.constants.CommonConstants;
import com.realtech.socialsurvey.utils.DriverFactory;


public class ProSearchPage
{
    WebDriver driver = DriverFactory.getDriver( DriverFactory.FIREFOX_BROWSER );

    private static final Logger LOG = LoggerFactory.getLogger( ProSearchPage.class );


    @Test
    public void launchHomeWebsite()
    {
        driver.get( CommonConstants.appUrl );

        //maximize the window
        driver.manage().window().maximize();
    }


    @Test
    public void searchForProWithEnterKey()
    {
        LOG.info( "Test for pro search with enter key" );

        //search for pro
        WebElement proFirstName = driver.findElement( By.id( "find-pro-first-name" ) );
        proFirstName.clear();
        proFirstName.sendKeys( "Jack" );

        WebElement proLastName = driver.findElement( By.id( "find-pro-last-name" ) );
        proLastName.clear();

        //test enter keypress
        proFirstName.sendKeys( Keys.ENTER );
    }


    @Test
    public void searchForProWithButtonSubmit()
    {
        LOG.info( "Test for pro search with submit button" );

        //search for pro
        WebElement proFirstName = driver.findElement( By.id( "find-pro-first-name" ) );
        proFirstName.clear();
        proFirstName.sendKeys( "Ni" );

        WebElement proLastName = driver.findElement( By.id( "find-pro-last-name" ) );
        proLastName.clear();
        proLastName.sendKeys( "k" );

        WebElement proSubmit = driver.findElement( By.id( "find-pro-submit" ) );
        proSubmit.click();
    }
}
