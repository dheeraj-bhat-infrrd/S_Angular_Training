package com.realtech.socialsurvey.api.validators;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.PaymentRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


/**
 * Validates the PaymentRequest
 */
@Component
public class PaymentRequestValidator implements Validator
{
    public boolean supports( Class<?> classes )
    {
        return PaymentRequest.class.isAssignableFrom( classes );
    }


    public void validate( Object target, Errors errors )
    {
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "nonce", ErrorCodes.NONCE_INVALID,
            "nonce cannot be empty" );

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
