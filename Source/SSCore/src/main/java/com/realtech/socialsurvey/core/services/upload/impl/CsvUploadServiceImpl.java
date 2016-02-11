package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.commons.Utils;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.UserDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.Licenses;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserEmailMapping;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.entities.WebAddressSettings;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.BranchAdditionException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.RegionAdditionException;
import com.realtech.socialsurvey.core.exception.UserAdditionException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserAssignmentException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


@Component
public class CsvUploadServiceImpl implements CsvUploadService
{

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

    private static final String COUNTRY = "United States";
    private static final String COUNTRY_CODE = "US";

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserDao userDao;

    @Resource
    @Qualifier ( "branch")
    private BranchDao branchDao;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Autowired
    private GenericDao<Region, Long> regionDao;

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private Utils utils;

    @Autowired
    private GenericDao<UserEmailMapping, Long> userEmailMappingDao;


    @Value ( "${FILEUPLOAD_DIRECTORY_LOCATION}")
    private String fileDirectory;

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;

    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String adminEmailId;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String adminName;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;
    
    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    private static Logger LOG = LoggerFactory.getLogger( CsvUploadServiceImpl.class );


    @Override
    public UploadValidation validateUserUploadFile( Company company, String fileName ) throws InvalidInputException
    {
        if ( fileName == null || fileName.isEmpty() ) {
            LOG.error( "Invalid upload details" );
            throw new InvalidInputException( "File name is not provided: " + fileName );
        }
        if ( company == null ) {
            LOG.error( "Invalid company details" );
            throw new InvalidInputException( "Invalid company details" );
        }
        LOG.info( "Validating the file for " + company.getCompany() + " and file " + fileName );
        UploadValidation validationObject = new UploadValidation();
        // get current hierarchy upload
        validationObject.setUpload( getHierarchyStructure( company ) );
        // read the file
        InputStream fileStream = null;
        try {
            //fileStream = new FileInputStream( fileName );
            fileStream = new URL( fileName ).openStream();
            XSSFWorkbook workBook = new XSSFWorkbook( fileStream );
            parseRegions( workBook, validationObject );
            parseBranches( workBook, validationObject );
            parseUsers( workBook, validationObject );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( fileStream != null ) {
                try {
                    fileStream.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        return validationObject;
    }


    /**
     * Validates regions to be uploaded.
     * @param workBook
     * @param validationObject
     */
    @Override
    public void parseRegions( XSSFWorkbook workBook, UploadValidation validationObject )
    {
        // Parse the list of regions from the sheet. Parse each row. Check for validation errors. If validation is successful, check if region is modified or added. If modified then add to the modified count or to the addition count. 
        // Then map and check if there are any regions that were deleted
        // Possible errors in regions
        // 1. Source region id is not present
        // 2. Region name is not present
        // 3. Region cannot be deleted if branches and users are associated.
        LOG.debug( "Parsing regions from sheet" );
        XSSFSheet regionSheet = workBook.getSheet( REGION_SHEET );
        Iterator<Row> rows = regionSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        RegionUploadVO uploadedRegion = null;
        List<RegionUploadVO> uploadedRegions = new ArrayList<>();
        while ( rows.hasNext() ) {
            row = (XSSFRow) rows.next();
            // skip the first header row.
            if ( row.getRowNum() < 1 ) {
                continue;
            }
            cells = row.cellIterator();
            uploadedRegion = new RegionUploadVO();
            int cellIndex = 0;
            try {
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    cellIndex = cell.getColumnIndex();
                    if ( cellIndex == REGION_ID_INDEX ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            try {
                                uploadedRegion.setSourceRegionId( String.valueOf( cell.getNumericCellValue() ) );
                            } catch ( NumberFormatException nfe ) {
                                LOG.error( "Source Id at row: " + row.getRowNum() + " is not provided." );
                                throw new InvalidInputException( "Source Id at row: " + row.getRowNum() + " is not provided." );
                            }
                        } else {
                            uploadedRegion.setSourceRegionId( cell.getStringCellValue() );
                        }
                        if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
                            LOG.error( "Source Id at row: " + row.getRowNum() + " is not provided." );
                            throw new InvalidInputException( "Source Id at row: " + row.getRowNum() + " is not provided." );
                        }
                    } else if ( cellIndex == REGION_NAME_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedRegion.setRegionName( cell.getStringCellValue().trim() );
                        } else {
                            LOG.error( "Region name at row: " + row.getRowNum() + " is not provided." );
                            throw new InvalidInputException( "Region name at row: " + row.getRowNum() + " is not provided." );
                        }
                        if ( uploadedRegion.getRegionName() == null || uploadedRegion.getRegionName().isEmpty() ) {
                            LOG.error( "Region name at row: " + row.getRowNum() + " is not provided." );
                            throw new InvalidInputException( "Region name at row: " + row.getRowNum() + " is not provided." );
                        }
                    } else if ( cellIndex == REGION_ADDRESS1_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedRegion.setRegionAddress1( cell.getStringCellValue() );
                            uploadedRegion.setAddressSet( true );
                        }
                    } else if ( cellIndex == REGION_ADDRESS2_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedRegion.setRegionAddress2( cell.getStringCellValue() );
                            uploadedRegion.setAddressSet( true );
                        }
                    } else if ( cellIndex == REGION_CITY_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedRegion.setRegionCity( cell.getStringCellValue() );
                        }
                    } else if ( cellIndex == REGION_STATE_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedRegion.setRegionState( cell.getStringCellValue() );
                        }
                    } else if ( cellIndex == REGION_ZIP_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                                uploadedRegion.setRegionZipcode( cell.getStringCellValue() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                                uploadedRegion.setRegionZipcode( String.valueOf( (int) cell.getNumericCellValue() ) );
                            }
                        }
                    }
                }
                // instances of above check for errors being bypassed have been found. Checking for more issues
                if ( validateUploadedRegion( uploadedRegion, row.getRowNum() ) ) {
                    // check if region is added or modified
                    if ( isNewRegion( uploadedRegion, validationObject.getUpload() ) ) {
                        validationObject.setNumberOfRegionsAdded( validationObject.getNumberOfRegionsAdded() + 1 );
                        uploadedRegion.setRegionAdded( true );
                        validationObject.getUpload().getRegions().add( uploadedRegion );
                    } else {
                        // region already exists
                        // Check all the fields 
                        int index = validationObject.getUpload().getRegions().indexOf( uploadedRegion );
                        if(index > -1){
                            RegionUploadVO currentObject = validationObject.getUpload().getRegions().get( index );
                            if(checkForModificationAndSuperImpose(currentObject, uploadedRegion)){
                                currentObject.setRegionModified( true );
                            }
                        }
                    }
                    // add to uploaded regions list.
                    uploadedRegions.add( uploadedRegion );
                }
            } catch ( InvalidInputException iie ) {
                // add to region errors
                if ( validationObject.getRegionValidationErrors() == null ) {
                    validationObject.setRegionValidationErrors( new ArrayList<String>() );
                }
                uploadedRegion.setErrorRecord( true );
                validationObject.getRegionValidationErrors().add( iie.getMessage() );
            }
        }
        markDeletedRegions( uploadedRegions, validationObject.getUpload() );
    }

    // checks the fields that are modified. return true of the value is modified and modify the current region
    private boolean checkForModificationAndSuperImpose(RegionUploadVO currentRegion, RegionUploadVO uploadedRegion){
        boolean isModified = false;
        if(!currentRegion.getRegionName().equals( uploadedRegion.getRegionName() )){
            isModified = true;
            currentRegion.setRegionName( uploadedRegion.getRegionName() );
            currentRegion.setRegionNameModified( true );
        }
        if(currentRegion.getRegionAddress1() == null){
            currentRegion.setRegionAddress1("");
        }
        if(uploadedRegion.getRegionAddress1() == null){
            uploadedRegion.setRegionAddress1("");
        }
        if(!currentRegion.getRegionAddress1().equals( uploadedRegion.getRegionAddress1() )){
            if(uploadedRegion.getRegionAddress1().equals( "" )){
                isModified = true;
                currentRegion.setRegionAddress1( null );
            }else{
                currentRegion.setRegionAddress1( uploadedRegion.getRegionAddress1() );
            }
            currentRegion.setRegionAddress1Modified( true );
        }
        
        if(currentRegion.getRegionAddress2() == null){
            currentRegion.setRegionAddress2("");
        }
        if(uploadedRegion.getRegionAddress2() == null){
            uploadedRegion.setRegionAddress2("");
        }
        if(!currentRegion.getRegionAddress2().equals( uploadedRegion.getRegionAddress2() )){
            if(uploadedRegion.getRegionAddress2().equals( "" )){
                isModified = true;
                currentRegion.setRegionAddress2( null );
            }else{
                currentRegion.setRegionAddress2( uploadedRegion.getRegionAddress2() );
            }
            currentRegion.setRegionAddress2Modified( true );
        }
        
        return isModified;
    }
    
    private boolean validateUploadedRegion( RegionUploadVO uploadedRegion, int rowNumber ) throws InvalidInputException
    {
        LOG.debug( "Validating uploaded region" );
        if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
            LOG.error( "Source Id at row: " + rowNumber + " is not provided." );
            throw new InvalidInputException( "Source Id at row: " + rowNumber + " is not provided." );
        }
        if ( uploadedRegion.getRegionName() == null || uploadedRegion.getRegionName().isEmpty() ) {
            LOG.error( "Region name at row: " + rowNumber + " is not provided." );
            throw new InvalidInputException( "Region name at row: " + rowNumber + " is not provided." );
        }
        return true;
    }
    
    private boolean validateUploadedBranch( BranchUploadVO uploadedBranch, int rowNumber ) throws InvalidInputException{
        LOG.debug( "Validating uploaded branch" );
        if(uploadedBranch.getSourceBranchId() == null || uploadedBranch.getSourceBranchId().isEmpty()){
            throw new InvalidInputException("Source Id at row: " + rowNumber + " is not provided.");
        }
        if(uploadedBranch.getBranchName() == null || uploadedBranch.getBranchName().isEmpty()){
            throw new InvalidInputException("Office name at row: " + rowNumber + " is not provided.");
        }
        if(!uploadedBranch.isAddressSet()){
            throw new InvalidInputException("Office address at row: " + rowNumber + " is not provided.");
        }
        return true;
    }

    private boolean validateUploadedBranchForWarnings( BranchUploadVO uploadedBranch, int rowNumber, HierarchyUpload upload ) throws InvalidInputException{
        LOG.debug( "Validating uploaded branch for warning" );
        if((upload.getRegions() != null && !upload.getRegions().isEmpty()) && (uploadedBranch.getSourceRegionId() == null || uploadedBranch.getSourceRegionId().isEmpty())){
            throw new InvalidInputException("Office at " + rowNumber + " is not linked to any region.");
        }
        return true;
    }

    private boolean isNewRegion( RegionUploadVO uploadedRegion, HierarchyUpload upload )
    {
        // If the source is present in the mapping, then its a modified record
        if(upload.getRegionSourceMapping() != null && upload.getRegionSourceMapping().size() > 0){
           if(upload.getRegionSourceMapping().containsKey( uploadedRegion.getSourceRegionId() )){
               // record already present.
               return false;
           }else{
               // new record
               return true;
           }
        }else{
            return true;
        }
    }
    
    private boolean isNewBranch( BranchUploadVO uploadedBranch, HierarchyUpload upload )
    {
        // TODO: check for new branch addition
        return true;
    }


    private void markDeletedRegions( List<RegionUploadVO> uploadedRegions, HierarchyUpload upload )
    {
        // TODO: iterate and mark the deleted regions
    }
    
    private void markDeletedBranches( List<BranchUploadVO> uploadedBranches, HierarchyUpload upload )
    {
        // TODO: iterate and mark the deleted branches
    }


    public void parseBranches( XSSFWorkbook workBook, UploadValidation validationObject )
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
        List<BranchUploadVO> uploadedBranches = new ArrayList<>();
        XSSFSheet branchSheet = workBook.getSheet( BRANCH_SHEET );
        Iterator<Row> rows = branchSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        BranchUploadVO uploadedBranch = null;
        while ( rows.hasNext() ) {
            row = (XSSFRow) rows.next();
            // skip the first header row.
            if ( row.getRowNum() < 1 ) {
                continue;
            }
            cells = row.cellIterator();
            uploadedBranch = new BranchUploadVO();
            int cellIndex = 0;
            try {
                while ( cells.hasNext() ) {
                    cell = (XSSFCell) cells.next();
                    cellIndex = cell.getColumnIndex();
                    if ( cellIndex == BRANCH_ID_INDEX ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            try {
                                uploadedBranch.setSourceBranchId( String.valueOf( cell.getNumericCellValue() ) );
                            } catch ( NumberFormatException nfe ) {
                                LOG.error( "Source Id at row: " + row.getRowNum() + " is not provided." );
                                throw new InvalidInputException( "Source Id at row: " + row.getRowNum() + " is not provided." );
                            }
                        } else {
                            uploadedBranch.setSourceBranchId( cell.getStringCellValue() );
                        }
                    } else if ( cellIndex == BRANCH_NAME_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedBranch.setBranchName( cell.getStringCellValue().trim() );
                        } else {
                            LOG.error( "Office name at row: " + row.getRowNum() + " is not provided." );
                            throw new InvalidInputException( "Office name at row: " + row.getRowNum() + " is not provided." );
                        }
                    } else if ( cellIndex == BRANCH_REGION_ID_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            // map it with the region
                            String sourceRegionId = null;
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                                sourceRegionId = String.valueOf( cell.getNumericCellValue() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                                sourceRegionId = cell.getStringCellValue();
                            }
                            // check if source region id is present in the hierarchy
                            if(checkSourceRegionId(sourceRegionId, validationObject.getUpload())){
                                uploadedBranch.setSourceRegionId( sourceRegionId );
                            }else{
                                LOG.error( "The region id in row: "+row.getRowNum()+" is not present." );
                                throw new InvalidInputException("The region id in row: "+row.getRowNum()+" is not present.");
                            }
                        }
                    } else if ( cellIndex == BRANCH_ADDRESS1_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedBranch.setBranchAddress1( cell.getStringCellValue() );
                            uploadedBranch.setAddressSet( true );
                        }
                    } else if ( cellIndex == BRANCH_ADDRESS2_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedBranch.setBranchAddress2( cell.getStringCellValue() );
                            uploadedBranch.setAddressSet( true );
                        }
                    } else if ( cellIndex == BRANCH_CITY_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedBranch.setBranchCity( cell.getStringCellValue() );
                        } else {
                            LOG.error( "Office city at row: " + row.getRowNum() + " is not provided." );
                            throw new InvalidInputException( "Office city at row: " + row.getRowNum() + " is not provided." );
                        }
                    } else if ( cellIndex == BRANCH_STATE_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            uploadedBranch.setBranchState( cell.getStringCellValue() );
                        }
                    } else if ( cellIndex == BRANCH_ZIP_INDEX ) {
                        if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                            if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                                uploadedBranch.setBranchZipcode( cell.getStringCellValue() );
                            } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                                uploadedBranch.setBranchZipcode( String.valueOf( (int) cell.getNumericCellValue() ) );
                            }
                        }
                    }
                }
                // instances of above check for errors being bypassed have been found. Checking for more issues
                if ( validateUploadedBranch( uploadedBranch, row.getRowNum() ) ) {
                    // check if branch is added or modified
                    if ( isNewBranch( uploadedBranch, validationObject.getUpload() ) ) {
                        validationObject.setNumberOfBranchesAdded( validationObject.getNumberOfBranchesAdded() + 1 );
                        uploadedBranch.setBranchAdded( true );
                        validationObject.getUpload().getBranches().add( uploadedBranch);
                    } else {
                        // branch already exists
                        // TODO: check if branch is modified. If modified then set the modified details 
                    }
                    // add to uploaded regions list.
                    uploadedBranches.add( uploadedBranch );
                }
                // validate for warnings
                try{
                    validateUploadedBranchForWarnings( uploadedBranch, row.getRowNum(), validationObject.getUpload() );
                }catch ( InvalidInputException iie ) {
                    if ( validationObject.getBranchValidationWarnings() == null ) {
                        validationObject.setBranchValidationWarnings( new ArrayList<String>() );
                    }
                    validationObject.getBranchValidationWarnings().add( iie.getMessage() );
                }
            } catch ( InvalidInputException iie ) {
                // add to region errors
                if ( validationObject.getBranchValidationErrors() == null ) {
                    validationObject.setBranchValidationErrors( new ArrayList<String>() );
                }
                uploadedBranch.setErrorRecord( true );
                validationObject.getBranchValidationErrors().add( iie.getMessage() );
            }
        }
        markDeletedBranches( uploadedBranches, validationObject.getUpload() );
    }

    private boolean checkSourceRegionId(String sourceRegionId, HierarchyUpload upload){
        LOG.debug( "Checking if source region id is present" );
        RegionUploadVO regionUploadVO = new RegionUploadVO();
        regionUploadVO.setSourceRegionId( sourceRegionId );
        if(upload.getRegions() != null && !upload.getRegions().isEmpty()){
            return upload.getRegions().contains( regionUploadVO );
        }
        return false;
    }

    public void parseUsers( XSSFWorkbook workBook, UploadValidation validationObject )
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
        // 1. There are no branch, region, branch admin, region admin assignments. The user will be added under the company as an individual.
    }


    public HierarchyUpload getHierarchyStructure( Company company )
    {
        // TODO: Query for structure
        HierarchyUpload upload = new HierarchyUpload();
        upload.setRegions( new ArrayList<RegionUploadVO>() );
        upload.setBranches( new ArrayList<BranchUploadVO>() );
        upload.setUsers( new ArrayList<UserUploadVO>() );
        return upload;
    }
    
    
    /**
     * Method to update company hierarchy structure in mongo
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public HierarchyUpload updateHierarchyStructure( Company company ) throws InvalidInputException
    {
        LOG.info( "Method updateHierarchyStructure started for company : " + company.getCompany() );
        /* 
         * 1. fetch from mongo (oldHierarchyStructure)
         * 2. If empty go to step 5
         * 3. create current hierarchy upload object that reflects the current hierarchy structure.
         * 4. compare and update oldHierarchyStructure and currentHierarchyStructure)
         */
        HierarchyUpload oldHierarchyUpload = hierarchyUploadDao.getHierarchyUploadByCompany( company.getCompanyId() );
        // Generate new hierarchyupload
        HierarchyUpload currentHierarchyUpload = generateCurrentHierarchyStructure( company );
        if ( oldHierarchyUpload == null ) {
            LOG.warn( "No pre-existing hierarchy structure found for the company" );
            //generate source Ids and save in map
            generateSourceIdsForNewHierarchyUpload( currentHierarchyUpload );
            hierarchyUploadDao.saveHierarchyUploadObject( currentHierarchyUpload );
        } else {
            //TODO : Compare and update hierarchy structures
        }

        LOG.info( "Method updateHierarchyStructure finished for company : " + company.getCompany() );
        return null;
    }

    
    /**
     * Method to generate source ids for new hierarchy uploads
     * @param hierarchyUpload
     */
    public void generateSourceIdsForNewHierarchyUpload( HierarchyUpload hierarchyUpload )
    {
        //Set source ids for users, regions and branches
        Map<String, Long> regionSourceMap = hierarchyUpload.getRegionSourceMapping();
        Map<Long, String> regionMap = new HashMap<Long, String>();
        
        List<RegionUploadVO> regionUploadVOs = hierarchyUpload.getRegions();
        for ( RegionUploadVO regionUploadVO : regionUploadVOs ) {
            String sourceId = generateSourceId( CommonConstants.REGION_COLUMN );
            regionUploadVO.setSourceRegionId( sourceId );
            regionSourceMap.put( sourceId, regionUploadVO.getRegionId() );
            regionMap.put( regionUploadVO.getRegionId(), sourceId );

        }
        hierarchyUpload.setRegionSourceMapping( regionSourceMap );
        
        Map<String, Long> branchSourceMap = hierarchyUpload.getBranchSourceMapping();
        Map<Long, String> branchMap = new HashMap<Long, String>();
        
        List<BranchUploadVO> branchUploadVOs = hierarchyUpload.getBranches();
        for ( BranchUploadVO branchUploadVO : branchUploadVOs ) {
            String sourceId = generateSourceId( CommonConstants.BRANCH_NAME_COLUMN );
            branchUploadVO.setSourceBranchId( sourceId );
            branchUploadVO.setSourceRegionId( regionMap.get( branchUploadVO.getRegionId() ) );
            branchSourceMap.put( sourceId, branchUploadVO.getBranchId() );
            branchMap.put( branchUploadVO.getBranchId(), sourceId );
        }
        hierarchyUpload.setBranchSourceMapping( branchSourceMap );
        
        Map<String, Long> userSourceMap = hierarchyUpload.getUserSourceMapping();
        
        List<UserUploadVO> userUploadVOs = hierarchyUpload.getUsers();
        for ( UserUploadVO userUploadVO : userUploadVOs ) {
            String sourceId = generateSourceId( CommonConstants.USER_COLUMN );
            userUploadVO.setSourceUserId( sourceId );
            userUploadVO.setSourceBranchId( branchMap.get( userUploadVO.getBranchId() ) );
            userUploadVO.setSourceRegionId( regionMap.get( userUploadVO.getRegionId() ) );
            userSourceMap.put( sourceId, userUploadVO.getUserId() );
        }
        
        //Set source ids in maps
        hierarchyUpload.setRegionSourceMapping( regionSourceMap );
        hierarchyUpload.setBranchSourceMapping( branchSourceMap );
        hierarchyUpload.setUserSourceMapping( userSourceMap );
    }

    /**
     * Method to aggregate hierarchy structure
     * @param oldHierarchyUpload
     * @param currentHierarchyUpload
     * @return
     * @throws InvalidInputException 
     */
    public HierarchyUpload aggregateHierarchyStructure( HierarchyUpload oldHierarchyUpload,
        HierarchyUpload currentHierarchyUpload ) throws InvalidInputException
    {
        LOG.info( "Method to aggregate hierarchy structure started" );
        if ( oldHierarchyUpload == null ) {
            throw new InvalidInputException( "OldHierarchyUpload object is empty" );
        }
        if ( currentHierarchyUpload == null ) {
            throw new InvalidInputException( "CurrentHierarchyUpload object is empty" );
        }

        HierarchyUpload newHierarchyUpload = oldHierarchyUpload;

        //Compare and aggregate regions
        aggregateRegionsStructure( oldHierarchyUpload.getRegions(), currentHierarchyUpload.getRegions(), newHierarchyUpload );

        //Compare and aggregate branches
        aggregateBranchesStructure( oldHierarchyUpload.getBranches(), currentHierarchyUpload.getBranches(), newHierarchyUpload );

        //Compare and aggregate users
        aggregateUsersStructure( oldHierarchyUpload.getUsers(), currentHierarchyUpload.getUsers(), newHierarchyUpload );

        LOG.info( "Method to aggregate hierarchy structure finished" );
        return newHierarchyUpload;
    }


    public void aggregateUsersStructure( List<UserUploadVO> oldUsers, List<UserUploadVO> currentUsers,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate users structure started" );
        List<UserUploadVO> newUsers = new ArrayList<UserUploadVO>();

        Map<Long, UserUploadVO> oldUsersMap = new HashMap<Long, UserUploadVO>();
        //Get map from UserUploadVO
        for ( UserUploadVO userUploadVO : oldUsers ) {
            oldUsersMap.put( userUploadVO.getUserId(), userUploadVO );
        }

        /*
         * Things to check
         * 1. user addition
         * 2. user deletion
         */
        
        Map<String, Long> regionMapping = newHierarchyUpload.getRegionSourceMapping();
        
        Map<Long, String> mappedRegion = new HashMap<Long, String>();
        for ( String key : regionMapping.keySet() ) {
            mappedRegion.put( regionMapping.get( key ), key );
        }
        
        Map<String, Long> branchMapping = newHierarchyUpload.getBranchSourceMapping();
        
        Map<Long, String> mappedBranch = new HashMap<Long, String>();
        for ( String key : branchMapping.keySet() ) {
            mappedBranch.put( branchMapping.get( key ), key );
        }
        
        Map<String, Long> userMapping = newHierarchyUpload.getUserSourceMapping();
        
        //Iterate through new list
        for ( UserUploadVO currentUser : currentUsers ) {
            UserUploadVO oldUser = oldUsersMap.get( currentUser.getUserId() );
            //If oldUser does not exist, then the currentUser is a new user
            if ( oldUser == null ) {
                //Generate sourceId for it and set the flag as true
                currentUser.setSourceUserId( generateSourceId( CommonConstants.USER_COLUMN ) );
                currentUser.setSourceUserIdGenerated( true );
                newUsers.add( currentUser );
                //Add new users' sourceIds in the hierarchyupload
                userMapping.put( currentUser.getSourceUserId(), currentUser.getUserId() );
            } else {
                //Superimpose current on old and store in new list
                UserUploadVO amalgamatedUser = oldUser;
                //set sourceRegionId
                amalgamatedUser.setSourceRegionId( mappedRegion.get( amalgamatedUser.getRegionId() ) );
                
                //set sourceBranchId
                amalgamatedUser.setSourceBranchId( mappedRegion.get( amalgamatedUser.getBranchId() ) );
                
                amalgamatedUser.setFirstName( currentUser.getFirstName() );
                amalgamatedUser.setLastName( currentUser.getLastName() );
                amalgamatedUser.setTitle( currentUser.getTitle() );
                amalgamatedUser.setBranchId( currentUser.getBranchId() );
                amalgamatedUser.setRegionId( currentUser.getRegionId() );
                amalgamatedUser.setAgent( currentUser.isAgent() );
                amalgamatedUser.setEmailId( currentUser.getEmailId() );
                amalgamatedUser.setBelongsToCompany( currentUser.isBelongsToCompany() );
                amalgamatedUser.setBranchAdmin( currentUser.isBranchAdmin() );
                amalgamatedUser.setPhoneNumber( currentUser.getPhoneNumber() );
                amalgamatedUser.setWebsiteUrl( currentUser.getWebsiteUrl() );
                amalgamatedUser.setLicense( currentUser.getLicense() );
                amalgamatedUser.setLegalDisclaimer( currentUser.getLegalDisclaimer() );
                amalgamatedUser.setAboutMeDescription( currentUser.getAboutMeDescription() );
                amalgamatedUser.setUserPhotoUrl( currentUser.getUserPhotoUrl() );

                newUsers.add( amalgamatedUser );

                //Delete object entry from oldUsers
                oldUsers.remove( oldUser );
            }
        }

        if ( !( oldUsers.isEmpty() ) ) {
            //The remaining users are the ones that have been deleted
            //don't add these to the new list
            //Remove deleted users' sourceIds in the hierarchyupload map
            for ( UserUploadVO userUploadVO : oldUsers ) {
                userMapping.remove( userUploadVO.getSourceUserId() );
            }
            LOG.warn( "Some users have been deleted recently" );
        }
        
        newHierarchyUpload.setUserSourceMapping( userMapping );

        newHierarchyUpload.setUsers( newUsers );
        LOG.info( "Method to aggregate users structure finished" );
    }


    /**
     * Method to aggregate branches structure
     * @param oldBranches
     * @param currentBranches
     * @return
     */
    public void aggregateBranchesStructure( List<BranchUploadVO> oldBranches, List<BranchUploadVO> currentBranches,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate branches structure started" );
        List<BranchUploadVO> newBranches = new ArrayList<BranchUploadVO>();

        Map<Long, BranchUploadVO> oldBranchesMap = new HashMap<Long, BranchUploadVO>();
        //Get map from BranchUploadVO
        for ( BranchUploadVO branchUploadVO : oldBranches ) {
            oldBranchesMap.put( branchUploadVO.getRegionId(), branchUploadVO );
        }

        /*
         * Things to check
         * 1. branch addition
         * 2. branch deletion
         */

        //Iterate through new list
        for ( BranchUploadVO currentBranch : currentBranches ) {
            BranchUploadVO oldBranch = oldBranchesMap.get( currentBranch.getBranchId() );
            //If oldBranch does not exist, then the currentBranch is a newly added branch
            if ( oldBranch == null ) {
                //Generate sourceId for it and set the flag as true
                currentBranch.setSourceBranchId( generateSourceId( CommonConstants.BRANCH_NAME_COLUMN ) );
                currentBranch.setSourceBrancIdGenerated( true );
                newBranches.add( currentBranch );
            } else {
                //Superimpose current on old and store in new list
                BranchUploadVO amalgamatedBranch = oldBranch;
                amalgamatedBranch.setRegionId( currentBranch.getRegionId() );
                amalgamatedBranch.setBranchName( currentBranch.getBranchName() );
                amalgamatedBranch.setBranchAddress1( currentBranch.getBranchAddress1() );
                amalgamatedBranch.setBranchAddress2( currentBranch.getBranchAddress2() );
                amalgamatedBranch.setBranchCountry( currentBranch.getBranchCountry() );
                amalgamatedBranch.setBranchCountryCode( currentBranch.getBranchCountryCode() );
                amalgamatedBranch.setBranchCity( currentBranch.getBranchCity() );
                amalgamatedBranch.setBranchZipcode( currentBranch.getBranchZipcode() );
                //TODO: set sourceRegionId
                newBranches.add( amalgamatedBranch );

                //Delete object entry from oldRegions
                oldBranches.remove( oldBranch );
            }
        }

        if ( !( oldBranches.isEmpty() ) ) {
            //The remaining branches are the ones that have been deleted
            //don't add these to the new list
            //TODO : Remove deleted branches' sourceIds in the hierarchyupload map
            LOG.warn( "Some branches have been deleted recently" );
        }

        //TODO : Add new branches' sourceIds in the hierarchyupload

        newHierarchyUpload.setBranches( newBranches );
        LOG.info( "Method to aggregate branches structure finished" );
    }


    /**
     * Method to aggregate region structure
     * @param oldRegions
     * @param currentRegions
     * @return
     */
    public void aggregateRegionsStructure( List<RegionUploadVO> oldRegions, List<RegionUploadVO> currentRegions,
        HierarchyUpload newHierarchyUpload )
    {
        LOG.info( "Method to aggregate regions structure started" );
        List<RegionUploadVO> newRegions = new ArrayList<RegionUploadVO>();

        Map<Long, RegionUploadVO> oldRegionsMap = new HashMap<Long, RegionUploadVO>();
        //Get map from RegionUploadVO
        for ( RegionUploadVO regionUploadVO : oldRegions ) {
            oldRegionsMap.put( regionUploadVO.getRegionId(), regionUploadVO );
        }

        /*
         * Things to check
         * 1. region addition
         * 2. region deletion
         */

        //Iterate through new list
        for ( RegionUploadVO currentRegion : currentRegions ) {
            RegionUploadVO oldRegion = oldRegionsMap.get( currentRegion.getRegionId() );
            //If oldRegion does not exist, then the currentRegion is a newly added region
            if ( oldRegion == null ) {
                //Generate sourceId for it and set the flag as true
                currentRegion.setSourceRegionId( generateSourceId( CommonConstants.REGION_COLUMN ) );
                currentRegion.setSourceRegionIdGenerated( true );
                newRegions.add( currentRegion );
            } else {
                //Superimpose current on old and store in new list
                RegionUploadVO amalgamatedRegion = oldRegion;
                amalgamatedRegion.setRegionName( currentRegion.getRegionName() );
                amalgamatedRegion.setRegionAddress1( currentRegion.getRegionAddress1() );
                amalgamatedRegion.setRegionAddress2( currentRegion.getRegionAddress2() );
                amalgamatedRegion.setRegionCountry( currentRegion.getRegionCountry() );
                amalgamatedRegion.setRegionCountryCode( currentRegion.getRegionCountryCode() );
                amalgamatedRegion.setRegionCity( currentRegion.getRegionCity() );
                amalgamatedRegion.setRegionState( currentRegion.getRegionState() );
                amalgamatedRegion.setRegionZipcode( currentRegion.getRegionZipcode() );
                newRegions.add( amalgamatedRegion );

                //Delete object entry from oldRegions
                oldRegions.remove( oldRegion );
            }
        }

        if ( !( oldRegions.isEmpty() ) ) {
            //The remaining regions are the ones that have been deleted
            //don't add these to the new list
            //TODO : Remove deleted regions' sourceIds in the hierarchyupload map
            LOG.warn( "Some regions have been deleted recently" );
        }
        //TODO: Add new regions' sourceIds in the hierarchyupload map

        newHierarchyUpload.setRegions( newRegions );
        LOG.info( "Method to aggregate regions structure finished" );
    }


    public String generateSourceId( String entityType )
    {
        return entityType + String.valueOf( System.currentTimeMillis() );
    }


    /**
     * Method to generate current hierarchy structure for a company
     * @param company
     * @return
     * @throws InvalidInputException 
     */
    public HierarchyUpload generateCurrentHierarchyStructure( Company company ) throws InvalidInputException
    {
        LOG.info( "Method to generate current hierarchy structure for company : " + company.getCompany() + " started" );

        HierarchyUpload hierarchyUpload = new HierarchyUpload();

        //Set company Id
        hierarchyUpload.setCompanyId( company.getCompanyId() );

        //Set RegionVOs
        List<RegionUploadVO> regions = generateRegionUploadVOsForCompany( company );
        hierarchyUpload.setRegions( regions );

        //Set BranchVOs
        List<BranchUploadVO> branches = generateBranchUploadVOsForCompany( company );
        hierarchyUpload.setBranches( branches );

        //Set UserVOs
        List<UserUploadVO> users = generateUserUploadVOsForCompany( company );
        hierarchyUpload.setUsers( users );

        LOG.info( "Method to generate current hierarchy structure for company : " + company.getCompany() + " finished" );
        return hierarchyUpload;
    }


    /**
     * Method to generate UserUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<UserUploadVO> generateUserUploadVOsForCompany( Company company ) throws InvalidInputException
    {
        LOG.info( "Method to generate user upload VOs for company : " + company.getCompany() + " started" );
        List<UserUploadVO> userVOs = new ArrayList<UserUploadVO>();
        List<User> users = company.getUsers();
        for ( User user : users ) {
            UserUploadVO userUploadVO = generateUserUploadVOForUser( user );
            userVOs.add( userUploadVO );
        }
        LOG.info( "Method to generate user upload VOs for company : " + company.getCompany() + " finished" );
        return userVOs;
    }


    /**
     * Method to get user upload VO for user
     * @param user
     * @return
     * @throws InvalidInputException
     */
    public UserUploadVO generateUserUploadVOForUser( User user ) throws InvalidInputException
    {
        if ( user == null ) {
            throw new InvalidInputException( "User is null" );
        }
        LOG.info( "Method to get user upload VO for user : " + user.getUsername() + " started" );

        //Get userSettings
        AgentSettings agentSettings;
        try {
            agentSettings = organizationManagementService.getAgentSettings( user.getUserId() );
        } catch ( NoRecordsFetchedException e ) {
            throw new InvalidInputException( "Agent Setting null for userId : " + user.getUserId() );
        }

        UserUploadVO userUploadVO = new UserUploadVO();

        userUploadVO.setFirstName( user.getFirstName() );
        if ( user.getLastName() != null && !( user.getLastName().isEmpty() ) ) {
            userUploadVO.setLastName( user.getLastName() );
        }

        userUploadVO.setBelongsToCompany( true );

        //Get list of branchIds, list of regionIds and isAgent
        List<UserProfile> userProfiles = user.getUserProfiles();
        List<Long> branchIds = new ArrayList<Long>();
        List<Long> regionIds = new ArrayList<Long>();
        for ( UserProfile userProfile : userProfiles ) {
            if ( userProfile.getStatus() == CommonConstants.STATUS_ACTIVE ) {
                branchIds.add( userProfile.getBranchId() );
                regionIds.add( userProfile.getRegionId() );
                if ( userProfile.getIsPrimary() == 1 ) {
                    if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                        userUploadVO.setAgent( true );
                    }
                }
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_BRANCH_ADMIN_PROFILE_ID ) {
                    userUploadVO.setBranchAdmin( true );
                }
                if ( userProfile.getProfilesMaster().getProfileId() == CommonConstants.PROFILES_MASTER_REGION_ADMIN_PROFILE_ID ) {
                    userUploadVO.setRegionAdmin( true );
                }
            }
        }


        if ( agentSettings.getContact_details() != null ) {
            if ( agentSettings.getContact_details().getTitle() != null
                && !( agentSettings.getContact_details().getTitle().isEmpty() ) ) {
                userUploadVO.setTitle( agentSettings.getContact_details().getTitle() );
            }
            if ( agentSettings.getContact_details().getMail_ids() != null
                && agentSettings.getContact_details().getMail_ids().getWork() != null
                && !( agentSettings.getContact_details().getMail_ids().getWork().isEmpty() ) ) {
                userUploadVO.setEmailId( agentSettings.getContact_details().getMail_ids().getWork() );
            }
            if ( agentSettings.getContact_details().getContact_numbers() != null
                && agentSettings.getContact_details().getContact_numbers().getWork() != null
                && !( agentSettings.getContact_details().getContact_numbers().getWork().isEmpty() ) ) {
                userUploadVO.setPhoneNumber( agentSettings.getContact_details().getContact_numbers().getWork() );
            }
            if ( agentSettings.getContact_details().getWeb_addresses() != null
                && agentSettings.getContact_details().getWeb_addresses().getWork() != null
                && !( agentSettings.getContact_details().getWeb_addresses().getWork().isEmpty() ) ) {
                userUploadVO.setWebsiteUrl( agentSettings.getContact_details().getWeb_addresses().getWork() );
            }
            if ( agentSettings.getContact_details().getAbout_me() != null
                && !( agentSettings.getContact_details().getAbout_me().isEmpty() ) ) {
                userUploadVO.setAboutMeDescription( agentSettings.getContact_details().getAbout_me() );
            }
        }
        if ( agentSettings.getLicenses() != null && agentSettings.getLicenses().getLicense_disclaimer() != null
            && !( agentSettings.getLicenses().getLicense_disclaimer().isEmpty() ) ) {
            userUploadVO.setLicense( agentSettings.getLicenses().getLicense_disclaimer() );
        }
        if ( agentSettings.getDisclaimer() != null && !( agentSettings.getDisclaimer().isEmpty() ) ) {
            userUploadVO.setLegalDisclaimer( agentSettings.getDisclaimer() );
        }
        if ( agentSettings.getProfileImageUrl() != null && !( agentSettings.getProfileImageUrl().isEmpty() ) ) {
            userUploadVO.setUserPhotoUrl( agentSettings.getProfileImageUrl() );
        }

        LOG.info( "Method to get user upload VO for user : " + user.getUsername() + " finished" );
        return userUploadVO;
    }


    /**
     * Method to generate BranchUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException
     */
    public List<BranchUploadVO> generateBranchUploadVOsForCompany( Company company ) throws InvalidInputException
    {

        LOG.info( "Method to generate branch upload VOs for company : " + company.getCompany() + " started" );
        List<BranchUploadVO> branchVOs = new ArrayList<BranchUploadVO>();
        List<Branch> branches = company.getBranches();
        for ( Branch branch : branches ) {
            BranchUploadVO branchUploadVO = generateBranchUploadVOForBranch( branch );
            branchVOs.add( branchUploadVO );
        }
        LOG.info( "Method to generate branch upload VOs for company : " + company.getCompany() + " finished" );
        return branchVOs;
    }


    /**
     * Method to get BranchUploadVO for branch
     * @param branch
     * @return
     * @throws InvalidInputException
     */
    public BranchUploadVO generateBranchUploadVOForBranch( Branch branch ) throws InvalidInputException
    {
        if ( branch == null ) {
            throw new InvalidInputException( "Branch is null" );
        }
        LOG.info( "Method to get branch upload VO for branch : " + branch.getBranch() + " started" );

        //Get branchSettings
        OrganizationUnitSettings branchSettings;
        try {
            branchSettings = organizationManagementService.getBranchSettingsDefault( branch.getBranchId() );
        } catch ( NoRecordsFetchedException e ) {
            throw new InvalidInputException( "Branch settings is null for branch : " + branch.getBranchId() );
        }

        BranchUploadVO branchUploadVO = new BranchUploadVO();

        branchUploadVO.setBranchId( branch.getBranchId() );
        branchUploadVO.setRegionId( branch.getRegion().getRegionId() );
        branchUploadVO.setBranchName( branch.getBranchName() );
        branchUploadVO.setAssignedRegionName( branch.getRegion().getRegion() );

        if ( branchSettings.getContact_details() != null ) {
            if ( branchSettings.getContact_details().getAddress1() != null
                && !( branchSettings.getContact_details().getAddress1().isEmpty() ) ) {
                branchUploadVO.setBranchAddress1( branchSettings.getContact_details().getAddress1() );
            }
            if ( branchSettings.getContact_details().getAddress2() != null
                && !( branchSettings.getContact_details().getAddress2().isEmpty() ) ) {
                branchUploadVO.setBranchAddress2( branchSettings.getContact_details().getAddress2() );
            }
            if ( branchSettings.getContact_details().getCountry() != null
                && !( branchSettings.getContact_details().getCountry().isEmpty() ) ) {
                branchUploadVO.setBranchCountry( branchSettings.getContact_details().getCountry() );
            }
            if ( branchSettings.getContact_details().getCountryCode() != null
                && !( branchSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                branchUploadVO.setBranchCountryCode( branchSettings.getContact_details().getCountryCode() );
            }
            if ( branchSettings.getContact_details().getState() != null
                && !( branchSettings.getContact_details().getState().isEmpty() ) ) {
                branchUploadVO.setBranchState( branchSettings.getContact_details().getState() );
            }
            if ( branchSettings.getContact_details().getCity() != null
                && !( branchSettings.getContact_details().getCity().isEmpty() ) ) {
                branchUploadVO.setBranchCity( branchSettings.getContact_details().getCity() );
            }
            if ( branchSettings.getContact_details().getZipcode() != null
                && !( branchSettings.getContact_details().getZipcode().isEmpty() ) ) {
                branchUploadVO.setBranchZipcode( branchSettings.getContact_details().getZipcode() );
            }
        }


        LOG.info( "Method to get branch upload VO for branch : " + branch.getBranch() + " finished" );
        return branchUploadVO;
    }


    /**
     * Method to generate RegionUploadVOs for a company
     * @param company
     * @return
     * @throws InvalidInputException 
     */
    public List<RegionUploadVO> generateRegionUploadVOsForCompany( Company company ) throws InvalidInputException
    {

        LOG.info( "Method to generate region upload VOs for comapny : " + company.getCompany() + " started" );
        List<RegionUploadVO> regionVOs = new ArrayList<RegionUploadVO>();
        List<Region> regions = company.getRegions();
        for ( Region region : regions ) {
            RegionUploadVO regionUploadVO = getRegionUploadVOForRegion( region );
            regionVOs.add( regionUploadVO );
        }
        LOG.info( "Method to generate region upload VOs for comapny : " + company.getCompany() + " finished" );
        return regionVOs;
    }


    /**
     * Method to get RegionUploadVO for a region
     * @param region
     * @return
     * @throws InvalidInputException 
     */
    public RegionUploadVO getRegionUploadVOForRegion( Region region ) throws InvalidInputException
    {
        if ( region == null ) {
            throw new InvalidInputException( "Region is null" );
        }
        LOG.info( "Method to get region upload VO for region : " + region.getRegion() + " started" );

        //Get regionSettings
        OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( region.getRegionId() );
        if ( regionSettings == null ) {
            throw new InvalidInputException( "Region settings is null" );
        }


        RegionUploadVO regionUploadVO = new RegionUploadVO();
        regionUploadVO.setRegionId( region.getRegionId() );
        regionUploadVO.setRegionName( region.getRegion() );
        if ( regionSettings.getContact_details() != null ) {
            if ( regionSettings.getContact_details().getAddress1() != null
                && !( regionSettings.getContact_details().getAddress1().isEmpty() ) ) {
                regionUploadVO.setRegionAddress1( regionSettings.getContact_details().getAddress1() );
            }

            if ( regionSettings.getContact_details().getAddress2() != null
                && !( regionSettings.getContact_details().getAddress2().isEmpty() ) ) {
                regionUploadVO.setRegionAddress2( regionSettings.getContact_details().getAddress2() );
            }

            if ( regionSettings.getContact_details().getCountry() != null
                && !( regionSettings.getContact_details().getCountry().isEmpty() ) ) {
                regionUploadVO.setRegionCountry( regionSettings.getContact_details().getCountry() );
            }

            if ( regionSettings.getContact_details().getCountryCode() != null
                && !( regionSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                regionUploadVO.setRegionCountryCode( regionSettings.getContact_details().getCountryCode() );
            }

            if ( regionSettings.getContact_details().getCountryCode() != null
                && !( regionSettings.getContact_details().getCountryCode().isEmpty() ) ) {
                regionUploadVO.setRegionCountryCode( regionSettings.getContact_details().getCountryCode() );
            }

            if ( regionSettings.getContact_details().getState() != null
                && !( regionSettings.getContact_details().getState().isEmpty() ) ) {
                regionUploadVO.setRegionState( regionSettings.getContact_details().getState() );
            }

            if ( regionSettings.getContact_details().getCity() != null
                && !( regionSettings.getContact_details().getCity().isEmpty() ) ) {
                regionUploadVO.setRegionCity( regionSettings.getContact_details().getCity() );
            }

            if ( regionSettings.getContact_details().getZipcode() != null
                && !( regionSettings.getContact_details().getZipcode().isEmpty() ) ) {
                regionUploadVO.setRegionZipcode( regionSettings.getContact_details().getZipcode() );
            }

        }

        LOG.info( "Method to get region upload VO for region : " + region.getRegion() + " finished" );
        return regionUploadVO;
    }


    @Transactional
    @Override
    public List<String> parseAndUploadTempCsv( FileUpload fileUpload ) throws InvalidInputException
    {
        if ( fileUpload == null || fileUpload.getFileName() == null || fileUpload.getFileName().isEmpty()
            || fileUpload.getCompany() == null || fileUpload.getAdminUserId() <= 0l ) {
            LOG.info( "Invalid upload details" );
            throw new InvalidInputException( "File name is not provided" );
        }
        InputStream fileStream = null;
        List<String> regionErrors = null;
        List<String> branchErrors = null;
        List<String> userErrors = new ArrayList<String>();
        User adminUser = getUser( fileUpload.getAdminUserId() );
        adminUser.setCompanyAdmin( true );
        try {
            fileStream = new FileInputStream( fileDirectory + fileUpload.getFileName() );
            XSSFWorkbook workBook = new XSSFWorkbook( fileStream );
            List<RegionUploadVO> uploadedRegions = parseAndUploadRegions( fileUpload, workBook, regionErrors, adminUser );
            List<BranchUploadVO> uploadedBranches = parseAndUploadBranches( fileUpload, workBook, branchErrors,
                uploadedRegions, adminUser );
            userErrors = parseAndUploadUsers( fileUpload, workBook, userErrors, uploadedRegions, uploadedBranches, adminUser );

            if ( userErrors != null && !userErrors.isEmpty() ) {
                LOG.debug( "Sending mail to realtech admin for users who were not uploaded due to some exception " );
                generateExcelForFailedRecordsAndSendMail( userErrors );

            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( fileStream != null ) {
                try {
                    fileStream.close();
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        }
        LOG.info( "Parsing and uploading hierarchy " + fileUpload.getFileName() + " for company "
            + fileUpload.getCompany().getCompany() );

        return null;
    }


    private void generateExcelForFailedRecordsAndSendMail( List<String> userErrors )
    {
        int rownum = 1;
        int count = 1;
        boolean excelCreated = false;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet( "Records not uploaded" );
        sheet = fillHeaders( sheet );
        for ( String error : userErrors ) {
            Row row = sheet.createRow( rownum++ );
            row = fillCellsInRow( row, error, count++ );
        }

        String fileName = "Record_Upload_Failure" + "_" + System.currentTimeMillis();
        FileOutputStream fileOutput = null;
        InputStream inputStream = null;
        File file = null;
        String filePath = null;
        try {
            file = new File( fileDirectoryLocation + File.separator + fileName + ".xls" );
            fileOutput = new FileOutputStream( file );
            file.createNewFile();
            workbook.write( fileOutput );
            filePath = file.getPath();
            excelCreated = true;
        } catch ( FileNotFoundException fe ) {
            LOG.error( "Exception caught " + fe.getMessage() );
            excelCreated = false;
        } catch ( IOException e ) {
            LOG.error( "Exception caught " + e.getMessage() );
            excelCreated = false;
        } finally {
            try {
                fileOutput.close();
                if ( inputStream != null ) {
                    inputStream.close();
                }
            } catch ( IOException e ) {
                LOG.error( "Exception caught " + e.getMessage() );
                excelCreated = false;
            }
        }
        if ( excelCreated ) {
            try {
                Map<String, String> attachmentsDetails = new HashMap<String, String>();
                attachmentsDetails.put( "CorruptRecords.xls", filePath );
                emailServices.sendRecordsNotUploadedCrmNotificationMail( adminName, "", adminEmailId, attachmentsDetails );
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception caught " + e.getMessage() );
            } catch ( UndeliveredEmailException e ) {
                LOG.error( "Exception caught " + e.getMessage() );
            }
        }
    }


    private Row fillCellsInRow( Row row, String userErrorString, int counter )
    {
        int cellnum = 0;
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( counter );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( userErrorString );
        return row;

    }


    public HSSFSheet fillHeaders( HSSFSheet sheet )
    {
        int cellnum = 0;
        Row row = sheet.createRow( 0 );
        Cell cell1 = row.createCell( cellnum++ );
        cell1.setCellValue( "S.No" );
        Cell cell2 = row.createCell( cellnum++ );
        cell2.setCellValue( "Error" );

        return sheet;
    }


    @SuppressWarnings ( { "rawtypes", "unchecked" })
    private List<String> parseAndUploadUsers( FileUpload fileUpload, XSSFWorkbook workBook, List<String> userErrors,
        List<RegionUploadVO> uploadedRegions, List<BranchUploadVO> uploadedBranches, User adminUser )
    {
        LOG.debug( "Parsing and uploading users: BEGIN" );
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        Map<UserUploadVO, User> map = new HashMap<UserUploadVO, User>();
        List<UserUploadVO> userUploads = parseUsers( fileUpload, workBook, userErrors, uploadedRegions, uploadedBranches,
            adminUser );
        if ( userUploads != null && !userUploads.isEmpty() ) {
            LOG.info( "Uploading users to database." );
            userMap = uploadUsers( userUploads, adminUser, userErrors );
            map = (HashMap) userMap.get( "ValidUser" );
            userErrors = (List) userMap.get( "InvalidUser" );
            if ( map != null && !map.isEmpty() ) {
                LOG.debug( "Adding extra user details " );
                for ( Map.Entry<UserUploadVO, User> entry : map.entrySet() ) {
                    UserUploadVO userUploadVO = entry.getKey();
                    User uploadedUser = entry.getValue();
                    try {
                        updateUserSettingsInMongo( uploadedUser, userUploadVO, userErrors );
                    } catch ( Exception e ) {
                        userErrors.add( "Exception caught for user " + uploadedUser.getUsername() + " "
                            + uploadedUser.getUserId() );
                    }
                }
            }
        } else {
            LOG.info( "No users to upload into the database." );
        }
        return userErrors;
    }


    private void updateUserSettingsInMongo( User user, UserUploadVO userUploadVO, List<String> userErrors )
        throws InvalidInputException
    {
        LOG.debug( "Inside method updateUserSettingsInMongo " );
        AgentSettings agentSettings = userManagementService.getAgentSettingsForUserProfiles( user.getUserId() );
        if ( agentSettings == null ) {
            userErrors.add( "No company settings found for user " + user.getUsername() + " " + user.getUserId() );

        } else {
            ContactDetailsSettings contactDetailsSettings = agentSettings.getContact_details();
            if ( contactDetailsSettings == null ) {
                contactDetailsSettings = new ContactDetailsSettings();
            }
            ContactNumberSettings contactNumberSettings = contactDetailsSettings.getContact_numbers();
            if ( contactNumberSettings == null ) {
                contactNumberSettings = new ContactNumberSettings();
            }
            contactNumberSettings.setWork( userUploadVO.getPhoneNumber() );
            contactDetailsSettings.setContact_numbers( contactNumberSettings );
            contactDetailsSettings.setAbout_me( userUploadVO.getAboutMeDescription() );
            contactDetailsSettings.setTitle( userUploadVO.getTitle() );
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
                List<String> authorizedIn = licenses.getAuthorized_in();
                if ( authorizedIn == null ) {
                    authorizedIn = new ArrayList<String>();
                }
                licenses.setAuthorized_in( getAllStateLicenses( userUploadVO.getLicense(), authorizedIn ) );
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


    private List<String> getAllStateLicenses( String licenses, List<String> authorizedIn )
    {
        String toRemove = "Licensed State(s):";
        if ( licenses.indexOf( toRemove ) != -1 ) {
            licenses = licenses.substring( licenses.indexOf( "Licensed State(s):" ) + toRemove.length(), licenses.length() );
        }
        licenses = licenses.trim();
        authorizedIn.add( licenses );
        return authorizedIn;
    }


    private List<UserUploadVO> parseUsers( FileUpload fileUpload, XSSFWorkbook workBook, List<String> userErrors,
        List<RegionUploadVO> uploadedRegions, List<BranchUploadVO> uploadedBranches, User adminUser )
    {
        LOG.debug( "Parsing users sheet" );
        List<UserUploadVO> usersToBeUploaded = new ArrayList<>();
        XSSFSheet userSheet = workBook.getSheet( USERS_SHEET );
        Iterator<Row> rows = userSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        UserUploadVO uploadedUser = null;
        boolean rowContainsError = false;
        while ( rows.hasNext() ) {
            row = (XSSFRow) rows.next();
            // skip the first 2 rows. first row is the schema and second is the header
            if ( row.getRowNum() < 2 ) {
                continue;
            }
            cells = row.cellIterator();
            uploadedUser = new UserUploadVO();
            int cellIndex = 0;
            while ( cells.hasNext() ) {
                cell = (XSSFCell) cells.next();
                cellIndex = cell.getColumnIndex();
                LOG.debug( "Column " + cell.getColumnIndex() );
                if ( cellIndex == USER_FIRST_NAME_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedUser.setFirstName( cell.getStringCellValue().trim() );
                    } else {
                        LOG.error( "First name is not present" );
                        rowContainsError = true;
                        break;
                    }
                } else if ( cellIndex == USER_LAST_NAME_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedUser.setLastName( cell.getStringCellValue().trim() );
                    }
                } else if ( cellIndex == USER_TITLE_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedUser.setTitle( cell.getStringCellValue().trim() );
                    }
                } else if ( cellIndex == USER_BRANCH_ID_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        // map it with the region
                        String sourceBranchId = null;
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            sourceBranchId = String.valueOf( cell.getNumericCellValue() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            sourceBranchId = cell.getStringCellValue();
                        }
                        try {
                            long branchId = getBranchIdFromSourceId( uploadedBranches, sourceBranchId );
                            uploadedUser.setBranchId( branchId );
                            uploadedUser.setSourceBranchId( sourceBranchId );
                        } catch ( UserAdditionException bae ) {
                            LOG.error( "Could not find branch" );
                            rowContainsError = true;
                            break;
                        }
                    }
                } else if ( cellIndex == USER_REGION_ID_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        // map it with the region
                        String sourceRegionId = null;
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            sourceRegionId = String.valueOf( cell.getNumericCellValue() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            sourceRegionId = cell.getStringCellValue();
                        }
                        try {
                            long regionId = getRegionIdFromSourceId( uploadedRegions, sourceRegionId );
                            uploadedUser.setRegionId( regionId );
                            uploadedUser.setSourceRegionId( sourceRegionId );
                        } catch ( BranchAdditionException bae ) {
                            LOG.error( "Could not find region" );
                            rowContainsError = true;
                            break;
                        }
                    }
                } else if ( cellIndex == USER_BRANCH_ID_ADMIN_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String cellValue = null;
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            cellValue = cell.getStringCellValue();
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            long lCellValue = (long) cell.getNumericCellValue();
                            cellValue = String.valueOf( lCellValue );
                        }
                        if ( cellValue != null && !cellValue.isEmpty() && !cellValue.equalsIgnoreCase( "No" ) ) {
                            uploadedUser.setBranchAdmin( true );
                        }
                    }
                } else if ( cellIndex == USER_REGION_ID_ADMIN_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String cellValue = null;
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            cellValue = cell.getStringCellValue();
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            long lCellValue = (long) cell.getNumericCellValue();
                            cellValue = String.valueOf( lCellValue );
                        }
                        if ( cellValue != null && !cellValue.isEmpty() && !cellValue.equalsIgnoreCase( "No" ) ) {
                            uploadedUser.setRegionAdmin( true );
                        }
                    }
                } else if ( cellIndex == USER_EMAIL_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String emailId = cell.getStringCellValue().trim();
                        if ( maskEmail.equals( CommonConstants.YES_STRING ) ) {
                            emailId = utils.maskEmailAddress( emailId );
                            if ( emailId != null ) {
                                uploadedUser.setEmailId( uploadedUser.getFirstName()
                                    + ( uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "" ) + " <"
                                    + emailId + ">" );
                            } else {
                                LOG.error( "Masking email address is not present" );
                                rowContainsError = true;
                                break;
                            }
                        } else {
                            uploadedUser.setEmailId( uploadedUser.getFirstName()
                                + ( uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "" ) + " <"
                                + emailId + ">" );
                        }
                    } else {
                        LOG.error( "Email address is not present" );
                        rowContainsError = true;
                        break;
                    }
                } else if ( cellIndex == USER_PHOTO_PROFILE_URL ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String userPhotoUrl = cell.getStringCellValue();
                        uploadedUser.setUserPhotoUrl( userPhotoUrl );
                    }
                } else if ( cellIndex == USER_PHONE_NUMBER ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            uploadedUser.setPhoneNumber( String.valueOf( (long) cell.getNumericCellValue() ) );
                        } else {
                            uploadedUser.setPhoneNumber( cell.getStringCellValue() );
                        }
                    }
                } else if ( cellIndex == USER_WEBSITE ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String websiteUrl = cell.getStringCellValue();
                        uploadedUser.setWebsiteUrl( websiteUrl );
                    }
                } else if ( cellIndex == USER_LICENSES ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String license = cell.getStringCellValue();
                        uploadedUser.setLicense( license );
                    }
                } else if ( cellIndex == USER_LEGAL_DISCLAIMER ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String legalDisclaimer = cell.getStringCellValue();
                        uploadedUser.setLegalDisclaimer( legalDisclaimer );
                    }
                } else if ( cellIndex == USER_ABOUT_ME_DESCRIPTION ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        String aboutMeDescription = cell.getStringCellValue();
                        uploadedUser.setAboutMeDescription( aboutMeDescription );
                    }
                }
            }
            if ( rowContainsError ) {
                LOG.error( "Could not process row" );
                if ( userErrors == null ) {
                    userErrors = new ArrayList<>();
                }
                userErrors.add( "Error in user row " + row.getRowNum() );
                rowContainsError = false;
                continue;
            }
            if ( uploadedUser.getBranchId() == 0l && uploadedUser.getRegionId() == 0l ) {
                uploadedUser.setBelongsToCompany( true );
            }
            usersToBeUploaded.add( uploadedUser );
        }
        return usersToBeUploaded;
    }


    // modifies the list of branchesToUpload with the actual branch id
    private Map<Object, Object> uploadUsers( List<UserUploadVO> usersToUpload, User adminUser, List<String> userErrors )
    {
        LOG.debug( "Uploading users to database" );
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        Map<UserUploadVO, User> map = new HashMap<UserUploadVO, User>();
        for ( UserUploadVO userToBeUploaded : usersToUpload ) {
            try {
                if ( checkIfEmailIdExists( userToBeUploaded.getEmailId(), adminUser.getCompany() ) ) {
                    try {
                        User user = assignUser( userToBeUploaded, adminUser );
                        if ( user != null ) {
                            map.put( userToBeUploaded, user );
                        }
                    } catch ( UserAdditionException e ) {
                        LOG.error( "UserAdditionException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "UserAdditionException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( SolrException e ) {
                        LOG.error( "SolrException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "SolrException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( NoRecordsFetchedException e ) {
                        LOG.error( "NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( UserAssignmentException e ) {
                        LOG.error( "UserAssignmentException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "UserAssignmentException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    }
                } else {
                    // add user
                    try {
                        User user = addUser( userToBeUploaded, adminUser );
                        if ( user != null ) {
                            map.put( userToBeUploaded, user );
                        }
                    } catch ( InvalidInputException e ) {
                        LOG.error( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( NoRecordsFetchedException e ) {
                        LOG.error( "NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "NoRecordsFetchedException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( SolrException e ) {
                        LOG.error( "SolrException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "SolrException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( UserAssignmentException e ) {
                        LOG.error( "UserAssignmentException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "UserAssignmentException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    } catch ( UserAdditionException e ) {
                        LOG.error( "UserAdditionException while adding user: " + userToBeUploaded.getEmailId() );
                        userErrors.add( "UserAdditionException while adding user: " + userToBeUploaded.getEmailId()
                            + " Exception is : " + e.getMessage() );
                    }
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId() );
                userErrors.add( "InvalidInputException while adding user: " + userToBeUploaded.getEmailId()
                    + " Exception is : " + e.getMessage() );
            }
        }
        userMap.put( "ValidUser", map );
        userMap.put( "InvalidUser", userErrors );
        return userMap;

    }


    private List<BranchUploadVO> parseAndUploadBranches( FileUpload fileUpload, XSSFWorkbook workBook,
        List<String> branchErrors, List<RegionUploadVO> uploadedRegions, User adminUser )
    {
        LOG.debug( "Parsing and uploading branches: BEGIN" );
        List<BranchUploadVO> branchUploads = parseBranches( fileUpload, workBook, branchErrors, uploadedRegions, adminUser );
        if ( branchUploads != null && !branchUploads.isEmpty() ) {
            LOG.info( "Uploading branches to database." );
            uploadBranches( branchUploads, adminUser, branchErrors );
        } else {
            LOG.info( "No branches to upload into the database." );
        }
        return branchUploads;
    }


    private List<BranchUploadVO> parseBranches( FileUpload fileUpload, XSSFWorkbook workBook, List<String> branchErrors,
        List<RegionUploadVO> uploadedRegions, User adminUser )
    {
        LOG.debug( "Parsing branches sheet" );
        List<BranchUploadVO> branchesToBeUploaded = new ArrayList<>();
        XSSFSheet branchSheet = workBook.getSheet( BRANCH_SHEET );
        Iterator<Row> rows = branchSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        BranchUploadVO uploadedBranch = null;
        boolean rowContainsError = false;
        while ( rows.hasNext() ) {
            row = (XSSFRow) rows.next();
            // skip the first 2 row. first row is the schema and second is the header
            if ( row.getRowNum() < 2 ) {
                continue;
            }
            cells = row.cellIterator();
            uploadedBranch = new BranchUploadVO();
            int cellIndex = 0;
            while ( cells.hasNext() ) {
                cell = (XSSFCell) cells.next();
                cellIndex = cell.getColumnIndex();
                if ( cellIndex == BRANCH_ID_INDEX ) {
                    if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                        try {
                            uploadedBranch.setSourceBranchId( String.valueOf( cell.getNumericCellValue() ) );
                        } catch ( NumberFormatException nfe ) {
                            // TODO: mark this record as error
                            LOG.error( "Source branch id is not present" );
                            rowContainsError = true;
                            break;
                        }
                    } else {
                        uploadedBranch.setSourceBranchId( cell.getStringCellValue() );
                    }
                } else if ( cellIndex == BRANCH_NAME_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedBranch.setBranchName( cell.getStringCellValue().trim() );
                    } else {
                        LOG.error( "branch name not present" );
                        rowContainsError = true;
                        break;
                    }
                } else if ( cellIndex == BRANCH_REGION_ID_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        // map it with the region
                        String sourceRegionId = null;
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            sourceRegionId = String.valueOf( cell.getNumericCellValue() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            sourceRegionId = cell.getStringCellValue();
                        }
                        try {
                            long regionId = getRegionIdFromSourceId( uploadedRegions, sourceRegionId );
                            uploadedBranch.setRegionId( regionId );
                            uploadedBranch.setSourceRegionId( sourceRegionId );
                        } catch ( BranchAdditionException bae ) {
                            LOG.error( "Could not find region" );
                            rowContainsError = true;
                            break;
                        }
                    }
                } else if ( cellIndex == BRANCH_ADDRESS1_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedBranch.setBranchAddress1( cell.getStringCellValue() );
                        uploadedBranch.setAddressSet( true );
                    }
                } else if ( cellIndex == BRANCH_ADDRESS2_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedBranch.setBranchAddress2( cell.getStringCellValue() );
                        uploadedBranch.setAddressSet( true );
                    }
                } else if ( cellIndex == BRANCH_CITY_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedBranch.setBranchCity( cell.getStringCellValue() );
                    } else {
                        LOG.error( "branch city not present" );
                        rowContainsError = true;
                        break;
                    }
                } else if ( cellIndex == BRANCH_STATE_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedBranch.setBranchState( cell.getStringCellValue() );
                    }
                } else if ( cellIndex == BRANCH_ZIP_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            uploadedBranch.setBranchZipcode( cell.getStringCellValue() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            uploadedBranch.setBranchZipcode( String.valueOf( (int) cell.getNumericCellValue() ) );
                        }
                    }
                }
            }
            if ( rowContainsError ) {
                LOG.error( "Could not process row" );
                if ( branchErrors == null ) {
                    branchErrors = new ArrayList<>();
                }
                branchErrors.add( "Error in Branch row " + row.getRowNum() );
                rowContainsError = false;
                continue;
            }
            branchesToBeUploaded.add( uploadedBranch );
        }
        return branchesToBeUploaded;
    }


    // modifies the list of branchesToUpload with the actual branch id
    private void uploadBranches( List<BranchUploadVO> branchesToUpload, User adminUser, List<String> branchErrors )
    {
        LOG.debug( "Uploading branches" );
        Branch branch = null;
        for ( BranchUploadVO branchToUpload : branchesToUpload ) {
            try {
                branch = createBranch( adminUser, branchToUpload );
                branchToUpload.setBranchId( branch.getBranchId() );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException while uploading branch to database. " + branchToUpload.getSourceRegionId(), e );
                branchErrors.add( "Error while uploading branch to database. " + branchToUpload.getSourceRegionId() );
            } catch ( BranchAdditionException e ) {
                LOG.error( "RegionAdditionException while uploading branch to database. " + branchToUpload.getSourceRegionId(),
                    e );
                branchErrors.add( "Error while uploading branch to database. " + branchToUpload.getSourceRegionId() );
            } catch ( SolrException e ) {
                LOG.error( "SolrException while uploading branch to database. " + branchToUpload.getSourceRegionId(), e );
                branchErrors.add( "Error while uploading branch to database. " + branchToUpload.getSourceRegionId() );
            }
        }
    }


    long getRegionIdFromSourceId( List<RegionUploadVO> uploadedRegions, String regionSourceId ) throws BranchAdditionException
    {
        LOG.debug( "Getting region id from source id" );
        long regionId = 0;
        for ( RegionUploadVO uploadedRegion : uploadedRegions ) {
            if ( uploadedRegion.getSourceRegionId().equals( regionSourceId ) ) {
                regionId = uploadedRegion.getRegionId();
                break;
            }
        }
        if ( regionId == 0l ) {
            throw new BranchAdditionException( "Could not find region id for the region" );
        }
        return regionId;
    }


    long getBranchIdFromSourceId( List<BranchUploadVO> uploadedBranches, String regionBranchId ) throws UserAdditionException
    {
        LOG.debug( "Getting branch id from source id" );
        long branchId = 0;
        for ( BranchUploadVO uploadedBranch : uploadedBranches ) {
            if ( uploadedBranch.getSourceBranchId().equals( regionBranchId ) ) {
                branchId = uploadedBranch.getBranchId();
                break;
            }
        }
        if ( branchId == 0l ) {
            throw new UserAdditionException( "Could not find brach id for the user" );
        }
        return branchId;
    }


    private List<RegionUploadVO> parseAndUploadRegions( FileUpload fileUpload, XSSFWorkbook workBook,
        List<String> regionErrors, User adminUser )
    {
        LOG.debug( "Parsing and uploading regions: BEGIN" );
        List<RegionUploadVO> regionUploads = parseRegions( fileUpload, workBook, regionErrors, adminUser );
        // uploading regions
        if ( regionUploads != null && !regionUploads.isEmpty() ) {
            LOG.info( "Uploading regions to database." );
            uploadRegions( regionUploads, adminUser, regionErrors );
        } else {
            LOG.info( "No regions to upload into the database." );
        }
        return regionUploads;
    }


    private List<RegionUploadVO> parseRegions( FileUpload fileUpload, XSSFWorkbook workBook, List<String> regionErrors,
        User adminUser )
    {
        LOG.debug( "Parsing regions from CSV" );
        List<RegionUploadVO> uploadedRegions = new ArrayList<RegionUploadVO>();
        XSSFSheet regionSheet = workBook.getSheet( REGION_SHEET );
        Iterator<Row> rows = regionSheet.rowIterator();
        Iterator<Cell> cells = null;
        XSSFRow row = null;
        XSSFCell cell = null;
        RegionUploadVO uploadedRegion = null;
        boolean rowContainsError = false;
        while ( rows.hasNext() ) {
            row = (XSSFRow) rows.next();
            // skip the first 2 row. first row is the schema and second is the header
            if ( row.getRowNum() < 2 ) {
                continue;
            }
            cells = row.cellIterator();
            uploadedRegion = new RegionUploadVO();
            int cellIndex = 0;
            while ( cells.hasNext() ) {
                cell = (XSSFCell) cells.next();
                cellIndex = cell.getColumnIndex();
                if ( cellIndex == REGION_ID_INDEX ) {
                    if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                        try {
                            uploadedRegion.setSourceRegionId( String.valueOf( cell.getNumericCellValue() ) );
                        } catch ( NumberFormatException nfe ) {
                            // TODO: mark this record as error
                            LOG.error( "Source region id is not present" );
                            rowContainsError = true;
                            break;
                        }
                    } else {
                        uploadedRegion.setSourceRegionId( cell.getStringCellValue() );
                    }
                } else if ( cellIndex == REGION_NAME_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedRegion.setRegionName( cell.getStringCellValue().trim() );
                    } else {
                        LOG.error( "Region name is not present" );
                        rowContainsError = true;
                        break;
                    }
                } else if ( cellIndex == REGION_ADDRESS1_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedRegion.setRegionAddress1( cell.getStringCellValue() );
                        uploadedRegion.setAddressSet( true );
                    }
                } else if ( cellIndex == REGION_ADDRESS2_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedRegion.setRegionAddress2( cell.getStringCellValue() );
                        uploadedRegion.setAddressSet( true );
                    }
                } else if ( cellIndex == REGION_CITY_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedRegion.setRegionCity( cell.getStringCellValue() );
                    }
                } else if ( cellIndex == REGION_STATE_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        uploadedRegion.setRegionState( cell.getStringCellValue() );
                    }
                } else if ( cellIndex == REGION_ZIP_INDEX ) {
                    if ( cell.getCellType() != XSSFCell.CELL_TYPE_BLANK ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING ) {
                            uploadedRegion.setRegionZipcode( cell.getStringCellValue() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC ) {
                            uploadedRegion.setRegionZipcode( String.valueOf( (int) cell.getNumericCellValue() ) );
                        }
                    }
                }
            }
            if ( rowContainsError ) {
                LOG.error( "Could not process row" );
                if ( regionErrors == null ) {
                    regionErrors = new ArrayList<>();
                }
                regionErrors.add( "Error in Region row " + row.getRowNum() );
                rowContainsError = false;
                continue;
            }
            uploadedRegions.add( uploadedRegion );
        }
        return uploadedRegions;
    }


    // modifies the list of regionsToUpload with the actual region id
    private void uploadRegions( List<RegionUploadVO> regionsToUpload, User adminUser, List<String> regionErrors )
    {
        LOG.debug( "Uploading regions" );
        Region region = null;
        for ( RegionUploadVO regionToUpload : regionsToUpload ) {
            try {
                region = createRegion( adminUser, regionToUpload );
                regionToUpload.setRegionId( region.getRegionId() );
            } catch ( InvalidInputException e ) {
                LOG.error( "InvalidInputException while uploading region to database. " + regionToUpload.getSourceRegionId(), e );
                regionErrors.add( "Error while uploading region to database. " + regionToUpload.getSourceRegionId() );
            } catch ( RegionAdditionException e ) {
                LOG.error( "RegionAdditionException while uploading region to database. " + regionToUpload.getSourceRegionId(),
                    e );
                regionErrors.add( "Error while uploading region to database. " + regionToUpload.getSourceRegionId() );
            } catch ( SolrException e ) {
                LOG.error( "SolrException while uploading region to database. " + regionToUpload.getSourceRegionId(), e );
                regionErrors.add( "Error while uploading region to database. " + regionToUpload.getSourceRegionId() );
            }
        }
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


    @SuppressWarnings ( "unchecked")
    User addUser( UserUploadVO user, User adminUser ) throws InvalidInputException, NoRecordsFetchedException, SolrException,
        UserAssignmentException, UserAdditionException
    {
        User uploadedUser = null;
        Map<String, Object> map = new HashMap<String, Object>();
        List<User> userList = new ArrayList<User>();
        if ( checkIfEmailIdExists( user.getEmailId(), adminUser.getCompany() ) ) {
            throw new UserAdditionException( "The user already exists" );
        }
        if ( user.isBelongsToCompany() ) {
            // He belongs to the company
            LOG.debug( "Adding user : " + user.getEmailId() + " belongs to company" );
            map = organizationManagementService.addIndividual( adminUser, 0, 0, 0, new String[] { user.getEmailId() }, false,
                true );
            if ( map != null ) {
                userList = (List<User>) map.get( CommonConstants.VALID_USERS_LIST );
            }
        } else if ( user.getBranchId() > 0l ) {
            // He belongs to a branch
            LOG.debug( "Adding user : " + user.getEmailId() + " belongs to branch : " + user.getBranchId() );
            Branch branch = branchDao.findById( Branch.class, user.getBranchId() );

            if ( user.isBranchAdmin() ) {
                LOG.debug( "User is the branch admin" );
                map = organizationManagementService.addIndividual( adminUser, 0, branch.getBranchId(), branch.getRegion()
                    .getRegionId(), new String[] { user.getEmailId() }, true, true );
                if ( user.isAgent() ) {
                    organizationManagementService.addIndividual( adminUser, 0, branch.getBranchId(), branch.getRegion()
                        .getRegionId(), new String[] { user.getEmailId() }, false, true );
                }
                if ( map != null ) {
                    userList = (List<User>) map.get( CommonConstants.VALID_USERS_LIST );
                }
                LOG.debug( "Added user : " + user.getEmailId() );
            } else {
                LOG.debug( "User is not the branch admin" );
                map = organizationManagementService.addIndividual( adminUser, 0, branch.getBranchId(), branch.getRegion()
                    .getRegionId(), new String[] { user.getEmailId() }, false, true );
                if ( map != null ) {
                    userList = (List<User>) map.get( CommonConstants.VALID_USERS_LIST );
                }
                LOG.debug( "Added user : " + user.getEmailId() );
            }
        } else if ( user.getRegionId() > 0l ) {
            // He belongs to the region
            LOG.debug( "Adding user : " + user.getEmailId() + " belongs to region : " + user.getRegionId() );
            Region region = regionDao.findById( Region.class, user.getRegionId() );
            if ( user.isRegionAdmin() ) {
                LOG.debug( "User is the region admin." );
                map = organizationManagementService.addIndividual( adminUser, 0, 0, region.getRegionId(),
                    new String[] { user.getEmailId() }, true, true );
                if ( user.isAgent() ) {
                    organizationManagementService.addIndividual( adminUser, 0, 0, region.getRegionId(),
                        new String[] { user.getEmailId() }, false, true );
                }
                if ( map != null ) {
                    userList = (List<User>) map.get( CommonConstants.VALID_USERS_LIST );
                }
                LOG.debug( "Added user : " + user.getEmailId() );
            } else {
                LOG.debug( "User is not the admin of the region" );
                map = organizationManagementService.addIndividual( adminUser, 0, 0, region.getRegionId(),
                    new String[] { user.getEmailId() }, false, true );
                if ( map != null ) {
                    userList = (List<User>) map.get( CommonConstants.VALID_USERS_LIST );
                }
                LOG.debug( "Added user : " + user.getEmailId() );
            }
        } else {
            LOG.error( "Please specifiy where the user belongs!" );
            throw new UserAdditionException( "Please specifiy where the user belongs!" );
        }

        if ( userList != null && !userList.isEmpty() ) {
            uploadedUser = userList.get( 0 );
        }
        return uploadedUser;

    }


    User assignUser( UserUploadVO user, User adminUser ) throws UserAdditionException, InvalidInputException, SolrException,
        NoRecordsFetchedException, UserAssignmentException
    {

        LOG.info( "User already exists so assigning user to approprite place" );
        if ( !( checkIfEmailIdExistsWithCompany( user.getEmailId(), adminUser.getCompany() ) ) ) {
            throw new UserAdditionException( "User : " + user.getEmailId() + " belongs to a different company" );
        }
        User assigneeUser = userManagementService.getUserByEmailAddress( extractEmailId( user.getEmailId() ) );

        if ( user.isBelongsToCompany() ) {
            LOG.debug( "Assigning user id : " + assigneeUser.getUserId() );
            organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), 0, 0, null, false, true );
        } else if ( user.getBranchId() > 0l ) {
            // User belongs to a branch
            LOG.debug( "Assigning user : " + user.getEmailId() + " belongs to branch : " + user.getBranchId() );
            Branch branch = branchDao.findById( Branch.class, user.getBranchId() );
            if ( user.isBranchAdmin() ) {
                LOG.debug( "User is the branch admin" );
                organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch
                    .getRegion().getRegionId(), null, true, true );
                if ( user.isAgent() ) {
                    organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), branch.getBranchId(),
                        branch.getRegion().getRegionId(), null, false, true );
                }
                LOG.debug( "Added user : " + user.getEmailId() );
            } else {
                LOG.debug( "User is not the branch admin" );
                organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), branch.getBranchId(), branch
                    .getRegion().getRegionId(), null, false, true );
                LOG.debug( "Added user : " + user.getEmailId() );
            }
        } else if ( user.getRegionId() > 0l ) {
            // He belongs to the region
            LOG.debug( "Assigning user : " + user.getEmailId() + " belongs to region : " + user.getRegionId() );
            Region region = regionDao.findById( Region.class, user.getRegionId() );
            if ( user.isRegionAdmin() ) {
                LOG.debug( "User is the region admin." );
                organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), 0, region.getRegionId(),
                    null, true, true );
                LOG.debug( "Added user : " + user.getEmailId() );
                if ( user.isAgent() ) {
                    organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), 0, region.getRegionId(),
                        null, false, true );
                }

            } else {
                LOG.debug( "User is not the admin of the region" );
                organizationManagementService.addIndividual( adminUser, assigneeUser.getUserId(), 0, region.getRegionId(),
                    null, false, true );
                LOG.debug( "Added user : " + user.getEmailId() );
            }
        }

        return assigneeUser;

    }


    /**
     * Creates a user and assigns him under the appropriate branch or region else company.
     * 
     * @param adminUser
     * @param user
     * @throws InvalidInputException
     * @throws UserAdditionException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     */
    @Transactional
    @Override
    public void createUser( User adminUser, UserUploadVO user ) throws InvalidInputException, UserAdditionException,
        NoRecordsFetchedException, SolrException, UserAssignmentException
    {

        if ( adminUser == null ) {
            LOG.error( "admin user parameter is null!" );
            throw new InvalidInputException( "admin user parameter is null!" );
        }
        if ( user == null ) {
            LOG.error( "user parameter is null!" );
            throw new InvalidInputException( "user parameter is null!" );
        }

        LOG.info( "createUser called to create user : " + user.getEmailId() );
        Company company = getCompany( adminUser );
        LicenseDetail companyLicenseDetail = getLicenseDetail( company );

        if ( companyLicenseDetail.getAccountsMaster().getMaxUsersAllowed() != CommonConstants.NO_LIMIT ) {
            if ( userDao.getUsersCountForCompany( company ) >= companyLicenseDetail.getAccountsMaster().getMaxUsersAllowed() ) {
                LOG.error( "Max number of users added! Cannot add more users." );
                throw new UserAdditionException( "Max number of users added! Cannot add more users." );
            } else {
                if ( !organizationManagementService.validateEmail( user.getEmailId() ) ) {
                    LOG.error( "Email id for the user is invalid!" );
                    throw new UserAdditionException( "Email id for the user is invalid!" );
                }
            }
        }

        if ( checkIfEmailIdExists( user.getEmailId(), company ) ) {
            LOG.debug( "Validations complete, adding user!" );
            assignUser( user, adminUser );
            LOG.debug( "User added!" );
        } else {
            LOG.debug( "User already exists. Assigning him appropriately" );
            addUser( user, adminUser );
            LOG.debug( "User assigned" );
        }

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
    @Override
    public Branch createBranch( User adminUser, BranchUploadVO branch ) throws InvalidInputException, BranchAdditionException,
        SolrException
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

        Company company = getCompany( adminUser );
        LicenseDetail companyLicenseDetail = getLicenseDetail( company );

        if ( organizationManagementService.isBranchAdditionAllowed( adminUser,
            AccountType.getAccountType( companyLicenseDetail.getAccountsMaster().getAccountsMasterId() ) ) ) {

            /*
             * if (!validateBranch(branch, company)) {
             * LOG.error("Branch with the name already exists!"); throw new
             * BranchAdditionException("Branch with the name already exists!"); } if
             * (branch.getAssignedRegionName() != null) { // He belongs to the region
             * LOG.debug("Adding branch : " + branch.getBranchName() + " belongs to region : " +
             * branch.getAssignedRegionName()); Map<String, Object> queries = new HashMap<String,
             * Object>(); queries.put(CommonConstants.REGION_NAME_COLUMN,
             * branch.getAssignedRegionName()); queries.put(CommonConstants.COMPANY_COLUMN,
             * adminUser.getCompany()); queries.put(CommonConstants.STATUS_COLUMN,
             * CommonConstants.STATUS_ACTIVE); List<Region> regions =
             * regionDao.findByKeyValue(Region.class, queries); if (regions == null ||
             * regions.isEmpty()) { LOG.error("Region name is invalid!"); throw new
             * BranchAdditionException("Region name is invalid!"); } else { Region region =
             * regions.get(CommonConstants.INITIAL_INDEX);
             * organizationManagementService.addNewBranch(adminUser, region.getRegionId(),
             * CommonConstants.NO, branch.getBranchName(), branch.getBranchAddress1(),
             * branch.getBranchAddress2(), branch.getBranchCountry(), branch.getBranchCountryCode(),
             * branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode());
             * LOG.debug("Branch added!"); } } else if (branch.isAssignToCompany()) {
             * LOG.debug("adding branch : " + branch.getBranchName() + " under the company!");
             * organizationManagementService.addNewBranch(adminUser, 0l, CommonConstants.NO,
             * branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(),
             * branch.getBranchCountry(), branch.getBranchCountryCode(), branch.getBranchState(),
             * branch.getBranchCity(), branch.getBranchZipcode()); LOG.debug("Branch added!"); }
             * else { LOG.error("Please specifiy where the branch belongs!"); throw new
             * BranchAdditionException("Please specifiy where the branch belongs!"); }
             */
            newBranch = organizationManagementService.addNewBranch( adminUser, branch.getRegionId(), CommonConstants.NO,
                branch.getBranchName(), branch.getBranchAddress1(), branch.getBranchAddress2(), COUNTRY, COUNTRY_CODE,
                branch.getBranchState(), branch.getBranchCity(), branch.getBranchZipcode() );
        } else {
            LOG.error( "admin user : " + adminUser.getEmailId() + " is not authorized to add branches! Accounttype : "
                + companyLicenseDetail.getAccountsMaster().getAccountName() );
            throw new BranchAdditionException( "admin user : " + adminUser.getEmailId() + " is not authorized to add branches!" );
        }
        return newBranch;
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
    @Override
    public Region createRegion( User adminUser, RegionUploadVO region ) throws InvalidInputException, RegionAdditionException,
        SolrException
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
        Company company = getCompany( adminUser );
        LicenseDetail licenseDetail = getLicenseDetail( company );

        if ( organizationManagementService.isRegionAdditionAllowed( adminUser,
            AccountType.getAccountType( licenseDetail.getAccountsMaster().getAccountsMasterId() ) ) ) {
            /*if (!validateRegion(region, company)) {
            	LOG.error("Region with that name already exists!");
            	throw new RegionAdditionException("Region with that name already exists!");
            }*/
            LOG.debug( "Adding region : " + region.getRegionName() );
            newRegion = organizationManagementService.addNewRegion( adminUser, region.getRegionName(), CommonConstants.NO,
                region.getRegionAddress1(), region.getRegionAddress2(), region.getRegionCountry(),
                region.getRegionCountryCode(), region.getRegionState(), region.getRegionCity(), region.getRegionZipcode() );
            organizationManagementService.addNewBranch( adminUser, newRegion.getRegionId(), CommonConstants.YES,
                CommonConstants.DEFAULT_BRANCH_NAME, null, null, null, null, null, null, null );
        } else {
            LOG.error( "admin user : " + adminUser.getEmailId() + " is not authorized to add regions" );
            throw new RegionAdditionException( "admin user : " + adminUser.getEmailId() + " is not authorized to add regions" );
        }
        return newRegion;
    }


    /**
     * Used to get the admin user while testing
     * 
     * @return
     */
    @Transactional
    @Override
    public User getUser( long userId )
    {
        User adminUser = userDao.findById( User.class, userId );
        return adminUser;
    }


    /**
     * Takes a map of objects and creates them and returns list of errors if any
     * 
     * @param uploadObjects
     * @param adminUser
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     * @throws SolrException
     * @throws UserAssignmentException
     */
    @Transactional
    @Override
    public List<String> createAndReturnErrors( Map<String, List<Object>> uploadObjects, User adminUser )
        throws InvalidInputException, NoRecordsFetchedException, SolrException, UserAssignmentException
    {

        if ( uploadObjects == null || uploadObjects.isEmpty() ) {
            LOG.error( "uploadObjects parameter is null or empty!" );
            throw new InvalidInputException( "uploadObjects parameter is null or empty!" );
        }
        List<String> errorList = new ArrayList<String>();
        LOG.info( "Creating all the users,branches and regions" );

        if ( adminUser.getStatus() == CommonConstants.STATUS_NOT_VERIFIED ) {
            LOG.error( "User has not verified account. So region addition not allowed!" );
            errorList.add( "ERROR : Account has not been verified!" );
            return errorList;
        }

        // We create all the regions
        List<Object> regionUploadVOs = uploadObjects.get( CommonConstants.REGIONS_MAP_KEY );
        if ( regionUploadVOs != null && !regionUploadVOs.isEmpty() ) {
            LOG.debug( "Creating all the regions" );
            RegionUploadVO region = null;
            for ( Object regionUploadVO : regionUploadVOs ) {
                try {
                    region = (RegionUploadVO) regionUploadVO;
                    LOG.debug( "Creating region : " + region.getRegionName() );
                    createRegion( adminUser, region );
                    region = null;
                } catch ( RegionAdditionException e ) {
                    LOG.error( "ERROR : " + " while adding region : " + region.getRegionName() + " message : " + e.getMessage() );
                    errorList.add( "ERROR : " + " while adding region : " + region.getRegionName() + " message : "
                        + e.getMessage() );
                }
            }
            LOG.debug( "Creation of all regions complete!" );
        }

        // We create all the branches
        List<Object> branchUploadVOs = uploadObjects.get( CommonConstants.BRANCHES_MAP_KEY );
        if ( branchUploadVOs != null && !branchUploadVOs.isEmpty() ) {
            LOG.debug( "Creating all the branches" );
            BranchUploadVO branch = null;
            for ( Object branchUploadVO : branchUploadVOs ) {
                try {
                    branch = (BranchUploadVO) branchUploadVO;
                    LOG.debug( "Creating branch : " + branch.getBranchName() );
                    createBranch( adminUser, branch );
                    branch = null;
                } catch ( BranchAdditionException e ) {
                    LOG.error( "ERROR : " + " while adding branch : " + branch.getBranchName() + " message : " + e.getMessage() );
                    errorList.add( "ERROR : " + " while adding branch : " + branch.getBranchName() + " message : "
                        + e.getMessage() );
                }
            }
            LOG.debug( "Creation of all branches complete!" );
        }

        // First we create all the users
        List<Object> userUploadVOs = uploadObjects.get( CommonConstants.USERS_MAP_KEY );
        if ( userUploadVOs != null && !userUploadVOs.isEmpty() ) {
            LOG.debug( "Creating all the users" );
            UserUploadVO user = null;
            for ( Object userUploadVO : userUploadVOs ) {
                try {
                    user = (UserUploadVO) userUploadVO;
                    LOG.debug( "Creating user : " + user.getEmailId() );
                    createUser( adminUser, user );
                    user = null;
                } catch ( UserAdditionException e ) {
                    LOG.error( "ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage() );
                    errorList.add( "ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage() );
                } catch ( InvalidInputException e ) {
                    if ( e.getMessage() != null
                        && e.getMessage().equals( DisplayMessageConstants.USER_ASSIGNMENT_ALREADY_EXISTS ) ) {
                        LOG.error( "ERROR : " + " while adding user : " + user.getEmailId() + " message : " + e.getMessage() );
                        errorList.add( "ERROR : " + " while adding user : " + user.getEmailId()
                            + " message : User aleardy exists and assigned!" );
                    } else {
                        LOG.info( e.getErrorCode() );
                        throw e;
                    }
                }
            }
            LOG.debug( "Creation of all users complete!" );
        }

        LOG.info( "Objects created. Returning the list of errors" );

        return errorList;
    }


    @Transactional
    @Override
    public void postProcess( User adminUser )
    {
        LOG.info( "Post processing.." );
        // TODO: to be taken out for live example
        // get list of all users and activate them
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put( CommonConstants.COMPANY_COLUMN, adminUser.getCompany() );
        queryMap.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_NOT_VERIFIED );
        List<User> userList = userDao.findByKeyValue( User.class, queryMap );
        if ( userList != null ) {
            for ( User user : userList ) {
                try {
                    if ( user.getLoginPassword() == null ) {
                        user.setIsAtleastOneUserprofileComplete( CommonConstants.STATUS_ACTIVE );
                        user.setStatus( CommonConstants.STATUS_ACTIVE );
                        user.setModifiedBy( String.valueOf( user.getUserId() ) );
                        user.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );

                        /**
                         * Set the new password
                         */
                        String encryptedPassword = encryptionHelper.encryptSHA512( "d#demo" );
                        user.setLoginPassword( encryptedPassword );

                        userDao.saveOrUpdate( user );

                        // update the solr status too
                        solrSearchService.editUserInSolr( user.getUserId(), CommonConstants.STATUS_SOLR,
                            String.valueOf( user.getStatus() ) );
                    }
                } catch ( InvalidInputException ie ) {
                    LOG.error( "Error while post processing user " + user.toString(), ie );
                } catch ( SolrException e ) {
                    LOG.error( "SOLR issue while post processing user " + user.toString(), e );
                }
            }
        }

    }


    @Transactional
    @Override
    public List<FileUpload> getFilesToBeUploaded() throws NoRecordsFetchedException
    {
        LOG.info( "Check if files need to be uploaded" );
        Map<String, Object> queries = new HashMap<>();
        queries.put( CommonConstants.FILE_UPLOAD_TYPE_COLUMN, CommonConstants.FILE_UPLOAD_HIERARCHY_TYPE );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<FileUpload> filesToBeUploaded = fileUploadDao.findByKeyValue( FileUpload.class, queries );
        if ( filesToBeUploaded == null || filesToBeUploaded.isEmpty() ) {
            throw new NoRecordsFetchedException( "No files to be uploaded" );
        }
        return filesToBeUploaded;
    }


    @Transactional
    @Override
    public void updateFileUploadRecord( FileUpload fileUpload ) throws InvalidInputException
    {
        LOG.info( "Check if files need to be uploaded" );
        if ( fileUpload == null ) {
            throw new InvalidInputException( "File upload is null" );
        }
        fileUploadDao.update( fileUpload );
    }

}
