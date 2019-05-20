package com.realtech.socialsurvey.web.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.entities.ContactDetailsSettings;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.socialmonitor.feed.SocialFeedService;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


@Controller
public class IncomingMailController
{
    private static final Logger LOG = LoggerFactory.getLogger( IncomingMailController.class );

    @Value ( "${SOCIALSURVEYME_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridMeEmailDomain;
    
    @Value ("${SOCIALSURVEYUS_SENDER_EMAIL_DOMAIN}")
    private String defaultSendGridUsEmailDomain;

    @Value ( "${APPLICATION_ADMIN_EMAIL}")
    private String applicationAdminEmail;

    @Value ( "${DEFAULT_EMAIL_FROM_ADDRESS}")
    private String defaultFromAddress;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;
    
    @Autowired
    private EmailServices emailServices;
    
    @Autowired
    private SocialFeedService socialFeedService;

    @Autowired
    private EncryptionHelper encryptionHelper;

	private static String NULL_USER_EMAIL_ADDRESS = "u-null@alert.socialsurvey.me";
	private static String NULL_USER_EMAIL_ADDRESS_DEMO = "u-null@socialsurvey.me";

	private String EMAIL_REGEX = ".*@([A-Za-z0-9\\.-]+)";
    private Pattern EMAIL_REGEX_PATTERN = Pattern.compile( EMAIL_REGEX );

    /**
     *  Method to handle forwarding of customer reply to corresponding recipient based on below strategy :
     *
     *  if mail replied to an agent mail, then mail will be forwarded to the agent
     *  if mail replied to a default from address, then mail will be send to application admin mail id configured.
     *
     * @param request
     * @return
     * @throws NumberFormatException
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @RequestMapping ( value = "/inboundmail")
    @ResponseBody
    public String inboundMail( HttpServletRequest request ) throws NumberFormatException, InvalidInputException,
        UndeliveredEmailException
    {
        LOG.info( "Method inboundMail() called to forward reply of customer" );
        String subject = request.getParameter( "subject" );
        String mailBody = request.getParameter( "html" );
        String mailFrom = request.getParameter( "from" );
        String mailTo = request.getParameter( "to" );
        String headers = request.getParameter( "headers" );
        
        LOG.info( "sendgrid header: {}",  headers);
        LOG.info( "HTML from sendgrid, {}",  mailBody);

        if ( mailFrom == null || mailFrom.isEmpty() ) {
            LOG.error( "From address is missing from request" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        if ( mailTo == null || mailTo.isEmpty() ) {
            LOG.error( "To address is missing from request" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }

        if ( subject == null || subject.isEmpty() ) {
            LOG.error( "Subject is missing from request" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }

        if ( mailBody == null || mailBody.isEmpty() ) {
            LOG.error( "Mail body content is missing from request" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }

        if ( headers == null || headers.isEmpty() ) {
            LOG.error( "Header content is missing from request" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        

        LOG.info( "Proceeding to resolve mailTo : {}", mailTo );
        LOG.info( "subject: {}\n mailFrom: {}\n mailTo: {}\n header: {}",subject, mailFrom, mailTo, headers);
        String sendUsingDomain = getDefaultDomainFromEmail(mailTo);

        LOG.info( "finished getDefaultDomainFromTo and the sendUsingDomain :{}",sendUsingDomain );

        String postId = getPostIdFromMailTo( mailTo, sendUsingDomain );
        
        if(postId != null) {
            List<String> senderInfo = retrieveSenderInfoFromMailId( mailFrom );
            String senderName = mailFrom;
            if(!CollectionUtils.isEmpty( senderInfo )) {
                senderName = senderInfo.get( 0 );
            }
            socialFeedService.addEmailReplyAsCommentToSocialPost(postId,senderName, mailTo, mailBody, subject );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        
        String resolvedMailto = resolveMailTo( mailTo ,sendUsingDomain );
        if ( resolvedMailto == null || resolvedMailto.isEmpty() ) {
            LOG.error( "Resolved Mail id found null or empty" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        LOG.info( "Resolved mailTo : {}", resolvedMailto );
        LOG.info( "Proceeding to retrieve sender information from mailFrom :{}", mailFrom );
        List<String> senderInfo = retrieveSenderInfoFromMailId( mailFrom );
        if ( senderInfo == null || senderInfo.isEmpty() ) {
            LOG.error( "Sender Info found null or empty" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        LOG.info( "Proceeding to retrieve message id from headers found in request" );
        String messageId = parseHeaderForMessageId( headers );
        if ( messageId == null || messageId.isEmpty() ) {
            LOG.error( "Message ID found null or empty" );
            return CommonConstants.SENDGRID_OK_STATUS;
        }
        LOG.info( "Message Id : {}", messageId );
        LOG.info( "calling forwardCustomerReplyMail" );
        emailServices.forwardCustomerReplyMail( resolvedMailto, subject, mailBody, senderInfo.get( 0 ), senderInfo.get( 1 ),
            messageId , sendUsingDomain);
        LOG.info( "finished forwardCustomerReplyMail" );
        LOG.info( "Method inboundMail() call ended to forward reply of customer" );

        return CommonConstants.SENDGRID_OK_STATUS;
    }
    

    private String getDefaultDomainFromEmail( String emailId )
    {
        //Get the domain from the email ID
        if ( emailId == null || emailId.isEmpty() ) {
            LOG.error( "Mail To passed cannot be null or empty" );
            return null;
        }
        //check if the value after @ is either of the default values
        String emailDomain = null ;
        Matcher matcher = EMAIL_REGEX_PATTERN.matcher(emailId);
        if(matcher.find()){
         emailDomain = matcher.group(1);
        }
        
        if ( emailDomain == null || emailDomain.isEmpty() ) {
            return null;
        } else if ( emailDomain.equals( defaultSendGridMeEmailDomain ) ) {
            return defaultSendGridMeEmailDomain;
        } else if ( emailDomain.equals( defaultSendGridUsEmailDomain ) ) {
            return defaultSendGridUsEmailDomain;
        } else {
            return defaultSendGridMeEmailDomain;
        }
    }


    private String resolveMailTo( String mailTo , String defaultEmailDomain ) throws NumberFormatException, InvalidInputException
    {
        if ( mailTo == null || mailTo.isEmpty() ) {
            LOG.error( "Mail To passed cannot be null or empty" );
            return null;
        }

        LOG.info( "Method resolveMailTo() called to resolve mail to email id" );
        String resolvedMailId = mailTo;
        
        //user email regex
        String agentEmailRegex = "u-(.*)@" + Matcher.quoteReplacement( defaultEmailDomain );
        Pattern agentTegexPattern = Pattern.compile( agentEmailRegex );
        Matcher agentRegexMatcher = agentTegexPattern.matcher( mailTo );
        
        //company email regex
        String companyEmailRegex = "c-(.*)@" + Matcher.quoteReplacement( defaultEmailDomain );
        Pattern companyRegexPattern = Pattern.compile( companyEmailRegex );
        Matcher companyRegexMatcher = companyRegexPattern.matcher( mailTo );
        
        if ( agentRegexMatcher.find() ) {
            LOG.info( "Mail id belongs to agent mail id format" );
            String userEncryptedId = agentRegexMatcher.group( 1 );
            LOG.info( "userEncryptedId id from the mail id is {}", userEncryptedId );
            ContactDetailsSettings contactDetailsSettings = userManagementService.fetchAgentContactDetailByEncryptedId( userEncryptedId );
            if(contactDetailsSettings != null && contactDetailsSettings.getMail_ids() != null  )
                resolvedMailId = contactDetailsSettings.getMail_ids().getWork();
            if ( userEncryptedId == null )
	            resolvedMailId = applicationAdminEmail;
        }
        else if( companyRegexMatcher.find() ) {
        		LOG.info( "Mail id belongs to company mail id format" );
        		String encryptedId = companyRegexMatcher.group( 1 );
        		ContactDetailsSettings contactDetailsSettings = organizationManagementService.fetchContactDetailByEncryptedId(encryptedId, MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
                if(contactDetailsSettings != null && contactDetailsSettings.getMail_ids() != null  )
                    resolvedMailId = contactDetailsSettings.getMail_ids().getWork();
                if ( encryptedId == null )
    	            		resolvedMailId = applicationAdminEmail;
        } else if ( mailTo.contains( defaultFromAddress ) || mailTo.contains( applicationAdminEmail ) || mailTo
	        .contains( NULL_USER_EMAIL_ADDRESS ) || mailTo.contains( NULL_USER_EMAIL_ADDRESS_DEMO ) ) {
	        resolvedMailId = applicationAdminEmail;
        }
        LOG.info( "Mail id resolved to : {}", resolvedMailId );
        LOG.info( "Method resolveMailTo() call ended to resolve mail to email id" );
        return resolvedMailId;
    }
    
    private String getPostIdFromMailTo( String mailTo , String defaultEmailDomain ) throws InvalidInputException
    {
        if ( mailTo == null || mailTo.isEmpty() ) {
            LOG.error( "Mail To passed cannot be null or empty" );
            return null;
        }

        LOG.info( "Method getPostIdFromMailTo() called to get post it from email id" );
        
        //user email regex
        String agentEmailRegex = "post-(.*)@" + Matcher.quoteReplacement( defaultEmailDomain );
        Pattern agentTegexPattern = Pattern.compile( agentEmailRegex );
        Matcher agentRegexMatcher = agentTegexPattern.matcher( mailTo );
        
        if ( agentRegexMatcher.find() ) {
            LOG.info( "Mail id contains post id to add comment" );
            String userEncryptedId = agentRegexMatcher.group( 1 );
            return userEncryptedId;
        } else {
            return null;
        }
    }


    private List<String> retrieveSenderInfoFromMailId( String mailFrom )
    {
        if ( mailFrom == null || mailFrom.isEmpty() ) {
            LOG.error( "Mail From passed cannot be null or empty" );
            return Collections.emptyList();
        }
        LOG.info( "Method retrieveSenderInfoFromMailId() called to retrieve sender information from mail from id" );
        List<String> senderInfo = new ArrayList<>();
        String parts[] = mailFrom.split( "<" );
        if ( parts.length == 2 ) {
            senderInfo.add( parts[0].trim() );
            senderInfo.add( parts[1].replaceAll( ">", "" ).trim() );
        }
        LOG.info( "Sender Information : {}", senderInfo );
        LOG.info( "Method retrieveSenderInfoFromMailId() call ended to retrieve sender information from mail from id" );
        return senderInfo;
    }


    private String parseHeaderForMessageId( String headers )
    {
    	LOG.info("method parseHeaderForMessageId is start parsing header: {} ", headers );
        if ( headers == null || headers.isEmpty() ) {
            LOG.error( "Headers passed cannot be null or empty" );
            return null;
        }

        LOG.info( "Method parseHeaderForMessageId() called to parse header information for message id" );
        String messageIDRegex = "Message-ID:\\s+<(.*)>";
        Pattern pattern = Pattern.compile( messageIDRegex );
        Matcher matcher = pattern.matcher( headers );
        if ( matcher.find() ) {
            if(LOG.isDebugEnabled()) {
                LOG.info( "Found Message id in message header : {}", matcher.group( 1 ) );
            }
            LOG.info( "Found Message id in message header : {}", matcher.group( 1 ) );
            return matcher.group( 1 ).trim();
        }
        LOG.error( "Could not find message Id in the header" );
        LOG.info( "Method parseHeaderForMessageId() call ended to parse header information message id" );
        return null;
    }
}
