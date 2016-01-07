package com.realtech.socialsurvey.core.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.DashboardService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.upload.CsvUploadService;


@Component
public class PrepareBillingReport implements Runnable
{
    public static final Logger LOG = LoggerFactory.getLogger( PrepareBillingReport.class );

    @Value ( "${BATCH_SIZE}")
    private int batchSize;

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private CsvUploadService csvUploadService;

    @Autowired
    private EmailServices emailServices;

    @Value ( "${FILE_DIRECTORY_LOCATION}")
    private String fileDirectoryLocation;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String adminEmailId;

    @Value ( "${APPLICATION_ADMIN_NAME}")
    private String adminName;

    private long previousCompanyId = 0l;
    private long previousRegionId = 0l;
    private long previousBranchId = 0l;
    private String currentState = "";
    private String recipientMailId = "";


    @Override
    public void run()
    {
        LOG.info( "Started method to prepare billing report" );
        // Check if a request for billing report is present in FILE_UPLOAD table
        while ( true ) {
            previousCompanyId = 0l;
            previousRegionId = 0l;
            previousBranchId = 0l;
            try {
                List<FileUpload> filesToBeUploaded = dashboardService.getBillingReportToBeSent();
                if ( filesToBeUploaded != null && !( filesToBeUploaded.isEmpty() ) ) {
                    FileUpload fileUpload = filesToBeUploaded.get( 0 );
                    //FileName stores the recipient mail ID
                    recipientMailId = fileUpload.getFileName();

                    try {
                        // update the status to be processing
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        fileUpload.setStatus( CommonConstants.STATUS_UNDER_PROCESSING );
                        csvUploadService.updateFileUploadRecord( fileUpload );

                        // prepare and send the billing report to admin
                        prepareAndSendBillingReport();

                        // update the status to be processed
                        fileUpload.setStatus( CommonConstants.STATUS_INACTIVE );
                        fileUpload.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
                        csvUploadService.updateFileUploadRecord( fileUpload );
                    } catch ( InvalidInputException e ) {
                        LOG.debug( "Error updating the status" );
                        continue;
                    }
                }
            } catch ( NoRecordsFetchedException e ) {
                LOG.debug( "No files to be uploaded. Sleep for a minute" );
                try {
                    Thread.sleep( 1000 * 60 );
                } catch ( InterruptedException e1 ) {
                    LOG.warn( "Thread interrupted" );
                    break;
                }
            }
        }
    }


    /**
     * Method to prepare and mail billing report to admin
     */
    void prepareAndSendBillingReport()
    {
        int startIndex = 0;

        XSSFWorkbook workbook = new XSSFWorkbook();

        // Create a blank sheet
        XSSFSheet sheet = workbook.createSheet();
        Integer counter = 1;

        // This data needs to be written (List<Object>)
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> billingReportToPopulate = new ArrayList<>();

        // Store records for each batch here
        List<BillingReportData> records = null;

        // tracking variables
        String previousCompanyAddress = null;
        String previousRegionAddress = null;
        String previousBranchAddress = null;

        // Get records batch-wise and add to reportDataObject
        do {
            records = dashboardService.getBillingReportRecords( startIndex, batchSize );
            if ( records != null ) {

                // start populating the data
                for ( BillingReportData reportRow : records ) {
                    //Initialize current state for every user
                    currentState = "";
                    // Populate Company Name in the sheet
                    billingReportToPopulate.add( reportRow.getCompany() );
                    // Populate Region name in the sheet
                    if ( reportRow.getRegion().equalsIgnoreCase( CommonConstants.DEFAULT_REGION_NAME ) ) {
                        billingReportToPopulate.add( "" );
                    } else {
                        billingReportToPopulate.add( reportRow.getRegion() );
                    }
                    // Populate Branch name in the sheet
                    if ( reportRow.getBranch().equalsIgnoreCase( CommonConstants.DEFAULT_BRANCH_NAME ) ) {
                        billingReportToPopulate.add( "" );
                    } else {
                        billingReportToPopulate.add( reportRow.getBranch() );
                    }
                    // Populate First Name in the sheet
                    billingReportToPopulate.add( reportRow.getFirstName() );

                    // Populate Last Name in the sheet
                    if ( reportRow.getLastName() == null || reportRow.getLastName().isEmpty()
                        || reportRow.getLastName().equalsIgnoreCase( "null" ) ) {
                        billingReportToPopulate.add( "" );
                    } else {
                        billingReportToPopulate.add( reportRow.getLastName() );
                    }

                    // Populate Login ID in the sheet
                    billingReportToPopulate.add( reportRow.getLoginName() );

                    boolean isAgent = isUserAnAgent( reportRow.getProfilesMasterIds() );

                    AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( reportRow.getUserId() );

                    if ( agentSettings == null ) {
                        LOG.error( "Agent profile null for user ID : " + reportRow.getUserId() );
                        continue;
                    }

                    // Populate public profile page url in the sheet
                    if ( isAgent ) {
                        if ( agentSettings.getCompleteProfileUrl() == null || agentSettings.getCompleteProfileUrl().isEmpty() ) {
                            LOG.error( "Agent profile url is empty for agentID : " + agentSettings.getIden() );
                            billingReportToPopulate.add( "NA" );
                        } else {
                            billingReportToPopulate.add( agentSettings.getCompleteProfileUrl() );
                        }
                        billingReportToPopulate.add( CommonConstants.YES_STRING );
                    } else {
                        billingReportToPopulate.add( "" );
                        billingReportToPopulate.add( CommonConstants.NO_STRING );
                    }

                    // Populate address in the sheet
                    try {
                        billingReportToPopulate.add( getAddress( agentSettings, reportRow, previousCompanyAddress,
                            previousRegionAddress, previousBranchAddress ) );

                        // Populate state in the sheet
                        //DO NOT DELETE! THIS IS A REQUIREMENT.
                        billingReportToPopulate.add( currentState );
                    } catch ( InvalidInputException | NoRecordsFetchedException e ) {
                        LOG.error( "An error occured while fetching the address for the user. Reason : ", e );
                    }

                    data.put( ( ++counter ).toString(), billingReportToPopulate );
                    billingReportToPopulate = new ArrayList<>();
                }
            }
            startIndex += batchSize;
        } while ( records != null );

        // Setting up headers for the sheet
        billingReportToPopulate.add( CommonConstants.HEADER_COMPANY );
        billingReportToPopulate.add( CommonConstants.HEADER_REGION );
        billingReportToPopulate.add( CommonConstants.HEADER_BRANCH );
        billingReportToPopulate.add( CommonConstants.HEADER_FIRST_NAME );
        billingReportToPopulate.add( CommonConstants.HEADER_LAST_NAME );
        billingReportToPopulate.add( CommonConstants.HEADER_LOGIN_ID );
        billingReportToPopulate.add( CommonConstants.HEADER_PUBLIC_PROFILE_URL );
        billingReportToPopulate.add( CommonConstants.HEADER_IS_AGENT );
        billingReportToPopulate.add( CommonConstants.HEADER_ADDRESS );
        billingReportToPopulate.add( CommonConstants.HEADER_STATE );

        data.put( "1", billingReportToPopulate );

        // Iterate over data and write to sheet
        DecimalFormat decimalFormat = new DecimalFormat( "#0" );
        decimalFormat.setRoundingMode( RoundingMode.DOWN );

        int rownum = 0;
        for ( int i = 1; i <= data.size(); i++ ) {
            String key = String.valueOf( i );
            Row row = sheet.createRow( rownum++ );
            List<Object> objArr = data.get( key );
            int cellnum = 0;
            for ( Object obj : objArr ) {
                Cell cell = row.createCell( cellnum++ );
                if ( obj instanceof String )
                    cell.setCellValue( (String) obj );
                else if ( obj instanceof Integer )
                    cell.setCellValue( (Integer) obj );
                else if ( obj instanceof Double )
                    cell.setCellValue( decimalFormat.format( obj ) );
                else if ( obj instanceof Long )
                    cell.setCellValue( (Long) obj );
                else if ( obj instanceof Boolean )
                    cell.setCellValue( (Boolean) obj );
            }
        }
        // Create file and write report into it
        boolean excelCreated = false;
        String fileName = "Billing_Report-" + ( new Timestamp( new Date().getTime() ) );
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

        // Mail the report to the admin
        try {
            if ( excelCreated ) {
                Map<String, String> attachmentsDetails = new HashMap<String, String>();
                attachmentsDetails.put( fileName + ".xls", filePath );
                String mailId = null;
                if ( recipientMailId == null || recipientMailId.isEmpty() ) {
                    mailId = adminEmailId;
                } else {
                    mailId = recipientMailId;
                }
                emailServices.sendBillingReportMail( adminName, "", mailId, attachmentsDetails );
            }
        } catch ( InvalidInputException | UndeliveredEmailException e ) {
            LOG.error( "Exception caught in sendCorruptDataFromCrmNotificationMail() while sending mail to company admin" );
        }
    }


    /**
     * Method to check if user is agent or not
     * 
     * @param userId
     * @throws InvalidInputException
     */
    boolean isUserAnAgent( List<Long> profilesMasters )
    {
        for ( Long profilesMaster : profilesMasters ) {
            if ( profilesMaster == CommonConstants.PROFILES_MASTER_AGENT_PROFILE_ID ) {
                return true;
            }
        }
        return false;
    }


    /**
     * Method to get the user's address
     * 
     * @param agentSettings
     * @param reportRow
     * @param previousCompanyAddress
     * @param previousRegionAddress
     * @param previousBranchAddress
     * @return
     * @throws InvalidInputException
     * @throws NoRecordsFetchedException
     */
    String getAddress( AgentSettings agentSettings, BillingReportData reportRow, String previousCompanyAddress,
        String previousRegionAddress, String previousBranchAddress ) throws InvalidInputException, NoRecordsFetchedException
    {
        LOG.debug( "Getting address for user." );
        String address = null;
        boolean fetchAddressFromRegion = false;
        boolean fetchAddressFromCompany = false;
        //If agent has address, return it
        if ( agentSettings.getContact_details() != null && agentSettings.getContact_details().getAddress() != null
            && !agentSettings.getContact_details().getAddress().isEmpty() ) {
            address = getAddressFromContactDetails( agentSettings.getContact_details() );
        } else {
            // get the address from branch
            if ( !reportRow.getBranch().equalsIgnoreCase( CommonConstants.DEFAULT_BRANCH_NAME ) ) {
                //If branch ID has changed
                if ( reportRow.getBranchId() != previousBranchId || previousBranchAddress == null ) {
                    previousBranchId = reportRow.getBranchId();
                    //Get branch settings and check if branch has address
                    OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault( reportRow
                        .getBranchId() );
                    if ( branchSettings.getContact_details() != null
                        && branchSettings.getContact_details().getAddress() != null
                        && !branchSettings.getContact_details().getAddress().isEmpty() ) {
                        address = getAddressFromContactDetails( branchSettings.getContact_details() );
                        previousBranchAddress = address;
                    } else {
                        fetchAddressFromRegion = true;
                    }
                } else {
                    address = previousBranchAddress;
                }
            } else {
                // get address from region
                fetchAddressFromRegion = true;
            }
            if ( fetchAddressFromRegion ) {
                if ( !reportRow.getRegion().equalsIgnoreCase( CommonConstants.DEFAULT_REGION_NAME ) ) {
                    //Check if region ID has changed
                    if ( reportRow.getRegionId() != previousRegionId || previousRegionAddress == null ) {
                        previousRegionId = reportRow.getRegionId();
                        OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings( reportRow
                            .getRegionId() );
                        //Check if region has address
                        if ( regionSettings.getContact_details() != null
                            && regionSettings.getContact_details().getAddress() != null
                            && !regionSettings.getContact_details().getAddress().isEmpty() ) {
                            address = getAddressFromContactDetails( regionSettings.getContact_details() );
                            previousRegionAddress = address;
                        } else {
                            fetchAddressFromCompany = true;
                        }
                    } else {
                        address = previousRegionAddress;
                    }

                } else {
                    fetchAddressFromCompany = true;
                }
            }
            if ( fetchAddressFromCompany ) {
                // Check if company ID has changed
                if ( reportRow.getCompanyId() != previousCompanyId || previousCompanyAddress == null ) {
                    previousCompanyId = reportRow.getCompanyId();
                    OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( reportRow
                        .getCompanyId() );
                    if ( companySettings.getContact_details() != null
                        && companySettings.getContact_details().getAddress() != null
                        && !companySettings.getContact_details().getAddress().isEmpty() ) {
                        address = getAddressFromContactDetails( companySettings.getContact_details() );
                        previousCompanyAddress = address;
                    }
                } else {
                    address = previousCompanyAddress;
                }
            }
        }
        return address;
    }


    /**
     * Method to get address string from contact details
     * @param contactDetails
     * @return
     * @throws InvalidInputException
     */
    String getAddressFromContactDetails( ContactDetailsSettings contactDetails ) throws InvalidInputException
    {
        if ( contactDetails == null ) {
            throw new InvalidInputException( "Contact details is null" );
        }
        String fullAddress = "";

        // Get Address
        if ( contactDetails.getAddress() != null && !( contactDetails.getAddress().isEmpty() ) ) {
            fullAddress += contactDetails.getAddress();
        }

        // Get City
        if ( contactDetails.getCity() != null && !( contactDetails.getCity().isEmpty() ) ) {
            fullAddress += ", " + contactDetails.getCity();
        }

        // Get State
        if ( contactDetails.getState() != null && !( contactDetails.getState().isEmpty() ) ) {
            fullAddress += ", " + contactDetails.getState();
            currentState = contactDetails.getState();
        }

        // Get Zip-code
        if ( contactDetails.getZipcode() != null && !( contactDetails.getZipcode().isEmpty() ) ) {
            fullAddress += ", " + contactDetails.getZipcode();
        }

        // Get Country-code
        if ( contactDetails.getCountryCode() != null && !( contactDetails.getCountryCode().isEmpty() ) ) {
            fullAddress += ", " + contactDetails.getCountryCode();
        }

        return fullAddress;
    }
}
