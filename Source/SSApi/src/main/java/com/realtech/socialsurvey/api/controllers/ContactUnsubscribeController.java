/**
 * 
 */
package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.services.contact.ContactUnsubscribeService;

/**
 * @author user345
 *
 */

@RestController
@RequestMapping("/v1")
public class ContactUnsubscribeController {

    private static final Logger LOG = LoggerFactory.getLogger( ContactUnsubscribeController.class );

    @Autowired
    private ContactUnsubscribeService contactUnsubscribeService;

    @PostMapping( value = "/unsubscribe/contact" )
    public ResponseEntity<String> unsubscribeContact( @RequestParam( value = "companyId", required = false ) Long companyId, 
    		@RequestParam( value = "contactNumber" ) String contactNumber, @RequestParam( value = "agentId", required = false ) Long agentId, 
    		@RequestParam( value = "flag" ) boolean flag, @RequestParam( value = "modifiedBy", defaultValue = "0" ) int modifiedBy,
    		@RequestParam( value = "incomingMessageBody", defaultValue = "" ) String messageBody )
    {
        LOG.info( "API call to unsubscribe contact number with args: companyId:{}, contactNumber:{}, agentId:{}, flag:{} ",
            companyId, contactNumber, agentId, flag );
        String message = "";

        if ( companyId == null ) {
            companyId = 0L;
        }
        if ( agentId == null ) {
            agentId = 0L;
        }
        if ( flag ) {
            message = contactUnsubscribeService.unsubscribeContact( companyId, contactNumber, agentId, modifiedBy, messageBody );
        } else {
            message = contactUnsubscribeService.resubscribeContact( companyId, contactNumber, modifiedBy, messageBody );
        }
        return new ResponseEntity<>( message, HttpStatus.OK );
    }
    
    @GetMapping( value = "/unsubscribe/contact/isunsubscribed" )
	public ResponseEntity<Boolean> isContactNumberUnsubscribed(@RequestParam( value = "contactNumber" ) String contactNumber,
			@RequestParam( value = "companyId", required = false ) Long companyId ) {
	    boolean flag = false;
	    if ( companyId == null ) {
            companyId = 0L;
        }
	    flag = contactUnsubscribeService.isUnsubscribed( companyId, contactNumber );
	    return new ResponseEntity<>( flag, HttpStatus.OK );
	}
}
