package com.realtech.socialsurvey.api.controllers;

import java.util.List;

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

import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.models.request.CompanyProfileRequest;
import com.realtech.socialsurvey.api.models.response.AccountRegistrationResponse;
import com.realtech.socialsurvey.api.models.response.CompanyProfileResponse;
import com.realtech.socialsurvey.api.transformers.AccountRegistrationTransformer;
import com.realtech.socialsurvey.api.transformers.CompanyProfileTransformer;
import com.realtech.socialsurvey.api.transformers.IndustryTransformer;
import com.realtech.socialsurvey.api.transformers.PaymentPlanTransformer;
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.api.validators.CompanyProfileValidator;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.entities.api.PaymentPlan;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/account")
public class AccountController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountController.class );
    private AccountRegistrationValidator accountRegistrationValidator;
    private AccountRegistrationTransformer accountRegistrationTransformer;
    private AccountService accountService;
    private CompanyProfileTransformer companyProfileTransformer;
    private CompanyProfileValidator companyProfileValidator;
    private IndustryTransformer industryTransformer;
    private PaymentPlanTransformer paymentPlanTransformer;


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator,
        AccountRegistrationTransformer accountRegistrationTransformer, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer, CompanyProfileValidator companyProfileValidator,
        IndustryTransformer industryTransformer, PaymentPlanTransformer paymentPlanTransformer )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountRegistrationTransformer = accountRegistrationTransformer;
        this.accountService = accountService;
        this.companyProfileTransformer = companyProfileTransformer;
        this.companyProfileValidator = companyProfileValidator;
        this.industryTransformer = industryTransformer;
        this.paymentPlanTransformer = paymentPlanTransformer;
    }


    @InitBinder ( "accountRegistrationRequest")
    public void signUpAccountRegistrationBinder( WebDataBinder binder )
    {
        binder.setValidator( accountRegistrationValidator );
    }


    @InitBinder ( "companyProfileRequest")
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
            AccountRegistration accountRegistration = accountRegistrationTransformer
                .transformApiRequestToDomainObject( accountRegistrationRequest );
            accountService.saveAccountRegistrationDetailsAndSetDataInDO( accountRegistration );
            AccountRegistrationResponse response = accountRegistrationTransformer
                .transformDomainObjectToApiResponse( accountRegistration );
            LOGGER.info( "AccountController.initAccountRegsitration completed successfully" );
            return new ResponseEntity<AccountRegistrationResponse>( response, HttpStatus.OK );
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
            CompanyProfile companyProfile = accountService.getCompanyProfileDetails( Integer.parseInt( companyId ) );
            CompanyProfileResponse response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
            LOGGER.info( "AccountController.getCompanyProfile completed successfully" );
            return new ResponseEntity<CompanyProfileResponse>( response, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting company profile details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile details")
    public ResponseEntity<?> updateCompanyProfile( @PathVariable ( "companyId") String companyId,
        @Valid @RequestBody CompanyProfileRequest companyProfileRequest )
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfile started" );
            CompanyProfile companyProfile = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfileRequest );
            accountService.updateCompanyProfile( Integer.parseInt( companyId ), companyProfile );
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
        @RequestBody CompanyProfileRequest companyProfileRequest )
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfileImage started" );
            accountService.updateCompanyProfileImage( Integer.parseInt( companyId ), companyProfileRequest.getCompanyLogo() );
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
            List<VerticalsMaster> industryDOs = accountService.getIndustries();
            List<com.realtech.socialsurvey.api.models.Industry> industries = industryTransformer
                .transformDomainObjectListToApiResponseList( industryDOs );
            LOGGER.info( "AccountController.getIndustries completed successfully" );
            return new ResponseEntity<List<com.realtech.socialsurvey.api.models.Industry>>( industries, HttpStatus.OK );
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
            List<PaymentPlan> planDOs = accountService.getPaymentPlans();
            List<com.realtech.socialsurvey.api.models.PaymentPlan> plans = paymentPlanTransformer
                .transformDomainObjectListToApiResponseList( planDOs );
            LOGGER.info( "AccountController.getPaymentPlans completed successfully" );
            return new ResponseEntity<List<com.realtech.socialsurvey.api.models.PaymentPlan>>( plans, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting payment plans: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
