package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.BranchUploadVO;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.RegionUploadVO;
import com.realtech.socialsurvey.core.entities.UploadValidation;
import com.realtech.socialsurvey.core.entities.UserUploadVO;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.upload.HierarchyDownloadService;
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

    @Value ( "${MASK_EMAIL_ADDRESS}")
    private String maskEmail;


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
            parseRegions( workBook, validationObject );
            parseBranches( workBook, validationObject );
            parseUsers( workBook, validationObject );
            uploadValidationService.validateHeirarchyUpload( validationObject );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( Exception e ) {
            e.printStackTrace();
            String message = e.getMessage();
            if ( message.equals( CommonConstants.HIERARCHY_REGION_HEADERS_INVALID )
                || message.equals( CommonConstants.HIERARCHY_BRANCH_HEADERS_INVALID )
                || message.equals( CommonConstants.HIERARCHY_USER_HEADERS_INVALID ) ) {
                potentialException = new InvalidInputException( e.getMessage() );
            }
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
     * Validates regions to be uploaded.
     * 
     * @param workBook
     * @param validationObject
     * @throws InvalidInputException 
     */
    void parseRegions( XSSFWorkbook workBook, UploadValidation validationObject ) throws InvalidInputException
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
                            && !String.valueOf( cell.getNumericCellValue() ).trim().isEmpty() ) {
                            uploadedRegion.setSourceRegionId( String.valueOf( cell.getNumericCellValue() ).trim() );
                        } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedRegion.setSourceRegionId( cell.getStringCellValue().trim() );
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
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING && !cell.getStringCellValue().trim().isEmpty() ) {
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
            // check if region is added or modified
            if ( isNewRegion( uploadedRegion, validationObject.getUpload().getRegions() ) ) {
                validationObject.setNumberOfRegionsAdded( validationObject.getNumberOfRegionsAdded() + 1 );
                uploadedRegion.setRegionAdded( true );
                validationObject.getUpload().getRegions().add( uploadedRegion );
            } else {
                updateUploadValidationWithModifiedRegion( uploadedRegion, validationObject );
            }
            uploadedRegions.add( uploadedRegion );
        }
        markDeletedRegions( uploadedRegions, validationObject );
    }


    void parseBranches( XSSFWorkbook workBook, UploadValidation validationObject ) throws InvalidInputException
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
                            && !String.valueOf( cell.getNumericCellValue() ).trim().isEmpty() ) {
                            uploadedBranch.setSourceBranchId( String.valueOf( cell.getNumericCellValue() ).trim() );
                        } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setSourceBranchId( cell.getStringCellValue().trim() );
                        }
                    } else if ( cellIndex == BRANCH_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedBranch.setBranchName( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == BRANCH_REGION_ID_INDEX ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                            && !String.valueOf( cell.getNumericCellValue() ).trim().isEmpty() ) {
                            uploadedBranch.setSourceRegionId( String.valueOf( cell.getNumericCellValue() ).trim() );
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
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_STRING && !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedBranch.setBranchZipcode( cell.getStringCellValue().trim() );
                        } else if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                            && !String.valueOf( cell.getNumericCellValue() ).trim().isEmpty() ) {
                            uploadedBranch.setBranchZipcode( String.valueOf( (int) cell.getNumericCellValue() ).trim() );
                        }
                    }
                }
            }
            if ( isBranchUploadEmpty( uploadedBranch ) ) {
                continue;
            }
            // check if branch is added or modified
            if ( isNewBranch( uploadedBranch, validationObject.getUpload().getBranches() ) ) {
                validationObject.setNumberOfBranchesAdded( validationObject.getNumberOfBranchesAdded() + 1 );
                uploadedBranch.setBranchAdded( true );
                validationObject.getUpload().getBranches().add( uploadedBranch );
            } else {
                updateUploadValidationWithModifiedBranch( uploadedBranch, validationObject );
            }
            uploadedBranches.add( uploadedBranch );
        }
        markDeletedBranches( uploadedBranches, validationObject );
    }


    void parseUsers( XSSFWorkbook workBook, UploadValidation validationObject ) throws InvalidInputException
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
                    if ( cellIndex == USER_ID_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedUser.setSourceUserId( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == USER_FIRST_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedUser.setFirstName( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == USER_LAST_NAME_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedUser.setLastName( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == USER_TITLE_INDEX && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedUser.setTitle( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == USER_BRANCH_ID_INDEX
                        && !cell.getStringCellValue().replaceAll( " ", "" ).isEmpty() ) {
                        //set assigned branches instead
                        //uploadedUser.setSourceBranchId( cell.getStringCellValue().trim() );
                        uploadedUser.setAssignedBranches(
                            Arrays.asList( cell.getStringCellValue().replaceAll( " ", "" ).split( "\\s*,\\s*" ) ) );
                    } else if ( cellIndex == USER_REGION_ID_INDEX
                        && !cell.getStringCellValue().replaceAll( " ", "" ).isEmpty() ) {
                        //set assigned regions instead
                        //uploadedUser.setSourceRegionId( cell.getStringCellValue().trim() );
                        uploadedUser.setAssignedRegions(
                            Arrays.asList( cell.getStringCellValue().replaceAll( " ", "" ).split( "\\s*,\\s*" ) ) );
                    } else if ( cellIndex == USER_BRANCH_ID_ADMIN_INDEX
                        && !cell.getStringCellValue().replaceAll( " ", "" ).isEmpty() ) {
                        uploadedUser.setAssignedBranchesAdmin(
                            Arrays.asList( cell.getStringCellValue().replaceAll( " ", "" ).split( "\\s*,\\s*" ) ) );
                    } else if ( cellIndex == USER_REGION_ID_ADMIN_INDEX
                        && !cell.getStringCellValue().replaceAll( " ", "" ).isEmpty() ) {
                        uploadedUser.setAssignedRegionsAdmin(
                            Arrays.asList( cell.getStringCellValue().replaceAll( " ", "" ).split( "\\s*,\\s*" ) ) );
                    } else if ( cellIndex == USER_EMAIL_INDEX ) {
                        String emailId = cell.getStringCellValue().trim();
                        if ( emailId != null && !emailId.isEmpty() ) {
                            //Masking shouldn't happen at this stage
                            /*if ( CommonConstants.YES_STRING.equals( maskEmail ) ) {
                                emailId = utils.maskEmailAddress( emailId );
                                if ( emailId != null ) {
                                    uploadedUser.setEmailId( uploadedUser.getFirstName()
                                        + ( uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "" ) + " <"
                                        + emailId + ">" );
                                }
                            } else {
                                uploadedUser.setEmailId( uploadedUser.getFirstName()
                                    + ( uploadedUser.getLastName() != null ? " " + uploadedUser.getLastName() : "" ) + " <"
                                    + emailId + ">" );
                            }*/
                            uploadedUser.setEmailId( emailId );
                        }
                    } else if ( cellIndex == USER_PHOTO_PROFILE_URL && !cell.getStringCellValue().trim().isEmpty() ) {
                        uploadedUser.setUserPhotoUrl( cell.getStringCellValue().trim() );
                    } else if ( cellIndex == USER_PHONE_NUMBER ) {
                        if ( cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC
                            && !String.valueOf( (long) cell.getNumericCellValue() ).isEmpty() ) {
                            uploadedUser.setPhoneNumber( String.valueOf( (long) cell.getNumericCellValue() ) );
                        } else if ( !cell.getStringCellValue().trim().isEmpty() ) {
                            uploadedUser.setPhoneNumber( cell.getStringCellValue().trim() );
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

                updateUploadValidationWithModifiedUser( uploadedUser, validationObject );
            }
            uploadedUsers.add( uploadedUser );
        }
        markDeletedUsers( uploadedUsers, validationObject );
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


    boolean isNewRegion( RegionUploadVO uploadedRegion, List<RegionUploadVO> uploadedRegions )
    {
        if ( uploadedRegions != null && !uploadedRegions.contains( uploadedRegion ) ) {
            return true;
        } else if ( uploadedRegion.getSourceRegionId() == null || uploadedRegion.getSourceRegionId().isEmpty() ) {
            return true;
        } else {
            return false;
        }
    }


    boolean isNewBranch( BranchUploadVO uploadedBranch, List<BranchUploadVO> uploadedBranches )
    {
        if ( uploadedBranches != null && !uploadedBranches.contains( uploadedBranch ) ) {
            return true;
        } else if ( uploadedBranch.getSourceBranchId() == null || uploadedBranch.getSourceBranchId().isEmpty() ) {
            return true;
        } else {
            return false;
        }
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


    void updateUploadValidationWithModifiedRegion( RegionUploadVO uploadedRegion, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getRegions().isEmpty() ) {
            for ( RegionUploadVO region : validationObject.getUpload().getRegions() ) {
                if ( !region.isRegionAdded()
                    && region.getSourceRegionId().equalsIgnoreCase( uploadedRegion.getSourceRegionId() ) ) {
                    if ( ( region.getRegionName() != null && uploadedRegion.getRegionName() == null )
                        || ( region.getRegionName() == null && uploadedRegion.getRegionName() != null )
                        || ( region.getRegionName() != null && uploadedRegion.getRegionName() != null
                            && !region.getRegionName().equalsIgnoreCase( uploadedRegion.getRegionName() ) ) ) {
                        region.setRegionName( uploadedRegion.getRegionName() );
                        region.setRegionNameModified( true );
                    }
                    if ( ( region.getRegionAddress1() != null && uploadedRegion.getRegionAddress1() == null )
                        || ( region.getRegionAddress1() == null && uploadedRegion.getRegionAddress1() != null )
                        || ( region.getRegionAddress1() != null && uploadedRegion.getRegionAddress1() != null
                            && !region.getRegionAddress1().equalsIgnoreCase( uploadedRegion.getRegionAddress1() ) ) ) {
                        region.setRegionAddress1( uploadedRegion.getRegionAddress1() );
                        region.setRegionAddress1Modified( true );
                    }
                    if ( ( region.getRegionAddress2() != null && uploadedRegion.getRegionAddress2() == null )
                        || ( region.getRegionAddress2() == null && uploadedRegion.getRegionAddress2() != null )
                        || ( region.getRegionAddress2() != null && uploadedRegion.getRegionAddress2() != null
                            && !region.getRegionAddress2().equalsIgnoreCase( uploadedRegion.getRegionAddress2() ) ) ) {
                        region.setRegionAddress2( uploadedRegion.getRegionAddress2() );
                        region.setRegionAddress2Modified( true );
                    }
                    if ( ( region.getRegionCity() != null && uploadedRegion.getRegionCity() == null )
                        || ( region.getRegionCity() == null && uploadedRegion.getRegionCity() != null )
                        || ( region.getRegionCity() != null && uploadedRegion.getRegionCity() != null
                            && !region.getRegionCity().equalsIgnoreCase( uploadedRegion.getRegionCity() ) ) ) {
                        region.setRegionCity( uploadedRegion.getRegionCity() );
                        region.setRegionCityModified( true );
                    }
                    if ( ( region.getRegionState() != null && uploadedRegion.getRegionState() == null )
                        || ( region.getRegionState() == null && uploadedRegion.getRegionState() != null )
                        || ( region.getRegionState() != null && uploadedRegion.getRegionState() != null
                            && !region.getRegionState().equalsIgnoreCase( uploadedRegion.getRegionState() ) ) ) {
                        region.setRegionState( uploadedRegion.getRegionState() );
                        region.setRegionStateModified( true );
                    }
                    if ( ( region.getRegionZipcode() != null && uploadedRegion.getRegionZipcode() == null )
                        || ( region.getRegionZipcode() == null && uploadedRegion.getRegionZipcode() != null )
                        || ( region.getRegionZipcode() != null && uploadedRegion.getRegionZipcode() != null
                            && !region.getRegionZipcode().equalsIgnoreCase( uploadedRegion.getRegionZipcode() ) ) ) {
                        region.setRegionZipcode( uploadedRegion.getRegionZipcode() );
                        region.setRegionZipcodeModified( true );
                    }
                    if ( region.isRegionNameModified() || region.isRegionAddress1Modified() || region.isRegionAddress2Modified()
                        || region.isRegionCityModified() || region.isRegionStateModified()
                        || region.isRegionZipcodeModified() ) {
                        validationObject.setNumberOfRegionsModified( validationObject.getNumberOfRegionsModified() + 1 );
                        region.setRegionModified( true );
                    }
                    //Set new row number for the region
                    region.setRowNum( uploadedRegion.getRowNum() );
                }
            }
        }
    }


    void updateUploadValidationWithModifiedBranch( BranchUploadVO uploadedBranch, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getBranches().isEmpty() ) {
            for ( BranchUploadVO branch : validationObject.getUpload().getBranches() ) {
                if ( !branch.isBranchAdded()
                    && branch.getSourceBranchId().equalsIgnoreCase( uploadedBranch.getSourceBranchId() ) ) {
                    if ( ( branch.getBranchName() == null && uploadedBranch.getBranchName() != null )
                        || ( branch.getBranchName() != null && uploadedBranch.getBranchName() == null )
                        || ( branch.getBranchName() != null && uploadedBranch.getBranchName() != null
                            && !branch.getBranchName().equalsIgnoreCase( uploadedBranch.getBranchName() ) ) ) {
                        branch.setBranchName( uploadedBranch.getBranchName() );
                        branch.setBranchNameModified( true );
                    }
                    if ( ( branch.getBranchAddress1() == null && uploadedBranch.getBranchAddress1() != null )
                        || ( branch.getBranchAddress1() != null && uploadedBranch.getBranchAddress1() == null )
                        || ( branch.getBranchAddress1() != null && uploadedBranch.getBranchAddress1() != null
                            && !branch.getBranchAddress1().equalsIgnoreCase( uploadedBranch.getBranchAddress1() ) ) ) {
                        branch.setBranchAddress1( uploadedBranch.getBranchAddress1() );
                        branch.setBranchAddress1Modified( true );
                    }
                    if ( branch.getBranchAddress1() != null && !branch.getBranchAddress1().isEmpty() ) {
                        branch.setAddressSet( true );
                    }
                    if ( ( branch.getBranchAddress2() == null && uploadedBranch.getBranchAddress2() != null )
                        || ( branch.getBranchAddress2() != null && uploadedBranch.getBranchAddress2() == null )
                        || ( branch.getBranchAddress2() != null && uploadedBranch.getBranchAddress2() != null
                            && !branch.getBranchAddress2().equalsIgnoreCase( uploadedBranch.getBranchAddress2() ) ) ) {
                        branch.setBranchAddress2( uploadedBranch.getBranchAddress2() );
                        branch.setBranchAddress2Modified( true );
                    }
                    if ( ( branch.getBranchCity() == null && uploadedBranch.getBranchCity() != null )
                        || ( branch.getBranchCity() != null && uploadedBranch.getBranchCity() == null )
                        || ( branch.getBranchCity() != null && uploadedBranch.getBranchCity() != null
                            && !branch.getBranchCity().equalsIgnoreCase( uploadedBranch.getBranchCity() ) ) ) {
                        branch.setBranchCity( uploadedBranch.getBranchCity() );
                        branch.setBranchCityModified( true );
                    }
                    if ( ( branch.getBranchState() == null && uploadedBranch.getBranchState() != null )
                        || ( branch.getBranchState() != null && uploadedBranch.getBranchState() == null )
                        || ( branch.getBranchState() != null && uploadedBranch.getBranchState() != null
                            && !branch.getBranchState().equalsIgnoreCase( uploadedBranch.getBranchState() ) ) ) {
                        branch.setBranchState( uploadedBranch.getBranchState() );
                        branch.setBranchStateModified( true );
                    }
                    if ( ( branch.getBranchZipcode() == null && uploadedBranch.getBranchZipcode() != null )
                        || ( branch.getBranchZipcode() != null && uploadedBranch.getBranchZipcode() == null )
                        || ( branch.getBranchZipcode() != null && uploadedBranch.getBranchZipcode() != null
                            && !branch.getBranchZipcode().equalsIgnoreCase( uploadedBranch.getBranchZipcode() ) ) ) {
                        branch.setBranchZipcode( uploadedBranch.getBranchZipcode() );
                        branch.setBranchZipcodeModified( true );
                    }
                    if ( ( branch.getSourceRegionId() == null && uploadedBranch.getSourceRegionId() != null )
                        || ( branch.getSourceRegionId() != null && uploadedBranch.getSourceRegionId() == null )
                        || ( branch.getSourceRegionId() != null && uploadedBranch.getSourceRegionId() != null
                            && !branch.getSourceRegionId().equalsIgnoreCase( uploadedBranch.getSourceRegionId() ) ) ) {
                        branch.setSourceRegionId( uploadedBranch.getSourceRegionId() );
                        branch.setSourceRegionIdModified( true );
                    }
                    if ( branch.isBranchNameModified() || branch.isBranchAddress1Modified() || branch.isBranchAddress2Modified()
                        || branch.isBranchCityModified() || branch.isBranchStateModified() || branch.isBranchZipcodeModified()
                        || branch.isSourceRegionIdModified() ) {
                        validationObject.setNumberOfBranchesModified( validationObject.getNumberOfBranchesModified() + 1 );
                        branch.setBranchModified( true );
                    }
                    //Set new row number for the branch
                    branch.setRowNum( uploadedBranch.getRowNum() );
                }
            }
        }
    }


    void updateUploadValidationWithModifiedUser( UserUploadVO uploadedUser, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getUsers() != null
            && !validationObject.getUpload().getUsers().isEmpty() ) {
            for ( UserUploadVO user : validationObject.getUpload().getUsers() ) {
                if ( !user.isUserAdded() && user.getSourceUserId().equalsIgnoreCase( uploadedUser.getSourceUserId() ) ) {
                    if ( ( user.getFirstName() == null && uploadedUser.getFirstName() != null )
                        || ( user.getFirstName() != null && uploadedUser.getFirstName() == null )
                        || ( user.getFirstName() != null && uploadedUser.getFirstName() != null
                            && !user.getFirstName().equalsIgnoreCase( uploadedUser.getFirstName() ) ) ) {
                        user.setFirstName( uploadedUser.getFirstName() );
                        user.setFirstNameModified( true );
                    }
                    if ( ( user.getLastName() == null && uploadedUser.getLastName() != null )
                        || ( user.getLastName() != null && uploadedUser.getLastName() == null )
                        || ( user.getLastName() != null && uploadedUser.getLastName() != null
                            && !user.getLastName().equalsIgnoreCase( uploadedUser.getLastName() ) ) ) {
                        user.setLastName( uploadedUser.getLastName() );
                        user.setLastNameModified( true );
                    }
                    if ( ( user.getTitle() == null && uploadedUser.getTitle() != null )
                        || ( user.getTitle() != null && uploadedUser.getTitle() == null )
                        || ( user.getTitle() != null && uploadedUser.getTitle() != null
                            && !user.getTitle().equalsIgnoreCase( uploadedUser.getTitle() ) ) ) {
                        user.setTitle( uploadedUser.getTitle() );
                        user.setTitleModified( true );
                    }
                    /*if ( ( user.getSourceRegionId() == null && uploadedUser.getSourceRegionId() != null )
                        || ( user.getSourceRegionId() != null && uploadedUser.getSourceRegionId() == null )
                        || ( user.getSourceRegionId() != null && uploadedUser.getSourceRegionId() != null && !user
                            .getSourceRegionId().equalsIgnoreCase( uploadedUser.getSourceRegionId() ) ) ) {
                        user.setSourceRegionId( uploadedUser.getSourceRegionId() );
                        user.setSourceRegionIdModified( true );
                    }
                    if ( ( user.getSourceBranchId() == null && uploadedUser.getSourceBranchId() != null )
                        || ( user.getSourceBranchId() != null && uploadedUser.getSourceBranchId() == null )
                        || ( user.getSourceBranchId() != null && uploadedUser.getSourceBranchId() != null && !user
                            .getSourceBranchId().equalsIgnoreCase( uploadedUser.getSourceBranchId() ) ) ) {
                        user.setSourceBranchId( uploadedUser.getSourceBranchId() );
                        user.setSourceBranchIdModified( true );
                    }*/
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
                        user.setAssignedBranchesAdmin( uploadedUser.getAssignedBranchesAdmin() );
                        user.setAssignedBrachesAdminModified( true );
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
                        user.setAssignedRegionsAdmin( uploadedUser.getAssignedRegionsAdmin() );
                        user.setAssignedRegionsAdminModified( true );
                    }
                    if ( ( ( user.getAssignedBranches() != null && !user.getAssignedBranches().isEmpty() )
                        && ( uploadedUser.getAssignedBranches() == null || uploadedUser.getAssignedBranches().isEmpty() ) )
                        || ( ( user.getAssignedBranches() == null || user.getAssignedBranches().isEmpty() )
                            && ( uploadedUser.getAssignedBranches() != null && !uploadedUser.getAssignedBranches().isEmpty() ) )
                        || ( user.getAssignedBranches() != null && uploadedUser.getAssignedBranches() != null
                            && !( uploadedUser.getAssignedBranches().containsAll( user.getAssignedBranches() )
                                && user.getAssignedBranches().containsAll( uploadedUser.getAssignedBranches() ) ) ) ) {
                        user.setAssignedBranches( uploadedUser.getAssignedBranches() );
                        user.setAssignedBranchesModified( true );
                    }
                    if ( ( ( user.getAssignedRegions() != null && !user.getAssignedRegions().isEmpty() )
                        && ( uploadedUser.getAssignedRegions() == null || uploadedUser.getAssignedRegions().isEmpty() ) )
                        || ( ( user.getAssignedRegions() == null || user.getAssignedRegions().isEmpty() )
                            && ( uploadedUser.getAssignedRegions() != null && !uploadedUser.getAssignedRegions().isEmpty() ) )
                        || ( user.getAssignedRegions() != null && uploadedUser.getAssignedRegions() != null
                            && !( user.getAssignedRegions().containsAll( uploadedUser.getAssignedRegions() )
                                && uploadedUser.getAssignedRegions().containsAll( user.getAssignedRegions() ) ) ) ) {
                        user.setAssignedRegions( uploadedUser.getAssignedRegions() );
                        user.setAssignedRegionsModified( true );
                    }
                    if ( ( user.getEmailId() != null && uploadedUser.getEmailId() == null )
                        || ( user.getEmailId() == null && uploadedUser.getEmailId() != null )
                        || ( user.getEmailId() != null && uploadedUser.getEmailId() != null
                            && !user.getEmailId().equalsIgnoreCase( uploadedUser.getEmailId() ) ) ) {
                        user.setEmailId( uploadedUser.getEmailId() );
                        user.setEmailIdModified( true );
                    }
                    if ( ( user.getPhoneNumber() != null && uploadedUser.getPhoneNumber() == null )
                        || ( user.getPhoneNumber() == null && uploadedUser.getPhoneNumber() != null )
                        || ( user.getPhoneNumber() != null && uploadedUser.getPhoneNumber() != null
                            && !user.getPhoneNumber().equalsIgnoreCase( uploadedUser.getPhoneNumber() ) ) ) {
                        user.setPhoneNumber( uploadedUser.getPhoneNumber() );
                        user.setPhoneNumberModified( true );
                    }
                    if ( ( user.getWebsiteUrl() != null && uploadedUser.getWebsiteUrl() == null )
                        || ( user.getWebsiteUrl() == null && uploadedUser.getWebsiteUrl() != null )
                        || ( user.getWebsiteUrl() != null && uploadedUser.getWebsiteUrl() != null
                            && !user.getWebsiteUrl().equalsIgnoreCase( uploadedUser.getWebsiteUrl() ) ) ) {
                        user.setWebsiteUrl( uploadedUser.getWebsiteUrl() );
                        user.setWebsiteUrlModified( true );
                    }
                    if ( ( user.getLicense() != null && uploadedUser.getLicense() == null )
                        || ( user.getLicense() == null && uploadedUser.getLicense() != null )
                        || ( user.getLicense() != null && uploadedUser.getLicense() != null
                            && !user.getLicense().equalsIgnoreCase( uploadedUser.getLicense() ) ) ) {
                        user.setLicense( uploadedUser.getLicense() );
                        user.setLicenseModified( true );
                    }
                    if ( ( user.getLegalDisclaimer() != null && uploadedUser.getLegalDisclaimer() == null )
                        || ( user.getLegalDisclaimer() == null && uploadedUser.getLegalDisclaimer() != null )
                        || ( user.getLegalDisclaimer() != null && uploadedUser.getLegalDisclaimer() != null
                            && !user.getLegalDisclaimer().equalsIgnoreCase( uploadedUser.getLegalDisclaimer() ) ) ) {
                        user.setLegalDisclaimer( uploadedUser.getLegalDisclaimer() );
                        user.setLegalDisclaimerModified( true );
                    }
                    if ( ( user.getUserPhotoUrl() != null && uploadedUser.getUserPhotoUrl() == null )
                        || ( user.getUserPhotoUrl() == null && uploadedUser.getUserPhotoUrl() != null )
                        || ( user.getUserPhotoUrl() != null && uploadedUser.getUserPhotoUrl() != null
                            && !user.getUserPhotoUrl().equalsIgnoreCase( uploadedUser.getUserPhotoUrl() ) ) ) {
                        user.setUserPhotoUrl( uploadedUser.getUserPhotoUrl() );
                        user.setUserPhotoUrlModified( true );
                    }
                    if ( ( user.getAboutMeDescription() != null && uploadedUser.getAboutMeDescription() == null )
                        || ( user.getAboutMeDescription() == null && uploadedUser.getAboutMeDescription() != null )
                        || ( user.getAboutMeDescription() != null && uploadedUser.getAboutMeDescription() != null
                            && !user.getAboutMeDescription().equalsIgnoreCase( uploadedUser.getAboutMeDescription() ) ) ) {
                        user.setAboutMeDescription( uploadedUser.getAboutMeDescription() );
                        user.setAboutMeDescriptionModified( true );
                    }
                    if ( user.isFirstNameModified() || user.isLastNameModified() || user.isTitleModified()
                        || user.isAssignedBranchesModified() || user.isAssignedRegionsModified()
                        || user.isAssignedBrachesAdminModified() || user.isAssignedRegionsAdminModified()
                        || user.isEmailIdModified() || user.isPhoneNumberModified() || user.isWebsiteUrlModified()
                        || user.isLicenseModified() || user.isLegalDisclaimerModified() || user.isUserPhotoUrlModified()
                        || user.isAboutMeDescriptionModified() ) {
                        validationObject.setNumberOfUsersModified( validationObject.getNumberOfUsersModified() + 1 );
                        user.setUserModified( true );
                    }
                    //Set new row number for the user
                    user.setRowNum( uploadedUser.getRowNum() );
                }
            }
        }
    }


    void markDeletedRegions( List<RegionUploadVO> uploadedRegions, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getRegions() != null
            && !validationObject.getUpload().getRegions().isEmpty() ) {
            for ( RegionUploadVO region : validationObject.getUpload().getRegions() ) {
                if ( region.getSourceRegionId() != null && !region.getSourceRegionId().isEmpty()
                    && !uploadedRegions.contains( region ) ) {
                    region.setDeletedRecord( true );
                }
            }
        }
    }


    void markDeletedBranches( List<BranchUploadVO> uploadedBranches, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getBranches() != null
            && !validationObject.getUpload().getBranches().isEmpty() ) {
            for ( BranchUploadVO branch : validationObject.getUpload().getBranches() ) {
                if ( branch.getSourceBranchId() != null && !branch.getSourceBranchId().isEmpty()
                    && !uploadedBranches.contains( branch ) ) {
                    branch.setDeletedRecord( true );
                }
            }
        }
    }


    void markDeletedUsers( List<UserUploadVO> uploadeUsers, UploadValidation validationObject )
    {
        if ( validationObject.getUpload() != null && validationObject.getUpload().getUsers() != null
            && !validationObject.getUpload().getUsers().isEmpty() ) {
            for ( UserUploadVO user : validationObject.getUpload().getUsers() ) {
                if ( user.getSourceUserId() != null && !user.getSourceUserId().isEmpty() && !uploadeUsers.contains( user ) ) {
                    user.setDeletedRecord( true );
                    validationObject.setNumberOfUsersDeleted( validationObject.getNumberOfUsersDeleted() + 1 );
                }
            }
        }
    }


    boolean isUserUploadEmpty( UserUploadVO user )
    {
        if ( ( user.getSourceUserId() == null || user.getSourceUserId().isEmpty() )
            && ( user.getFirstName() == null || user.getSourceUserId().isEmpty() )
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
}
