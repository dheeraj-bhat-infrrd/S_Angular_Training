package com.realtech.socialsurvey.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.models.Phone;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class PhoneValidator implements Validator
{

    public boolean supports( Class<?> clazz )
    {
        return Phone.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        // TODO Auto-generated method stub

    }

}
