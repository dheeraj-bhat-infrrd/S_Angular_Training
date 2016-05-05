package com.realtech.socialsurvey.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.models.request.UserProfileRequest;


/**
 * @author Shipra Goyal, RareMile
 *
 */
public class UserProfileImageValidator implements Validator
{
    @Autowired
    private ImageValidator imageValidator;


    public boolean supports( Class<?> clazz )
    {
        return UserProfileRequest.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        if ( target != null ) {
            UserProfileRequest request = (UserProfileRequest) target;
            ValidationUtils.invokeValidator( imageValidator, request.getProfilePhotoUrl(), errors, "profilePhotoUrl",
                ErrorCodes.PROFILEPHOTOURL_INVALID );
        }
    }
}
