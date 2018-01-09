package com.realtech.socialsurvey.compute.utils;

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

}
