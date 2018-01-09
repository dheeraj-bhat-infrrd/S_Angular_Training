package com.realtech.socialsurvey.compute.utils;

/**
 * Operations on the Throwable class
 * @author nishit
 *
 */
public class ThrowableUtils
{
    
    // private constructor to avoid instantiation
    private ThrowableUtils() {}
    
    private static final int STACK_DEPTH = 5;
    
    /**
     * Gets a Stringified version of stack track with controlled depth
     * @param thrw
     * @return
     */
    public static String controlledStacktrace(Throwable thrw) {
        StringBuilder stackBuilder = new StringBuilder( thrw.toString() );
        if(thrw.getStackTrace().length > 0) {
            for(int depth = 0; depth < (thrw.getStackTrace().length <= STACK_DEPTH ? thrw.getStackTrace().length: STACK_DEPTH); depth++) {
                stackBuilder.append( "\nat " ).append( thrw.getStackTrace()[depth] );
            }
        }
        return stackBuilder.toString();
    }
}
