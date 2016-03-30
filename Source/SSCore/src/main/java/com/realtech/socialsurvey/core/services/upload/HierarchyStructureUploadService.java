package com.realtech.socialsurvey.core.services.upload;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.UploadStatus;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


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
    public Map<String, List<String>> uploadHierarchy( HierarchyUpload upload, Company company, User user, boolean isAppend )
        throws InvalidInputException;

    public List<UploadStatus> findInitiatedHierarchyUploads() throws NoRecordsFetchedException;

    public void updateUploadStatus( UploadStatus uploadStatus );

    public HierarchyUpload fetchHierarchyToBeUploaded( Company company ) throws InvalidInputException;

    public User getUser( long userId );

    public void addUploadStatusEntry( UploadStatus uploadStatus );


    /**
     * Saves the hierarchy upload object in mongo db
     * @param upload
     * @throws InvalidInputException
     */
    public void saveHierarchyUploadInMongo( HierarchyUpload upload ) throws InvalidInputException;

    public void addNewUploadRequest( User adminUser, boolean isAppend );

    public UploadStatus fetchLatestUploadStatus( Company company );

    public void uploadUsers( HierarchyUpload upload, User adminUser, List<String> errorList );

    public void deleteUsers( HierarchyUpload upload, User adminUser, Company company, List<String> errorList );

    public void uploadRegions( HierarchyUpload upload, User user, Company company, List<String> errorList );

    public void uploadBranches( HierarchyUpload upload, User user, Company company, List<String> errorList );

    public void deleteRegions( HierarchyUpload upload, User adminUser, Company company, List<String> errorList );

    public void deleteBranches( HierarchyUpload upload, User adminUser, Company company, List<String> errorList );

    public User assignBranchesToUser( UserUploadVO user, User adminUser, User assigneeUser, Map<String, UserUploadVO> currentUserMap,
        HierarchyUpload upload, boolean isAdmin ) throws UserAssignmentException, InvalidInputException,
        NoRecordsFetchedException, SolrException, UserAdditionException;

    public User assignRegionsToUser( UserUploadVO user, User adminUser, User assigneeUser, Map<String, UserUploadVO> currentUserMap,
        HierarchyUpload upload, boolean isAdmin ) throws UserAssignmentException, InvalidInputException,
        NoRecordsFetchedException, SolrException, UserAdditionException;

    public User modifyUser( UserUploadVO user, User adminUser, Map<String, UserUploadVO> currentUserMap, HierarchyUpload upload )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException,
        UndeliveredEmailException;

    public User assignUser( UserUploadVO user, User adminUser, Map<String, UserUploadVO> currentUserMap, HierarchyUpload upload )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException;

    
    /**
     * Method to fetch all the upload statuses for a company
     * @param company
     * @return
     */
    public List<UploadStatus> fetchUploadStatusForCompany( Company company );

    
    /**
     * Method to determine the latest status
     * @param uploadStatuses
     * @return
     */
    public UploadStatus highestStatus( List<UploadStatus> uploadStatuses );

    
    /**
     * Method to set upload status to no upload pending
     * @param uploadStatus
     */
    public void updateUploadStatusToNoUpload( UploadStatus uploadStatus );
}
