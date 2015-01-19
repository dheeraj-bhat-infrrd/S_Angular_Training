package com.realtech.socialsurvey.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.utils.MessageUtils;
import com.realtech.socialsurvey.web.common.JspResolver;

@Controller
public class SurveyBuilderController {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyBuilderController.class);

	@Autowired
	private MessageUtils messageUtils;

	@Autowired
	private SurveyBuilder surveyBuilder;

	/**
	 * Method to show the build survey page
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/showbuildsurveypage", method = RequestMethod.GET)
	public String showBuildSurveyPage(Model model, HttpServletRequest request) {
		LOG.info("Method showBuildSurveyPage called");
		return JspResolver.SURVEY_BUILDER;
	}
	
	/**
	 * Method to create new survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/createsurvey", method = RequestMethod.POST)
	public String createNewSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method createNewSurvey of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		
		// surveyBuilder.createNewSurvey(user, surveyQuestions);
		LOG.info("Method createNewSurvey of SurveyBuilderController finished successfully");
		return null;
	}

	/**
	 * Method to map survey to company
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addsurveytocompany", method = RequestMethod.POST)
	public String addSurveyToCompany(Model model, HttpServletRequest request) {
		LOG.info("Method addSurveyToCompany of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		
		String surveyId = request.getParameter("surveyId");
		// surveyBuilder.addSurveyToCompany(survey, user.getCompany(), user);
		
		LOG.info("Method addSurveyToCompany of SurveyBuilderController finished successfully");
		return null;
	}

	/**
	 * Method to add questions to existing survey
	 * 
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addquestionstosurvey", method = RequestMethod.POST)
	public String addQuestionsToExistingSurvey(Model model, HttpServletRequest request) {
		LOG.info("Method addQuestionsToExistingSurvey of SurveyBuilderController called");
		User user = (User) request.getSession(false).getAttribute(CommonConstants.USER_IN_SESSION);
		
		LOG.info("Method addQuestionsToExistingSurvey of SurveyBuilderController finished successfully");
		return null;
	}
}