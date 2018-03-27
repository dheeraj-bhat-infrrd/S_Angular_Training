package com.realtech.socialsurvey.compute.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for character array operations
 * @author nishit
 *
 */
public class ChararcterUtils
{

    // private constructor to avoid instantiation
    private ChararcterUtils()
    {}

    private static final String RETWEET_REGEX = "(^rt\\s*@(.*):\\s)(.*)";
    private static final Pattern PATTERN = Pattern.compile( RETWEET_REGEX, Pattern.CASE_INSENSITIVE );

    /**
     * Escapes SOLR query characters
     * @param s
     * @return
     */
    public static String escapeSOLRQueryChars( String s )
    {
        StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < s.length(); i++ ) {
            char c = s.charAt( i );
            // These characters are part of the query syntax and must be escaped
            if ( c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':' || c == '^' || c == '['
                || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~' || c == '*' || c == '?' || c == '|' || c == '&'
                || c == ';' || c == '/' || Character.isWhitespace( c ) ) {
                sb.append( '\\' );
            }
            sb.append( c );
        }
        return sb.toString();
    }


    public static String appendWithHypen( String word, String appendWord )
    {
        return word + "-" + appendWord;
    }


    /**
     * Escapes RT @<>: from the post
     * @param text
     * @return
     */
    public static String getTextIfRetweet( String text )
    {
        Matcher matcher = PATTERN.matcher( text );
        if ( matcher.matches() ) {
            text = matcher.group( 3 );
        }
        return text;
    }
    
}
