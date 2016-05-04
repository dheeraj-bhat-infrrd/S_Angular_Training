package com.realtech.socialsurvey.api.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Aspect for handling authorization header
 */

@Aspect
public class AuthorizationHeaderAspect
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationHeaderAspect.class);

    @Pointcut("@annotation(com.realtech.socialsurvey.api.annotations.ValidateAuthHeader)")
    private void validateAuthHeader(){}

    @Pointcut("execution(* *(..))")
    public void allPoints(){}


    @Before( "validateAuthHeader()" )
    public void validateAuthorizationHeader(){
        System.out.println( "Validating header" );
    }

    @Before( "allPoints()" )
    public void allPointsAdvice(){
        System.out.println( "Hmm...." );
    }

}
