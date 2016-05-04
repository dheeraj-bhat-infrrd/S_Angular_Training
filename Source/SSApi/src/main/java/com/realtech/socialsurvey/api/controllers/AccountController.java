package com.realtech.socialsurvey.api.controllers;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.models.response.AccountRegistrationResponse;
import com.realtech.socialsurvey.api.transformers.AccountRegistrationTransformer;
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.core.entities.api.AccountRegistration;
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


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator,
        AccountRegistrationTransformer accountRegistrationTransformer, AccountService accountService )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountRegistrationTransformer = accountRegistrationTransformer;
        this.accountService = accountService;
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
}
