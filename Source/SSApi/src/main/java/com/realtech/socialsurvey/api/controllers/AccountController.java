package com.realtech.socialsurvey.api.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
import com.realtech.socialsurvey.core.entities.api.CompanyProfile;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.wordnik.swagger.annotations.ApiOperation;


/**
 * @author Shipra Goyal, RareMile
 *
 */

@RestController
@RequestMapping ( "/account")
public class AccountController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountController.class );
    private AccountRegistrationValidator accountRegistrationValidator;
    private AccountRegistrationTransformer accountRegistrationTransformer;
    private AccountService accountService;
    private CompanyProfileTransformer companyProfileTransformer;


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator,
        AccountRegistrationTransformer accountRegistrationTransformer, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountRegistrationTransformer = accountRegistrationTransformer;
        this.accountService = accountService;
        this.companyProfileTransformer = companyProfileTransformer;
    }


    @InitBinder
    public void signUpBinder( WebDataBinder binder )
    {
        binder.setValidator( accountRegistrationValidator );
    }


    @RequestMapping ( value = "/register/init", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> initAccountRegsitration(
        @Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest )
    {
        try {
            AccountRegistration accountRegistration = accountRegistrationTransformer
                .transformApiRequestToDomainObject( accountRegistrationRequest );
            accountService.saveAccountRegistrationDetailsAndSetDataInDO( accountRegistration );
            AccountRegistrationResponse response = accountRegistrationTransformer
                .transformDomainObjectToApiResponse( accountRegistration );
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
            CompanyProfile companyProfile = accountService.getCompanyProfileDetails( Integer.parseInt( userId ) );
            CompanyProfileResponse response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
            return new ResponseEntity<CompanyProfileResponse>( response, HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while getting company profile details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/phase1/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile phase 1 details")
    public ResponseEntity<?> updateCompanyProfilePhase1( @PathVariable ( "companyId") String companyId,
        @Valid @RequestBody CompanyProfileRequest companyProfilePhase1Request )
    {
        try {
            CompanyProfile companyProfile = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfilePhase1Request );
            accountService.updateCompanyProfile( Integer.parseInt( companyId ), companyProfile );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating company profile phase 1 details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/phase2/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile phase 2 details")
    public ResponseEntity<?> updateCompanyProfilePhase2( @PathVariable ( "companyId") String companyId,
        @Valid @RequestBody CompanyProfileRequest companyProfilePhase2Request )
    {
        try {
            CompanyProfile companyProfile = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfilePhase2Request );
            accountService.updateCompanyProfile( Integer.parseInt( companyId ), companyProfile );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating company profile phase 2 details: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }


    @RequestMapping ( value = "/company/profile/profileimage/remove/{companyId}", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete company profile image")
    public ResponseEntity<?> deleteCompanyProfileImage( @PathVariable ( "companyId") String companyId )
    {
        try {
            accountService.deleteCompanyProfileImage( Integer.parseInt( companyId ) );
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
        @Valid @RequestBody CompanyProfileRequest companyProfileImageRequest, BindingResult errors )
    {
        try {
            CompanyProfile companyProfile = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfileImageRequest );
            accountService.updateCompanyProfileImage( Integer.parseInt( companyId ), companyProfile.getCompanyLogo() );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception ex ) {
            if ( LOGGER.isDebugEnabled() ) {
                LOGGER.debug( "Exception thrown while updating user profile image: " + ex.getMessage() );
            }
            return new ResponseEntity<Void>( HttpStatus.BAD_REQUEST );
        }
    }
}
