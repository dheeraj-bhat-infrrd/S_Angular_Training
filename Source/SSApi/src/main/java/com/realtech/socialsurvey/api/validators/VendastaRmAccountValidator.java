package com.realtech.socialsurvey.api.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.realtech.socialsurvey.api.exceptions.BadRequestException;
import com.realtech.socialsurvey.api.models.VendastaRmAccountVO;


@Component
public class VendastaRmAccountValidator implements Validator
{

    @Override
    public boolean supports( Class<?> clazz )
    {
        return VendastaRmAccountVO.class.isAssignableFrom( clazz );

    }


    @Override
    public void validate( Object target, Errors errors )
    {
        VendastaRmAccountVO request = (VendastaRmAccountVO) target;
        ValidationUtils.rejectIfEmptyOrWhitespace( errors, "entityType", "entityType.invalid", "entityType cannot be empty" );
        if ( request.getEntityId() <= 0 ) {
            errors.rejectValue( "entityId", "entityId.invalid", "entityId cannot be empty" );
        }
        if ( errors.hasErrors() ) {
            throw new BadRequestException( "Validation errors", errors );
        }
    }
}
