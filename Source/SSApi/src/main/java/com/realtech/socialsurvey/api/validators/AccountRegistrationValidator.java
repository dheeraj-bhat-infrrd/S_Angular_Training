package com.realtech.socialsurvey.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class AccountRegistrationValidator implements Validator
{
    @Autowired
    private EmailValidator emailValidator;

    @Autowired
    private PhoneValidator phoneValidator;


    public boolean supports( Class<?> classes )
    {
        return AccountRegistrationRequest.class.isAssignableFrom( classes );
    }


    public void validate( Object target, Errors errors )
    {
        AccountRegistrationRequest request = (AccountRegistrationRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "firstName", ErrorCodes.FIRSTNAME_INVALID,
            "firstName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "lastName", ErrorCodes.LASTNAME_INVALID,
            "lastName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "companyName", ErrorCodes.COMPANYNAME_INVALID,
            "companyName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", ErrorCodes.EMAIL_INVALID,
            "email cannot be empty" );
        ValidationUtils.invokeValidator( emailValidator, request.getEmail(), errors );
        ValidationUtils.invokeValidator( phoneValidator, request.getPhone(), errors );

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
