package com.realtech.socialsurvey.api.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class EmailValidator implements Validator
{
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public boolean supports( Class<?> clazz )
    {
        return String.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        if ( target != null ) {
            String email = (String) target;
            Pattern pattern = Pattern.compile( EMAIL_PATTERN );
            Matcher matcher = pattern.matcher( email );
            if ( !matcher.matches() ) {
                errors.rejectValue( "email", ErrorCodes.EMAIL_INVALID, "email address is invalid" );
            }
        }
    }
}
