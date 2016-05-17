package com.realtech.socialsurvey.api.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.models.CompanyProfile;
import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.transformers.CompanyProfileTransformer;
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.api.validators.CompanyProfileValidator;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/account")
public class AccountController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountController.class );
    private AccountRegistrationValidator accountRegistrationValidator;
    private AccountService accountService;
    private CompanyProfileTransformer companyProfileTransformer;
    private CompanyProfileValidator companyProfileValidator;
    private OrganizationManagementService organizationManagementService;


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer, CompanyProfileValidator companyProfileValidator,
        OrganizationManagementService organizationManagementService )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountService = accountService;
        this.companyProfileTransformer = companyProfileTransformer;
        this.companyProfileValidator = companyProfileValidator;
        this.organizationManagementService = organizationManagementService;
    }


    @InitBinder ( "accountRegistrationRequest")
    public void signUpAccountRegistrationBinder( WebDataBinder binder )
    {
        binder.setValidator( accountRegistrationValidator );
    }


    @InitBinder ( "companyProfile")
    public void signUpCompanyProfileBinder( WebDataBinder binder )
    {
        binder.setValidator( companyProfileValidator );
    }


    @RequestMapping ( value = "/register/init", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> initAccountRegsitration(
        @Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest )
    {
        try {
            LOGGER.info( "AccountController.initAccountRegsitration started" );
            User user = new User();
            user.setFirstName( accountRegistrationRequest.getFirstName() );
            user.setLastName( accountRegistrationRequest.getLastName() );
            user.setEmailId( accountRegistrationRequest.getEmail() );
            Map<String, Long> ids = accountService.saveAccountRegistrationDetailsAndGetIdsInMap( user,
                accountRegistrationRequest.getCompanyName(), accountRegistrationRequest.getPhone() );
            LOGGER.info( "AccountController.initAccountRegsitration completed successfully" );
            return new ResponseEntity<Map<String, Long>>( ids, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while initiating account registration: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/details/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile details")
    public ResponseEntity<?> getCompanyProfile( @PathVariable ( "companyId") String companyId )
    {
        try {
            LOGGER.info( "AccountController.getCompanyProfile started" );
            CompanyCompositeEntity companyProfile = accountService.getCompanyProfileDetails( Integer.parseInt( companyId ) );
            CompanyProfile response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
            LOGGER.info( "AccountController.getCompanyProfile completed successfully" );
            return new ResponseEntity<CompanyProfile>( response, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting company profile details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile details")
    public ResponseEntity<?> updateCompanyProfile( @Valid @RequestBody CompanyProfile companyProfile,
        @PathVariable ( "companyId") String companyId )
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfile started" );
            long compId = Long.parseLong( companyId );
            OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( compId );
            Company company = organizationManagementService.getCompanyById( compId );
            CompanyCompositeEntity companyProfileDetails = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfile, company, unitSettings );
            accountService.updateCompanyProfile( compId, companyProfileDetails );
            LOGGER.info( "AccountController.updateCompanyProfile completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating company profile details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/profileimage/remove/{companyId}", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete company profile image")
    public ResponseEntity<?> deleteCompanyProfileImage( @PathVariable ( "companyId") String companyId )
    {
        try {
            LOGGER.info( "AccountController.deleteCompanyProfileImage started" );
            accountService.deleteCompanyProfileImage( Integer.parseInt( companyId ) );
            LOGGER.info( "AccountController.deleteCompanyProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while deleting company profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/profileimage/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile image")
    public ResponseEntity<?> updateCompanyProfileImage( @PathVariable ( "companyId") String companyId,
        @RequestBody CompanyProfile companyProfile )
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfileImage started" );
            accountService.updateCompanyProfileImage( Integer.parseInt( companyId ), companyProfile.getCompanyLogo() );
            LOGGER.info( "AccountController.updateCompanyProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating company profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/stage/update/{companyId}/{stage}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update stage")
    public ResponseEntity<?> updateStage( @PathVariable ( "companyId") String companyId, @PathVariable ( "stage") String stage )
    {
        try {
            LOGGER.info( "AccountController.updateStage started" );
            accountService.updateStage( Integer.parseInt( companyId ), stage );
            LOGGER.info( "AccountController.updateStage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating stage: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/industries", method = RequestMethod.GET)
    @ApiOperation ( value = "Get industries drop down data")
    public ResponseEntity<?> getIndustries()
    {
        try {
            LOGGER.info( "AccountController.getIndustries started" );
            List<VerticalsMaster> industries = accountService.getIndustries();
            LOGGER.info( "AccountController.getIndustries completed successfully" );
            return new ResponseEntity<List<VerticalsMaster>>( industries, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting industries drop down data: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/payment/plans", method = RequestMethod.GET)
    @ApiOperation ( value = "Get payment plans")
    public ResponseEntity<?> getPaymentPlans()
    {
        try {
            LOGGER.info( "AccountController.getPaymentPlans started" );
            List<PaymentPlan> plans = accountService.getPaymentPlans();
            LOGGER.info( "AccountController.getPaymentPlans completed successfully" );
            return new ResponseEntity<List<PaymentPlan>>( plans, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting payment plans: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
