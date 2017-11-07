package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.StringListUploadHistory;
import com.realtech.socialsurvey.core.entities.StringUploadHistory;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyStructureUploadService;
import com.realtech.socialsurvey.core.services.upload.HierarchyUploadService;
import com.realtech.socialsurvey.core.services.upload.UploadValidationService;


@Component
public class HierarchyUploadServiceImpl implements HierarchyUploadService
{

    private static Logger LOG = LoggerFactory.getLogger( HierarchyUploadServiceImpl.class );
    private static final String REGION_SHEET = "Regions";
    private static final String BRANCH_SHEET = "Offices";
    private static final String USERS_SHEET = "Users";

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

    @Autowired
    private UploadValidationService uploadValidationService;

    @Autowired
    private HierarchyDownloadService hierarchyDownloadService;

    @Autowired
    private HierarchyStructureUploadService hierarchyStructureUploadService;
    
    @Autowired
    private UserDao userDao;


    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;


    @Override
    @Transactional
    public UploadValidation validateUserUploadFile( Company company, String fileName, boolean isAppend )
        throws InvalidInputException
    {
        if ( fileName == null || fileName.isEmpty() ) {
            LOG.error( "Invalid upload details" );
            throw new InvalidInputException( "File name is not provided: " + fileName );
        }
        if ( company == null ) {
            LOG.error( "Invalid company details" );
            throw new InvalidInputException( "Invalid company details" );
        }
        InvalidInputException potentialException = null;
        InvalidInputException xlsxException = null;
        LOG.info( "Validating the file for " + company.getCompany() + " and file " + fileName );
        UploadValidation validationObject = new UploadValidation();
        // get current hierarchy upload
        validationObject.setUpload( hierarchyDownloadService.fetchUpdatedHierarchyStructure( company ) );
        // read the file
        InputStream fileStream = null;
        try {
            // fileStream = new FileInputStream( fileName );
            fileStream = new URL( fileName ).openStream();
            XSSFWorkbook workBook = new XSSFWorkbook( fileStream );
            if ( workBook.getSheet( REGION_SHEET ) == null || workBook.getSheet( BRANCH_SHEET ) == null
                || workBook.getSheet( USERS_SHEET ) == null ) {
                LOG.error( "The xlsx file does not contain the required sheets" );
                xlsxException = new InvalidInputException( "The xlsx file does not contain the required sheets" );
                throw xlsxException;
            }
            Map<String, String> regionErrors = new HashMap<String, String>();
            Map<String, String> branchErrors = new HashMap<String, String>();
            Map<String, String> userErrors = new HashMap<String, String>();
            parseRegions( workBook, validationObject, regionErrors, isAppend );
            parseBranches( workBook, validationObject, branchErrors, isAppend );
            parseUsers( workBook, validationObject, userErrors, isAppend ,  company);
            if ( validationObject.isBranchHeadersInvalid() || validationObject.isRegionHeadersInvalid()
                || validationObject.isUserHeadersInvalid() ) {
                throw new InvalidInputException();
            }
            if ( validationObject.isBranchHeadersInvalid() ) {
                throw new InvalidInputException( "Office sheet headers are invalid" );
            } else if ( validationObject.isRegionHeadersInvalid() ) {
	            throw new InvalidInputException( "Region sheet headers are invalid" );
            } else if ( validationObject.isUserHeadersInvalid() ) {
	            throw new InvalidInputException( "User sheet headers are invalid" );
            }
            uploadValidationService.validateHeirarchyUpload( validationObject, regionErrors, branchErrors, userErrors );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( Exception e ) {
            String message = "";
            if ( validationObject.isRegionHeadersInvalid() ) {
                message = message + CommonConstants.HIERARCHY_REGION_HEADERS_INVALID + ", ";
            }
            if ( validationObject.isBranchHeadersInvalid() ) {
                message = message + CommonConstants.HIERARCHY_BRANCH_HEADERS_INVALID + ", ";
            }
            if ( validationObject.isUserHeadersInvalid() ) {
                message = message + CommonConstants.HIERARCHY_USER_HEADERS_INVALID + ", ";
            }
            if ( !message.isEmpty() ) {
                message = message.substring( 0, message.length() - 2 );
            } else {
                message = e.getMessage();
            }
            potentialException = new InvalidInputException( message );
        } finally {
            if ( fileStream != null ) {
                try {
                    fileStream.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        if ( xlsxException != null ) {
            throw xlsxException;
        }
        if ( potentialException != null ) {
            throw potentialException;
        }
        return validationObject;
    }


    /**
     * Validate hierarchy upload when modified in the UI
     * @param company
     * @param newUploadValidation
     * @return
     * @throws InvalidInputException
     */
    @Override
    public UploadValidation validateHierarchyUploadJson( Company company, UploadValidation newUploadValidation,
        boolean isAppend ) throws InvalidInputException
    {
        LOG.info( "Method validateHierarchyUploadJson() started" );
        if ( newUploadValidation == null || newUploadValidation.getUpload() == null ) {
            throw new InvalidInputException( "Invalid upload details" );
        }

        UploadValidation validationObject = new UploadValidation();
        // get current hierarchy upload
        validationObject.setUpload( hierarchyDownloadService.fetchUpdatedHierarchyStructure( company ) );

        Map<String, String> sourceRegionIdErrors = new HashMap<String, String>();
        Map<String, String> sourceBranchIdErrors = new HashMap<String, String>();
        Map<String, String> sourceUserIdErrors = new HashMap<String, String>();

        parseRegions( newUploadValidation.getUpload().getRegions(), validationObject, sourceRegionIdErrors, isAppend );
        parseBranches( newUploadValidation.getUpload().getBranches(), validationObject, sourceBranchIdErrors, isAppend );
        parseUsers( newUploadValidation.getUpload().getUsers(), validationObject, sourceUserIdErrors, isAppend );
        uploadValidationService.validateHeirarchyUpload( validationObject, sourceRegionIdErrors, sourceBranchIdErrors,
            sourceUserIdErrors );
        LOG.info( "Method validateHierarchyUploadJson() finished" );
        return validationObject;
    }


    /**
     * Validates regions modified in the UI
     * @param inputRegions
     * @param validationObject
     */
    void parseRegions( List<RegionUploadVO> inputRegions, UploadValidation validationObject,
        Map<String, String> sourceRegionIdErrors, boolean isAppend )
    {
        if ( inputRegions == null ) {
            inputRegions = new ArrayList<RegionUploadVO>();
        }
        List<RegionUploadVO> uploadedRegions = new ArrayList<RegionUploadVO>();
        Map<String, Integer> regionMap = null;
        if ( validationObject != null && validationObject.getUpload() != null ) {
            regionMap = generateRegionIndexMap( validationObject.getUpload().getRegions(), true );
        }
        if ( regionMap == null ) {
            regionMap = new HashMap<String, Integer>();
        }

        for ( RegionUploadVO uploadedRegion : inputRegions ) {
            if ( isRegionUploadEmpty( uploadedRegion ) || !uploadedRegion.isInAppendMode() ) {
                continue;
            }
            // check if region is added or modified
            if ( isNewRegion( uploadedRegion, validationObject.getUpload().getRegions(), uploadedRegions ) ) {
                validationObject.setNumberOfRegionsAdded( validationObject.getNumberOfRegionsAdded() + 1 );
                uploadedRegion.setRegionAdded( true );
                validationObject.getUpload().getRegions().add( uploadedRegion );
            } else {
                updateUploadValidationWithModifiedRegion( uploadedRegion, validationObject, regionMap );
            }

            //check for duplicate source ids
            checkForDuplicateSourceRegionIds( sourceRegionIdErrors, uploadedRegion );
            uploadedRegions.add( uploadedRegion );
        }
        //In case of append mode, we skip deletion
        if ( !isAppend ) {
            markDeletedRegions( uploadedRegions, validationObject );
        }
        List<String> errors = new ArrayList<String>();
        if ( !sourceRegionIdErrors.isEmpty() ) {
            for ( String key : sourceRegionIdErrors.keySet() ) {
                if ( sourceRegionIdErrors.get( key ) != null && !sourceRegionIdErrors.get( key ).isEmpty() ) {
                    errors.add( sourceRegionIdErrors.get( key ) );
                }
            }
            validationObject.setRegionValidationErrors( errors );
        }
    }


    /**
     * Method to generate sourceId : index map from a list of regions
     * @param regions
     * @return
     */
    Map<String, Integer> generateRegionIndexMap( List<RegionUploadVO> regions, boolean isFromUI )
    {
        Map<String, Integer> regionMap = new HashMap<String, Integer>();
        if ( regions != null && !regions.isEmpty() ) {
            for ( RegionUploadVO region : regions ) {
                if ( !isFromUI ) {
                    // Reset inAppendMode flag
                    region.setInAppendMode( false );
                }
                if ( region.getSourceRegionId() == null || region.getSourceRegionId().isEmpty() ) {
                    continue;
                }
                regionMap.put( region.getSourceRegionId(), regions.indexOf( region ) );
            }
        }
        return regionMap;
    }


    /**
     * Method to generate sourceId : index map from a list of branches
     * @param branches
     * @return
     */
    Map<String, Integer> generateBranchIndexMap( List<BranchUploadVO> branches, boolean isFromUI )
    {
        Map<String, Integer> branchMap = new HashMap<String, Integer>();
        if ( branches != null && !branches.isEmpty() ) {
            for ( BranchUploadVO branch : branches ) {
                if ( !isFromUI ) {
                    // Reset inAppendMode flag
                    branch.setInAppendMode( false );
                }
                if ( branch.getSourceBranchId() == null || branch.getSourceBranchId().isEmpty() ) {
                    continue;
                }
                branchMap.put( branch.getSourceBranchId(), branches.indexOf( branch ) );
            }
        }
        return branchMap;
    }


    /**
     * Method to generate sourceId : index map from a list of users
     * @param users
     * @return
     */
    Map<String, Integer> generateUserIndexMap( List<UserUploadVO> users, boolean isFromUI )
    {
        Map<String, Integer> userMap = new HashMap<String, Integer>();
        if ( users != null && !users.isEmpty() ) {
            for ( UserUploadVO user : users ) {
                if ( !isFromUI ) {
                    // Reset inAppendMode flag
                    user.setInAppendMode( false );
                }
                if ( user.getSourceUserId() == null || user.getSourceUserId().isEmpty() ) {
                    continue;
                }
                userMap.put( user.getSourceUserId(), users.indexOf( user ) );
            }
        }
        return userMap;
    }


    /**
     * Validates branches modified in the UI
     * @param inputBranches
     * @param validationObject
     */
    void parseBranches( List<BranchUploadVO> inputBranches, UploadValidation validationObject,
        Map<String, String> sourceBranchIdErrors, boolean isAppend )
    {
        if ( inputBranches == null ) {
            inputBranches = new ArrayList<BranchUploadVO>();
        }

        Map<String, Integer> branchMap = null;
        List<BranchUploadVO> uploadedBranches = new ArrayList<BranchUploadVO>();
        if ( validationObject != null && validationObject.getUpload() != null ) {
            branchMap = generateBranchIndexMap( validationObject.getUpload().getBranches(), true );
        }
        if ( branchMap == null ) {
            branchMap = new HashMap<String, Integer>();
        }

        for ( BranchUploadVO uploadedBranch : inputBranches ) {
            if ( isBranchUploadEmpty( uploadedBranch ) || !uploadedBranch.isInAppendMode() ) {
                continue;
            }
            // check if branch is added or modified
            if ( isNewBranch( uploadedBranch, validationObject.getUpload().getBranches(), uploadedBranches ) ) {
                validationObject.setNumberOfBranchesAdded( validationObject.getNumberOfBranchesAdded() + 1 );
                uploadedBranch.setBranchAdded( true );
                validationObject.getUpload().getBranches().add( uploadedBranch );
            } else {
                updateUploadValidationWithModifiedBranch( uploadedBranch, validationObject, branchMap );
            }
            //check for duplicate source ids
            checkForDuplicateSourceBranchIds( sourceBranchIdErrors, uploadedBranch );
            uploadedBranches.add( uploadedBranch );
        }
        //In case of append mode, we skip deletion
        if ( !isAppend ) {
            markDeletedBranches( uploadedBranches, validationObject );
        }
        List<String> errors = new ArrayList<String>();
        if ( !sourceBranchIdErrors.isEmpty() ) {
            for ( String key : sourceBranchIdErrors.keySet() ) {
                if ( sourceBranchIdErrors.get( key ) != null && !sourceBranchIdErrors.get( key ).isEmpty() ) {
                    errors.add( sourceBranchIdErrors.get( key ) );
                }
            }
            validationObject.setBranchValidationErrors( errors );
        }
    }


    void parseUsers( List<UserUploadVO> uploadedUsers, UploadValidation validationObject, Map<String, String> userErrors,
        boolean isAppend )

    {
        if ( uploadedUsers == null ) {
            uploadedUsers = new ArrayList<UserUploadVO>();
        }

        Map<String, Integer> userMap = null;
        if ( validationObject != null && validationObject.getUpload() != null ) {
            userMap = generateUserIndexMap( validationObject.getUpload().getUsers(), true );
        }
        if ( userMap == null ) {
            userMap = new HashMap<String, Integer>();
        }

        Map<String, String> sourceUserIdErrors = new HashMap<String, String>();
        Map<String, String> emailErrors = new HashMap<String, String>();
        Set<String> emailSet = new HashSet<String>();

        for ( UserUploadVO uploadedUser : uploadedUsers ) {
            if ( isUserUploadEmpty( uploadedUser ) || !uploadedUser.isInAppendMode() ) {
                continue;
            }
            if ( uploadedUser.getSourceBranchId() == null && uploadedUser.getSourceRegionId() == null
                && ( uploadedUser.getAssignedBranchesAdmin() == null || uploadedUser.getAssignedBranchesAdmin().isEmpty() )
                && ( uploadedUser.getAssignedRegionsAdmin() == null || uploadedUser.getAssignedRegionsAdmin().isEmpty() ) ) {
                uploadedUser.setBelongsToCompany( true );
            }

            // check if user is added or modified
            if ( isNewUser( uploadedUser, validationObject.getUpload().getUsers() ) ) {
                validationObject.setNumberOfUsersAdded( validationObject.getNumberOfUsersAdded() + 1 );
                uploadedUser.setUserAdded( true );
                validationObject.getUpload().getUsers().add( uploadedUser );
            } else {

                updateUploadValidationWithModifiedUser( uploadedUser, validationObject, userMap );
            }

            //Check for email address duplication
            if ( emailSet.contains( uploadedUser.getEmailId() ) ) {
                emailErrors.put( uploadedUser.getSourceUserId(),
                    "The email address " + uploadedUser.getEmailId() + " is duplicated at row : " + uploadedUser.getRowNum() );
            } else {
                emailSet.add( uploadedUser.getEmailId() );
            }

            //check for duplicate source ids
            checkForDuplicateSourceUserIds( sourceUserIdErrors, uploadedUser );
        }

        //In case of append mode, we skip deletion
        if ( !isAppend ) {
            markDeletedUsers( uploadedUsers, validationObject );
        }
        userErrors.putAll( emailErrors );
        List<String> errors = new ArrayList<String>();
        if ( !sourceUserIdErrors.isEmpty() ) {
            for ( String key : sourceUserIdErrors.keySet() ) {
                if ( sourceUserIdErrors.get( key ) != null && !sourceUserIdErrors.get( key ).isEmpty() ) {
                    errors.add( sourceUserIdErrors.get( key ) );
                    if ( userErrors.containsKey( key ) ) {
                        //Append
                        String value = userErrors.get( key );
                        value += ", " + sourceUserIdErrors.get( key );
                        userErrors.put( key, value );
                    } else {
                        //Add
                        userErrors.put( key, sourceUserIdErrors.get( key ) );
                    }
                }
            }
        }
    }


    /**
     * Validates regions to be uploaded.
     * 
     * @param workBook
     * @param validationObject
     * @throws InvalidInputException 
     */
    void parseRegions( XSSFWorkbook workBook, UploadValidation validationObject, Map<String, String> sourceRegionIdErrors,
        boolean isAppend ) throws InvalidInputException
    {
        // Parse the list of regions from the sheet. Parse each row. Check for validation errors. If validation is successful, check if region is modified or added. If modified then add to the modified count or to the addition count. Then map and check if there are any regions that were deleted
        // Possible errors in regions
        // 1. Source region id is not present
        // 2. Region name is not present
        // 3. Region cannot be deleted if branches and users are associated.
        LOG.debug( "Parsing regions sheet" );
        XSSFSheet regionSheet = workBook.getSheet( REGION_SHEET );
        Iterator<Row> rows = regionSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        RegionUploadVO uploadedRegion = null;
        List<RegionUploadVO> uploadedRegions = new ArrayList<RegionUploadVO>();

        //Create header map
        Map<Integer, String> headerMap = new HashMap<Integer, String>();
        headerMap.put( 1, CommonConstants.CHR_REGION_REGION_ID );
        headerMap.put( 2, CommonConstants.CHR_REGION_REGION_NAME );
        headerMap.put( 3, CommonConstants.CHR_ADDRESS_1 );
        headerMap.put( 4, CommonConstants.CHR_ADDRESS_2 );
        headerMap.put( 5, CommonConstants.CHR_CITY );
        headerMap.put( 6, CommonConstants.CHR_STATE );
        headerMap.put( 7, CommonConstants.CHR_ZIP );

        Map<String, Integer> regionMap = null;
        try {
            if ( validationObject != null && validationObject.getUpload() != null ) {
                regionMap = generateRegionIndexMap( validationObject.getUpload().getRegions(), false );
            }
            if ( regionMap == null ) {
                regionMap = new HashMap<String, Integer>();
            }

            if ( !rows.hasNext() ) {
                throw new InvalidInputException( CommonConstants.HIERARCHY_REGION_HEADERS_INVALID );
            }

            while ( rows.hasNext() ) {
                row = (XSSFRow) rows.next();
                // skip the first 1st row. first row is the header
                if ( row.getRowNum() < 1 ) {
                    //Validate column headings
                    if ( !isHeaderValid( row, headerMap ) ) {
                        throw new InvalidInputException( CommonConstants.HIERARCHY_REGION_HEADERS_INVALID );
                    }
                    continue;
                }
                cells = row.cellIterator();
                uploadedRegion = new RegionUploadVO();
                int cellIndex = 0;
                uploadedRegion.setRowNum( row.getRowNum() + 1 );
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    cellIndex = cell.getColumnIndex();
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        if ( cellIndex == REGION_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedRegion.setSourceRegionId( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ).trim() ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedRegion.setSourceRegionId( processSourceId( cell.getStringCellValue().trim() ) );
                            }
                        } else if ( cellIndex == REGION_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setRegionName( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == REGION_ADDRESS1_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setRegionAddress1( cell.getStringCellValue().trim() );
                            uploadedRegion.setAddressSet( true );
                        } else if ( cellIndex == REGION_ADDRESS2_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setRegionAddress2( cell.getStringCellValue().trim() );
                            uploadedRegion.setAddressSet( true );
                        } else if ( cellIndex == REGION_CITY_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setRegionCity( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == REGION_STATE_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setRegionState( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == REGION_ZIP_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING
                                && !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedRegion.setRegionZipcode( cell.getStringCellValue().trim() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (int) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedRegion.setRegionZipcode( String.valueOf( (int) cell.getNumericCellValue() ).trim() );
                            }
                        }
                    }
                }
                if ( isRegionUploadEmpty( uploadedRegion ) ) {
                    continue;
                }
                uploadedRegion.setInAppendMode( false );
                if ( isAppend ) {
                    uploadedRegion.setInAppendMode( true );
                }
                // check if region is added or modified
                // if duplicate record
                if ( isNewRegion( uploadedRegion, validationObject.getUpload().getRegions(), uploadedRegions ) ) {
                    validationObject.setNumberOfRegionsAdded( validationObject.getNumberOfRegionsAdded() + 1 );
                    uploadedRegion.setRegionAdded( true );
                    validationObject.getUpload().getRegions().add( uploadedRegion );
                } else {
                    updateUploadValidationWithModifiedRegion( uploadedRegion, validationObject, regionMap );
                }
                //check for duplicate source ids
                checkForDuplicateSourceRegionIds( sourceRegionIdErrors, uploadedRegion );
                uploadedRegions.add( uploadedRegion );
            }

            List<String> errors = new ArrayList<String>();
            if ( !sourceRegionIdErrors.isEmpty() ) {
                for ( String key : sourceRegionIdErrors.keySet() ) {
                    if ( sourceRegionIdErrors.get( key ) != null && !sourceRegionIdErrors.get( key ).isEmpty() ) {
                        errors.add( sourceRegionIdErrors.get( key ) );
                    }
                }
                validationObject.setRegionValidationErrors( errors );
            }
            //In case of append mode, we skip deletion
            if ( !isAppend ) {
                markDeletedRegions( uploadedRegions, validationObject );
            }
        } catch ( InvalidInputException ex ) {
            validationObject.setRegionHeadersInvalid( true );
        }
    }


    void parseBranches( XSSFWorkbook workBook, UploadValidation validationObject, Map<String, String> sourceBranchIdErrors,
        boolean isAppend ) throws InvalidInputException

    {
        // Parse each row for branches and then check for valid branches. On successful validation, check if the branch is a new, modified or deleted branch.
        // Possible reasons for errors
        // 1. Branch Source id is not present
        // 2. Branch name is not present.
        // 3. Branch address is not present.
        // 4. Source region id is not present in the regions tab
        // Possible warnings
        // 1. For a company with regions, if the branch does not have a source region id

        LOG.debug( "Parsing branches sheet" );
        XSSFSheet branchSheet = workBook.getSheet( BRANCH_SHEET );
        Iterator<Row> rows = branchSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        BranchUploadVO uploadedBranch = null;
        List<BranchUploadVO> uploadedBranches = new ArrayList<BranchUploadVO>();

        //Create header map
        Map<Integer, String> headerMap = new HashMap<Integer, String>();
        headerMap.put( 1, CommonConstants.CHR_BRANCH_BRANCH_ID );
        headerMap.put( 2, CommonConstants.CHR_BRANCH_BRANCH_NAME );
        headerMap.put( 3, CommonConstants.CHR_REGION_REGION_ID );
        headerMap.put( 4, CommonConstants.CHR_ADDRESS_1 );
        headerMap.put( 5, CommonConstants.CHR_ADDRESS_2 );
        headerMap.put( 6, CommonConstants.CHR_CITY );
        headerMap.put( 7, CommonConstants.CHR_STATE );
        headerMap.put( 8, CommonConstants.CHR_ZIP );

        Map<String, Integer> branchMap = null;

        try {
            if ( validationObject != null && validationObject.getUpload() != null ) {
                branchMap = generateBranchIndexMap( validationObject.getUpload().getBranches(), false );
            }
            if ( branchMap == null ) {
                branchMap = new HashMap<String, Integer>();
            }

            if ( !rows.hasNext() ) {
                throw new InvalidInputException( CommonConstants.HIERARCHY_BRANCH_HEADERS_INVALID );
            }

            while ( rows.hasNext() ) {
                row = (XSSFRow) rows.next();
                // skip the first 1 row. first row is the schema and second is the header
                if ( row.getRowNum() < 1 ) {
                    //Validate column headings
                    if ( !isHeaderValid( row, headerMap ) ) {
                        throw new InvalidInputException( CommonConstants.HIERARCHY_BRANCH_HEADERS_INVALID );
                    }
                    continue;
                }
                cells = row.cellIterator();
                uploadedBranch = new BranchUploadVO();
                int cellIndex = 0;
                uploadedBranch.setRowNum( row.getRowNum() + 1 );
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    cellIndex = cell.getColumnIndex();
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        if ( cellIndex == BRANCH_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedBranch.setSourceBranchId( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ).trim() ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedBranch.setSourceBranchId( processSourceId( cell.getStringCellValue().trim() ) );
                            }
                        } else if ( cellIndex == BRANCH_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchName( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == BRANCH_REGION_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedBranch.setSourceRegionId( String.valueOf( (long) cell.getNumericCellValue() ).trim() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING
                                && !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedBranch.setSourceRegionId( cell.getStringCellValue().trim() );
                            }
                        } else if ( cellIndex == BRANCH_ADDRESS1_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchAddress1( cell.getStringCellValue().trim() );
                            uploadedBranch.setAddressSet( true );
                        } else if ( cellIndex == BRANCH_ADDRESS2_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchAddress2( cell.getStringCellValue().trim() );
                            uploadedBranch.setAddressSet( true );
                        } else if ( cellIndex == BRANCH_CITY_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchCity( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == BRANCH_STATE_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchState( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == BRANCH_ZIP_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING
                                && !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedBranch.setBranchZipcode( cell.getStringCellValue().trim() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (int) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedBranch.setBranchZipcode( String.valueOf( (int) cell.getNumericCellValue() ).trim() );
                            }
                        }
                    }
                }
                if ( isBranchUploadEmpty( uploadedBranch ) ) {
                    continue;
                }
                uploadedBranch.setInAppendMode( false );
                if ( isAppend ) {
                    uploadedBranch.setInAppendMode( true );
                }
                // check if branch is added or modified
                if ( isNewBranch( uploadedBranch, validationObject.getUpload().getBranches(), uploadedBranches ) ) {
                    validationObject.setNumberOfBranchesAdded( validationObject.getNumberOfBranchesAdded() + 1 );
                    uploadedBranch.setBranchAdded( true );
                    validationObject.getUpload().getBranches().add( uploadedBranch );
                } else {
                    updateUploadValidationWithModifiedBranch( uploadedBranch, validationObject, branchMap );
                }
                uploadedBranches.add( uploadedBranch );
                //check for duplicate source ids
                checkForDuplicateSourceBranchIds( sourceBranchIdErrors, uploadedBranch );
            }
            List<String> errors = new ArrayList<String>();
            if ( !sourceBranchIdErrors.isEmpty() ) {
                for ( String key : sourceBranchIdErrors.keySet() ) {
                    if ( sourceBranchIdErrors.get( key ) != null && !sourceBranchIdErrors.get( key ).isEmpty() ) {
                        errors.add( sourceBranchIdErrors.get( key ) );
                    }
                }
                validationObject.setBranchValidationErrors( errors );
            }
            //In case of append mode, we skip deletion
            if ( !isAppend ) {
                markDeletedBranches( uploadedBranches, validationObject );
            }
        } catch ( InvalidInputException ex ) {
            validationObject.setBranchHeadersInvalid( true );
        }
    }


    String processSourceId( String sourceId )
    {
        if ( sourceId.contains( "." ) ) {
            sourceId = sourceId.replace( ".", "" );
        }
        return sourceId;
    }

    void parseUsers( XSSFWorkbook workBook, UploadValidation validationObject, Map<String, String> userErrors,
        boolean isAppend , Company company ) throws InvalidInputException
    {
        // Parse each row for users and then check for valid users. On successful validation, check if the user is a new, modified or deleted user.
        // Possible reasons for errors
        // 1. User source id is not present.
        // 2. User first name is not present.
        // 3. User assigned branches do not match the branches sheet.
        // 4. User assigned regions do not match the regions sheet.
        // 5. User admin assignment branches do not match the branches sheet.
        // 6. User admin assignment regions do not match the regions sheet.
        // 7. User email address is not present
        // Possible warnings
        // 1. There are no branch, region, branch admin, region admin assignments. The user will be added under the company as an individual. Parse each row for users and then check for valid users. On successful validation, check if the user is a new, modified or deleted user.

        LOG.debug( "Parsing users sheet" );
        XSSFSheet userSheet = workBook.getSheet( USERS_SHEET );
        Iterator<Row> rows = userSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        UserUploadVO uploadedUser = null;
        List<UserUploadVO> uploadedUsers = new ArrayList<UserUploadVO>();
        Set<String> emailSet = new HashSet<String>();
        Map<String, String> sourceUserIdErrors = new HashMap<String, String>();
        Map<String, String> emailErrors = new HashMap<String, String>();
        
        List<String> existingEmailsInApplication = userDao.getRegisteredEmailsInOtherCompanies( company );

        //Create header map
        Map<Integer, String> headerMap = new HashMap<Integer, String>();
        headerMap.put( 1, CommonConstants.CHR_USERS_USER_ID );
        headerMap.put( 2, CommonConstants.CHR_USERS_FIRST_NAME );
        headerMap.put( 3, CommonConstants.CHR_USERS_LAST_NAME );
        headerMap.put( 4, CommonConstants.CHR_USERS_TITLE );
        headerMap.put( 5, CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS );
        headerMap.put( 6, CommonConstants.CHR_USERS_REGION_ASSIGNMENTS );
        headerMap.put( 7, CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE );
        headerMap.put( 8, CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE );
        headerMap.put( 9, CommonConstants.CHR_USERS_EMAIL );
        headerMap.put( 10, CommonConstants.CHR_USERS_PHONE );
        headerMap.put( 11, CommonConstants.CHR_USERS_WEBSITE );
        headerMap.put( 12, CommonConstants.CHR_USERS_LICENSE );
        headerMap.put( 13, CommonConstants.CHR_USERS_LEGAL_DISCLAIMER );
        headerMap.put( 14, CommonConstants.CHR_USERS_PHOTO );
        headerMap.put( 15, CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION );


        Map<String, Integer> userMap = null;
        try {
            if ( validationObject != null && validationObject.getUpload() != null ) {
                userMap = generateUserIndexMap( validationObject.getUpload().getUsers(), false );
            }
            if ( userMap == null ) {
                userMap = new HashMap<String, Integer>();
            }
            if ( !rows.hasNext() ) {
                throw new InvalidInputException( CommonConstants.HIERARCHY_USER_HEADERS_INVALID );
            }

            while ( rows.hasNext() ) {
                row = (XSSFRow) rows.next();
                // skip the first 1 rows. first row is the schema and second is the header
                if ( row.getRowNum() < 1 ) {
                    //Validate column headings
                    if ( !isHeaderValid( row, headerMap ) ) {
                        throw new InvalidInputException( CommonConstants.HIERARCHY_USER_HEADERS_INVALID );
                    }
                    continue;
                }
                cells = row.cellIterator();
                uploadedUser = new UserUploadVO();
                int cellIndex = 0;
                uploadedUser.setRowNum( row.getRowNum() + 1 );
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    cellIndex = cell.getColumnIndex();
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {

                        if ( cellIndex == USER_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedUser.setSourceUserId( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ).trim() ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser.setSourceUserId( processSourceId( cell.getStringCellValue().trim() ) );
                            }
                        } else if ( cellIndex == USER_FIRST_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setFirstName( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_LAST_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setLastName( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_TITLE_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setTitle( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_BRANCH_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedUser.setAssignedBranches(
                                    Arrays.asList( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ) ).split( "\\s*,\\s*" ) ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser
                                    .setAssignedBranches( Arrays.asList( processSourceId( cell.getStringCellValue() ).split( "\\s*,\\s*" ) ) );
                            }
                        } else if ( cellIndex == USER_REGION_ID_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedUser.setAssignedRegions(
                                    Arrays.asList( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ) ).split( "\\s*,\\s*" ) ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser
                                    .setAssignedRegions( Arrays.asList( processSourceId( cell.getStringCellValue() ).split( "\\s*,\\s*" ) ) );
                            }
                        } else if ( cellIndex == USER_BRANCH_ID_ADMIN_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedUser.setAssignedBranchesAdmin(
                                    Arrays.asList( processSourceId( String.valueOf( (long) cell.getNumericCellValue() ) ).split( "\\s*,\\s*" ) ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser.setAssignedBranchesAdmin(
                                    Arrays.asList( processSourceId( cell.getStringCellValue() ).split( "\\s*,\\s*" ) ) );
                            }
                        } else if ( cellIndex == USER_REGION_ID_ADMIN_INDEX ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).trim().isEmpty() ) {
                                uploadedUser.setAssignedRegionsAdmin( Arrays.asList(
	                                processSourceId( String.valueOf( (long) cell.getNumericCellValue() ) ).split( "\\s*,\\s*" ) ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser
                                    .setAssignedRegionsAdmin( Arrays.asList( processSourceId( cell.getStringCellValue() ).split( "\\s*,\\s*" ) ) );
                            }
                        } else if ( cellIndex == USER_EMAIL_INDEX ) {
                            String emailId = cell.getStringCellValue().trim();
                            if ( emailId != null && !emailId.isEmpty() ) {
                                uploadedUser.setEmailId( emailId );
                            }
                        } else if ( cellIndex == USER_PHOTO_PROFILE_URL && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setUserPhotoUrl( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_PHONE_NUMBER ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                                && !String.valueOf( (long) cell.getNumericCellValue() ).isEmpty() ) {
                                uploadedUser.setPhoneNumber( String.valueOf( (long) cell.getNumericCellValue() ).trim()
                                    .replaceAll( "[^0-9a-zA-Z\\(\\)\\-]", "" ) );
                            } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                                uploadedUser.setPhoneNumber(
                                    cell.getStringCellValue().trim().replaceAll( "[^0-9a-zA-Z\\(\\)\\-\\s]", "" ) );
                            }
                        } else if ( cellIndex == USER_WEBSITE && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setWebsiteUrl( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_LICENSES && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setLicense( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_LEGAL_DISCLAIMER && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setLegalDisclaimer( cell.getStringCellValue().trim() );
                        } else if ( cellIndex == USER_ABOUT_ME_DESCRIPTION && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setAboutMeDescription( cell.getStringCellValue().trim() );
                        }
                    }
                }
                if ( isUserUploadEmpty( uploadedUser ) ) {
                    continue;
                }
                uploadedUser.setInAppendMode( false );
                if ( isAppend ) {
                    uploadedUser.setInAppendMode( true );
                }
                if ( uploadedUser.getSourceBranchId() == null && uploadedUser.getSourceRegionId() == null
                    && ( uploadedUser.getAssignedBranchesAdmin() == null || uploadedUser.getAssignedBranchesAdmin().isEmpty() )
                    && ( uploadedUser.getAssignedRegionsAdmin() == null
                        || uploadedUser.getAssignedRegionsAdmin().isEmpty() ) ) {
                    uploadedUser.setBelongsToCompany( true );
                }

                // check if user is added or modified
                if ( isNewUser( uploadedUser, validationObject.getUpload().getUsers() ) ) {
                    validationObject.setNumberOfUsersAdded( validationObject.getNumberOfUsersAdded() + 1 );
                    uploadedUser.setUserAdded( true );
                    validationObject.getUpload().getUsers().add( uploadedUser );
                } else {
                    updateUploadValidationWithModifiedUser( uploadedUser, validationObject, userMap );
                }
                uploadedUsers.add( uploadedUser );
                
                //check if email is already present in other companies
                if(existingEmailsInApplication.contains( uploadedUser.getEmailId() )){
                    emailErrors.put( uploadedUser.getSourceUserId(), "The email address " + uploadedUser.getEmailId()
                        + " is already registered with other company at row : " + uploadedUser.getRowNum() );
                }
                
                //Check for email address duplication
                if ( emailSet.contains( uploadedUser.getEmailId() ) ) {
                    emailErrors.put( uploadedUser.getSourceUserId(), "The email address " + uploadedUser.getEmailId()
                        + " is duplicated at row : " + uploadedUser.getRowNum() );
                } else {
                    emailSet.add( uploadedUser.getEmailId() );
                }

                //check for duplicate source ids
                checkForDuplicateSourceUserIds( sourceUserIdErrors, uploadedUser );
            }

            userErrors.putAll( emailErrors );
            List<String> errors = new ArrayList<String>();
            if ( !sourceUserIdErrors.isEmpty() ) {
                for ( String key : sourceUserIdErrors.keySet() ) {
                    if ( sourceUserIdErrors.get( key ) != null && !sourceUserIdErrors.get( key ).isEmpty() ) {
                        errors.add( sourceUserIdErrors.get( key ) );
                        if ( userErrors.containsKey( key ) ) {
                            //Append
                            String value = userErrors.get( key );
                            value += ", " + sourceUserIdErrors.get( key );
                            userErrors.put( key, value );
                        } else {
                            //Add
                            userErrors.put( key, sourceUserIdErrors.get( key ) );
                        }
                    }
                }
                validationObject.setUserValidationErrors( errors );
            }
            //In case of append mode, we skip deletion
            if ( !isAppend ) {
                markDeletedUsers( uploadedUsers, validationObject );
            }
        } catch ( InvalidInputException ex ) {
            validationObject.setUserHeadersInvalid( true );
        }
    }


    boolean isHeaderValid( XSSFRow row, Map<Integer, String> headerMap )
    {
        if ( row == null ) {
            return false;
        }
        Iterator<Cell> cells = row.cellIterator();
        int cellNo = 0;
        if ( !cells.hasNext() ) {
            return false;
        }
        while ( cells.hasNext() ) {
            cellNo += 1;
            Cell cell = cells.next();
            if ( !cell.getStringCellValue().equalsIgnoreCase( headerMap.get( cellNo ) ) ) {
                return false;
            }
        }
        if ( cellNo != headerMap.size() ) {
            return false;
        }
        return true;
    }


    boolean isNewRegion( RegionUploadVO uploadedRegion, List<RegionUploadVO> existingRegions,
        List<RegionUploadVO> uploadedRegions )
    {
        if ( existingRegions != null && !existingRegions.contains( uploadedRegion ) ) {
            return true;
        } else if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
            return true;
        } else if ( uploadedRegions.contains( uploadedRegion ) ) {
            return true;
        }
        return false;
    }


    boolean isNewBranch( BranchUploadVO uploadedBranch, List<BranchUploadVO> existingBranches,
        List<BranchUploadVO> uploadedBranches )
    {
        if ( existingBranches != null && !existingBranches.contains( uploadedBranch ) ) {
            return true;
        } else if ( uploadedBranch.getSourceBranchId() == null || uploadedBranch.getSourceBranchId().isEmpty() ) {
            return true;
        } else if ( uploadedBranches.contains( uploadedBranch ) ) {
            return true;
        }
        return false;
    }


    boolean isNewUser( UserUploadVO uploadedUser, List<UserUploadVO> uploadedUsers )
    {
        if ( uploadedUsers != null && !uploadedUsers.contains( uploadedUser ) ) {
            return true;
        } else if ( uploadedUser.getSourceUserId() == null || uploadedUser.getSourceUserId().isEmpty() ) {
            return true;
        } else {
            return false;
        }
    }


    void updateUploadValidationWithModifiedRegion( RegionUploadVO uploadedRegion, UploadValidation validationObject,
        Map<String, Integer> regionMap )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getRegions().isEmpty() ) {
            if ( regionMap.containsKey( uploadedRegion.getSourceRegionId() ) ) {
                int index = regionMap.get( uploadedRegion.getSourceRegionId() );
                RegionUploadVO region = validationObject.getUpload().getRegions().get( index );
                if ( !region.isRegionAdded() ) {
                    if ( ( region.getRegionName() != null && uploadedRegion.getRegionName() == null )
                        || ( region.getRegionName() == null && uploadedRegion.getRegionName() != null )
                        || ( region.getRegionName() != null && uploadedRegion.getRegionName() != null
                            && !region.getRegionName().equalsIgnoreCase( uploadedRegion.getRegionName() ) ) ) {
                        //Update history
                        region.setRegionNameHistory( updateHistory( region.getRegionNameHistory(),
                            uploadedRegion.getRegionName(), region.getRegionName() ) );

                        region.setRegionName( uploadedRegion.getRegionName() );
                        region.setRegionNameModified( true );
                    } else {
                        region.setRegionNameModified( false );
                    }
                    if ( ( region.getRegionAddress1() != null && uploadedRegion.getRegionAddress1() == null )
                        || ( region.getRegionAddress1() == null && uploadedRegion.getRegionAddress1() != null )
                        || ( region.getRegionAddress1() != null && uploadedRegion.getRegionAddress1() != null
                            && !region.getRegionAddress1().equalsIgnoreCase( uploadedRegion.getRegionAddress1() ) ) ) {

                        //Update history
                        region.setRegionAddress1History( updateHistory( region.getRegionAddress1History(),
                            uploadedRegion.getRegionAddress1(), region.getRegionAddress1() ) );

                        region.setRegionAddress1( uploadedRegion.getRegionAddress1() );
                        region.setRegionAddress1Modified( true );

                    } else {
                        region.setRegionAddress1Modified( false );
                    }
                    if ( ( region.getRegionAddress2() != null && uploadedRegion.getRegionAddress2() == null )
                        || ( region.getRegionAddress2() == null && uploadedRegion.getRegionAddress2() != null )
                        || ( region.getRegionAddress2() != null && uploadedRegion.getRegionAddress2() != null
                            && !region.getRegionAddress2().equalsIgnoreCase( uploadedRegion.getRegionAddress2() ) ) ) {

                        //Update history
                        region.setRegionAddress2History( updateHistory( region.getRegionAddress2History(),
                            uploadedRegion.getRegionAddress2(), region.getRegionAddress2() ) );

                        region.setRegionAddress2( uploadedRegion.getRegionAddress2() );
                        region.setRegionAddress2Modified( true );
                    } else {
                        region.setRegionAddress2Modified( false );
                    }
                    if ( ( region.getRegionCity() != null && uploadedRegion.getRegionCity() == null )
                        || ( region.getRegionCity() == null && uploadedRegion.getRegionCity() != null )
                        || ( region.getRegionCity() != null && uploadedRegion.getRegionCity() != null
                            && !region.getRegionCity().equalsIgnoreCase( uploadedRegion.getRegionCity() ) ) ) {

                        //Update history
                        region.setRegionCityHistory( updateHistory( region.getRegionCityHistory(),
                            uploadedRegion.getRegionCity(), region.getRegionCity() ) );

                        region.setRegionCity( uploadedRegion.getRegionCity() );
                        region.setRegionCityModified( true );
                    } else {
                        region.setRegionCityModified( false );
                    }
                    if ( ( region.getRegionState() != null && uploadedRegion.getRegionState() == null )
                        || ( region.getRegionState() == null && uploadedRegion.getRegionState() != null )
                        || ( region.getRegionState() != null && uploadedRegion.getRegionState() != null
                            && !region.getRegionState().equalsIgnoreCase( uploadedRegion.getRegionState() ) ) ) {

                        //Update history
                        region.setRegionStateHistory( updateHistory( region.getRegionStateHistory(),
                            uploadedRegion.getRegionState(), region.getRegionState() ) );

                        region.setRegionState( uploadedRegion.getRegionState() );
                        region.setRegionStateModified( true );
                    } else {
                        region.setRegionStateModified( false );
                    }
                    if ( ( region.getRegionZipcode() != null && uploadedRegion.getRegionZipcode() == null )
                        || ( region.getRegionZipcode() == null && uploadedRegion.getRegionZipcode() != null )
                        || ( region.getRegionZipcode() != null && uploadedRegion.getRegionZipcode() != null
                            && !region.getRegionZipcode().equalsIgnoreCase( uploadedRegion.getRegionZipcode() ) ) ) {

                        //Update history
                        region.setRegionZipcodeHistory( updateHistory( region.getRegionZipcodeHistory(),
                            uploadedRegion.getRegionZipcode(), region.getRegionZipcode() ) );

                        region.setRegionZipcode( uploadedRegion.getRegionZipcode() );
                        region.setRegionZipcodeModified( true );
                    } else {
                        region.setRegionZipcodeModified( false );
                    }
                    if ( region.isRegionNameModified() || region.isRegionAddress1Modified() || region.isRegionAddress2Modified()
                        || region.isRegionCityModified() || region.isRegionStateModified()
                        || region.isRegionZipcodeModified() ) {
                        validationObject.setNumberOfRegionsModified( validationObject.getNumberOfRegionsModified() + 1 );
                        region.setRegionModified( true );
                    } else {
                        region.setRegionModified( false );
                    }
                    //Set new row number for the region
                    region.setRowNum( uploadedRegion.getRowNum() );
                    region.setInAppendMode( uploadedRegion.isInAppendMode() );
                }
            }
        }
    }


    /**
     * Method to get the latest history entry from a list of StringUploadHistory
     * @param uploadHistories
     * @return
     */
    StringUploadHistory getLatestStringUploadHistoryEntry( List<StringUploadHistory> uploadHistories )
    {
        StringUploadHistory latestEntry = null;
        for ( StringUploadHistory stringUploadHistory : uploadHistories ) {
            if ( latestEntry == null ) {
                latestEntry = stringUploadHistory;
            } else if ( latestEntry.getTime().before( stringUploadHistory.getTime() ) ) {
                latestEntry = stringUploadHistory;
            }
        }
        return latestEntry;
    }


    /**
     * Method to get the latest history entry from a list of StringListUploadHistory
     * @param uploadHistories
     * @return
     */
    StringListUploadHistory getLatestStringListUploadHistoryEntry( List<StringListUploadHistory> uploadHistories )
    {
        StringListUploadHistory latestEntry = null;
        for ( StringListUploadHistory stringListUploadHistory : uploadHistories ) {
            if ( latestEntry == null ) {
                latestEntry = stringListUploadHistory;
            } else if ( latestEntry.getTime().before( stringListUploadHistory.getTime() ) ) {
                latestEntry = stringListUploadHistory;
            }
        }
        return latestEntry;
    }


    /**
     * Returns StringUploadHistory object if history needs to be updated
     * Returns null otherwise
     * @param historyList
     * @param latestValue
     * @param oldValue
     * @return
     */
    List<StringUploadHistory> updateHistory( List<StringUploadHistory> historyList, String latestValue, String oldValue )
    {
        if ( historyList == null ) {
            historyList = new ArrayList<StringUploadHistory>();
        }
        //check if current value is already the latest value in history
        StringUploadHistory latestHistoryEntry = getLatestStringUploadHistoryEntry( historyList );
        StringUploadHistory history = null;
        if ( latestHistoryEntry == null || !latestHistoryEntry.getValue().equals( oldValue ) ) {
            history = new StringUploadHistory();
            history.setTime( new Date( System.currentTimeMillis() ) );
            history.setValue( oldValue );
            historyList.add( history );
        }
        return historyList;
    }


    /**
     * Returns StringListUploadHistory object if history needs to be updated
     * Returns null otherwise
     * @param historyList
     * @param latestValue
     * @param oldValue
     */
    List<StringListUploadHistory> updateHistory( List<StringListUploadHistory> historyList, List<String> latestValue,
        List<String> oldValue )
    {
        if ( historyList == null ) {
            historyList = new ArrayList<>();
        }
        //Check if current value is already the latest value in history
        StringListUploadHistory latestHistoryEntry = getLatestStringListUploadHistoryEntry( historyList );
        StringListUploadHistory history = null;
        if ( latestHistoryEntry == null || !latestHistoryEntry.getValue().equals( oldValue ) ) {
            history = new StringListUploadHistory();
            history.setTime( new Date( System.currentTimeMillis() ) );
            history.setValue( oldValue );
            historyList.add( history );
        }
        return historyList;
    }


    void updateUploadValidationWithModifiedBranch( BranchUploadVO uploadedBranch, UploadValidation validationObject,
        Map<String, Integer> branchMap )
    {

        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getBranches().isEmpty() ) {
            if ( branchMap.containsKey( uploadedBranch.getSourceBranchId() ) ) {
                int index = branchMap.get( uploadedBranch.getSourceBranchId() );
                BranchUploadVO branch = validationObject.getUpload().getBranches().get( index );
                if ( !branch.isBranchAdded() ) {
                    if ( ( branch.getBranchName() == null && uploadedBranch.getBranchName() != null )
                        || ( branch.getBranchName() != null && uploadedBranch.getBranchName() == null )
                        || ( branch.getBranchName() != null && uploadedBranch.getBranchName() != null
                            && !branch.getBranchName().equalsIgnoreCase( uploadedBranch.getBranchName() ) ) ) {

                        //Update branch name history
                        branch.setBranchNameHistory( updateHistory( branch.getBranchNameHistory(),
                            uploadedBranch.getBranchName(), branch.getBranchName() ) );

                        branch.setBranchName( uploadedBranch.getBranchName() );
                        branch.setBranchNameModified( true );
                    } else {
                        branch.setBranchNameModified( false );
                    }
                    if ( ( branch.getBranchAddress1() == null && uploadedBranch.getBranchAddress1() != null )
                        || ( branch.getBranchAddress1() != null && uploadedBranch.getBranchAddress1() == null )
                        || ( branch.getBranchAddress1() != null && uploadedBranch.getBranchAddress1() != null
                            && !branch.getBranchAddress1().equalsIgnoreCase( uploadedBranch.getBranchAddress1() ) ) ) {

                        //Update branch address 1 history
                        branch.setBranchAddress1History( updateHistory( branch.getBranchAddress1History(),
                            uploadedBranch.getBranchAddress1(), branch.getBranchAddress1() ) );

                        branch.setBranchAddress1( uploadedBranch.getBranchAddress1() );
                        branch.setBranchAddress1Modified( true );
                    } else {
                        branch.setBranchAddress1Modified( false );
                    }
                    if ( branch.getBranchAddress1() != null && !branch.getBranchAddress1().isEmpty() ) {
                        branch.setAddressSet( true );
                    } else {
                        branch.setAddressSet( false );
                    }
                    if ( ( branch.getBranchAddress2() == null && uploadedBranch.getBranchAddress2() != null )
                        || ( branch.getBranchAddress2() != null && uploadedBranch.getBranchAddress2() == null )
                        || ( branch.getBranchAddress2() != null && uploadedBranch.getBranchAddress2() != null
                            && !branch.getBranchAddress2().equalsIgnoreCase( uploadedBranch.getBranchAddress2() ) ) ) {

                        //Update branch address 2 history
                        branch.setBranchAddress2History( updateHistory( branch.getBranchAddress2History(),
                            uploadedBranch.getBranchAddress2(), branch.getBranchAddress2() ) );

                        branch.setBranchAddress2( uploadedBranch.getBranchAddress2() );
                        branch.setBranchAddress2Modified( true );
                    } else {
                        branch.setBranchAddress2Modified( false );
                    }
                    if ( ( branch.getBranchCity() == null && uploadedBranch.getBranchCity() != null )
                        || ( branch.getBranchCity() != null && uploadedBranch.getBranchCity() == null )
                        || ( branch.getBranchCity() != null && uploadedBranch.getBranchCity() != null
                            && !branch.getBranchCity().equalsIgnoreCase( uploadedBranch.getBranchCity() ) ) ) {

                        //Update branch city history
                        branch.setBranchCityHistory( updateHistory( branch.getBranchCityHistory(),
                            uploadedBranch.getBranchCity(), branch.getBranchCity() ) );

                        branch.setBranchCity( uploadedBranch.getBranchCity() );
                        branch.setBranchCityModified( true );
                    } else {
                        branch.setBranchCityModified( false );
                    }
                    if ( ( branch.getBranchState() == null && uploadedBranch.getBranchState() != null )
                        || ( branch.getBranchState() != null && uploadedBranch.getBranchState() == null )
                        || ( branch.getBranchState() != null && uploadedBranch.getBranchState() != null
                            && !branch.getBranchState().equalsIgnoreCase( uploadedBranch.getBranchState() ) ) ) {

                        //Update branch state history
                        branch.setBranchStateHistory( updateHistory( branch.getBranchStateHistory(),
                            uploadedBranch.getBranchState(), branch.getBranchState() ) );

                        branch.setBranchState( uploadedBranch.getBranchState() );
                        branch.setBranchStateModified( true );
                    } else {
                        branch.setBranchStateModified( false );
                    }
                    if ( ( branch.getBranchZipcode() == null && uploadedBranch.getBranchZipcode() != null )
                        || ( branch.getBranchZipcode() != null && uploadedBranch.getBranchZipcode() == null )
                        || ( branch.getBranchZipcode() != null && uploadedBranch.getBranchZipcode() != null
                            && !branch.getBranchZipcode().equalsIgnoreCase( uploadedBranch.getBranchZipcode() ) ) ) {

                        //Update branch zipcode history
                        branch.setBranchZipcodeHistory( updateHistory( branch.getBranchZipcodeHistory(),
                            uploadedBranch.getBranchZipcode(), branch.getBranchZipcode() ) );

                        branch.setBranchZipcode( uploadedBranch.getBranchZipcode() );
                        branch.setBranchZipcodeModified( true );
                    } else {
                        branch.setBranchZipcodeModified( false );
                    }
                    if ( ( branch.getSourceRegionId() == null && uploadedBranch.getSourceRegionId() != null )
                        || ( branch.getSourceRegionId() != null && uploadedBranch.getSourceRegionId() == null )
                        || ( branch.getSourceRegionId() != null && uploadedBranch.getSourceRegionId() != null
                            && !branch.getSourceRegionId().equalsIgnoreCase( uploadedBranch.getSourceRegionId() ) ) ) {

                        //Update branch sourceRegionId history
                        branch.setSourceBranchIdHistory( updateHistory( branch.getSourceRegionIdHistory(),
                            uploadedBranch.getSourceRegionId(), branch.getSourceRegionId() ) );

                        branch.setSourceRegionId( uploadedBranch.getSourceRegionId() );
                        branch.setSourceRegionIdModified( true );
                    } else {
                        branch.setSourceRegionIdModified( false );
                    }
                    if ( branch.isBranchNameModified() || branch.isBranchAddress1Modified() || branch.isBranchAddress2Modified()
                        || branch.isBranchCityModified() || branch.isBranchStateModified() || branch.isBranchZipcodeModified()
                        || branch.isSourceRegionIdModified() ) {
                        validationObject.setNumberOfBranchesModified( validationObject.getNumberOfBranchesModified() + 1 );
                        branch.setBranchModified( true );
                    } else {
                        branch.setBranchModified( false );
                    }
                    //Set new row number for the branch
                    branch.setRowNum( uploadedBranch.getRowNum() );
                    branch.setInAppendMode( uploadedBranch.isInAppendMode() );
                }
            }
        }
    }


    void updateUploadValidationWithModifiedUser( UserUploadVO uploadedUser, UploadValidation validationObject,
        Map<String, Integer> userMap )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getUsers() != null
            && !validationObject.getUpload().getUsers().isEmpty() ) {
            if ( userMap.containsKey( uploadedUser.getSourceUserId() ) ) {
                int index = userMap.get( uploadedUser.getSourceUserId() );
                UserUploadVO user = validationObject.getUpload().getUsers().get( index );
                if ( !user.isUserAdded() ) {
                    if ( ( user.getFirstName() == null && uploadedUser.getFirstName() != null )
                        || ( user.getFirstName() != null && uploadedUser.getFirstName() == null )
                        || ( user.getFirstName() != null && uploadedUser.getFirstName() != null
                            && !user.getFirstName().equalsIgnoreCase( uploadedUser.getFirstName() ) ) ) {


                        //Update first name history
                        user.setFirstNameHistory(
                            updateHistory( user.getFirstNameHistory(), uploadedUser.getFirstName(), user.getFirstName() ) );

                        user.setFirstName( uploadedUser.getFirstName() );
                        user.setFirstNameModified( true );
                    } else {
                        user.setFirstNameModified( false );
                    }

                    try {
                        if ( ( user.getLastName() == null && uploadedUser.getLastName() != null )
                            || ( user.getLastName() != null && uploadedUser.getLastName() == null )
                            || ( user.getLastName() != null
                                && uploadedUser.getLastName() != null
                                && !user.getLastName().equalsIgnoreCase( uploadedUser.getLastName() )
                                && !user.getLastName()
                                    .equalsIgnoreCase(
                                        new String( uploadedUser.getLastName().getBytes( Charset.forName( "ISO-8859-1" ) ),
                                            "UTF-8" ) ) && !new String( user.getLastName().getBytes(
                                Charset.forName( "ISO-8859-1" ) ), "UTF-8" ).equalsIgnoreCase( uploadedUser.getLastName() ) ) ) {
                            user.setLastName( uploadedUser.getLastName() );
                            user.setLastNameModified( true );
                        } else {
                            user.setLastNameModified( false );
                        }
                    } catch ( UnsupportedEncodingException e1 ) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                    if ( ( user.getTitle() == null && uploadedUser.getTitle() != null )
                        || ( user.getTitle() != null && uploadedUser.getTitle() == null )
                        || ( user.getTitle() != null && uploadedUser.getTitle() != null
                            && !user.getTitle().equalsIgnoreCase( uploadedUser.getTitle() ) ) ) {

                        //Update title history
                        user.setTitleHistory(
                            updateHistory( user.getTitleHistory(), uploadedUser.getTitle(), user.getTitle() ) );

                        user.setTitle( uploadedUser.getTitle() );
                        user.setTitleModified( true );
                    } else {
                        user.setTitleModified( false );
                    }
                    if ( ( ( user.getAssignedBranchesAdmin() != null && !user.getAssignedBranchesAdmin().isEmpty() )
                        && ( uploadedUser.getAssignedBranchesAdmin() == null
                            || uploadedUser.getAssignedBranchesAdmin().isEmpty() ) )
                        || ( ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() )
                            && ( uploadedUser.getAssignedBranchesAdmin() != null
                                && !uploadedUser.getAssignedBranchesAdmin().isEmpty() ) )
                        || ( user.getAssignedBranchesAdmin() != null && uploadedUser.getAssignedBranchesAdmin() != null
                            && !( user.getAssignedBranchesAdmin().containsAll( uploadedUser.getAssignedBranchesAdmin() )
                                && uploadedUser.getAssignedBranchesAdmin()
                                    .containsAll( user.getAssignedBranchesAdmin() ) ) ) ) {

                        //Update assignedBranchesAdmin history
                        user.setAssignedBrachesAdminHistory( updateHistory( user.getAssignedBrachesAdminHistory(),
                            uploadedUser.getAssignedBranchesAdmin(), user.getAssignedBranchesAdmin() ) );

                        user.setAssignedBranchesAdmin( uploadedUser.getAssignedBranchesAdmin() );
                        user.setAssignedBrachesAdminModified( true );
                    } else {
                        user.setAssignedBrachesAdminModified( false );
                    }
                    if ( ( ( user.getAssignedRegionsAdmin() != null && !user.getAssignedRegionsAdmin().isEmpty() )
                        && ( uploadedUser.getAssignedRegionsAdmin() == null
                            || uploadedUser.getAssignedRegionsAdmin().isEmpty() ) )
                        || ( ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() )
                            && ( uploadedUser.getAssignedRegionsAdmin() != null
                                && !uploadedUser.getAssignedRegionsAdmin().isEmpty() ) )
                        || ( user.getAssignedRegionsAdmin() != null && uploadedUser.getAssignedRegionsAdmin() != null
                            && !( user.getAssignedRegionsAdmin().containsAll( uploadedUser.getAssignedRegionsAdmin() )
                                && uploadedUser.getAssignedRegionsAdmin().containsAll( user.getAssignedRegionsAdmin() ) ) ) ) {

                        //Update assignedRegionsAdmin history
                        user.setAssignedRegionsAdminHistory( updateHistory( user.getAssignedRegionsAdminHistory(),
                            uploadedUser.getAssignedRegionsAdmin(), user.getAssignedRegionsAdmin() ) );

                        user.setAssignedRegionsAdmin( uploadedUser.getAssignedRegionsAdmin() );
                        user.setAssignedRegionsAdminModified( true );
                    } else {
                        user.setAssignedRegionsAdminModified( false );
                    }
                    if ( ( ( user.getAssignedBranches() != null && !user.getAssignedBranches().isEmpty() )
                        && ( uploadedUser.getAssignedBranches() == null || uploadedUser.getAssignedBranches().isEmpty() ) )
                        || ( ( user.getAssignedBranches() == null || user.getAssignedBranches().isEmpty() )
                            && ( uploadedUser.getAssignedBranches() != null && !uploadedUser.getAssignedBranches().isEmpty() ) )
                        || ( user.getAssignedBranches() != null && uploadedUser.getAssignedBranches() != null
                            && !( uploadedUser.getAssignedBranches().containsAll( user.getAssignedBranches() )
                                && user.getAssignedBranches().containsAll( uploadedUser.getAssignedBranches() ) ) ) ) {

                        //Update assignedBranches history
                        user.setAssignedBranchesHistory( updateHistory( user.getAssignedBranchesHistory(),
                            uploadedUser.getAssignedBranches(), user.getAssignedBranches() ) );

                        user.setAssignedBranches( uploadedUser.getAssignedBranches() );
                        user.setAssignedBranchesModified( true );
                    } else {
                        user.setAssignedBranchesModified( false );
                    }
                    if ( ( ( user.getAssignedRegions() != null && !user.getAssignedRegions().isEmpty() )
                        && ( uploadedUser.getAssignedRegions() == null || uploadedUser.getAssignedRegions().isEmpty() ) )
                        || ( ( user.getAssignedRegions() == null || user.getAssignedRegions().isEmpty() )
                            && ( uploadedUser.getAssignedRegions() != null && !uploadedUser.getAssignedRegions().isEmpty() ) )
                        || ( user.getAssignedRegions() != null && uploadedUser.getAssignedRegions() != null
                            && !( user.getAssignedRegions().containsAll( uploadedUser.getAssignedRegions() )
                                && uploadedUser.getAssignedRegions().containsAll( user.getAssignedRegions() ) ) ) ) {

                        //Update assignedRegions history
                        user.setAssignedRegionsHistory( updateHistory( user.getAssignedRegionsHistory(),
                            uploadedUser.getAssignedRegions(), user.getAssignedRegions() ) );

                        user.setAssignedRegions( uploadedUser.getAssignedRegions() );
                        user.setAssignedRegionsModified( true );
                    } else {
                        user.setAssignedRegionsModified( false );
                    }
                    if ( ( user.getEmailId() != null && uploadedUser.getEmailId() == null )
                        || ( user.getEmailId() == null && uploadedUser.getEmailId() != null ) ) {

                        //Update emailId history
                        user.setEmailIdHistory(
                            updateHistory( user.getEmailIdHistory(), uploadedUser.getEmailId(), user.getEmailId() ) );

                        user.setEmailId( uploadedUser.getEmailId() );
                        user.setEmailIdModified( true );
                    } else {
                        String emailId = uploadedUser.getEmailId();
                        if ( ( user.getEmailId() != null && emailId != null
                            && !user.getEmailId().equalsIgnoreCase( emailId ) ) ) {

                            //Update emailId history
                            user.setEmailIdHistory(
                                updateHistory( user.getEmailIdHistory(), uploadedUser.getEmailId(), user.getEmailId() ) );

                            user.setEmailId( uploadedUser.getEmailId() );
                            user.setEmailIdModified( true );
                        } else {
                            user.setEmailIdModified( false );
                        }
                    }
                    if ( ( user.getPhoneNumber() != null && uploadedUser.getPhoneNumber() == null )
                        || ( user.getPhoneNumber() == null && uploadedUser.getPhoneNumber() != null )
                        || ( user.getPhoneNumber() != null && uploadedUser.getPhoneNumber() != null
                            && !user.getPhoneNumber().equalsIgnoreCase( uploadedUser.getPhoneNumber() ) ) ) {

                        //Update phone number history
                        user.setPhoneNumberHistory( updateHistory( user.getPhoneNumberHistory(), uploadedUser.getPhoneNumber(),
                            user.getPhoneNumber() ) );

                        user.setPhoneNumber( uploadedUser.getPhoneNumber() );
                        user.setPhoneNumberModified( true );
                    } else {
                        user.setPhoneNumberModified( false );
                    }
                    if ( ( user.getWebsiteUrl() != null && uploadedUser.getWebsiteUrl() == null )
                        || ( user.getWebsiteUrl() == null && uploadedUser.getWebsiteUrl() != null )
                        || ( user.getWebsiteUrl() != null && uploadedUser.getWebsiteUrl() != null
                            && !user.getWebsiteUrl().equalsIgnoreCase( uploadedUser.getWebsiteUrl() ) ) ) {

                        //Update websiteUrl history
                        user.setWebsiteUrlHistory(
                            updateHistory( user.getWebsiteUrlHistory(), uploadedUser.getWebsiteUrl(), user.getWebsiteUrl() ) );

                        user.setWebsiteUrl( uploadedUser.getWebsiteUrl() );
                        user.setWebsiteUrlModified( true );
                    } else {
                        user.setWebsiteUrlModified( false );
                    }
                    if ( ( user.getLicense() != null && uploadedUser.getLicense() == null )
                        || ( user.getLicense() == null && uploadedUser.getLicense() != null )
                        || ( user.getLicense() != null && uploadedUser.getLicense() != null
                            && !user.getLicense().equalsIgnoreCase( uploadedUser.getLicense() ) ) ) {

                        //Update license history
                        user.setLicenseHistory(
                            updateHistory( user.getLicenseHistory(), uploadedUser.getLicense(), user.getLicense() ) );

                        user.setLicense( uploadedUser.getLicense() );
                        user.setLicenseModified( true );
                    } else {
                        user.setLicenseModified( false );
                    }

                    try {
                        if ( ( user.getLegalDisclaimer() != null && uploadedUser.getLegalDisclaimer() == null )
                            || ( user.getLegalDisclaimer() == null && uploadedUser.getLegalDisclaimer() != null )
                            || ( user.getLegalDisclaimer() != null
                                && uploadedUser.getLegalDisclaimer() != null
                                && !user.getLegalDisclaimer().equalsIgnoreCase( uploadedUser.getLegalDisclaimer() )
                                && !user.getLegalDisclaimer().equalsIgnoreCase(
                                    new String( uploadedUser.getLegalDisclaimer().getBytes( Charset.forName( "ISO-8859-1" ) ),
                                        "UTF-8" ) ) && !new String( user.getLegalDisclaimer().getBytes(
                                Charset.forName( "ISO-8859-1" ) ), "UTF-8" ).equalsIgnoreCase( uploadedUser
                                .getLegalDisclaimer() ) ) ) {
                            user.setLegalDisclaimer( uploadedUser.getLegalDisclaimer() );
                            user.setLegalDisclaimerModified( true );
                        } else {
                            user.setLegalDisclaimerModified( false );
                        }
                    } catch ( UnsupportedEncodingException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if ( ( user.getUserPhotoUrl() != null && uploadedUser.getUserPhotoUrl() == null )
                        || ( user.getUserPhotoUrl() == null && uploadedUser.getUserPhotoUrl() != null )
                        || ( user.getUserPhotoUrl() != null && uploadedUser.getUserPhotoUrl() != null
                            && !user.getUserPhotoUrl().equalsIgnoreCase( uploadedUser.getUserPhotoUrl() ) ) ) {

                        //Update userPhotoUrl history
                        user.setUserPhotoUrlHistory( updateHistory( user.getUserPhotoUrlHistory(),
                            uploadedUser.getUserPhotoUrl(), user.getUserPhotoUrl() ) );

                        user.setUserPhotoUrl( uploadedUser.getUserPhotoUrl() );
                        user.setUserPhotoUrlModified( true );
                    } else {
                        user.setUserPhotoUrlModified( false );
                    }
                    try {
                        if ( ( user.getAboutMeDescription() != null && uploadedUser.getAboutMeDescription() == null )
                            || ( user.getAboutMeDescription() == null && uploadedUser.getAboutMeDescription() != null )
                            || ( user.getAboutMeDescription() != null
                                && uploadedUser.getAboutMeDescription() != null
                                && !user.getAboutMeDescription().equalsIgnoreCase( uploadedUser.getAboutMeDescription() )
                                && !user.getAboutMeDescription().equalsIgnoreCase(
                                    new String(
                                        uploadedUser.getAboutMeDescription().getBytes( Charset.forName( "ISO-8859-1" ) ),
                                        "UTF-8" ) ) && !new String( user.getAboutMeDescription().getBytes(
                                Charset.forName( "ISO-8859-1" ) ), "UTF-8" ).equalsIgnoreCase( uploadedUser
                                .getAboutMeDescription() ) ) ) {
                            user.setAboutMeDescription( uploadedUser.getAboutMeDescription() );
                            user.setAboutMeDescriptionModified( true );
                        } else {
                            user.setAboutMeDescriptionModified( false );
                        }
                    } catch ( UnsupportedEncodingException e ) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if ( user.isFirstNameModified() || user.isLastNameModified() || user.isTitleModified()
                        || user.isAssignedBranchesModified() || user.isAssignedRegionsModified()
                        || user.isAssignedBrachesAdminModified() || user.isAssignedRegionsAdminModified()
                        || user.isEmailIdModified() || user.isPhoneNumberModified() || user.isWebsiteUrlModified()
                        || user.isLicenseModified() || user.isLegalDisclaimerModified() || user.isUserPhotoUrlModified()
                        || user.isAboutMeDescriptionModified() ) {
                        validationObject.setNumberOfUsersModified( validationObject.getNumberOfUsersModified() + 1 );
                        user.setUserModified( true );
                    } else {
                        user.setUserModified( false );
                    }
                    //Set new row number for the user
                    user.setRowNum( uploadedUser.getRowNum() );
                    user.setInAppendMode( uploadedUser.isInAppendMode() );
                }
            }
        }
    }


    void markDeletedRegions( List<RegionUploadVO> uploadedRegions, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getRegions().isEmpty() ) {
            Map<String, RegionUploadVO> uploadedRegionMap = new HashMap<String, RegionUploadVO>();
            if ( uploadedRegions != null && !uploadedRegions.isEmpty() ) {
                for ( RegionUploadVO uploadedRegion : uploadedRegions ) {
                    uploadedRegionMap.put( uploadedRegion.getSourceRegionId(), uploadedRegion );
                }
            }
            for ( RegionUploadVO region : validationObject.getUpload().getRegions() ) {
                if ( region.getSourceRegionId() != null && !region.getSourceRegionId().isEmpty()
                    && ( !uploadedRegions.contains( region )
                        || uploadedRegionMap.get( region.getSourceRegionId() ).isDeletedRecord() ) ) {
                    region.setDeletedRecord( true );
                    region.setRowNum( 0 );
                    validationObject.setNumberOfRegionsDeleted( validationObject.getNumberOfRegionsDeleted() + 1 );
                }
            }
        }
    }


    void markDeletedBranches( List<BranchUploadVO> uploadedBranches, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getBranches() != null
            && !validationObject.getUpload().getBranches().isEmpty() ) {
            Map<String, BranchUploadVO> uploadedBranchMap = new HashMap<String, BranchUploadVO>();
            if ( uploadedBranches != null && !uploadedBranches.isEmpty() ) {
                for ( BranchUploadVO uploadedBranch : uploadedBranches ) {
                    uploadedBranchMap.put( uploadedBranch.getSourceBranchId(), uploadedBranch );
                }
            }
            for ( BranchUploadVO branch : validationObject.getUpload().getBranches() ) {
                if ( branch.getSourceBranchId() != null && !branch.getSourceBranchId().isEmpty()
                    && ( !uploadedBranches.contains( branch )
                        || uploadedBranchMap.get( branch.getSourceBranchId() ).isDeletedRecord() ) ) {
                    branch.setDeletedRecord( true );
                    branch.setRowNum( 0 );
                    validationObject.setNumberOfBranchesDeleted( validationObject.getNumberOfBranchesDeleted() + 1 );
                }
            }
        }
    }


    void markDeletedUsers( List<UserUploadVO> uploadeUsers, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getUsers() != null
            && !validationObject.getUpload().getUsers().isEmpty() ) {
            Map<String, UserUploadVO> uploadedUserMap = new HashMap<String, UserUploadVO>();
            if ( uploadeUsers != null && !uploadeUsers.isEmpty() ) {
                for ( UserUploadVO uploadedUser : uploadeUsers ) {
                    uploadedUserMap.put( uploadedUser.getSourceUserId(), uploadedUser );
                }
            }
            for ( UserUploadVO user : validationObject.getUpload().getUsers() ) {
                if ( user.getSourceUserId() != null && !user.getSourceUserId().isEmpty()
                    && ( !uploadeUsers.contains( user ) || uploadedUserMap.get( user.getSourceUserId() ).isDeletedRecord() ) ) {
                    user.setDeletedRecord( true );
                    user.setRowNum( 0 );
                    validationObject.setNumberOfUsersDeleted( validationObject.getNumberOfUsersDeleted() + 1 );
                }
            }
        }
    }


    boolean isUserUploadEmpty( UserUploadVO user )
    {
        if ( ( user.getSourceUserId() == null || user.getSourceUserId().isEmpty() )
            && ( user.getFirstName() == null || user.getFirstName().isEmpty() )
            && ( user.getLastName() == null || user.getLastName().isEmpty() )
            && ( user.getTitle() == null || user.getTitle().isEmpty() )
            && ( user.getSourceBranchId() == null || user.getSourceBranchId().isEmpty() )
            && ( user.getSourceRegionId() == null || user.getSourceRegionId().isEmpty() )
            && ( user.getAssignedBranchesAdmin() == null || user.getAssignedBranchesAdmin().isEmpty() )
            && ( user.getAssignedRegionsAdmin() == null || user.getAssignedRegionsAdmin().isEmpty() )
            && ( user.getEmailId() == null || user.getEmailId().isEmpty() )
            && ( user.getUserPhotoUrl() == null || user.getUserPhotoUrl().isEmpty() )
            && ( user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() )
            && ( user.getWebsiteUrl() == null || user.getWebsiteUrl().isEmpty() )
            && ( user.getLicense() == null || user.getLicense().isEmpty() )
            && ( user.getLegalDisclaimer() == null || user.getLegalDisclaimer().isEmpty() )
            && ( user.getAboutMeDescription() == null || user.getAboutMeDescription().isEmpty() ) ) {
            return true;
        }
        return false;
    }


    boolean isBranchUploadEmpty( BranchUploadVO branch )
    {
        if ( ( branch.getSourceBranchId() == null || branch.getSourceBranchId().isEmpty() )
            && ( branch.getBranchName() == null || branch.getBranchName().isEmpty() )
            && ( branch.getSourceRegionId() == null || branch.getSourceRegionId().isEmpty() )
            && ( branch.getBranchAddress1() == null || branch.getBranchAddress1().isEmpty() )
            && ( branch.getBranchAddress2() == null || branch.getBranchAddress2().isEmpty() )
            && ( branch.getBranchCity() == null || branch.getBranchCity().isEmpty() )
            && ( branch.getBranchState() == null || branch.getBranchState().isEmpty() )
            && ( branch.getBranchZipcode() == null || branch.getBranchZipcode().isEmpty() ) ) {
            return true;
        }
        return false;
    }


    boolean isRegionUploadEmpty( RegionUploadVO region )
    {
        if ( ( region.getSourceRegionId() == null || region.getSourceRegionId().isEmpty() )
            && ( region.getRegionName() == null || region.getRegionName().isEmpty() )
            && ( region.getRegionAddress1() == null || region.getRegionAddress1().isEmpty() )
            && ( region.getRegionAddress2() == null || region.getRegionAddress2().isEmpty() )
            && ( region.getRegionCity() == null || region.getRegionCity().isEmpty() )
            && ( region.getRegionState() == null || region.getRegionState().isEmpty() )
            && ( region.getRegionZipcode() == null || region.getRegionZipcode().isEmpty() ) ) {
            return true;
        }
        return false;
    }


    /**
     * Method to check for duplicate sourceRegionIds
     * @param sourceRegionIdErrors
     * @param uploadedRegion
     */
    void checkForDuplicateSourceRegionIds( Map<String, String> sourceRegionIdErrors, RegionUploadVO uploadedRegion )
    {
        //check for duplicate source ids
        if ( sourceRegionIdErrors.containsKey( uploadedRegion.getSourceRegionId() ) ) {
            if ( sourceRegionIdErrors.get( uploadedRegion.getSourceRegionId() ) == null
                || sourceRegionIdErrors.get( uploadedRegion.getSourceRegionId() ).isEmpty() ) {
                sourceRegionIdErrors.put( uploadedRegion.getSourceRegionId(), "The Region ID : "
                    + uploadedRegion.getSourceRegionId() + " is duplicated at row : " + uploadedRegion.getRowNum() );
            } else {
                sourceRegionIdErrors.put( uploadedRegion.getSourceRegionId(),
                    sourceRegionIdErrors.get( uploadedRegion.getSourceRegionId() ) + ", " + uploadedRegion.getRowNum() );
            }
        } else {
            sourceRegionIdErrors.put( uploadedRegion.getSourceRegionId(), null );
        }
    }


    /**
     * Method to check for duplicate sourceBranchIds
     * @param sourceBranchIdErrors
     * @param uploadedBranch
     */
    void checkForDuplicateSourceBranchIds( Map<String, String> sourceBranchIdErrors, BranchUploadVO uploadedBranch )
    {
        if ( sourceBranchIdErrors.containsKey( uploadedBranch.getSourceBranchId() ) ) {
            if ( sourceBranchIdErrors.get( uploadedBranch.getSourceBranchId() ) == null
                || sourceBranchIdErrors.get( uploadedBranch.getSourceBranchId() ).isEmpty() ) {
                sourceBranchIdErrors.put( uploadedBranch.getSourceBranchId(), "The Office ID : "
                    + uploadedBranch.getSourceBranchId() + " is duplicated at row : " + uploadedBranch.getRowNum() );
            } else {
                sourceBranchIdErrors.put( uploadedBranch.getSourceBranchId(),
                    sourceBranchIdErrors.get( uploadedBranch.getSourceBranchId() ) + ", " + uploadedBranch.getRowNum() );
            }
        } else {
            sourceBranchIdErrors.put( uploadedBranch.getSourceBranchId(), null );
        }
    }


    /**
     * Method to check for duplicate sourceUserIds
     * @param sourceUserIdErrors
     * @param uploadedUser
     */
    void checkForDuplicateSourceUserIds( Map<String, String> sourceUserIdErrors, UserUploadVO uploadedUser )
    {
        if ( sourceUserIdErrors.containsKey( uploadedUser.getSourceUserId() ) ) {
            if ( sourceUserIdErrors.get( uploadedUser.getSourceUserId() ) == null
                || sourceUserIdErrors.get( uploadedUser.getSourceUserId() ).isEmpty() ) {
                sourceUserIdErrors.put( uploadedUser.getSourceUserId(),
                    "The User ID : " + uploadedUser.getSourceUserId() + " is duplicated at row : " + uploadedUser.getRowNum() );
            } else {
                sourceUserIdErrors.put( uploadedUser.getSourceUserId(),
                    sourceUserIdErrors.get( uploadedUser.getSourceUserId() ) + ", " + uploadedUser.getRowNum() );
            }
        } else {
            sourceUserIdErrors.put( uploadedUser.getSourceUserId(), null );
        }
    }
}
