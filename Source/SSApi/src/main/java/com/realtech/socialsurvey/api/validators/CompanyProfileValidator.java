package com.realtech.socialsurvey.api.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.request.CompanyProfileRequest;


@Component
public class CompanyProfileValidator implements Validator
{
    @Autowired
    private PhoneValidator phoneValidator;


    public boolean supports( Class<?> clazz )
    {
        return CompanyProfileRequest.class.isAssignableFrom( clazz );
    }


    public void validate( Object target, Errors errors )
    {
        CompanyProfileRequest request = (CompanyProfileRequest) target;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "companyName", ErrorCodes.COMPANYNAME_INVALID,
            "companyName cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "industry", ErrorCodes.INDUSTRY_INVALID,
            "industry cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "country", ErrorCodes.COUNTRY_INVALID, "country cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "address", ErrorCodes.ADDRESS_INVALID, "address cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "city", ErrorCodes.CITY_INVALID, "city cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "state", ErrorCodes.STATE_INVALID, "state cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "zip", ErrorCodes.ZIP_INVALID, "zip cannot be empty" );
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "officePhone", ErrorCodes.OFFICEPHONE_INVALID,
            "officePhone cannot be empty" );

        ValidationUtils.invokeValidator( phoneValidator, request.getOfficePhone(), errors, "officePhone",
            ErrorCodes.OFFICEPHONE_INVALID );

        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
