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
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyAnswerOptions;
import com.realtech.socialsurvey.core.entities.SurveyDetail;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.SurveyTemplate;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.enums.DisplayMessageType;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

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

	/**
	 * Method to add question to existing survey
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

		try {
			// Check if survey is default one and clone it
			surveyBuilder.checkIfSurveyIsDefaultAndClone(user);
			
			// Getting the survey for user
			Survey survey = surveyBuilder.checkForExistingSurvey(user);
			if (survey == null) {
				survey = surveyBuilder.createNewSurvey(user);
			}
			
			// Order of question 
			String order = request.getParameter("order");

			// Creating new SurveyQuestionDetails from form
			String questionType = request.getParameter("sb-question-type-" + order);
			int activeQuestionsInSurvey = (int) surveyBuilder.countActiveQuestionsInSurvey(survey);

			SurveyQuestionDetails questionDetails = new SurveyQuestionDetails();
			questionDetails.setQuestion(request.getParameter("sb-question-txt-" + order));
			questionDetails.setQuestionType(questionType);
			questionDetails.setQuestionOrder(activeQuestionsInSurvey + 1);

			if (questionType.indexOf(CommonConstants.QUESTION_RATING) != -1) {
				questionDetails.setIsRatingQuestion(CommonConstants.QUESTION_RATING_VALUE_TRUE);
			}
			else {
				questionDetails.setIsRatingQuestion(CommonConstants.QUESTION_RATING_VALUE_FALSE);
			}

			if (questionType.indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1) {
				List<SurveyAnswerOptions> answers = new ArrayList<SurveyAnswerOptions>();
				List<String> strAnswers = Arrays.asList(request.getParameterValues("sb-answers-" + order + "[]"));

				SurveyAnswerOptions surveyAnswerOptions;
				int answerOrder = 1;
				for (String answerStr : strAnswers) {
					/*if (answerStr.equals("")) {
						LOG.error("Answer text cannot be empty !");
						throw new InvalidInputException("Answer text cannot be empty !");
					}*/
					if (!answerStr.equals("")) {
						surveyAnswerOptions = new SurveyAnswerOptions();
						surveyAnswerOptions.setAnswerText(answerStr);
						surveyAnswerOptions.setAnswerOrder(answerOrder);
						answers.add(surveyAnswerOptions);

						answerOrder++;
					}
				}
				questionDetails.setAnswers(answers);
			}

			// Adding the question to survey
			long surveyQuestionMappingId = surveyBuilder.addQuestionToExistingSurvey(user, survey, questionDetails);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_MAPPING_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();

			statusMap.put("questionId", surveyQuestionMappingId + "");
			statusMap.put("status", CommonConstants.SUCCESS_ATTRIBUTE);
			statusJson = new Gson().toJson(statusMap);;
			
			LOG.info("Method addQuestionToExistingSurvey of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.INVALID_SURVEY_ANSWER, DisplayMessageType.ERROR_MESSAGE).getMessage();
			statusMap.put("status", CommonConstants.ERROR);
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.NO_SURVEY_FOUND, DisplayMessageType.ERROR_MESSAGE).getMessage();
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
		String message = "";

		try {
			// Check if survey is default one and clone it
			Map<Long, Long> oldToNewQuestionMap = surveyBuilder.checkIfSurveyIsDefaultAndClone(user);

			long surveyQuestionId;
			if (oldToNewQuestionMap != null) {
				surveyQuestionId = oldToNewQuestionMap.get(Long.parseLong(request.getParameter("questionId")));
				LOG.info(" Mapping question id : " + surveyQuestionId + " to : " + oldToNewQuestionMap.get(surveyQuestionId));
			}
			else {
				surveyQuestionId = Long.parseLong(request.getParameter("questionId"));
			}

			// Order of question 
			String order = request.getParameter("order");

			// Creating new SurveyQuestionDetails from form
			String questionType = request.getParameter("sb-question-type-" + order);
			// String questionType = request.getParameter("sb-question-edit-type");
			SurveyQuestionDetails questionDetails = new SurveyQuestionDetails();
			questionDetails.setQuestion(request.getParameter("sb-question-txt-" + order));
			// questionDetails.setQuestion(request.getParameter("sb-question-edit-txt"));
			questionDetails.setQuestionType(questionType);
			questionDetails.setIsRatingQuestion(1);

			if (questionType.indexOf(CommonConstants.QUESTION_MULTIPLE_CHOICE) != -1) {
				List<SurveyAnswerOptions> answers = new ArrayList<SurveyAnswerOptions>();
				// List<String> strAnswerIds = Arrays.asList(request.getParameterValues("sb-edit-answers-id[]"));
				List<String> strAnswerTexts = Arrays.asList(request.getParameterValues("sb-answers-" + order + "[]"));
				// List<String> strAnswerTexts = Arrays.asList(request.getParameterValues("sb-edit-answers-text[]"));

				int answerOrder = 1;
				SurveyAnswerOptions surveyAnswer;
				for (String answerStr : strAnswerTexts) {
					/*if (answerStr.equals("")) {
						LOG.error("Answer text cannot be empty");
						throw new InvalidInputException("Answer text cannot be empty !");
					}*/
					if (!answerStr.equals("")) {
						surveyAnswer = new SurveyAnswerOptions();
						surveyAnswer.setAnswerText(answerStr);
						surveyAnswer.setAnswerOrder(answerOrder);
						answers.add(surveyAnswer);

						answerOrder++;
					}
				}
				/*for (String answerIdStr : strAnswerIds) {
					long answerId = Long.parseLong(answerIdStr);

					surveyAnswer = new SurveyAnswerOptions();
					String answerStr = strAnswerTexts.get(answerOrder);
					if (answerStr.equals("")) {
						LOG.error("Answer text cannot be empty");
						throw new InvalidInputException("Answer text cannot be empty !");
					}
					surveyAnswer.setAnswerText(answerStr);
					surveyAnswer.setAnswerId(answerId);
					answers.add(surveyAnswer);

					answerOrder++;
				}*/
				questionDetails.setAnswers(answers);
			}
			surveyBuilder.updateQuestionAndAnswers(user, surveyQuestionId, questionDetails);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_MODIFY_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE)
					.getMessage();
			LOG.info("Method updateQuestionFromExistingSurvey of SurveyBuilderController finished successfully");
		}
		catch (NumberFormatException e) {
			LOG.error("NumberFormatException while updating question. Reason:" + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getMessage(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException while updating question. Reason: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		catch (NoRecordsFetchedException e) {
			LOG.error("NoRecordsFetchedException while adding Question to Survey: " + e.getMessage(), e);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.NO_SURVEY_FOUND, DisplayMessageType.ERROR_MESSAGE).getMessage();
		}
		return message;
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
	@ResponseBody
	@RequestMapping(value = "/getactivesurveyquestions", method = RequestMethod.GET)
	public String getActiveSurveyDetails(Model model, HttpServletRequest request) {
		LOG.info("Method getSurveyDetails of SurveyBuilderController called");
		User user = sessionHelper.getCurrentUser();
		String surveyJson = "";
		String status = "";

		SurveyDetail surveyDetail = new SurveyDetail();
		List<SurveyQuestionDetails> surveyQuestions;
		try {
			// Fetch active questions
			surveyQuestions = surveyBuilder.getAllActiveQuestionsOfMappedSurvey(user);
			surveyDetail.setQuestions(surveyQuestions);

			// Fetch count of active Rating questions
			int activeRatingQues = (int) surveyBuilder.countActiveRatingQuestionsInSurvey(user);
			if (activeRatingQues < minRatingQuestions) {
				LOG.info("Marking Survey as inactive");
				surveyBuilder.changeSurveyStatus(user, CommonConstants.NO);
				status = "Add " + (minRatingQuestions - activeRatingQues) + " more rating questions to activate the survey";
			} else {
				LOG.info("Marking Survey as active");
				surveyBuilder.changeSurveyStatus(user, CommonConstants.YES);
			}
			surveyDetail.setStatus(status);

			// Converting to json
			surveyJson = new Gson().toJson(surveyDetail);
			LOG.info("Method getSurveyDetails of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			LOG.warn("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
		}
		return surveyJson;
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