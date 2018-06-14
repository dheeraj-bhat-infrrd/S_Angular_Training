/**
 * 
 */
package com.realtech.socialsurvey.api.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.realtech.socialsurvey.core.services.mail.EmailUnsubscribeService;

/**
 * @author Subhrajit
 *
 */

@RestController
@RequestMapping("/v1")
public class EmailUnsubscribeController {
	
	private static final Logger LOG = LoggerFactory.getLogger( EmailUnsubscribeController.class );
	
	@Autowired
	private EmailUnsubscribeService emailUnsubscribeService;
	

    @RequestMapping ( value = "/unsubscribe/email", method = RequestMethod.POST)
    public String unsubscribeEmail( Long companyId, String emailId, Long agentId, boolean flag )
    {
        LOG.info( "API call to unsubscribe email." );
        String message = "";
        
        if(companyId == null) {
            companyId = 0L;
        }
        if(agentId == null) {
            agentId = 0L;
        }
        if ( flag ) {
            message = emailUnsubscribeService.unsubscribeEmail( companyId, emailId, agentId );
        } else {
            message = emailUnsubscribeService.resubscribeEmail( companyId, emailId );
        }
        return message;
    }
	
	@RequestMapping( value = "/unsubscribe/isunsubscribed", method = RequestMethod.GET)
	public ResponseEntity<Boolean> isUnsubscribed(String emailId, long companyId) {
	    boolean flag = false;
	    flag = emailUnsubscribeService.isUnsubscribed(emailId,companyId);
	    return new ResponseEntity<Boolean>( flag, HttpStatus.OK );
	}

}
