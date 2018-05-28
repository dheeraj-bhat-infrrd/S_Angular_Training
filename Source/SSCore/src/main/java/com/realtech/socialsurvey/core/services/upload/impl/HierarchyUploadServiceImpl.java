package com.realtech.socialsurvey.core.services.upload.impl;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.UserProfileDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUploadAggregate;
import com.realtech.socialsurvey.core.entities.HierarchyUploadIntermediate;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.MailIdSettings;
import com.realtech.socialsurvey.core.entities.MiscValues;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.StringSetUploadHistory;
import com.realtech.socialsurvey.core.entities.StringUploadHistory;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyUploadService;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;


@Component
public class HierarchyUploadServiceImpl implements HierarchyUploadService
{

    private static Logger LOG = LoggerFactory.getLogger( HierarchyUploadServiceImpl.class );
    private static final String REGION_SHEET = "Regions";
    private static final String BRANCH_SHEET = "Offices";
    private static final String USERS_SHEET = "Users";
    private static final char TYPE_USER = 'U';
    private static final char TYPE_BRANCH = 'O';
    private static final char TYPE_REGION = 'R';

    private static final List<String> regionHeaderList = Arrays.asList( CommonConstants.CHR_REGION_REGION_ID,
        CommonConstants.CHR_REGION_REGION_NAME, CommonConstants.CHR_ADDRESS_1, CommonConstants.CHR_ADDRESS_2,
        CommonConstants.CHR_CITY, CommonConstants.CHR_STATE, CommonConstants.CHR_ZIP );

    private static final List<String> branchHeaderList = Arrays.asList( CommonConstants.CHR_BRANCH_BRANCH_ID,
        CommonConstants.CHR_BRANCH_BRANCH_NAME, CommonConstants.CHR_REGION_REGION_ID, CommonConstants.CHR_ADDRESS_1,
        CommonConstants.CHR_ADDRESS_2, CommonConstants.CHR_CITY, CommonConstants.CHR_STATE, CommonConstants.CHR_ZIP );

    private static final List<String> userHeaderList = Arrays.asList( CommonConstants.CHR_USERS_USER_ID,
        CommonConstants.CHR_USERS_FIRST_NAME, CommonConstants.CHR_USERS_LAST_NAME, CommonConstants.CHR_USERS_TITLE,
        CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS, CommonConstants.CHR_USERS_REGION_ASSIGNMENTS,
        CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE, CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE,
        CommonConstants.CHR_USERS_EMAIL, CommonConstants.CHR_USERS_PHONE, CommonConstants.CHR_USERS_WEBSITE,
        CommonConstants.CHR_USERS_LICENSE, CommonConstants.CHR_USERS_LEGAL_DISCLAIMER, CommonConstants.CHR_USERS_PHOTO,
        CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION );

    private static final int REGION_ID_INDEX = 0;
    private static final int REGION_NAME_INDEX = 1;
    private static final int REGION_ADDRESS1_INDEX = 2;
    private static final int REGION_ADDRESS2_INDEX = 3;
    private static final int REGION_CITY_INDEX = 4;
    private static final int REGION_STATE_INDEX = 5;
    private static final int REGION_ZIP_INDEX = 6;

    private static final int BRANCH_ID_INDEX = 0;
    private static final int BRANCH_NAME_INDEX = 1;
    private static final int BRANCH_REGION_ID_INDEX = 2;
    private static final int BRANCH_ADDRESS1_INDEX = 3;
    private static final int BRANCH_ADDRESS2_INDEX = 4;
    private static final int BRANCH_CITY_INDEX = 5;
    private static final int BRANCH_STATE_INDEX = 6;
    private static final int BRANCH_ZIP_INDEX = 7;

    private static final int USER_ID_INDEX = 0;
    private static final int USER_FIRST_NAME_INDEX = 1;
    private static final int USER_LAST_NAME_INDEX = 2;
    private static final int USER_TITLE_INDEX = 3;
    private static final int USER_BRANCH_ID_INDEX = 4;
    private static final int USER_REGION_ID_INDEX = 5;

    private static final int USER_BRANCH_ID_ADMIN_INDEX = 6;
    private static final int USER_REGION_ID_ADMIN_INDEX = 7;
    private static final int USER_EMAIL_INDEX = 8;
    private static final int USER_PHONE_NUMBER = 9;
    private static final int USER_WEBSITE = 10;
    private static final int USER_LICENSES = 11;
    private static final int USER_LEGAL_DISCLAIMER = 12;
    private static final int USER_PHOTO_PROFILE_URL = 13;
    private static final int USER_ABOUT_ME_DESCRIPTION = 14;
    private static final int USER_SEND_EMAIL = 15;

    @Autowired
    private HierarchyDownloadService hierarchyDownloadService;

    @Autowired
    private HierarchyUploadStatusUpdate hierarchyUploadStatusUpdate;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserProfileDao userProfileDao;

    @Autowired
    private CompanyDao companyDao;


    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private Utils utils;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private SettingsLocker settingsLocker;

    @Autowired
    private WorkbookOperations workbookOperations;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;
    
    @Value ( "${CDN_PATH}")
    private String cdnUrl;


    // V2.0 : BEGIN

    @Override
    public boolean processHierarchyUploadXlsx( ParsedHierarchyUpload parsedHierarchyUpload ) throws InvalidInputException
    {
        LOG.debug( "method processHierarchyUploadXlsx() started" );
        if ( parsedHierarchyUpload == null ) {
            throw new InvalidInputException( "upload object passed is null" );
        } else if ( parsedHierarchyUpload.getCompanyId() <= 0 ) {
            throw new InvalidInputException( "company ID invalid" );
        }

        // initialize general error list
        parsedHierarchyUpload.setGeneralErrors( new ArrayList<String>() );

        // change the status of upload process to "verifying"
        parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_VERIFING );

        // start the update thread
        hierarchyUploadStatusUpdate.updateParsedHierarchyUpload( System.currentTimeMillis(), 5000, parsedHierarchyUpload );

        XSSFWorkbook hierarchyWorkbook = null;


        //get the workbook from the amazon cloud
        try {
            hierarchyWorkbook = generateWorkBookFromXlsx( parsedHierarchyUpload.getFileURI() );
        } catch ( InvalidInputException unableToObtainWorkbookObject ) {
            return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload,
                "Unable to obtain xlsx file, please try again" );
        }

        // if unable to recognize even one sheet, return with errors
        if ( hierarchyWorkbook.getSheet( REGION_SHEET ) == null && hierarchyWorkbook.getSheet( BRANCH_SHEET ) == null
            && hierarchyWorkbook.getSheet( USERS_SHEET ) == null ) {
            return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload,
                "Unable to Detect xlsx sheets, please add valid xlsx sheets" );
        }

        // ready the iterators
        Iterator<Row> regionsIterator = hierarchyWorkbook.getSheet( REGION_SHEET ) != null
            ? hierarchyWorkbook.getSheet( REGION_SHEET ).rowIterator()
            : null;

        Iterator<Row> branchesIterator = hierarchyWorkbook.getSheet( BRANCH_SHEET ) != null
            ? hierarchyWorkbook.getSheet( BRANCH_SHEET ).rowIterator()
            : null;

        Iterator<Row> usersIterator = hierarchyWorkbook.getSheet( USERS_SHEET ) != null
            ? hierarchyWorkbook.getSheet( USERS_SHEET ).rowIterator()
            : null;


        // check for the validity of the headers
        if ( regionsIterator != null && regionsIterator.hasNext() ) {

            if ( !isHeaderValid( regionsIterator.next(), regionHeaderList ) ) {
                return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload, "Invalid Region header." );
            }
        }
        if ( branchesIterator != null && branchesIterator.hasNext() ) {

            if ( !isHeaderValid( branchesIterator.next(), branchHeaderList ) ) {
                return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload, "Invalid Branch header." );
            }
        }
        if ( usersIterator != null && usersIterator.hasNext() ) {

            if ( !isHeaderValid( usersIterator.next(), userHeaderList ) ) {
                return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload, "Invalid User header." );
            }
        }


        // ~~~~~~ initial checks finished, start the verification process

        // build the intermediate object with contains all the necessary data
        HierarchyUploadIntermediate hierarchyIntermediate = generateHierarchyUploadIntermediate(
            companyDao.findById( Company.class, parsedHierarchyUpload.getCompanyId() ) );


        parseAndVerifyRegions( hierarchyIntermediate, parsedHierarchyUpload, regionsIterator );
        parseAndVerifyBranches( hierarchyIntermediate, parsedHierarchyUpload, branchesIterator );
        parseAndVerifyUsers( hierarchyIntermediate, parsedHierarchyUpload, usersIterator );

        // end of verification process, check if entities were processed
        if ( hierarchyIntermediate.getRegionsProcessed() == 0 && hierarchyIntermediate.getBranchesProcessed() == 0
            && hierarchyIntermediate.getUsersProcessed() == 0 ) {
            return updateParsedHierarchyUploadWithGeneralErrors( parsedHierarchyUpload,
                "Unable to Detect or parse rows in the uploaded file" );
        }

        // set the number of users to be deleted in replace mode
        if ( !parsedHierarchyUpload.isInAppendMode() ) {

            parsedHierarchyUpload.setNumberOfRegionsDeleted( hierarchyIntermediate.getRegions() != null
                ? hierarchyIntermediate.getRegions().size() - hierarchyIntermediate.getRegionsProcessed()
                : 0 );

            parsedHierarchyUpload.setNumberOfBranchesDeleted( hierarchyIntermediate.getBranches() != null
                ? hierarchyIntermediate.getBranches().size() - hierarchyIntermediate.getBranchesProcessed()
                : 0 );

            parsedHierarchyUpload.setNumberOfUsersDeleted( hierarchyIntermediate.getUsers() != null
                ? hierarchyIntermediate.getUsers().size() - hierarchyIntermediate.getUsersProcessed()
                : 0 );
        }

        // check for errors or warnings
        if ( parsedHierarchyUpload.hasErrors()
            || ( !parsedHierarchyUpload.isWarningToBeIgnored() && parsedHierarchyUpload.hasWarnings() ) ) {

            // end if their are errors or warnings
            parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_VERIFIED_WITH_ERRORS_OR_WARNINGS );
            hierarchyUploadDao.reinsertParsedHierarchyUpload( parsedHierarchyUpload );
            return false;
        }

        if ( parsedHierarchyUpload.getVerifyOnly() ) {

            // aborting after verification
            parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_VERIFIED_SUCCESSFULLY );
            hierarchyUploadDao.reinsertParsedHierarchyUpload( parsedHierarchyUpload );
            return true;

        }


        // ~~~~~~ verification finished, move on to import process

        // reset count statistics
        parsedHierarchyUpload.setNumberOfRegionsAdded( 0 );
        parsedHierarchyUpload.setNumberOfRegionsModified( 0 );
        parsedHierarchyUpload.setNumberOfRegionsDeleted( 0 );

        parsedHierarchyUpload.setNumberOfBranchesAdded( 0 );
        parsedHierarchyUpload.setNumberOfBranchesModified( 0 );
        parsedHierarchyUpload.setNumberOfBranchesDeleted( 0 );


        parsedHierarchyUpload.setNumberOfUsersAdded( 0 );
        parsedHierarchyUpload.setNumberOfUsersModified( 0 );
        parsedHierarchyUpload.setNumberOfUsersDeleted( 0 );

        // remove warnings messages
        if ( parsedHierarchyUpload.getRegionValidationWarnings() != null ) {
            parsedHierarchyUpload.getRegionValidationWarnings().clear();
        }

        if ( parsedHierarchyUpload.getBranchValidationWarnings() != null ) {
            parsedHierarchyUpload.getBranchValidationWarnings().clear();
        }

        if ( parsedHierarchyUpload.getUserValidationWarnings() != null ) {
            parsedHierarchyUpload.getUserValidationWarnings().clear();
        }

        parsedHierarchyUpload.setHasWarnings( false );

        // change the status of upload process to "importing"
        parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTING );

        // get the user object which represents the import initiated user
        User adminUser = userManagementService.getUserByUserId( parsedHierarchyUpload.getImportInitiatedUserId() );

        try {

            List<RegionUploadVO> deletedRegions = importRegions( hierarchyIntermediate, parsedHierarchyUpload, adminUser );
            List<BranchUploadVO> deletedBranches = importBranches( hierarchyIntermediate, parsedHierarchyUpload, adminUser );
            List<UserUploadVO> deletedUsers = importUsers( hierarchyIntermediate, parsedHierarchyUpload, adminUser );

            // delete unprocessed entities while processing in overwrite/replace mode
            deleteUsers( hierarchyIntermediate, parsedHierarchyUpload, adminUser, deletedUsers );
            deleteBranches( hierarchyIntermediate, parsedHierarchyUpload, adminUser, deletedBranches );
            deleteRegions( hierarchyIntermediate, parsedHierarchyUpload, adminUser, deletedRegions );

        } catch ( Exception errorWhileImportingRegionsOrBranches ) {
            parsedHierarchyUpload.setHasGeneralErrors( true );
            parsedHierarchyUpload.getGeneralErrors().add( errorWhileImportingRegionsOrBranches.getMessage() );
        }

        boolean returnStatus = true;
        if ( parsedHierarchyUpload.hasErrors() || parsedHierarchyUpload.hasGeneralErrors() ) {
            parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTED_WITH_ERRORS );
            returnStatus = false;
        } else {
            // change the status of upload process to "imported"
            parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTED );
        }

        // update the parsedHierarchyUpload in mongoDB
        hierarchyUploadDao.reinsertParsedHierarchyUpload( parsedHierarchyUpload );


        // reinsert hierarchy upload object to retain new source Id
        HierarchyUpload latestUpload = new HierarchyUpload();
        latestUpload.setCompanyId( parsedHierarchyUpload.getCompanyId() );

        if ( hierarchyIntermediate.getRegions() != null && !hierarchyIntermediate.getRegions().isEmpty() ) {
            latestUpload.setRegions( new ArrayList<>( hierarchyIntermediate.getRegions().values() ) );
        }
        if ( hierarchyIntermediate.getBranches() != null && !hierarchyIntermediate.getBranches().isEmpty() ) {
            latestUpload.setBranches( new ArrayList<>( hierarchyIntermediate.getBranches().values() ) );
        }
        if ( hierarchyIntermediate.getUsers() != null && !hierarchyIntermediate.getUsers().isEmpty() ) {
            latestUpload.setUsers( new ArrayList<>( hierarchyIntermediate.getUsers().values() ) );
        }

        hierarchyUploadDao.reinsertHierarchyUploadObjectForACompany( latestUpload );

        LOG.debug( "method processHierarchyUploadXlsx() finished" );
        return returnStatus;
    }


    private void deleteUsers( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        User adminUser, List<UserUploadVO> deletedUsers ) throws Exception
    {
        if ( deletedUsers != null && deletedUsers.size() > 0 ) {
            for ( UserUploadVO user : deletedUsers ) {
                try {
                    // never delete the administrator( owner ) 
                    if ( adminUser.getUserId() != user.getUserId() ) {
                        deleteUser( adminUser, user );
                        parsedHierarchyUpload.setNumberOfUsersDeleted( parsedHierarchyUpload.getNumberOfUsersDeleted() + 1 );
                        if ( hierarchyIntermediate.getUsers() != null ) {
                            hierarchyIntermediate.getUsers().remove( user.getSourceUserId() );
                        }
                    }
                } catch ( Exception error ) {

                    if ( parsedHierarchyUpload.getUserErrors() == null ) {
                        parsedHierarchyUpload.setUserErrors( new ArrayList<String>() );
                    }

                    //set errors
                    parsedHierarchyUpload.setHasErrors( true );
                    parsedHierarchyUpload.getUserErrors().add( error.getMessage() );
                    // continue with other users
                }
            }
        }
    }


    private void deleteBranches( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        User user, List<BranchUploadVO> deletedBranches ) throws Exception
    {
        if ( deletedBranches != null && deletedBranches.size() > 0 ) {
            for ( BranchUploadVO branch : deletedBranches ) {
                try {
                    deleteBranch( user, branch );
                    parsedHierarchyUpload.setNumberOfBranchesDeleted( parsedHierarchyUpload.getNumberOfBranchesDeleted() + 1 );
                    if ( hierarchyIntermediate.getBranches() != null ) {
                        hierarchyIntermediate.getBranches().remove( branch.getSourceBranchId() );
                    }
                } catch ( Exception error ) {

                    if ( parsedHierarchyUpload.getBranchErrors() == null ) {
                        parsedHierarchyUpload.setBranchErrors( new ArrayList<String>() );
                    }

                    //set errors
                    parsedHierarchyUpload.setHasErrors( true );
                    parsedHierarchyUpload.getBranchErrors().add( error.getMessage() );
                    // continue with other branches
                }
            }
        }
    }


    private void deleteRegions( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        User adminUser, List<RegionUploadVO> deletdRegions ) throws Exception
    {
        if ( deletdRegions != null && deletdRegions.size() > 0 ) {
            for ( RegionUploadVO region : deletdRegions ) {
                try {
                    deleteRegion( adminUser, region );
                    parsedHierarchyUpload.setNumberOfRegionsDeleted( parsedHierarchyUpload.getNumberOfRegionsDeleted() + 1 );
                    if ( hierarchyIntermediate.getRegions() != null ) {
                        hierarchyIntermediate.getRegions().remove( region.getSourceRegionId() );
                    }
                } catch ( Exception error ) {
                    if ( parsedHierarchyUpload.getRegionErrors() == null ) {
                        parsedHierarchyUpload.setRegionErrors( new ArrayList<String>() );
                    }

                    //set errors
                    parsedHierarchyUpload.setHasErrors( true );
                    parsedHierarchyUpload.getRegionErrors().add( error.getMessage() );
                    // continue with other regions
                }
            }
        }
    }


    private List<UserUploadVO> importUsers( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, User adminUser )
    {
        List<UserUploadVO> deletedUsers = null;
        if ( hierarchyIntermediate.getUsers() != null && !hierarchyIntermediate.getUsers().isEmpty() ) {
            LOG.debug( "Importing users." );
            deletedUsers = new ArrayList<>();
            for ( UserUploadVO user : hierarchyIntermediate.getUsers().values() ) {

                try {

                    if ( user.isUserProcessed() ) {

                        if ( !user.isUserAdded() && !user.isUserModified() ) {
                            if ( user.isSendMail() ) {
                                resendVerificationMail( user );
                                continue;
                            }
                        }

                        User processedUser = null;
                        checkAndMaskEmailId( user );

                        if ( user.isUserAdded() ) {

                            processedUser = addUser( hierarchyIntermediate, user, adminUser );
                            parsedHierarchyUpload.setNumberOfUsersAdded( parsedHierarchyUpload.getNumberOfUsersAdded() + 1 );


                        } else if ( user.isUserModified() ) {

                            // modify existing user
                            processedUser = modifyUser( hierarchyIntermediate, user, adminUser );
                            parsedHierarchyUpload
                                .setNumberOfUsersModified( parsedHierarchyUpload.getNumberOfUsersModified() + 1 );
                        }

                        // update mongoDB
                        if ( user.isUserAdded() || user.isUserModified() ) {
                            user.setUserId( processedUser.getUserId() );
                            updateUserSettingsInMongo( processedUser, user );
                        }

                    } else {
                        if ( !parsedHierarchyUpload.isInAppendMode() ) {

                            // in override mode, delete unprocessed ones, except for the administrator ( owner )
                            deletedUsers.add( user );

                        }
                    }
                } catch ( Exception errorWhileParsingOneOfTheUsers ) {

                    if ( parsedHierarchyUpload.getUserErrors() == null ) {
                        parsedHierarchyUpload.setUserErrors( new ArrayList<String>() );
                    }

                    //set errors
                    parsedHierarchyUpload.setHasErrors( true );
                    parsedHierarchyUpload.getUserErrors()
                        .add( "Row: " + user.getRowNum() + ", " + errorWhileParsingOneOfTheUsers.getMessage() );
                    // continue with other users
                }
            }
        }
        return deletedUsers;

    }


    private void checkAndMaskEmailId( UserUploadVO user )
    {
        //Mask email ID if necessary
        if ( user.isUserAdded() || user.isEmailModified() ) {

            String emailId = user.getEmailId();
            if ( CommonConstants.YES_STRING.equals( maskEmail ) ) {
                emailId = utils.maskEmailAddress( emailId );
            }
            user.setEmailId( emailId );
        }

    }


    @Transactional
    private User addUser( HierarchyUploadIntermediate hierarchyIntermediate, UserUploadVO user, User adminUser )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException, UserAdditionException
    {
        LOG.info( "Method addUser() started for user : " + user.getEmailId() );
        User uploadedUser = null;

        if ( user.getEmailId() == null || user.getEmailId().isEmpty() ) {
            throw new InvalidInputException( "User email ID cannot be null" );
        }

        //Add user and call assignUser method
        //Add user
        //If sendMail = true, then you need to send the mail. So holdSendingMail should be false.
        try {
            uploadedUser = userManagementService.inviteUserToRegister( adminUser, user.getFirstName(), user.getLastName(),
                user.getEmailId(), true, user.isSendMail(), true, true );

            if ( uploadedUser == null ) {
                throw new UserAdditionException( "Unable to add user with emailID : " + user.getEmailId() );
            }

        } catch ( UserAlreadyExistsException | UndeliveredEmailException e ) {
            throw new UserAdditionException( "Unable to add user with emailID : " + user.getEmailId() );
        }

        //Reset sendMail to false
        user.setSendMail( false );

        LOG.debug( "Added user with email : " + user.getEmailId() );
        //Assign user
        assignUser( hierarchyIntermediate, user, adminUser );

        LOG.info( "Method addUser() finished for user : " + user.getEmailId() );
        return uploadedUser;
    }


    @Transactional
    private User assignUser( HierarchyUploadIntermediate hierarchyIntermediate, UserUploadVO user, User adminUser )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException
    {
        LOG.info( "Method assignUser() started for user : " + user.getEmailId() );
        User assigneeUser = null;
        try {
            assigneeUser = userManagementService.getUserByUserId( user.getUserId() );
        } catch ( InvalidInputException e ) {
            LOG.warn( e.getMessage() );
        }
        if ( assigneeUser == null ) {
        }
        assigneeUser = userManagementService.getUserByEmailAddress( user.getEmailId() );
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "Couldn't find user : " + user.getSourceUserId() );
        }
        if ( ( user.getAssignedBranches() == null || user.getAssignedBranches().isEmpty() )
            && ( user.getAssignedRegions() == null || user.getAssignedRegions().isEmpty() )
            && ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() )
            && ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() ) ) {

            //Assign user to company

            Region region = organizationManagementService.getDefaultRegionForCompany( adminUser.getCompany() );
            Branch branch = organizationManagementService.getDefaultBranchForRegion( region.getRegionId() );
            organizationManagementService.assignBranchToUser( adminUser, branch.getBranchId(), region.getRegionId(),
                assigneeUser, false );
            assigneeUser.setAgent( true );
            assigneeUser.setBranchAdmin( false );
            assigneeUser.setRegionAdmin( false );

        }
        //Agent assignments
        assigneeUser = assignBranchesToUser( hierarchyIntermediate, user, adminUser, assigneeUser, false );
        assigneeUser = assignRegionsToUser( hierarchyIntermediate, user, adminUser, assigneeUser, false );
        //Admin assignments
        assigneeUser = assignBranchesToUser( hierarchyIntermediate, user, adminUser, assigneeUser, true );
        assigneeUser = assignRegionsToUser( hierarchyIntermediate, user, adminUser, assigneeUser, true );

        if ( ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() ) ) {
            assigneeUser.setBranchAdmin( false );
        }
        if ( ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() ) ) {
            assigneeUser.setRegionAdmin( false );
        }
        LOG.info( "Method assignUser() finished for user : " + user.getEmailId() );
        return assigneeUser;
    }


    @Transactional
    private User assignBranchesToUser( HierarchyUploadIntermediate hierarchyIntermediate, UserUploadVO user, User adminUser,
        User assigneeUser, boolean isAdmin )
        throws UserAssignmentException, InvalidInputException, NoRecordsFetchedException, SolrException, UserAdditionException
    {
        LOG.info( "Method assignBranchesToUser() for user : " + user.getEmailId() + " isAdmin : " + isAdmin + " started." );
        if ( ( !isAdmin && ( user.isAssignedBranchesModified() ) ) || ( isAdmin && user.isAssignedBranchesAdminModified() )
            || user.isUserAdded() ) {

            //Compare the current user assignments and the new assignments to find the changes
            Set<String> addedAssignments = new HashSet<String>();
            Set<String> deletedAssignments = new HashSet<String>();

            if ( isAdmin ) {

                if ( user.getAssignedBranchesAdminHistory() != null && !user.getAssignedBranchesAdminHistory().isEmpty() )
                    deletedAssignments.addAll( user.getAssignedBranchesAdminHistory()
                        .get( user.getAssignedBranchesAdminHistory().size() - 1 ).getValue() );

                if ( user.getAssignedBranchesAdmin() != null && !user.getAssignedBranchesAdmin().isEmpty() ) {
                    for ( String assignedBranchAdmin : user.getAssignedBranchesAdmin() ) {
                        if ( !deletedAssignments.contains( assignedBranchAdmin ) ) {
                            addedAssignments.add( assignedBranchAdmin );
                        } else {
                            deletedAssignments.remove( assignedBranchAdmin );
                        }
                    }
                }

            } else {
                if ( user.getAssignedBranchesHistory() != null && !user.getAssignedBranchesHistory().isEmpty() )
                    deletedAssignments.addAll(
                        user.getAssignedBranchesHistory().get( user.getAssignedBranchesHistory().size() - 1 ).getValue() );

                if ( user.getAssignedBranches() != null && !user.getAssignedBranches().isEmpty() ) {
                    for ( String assignedBranch : user.getAssignedBranches() ) {
                        if ( !deletedAssignments.contains( assignedBranch ) ) {
                            addedAssignments.add( assignedBranch );
                        } else {
                            deletedAssignments.remove( assignedBranch );
                        }
                    }
                }
            }

            //Add branch assignments
            for ( String sourceBranchId : addedAssignments ) {
                if ( hierarchyIntermediate.getBranches() == null ) {
                    throw new UserAssignmentException( "No branches in the company detected." );
                } else if ( StringUtils.isEmpty( sourceBranchId ) ) {
                    throw new UserAssignmentException(
                        "Invalid branch assignment for user with email ID: " + user.getEmailId() );
                }
                BranchUploadVO branchVO = hierarchyIntermediate.getBranches().get( sourceBranchId );
                Branch branch = ( branchVO != null ) ? userManagementService.getBranchById( branchVO.getBranchId() ) : null;
                if ( branch == null ) {
                    throw new UserAssignmentException( "unable to find branch with branchId : " + branchVO.getBranchId()
                        + " and sourceId : " + sourceBranchId );
                }
                long regionId = branch.getRegion().getRegionId();
                organizationManagementService.assignBranchToUser( adminUser, branchVO.getBranchId(), regionId, assigneeUser,
                    isAdmin );

            }

            if ( !addedAssignments.isEmpty() ) {
                if ( isAdmin ) {
                    assigneeUser.setBranchAdmin( true );
                } else {
                    assigneeUser.setAgent( true );
                }
            }

            //Remove branch assignments
            for ( String sourceBranchId : deletedAssignments ) {
                if ( hierarchyIntermediate.getBranches() == null ) {
                    throw new UserAssignmentException( "No branches in the company detected." );
                } else if ( StringUtils.isEmpty( sourceBranchId ) ) {
                    throw new UserAssignmentException(
                        "Invalid branch assignment for user with email ID: " + user.getEmailId() );
                }
                BranchUploadVO branchVO = hierarchyIntermediate.getBranches().get( sourceBranchId );
                Branch branch = ( branchVO != null ) ? userManagementService.getBranchById( branchVO.getBranchId() ) : null;
                if ( branch == null ) {
                    throw new UserAdditionException( "unable to find branch with branchId : " + branchVO.getBranchId()
                        + " and sourceId : " + sourceBranchId );
                }
                long regionId = branch.getRegion().getRegionId();
                try {
                    int profilesMaster;
                    if ( isAdmin ) {
                        profilesMaster = CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID;
                    } else {
                        profilesMaster = CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID;
                    }
                    UserProfile profile = userProfileDao.findUserProfile( assigneeUser.getUserId(), branchVO.getBranchId(),
                        regionId, profilesMaster );
                    userManagementService.removeUserProfile( assigneeUser, adminUser, profile.getUserProfileId() );
                } catch ( NoRecordsFetchedException e ) {
                    throw new UserAssignmentException( "Unable to fetch userProfile for user. Reason: ", e );
                }

            }
        }
        LOG.info( "Method assignBranchesToUser() for user : " + user.getEmailId() + " isAdmin : " + isAdmin + " finished." );
        return assigneeUser;
    }


    @Transactional
    private User assignRegionsToUser( HierarchyUploadIntermediate hierarchyIntermediate, UserUploadVO user, User adminUser,
        User assigneeUser, boolean isAdmin )
        throws UserAssignmentException, InvalidInputException, NoRecordsFetchedException, SolrException, UserAdditionException
    {
        LOG.info( "Method assignRegionsToUser started for user : " + user.getEmailId() );
        if ( ( !isAdmin && ( user.isAssignedRegionsModified() ) )
            || ( isAdmin && user.isAssignedRegionsAdminModified() || user.isUserAdded() ) ) {
            //Compare the current user assignments and the new assignments to find the changes

            List<String> addedAssignments = new ArrayList<String>();
            List<String> deletedAssignments = new ArrayList<String>();

            if ( isAdmin ) {

                if ( user.getAssignedRegionsAdminHistory() != null && !user.getAssignedRegionsAdminHistory().isEmpty() )
                    deletedAssignments.addAll( user.getAssignedRegionsAdminHistory()
                        .get( user.getAssignedRegionsAdminHistory().size() - 1 ).getValue() );

                if ( user.getAssignedRegionsAdmin() != null && !user.getAssignedRegionsAdmin().isEmpty() ) {
                    for ( String assignedRegionAdmin : user.getAssignedRegionsAdmin() ) {
                        if ( !deletedAssignments.contains( assignedRegionAdmin ) ) {
                            addedAssignments.add( assignedRegionAdmin );
                        } else {
                            deletedAssignments.remove( assignedRegionAdmin );
                        }
                    }
                }

            } else {
                if ( user.getAssignedRegionsHistory() != null && !user.getAssignedRegionsHistory().isEmpty() )
                    deletedAssignments.addAll(
                        user.getAssignedRegionsHistory().get( user.getAssignedRegionsHistory().size() - 1 ).getValue() );

                if ( user.getAssignedRegions() != null && !user.getAssignedRegions().isEmpty() ) {
                    for ( String assignedRegion : user.getAssignedRegions() ) {
                        if ( !deletedAssignments.contains( assignedRegion ) ) {
                            addedAssignments.add( assignedRegion );
                        } else {
                            deletedAssignments.remove( assignedRegion );
                        }
                    }
                }
            }

            //Add region assignments
            for ( String sourceRegionId : addedAssignments ) {

                if ( hierarchyIntermediate.getRegions() == null ) {
                    throw new UserAssignmentException( "No regions in the company detected." );
                } else if ( StringUtils.isEmpty( sourceRegionId ) ) {
                    throw new UserAssignmentException(
                        "Invalid region assignment for user with email ID: " + user.getEmailId() );
                }

                RegionUploadVO regionVO = hierarchyIntermediate.getRegions().get( sourceRegionId );
                if ( regionVO == null ) {
                    throw new UserAssignmentException(
                        "Invalid region assignment for user with email ID: " + user.getEmailId() );
                }
                long regionId = regionVO.getRegionId();
                organizationManagementService.assignRegionToUser( adminUser, regionId, assigneeUser, isAdmin );
            }

            if ( !addedAssignments.isEmpty() ) {
                if ( isAdmin ) {
                    assigneeUser.setRegionAdmin( true );
                } else {
                    assigneeUser.setAgent( true );
                }
            }

            //Remove region assignments
            for ( String sourceRegionId : deletedAssignments ) {

                if ( hierarchyIntermediate.getRegions() == null ) {
                    throw new UserAssignmentException( "No regions in the company detected." );
                } else if ( StringUtils.isEmpty( sourceRegionId ) ) {
                    throw new UserAssignmentException(
                        "Invalid region assignment for user with email ID: " + user.getEmailId() );
                }

                RegionUploadVO regionVO = hierarchyIntermediate.getRegions().get( sourceRegionId );
                if ( regionVO == null ) {
                    throw new UserAssignmentException(
                        "Invalid region assignment for user with email ID: " + user.getEmailId() );
                }
                long regionId = regionVO.getRegionId();

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

            }
        }
        LOG.info( "Method assignRegionsToUser finished for user : " + user.getEmailId() );
        return assigneeUser;
    }


    /**
     * Method to send/resend user verification mail
     * @param userUpload
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     * @throws NoRecordsFetchedException 
     */
    @Transactional
    private void resendVerificationMail( UserUploadVO userUpload )
        throws InvalidInputException, UndeliveredEmailException, NoRecordsFetchedException
    {
        LOG.info( "Method to resend verification mail started for user : " + userUpload.getSourceUserId() );
        //Resend verification mail if sendMail is true
        User user = userManagementService.getUserByEmailAddress( userUpload.getEmailId() );
        if ( userUpload.isSendMail() ) {
            String profileName = userManagementService.getUserSettings( user.getUserId() ).getProfileName();
            userManagementService.sendRegistrationCompletionLink( user.getEmailId(), user.getFirstName(), user.getLastName(),
                user.getCompany().getCompanyId(), profileName, user.getLoginName(), false );
            userUpload.setSendMail( false );
        }
        LOG.info( "Method to resend verification mail finished for user : " + userUpload.getSourceUserId() );
    }


    @Transactional
    private User modifyUser( HierarchyUploadIntermediate hierarchyIntermediate, UserUploadVO user, User adminUser )
        throws UserAdditionException, InvalidInputException, SolrException, NoRecordsFetchedException, UserAssignmentException,
        UndeliveredEmailException, UserAlreadyExistsException
    {
        LOG.info( "Method modifyUser() started for user : " + user.getEmailId() );
        User assigneeUser = null;
        try {
            assigneeUser = userManagementService.getUserByUserId( user.getUserId() );
        } catch ( InvalidInputException e ) {
            LOG.warn( e.getMessage() );
        }
        if ( assigneeUser == null ) {
            assigneeUser = userManagementService.getUserByEmailAddress( user.getEmailId() );
        }
        if ( assigneeUser == null ) {
            throw new InvalidInputException( "Couldn't find user : " + user.getSourceUserId() );
        }


        // check and modify email
        if ( user.isEmailModified() ) {

            if ( assigneeUser.getStatus() != CommonConstants.STATUS_ACTIVE ) {
                assigneeUser.setEmailId( user.getEmailId() );
                assigneeUser.setLoginName( user.getEmailId() );
                assigneeUser.setStatus( CommonConstants.STATUS_NOT_VERIFIED );

                // Modify email Ids in userprofile
                userProfileDao.updateEmailIdForUserProfile( assigneeUser.getUserId(), user.getEmailId() );
                
            } else {

                startEmailModificationProcess( assigneeUser, user.getEmailId() );
            }
        }

        assigneeUser.setFirstName( user.getFirstName() );
        assigneeUser.setLastName( user.getLastName() );


        userDao.update( assigneeUser );

        if ( user.isAssignedBranchesModified() || user.isAssignedBranchesAdminModified() || user.isAssignedRegionsModified()
            || user.isAssignedRegionsAdminModified() ) {
            assignUser( hierarchyIntermediate, user, adminUser );
        }

        //send verification mail if needed
        if ( user.isSendMail() ) {
            resendVerificationMail( user );
        }

        //Add user to Solr
        solrSearchService.addUserToSolr( assigneeUser );

        return assigneeUser;
    }


    private void startEmailModificationProcess( User user, String emailId )
        throws InvalidInputException, UndeliveredEmailException, UserAlreadyExistsException, NoRecordsFetchedException
    {
        LOG.debug( "method startEmailModificationProcess() started" );
        MiscValues workEmail = new MiscValues();
        workEmail.setKey( CommonConstants.EMAIL_TYPE_WORK );
        workEmail.setValue( emailId );

        boolean isWorkEmailLockedByCompany = settingsLocker.isSettingsValueLocked( OrganizationUnit.COMPANY,
            Double.parseDouble( user.getCompany().getSettingsLockStatus() ), SettingsForApplication.EMAIL_ID_WORK );

        AgentSettings agentSettings = organizationManagementService.getAgentSettings( user.getUserId() );

        LOG.trace( "changing the email ID for user: {} to {}", user.getUserId(), emailId );
        profileManagementService.updateVerifiedEmail( agentSettings, isWorkEmailLockedByCompany,
            user.getCompany().getCompanyId(), MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
            Arrays.asList( workEmail ) );
        LOG.debug( "method startEmailModificationProcess() finished" );
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    private void deleteUser( User adminUser, UserUploadVO user ) throws Exception
    {

        // Delete the user
        try {
            userManagementService.deleteUserDataFromAllSources( adminUser, user.getUserId(), CommonConstants.STATUS_INACTIVE, true, true );

        } catch ( Exception errorWhileDeletingUser ) {
            // process errors and return them to the user
            throw new Exception(
                "Error while deleting user with Id: " + user.getUserId() + " Reason: " + errorWhileDeletingUser.getMessage() );
        }


    }


    /**
     * Method to update agentSettings of user in mongo
     * @param user
     * @param userUploadVO
     * @throws InvalidInputException
     */
    private void updateUserSettingsInMongo( User user, UserUploadVO userUploadVO ) throws InvalidInputException
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

            if ( userUploadVO.isEmailModified() && user.getStatus() != CommonConstants.STATUS_ACTIVE ) {
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

            if ( userUploadVO.getUserPhotoUrl() != null && !userUploadVO.getUserPhotoUrl().isEmpty() ) {
                updateProfileImageForAgent( userUploadVO.getUserPhotoUrl(), agentSettings );
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
        if ( isImagevalid( userPhotoUrl ) ) {
            if ( userPhotoUrl != agentSettings.getProfileImageUrl() && !userPhotoUrl.contains( cdnUrl ) ) {
                String url = null;
                if ( userPhotoUrl.contains( cdnUrl ) ) {
                    url = userPhotoUrl;
                } else {
                    try {
                        url = uploadProfileImageToCloud( userPhotoUrl );
                    } catch ( Exception e ) {
                        LOG.warn( "Unable to upload image to cloud.", e );
                        throw new InvalidInputException( "Image format not valid for url:" + userPhotoUrl );
                    }
                }
                if ( url != null && !url.isEmpty() ) {
                    profileManagementService.updateProfileImage( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                        agentSettings, url );
                }
            }
        }
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    private List<BranchUploadVO> importBranches( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, User adminUser ) throws Exception
    {
        List<BranchUploadVO> deletedBranches = null;
        if ( hierarchyIntermediate.getBranches() != null && !hierarchyIntermediate.getBranches().isEmpty() ) {
            LOG.debug( "Importing branches." );
            deletedBranches = new ArrayList<>();
            try {
                for ( BranchUploadVO branch : hierarchyIntermediate.getBranches().values() ) {
                    if ( branch.isBranchProcessed() ) {

                        Branch processedBranch = null;
                        if ( branch.isBranchAdded() ) {

                            processedBranch = createBranch( hierarchyIntermediate, adminUser, branch );
                            parsedHierarchyUpload
                                .setNumberOfBranchesAdded( parsedHierarchyUpload.getNumberOfBranchesAdded() + 1 );

                            // update ID
                            branch.setBranchId( processedBranch.getBranchId() );


                        } else if ( branch.isBranchModified() ) {

                            // modify existing branch
                            processedBranch = modifyBranch( adminUser, branch );
                            parsedHierarchyUpload
                                .setNumberOfBranchesModified( parsedHierarchyUpload.getNumberOfBranchesModified() + 1 );

                            // update ID
                            if ( branch.isBranchAdded() || branch.isBranchModified() ) {
                                branch.setBranchId( processedBranch.getBranchId() );
                            }
                        }


                    } else {
                        if ( !parsedHierarchyUpload.isInAppendMode() ) {

                            // in override mode, delete unprocessed ones
                            deletedBranches.add( branch );

                        }
                    }
                }
            } catch ( Exception errorWhileParsingOneOfTheBranches ) {

                LOG.error( "error while importing branches, reason: {}", errorWhileParsingOneOfTheBranches.getMessage(),
                    errorWhileParsingOneOfTheBranches );

                if ( parsedHierarchyUpload.getBranchErrors() == null ) {
                    parsedHierarchyUpload.setBranchErrors( new ArrayList<String>() );
                }

                //set errors
                parsedHierarchyUpload.setHasErrors( true );
                parsedHierarchyUpload.getBranchErrors().add( errorWhileParsingOneOfTheBranches.getMessage() );
                throw errorWhileParsingOneOfTheBranches;

            }
        }
        return deletedBranches;
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
    private Branch createBranch( HierarchyUploadIntermediate hierarchyIntermediate, User adminUser, BranchUploadVO branch )
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

        LOG.debug( "createBranch called to create branch :  " + branch.getBranchName() );
        String country, countryCode;
        if ( branch.getBranchCountry() != null && branch.getBranchCountryCode() != null ) {
            country = branch.getBranchCountry();
            countryCode = branch.getBranchCountryCode();
        } else {
            OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( adminUser );
            country = companySettings.getContact_details().getCountry();
            countryCode = companySettings.getContact_details().getCountryCode();
        }

        //Resolve region Id
        if ( hierarchyIntermediate.getRegions() != null
            && hierarchyIntermediate.getRegions().containsKey( branch.getSourceRegionId() ) ) {
            branch.setRegionId( hierarchyIntermediate.getRegions().get( branch.getSourceRegionId() ).getRegionId() );
        }


        newBranch = organizationManagementService.addNewBranch( adminUser, branch.getRegionId(), CommonConstants.NO,
            branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(), country, countryCode,
            branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode() );

        LOG.debug( "createBranch finished for branch : " + branch.getBranchName() );
        return newBranch;

    }


    @Transactional
    private Branch modifyBranch( User adminUser, BranchUploadVO branch )
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

        LOG.debug( "ModifyBranch called for branch : " + branch.getBranchName() );
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
            countryCode, branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode(), 0, null, false, false, true );
        newBranch = (Branch) map.get( CommonConstants.BRANCH_OBJECT );
        if ( newBranch == null ) {
            LOG.error( "No branch found with branchId :" + branch.getBranchId() );
            throw new InvalidInputException( "No branch found with branchId :" + branch.getBranchId() );
        }

        LOG.info( "ModifyBranch finished for branch : " + branch.getBranchName() );
        return newBranch;
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    private void deleteBranch( User adminUser, BranchUploadVO branch ) throws Exception
    {
        try {
            // Check if branch can be deleted
            LOG.debug( "Calling service to get the count of users in branch" );
            long usersCount = organizationManagementService.getCountUsersInBranch( branch.getBranchId() );
            LOG.debug( "Successfully executed service to get the count of users in branch : " + usersCount );

            if ( usersCount > 0l ) {
                LOG.error( "Cannot delete branch : " + branch.getBranchName() + ". There are active users in the branch." );
                throw new InvalidInputException(
                    "Cannot delete branch : " + branch.getBranchName() + ". There are active users in the branch." );
            } else {
                //Delete the branch
                organizationManagementService.deleteBranchDataFromAllSources( branch.getBranchId(), adminUser, null,
                    CommonConstants.STATUS_INACTIVE );
            }

        } catch ( Exception errorWhileDeletingBranch ) {
            //process errors and return them to the user
            throw new Exception( "Error while deleting branch with Id: " + branch.getBranchId() + " Reason: "
                + errorWhileDeletingBranch.getMessage() );
        }
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    private List<RegionUploadVO> importRegions( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, User adminUser ) throws Exception
    {
        List<RegionUploadVO> deletedRegions = null;
        if ( hierarchyIntermediate.getRegions() != null && !hierarchyIntermediate.getRegions().isEmpty() ) {
            LOG.debug( "Importing regions." );
            deletedRegions = new ArrayList<>();
            try {
                for ( RegionUploadVO region : hierarchyIntermediate.getRegions().values() ) {
                    if ( region.isRegionProcessed() ) {

                        Region processedRegion = null;
                        if ( region.isRegionAdded() ) {

                            processedRegion = createRegion( adminUser, region );
                            parsedHierarchyUpload
                                .setNumberOfRegionsAdded( parsedHierarchyUpload.getNumberOfRegionsAdded() + 1 );

                            // set region ID to VO


                        } else if ( region.isRegionModified() ) {

                            // modify existing region
                            processedRegion = modifyRegion( adminUser, region );
                            parsedHierarchyUpload
                                .setNumberOfRegionsModified( parsedHierarchyUpload.getNumberOfRegionsModified() + 1 );
                        }

                        // update id
                        if ( region.isRegionAdded() || region.isRegionModified() ) {
                            region.setRegionId( processedRegion.getRegionId() );
                        }

                    } else {
                        if ( !parsedHierarchyUpload.isInAppendMode() ) {

                            // in override mode, delete unprocessed ones
                            deletedRegions.add( region );
                        }
                    }
                }
            } catch ( Exception errorWhileParsingOneOfTheRegions ) {

                LOG.error( "error while importing regions, reason {}", errorWhileParsingOneOfTheRegions.getMessage(),
                    errorWhileParsingOneOfTheRegions );

                if ( parsedHierarchyUpload.getRegionErrors() == null ) {
                    parsedHierarchyUpload.setRegionErrors( new ArrayList<String>() );
                }

                //set errors
                parsedHierarchyUpload.setHasErrors( true );
                parsedHierarchyUpload.getRegionErrors().add( errorWhileParsingOneOfTheRegions.getMessage() );
                throw errorWhileParsingOneOfTheRegions;

            }
        }
        return deletedRegions;
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
    private Region createRegion( User adminUser, RegionUploadVO region ) throws InvalidInputException, SolrException
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
        LOG.debug( "createRegion called to add region : " + region.getRegionName() );

        LOG.debug( "Adding region : " + region.getRegionName() );
        newRegion = organizationManagementService.addNewRegion( adminUser, region.getRegionName(), CommonConstants.NO,
            region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(), region.getRegionCountryCode(),
            region.getRegionState(), region.getRegionCity(), region.getRegionZipcode() );
        organizationManagementService.addNewBranch( adminUser, newRegion.getRegionId(), CommonConstants.YES,
            CommonConstants.DEFAULT_BRANCH_NAME, null, null, null, null, null, null, null );
        return newRegion;
    }


    @Transactional
    private Region modifyRegion( User adminUser, RegionUploadVO region )
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

        LOG.debug( "ModifyRegion called for region : " + region.getRegionName() );
        LOG.debug( "Updating region with RegionId : " + region.getRegionId() );

        //Update region
        Map<String, Object> map = organizationManagementService.updateRegion( adminUser, region.getRegionId(),
            region.getRegionName(), region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(),
            region.getRegionCountryCode(), region.getRegionState(), region.getRegionCity(), region.getRegionZipcode(), 0, null,
            false, false, true );
        newRegion = (Region) map.get( CommonConstants.REGION_OBJECT );

        if ( newRegion == null ) {
            LOG.error( "No region found with regionId :" + region.getRegionId() );
            throw new InvalidInputException( "No region found with regionId :" + region.getRegionId() );
        }

        return newRegion;
    }


    @Transactional ( propagation = Propagation.REQUIRES_NEW)
    private void deleteRegion( User user, RegionUploadVO region ) throws Exception
    {
        LOG.debug( "Deleting region with source ID: " + region.getSourceRegionId() );

        try {

            //Check if the region can be deleted
            LOG.debug( "Calling service to get the count of branches in region" );
            long branchCount = organizationManagementService.getCountBranchesInRegion( region.getRegionId() );
            LOG.debug( "Successfully executed service to get the count of branches in region : " + branchCount );

            if ( branchCount > 0l ) {
                LOG.error( "Cannot delete region : " + region.getRegionName() + ". There are active branches in the region." );
                throw new InvalidInputException(
                    "Cannot delete region: " + region.getRegionName() + ". There are active branches in the region." );
            } else {
                // Delete the region
                organizationManagementService.deleteRegionDataFromAllSources( region.getRegionId(), user, null,
                    CommonConstants.STATUS_INACTIVE );

            }

        } catch ( Exception errorWhileDeletingRegion ) {
            //process errors and return them to the user
            throw new Exception( "Error while deleting Region with Id: " + region.getRegionId() + " Reason: "
                + errorWhileDeletingRegion.getMessage() );
        }

    }


    private XSSFWorkbook generateWorkBookFromXlsx( String fileURI ) throws InvalidInputException
    {
        if ( fileURI == null ) {
            throw new InvalidInputException( "xlsx file not found" );
        }

        try {
            return new XSSFWorkbook( new URL( fileURI ).openStream() );
        } catch ( IOException error ) {
            throw new InvalidInputException( "Invalid file URL." );
        }

    }


    private boolean updateParsedHierarchyUploadWithGeneralErrors( ParsedHierarchyUpload parsedHierarchyUpload, String error )
        throws InvalidInputException
    {
        // add the error to general error list
        parsedHierarchyUpload.getGeneralErrors().add( error );
        parsedHierarchyUpload.setHasGeneralErrors( true );
        parsedHierarchyUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_VERIFIED_WITH_GENERAL_ERRORS );

        // change the status of upload process to "verified with errors"
        hierarchyUploadDao.reinsertParsedHierarchyUpload( parsedHierarchyUpload );

        return false;
    }


    private void parseAndVerifyUsers( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, Iterator<Row> usersIterator ) throws InvalidInputException
    {
        if ( usersIterator != null ) {

            // initialize user error and warning list
            if ( parsedHierarchyUpload.getUserValidationWarnings() == null ) {
                parsedHierarchyUpload.setUserValidationWarnings( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getUserValidationWarnings().clear();
            }
            if ( parsedHierarchyUpload.getUserErrors() == null ) {
                parsedHierarchyUpload.setUserErrors( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getUserErrors().clear();
            }


            List<String> applicationEmailIdList = new ArrayList<>();
            List<String> companyEmailIdList = new ArrayList<>();
            BiMap<String, String> emailsUploadedMap = null;

            // initialize other necessary objects
            if ( usersIterator.hasNext() ) {
                applicationEmailIdList = userDao.getRegisteredEmailsInOtherCompanies( hierarchyIntermediate.getCompany() );
                companyEmailIdList = userDao.getRegisteredEmailsInTheCompany( hierarchyIntermediate.getCompany() );
                emailsUploadedMap = HashBiMap.create( new HashMap<String, String>() );
                if ( hierarchyIntermediate.getUsers() == null ) {
                    hierarchyIntermediate.setUsers( new HashMap<String, UserUploadVO>() );
                }
            }

            while ( usersIterator.hasNext() ) {

                String sourceUserId = "";
                Row row = usersIterator.next();

                // discard the current row if it is empty or doesn't have values
                if ( row.getLastCellNum() <= 0 || row.getPhysicalNumberOfCells() < 1 ) {
                    continue;
                }

                Cell userSourceIdCell = row.getCell( USER_ID_INDEX );
                Cell userFirstNameCell = row.getCell( USER_FIRST_NAME_INDEX );
                Cell userLastNameCell = row.getCell( USER_LAST_NAME_INDEX );
                Cell userTitleCell = row.getCell( USER_TITLE_INDEX );
                Cell userBranchIdCell = row.getCell( USER_BRANCH_ID_INDEX );
                Cell userRegionIdCell = row.getCell( USER_REGION_ID_INDEX );
                Cell userBranchIdAdminCell = row.getCell( USER_BRANCH_ID_ADMIN_INDEX );
                Cell userRegionIdAdminCell = row.getCell( USER_REGION_ID_ADMIN_INDEX );
                Cell userEmailCell = row.getCell( USER_EMAIL_INDEX );
                Cell userPhoneNumberCell = row.getCell( USER_PHONE_NUMBER );
                Cell userWebsiteCell = row.getCell( USER_WEBSITE );
                Cell userLicensesCell = row.getCell( USER_LICENSES );
                Cell userLegalDisclaimerCell = row.getCell( USER_LEGAL_DISCLAIMER );
                Cell userPhotoProfileURLCell = row.getCell( USER_PHOTO_PROFILE_URL );
                Cell userAboutMeCell = row.getCell( USER_ABOUT_ME_DESCRIPTION );
                Cell userSendEmailCell = row.getCell( USER_SEND_EMAIL );

                String userFirstName = workbookOperations.getStringValue( userFirstNameCell );
                String userLastName = workbookOperations.getStringValue( userLastNameCell );
                String userTitle = workbookOperations.getStringValue( userTitleCell );
                String userEmail = workbookOperations.getStringValue( userEmailCell );
                String userPhoneNumber = workbookOperations.getStringValue( userPhoneNumberCell );
                String userWebsite = workbookOperations.getStringValue( userWebsiteCell );
                String userLicenses = workbookOperations.getStringValue( userLicensesCell );
                String userLegalDisclaimer = workbookOperations.getStringValue( userLegalDisclaimerCell );
                String userPhotoProfileURL = workbookOperations.getStringValue( userPhotoProfileURLCell );
                String userAboutMe = workbookOperations.getStringValue( userAboutMeCell );
                boolean userSendEmail = CommonConstants.YES_STRING
                    .equals( workbookOperations.getStringValue( userSendEmailCell ) );
                String tempSourceId = workbookOperations.getStringValue( userSourceIdCell );

                String userBranchIdAdminStr = workbookOperations.getStringValue( userBranchIdAdminCell );
                HashSet<String> userBranchIdAdminSet = StringUtils.isNotEmpty( userBranchIdAdminStr )
                    ? Sets.newHashSet( userBranchIdAdminStr.split( CommonConstants.COMMA_SEPERATOR_PATTERN ) )
                    : null;

                String userRegionIdAdminStr = workbookOperations.getStringValue( userRegionIdAdminCell );
                HashSet<String> userRegionIdAdminSet = StringUtils.isNotEmpty( userRegionIdAdminStr )
                    ? Sets.newHashSet( userRegionIdAdminStr.split( CommonConstants.COMMA_SEPERATOR_PATTERN ) )
                    : null;

                String userBranchIdStr = workbookOperations.getStringValue( userBranchIdCell );
                HashSet<String> userBranchIdSet = StringUtils.isNotEmpty( userBranchIdStr )
                    ? Sets.newHashSet( userBranchIdStr.split( CommonConstants.COMMA_SEPERATOR_PATTERN ) )
                    : null;

                String userRegionIdStr = workbookOperations.getStringValue( userRegionIdCell );
                HashSet<String> userRegionIdSet = StringUtils.isNotEmpty( userRegionIdStr )
                    ? Sets.newHashSet( userRegionIdStr.split( CommonConstants.COMMA_SEPERATOR_PATTERN ) )
                    : null;


                // check for a row with empty data        
                if ( isUserRowEmpty( tempSourceId, userFirstName, userLastName, userTitle, userEmail, userPhoneNumber,
                    userWebsite, userLicenses, userLegalDisclaimer, userPhotoProfileURL, userAboutMe ) ) {
                    continue;
                }

                UserUploadVO currentUserVO = null;
                boolean isSourceIdUndetectable = false;
                int sourceIdDuplicate = 0;


                if ( userSourceIdCell != null ) {
                    sourceUserId = workbookOperations.getStringValue( userSourceIdCell );
                    isSourceIdUndetectable = StringUtils.isEmpty( sourceUserId );
                } else {
                    isSourceIdUndetectable = true;
                }

                sourceUserId = StringUtils.defaultIfEmpty( sourceUserId, generateSourceIdArbitrarily( TYPE_USER ) );


                // Analyze and obtain the required VO 
                if ( !hierarchyIntermediate.getUsers().containsKey( sourceUserId ) ) {

                    currentUserVO = new UserUploadVO();
                    currentUserVO.setSourceUserId( sourceUserId );
                    currentUserVO.setUserAdded( true );
                    parsedHierarchyUpload.setNumberOfUsersAdded( parsedHierarchyUpload.getNumberOfUsersAdded() + 1 );

                } else {

                    if ( !hierarchyIntermediate.getUsers().get( sourceUserId ).isUserProcessed() ) {
                        currentUserVO = hierarchyIntermediate.getUsers().get( sourceUserId );
                    } else {
                        sourceIdDuplicate = hierarchyIntermediate.getUsers().get( sourceUserId ).getRowNum();
                        sourceUserId = sourceUserId + CommonConstants.DUPLICATE_SOURCE_ID_SUBSTRING
                            + utils.generateRandomAlphaNumericString();
                        currentUserVO = new UserUploadVO();
                        currentUserVO.setUserAdded( true );
                        currentUserVO.setSourceUserId( sourceUserId );
                        parsedHierarchyUpload.setNumberOfUsersAdded( parsedHierarchyUpload.getNumberOfUsersAdded() + 1 );
                    }
                }

                // set the row number for the VO
                currentUserVO.setRowNum( row.getRowNum() + 1 );

                if ( isSourceIdUndetectable ) {
                    parsedHierarchyUpload.getUserValidationWarnings()
                        .add( "Row: " + currentUserVO.getRowNum() + ", Unable to parse source ID, generated a new one." );
                    currentUserVO.setWarningRecord( true );
                }

                if ( sourceIdDuplicate != 0 ) {
                    parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum()
                        + ", This source ID is a Duplicate, has already been parsed  at row : " + sourceIdDuplicate );
                    currentUserVO.setErrorRecord( true );
                }
                
                if ( userPhotoProfileURL != null && !userPhotoProfileURL.isEmpty() && !isImagevalid( userPhotoProfileURL ) ) {
                    parsedHierarchyUpload.getUserValidationWarnings()
                        .add( "Row: " + currentUserVO.getRowNum() + ", Unable to parse image for url:" + userPhotoProfileURL );
                    currentUserVO.setWarningRecord( true );
                }


                processUser( hierarchyIntermediate, parsedHierarchyUpload, currentUserVO, applicationEmailIdList,
                    companyEmailIdList, emailsUploadedMap, userFirstName, userLastName, userTitle, userBranchIdSet,
                    userRegionIdSet, userBranchIdAdminSet, userRegionIdAdminSet, userEmail, userPhoneNumber, userWebsite,
                    userLicenses, userLegalDisclaimer, userPhotoProfileURL, userAboutMe, userSendEmail );

                if ( currentUserVO.isUserModified() ) {
                    parsedHierarchyUpload.setNumberOfUsersModified( parsedHierarchyUpload.getNumberOfUsersModified() + 1 );
                }


                // mark user as processed
                currentUserVO.setUserProcessed( true );
                hierarchyIntermediate.setUsersProcessed( hierarchyIntermediate.getUsersProcessed() + 1 );

                //add the user to the intermediate object
                hierarchyIntermediate.getUsers().put( currentUserVO.getSourceUserId(), currentUserVO );

            }

            // set global error and warning flags
            if ( !parsedHierarchyUpload.getUserErrors().isEmpty() ) {
                parsedHierarchyUpload.setHasErrors( true );
            }

            if ( !parsedHierarchyUpload.getUserValidationWarnings().isEmpty() ) {
                parsedHierarchyUpload.setHasWarnings( true );
            }

        }

    }


    private boolean isUserRowEmpty( String tempSourceId, String userFirstName, String userLastName, String userTitle,
        String userEmail, String userPhoneNumber, String userWebsite, String userLicenses, String userLegalDisclaimer,
        String userPhotoProfileURL, String userAboutMe )
    {
        if ( StringUtils.isEmpty( tempSourceId ) && StringUtils.isEmpty( userLastName ) && StringUtils.isEmpty( userFirstName )
            && StringUtils.isEmpty( userAboutMe ) && StringUtils.isEmpty( userPhotoProfileURL )
            && StringUtils.isEmpty( userLegalDisclaimer ) && StringUtils.isEmpty( userLicenses )
            && StringUtils.isEmpty( userWebsite ) && StringUtils.isEmpty( userPhoneNumber ) && StringUtils.isEmpty( userEmail )
            && StringUtils.isEmpty( userTitle ) ) {
            return true;
        }
        return false;
    }


    private void processUser( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        UserUploadVO currentUserVO, List<String> applicationEmailIdList, List<String> companyEmailIdList,
        BiMap<String, String> emailsUploadedMap, String userFirstName, String userLastName, String userTitle,
        Set<String> userBranchIdSet, Set<String> userRegionIdSet, Set<String> userBranchIdAdminSet,
        Set<String> userRegionIdAdminSet, String userEmail, String userPhoneNumber, String userWebsite, String userLicenses,
        String userLegalDisclaimer, String userPhotoProfileURL, String userAboutMe, boolean userSendEmail )
    {
        boolean isAssignmentModified = false;
        if ( !StringUtils.equals( userFirstName, currentUserVO.getFirstName() ) && !StringUtils.isEmpty( userFirstName ) ) {

            if ( currentUserVO.getFirstNameHistory() == null )
                currentUserVO.setFirstNameHistory( new ArrayList<StringUploadHistory>() );

            // append to user first name history
            currentUserVO.getFirstNameHistory().add( new StringUploadHistory( currentUserVO.getFirstName(), new Date() ) );

            // update with the new first name
            currentUserVO.setFirstName( userFirstName );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        } else if ( StringUtils.isEmpty( userFirstName ) ) {

            // set errors for the first name empty          
            parsedHierarchyUpload.getUserErrors()
                .add( "Row: " + currentUserVO.getRowNum() + ", User first name not provided." );
            currentUserVO.setErrorRecord( true );

        }


        if ( !StringUtils.equals( userTitle, currentUserVO.getTitle() ) && !StringUtils.isEmpty( userTitle ) ) {

            if ( currentUserVO.getTitleHistory() == null )
                currentUserVO.setTitleHistory( new ArrayList<StringUploadHistory>() );

            // append to user title history
            currentUserVO.getTitleHistory().add( new StringUploadHistory( currentUserVO.getTitle(), new Date() ) );

            // update with the new title
            currentUserVO.setTitle( userTitle );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }

        if ( !StringUtils.equals( userPhoneNumber, currentUserVO.getPhoneNumber() )
            && !StringUtils.isEmpty( userPhoneNumber ) ) {

            if ( currentUserVO.getPhoneNumberHistory() == null )
                currentUserVO.setPhoneNumberHistory( new ArrayList<StringUploadHistory>() );

            // append to phone number history
            currentUserVO.getPhoneNumberHistory().add( new StringUploadHistory( currentUserVO.getPhoneNumber(), new Date() ) );

            // update with the new phone number
            currentUserVO.setPhoneNumber( userPhoneNumber );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }


        if ( !StringUtils.equals( userWebsite, currentUserVO.getWebsiteUrl() ) && !StringUtils.isEmpty( userWebsite ) ) {

            if ( currentUserVO.getWebsiteUrlHistory() == null )
                currentUserVO.setWebsiteUrlHistory( new ArrayList<StringUploadHistory>() );

            // append to user Web site history
            currentUserVO.getWebsiteUrlHistory().add( new StringUploadHistory( currentUserVO.getWebsiteUrl(), new Date() ) );

            // update with the new user Web site
            currentUserVO.setWebsiteUrl( userWebsite );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }


        if ( !StringUtils.equals( userLicenses, currentUserVO.getLicense() ) && !StringUtils.isEmpty( userLicenses ) ) {

            if ( currentUserVO.getLicenseHistory() == null )
                currentUserVO.setLicenseHistory( new ArrayList<StringUploadHistory>() );

            // append to user Licenses history
            currentUserVO.getLicenseHistory().add( new StringUploadHistory( currentUserVO.getLicense(), new Date() ) );

            // update with the new user Licenses
            currentUserVO.setLicense( userLicenses );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }

        if ( !StringUtils.equals( userPhotoProfileURL, currentUserVO.getUserPhotoUrl() )
            && !StringUtils.isEmpty( userPhotoProfileURL ) ) {

            if ( currentUserVO.getUserPhotoUrlHistory() == null )
                currentUserVO.setUserPhotoUrlHistory( new ArrayList<StringUploadHistory>() );

            // append to user Photo Profile URL history
            currentUserVO.getUserPhotoUrlHistory()
                .add( new StringUploadHistory( currentUserVO.getUserPhotoUrl(), new Date() ) );

            // update with the new user Photo Profile URL
            currentUserVO.setUserPhotoUrl( userPhotoProfileURL );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }


        // initialize sets
        if ( userBranchIdSet == null ) {
            userBranchIdSet = new HashSet<>();
        }
        if ( currentUserVO.getAssignedBranches() == null ) {
            currentUserVO.setAssignedBranches( new HashSet<String>() );
        }

        if ( !userBranchIdSet.isEmpty()
            && !Sets.symmetricDifference( userBranchIdSet, currentUserVO.getAssignedBranches() ).isEmpty() ) {

            if ( currentUserVO.getAssignedBranchesHistory() == null )
                currentUserVO.setAssignedBranchesHistory( new ArrayList<StringSetUploadHistory>() );

            // append to user Branch Id Set history
            currentUserVO.getAssignedBranchesHistory()
                .add( new StringSetUploadHistory( currentUserVO.getAssignedBranches(), new Date() ) );

            // update with the new user Branch Id Set
            currentUserVO.setAssignedBranchesModified( true );
            currentUserVO.setAssignedBranches( userBranchIdSet );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

            isAssignmentModified = true;

            // checking for invalid assignments
            for ( String sourceBranchId : userBranchIdSet ) {
                if ( hierarchyIntermediate.getBranches() != null
                    && hierarchyIntermediate.getBranches().containsKey( sourceBranchId ) ) {

                    if ( !parsedHierarchyUpload.isInAppendMode()
                        && !hierarchyIntermediate.getBranches().get( sourceBranchId ).isBranchProcessed() ) {
                        parsedHierarchyUpload.getUserErrors()
                            .add( "Row: " + currentUserVO.getRowNum() + ", The branch assignment: " + sourceBranchId
                                + " is not valid \n The assigned branch is flagged for deletion in this mode." );
                        currentUserVO.setErrorRecord( true );
                    }

                } else {
                    parsedHierarchyUpload.getUserErrors().add(
                        "Row: " + currentUserVO.getRowNum() + ", The branch assignment: " + sourceBranchId + " is not valid." );
                    currentUserVO.setErrorRecord( true );
                }
            }

        }


        // initialize sets
        if ( userRegionIdSet == null ) {
            userRegionIdSet = new HashSet<String>();
        }
        if ( currentUserVO.getAssignedRegions() == null ) {
            currentUserVO.setAssignedRegions( new HashSet<String>() );
        }

        if ( !Sets.symmetricDifference( userRegionIdSet, currentUserVO.getAssignedRegions() ).isEmpty()
            && !userRegionIdSet.isEmpty() ) {

            if ( currentUserVO.getAssignedRegionsHistory() == null )
                currentUserVO.setAssignedRegionsHistory( new ArrayList<StringSetUploadHistory>() );

            // append to user Region Id Set history
            currentUserVO.getAssignedRegionsHistory()
                .add( new StringSetUploadHistory( currentUserVO.getAssignedRegions(), new Date() ) );

            // update with the new user Region Id Set
            currentUserVO.setAssignedRegionsModified( true );
            currentUserVO.setAssignedRegions( userRegionIdSet );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

            isAssignmentModified = true;

            // checking for invalid assignments
            for ( String sourceRegionId : userRegionIdSet ) {
                if ( hierarchyIntermediate.getRegions() != null
                    && hierarchyIntermediate.getRegions().containsKey( sourceRegionId ) ) {

                    if ( !parsedHierarchyUpload.isInAppendMode()
                        && !hierarchyIntermediate.getRegions().get( sourceRegionId ).isRegionProcessed() ) {
                        parsedHierarchyUpload.getUserErrors()
                            .add( "Row: " + currentUserVO.getRowNum() + ", The region assignment: " + sourceRegionId
                                + " is not valid \n The assigned region is flagged for deletion in this mode." );
                        currentUserVO.setErrorRecord( true );
                    }

                } else {
                    parsedHierarchyUpload.getUserErrors().add(
                        "Row: " + currentUserVO.getRowNum() + ", The region assignment: " + sourceRegionId + " is not valid." );
                    currentUserVO.setErrorRecord( true );
                }
            }

        }


        // initialize sets
        if ( userRegionIdAdminSet == null ) {
            userRegionIdAdminSet = new HashSet<String>();
        }
        if ( currentUserVO.getAssignedRegionsAdmin() == null ) {
            currentUserVO.setAssignedRegionsAdmin( new HashSet<String>() );
        }

        if ( !Sets.symmetricDifference( userRegionIdAdminSet, currentUserVO.getAssignedRegionsAdmin() ).isEmpty()
            && !userRegionIdAdminSet.isEmpty() ) {

            if ( currentUserVO.getAssignedRegionsAdminHistory() == null )
                currentUserVO.setAssignedRegionsAdminHistory( new ArrayList<StringSetUploadHistory>() );

            // append to user Region Id Admin Set history
            currentUserVO.getAssignedRegionsAdminHistory()
                .add( new StringSetUploadHistory( currentUserVO.getAssignedRegionsAdmin(), new Date() ) );

            // update with the new user Region Id Admin Set
            currentUserVO.setAssignedRegionsAdminModified( true );
            currentUserVO.setAssignedRegionsAdmin( userRegionIdAdminSet );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

            isAssignmentModified = true;

            // checking for invalid assignments
            for ( String sourceRegionIdAdmin : userRegionIdAdminSet ) {
                if ( hierarchyIntermediate.getRegions() != null
                    && hierarchyIntermediate.getRegions().containsKey( sourceRegionIdAdmin ) ) {

                    if ( !parsedHierarchyUpload.isInAppendMode()
                        && !hierarchyIntermediate.getRegions().get( sourceRegionIdAdmin ).isRegionProcessed() ) {
                        parsedHierarchyUpload.getUserErrors()
                            .add( "Row: " + currentUserVO.getRowNum() + ", The region admin assignment: " + sourceRegionIdAdmin
                                + " is not valid \n The assigned region is flagged for deletion in this mode." );
                        currentUserVO.setErrorRecord( true );
                    }

                } else {
                    parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum()
                        + ", The region admin assignment: " + sourceRegionIdAdmin + " is not valid." );
                    currentUserVO.setErrorRecord( true );
                }
            }

        }


        // initialize sets
        if ( userBranchIdAdminSet == null ) {
            userBranchIdAdminSet = new HashSet<String>();
        }
        if ( currentUserVO.getAssignedBranchesAdmin() == null ) {
            currentUserVO.setAssignedBranchesAdmin( new HashSet<String>() );
        }

        if ( !Sets.symmetricDifference( userBranchIdAdminSet, currentUserVO.getAssignedBranchesAdmin() ).isEmpty()
            && !userBranchIdAdminSet.isEmpty() ) {

            if ( currentUserVO.getAssignedBranchesAdminHistory() == null )
                currentUserVO.setAssignedBranchesAdminHistory( new ArrayList<StringSetUploadHistory>() );

            // append to user Branch Id Admin Set history
            currentUserVO.getAssignedBranchesAdminHistory()
                .add( new StringSetUploadHistory( currentUserVO.getAssignedBranchesAdmin(), new Date() ) );

            // update with the new user Branch Id Admin Set
            currentUserVO.setAssignedBranchesAdminModified( true );
            currentUserVO.setAssignedBranchesAdmin( userBranchIdAdminSet );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

            isAssignmentModified = true;

            // checking for invalid assignments
            for ( String sourceBranchIdAdmin : userBranchIdAdminSet ) {
                if ( hierarchyIntermediate.getBranches() != null
                    && hierarchyIntermediate.getBranches().containsKey( sourceBranchIdAdmin ) ) {

                    if ( !parsedHierarchyUpload.isInAppendMode()
                        && !hierarchyIntermediate.getBranches().get( sourceBranchIdAdmin ).isBranchProcessed() ) {
                        parsedHierarchyUpload.getUserErrors()
                            .add( "Row: " + currentUserVO.getRowNum() + ", The branch admin assignment: " + sourceBranchIdAdmin
                                + " is not valid \n The assigned branch is flagged for deletion in this mode." );
                        currentUserVO.setErrorRecord( true );
                    }

                } else {
                    parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum()
                        + ", The branch admin assignment: " + sourceBranchIdAdmin + " is not valid." );
                    currentUserVO.setErrorRecord( true );
                }
            }

        }

        if ( currentUserVO.getAssignedBranches().isEmpty() && currentUserVO.getAssignedRegions().isEmpty()
            && currentUserVO.getAssignedBranchesAdmin().isEmpty() && currentUserVO.getAssignedRegionsAdmin().isEmpty()
            && ( currentUserVO.isUserAdded() || isAssignmentModified ) ) {

            // default company assignment
            parsedHierarchyUpload.getUserValidationWarnings().add( "Row: " + currentUserVO.getRowNum()
                + ", user is not related to any hierarchy, wil be assigned as an agent under the company" );
            currentUserVO.setWarningRecord( true );
        }

        if ( !emailsUploadedMap.containsValue( userEmail ) ) {
            emailsUploadedMap.put( currentUserVO.getSourceUserId(), userEmail );
        } else {
            parsedHierarchyUpload.getUserErrors()
                .add( "Row: " + currentUserVO.getRowNum() + ", The Email ID entered is duplicate of a row at : "
                    + hierarchyIntermediate.getUsers().get( emailsUploadedMap.inverse().get( userEmail ) ).getRowNum() );
            currentUserVO.setErrorRecord( true );
        }

        if ( !StringUtils.equals( userEmail, currentUserVO.getEmailId() ) && !StringUtils.isEmpty( userEmail ) ) {

            if ( currentUserVO.getEmailIdHistory() == null )
                currentUserVO.setEmailIdHistory( new ArrayList<StringUploadHistory>() );

            // append to user Email history
            currentUserVO.getEmailIdHistory().add( new StringUploadHistory( currentUserVO.getEmailId(), new Date() ) );

            // update with the new user Email
            currentUserVO.setEmailModified( true );
            currentUserVO.setEmailId( userEmail );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

            // check for syntax
            if ( userEmail.contains( "\"" ) || userEmail.contains( "<" ) || userEmail.contains( ">" ) ) {
                parsedHierarchyUpload.getUserErrors()
                    .add( "Row: " + currentUserVO.getRowNum() + ", Email ID entered is not in valid format." );
                currentUserVO.setErrorRecord( true );
            }

            // check for email ID duplication across social survey and the provided list of users outside of the company
            if ( applicationEmailIdList.contains(
                CommonConstants.YES_STRING.equals( maskEmail ) ? utils.maskEmailAddress( userEmail ) : userEmail ) ) {
                parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum()
                    + ", Email ID entered already exists in SocialSurvey in another account." );
                currentUserVO.setErrorRecord( true );
            }

            // check if the email ID is registered within the company
            if ( currentUserVO.isUserAdded() && companyEmailIdList.contains(
                CommonConstants.YES_STRING.equals( maskEmail ) ? utils.maskEmailAddress( userEmail ) : userEmail ) ) {
                parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum()
                    + ", Cannot create this user, Email ID entered is already registered for this account. Please use the source ID generated by SocialSurvey for this user provided in company Hierarchy report in order to modify this user." );
                currentUserVO.setErrorRecord( true );
            }

        } else if ( StringUtils.isEmpty( userEmail ) ) {

            // set errors for the empty user email 
            parsedHierarchyUpload.getUserErrors().add( "Row: " + currentUserVO.getRowNum() + ", User email not provided." );
            currentUserVO.setErrorRecord( true );
        }


        if ( !StringUtils.equals( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userLastName ),
            utils.convert_ISO_8859_1_To_UTF_8_Encoding( currentUserVO.getLastName() ) )
            && !StringUtils.isEmpty( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userLastName ) ) ) {

            if ( currentUserVO.getLastNameHistory() == null )
                currentUserVO.setLastNameHistory( new ArrayList<StringUploadHistory>() );

            // append to user Last Name history
            currentUserVO.getLastNameHistory().add( new StringUploadHistory(
                utils.convert_ISO_8859_1_To_UTF_8_Encoding( currentUserVO.getLastName() ), new Date() ) );

            // update with the new user Last Name
            currentUserVO.setLastName( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userLastName ) );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }


        if ( !StringUtils.equals( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userLegalDisclaimer ),
            utils.convert_ISO_8859_1_To_UTF_8_Encoding( currentUserVO.getLegalDisclaimer() ) )
            && !StringUtils.isEmpty( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userLegalDisclaimer ) ) ) {

            if ( currentUserVO.getLegalDisclaimerHistory() == null )
                currentUserVO.setLegalDisclaimerHistory( new ArrayList<StringUploadHistory>() );

            // append to user Legal Disclaimer history
            currentUserVO.getLegalDisclaimerHistory()
                .add( new StringUploadHistory( currentUserVO.getLegalDisclaimer(), new Date() ) );

            // update with the new user Legal Disclaimer
            currentUserVO.setLegalDisclaimer( userLegalDisclaimer );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }

        }

        if ( !StringUtils.equals( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userAboutMe ),
            utils.convert_ISO_8859_1_To_UTF_8_Encoding( currentUserVO.getAboutMeDescription() ) )
            && !StringUtils.isEmpty( utils.convert_ISO_8859_1_To_UTF_8_Encoding( userAboutMe ) ) ) {

            if ( currentUserVO.getAboutMeDescriptionHistory() == null )
                currentUserVO.setAboutMeDescriptionHistory( new ArrayList<StringUploadHistory>() );

            // append to user About Me history
            currentUserVO.getAboutMeDescriptionHistory()
                .add( new StringUploadHistory( currentUserVO.getAboutMeDescription(), new Date() ) );

            // update with the new user About Me
            currentUserVO.setAboutMeDescription( userAboutMe );

            // set modified record flag
            if ( !currentUserVO.isUserAdded() ) {
                currentUserVO.setUserModified( true );
            }
        }

        currentUserVO.setSendMail( userSendEmail );

    }


    private void parseAndVerifyBranches( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, Iterator<Row> branchesIterator )
    {
        if ( branchesIterator != null ) {

            // initialize branch error and warning list
            if ( parsedHierarchyUpload.getBranchValidationWarnings() == null ) {
                parsedHierarchyUpload.setBranchValidationWarnings( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getBranchValidationWarnings().clear();
            }


            if ( parsedHierarchyUpload.getBranchErrors() == null ) {
                parsedHierarchyUpload.setBranchErrors( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getBranchErrors().clear();
            }

            // initialize other necessary objects
            if ( branchesIterator.hasNext() ) {

                if ( hierarchyIntermediate.getBranches() == null ) {
                    hierarchyIntermediate.setBranches( new HashMap<String, BranchUploadVO>() );
                }

                if ( hierarchyIntermediate.getBranchNameMap() == null ) {
                    hierarchyIntermediate.setBranchNameMap( new HashMap<String, String>() );
                }
            }

            while ( branchesIterator.hasNext() ) {

                String sourceBranchId = "";
                Row row = branchesIterator.next();

                // discard the current row if it is empty or doesn't have values
                if ( row.getLastCellNum() <= 0 || row.getPhysicalNumberOfCells() < 1 ) {
                    continue;
                }

                Cell branchSourceIdCell = row.getCell( BRANCH_ID_INDEX );
                Cell branchNameCell = row.getCell( BRANCH_NAME_INDEX );
                Cell sourceRegionIdCell = row.getCell( BRANCH_REGION_ID_INDEX );
                Cell branchAddress1Cell = row.getCell( BRANCH_ADDRESS1_INDEX );
                Cell branchAddress2Cell = row.getCell( BRANCH_ADDRESS2_INDEX );
                Cell branchCityCell = row.getCell( BRANCH_CITY_INDEX );
                Cell branchStateCell = row.getCell( BRANCH_STATE_INDEX );
                Cell branchZipCodeCell = row.getCell( BRANCH_ZIP_INDEX );

                String branchName = workbookOperations.getStringValue( branchNameCell );
                String sourceRegionId = workbookOperations.getStringValue( sourceRegionIdCell );
                String branchAddress1 = workbookOperations.getStringValue( branchAddress1Cell );
                String branchAddress2 = workbookOperations.getStringValue( branchAddress2Cell );
                String branchCity = workbookOperations.getStringValue( branchCityCell );
                String branchState = workbookOperations.getStringValue( branchStateCell );
                String branchZipCode = workbookOperations.getStringValue( branchZipCodeCell );
                String tempSourceId = workbookOperations.getStringValue( branchSourceIdCell );


                // check for a row with empty data        
                if ( isBranchRowEmpty( tempSourceId, branchName, branchAddress1, branchAddress2, branchCity, branchState,
                    branchZipCode ) ) {
                    continue;
                }

                BranchUploadVO currentBranchVO = null;
                boolean isSourceIdUndetectable = false;
                int sourceIdDuplicate = 0;


                if ( branchSourceIdCell != null ) {
                    sourceBranchId = workbookOperations.getStringValue( branchSourceIdCell );
                    isSourceIdUndetectable = StringUtils.isEmpty( sourceBranchId );
                } else {
                    isSourceIdUndetectable = true;
                }

                sourceBranchId = StringUtils.defaultIfEmpty( sourceBranchId, generateSourceIdArbitrarily( TYPE_BRANCH ) );


                // Analyze and obtain the required VO 
                if ( !hierarchyIntermediate.getBranches().containsKey( sourceBranchId ) ) {

                    currentBranchVO = new BranchUploadVO();
                    currentBranchVO.setSourceBranchId( sourceBranchId );
                    currentBranchVO.setBranchAdded( true );
                    parsedHierarchyUpload.setNumberOfBranchesAdded( parsedHierarchyUpload.getNumberOfBranchesAdded() + 1 );

                } else {

                    if ( !hierarchyIntermediate.getBranches().get( sourceBranchId ).isBranchProcessed() ) {
                        currentBranchVO = hierarchyIntermediate.getBranches().get( sourceBranchId );
                    } else {
                        sourceIdDuplicate = hierarchyIntermediate.getBranches().get( sourceBranchId ).getRowNum();
                        sourceBranchId = sourceBranchId + CommonConstants.DUPLICATE_SOURCE_ID_SUBSTRING
                            + utils.generateRandomAlphaNumericString();
                        currentBranchVO = new BranchUploadVO();
                        currentBranchVO.setBranchAdded( true );
                        currentBranchVO.setSourceBranchId( sourceBranchId );
                        parsedHierarchyUpload.setNumberOfBranchesAdded( parsedHierarchyUpload.getNumberOfBranchesAdded() + 1 );
                    }
                }


                // set the row number for the VO
                currentBranchVO.setRowNum( row.getRowNum() + 1 );


                if ( isSourceIdUndetectable ) {
                    parsedHierarchyUpload.getBranchValidationWarnings()
                        .add( "Row: " + currentBranchVO.getRowNum() + ", Unable to parse source ID, generated a new one." );
                    currentBranchVO.setWarningRecord( true );
                }

                if ( sourceIdDuplicate != 0 ) {
                    parsedHierarchyUpload.getBranchErrors().add( "Row: " + currentBranchVO.getRowNum()
                        + " This source ID is a Duplicate, has already been parsed  at row : " + sourceIdDuplicate );
                    currentBranchVO.setErrorRecord( true );
                }


                processBranch( hierarchyIntermediate, parsedHierarchyUpload, currentBranchVO, branchName, sourceRegionId,
                    branchAddress1, branchAddress2, branchCity, branchState, branchZipCode );

                if ( currentBranchVO.isBranchModified() ) {
                    parsedHierarchyUpload
                        .setNumberOfBranchesModified( parsedHierarchyUpload.getNumberOfBranchesModified() + 1 );
                }

                // mark branch as processed
                currentBranchVO.setBranchProcessed( true );
                hierarchyIntermediate.setBranchesProcessed( hierarchyIntermediate.getBranchesProcessed() + 1 );

                //add the branch to the intermediate object
                hierarchyIntermediate.getBranches().put( currentBranchVO.getSourceBranchId(), currentBranchVO );

            }

            // set global error and warning flags
            if ( !parsedHierarchyUpload.getBranchErrors().isEmpty() ) {
                parsedHierarchyUpload.setHasErrors( true );
            }

            if ( !parsedHierarchyUpload.getBranchValidationWarnings().isEmpty() ) {
                parsedHierarchyUpload.setHasWarnings( true );
            }
        }

    }


    private boolean isBranchRowEmpty( String tempSourceId, String branchName, String branchAddress1, String branchAddress2,
        String branchCity, String branchState, String branchZipCode )
    {
        if ( StringUtils.isEmpty( tempSourceId ) && StringUtils.isEmpty( branchName ) && StringUtils.isEmpty( branchAddress1 )
            && StringUtils.isEmpty( branchAddress2 ) && StringUtils.isEmpty( branchCity ) && StringUtils.isEmpty( branchState )
            && StringUtils.isEmpty( branchZipCode ) ) {
            return true;
        }
        return false;
    }


    private void processBranch( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        BranchUploadVO currentBranchVO, String branchName, String sourceRegionId, String branchAddress1, String branchAddress2,
        String branchCity, String branchState, String branchZipCode )
    {
        boolean isAssignmentModified = false;
        if ( !StringUtils.equals( branchName, currentBranchVO.getBranchName() ) && !StringUtils.isEmpty( branchName ) ) {

            if ( currentBranchVO.getBranchNameHistory() == null )
                currentBranchVO.setBranchNameHistory( new ArrayList<StringUploadHistory>() );

            // append to branch name history
            currentBranchVO.getBranchNameHistory()
                .add( new StringUploadHistory( currentBranchVO.getBranchName(), new Date() ) );

            // update with the new name
            currentBranchVO.setBranchName( branchName );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );

            // check for name duplication
            String branchSourceIdWithExistingName = containsStringValue( hierarchyIntermediate.getBranchNameMap(), branchName );
            if ( StringUtils.isNotEmpty( branchSourceIdWithExistingName ) ) {

                BranchUploadVO branch = hierarchyIntermediate.getBranches().get( branchSourceIdWithExistingName );
                if ( branch.isBranchProcessed() && branch.isBranchAdded() ) {
                    parsedHierarchyUpload.getBranchValidationWarnings().add( "Row: " + currentBranchVO.getRowNum()
                        + ", A Branch with the same already exists at : " + branch.getRowNum() );
                } else if ( branch.isBranchProcessed() && !branch.isBranchAdded() ) {
                    parsedHierarchyUpload.getBranchValidationWarnings().add(
                        "Row: " + currentBranchVO.getRowNum() + ", A Branch with the same already exists in the company." );
                }

                branch.setWarningRecord( true );
            } else {
                hierarchyIntermediate.getBranchNameMap().put( currentBranchVO.getSourceBranchId(),
                    currentBranchVO.getBranchName() );
            }

        } else if ( StringUtils.isEmpty( branchName ) ) {

            // set errors for the branch name         
            parsedHierarchyUpload.getBranchErrors()
                .add( "Row: " + currentBranchVO.getRowNum() + ", Branch name not provided." );
            currentBranchVO.setErrorRecord( true );

        }


        if ( !StringUtils.equals( sourceRegionId, currentBranchVO.getSourceRegionId() )
            && !StringUtils.isEmpty( sourceRegionId ) ) {

            if ( currentBranchVO.getSourceRegionIdHistory() == null )
                currentBranchVO.setSourceRegionIdHistory( new ArrayList<StringUploadHistory>() );

            // append to source region ID history
            currentBranchVO.getSourceRegionIdHistory()
                .add( new StringUploadHistory( currentBranchVO.getSourceRegionId(), new Date() ) );

            // update with the new source region ID
            currentBranchVO.setSourceRegionId( sourceRegionId );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );

            isAssignmentModified = true;

            // check for valid region assignment
            if ( hierarchyIntermediate.getRegions().containsKey( sourceRegionId ) ) {

                RegionUploadVO parentRegion = hierarchyIntermediate.getRegions().get( sourceRegionId );
                if ( !parentRegion.isRegionProcessed() && !parsedHierarchyUpload.isInAppendMode() ) {
                    parsedHierarchyUpload.getBranchErrors()
                        .add( "Row: " + currentBranchVO.getRowNum() + ", The region assignment is invalid" );
                }
            }

        } else if ( StringUtils.isEmpty( sourceRegionId ) && ( currentBranchVO.isBranchAdded() || isAssignmentModified ) ) {

            // set warning for the branch          
            parsedHierarchyUpload.getBranchValidationWarnings().add( "Row: " + currentBranchVO.getRowNum()
                + ", Region ID is not provided.\n Current branch will be assigned the company." );
            currentBranchVO.setWarningRecord( true );

        }


        if ( !StringUtils.equals( branchAddress1, currentBranchVO.getBranchAddress1() )
            && !StringUtils.isEmpty( branchAddress1 ) ) {

            if ( currentBranchVO.getBranchAddress1History() == null )
                currentBranchVO.setBranchAddress1History( new ArrayList<StringUploadHistory>() );

            // append to Branch address1 history
            currentBranchVO.getBranchAddress1History()
                .add( new StringUploadHistory( currentBranchVO.getBranchAddress1(), new Date() ) );

            // update with the new address1
            currentBranchVO.setBranchAddress1( branchAddress1 );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );
        }


        if ( !StringUtils.equals( branchAddress2, currentBranchVO.getBranchAddress2() )
            && !StringUtils.isEmpty( branchAddress2 ) ) {

            if ( currentBranchVO.getBranchAddress2History() == null )
                currentBranchVO.setBranchAddress2History( new ArrayList<StringUploadHistory>() );

            // append to Branch address2 history
            currentBranchVO.getBranchAddress2History()
                .add( new StringUploadHistory( currentBranchVO.getBranchAddress2(), new Date() ) );

            // update with the new address2
            currentBranchVO.setBranchAddress2( branchAddress2 );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );

        } else if ( StringUtils.isEmpty( branchAddress2 ) ) {

            // address must be provided
            if ( StringUtils.isEmpty( currentBranchVO.getBranchAddress1() )
                && StringUtils.isEmpty( currentBranchVO.getBranchAddress2() ) ) {
                parsedHierarchyUpload.getBranchErrors()
                    .add( "Row: " + currentBranchVO.getRowNum() + ", At least one address must be provided." );
                currentBranchVO.setErrorRecord( true );
            }
        }


        if ( !StringUtils.equals( branchCity, currentBranchVO.getBranchCity() ) && !StringUtils.isEmpty( branchCity ) ) {

            if ( currentBranchVO.getBranchCityHistory() == null )
                currentBranchVO.setBranchCityHistory( new ArrayList<StringUploadHistory>() );

            // append to Branch city history
            currentBranchVO.getBranchCityHistory()
                .add( new StringUploadHistory( currentBranchVO.getBranchCity(), new Date() ) );

            // update with the new City
            currentBranchVO.setBranchCity( branchCity );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );
        }


        if ( !StringUtils.equals( branchState, currentBranchVO.getBranchState() ) && !StringUtils.isEmpty( branchState ) ) {

            if ( currentBranchVO.getBranchStateHistory() == null )
                currentBranchVO.setBranchStateHistory( new ArrayList<StringUploadHistory>() );

            // append to Branch state history
            currentBranchVO.getBranchStateHistory()
                .add( new StringUploadHistory( currentBranchVO.getBranchState(), new Date() ) );

            // update with the new state
            currentBranchVO.setBranchState( branchState );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );
        }

        if ( !StringUtils.equals( branchZipCode, currentBranchVO.getBranchZipcode() )
            && !StringUtils.isEmpty( branchZipCode ) ) {

            if ( currentBranchVO.getBranchZipcodeHistory() == null )
                currentBranchVO.setBranchZipcodeHistory( new ArrayList<StringUploadHistory>() );

            // append to Branch zipcode history
            currentBranchVO.getBranchZipcodeHistory()
                .add( new StringUploadHistory( currentBranchVO.getBranchZipcode(), new Date() ) );

            // update with the new zipcode
            currentBranchVO.setBranchZipcode( branchZipCode );

            // set modified record flag
            if ( !currentBranchVO.isBranchAdded() )
                currentBranchVO.setBranchModified( true );
        }
    }


    private void parseAndVerifyRegions( HierarchyUploadIntermediate hierarchyIntermediate,
        ParsedHierarchyUpload parsedHierarchyUpload, Iterator<Row> regionsIterator )
    {
        if ( regionsIterator != null ) {

            // initialize region error and warning list
            if ( parsedHierarchyUpload.getRegionValidationWarnings() == null ) {
                parsedHierarchyUpload.setRegionValidationWarnings( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getRegionValidationWarnings().clear();
            }

            if ( parsedHierarchyUpload.getRegionErrors() == null ) {
                parsedHierarchyUpload.setRegionErrors( new ArrayList<String>() );
            } else {
                parsedHierarchyUpload.getRegionErrors().clear();
            }

            // initialize other necessary objects
            if ( regionsIterator.hasNext() ) {
                if ( hierarchyIntermediate.getRegions() == null ) {
                    hierarchyIntermediate.setRegions( new HashMap<String, RegionUploadVO>() );
                }

                if ( hierarchyIntermediate.getRegionNameMap() == null ) {
                    hierarchyIntermediate.setRegionNameMap( new HashMap<String, String>() );
                }
            }

            while ( regionsIterator.hasNext() ) {

                String sourceRegionId = "";
                Row row = regionsIterator.next();

                // discard the current row if it is empty or doesn't have values
                if ( row.getLastCellNum() <= 0 || row.getPhysicalNumberOfCells() < 1 ) {
                    continue;
                }

                Cell regionSourceIdCell = row.getCell( REGION_ID_INDEX );
                Cell regionNameCell = row.getCell( REGION_NAME_INDEX );
                Cell regionAddress1Cell = row.getCell( REGION_ADDRESS1_INDEX );
                Cell regionAddress2Cell = row.getCell( REGION_ADDRESS2_INDEX );
                Cell regionCityCell = row.getCell( REGION_CITY_INDEX );
                Cell regionStateCell = row.getCell( REGION_STATE_INDEX );
                Cell regionZipCodeCell = row.getCell( REGION_ZIP_INDEX );

                String regionName = workbookOperations.getStringValue( regionNameCell );
                String regionAddress1 = workbookOperations.getStringValue( regionAddress1Cell );
                String regionAddress2 = workbookOperations.getStringValue( regionAddress2Cell );
                String regionCity = workbookOperations.getStringValue( regionCityCell );
                String regionState = workbookOperations.getStringValue( regionStateCell );
                String regionZipCode = workbookOperations.getStringValue( regionZipCodeCell );
                String tempSourceId = workbookOperations.getStringValue( regionSourceIdCell );

                // check for a row with empty data        
                if ( isRegionRowEmpty( tempSourceId, regionName, regionAddress1, regionAddress2, regionCity, regionState,
                    regionZipCode ) ) {
                    continue;
                }

                RegionUploadVO currentRegionVO = null;
                boolean isSourceIdUndetectable = false;
                int sourceIdDuplicate = 0;


                if ( regionSourceIdCell != null ) {
                    sourceRegionId = workbookOperations.getStringValue( regionSourceIdCell );
                    isSourceIdUndetectable = StringUtils.isEmpty( sourceRegionId );
                } else {
                    isSourceIdUndetectable = true;
                }


                sourceRegionId = StringUtils.defaultIfEmpty( sourceRegionId, generateSourceIdArbitrarily( TYPE_REGION ) );


                // Analyze and obtain the required VO 
                if ( !hierarchyIntermediate.getRegions().containsKey( sourceRegionId ) ) {

                    currentRegionVO = new RegionUploadVO();
                    currentRegionVO.setSourceRegionId( sourceRegionId );
                    currentRegionVO.setRegionAdded( true );
                    parsedHierarchyUpload.setNumberOfRegionsAdded( parsedHierarchyUpload.getNumberOfRegionsAdded() + 1 );

                } else {

                    if ( !hierarchyIntermediate.getRegions().get( sourceRegionId ).isRegionProcessed() ) {
                        currentRegionVO = hierarchyIntermediate.getRegions().get( sourceRegionId );
                    } else {
                        sourceIdDuplicate = hierarchyIntermediate.getRegions().get( sourceRegionId ).getRowNum();
                        sourceRegionId = sourceRegionId + CommonConstants.DUPLICATE_SOURCE_ID_SUBSTRING
                            + utils.generateRandomAlphaNumericString();
                        currentRegionVO = new RegionUploadVO();
                        currentRegionVO.setSourceRegionId( sourceRegionId );
                        currentRegionVO.setRegionAdded( true );
                        parsedHierarchyUpload.setNumberOfRegionsAdded( parsedHierarchyUpload.getNumberOfRegionsAdded() + 1 );
                    }
                }


                // set the row number for the VO
                currentRegionVO.setRowNum( row.getRowNum() + 1 );

                if ( isSourceIdUndetectable ) {
                    parsedHierarchyUpload.getRegionValidationWarnings()
                        .add( "Row: " + currentRegionVO.getRowNum() + ", Unable to parse source ID, generated a new one." );
                    currentRegionVO.setWarningRecord( true );
                }

                if ( sourceIdDuplicate != 0 ) {
                    parsedHierarchyUpload.getRegionErrors().add( "Row: " + currentRegionVO.getRowNum()
                        + " This source ID is a Duplicate, has already been parsed  at row : " + sourceIdDuplicate );
                    currentRegionVO.setErrorRecord( true );
                }

                processRegion( hierarchyIntermediate, parsedHierarchyUpload, currentRegionVO, regionName, regionAddress1,
                    regionAddress2, regionCity, regionState, regionZipCode );


                if ( currentRegionVO.isRegionModified() ) {
                    parsedHierarchyUpload.setNumberOfRegionsModified( parsedHierarchyUpload.getNumberOfRegionsModified() + 1 );
                }

                // mark region as processed
                currentRegionVO.setRegionProcessed( true );
                hierarchyIntermediate.setRegionsProcessed( hierarchyIntermediate.getRegionsProcessed() + 1 );

                //add the region to the intermediate object
                hierarchyIntermediate.getRegions().put( currentRegionVO.getSourceRegionId(), currentRegionVO );

            }


            // set global error and warning flags
            if ( !parsedHierarchyUpload.getRegionErrors().isEmpty() ) {
                parsedHierarchyUpload.setHasErrors( true );
            }

            if ( !parsedHierarchyUpload.getRegionValidationWarnings().isEmpty() ) {
                parsedHierarchyUpload.setHasWarnings( true );
            }
        }

    }


    private boolean isRegionRowEmpty( String regionSourceId, String regionName, String regionAddress1, String regionAddress2,
        String regionCity, String regionState, String regionZipCode )
    {
        if ( StringUtils.isEmpty( regionSourceId ) && StringUtils.isEmpty( regionName ) && StringUtils.isEmpty( regionAddress1 )
            && StringUtils.isEmpty( regionAddress2 ) && StringUtils.isEmpty( regionCity ) && StringUtils.isEmpty( regionState )
            && StringUtils.isEmpty( regionZipCode ) ) {
            return true;
        }
        return false;
    }


    private void processRegion( HierarchyUploadIntermediate hierarchyIntermediate, ParsedHierarchyUpload parsedHierarchyUpload,
        RegionUploadVO currentRegionVO, String regionName, String regionAddress1, String regionAddress2, String regionCity,
        String regionState, String regionZipCode )
    {
        if ( !StringUtils.equals( regionName, currentRegionVO.getRegionName() ) && !StringUtils.isEmpty( regionName ) ) {

            if ( currentRegionVO.getRegionNameHistory() == null )
                currentRegionVO.setRegionNameHistory( new ArrayList<StringUploadHistory>() );

            // append to region name history
            currentRegionVO.getRegionNameHistory()
                .add( new StringUploadHistory( currentRegionVO.getRegionName(), new Date() ) );

            // update with the new name
            currentRegionVO.setRegionName( regionName );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

            // check for name duplication
            String regionSourceIdWithExistingName = containsStringValue( hierarchyIntermediate.getRegionNameMap(), regionName );

            if ( StringUtils.isNotEmpty( regionSourceIdWithExistingName ) ) {

                RegionUploadVO region = hierarchyIntermediate.getRegions().get( regionSourceIdWithExistingName );
                if ( region.isRegionProcessed() && region.isRegionAdded() ) {
                    parsedHierarchyUpload.getRegionValidationWarnings().add( "Row: " + currentRegionVO.getRowNum()
                        + ", A Region with the same already exists at Row: " + region.getRowNum() );
                } else if ( region.isRegionProcessed() && !region.isRegionAdded() ) {
                    parsedHierarchyUpload.getRegionValidationWarnings().add(
                        "Row: " + currentRegionVO.getRowNum() + ", A Region with the same already exists in the company." );
                }

                currentRegionVO.setWarningRecord( true );
            } else {
                hierarchyIntermediate.getRegionNameMap().put( currentRegionVO.getSourceRegionId(),
                    currentRegionVO.getRegionName() );
            }

        } else if ( StringUtils.isEmpty( regionName ) ) {

            // set errors for the region          
            parsedHierarchyUpload.getRegionErrors().add( "Row: " + currentRegionVO.getRowNum() + " region name not provided." );
            currentRegionVO.setErrorRecord( true );

        }


        if ( !StringUtils.equals( regionAddress1, currentRegionVO.getRegionAddress1() )
            && !StringUtils.isEmpty( regionAddress1 ) ) {

            if ( currentRegionVO.getRegionAddress1History() == null )
                currentRegionVO.setRegionAddress1History( new ArrayList<StringUploadHistory>() );

            // append to region address1 history
            currentRegionVO.getRegionAddress1History()
                .add( new StringUploadHistory( currentRegionVO.getRegionAddress1(), new Date() ) );

            // update with the new address1
            currentRegionVO.setRegionAddress1( regionAddress1 );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

        }


        if ( !StringUtils.equals( regionAddress2, currentRegionVO.getRegionAddress2() )
            && !StringUtils.isEmpty( regionAddress2 ) ) {

            if ( currentRegionVO.getRegionAddress2History() == null )
                currentRegionVO.setRegionAddress2History( new ArrayList<StringUploadHistory>() );

            // append to region address2 history
            currentRegionVO.getRegionAddress2History()
                .add( new StringUploadHistory( currentRegionVO.getRegionAddress2(), new Date() ) );

            // update with the new address2
            currentRegionVO.setRegionAddress2( regionAddress2 );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

        }


        if ( !StringUtils.equals( regionCity, currentRegionVO.getRegionCity() ) && !StringUtils.isEmpty( regionCity ) ) {

            if ( currentRegionVO.getRegionCityHistory() == null )
                currentRegionVO.setRegionCityHistory( new ArrayList<StringUploadHistory>() );

            // append to region city history
            currentRegionVO.getRegionCityHistory()
                .add( new StringUploadHistory( currentRegionVO.getRegionCity(), new Date() ) );

            // update with the new City
            currentRegionVO.setRegionCity( regionCity );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

        }


        if ( !StringUtils.equals( regionState, currentRegionVO.getRegionState() ) && !StringUtils.isEmpty( regionState ) ) {

            if ( currentRegionVO.getRegionStateHistory() == null )
                currentRegionVO.setRegionStateHistory( new ArrayList<StringUploadHistory>() );

            // append to region state history
            currentRegionVO.getRegionStateHistory()
                .add( new StringUploadHistory( currentRegionVO.getRegionState(), new Date() ) );

            // update with the new state
            currentRegionVO.setRegionState( regionState );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

        }


        if ( !StringUtils.equals( regionZipCode, currentRegionVO.getRegionZipcode() )
            && !StringUtils.isEmpty( regionZipCode ) ) {

            if ( currentRegionVO.getRegionZipcodeHistory() == null )
                currentRegionVO.setRegionZipcodeHistory( new ArrayList<StringUploadHistory>() );

            // append to region zipcode history
            currentRegionVO.getRegionZipcodeHistory()
                .add( new StringUploadHistory( currentRegionVO.getRegionZipcode(), new Date() ) );

            // update with the new zipcode
            currentRegionVO.setRegionZipcode( regionZipCode );

            // set modified record flag
            if ( !currentRegionVO.isRegionAdded() )
                currentRegionVO.setRegionModified( true );

        }

    }


    private HierarchyUploadIntermediate generateHierarchyUploadIntermediate( Company company ) throws InvalidInputException
    {
        LOG.debug( "method generateHierarchyUploadIntermediate() started" );

        if ( company == null ) {
            throw new InvalidInputException( "company is non-existent." );
        }

        HierarchyUploadIntermediate hierarchyIntermediate = new HierarchyUploadIntermediate();
        hierarchyIntermediate.setCompany( company );

        HierarchyUploadAggregate hierarchyAggregate = hierarchyDownloadService.fetchUpdatedHierarchyUploadStructure( company );

        if ( hierarchyAggregate.getRegionUploadVOMap() != null ) {
            hierarchyIntermediate.setRegions( hierarchyAggregate.getRegionUploadVOMap() );
        }

        if ( hierarchyAggregate.getRegionNameMap() != null ) {
            hierarchyIntermediate.setRegionNameMap( hierarchyAggregate.getRegionNameMap() );
        }

        if ( hierarchyAggregate.getBranchUploadVOMap() != null ) {
            hierarchyIntermediate.setBranches( hierarchyAggregate.getBranchUploadVOMap() );
        }

        if ( hierarchyAggregate.getBranchNameMap() != null ) {
            hierarchyIntermediate.setBranchNameMap( hierarchyAggregate.getBranchNameMap() );
        }
        if ( hierarchyAggregate.getUserUploadVOMap() != null ) {
            hierarchyIntermediate.setUsers( hierarchyAggregate.getUserUploadVOMap() );
        }

        LOG.debug( "method generateHierarchyUploadIntermediate() finished" );
        return hierarchyIntermediate;
    }


    private String generateSourceIdArbitrarily( char entityType )
    {
        return entityType
            + Integer.toHexString( String.valueOf( System.currentTimeMillis() + new SecureRandom().nextLong() ).hashCode() );
    }


    private boolean isHeaderValid( Row header, List<String> headerList )
    {
        if ( header == null ) {
            return false;
        }

        int cellNo = 0;
        Iterator<Cell> headerData = header.cellIterator();

        try {
            while ( headerData.hasNext() && cellNo < headerList.size() ) {

                if ( !headerData.next().getStringCellValue().trim().equalsIgnoreCase( headerList.get( cellNo ) ) ) {
                    return false;
                }
                cellNo++;
            }
        } catch ( Exception invalidHeaderError ) {
            LOG.warn( "Unable to recognise header." );
            return false;
        }

        return cellNo == headerList.size() ? true : false;
    }


    @Override
    public List<ParsedHierarchyUpload> findInitiatedHierarchyUploads() throws NoRecordsFetchedException
    {
        return hierarchyUploadDao.getActiveHierarchyUploads();
    }


    @Override
    public ParsedHierarchyUpload insertUploadHierarchyXlsxDetails( User user, String fileLocalName, String uploadedFileName,
        Date uploadedDate, boolean isInAppendMode ) throws InvalidInputException
    {
        LOG.debug(
            "method reinsertUploadHierarchyXlsxDetails started for company with ID: " + user.getCompany().getCompanyId() );

        ParsedHierarchyUpload initialUpload = new ParsedHierarchyUpload();
        initialUpload.setCompanyId( user.getCompany().getCompanyId() );
        initialUpload.setImportInitiatedUserId( user.getUserId() );
        initialUpload.setGivenFileName( fileLocalName );
        initialUpload.setUploadedDate( uploadedDate );
        initialUpload.setFileURI( uploadedFileName );
        initialUpload.setInAppendMode( isInAppendMode );
        initialUpload.setStatus( CommonConstants.HIERARCHY_UPLOAD_STATUS_NEW_ENTRY );

        //save in mongoDB
        hierarchyUploadDao.reinsertParsedHierarchyUpload( initialUpload );

        return initialUpload;
    }


    @Override
    public boolean updateStatusForParsedHierarchyUpload( long companyId, int hierarchyUploadStatus )
        throws InvalidInputException
    {
        hierarchyUploadDao.updateStatusForParsedHierarchyUpload( companyId, hierarchyUploadStatus );
        return true;
    }


    @Override
    public ParsedHierarchyUpload getParsedHierarchyUpload( long companyId )
        throws NoRecordsFetchedException, InvalidInputException
    {
        return hierarchyUploadDao.getParsedHierarchyUpload( companyId );
    }


    @Override
    public boolean reinsertParsedHierarchyUpload( ParsedHierarchyUpload upload ) throws InvalidInputException
    {
        hierarchyUploadDao.reinsertParsedHierarchyUpload( upload );
        return true;
    }


    private String containsStringValue( Map<String, String> map, String value )
    {
        for ( Entry<String, String> entry : map.entrySet() ) {
            if ( StringUtils.equals( entry.getValue(), value ) ) {
                return entry.getKey();
            }
        }
        return "";
    }
    

    private boolean isImagevalid( String userPhotoUrl )
    {
        try {
            Image image = ImageIO.read( new URL( userPhotoUrl ) );
            if ( image == null ) {
                return false;
            }
        } catch ( IOException e ) {
            LOG.error( "The url given is not a valid image url." + userPhotoUrl, e );
            return false;
        }
        return true;
    }
    

    private String uploadProfileImageToCloud( String userPhotoUrl ) throws Exception
    {
        String imageName = java.util.UUID.randomUUID().toString();
        if ( userPhotoUrl.contains( ".png" ) || userPhotoUrl.contains( ".PNG" ) ) {
            imageName = imageName + ".png";
        } else if ( userPhotoUrl.contains( ".jpg" ) || userPhotoUrl.contains( ".JPG" ) ) {
            imageName = imageName + ".jpg";
        } else if ( userPhotoUrl.contains( ".jpeg" ) || userPhotoUrl.contains( ".JPEG" ) ) {
            imageName = imageName + ".jpeg";
        } else {
            LOG.error( "The url given is not a valid image url." );
            throw new InvalidInputException( "Image format not valid" );
        }
        return profileManagementService.copyImage( userPhotoUrl, imageName );
    }

    // V2.0 : END
}
