package com.realtech.socialsurvey.core.starter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.impl.MongoSocialPostDaoImpl;
import com.realtech.socialsurvey.core.dummy.generator.SurveyGenerationService;
import com.realtech.socialsurvey.core.entities.SurveyQuestionDetails;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.generator.URLGenerator;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyBuilder;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;

public class DummySurveyGenerator {

	@Autowired
	private URLGenerator urlGenerator;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private SurveyHandler surveyHandler;

	public static final Logger LOG = LoggerFactory.getLogger(DummySurveyGenerator.class);
	private static int totalSize = 100;
	private static int happyUpperLimit = 3;
	private static int neutralUpperLimit = 6;
	
	private static String fileName = "/Users/nishit/work/Social_Survey/emailids.txt";

	public static void main(String[] args) {
		@SuppressWarnings("resource") ApplicationContext context = new ClassPathXmlApplicationContext("ss-starter-config.xml");
		SurveyHandler surveyHandler = (SurveyHandler) context.getBean("surveyHandler");
		SurveyBuilder surveyBuilder = (SurveyBuilder) context.getBean("surveyBuilder");
		SurveyGenerationService surveyGenerationService = (SurveyGenerationService) context.getBean("surveyGenerationService");

		// Email of customer.
		String custEmail = "customer";
		String custEmailDomain = "@mailinator.com";
		int count = 0;
		// Get agents
		List<String> emailIds = readEmailIdsFromFile(fileName);
		List<User> agents = surveyGenerationService.getAgents(emailIds);

		for (User user : agents) {
			for (count = 1; count <= totalSize; count++) {
				try {
					String mood = "happy";
					String rating = "5";
					if (count < neutralUpperLimit && count > happyUpperLimit) {
						rating = "3";
						mood = "neutral";
					}
					else if (count > neutralUpperLimit) {
						rating = "1";
						mood = "sad";
					}
					// Trigger survey (Store initial details)
					surveyHandler.storeInitialSurveyDetails(user.getUserId(), custEmail + count + custEmailDomain, custEmail + count, custEmail
							+ "last", 0, "transacted", "http://localhost:8080/" + CommonConstants.SHOW_SURVEY_PAGE, MongoSocialPostDaoImpl.KEY_SOURCE_SS);
					// Fetch survey for each agent
					List<SurveyQuestionDetails> surveyQuestionDetails = surveyBuilder.getSurveyByAgenId(user.getUserId());
					// Store answer to each question
					int qno = 1;
					for (SurveyQuestionDetails surveyQuestion : surveyQuestionDetails) {
						if (surveyQuestion.getIsRatingQuestion() == 1) {
							surveyQuestion.setCustomerResponse(rating);
						}
						else {
							surveyQuestion.setCustomerResponse("Random Answer");
						}
						surveyHandler.updateCustomerAnswersInSurvey(user.getUserId(), custEmail + count + custEmailDomain,
								surveyQuestion.getQuestion(), surveyQuestion.getQuestionType(), surveyQuestion.getCustomerResponse(), qno++);
					}
					// Store final answer and gateway answer.
					surveyHandler.updateGatewayQuestionResponseAndScore(user.getUserId(), custEmail + count + custEmailDomain, mood,
							"I am kind of happy but dont go by the rating. It may say anything. ", false, "false");
				}
				catch (SolrException | NoRecordsFetchedException | InvalidInputException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static List<String> readEmailIdsFromFile(String fileName){
		BufferedReader bfrReader = null;
		FileReader reader = null;
		List<String> emailIds = new ArrayList<>();
		try {
			reader = new FileReader(fileName);
			bfrReader = new BufferedReader(reader);
			String line = null;
			while ((line = bfrReader.readLine()) != null) {
				emailIds.add(line.trim());
			}
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (bfrReader != null) {
				try {
					bfrReader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				}
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return emailIds;
	}
}
