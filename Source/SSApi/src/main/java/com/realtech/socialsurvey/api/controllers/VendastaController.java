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

import com.realtech.socialsurvey.api.exceptions.SSApiException;
import com.realtech.socialsurvey.api.models.VendastaRmAccountVO;
import com.realtech.socialsurvey.api.transformers.VendastaRmAccountTransformer;
import com.realtech.socialsurvey.api.validators.VendastaRmAccountValidator;
import com.realtech.socialsurvey.core.entities.VendastaRmAccount;
import com.realtech.socialsurvey.core.services.organizationmanagement.VendastaManagementService;
import com.wordnik.swagger.annotations.ApiOperation;


@RestController
@RequestMapping ( "/vendasta")
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


    @RequestMapping ( value = "/rm/account/create", method = RequestMethod.POST)
    @ApiOperation ( value = "Create vendasta rm account")
    public ResponseEntity<?> createVendastaRmAccount( @Valid @RequestBody VendastaRmAccountVO vendastaRmAccountVO )
        throws SSApiException
    {
        try {
            Object obj = null;
            VendastaRmAccount vendastaRmAccount = vendastaRmAccountTransformer
                .transformApiRequestToDomainObject( vendastaRmAccountVO, obj );
            vendastaManagementService.createRmAccount( vendastaRmAccount );
            return new ResponseEntity<Void>( HttpStatus.OK );
        } catch ( Exception e ) {
            throw new SSApiException( e.getMessage(), e );
        }
    }
}
