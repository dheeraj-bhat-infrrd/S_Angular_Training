package com.realtech.socialsurvey.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.Survey;
import com.realtech.socialsurvey.core.entities.SurveyQuestion;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
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
	private MessageUtils messageUtils;

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
		
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String highestRole = (Integer) request.getSession(false).getAttribute(CommonConstants.HIGHEST_ROLE_ID_IN_SESSION) + "";
		boolean isSurveyBuildingAllowed = false;
		try {
			LOG.debug("Calling service for checking the status of regions already added");
			isSurveyBuildingAllowed = surveyBuilder.isSurveyBuildingAllowed(user, highestRole);
			
			if(!isSurveyBuildingAllowed) {
				LOG.error("User not allowed to access BuildSurvey Page. Reason: Access Denied");
				model.addAttribute("message", "User not authorized to access BuildSurvey Page. Reason: Access Denied");
				return JspResolver.MESSAGE_HEADER;
			}
			return JspResolver.SURVEY_BUILDER;
		}
		catch (InvalidInputException e) {
			LOG.error("InvalidInputException in showBuildSurveyPage. Reason:" + e.getMessage(), e);
			model.addAttribute("message", messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE));
			return JspResolver.MESSAGE_HEADER;
		}
	}
	
	/**
	 * Method to create new survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/fetchsurveytemplates", method = RequestMethod.GET)
	public String fetchSurveyTemplates(Model model, HttpServletRequest request) {
		LOG.info("Method fetchSurveyTemplates of SurveyBuilderController called");
		String jspToReturn = null;
		try {
			List<Survey> surveytemplates = surveyBuilder.getSurveyTemplates();
			model.addAttribute("surveytemplates", surveytemplates);
			// TODO return JSP
			LOG.info("Method fetchSurveyTemplates of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			jspToReturn = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while fetching SurveyTemplates: " + e.getMessage(), e);
		}
		return jspToReturn;
	}

	/**
	 * Method to create new survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createsurvey", method = RequestMethod.POST)
	@ResponseBody
	public String createNewSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method createNewSurvey of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String message = "";
		try {
			surveyBuilder.createNewSurvey(user);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_CREATION_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method createNewSurvey of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while creating NewSurvey: " + e.getMessage(), e);
		}
		return message;
	}
	
	/**
	 * Method to add question to existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addquestiontosurvey", method = RequestMethod.POST)
	public String addQuestionToExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addQuestionToExistingSurvey of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String message = "";
		
		//TODO Get objects from UI
		Survey survey = new Survey();
		SurveyQuestionDetails questionDetails = new SurveyQuestionDetails();
		
		try {
			surveyBuilder.addQuestionToExistingSurvey(user, survey, questionDetails);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_MAPPING_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method addQuestionToExistingSurvey of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while adding Question to Survey: " + e.getMessage(), e);
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
	@RequestMapping(value = "/removequestionfromsurvey", method = RequestMethod.POST)
	public String removeQuestionFromExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method removequestionfromsurvey of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String message = "";
		
		try {
			long surveyQuestionId = Long.parseLong(request.getParameter("surveyQuestionId"));
			SurveyQuestion question = surveyBuilder.getSurveyQuestion(surveyQuestionId);
			
			surveyBuilder.deactivateQuestionSurveyMapping(user, question);
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_QUESTION_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method removequestionfromsurvey of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while disabling Question from Survey: " + e.getMessage(), e);
		}
		return message;
	}
	
	/**
	 * Method to deactivate survey for company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/deletesurveyforcompany", method = RequestMethod.POST)
	public String deactivateSurveyCompanyMapping(Model model, HttpServletRequest request) {
		LOG.info("Method deactivateSurveyCompanyMapping of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String message = "";
		
		//TODO Get objects from UI
		Survey survey = new Survey();
		
		try {
			surveyBuilder.deactivateSurveyCompanyMapping(user, survey, user.getCompany());
			message = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_COMPANY_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method deactivateSurveyCompanyMapping of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
		}
		catch (NoRecordsFetchedException e) {
			message = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("NoRecordsFetchedException while disabling Survey from company: " + e.getMessage(), e);
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
	@RequestMapping(value = "/getactivesurveydetails", method = RequestMethod.POST)
	public String getActiveSurveyDetails(Model model, HttpServletRequest request) {
		LOG.info("Method getSurveyDetails of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String jspToReturn = "";
		
		List<SurveyQuestionDetails> surveyQuestionDetails;
		try {
			surveyQuestionDetails = surveyBuilder.getAllActiveQuestionsOfMappedSurvey(user);
			model.addAttribute("activesurveyquestions", surveyQuestionDetails);

			// TODO return JSP
			jspToReturn = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_COMPANY_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method getSurveyDetails of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			jspToReturn = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
		}
		return jspToReturn;
	}
	
	/**
	 * Method to return particular survey for company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getsurveydetails", method = RequestMethod.POST)
	public String getSurveyDetails(Model model, HttpServletRequest request) {
		LOG.info("Method getSurveyDetails of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		String jspToReturn = "";
		
		try {
			long surveyId = Long.parseLong(request.getParameter("surveyId"));
			List<SurveyQuestionDetails> surveyQuestionDetails;
		
			Survey survey = surveyBuilder.getSurvey(surveyId);
			surveyQuestionDetails = surveyBuilder.getAllActiveQuestionsOfSurvey(user, survey);
			model.addAttribute("activesurveyquestions", surveyQuestionDetails);

			// TODO return JSP
			jspToReturn = messageUtils.getDisplayMessage(DisplayMessageConstants.SURVEY_COMPANY_DISABLE_SUCCESSFUL, DisplayMessageType.SUCCESS_MESSAGE).getMessage();
			LOG.info("Method getSurveyDetails of SurveyBuilderController finished successfully");
		}
		catch (InvalidInputException e) {
			jspToReturn = messageUtils.getDisplayMessage(e.getErrorCode(), DisplayMessageType.ERROR_MESSAGE).getMessage();
			LOG.error("InvalidInputException while disabling Survey from company: " + e.getMessage(), e);
		}
		return jspToReturn;
	}
}