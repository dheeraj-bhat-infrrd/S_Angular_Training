package com.realtech.socialsurvey.core.services.reports.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BillingReportData;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.EmailAttachment;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.impl.DashboardServiceImpl;
import com.realtech.socialsurvey.core.services.reports.BillingReportsService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookData;
import com.realtech.socialsurvey.core.workbook.utils.WorkbookOperations;


@Component
public class BillingReportsServiceImpl implements BillingReportsService
{
    private static final Logger LOG = LoggerFactory.getLogger( DashboardServiceImpl.class );

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private FileUploadService fileUploadService;
    
    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private GenericDao<LicenseDetail, Long> licenceDetailDao;

    @Autowired
    private WorkbookOperations workbookOperations;

    @Autowired
    private WorkbookData workbookData;

    @Value ( "${BILLING_REPORT_BATCH_SIZE}")
    private int batchSize;

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


    /**
     * Method to generate data for billing report
     */
    @Override
    @Transactional
    public Map<Integer, List<Object>> generateBillingReportDataForCompanies()
    {
        LOG.debug( "Metohd generateBillingReportDataForCompanies started" );
        int startIndex = 0;
        Integer counter = 1;

        // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> billingReportToPopulate = new ArrayList<>();

        // Store records for each batch here
        List<BillingReportData> records = null;

        // tracking variables
        String previousCompanyAddress = null;
        String previousRegionAddress = null;
        String previousBranchAddress = null;

        // Get records batch-wise and add to reportDataObject
        do {
            records = companyDao.getAllUsersInCompanysForBillingReport( startIndex, batchSize );

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
                        billingReportToPopulate = new ArrayList<>();
                        continue;
                    }

                    // Populate public profile page url in the sheet
                    if ( isAgent ) {
                        if ( agentSettings.getCompleteProfileUrl() == null
                            || agentSettings.getCompleteProfileUrl().isEmpty() ) {
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

                    data.put( ++counter, billingReportToPopulate );
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

        data.put( 1, billingReportToPopulate );
        return data;
    }


    /**
     * Method to generate data for billing report
     */
    @Override
    @Transactional
    public Map<Integer, List<Object>> generateBillingReportDataForACompany( long companyId )
    {
        LOG.info( "Method generateBillingReportDataForACompany started for company : " + companyId );
        int startIndex = 0;
        Integer counter = 1;

        // This data needs to be written (List<Object>)
        Map<Integer, List<Object>> data = new TreeMap<>();
        List<Object> billingReportToPopulate = new ArrayList<>();

        // Store records for each batch here
        List<BillingReportData> records = null;

        // tracking variables
        String previousCompanyAddress = null;
        String previousRegionAddress = null;
        String previousBranchAddress = null;

        // Get records batch-wise and add to reportDataObject
        do {
            records = companyDao.getAllUsersInGivenCompaniesForBillingReport( startIndex, batchSize, companyId );

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
                        billingReportToPopulate = new ArrayList<>();
                        continue;
                    }

                    // Populate public profile page url in the sheet
                    if ( isAgent ) {
                        if ( agentSettings.getCompleteProfileUrl() == null
                            || agentSettings.getCompleteProfileUrl().isEmpty() ) {
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

                    data.put( ++counter, billingReportToPopulate );
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

        data.put( 1, billingReportToPopulate );

        LOG.info( "Method generateBillingReportDataForACompany ended for company : " + companyId );
        return data;
    }


    /**
     * Method to prepare and mail billing report to admin
     * @throws UndeliveredEmailException 
     * @throws InvalidInputException 
     */
    @Override
    @Transactional
    public void generateBillingReportAndMail( Map<Integer, List<Object>> data, String recipientMailId, String recipientName )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.info( "method generateBillingReportAndMail started" );
        DecimalFormat decimalFormat = new DecimalFormat( "#0" );
        decimalFormat.setRoundingMode( RoundingMode.DOWN );
        XSSFWorkbook workbook = workbookOperations.createWorkbook( data, decimalFormat );

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
            LOG.error( "Exception caught while generating billing report " + fe.getMessage() );
            excelCreated = false;
        } catch ( IOException e ) {
            LOG.error( "Exception caught  while generating billing report " + e.getMessage() );
            excelCreated = false;
        } finally {
            try {
                fileOutput.close();
                if ( inputStream != null ) {
                    inputStream.close();
                }
            } catch ( IOException e ) {
                LOG.error( "Exception caught  while generating billing report " + e.getMessage() );
                excelCreated = false;
            }
        }

        boolean excelUploaded = false;
        if ( excelCreated ) {
            try {
                filePath = fileUploadService.uploadOldReport( file, fileName );
                
                excelUploaded = true;
            } catch ( NonFatalException e ) {
                LOG.error( "Exception caught while uploading old report", e);
            }
            LOG.debug( "fileUpload on s3 step is done for filename : {}", fileName );
        } else {
            LOG.warn( "Could not write into file {}", fileName );
        }

        // Mail the report to the admin
        if ( excelCreated && excelUploaded) {
            List<EmailAttachment> attachments = new ArrayList<EmailAttachment>();
            attachments.add( new EmailAttachment(fileName + ".xls", filePath) );
            String mailId = null;
            if ( recipientMailId == null || recipientMailId.isEmpty() ) {
                mailId = adminEmailId;
            } else {
                mailId = recipientMailId;
            }

            String name = null;
            if ( recipientName == null || recipientName.isEmpty() ) {
                name = adminName;
            } else {
                name = recipientName;
            }

            LOG.debug( "sending mail to : " + name + " at : " + mailId );
            emailServices.sendBillingReportMail( name, "", mailId, attachments);
        }

        LOG.info( "method generateBillingReportAndMail ended" );
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
                    OrganizationUnitSettings branchSettings = organizationManagementService
                        .getBranchSettingsDefault( reportRow.getBranchId() );
                    if ( branchSettings.getContact_details() != null && branchSettings.getContact_details().getAddress() != null
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
                        OrganizationUnitSettings regionSettings = organizationManagementService
                            .getRegionSettings( reportRow.getRegionId() );
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
                    OrganizationUnitSettings companySettings = organizationManagementService
                        .getCompanySettings( reportRow.getCompanyId() );
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


    @Override
    @Transactional
    public List<Company> getCompaniesWithExpiredInvoice()
    {
        LOG.info( "Inside method getCompaniesWithExpiredInvoice" );
        return companyDao.getCompaniesWithExpiredInvoice();
    }


    @Override
    @Transactional
    public void updateNextInvoiceBillingDateInLicenceDetail( LicenseDetail licenseDetail )
    {
        LOG.info( "Method updateNextInvoiceBillingDateInLicenceDetail started" );
        // update next invoice billing date
        Calendar calender = Calendar.getInstance();
        calender.setTime( licenseDetail.getNextInvoiceBillingDate() );
        calender.add( Calendar.MONTH, licenseDetail.getInvoiceCyclePeriodInMonth() );
        calender.set( Calendar.HOUR, 0 );
        calender.set( Calendar.MINUTE, 0 );
        calender.set( Calendar.SECOND, 0 );
        calender.set( Calendar.HOUR_OF_DAY, 0 );
        Date newNextInvoiceDate = calender.getTime();
        licenseDetail.setNextInvoiceBillingDate( new java.sql.Timestamp( newNextInvoiceDate.getTime() ) );

        licenseDetail.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        licenceDetailDao.update( licenseDetail );
        LOG.info( "Method updateNextInvoiceBillingDateInLicenceDetail ended" );
    }


    @Override
    @Transactional
    public void companiesBillingReportGenerator()
    {
        List<Company> companies = getCompaniesWithExpiredInvoice();
        for ( Company company : companies ) {
            LOG.debug( "generating billing report for company : " + company.getCompany() );
            try {
                if ( company.getLicenseDetails() != null && company.getLicenseDetails().size() > 0 ) {
                    LicenseDetail licenseDetail = company.getLicenseDetails().get( 0 );
                    Map<Integer, List<Object>> data = generateBillingReportDataForACompany( company.getCompanyId() );
                    User companyAdmin = userManagementService.getCompanyAdmin( company.getCompanyId() );
                    String adminName = null;
                    if ( licenseDetail.getRecipientMailId() != null && !licenseDetail.getRecipientMailId().isEmpty() ) {
                        adminName = companyAdmin.getFirstName();
                    }
                    generateBillingReportAndMail( data, licenseDetail.getRecipientMailId(), adminName );
                    updateNextInvoiceBillingDateInLicenceDetail( licenseDetail );
                }
            } catch ( Exception e ) {
                LOG.error( "Error while generating and mailing billing report for company : " + company.getCompany() );
                try {
                    batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.COMPANIES_BILLING_REPORT_GENERATOR,
                        System.currentTimeMillis(), e );
                } catch ( InvalidInputException | UndeliveredEmailException e1 ) {
                    LOG.error( "error while sende report bug mail to admin ", e1 );
                }
                continue;
            }
        }
    }
}
