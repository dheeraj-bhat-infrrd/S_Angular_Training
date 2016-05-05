package com.realtech.socialsurvey.api.controllers;

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
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.api.validators.CompanyProfileValidator;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
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


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator,
        AccountRegistrationTransformer accountRegistrationTransformer, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer, CompanyProfileValidator companyProfileValidator )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountRegistrationTransformer = accountRegistrationTransformer;
        this.accountService = accountService;
        this.companyProfileTransformer = companyProfileTransformer;
        this.companyProfileValidator = companyProfileValidator;
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
            LOGGER.info( "initAccountRegsitration started" );
            AccountRegistration accountRegistration = accountRegistrationTransformer
                .transformApiRequestToDomainObject( accountRegistrationRequest );
            accountService.saveAccountRegistrationDetailsAndSetDataInDO( accountRegistration );
            AccountRegistrationResponse response = accountRegistrationTransformer
                .transformDomainObjectToApiResponse( accountRegistration );
            LOGGER.info( "initAccountRegsitration completed successfully" );
            return new ResponseEntity<AccountRegistrationResponse>( response, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while initiating account registration: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/details/{userId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile details")
    public ResponseEntity<?> getCompanyProfile( @PathVariable ( "userId") String userId )
    {
        try {
            LOGGER.info( "getCompanyProfile started" );
            CompanyProfile companyProfile = accountService.getCompanyProfileDetails( Integer.parseInt( userId ) );
            CompanyProfileResponse response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
            LOGGER.info( "getCompanyProfile completed successfully" );
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
            LOGGER.info( "updateCompanyProfile started" );
            CompanyProfile companyProfile = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfileRequest );
            accountService.updateCompanyProfile( Integer.parseInt( companyId ), companyProfile );
            LOGGER.info( "updateCompanyProfile completed successfully" );
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
            LOGGER.info( "deleteCompanyProfileImage started" );
            accountService.deleteCompanyProfileImage( Integer.parseInt( companyId ) );
            LOGGER.info( "deleteCompanyProfileImage completed successfully" );
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
            LOGGER.info( "updateCompanyProfileImage started" );
            accountService.updateCompanyProfileImage( Integer.parseInt( companyId ), companyProfileRequest.getCompanyLogo() );
            LOGGER.info( "updateCompanyProfileImage completed successfully" );
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
            LOGGER.info( "updateStage started" );
            accountService.updateStage( Integer.parseInt( companyId ), stage );
            LOGGER.info( "updateStage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating stage: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
