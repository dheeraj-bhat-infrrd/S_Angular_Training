package com.realtech.socialsurvey.core.services.upload;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component
public interface HierarchyUploadService
{
    /**
     * Validates the user uploaded file for hierarchy upload
     * @param company
     * @param fileName
     * @return
     * @throws InvalidInputException
     */
    public UploadValidation validateUserUploadFile( Company company, String fileName, boolean isAppend )
        throws InvalidInputException;

    /**
     * Validate hierarchy upload when modified in the UI
     * @param company
     * @param newUploadValidation
     * @return
     * @throws InvalidInputException
     */
    public UploadValidation validateHierarchyUploadJson( Company company, UploadValidation newUploadValidation, boolean isAppend )
        throws InvalidInputException;
}
