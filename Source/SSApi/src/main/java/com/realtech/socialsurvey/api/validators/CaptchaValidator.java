package com.realtech.socialsurvey.api.validators;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.CaptchaRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates the google no captcha input
 */
public class CaptchaValidator implements Validator
{
    public boolean supports( Class<?> clazz )
    {
        return CaptchaRequest.class.isAssignableFrom( clazz );
    }

    public void validate( Object target, Errors errors ){
        ValidationUtils.rejectIfEmpty( errors, "remoteAddress", ErrorCodes.REMOTE_ADDR_INVALID, "remote address cannot empty" );
        ValidationUtils.rejectIfEmpty( errors, "captchaResponse", ErrorCodes.CAPTCHA_RESPONSE_INVALID, "captcha response cannot empty" );

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
