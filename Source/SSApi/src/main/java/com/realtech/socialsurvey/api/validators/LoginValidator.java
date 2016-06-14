package com.realtech.socialsurvey.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.LoginRequest;


@Component
public class LoginValidator implements Validator
{
    @Autowired
    private EmailValidator emailValidator;


    public boolean supports( Class<?> clazz )
    {
        return LoginRequest.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        LoginRequest loginRequest = (LoginRequest) target;

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "email", ErrorCodes.EMAIL_INVALID, "email cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "password", ErrorCodes.PASSWORD_INVALID,
            "password cannot be empty" );
        ValidationUtils.invokeValidator( emailValidator, loginRequest.getEmail(), errors );

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}