package com.realtech.socialsurvey.core.services.upload.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BooleanUploadHistory;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.LongUploadHistory;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.StringListUploadHistory;
import com.realtech.socialsurvey.core.entities.StringUploadHistory;
import com.realtech.socialsurvey.core.entities.UploadStatus;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.upload.HierarchyStructureUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;


@Component
public class HierarchyStructureUploadServiceImpl implements HierarchyStructureUploadService
{

    private static Logger LOG = LoggerFactory.getLogger( HierarchyStructureUploadServiceImpl.class );

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private SocialManagementService socialManagementService;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;

    @Autowired
    private Utils utils;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;
    
    @Resource
    @Qualifier ( "user")
    private UserDao userDao;
    
    @Autowired
    GenericDao<UploadStatus, Long> uploadStatusDao;


    @Override
    public Map<String, List<String>> uploadHierarchy( HierarchyUpload upload, Company company, User user, boolean isAppend )
        throws InvalidInputException
    {
        // the upload object should have the current value as well the changes made by the user in the sheet/ UI
        if ( upload == null ) {
            LOG.error( "No upload object to upload." );
            throw new InvalidInputException( "No upload object to upload." );
        }
        if ( company == null ) {
            LOG.error( "No company object to upload." );
            throw new InvalidInputException( "No company object to upload." );
        }
        if ( user == null ) {
            LOG.error( "Invalid user details to upload." );
            throw new InvalidInputException( "Invalid user details to upload." );
        }
        if ( !user.isCompanyAdmin() && user.getIsOwner() != CommonConstants.IS_OWNER ) {
            LOG.error( "User is not authorized to upload hierarchy." );
            throw new InvalidInputException( "User is not authorized to upload hierarchy." );
        }
        LOG.info( "Uploading hierarchy for company " + upload.getCompanyId() );
        List<String> regionUploadErrors = new ArrayList<String>();
        List<String> branchUploadErrors = new ArrayList<String>();
        List<String> userUploadErrors = new ArrayList<String>();
        List<String> userDeleteErrors = new ArrayList<String>();
        List<String> branchDeleteErrors = new ArrayList<String>();
        List<String> regionDeleteErrors = new ArrayList<String>();
        // start with addition and modification of each unit starting from the highest hierarchy and then deletion starting from the lowest hierarchy
        // uploading regions
        //Add appropriate upload statuses at each stage
        uploadRegions( upload, user, company, regionUploadErrors );
        // Uploading branches
        uploadBranches( upload, user, company, branchUploadErrors );
        // Uploading users
        uploadUsers( upload, user, userUploadErrors );
        
        //Append mode doesn't deal with deletion, you can either add or delete users
        if ( !isAppend ) {
            // Delete users
            deleteUsers( upload, user, company, userDeleteErrors );
            // Delete branches
            deleteBranches( upload, user, company, branchDeleteErrors );
            // Delete regions
            deleteRegions( upload, user, company, regionDeleteErrors );
        }

        hierarchyUploadDao.saveHierarchyUploadObject( upload );
        
        Map<String, List<String>> errorMap = new HashMap<String, List<String>>();
        if ( userUploadErrors != null && !userUploadErrors.isEmpty() ) {
            errorMap.put( CommonConstants.USER_UPLOAD_ERROR_LIST, userUploadErrors );
        }
        if ( branchUploadErrors != null && !branchUploadErrors.isEmpty() ) {
            errorMap.put( CommonConstants.BRANCH_UPLOAD_ERROR_LIST, branchUploadErrors );
        }
        if ( regionUploadErrors != null && !regionUploadErrors.isEmpty() ) {
            errorMap.put( CommonConstants.REGION_UPLOAD_ERROR_LIST, regionUploadErrors );
        }
        if ( userDeleteErrors != null && !userDeleteErrors.isEmpty() ) {
            errorMap.put( CommonConstants.USER_DELETE_ERROR_LIST, userDeleteErrors );
        }
        if ( branchDeleteErrors != null && !branchDeleteErrors.isEmpty() ) {
            errorMap.put( CommonConstants.BRANCH_DELETE_ERROR_LIST, branchDeleteErrors );
        }
        if ( regionDeleteErrors != null && !regionDeleteErrors.isEmpty() ) {
            errorMap.put( CommonConstants.REGION_DELETE_ERROR_LIST, regionDeleteErrors );
        }
        return errorMap;
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteUsers( HierarchyUpload upload, User adminUser, Company company, List<String> errorList )
    {
        LOG.debug( "Deleting removed users" );
        
        //Keep a count of deleted users
        int deletedUsers = 0;
        int deletedUsersOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus deletedUsersStatus = createUploadStatus( adminUser, CommonConstants.UPLOAD_DELETED_USERS, deletedUsers );
        
        List<UserUploadVO> userList = upload.getUsers();
        if ( userList == null || userList.isEmpty() ) {
            LOG.warn( "Empty userList" );
            return;
        }
        List<UserUploadVO> deletedUsersList = new ArrayList<UserUploadVO>();
        for ( UserUploadVO user : userList ) {
            if ( user.isDeletedRecord() ) {
                // Delete the user
                try {
                    userManagementService.removeExistingUser( adminUser, user.getUserId() );
                    // update the user count modificaiton notification
                    userManagementService.updateUserCountModificationNotification( adminUser.getCompany() );
                    LOG.debug( "Removing user {} from solr.", user.getUserId() );
                    solrSearchService.removeUserFromSolr( user.getUserId() );
                    deletedUsersList.add( user );
                    upload.getUserSourceMapping().remove( user.getSourceUserId() );
                    deletedUsers += 1;
                    
                    if ( deletedUsers > deletedUsersOldCount && System.currentTimeMillis() > nextTime ) {
                        deletedUsersStatus = updateUploadStatus( deletedUsersStatus, CommonConstants.UPLOAD_DELETED_USERS,
                            deletedUsers );
                        deletedUsersOldCount = deletedUsers;
                        nextTime = nextTime + 15*1000;
                    }
                } catch ( Exception e ) {
                    // process errors and return them to the user
                    errorList.add( e.getMessage() );
                    deletedUsersStatus = updateUploadStatusForError( deletedUsersStatus,
                        CommonConstants.UPLOAD_DELETED_USERS, deletedUsers );
                }
            }
        }
        upload.getUsers().removeAll( deletedUsersList );
        deletedUsersStatus = updateUploadStatusToDone( deletedUsersStatus, CommonConstants.UPLOAD_DELETED_USERS,
            deletedUsers );
        LOG.debug( "Finished deleting removed users" );
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteBranches( HierarchyUpload upload, User adminUser, Company company, List<String> errorList )
    {
        LOG.info( "Deleting branches" );
        
        //Keep a count of deleted users
        int deletedBranches = 0;
        int deletedBranchesOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus deletedBranchesStatus = createUploadStatus( adminUser, CommonConstants.UPLOAD_DELETED_BRANCHES, deletedBranches );
        
        List<BranchUploadVO> branches = upload.getBranches();
        if ( branches == null || branches.isEmpty() ) {
            LOG.warn( "Empty branch list" );
            return;
        }
        List<BranchUploadVO> deletedBranchesList = new ArrayList<BranchUploadVO>();

        for ( BranchUploadVO branch : branches ) {
            if ( branch.isDeletedRecord() ) {
                try {
                    // Check if branch can be deleted
                    LOG.debug( "Calling service to get the count of users in branch" );
                    long usersCount = organizationManagementService.getCountUsersInBranch( branch.getBranchId() );
                    LOG.debug( "Successfully executed service to get the count of users in branch : " + usersCount );

                    if ( usersCount > 0l ) {
                        LOG.error(
                            "Cannot delete branch : " + branch.getBranchName() + ". There are active users in the branch." );
                        throw new InvalidInputException(
                            "Cannot delete branch : " + branch.getBranchName() + ". There are active users in the branch." );
                    } else {
                        //Delete the branch
                        LOG.debug( "Calling service to deactivate branch" );
                        organizationManagementService.updateBranchStatus( adminUser, branch.getBranchId(),
                            CommonConstants.STATUS_INACTIVE );
                        //update profile name and url
                        organizationManagementService.updateProfileUrlAndStatusForDeletedEntity(
                            CommonConstants.BRANCH_ID_COLUMN, branch.getBranchId() );
                        //remove social media connections
                        socialManagementService.disconnectAllSocialConnections( CommonConstants.BRANCH_ID_COLUMN,
                            branch.getBranchId() );
                        deletedBranchesList.add( branch );
                        upload.getBranchSourceMapping().remove( branch.getSourceBranchId() );
                        deletedBranches += 1;
                        
                        if ( deletedBranches > deletedBranchesOldCount && System.currentTimeMillis() > nextTime ) {
                            deletedBranchesStatus = updateUploadStatus( deletedBranchesStatus,
                                CommonConstants.UPLOAD_DELETED_BRANCHES, deletedBranches );
                            deletedBranchesOldCount = deletedBranches;
                            nextTime = nextTime + 15*1000;
                        }
                    }

                } catch ( Exception e ) {
                    //process errors and return them to the user
                    e.printStackTrace();
                    errorList.add( e.getMessage() );
                    deletedBranchesStatus = updateUploadStatusForError( deletedBranchesStatus,
                        CommonConstants.UPLOAD_DELETED_BRANCHES, deletedBranches );
                }
            }
        }
        upload.getBranches().removeAll( deletedBranchesList );
        
        deletedBranchesStatus = updateUploadStatusToDone( deletedBranchesStatus, CommonConstants.UPLOAD_DELETED_BRANCHES,
            deletedBranches );
        LOG.info( "Finished deleting branches" );
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteRegions( HierarchyUpload upload, User adminUser, Company company, List<String> errorList )
    {
        LOG.info( "Deleting regions" );
        
        //Keep a count of deleted users
        int deletedRegions = 0;
        int deletedRegionsOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus deletedRegionsStatus = createUploadStatus( adminUser, CommonConstants.UPLOAD_DELETED_REGIONS, deletedRegions );
        
        List<RegionUploadVO> regions = upload.getRegions();
        if ( regions == null || regions.isEmpty() ) {
            LOG.warn( "Empty region list" );
            return;
        }
        List<RegionUploadVO> deletedRegionsList = new ArrayList<RegionUploadVO>();

        for ( RegionUploadVO region : regions ) {
            if ( region.isDeletedRecord() ) {
                try {

                    //Check if the region can be deleted
                    LOG.debug( "Calling service to get the count of branches in region" );
                    long branchCount = organizationManagementService.getCountBranchesInRegion( region.getRegionId() );
                    LOG.debug( "Successfully executed service to get the count of branches in region : " + branchCount );

                    if ( branchCount > 0l ) {
                        LOG.error(
                            "Cannot delete region : " + region.getRegionName() + ". There are active branches in the region." );
                        throw new InvalidInputException(
                            "Cannot delete region: " + region.getRegionName() + ". There are active branches in the region." );
                    } else {
                        // Delete the region
                        LOG.debug( "Calling service to deactivate region" );
                        organizationManagementService.updateRegionStatus( adminUser, region.getRegionId(),
                            CommonConstants.STATUS_INACTIVE );

                        //update profile name and url
                        organizationManagementService.updateProfileUrlAndStatusForDeletedEntity(
                            CommonConstants.REGION_ID_COLUMN, region.getRegionId() );
                        //remove social media connections
                        socialManagementService.disconnectAllSocialConnections( CommonConstants.REGION_ID_COLUMN,
                            region.getRegionId() );
                        deletedRegionsList.add( region );
                        upload.getRegionSourceMapping().remove( region.getSourceRegionId() );
                        deletedRegions += 1;
                        if ( deletedRegions > deletedRegionsOldCount && System.currentTimeMillis() > nextTime ) {
                            deletedRegionsStatus = updateUploadStatus( deletedRegionsStatus,
                                CommonConstants.UPLOAD_DELETED_REGIONS, deletedRegions );
                            deletedRegionsOldCount = deletedRegions;
                            nextTime = nextTime + 15*1000;
                        }
                    }

                } catch ( Exception e ) {
                    //process errors and return them to the user
                    e.printStackTrace();
                    errorList.add( e.getMessage() );
                    deletedRegionsStatus = updateUploadStatusForError( deletedRegionsStatus,
                        CommonConstants.UPLOAD_DELETED_REGIONS, deletedRegions );
                }
            }
        }
        upload.getRegions().removeAll( deletedRegionsList );
        
        deletedRegionsStatus = updateUploadStatusToDone( deletedRegionsStatus, CommonConstants.UPLOAD_DELETED_REGIONS,
            deletedRegions );
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void uploadBranches( HierarchyUpload upload, User user, Company company, List<String> errorList )
    {
        LOG.debug( "Uploading new branches" );
        
        //Keep a count of added and modified regions
        int addedBranches = 0;
        int modifiedBranches = 0;
        int addedBranchesOldCount = 0;
        int modifiedBranchesOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus addedBranchesStatus = createUploadStatus( user, CommonConstants.UPLOAD_ADDED_BRANCHES, addedBranches );
        
        UploadStatus modifiedBranchesStatus = createUploadStatus( user, CommonConstants.UPLOAD_MODIFIED_BRANCHES, modifiedBranches );
        
        List<BranchUploadVO> branchesToBeUploaded = upload.getBranches();
        if ( branchesToBeUploaded != null && !branchesToBeUploaded.isEmpty() ) {
            Branch branch = null;
            for ( BranchUploadVO branchUpload : branchesToBeUploaded ) {

                try {
                    //If branch waasn't added, modified, nor deleted, skip to the next step
                    if ( !( branchUpload.isBranchAdded() || branchUpload.isBranchModified() ) ) {
                        continue;
                    }

                    //Get region Id for branch. If null, set to default region of company
                    long regionId;
                    if ( upload.getRegionSourceMapping().get( branchUpload.getSourceRegionId() ) == null ) {
                        regionId = organizationManagementService.getDefaultRegionForCompany( company ).getRegionId();
                    } else {
                        regionId = upload.getRegionSourceMapping().get( branchUpload.getSourceRegionId() );
                    }
                    branchUpload.setRegionId( regionId );

                    if ( branchUpload.isBranchAdded() ) {
                        // Add branch
                        branch = createBranch( user, branchUpload, upload );
                        branchUpload.setBranchId( branch.getBranchId() );
                        addedBranches += 1;
                    } else if ( branchUpload.isBranchModified() ) {
                        // Modify branch
                        branch = modifyBranch( user, branchUpload );
                        modifiedBranches += 1;
                    }

                    // map the history records
                    mapBranchModificationHistory( branchUpload, branch );

                    // map the id mapping
                    /*if ( branchUpload.getSourceRegionId() != null && !branchUpload.getSourceRegionId().isEmpty() ) {
                        upload.getRegionSourceMapping().put( branchUpload.getSourceRegionId(), branch.getRegion().getRegionId() );
                    }*/
                    if ( branchUpload.getSourceBranchId() != null && !branchUpload.getSourceBranchId().isEmpty() ) {
                        upload.getBranchSourceMapping().put( branchUpload.getSourceBranchId(), branch.getBranchId() );
                    }

                    upload.setBranches( branchesToBeUploaded );
                    if ( System.currentTimeMillis() > nextTime ) {
                        //Update count in upload_status
                        if ( addedBranches > addedBranchesOldCount ) {
                            addedBranchesStatus = updateUploadStatus( addedBranchesStatus,
                                CommonConstants.UPLOAD_ADDED_BRANCHES, addedBranches );
                            addedBranchesOldCount = addedBranches;
                        }
                        if ( modifiedBranches > modifiedBranchesOldCount ) {
                            modifiedBranchesStatus = updateUploadStatus( modifiedBranchesStatus,
                                CommonConstants.UPLOAD_MODIFIED_BRANCHES, modifiedBranches );
                            modifiedBranchesOldCount = modifiedBranches;
                        }
                        nextTime = nextTime + 15*1000;
                    }
                } catch ( InvalidInputException | BranchAdditionException | SolrException | NoRecordsFetchedException
                    | UserAssignmentException e ) {
                    //Add error records
                    errorList.add( e.getMessage() );
                    e.printStackTrace();

                    //Update upload status to show the latest counts
                    addedBranchesStatus = updateUploadStatusForError( addedBranchesStatus,
                        CommonConstants.UPLOAD_ADDED_BRANCHES, addedBranches );

                    modifiedBranchesStatus = updateUploadStatusForError( modifiedBranchesStatus,
                        CommonConstants.UPLOAD_MODIFIED_BRANCHES, modifiedBranches );
                }
            }
        }
        //Update upload status to show the latest counts
        addedBranchesStatus = updateUploadStatusToDone( addedBranchesStatus, CommonConstants.UPLOAD_ADDED_BRANCHES,
            addedBranches );
        modifiedBranchesStatus = updateUploadStatusToDone( modifiedBranchesStatus,
            CommonConstants.UPLOAD_MODIFIED_BRANCHES, modifiedBranches );
    }


    @Transactional
    Branch modifyBranch( User adminUser, BranchUploadVO branch )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Branch newBranch = null;
        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( branch == null ) {
            LOG.error( "branch parameter is null!" );
            throw new InvalidInputException( "branch parameter is null!" );
        }

        LOG.info( "ModifyBranch called for branch : " + branch.getBranchName() );
        LOG.debug( "Updating branch with BranchId : " + branch.getBranchId() );
        String country, countryCode;
        if ( branch.getBranchCountry() != null && branch.getBranchCountryCode() != null ) {
            country = branch.getBranchCountry();
            countryCode = branch.getBranchCountryCode();
        } else {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( adminUser );
            country = companySettings.getContact_details().getCountry();
            countryCode = companySettings.getContact_details().getCountryCode();
        }
        Map<String, Object> map = organizationManagementService.updateBranch( adminUser, branch.getBranchId(),
            branch.getRegionId(), branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(), country,
            countryCode, branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode(), 0, null, false, false );
        newBranch = (Branch) map.get( CommonConstants.BRANCH_OBJECT );
        if ( newBranch == null ) {
            LOG.error( "No branch found with branchId :" + branch.getBranchId() );
            throw new InvalidInputException( "No branch found with branchId :" + branch.getBranchId() );
        }

        LOG.info( "ModifyBranch finished for branch : " + branch.getBranchName() );
        return newBranch;
    }


    /**
     * Creates a branch and assigns it under the appropriate region or company
     * 
     * @param adminUser
     * @param branch
     * @throws InvalidInputException
     * @throws BranchAdditionException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     */
    @Transactional
    Branch createBranch( User adminUser, BranchUploadVO branch, HierarchyUpload upload )
        throws InvalidInputException, BranchAdditionException, SolrException
    {
        Branch newBranch = null;
        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( branch == null ) {
            LOG.error( "branch parameter is null!" );
            throw new InvalidInputException( "branch parameter is null!" );
        }

        LOG.info( "createBranch called to create branch :  " + branch.getBranchName() );
        String country, countryCode;
        if ( branch.getBranchCountry() != null && branch.getBranchCountryCode() != null ) {
            country = branch.getBranchCountry();
            countryCode = branch.getBranchCountryCode();
        } else {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( adminUser );
            country = companySettings.getContact_details().getCountry();
            countryCode = companySettings.getContact_details().getCountryCode();
        }
        //Default region
        if ( branch.getSourceRegionId() == null || branch.getSourceRegionId().isEmpty() ) {
            try {
                Region region = organizationManagementService.getDefaultRegionForCompany( adminUser.getCompany() );
                branch.setRegionId( region.getRegionId() );
            } catch ( NoRecordsFetchedException e ) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } else {
            //Resolve region Id
            if ( upload.getRegionSourceMapping().containsKey( branch.getSourceRegionId() ) ) {
                branch.setRegionId( upload.getRegionSourceMapping().get( branch.getSourceRegionId() ) );
            }
        }

        newBranch = organizationManagementService.addNewBranch( adminUser, branch.getRegionId(), CommonConstants.NO,
            branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(), country, countryCode,
            branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode() );

        LOG.info( "createBranch finished for branch : " + branch.getBranchName() );
        return newBranch;
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void uploadRegions( HierarchyUpload upload, User user, Company company, List<String> errorList )
    {
        LOG.debug( "Uploading new regions." );
        
        //Keep a count of added and modified regions
        int addedRegions = 0;
        int modifiedRegions = 0;
        int addedRegionsOldCount = 0;
        int modifiedRegionsOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus addedRegionsStatus = createUploadStatus( user, CommonConstants.UPLOAD_ADDED_REGIONS, addedRegions );
        UploadStatus modifiedRegionsStatus = createUploadStatus( user, CommonConstants.UPLOAD_MODIFIED_REGIONS, modifiedRegions );
        
        
        List<RegionUploadVO> regionsToBeUploaded = upload.getRegions();
        if ( regionsToBeUploaded != null && !regionsToBeUploaded.isEmpty() ) {
            Region region = null;
            for ( RegionUploadVO regionUpload : regionsToBeUploaded ) {

                // create the region. add the field to history for all fields as its new region and map source id to the id mapping list
                try {
                    //If the region wasn't added, modified nor deleted, skip the next step
                    if ( !( regionUpload.isRegionAdded() || regionUpload.isRegionModified() ) ) {
                        continue;
                    }
                    if ( regionUpload.isRegionAdded() ) {
                        region = createRegion( user, regionUpload );
                        regionUpload.setRegionId( region.getRegionId() );
                        addedRegions += 1;
                    } else if ( regionUpload.isRegionModified() ) {
                        //process modified records
                        region = modifyRegion( user, regionUpload );
                        modifiedRegions += 1;
                    }
                    // map the history records
                    mapRegionModificationHistory( regionUpload, region );
                    // map the id mapping
                    if ( regionUpload.getSourceRegionId() != null && !regionUpload.getSourceRegionId().isEmpty() ) {
                        upload.getRegionSourceMapping().put( regionUpload.getSourceRegionId(), region.getRegionId() );
                    }

                    //Store the updated regionUploads in upload
                    upload.setRegions( regionsToBeUploaded );
                    
                    if ( System.currentTimeMillis() > nextTime ) {
                        //Update count in upload_status
                        if ( addedRegions > addedRegionsOldCount ) {
                            addedRegionsStatus = updateUploadStatus( addedRegionsStatus,
                                CommonConstants.UPLOAD_ADDED_REGIONS, addedRegions );
                            addedRegionsOldCount = addedRegions;
                        }
                        if ( modifiedRegions > modifiedRegionsOldCount ) {
                            modifiedRegionsStatus = updateUploadStatus( modifiedRegionsStatus,
                                CommonConstants.UPLOAD_MODIFIED_REGIONS, modifiedRegions );
                            modifiedRegionsOldCount = modifiedRegions;
                        }
                        nextTime = nextTime + 15 * 1000;
                    }
                } catch ( InvalidInputException | SolrException | NoRecordsFetchedException | UserAssignmentException e ) {
                    // Add error records
                    errorList.add( e.getMessage() );
                    e.printStackTrace();
                    //Update upload status to show the latest counts
                    addedRegionsStatus = updateUploadStatusForError( addedRegionsStatus,
                        CommonConstants.UPLOAD_ADDED_REGIONS, addedRegions );

                    modifiedRegionsStatus = updateUploadStatusForError( modifiedRegionsStatus,
                        CommonConstants.UPLOAD_MODIFIED_REGIONS, modifiedRegions );
                }
            }
        }
        //Update upload status to show the latest counts
        addedRegionsStatus = updateUploadStatusToDone( addedRegionsStatus, CommonConstants.UPLOAD_ADDED_REGIONS,
            addedRegions );
        modifiedRegionsStatus = updateUploadStatusToDone( modifiedRegionsStatus, CommonConstants.UPLOAD_MODIFIED_REGIONS,
            modifiedRegions );
    }


    @Transactional
    Region modifyRegion( User adminUser, RegionUploadVO region )
        throws InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        Region newRegion = null;
        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( region == null ) {
            LOG.error( "region parameter is null!" );
            throw new InvalidInputException( "region parameter is null!" );
        }

        LOG.info( "ModifyRegion called for region : " + region.getRegionName() );
        LOG.debug( "Updating region with RegionId : " + region.getRegionId() );

        //Update region
        Map<String, Object> map = organizationManagementService.updateRegion( adminUser, region.getRegionId(),
            region.getRegionName(), region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(),
            region.getRegionCountryCode(), region.getRegionState(), region.getRegionCity(), region.getRegionZipcode(), 0, null,
            false, false );
        newRegion = (Region) map.get( CommonConstants.REGION_OBJECT );

        if ( newRegion == null ) {
            LOG.error( "No region found with regionId :" + region.getRegionId() );
            throw new InvalidInputException( "No region found with regionId :" + region.getRegionId() );
        }

        return newRegion;
    }


    RegionUploadVO mapRegionModificationHistory( RegionUploadVO regionUpload, Region region )
    {
        LOG.debug( "mapping region history" );
        Date currentDate = new Date( System.currentTimeMillis() );
        // map region id history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionIdModified() ) {
            List<LongUploadHistory> regionIdHistoryList = regionUpload.getRegionIdHistory();
            if ( regionIdHistoryList == null ) {
                regionIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory regionIdHistory = new LongUploadHistory();
            regionIdHistory.setValue( region.getRegionId() );
            regionIdHistory.setTime( currentDate );
            regionIdHistoryList.add( regionIdHistory );
            regionUpload.setRegionIdHistory( regionIdHistoryList );
            regionUpload.setRegionIdModified( false );
        }

        // map source region id history
        if ( regionUpload.isRegionAdded() || regionUpload.isSourceRegionIdModified() ) {
            List<StringUploadHistory> sourceIdHistoryList = regionUpload.getSourceRegionIdHistory();
            if ( sourceIdHistoryList == null ) {
                sourceIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory sourceIdHistory = new StringUploadHistory();
            sourceIdHistory.setValue( regionUpload.getSourceRegionId() );
            sourceIdHistory.setTime( currentDate );
            sourceIdHistoryList.add( sourceIdHistory );
            regionUpload.setSourceRegionIdHistory( sourceIdHistoryList );
            regionUpload.setSourceRegionIdModified( false );
        }

        // map region name history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionNameModified() ) {
            List<StringUploadHistory> regionNameHistoryList = regionUpload.getRegionNameHistory();
            if ( regionNameHistoryList == null ) {
                regionNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionNameHistory = new StringUploadHistory();
            regionNameHistory.setValue( regionUpload.getRegionName() );
            regionNameHistory.setTime( currentDate );
            regionNameHistoryList.add( regionNameHistory );
            regionUpload.setRegionNameHistory( regionNameHistoryList );
            regionUpload.setRegionNameModified( false );
        }

        // map region address 1 history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionAddress1Modified() ) {
            List<StringUploadHistory> regionAddress1HistoryList = regionUpload.getRegionAddress1History();
            if ( regionAddress1HistoryList == null ) {
                regionAddress1HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionAddress1History = new StringUploadHistory();
            regionAddress1History.setValue( regionUpload.getRegionAddress1() );
            regionAddress1History.setTime( currentDate );
            regionAddress1HistoryList.add( regionAddress1History );
            regionUpload.setRegionAddress1History( regionAddress1HistoryList );
            regionUpload.setRegionAddress1Modified( false );
        }

        // map region address 2 history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionAddress2Modified() ) {
            List<StringUploadHistory> regionAddress2HistoryList = regionUpload.getRegionAddress2History();
            if ( regionAddress2HistoryList == null ) {
                regionAddress2HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionAddress2History = new StringUploadHistory();
            regionAddress2History.setValue( regionUpload.getRegionAddress2() );
            regionAddress2History.setTime( currentDate );
            regionAddress2HistoryList.add( regionAddress2History );
            regionUpload.setRegionAddress2History( regionAddress2HistoryList );
            regionUpload.setRegionAddress2Modified( false );
        }

        // map city history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionCityModified() ) {
            List<StringUploadHistory> regionCityHistoryList = regionUpload.getRegionCityHistory();
            if ( regionCityHistoryList == null ) {
                regionCityHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionCityHistory = new StringUploadHistory();
            regionCityHistory.setValue( regionUpload.getRegionCity() );
            regionCityHistory.setTime( currentDate );
            regionCityHistoryList.add( regionCityHistory );
            regionUpload.setRegionCityHistory( regionCityHistoryList );
            regionUpload.setRegionCityModified( false );
        }

        // map state history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionStateModified() ) {
            List<StringUploadHistory> regionStateHistoryList = regionUpload.getRegionStateHistory();
            if ( regionStateHistoryList == null ) {
                regionStateHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionStateHistory = new StringUploadHistory();
            regionStateHistory.setValue( regionUpload.getRegionState() );
            regionStateHistory.setTime( currentDate );
            regionStateHistoryList.add( regionStateHistory );
            regionUpload.setRegionStateHistory( regionStateHistoryList );
            regionUpload.setRegionStateModified( false );
        }

        // map zip history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionZipcodeModified() ) {
            List<StringUploadHistory> regionZipCodeHistoryList = regionUpload.getRegionZipcodeHistory();
            if ( regionZipCodeHistoryList == null ) {
                regionZipCodeHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionZipCodeHistory = new StringUploadHistory();
            regionZipCodeHistory.setValue( regionUpload.getRegionZipcode() );
            regionZipCodeHistory.setTime( currentDate );
            regionZipCodeHistoryList.add( regionZipCodeHistory );
            regionUpload.setRegionZipcodeHistory( regionZipCodeHistoryList );
            regionUpload.setRegionZipcodeModified( false );
        }

        //map country history
        if ( regionUpload.isRegionAdded() || regionUpload.isRegionCountryModified() ) {
            List<StringUploadHistory> regionCountryHistoryList = regionUpload.getRegionCountryHistory();
            if ( regionCountryHistoryList == null ) {
                regionCountryHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionCountryHistory = new StringUploadHistory();
            regionCountryHistory.setTime( currentDate );
            regionCountryHistory.setValue( regionUpload.getRegionCountry() );
            regionCountryHistoryList.add( regionCountryHistory );
            regionUpload.setRegionCountryHistory( regionCountryHistoryList );
            regionUpload.setRegionCountryModified( false );
        }

        regionUpload.setRegionAdded( false );
        regionUpload.setRegionModified( false );
        regionUpload.setErrorRecord( false );
        regionUpload.setWarningRecord( false );
        regionUpload.setDeletedRecord( false );
        return regionUpload;
    }


    /**
     * Creates a region
     * 
     * @param adminUser
     * @param region
     * @throws InvalidInputException
     * @throws RegionAdditionException
     * @throws SolrException
     */
    @Transactional
    Region createRegion( User adminUser, RegionUploadVO region ) throws InvalidInputException, SolrException
    {
        Region newRegion = null;
        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( region == null ) {
            LOG.error( "region parameter is null!" );
            throw new InvalidInputException( "region parameter is null!" );
        }
        LOG.info( "createRegion called to add region : " + region.getRegionName() );

        LOG.debug( "Adding region : " + region.getRegionName() );
        newRegion = organizationManagementService.addNewRegion( adminUser, region.getRegionName(), CommonConstants.NO,
            region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(), region.getRegionCountryCode(),
            region.getRegionState(), region.getRegionCity(), region.getRegionZipcode() );
        organizationManagementService.addNewBranch( adminUser, newRegion.getRegionId(), CommonConstants.YES,
            CommonConstants.DEFAULT_BRANCH_NAME, null, null, null, null, null, null, null );
        return newRegion;
    }


    UserUploadVO mapUserModificationHistory( UserUploadVO userUpload, User user )
    {
        LOG.info( "Mapping user history" );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        //map user first name history
        if ( userUpload.isUserAdded() || userUpload.isFirstNameModified() ) {
            List<StringUploadHistory> firstNameHistoryList = userUpload.getFirstNameHistory();
            if ( firstNameHistoryList == null ) {
                firstNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory firstNameHistory = new StringUploadHistory();
            firstNameHistory.setTime( currentTimestamp );
            firstNameHistory.setValue( userUpload.getFirstName() );
            firstNameHistoryList.add( firstNameHistory );
            userUpload.setFirstNameHistory( firstNameHistoryList );
            userUpload.setFirstNameModified( false );
        }

        //map user last name history
        if ( userUpload.isUserAdded() || userUpload.isLastNameModified() ) {
            List<StringUploadHistory> lastNameHistoryList = userUpload.getLastNameHistory();
            if ( lastNameHistoryList == null ) {
                lastNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory lastNameHistory = new StringUploadHistory();
            lastNameHistory.setTime( currentTimestamp );
            lastNameHistory.setValue( userUpload.getLastName() );
            lastNameHistoryList.add( lastNameHistory );
            userUpload.setLastNameHistory( lastNameHistoryList );
            userUpload.setLastNameModified( false );
        }

        //map user title history
        if ( userUpload.isUserAdded() || userUpload.isTitleModified() ) {
            List<StringUploadHistory> titleHistoryList = userUpload.getTitleHistory();
            if ( titleHistoryList == null ) {
                titleHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory titleHistory = new StringUploadHistory();
            titleHistory.setTime( currentTimestamp );
            titleHistory.setValue( userUpload.getTitle() );
            titleHistoryList.add( titleHistory );
            userUpload.setTitleHistory( titleHistoryList );
            userUpload.setTitleModified( false );
        }

        //map branch id history
        if ( userUpload.isUserAdded() || userUpload.isBranchIdModified() ) {
            List<LongUploadHistory> branchIdHistoryList = userUpload.getBranchIdHistory();
            if ( branchIdHistoryList == null ) {
                branchIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory branchIdHistory = new LongUploadHistory();
            branchIdHistory.setTime( currentTimestamp );
            branchIdHistory.setValue( userUpload.getBranchId() );
            branchIdHistoryList.add( branchIdHistory );
            userUpload.setBranchIdHistory( branchIdHistoryList );
            userUpload.setBranchIdModified( false );
        }

        //map source branch id history
        if ( userUpload.isUserAdded() || userUpload.isSourceBranchIdModified() ) {
            List<StringUploadHistory> sourceBranchIdHistoryList = userUpload.getSourceBranchIdHistory();
            if ( sourceBranchIdHistoryList == null ) {
                sourceBranchIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory sourceBranchIdHistory = new StringUploadHistory();
            sourceBranchIdHistory.setTime( currentTimestamp );
            sourceBranchIdHistory.setValue( userUpload.getSourceBranchId() );
            sourceBranchIdHistoryList.add( sourceBranchIdHistory );
            userUpload.setSourceBranchIdHistory( sourceBranchIdHistoryList );
            userUpload.setSourceBranchIdModified( false );
        }

        //map region id history
        if ( userUpload.isUserAdded() || userUpload.isRegionIdModified() ) {
            List<LongUploadHistory> regionIdHistoryList = userUpload.getRegionIdHistory();
            if ( regionIdHistoryList == null ) {
                regionIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory regionIdHistory = new LongUploadHistory();
            regionIdHistory.setTime( currentTimestamp );
            regionIdHistory.setValue( userUpload.getRegionId() );
            regionIdHistoryList.add( regionIdHistory );
            userUpload.setRegionIdHistory( regionIdHistoryList );
            userUpload.setRegionIdModified( false );
        }

        //map source region id history
        if ( userUpload.isUserAdded() || userUpload.isSourceRegionIdModified() ) {
            List<StringUploadHistory> sourceRegionIdHistoryList = userUpload.getSourceRegionIdHistory();
            if ( sourceRegionIdHistoryList == null ) {
                sourceRegionIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory sourceRegionIdHistory = new StringUploadHistory();
            sourceRegionIdHistory.setTime( currentTimestamp );
            sourceRegionIdHistory.setValue( userUpload.getSourceRegionId() );
            sourceRegionIdHistoryList.add( sourceRegionIdHistory );
            userUpload.setSourceRegionIdHistory( sourceRegionIdHistoryList );
            userUpload.setSourceRegionIdModified( false );
        }

        //map is agent history
        if ( userUpload.isUserAdded() || userUpload.isAgentModified() ) {
            List<BooleanUploadHistory> isAgentHistoryList = userUpload.getIsAgentHistory();
            if ( isAgentHistoryList == null ) {
                isAgentHistoryList = new ArrayList<BooleanUploadHistory>();
            }
            BooleanUploadHistory isAgentHistory = new BooleanUploadHistory();
            isAgentHistory.setTime( currentTimestamp );
            isAgentHistory.setValue( userUpload.isAgent() );
            isAgentHistoryList.add( isAgentHistory );
            userUpload.setIsAgentHistory( isAgentHistoryList );
            userUpload.setAgentModified( false );
        }

        //map email ID history
        if ( userUpload.isUserAdded() || userUpload.isEmailIdModified() ) {
            List<StringUploadHistory> emailIdHistoryList = userUpload.getEmailIdHistory();
            if ( emailIdHistoryList == null ) {
                emailIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory emailIdHistory = new StringUploadHistory();
            emailIdHistory.setTime( currentTimestamp );
            emailIdHistory.setValue( userUpload.getEmailId() );
            emailIdHistoryList.add( emailIdHistory );
            userUpload.setEmailIdHistory( emailIdHistoryList );
            userUpload.setEmailIdModified( false );
        }

        //map belongs to company history
        if ( userUpload.isUserAdded() || userUpload.isBelongsToCompanyModified() ) {
            List<BooleanUploadHistory> belongsToCompanyHistoryList = userUpload.getBelongsToCompanyHistory();
            if ( belongsToCompanyHistoryList == null ) {
                belongsToCompanyHistoryList = new ArrayList<BooleanUploadHistory>();
            }
            BooleanUploadHistory belongsToCompanyHistory = new BooleanUploadHistory();
            belongsToCompanyHistory.setTime( currentTimestamp );
            belongsToCompanyHistory.setValue( userUpload.isBelongsToCompany() );
            belongsToCompanyHistoryList.add( belongsToCompanyHistory );
            userUpload.setBelongsToCompanyHistory( belongsToCompanyHistoryList );
            userUpload.setBelongsToCompanyModified( false );
        }

        //map assign to company history
        if ( userUpload.isUserAdded() || userUpload.isAssignToCompany() ) {
            List<BooleanUploadHistory> assignedToCompanyHistoryList = userUpload.getAssignToCompanyHistory();
            if ( assignedToCompanyHistoryList == null ) {
                assignedToCompanyHistoryList = new ArrayList<BooleanUploadHistory>();
            }
            BooleanUploadHistory assignedToCompanyHistory = new BooleanUploadHistory();
            assignedToCompanyHistory.setTime( currentTimestamp );
            assignedToCompanyHistory.setValue( userUpload.isAssignToCompany() );
            assignedToCompanyHistoryList.add( assignedToCompanyHistory );
            userUpload.setAssignToCompanyHistory( assignedToCompanyHistoryList );
            userUpload.setAssignToCompanyModified( false );
        }

        //map assigned branch name history
        if ( userUpload.isUserAdded() || userUpload.isAssignedBranchNameModified() ) {
            List<StringUploadHistory> branchNameHistoryList = userUpload.getAssignedBranchNameHistory();
            if ( branchNameHistoryList == null ) {
                branchNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory branchNameHistory = new StringUploadHistory();
            branchNameHistory.setTime( currentTimestamp );
            branchNameHistory.setValue( userUpload.getAssignedBranchName() );
            branchNameHistoryList.add( branchNameHistory );
            userUpload.setAssignedBranchNameHistory( branchNameHistoryList );
            userUpload.setAssignedBranchNameModified( false );
        }

        //map assigned branches history
        if ( userUpload.isUserAdded() || userUpload.isAssignedBranchesModified() ) {
            List<StringListUploadHistory> assignedBranchesHistoryList = userUpload.getAssignedBranchesHistory();
            if ( assignedBranchesHistoryList == null ) {
                assignedBranchesHistoryList = new ArrayList<StringListUploadHistory>();
            }
            StringListUploadHistory assignedBranchesHistory = new StringListUploadHistory();
            assignedBranchesHistory.setTime( currentTimestamp );
            assignedBranchesHistory.setValue( userUpload.getAssignedBranches() );
            assignedBranchesHistoryList.add( assignedBranchesHistory );
            userUpload.setAssignedBranchesHistory( assignedBranchesHistoryList );
            userUpload.setAssignedBranchesModified( false );
        }

        //map assigned region name history
        if ( userUpload.isUserAdded() || userUpload.isAssignedRegionNameModified() ) {
            List<StringUploadHistory> regionNameHistoryList = userUpload.getAssignedRegionNameHistory();
            if ( regionNameHistoryList == null ) {
                regionNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionNameHistory = new StringUploadHistory();
            regionNameHistory.setTime( currentTimestamp );
            regionNameHistory.setValue( userUpload.getAssignedRegionName() );
            regionNameHistoryList.add( regionNameHistory );
            userUpload.setAssignedRegionNameHistory( regionNameHistoryList );
            userUpload.setAssignedRegionNameModified( false );
        }

        //map assigned regions history
        if ( userUpload.isUserAdded() || userUpload.isAssignedRegionsModified() ) {
            List<StringListUploadHistory> assignedRegionsHistoryList = userUpload.getAssignedRegionsHistory();
            if ( assignedRegionsHistoryList == null ) {
                assignedRegionsHistoryList = new ArrayList<StringListUploadHistory>();
            }
            StringListUploadHistory assignedRegionsHistory = new StringListUploadHistory();
            assignedRegionsHistory.setTime( currentTimestamp );
            assignedRegionsHistory.setValue( userUpload.getAssignedRegions() );
            assignedRegionsHistoryList.add( assignedRegionsHistory );
            userUpload.setAssignedRegionsHistory( assignedRegionsHistoryList );
            userUpload.setAssignedRegionsModified( false );
        }

        //map is branch admin history
        if ( userUpload.isUserAdded() || userUpload.isBranchAdminModified() ) {
            List<BooleanUploadHistory> isBranchAdminHistoryList = userUpload.getIsBranchAdminHistory();
            if ( isBranchAdminHistoryList == null ) {
                isBranchAdminHistoryList = new ArrayList<BooleanUploadHistory>();
            }
            BooleanUploadHistory isBranchAdminHistory = new BooleanUploadHistory();
            isBranchAdminHistory.setTime( currentTimestamp );
            isBranchAdminHistory.setValue( userUpload.isBranchAdmin() );
            isBranchAdminHistoryList.add( isBranchAdminHistory );
            userUpload.setIsBranchAdminHistory( isBranchAdminHistoryList );
            userUpload.setBranchAdminModified( false );
        }

        //map assigned branches admin history
        if ( userUpload.isUserAdded() || userUpload.isAssignedBrachesAdminModified() ) {
            List<StringListUploadHistory> assignedBranchesAdminHistoryList = userUpload.getAssignedBrachesAdminHistory();
            if ( assignedBranchesAdminHistoryList == null ) {
                assignedBranchesAdminHistoryList = new ArrayList<StringListUploadHistory>();
            }
            StringListUploadHistory assignedBranchesAdminHistory = new StringListUploadHistory();
            assignedBranchesAdminHistory.setTime( currentTimestamp );
            assignedBranchesAdminHistory.setValue( userUpload.getAssignedBranchesAdmin() );
            assignedBranchesAdminHistoryList.add( assignedBranchesAdminHistory );
            userUpload.setAssignedBrachesAdminHistory( assignedBranchesAdminHistoryList );
            userUpload.setAssignedBrachesAdminModified( false );
        }

        //map is region admin history
        if ( userUpload.isUserAdded() || userUpload.isRegionAdminModified() ) {
            List<BooleanUploadHistory> isRegionAdminHistoryList = userUpload.getIsRegionAdminHistory();
            if ( isRegionAdminHistoryList == null ) {
                isRegionAdminHistoryList = new ArrayList<BooleanUploadHistory>();
            }
            BooleanUploadHistory isRegionAdminHistory = new BooleanUploadHistory();
            isRegionAdminHistory.setTime( currentTimestamp );
            isRegionAdminHistory.setValue( userUpload.isRegionAdmin() );
            isRegionAdminHistoryList.add( isRegionAdminHistory );
            userUpload.setIsRegionAdminHistory( isRegionAdminHistoryList );
            userUpload.setRegionAdminModified( false );
        }

        //map assigned regions admin history
        if ( userUpload.isUserAdded() || userUpload.isAssignedRegionsAdminModified() ) {
            List<StringListUploadHistory> assignedRegionsAdminHistoryList = userUpload.getAssignedRegionsAdminHistory();
            if ( assignedRegionsAdminHistoryList == null ) {
                assignedRegionsAdminHistoryList = new ArrayList<StringListUploadHistory>();
            }
            StringListUploadHistory assignedRegionsAdminHistory = new StringListUploadHistory();
            assignedRegionsAdminHistory.setTime( currentTimestamp );
            assignedRegionsAdminHistory.setValue( userUpload.getAssignedRegionsAdmin() );
            assignedRegionsAdminHistoryList.add( assignedRegionsAdminHistory );
            userUpload.setAssignedRegionsAdminHistory( assignedRegionsAdminHistoryList );
            userUpload.setAssignedRegionsAdminModified( false );
        }

        //map phone number history
        if ( userUpload.isUserAdded() || userUpload.isPhoneNumberModified() ) {
            List<StringUploadHistory> phoneNumberHistoryList = userUpload.getPhoneNumberHistory();
            if ( phoneNumberHistoryList == null ) {
                phoneNumberHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory phoneNumberHistory = new StringUploadHistory();
            phoneNumberHistory.setTime( currentTimestamp );
            phoneNumberHistory.setValue( userUpload.getPhoneNumber() );
            phoneNumberHistoryList.add( phoneNumberHistory );
            userUpload.setPhoneNumberHistory( phoneNumberHistoryList );
            userUpload.setPhoneNumberModified( false );
        }

        //map website url history
        if ( userUpload.isUserAdded() || userUpload.isWebsiteUrlModified() ) {
            List<StringUploadHistory> websiteUrlHistoryList = userUpload.getWebsiteUrlHistory();
            if ( websiteUrlHistoryList == null ) {
                websiteUrlHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory websiteUrlHistory = new StringUploadHistory();
            websiteUrlHistory.setTime( currentTimestamp );
            websiteUrlHistory.setValue( userUpload.getWebsiteUrl() );
            websiteUrlHistoryList.add( websiteUrlHistory );
            userUpload.setWebsiteUrlHistory( websiteUrlHistoryList );
            userUpload.setWebsiteUrlModified( false );
        }

        //map license history
        if ( userUpload.isUserAdded() || userUpload.isLicenseModified() ) {
            List<StringUploadHistory> licenseHistoryList = userUpload.getLicenseHistory();
            if ( licenseHistoryList == null ) {
                licenseHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory licenseHistory = new StringUploadHistory();
            licenseHistory.setTime( currentTimestamp );
            licenseHistory.setValue( userUpload.getLicense() );
            licenseHistoryList.add( licenseHistory );
            userUpload.setLicenseHistory( licenseHistoryList );
            userUpload.setLicenseModified( false );
        }

        //map legal disclaimer history
        if ( userUpload.isUserAdded() || userUpload.isLegalDisclaimerModified() ) {
            List<StringUploadHistory> legalDisclaimerHistoryList = userUpload.getLegalDisclaimerHistory();
            if ( legalDisclaimerHistoryList == null ) {
                legalDisclaimerHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory legalDisclaimerHistory = new StringUploadHistory();
            legalDisclaimerHistory.setTime( currentTimestamp );
            legalDisclaimerHistory.setValue( userUpload.getLegalDisclaimer() );
            legalDisclaimerHistoryList.add( legalDisclaimerHistory );
            userUpload.setLegalDisclaimerHistory( legalDisclaimerHistoryList );
            userUpload.setLegalDisclaimerModified( false );
        }

        //map about me history
        if ( userUpload.isUserAdded() || userUpload.isAboutMeDescriptionModified() ) {
            List<StringUploadHistory> aboutMeHistoryList = userUpload.getAboutMeDescriptionHistory();
            if ( aboutMeHistoryList == null ) {
                aboutMeHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory aboutMeHistory = new StringUploadHistory();
            aboutMeHistory.setTime( currentTimestamp );
            aboutMeHistory.setValue( userUpload.getAboutMeDescription() );
            aboutMeHistoryList.add( aboutMeHistory );
            userUpload.setAboutMeDescriptionHistory( aboutMeHistoryList );
            userUpload.setAboutMeDescriptionModified( false );
        }

        //map user profile photo
        if ( userUpload.isUserAdded() || userUpload.isUserPhotoUrlModified() ) {
            List<StringUploadHistory> photoHistoryList = userUpload.getUserPhotoUrlHistory();
            if ( photoHistoryList == null ) {
                photoHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory photoHistory = new StringUploadHistory();
            photoHistory.setTime( currentTimestamp );
            photoHistory.setValue( userUpload.getUserPhotoUrl() );
            photoHistoryList.add( photoHistory );
            userUpload.setLegalDisclaimerHistory( photoHistoryList );
            userUpload.setUserPhotoUrlModified( false );
        }
        userUpload.setUserAdded( false );
        userUpload.setUserModified( false );
        userUpload.setWarningRecord( false );
        userUpload.setErrorRecord( false );
        userUpload.setDeletedRecord( false );
        userUpload.setSendMail( false );
        return userUpload;

    }


    BranchUploadVO mapBranchModificationHistory( BranchUploadVO branchUpload, Branch branch )
    {
        LOG.info( "Mapping branch history" );
        Timestamp currentTimestamp = new Timestamp( System.currentTimeMillis() );
        //map branch id history
        if ( branchUpload.isBranchIdModified() || branchUpload.isBranchAdded() ) {
            List<LongUploadHistory> branchIdHistoryList = branchUpload.getBranchIdHistory();
            if ( branchIdHistoryList == null ) {
                branchIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory branchIdHistory = new LongUploadHistory();
            branchIdHistory.setTime( currentTimestamp );
            branchIdHistory.setValue( branch.getBranchId() );
            branchIdHistoryList.add( branchIdHistory );
            branchUpload.setBranchIdHistory( branchIdHistoryList );
            branchUpload.setBranchIdModified( false );
        }

        //map source branch id history
        if ( branchUpload.isSourceBranchIdModified() || branchUpload.isBranchAdded() ) {
            List<StringUploadHistory> branchSourceIdHistoryList = branchUpload.getSourceBranchIdHistory();
            if ( branchSourceIdHistoryList == null ) {
                branchSourceIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory branchSourceIdHistory = new StringUploadHistory();
            branchSourceIdHistory.setTime( currentTimestamp );
            branchSourceIdHistory.setValue( branchUpload.getSourceBranchId() );
            branchSourceIdHistoryList.add( branchSourceIdHistory );
            branchUpload.setSourceBranchIdHistory( branchSourceIdHistoryList );
            branchUpload.setSourceBranchIdModified( false );
        }

        //map region id history
        if ( branchUpload.isRegionIdModified() || branchUpload.isBranchAdded() ) {
            List<LongUploadHistory> regionIdHistoryList = branchUpload.getRegionIdHistory();
            if ( regionIdHistoryList == null ) {
                regionIdHistoryList = new ArrayList<LongUploadHistory>();
            }
            LongUploadHistory regionIdHistory = new LongUploadHistory();
            regionIdHistory.setTime( currentTimestamp );
            regionIdHistory.setValue( branchUpload.getRegionId() );
            regionIdHistoryList.add( regionIdHistory );
            branchUpload.setRegionIdHistory( regionIdHistoryList );
            branchUpload.setRegionIdModified( false );
        }

        //map region source id history
        if ( branchUpload.isSourceRegionIdModified() || branchUpload.isBranchAdded() ) {
            List<StringUploadHistory> regionSourceIdHistoryList = branchUpload.getSourceRegionIdHistory();
            if ( regionSourceIdHistoryList == null ) {
                regionSourceIdHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory regionSourceIdHistory = new StringUploadHistory();
            regionSourceIdHistory.setTime( currentTimestamp );
            regionSourceIdHistory.setValue( branchUpload.getSourceRegionId() );
            regionSourceIdHistoryList.add( regionSourceIdHistory );
            branchUpload.setSourceRegionIdHistory( regionSourceIdHistoryList );
            branchUpload.setSourceRegionIdModified( false );
        }

        //map branch name history
        if ( branchUpload.isBranchNameModified() || branchUpload.isBranchAdded() ) {
            List<StringUploadHistory> branchNameHistoryList = branchUpload.getBranchNameHistory();
            if ( branchNameHistoryList == null ) {
                branchNameHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory branchNameHistory = new StringUploadHistory();
            branchNameHistory.setTime( currentTimestamp );
            branchNameHistory.setValue( branchUpload.getBranchName() );
            branchNameHistoryList.add( branchNameHistory );
            branchUpload.setBranchNameHistory( branchNameHistoryList );
            branchUpload.setBranchNameModified( false );
        }

        //map branch address 1 history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchAddress1Modified() ) {
            List<StringUploadHistory> address1HistoryList = branchUpload.getBranchAddress1History();
            if ( address1HistoryList == null ) {
                address1HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory address1History = new StringUploadHistory();
            address1History.setTime( currentTimestamp );
            address1History.setValue( branchUpload.getBranchAddress1() );
            address1HistoryList.add( address1History );
            branchUpload.setBranchAddress1History( address1HistoryList );
            branchUpload.setBranchAddress1Modified( false );
        }

        //map branch address 2 history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchAddress2Modified() ) {
            List<StringUploadHistory> address2HistoryList = branchUpload.getBranchAddress2History();
            if ( address2HistoryList == null ) {
                address2HistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory address2History = new StringUploadHistory();
            address2History.setTime( currentTimestamp );
            address2History.setValue( branchUpload.getBranchAddress2() );
            address2HistoryList.add( address2History );
            branchUpload.setBranchAddress2History( address2HistoryList );
            branchUpload.setBranchAddress2Modified( false );
        }

        //map branch country history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchCountryModified() ) {
            List<StringUploadHistory> countryHistoryList = branchUpload.getBranchCountryHistory();
            if ( countryHistoryList == null ) {
                countryHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory countryHistory = new StringUploadHistory();
            countryHistory.setTime( currentTimestamp );
            countryHistory.setValue( branchUpload.getBranchCountry() );
            countryHistoryList.add( countryHistory );
            branchUpload.setBranchCountryHistory( countryHistoryList );
            branchUpload.setBranchCountryModified( false );
        }

        //map branch country code history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchCountryCodeModified() ) {
            List<StringUploadHistory> countryCodeHistoryList = branchUpload.getBranchCountryCodeHistory();
            if ( countryCodeHistoryList == null ) {
                countryCodeHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory countryCodeHistory = new StringUploadHistory();
            countryCodeHistory.setTime( currentTimestamp );
            countryCodeHistory.setValue( branchUpload.getBranchCountryCode() );
            countryCodeHistoryList.add( countryCodeHistory );
            branchUpload.setBranchCountryCodeHistory( countryCodeHistoryList );
            branchUpload.setBranchCountryCodeModified( false );
        }

        //map branch state history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchStateModified() ) {
            List<StringUploadHistory> stateHistoryList = branchUpload.getBranchStateHistory();
            if ( stateHistoryList == null ) {
                stateHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory stateHistory = new StringUploadHistory();
            stateHistory.setTime( currentTimestamp );
            stateHistory.setValue( branchUpload.getBranchState() );
            stateHistoryList.add( stateHistory );
            branchUpload.setBranchStateHistory( stateHistoryList );
            branchUpload.setBranchStateModified( false );
        }

        //map branch city history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchCityModified() ) {
            List<StringUploadHistory> cityHistoryList = branchUpload.getBranchCityHistory();
            if ( cityHistoryList == null ) {
                cityHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory cityHistory = new StringUploadHistory();
            cityHistory.setTime( currentTimestamp );
            cityHistory.setValue( branchUpload.getBranchCity() );
            cityHistoryList.add( cityHistory );
            branchUpload.setBranchCityHistory( cityHistoryList );
            branchUpload.setBranchCityModified( false );
        }

        //map branch zipcode history
        if ( branchUpload.isBranchAdded() || branchUpload.isBranchZipcodeModified() ) {
            List<StringUploadHistory> zipcodeHistoryList = branchUpload.getBranchZipcodeHistory();
            if ( zipcodeHistoryList == null ) {
                zipcodeHistoryList = new ArrayList<StringUploadHistory>();
            }
            StringUploadHistory zipcodeHistory = new StringUploadHistory();
            zipcodeHistory.setTime( currentTimestamp );
            zipcodeHistory.setValue( branchUpload.getBranchZipcode() );
            zipcodeHistoryList.add( zipcodeHistory );
            branchUpload.setBranchZipcodeHistory( zipcodeHistoryList );
            branchUpload.setBranchZipcodeModified( false );
        }
        branchUpload.setBranchAdded( false );
        branchUpload.setBranchModified( false );
        branchUpload.setErrorRecord( false );
        branchUpload.setWarningRecord( false );
        branchUpload.setDeletedRecord( false );
        return branchUpload;
    }


    Company getCompany( User user ) throws InvalidInputException
    {
        Company company = user.getCompany();
        if ( company == null ) {
            LOG.error( "Company property not found in admin user object!" );
            throw new InvalidInputException( "Company property not found in admin user object!" );

        }
        return company;
    }


    LicenseDetail getLicenseDetail( Company company ) throws InvalidInputException
    {
        LicenseDetail companyLicenseDetail = null;
        if ( company.getLicenseDetails() != null && !company.getLicenseDetails().isEmpty() ) {
            companyLicenseDetail = company.getLicenseDetails().get( CommonConstants.INITIAL_INDEX );
        } else {
            LOG.error( "License Detail property not found in admin user's company object!" );
            throw new InvalidInputException( "License Detail property not found in admin user's company object!" );
        }
        return companyLicenseDetail;
    }


    /**
     * Method to assign/unassign branches to user
     * @param user
     * @param adminUser
     * @param assigneeUser
     * @param currentUserMap
     * @param upload
     * @return
     * @throws UserAssignmentException
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAdditionException
     */
    @Transactional
    @Override
    public User assignBranchesToUser( UserUploadVO user, User adminUser, User assigneeUser, Map<String, UserUploadVO> currentUserMap,
        HierarchyUpload upload, boolean isAdmin )
        throws UserAssignmentException, InvalidInputException, NoRecordsFetchedException, SolrException, UserAdditionException
    {
        LOG.info( "Method assignBranchesToUser() for user : " + user.getEmailId() + " isAdmin : " + isAdmin + " started." );
        if ( ( !isAdmin && ( user.isSourceBranchIdModified() || user.isAssignedBranchesModified() ) )
            || ( isAdmin && user.isAssignedBrachesAdminModified() ) || user.isUserAdded() ) {
            //Compare the current user assignments and the new assignments to find the changes
            /*
             * Cases to handle:
             * 1. All new assignments
             * 2. Delete all assignments
             * 3. add some, delete some assignments
             */
            List<String> addedAssignments = new ArrayList<String>();
            List<String> deletedAssignments = new ArrayList<String>();

            if ( currentUserMap.containsKey( user.getSourceUserId() ) ) {
                //Existing user
                List<String> oldAssignments = new ArrayList<String>();
                List<String> newAssignments = new ArrayList<String>();
                if ( isAdmin ) {
                    oldAssignments.addAll( currentUserMap.get( user.getSourceUserId() ).getAssignedBranchesAdmin() );
                    if ( user.getAssignedBranchesAdmin() != null ) {
                        newAssignments.addAll( user.getAssignedBranchesAdmin() );
                    }
                } else {
                    oldAssignments.addAll( currentUserMap.get( user.getSourceUserId() ).getAssignedBranches() );
                    if ( user.getAssignedBranches() != null ) {
                        newAssignments.addAll( user.getAssignedBranches() );
                    }
                }

                if ( oldAssignments == null || oldAssignments.isEmpty() ) {
                    //All assignments are new
                    addedAssignments.addAll( newAssignments );
                } else if ( newAssignments == null || newAssignments.isEmpty() ) {
                    //Delete all assignments
                    deletedAssignments.addAll( oldAssignments );
                } else {
                    //find added and deleted assignments
                    List<String> tempOldAssignments = new ArrayList<String>();
                    tempOldAssignments.addAll( oldAssignments );
                    List<String> tempNewAssignments = new ArrayList<String>();
                    tempNewAssignments.addAll( newAssignments );

                    tempOldAssignments.removeAll( newAssignments );
                    deletedAssignments.addAll( tempOldAssignments );

                    tempNewAssignments.removeAll( oldAssignments );
                    addedAssignments.addAll( tempNewAssignments );
                }
            } else {
                //All assignments are new
                if ( isAdmin ) {
                    if ( user.getAssignedBranchesAdmin() != null ) {
                        addedAssignments.addAll( user.getAssignedBranchesAdmin() );
                    }
                } else {
                    if ( user.getAssignedBranches() != null ) {
                        addedAssignments.addAll( user.getAssignedBranches() );
                    }
                }
            }
            //Add branch assignments
            for ( String sourceBranchId : addedAssignments ) {
                if ( upload.getBranchSourceMapping().containsKey( sourceBranchId ) ) {
                    long branchId = upload.getBranchSourceMapping().get( sourceBranchId );
                    Branch branch = userManagementService.getBranchById( branchId );
                    if ( branch == null ) {
                        throw new UserAssignmentException(
                            "unable to find branch with branchId : " + branchId + " and sourceId : " + sourceBranchId );
                    }
                    long regionId = branch.getRegion().getRegionId();
                    organizationManagementService.assignBranchToUser( adminUser, branchId, regionId, assigneeUser, isAdmin );
                } else {
                    throw new UserAssignmentException( "unable to resolve sourceBranchId : " + sourceBranchId );
                }
            }
            if ( !addedAssignments.isEmpty() ) {
                if ( isAdmin ) {
                    assigneeUser.setBranchAdmin( true );
                    user.setBranchAdmin( true );
                } else {
                    assigneeUser.setAgent( true );
                    user.setAgent( true );
                }
            }
            //Remove branch assignments
            for ( String sourceBranchId : deletedAssignments ) {
                if ( upload.getBranchSourceMapping().containsKey( sourceBranchId ) ) {
                    long branchId = upload.getBranchSourceMapping().get( sourceBranchId );
                    Branch branch = userManagementService.getBranchById( branchId );
                    if ( branch == null ) {
                        throw new UserAdditionException(
                            "unable to find branch with branchId : " + branchId + " and sourceId : " + sourceBranchId );
                    }
                    long regionId = branch.getRegion().getRegionId();
                    try {
                        int profilesMaster;
                        if ( isAdmin ) {
                            profilesMaster = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
                        } else {
                            profilesMaster = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
                        }
                        UserProfile profile = userProfileDao.findUserProfile( assigneeUser.getUserId(), branchId, regionId,
                            profilesMaster );
                        userManagementService.removeUserProfile( assigneeUser, adminUser, profile.getUserProfileId() );
                    } catch ( NoRecordsFetchedException e ) {
                        throw new UserAssignmentException( "Unable to fetch userProfile for user. Reason: ", e );
                    }
                } else {
                    throw new UserAssignmentException( "unable to resolve sourceBranchId : " + sourceBranchId );
                }
            }
        }
        LOG.info( "Method assignBranchesToUser() for user : " + user.getEmailId() + " isAdmin : " + isAdmin + " finished." );
        return assigneeUser;
    }


    /**
     * Method to assign/unassign regions to user
     * @param user
     * @param adminUser
     * @param assigneeUser
     * @param currentUserMap
     * @param upload
     * @return
     * @throws UserAssignmentException
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAdditionException
     */
    @Transactional
    @Override
    public User assignRegionsToUser( UserUploadVO user, User adminUser, User assigneeUser, Map<String, UserUploadVO> currentUserMap,
        HierarchyUpload upload, boolean isAdmin )
        throws UserAssignmentException, InvalidInputException, NoRecordsFetchedException, SolrException, UserAdditionException
    {
        LOG.info( "Method assignRegionsToUser started for user : " + user.getEmailId() );
        if ( ( !isAdmin && ( user.isSourceRegionIdModified() || user.isAssignedRegionsModified() ) )
            || ( isAdmin && user.isAssignedRegionsAdminModified() || user.isUserAdded() ) ) {
            //Compare the current user assignments and the new assignments to find the changes
            /*
             * Cases to handle:
             * 1. All new assignments
             * 2. Delete all assignments
             * 3. add some, delete some assignments
             */
            List<String> addedAssignments = new ArrayList<String>();
            List<String> deletedAssignments = new ArrayList<String>();

            if ( currentUserMap.containsKey( user.getSourceUserId() ) ) {
                //Existing user
                List<String> oldAssignments = new ArrayList<String>();
                List<String> newAssignments = new ArrayList<String>();
                if ( isAdmin ) {
                    oldAssignments = currentUserMap.get( user.getSourceUserId() ).getAssignedRegionsAdmin();
                    if ( user.getAssignedRegionsAdmin() != null ) {
                        newAssignments = user.getAssignedRegionsAdmin();
                    }
                } else {
                    oldAssignments = currentUserMap.get( user.getSourceUserId() ).getAssignedRegions();
                    if ( user.getAssignedRegions() != null ) {
                        newAssignments = user.getAssignedRegions();
                    }
                }

                if ( oldAssignments == null || oldAssignments.isEmpty() ) {
                    //All assignments are new
                    addedAssignments.addAll( newAssignments );
                } else if ( newAssignments == null || newAssignments.isEmpty() ) {
                    //Delete all assignments
                    deletedAssignments.addAll( oldAssignments );
                } else {
                    //find added and deleted assignments
                    List<String> tempOldAssignments = new ArrayList<String>();
                    tempOldAssignments.addAll( oldAssignments );
                    List<String> tempNewAssignments = new ArrayList<String>();
                    tempNewAssignments.addAll( newAssignments );

                    tempOldAssignments.removeAll( newAssignments );
                    deletedAssignments.addAll( tempOldAssignments );

                    tempNewAssignments.removeAll( oldAssignments );
                    addedAssignments.addAll( tempNewAssignments );
                }
            } else {
                //All assignments are new
                if ( isAdmin ) {
                    if ( user.getAssignedRegionsAdmin() != null ) {
                        addedAssignments.addAll( user.getAssignedRegionsAdmin() );
                    }
                } else {
                    if ( user.getAssignedRegions() != null ) {
                        addedAssignments.addAll( user.getAssignedRegions() );
                    }
                }
            }
            //Add region assignments
            for ( String sourceRegionId : addedAssignments ) {
                if ( upload.getRegionSourceMapping().containsKey( sourceRegionId ) ) {
                    long regionId = upload.getRegionSourceMapping().get( sourceRegionId );
                    organizationManagementService.assignRegionToUser( adminUser, regionId, assigneeUser, isAdmin );
                } else {
                    throw new UserAssignmentException( "unable to resolve sourceRegionId : " + sourceRegionId );
                }
            }
            if ( !addedAssignments.isEmpty() ) {
                if ( isAdmin ) {
                    assigneeUser.setRegionAdmin( true );
                    user.setRegionAdmin( true );
                } else {
                    assigneeUser.setAgent( true );
                    user.setAgent( true );
                }
            }
            //Remove region assignments
            for ( String sourceRegionId : deletedAssignments ) {
                if ( upload.getRegionSourceMapping().containsKey( sourceRegionId ) ) {
                    long regionId = upload.getRegionSourceMapping().get( sourceRegionId );
                    Branch branch = organizationManagementService.getDefaultBranchForRegion( regionId );
                    if ( branch == null ) {
                        throw new UserAdditionException(
                            "unable to find default branch for regionId : " + regionId + " and sourceId : " + sourceRegionId );
                    }
                    try {
                        int profilesMaster;
                        if ( isAdmin ) {
                            profilesMaster = CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID;
                        } else {
                            profilesMaster = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
                        }
                        UserProfile profile = userProfileDao.findUserProfile( assigneeUser.getUserId(), branch.getBranchId(),
                            regionId, profilesMaster );
                        userManagementService.removeUserProfile( assigneeUser, adminUser, profile.getUserProfileId() );
                    } catch ( NoRecordsFetchedException e ) {
                        throw new UserAssignmentException( "Unable to fetch userProfile for user. Reason: ", e );
                    }
                } else {
                    throw new UserAssignmentException( "unable to resolve sourceRegionId : " + sourceRegionId );
                }
            }
        }
        LOG.info( "Method assignRegionsToUser finished for user : " + user.getEmailId() );
        return assigneeUser;
    }

    @Transactional
    @Override
    public User modifyUser( UserUploadVO user, User adminUser, Map<String, UserUploadVO> currentUserMap, HierarchyUpload upload )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException,
        UndeliveredEmailException
    {
        LOG.info( "Method modifyUser() started for user : " + user.getEmailId() );
        if ( !( ( checkIfUserExistsWithinCompany( user, adminUser.getCompany() ) )
            || ( checkIfEmailIdExistsWithCompany( user.getEmailId(), adminUser.getCompany() ) ) ) ) {
            throw new UserAdditionException( "User : " + user.getEmailId() + "either belongs to a different company or doesn't exist" );
        }
        User assigneeUser = null;
        try {
            assigneeUser = userManagementService.getUserByUserId( user.getUserId() );
        } catch ( InvalidInputException e ){
            LOG.warn( e.getMessage() );
        }
        if ( assigneeUser == null ) {
            assigneeUser = userManagementService.getUserByEmailAddress( extractEmailId( user.getEmailId() ) );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "Couldn't find user : " + user.getSourceUserId() );
        }
        boolean isEmailModified = false;
        //check and modify user object
        if ( user.isEmailIdModified() && !user.isUserVerified() ) {
            assigneeUser.setEmailId( user.getEmailId() );
            assigneeUser.setLoginName( user.getEmailId() );
            isEmailModified = true;
        } else if ( user.isEmailIdModified() && user.isUserVerified() ) {
            throw new InvalidInputException( "User : " + user.getSourceUserId()
                + " is already verified. Email Addresses of verified users cannot be changed." );
        }
        if ( user.isFirstNameModified() ) {
            assigneeUser.setFirstName( user.getFirstName() );
        }
        if ( user.isLastNameModified() ) {
            assigneeUser.setLastName( user.getLastName() );
        }

        userDao.update( assigneeUser );

        if ( isEmailModified ) {
            // Modify email Ids in userprofile
            userProfileDao.updateEmailIdForUserProfile( assigneeUser.getUserId(), user.getEmailId() );
        }

        
        assignUser( user, adminUser, currentUserMap, upload );
        
        //send verification mail if needed
        if ( user.isSendMail() ) {
            resendVerificationMail( user );
        }
        
        //Add user to Solr
        solrSearchService.addUserToSolr( assigneeUser );

        return assigneeUser;
    }
    
    
    /**
     * Method to check if a user exists within a company
     * @param user
     * @param company
     * @return
     * @throws InvalidInputException
     */
    boolean checkIfUserExistsWithinCompany( UserUploadVO user, Company company ) throws InvalidInputException
    {
        if ( user.getUserId() > 0l ) {
            User foundUser = userManagementService.getUserByUserId( user.getUserId() );
            if ( foundUser == null ) {
                return false;
            } else if ( foundUser.getCompany().getCompanyId() == company.getCompanyId() ) {
                return true;
            }
        }
        return false;
    }
    

    /**
     * Method to send/resend user verification mail
     * @param user
     * @param userUpload
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     * @throws NoRecordsFetchedException 
     */
    @Transactional
    void resendVerificationMail( UserUploadVO userUpload ) throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
    {
        LOG.info( "Method to resend verification mail started for user : " + userUpload.getSourceUserId() );
        //Resend verification mail if sendMail is true
        User user = userManagementService.getUserByEmailAddress( extractEmailId( userUpload.getEmailId() ) );
        if ( userUpload.isSendMail() ) {
            String profileName = userManagementService.getUserSettings( user.getUserId() ).getProfileName();
            userManagementService.sendRegistrationCompletionLink( user.getEmailId(), user.getFirstName(), user.getLastName(),
                user.getCompany().getCompanyId(), profileName, user.getLoginName(), false );
            userUpload.setSendMail( false );
        }
        LOG.info( "Method to resend verification mail finished for user : " + userUpload.getSourceUserId() );
    }
    

    /**
     * Method to assign/unassign user to regions and branches
     * @param user
     * @param adminUser
     * @param currentUserMap
     * @param upload
     * @return
     * @throws UserAdditionException
     * @throws InvalidInputException
     * @throws SolrException
     * @throws NoRecordsFetchedException
     * @throws UserAssignmentException
     */
    @Transactional
    @Override
    public User assignUser( UserUploadVO user, User adminUser, Map<String, UserUploadVO> currentUserMap, HierarchyUpload upload )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        LOG.info( "Method assignUser() started for user : " + user.getEmailId() );
        if ( !( ( checkIfUserExistsWithinCompany( user, adminUser.getCompany() ) )
            || ( checkIfEmailIdExistsWithCompany( user.getEmailId(), adminUser.getCompany() ) ) ) ) {
            throw new UserAdditionException( "User : " + user.getEmailId() + " belongs to a different company" );
        }
        User assigneeUser = null;
        try {
        assigneeUser = userManagementService.getUserByUserId( user.getUserId() );
        } catch ( InvalidInputException e ) {
            LOG.warn( e.getMessage() );
        }
        if ( assigneeUser == null ) {
            assigneeUser = userManagementService.getUserByEmailAddress(  extractEmailId( user.getEmailId() )  );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "Couldn't find user : " + user.getSourceUserId() );
        }
        if ( ( user.getAssignedBranches() == null || user.getAssignedBranches().isEmpty() )
            && ( user.getAssignedRegions() == null || user.getAssignedRegions().isEmpty() )
            && ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() )
            && ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() ) ) {
            //Assign user to company
            try {
                Region region = organizationManagementService.getDefaultRegionForCompany( adminUser.getCompany() );
                Branch branch = organizationManagementService.getDefaultBranchForRegion( region.getRegionId() );
                //userManagementService.assignUserToRegion( adminUser, assigneeUser.getUserId(), region.getRegionId() );
                //userManagementService.assignUserToBranch( adminUser, assigneeUser.getUserId(), branch.getBranchId() );
                organizationManagementService.assignBranchToUser( adminUser, branch.getBranchId(), region.getRegionId(),
                    assigneeUser, false );
                assigneeUser.setAgent( true );
                user.setAgent( true );
                user.setAssignToCompany( true );
                user.setBranchAdmin( false );
                user.setRegionAdmin( false );
                assigneeUser.setBranchAdmin( false );
                assigneeUser.setRegionAdmin( false );
            } catch ( InvalidInputException e ) {
                if ( e.getMessage() == DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS ) {
                    LOG.debug( "User assignment already exists" );
                }
            }
        }
        //Agent assignments
        assigneeUser = assignBranchesToUser( user, adminUser, assigneeUser, currentUserMap, upload, false );
        assigneeUser = assignRegionsToUser( user, adminUser, assigneeUser, currentUserMap, upload, false );
        //Admin assignments
        assigneeUser = assignBranchesToUser( user, adminUser, assigneeUser, currentUserMap, upload, true );
        assigneeUser = assignRegionsToUser( user, adminUser, assigneeUser, currentUserMap, upload, true );

        if ( ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() ) ) {
            assigneeUser.setBranchAdmin( false );
            user.setBranchAdmin( false );
        }
        if ( ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() ) ) {
            assigneeUser.setRegionAdmin( false );
            user.setRegionAdmin( false );
        }
        LOG.info( "Method assignUser() finished for user : " + user.getEmailId() );
        return assigneeUser;
    }


    boolean checkIfEmailIdExists( String emailId, Company company ) throws InvalidInputException
    {
        boolean status = false;
        emailId = extractEmailId( emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "EmailId is empty" );
        }
        try {
            userManagementService.getUserByEmailAddress( emailId );
            status = true;
        } catch ( NoRecordsFetchedException e ) {
            status = false;
        }
        return status;
    }


    boolean checkIfEmailIdExistsWithCompany( String emailId, Company company ) throws InvalidInputException
    {
        boolean status = false;
        emailId = extractEmailId( emailId );
        if ( emailId == null || emailId.isEmpty() ) {
            throw new InvalidInputException( "EmailId is empty" );
        }
        try {
            User user = userManagementService.getUserByEmailAddress( emailId );
            if ( user.getCompany().getCompanyId() == company.getCompanyId() ) {
                status = true;
            }
        } catch ( NoRecordsFetchedException e ) {
            status = false;
        }
        return status;
    }


    public static String[] removeElements( String[] input, String deleteMe )
    {
        List<String> result = new LinkedList<String>();

        for ( String item : input )
            if ( !deleteMe.equals( item ) )
                result.add( item );

        String[] modifiedArray = result.toArray( new String[result.size()] );
        return modifiedArray;
    }


    String extractEmailId( String emailId )
    {
        if ( emailId.contains( "\"" ) ) {
            emailId = emailId.replace( "\"", "" );
        }
        String firstName = "";
        String lastName = "";
        String toRemove = null;
        if ( emailId.indexOf( "@" ) != -1 && emailId.indexOf( "." ) != -1 ) {
            if ( emailId.contains( " " ) ) {
                String[] userArray = emailId.split( " " );
                String[] userInformation = removeElements( userArray, "" );
                List<String> tempList = new LinkedList<String>();
                for ( String str : userInformation ) {
                    tempList.add( str );
                }
                String tempString = "";
                for ( int i = 0; i < tempList.size(); i++ ) {

                    LOG.debug( "removing extra spaces " );
                    if ( tempList.get( i ).equalsIgnoreCase( "<" ) ) {
                        if ( i + 1 < tempList.size() ) {
                            if ( !tempList.get( i + 1 ).contains( "<" ) ) {
                                tempString = tempList.get( i ).concat( tempList.get( i + 1 ) );

                                toRemove = tempList.get( i + 1 );
                                if ( i + 2 < tempList.size() ) {

                                    if ( tempList.get( i + 2 ).equalsIgnoreCase( ">" ) ) {
                                        tempString = tempString.concat( tempList.get( i + 2 ) );


                                    }
                                }
                            }
                        }
                    } else if ( tempList.get( i ).equalsIgnoreCase( ">" ) ) {
                        if ( !tempList.get( i - 1 ).contains( ">" ) ) {
                            if ( tempString.isEmpty() ) {
                                tempString = tempList.get( i - 1 ).concat( tempList.get( i ) );
                                toRemove = tempList.get( i - 1 );
                            }

                        }
                    }

                }
                if ( !tempString.isEmpty() ) {
                    tempList.add( tempString );
                }
                Iterator<String> it = tempList.iterator();
                while ( it.hasNext() ) {
                    String iteratedValue = it.next();
                    if ( iteratedValue.equalsIgnoreCase( "<" ) || iteratedValue.equalsIgnoreCase( ">" ) ) {
                        it.remove();
                    }
                    if ( toRemove != null ) {
                        if ( iteratedValue.equalsIgnoreCase( toRemove ) ) {
                            it.remove();
                        }
                    }
                }
                userInformation = tempList.toArray( new String[tempList.size()] );
                if ( userInformation.length >= 3 ) {
                    LOG.debug( "This contains middle name as well" );
                    for ( int i = 0; i < userInformation.length - 1; i++ ) {
                        firstName = firstName + userInformation[i] + " ";
                    }
                    firstName = firstName.trim();
                    lastName = userInformation[userInformation.length - 1];
                    if ( lastName.contains( "<" ) ) {
                        emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                        lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                        if ( lastName.equalsIgnoreCase( "" ) ) {
                            lastName = userInformation[userInformation.length - 2];
                            if ( firstName.contains( lastName ) ) {
                                firstName = firstName.substring( 0, firstName.indexOf( lastName ) );
                            }
                        }
                    }

                } else if ( userInformation.length == 2 ) {
                    firstName = userInformation[0];
                    lastName = userInformation[1];
                    if ( lastName.contains( "<" ) ) {
                        emailId = lastName.substring( lastName.indexOf( "<" ) + 1, lastName.length() - 1 );
                        lastName = lastName.substring( 0, lastName.indexOf( "<" ) );
                    }
                }
            } else {
                LOG.debug( "Contains no space hence wont have a last name" );
                lastName = null;
                if ( emailId.contains( "<" ) ) {
                    firstName = emailId.substring( 0, emailId.indexOf( "<" ) );
                    if ( firstName.equalsIgnoreCase( "" ) ) {
                        firstName = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( "@" ) );
                    }
                    emailId = emailId.substring( emailId.indexOf( "<" ) + 1, emailId.indexOf( ">" ) );

                } else {
                    LOG.debug( "This doesnt contain a first name and last name" );
                    firstName = emailId.substring( 0, emailId.indexOf( "@" ) );
                }

            }
        }
        return emailId;
    }


    /**
     * Method to upload users from hierarchy upload object
     * @param upload
     * @param adminUser
     */
    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    @Override
    public void uploadUsers( HierarchyUpload upload, User adminUser, List<String> errorList )
    {
        LOG.debug( "Uploading users to database" );
        
        //Keep a count of added and modified regions
        int addedUsers = 0;
        int modifiedUsers = 0;
        int addedUsersOldCount = 0;
        int modifiedUsersOldCount = 0;
        Long nextTime = System.currentTimeMillis();
        
        UploadStatus addedUsersStatus = createUploadStatus( adminUser, CommonConstants.UPLOAD_ADDED_USERS, addedUsers );
        
        
        UploadStatus modifiedUsersStatus = createUploadStatus( adminUser, CommonConstants.UPLOAD_MODIFIED_USERS, modifiedUsers );
        
        Map<String, UserUploadVO> currentUserMap = new HashMap<String, UserUploadVO>();
        try {
            HierarchyUpload currentUpload = hierarchyUploadDao
                .getHierarchyUploadByCompany( adminUser.getCompany().getCompanyId() );
            if ( currentUpload != null && currentUpload.getUsers() != null ) {
                for ( UserUploadVO user : currentUpload.getUsers() ) {
                    currentUserMap.put( user.getSourceUserId(), user );
                }
            }
            List<UserUploadVO> usersToUpload = upload.getUsers();
            if ( usersToUpload == null || usersToUpload.isEmpty() ) {
                return;
            }
            for ( UserUploadVO userToBeUploaded : usersToUpload ) {
                User user = null;
                //Mask email ID if necessary
                String emailId = userToBeUploaded.getEmailId();
                if ( CommonConstants.YES_STRING.equals( maskEmail ) ) {
                    emailId = utils.maskEmailAddress( emailId );
                }
                userToBeUploaded.setEmailId( emailId );
                if ( !userToBeUploaded.isUserAdded() && !userToBeUploaded.isUserModified() ) {
                    if ( userToBeUploaded.isSendMail() ) {
                        resendVerificationMail( userToBeUploaded );
                    }
                    continue;
                }
                
                if ( userToBeUploaded.isUserModified() ) {
                    user = modifyUser( userToBeUploaded, adminUser, currentUserMap, currentUpload );
                    modifiedUsers += 1;
                } else if ( userToBeUploaded.isUserAdded() ) {
                    // add user
                    user = addUser( userToBeUploaded, adminUser, currentUserMap, upload );
                    addedUsers += 1;
                }
                userToBeUploaded.setUserId( user.getUserId() );
                updateUserSettingsInMongo( user, userToBeUploaded );
                //map the history records
                mapUserModificationHistory( userToBeUploaded, user );
                //map the id mapping
                if ( userToBeUploaded.getSourceUserId() != null && !userToBeUploaded.getSourceUserId().isEmpty() ) {
                    upload.getUserSourceMapping().put( userToBeUploaded.getSourceUserId(), userToBeUploaded.getUserId() );
                }
                //Store the updated userUploads in upload
                upload.setUsers( usersToUpload );
                
                if ( System.currentTimeMillis() > nextTime ) {
                    //Update count in upload_status
                    if ( addedUsers > addedUsersOldCount ) {
                        addedUsersStatus = updateUploadStatus( addedUsersStatus, CommonConstants.UPLOAD_ADDED_USERS,
                            addedUsers );
                        addedUsersOldCount = addedUsers;
                    }
                    if ( modifiedUsers > modifiedUsersOldCount ) {
                        modifiedUsersStatus = updateUploadStatus( modifiedUsersStatus,
                            CommonConstants.UPLOAD_MODIFIED_USERS, modifiedUsers );
                        modifiedUsersOldCount = modifiedUsers;
                    }
                    nextTime = nextTime + 15 * 1000;
                }
            }
        } catch ( Exception e ) {
            // Add error records
            e.printStackTrace();
            errorList.add( e.getMessage() );
            //Update upload status to show the latest counts
            addedUsersStatus = updateUploadStatusForError( addedUsersStatus, CommonConstants.UPLOAD_ADDED_USERS,
                addedUsers );
            modifiedUsersStatus = updateUploadStatusForError( modifiedUsersStatus, CommonConstants.UPLOAD_MODIFIED_USERS,
                modifiedUsers );
        }
        //Update upload status to show the latest counts
        addedUsersStatus = updateUploadStatusToDone( addedUsersStatus, CommonConstants.UPLOAD_ADDED_USERS, addedUsers );
        modifiedUsersStatus = updateUploadStatusToDone( modifiedUsersStatus, CommonConstants.UPLOAD_MODIFIED_USERS,
            modifiedUsers );
        LOG.debug( "Finished uploading users to the database" );
    }


    /**
     * Method to add user to MySQL
     * @param user
     * @param adminUser
     * @param currentUserMap
     * @param upload
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     * @throws UserAdditionException
     */
    @Transactional
    User addUser( UserUploadVO user, User adminUser, Map<String, UserUploadVO> currentUserMap, HierarchyUpload upload )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException, UserAdditionException
    {
        LOG.info( "Method addUser() started for user : " + user.getEmailId() );
        User uploadedUser = null;
        List<User> userList = new ArrayList<User>();
        if ( checkIfEmailIdExists( user.getEmailId(), adminUser.getCompany() ) ) {
            throw new UserAdditionException( "The user "+user.getSourceUserId()+" already exists" );
        }
        //Mask email address if needed
        String emailId = user.getEmailId();
        if ( user.getEmailId() == null || user.getEmailId().isEmpty() ) {
            throw new InvalidInputException( "User email ID cannot be null" );
        }

        user.setEmailId( user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ) + " <" + emailId
            + ">" );

        //Add user and call assignUser method
        //Add user
        //If sendMail = true, then you need to send the mail. So holdSendingMail should be false.
        Map<String, List<User>> resultMap = organizationManagementService
            .getUsersFromEmailIdsAndInvite( new String[] { user.getEmailId() }, adminUser, false, user.isSendMail() );
        //Reset sendMail to false
        user.setSendMail( false );
        
        if ( resultMap != null ) {
            userList = (List<User>) resultMap.get( CommonConstants.VALID_USERS_LIST );
            if ( userList != null && !userList.isEmpty() ) {
                uploadedUser = userList.get( 0 );
            } else {
                throw new UserAdditionException( "Unable to add user with emailID : " + user.getEmailId() );
            }
        }
        LOG.debug( "Added user with email : " + user.getEmailId() );
        //Assign user
        assignUser( user, adminUser, currentUserMap, upload );

        LOG.info( "Method addUser() finished for user : " + user.getEmailId() );
        return uploadedUser;
    }


    /**
     * Method to update agentSettings of user in mongo
     * @param user
     * @param userUploadVO
     * @throws InvalidInputException
     */
    void updateUserSettingsInMongo( User user, UserUploadVO userUploadVO ) throws InvalidInputException
    {
        LOG.debug( "Inside method updateUserSettingsInMongo " );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( user.getUserId() );
        if ( agentSettings == null ) {
            throw new InvalidInputException(
                "No company settings found for user " + user.getUsername() + " " + user.getUserId() );
        } else {
            ContactDetailsSettings contactDetailsSettings = agentSettings.getContact_details();
            if ( contactDetailsSettings == null ) {
                contactDetailsSettings = new ContactDetailsSettings();
            }
            ContactNumberSettings contactNumberSettings = contactDetailsSettings.getContact_numbers();
            if ( contactNumberSettings == null ) {
                contactNumberSettings = new ContactNumberSettings();
            }
            
            //Change email if user is not verified
            if ( userUploadVO.isEmailIdModified() && !userUploadVO.isUserVerified() ) {
                MailIdSettings mail_ids = contactDetailsSettings.getMail_ids();
                if ( mail_ids == null ) {
                    mail_ids = new MailIdSettings();
                }
                mail_ids.setWork( userUploadVO.getEmailId() );
            }
            
            contactNumberSettings.setWork( userUploadVO.getPhoneNumber() );
            contactDetailsSettings.setContact_numbers( contactNumberSettings );
            contactDetailsSettings.setAbout_me( userUploadVO.getAboutMeDescription() );
            contactDetailsSettings.setTitle( userUploadVO.getTitle() );
            String fullName = "";
            if ( userUploadVO.getFirstName() != null && !userUploadVO.getFirstName().isEmpty() ) {
                contactDetailsSettings.setFirstName( userUploadVO.getFirstName() );
                fullName = userUploadVO.getFirstName();
            }
            if ( userUploadVO.getLastName() != null && !userUploadVO.getLastName().isEmpty() ) {
                contactDetailsSettings.setLastName( userUploadVO.getLastName() );
                fullName += " " + userUploadVO.getLastName();
            }
            if ( !fullName.isEmpty() ) {
                contactDetailsSettings.setName( fullName );
            }
            WebAddressSettings webAddressSettings = contactDetailsSettings.getWeb_addresses();
            if ( webAddressSettings == null ) {
                webAddressSettings = new WebAddressSettings();
            }
            webAddressSettings.setWork( userUploadVO.getWebsiteUrl() );
            contactDetailsSettings.setWeb_addresses( webAddressSettings );
            agentSettings.setContact_details( contactDetailsSettings );

            if ( userUploadVO.getLicense() != null && !userUploadVO.getLicense().isEmpty() ) {
                Licenses licenses = agentSettings.getLicenses();
                if ( licenses == null ) {
                    licenses = new Licenses();
                }
                licenses.setAuthorized_in( getAllStateLicenses( userUploadVO.getLicense() ) );
                agentSettings.setLicenses( licenses );
                if ( licenses != null && licenses.getAuthorized_in() != null && !licenses.getAuthorized_in().isEmpty() ) {
                    organizationUnitSettingsDao.updateParticularKeyAgentSettings(
                        MongoOrganizationUnitSettingDaoImpl.KEY_LICENCES, licenses, agentSettings );
                }
            }
            agentSettings.setDisclaimer( userUploadVO.getLegalDisclaimer() );

            profileManagementService.updateAgentContactDetails( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                agentSettings, contactDetailsSettings );

            if ( userUploadVO.getLegalDisclaimer() != null ) {
                organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
                    MongoOrganizationUnitSettingDaoImpl.KEY_DISCLAIMER, userUploadVO.getLegalDisclaimer(), agentSettings,
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION );
            }

            if ( userUploadVO.getUserPhotoUrl() != null ) {

                updateProfileImageForAgent( userUploadVO.getUserPhotoUrl(), agentSettings );
                /*
                 * profileManagementService.updateProfileImage(
                 * MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, agentSettings,
                 * userUploadVO.getUserPhotoUrl() );
                 */
            }
        }
    }


    private List<String> getAllStateLicenses( String licenses )
    {
        String toRemove = "Licensed State(s):";
        List<String> authorizedIn = new ArrayList<String>();
        if ( licenses.indexOf( toRemove ) != -1 ) {
            licenses = licenses.substring( licenses.indexOf( "Licensed State(s):" ) + toRemove.length(), licenses.length() );
        }
        licenses = licenses.trim();
        authorizedIn.add( licenses );
        return authorizedIn;
    }


    private void updateProfileImageForAgent( String userPhotoUrl, AgentSettings agentSettings ) throws InvalidInputException
    {
        LOG.debug( "Uploading for agent " + agentSettings.getIden() + " with photo: " + userPhotoUrl );
        // TODO: Check if the image is local or online. In case it is local, then we should
        // upload that to S3 and then link the same
        /*
         * String profileImageUrl = null; if
         * (userPhotoUrl.trim().matches(CommonConstants.URL_REGEX)) {
         * LOG.debug("Profile photo is publicaly available"); profileImageUrl = userPhotoUrl; } else
         * { LOG.debug("User photo is locally available. Uploading the image to cloud"); File
         * imageFile = new File(userPhotoUrl); String imageName =
         * userPhotoUrl.substring(userPhotoUrl.lastIndexOf(CommonConstants.FILE_SEPARATOR)); String
         * profileImageName = fileUploadService.fileUploadHandler(imageFile, imageName);
         * profileImageUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonImageBucket +
         * CommonConstants.FILE_SEPARATOR + profileImageName; }
         */
        profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            agentSettings, userPhotoUrl );
    }
    

    /**
     * Method to search for initiated hierarchy upload entries in the upload status table
     * @return
     * @throws NoRecordsFetchedException
     */
    @Override
    public List<UploadStatus> findInitiatedHierarchyUploads() throws NoRecordsFetchedException
    {
        LOG.info( "Searching for initiated hierarchy upload entries" );
        Map<String, Object> queries = new HashMap<String, Object>();
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.HIERARCHY_UPLOAD_ENTITY_INITIATED );
        queries.put( CommonConstants.MESSAGE, CommonConstants.UPLOAD_MSG_INITIATED );
        List<UploadStatus> initiatedUploads = uploadStatusDao.findByKeyValue( UploadStatus.class, queries );
        if ( initiatedUploads == null || initiatedUploads.isEmpty() ) {
            throw new NoRecordsFetchedException( "No hierarchy upload entries found" );
        }
        return initiatedUploads;
    }
    
    
    /**
     * Method to update an uploadStatus entry
     * @param uploadStatus
     */
    @Override
    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    public void updateUploadStatus( UploadStatus uploadStatus )
    {
        LOG.info( "Updating uploadStatus" );
        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        uploadStatus.setModifiedOn( currentTime );
        uploadStatusDao.update( uploadStatus );
    }
    
    
    
    @Override
    @Transactional
    public void updateUploadStatusToNoUpload(UploadStatus uploadStatus)
    {
        LOG.info( "Upadting uploadStatus to no upload" );
        uploadStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_NO_UPLOAD );
        uploadStatusDao.update( uploadStatus );
    }
    
    
    /**
     * Method to add an uploadStatus entry
     * @param uploadStatus
     */
    @Override
    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    public void addUploadStatusEntry( UploadStatus uploadStatus )
    {
        LOG.info( "Adding uploadStatus entry" );
        Timestamp currentTime = new Timestamp( System.currentTimeMillis() );
        uploadStatus.setModifiedOn( currentTime );
        uploadStatus.setCreatedOn( currentTime );
        uploadStatusDao.save( uploadStatus );
    }
    
    
    /**
     * Method to fetch the latest upload status
     * @param company
     * @return
     */
    @Override
    public UploadStatus fetchLatestUploadStatus( Company company )
    {
        LOG.info( "Method to fetch latest uploadStatus started for company " + company.getCompany() );
        UploadStatus latestStatus = null;

        //Get a list of all the statuses
        List<UploadStatus> uploadStatuses = uploadStatusDao.findByColumn( UploadStatus.class, "company.companyId",
            company.getCompanyId() );
        if ( uploadStatuses != null && !uploadStatuses.isEmpty() ) {
            for ( UploadStatus uploadStatus : uploadStatuses ) {
                latestStatus = newerStatus( latestStatus, uploadStatus );
            }
        }

        return latestStatus;
    }
    
    
    /**
     * Method to fetch all the upload statuses for a company
     * @param company
     * @return
     */
    @Override
    public List<UploadStatus> fetchUploadStatusForCompany( Company company )
    {
        LOG.info( "Method to fetch all the upload statuses for company : " + company.getCompany() + " started" );

        //Get a list of all the statuses
        List<UploadStatus> uploadStatuses = uploadStatusDao.findByColumn( UploadStatus.class, "company.companyId",
            company.getCompanyId() );
        List<UploadStatus> returnedStatuses = null;
        if ( uploadStatuses != null && !uploadStatuses.isEmpty() ) {
            returnedStatuses = new ArrayList<UploadStatus>();
            for ( UploadStatus uploadStatus : uploadStatuses ) {
                if ( !uploadStatus.getMessage().endsWith( "0" ) ) {
                    returnedStatuses.add( uploadStatus );
                }
            }
        }
        return returnedStatuses;
    }
    

    /**
     * Method to select the newer status
     * @param currentStatus
     * @param newStatus
     * @return
     */
    UploadStatus newerStatus( UploadStatus currentStatus, UploadStatus newStatus )
    {
        //Least status will always be 0 or 1. Can't have both

        //When we get the first status
        if ( currentStatus == null ) {
            return newStatus;
        }
        if ( currentStatus.getStatus() > newStatus.getStatus() ) {
            return currentStatus;
        } else {
            return newStatus;
        }
    }
    
    
    /**
     * Method to determine the latest status
     * @param uploadStatuses
     * @return
     */
    @Override
    public UploadStatus highestStatus( List<UploadStatus> uploadStatuses )
    {
        UploadStatus latestStatus = null;
        if ( uploadStatuses != null && !uploadStatuses.isEmpty() ) {
            latestStatus = uploadStatuses.get( 0 );
            for ( UploadStatus uploadStatus : uploadStatuses ) {
                latestStatus = newerStatus( latestStatus, uploadStatus );
            }
            return latestStatus;
        }
        return null;
    }
    
    
    
    /**
     * Method to initiate hierarchy upload
     * @param adminUser
     */
    @Override
    public void addNewUploadRequest( User adminUser, boolean isAppend )
    {
        //Remove old records for the company
        List<String> conditions = new ArrayList<String>();
        conditions.add( "company.companyId = " + adminUser.getCompany().getCompanyId() );
        uploadStatusDao.deleteByCondition( "UploadStatus", conditions );

        //Add new entry
        UploadStatus newStatus = new UploadStatus();
        newStatus.setAdminUserId( adminUser.getUserId() );
        newStatus.setCompany( adminUser.getCompany() );
        newStatus.setMessage( CommonConstants.UPLOAD_MSG_INITIATED );
        newStatus.setStatus( CommonConstants.HIERARCHY_UPLOAD_ENTITY_INITIATED );
        if ( isAppend ) {
            newStatus.setUploadMode( CommonConstants.UPLOAD_MODE_APPEND );
        }
        addUploadStatusEntry( newStatus );
    }
    
    
    /**
     * Method to create an upload status initiated entry
     * @param adminUser
     * @param message
     * @param value
     * @return
     */
    UploadStatus createUploadStatus(User adminUser, String message, int value)
    {
        UploadStatus status = new UploadStatus();
        status.setAdminUserId( adminUser.getUserId() );
        status.setCompany( adminUser.getCompany() );
        status.setMessage( message + value );
        status.setStatus( CommonConstants.HIERARCHY_UPLOAD_ENTITY_INITIATED );
        addUploadStatusEntry( status );
        return status;
    }
    
    
    /**
     * Method to update the message of an existing upload status entry
     * @param status
     * @param message
     * @param value
     * @return
     */
    UploadStatus updateUploadStatus( UploadStatus status, String message, int value )
    {
        status.setStatus( CommonConstants.HIERARCHY_UPLOAD_ENTITY_STARTED );
        status.setMessage( message + value );
        updateUploadStatus( status );
        return status;
    }
    
    
    /**
     * Method to update the message on error for an upload status entry
     * @param status
     * @param value
     * @return
     */
    UploadStatus updateUploadStatusForError( UploadStatus status, String message, int value )
    {
        status.setStatus( CommonConstants.HIERARCHY_UPLOAD_ENTITY_ERROR );
        status.setMessage( message + value );
        updateUploadStatus( status );
        return status;
    }
    
    
    /**
     * Method to update an upload status entry to done
     * @param status
     * @param value
     * @return
     */
    UploadStatus updateUploadStatusToDone( UploadStatus status, String message, int value )
    {
        status.setStatus( CommonConstants.HIERARCHY_UPLOAD_ENTITY_DONE );
        status.setMessage( message + value );
        updateUploadStatus( status );
        return status;
    }
    
    
    /**
     * Method to fetch the hierarchy to be uploaded
     * @param company
     * @return
     * @throws InvalidInputException
     */
    @Override
    public HierarchyUpload fetchHierarchyToBeUploaded( Company company ) throws InvalidInputException
    {
        if ( company == null ) {
            throw new InvalidInputException( "Company object is empty" );
        }
        LOG.info( "Fetching hierarchy to be uploaded for company : " + company.getCompany() );
        return hierarchyUploadDao.getUploadHierarchyDetailsByCompany( company.getCompanyId() );
    }
    
    /**
     * Used to get user from userId
     * 
     * @return
     */
    @Transactional
    @Override
    public User getUser( long userId )
    {
        User user = userDao.findById( User.class, userId );
        return user;
    }

    @Override
    public void saveHierarchyUploadInMongo( HierarchyUpload upload ) throws InvalidInputException
    {
        hierarchyUploadDao.saveUploadHierarchyDetails( upload );
    }
}
