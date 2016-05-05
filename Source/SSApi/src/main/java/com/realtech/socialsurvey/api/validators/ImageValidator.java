package com.realtech.socialsurvey.api.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public class ImageValidator implements SmartValidator
{
    public boolean supports( Class<?> clazz )
    {
        return String.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        // TODO Auto-generated method stub
    }


    public void validate( Object target, Errors errors, Object... validationHints )
    {
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, validationHints[0].toString(), validationHints[1].toString(),
            validationHints[0].toString() + " cannot be empty" );
    }
}
