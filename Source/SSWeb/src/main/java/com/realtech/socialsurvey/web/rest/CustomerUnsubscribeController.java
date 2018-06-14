/**
 * 
 */
package com.realtech.socialsurvey.web.rest;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * @author Subhrajit
 *
 */

@Controller
@RequestMapping(value = "/unsubscribe")
public class CustomerUnsubscribeController
{
    private static final Logger LOG = LoggerFactory.getLogger( CustomerUnsubscribeController.class );
    
    @Autowired
    private URLGenerator urlGenerator;
    
    @Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;
    
    @RequestMapping(value = "/customeremail", method = RequestMethod.GET)
    public String unsubscribeCustomeEmail(Model model, HttpServletRequest request, @RequestParam("q") String query ) {
        query = "q="+query;
        Map<String, String> params = null;
        String message = null;
        try {
            params = urlGenerator.decryptUrl( query );
            long agentId = Long.parseLong( params.get( CommonConstants.AGENT_ID_COLUMN ) );
            long companyId = Long.parseLong( params.get( CommonConstants.COMPANY_ID_COLUMN ) );
            String emailId = params.get( CommonConstants.CUSTOMER_EMAIL_COLUMN );
            
            // API call to unsubscribe the customer email id.
            message = ssApiIntergrationBuilder.getIntegrationApi().unsubscribeEmail( companyId, emailId, agentId, true );
        } catch ( Exception e ) {
            message = CommonConstants.STATUS_UNSUBSCRIBE_FAILED;
            LOG.warn( "Exception occured while decrypting the URL",e );
        }
        LOG.info( message );
        model.addAttribute( "message", message );
        return JspResolver.UNSUBSCRIBE_MESSAGE;
    }
    

}
