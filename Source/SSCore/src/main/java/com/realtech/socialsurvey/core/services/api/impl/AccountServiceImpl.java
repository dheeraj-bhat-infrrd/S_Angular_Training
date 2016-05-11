package com.realtech.socialsurvey.core.services.api.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.entities.ContactNumberSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.entities.api.Country;
import com.realtech.socialsurvey.core.entities.api.PaymentPlan;
import com.realtech.socialsurvey.core.entities.api.RegistrationStage;
import com.realtech.socialsurvey.core.enums.AccountType;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.api.UserService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;


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


    @Autowired
    public AccountServiceImpl( GenericDao<VerticalsMaster, Integer> industryDao,
        GenericDao<AccountsMaster, Integer> paymentPlanDao, CompanyDao companyDao,
        GenericDao<VerticalsMaster, Integer> verticalMastersDao, OrganizationManagementService organizationManagementService,
        OrganizationUnitSettingsDao organizationUnitSettingsDao, UserManagementService userManagementService,
        UserService userService )
    {
        this.industryDao = industryDao;
        this.paymentPlanDao = paymentPlanDao;
        this.companyDao = companyDao;
        this.verticalMastersDao = verticalMastersDao;
        this.organizationManagementService = organizationManagementService;
        this.organizationUnitSettingsDao = organizationUnitSettingsDao;
        this.userManagementService = userManagementService;
        this.userService = userService;

    }


    @Override
    @Transactional ( rollbackFor = { NonFatalException.class, FatalException.class })
    public void saveAccountRegistrationDetailsAndSetDataInDO( AccountRegistration accountRegistration ) throws NonFatalException
    {
        // validate if the email address is not taken already.
        if ( userManagementService.userExists( accountRegistration.getEmail() ) ) {
            throw new UserAlreadyExistsException( "User with User ID : " + accountRegistration.getEmail() + " already exists" );
        } else {
            // Create a company with registration stage as 1. Insert into mongo with status 'I'
            Company company = addCompany( accountRegistration );
            accountRegistration.setCompanyId( (int) company.getCompanyId() );

            // Create a user in user table with registration stage as 1 and status 1,user profile in UserProfile table with 'CA' (1), 
            //solr, mongo with status 'I'. Set the force password column to 1.
            User user = userService.addUser( accountRegistration.getFirstName(), accountRegistration.getLastName(),
                accountRegistration.getEmail(), company );
            accountRegistration.setUserId( (int) user.getUserId() );

            // Send registration email to user, Send mail to sales lead, maybe to support
            userService.sendRegistrationEmail( user );
        }
    }


    @Override
    public CompanyProfile getCompanyProfileDetails( int companyId ) throws InvalidInputException
    {
        CompanyProfile companyProfile = new CompanyProfile();
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( companyId );
        Company company = companyDao.findById( Company.class, (long) companyId );

        companyProfile.setCompanyLogo( unitSettings.getLogo() );
        companyProfile.setCompanyName( company.getCompany() );

        VerticalsMaster verticalsMaster = verticalMastersDao
            .findByColumn( VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, unitSettings.getVertical() )
            .get( CommonConstants.INITIAL_INDEX );
        companyProfile.setIndustry( verticalsMaster );

        if ( unitSettings.getContact_details() != null ) {
            companyProfile.setAddress( unitSettings.getContact_details().getAddress() );
            companyProfile.setCity( unitSettings.getContact_details().getCity() );
            companyProfile.setState( unitSettings.getContact_details().getState() );
            companyProfile.setZip( unitSettings.getContact_details().getZipcode() );
            if ( unitSettings.getContact_details().getContact_numbers() != null ) {
                companyProfile.setOfficePhone( unitSettings.getContact_details().getContact_numbers().getPhone1() );
            }
            Country country = new Country();
            country.setCountryCode( unitSettings.getContact_details().getCountryCode() );
            country.setCountryName( unitSettings.getContact_details().getCountry() );
            companyProfile.setCountry( country );
        }
        return companyProfile;
    }


    @Override
    public void updateCompanyProfile( int companyId, CompanyProfile companyProfile ) throws InvalidInputException
    {
        Company company = companyDao.findById( Company.class, (long) companyId );
        updateCompanyDetailsInMySql( company, companyProfile );
        updateCompanyDetailsInMongo( company, companyProfile );
    }


    @Override
    public void deleteCompanyProfileImage( int companyId )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateCompanyProfileImage( int companyId, String imageUrl )
    {
        // TODO Auto-generated method stub

    }


    @Override
    public void updateStage( int companyId, String stage )
    {
        Company company = companyDao.findById( Company.class, (long) companyId );
        company.setRegistrationStage( stage );
        companyDao.update( company );
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


    private void updateCompanyDetailsInMongo( Company company, CompanyProfile companyProfile ) throws InvalidInputException
    {
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( company.getCompanyId() );
        ContactDetailsSettings contactDetails = unitSettings.getContact_details();
        if ( contactDetails == null ) {
            contactDetails = new ContactDetailsSettings();
        }
        contactDetails.setName( companyProfile.getCompanyName() );
        contactDetails.setAddress( companyProfile.getAddress() );
        contactDetails.setCity( companyProfile.getCity() );
        contactDetails.setCountry( companyProfile.getCountry().getCountryName() );
        contactDetails.setCountryCode( companyProfile.getCountry().getCountryCode() );
        contactDetails.setState( companyProfile.getState() );
        contactDetails.setZipcode( companyProfile.getZip() );

        if ( contactDetails.getContact_numbers() == null ) {
            contactDetails.setContact_numbers( new ContactNumberSettings() );
        }
        contactDetails.getContact_numbers().setWork( companyProfile.getOfficePhone().getCountryCode() + "-"
            + companyProfile.getOfficePhone().getNumber() + "x" + companyProfile.getOfficePhone().getExtension() );
        contactDetails.getContact_numbers().setPhone1( companyProfile.getOfficePhone() );

        unitSettings.setContact_details( contactDetails );
        unitSettings.setLogo( companyProfile.getCompanyLogo() );
        unitSettings.setVertical( companyProfile.getIndustry().getVerticalName() );
        String profileName = organizationManagementService.generateProfileNameForCompany( companyProfile.getCompanyName(),
            company.getCompanyId() );
        unitSettings.setProfileName( profileName );
        unitSettings.setProfileUrl( CommonConstants.FILE_SEPARATOR + unitSettings.getProfileName() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, contactDetails, unitSettings,
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
    }


    private void updateCompanyDetailsInMySql( Company company, CompanyProfile companyProfile )
    {
        company.setCompany( companyProfile.getCompanyName() );
        company.setVerticalsMaster( companyProfile.getIndustry() );
        companyDao.merge( company );
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


    private Company addCompany( AccountRegistration accountRegistration ) throws InvalidInputException
    {
        LOGGER.debug( "Method addCompany started for company: " + accountRegistration.getCompanyName() );
        Company company = addCompanyDetailsInMySql( accountRegistration.getCompanyName() );
        addCompanyDetailsInMongo( accountRegistration, company );
        LOGGER.debug( "Method addCompany finished for company: " + accountRegistration.getCompanyName() );
        return company;
    }


    @Transactional
    private Company addCompanyDetailsInMySql( String companyName )
    {
        LOGGER.debug( "Method addCompanyDetailsInMySql started for company: " + companyName );
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
        LOGGER.debug( "Method addCompanyDetailsInMySql finished." );
        return company;
    }


    private void addCompanyDetailsInMongo( AccountRegistration accountReg, Company company ) throws InvalidInputException
    {
        LOGGER.debug( "Method addCompanyDetailsInMongo called." );

        User user = new User();
        user.setEmailId( accountReg.getEmail() );
        String contactNumber = accountReg.getPhone().getCountryCode() + "-" + accountReg.getPhone().getNumber() + "x"
            + accountReg.getPhone().getExtension();
        VerticalsMaster verticalsMaster = verticalMastersDao
            .findByColumn( VerticalsMaster.class, CommonConstants.VERTICALS_MASTER_NAME_COLUMN, "CUSTOM" )
            .get( CommonConstants.INITIAL_INDEX );
        Map<String, String> companyDetails = new HashMap<String, String>();
        companyDetails.put( CommonConstants.COMPANY_NAME, accountReg.getCompanyName() );
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
        unitSettings.getContact_details().getContact_numbers().setPhone1( accountReg.getPhone() );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings(
            MongoOrganizationUnitSettingDaoImpl.KEY_CONTACT_DETAIL_SETTINGS, unitSettings.getContact_details(), unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        organizationUnitSettingsDao.updateParticularKeyOrganizationUnitSettings( MongoOrganizationUnitSettingDaoImpl.KEY_STATUS,
            CommonConstants.STATUS_INCOMPLETE_MONGO, unitSettings,
            MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION );

        LOGGER.debug( "Method addCompanyDetailsInMongo finished" );
    }
}
