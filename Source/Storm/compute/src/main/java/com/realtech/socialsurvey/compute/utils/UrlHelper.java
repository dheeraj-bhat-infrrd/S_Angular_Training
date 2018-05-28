package com.realtech.socialsurvey.compute.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author manish
 *
 */
public class UrlHelper
{
    private static final Logger LOG = LoggerFactory.getLogger( UrlHelper.class );
    
    private static final String URL_REGEX_FOR_FACEBOOK_PAGE_ID= "(?:https?:\\/\\/)?(?:www\\.)?facebook\\.com\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:groups\\/)?(?:[\\w\\-]*\\/)*?(\\/)?([^/?]*)";
    
    private static final String URL_REGEX_FOR_LINKEDIN_PAGE_ID= "(?:https?:\\/\\/)?(?:www\\.)?linkedin\\.com\\/(?:(?:\\w)*#!\\/)?(?:in\\/)?(?:company\\/)?(?:[\\w\\-]*\\/)*?(\\/)?([^/?]*)";

    private static final String URL_REGEX_FOR_TWITTER_PAGE_ID = "(?:https?:\\/\\/)?(?:www\\.)?twitter\\.com\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:groups\\/)?(?:[\\w\\-]*\\/)*?(\\/)?([^/?]*)";

    private static final String URL_REGEX_FOR_INSTAGRAM_PAGE_ID= "(?:https?:\\/\\/)?(?:www\\.)?instagram\\.com\\/(?:(?:\\w)*#!\\/)?(?:pages\\/)?(?:groups\\/)?(?:[\\w\\-]*\\/)*?(\\/)?([^/?]*)";

    // private constructor to avoid instantiation
    private UrlHelper()
    {}


    /**
     * 
     * @param urlString
     * @return
     */
    public static Map<String, String> getQueryParamsFromUrl( String urlString )
    {
        URL url = null;

        //Throw an exception if the url is malformed
        try {
            url = new URL( urlString );
        } catch ( MalformedURLException e ) {
            LOG.warn( "Malformed URL passed:" + urlString, e );
        }

        //Return a hashmap in all cases, even if no query params are found
        Map<String, String> map = new HashMap<>();

        if ( url != null ) {
            String[] params = url.getQuery().split( "&" );

            for ( int i = 0; i < params.length; i++ ) {
                String[] pair = params[i].split( "=" );

                //Store the param in the map only if it has a non empty key and value
                if ( ( pair.length == 2 ) && ( !pair[0].isEmpty() ) && ( !pair[1].isEmpty() ) ) {
                    map.put( pair[0], pair[1] );
                }
            }
        }

        return map;
    }
    
    /**Common Method to find pageid from social media URL
     * @param url
     * @param urlRegex
     * @return
     */
    public static String getPageIdFromURL(String url, String urlRegex){
        Matcher matcher = Pattern.compile( urlRegex ).matcher( url );
        if(matcher.find() && matcher.groupCount() >=2){
            return matcher.group(2);
        }
        return null;
    }
    
    /**
     * Get facebook page id from facebook page URL
     * @param url
     * @return
     */
    public static String getFacebookPageIdFromURL(String url){
        return getPageIdFromURL(url, URL_REGEX_FOR_FACEBOOK_PAGE_ID);
    }
    
    /**
     * Get twitter page id from twitter page URL
     * @param url
     * @return
     */
    public static String getTwitterPageIdFromURL(String url){
        return getPageIdFromURL(url, URL_REGEX_FOR_TWITTER_PAGE_ID);
    }
    
    /**
     * Get linkedin page id from linkedin page URL
     * @param url
     * @return
     */
    public static String getLinkedinPageIdFromURL(String url){
        return getPageIdFromURL(url, URL_REGEX_FOR_LINKEDIN_PAGE_ID);
    }


    /**
     * Get instagram page id from linkedin page URL
     * @param url
     * @return
     */
    public static String getInstagramPageIdFromURL(String url) {
        return getPageIdFromURL( url, URL_REGEX_FOR_INSTAGRAM_PAGE_ID );
    }
}