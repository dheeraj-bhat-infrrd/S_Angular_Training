package com.realtech.socialsurvey.core.services.upload;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


@Component
public interface CsvUploadService
{

    /**
     * Parses the temp csv and upload
     * @param fileUpload
     * @return
     * @throws InvalidInputException
     */
    public List<String> parseAndUploadTempCsv( FileUpload fileUpload ) throws InvalidInputException;


    /**
     * Used to get the admin user while testing
     * @return
     */
    public User getUser( long userId );


    /**
     * Creates a user and assigns him under the appropriate branch or region else company.
     * @param adminUser
     * @param user
     * @throws InvalidInputException
     * @throws UserAdditionException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     */
    public void createUser( User adminUser, UserUploadVO user )
        throws InvalidInputException, UserAdditionException, NoRecordsFetchedException, SolrException, UserAssignmentException;


    /**
     * Creates a branch and assigns it under the appropriate region or company
     * @param adminUser
     * @param branch
     * @throws InvalidInputException
     * @throws BranchAdditionException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     */
    public Branch createBranch( User adminUser, BranchUploadVO branch )
        throws InvalidInputException, BranchAdditionException, SolrException;


    /**
     * Creates a region
     * @param adminUser
     * @param region
     * @return region
     * @throws InvalidInputException
     * @throws RegionAdditionException
     * @throws SolrException
     */
    public Region createRegion( User adminUser, RegionUploadVO region )
        throws InvalidInputException, RegionAdditionException, SolrException;


    /**
     * Takes a map of objects and creates them and returns list of errors if any
     * @param uploadObjects
     * @param adminUser
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     */
    public List<String> createAndReturnErrors( Map<String, List<Object>> uploadObjects, User adminUser )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException;


    /**
     * Post process code
     * @param adminUser
     */
    public void postProcess( User adminUser );


    /**
     * Returns a list of files that need to be uploaded
     * @return
     * @throws NoRecordsFetchedException
     */
    public List<FileUpload> getFilesToBeUploaded() throws NoRecordsFetchedException;


    /**
     * Updates the status of file upload
     * @param fileUpload
     * @throws InvalidInputException
     */
    public void updateFileUploadRecord( FileUpload fileUpload ) throws InvalidInputException;

}
