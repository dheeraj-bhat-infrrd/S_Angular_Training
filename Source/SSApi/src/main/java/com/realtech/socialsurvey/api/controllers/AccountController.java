package com.realtech.socialsurvey.api.controllers;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.api.models.CompanyProfile;
import com.realtech.socialsurvey.api.models.request.AccountRegistrationRequest;
import com.realtech.socialsurvey.api.transformers.CompanyProfileTransformer;
import com.realtech.socialsurvey.api.validators.AccountRegistrationValidator;
import com.realtech.socialsurvey.api.validators.CompanyProfileValidator;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyCompositeEntity;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.PaymentPlan;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.VerticalsMaster;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.UserAlreadyExistsException;
import com.realtech.socialsurvey.core.services.api.AccountService;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
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
    private FileUploadService fileUploadService;

    @Value ( "${CDN_PATH}")
    private String endpoint;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String logoBucket;


    @Autowired
    public AccountController( AccountRegistrationValidator accountRegistrationValidator, AccountService accountService,
        CompanyProfileTransformer companyProfileTransformer, CompanyProfileValidator companyProfileValidator,
        OrganizationManagementService organizationManagementService, FileUploadService fileUploadService )
    {
        this.accountRegistrationValidator = accountRegistrationValidator;
        this.accountService = accountService;
        this.companyProfileTransformer = companyProfileTransformer;
        this.companyProfileValidator = companyProfileValidator;
        this.organizationManagementService = organizationManagementService;
        this.fileUploadService = fileUploadService;
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
        throws InvalidInputException, UserAlreadyExistsException, SolrException, NoRecordsFetchedException, NonFatalException
    {
        LOGGER.info( "AccountController.initAccountRegsitration started" );
        User user = new User();
        user.setFirstName( accountRegistrationRequest.getFirstName() );
        user.setLastName( accountRegistrationRequest.getLastName() );
        user.setEmailId( accountRegistrationRequest.getEmail() );
        Map<String, Long> ids = accountService.saveAccountRegistrationDetailsAndGetIdsInMap( user,
            accountRegistrationRequest.getCompanyName(), accountRegistrationRequest.getPhone() );
        LOGGER.info( "AccountController.initAccountRegsitration completed successfully" );
        return new ResponseEntity<Map<String, Long>>( ids, HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/details/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile details")
    public ResponseEntity<?> getCompanyProfile( @PathVariable ( "companyId") String companyId ) throws InvalidInputException
    {
        LOGGER.info( "AccountController.getCompanyProfile started" );
        CompanyCompositeEntity companyProfile = accountService.getCompanyProfileDetails( Long.parseLong( companyId ) );
        CompanyProfile response = companyProfileTransformer.transformDomainObjectToApiResponse( companyProfile );
        LOGGER.info( "AccountController.getCompanyProfile completed successfully" );
        return new ResponseEntity<CompanyProfile>( response, HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/update/{companyId}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update company profile details")
    public ResponseEntity<?> updateCompanyProfile( @Valid @RequestBody CompanyProfile companyProfile,
        @PathVariable ( "companyId") String companyId ) throws InvalidInputException
    {
        LOGGER.info( "AccountController.updateCompanyProfile started" );
        long compId = Long.parseLong( companyId );
        OrganizationUnitSettings unitSettings = organizationManagementService.getCompanySettings( compId );
        Company company = organizationManagementService.getCompanyById( compId );
        CompanyCompositeEntity companyProfileDetails = companyProfileTransformer
            .transformApiRequestToDomainObject( companyProfile, company, unitSettings );
        accountService.updateCompanyProfile( compId, companyProfileDetails );
        LOGGER.info( "AccountController.updateCompanyProfile completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/profileimage/remove/{companyId}", method = RequestMethod.DELETE)
    @ApiOperation ( value = "Delete company profile image")
    public ResponseEntity<?> deleteCompanyProfileImage( @PathVariable ( "companyId") String companyId )
        throws InvalidInputException
    {
        LOGGER.info( "AccountController.deleteCompanyProfileImage started" );
        accountService.deleteCompanyProfileImage( Long.parseLong( companyId ) );
        LOGGER.info( "AccountController.deleteCompanyProfileImage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/profileimage/update/{companyId}/{logoName}", method = RequestMethod.POST)
    @ApiOperation ( value = "Update company profile image")
    public ResponseEntity<?> updateCompanyProfileImage( @PathVariable ( "companyId") String companyId,
        @PathVariable ( "logoName") String logoName, @RequestBody MultipartFile fileLocal ) throws InvalidInputException
    {
        LOGGER.info( "AccountController.updateCompanyProfileImage started" );
        String logoUrl = fileUploadService.uploadLogo( fileLocal, logoName );
        logoUrl = endpoint + CommonConstants.FILE_SEPARATOR + logoBucket + CommonConstants.FILE_SEPARATOR + logoName;
        accountService.updateCompanyProfileImage( Long.parseLong( companyId ), logoUrl );
        LOGGER.info( "AccountController.updateCompanyProfileImage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/stage/update/{companyId}/{stage}", method = RequestMethod.PUT)
    @ApiOperation ( value = "Update stage")
    public ResponseEntity<?> updateStage( @PathVariable ( "companyId") String companyId, @PathVariable ( "stage") String stage )
    {
        LOGGER.info( "AccountController.updateStage started" );
        accountService.updateStage( Long.parseLong( companyId ), stage );
        LOGGER.info( "AccountController.updateStage completed successfully" );
        return new ResponseEntity<Void>( HttpStatus.OK );
    }


    @RequestMapping ( value = "/company/profile/industries", method = RequestMethod.GET)
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


    @RequestMapping ( value = "/company/profile/stage/{companyId}", method = RequestMethod.GET)
    @ApiOperation ( value = "Get company profile stage")
    public ResponseEntity<?> getCompanyStage( @PathVariable ( "companyId") String companyId )
    {
        Company company = organizationManagementService.getCompanyById( Long.parseLong( companyId ) );
        return new ResponseEntity<String>( company.getRegistrationStage(), HttpStatus.OK );
    }
}
