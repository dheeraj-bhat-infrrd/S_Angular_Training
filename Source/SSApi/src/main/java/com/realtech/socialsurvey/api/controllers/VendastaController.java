package com.realtech.socialsurvey.api.controllers;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.VendastaRmAccountResponse;
import com.realtech.socialsurvey.api.models.VendastaRmAccountVO;
import com.realtech.socialsurvey.api.transformers.VendastaRmAccountTransformer;
import com.realtech.socialsurvey.api.validators.VendastaRmAccountValidator;
import com.realtech.socialsurvey.core.entities.VendastaRmAccount;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;
import io.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/v1")
public class VendastaController
{
    private static final Logger LOGGER = LoggerFactory.getLogger( VendastaController.class );
    private VendastaRmAccountValidator vendastaRmAccountValidator;
    private VendastaManagementService vendastaManagementService;
    private VendastaRmAccountTransformer vendastaRmAccountTransformer;


    @Autowired
    public VendastaController( VendastaRmAccountValidator vendastaRmAccountValidator,
        VendastaManagementService vendastaManagementService, VendastaRmAccountTransformer vendastaRmAccountTransformer )
    {
        this.vendastaRmAccountValidator = vendastaRmAccountValidator;
        this.vendastaManagementService = vendastaManagementService;
        this.vendastaRmAccountTransformer = vendastaRmAccountTransformer;
    }


    @InitBinder ( "vendastaRmAccountVO")
    public void vendastaRmAccountBinder( WebDataBinder binder )
    {
        binder.setValidator( vendastaRmAccountValidator );
    }


    @RequestMapping ( value = "/vendasta/rm/account/create", method = RequestMethod.POST)
    @ApiOperation ( value = "Create vendasta rm account")
    public ResponseEntity<?> createVendastaRmAccount( @Valid @RequestBody VendastaRmAccountVO vendastaRmAccountVO,
        @RequestParam ( value = "isForced", defaultValue = "false") boolean isForced ) throws SSApiException
    {
        LOGGER.info( "Creating Vendasta RM account for entityId: " + vendastaRmAccountVO.getEntityId() + ", entityType: "
            + vendastaRmAccountVO.getEntityType() );
        try {
            Object obj = null;
            VendastaRmAccount vendastaRmAccount = vendastaRmAccountTransformer
                .transformApiRequestToDomainObject( vendastaRmAccountVO, obj );
            Map<String, Object> dataMap = vendastaManagementService.validateAndCreateRmAccount( vendastaRmAccount, isForced );
            String message = "Error in creating RM account.";
            String customerIdentifier = null;
            boolean isAlreadyExistingAccount = false;
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if ( dataMap != null && dataMap.size() > 0 ) {
                if ( dataMap.get( "customerIdentifier" ) != null ) {
                    customerIdentifier = (String) dataMap.get( "customerIdentifier" );
                }
                if ( dataMap.get( "isAlreadyExistingAccount" ) != null ) {
                    isAlreadyExistingAccount = (boolean) dataMap.get( "isAlreadyExistingAccount" );
                }
                if ( customerIdentifier != null ) {
                    if ( isForced ) {
                        message = "Successfully created RM account.";
                        httpStatus = HttpStatus.CREATED;
                    } else if ( isAlreadyExistingAccount ) {
                        message = "RM account already existing.";
                        httpStatus = HttpStatus.OK;
                    } else {
                        message = "Successfully created RM account.";
                        httpStatus = HttpStatus.CREATED;
                    }
                }
            }
            VendastaRmAccountResponse response = new VendastaRmAccountResponse( message, vendastaRmAccountVO.getEntityId(),
                vendastaRmAccountVO.getEntityType(), customerIdentifier );
            return new ResponseEntity<VendastaRmAccountResponse>( response, httpStatus );
        } catch ( NonFatalException e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }
}
