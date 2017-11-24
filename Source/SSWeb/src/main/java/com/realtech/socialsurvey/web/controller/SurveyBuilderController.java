package com.realtech.socialsurvey.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyAnswerOptions;
import com.realtech.socialsurvey.core.entities.SurveyDetail;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsAnswerOption;
import com.realtech.socialsurvey.core.entities.SurveyQuestionsMapping;
import com.realtech.socialsurvey.core.entities.SurveyTemplate;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.api.builder.SSApiIntergrationBuilder;
import com.realtech.socialsurvey.web.common.JspResolver;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@Controller
public class SurveyBuilderController {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderController.class);

	@Autowired
	private SurveyBuilder surveyBuilder;

	@Autowired
	private SessionHelper sessionHelper;

	@Autowired
	private MessageUtils messageUtils;

	@Value("${MINIMUM_RATING_QUESTIONS}")
	private int minRatingQuestions;
	
	private SSApiIntergrationBuilder ssApiIntergrationBuilder;
	

	@Autowired
	public void setSsApiIntergrationBuilder(SSApiIntergrationBuilder ssApiIntergrationBuilder) {
		this.ssApiIntergrationBuilder = ssApiIntergrationBuilder;
	}

	/**
	 * Method to show the build survey page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/showbuildsurveypage", method = RequestMethod.GET)
	public String showBuildSurveyPage(Model model, HttpServletRequest request) {
		LOG.info("Method showBuildSurveyPage started");
		User user = sessionHelper.getCurrentUser();
		boolean isSurveyBuildingAllowed = false;

		try {
			isSurveyBuildingAllowed = surveyBuilder.isSurveyBuildingAllowed(user);

			if (!isSurveyBuildingAllowed) {
				LOG.error("User not allowed to access BuildSurvey Page. Reason: Access Denied");
				model.addAttribute("message", "User not authorized to access BuildSurvey Page. Reason: Access Denied");
			}
			else {
				return JspResolver.SURVEY_BUILDER;
			}
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException in showBuildSurveyPage. Reason:" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.MESSAGE_HEADER;
	}

	@RequestMapping(value = "/revertquestionoverlay", method = RequestMethod.GET)
	public String revertAddQuestionOverlay(Model model, HttpServletRequest request) {
		LOG.info("Method revertAddQuestionOverlay started");
		return JspResolver.SURVEY_BUILDER_QUESTION_OVERLAY;
	}

	@RequestMapping(value = "/populatenewform", method = RequestMethod.GET)
	public String populateNewForm(Model model, HttpServletRequest request) {
		LOG.info("Method populateNewForm started");
		
		String order = request.getParameter("order");
		model.addAttribute("order", order);
		
		return JspResolver.SURVEY_BUILDER_QUESTION_NEW;
	}

	/**
	 * Web controller method to add question to existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addquestiontosurvey", method = RequestMethod.POST)
	public String addQuestionToExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addQuestionToExistingSurvey of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		Map<String, String> statusMap = new HashMap<String, String>();
		String message = "";
		String statusJson = "";

		String isUserRankingStr = request.getParameter("user-ranking-ques");
		String isNPSStr = request.getParameter("nps-ques");
		//Created VO and added required fields.
		SurveyQuestionDetails questionDetails = new SurveyQuestionDetails();
		questionDetails.setUserId(user.getUserId());
		questionDetails.setCompanyId(user.getCompany().getCompanyId());
		questionDetails.setVerticalId(user.getCompany().getVerticalsMaster().getVerticalsMasterId());
		// Order of question
		String order = request.getParameter("order");
		// Creating new SurveyQuestionDetails from form
		String questionType = request.getParameter("sb-question-type-" + order);

		questionDetails.setQuestion(request.getParameter("sb-question-txt-" + order));
		questionDetails.setQuestionType(questionType);
		List<String> answers = Arrays.asList(request.getParameterValues("sb-answers-" + order + "[]"));

		questionDetails.setIsNPSStr(isNPSStr);
		questionDetails.setIsUserRankingStr(isUserRankingStr);
		questionDetails.setAnswerStr(answers);
		Response response = null; 
		try{
			response = ssApiIntergrationBuilder.getIntegrationApi()
					.addQuestionToExistingSurvey(questionDetails);
			if(response.getStatus() == 200) {
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_MAPPING_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
						.getMessage();
				String questionId = new String( ( (TypedByteArray) response.getBody() ).getBytes() );
				statusMap.put("questionId", questionId);
				statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
			} 
		} catch (Exception e) {
			LOG.error("API call exception", e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put("status", CommonConstants.ERROR);
		}
		
		statusMap.put("message", message);
		statusJson = new Gson().toJson(statusMap);
		return statusJson;
	}
	

	/**
	 * Method to update question from existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/updatequestionfromsurvey", method = RequestMethod.POST)
	public String updateQuestionFromExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method updateQuestionFromExistingSurvey of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		Map<String, String> statusMap = new HashMap<String, String>();
		String message = "";
		String statusJson = "";

		String isUserRankingStr = request.getParameter("user-ranking-ques");
		String isNPSStr = request.getParameter("nps-ques");
		//Created VO and added required fields.
		SurveyQuestionDetails questionDetails = new SurveyQuestionDetails();
		questionDetails.setUserId(user.getUserId());
		questionDetails.setCompanyId(user.getCompany().getCompanyId());
		questionDetails.setVerticalId(user.getCompany().getVerticalsMaster().getVerticalsMasterId());
		// Order of question
		String order = request.getParameter("order");
		// Creating new SurveyQuestionDetails from form
		String questionType = request.getParameter("sb-question-type-" + order);

		questionDetails.setQuestion(request.getParameter("sb-question-txt-" + order));
		questionDetails.setQuestionType(questionType);
		questionDetails.setQuestionId(Long.parseLong(request.getParameter("questionId")));
		List<String> answers = Arrays.asList(request.getParameterValues("sb-answers-" + order + "[]"));

		questionDetails.setIsNPSStr(isNPSStr);
		questionDetails.setIsUserRankingStr(isUserRankingStr);
		questionDetails.setAnswerStr(answers);
		Response response = null; 
		try {
			response = ssApiIntergrationBuilder.getIntegrationApi().updateQuestionFromExistingSurvey(questionDetails);
			if (response.getStatus() == 200) {
				message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_MODIFY_SUCCESSFUL,
						DisplayMessageType.SUCCESS_MESSAGE).getMessage();
				statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
				LOG.info("Method updateQuestionFromExistingSurvey of SurveyBuilderController finished successfully");
			}
		}
		catch (Exception e) {
			LOG.error("Exception occured in SS-API while updating survey question.", e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put("status", CommonConstants.ERROR);
		}

		statusMap.put("message", message);
		statusJson = new Gson().toJson(statusMap);
		return statusJson;
	}

	/**
	 * Method to deactivate question from existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removequestionfromsurvey", method = RequestMethod.POST)
	public String removeQuestionFromExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method removequestionfromsurvey of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String message = "";

		try {
			// Check if default survey is mapped and clone it
			Map<Long, Long> oldToNewQuestionMap = surveyBuilder.checkIfSurveyIsDefaultAndClone(user);

			long surveyQuestionId;
			if (oldToNewQuestionMap != null) {
				surveyQuestionId = oldToNewQuestionMap.get(Long.parseLong(request.getParameter("questionId")));
				LOG.info(" Mapping question id : " + surveyQuestionId + " to : " + oldToNewQuestionMap.get(surveyQuestionId));
			}
			else {
				surveyQuestionId = Long.parseLong(request.getParameter("questionId"));
			}

			surveyBuilder.deactivateQuestionSurveyMapping(user, surveyQuestionId);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method removequestionfromsurvey of SurveyBuilderController finished successfully");
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while disabling question. Reason:" + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while disabling Question from Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.NO_SURVEY_FOUND, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to deactivate questions from existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/removequestionsfromsurvey", method = RequestMethod.POST)
	public String removeQuestionsFromExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method removequestionfromsurvey of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String message = "";

		try {
			// Check if default survey is mapped and clone it
			Map<Long, Long> oldToNewQuestionMap = surveyBuilder.checkIfSurveyIsDefaultAndClone(user);

			String questionIdsStr = request.getParameter("questionIds");
			List<String> surveyQuestionIdStrs = Arrays.asList(questionIdsStr.split(","));

			for (String questionIdStr : surveyQuestionIdStrs) {
				if (oldToNewQuestionMap != null) {
					LOG.info(" Mapping question id : " + questionIdStr + " to : " + oldToNewQuestionMap.get(Long.parseLong(questionIdStr)));
					surveyBuilder.deactivateQuestionSurveyMapping(user, oldToNewQuestionMap.get(Long.parseLong(questionIdStr)));
				}
				else {
					surveyBuilder.deactivateQuestionSurveyMapping(user, Long.parseLong(questionIdStr));
				}
			}
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTIONS_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method removequestionfromsurvey of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while disabling Questions from Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.NO_SURVEY_FOUND, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to reorder question in existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/reorderQuestion", method = RequestMethod.POST)
	public String reorderQuestion(Model model, HttpServletRequest request) {
		LOG.info("Method reorderQuestion of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String message = "";

		try {
			// Check if default survey is mapped and clone it
			Map<Long, Long> oldToNewQuestionMap = surveyBuilder.checkIfSurveyIsDefaultAndClone(user);
			
			long surveyQuestionId;
			if (oldToNewQuestionMap != null) {
				surveyQuestionId = oldToNewQuestionMap.get(Long.parseLong(request.getParameter("questionId")));
				LOG.info(" Mapping question id : " + surveyQuestionId + " to : " + oldToNewQuestionMap.get(surveyQuestionId));
			}
			else {
				surveyQuestionId = Long.parseLong(request.getParameter("questionId"));
			}

			String reorderType = request.getParameter("reorderType");
			surveyBuilder.reorderQuestion(user, surveyQuestionId, reorderType);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_REORDER_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method reorderQuestion of SurveyBuilderController finished successfully");
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while reordering question. Reason:" + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while reordering Question from Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.NO_SURVEY_FOUND, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to return active survey for company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getactivesurveyquestions", method = RequestMethod.GET)
	public String getActiveSurveyDetails(Model model, HttpServletRequest request) {
		LOG.info("Method getSurveyDetails of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String status = "";

		SurveyDetail surveyDetail = new SurveyDetail();
		List<SurveyQuestionDetails> surveyQuestions;
		try {
			// Fetch active questions
			surveyQuestions = surveyBuilder.getAllActiveQuestionsOfMappedSurvey(user);
			surveyDetail.setQuestions(surveyQuestions);

			// Fetch count of active Rating questions
			int activeRatingQues = (int) surveyBuilder.countActiveRatingQuestionsInSurvey(user);
			surveyDetail.setCountOfRatingQuestions( activeRatingQues );
			if (activeRatingQues < minRatingQuestions) {
				LOG.info("Marking Survey as inactive");
				surveyBuilder.changeSurveyStatus(user, CommonConstants.NO);
				status = "It is recommended to add " + (minRatingQuestions - activeRatingQues) + " more rating question(s)";
			} else {
				LOG.info("Marking Survey as active");
				surveyBuilder.changeSurveyStatus(user, CommonConstants.YES);
			}
			surveyDetail.setStatus(status);

			model.addAttribute("surveyDetail", surveyDetail);
			LOG.info("Method getSurveyDetails of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.warn("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.SURVEY_BUILDER_QUESTION_LIST;
	}
	
	@RequestMapping(value = "/getsurveyquestion", method = RequestMethod.GET)
	public String getSurveyQuestionDetails(Model model, HttpServletRequest request) {
		LOG.info("Method getSurveyQuestionDetails of SurveyBuilderController called");

		SurveyQuestionDetails surveyQuestion = new SurveyQuestionDetails();
		try {
			long questionMappingId = Long.parseLong(request.getParameter("questionId"));
			SurveyQuestionsMapping questionsMapping = surveyBuilder.getSurveyQuestionFromMapping(questionMappingId);
			SurveyQuestion question = questionsMapping.getSurveyQuestion();
			
			surveyQuestion.setQuestionId(questionMappingId);
			surveyQuestion.setQuestion(question.getSurveyQuestion());
			surveyQuestion.setQuestionType(question.getSurveyQuestionsCode());
			surveyQuestion.setIsUserRankingQuestion( questionsMapping.getIsUserRankingQuestion() );
            surveyQuestion.setIsRatingQuestion( questionsMapping.getIsRatingQuestion() );
			
			// For each answer
			SurveyAnswerOptions surveyAnswerOptions = null;
			List<SurveyAnswerOptions> answerOptionsToQuestion = new ArrayList<SurveyAnswerOptions>();
			for (SurveyQuestionsAnswerOption surveyQuestionsAnswerOption : question.getSurveyQuestionsAnswerOptions()) {
				if (surveyQuestionsAnswerOption.getStatus() != CommonConstants.STATUS_ACTIVE) {
					continue;
				}
				
				surveyAnswerOptions = new SurveyAnswerOptions();

				surveyAnswerOptions.setAnswerId(surveyQuestionsAnswerOption.getSurveyQuestionsAnswerOptionsId());
				surveyAnswerOptions.setAnswerText(surveyQuestionsAnswerOption.getAnswer());
				surveyAnswerOptions.setAnswerOrder(surveyQuestionsAnswerOption.getAnswerOrder());

				answerOptionsToQuestion.add(surveyAnswerOptions);
			}
			surveyQuestion.setAnswers(answerOptionsToQuestion);
			
			model.addAttribute("surveyQuestion", surveyQuestion);
			LOG.info("Method getSurveyQuestionDetails of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.warn("InvalidInputException while fetching SurveyQuestion: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return JspResolver.SURVEY_BUILDER_QUESTION_EDIT;
	}

	/**
	 * Method to create new survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getactivesurveytemplates", method = RequestMethod.GET)
	public String fetchSurveyTemplates(Model model, HttpServletRequest request) {
		LOG.info("Method fetchSurveyTemplates of SurveyBuilderController called");
		String templatesJson = null;
		User user = sessionHelper.getCurrentUser();
		try {
			List<SurveyTemplate> surveytemplates = surveyBuilder.getSurveyTemplates(user);
			templatesJson = new Gson().toJson(surveytemplates);
			LOG.info("Method fetchSurveyTemplates of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.warn("InvalidInputException while fetching SurveyTemplates: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return templatesJson;
	}

	/**
	 * Method to deactivate survey for company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/deletesurveyforcompany", method = RequestMethod.POST)
	public String deactivateSurveyCompanyMapping(Model model, HttpServletRequest request) {
		LOG.info("Method deactivateSurveyCompanyMapping of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String message = "";

		try {
			surveyBuilder.deactivateSurveyCompanyMapping(user);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_COMPANY_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method deactivateSurveyCompanyMapping of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while disabling Survey from company: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}

	/**
	 * Method to activate survey for company from template
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/activatesurveyfromtemplate", method = RequestMethod.POST)
	public String activateSurveyFromTemplate(Model model, HttpServletRequest request) {
		LOG.info("Method activateSurveyFromTemplate of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String message = "";

		try {
			long templateId = Long.parseLong(request.getParameter("templateId"));

			surveyBuilder.cloneSurveyFromTemplate(user, templateId);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_TEMPLATE_CLONE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method activateSurveyFromTemplate of SurveyBuilderController finished successfully");
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while cloning survey for company. Reason:" + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while cloning Survey for company: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while cloning Survey for company: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
	}
}