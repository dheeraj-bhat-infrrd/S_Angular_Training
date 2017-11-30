package com.realtech.socialsurvey.web.controller;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.UserSessionInvalidateException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.exception.SSAPIBadRequestException;
import com.realtech.socialsurvey.web.api.exception.SSAPIException;
import com.realtech.socialsurvey.web.common.JspResolver;


/**
 * Global error handler
 */
@ControllerAdvice
public class GlobalErrorController
{

    private static final Logger LOG = LoggerFactory.getLogger( GlobalErrorController.class );

    private EmailServices emailServices;

    private MessageUtils messageUtils;
    
    @Value("${WEB_EXCEPTION_REPORTING_ADDRESS}")
    private String webExceptionReportingEmailAddress;


    @Autowired
    public void setEmailServices( EmailServices emailServices )
    {
        this.emailServices = emailServices;
    }


    @Autowired
    public void setMessageUtils( MessageUtils messageUtils )
    {
        this.messageUtils = messageUtils;
    }


    @ExceptionHandler ( value = SSAPIException.class)
    @ResponseStatus ( value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleSSAPIException( SSAPIException ex )
    {
        LOG.warn( "Returning ss api error message" );
        return ex.getMessage();
    }


    @ExceptionHandler ( value = SSAPIBadRequestException.class)
    @ResponseStatus ( value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleSSAPIBadRequestException( SSAPIBadRequestException ex )
    {
        LOG.warn( "Returning ss api bad request error message" );
        return ex.getMessage();
    }


    /**
     * Returns 500 ISE in case of FatalException
     * 
     * @param fe
     */
    @ExceptionHandler ( value = FatalException.class)
    @ResponseStatus ( value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Fatal Exception")
    public void handleFatalException( FatalException fe )
    {
        LOG.error( "=====> FATAL ERROR: " + fe.getMessage(), fe );
    }


    /**
     * Returns 404 Not Found in case of NoRecordsFetchedException
     * 
     * @param e
     */
    /*@ExceptionHandler(ProfileNotFoundException.class)*/
    @ResponseStatus ( value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
    public void handleNotFound( ProfileNotFoundException e )
    {
        LOG.error( "=====> RESOURCE NOT FOUND: " + e.getMessage(), e );
    }


    /**
     * Returns 401 UnAuthorised in case of UserSessionInvalidateException
     * 
     * @param ex
     */
    @ExceptionHandler ( value = UserSessionInvalidateException.class)
    public String handleUserInvalidateSession( UserSessionInvalidateException ex, Model model )
    {
        LOG.error( "=====> UN-AUTHORISED ACCESS: " + ex.getMessage(), ex );
        model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_USER_CREDENTIALS,
            DisplayMessageType.ERROR_MESSAGE ) );
        return JspResolver.LOGIN;
    }


    /**
     * Handles any leaked exception to send mails
     */

    @ExceptionHandler ( value = Throwable.class)
    public void throwExceptions( Throwable thrw ) throws Throwable
    {
        LOG.info( "Sending failure mail to recpient : {}", webExceptionReportingEmailAddress );
        String stackTrace = ExceptionUtils.getFullStackTrace( thrw );
        try {
            //emailServices.sendWebExceptionEmail( webExceptionReportingEmailAddress, stackTrace );
            LOG.error("==============> Unhandled Exception <==============", thrw);
            LOG.debug( "Failure mail sent to admin." );
        } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
            LOG.error(
                "CustomItemProcessor : Exception caught when sending Fatal Exception mail. Message : {}", e1.getMessage() );
        }
        throw thrw;
    }

}
