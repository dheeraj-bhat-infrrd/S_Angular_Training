package com.realtech.socialsurvey.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.PersonalProfile;


@Component
public class PersonalProfileValidator implements Validator
{
    @Autowired
    private PhoneValidator phoneValidator;


    public boolean supports( Class<?> clazz )
    {
        return PersonalProfile.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        PersonalProfile request = (PersonalProfile) target;

        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "firstName", ErrorCodes.FIRSTNAME_INVALID,
            "firstName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "lastName", ErrorCodes.LASTNAME_INVALID,
            "lastName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "title", ErrorCodes.TITLE_INVALID, "title cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "phone1", ErrorCodes.PHONE1_INVALID, "phone1 cannot be empty" );

        ValidationUtils.invokeValidator( phoneValidator, request.getPhone1(), errors, "phone1", ErrorCodes.PHONE1_INVALID );
        ValidationUtils.invokeValidator( phoneValidator, request.getPhone2(), errors, "phone2", ErrorCodes.PHONE2_INVALID );

        if ( request.getUserId() <= 0 ) {
            errors.rejectValue( "userId", ErrorCodes.USERID_INVALID, "userId is invalid" );
        }

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
