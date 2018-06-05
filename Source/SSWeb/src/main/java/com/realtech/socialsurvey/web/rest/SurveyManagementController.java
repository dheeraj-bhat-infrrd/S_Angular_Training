package com.realtech.socialsurvey.web.rest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
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
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoOrganizationUnitSettingDaoImpl;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.entities.AbusiveMailSettings;
import com.realtech.socialsurvey.core.entities.AgentMediaPostDetails;
import com.realtech.socialsurvey.core.entities.AgentSettings;
import com.realtech.socialsurvey.core.entities.BranchMediaPostDetails;
import com.realtech.socialsurvey.core.entities.BranchSettings;
import com.realtech.socialsurvey.core.entities.BulkSurveyDetail;
import com.realtech.socialsurvey.core.entities.ComplaintResolutionSettings;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.entities.RegionMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SocialMediaPostDetails;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;
import com.realtech.socialsurvey.core.entities.SurveySettings;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.entities.UserProfile;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.authentication.CaptchaValidation;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.OrganizationManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileManagementService;
import com.realtech.socialsurvey.core.services.organizationmanagement.ProfileNotFoundException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.SolrSearchService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.settingsmanagement.impl.InvalidSettingsStateException;
import com.realtech.socialsurvey.core.services.social.SocialManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.DuplicateSurveyRequestException;
import com.realtech.socialsurvey.core.services.surveybuilder.impl.SelfSurveyInitiationException;
import com.realtech.socialsurvey.core.utils.CommonUtils;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EmailFormatHelper;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.core.utils.UrlValidationHelper;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.ErrorCodes;
import com.realtech.socialsurvey.web.common.ErrorResponse;
import com.realtech.socialsurvey.web.common.JspResolver;
import com.realtech.socialsurvey.web.util.RequestUtils;

import facebook4j.FacebookException;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import twitter4j.TwitterException;

// JIRA SS-119 by RM-05 : BOC
@Controller
@RequestMapping(value = "/survey")
public class SurveyManagementController
{

	private static final Logger LOG = LoggerFactory.getLogger(SurveyManagementController.class);

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
	@Qualifier("nocaptcha")
	private CaptchaValidation captchaValidation;

	@Value("${VALIDATE_CAPTCHA}")
	private String validateCaptcha;

	@Value("${CAPTCHA_SECRET}")
	private String captchaSecretKey;

	@Value("${GATEWAY_QUESTION}")
	private String gatewayQuestion;

	@Value("${APPLICATION_ADMIN_EMAIL}")
	private String applicationAdminEmail;

	@Value("${APPLICATION_ADMIN_NAME}")
	private String applicationAdminName;

	@Autowired
	private EncryptionHelper encryptionHelper;

	@Value("${FB_CLIENT_ID}")
	private String facebookAppId;

	@Value("${GOOGLE_API_KEY}")
	private String googlePlusId;

	@Autowired
    private SSApiIntergrationBuilder ssApiIntergrationBuilder;

	/*
	 * Method to store answer to the current question of the survey.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/storeAnswer" , method = RequestMethod.GET)
	public Response storeSurveyAnswer(HttpServletRequest request) {
		LOG.info("Method storeSurveyAnswer() started to store response of customer.");
		Response response = null;
		String answer = request.getParameter("answer");
		String question = request.getParameter("question");
		String questionType = request.getParameter("questionType");
		int stage = Integer.parseInt(request.getParameter("stage"));
		String isUserRankingQuestionStr = request.getParameter("isUserRankingQuestion");
		String surveyId = request.getParameter("surveyId");
		//add a request parameter to check if the question is set to nps or not 
		String isNpsQuestionStr = request.getParameter("isNPSQuestion");
		int considerForScoreInt = Integer.parseInt(request.getParameter("considerForScore"));
		int questionId = Integer.parseInt( request.getParameter("questionId") );
		//encode question and response
		question = new String( DatatypeConverter.parseBase64Binary(question) );
		answer = new String( DatatypeConverter.parseBase64Binary(answer) );
		
		//get boolean value for isUserRankingQues
		int isUserRankingQuestionInt = CommonConstants.QUESTION_RATING_VALUE_FALSE;
		if( ! StringUtils.isEmpty(isUserRankingQuestionStr)) {
			isUserRankingQuestionInt = Integer.parseInt(isUserRankingQuestionStr);
		}
		boolean isUserRankingQuestion = false;
		if(isUserRankingQuestionInt == CommonConstants.QUESTION_RATING_VALUE_TRUE){
		    isUserRankingQuestion = true;
		}
		
		int isNpsQuestionInt = CommonConstants.QUESTION_RATING_VALUE_FALSE;
		if( ! StringUtils.isEmpty(isNpsQuestionStr)) {
			isNpsQuestionInt = Integer.parseInt(isNpsQuestionStr);
		}
		boolean isNpsQuestion = false;
		if(isNpsQuestionInt == CommonConstants.QUESTION_RATING_VALUE_TRUE){
		    isNpsQuestion = true;
		}
		
		boolean considerForScore = false;
        if(considerForScoreInt == CommonConstants.QUESTION_RATING_VALUE_TRUE){
            considerForScore = true;
        }
		/*surveyHandler.updateCustomerAnswersInSurvey(surveyId, question, questionType, answer, stage, isUserRankingQuestion, isNpsQuestion);
		LOG.info("Method storeSurveyAnswer() finished to store response of customer.");
		return surveyHandler.getSwearWords();*/
		try{
		      response = ssApiIntergrationBuilder.getIntegrationApi().updateSurveyResponse( surveyId, question, questionType, answer, stage, isUserRankingQuestion, isNpsQuestion, questionId, considerForScore );
		}catch(Exception e){
		    LOG.error( "Method store survey answer has an exception in api : ",e );
		}
		return response;
	}
	
	/*
     * Method to store answer to the current question of the survey.
     */
    @ResponseBody
    @RequestMapping(value = "/data/getSwearWords" , method = RequestMethod.GET)
    public String getSwearList(HttpServletRequest request) {
        LOG.info( "Method to get swear list started" );
        long companyId = Long.parseLong( request.getParameter("companyId") );
        Response response = ssApiIntergrationBuilder.getIntegrationApi().getSwearWordsList(companyId);
        return new String( ( (TypedByteArray) response.getBody() ).getBytes() );

    }

	/*
	 * Method to store final feedback of the survey from customer.
	 */
	@ResponseBody
	@RequestMapping(value = "/data/storeFeedback")
	public String storeFeedbackAndCloseSurvey(HttpServletRequest request) {
		LOG.info("Method storeFeedback() started to store response of customer.");

		// To store final feedback provided by customer in mongoDB.
		try {
			String feedback = request.getParameter("feedback");
			String mood = request.getParameter("mood");
			String customerEmail = request.getParameter("customerEmail");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String agreedToShare = request.getParameter("agreedToShare");
			String strIsIsoEncoded = request.getParameter("isIsoEncoded");
			String surveyId = request.getParameter("surveyId");

			if (surveyId == null || surveyId.isEmpty()) {
				throw new InvalidInputException("Passed parameter survey id is null or empty");
			}

			boolean isIsoEncoded = Boolean.parseBoolean(strIsIsoEncoded);
			if (isIsoEncoded) {
				feedback = new String(feedback.getBytes(Charset.forName("ISO-8859-1")), "UTF-8");
			}

			long agentId = 0;
			try {
				String agentIdStr = request.getParameter("agentId");
				if (agentIdStr == null || agentIdStr.isEmpty()) {
					LOG.error("Null/empty value found for agentId in storeFeedback().");
					throw new InvalidInputException("Null/empty value found for agentId in storeFeedback().");
				}
				agentId = Long.valueOf(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException occurred in storeFeedback() while getting agentId.");
			}

			boolean isAbusive = Boolean.parseBoolean(request.getParameter("isAbusive"));
			//update gateway question response and score 
	        Response response = ssApiIntergrationBuilder.getIntegrationApi().updateScore(surveyId, mood, feedback, isAbusive, agreedToShare);
	        /*  surveyHandler.updateGatewayQuestionResponseAndScore(surveyId, mood, feedback, isAbusive, agreedToShare);*/	
	        surveyHandler.increaseSurveyCountForAgent(agentId);
			SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(surveyId);
			SurveyPreInitiation surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(surveyDetails.getSurveyPreIntitiationId());
			surveyHandler.deleteSurveyPreInitiationDetailsPermanently(surveyPreInitiation);

			// update the modified time of hierarchy for seo
			surveyHandler.updateModifiedOnColumnForAgentHierachy(agentId);
			if (mood == null || mood.isEmpty()) {
				LOG.error("Null/empty value found for mood in storeFeedback().");
				throw new InvalidInputException("Null/empty value found for mood in storeFeedback().");
			}
			Map<String, String> emailIdsToSendMail = new HashMap<>();
			SolrDocument solrDocument = null;
	        SurveyDetails survey = surveyHandler.getSurveyDetails(surveyId);

			try {
				solrDocument = solrSearchService.getUserByUniqueId(agentId);
			}
			catch (SolrException e) {
				LOG.error("SolrException occurred in storeFeedback() while fetching email id of agent. NEsted exception is ", e);
			}

			//SS-1436
            Map<String, Double> surveyMailThreshold = surveyHandler.buildSurveyCompletionThresholdMap( survey );

            double agentThreshold = ( surveyMailThreshold != null
                && surveyMailThreshold.get( CommonConstants.AGENT_ID_COLUMN ) != null )
                    ? surveyMailThreshold.get( CommonConstants.AGENT_ID_COLUMN ) : 0.0d;

            if ( solrDocument != null && !solrDocument.isEmpty() && surveyMailThreshold != null
                && agentThreshold <= survey.getScore() ) {
                emailIdsToSendMail.put( solrDocument.get( CommonConstants.USER_EMAIL_ID_SOLR ).toString(),
                    solrDocument.get( CommonConstants.USER_DISPLAY_NAME_SOLR ).toString() );
            }

            String moodsToSendMail = surveyHandler.getMoodsToSendMail();
            if ( !moodsToSendMail.isEmpty() && moodsToSendMail != null ) {
                List<String> moods = new ArrayList<>( Arrays.asList( moodsToSendMail.split( "," ) ) );
                if ( moods.contains( mood ) ) {

                    // SS-1436
                    // check if "Agent Notification Threshold" is enabled for a hierarchy
                    emailIdsToSendMail.putAll( surveyHandler.buildPreferredAdminEmailListForSurvey( survey,
                        surveyMailThreshold.get( CommonConstants.COMPANY_ID_COLUMN ) != null
                            ? surveyMailThreshold.get( CommonConstants.COMPANY_ID_COLUMN ) : 0.0d,
                        surveyMailThreshold.get( CommonConstants.REGION_ID_COLUMN ) != null
                            ? surveyMailThreshold.get( CommonConstants.REGION_ID_COLUMN ) : 0.0d,
                        surveyMailThreshold.get( CommonConstants.BRANCH_ID_COLUMN ) != null
                            ? surveyMailThreshold.get( CommonConstants.BRANCH_ID_COLUMN ) : 0.0d ) );
                }
            }

			// Sending email to the customer telling about successful completion
			// of survey.
			try {
				String customerName = emailFormatHelper.getCustomerDisplayNameForEmail(survey.getCustomerFirstName(), survey.getCustomerLastName());
				User agent = userManagementService.getUserByUserId(agentId);

				String logoUrl = userManagementService.fetchAppropriateLogoUrlFromHierarchyForUser(agent.getUserId());
				LOG.info("logourl is : " + logoUrl + " for user " + agent.getUserId());
				if (survey.getMood().equalsIgnoreCase("Unpleasant")) {
					surveyHandler.sendSurveyCompletionUnpleasantMail(customerEmail, survey.getCustomerFirstName(), survey.getCustomerLastName(),
							agent);
				} else {
					surveyHandler.sendSurveyCompletionMail(customerEmail, survey.getCustomerFirstName(), survey.getCustomerLastName(), agent);
				}

				double surveyScoreValue = survey.getScore();
				boolean allowCheckBox = true;
				OrganizationUnitSettings agentSettings = organizationManagementService.getAgentSettings(agentId);

				if (mood != null && !mood.equalsIgnoreCase("Great")) {
					allowCheckBox = false;
				} else {
					if (agentSettings != null) {
						SurveySettings surveySettings = agentSettings.getSurvey_settings();
						if (surveySettings != null) {
							float throttleScore = surveySettings.getShow_survey_above_score();
							if (surveyScoreValue < throttleScore) {
								allowCheckBox = false;
							}
						} else {
							if (surveyScoreValue < CommonConstants.DEFAULT_AUTOPOST_SCORE) {
								allowCheckBox = false;
							}
						}
					}
				}
                // Generate the text as in mail
                String surveyDetail = generateSurveyTextForMail( customerName, mood, survey, isAbusive, allowCheckBox );

              //prepare customer full name
                String customerFullName = "";
                if ( StringUtils.isEmpty( survey.getCustomerFirstName() ) )
                    throw new InvalidInputException( "customer first name cannot be empty" );
                else
                    customerFullName = WordUtils.capitalize(
                        survey.getCustomerFirstName().trim() + ( StringUtils.isEmpty( survey.getCustomerLastName() ) ? ""
                            : " " + survey.getCustomerLastName().trim() ) );

                // Generate the text for customer details in mail 
                String customerDetail = generateCustomerTextForMail( customerFullName, customerEmail, survey.getSourceId() );
                
                // fetch the company settings
                OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(survey.getCompanyId());
                
                if (companySettings == null)
                    throw new NonFatalException("Company settings cannot be found for id : " + survey.getCompanyId());

                String surveyScore = String.valueOf( surveyHandler.getFormattedSurveyScore( survey.getScore() ) );
                String agentName = ( agent.getLastName() != null && !agent.getLastName().isEmpty() )
                    ? ( agent.getFirstName() + " " + agent.getLastName() ) : agent.getFirstName();
                String propertyAddress = "";
                if( survey.getPropertyAddress() != null)
                    propertyAddress = "Property Address : "+survey.getPropertyAddress();
  
                for ( Entry<String, String> admin : emailIdsToSendMail.entrySet() ) {
                    emailServices.sendSurveyCompletionMailToAdminsAndAgent( agentName, admin.getValue(), admin.getKey(),
                        surveyDetail, customerName, surveyScore, logoUrl, agentSettings.getCompleteProfileUrl(),
                        customerDetail, propertyAddress );
                }
                

				if (companySettings.getSurvey_settings() != null && companySettings.getSurvey_settings().getComplaint_res_settings() != null) {
					ComplaintResolutionSettings complaintRegistrationSettings = companySettings.getSurvey_settings().getComplaint_res_settings();

					if (complaintRegistrationSettings.isEnabled() && ((survey.getScore() > 0d && complaintRegistrationSettings.getRating() > 0d
							&& survey.getScore() < complaintRegistrationSettings.getRating())
							|| (!complaintRegistrationSettings.getMood().trim().isEmpty()
									&& complaintRegistrationSettings.getMoodList().contains(mood.toLowerCase())))) {
						survey.setUnderResolution(true);
						surveyHandler.updateSurveyAsUnderResolution(survey.get_id());

						// SS-1435: Send survey details too.
						// SS-715: Full customer name
						String displayName = survey.getCustomerFirstName();
						if (survey.getCustomerLastName() != null)
							displayName = displayName + " " + survey.getCustomerLastName();
						emailServices.sendComplaintHandleMail( complaintRegistrationSettings.getMailId(), displayName,
                            customerEmail, survey.getAgentName(), mood, surveyScore, survey.getSourceId(), surveyDetail );
					}

				}
				
				if(isAbusive) {
					if (companySettings.getSurvey_settings() != null && companySettings.getSurvey_settings().getAbusive_mail_settings() != null) {
						AbusiveMailSettings abusiveMailSettings = companySettings.getSurvey_settings().getAbusive_mail_settings();
						survey.setAbusiveNotify(true);
						surveyHandler.updateSurveyAsAbusiveNotify(survey.get_id());

						// SS-1435: Send survey details too.
						// SS-715: Full customer name
						String displayName = survey.getCustomerFirstName();
						if (survey.getCustomerLastName() != null)
							displayName = displayName + " " + survey.getCustomerLastName();
						emailServices.sendAbusiveNotifyMail(CommonConstants.REPORT_ABUSE_BY_APPLICATION_NAME, abusiveMailSettings.getMailId(), displayName,customerEmail, survey.getAgentName(), 
								agent.getEmailId(),mood, surveyScore, survey.getSourceId(), feedback,survey.getSurveyCompletedDate().toString());
					}
				}
				
			}
			catch (InvalidInputException e) {
				LOG.error("Exception occurred while trying to send survey completion mail to : " + customerEmail);
				throw e;
			}
		}
		catch (NonFatalException e) {
			LOG.error("Non fatal exception caught in storeFeedback(). Nested exception is ", e);
			return e.getMessage();
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("An exception occured while changing the character encoding of the feedback");
		}
		LOG.info("Method storeFeedback() finished to store response of customer.");
		return "Survey stored successfully";
	}

	private String generateCustomerTextForMail( String customerFullName, String customerEmailId, String surveySourceId )
    {
	    
        StringBuilder customerDetail = new StringBuilder( "<div style=\"margin: 15px 0px 15px 0px;\">" );
   
        customerDetail.append( "Here are the customer details:" );
            
        customerDetail.append( "<div style=\"margin: 10px 0px 10px 10px;\">" );
        customerDetail.append( "Customer Name: " );
        customerDetail.append( customerFullName == null ? "" : customerFullName );
        customerDetail.append( "<br/> Customer Email: " );
        customerDetail.append( customerEmailId == null ? "" : customerEmailId );
        customerDetail.append( "<br/> Transaction Id ( Loan# ): ");
        customerDetail.append( surveySourceId == null ? CommonConstants.NOT_AVAILABLE : surveySourceId );
        
        customerDetail.append( "</div>" );
        customerDetail.append( "</div>" );
        
        return customerDetail.toString();
        
    }

    private String generateSurveyTextForMail(String customerName, String mood, SurveyDetails survey, boolean isAbusive, boolean allowCheckBox) {
        Map<String,String> questionTypeDisplayName = new HashMap<>();
        questionTypeDisplayName.put( "sb-range-star", "1-5 Range" );
        questionTypeDisplayName.put( "sb-range-smiles", "1-5 Range" );
        questionTypeDisplayName.put( "sb-range-0to10", "0-10 Range" );
        questionTypeDisplayName.put( "sb-sel-mcq", "Multiple Choice" );
        questionTypeDisplayName.put( "sb-sel-desc", "Comment" );


		final String tableOneFirstColumnWidth = "150px";
		final String tableTwoFirstColumnWidth = "50%";

		final String tableStart = "<table class=\"table\">";
		final String tableEnd = "</table>";
		final String paragraph = "<p>";
		final String tableBreak = "<TR/>";
		final String tableOneRowStart = "<TR><TD width=" + tableOneFirstColumnWidth + ">";
		final String tableTwoRowStart = "<TR><TD style=\" border-top:0px; word-wrap:break-word;\" width=" + tableTwoFirstColumnWidth + ">";
		final String tableRowMiddle = "</TD><TD style=\"padding-left: 10px; border-top:0px; word-wrap:break-word;\"><strong>";
		final String tableRowEnd = "</strong></TD></TR>";

		StringBuilder surveyDetail = new StringBuilder(tableStart);

		surveyDetail.append(tableOneRowStart).append("Date:").append(tableRowMiddle)
				.append(CommonUtils.formatDate(survey.getCreatedOn(), "MM/dd/yyyy")).append(tableRowEnd);
		surveyDetail.append(tableOneRowStart).append("Overall Experience:").append(tableRowMiddle).append(mood).append(tableRowEnd);

		if (allowCheckBox) {
			surveyDetail.append(tableOneRowStart).append("Agreed to Share:").append(tableRowMiddle)
					.append(survey.getAgreedToShare().equalsIgnoreCase("true") ? "Yes" : "No").append(tableRowEnd);
		}

		surveyDetail.append(tableOneRowStart).append("Abusive Words:").append(tableRowMiddle).append((isAbusive) ? "Yes" : "No").append(tableRowEnd);

		// adding transaction ref id
		if (!StringUtils.isEmpty(survey.getSourceId())) {
			surveyDetail.append(tableOneRowStart).append("Transaction Ref Id:").append(tableRowMiddle).append(survey.getSourceId())
					.append(tableRowEnd);
		}

		surveyDetail.append(tableBreak).append(tableOneRowStart).append("Comment:").append(tableRowMiddle).append(survey.getReview())
				.append(tableRowEnd).append(tableEnd);

		surveyDetail.append(paragraph).append(tableStart);

		for (SurveyResponse response : survey.getSurveyResponse()) {
			surveyDetail.append(tableTwoRowStart).append(response.getQuestion()).append( " (" ).append( questionTypeDisplayName.get( response.getQuestionType() ) ).append( ")" )
			.append(tableRowMiddle).append(response.getAnswer())
			.append(tableRowEnd);
		}

		surveyDetail.append(tableEnd).append(paragraph);

		// update survey details with values
		String surveyDetailStr = surveyDetail.toString();
		surveyDetailStr = surveyDetailStr.replaceAll("\\[name\\]", survey.getAgentName());
		surveyDetailStr = surveyDetailStr.replaceAll("\\[Name\\]", survey.getAgentName());

		return surveyDetailStr;
	}

	@ResponseBody
	@RequestMapping(value = "/redirecttodetailspage")
	public String showDetailsPage(Model model, HttpServletRequest request) {
		LOG.info("Method to redirect to survey page started.");
		String agentId = request.getParameter("userId");
		LOG.info("Method to redirect to survey page started.");
		return getApplicationBaseUrl() + "rest/survey/showsurveypage/" + agentId;
	}

	@RequestMapping(value = "/showsurveypage/{agentIdStr}")
	public String initiateSurvey(Model model, @PathVariable String agentIdStr) {
		LOG.info("Method to start survey initiateSurvey() started.");
		if (agentIdStr == null || agentIdStr.isEmpty()) {
			LOG.error("Invalid agentId passed. Agent Id can not be null or empty.");
			return JspResolver.ERROR_PAGE;
		}
		long agentId = 0l;
		try {
			agentId = Long.parseLong(agentIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("Invalid agent Id passed. Error is : " + e);
			return JspResolver.ERROR_PAGE;
		}
		String agentName = "";
		String agentEmail = "";
		String profileImageUrl = "";
		String logoUrl = "";
		try {
			// get details from mongo
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			if (agentSettings != null) {
				agentName = agentSettings.getContact_details().getName();
				agentEmail = agentSettings.getContact_details().getMail_ids().getWork();
				profileImageUrl = (agentSettings.getProfileImageUrlThumbnail() != null ? agentSettings.getProfileImageUrlThumbnail()
						: agentSettings.getProfileImageUrl());
				// get the proper logo url based on settings for the
				// organization
				logoUrl = getLogoBasedOnSettingsForUser(agentId, agentSettings);
			}
		}
		catch (InvalidInputException e) {
			LOG.error("Error occurred while fetching details of agent. Error is : " + e);
			return JspResolver.ERROR_PAGE;
		}

		model.addAttribute("agentId", agentId);
		model.addAttribute("agentName", agentName);
		model.addAttribute("agentEmail", agentEmail);
		model.addAttribute("profileImage", profileImageUrl);
		model.addAttribute("logo", logoUrl);
		LOG.info("Method to start survey initiateSurvey() finished.");
		return JspResolver.SHOW_SURVEY_FORM;
	}

	@RequestMapping(value = "/showsurveypageforurl")
	public String initiateSurveyWithUrl(Model model, HttpServletRequest request) {
		LOG.info("Method to start survey initiateSurveyWithUrl() started.");
		String param = request.getParameter("q");
		try {
			Map<String, String> urlParams = urlGenerator.decryptParameters(param);
			if (urlParams != null) {
				long agentId = Long.parseLong(urlParams.get(CommonConstants.AGENT_ID_COLUMN));
				String customerEmail = urlParams.get(CommonConstants.CUSTOMER_EMAIL_COLUMN);
				String custFirstName = urlParams.get(CommonConstants.FIRST_NAME);
				String custLastName = urlParams.get(CommonConstants.LAST_NAME);

				boolean retakeSurvey = false;
				String retakeSurveyString = urlParams.get(CommonConstants.URL_PARAM_RETAKE_SURVEY);
				if (retakeSurveyString != null && !retakeSurveyString.isEmpty()) {
					retakeSurvey = Boolean.parseBoolean(retakeSurveyString);
				}

				SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, custFirstName, custLastName);
				if (surveyDetails != null && surveyDetails.getReview() != null && surveyDetails.getStage() != -1 && !retakeSurvey) {
					return JspResolver.SURVEY_LINK_INVALID;
				}

			}
		}
		catch (InvalidInputException error) {
			return JspResolver.SURVEY_LINK_INVALID;
		}
		model.addAttribute("q", param);
		LOG.info("Method to start survey initiateSurveyWithUrl() finished.");
		return JspResolver.SHOW_SURVEY_QUESTIONS;
	}

	/*
	 * Method to retrieve survey questions for a survey based upon the company id and agent id.
	 */
	@RequestMapping(value = "/triggersurvey")
	public String triggerSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");

		long agentId = 0;
		String customerEmail;
		String firstName;
		String lastName;
		String custRelationWithAgent;
		String agentName;
		String source;
		customerEmail = request.getParameter(CommonConstants.CUSTOMER_EMAIL_COLUMN);
		firstName = request.getParameter("firstName");
		lastName = request.getParameter("lastName");
		agentName = request.getParameter("agentName");
		source = "customer";
		// custRelationWithAgent = request.getParameter("relationship");
		// TODO:remove customer relation with agent
		custRelationWithAgent = "transacted";
		String errorMsg = null;
		try {
			try {
				String agentIdStr = request.getParameter(CommonConstants.AGENT_ID_COLUMN);
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught in triggerSurvey(). Details are " + e);
				throw e;
			}

			if (validateCaptcha.equals(CommonConstants.YES_STRING)) {
				if (!captchaValidation.isCaptchaValid(request.getRemoteAddr(), captchaSecretKey, request.getParameter("g-recaptcha-response"))) {
					LOG.error("Captcha Validation failed!");
					errorMsg = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE).getMessage();
					throw new InvalidInputException(errorMsg, DisplayMessageConstants.INVALID_CAPTCHA);
				}
			}

			model.addAttribute("agentId", agentId);
			model.addAttribute("firstName", firstName);
			model.addAttribute("lastName", lastName);
			model.addAttribute("customerEmail", customerEmail);
			model.addAttribute("relation", custRelationWithAgent);
			model.addAttribute("source", source);

			User user = userManagementService.getUserByUserId(agentId);

			try {
				surveyHandler.initiateSurveyRequest(user.getUserId(), customerEmail, firstName, lastName, source);
			}
			catch (SelfSurveyInitiationException e) {
				errorMsg = messageUtils.getDisplayMessage(DisplayMessageConstants.SELF_SURVEY_INITIATION, DisplayMessageType.ERROR_MESSAGE)
						.getMessage();
				throw new NonFatalException(e.getMessage(), e.getErrorCode());
			}
			catch (DuplicateSurveyRequestException e) {
				errorMsg = messageUtils.getDisplayMessage(DisplayMessageConstants.DUPLICATE_SURVEY_REQUEST, DisplayMessageType.ERROR_MESSAGE)
						.getMessage();
				throw new NonFatalException(e.getMessage(), e.getErrorCode());
			}

		}
		catch (NonFatalException e) {
			LOG.error("Exception caught in getSurvey() method of SurveyManagementController.");
			model.addAttribute("status", DisplayMessageType.ERROR_MESSAGE);
			if (errorMsg != null)
				model.addAttribute("message", errorMsg);
			else
				model.addAttribute("message",
						messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_CAPTCHA, DisplayMessageType.ERROR_MESSAGE));
			model.addAttribute("agentId", agentId);
			model.addAttribute("agentName", agentName);
			model.addAttribute("firstName", firstName);
			model.addAttribute("lastName", lastName);
			model.addAttribute("customerEmail", customerEmail);
			model.addAttribute("relation", custRelationWithAgent);
			return JspResolver.SHOW_SURVEY_FORM;
		}
		LOG.info("Method to store initial details of customer and agent and to get questions of survey, triggerSurvey() started.");
		return JspResolver.SURVEY_INVITE_SUCCESSFUL;
	}

	@ResponseBody
	@RequestMapping(value = "/triggersurveywithurl", method = RequestMethod.GET)
	public String triggerSurveyWithUrl(HttpServletRequest request) {
		LOG.info("Method to start survey when URL is given, triggerSurveyWithUrl() started.");
		Map<String, Object> surveyAndStage = new HashMap<>();
		try {
			String url = request.getParameter("q");
			Map<String, String> urlParams = urlGenerator.decryptParameters(url);
			if (urlParams != null) {
				long agentId = Long.parseLong(urlParams.get(CommonConstants.AGENT_ID_COLUMN));
				String customerEmail = urlParams.get(CommonConstants.CUSTOMER_EMAIL_COLUMN);
				String custFirstName = urlParams.get(CommonConstants.FIRST_NAME);
				String custLastName = urlParams.get(CommonConstants.LAST_NAME);
				long surveyPreInitiationId = 0;

				String surveyPreInitiationIdStr = urlParams.get(CommonConstants.SURVEY_PREINITIATION_ID_COLUMN);
				if (surveyPreInitiationIdStr != null && !surveyPreInitiationIdStr.isEmpty()) {
					surveyPreInitiationId = Long.parseLong(surveyPreInitiationIdStr);
				}

				boolean retakeSurvey = false;
				String retakeSurveyString = urlParams.get(CommonConstants.URL_PARAM_RETAKE_SURVEY);
				if (retakeSurveyString != null && !retakeSurveyString.isEmpty()) {
					retakeSurvey = Boolean.parseBoolean(retakeSurveyString);
				}

				if (custFirstName != null && !custFirstName.isEmpty()) {
					// check if name is null for dotloop data
					if (custFirstName.equalsIgnoreCase("null")) {
						custFirstName = null;
					}
				}
				if (custLastName != null && !custLastName.isEmpty()) {
					// check if name is null for dotloop data
					if (custLastName.equalsIgnoreCase("null")) {
						custLastName = null;
					}
				}

				User user = userManagementService.getUserByUserId(agentId);

				boolean isOldRecord = true;

				SurveyPreInitiation surveyPreInitiation = null;
				if (surveyPreInitiationId > 0) {
					isOldRecord = false;
					surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(surveyPreInitiationId);
				} else {
					surveyPreInitiation = surveyHandler.getPreInitiatedSurvey(agentId, customerEmail, custFirstName, custLastName);
				}

				// if no old survey pre initiation entry found than insert in
				// survey pre initiation
				String surveySource;
				if (surveyPreInitiation != null) {
					surveySource = surveyPreInitiation.getSurveySource();
				} else {
					// get the survey from mmongo
					SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, custFirstName, custLastName);
					if (surveyDetails != null) {
						surveySource = surveyDetails.getSource();
					} else {
						surveySource = MongoSocialPostDaoImpl.KEY_SOURCE_SS;
					}
				}

				if (surveyPreInitiation == null) {
					surveyPreInitiation = surveyHandler.preInitiateSurvey(user, customerEmail, custFirstName, custLastName, 0, null, surveySource);
				}

				String surveyURL = surveyHandler.composeLink(agentId, customerEmail, custFirstName, custLastName,
						surveyPreInitiation.getSurveyPreIntitiationId(), retakeSurvey);

				// Primary method
				surveyAndStage = getSurvey(user, surveyPreInitiation, surveyURL, isOldRecord, retakeSurvey);

				if (surveyPreInitiation.getStatus() == CommonConstants.SURVEY_STATUS_PRE_INITIATED)
					surveyHandler.markSurveyAsStarted(surveyPreInitiation);

			}
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in triggerSurveyWithUrl().");
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setErrCode(ErrorCodes.REQUEST_FAILED);
			errorResponse.setErrMessage(e.getMessage());
			String errorMessage = new Gson().toJson(errorResponse);
			return errorMessage;
		}
		LOG.info("Method to start survey when URL is given, triggerSurveyWithUrl() finished.");
		return new Gson().toJson(surveyAndStage);
	}

	@ResponseBody
	@RequestMapping(value = "/posttosocialnetwork", method = RequestMethod.GET)
	public String postToSocialMedia(HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to various pages of social networking sites started.");

		try {

			String agentName = request.getParameter("agentName");
			String agentProfileLink = request.getParameter("agentProfileLink");
			String custFirstName = request.getParameter("firstName");
			String custLastName = request.getParameter("lastName");
			String agentIdStr = request.getParameter("agentId");
			String ratingStr = request.getParameter("rating");
			String customerEmail = request.getParameter("customerEmail");
			String feedback = request.getParameter("feedback");
			String isAbusiveStr = request.getParameter("isAbusive");
			String serverBaseUrl = requestUtils.getRequestServerName(request);
			String onlyPostToSocialSurveyStr = request.getParameter("onlyPostToSocialSurvey");
			String strIsIsoEncoded = request.getParameter("isIsoEncoded");
			String surveyId = request.getParameter("surveyId");

			if (surveyId == null || surveyId.isEmpty()) {
				throw new InvalidInputException("Passed parameter survey id is null or empty");
			}
			boolean isIsoEncoded = Boolean.parseBoolean(strIsIsoEncoded);
			if (isIsoEncoded) {
				feedback = new String(feedback.getBytes(Charset.forName("ISO-8859-1")), "UTF-8");
			}

			long agentId = 0;
			double rating = 0;
			boolean isAbusive = false;
			boolean onlyPostToSocialSurvey = false;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
				isAbusive = Boolean.parseBoolean(isAbusiveStr);
				onlyPostToSocialSurvey = Boolean.parseBoolean(onlyPostToSocialSurveyStr);
			}
			catch (NumberFormatException | NullPointerException e) {
				LOG.error("Number format/Null Pointer exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ",
						e);
				return e.getMessage();
			}

			if (socialManagementService.postToSocialMedia(agentName, agentProfileLink, custFirstName, custLastName, agentId, rating, surveyId,
					feedback, isAbusive, serverBaseUrl, onlyPostToSocialSurvey)) {
				return "Successfully posted to all the places in hierarchy";
			}

		}
		catch (NonFatalException e) {
			LOG.error("Non fatal Exception caught in postToSocialMedia() while trying to post to social networking sites. Nested excption is ", e);
			return e.getMessage();
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("An exception occured while changing the character encoding of the feedback");
		}
		LOG.info("Method to post feedback of customer to various pages of social networking sites finished.");
		return "Error while posting on social media";

	}

	// @ResponseBody
	@RequestMapping(value = "/posttofacebook", method = RequestMethod.GET)
	public String postToFacebook(Model model, HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to facebook started.");
		try {
			String urlParam = request.getParameter("q");
			Map<String, String> facebookDetails = urlGenerator.decryptParameters(urlParam);
			String agentName = facebookDetails.get("agentName");
			String custFirstName = facebookDetails.get("firstName");
			String agentProfileLink = facebookDetails.get("agentProfileLink");
			String custLastName = facebookDetails.get("lastName");
			String agentIdStr = facebookDetails.get("agentId");
			String feedback = facebookDetails.get("feedback");
			String ratingStr = facebookDetails.get("rating");
			String customerEmail = facebookDetails.get("customerEmail");
			String serverBaseUrl = requestUtils.getRequestServerName(request);

			String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail(custFirstName, custLastName);

			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToSocialMedia() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}

			Map<String, List<OrganizationUnitSettings>> settingsMap = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			List<OrganizationUnitSettings> companySettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
			List<OrganizationUnitSettings> regionSettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			List<OrganizationUnitSettings> branchSettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);

			SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, custFirstName, custLastName);
			SocialMediaPostDetails socialMediaPostDetails = surveyHandler.getSocialMediaPostDetailsBySurvey(surveyDetails, companySettings.get(0),
					regionSettings, branchSettings);

			if (socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn() == null) {
				socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn(new ArrayList<String>());
			}
			if (socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn() == null) {
				socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn(new ArrayList<String>());
			}
			for (BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList()) {
				if (branchMediaPostDetails.getSharedOn() == null) {
					branchMediaPostDetails.setSharedOn(new ArrayList<String>());
				}
			}
			for (RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList()) {
				if (regionMediaPostDetails.getSharedOn() == null) {
					regionMediaPostDetails.setSharedOn(new ArrayList<String>());
				}
			}

			List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
			List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn();

			String facebookMessage = surveyHandler.getFormattedSurveyScore(rating) + "-Star Survey Response from " + customerDisplayName + " for "
					+ agentName + " on SocialSurvey - view at " + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL
					+ agentProfileLink;
			facebookMessage += "\n Feedback : " + feedback;

			// TODO: Bad code: DELETE: BEGIN
			// get the company id of the agent
			User user = userManagementService.getUserObjByUserId(agentId);
			try {
				if (surveyHandler.canPostOnSocialMedia(agentSettings, rating)) {
					if (!socialManagementService.updateStatusIntoFacebookPage(agentSettings, facebookMessage, serverBaseUrl,
							user.getCompany().getCompanyId(), agentSettings.getCompleteProfileUrl())) {
						// TODO: Bad code: Remove the company id from the
						// parameter: End
						if (!agentSocialList.contains(CommonConstants.FACEBOOK_SOCIAL_SITE))
							agentSocialList.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
					}
				}
			}
			catch (FacebookException e) {
				LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : companySettings) {
				try {
					if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
						if (!socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage, serverBaseUrl,
								user.getCompany().getCompanyId(), agentSettings.getCompleteProfileUrl())) {
							if (!companySocialList.contains(CommonConstants.FACEBOOK_SOCIAL_SITE))
								companySocialList.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
						}
					}
				}
				catch (FacebookException e) {
					LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
				}
			}
			for (RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList()) {
				try {
					OrganizationUnitSettings setting = organizationManagementService.getRegionSettings(regionMediaPostDetails.getRegionId());
					if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
						if (!socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage, serverBaseUrl,
								user.getCompany().getCompanyId(), agentSettings.getCompleteProfileUrl())) {
							List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
							if (!regionSocialList.contains(CommonConstants.FACEBOOK_SOCIAL_SITE))
								regionSocialList.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
							regionMediaPostDetails.setSharedOn(regionSocialList);
						}
					}
				}
				catch (FacebookException e) {
					LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
				}
			}
			for (BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList()) {
				try {
					OrganizationUnitSettings setting = organizationManagementService.getBranchSettingsDefault(branchMediaPostDetails.getBranchId());
					if (setting != null) {

						if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
							if (!socialManagementService.updateStatusIntoFacebookPage(setting, facebookMessage, serverBaseUrl,
									user.getCompany().getCompanyId(), agentSettings.getCompleteProfileUrl())) {
								List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
								if (!branchSocialList.contains(CommonConstants.FACEBOOK_SOCIAL_SITE))
									branchSocialList.add(CommonConstants.FACEBOOK_SOCIAL_SITE);
								branchMediaPostDetails.setSharedOn(branchSocialList);
							}
						}
					}
				}
				catch (FacebookException e) {
					LOG.error("FacebookException caught in postToSocialMedia() while trying to post to facebook. Nested excption is ", e);
				}
			}
			socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn(agentSocialList);
			socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn(companySocialList);
			surveyDetails.setSocialMediaPostDetails(socialMediaPostDetails);
			surveyHandler.updateSurveyDetails(surveyDetails);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in postToFacebook(). Nested exception is ", e);
		}
		LOG.info("Method to post feedback of customer to facebook finished.");
		model.addAttribute("socialMedia", CommonConstants.FACEBOOK_SOCIAL_SITE);
		return JspResolver.POST_ON_SOCIAL_MEDIA_SUCCESS;
	}

	@RequestMapping(value = "/posttotwitter", method = RequestMethod.GET)
	public String postToTwitter(Model model, HttpServletRequest request) {
		LOG.info("Method to post feedback of customer to twitter started.");
		try {
			String urlParam = request.getParameter("q");
			Map<String, String> twitterDetails = urlGenerator.decryptParameters(urlParam);
			String agentName = twitterDetails.get("agentName");
			String agentProfileLink = twitterDetails.get("agentProfileLink");
			String custFirstName = twitterDetails.get("firstName");
			String custLastName = twitterDetails.get("lastName");
			String agentIdStr = twitterDetails.get("agentId");
			String ratingStr = twitterDetails.get("rating");
			String customerEmail = twitterDetails.get("customerEmail");

			String customerDisplayName = emailFormatHelper.getCustomerDisplayNameForEmail(custFirstName, custLastName);
			long agentId = 0;
			double rating = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
				rating = Double.parseDouble(ratingStr);
			}
			catch (NumberFormatException e) {
				LOG.error("Number format exception caught in postToTwitter() while trying to convert agent Id. Nested exception is ", e);
				return e.getMessage();
			}

			Map<String, List<OrganizationUnitSettings>> settingsMap = socialManagementService.getSettingsForBranchesAndRegionsInHierarchy(agentId);
			List<OrganizationUnitSettings> companySettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.COMPANY_SETTINGS_COLLECTION);
			List<OrganizationUnitSettings> regionSettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.REGION_SETTINGS_COLLECTION);
			List<OrganizationUnitSettings> branchSettings = settingsMap.get(MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION);
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, custFirstName, custLastName);
			SocialMediaPostDetails socialMediaPostDetails = surveyHandler.getSocialMediaPostDetailsBySurvey(surveyDetails, companySettings.get(0),
					regionSettings, branchSettings);

			if (socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn() == null) {
				socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn(new ArrayList<String>());
			}
			if (socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn() == null) {
				socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn(new ArrayList<String>());
			}

			List<String> agentSocialList = socialMediaPostDetails.getAgentMediaPostDetails().getSharedOn();
			List<String> companySocialList = socialMediaPostDetails.getCompanyMediaPostDetails().getSharedOn();

			for (BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList()) {
				if (branchMediaPostDetails.getSharedOn() == null) {
					branchMediaPostDetails.setSharedOn(new ArrayList<String>());
				}
			}
			for (RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList()) {
				if (regionMediaPostDetails.getSharedOn() == null) {
					regionMediaPostDetails.setSharedOn(new ArrayList<String>());
				}
			}

			/*
			 * String twitterMessage = rating + "-Star Survey Response from " + customerDisplayName
			 * + " for " + agentName + " on @SocialSurveyMe - view at " + getApplicationBaseUrl() +
			 * CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			 */
			String twitterMessage = String.format(CommonConstants.TWITTER_MESSAGE, surveyHandler.getFormattedSurveyScore(rating), customerDisplayName,
					agentName, "@SocialSurveyMe") + getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + agentProfileLink;
			// TODO: Bad code: DELETE: BEGIN
			// get the company id of the agent
			User user = userManagementService.getUserObjByUserId(agentId);
			try {
				if (surveyHandler.canPostOnSocialMedia(agentSettings, rating)) {
					if (!socialManagementService.tweet(agentSettings, twitterMessage, user.getCompany().getCompanyId())) {
						if (!agentSocialList.contains(CommonConstants.TWITTER_SOCIAL_SITE))
							agentSocialList.add(CommonConstants.TWITTER_SOCIAL_SITE);
					}
				}
			}
			catch (TwitterException e) {
				LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
			}
			for (OrganizationUnitSettings setting : companySettings) {
				try {
					if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
						if (!socialManagementService.tweet(setting, twitterMessage, user.getCompany().getCompanyId())) {
							if (!companySocialList.contains(CommonConstants.TWITTER_SOCIAL_SITE))
								companySocialList.add(CommonConstants.TWITTER_SOCIAL_SITE);
						}
					}
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
				}
			}
			for (RegionMediaPostDetails regionMediaPostDetails : socialMediaPostDetails.getRegionMediaPostDetailsList()) {
				try {

					OrganizationUnitSettings setting = organizationManagementService.getRegionSettings(regionMediaPostDetails.getRegionId());
					if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
						if (!socialManagementService.tweet(setting, twitterMessage, user.getCompany().getCompanyId())) {
							List<String> regionSocialList = regionMediaPostDetails.getSharedOn();
							if (!regionSocialList.contains(CommonConstants.TWITTER_SOCIAL_SITE))
								regionSocialList.add(CommonConstants.TWITTER_SOCIAL_SITE);
							regionMediaPostDetails.setSharedOn(regionSocialList);
						}
					}
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
				}
			}
			for (BranchMediaPostDetails branchMediaPostDetails : socialMediaPostDetails.getBranchMediaPostDetailsList()) {
				try {
					OrganizationUnitSettings setting = organizationManagementService.getBranchSettingsDefault(branchMediaPostDetails.getBranchId());
					if (setting != null) {

						if (surveyHandler.canPostOnSocialMedia(setting, rating)) {
							if (!socialManagementService.tweet(setting, twitterMessage, user.getCompany().getCompanyId())) {
								List<String> branchSocialList = branchMediaPostDetails.getSharedOn();
								if (!branchSocialList.contains(CommonConstants.TWITTER_SOCIAL_SITE))
									branchSocialList.add(CommonConstants.TWITTER_SOCIAL_SITE);
								branchMediaPostDetails.setSharedOn(branchSocialList);
							}
						}
					}
				}
				catch (TwitterException e) {
					LOG.error("TwitterException caught in postToTwitter() while trying to post to twitter. Nested excption is ", e);
				}
			}
			socialMediaPostDetails.getAgentMediaPostDetails().setSharedOn(agentSocialList);
			socialMediaPostDetails.getCompanyMediaPostDetails().setSharedOn(companySocialList);
			surveyDetails.setSocialMediaPostDetails(socialMediaPostDetails);
			surveyHandler.updateSurveyDetails(surveyDetails);
		}
		catch (NonFatalException e) {
			LOG.error("NonFatalException caught in postToTwitter(). Nested exception is ", e);
		}
		LOG.info("Method to post feedback of customer to twitter finished.");
		model.addAttribute("socialMedia", CommonConstants.TWITTER_SOCIAL_SITE);
		return JspResolver.POST_ON_SOCIAL_MEDIA_SUCCESS;
	}

	@ResponseBody
	@RequestMapping(value = "/displaypiclocationofagent", method = RequestMethod.GET)
	public String getDisplayPicLocationOfAgent(HttpServletRequest request) {
		LOG.info("Method to get location of agent's image started.");
		String picLocation = "";
		String agentIdStr = request.getParameter("agentId");
		long agentId = 0;

		if (agentIdStr == null || agentIdStr.isEmpty()) {
			LOG.error("Null value found for agent Id in request param.");
			return "Null value found for agent Id in request param.";
		}

		try {
			agentId = Long.parseLong(agentIdStr);
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException caught in getDisplayPicLocationOfAgent() while getting agent id");
			return e.getMessage();
		}

		try {
			AgentSettings agentSettings = userManagementService.getUserSettings(agentId);
			if (agentSettings != null) {
				if (agentSettings.getProfileImageUrlThumbnail() != null && !agentSettings.getProfileImageUrlThumbnail().isEmpty()) {
					picLocation = agentSettings.getProfileImageUrlThumbnail();
				}
			}
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException caught in getDisplayPicLocationOfAgent() while fetching image for agent.");
			return e.getMessage();
		}

		LOG.info("Method to get location of agent's image finished.");
		return picLocation;
	}

	@ResponseBody
	@RequestMapping(value = "/getyelplinkrest", method = RequestMethod.GET)
	public String getYelpLinkRest(HttpServletRequest request) {
		LOG.info("Method to get Yelp details, getYelpLink() started.");
		Map<String, String> yelpUrl = new HashMap<String, String>();
		try {
			String agentIdStr = request.getParameter("agentId");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);

			if (settings.getSocialMediaTokens() == null || settings.getSocialMediaTokens().getYelpToken() == null) {

			} else {
				String validUrl = settings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
				try {
					validUrl = urlValidationHelper.buildValidUrl(validUrl);
				}
				catch (IOException ioException) {
					throw new InvalidInputException("Yelp link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
				}

				yelpUrl.put("relativePath", validUrl);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in getYelpLink() while trying to post into Yelp.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Yelp.");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		LOG.info("Method to get Yelp details, getYelpLink() finished.");
		return new Gson().toJson(yelpUrl);
	}

	@ResponseBody
	@RequestMapping(value = "/getgooglepluslinkrest", method = RequestMethod.GET)
	public String getGooglePlusLinkRest(HttpServletRequest request) {
		LOG.info("Method to get Google details, getGooglePlusLink() started.");
		Map<String, String> googleUrl = new HashMap<String, String>();
		try {
			// if url is encrypted. in case of incomplete social post reminder
			// mail
			/*
			 * String encryptedUrl = request.getRequestURI() + request.getQueryString(); Map<String
			 * , String> urlparameters = urlGenerator.decryptUrl( encryptedUrl ); String agentIdStr
			 * = urlparameters.get( "agentId" );
			 */

			String agentIdStr = request.getParameter("agentId");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);

			if (settings.getProfileUrl() != null) {
				googleUrl.put("host", surveyHandler.getGoogleShareUri());
				googleUrl.put("profileServer", surveyHandler.getApplicationBaseUrl() + "pages");
				googleUrl.put("relativePath", settings.getProfileUrl());
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in getGooglePlusLink() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Google.");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		LOG.info("Method to get Google details, getGooglePlusLink() finished.");
		return new Gson().toJson(googleUrl);
	}

	@ResponseBody
	@RequestMapping(value = "/posttogoogleplus", method = RequestMethod.GET)
	public ModelAndView postToGooglePlus(HttpServletRequest request) {
		LOG.info("Method to get Google details, postOnGooglePlus() started.");
		String redirectUrl = null;
		try {
			Map<String, String> urlParameters = urlGenerator.decryptParameters(request.getParameter("q"));
			String agentIdStr = urlParameters.get("agentId");
			String customerEmail = urlParameters.get("customerEmail");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in postOnGooglePlus(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in postOnGooglePlus(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);
			if (settings.getProfileUrl() != null) {
				redirectUrl = surveyHandler.getGoogleShareUri() + surveyHandler.getApplicationBaseUrl() + "pages" + settings.getProfileUrl();
			}

			// update shared on
			SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, null, null);
			SocialMediaPostDetails socialMediaPostDetails = surveyDetails.getSocialMediaPostDetails();
			if (socialMediaPostDetails == null) {
				socialMediaPostDetails = new SocialMediaPostDetails();

			}
			AgentMediaPostDetails agentMediaPostDetails = socialMediaPostDetails.getAgentMediaPostDetails();
			if (agentMediaPostDetails == null) {
				agentMediaPostDetails = new AgentMediaPostDetails();
				agentMediaPostDetails.setAgentId(agentId);
			}
			if (agentMediaPostDetails.getSharedOn() == null) {
				agentMediaPostDetails.setSharedOn(new ArrayList<String>());
			}
			List<String> agentSocialList = agentMediaPostDetails.getSharedOn();
			if (!agentSocialList.contains(CommonConstants.GOOGLE_SOCIAL_SITE))
				agentSocialList.add(CommonConstants.GOOGLE_SOCIAL_SITE);
			agentMediaPostDetails.setSharedOn(agentSocialList);
			socialMediaPostDetails.setAgentMediaPostDetails(agentMediaPostDetails);
			surveyDetails.setSocialMediaPostDetails(socialMediaPostDetails);
			surveyHandler.updateSurveyDetails(surveyDetails);

		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in postOnGooglePlus() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Google.");
			response.setErrMessage(e.getMessage());
			return new ModelAndView(response.toString());
		}
		LOG.info("Method to get Google details, postOnGooglePlus() finished.");
		return new ModelAndView("redirect:" + redirectUrl);
	}

	@ResponseBody
	@RequestMapping(value = "/posttoyelp", method = RequestMethod.GET)
	public ModelAndView postToYelp(HttpServletRequest request) {
		LOG.info("Method to get Yelp details, postToYelp() started.");
		String redirectUrl = null;
		try {
			Map<String, String> urlParameters = urlGenerator.decryptParameters(request.getParameter("q"));
			String agentIdStr = urlParameters.get("agentId");
			String customerEmail = urlParameters.get("customerEmail");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in postToYelp(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			OrganizationUnitSettings settings = userManagementService.getUserSettings(agentId);

			if (settings.getSocialMediaTokens() == null || settings.getSocialMediaTokens().getYelpToken() == null) {

			} else {
				String validUrl = settings.getSocialMediaTokens().getYelpToken().getYelpPageLink();
				try {
					validUrl = urlValidationHelper.buildValidUrl(validUrl);
				}
				catch (IOException ioException) {
					throw new InvalidInputException("Yelp link passed was invalid", DisplayMessageConstants.GENERAL_ERROR, ioException);
				}

				redirectUrl = validUrl;

				// update shared on
				SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, null, null);
				SocialMediaPostDetails socialMediaPostDetails = surveyDetails.getSocialMediaPostDetails();
				if (socialMediaPostDetails == null) {
					socialMediaPostDetails = new SocialMediaPostDetails();

				}
				AgentMediaPostDetails agentMediaPostDetails = socialMediaPostDetails.getAgentMediaPostDetails();
				if (agentMediaPostDetails == null) {
					agentMediaPostDetails = new AgentMediaPostDetails();
					agentMediaPostDetails.setAgentId(agentId);
				}
				if (agentMediaPostDetails.getSharedOn() == null) {
					agentMediaPostDetails.setSharedOn(new ArrayList<String>());
				}
				List<String> agentSocialList = agentMediaPostDetails.getSharedOn();
				if (!agentSocialList.contains(CommonConstants.YELP_SOCIAL_SITE))
					agentSocialList.add(CommonConstants.YELP_SOCIAL_SITE);
				agentMediaPostDetails.setSharedOn(agentSocialList);
				socialMediaPostDetails.setAgentMediaPostDetails(agentMediaPostDetails);
				surveyDetails.setSocialMediaPostDetails(socialMediaPostDetails);
				surveyHandler.updateSurveyDetails(surveyDetails);
			}
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in postToYelp() while trying to post into Yelp.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("Error while trying to post on Yelp.");
			response.setErrMessage(e.getMessage());
			return new ModelAndView(response.toString());
		}
		LOG.info("Method to get Yelp details, postToYelp() finished.");
		return new ModelAndView(redirectUrl);
	}

	@ResponseBody
	@RequestMapping(value = "/updatesharedon", method = RequestMethod.GET)
	public String updateSharedOn(HttpServletRequest request) {
		LOG.info("Method to get update shared on social sites details, updateSharedOn() started.");

		try {
			String agentIdStr = request.getParameter("agentId");
			String customerEmail = request.getParameter("customerEmail");
			String socialSite = request.getParameter("socialSite");
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("InvalidInputException caught in getYelpLinkRest(). Agent Id cannot be null or empty.");
			}

			long agentId = 0;
			try {
				agentId = Long.parseLong(agentIdStr);
			}
			catch (NumberFormatException e) {
				LOG.error("NumberFormatException caught while trying to convert agentId in getYelpLink(). Nested exception is ", e);
				throw e;
			}

			SurveyDetails surveyDetails = surveyHandler.getSurveyDetails(agentId, customerEmail, null, null);
			SocialMediaPostDetails socialMediaPostDetails;
			if (surveyDetails.getSocialMediaPostDetails() != null) {
				socialMediaPostDetails = surveyDetails.getSocialMediaPostDetails();

			} else {
				socialMediaPostDetails = new SocialMediaPostDetails();
			}

			AgentMediaPostDetails agentMediaPostDetails;
			if (socialMediaPostDetails != null && socialMediaPostDetails.getAgentMediaPostDetails() != null) {
				agentMediaPostDetails = socialMediaPostDetails.getAgentMediaPostDetails();
			} else {
				agentMediaPostDetails = new AgentMediaPostDetails();
				agentMediaPostDetails.setAgentId(agentId);
			}

			if (agentMediaPostDetails.getSharedOn() == null) {
				agentMediaPostDetails.setSharedOn(new ArrayList<String>());
			}
			List<String> agentSocialList = agentMediaPostDetails.getSharedOn();
			agentSocialList.add(socialSite);
			agentMediaPostDetails.setSharedOn(agentSocialList);
			socialMediaPostDetails.setAgentMediaPostDetails(agentMediaPostDetails);
			surveyDetails.setSocialMediaPostDetails(socialMediaPostDetails);
			surveyHandler.updateSurveyDetails(surveyDetails);
		}
		catch (NonFatalException e) {
			LOG.error("Exception occured in updateSharedOn() while trying to post into Google.");
			ErrorResponse response = new ErrorResponse();
			response.setErrCode("");
			response.setErrMessage(e.getMessage());
			return new Gson().toJson(response);
		}
		LOG.info("Method to get Google details, updateSharedOn() finished.");
		return new Gson().toJson("Success");
	}

	@ResponseBody
	@RequestMapping(value = "/restartsurvey")
	public void restartSurvey(HttpServletRequest request) {
		
		String surveyId = request.getParameter("surveyId");

		try {

			if (surveyId == null || surveyId.isEmpty()) {
				throw new InvalidInputException("Passed parameter survey id is null or empty");
			}

			
			surveyHandler.markSurveyAsRetake(surveyId, true, CommonConstants.RETAKE_REQUEST_CUSTOMER);
			SurveyDetails survey = surveyHandler.getSurveyDetails(surveyId);
			long agentId = survey.getAgentId();
			User user = userManagementService.getUserByUserId(agentId);
			Map<String, String> urlParams = urlGenerator.decryptUrl(survey.getUrl());
			urlParams.put(CommonConstants.URL_PARAM_RETAKE_SURVEY, "true");
			String updatedUrl = urlGenerator.generateUrl(urlParams, getApplicationBaseUrl() + CommonConstants.SHOW_SURVEY_PAGE_FOR_URL);
			surveyHandler.sendSurveyRestartMail(survey.getCustomerFirstName(), survey.getCustomerLastName(), survey.getCustomerEmail(), survey.getCustRelationWithAgent(), user, updatedUrl);
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException caught in makeSurveyEditable(). Nested exception is ", e);
		}
	}

	// Method to re-send mail to the customer for taking survey.

	@ResponseBody
	@RequestMapping(value = "/resendsurveylink", method = RequestMethod.POST)
	public void resendSurveyLink(HttpServletRequest request) {
		String agentIdStr = request.getParameter("agentId");
		String customerEmail = request.getParameter("customerEmail");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String surveyId = request.getParameter("surveyId");

		try {
			if (agentIdStr == null || agentIdStr.isEmpty()) {
				throw new InvalidInputException("Invalid value (Null/Empty) found for agentId.");
			}
			long agentId = Long.parseLong(agentIdStr);
			surveyHandler.markSurveyAsRetake(surveyId, true , CommonConstants.RETAKE_REQUEST_CUSTOMER);
			SurveyDetails survey = surveyHandler.getSurveyDetails(surveyId);
			User user = userManagementService.getUserByUserId(agentId);
			Map<String, String> urlParams = urlGenerator.decryptUrl(survey.getUrl());
			urlParams.put(CommonConstants.URL_PARAM_RETAKE_SURVEY, "true");
			String updatedUrl = urlGenerator.generateUrl(urlParams, getApplicationBaseUrl() + CommonConstants.SHOW_SURVEY_PAGE_FOR_URL);
			surveyHandler.sendSurveyRestartMail(firstName, lastName, customerEmail, survey.getCustRelationWithAgent(), user, updatedUrl);
		}
		catch (NonFatalException e) {
			LOG.error("NonfatalException caught in makeSurveyEditable(). Nested exception is ", e);
		}
	}

	@RequestMapping(value = "/notfound")
	public String showNotFoundPage(HttpServletRequest request) {
		return JspResolver.NOT_FOUND_PAGE;
	}

	private String getApplicationBaseUrl() {
		return surveyHandler.getApplicationBaseUrl();
	}

	private Map<String, Object> getSurvey(User user, SurveyPreInitiation surveyPreInitiation, String url, boolean isOldRecord, boolean retakeSurvey)
			throws InvalidInputException, SolrException, NoRecordsFetchedException {
		LOG.info("Method getSurvey started.");
		Integer stage = null;
		Map<String, Object> surveyAndStage = new HashMap<>();
		List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgent(user);
		boolean editable = false;
		BranchSettings branchSettings = null;
		OrganizationUnitSettings regionSettings = null;
		OrganizationUnitSettings companySettings = null;
		OrganizationUnitSettings unitSettings = null;
		SurveyDetails survey = null;
		try {

			survey = surveyHandler.storeInitialSurveyDetails(user, surveyPreInitiation, url, isOldRecord, retakeSurvey);

			if (survey != null) {

				surveyHandler.updateSurveyAsClicked(survey.get_id());

				stage = survey.getStage();
				editable = survey.getEditable();
				surveyAndStage.put("agentName", survey.getAgentName());
				surveyAndStage.put("customerFirstName", survey.getCustomerFirstName());
				surveyAndStage.put("customerLastName", survey.getCustomerLastName());
				surveyAndStage.put("customerEmail", survey.getCustomerEmail());
				surveyAndStage.put("surveyId", survey.get_id());
				for (SurveyQuestionDetails surveyDetails : surveyQuestionDetails) {
					for (SurveyResponse surveyResponse : survey.getSurveyResponse()) {
						if (surveyDetails.getQuestion().trim().equalsIgnoreCase(surveyResponse.getQuestion())) {
							surveyDetails.setCustomerResponse(surveyResponse.getAnswer());
						}
					}
				}
				//need to reset stage to existing count of active surveyQuestions if stage is greater then the count 
				if(stage > surveyQuestionDetails.size()-1) {
				    stage = surveyQuestionDetails.size()-1;
				}
				
				branchSettings = organizationManagementService.getBranchSettings(survey.getBranchId());
				regionSettings = organizationManagementService.getRegionSettings(survey.getRegionId());
			} else {
				surveyAndStage.put("agentName", solrSearchService.getUserDisplayNameById(user.getUserId()));
				surveyAndStage.put("customerEmail", surveyPreInitiation.getCustomerEmailId());
				surveyAndStage.put("customerFirstName", surveyPreInitiation.getCustomerFirstName());
				surveyAndStage.put("customerLastName", surveyPreInitiation.getCustomerLastName());
				surveyAndStage.put("surveyId", "");
				try {
					UserProfile userProfile = userManagementService.getAgentUserProfileForUserId(user.getUserId());
					if (userProfile != null) {
						branchSettings = organizationManagementService.getBranchSettings(userProfile.getBranchId());
						regionSettings = organizationManagementService.getRegionSettings(userProfile.getRegionId());
					}
				}
				catch (Exception e) {
					LOG.error("Exception occurred while fetching user profile for agent id " + user.getUserId() + ".Reason :", e);
				}
			}
		}
		catch (SolrException e) {
			LOG.error("SolrException caught in triggerSurvey(). Details are " + e);
			throw e;
		}

		// Used to set survey texts (which can be changed by the company admin)
		companySettings = organizationManagementService.getCompanySettings(user);
		unitSettings = organizationManagementService.getAgentSettings(user.getUserId());

		// update the logo
		OrganizationUnitSettings bSetting = null;
		if (branchSettings != null)
			bSetting = branchSettings.getOrganizationUnitSettings();

		Map<SettingsForApplication, OrganizationUnit> mapPrimaryHierarchy = null;
		try {
			mapPrimaryHierarchy = profileManagementService.getPrimaryHierarchyByEntity(CommonConstants.AGENT_ID_COLUMN, user.getUserId());
		}
		catch (InvalidSettingsStateException e1) {
			LOG.error("An Exception occurred while fetching logo url. Reason : ", e1);
		}
		catch (ProfileNotFoundException e1) {
			LOG.error("An Exception occurred while fetching logo url. Reason : ", e1);
		}

		String logo = surveyHandler.getLogoUrlWithSettings(user, (AgentSettings) unitSettings, companySettings, regionSettings, bSetting,
				mapPrimaryHierarchy);
		LOG.info("Logo to be displayed: " + logo);
		surveyAndStage.put("companyLogo", logo);

		if (unitSettings.getSurvey_settings() == null) {
			SurveySettings surveySettings = new SurveySettings();
			surveySettings.setAutoPostEnabled(true);
			surveySettings.setShow_survey_above_score(CommonConstants.DEFAULT_AUTOPOST_SCORE);
			surveySettings.setAuto_post_score(CommonConstants.DEFAULT_AUTOPOST_SCORE);
			organizationManagementService.updateScoreForSurvey(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, unitSettings,
					surveySettings);
		} else {
			if (unitSettings.getSurvey_settings().getShow_survey_above_score() <= 0) {
				unitSettings.getSurvey_settings().setAutoPostEnabled(true);
				unitSettings.getSurvey_settings().setShow_survey_above_score(CommonConstants.DEFAULT_AUTOPOST_SCORE);
				unitSettings.getSurvey_settings().setAuto_post_score(CommonConstants.DEFAULT_AUTOPOST_SCORE);
				organizationManagementService.updateScoreForSurvey(MongoOrganizationUnitSettingDaoImpl.AGENT_SETTINGS_COLLECTION, unitSettings,
						unitSettings.getSurvey_settings());
			}
		}

		// Flags to check if any particular text is not set in the
		// companySettings
		boolean isHappyTextSet = false, isNeutralTextSet = false, isSadTextSet = false, isHappyTextCompleteSet = false,
				isNeutralTextCompleteSet = false, isSadTextCompleteSet = false;

		if (unitSettings != null && unitSettings.getSurvey_settings() != null) {
			SurveySettings surveySettings = unitSettings.getSurvey_settings();
			// AutopostScore and autopostEnabled values, we get from the agent
			surveyAndStage.put("autopostScore", surveySettings.getShow_survey_above_score());
			surveyAndStage.put("autopostEnabled", surveySettings.isAutoPostEnabled());
		}

		//get Facebook pixel detail
		String facebookPixelTag = organizationManagementService.getFacebookPixelImageTagsFromHierarchy( companySettings, regionSettings, bSetting, unitSettings );
		surveyAndStage.put("facebookPixelTag", facebookPixelTag);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Map<String, String> surveyMap = emailFormatHelper.fetchSurveySourceId(user.getUserId(), survey.getCustomerEmail(),
				dateFormat.format(new Date()));
		if (companySettings != null && companySettings.getSurvey_settings() != null) {
			LOG.info("Setting company specific values for surveyAndStage started");
			SurveySettings surveySettings = companySettings.getSurvey_settings();
			if (StringUtils.isNotEmpty(surveySettings.getHappyText())) {
			    //add Facebook pixel tag in happy text 
			    String happyText = surveySettings.getHappyText() + facebookPixelTag;
				surveyAndStage.put("happyText", surveyHandler.replaceGatewayQuestionText(happyText, unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isHappyTextSet = true;
			}
			if (StringUtils.isNotEmpty(surveySettings.getNeutralText())) {
				surveyAndStage.put("neutralText", surveyHandler.replaceGatewayQuestionText(surveySettings.getNeutralText(), unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isNeutralTextSet = true;
			}
			if (StringUtils.isNotEmpty(surveySettings.getSadText())) {
				surveyAndStage.put("sadText", surveyHandler.replaceGatewayQuestionText(surveySettings.getSadText(), unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isSadTextSet = true;
			}
			if (StringUtils.isNotEmpty(surveySettings.getHappyTextComplete())) {
				surveyAndStage.put("happyTextComplete", surveyHandler.replaceGatewayQuestionText(surveySettings.getHappyTextComplete(), unitSettings,
						user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isHappyTextCompleteSet = true;
			}
			if (StringUtils.isNotEmpty(surveySettings.getNeutralTextComplete())) {
				surveyAndStage.put("neutralTextComplete", surveyHandler.replaceGatewayQuestionText(surveySettings.getNeutralTextComplete(),
						unitSettings, user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isNeutralTextCompleteSet = true;
			}
			if (StringUtils.isNotEmpty(surveySettings.getSadTextComplete())) {
				surveyAndStage.put("sadTextComplete", surveyHandler.replaceGatewayQuestionText(surveySettings.getSadTextComplete(), unitSettings,
						user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
				isSadTextCompleteSet = true;
			}
			LOG.info("Setting company specific values for surveyAndStage finished");
		}

		// If any of the texts are not set by the company, store default values
		// for them.
		if (!(isHappyTextSet && isNeutralTextSet && isSadTextSet && isHappyTextCompleteSet && isNeutralTextCompleteSet && isSadTextCompleteSet)) {
			LOG.info("Setting default values for surveyAndStage started");
			SurveySettings defaultSurveySettings = organizationManagementService.retrieveDefaultSurveyProperties();
			if (!isHappyTextSet) {
			  //add Facebook pixel tag in happy text 
                String happyText = defaultSurveySettings.getHappyText() + facebookPixelTag;
				surveyAndStage.put("happyText", surveyHandler.replaceGatewayQuestionText(happyText, unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			if (!isNeutralTextSet) {
				surveyAndStage.put("neutralText", surveyHandler.replaceGatewayQuestionText(defaultSurveySettings.getNeutralText(), unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			if (!isSadTextSet) {
				surveyAndStage.put("sadText", surveyHandler.replaceGatewayQuestionText(defaultSurveySettings.getSadText(), unitSettings, user,
						companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			if (!isHappyTextCompleteSet) {
				surveyAndStage.put("happyTextComplete", surveyHandler.replaceGatewayQuestionText(defaultSurveySettings.getHappyTextComplete(),
						unitSettings, user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			if (isNeutralTextCompleteSet) {
				surveyAndStage.put("neutralTextComplete", surveyHandler.replaceGatewayQuestionText(defaultSurveySettings.getNeutralTextComplete(),
						unitSettings, user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			if (isSadTextCompleteSet) {
				surveyAndStage.put("sadTextComplete", surveyHandler.replaceGatewayQuestionText(defaultSurveySettings.getSadTextComplete(),
						unitSettings, user, companySettings, survey, logo, mapPrimaryHierarchy, regionSettings, bSetting, surveyMap));
			}
			LOG.info("Setting default values for surveyAndStage finished");
		}

		// Fetching Yelp Url
		surveyHandler.updateSurveyStageForYelp(unitSettings, branchSettings, regionSettings, companySettings, surveyAndStage);

		// Fetching Google Plus Url
		try {
			if (unitSettings.getSocialMediaTokens().getGoogleToken().getProfileLink() != null) {
				surveyAndStage.put("googleEnabled", true);
				surveyAndStage.put("googleLink", unitSettings.getSocialMediaTokens().getGoogleToken().getProfileLink());
			} else
				surveyAndStage.put("googleEnabled", false);
		}
		catch (NullPointerException e) {
			surveyAndStage.put("googleEnabled", false);
		}

		// zillow
		surveyHandler.updateSurveyStageForZillow(unitSettings, branchSettings, regionSettings, companySettings, surveyAndStage);

		// Fetching LendingTree Url
		surveyHandler.updateSurveyStageForLendingTree(unitSettings, branchSettings, regionSettings, companySettings, surveyAndStage);

		// realtor
		surveyHandler.updateSurveyStageForRealtor(unitSettings, branchSettings, regionSettings, companySettings, surveyAndStage);

		// Google Business Token
		surveyHandler.updateSurveyStageForGoogleBusinessToken(unitSettings, branchSettings, regionSettings, companySettings, surveyAndStage);

		// adding facebook and google plus api keys for customer share
		surveyAndStage.put("fbAppId", facebookAppId);
		surveyAndStage.put("googlePlusAppId", googlePlusId);

		surveyAndStage.put("agentFullProfileLink", getApplicationBaseUrl() + CommonConstants.AGENT_PROFILE_FIXED_URL + unitSettings.getProfileUrl());
		surveyAndStage.put("agentProfileLink", unitSettings.getProfileUrl());
		surveyAndStage.put("stage", stage);
		surveyAndStage.put("survey", surveyQuestionDetails);
		surveyAndStage.put("agentId", user.getUserId());
		surveyAndStage.put("editable", editable);
		surveyAndStage.put("source", surveyPreInitiation.getSurveySource());
		surveyAndStage.put("hiddenSection", companySettings.isHiddenSection());
		surveyAndStage.put("companyName", user.getCompany().getCompany());
		surveyAndStage.put("companyId", companySettings.getIden());
		if(companySettings != null) {
		    surveyAndStage.put("copyToClipBoard", companySettings.getIsCopyToClipboard());
		}

		LOG.info("Method getSurvey finished.");
		return surveyAndStage;
	}

	private void reportBug(String socAppName, String name, Exception e) {
		try {
			LOG.info("Building error message for the auto post failure");
			String errorMsg = "<br>" + e.getMessage() + "<br><br>";
			if (socAppName.length() > 0)
				errorMsg += "Social Application : " + socAppName;
			errorMsg += "<br>Agent Name : " + name + "<br>";
			errorMsg += "<br>StackTrace : <br>" + ExceptionUtils.getStackTrace(e).replaceAll("\n", "<br>") + "<br>";
			LOG.info("Error message built for the auto post failure");
			LOG.info("Sending bug mail to admin for the auto post failure");
			emailServices.sendReportBugMailToAdmin(applicationAdminName, errorMsg, applicationAdminEmail);
			LOG.info("Sent bug mail to admin for the auto post failure");
		}
		catch (UndeliveredEmailException ude) {
			LOG.error("error while sending report bug mail to admin ", ude);
		}
		catch (InvalidInputException iie) {
			LOG.error("error while sending report bug mail to admin ", iie);
		}
	}

	@ResponseBody
	@RequestMapping(value = "/bulk/uploadSurvey", method = RequestMethod.POST)
	public String bulkUploadSurvey(HttpServletRequest request) {
		String authorizationHeader = request.getHeader("Authorization");
		Map<String, String> params = new HashMap<String, String>();
		String message = "";
		String surveyJsonString = "";
		boolean error = false;
		long companyId = 0;

		if (authorizationHeader == null || authorizationHeader.isEmpty()) {
			message = DisplayMessageConstants.INVALID_AUTHORIZATION_HEADER;
			error = true;
		}
		if (!error) {
			LOG.debug("Validating authroization header ");
			try {
				String plainText = encryptionHelper.decryptAES(authorizationHeader, "");
				String keyValuePairs[] = plainText.split("&");

				for (int counter = 0; counter < keyValuePairs.length; counter += 1) {
					String[] keyValuePair = keyValuePairs[counter].split("=");
					params.put(keyValuePair[0], keyValuePair[1]);
				}
			}
			catch (InvalidInputException e) {
				message = DisplayMessageConstants.INVALID_AUTHORIZATION_HEADER;
				error = true;
			}
		}
		if (!error) {
			if (!surveyHandler.validateDecryptedApiParams(params)) {
				error = true;
			} else {
				companyId = Long.valueOf(params.get(CommonConstants.COMPANY_ID_COLUMN));
			}
		}
		if (!error) {
			LOG.debug("The authorization header is valid ");
			surveyJsonString = request.getParameter("SurveyList");
			if (surveyJsonString == null || surveyJsonString.isEmpty()) {
				message = DisplayMessageConstants.INVALID_SURVEY_JSON;
				error = true;
			}
		}
		List<BulkSurveyDetail> bulkSurveyList = new ArrayList<BulkSurveyDetail>();

		if (!error) {
			bulkSurveyList = new Gson().fromJson(surveyJsonString, new TypeToken<List<BulkSurveyDetail>>() {}.getType());
			List<BulkSurveyDetail> list = surveyHandler.processBulkSurvey(bulkSurveyList, companyId);
			message = new Gson().toJson(list);
		}
		return message;
	}

	@ResponseBody
	@RequestMapping(value = "/apicheck/abusivephrase", method = RequestMethod.GET)
	public String getAbusivePhrase(@QueryParam(value = "feedback") String feedback) {
		LOG.debug("Checking the abusive phrase for feedback " + feedback);
		String phrase = null;
		if (feedback != null && !feedback.isEmpty()) {
			feedback = feedback.toLowerCase();
			String[] swearWords = surveyHandler.getSwearList();
			List<String> swearList = Arrays.asList(swearWords);
			String reviewParts[] = feedback.split(" ");
			for (String reviewWord : reviewParts) {
				if (swearList.contains(reviewWord.trim().toLowerCase())) {
					LOG.info("Method to check review for abusive words, checkReviewForSwearWords ended");
					phrase = reviewWord;
					break;
				}
			}
		}
		return phrase;
	}

	private String getLogoBasedOnSettingsForUser(long agentId, AgentSettings agentSettings) {
		LOG.debug("Getting logo based on settings for user " + agentId);
		String logoUrl = null;
		try {
			Map<String, Long> hierarchyMap = null;
			Map<SettingsForApplication, OrganizationUnit> map = profileManagementService.getPrimaryHierarchyByEntity(CommonConstants.AGENT_ID_COLUMN,
					agentId);
			hierarchyMap = profileManagementService.getPrimaryHierarchyByAgentProfile(agentSettings);

			long companyId = hierarchyMap.get(CommonConstants.COMPANY_ID_COLUMN);
			long regionId = hierarchyMap.get(CommonConstants.REGION_ID_COLUMN);
			long branchId = hierarchyMap.get(CommonConstants.BRANCH_ID_COLUMN);
			if (map != null) {
				OrganizationUnit organizationUnit = map.get(SettingsForApplication.LOGO);
				if (organizationUnit == OrganizationUnit.COMPANY) {
					OrganizationUnitSettings companySettings = organizationManagementService.getCompanySettings(companyId);
					logoUrl = companySettings.getLogo();
				} else if (organizationUnit == OrganizationUnit.REGION) {
					OrganizationUnitSettings regionSettings = organizationManagementService.getRegionSettings(regionId);
					logoUrl = regionSettings.getLogo();
				} else if (organizationUnit == OrganizationUnit.BRANCH) {
					OrganizationUnitSettings branchSettings = organizationManagementService.getBranchSettingsDefault(branchId);
					logoUrl = branchSettings.getLogo();
				} else if (organizationUnit == OrganizationUnit.AGENT) {
					logoUrl = agentSettings.getLogo();
				}
			}
		}
		catch (InvalidInputException | InvalidSettingsStateException | ProfileNotFoundException | NoRecordsFetchedException e) {
			LOG.warn("Error while fetching logo url " + e.getMessage());
		}
		LOG.debug("Returning logo url: " + logoUrl);
		return logoUrl;
	}

}
// JIRA SS-119 by RM-05 : EOC