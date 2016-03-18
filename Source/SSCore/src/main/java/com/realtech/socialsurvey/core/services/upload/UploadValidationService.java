package com.realtech.socialsurvey.core.services.upload;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.UploadValidation;

@Component
public interface UploadValidationService
{
    public void validateHeirarchyUpload( UploadValidation validationObject, Map<String, String> regionErrors,
        Map<String, String> branchErrors, Map<String, String> userErrors );
}
