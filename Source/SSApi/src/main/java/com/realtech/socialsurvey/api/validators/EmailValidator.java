package com.realtech.socialsurvey.api.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

import com.realtech.socialsurvey.core.commons.CommonConstants;


@Component
public class EmailValidator implements SmartValidator
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
        if ( target != null ) {
            String email = (String) target;
            Pattern pattern = Pattern.compile( CommonConstants.EMAIL_REGEX );
            Matcher matcher = pattern.matcher( email );
            if ( !matcher.matches() ) {
                errors.rejectValue( validationHints[0].toString(), validationHints[1].toString(),
                    validationHints[0].toString() + " is invalid" );
            }
        }
    }
}
