package com.realtech.socialsurvey.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.realtech.socialsurvey.constants.GlobalConstants;

public class DriverFactory {
	
	enum OS{
		WINDOWS, UNIX, UNKNOWN;
	}
	
	public static String FIREFOX_BROWSER = "FIREFOX";
	public static String CHROME_BROWSER = "CHROME";
	public static String IE_BROWSER = "IE";
	public static String HTMLUNIT_BROWSER = "HTMLUNIT";
	
	/**
	 * Return the WebDriver corresponding to the specified browser name
	 * 
	 * @param browserName
	 * @return
	 */
	public static WebDriver getDriver(String browserName){
		WebDriver driver = null;
		
		if((browserName != null) && (browserName.equalsIgnoreCase(IE_BROWSER))){
			//Load Internet Explorer
			System.setProperty("webdriver.ie.driver", GlobalConstants.get(GlobalConstants.IE_DRIVER_PATH));
			driver = new InternetExplorerDriver();
		}
		else if((browserName != null) && (browserName.equalsIgnoreCase(FIREFOX_BROWSER))){
			//Load Firefox along with the Javascript warning/error capture extension 
			FirefoxProfile profile = new FirefoxProfile();
			profile.setPreference("browser.download.folderList", 2); 
			profile.setPreference("browser.download.dir","/home/manish/Downloads"); 
			profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
				"application/x-bzpdf,application/x-gzpdf,text/pdf,text/x-pdf,application/pdf,application/x-pdf,application/acrobat,applications/vnd.pdf,text/calendar,application/csv,text/csv,image/svg+xml,image/png,application/zip"); 
																									
			// profile.setPreference("browser.download.manager.showWhenStarting",
			// "false");
			profile.setPreference("browser.download.useDownloadDir", "false");

			profile.setEnableNativeEvents(true);
			/*try {
				JavaScriptError.addExtension(profile);
			}
			catch (IOException e) {
				e.printStackTrace();
				System.err.println("Could not load Javascript warning/error capture in firefox profile");
			}*/
			driver = new FirefoxDriver(profile);
		}
		else if((browserName != null) && (browserName.equalsIgnoreCase(CHROME_BROWSER))){
			//Load Chrome
			System.setProperty("webdriver.chrome.driver",
					GlobalConstants.get(GlobalConstants.CHROME_DRIVER_PATH));
			ChromeOptions options = new ChromeOptions();
			//options.setExperimentalOptions("args", "--start-maximized");
			options.addArguments("extensions_to_open", "pdf");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);

			driver = new ChromeDriver(capabilities);
		}
		/*else if((browserName != null) && (browserName.equalsIgnoreCase(HTMLUNIT_BROWSER))){
			//Load Headless browser HTML UNIT
			driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_17);
			((HtmlUnitDriver)driver).setJavascriptEnabled(true);
		}*/
		else {
			//The browser isn't recognised
			throw new IllegalArgumentException("Could not recognise browser");
		}
				
		return driver;
	}
	
	/**
	 * Returns a WebDriver based on the OS
	 * 
	 * @return
	 */
	public static WebDriver getDriver(){
		String osName = System.getProperty("os.name").toLowerCase();
		OS os = getOSName(osName);
		WebDriver driver = null;
		
		switch(os){
			//TODO: Get the default browser of each OS from properties file
			case UNIX:
				driver = getDriver(GlobalConstants.get(GlobalConstants.UNIX_DEFAULT_BROWSER));
				break;
			
			case WINDOWS:
				driver = getDriver(GlobalConstants.get(GlobalConstants.WINDOWS_DEFAULT_BROWSER));
				break;
			
			default:
				driver = getDriver(GlobalConstants.get(GlobalConstants.DEFAULT_BROWSER));
		}
		return driver;
	}
	
	private static OS getOSName(String osName){
		
		if(osName.indexOf("win") >= 0)
			return OS.WINDOWS;
		else if(osName.indexOf("nix") >= 0 || osName.indexOf("nux") >= 0 || osName.indexOf("aix") > 0 )
			return OS.UNIX;
		
		return OS.UNKNOWN;
	}
	
}
