package com.realtech.socialsurvey.api.controllers;

import com.realtech.socialsurvey.api.models.request.CaptchaRequest;
import com.realtech.socialsurvey.api.validators.CaptchaValidator;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;


/**
 * Util controller for general utility apis
 */

@RestController
@RequestMapping ( "/v1")
public class UtilitiesController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( UtilitiesController.class );

    @Resource
    @Qualifier ("nocaptcha")
    private CaptchaValidation captchaValidation;

    @Value ("${CAPTCHA_SECRET}")
    private String captchaSecretKey;

    private CaptchaValidator captchaValidator;

    @Autowired
    public UtilitiesController(CaptchaValidator captchaValidator){
        this.captchaValidator = captchaValidator;
    }

    @InitBinder
    public void capthcaValidationBinder( WebDataBinder binder )
    {
        binder.setValidator( captchaValidator );
    }

    @RequestMapping ( value = "/nocaptcha", method = RequestMethod.POST)
    @ApiOperation ( value = "Validates the google no-captcha")
    public ResponseEntity<?> validateNoCaptcha(@Valid @RequestBody CaptchaRequest captchaRequest) throws InvalidInputException{
        LOGGER.info( "Validating captcha" );
        if (!captchaValidation.isCaptchaValid(captchaRequest.getRemoteAddress(), captchaSecretKey, captchaRequest.getCaptchaResponse())) {
            LOGGER.error("Captcha Validation failed!");
            throw new InvalidInputException("Captcha Validation failed!", DisplayMessageConstants.INVALID_CAPTCHA);
        }
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


}
