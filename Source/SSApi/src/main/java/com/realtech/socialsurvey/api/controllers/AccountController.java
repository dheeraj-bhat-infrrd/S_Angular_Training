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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.CompanyProfile;
import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.models.request.PaymentRequest;
import com.realtech.socialsurvey.api.transformers.CompanyProfileTransformer;
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.api.validators.CompanyProfileValidator;
import com.realtech.socialsurvey.api.validators.PaymentRequestValidator;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.StateLookup;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v1")
public class AccountController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( AccountController.class );
    private AccountRegistrationValidator accountRegistrationValidator;
    private PaymentRequestValidator paymentRequestValidator;
    private AccountService accountService;
    private CompanyProfileTransformer companyProfileTransformer;
    private CompanyProfileValidator companyProfileValidator;
    private OrganizationManagementService organizationManagementService;


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator,
        PaymentRequestValidator paymentRequestValidator, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer, CompanyProfileValidator companyProfileValidator,
        OrganizationManagementService organizationManagementService )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.paymentRequestValidator = paymentRequestValidator;
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


    @InitBinder ( "paymentRequestValidator")
    public void paymentRequestBinder( WebDataBinder binder )
    {
        binder.setValidator( paymentRequestValidator );
    }


    @RequestMapping ( value = "/companies/register", method = RequestMethod.POST)
    @ApiOperation ( value = "Initiate account registration")
    public ResponseEntity<?> initAccountRegsitration(
        @Valid @RequestBody AccountRegistrationRequest accountRegistrationRequest ) throws SSApiException
    {
        try {
            LOGGER.info( "AccountController.initAccountRegsitration started" );
            User user = new User();
            user.setFirstName( accountRegistrationRequest.getFirstName() );
            user.setLastName( accountRegistrationRequest.getLastName() );
            user.setEmailId( accountRegistrationRequest.getEmail() );
            Map<String, Long> ids = accountService.saveAccountRegistrationDetailsAndGetIdsInMap( user,
                accountRegistrationRequest.getCompanyName(), accountRegistrationRequest.getPhone(),
                accountRegistrationRequest.getPlanId() );
            LOGGER.info( "AccountController.initAccountRegsitration completed successfully" );
            return new ResponseEntity<Map<String, Long>>( ids, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile details")
    public ResponseEntity<?> getCompanyProfile( @PathVariable ( "companyId") String companyId ) throws SSApiException
    {
        try {
            LOGGER.info( "AccountController.getCompanyProfile started" );
            CompanyCompositeEntity companyProfile = accountService.getCompanyProfileDetails( Long.parseLong( companyId ) );
            CompanyProfile response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
            LOGGER.info( "AccountController.getCompanyProfile completed successfully" );
            return new ResponseEntity<CompanyProfile>( response, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile details")
    public ResponseEntity<?> updateCompanyProfile( @Valid @RequestBody CompanyProfile companyProfile,
        @PathVariable ( "companyId") String companyId, @RequestParam ( "userId") String userId ) throws SSApiException
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfile started" );
            long compId = Long.parseLong( companyId );
            long userIdLong = Long.parseLong( userId );
            OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( compId );
            Company company = organizationManagementService.getCompanyById( compId );
            CompanyCompositeEntity companyProfileDetails = companyProfileTransformer
                .transformApiRequestToDomainObject( companyProfile, company, unitSettings );
            accountService.updateCompanyProfile( compId, userIdLong, companyProfileDetails );
            LOGGER.info( "AccountController.updateCompanyProfile completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/profileimage", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete company profile image")
    public ResponseEntity<?> deleteCompanyProfileImage( @PathVariable ( "companyId") String companyId,
        @RequestParam ( "userId") String userId ) throws SSApiException
    {
        try {
            LOGGER.info( "AccountController.deleteCompanyProfileImage started" );
            accountService.deleteCompanyProfileImage( Long.parseLong( companyId ), Long.parseLong( userId ) );
            LOGGER.info( "AccountController.deleteCompanyProfileImage completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/profileimage", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile image")
    public ResponseEntity<?> updateCompanyProfileImage( @PathVariable ( "companyId") String companyId,
        @RequestParam ( "userId") String userId, @RequestBody String logoUrl ) throws SSApiException
    {
        try {
            LOGGER.info( "AccountController.updateCompanyProfileImage started" );
            accountService.updateCompanyProfileImage( Long.parseLong( companyId ), Long.parseLong( userId ), logoUrl );
            LOGGER.info( "AccountController.updateCompanyProfileImage completed successfully" );
            return new ResponseEntity<String>( logoUrl, HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/stage/{stage}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update stage")
    public ResponseEntity<?> updateStage( @PathVariable ( "companyId") String companyId, @PathVariable ( "stage") String stage )
    {
        LOGGER.info( "AccountController.updateStage started" );
        accountService.updateStage( Long.parseLong( companyId ), stage );
        LOGGER.info( "AccountController.updateStage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/industries", method = RequestMethod.GET)
    @ApiOperation ( value = "Get industries drop down data")
    public ResponseEntity<?> getIndustries()
    {
        LOGGER.info( "AccountController.getIndustries started" );
        List<VerticalsMaster> industries = accountService.getIndustries();
        LOGGER.info( "AccountController.getIndustries completed successfully" );
        return new ResponseEntity<List<VerticalsMaster>>( industries, HttpStatus.OK );
    }


    @RequestMapping ( value = "/payment/plans", method = RequestMethod.GET)
    @ApiOperation ( value = "Get payment plans")
    public ResponseEntity<?> getPaymentPlans()
    {
        LOGGER.info( "AccountController.getPaymentPlans started" );
        List<PaymentPlan> plans = accountService.getPaymentPlans();
        LOGGER.info( "AccountController.getPaymentPlans completed successfully" );
        return new ResponseEntity<List<PaymentPlan>>( plans, HttpStatus.OK );
    }


    @RequestMapping ( value = "/companies/{companyId}/stage", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile stage")
    public ResponseEntity<?> getCompanyStage( @PathVariable ( "companyId") String companyId )
    {
        Company company = organizationManagementService.getCompanyById( Long.parseLong( companyId ) );
        return new ResponseEntity<String>( company.getRegistrationStage(), HttpStatus.OK );
    }


    @RequestMapping ( value = "/companies/{companyId}/plan/{planId}/payment", method = RequestMethod.POST)
    @ApiOperation ( value = "Payment for company for a particular plan")
    public ResponseEntity<?> payForPlan( @Valid @RequestBody PaymentRequest paymentRequest, @PathVariable long companyId,
        @PathVariable int planId ) throws SSApiException
    {
        try {
            LOGGER.info( "Payment initiated for company id " + companyId + " for plan id: " + planId );
            accountService.payForPlan( companyId, planId, paymentRequest.getNonce(), paymentRequest.getCardHolderName(),
                paymentRequest.getName(), paymentRequest.getEmail(), paymentRequest.getMessage() );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/companies/{companyId}/hierarchy", method = RequestMethod.POST)
    @ApiOperation ( value = "Generate default company heirarchy")
    public ResponseEntity<?> generateDefaultHierarchyForCompany( @PathVariable ( "companyId") String companyId )
        throws SSApiException
    {
        try {
            //Generate default company hierarchy for company
            LOGGER.info( "AccountController.generateDefaultHierarchyForCompany started" );
            accountService.generateDefaultHierarchy( Long.parseLong( companyId ) );
            LOGGER.info( "AccountController.generateDefaultHierarchyForCompany completed successfully" );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e.getErrorCode() );
        }
    }


    @RequestMapping ( value = "/usstates", method = RequestMethod.GET)
    @ApiOperation ( value = "Get US states list")
    public ResponseEntity<?> getUsStateList()
    {
        LOGGER.info( "AccountController.getUsStateList started" );
        List<StateLookup> lookups = accountService.getUsStatesList();
        LOGGER.info( "AccountController.getUsStateList completed successfully" );
        return new ResponseEntity<List<StateLookup>>( lookups, HttpStatus.OK );
    }
}
