package com.realtech.socialsurvey.core.services.upload;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.UploadValidation;

@Component
public interface UploadValidationService
{
    public void validateHeirarchyUpload(UploadValidation validationObject);
}
