package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.support.UserSupportService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.services.upload.impl.UploadUtils;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.BotRequestUtils;

@Controller
public class HelpController {

	private static final Logger LOG = LoggerFactory.getLogger( HelpController.class );

    // JIRA SS-97 by RM-06 : BOC
    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private UploadUtils uploadUtils;

    @Autowired
    private SessionHelper sessionHelper;

    @Autowired
    private UrlValidationHelper urlValidationHelper;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private SolrSearchService solrSearchService;

    @Value ( "${APPLICATION_BASE_URL}")
    private String applicationBaseUrl;

    @Value ( "${CDN_PATH}")
    private String amazonEndpoint;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String amazonImageBucket;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String amazonLogoBucket;

    @Autowired
    private BotRequestUtils botRequestUtils;
    
    @Autowired
    private UserSupportService userSupportService;


    @Transactional
    @RequestMapping ( value = "/showhelppage", method = RequestMethod.GET)
    public String showHelpPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method showProfileEditPage() called from ProfileManagementService" );
        return JspResolver.HELP_EDIT;
    }
    
    
    @ResponseBody
    @RequestMapping ( value = "/sendhelpmailtoadmin")
    public String sendHelpMailToAdmin( HttpServletRequest request,  @RequestParam String subject , @RequestParam String mailText , @RequestParam String emailId ){
    	LOG.info( "Method sendHelpMailToAdmin() called" );
    	User user = sessionHelper.getCurrentUser();
    	try{
    	    if(subject.isEmpty() || subject == null){
    	        throw new InvalidInputException( "Mail Subject can't be empty" );
    	    }
    	    
    	    if(mailText.isEmpty() || mailText == null){
                throw new InvalidInputException( "Mail Body can't be empty" );
            }
    	    
            if(emailId.isEmpty() || emailId == null){
                throw new InvalidInputException( "Email Address can't be empty" );
            }
    	    
    		List<MultipartFile> attachmentList = new ArrayList<MultipartFile>();
    		Map<String , String > attachmentsDetails = userSupportService.saveAttachmentLocally(attachmentList);
    		
    		userSupportService.sendHelpMailToAdmin( emailId, user.getFirstName() + ( user.getLastName() != null ? " " + user.getLastName() : "" ), subject, mailText, attachmentsDetails );

    	}catch(NonFatalException e){
    		LOG.error("Erroe in sending the help mail : " + e.getMessage());
    		return messageUtils.getDisplayMessage(DisplayMessageConstants.ERROR_IN_SENDING_HELP_MESSAGE, DisplayMessageType.ERROR_MESSAGE).getMessage();
    	}
		
    	return messageUtils.getDisplayMessage(DisplayMessageConstants.HELP_MESSAGE_SUCCESSFULLY_SEND, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
    }
}