package com.realtech.socialsurvey.web.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.sendgridmanagement.SendgridManagementService;
import com.realtech.socialsurvey.core.vo.SendgridUnsubscribeVO;
import com.realtech.socialsurvey.web.common.JspResolver;

/**
 * 
 * @author rohit
 *
 */
@RestController
@RequestMapping(value = "/sendgrid")
public class SendgridController
{
    
    private static final Logger LOG = LoggerFactory.getLogger( SendgridController.class );
    
    
    @Autowired
    SendgridManagementService sendgridManagementService; 
    
    @RequestMapping ( value = "/addnewunsubscribeemail",  method = RequestMethod.POST)
    public String addNewunSubscribeEmail( @QueryParam ( value = "emailId") String emailId)
    {
        LOG.info( "Method addNewunSubscribeEmail started with email id {}", emailId );
        try {
            if ( emailId == null || emailId.isEmpty() ) {
                throw new InvalidInputException( "The emailId parameter is empty" );
            }

            sendgridManagementService.addNewEmailToUnsubscribeList( emailId );
        } catch (  NonFatalException e ) {
            return "false";
        }
        LOG.info( "Method addNewunSubscribeEmail started with email id {}", emailId );
        return "true";
    }
    
    @RequestMapping ( value = "/removeunsubscribedemail",  method = RequestMethod.POST)
    public Boolean removeUnsubscribedEmail( @QueryParam ( value = "emailId") String emailId)
    {
        LOG.info( "Method removeUnsubscribedEmail started with email id {}", emailId );
        try {
            if ( emailId == null || emailId.isEmpty() ) {
                throw new InvalidInputException( "The emailId parameter is empty" );
            }

            sendgridManagementService.removewEmailFromUnsubscribeList( emailId );
        } catch (  NonFatalException e ) {
            return false;
        }
        LOG.info( "Method removeUnsubscribedEmail started with email id {}", emailId );
        return true;
    }
    
    
    @RequestMapping ( value = "/getunsubscribedemails",  method = RequestMethod.GET)
    public List<SendgridUnsubscribeVO> getUnsubscribedEmails( @QueryParam ( value = "emailId") String emailId)
    {
        LOG.info( "Method getUnsubscribedEmails started with email id {}", emailId );
        List<SendgridUnsubscribeVO> sendgridUnsubscribeVOs =  null;
        try {
            if ( emailId == null || emailId.isEmpty() ) {
                throw new InvalidInputException( "The emailId parameter is empty" );
            }
            sendgridUnsubscribeVOs =  sendgridManagementService.getUnsubscribedEmailList();
        } catch (  NonFatalException e ) {
            LOG.error( "Eror while getting unsubscribed email list " );
        }
        LOG.info( "Method getUnsubscribedEmails started with email id {}", emailId );
        return sendgridUnsubscribeVOs;
    }

    @RequestMapping ( value = "/showunsubscribeemailpage",  method = RequestMethod.GET)
    public ModelAndView showUnsubscribeEmailPage(Model model , HttpServletRequest request) {
        LOG.info("Method to start survey showUnsubscribeEmailPage() started.");
        String logoUrl = "";
        ModelAndView mav = new ModelAndView(JspResolver.SENDGRID_EMAIL_MNGMNT);
        model.addAttribute("logo", logoUrl);
        LOG.info("Method to start survey showUnsubscribeEmailPage() finished.");
        return mav;
    } 
   
}
