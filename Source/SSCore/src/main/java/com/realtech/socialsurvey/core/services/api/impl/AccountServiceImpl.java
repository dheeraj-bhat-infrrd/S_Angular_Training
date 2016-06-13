package com.realtech.socialsurvey.core.services.api.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.LicenseDetail;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.Plan;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.HierarchyAlreadyExistsException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.ActiveSubscriptionFoundException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;


@Service
public class AccountServiceImpl implements AccountService
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountServiceImpl.class );
    private GenericDao<VerticalsMaster, Integer> industryDao;
    private GenericDao<AccountsMaster, Integer> paymentPlanDao;
    private CompanyDao companyDao;
    private GenericDao<VerticalsMaster, Integer> verticalMastersDao;
    private OrganizationManagementService organizationManagementService;
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;
    private UserManagementService userManagementService;
    private UserService userService;
    private Payment payment;
    private EmailServices emailServices;
    private SurveyBuilder surveyBuilder;

    @Value ( "${SALES_LEAD_EMAIL_ADDRESS}")
    private String salesLeadEmail;


    @Autowired
    public AccountServiceImpl( GenericDao<VerticalsMaster, Integer> industryDao,
        GenericDao<AccountsMaster, Integer> paymentPlanDao, CompanyDao companyDao,
        GenericDao<VerticalsMaster, Integer> verticalMastersDao, OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService,
        UserService userService, Payment payment, EmailServices emailServices, SurveyBuilder surveyBuilder )
    {
        this.industryDao = industryDao;
        this.paymentPlanDao = paymentPlanDao;
        this.companyDao = companyDao;
        this.verticalMastersDao = verticalMastersDao;
        this.organizationManagementService = organizationManagementService;
        this.organizationUnitSettingsDao = organizationUnitSettingsDao;
        this.userManagementService = userManagementService;
        this.userService = userService;
        this.payment = payment;
        this.emailServices = emailServices;
        this.surveyBuilder = surveyBuilder;
    }


    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public Map<String, Long> saveAccountRegistrationDetailsAndGetIdsInMap( User user, String companyName, Phone phone )
        throws InvalidInputException, UserAlreadyExistsException, SolrException, NoRecordsFetchedException, NonFatalException
    {
        LOGGER.info( "Method saveAccountRegistrationDetailsAndSetDataInDO started for company: " + companyName );
        Map<String, Long> ids = new HashMap<String, Long>();

        // validate if the email address is not taken already.
        if ( userService.isUserExist( user.getEmailId() ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + user.getEmailId() + " already exists" );
        } else {
            // Create a company with registration stage as 1. Insert into mongo with status 'I'
            Company company = addCompany( user, companyName, phone );
            ids.put( "companyId", company.getCompanyId() );

            // Create a user in user table with registration stage as 1 and status 1,user profile in UserProfile table with 'CA' (1), 
            //solr, mongo with status 'I'. Set the force password column to 1.
            user = userService.addUser( user.getFirstName(), user.getLastName(), user.getEmailId(), phone, company );
            ids.put( "userId", user.getUserId() );

            // Send registration email to user, Send mail to sales lead, maybe to support
            userService.sendRegistrationEmail( user );
        }

        LOGGER.info( "Method saveAccountRegistrationDetailsAndSetDataInDO finished for company: " + companyName );
        return ids;
    }


    @Override
    public CompanyCompositeEntity getCompanyProfileDetails( long companyId ) throws InvalidInputException
    {
        LOGGER.info( "Method getCompanyProfileDetails started for company: " + companyId );
        CompanyCompositeEntity companyProfile = new CompanyCompositeEntity();
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        Company company = companyDao.findById( Company.class, companyId );
        companyProfile.setCompany( company );
        companyProfile.setCompanySettings( unitSettings );
        LOGGER.info( "Method getCompanyProfileDetails finished for company: " + company.getCompany() );
        return companyProfile;
    }


    @Override
    public void updateCompanyProfile( long companyId, long userId, CompanyCompositeEntity companyProfile )
        throws InvalidInputException
    {
        LOGGER.info( "Method updateCompanyProfile started for company: " + companyId );
        updateCompanyDetailsInMySql( companyId, userId, companyProfile.getCompany() );
        updateCompanyDetailsInMongo( companyId, userId, companyProfile.getCompanySettings() );
        LOGGER.info( "Method updateCompanyProfile finished for company: " + companyId );
    }


    @Override
    public void deleteCompanyProfileImage( long companyId, long userId ) throws InvalidInputException
    {
        LOGGER.info( "Method deleteCompanyProfileImage started for company: " + companyId );
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        unitSettings.setLogo( null );
        unitSettings.setLogoThumbnail( null );
        unitSettings.setModifiedOn( System.currentTimeMillis() );
        unitSettings.setModifiedBy( String.valueOf( userId ) );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_LOGO_THUMBNAIL, unitSettings.getLogoThumbnail(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, unitSettings.getModifiedBy(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.info( "Method deleteCompanyProfileImage finished for company: " + companyId );
    }


    @Override
    public void updateCompanyProfileImage( long companyId, long userId, String imageUrl ) throws InvalidInputException
    {
        LOGGER.info( "Method updateCompanyProfileImage started for company: " + companyId );
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        unitSettings.setLogo( imageUrl );
        unitSettings.setLogoThumbnail( imageUrl );
        unitSettings.setModifiedOn( System.currentTimeMillis() );
        unitSettings.setModifiedBy( String.valueOf( userId ) );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, unitSettings.getModifiedBy(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.info( "Method updateCompanyProfileImage finished for company: " + companyId );
    }


    @Override
    public void updateStage( long companyId, String stage )
    {
        LOGGER.info( "Method updateStage started for company: " + companyId + ", stage: " + stage );
        Company company = companyDao.findById( Company.class, companyId );
        company.setRegistrationStage( stage );
        company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyDao.update( company );
        LOGGER.info( "Method updateStage finished for company: " + companyId + ", stage: " + stage );
    }


    @Override
    public List<VerticalsMaster> getIndustries()
    {
        LOGGER.info( "AccountServiceImpl.getIndustries started" );
        List<VerticalsMaster> industries = industryDao.findAll( VerticalsMaster.class );
        LOGGER.info( "AccountServiceImpl.getIndustries completed successfully" );
        return industries;
    }


    @Override
    public List<PaymentPlan> getPaymentPlans()
    {
        LOGGER.info( "AccountServiceImpl.getPaymentPlans started" );
        List<PaymentPlan> paymentPlans = new ArrayList<>();
        List<AccountsMaster> plans = paymentPlanDao.findAll( AccountsMaster.class );
        for ( AccountsMaster plan : plans ) {
            if ( plan.getAccountsMasterId() == AccountType.INDIVIDUAL.getValue() ) {
                paymentPlans.add( getPaymentPlan( plan.getAmount(), Plan.INDIVIDUAL ) );
            } else if ( plan.getAccountsMasterId() == AccountType.ENTERPRISE.getValue() ) {
                paymentPlans.add( getPaymentPlan( plan.getAmount(), Plan.BUSINESS ) );
                paymentPlans.add( getPaymentPlan( plan.getAmount(), Plan.ENTERPRISE ) );
            }
        }
        LOGGER.info( "AccountServiceImpl.getPaymentPlans completed successfully" );
        return paymentPlans;
    }


    @Transactional
    @Override
    public void generateDefaultHierarchy( long companyId )
        throws InvalidInputException, SolrException, HierarchyAlreadyExistsException
    {
        LOGGER.info( "AccountServiceImpl.generateDefaultHierarchy started" );
        Company company = companyDao.findById( Company.class, companyId );
        if ( company == null ) {
            throw new InvalidInputException( "Company with companyId : " + companyId + " does not exist" );
        }

        //Get the company admin
        User companyAdmin = userManagementService.getAdminUserByCompanyId( companyId );
        if ( companyAdmin == null ) {
            throw new InvalidInputException( "No company admin exists for companyId : " + companyId );
        }
        if ( companyAdmin.getStatus() != CommonConstants.STATUS_INCOMPLETE ) {
            throw new HierarchyAlreadyExistsException( "The hierarchy already exists for companyId: " + companyId );
        }

        //Get license details for the company
        LicenseDetail companyLicenseDetail = company.getLicenseDetails().get( CommonConstants.INITIAL_INDEX );
        if ( companyLicenseDetail == null ) {
            throw new InvalidInputException( "LicenseDetails for companyId : " + companyId + " does not exist" );
        }

        //Get current accounts master
        AccountsMaster accountsMaster = companyLicenseDetail.getAccountsMaster();
        if ( accountsMaster == null ) {
            throw new InvalidInputException( "AccountsMaster for companyId : " + companyId + " does not exist" );
        }
        organizationManagementService.addAccountTypeForCompany( companyAdmin,
            String.valueOf( accountsMaster.getAccountsMasterId() ) );

        //add default survey questions
        if ( surveyBuilder.checkForExistingSurvey( companyAdmin ) == null ) {
            surveyBuilder.addDefaultSurveyToCompany( companyAdmin );
        }

        //Update profile completion stage for company admin
        userManagementService.updateProfileCompletionStage( companyAdmin,
            CommonConstants.PROFILES_MASTER_COMPANY_ADMIN_PROFILE_ID, CommonConstants.DASHBOARD_STAGE );

        //Activate company
        organizationManagementService.activateCompany( company );

        //Activate company admin
        userManagementService.activateCompanyAdmin( companyAdmin );

        LOGGER.info( "AccountServiceImpl.generateDefaultHierarchy finished" );
    }


    private void updateCompanyDetailsInMongo( long companyId, long userId, OrganizationUnitSettings unitSettings )
        throws InvalidInputException
    {
        LOGGER.info( "Method updateCompanyDetailsInMongo started for company: " + companyId );

        if ( unitSettings != null && unitSettings.getContact_details() != null ) {
            if ( unitSettings.getContact_details().getContact_numbers() != null
                && unitSettings.getContact_details().getContact_numbers().getPhone1() != null ) {
                unitSettings.getContact_details().getContact_numbers()
                    .setWork( unitSettings.getContact_details().getContact_numbers().getPhone1().getCountryCode() + "-"
                        + unitSettings.getContact_details().getContact_numbers().getPhone1().getNumber() + "x"
                        + unitSettings.getContact_details().getContact_numbers().getPhone1().getExtension() );
            }
            String profileName = organizationManagementService
                .generateProfileNameForCompany( unitSettings.getContact_details().getName(), companyId );
            unitSettings.setProfileName( profileName );
            unitSettings.setProfileUrl( CommonConstants.FILE_SEPARATOR + unitSettings.getProfileName() );
        }

        unitSettings.setModifiedOn( System.currentTimeMillis() );
        unitSettings.setModifiedBy( String.valueOf( userId ) );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, unitSettings.getContact_details(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_VERTICAL, unitSettings.getVertical(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_LOGO_THUMBNAIL, unitSettings.getLogoThumbnail(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE, unitSettings.getProfileImageUrl(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_IMAGE_THUMBNAIL, unitSettings.getProfileImageUrlThumbnail(),
            unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, unitSettings.getProfileName(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, unitSettings.getProfileUrl(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, unitSettings.getModifiedBy(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        AgentSettings agentSettings = organizationUnitSettingsDao.fetchAgentSettingsById( userId );
        organizationUnitSettingsDao.updateParticularKeyAgentSettings( MongoOrganizationUnitSettingDaoImpl.KEY_VERTICAL,
            unitSettings.getVertical(), agentSettings );

        LOGGER.info( "Method updateCompanyDetailsInMongo finished for company: " + companyId );
    }


    private void updateCompanyDetailsInMySql( long companyId, long userId, Company companyProfile )
    {
        LOGGER.info( "Method updateCompanyDetailsInMySql started for company: " + companyId );
        companyProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyProfile.setModifiedBy( String.valueOf( userId ) );
        companyDao.update( companyProfile );
        LOGGER.info( "Method updateCompanyDetailsInMySql finished for company: " + companyId );
    }


    private PaymentPlan getPaymentPlan( double amount, Plan plan )
    {
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setAmount( amount );
        paymentPlan.setLevel( plan.getAccountMasterId() );
        paymentPlan.setPlanCurrency( plan.getPlanCurrency() );
        paymentPlan.setPlanId( plan.getPlanId() );
        paymentPlan.setPlanName( plan.getPlanName() );
        paymentPlan.setSupportingText( plan.getSupportingText() );
        paymentPlan.setTerms( plan.getTerms() );
        return paymentPlan;
    }


    private Company addCompany( User user, String companyName, Phone phone ) throws InvalidInputException
    {
        LOGGER.info( "Method addCompany started for company: " + companyName );
        Company company = addCompanyDetailsInMySql( companyName );
        addCompanyDetailsInMongo( user, company, phone );
        LOGGER.info( "Method addCompany finished for company: " + companyName );
        return company;
    }


    @Transactional
    private Company addCompanyDetailsInMySql( String companyName )
    {
        LOGGER.info( "Method addCompanyDetailsInMySql started for company: " + companyName );
        Company company = new Company();
        company.setCompany( companyName );
        company.setIsRegistrationComplete( 0 );
        company.setStatus( CommonConstants.STATUS_INCOMPLETE );
        company.setBillingMode( "A" );
        company.setCreatedBy( CommonConstants.ACCOUNT_REGISTER );
        company.setModifiedBy( CommonConstants.ACCOUNT_REGISTER );
        company.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        company.setCreatedOn( new Timestamp( System.currentTimeMillis() ) );
        company.setRegistrationStage( RegistrationStage.INITIATE_REGISTRATION.getCode() );
        company.setSettingsLockStatus( "0" );
        company.setSettingsSetStatus( "0" );
        company.setIsZillowConnected( 0 );
        company.setZillowReviewCount( 0 );
        VerticalsMaster verticalsMaster = verticalMastersDao
            .findByColumn( VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, "CUSTOM" )
            .get( CommonConstants.INITIAL_INDEX );
        company.setVerticalsMaster( verticalsMaster );
        company = companyDao.save( company );
        LOGGER.info( "Method addCompanyDetailsInMySql finished for company: " + companyName );
        return company;
    }


    private void addCompanyDetailsInMongo( User user, Company company, Phone phone ) throws InvalidInputException
    {
        LOGGER.info( "Method addCompanyDetailsInMongo started for company: " + company.getCompany() );

        String contactNumber = null;
        if ( phone != null ) {
            contactNumber = phone.getCountryCode() + "-" + phone.getNumber() + "x" + phone.getExtension();
        }
        VerticalsMaster verticalsMaster = verticalMastersDao
            .findByColumn( VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, "CUSTOM" )
            .get( CommonConstants.INITIAL_INDEX );
        Map<String, String> companyDetails = new HashMap<String, String>();
        companyDetails.put( CommonConstants.COMPANY_NAME, company.getCompany() );
        companyDetails.put( CommonConstants.COMPANY_CONTACT_NUMBER, contactNumber );
        companyDetails.put( CommonConstants.BILLING_MODE_COLUMN, CommonConstants.BILLING_MODE_AUTO );
        companyDetails.put( CommonConstants.VERTICAL, verticalsMaster.getVerticalName() );
        organizationManagementService.addOrganizationalDetails( user, company, companyDetails );

        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( company.getCompanyId() );

        if ( unitSettings.getContact_details() == null ) {
            unitSettings.setContact_details( new ContactDetailsSettings() );
        }
        if ( unitSettings.getContact_details().getContact_numbers() == null ) {
            unitSettings.getContact_details().setContact_numbers( new ContactNumberSettings() );
        }
        unitSettings.getContact_details().getContact_numbers().setPhone1( phone );
        unitSettings.setCreatedBy( CommonConstants.ACCOUNT_REGISTER );
        unitSettings.setModifiedBy( CommonConstants.ACCOUNT_REGISTER );
        unitSettings.setModifiedOn( System.currentTimeMillis() );
        unitSettings.setCreatedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, unitSettings.getContact_details(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_STATUS,
            CommonConstants.STATUS_INCOMPLETE_MONGO, unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_BY, unitSettings.getModifiedBy(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CREATED_ON, unitSettings.getCreatedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CREATED_BY, unitSettings.getCreatedBy(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.info( "Method addCompanyDetailsInMongo started for company: " + company.getCompany() );
    }


    @Transactional
    @Override
    public void payForPlan( long companyId, int planId, String nonce, String cardHolderName, String name, String email,
        String message ) throws InvalidInputException, PaymentException, SubscriptionUnsuccessfulException,
        NoRecordsFetchedException, CreditCardException, ActiveSubscriptionFoundException, UndeliveredEmailException
    {
        LOGGER.info( "Paying for company id " + companyId + " for plan " + planId );
        Company company = companyDao.findById( Company.class, companyId );
        User user = userManagementService.getAdminUserByCompanyId( companyId );
        if ( planId < Plan.ENTERPRISE.getPlanId() ) {
            // pass the company and nonce to make a payment. Get the subscription id and insert into license table.
            String subscriptionId = payment.subscribeForCompany( company, nonce, planId, cardHolderName );

            // insert into License Details table
            payment.insertIntoLicenseTable( getPlanById( planId ).getLevel(), user, subscriptionId );
        } else {
            payment.insertIntoLicenseTable( getPlanById( planId ).getLevel(), user,
                CommonConstants.INVOICE_BILLED_DEFULAT_SUBSCRIPTION_ID );
            company.setBillingMode( CommonConstants.BILLING_MODE_INVOICE );
            updateCompanyDetailsInMySql( companyId, user.getUserId(), company );
            String additionalEmailBody = "Please contact the below user to discuss plan details for Enterprise account. <br> Name: "
                + name + "<br> Email: " + email + "<br> Message: " + message;
            sendMailToSalesLead( user, additionalEmailBody );
        }
        sendMailToSalesLead( user, null );
    }


    private void sendMailToSalesLead( User user, String additionalEmailBody )
        throws InvalidInputException, UndeliveredEmailException
    {
        // Send mail to sales lead
        Date today = new Date( System.currentTimeMillis() );
        SimpleDateFormat utcDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        utcDateFormat.setTimeZone( TimeZone.getTimeZone( "UTC" ) );

        SimpleDateFormat pstDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        pstDateFormat.setTimeZone( TimeZone.getTimeZone( "PST" ) );

        SimpleDateFormat estDateFormat = new SimpleDateFormat( CommonConstants.DATE_FORMAT_WITH_TZ );
        estDateFormat.setTimeZone( TimeZone.getTimeZone( "EST" ) );
        String details = "First Name : " + user.getFirstName() + "<br/>" + "Last Name : " + user.getLastName() + "<br/>"
            + "Email Address : " + user.getEmailId() + "<br/>" + "Time : " + "<br/>" + utcDateFormat.format( today ) + "<br/>"
            + estDateFormat.format( today ) + "<br/>" + pstDateFormat.format( today );
        if ( additionalEmailBody != null ) {
            details = details + "<br>" + additionalEmailBody;
        }
        try {
            emailServices.sendCompanyRegistrationStageMail( user.getFirstName(), user.getLastName(),
                Arrays.asList( salesLeadEmail ), CommonConstants.COMPANY_REGISTRATION_STAGE_COMPLETE, user.getEmailId(),
                details, true );
        } catch ( InvalidInputException e ) {
            e.printStackTrace();
        } catch ( UndeliveredEmailException e ) {
            e.printStackTrace();
        }
        emailServices.sendCompanyRegistrationStageMail( user.getFirstName(), user.getLastName(),
            Arrays.asList( salesLeadEmail ), CommonConstants.COMPANY_REGISTRATION_STAGE_STARTED, user.getEmailId(), details,
            true );
    }


    private PaymentPlan getPlanById( int planId )
    {
        for ( PaymentPlan plan : getPaymentPlans() ) {
            if ( plan.getPlanId() == planId ) {
                return plan;
            }
        }
        return null;
    }


    @Override
    public List<StateLookup> getUsStatesList()
    {
        List<StateLookup> lookups = organizationManagementService.getUsStateList();
        return lookups;
    }
}
