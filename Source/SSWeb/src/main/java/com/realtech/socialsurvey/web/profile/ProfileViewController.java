/**
 * Entry point for profile view pages.
 */
package com.realtech.socialsurvey.web.profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.PublicProfileAggregate;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.web.handler.ModelAndViewHandler;


@Controller
public class ProfileViewController
{

    private static final Logger LOG = LoggerFactory.getLogger( ProfileViewController.class );

    @Autowired
    private ProfileManagementService profileManagementService;

    @Autowired
    private ModelAndViewHandler modelAndViewHandler;


    /**
     * Method to return company profile page
     * 
     * @param profileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/company/{profileName}" }, method = RequestMethod.GET)
    public String initCompanyProfilePage( @PathVariable String profileName, Model model, HttpServletRequest request,
        HttpServletResponse response, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate company profile page called" );
        boolean isBotRequest = false;
        PublicProfileAggregate profileAggregate = null;
        String profileTemplate = null;
        try {
            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_COMPANY );
            profileAggregate.setProfileName( profileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( profileName.toLowerCase() );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate company profile page executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return company profile page with review pop-up that belongs to the company
     * 
     * @param profileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/company/{profileName}/{surveyId}" }, method = RequestMethod.GET)
    public String initCompanyProfilePageWithReviewPopup( @PathVariable String profileName, @PathVariable String surveyId,
        Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes )
        throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate company profile page with review popup called" );
        boolean isBotRequest = false;
        PublicProfileAggregate profileAggregate = null;
        String profileTemplate = null;
        try {
            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_COMPANY );
            profileAggregate.setProfileName( profileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( profileName.toLowerCase() );
            profileAggregate.setSurveyId( surveyId );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate company profile page with review popup executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_COMPANY_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_COMPANY_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return region profile page
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */

    @RequestMapping ( value = { "/region/{companyProfileName}/{regionProfileName}" })
    public String initRegionProfilePage( @PathVariable String companyProfileName, @PathVariable String regionProfileName,
        Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes )
        throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate region profile page called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_REGION );
            profileAggregate.setProfileName( regionProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( companyProfileName.toLowerCase() );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate region profile page executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_REGION_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return region profile page with review pop-up that belongs to the region
     * 
     * @param companyProfileName
     * @param regionProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */

    @RequestMapping ( value = { "/region/{companyProfileName}/{regionProfileName}/{surveyId}" })
    public String initRegionProfilePageWithReviewPopup( @PathVariable String companyProfileName,
        @PathVariable String regionProfileName, @PathVariable String surveyId, Model model, HttpServletRequest request,
        HttpServletResponse response, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate region profile page with review popup called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_REGION );
            profileAggregate.setProfileName( regionProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( companyProfileName.toLowerCase() );
            profileAggregate.setSurveyId( surveyId );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate region profile page with review popup executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_REGION_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_REGION_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return branch profile page
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/office/{companyProfileName}/{branchProfileName}" })
    public String initBranchProfilePage( @PathVariable String companyProfileName, @PathVariable String branchProfileName,
        Model model, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes )
        throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate branch profile page called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_BRANCH );
            profileAggregate.setProfileName( branchProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( companyProfileName.toLowerCase() );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate branch profile page executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return branch profile page with a review pop-up that belongs to the branch
     * 
     * @param companyProfileName
     * @param branchProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/office/{companyProfileName}/{branchProfileName}/{surveyId}" })
    public String initBranchProfilePageWithReviewPopup( @PathVariable String companyProfileName,
        @PathVariable String branchProfileName, @PathVariable String surveyId, Model model, HttpServletRequest request,
        HttpServletResponse response, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate branch profile page with review popup called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_BRANCH );
            profileAggregate.setProfileName( branchProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( companyProfileName.toLowerCase() );
            profileAggregate.setSurveyId( surveyId );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate branch profile page with the review popup executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_BRANCH_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_BRANCH_PROFILE, error,
                model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return agent profile page
     * 
     * @param agentProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/{agentProfileName}" })
    public String initAgentProfilePage( @PathVariable String agentProfileName, Model model, HttpServletResponse response,
        HttpServletRequest request, RedirectAttributes redirectAttributes ) throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate agent profile page called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_INDIVIDUAL );
            profileAggregate.setProfileName( agentProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( null );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );

            LOG.info( "Service to initiate agent profile page executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE,
                error, model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method to return agent profile page with a review pop-up
     * 
     * @param agentProfileName
     * @param model
     * @return
     * @throws NoRecordsFetchedException 
     */
    @RequestMapping ( value = { "/{agentProfileName}/{surveyId}" })
    public String initAgentProfilePageWithReviewPopup( @PathVariable String agentProfileName, @PathVariable String surveyId,
        Model model, HttpServletResponse response, HttpServletRequest request, RedirectAttributes redirectAttributes )
        throws NoRecordsFetchedException
    {
        LOG.info( "Service to initiate agent profile page with review popup called" );
        boolean isBotRequest = false;
        String profileTemplate = null;
        PublicProfileAggregate profileAggregate = null;

        try {

            // check if the request is from BOT
            isBotRequest = modelAndViewHandler.isItABotRequest( request );

            profileAggregate = new PublicProfileAggregate();

            profileAggregate.setProfileLevel( CommonConstants.PROFILE_LEVEL_INDIVIDUAL );
            profileAggregate.setProfileName( agentProfileName.toLowerCase() );
            profileAggregate.setCompanyProfileName( null );
            profileAggregate.setSurveyId( surveyId );

            // build up the relevant profile data and populate the model object 
            profileAggregate = profileManagementService.buildPublicProfileAggregate( profileAggregate, isBotRequest );

            //return the appropriate profile template
            profileTemplate = modelAndViewHandler.handlePublicProfileModelAndView( model, profileAggregate, isBotRequest );
            
            String smImage = profileManagementService.getSMImageOfReviewer(surveyId);
            
            if(smImage != null && !smImage.isEmpty()) {
            	model.addAttribute( "isOgImageChange", "true" );
            	model.addAttribute( "smImage", smImage);
            }

            LOG.info( "Service to initiate agent profile page with review popup executed successfully" );

        } catch ( Exception error ) {
            profileTemplate = modelAndViewHandler.handlePublicProfileExceptionsModelAndView( profileAggregate,
                CommonConstants.ERROR_CODE_INDIVIDUAL_PROFILE_SERVICE_FAILURE, CommonConstants.SERVICE_CODE_INDIVIDUAL_PROFILE,
                error, model, redirectAttributes, response );
        }

        return profileTemplate;
    }


    /**
     * Method called on click of the contact us link on all profile pages
     * @param request
     * @return
     */
    @RequestMapping ( value = "/profile/sendmail", method = RequestMethod.POST)
    public @ResponseBody String sendEmail( HttpServletRequest request )
    {
    	
    		String profileName = request.getParameter("profilename");
    		String profileType = request.getParameter("profiletype");
    		String companyProfileName = request.getParameter("companyprofilename");

    		String senderName = request.getParameter("name");
    		String senderMailId = request.getParameter("email");
    		String message = request.getParameter( CommonConstants.MESSAGE);

    		String captchaResponse = request.getParameter( CommonConstants.GOOGLE_CAPTCHA_RESPONSE);
    		
        LOG.info( "Contact us mail controller called!" );

        String returnMessage = null;
        int status = CommonConstants.STATUS_INACTIVE;

        try {

            if ( profileManagementService.isCaptchaForContactUsMailProcessed( request.getRemoteAddr(), captchaResponse ) ) {

                LOG.debug( "Sending mail to :  {} from : {}", profileName, senderMailId );

                profileManagementService.findProfileMailIdAndSendMail( companyProfileName, profileName, message, senderName,
                    senderMailId, profileType );
                LOG.debug( "Mail sent!" );

                status = CommonConstants.STATUS_ACTIVE;
                returnMessage = modelAndViewHandler
                    .handlePublicProfileContactUsMailModelAndView( CommonConstants.SUCCESS_ATTRIBUTE );

            } else {
                returnMessage = modelAndViewHandler
                    .handlePublicProfileContactUsMailModelAndView( CommonConstants.INVALID_CAPTCHA );
            }

        } catch ( Exception error ) {
            LOG.error( error.toString(), error );
            returnMessage = modelAndViewHandler.handlePublicProfileContactUsMailModelAndView( error );
        }

        return profileManagementService.buildJsonMessageWithStatus( status, returnMessage );
    }

}
