package com.realtech.socialsurvey.version1.page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.realtech.socialsurvey.constants.GlobalConstants;
import com.realtech.socialsurvey.utils.Utils;


public class BasePage
{
    private static final Logger LOG = LoggerFactory.getLogger( BasePage.class );
    protected WebDriver driver;

    public static final String ERROR_LOCATOR = "id=err-nw-txt";
    public static final String TOAST_MESSAGE_LOCATOR = "id=overlay-toast";
    public static final String OVELAY_CONTINUE_LOCATOR = "id=overlay-continue";
    public static final String OVELAY_CANCEL_LOCATOR = "id=overlay-cancel";


    public BasePage( WebDriver driver )
    {
        this.driver = driver;
        waitForAjax();
    }


    public String getTitle()
    {
        return driver.getTitle();
    }


    /**
     * Given a string locator (in selenium 1 format) returns the element.
     * 
     * @param locator
     * @return
     */
    public WebElement getElement( String locator )
    {
        LOG.trace( "++++ Getting element: " + locator + " at " + Utils.getHumanReadableTimeStamp() );

        WebElement element = driver.findElement( getLocator( locator ) );

        LOG.trace( "++++ Got element: " + locator + " at " + Utils.getHumanReadableTimeStamp() );

        return element;
    }


    /**
     * Given a string locator (in selenium 1 format) returns a list of elements.
     * 
     * @param locator
     * @return
     */
    public List<WebElement> getElements( String locator )
    {
        LOG.trace( "++++ Getting elements: " + locator + " at " + Utils.getHumanReadableTimeStamp() );

        List<WebElement> elements = driver.findElements( getLocator( locator ) );

        LOG.trace( "++++ Got elements: " + locator + " at " + Utils.getHumanReadableTimeStamp() );

        return elements;
    }


    /**
     * Returns the By object for the selenium 1 format locator given
     */
    private By getLocator( String locator )
    {
        By by = null;
        String newLocator = locator.substring( locator.indexOf( "=" ) + 1 );

        if ( locator.startsWith( "id" ) ) {
            by = By.id( newLocator );
        } else if ( locator.startsWith( "name" ) ) {
            by = By.name( newLocator );
        } else if ( locator.startsWith( "css" ) ) {
            by = By.cssSelector( newLocator );
        } else if ( locator.startsWith( "link" ) ) {
            by = By.linkText( newLocator );
        } else if ( locator.startsWith( "xpath" ) ) {
            by = By.xpath( newLocator );
        } else if ( locator.startsWith( "partiallink" ) ) {
            by = By.partialLinkText( newLocator );
        } else if ( locator.startsWith( "tagname" ) ) {
            by = By.tagName( newLocator );
        } else {
            throw new IllegalArgumentException( "Locator not recognised: " + locator );
        }
        return by;
    }


    /**
     * Is the element present in the DOM
     * 
     * @param locator
     * @return
     */
    public boolean isElementPresent( String locator )
    {
        try {
            //set the implicit wait to 0 seconds, so that it immediately lets you know if the
            //element is present in the DOM
            driver.manage().timeouts().implicitlyWait( 0, TimeUnit.SECONDS );

            getElement( locator );

            return true;
        } catch ( Exception e ) {
            return false;
        } finally {
            // reset the implicit wait to its default value
            driver
                .manage()
                .timeouts()
                .implicitlyWait( Integer.parseInt( GlobalConstants.get( GlobalConstants.DRIVER_IMPLICIT_WAIT_TIME_DEFAULT ) ),
                    TimeUnit.SECONDS );
        }
    }


    /**
     * Checking the WebElement visibility.
     * 
     * @param element
     *            : WebElement for checking Visibility
     * @return
     */
    public boolean isVisible( WebElement element )
    {
        Dimension size = element.getSize();

        return ( ( size.height > 0 ) && ( size.width > 0 ) );
    }


    /**
     * Check if the element is clickable
     * 
     * @param locator
     * @return
     */
    public boolean isElementClickable( String locator )
    {
        if ( isElementPresent( locator ) ) {
            //Wait for any animation to complete
            WebElement element = waitForElementToAppear( locator, 1 );
            return element.isDisplayed();
        }
        return false;
    }


    /**
     * Wait till 20 seconds or till the element becomes visible, whichever is earlier.
     * 
     * @param locator
     * @return
     */
    public WebElement waitForElementToAppear( String locator )
    {
        return waitForElementToAppear( locator, 20 );
    }


    /**
     * Wait till 'timeout' seconds or till the element becomes visible, whichever is earlier.
     * 
     * @param locator
     * @param timeoutInSeconds
     * @return
     */
    public WebElement waitForElementToAppear( String locator, int timeoutInSeconds )
    {
        LOG.trace( "++++ Waiting for locator: " + locator + " at " + Utils.getHumanReadableTimeStamp() );

        // First remove the implicit wait present in the driver,
        // so that it doesn't clash with the explicit wait we're going to use
        driver.manage().timeouts().implicitlyWait( 0, TimeUnit.SECONDS );

        WebDriverWait wait = new WebDriverWait( driver, timeoutInSeconds );
        WebElement element = wait.until( ExpectedConditions.elementToBeClickable( getLocator( locator ) ) );

        // reset the implicit wait to its default value
        driver
            .manage()
            .timeouts()
            .implicitlyWait( Integer.parseInt( GlobalConstants.get( GlobalConstants.DRIVER_IMPLICIT_WAIT_TIME_DEFAULT ) ),
                TimeUnit.SECONDS );

        // Wait for the element to stabilise (if it is being animated)
        if ( isVisible( element ) ) {
            LOG.trace( "++++ Element [" + locator + "] visible now" );
            if ( hasLocationStabilised( element, 5000 ) ) {
                LOG.trace( "++++ Element [" + locator + "] stabilised at " + Utils.getHumanReadableTimeStamp() + "!" );
                return element;
            }
        }

        // Throw an exception if we still can't find it
        throw new RuntimeException( "Can't find element using locator: " + locator );
    }


    /**
     * Wait for elements for the default timeout.
     * 
     * @param locator
     * @return
     */
    public List<WebElement> waitForElementsToAppear( final String locator )
    {
        return waitForElementsToAppear( locator, 20 );
    }


    /**
     * Wait till timeout for elements to appear.
     * 
     * @param locator
     * @param timeOutInSeconds
     * @return
     */
    public List<WebElement> waitForElementsToAppear( final String locator, int timeOutInSeconds )
    {
        List<WebElement> elements;
        try {
            driver.manage().timeouts().implicitlyWait( 0, TimeUnit.SECONDS );
            WebDriverWait wait = new WebDriverWait( driver, timeOutInSeconds );
            wait.until( ( new ExpectedCondition<Boolean>() {
                //@Override
                public Boolean apply( WebDriver driverObject )
                {
                    try {
                        driverObject.findElements( getLocator( locator ) );
                        return true;
                    } catch ( NoSuchElementException e ) {
                        return false;
                    }
                }
            } ) );

            elements = driver.findElements( getLocator( locator ) );
            driver.manage().timeouts().implicitlyWait( 20, TimeUnit.SECONDS );
            return elements; // return the element
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Wait till element's location stabilises or timeout occurs.
     * 
     * @param element
     * @param timeout
     * @return
     */
    public boolean hasLocationStabilised( WebElement element, int timeout )
    {
        int currenttime = 0;
        Point pointBefore = element.getLocation();

        while ( currenttime <= timeout ) {
            sleep( 50 );
            currenttime += 50;

            Point pointAfter = element.getLocation();

            if ( ( pointBefore.getX() == pointAfter.getX() ) && ( pointBefore.getY() == pointAfter.getY() ) ) {
                return true;
            } else {
                LOG.trace( "Moved " + pointBefore + " - " + pointAfter + "..." );
                pointBefore = pointAfter;
            }
        }

        return false;
    }


    /**
     * waitForAjax with default time settings.
     */
    public void waitForAjax()
    {
        waitForAjax( 15000, 250 );

    }


    /**
     * waitForAjax with specified timeout and time interval between 2 ajax checks.
     * 
     * @param timeout
     * @param timeIntervalBetweenChecks
     */
    public void waitForAjax( int timeout, int timeIntervalBetweenChecks )
    {
        LOG.debug( "++++ Waiting for Ajax: " + Utils.getHumanReadableTimeStamp() );

        boolean isAjaxCompleted = false;
        int currenttime = 0;
        boolean flagIsConsecutive = false;
        long startTime = System.currentTimeMillis();
        while ( currenttime <= timeout ) {
            isAjaxCompleted = (Boolean) ( (JavascriptExecutor) driver )
                .executeScript( "return window.jQuery != undefined && jQuery.active == 0" );

            Object numberOfActiveRequests = ( (JavascriptExecutor) driver ).executeScript( "return jQuery.active" );

            LOG.trace( "++++ AjaxCompleted: " + isAjaxCompleted + "... activeRequests: " + numberOfActiveRequests
                + "... currentTime: " + currenttime + "... flagIsConsecutive: " + flagIsConsecutive );

            if ( isAjaxCompleted ) {
                if ( flagIsConsecutive ) {
                    break;
                } else {
                    // first pass. set the flag that it has passed once.
                    flagIsConsecutive = true;
                }
            } else {
                flagIsConsecutive = false;
            }

            sleep( timeIntervalBetweenChecks );
            currenttime += timeIntervalBetweenChecks;
        }

        if ( !isAjaxCompleted ) {
            ( (JavascriptExecutor) driver ).executeScript( "jQuery.active=0" );
        }

        LOG.debug( "++++ Time Taken for wait for Ajax: " + ( System.currentTimeMillis() - startTime ) );
    }


    /**
     * Wait for a specified amount of time.
     * 
     * @param time: Time in milliseconds.
     */
    public void sleep( int time )
    {
        try {
            Thread.sleep( time );
        } catch ( InterruptedException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * Gets all the names currently being used for the given locator. If the attributeName
     * is given, it will look for the name in the attribute. Else, it will look for the name in
     * the inner text of the element. 
     * 
     * @param elementLocator
     * @param attributeName
     * @return
     */
    public String[] getAllUsedNames( String elementLocator, String attributeName )
    {
        List<WebElement> elements = waitForElementsToAppear( elementLocator );

        ArrayList<String> names = new ArrayList<String>();

        for ( int i = 0; i < elements.size(); i++ ) {
            WebElement tempElement = elements.get( i );
            String tempElementName = null;

            // if an attributeName is given, get the name using the attribute. 
            // Or else, just get the inner text
            if ( ( attributeName != null ) && ( !attributeName.equals( "" ) ) ) {
                tempElementName = tempElement.getAttribute( attributeName );
            } else {
                tempElementName = tempElement.getText();
            }

            if ( tempElementName != null ) {
                names.add( tempElementName );
            }
        }

        return names.toArray( new String[names.size()] );
    }


    /**
     * Search the locator for existing names and return a 'safe' version of the name to use. E.g. if
     * you pass it a name 'UI-Automation-Tab', it will search all the elements using the locator and
     * will return:
     * a. 'UI-Automation-Tab' if there exists no element with that name or
     * b. the next name in the series that's available.
     * E.g., 'UI-Automation-Tab8' if 'UI-Automation-Tab7' is the highest name in the series that exists.
     * 
     * @param elementLocator
     * @param attributeName
     * @param name
     * @return
     */
    public String getSafeNameToUse( String elementLocator, String attributeName, String name )
    {
        String[] usedNames = getAllUsedNames( elementLocator, attributeName );

        int highestInSeries = -1;

        // Prepare pattern to match name of the elements
        Pattern namePattern = Pattern.compile( name + "([\\d]*)" );

        for ( String tempElementName : usedNames ) {
            // continue only if element name isn't null
            if ( tempElementName != null ) {
                Matcher matcher = namePattern.matcher( tempElementName );
                if ( matcher.find() ) {
                    String matchedNumberString = matcher.group( 1 );

                    // the name was found but without any number, so internally set it to 0
                    if ( ( matchedNumberString == null ) || ( matchedNumberString.equals( "" ) ) ) {
                        matchedNumberString = "0";
                    }

                    int tempNumber = Integer.parseInt( matchedNumberString );

                    // if the tempNumber is higher than the current highestInSeries, replace it.
                    // Or else let it be.
                    highestInSeries = ( tempNumber > highestInSeries ) ? tempNumber : highestInSeries;
                }
            }
        }

        // if the highestInSeries hasn't changed from -1, that means the name wasn't found,
        // so just return it
        if ( highestInSeries == -1 ) {
            return name;
        }

        // the name was found, return the next number in the series
        return name + ( highestInSeries + 1 );
    }


    /**
     * Method is for selecting last option from dropdown options.
     * 
     * @param locator
     *            : for locating drop down in page
     */
    public void selectLastOptionOfDropDown( String locator )
    {
        Select droplist = new Select( waitForElementToAppear( locator ) );
        droplist.selectByIndex( droplist.getOptions().size() - 1 );
    }


    /**
     * Asserting to check if message is found in the notification messages
     * 
     * @param locator
     *            : Locator to get the div element for notify message.
     * @param message
     *            : Expected message to appear in notify message.
     */

    public void assertNotifyMessage( String locator, String message )
    {

        // waiting for ajax to check the active ajax.
        waitForAjax( 10000, 0 );

        List<WebElement> notifyMessages = getElements( locator );
        if ( !notifyMessages.isEmpty() ) {
            boolean foundMessage = false;
            for ( WebElement notifyMessage : notifyMessages ) {
                if ( notifyMessage.getText().contains( message ) ) {
                    foundMessage = true;
                }
            }

            if ( !foundMessage ) {
                Assert.fail( "Could not find expected message : " + message );
            }
        } else {
            Assert.fail( "Could not find message : " + message );
        }
    }


    public String getErrorMessage()
    {
        WebElement message = getElement( ERROR_LOCATOR );
        return message.getText();
    }


    public String getToastMessage()
    {
        WebElement toastMessage = getElement( TOAST_MESSAGE_LOCATOR );
        return toastMessage.getText();
    }


    /**
     * 
     * 
     * @param locators
     * @return
     */
    public boolean navigateToPage( String... locators )
    {

        //now process the locators      
        for ( int index = 0; index < locators.length; index++ ) {
            //Get current element locator
            String locator = locators[index];

            //check if current element is clickable
            if ( !isElementClickable( locator ) ) {
                return false;
            }

            //check if there's a child element
            if ( index < ( locators.length - 1 ) ) {
                //if there is, check if it is visible
                WebElement childElement = getElement( locators[index + 1] );

                //if it is, then don't click on the current element, just continue with the for loop
                if ( isVisible( childElement ) ) {
                    continue;
                }
            }

            //if there are no more child elements, or if the child isn't visible, then click on the current element
            waitForElementToAppear( locator ).click();
        }

        waitForAjax();

        return true;
    }


    public void clickOverlayContinue()
    {
        WebElement overlayContinueBtn = getElement( OVELAY_CONTINUE_LOCATOR );
        overlayContinueBtn.click();
    }


    public void clickOverlayCancel()
    {
        WebElement overlayCancelBtn = getElement( OVELAY_CANCEL_LOCATOR );
        overlayCancelBtn.click();
    }

}
