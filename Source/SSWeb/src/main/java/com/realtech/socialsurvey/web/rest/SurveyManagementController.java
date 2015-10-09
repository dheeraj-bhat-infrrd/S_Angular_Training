package com.realtech.socialsurvey.web.rest;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import twitter4j.TwitterException;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.RequestUtils;

import facebook4j.FacebookException;


// JIRA SS-119 by RM-05 : BOC
@Controller
@RequestMapping ( value = "/survey")
public class SurveyManagementController
{

    private static final Logger LOG = LoggerFactory.getLogger( SurveyManagementController.class );

    @Autowired
    private SurveyBuilder surveyBuilder;

    @Autowired
    private SurveyHandler surveyHandler;

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private URLGenerator urlGenerator;

    @Autowired
    private UrlValidationHelper urlValidationHelper;

    @Autowired
    private SolrSearchService solrSearchService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private SocialManagementService socialManagementService;

    @Autowired
    private OrganizationManagementService organizationManagementService;

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private RequestUtils requestUtils;

    @Autowired
    private EmailFormatHelper emailFormatHelper;

    @Autowired
    private ProfileManagementService profileManagementService;

    @Resource
    @Qualifier ( "nocaptcha")
    private CaptchaValidation captchaValidation;

    @Value ( "${ENABLE_KAFKA}")
    private String enableKafka;

    @Value ( "${VALIDATE_CAPTCHA}")
    private String validateCaptcha;

    @Value ( "${CAPTCHA_SECRET}")
    private String captchaSecretKey;

    @Value ( "${GATEWAY_QUESTION}")
    private String gatewayQuestion;


    /*
     * Method to store answer to the current question of the survey.
     */
    @ResponseBody
    @RequestMapping ( value = "/data/storeAnswer")
    public String storeSurveyAnswer( HttpServletRequest request )
    {
        LOG.info( "Method storeSurveyAnswer() started to store response of customer." );
        String answer = request.getParameter( "answer" );
        String question = request.getParameter( "question" );
        String questionType = request.getParameter( "questionType" );
        int stage = Integer.parseInt( request.getParameter( "stage" ) );
        String customerEmail = request.getParameter( "customerEmail" );
        long agentId = Long.valueOf( request.getParameter( "agentId" ) );
        surveyHandler.updateCustomerAnswersInSurvey( agentId, customerEmail, question, questionType, answer, stage );
        LOG.info( "Method storeSurveyAnswer() finished to store response of customer." );
        return surveyHandler.getSwearWords();
    }


    /*
     * Method to store final feedback of the survey from customer.
     */
    @ResponseBody
    @RequestMapping ( value = "/data/storeFeedback")
    public String storeFeedbackAndCloseSurvey( HttpServletRequest request )
    {
        LOG.info( "Method storeFeedback() started to store response of customer." );

        // To store final feedback provided by customer in mongoDB.
        try {
            String feedback = request.getParameter( "feedback" );
            String mood = request.getParameter( "mood" );
            String customerEmail = request.getParameter( "customerEmail" );
            String firstName = request.getParameter( "firstName" );
            String lastName = request.getParameter( "lastName" );
            String agreedToShare = request.getParameter( "agreedToShare" );

            long agentId = 0;
            try {
                String agentIdStr = request.getParameter( "agentId" );
                if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                    LOG.error( "Null/empty value found for agentId in storeFeedback()." );
                    throw new InvalidInputException( "Null/empty value found for agentId in storeFeedback()." );
                }
                agentId = Long.valueOf( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException occurred in storeFeedback() while getting agentId." );
            }

            boolean isAbusive = Boolean.parseBoolean( request.getParameter( "isAbusive" ) );
            surveyHandler.updateGatewayQuestionResponseAndScore( agentId, customerEmail, mood, feedback, isAbusive,
                agreedToShare );
            surveyHandler.increaseSurveyCountForAgent( agentId );
            SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey( agentId, customerEmail, firstName,
                lastName );
            surveyHandler.deleteSurveyPreInitiationDetailsPermanently( surveyPreInitiation );

            // TODO Search Engine Optimization
            if ( mood == null || mood.isEmpty() ) {
                LOG.error( "Null/empty value found for mood in storeFeedback()." );
                throw new InvalidInputException( "Null/empty value found for mood in storeFeedback()." );
            }
            Map<String, String> emailIdsToSendMail = new HashMap<>();
            SolrDocument solrDocument = null;

            try {
                solrDocument = solrSearchService.getUserByUniqueId( agentId );
            } catch ( SolrException e ) {
                LOG.error( "SolrException occurred in storeFeedback() while fetching email id of agent. NEsted exception is ",
                    e );
            }

            if ( solrDocument != null && !solrDocument.isEmpty() ) {
                emailIdsToSendMail.put( solrDocument.get( CommonConstants.USER_EMAIL_ID_SOLR ).toString(),
                    solrDocument.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString() );
            }

            String moodsToSendMail = surveyHandler.getMoodsToSendMail();
            if ( !moodsToSendMail.isEmpty() && moodsToSendMail != null ) {
                List<String> moods = new ArrayList<>( Arrays.asList( moodsToSendMail.split( "," ) ) );
                if ( moods.contains( mood ) ) {
                    emailIdsToSendMail.putAll( surveyHandler.getEmailIdsOfAdminsInHierarchy( agentId ) );
                }
            }

            // Sending email to the customer telling about successful completion of survey.
            SurveyDetails survey = surveyHandler.getSurveyDetails( agentId, customerEmail, firstName, lastName );
            try {
                String customerName = emailFormatHelper.getCustomerDisplayNameForEmail( survey.getCustomerFirstName(),
                    survey.getCustomerLastName() );
                User agent = userManagementService.getUserByUserId( agentId );

                String logoUrl = userManagementService.fetchAppropriateLogoUrlFromHierarchyForUser( agent.getUserId() );
                LOG.info( "logourl is : " + logoUrl + " for user " + agent.getUserId() );
                if ( enableKafka.equals( CommonConstants.YES ) ) {
                    emailServices.queueSurveyCompletionMail( customerEmail, customerName, survey.getAgentName(),
                        agent.getEmailId(), agent.getProfileName() );
                } else {
                    surveyHandler.sendSurveyCompletionMail( customerEmail, survey.getCustomerFirstName(),
                        survey.getCustomerLastName(), agent );
                }

                // Generate the text as in mail
                String surveyDetail = generateSurveyTextForMail( customerName, mood, survey, isAbusive );
                String surveyScore = String.valueOf( survey.getScore() );
                if ( enableKafka.equals( CommonConstants.YES ) ) {
                    for ( Entry<String, String> admin : emailIdsToSendMail.entrySet() ) {
                        emailServices.queueSurveyCompletionMailToAdminsAndAgent( admin.getValue(), admin.getKey(),
                            surveyDetail, customerName, surveyScore );
                    }
                } else {
                    for ( Entry<String, String> admin : emailIdsToSendMail.entrySet() ) {
                        emailServices.sendSurveyCompletionMailToAdminsAndAgent( admin.getValue(), admin.getKey(), surveyDetail,
                            customerName, surveyScore );
                    }
                }
            } catch ( InvalidInputException e ) {
                LOG.error( "Exception occurred while trying to send survey completion mail to : " + customerEmail );
                throw e;
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Non fatal exception caught in storeFeedback(). Nested exception is ", e );
            return e.getMessage();
        }
        LOG.info( "Method storeFeedback() finished to store response of customer." );
        return "Survey stored successfully";
    }


    private String generateSurveyTextForMail( String customerName, String mood, SurveyDetails survey, boolean isAbusive )
    {
        StringBuilder surveyDetail = new StringBuilder( customerName ).append( " Complete Survey Response for " ).append(
            survey.getAgentName() );
        surveyDetail.append( "<br />" ).append( "Date Sent: " ).append( survey.getCreatedOn().toString() );
        surveyDetail.append( "<br />" ).append( "Date Completed: " ).append( survey.getModifiedOn().toString() );
        surveyDetail.append( "<br />" ).append( "Average Score: " ).append( String.valueOf( survey.getScore() ) );

        int count = 1;
        surveyDetail.append( "<br />" );
        for ( SurveyResponse response : survey.getSurveyResponse() ) {
            surveyDetail.append( "<br />" ).append( "Question " + count + ": " ).append( response.getQuestion() );
            surveyDetail.append( "<br />" ).append( "Response to Q" + count + ": " ).append( response.getAnswer() );
            count++;
        }

        surveyDetail.append( "<br />" );
        surveyDetail.append( "<br />" ).append( "Gateway Question: " ).append( gatewayQuestion );
        surveyDetail.append( "<br />" ).append( "Response to GQ: " ).append( mood );
        surveyDetail.append( "<br />" ).append( "Customer Comments: " ).append( survey.getReview() );

        surveyDetail.append( "<br />" );
        if ( survey.getAgreedToShare() != null && survey.getAgreedToShare().equalsIgnoreCase( "true" ) ) {
            surveyDetail.append( "<br />" ).append( "Share Checkbox: " ).append( "Yes" );
            if ( survey.getSharedOn() != null && !survey.getSharedOn().isEmpty() ) {
                surveyDetail.append( "<br />" ).append( "Shared on: " ).append( StringUtils.join( survey.getSharedOn(), ", " ) );
            }
        } else {
            surveyDetail.append( "<br />" ).append( "Share Checkbox: " ).append( "No" );
        }

        surveyDetail.append( "<br />" );
        if ( isAbusive ) {
            surveyDetail.append( "<br />" ).append( "Contains abusive words : " ).append( "Yes" );
        } else {
            surveyDetail.append( "<br />" ).append( "Contains abusive words: " ).append( "No" );
        }

        // update survey details with values
        String surveyDetailStr = surveyDetail.toString();
        surveyDetailStr = surveyDetailStr.replaceAll( "\\[name\\]", survey.getAgentName() );
        surveyDetailStr = surveyDetailStr.replaceAll( "\\[Name\\]", survey.getAgentName() );

        return surveyDetailStr;
    }


    @ResponseBody
    @RequestMapping ( value = "/redirecttodetailspage")
    public String showDetailsPage( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to redirect to survey page started." );
        String agentId = request.getParameter( "userId" );
        LOG.info( "Method to redirect to survey page started." );
        return getApplicationBaseUrl() + "rest/survey/showsurveypage/" + agentId;
    }


    @RequestMapping ( value = "/showsurveypage/{agentIdStr}")
    public String initiateSurvey( Model model, @PathVariable String agentIdStr )
    {
        LOG.info( "Method to start survey initiateSurvey() started." );
        if ( agentIdStr == null || agentIdStr.isEmpty() ) {
            LOG.error( "Invalid agentId passed. Agent Id can not be null or empty." );
            return JspResolver.ERROR_PAGE;
        }
        Long agentId = 0l;
        try {
            agentId = Long.parseLong( agentIdStr );
        } catch ( NumberFormatException e ) {
            LOG.error( "Invalid agent Id passed. Error is : " + e );
            return JspResolver.ERROR_PAGE;
        }
        String agentName = "";
        String agentEmail = "";
        try {
            SolrDocument user = solrSearchService.getUserByUniqueId( agentId );
            if ( user != null ) {
                agentName = user.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString();
                agentEmail = user.get( CommonConstants.USER_EMAIL_ID_SOLR ).toString();
            }
        } catch ( InvalidInputException | SolrException e ) {
            LOG.error( "Error occured while fetching details of agent. Error is : " + e );
            return JspResolver.ERROR_PAGE;
        }
        model.addAttribute( "agentId", agentId );
        model.addAttribute( "agentName", agentName );
        model.addAttribute( "agentEmail", agentEmail );
        LOG.info( "Method to start survey initiateSurvey() finished." );
        return JspResolver.SHOW_SURVEY_QUESTIONS;
    }


    @RequestMapping ( value = "/showsurveypageforurl")
    public String initiateSurveyWithUrl( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to start survey initiateSurveyWithUrl() started." );
        String param = request.getParameter( "q" );
        model.addAttribute( "q", param );
        LOG.info( "Method to start survey initiateSurveyWithUrl() finished." );
        return JspResolver.SHOW_SURVEY_QUESTIONS;
    }


    /*
     * Method to retrieve survey questions for a survey based upon the company id and agent id.
     */
    @RequestMapping ( value = "/triggersurvey")
    public String triggerSurvey( Model model, HttpServletRequest request )
    {
        LOG.info( "Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started." );

        long agentId = 0;
        String customerEmail;
        String firstName;
        String lastName;
        String custRelationWithAgent;
        String agentName;
        String source;
        customerEmail = request.getParameter( CommonConstants.CUSTOMER_EMAIL_COLUMN );
        firstName = request.getParameter( "firstName" );
        lastName = request.getParameter( "lastName" );
        agentName = request.getParameter( "agentName" );
        source = "customer";
        // custRelationWithAgent = request.getParameter("relationship");
        // TODO:remove customer relation with agent
        custRelationWithAgent = "transacted";
        String errorMsg = null;
        try {
            try {
                String agentIdStr = request.getParameter( CommonConstants.AGENT_ID_COLUMN );
                agentId = Long.parseLong( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error( "NumberFormatException caught in triggerSurvey(). Details are " + e );
                throw e;
            }

            if ( validateCaptcha.equals( CommonConstants.YES_STRING ) ) {
                if ( !captchaValidation.isCaptchaValid( request.getRemoteAddr(), captchaSecretKey,
                    request.getParameter( "g-recaptcha-response" ) ) ) {
                    LOG.error( "Captcha Validation failed!" );
                    errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_CAPTCHA,
                        DisplayMessageType.ERROR_MESSAGE ).getMessage();
                    throw new InvalidInputException( errorMsg, DisplayMessageConstants.INVALID_CAPTCHA );
                }
            }

            model.addAttribute( "agentId", agentId );
            model.addAttribute( "firstName", firstName );
            model.addAttribute( "lastName", lastName );
            model.addAttribute( "customerEmail", customerEmail );
            model.addAttribute( "relation", custRelationWithAgent );
            model.addAttribute( "source", source );

            User user = userManagementService.getUserByUserId( agentId );
            
            try{
                surveyHandler.initiateSurveyRequest( user.getUserId(), customerEmail, firstName, lastName, source );
            }catch(SelfSurveyInitiationException e){
                errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.SELF_SURVEY_INITIATION,
                    DisplayMessageType.ERROR_MESSAGE ).getMessage();
                throw new NonFatalException( e.getMessage() , e.getErrorCode() );
            }catch(DuplicateSurveyRequestException e){
                errorMsg = messageUtils.getDisplayMessage( DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST,
                    DisplayMessageType.ERROR_MESSAGE ).getMessage();
                throw new NonFatalException( e.getMessage() , e.getErrorCode() );
            }

        } catch ( NonFatalException e ) {
            LOG.error( "Exception caught in getSurvey() method of SurveyManagementController." );
            model.addAttribute( "status", DisplayMessageType.ERROR_MESSAGE );
            if(errorMsg != null)
                model.addAttribute( "message", errorMsg );
            else
                model.addAttribute( "message", messageUtils.getDisplayMessage( DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE ) );
            model.addAttribute( "agentId", agentId );
            model.addAttribute( "agentName", agentName );
            model.addAttribute( "firstName", firstName );
            model.addAttribute( "lastName", lastName );
            model.addAttribute( "customerEmail", customerEmail );
            model.addAttribute( "relation", custRelationWithAgent );
            return JspResolver.SHOW_SURVEY_QUESTIONS;
        }
        LOG.info( "Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started." );
        return JspResolver.SURVEY_INVITE_SUCCESSFUL;
    }


    @ResponseBody
    @RequestMapping ( value = "/triggersurveywithurl", method = RequestMethod.GET)
    public String triggerSurveyWithUrl( HttpServletRequest request )
    {
        LOG.info( "Method to start survey when URL is given, triggerSurveyWithUrl() started." );
        Map<String, Object> surveyAndStage = new HashMap<>();
        try {
            String url = request.getParameter( "q" );
            Map<String, String> urlParams = urlGenerator.decryptParameters( url );
            if ( urlParams != null ) {
                long agentId = Long.parseLong( urlParams.get( CommonConstants.AGENT_ID_COLUMN ) );
                String customerEmail = urlParams.get( CommonConstants.CUSTOMER_EMAIL_COLUMN );
                String custFirstName = urlParams.get( CommonConstants.FIRST_NAME );
                String custLastName = urlParams.get( CommonConstants.LAST_NAME );

                SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey( agentId, customerEmail,
                    custFirstName, custLastName );
                if ( surveyPreInitiation == null ) {
                    surveyAndStage = getSurvey( agentId, customerEmail, custFirstName, custLastName, 0, null,
                        surveyHandler.composeLink( agentId, customerEmail, custFirstName, custLastName ),
                        MongoSocialPostDaoImpl.KEY_SOURCE_SS );
                } else {
                    surveyAndStage = getSurvey( agentId, urlParams.get( CommonConstants.CUSTOMER_EMAIL_COLUMN ),
                        surveyPreInitiation.getCustomerFirstName(), surveyPreInitiation.getCustomerLastName(),
                        surveyPreInitiation.getReminderCounts(), surveyPreInitiation.getCustomerInteractionDetails(),
                        surveyHandler.composeLink( agentId, customerEmail, custFirstName, custLastName ),
                        surveyPreInitiation.getSurveySource() );

                    surveyHandler.markSurveyAsStarted( surveyPreInitiation );
                }

                // fetching company logo
                User user = userManagementService.getUserByUserId( agentId );
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings( user );
                if ( companySettings != null && companySettings.getLogo() != null ) {
                    surveyAndStage.put( "companyLogo", companySettings.getLogo() );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in triggerSurveyWithUrl()." );
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setErrCode( ErrorCodes.REQUEST_FAILED );
            errorResponse.setErrMessage( e.getMessage() );
            String errorMessage = new Gson().toJson( errorResponse );
            return errorMessage;
        }
        LOG.info( "Method to start survey when URL is given, triggerSurveyWithUrl() finished." );
        return new Gson().toJson( surveyAndStage );
    }


    @ResponseBody
    @RequestMapping ( value = "/posttosocialnetwork", method = RequestMethod.GET)
    public String postToSocialMedia( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to various pages of social networking sites started." );

        try {
            String agentName = request.getParameter( "agentName" );
            String agentProfileLink = request.getParameter( "agentProfileLink" );
            String custFirstName = request.getParameter( "firstName" );
            String custLastName = request.getParameter( "lastName" );
            String agentIdStr = request.getParameter( "agentId" );
            String ratingStr = request.getParameter( "rating" );
            String customerEmail = request.getParameter( "customerEmail" );
            String feedback = request.getParameter( "feedback" );
            String serverBaseUrl = requestUtils.getRequestServerName( request );

            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );

            long agentId = 0;
            double rating = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
                rating = Double.parseDouble( ratingStr );
            } catch ( NumberFormatException | NullPointerException e ) {
                LOG.error(
                    "Number format/Null Pointer exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ",
                    e );
                return e.getMessage();
            }
            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            if ( rating % 1 == 0 ) {
                ratingFormat = CommonConstants.SOCIAL_RANKING_WHOLE_FORMAT;
            }
            User agent = userManagementService.getUserByUserId( agentId );
            int accountMasterId = 0;
            try {
                accountMasterId = agent.getCompany().getLicenseDetails().get( CommonConstants.INITIAL_INDEX )
                    .getAccountsMaster().getAccountsMasterId();
            } catch ( NullPointerException e ) {
                LOG.error( "NullPointerException caught in postToSocialMedia() while fetching account master id for agent "
                    + agent.getFirstName() );
            }

            List<OrganizationUnitSettings> settings = socialManagementService
                .getSettingsForBranchesAndRegionsInHierarchy( agentId );
            AgentSettings agentSettings = userManagementService.getUserSettings( agentId );

            // Facebook
            String facebookMessage = ratingFormat.format( rating ) + "-Star Survey Response from " + customerDisplayName
                + " for " + agentName + " on Social Survey - view at " + getApplicationBaseUrl()
                + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            facebookMessage += "\n Feedback : " + feedback;
            try {
                if ( !socialManagementService.updateStatusIntoFacebookPage( agentSettings, facebookMessage, serverBaseUrl,
                    agent.getCompany().getCompanyId() ) ) {
                    surveyHandler.updateSharedOn( CommonConstants.FACEBOOK_SOCIAL_SITE, agentId, customerEmail );
                }
            } catch ( FacebookException e ) {
                LOG.error(
                    "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e );
            }
            if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                for ( OrganizationUnitSettings setting : settings ) {
                    try {
                        if ( !socialManagementService.updateStatusIntoFacebookPage( setting, facebookMessage, serverBaseUrl,
                            agent.getCompany().getCompanyId() ) ) {
                            surveyHandler.updateSharedOn( CommonConstants.FACEBOOK_SOCIAL_SITE, agentId, customerEmail );
                        }
                    } catch ( FacebookException e ) {
                        LOG.error(
                            "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                            e );
                    }
                }
            }

            // LinkedIn
            String linkedinMessage = ratingFormat.format( rating ) + "-Star Survey Response from " + customerDisplayName
                + " for " + agentName + " on SocialSurvey ";
            String linkedinProfileUrl = getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            String linkedinMessageFeedback = "From : " + customerDisplayName + " - " + feedback;
            if ( !socialManagementService.updateLinkedin( agentSettings, linkedinMessage, linkedinProfileUrl,
                linkedinMessageFeedback ) ) {
                surveyHandler.updateSharedOn( CommonConstants.LINKEDIN_SOCIAL_SITE, agentId, customerEmail );
            }
            if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                for ( OrganizationUnitSettings setting : settings ) {
                    if ( !socialManagementService.updateLinkedin( setting, linkedinMessage, linkedinProfileUrl,
                        linkedinMessageFeedback ) ) {
                        surveyHandler.updateSharedOn( CommonConstants.LINKEDIN_SOCIAL_SITE, agentId, customerEmail );
                    }
                }
            }

            // Twitter
            /*
             * String twitterMessage = rating + "-Star Survey Response from " + customerDisplayName
             * + " for " + agentName + " on @SocialSurveyMe - view at " + getApplicationBaseUrl() +
             * CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
             */
            String twitterMessage = String.format( CommonConstants.TWITTER_MESSAGE, ratingFormat.format( rating ),
                customerDisplayName, agentName, "@SocialSurveyMe" )
                + getApplicationBaseUrl()
                + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            try {
                if ( !socialManagementService.tweet( agentSettings, twitterMessage, agent.getCompany().getCompanyId() ) ) {
                    surveyHandler.updateSharedOn( CommonConstants.TWITTER_SOCIAL_SITE, agentId, customerEmail );
                }
            } catch ( TwitterException e ) {
                LOG.error(
                    "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ", e );
            }
            if ( accountMasterId != CommonConstants.ACCOUNTS_MASTER_INDIVIDUAL ) {
                for ( OrganizationUnitSettings setting : settings ) {
                    try {
                        if ( !socialManagementService.tweet( setting, twitterMessage, agent.getCompany().getCompanyId() ) ) {
                            surveyHandler.updateSharedOn( CommonConstants.TWITTER_SOCIAL_SITE, agentId, customerEmail );
                        }
                    } catch ( TwitterException e ) {
                        LOG.error(
                            "TwitterException caught in postToSocialMedia() while trying to post to twitter. Nested excption is ",
                            e );
                    }
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error(
                "Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ",
                e );
            return e.getMessage();
        }
        LOG.info( "Method to post feedback of customer to various pages of social networking sites finished." );
        return "Successfully posted to all the places in hierarchy";
    }


    @ResponseBody
    @RequestMapping ( value = "/posttofacebook", method = RequestMethod.GET)
    public String postToFacebook( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to facebook started." );
        try {
            String urlParam = request.getParameter( "q" );
            Map<String, String> facebookDetails = urlGenerator.decryptParameters( urlParam );
            String agentName = facebookDetails.get( "agentName" );
            String custFirstName = facebookDetails.get( "firstName" );
            String agentProfileLink = facebookDetails.get( "agentProfileLink" );
            String custLastName = facebookDetails.get( "lastName" );
            String agentIdStr = facebookDetails.get( "agentId" );
            String ratingStr = facebookDetails.get( "rating" );
            String customerEmail = facebookDetails.get( "customerEmail" );
            String serverBaseUrl = requestUtils.getRequestServerName( request );

            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );

            long agentId = 0;
            double rating = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
                rating = Double.parseDouble( ratingStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "Number format exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ",
                    e );
                return e.getMessage();
            }
            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            if ( rating % 1 == 0 ) {
                ratingFormat = CommonConstants.SOCIAL_RANKING_WHOLE_FORMAT;
            }
            List<OrganizationUnitSettings> settings = socialManagementService
                .getSettingsForBranchesAndRegionsInHierarchy( agentId );
            AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
            String facebookMessage = ratingFormat.format( rating ) + "-Star Survey Response from " + customerDisplayName
                + " for " + agentName + " on Social Survey - view at " + getApplicationBaseUrl()
                + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            // TODO: Bad code: DELETE: BEGIN
            // get the company id of the agent
            User user = userManagementService.getUserObjByUserId( agentId );
            try {
                socialManagementService.updateStatusIntoFacebookPage( agentSettings, facebookMessage, serverBaseUrl, user
                    .getCompany().getCompanyId() );
                // TODO: Bad code: Remove the comany id from the parameter: End
                surveyHandler.updateSharedOn( CommonConstants.FACEBOOK_SOCIAL_SITE, agentId, customerEmail );
            } catch ( FacebookException e ) {
                LOG.error(
                    "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e );
            }
            for ( OrganizationUnitSettings setting : settings ) {
                try {
                    socialManagementService.updateStatusIntoFacebookPage( setting, facebookMessage, serverBaseUrl, user
                        .getCompany().getCompanyId() );
                } catch ( FacebookException e ) {
                    LOG.error(
                        "FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ",
                        e );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in postToFacebook(). Nested exception is ", e );
        }
        LOG.info( "Method to post feedback of customer to facebook finished." );
        return "";
    }


    @ResponseBody
    @RequestMapping ( value = "/posttotwitter", method = RequestMethod.GET)
    public String postToTwitter( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to twitter started." );
        try {
            String urlParam = request.getParameter( "q" );
            Map<String, String> twitterDetails = urlGenerator.decryptParameters( urlParam );
            String agentName = twitterDetails.get( "agentName" );
            String agentProfileLink = twitterDetails.get( "agentProfileLink" );
            String custFirstName = twitterDetails.get( "firstName" );
            String custLastName = twitterDetails.get( "lastName" );
            String agentIdStr = twitterDetails.get( "agentId" );
            String ratingStr = twitterDetails.get( "rating" );
            String customerEmail = twitterDetails.get( "customerEmail" );

            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );
            long agentId = 0;
            double rating = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
                rating = Double.parseDouble( ratingStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "Number format exception caught in postToTwitter() while trying to convert agent Id. Nested exception is ",
                    e );
                return e.getMessage();
            }
            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            if ( rating % 1 == 0 ) {
                ratingFormat = CommonConstants.SOCIAL_RANKING_WHOLE_FORMAT;
            }
            List<OrganizationUnitSettings> settings = socialManagementService
                .getSettingsForBranchesAndRegionsInHierarchy( agentId );
            AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
            /*
             * String twitterMessage = rating + "-Star Survey Response from " + customerDisplayName
             * + " for " + agentName + " on @SocialSurveyMe - view at " + getApplicationBaseUrl() +
             * CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
             */
            String twitterMessage = String.format( CommonConstants.TWITTER_MESSAGE, ratingFormat.format( rating ),
                customerDisplayName, agentName, "@SocialSurveyMe" )
                + getApplicationBaseUrl()
                + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            // TODO: Bad code: DELETE: BEGIN
            // get the company id of the agent
            User user = userManagementService.getUserObjByUserId( agentId );
            try {
                socialManagementService.tweet( agentSettings, twitterMessage, user.getCompany().getCompanyId() );
                surveyHandler.updateSharedOn( CommonConstants.TWITTER_SOCIAL_SITE, agentId, customerEmail );
            } catch ( TwitterException e ) {
                LOG.error( "TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e );
            }
            for ( OrganizationUnitSettings setting : settings ) {
                try {
                    socialManagementService.tweet( setting, twitterMessage, user.getCompany().getCompanyId() );
                } catch ( TwitterException e ) {
                    LOG.error(
                        "TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e );
                }
            }
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in postToTwitter(). Nested exception is ", e );
        }
        LOG.info( "Method to post feedback of customer to twitter finished." );
        return "";
    }


    @ResponseBody
    @RequestMapping ( value = "/posttolinkedin", method = RequestMethod.GET)
    public String postToLinkedin( HttpServletRequest request )
    {
        LOG.info( "Method to post feedback of customer to linkedin started." );
        try {
            String urlParam = request.getParameter( "q" );
            Map<String, String> linkedinDetails = urlGenerator.decryptParameters( urlParam );
            String agentName = linkedinDetails.get( "agentName" );
            String custFirstName = linkedinDetails.get( "firstName" );
            String custLastName = linkedinDetails.get( "lastName" );
            String agentIdStr = linkedinDetails.get( "agentId" );
            String ratingStr = linkedinDetails.get( "rating" );
            String customerEmail = linkedinDetails.get( "customerEmail" );
            String agentProfileLink = linkedinDetails.get( "agentProfileLink" );
            String feedback = linkedinDetails.get( "feedback" );

            String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail( custFirstName, custLastName );
            long agentId = 0;
            double rating = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
                rating = Double.parseDouble( ratingStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "Number format exception caught in postToLinkedin() while trying to convert agent Id. Nested exception is ",
                    e );
                return e.getMessage();
            }
            DecimalFormat ratingFormat = CommonConstants.SOCIAL_RANKING_FORMAT;
            if ( rating % 1 == 0 ) {
                ratingFormat = CommonConstants.SOCIAL_RANKING_WHOLE_FORMAT;
            }
            List<OrganizationUnitSettings> settings = socialManagementService
                .getSettingsForBranchesAndRegionsInHierarchy( agentId );
            AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
            String message = ratingFormat.format( rating ) + "-Star Survey Response from " + customerDisplayName + " for "
                + agentName + " on SocialSurvey ";
            String linkedinProfileUrl = getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
            String linkedinMessageFeedback = "From : " + customerDisplayName + " " + feedback;
            socialManagementService.updateLinkedin( agentSettings, message, linkedinProfileUrl, linkedinMessageFeedback );
            for ( OrganizationUnitSettings setting : settings ) {
                socialManagementService.updateLinkedin( setting, message, linkedinProfileUrl, linkedinMessageFeedback );
            }
            surveyHandler.updateSharedOn( CommonConstants.LINKEDIN_SOCIAL_SITE, agentId, customerEmail );
        } catch ( NonFatalException e ) {
            LOG.error( "NonFatalException caught in postToTwitter(). Nested exception is ", e );
        }
        LOG.info( "Method to post feedback of customer to twitter finished." );
        return "";
    }


    @ResponseBody
    @RequestMapping ( value = "/displaypiclocationofagent", method = RequestMethod.GET)
    public String getDisplayPicLocationOfAgent( HttpServletRequest request )
    {
        LOG.info( "Method to get location of agent's image started." );
        String picLocation = "";
        String agentIdStr = request.getParameter( "agentId" );
        long agentId = 0;

        if ( agentIdStr == null || agentIdStr.isEmpty() ) {
            LOG.error( "Null value found for agent Id in request param." );
            return "Null value found for agent Id in request param.";
        }

        try {
            agentId = Long.parseLong( agentIdStr );
        } catch ( NumberFormatException e ) {
            LOG.error( "NumberFormatException caught in getDisplayPicLocationOfAgent() while getting agent id" );
            return e.getMessage();
        }

        try {
            AgentSettings agentSettings = userManagementService.getUserSettings( agentId );
            if ( agentSettings != null ) {
                if ( agentSettings.getProfileImageUrl() != null && !agentSettings.getProfileImageUrl().isEmpty() ) {
                    picLocation = agentSettings.getProfileImageUrl();
                }
            }
        } catch ( InvalidInputException e ) {
            LOG.error( "InvalidInputException caught in getDisplayPicLocationOfAgent() while fetching image for agent." );
            return e.getMessage();
        }

        LOG.info( "Method to get location of agent's image finished." );
        return picLocation;
    }


    @ResponseBody
    @RequestMapping ( value = "/getyelplinkrest", method = RequestMethod.GET)
    public String getYelpLinkRest( HttpServletRequest request )
    {
        LOG.info( "Method to get Yelp details, getYelpLink() started." );
        Map<String, String> yelpUrl = new HashMap<String, String>();
        try {
            String agentIdStr = request.getParameter( "agentId" );
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException(
                    "InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty." );
            }

            long agentId = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e );
                throw e;
            }

            OrganizationUnitSettings settings = userManagementService.getUserSettings( agentId );

            if ( settings.getSocialMediaTokens() == null || settings.getSocialMediaTokens().getYelpToken() == null ) {

            } else {
                String validUrl = settings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
                try {
                    validUrl = urlValidationHelper.buildValidUrl( validUrl );
                } catch ( IOException ioException ) {
                    throw new InvalidInputException( "Yelp link passed was invalid", DisplayMessageConstants.GENERAL_ERROR,
                        ioException );
                }

                yelpUrl.put( "relativePath", validUrl );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in getYelpLink() while trying to post into Yelp." );
            ErrorResponse response = new ErrorResponse();
            response.setErrCode( "Error while trying to post on Yelp." );
            response.setErrMessage( e.getMessage() );
            return new Gson().toJson( response );
        }
        LOG.info( "Method to get Yelp details, getYelpLink() finished." );
        return new Gson().toJson( yelpUrl );
    }


    @ResponseBody
    @RequestMapping ( value = "/getgooglepluslinkrest", method = RequestMethod.GET)
    public String getGooglePlusLinkRest( HttpServletRequest request )
    {
        LOG.info( "Method to get Google details, getGooglePlusLink() started." );
        Map<String, String> googleUrl = new HashMap<String, String>();
        try {
            String agentIdStr = request.getParameter( "agentId" );
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException(
                    "InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty." );
            }

            long agentId = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e );
                throw e;
            }

            OrganizationUnitSettings settings = userManagementService.getUserSettings( agentId );

            if ( settings.getProfileUrl() != null ) {
                googleUrl.put( "host", surveyHandler.getGoogleShareUri() );
                googleUrl.put( "profileServer", surveyHandler.getApplicationBaseUrl() + "pages" );
                googleUrl.put( "relativePath", settings.getProfileUrl() );
            }
        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in getGooglePlusLink() while trying to post into Google." );
            ErrorResponse response = new ErrorResponse();
            response.setErrCode( "Error while trying to post on Google." );
            response.setErrMessage( e.getMessage() );
            return new Gson().toJson( response );
        }
        LOG.info( "Method to get Google details, getGooglePlusLink() finished." );
        return new Gson().toJson( googleUrl );
    }


    @ResponseBody
    @RequestMapping ( value = "/updatesharedon", method = RequestMethod.GET)
    public String updateSharedOn( HttpServletRequest request )
    {
        LOG.info( "Method to get update shared on social sites details, updateSharedOn() started." );

        try {
            String agentIdStr = request.getParameter( "agentId" );
            String customerEmail = request.getParameter( "customerEmail" );
            String socialSite = request.getParameter( "socialSite" );
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException(
                    "InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty." );
            }

            long agentId = 0;
            try {
                agentId = Long.parseLong( agentIdStr );
            } catch ( NumberFormatException e ) {
                LOG.error(
                    "NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e );
                throw e;
            }
            surveyHandler.updateSharedOn( socialSite, agentId, customerEmail );

        } catch ( NonFatalException e ) {
            LOG.error( "Exception occured in updateSharedOn() while trying to post into Google." );
            ErrorResponse response = new ErrorResponse();
            response.setErrCode( "" );
            response.setErrMessage( e.getMessage() );
            return new Gson().toJson( response );
        }
        LOG.info( "Method to get Google details, updateSharedOn() finished." );
        return new Gson().toJson( "Success" );
    }


    @ResponseBody
    @RequestMapping ( value = "/restartsurvey")
    public void restartSurvey( HttpServletRequest request )
    {
        String agentIdStr = request.getParameter( "agentId" );
        String customerEmail = request.getParameter( "customerEmail" );
        String firstName = request.getParameter( "firstName" );
        String lastName = request.getParameter( "lastName" );
        try {
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException( "Invalid value (Null/Empty) found for agentId." );
            }
            long agentId = Long.parseLong( agentIdStr );
            surveyHandler.changeStatusOfSurvey( agentId, customerEmail, firstName, lastName, true );
            SurveyDetails survey = surveyHandler.getSurveyDetails( agentId, customerEmail, firstName, lastName );
            User user = userManagementService.getUserByUserId( agentId );
            surveyHandler.sendSurveyRestartMail( firstName, lastName, customerEmail, survey.getCustRelationWithAgent(), user,
                survey.getUrl() );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in makeSurveyEditable(). Nested exception is ", e );
        }
    }


    // Method to re-send mail to the customer for taking survey.

    @ResponseBody
    @RequestMapping ( value = "/resendsurveylink", method = RequestMethod.POST)
    public void resendSurveyLink( HttpServletRequest request )
    {
        String agentIdStr = request.getParameter( "agentId" );
        String customerEmail = request.getParameter( "customerEmail" );
        String firstName = request.getParameter( "firstName" );
        String lastName = request.getParameter( "lastName" );
        try {
            if ( agentIdStr == null || agentIdStr.isEmpty() ) {
                throw new InvalidInputException( "Invalid value (Null/Empty) found for agentId." );
            }
            long agentId = Long.parseLong( agentIdStr );
            SurveyPreInitiation survey = surveyHandler.getPreInitiatedSurvey( agentId, customerEmail, firstName, lastName );
            User user = userManagementService.getUserByUserId( agentId );
            surveyHandler.sendSurveyRestartMail( firstName, lastName, customerEmail, survey.getCustomerInteractionDetails(),
                user, surveyHandler.composeLink( agentId, customerEmail, firstName, lastName ) );
        } catch ( NonFatalException e ) {
            LOG.error( "NonfatalException caught in makeSurveyEditable(). Nested exception is ", e );
        }
    }


    @RequestMapping ( value = "/notfound")
    public String showNotFoundPage( HttpServletRequest request )
    {
        return JspResolver.NOT_FOUND_PAGE;
    }


    private SurveyDetails storeInitialSurveyDetails( long agentId, String customerEmail, String firstName, String lastName,
        int reminderCount, String custRelationWithAgent, String url, String source ) throws SolrException,
        NoRecordsFetchedException, InvalidInputException
    {
        return surveyHandler.storeInitialSurveyDetails( agentId, customerEmail, firstName, lastName, reminderCount,
            custRelationWithAgent, url, source );
    }


    private String getApplicationBaseUrl()
    {
        return surveyHandler.getApplicationBaseUrl();
    }


    private Map<String, Object> getSurvey( long agentId, String customerEmail, String firstName, String lastName,
        int reminderCount, String custRelationWithAgent, String url, String source ) throws InvalidInputException,
        SolrException, NoRecordsFetchedException
    {
        Integer stage = null;
        Map<String, Object> surveyAndStage = new HashMap<>();
        List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId( agentId );
        boolean editable = false;
        try {
            SurveyDetails survey = storeInitialSurveyDetails( agentId, customerEmail, firstName, lastName, reminderCount,
                custRelationWithAgent, url, source );
            surveyHandler.updateSurveyAsClicked( agentId, customerEmail );

            if ( survey != null ) {
                stage = survey.getStage();
                editable = survey.getEditable();
                surveyAndStage.put( "agentName", survey.getAgentName() );
                surveyAndStage.put( "customerFirstName", survey.getCustomerFirstName() );
                surveyAndStage.put( "customerLastName", survey.getCustomerLastName() );
                surveyAndStage.put( "customerEmail", survey.getCustomerEmail() );
                for ( SurveyQuestionDetails surveyDetails : surveyQuestionDetails ) {
                    for ( SurveyResponse surveyResponse : survey.getSurveyResponse() ) {
                        if ( surveyDetails.getQuestion().trim().equalsIgnoreCase( surveyResponse.getQuestion() ) ) {
                            surveyDetails.setCustomerResponse( surveyResponse.getAnswer() );
                        }
                    }
                }
            } else {
                surveyAndStage.put( "agentName", solrSearchService.getUserDisplayNameById( agentId ) );
                surveyAndStage.put( "customerEmail", customerEmail );
                surveyAndStage.put( "customerFirstName", firstName );
                surveyAndStage.put( "customerLastName", lastName );
            }
        } catch ( SolrException e ) {
            LOG.error( "SolrException caught in triggerSurvey(). Details are " + e );
            throw e;
        }

        /*OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(userManagementService.getUserByUserId(agentId));*/
        OrganizationUnitSettings unitSettings = organizationManagementService.getAgentSettings( agentId );
        if ( unitSettings.getSurvey_settings() == null ) {
            SurveySettings surveySettings = new SurveySettings();
            surveySettings.setAutoPostEnabled( true );
            surveySettings.setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
            organizationManagementService.updateScoreForSurvey( MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION,
                unitSettings, surveySettings );
        } else {
            if ( unitSettings.getSurvey_settings().getShow_survey_above_score() <= 0 ) {
                unitSettings.getSurvey_settings().setAutoPostEnabled( true );
                unitSettings.getSurvey_settings().setShow_survey_above_score( CommonConstants.DEFAULT_AUTOPOST_SCORE );
                organizationManagementService.updateScoreForSurvey(
                    MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, unitSettings,
                    unitSettings.getSurvey_settings() );
            }
        }
        SurveySettings defaultSurveySettings = organizationManagementService.retrieveDefaultSurveyProperties();
        surveyAndStage.put( "happyText", defaultSurveySettings.getHappyText() );
        surveyAndStage.put( "neutralText", defaultSurveySettings.getNeutralText() );
        surveyAndStage.put( "sadText", defaultSurveySettings.getSadText() );
        surveyAndStage.put( "happyTextComplete", defaultSurveySettings.getHappyTextComplete() );
        surveyAndStage.put( "neutralTextComplete", defaultSurveySettings.getNeutralTextComplete() );
        surveyAndStage.put( "sadTextComplete", defaultSurveySettings.getSadTextComplete() );

        if ( unitSettings != null ) {
            SurveySettings surveySettings = unitSettings.getSurvey_settings();
            if ( surveySettings != null ) {
                if ( surveySettings.getHappyText() != null && !( surveySettings.getHappyText().isEmpty() ) )
                    surveyAndStage.put( "happyText", surveySettings.getHappyText() );
                if ( surveySettings.getNeutralText() != null && !( surveySettings.getNeutralText().isEmpty() ) )
                    surveyAndStage.put( "neutralText", surveySettings.getNeutralText() );
                if ( surveySettings.getSadText() != null && !surveySettings.getSadText().isEmpty() )
                    surveyAndStage.put( "sadText", surveySettings.getSadText() );
                if ( surveySettings.getHappyTextComplete() != null && !surveySettings.getHappyTextComplete().isEmpty() )
                    surveyAndStage.put( "happyTextComplete", surveySettings.getHappyTextComplete() );
                if ( surveySettings.getNeutralTextComplete() != null && !surveySettings.getNeutralTextComplete().isEmpty() )
                    surveyAndStage.put( "neutralTextComplete", surveySettings.getNeutralTextComplete() );
                if ( surveySettings.getSadTextComplete() != null && !surveySettings.getSadTextComplete().isEmpty() )
                    surveyAndStage.put( "sadTextComplete", surveySettings.getSadTextComplete() );

                surveyAndStage.put( "autopostScore", surveySettings.getShow_survey_above_score() );
                surveyAndStage.put( "autopostEnabled", surveySettings.isAutoPostEnabled() );
            }
        }

        AgentSettings agentSettings = userManagementService.getUserSettings( agentId );

        // Fetching Yelp Url
        try {
            if ( agentSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() != null ) {
                surveyAndStage.put( "yelpEnabled", true );
                surveyAndStage.put( "yelpLink", agentSettings.getSocialMediaTokens().getYelpToken().getYelpPageLink() );
            } else
                surveyAndStage.put( "yelpEnabled", false );
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "yelpEnabled", false );
        }

        // Fetching Google Plus Url
        try {
            if ( agentSettings.getSocialMediaTokens().getGoogleToken().getProfileLink() != null ) {
                surveyAndStage.put( "googleEnabled", true );
                surveyAndStage.put( "googleLink", agentSettings.getSocialMediaTokens().getGoogleToken().getProfileLink() );
            } else
                surveyAndStage.put( "googleEnabled", false );
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "googleEnabled", false );
        }

        // Fetching Zillow Url
        try {
            if ( agentSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() != null ) {
                surveyAndStage.put( "zillowEnabled", true );
                surveyAndStage.put( "zillowLink", agentSettings.getSocialMediaTokens().getZillowToken().getZillowProfileLink() );
            } else
                surveyAndStage.put( "zillowEnabled", false );
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "zillowEnabled", false );
        }

        // Fetching LendingTree Url
        try {
            if ( agentSettings.getSocialMediaTokens().getLendingTreeToken().getLendingTreeProfileLink() != null ) {
                surveyAndStage.put( "lendingtreeEnabled", true );
                surveyAndStage.put( "lendingtreeLink", agentSettings.getSocialMediaTokens().getLendingTreeToken()
                    .getLendingTreeProfileLink() );
            } else
                surveyAndStage.put( "lendingtreeEnabled", false );
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "lendingtreeEnabled", false );
        }

        try {
            if ( agentSettings.getSocialMediaTokens().getRealtorToken().getRealtorProfileLink() != null ) {
                surveyAndStage.put( "realtorEnabled", true );
                surveyAndStage.put( "realtorLink", agentSettings.getSocialMediaTokens().getRealtorToken()
                    .getRealtorProfileLink() );
            } else
                surveyAndStage.put( "realtorEnabled", false );
        } catch ( NullPointerException e ) {
            surveyAndStage.put( "realtorEnabled", false );
        }

        surveyAndStage.put( "agentFullProfileLink", getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
            + agentSettings.getProfileUrl() );
        surveyAndStage.put( "agentProfileLink", agentSettings.getProfileUrl() );
        surveyAndStage.put( "stage", stage );
        surveyAndStage.put( "survey", surveyQuestionDetails );
        surveyAndStage.put( "agentId", agentId );
        surveyAndStage.put( "editable", editable );
        surveyAndStage.put( "source", source );
        return surveyAndStage;
    }
}
// JIRA SS-119 by RM-05 : EOC