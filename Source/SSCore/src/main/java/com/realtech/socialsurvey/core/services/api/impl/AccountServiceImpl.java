package com.realtech.socialsurvey.core.services.api.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.payment.Payment;
import com.realtech.socialsurvey.core.services.payment.exception.ActiveSubscriptionFoundException;
import com.realtech.socialsurvey.core.services.payment.exception.CreditCardException;
import com.realtech.socialsurvey.core.services.payment.exception.PaymentException;
import com.realtech.socialsurvey.core.services.payment.exception.SubscriptionUnsuccessfulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.AccountsMaster;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.Phone;
import com.realtech.socialsurvey.core.entities.RegistrationStage;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


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


    @Autowired
    public AccountServiceImpl( GenericDao<VerticalsMaster, Integer> industryDao,
        GenericDao<AccountsMaster, Integer> paymentPlanDao, CompanyDao companyDao,
        GenericDao<VerticalsMaster, Integer> verticalMastersDao, OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService, UserService userService, Payment payment )
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
    public void updateCompanyProfile( long companyId, CompanyCompositeEntity companyProfile ) throws InvalidInputException
    {
        LOGGER.info( "Method updateCompanyProfile started for company: " + companyId );
        updateCompanyDetailsInMySql( companyId, companyProfile.getCompany() );
        updateCompanyDetailsInMongo( companyId, companyProfile.getCompanySettings() );
        LOGGER.info( "Method updateCompanyProfile finished for company: " + companyId );
    }


    @Override
    public void deleteCompanyProfileImage( long companyId ) throws InvalidInputException
    {
        LOGGER.info( "Method deleteCompanyProfileImage started for company: " + companyId );
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        unitSettings.setLogo( null );
        unitSettings.setModifiedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.info( "Method deleteCompanyProfileImage finished for company: " + companyId );
    }


    @Override
    public void updateCompanyProfileImage( long companyId, String imageUrl ) throws InvalidInputException
    {
        LOGGER.info( "Method updateCompanyProfileImage started for company: " + companyId );
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        unitSettings.setLogo( imageUrl );
        unitSettings.setModifiedOn( System.currentTimeMillis() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
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
        List<PaymentPlan> paymentPlans = new ArrayList<PaymentPlan>();
        List<AccountsMaster> plans = paymentPlanDao.findAll( AccountsMaster.class );
        for ( AccountsMaster plan : plans ) {
            if ( plan.getAccountsMasterId() == AccountType.INDIVIDUAL.getValue() ) {
                paymentPlans
                    .add( getPaymentPlan( 1, plan.getAmount(), "$", plan.getAccountsMasterId(), "Individual", "", "" ) );
            } else if ( plan.getAccountsMasterId() == AccountType.ENTERPRISE.getValue() ) {
                paymentPlans.add( getPaymentPlan( 2, plan.getAmount(), "$", plan.getAccountsMasterId(), "Business", "", "" ) );
                paymentPlans.add( getPaymentPlan( 3, 0, "$", plan.getAccountsMasterId(), "Enterprise", "", "" ) );
            }
        }
        LOGGER.info( "AccountServiceImpl.getPaymentPlans completed successfully" );
        return paymentPlans;
    }


    private void updateCompanyDetailsInMongo( long companyId, OrganizationUnitSettings unitSettings )
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

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, unitSettings.getContact_details(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_VERTICAL, unitSettings.getVertical(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_LOGO,
            unitSettings.getLogo(), unitSettings, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_NAME, unitSettings.getProfileName(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_PROFILE_URL, unitSettings.getProfileUrl(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_MODIFIED_ON, unitSettings.getModifiedOn(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.info( "Method updateCompanyDetailsInMongo finished for company: " + companyId );
    }


    private void updateCompanyDetailsInMySql( long companyId, Company companyProfile )
    {
        LOGGER.info( "Method updateCompanyDetailsInMySql started for company: " + companyId );
        companyProfile.setModifiedOn( new Timestamp( System.currentTimeMillis() ) );
        companyDao.merge( companyProfile );
        LOGGER.info( "Method updateCompanyDetailsInMySql finished for company: " + companyId );
    }


    private PaymentPlan getPaymentPlan( int level, double amount, String currency, int planId, String planName, String text,
        String terms )
    {
        PaymentPlan paymentPlan = new PaymentPlan();
        paymentPlan.setAmount( amount );
        paymentPlan.setLevel( level );
        paymentPlan.setPlanCurrency( currency );
        paymentPlan.setPlanId( planId );
        paymentPlan.setPlanName( planName );
        paymentPlan.setSupportingText( text );
        paymentPlan.setTerms( terms );
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
    public void payForPlan(long companyId, int planId, String nonce, String cardHolderName) throws InvalidInputException,
        PaymentException, SubscriptionUnsuccessfulException, NoRecordsFetchedException, CreditCardException,
        ActiveSubscriptionFoundException
    {
        LOGGER.info( "Paying for company id "+companyId+ " for plan "+planId );
        Company company = companyDao.findById( Company.class, companyId );
        // pass the company and nonce to make a payment. Get the subscription id and insert into license table.
        String subscriptionId = payment.subscribeForCompany( company, nonce, planId, cardHolderName );
        // insert into License Details table
        User user = userManagementService.getAdminUserByCompanyId(companyId);
        payment.insertIntoLicenseTable( planId, user, subscriptionId );
    }
}
