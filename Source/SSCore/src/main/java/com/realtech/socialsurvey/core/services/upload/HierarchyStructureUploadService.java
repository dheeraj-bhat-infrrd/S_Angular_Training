package com.realtech.socialsurvey.core.services.upload;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * Uploads hierarchy for a company
 *
 */
public interface HierarchyStructureUploadService
{
    /**
     * Uploads the hierarchy for the company. Validations are assumed to be done prior to invoking this method
     * @param upload
     * @param company
     * @param user
     * @throws InvalidInputException
     */
    public void uploadHierarchy(HierarchyUpload upload, Company company, User user) throws InvalidInputException;
}
