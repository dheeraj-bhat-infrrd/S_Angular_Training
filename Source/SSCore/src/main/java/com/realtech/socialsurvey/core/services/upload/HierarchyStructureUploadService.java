package com.realtech.socialsurvey.core.services.upload;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.UploadStatus;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

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
     * @return 
     * @throws InvalidInputException
     */
    public Map<String, List<String>> uploadHierarchy(HierarchyUpload upload, Company company, User user) throws InvalidInputException;

    public List<UploadStatus> findInitiatedHierarchyUploads() throws NoRecordsFetchedException;

    public void updateUploadStatus( UploadStatus uploadStatus );

    public HierarchyUpload fetchHierarchyToBeUploaded( Company company ) throws InvalidInputException;

    public User getUser( long userId );

    public void addUploadStatusEntry( UploadStatus uploadStatus );
}
