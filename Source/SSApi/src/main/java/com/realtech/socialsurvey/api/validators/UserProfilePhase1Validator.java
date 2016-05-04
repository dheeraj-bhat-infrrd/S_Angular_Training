package com.realtech.socialsurvey.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.UserProfileRequest;


/**
 * @author Shipra Goyal, RareMile
 *
 */
@Component
public class UserProfilePhase1Validator implements Validator
{
    public boolean supports( Class<?> clazz )
    {
        return UserProfileRequest.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        UserProfileRequest request = (UserProfileRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "firstName", ErrorCodes.FIRSTNAME_INVALID,
            "firstName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "lastName", ErrorCodes.LASTNAME_INVALID,
            "lastName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "title", ErrorCodes.TITLE_INVALID, "title cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "profilePhotoUrl", ErrorCodes.PROFILEPHOTOURL_INVALID,
            "profilePhotoUrl cannot be empty" );

        if ( request.getUserId() <= 0 ) {
            errors.rejectValue( "userId", ErrorCodes.USERID_INVALID, "userId is invalid" );
        }

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
