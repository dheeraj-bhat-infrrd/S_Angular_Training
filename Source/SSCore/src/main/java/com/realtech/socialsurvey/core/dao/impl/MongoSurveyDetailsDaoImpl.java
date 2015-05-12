package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyDetailsDao;
import com.realtech.socialsurvey.core.entities.SurveyDetails;
import com.realtech.socialsurvey.core.entities.SurveyResponse;

/*
 * Provides list of operations to be performed on SurveyDetails collection of mongo. SurveyDetails
 * collection contains list of surveys taken by customers. It also contains answers provided by
 * customers for questions specific to an agent.
 */
@Repository
public class MongoSurveyDetailsDaoImpl implements SurveyDetailsDao {

	private static final Logger LOG = LoggerFactory.getLogger(MongoSurveyDetailsDaoImpl.class);

	public static final String SURVEY_DETAILS_COLLECTION = "SURVEY_DETAILS";

	@Autowired
	private MongoTemplate mongoTemplate;

	/*
	 * Method to fetch survey details on the basis of agentId and customer email.
	 */
	@Override
	public SurveyDetails getSurveyByAgentIdAndCustomerEmail(long agentId, String customerEmail) {
		LOG.info("Method getSurveyByAgentIdAndCustomerEmail() to insert details of survey started.");
		Query query = new Query(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		List<SurveyDetails> surveys = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);
		if (surveys == null || surveys.size() == 0)
			return null;
		LOG.info("Method insertSurveyDetails() to insert details of survey finished.");
		return surveys.get(CommonConstants.INITIAL_INDEX);
	}

	/*
	 * Method to insert survey details into the SURVEY_DETAILS collection.
	 */
	@Override
	public void insertSurveyDetails(SurveyDetails surveyDetails) {
		LOG.info("Method insertSurveyDetails() to insert details of survey started.");
		mongoTemplate.insert(surveyDetails, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method insertSurveyDetails() to insert details of survey finished.");
	}

	/*
	 * Method to update email id by appending timestamp in documents from SurveyDetails collection
	 * by agent id and customer's email-id.
	 */
	@Override
	public void updateEmailForExistingFeedback(long agentId, String customerEmail) {
		LOG.info("Method updateEmailForExistingFeedback() to insert details of survey started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set(CommonConstants.CUSTOMER_EMAIL_COLUMN, customerEmail + "#" + new Timestamp(System.currentTimeMillis()));
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method updateEmailForExistingFeedback() to insert details of survey finished.");
	}

	/*
	 * Method to update questions for survey in SURVEY_DETAILS collection.
	 */
	@Override
	public void updateCustomerResponse(long agentId, String customerEmail, SurveyResponse surveyResponse, int stage) {
		LOG.info("Method updateCustomerResponse() to update response provided by customer started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set("stage", stage);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		update.pull("surveyResponse", new BasicDBObject("question", surveyResponse.getQuestion()));
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		mongoTemplate.updateMulti(query, new Update().push("surveyResponse", surveyResponse), SURVEY_DETAILS_COLLECTION);
		LOG.info("Method updateCustomerResponse() to update response provided by customer finished.");
	}

	/*
	 * Method to update answer and response for gateway question of survey in SURVEY_DETAILS
	 * collection.
	 */
	@Override
	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review, boolean isAbusive) {
		LOG.info("Method updateGatewayAnswer() to update review provided by customer started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set(CommonConstants.STAGE_COLUMN, CommonConstants.SURVEY_STAGE_COMPLETE);
		update.set(CommonConstants.MOOD_COLUMN, mood);
		update.set("review", review);
		update.set(CommonConstants.IS_ABUSIVE_COLUMN, isAbusive);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		update.set(CommonConstants.EDITABLE_SURVEY_COLUMN, false);
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method updateGatewayAnswer() to update review provided by customer finished.");
	}

	/*
	 * Method to calculate and update final score based upon rating questions.
	 */
	@Override
	public void updateFinalScore(long agentId, String customerEmail) {
		LOG.info("Method to calculate and update final score based upon rating questions started.");
		Query query = new Query();
		List<String> ratingType = new ArrayList<>();
		ratingType.add("sb-range-smiles");
		ratingType.add("sb-range-scale");
		ratingType.add("sb-range-star");
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		query.addCriteria(Criteria.where("surveyResponse.questionType").in(ratingType));
		List<SurveyResponse> surveyResponse = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION)
				.get(CommonConstants.INITIAL_INDEX).getSurveyResponse();
		double noOfResponse = 0;
		double answer = 0;
		for (SurveyResponse response : surveyResponse) {
			if (response.getQuestionType().equals(ratingType.get(CommonConstants.INITIAL_INDEX))
					|| response.getQuestionType().equals(ratingType.get(1)) || response.getQuestionType().equals(ratingType.get(2))) {
				if (response.getAnswer() != null && !response.getAnswer().isEmpty()) {
					answer += Integer.parseInt(response.getAnswer());
					noOfResponse++;
				}
			}
		}
		Update update = new Update();
		update.set(CommonConstants.SCORE_COLUMN, Math.round(answer / noOfResponse * 1000.0) / 1000.0);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to calculate and update final score based upon rating questions finished.");
	}

	@Override
	public void updateSurveyAsClicked(long agentId, String customerEmail) {
		LOG.info("Method updateSurveyAsClicked() to mark survey as clicked started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set(CommonConstants.SURVEY_CLICKED_COLUMN, true);
		update.set(CommonConstants.CREATED_ON, new Date());
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method updateSurveyAsClicked() to mark survey as clicked finished.");
	}

	// JIRA SS-137 and 158 BY RM-05 : BOC

	// -----Methods to get aggregated data from SURVEY_DETAILS collection starting-----

	// This method returns all the surveys that have been sent to or started by customers so far.
	// If columnName field is passed null value it returns count of all the survey.
	// columnName field can contain either of "agentId/branchId/regionId/companyId".
	// columnValue field can contain respective values for the columnName.

	@Override
	public long getSentSurveyCount(String columnName, long columnValue, int noOfDays) {
		LOG.info("Method to get count of total number of surveys sent so far, getSentSurveyCount() started.");
		Date startDate = getNdaysBackDate(noOfDays);

		Query query = new Query();
		if (columnName != null) {
			query = new Query(Criteria.where(columnName).is(columnValue));
		}
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate));
		LOG.info("Method to get count of total number of surveys sent so far, getSentSurveyCount() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	// This method returns all the surveys that have been clicked by customers so far.
	// If columnName field is passed null value it returns count of all the survey.
	// "columnName" field can contain either of "agentId/branchId/regionId/companyId".
	// "columnValue" field can contain respective values for the columnName.

	@Override
	public long getClickedSurveyCount(String columnName, long columnValue, int noOfDays) {
		LOG.info("Method to get count of total number of surveys clicked so far, getClickedSurveyCount() started.");
		Date endDate = Calendar.getInstance().getTime();
		Date startDate = getNdaysBackDate(noOfDays);
		Query query = new Query(Criteria.where(CommonConstants.SURVEY_CLICKED_COLUMN).is(true));
		/*query.addCriteria(Criteria.where("surveyResponse").size(0));*/
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate).lte(endDate));
		LOG.info("Method to get count of total number of surveys clicked so far, getClickedSurveyCount() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	// This method returns all the surveys that have been completed by customers so far.
	// If columnName field is passed null value it returns count of all the survey.
	// "columnName" field can contain either of "agentId/branchId/regionId/companyId".
	// "columnValue" field can contain respective values for the columnName.

	@Override
	public long getCompletedSurveyCount(String columnName, long columnValue, int noOfDays) {
		LOG.info("Method to get count of total number of surveys completed so far, getCompletedSurveyCount() started.");
		Date endDate = Calendar.getInstance().getTime();
		Date startDate = getNdaysBackDate(noOfDays);
		Query query = new Query(Criteria.where(CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE));
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate).lte(endDate));
		LOG.info("Method to get count of total number of surveys completed so far, getCompletedSurveyCount() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	// This method returns all the surveys that are not yet completed by customers.
	// If columnName field is passed null value it returns count of all the survey.
	// "columnName" field can contain either of "agentId/branchId/regionId/companyId".
	// "columnValue" field can contain respective values for the columnName.

	@Override
	public long getIncompleteSurveyCount(String columnName, long columnValue, int noOfDays) {
		LOG.info("Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() started.");
		Date endDate = Calendar.getInstance().getTime();
		Date startDate = getNdaysBackDate(noOfDays);
		Query query = new Query(Criteria.where(CommonConstants.STAGE_COLUMN).ne(CommonConstants.SURVEY_STAGE_COMPLETE));
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate).lte(endDate));
		LOG.info("Method to get count of surveys which are not yet completed, getIncompleteSurveyCount() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	// This method returns a map of customers count based upon their mood.
	// Map contains Mood --> Customers count mapping for each mood for a given
	// agent/branch/region/company.

	@Override
	public Map<String, Long> getCountOfCustomersByMood(String columnName, long columnValue) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMood() started.");
		TypedAggregation<SurveyDetails> aggregation;
		if (columnName == null) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.group(CommonConstants.MOOD_COLUMN).count().as("count"));
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(columnName).is(columnValue)),
					Aggregation.group(CommonConstants.MOOD_COLUMN).count().as("count"));
		}

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMood() finished.");
		return moodSplit;
	}

	// This method returns the customers' count based upon the number of reminder mails sent to
	// them.

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMails(String columnName, long columnValue) {
		LOG.info("Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() started.");
		TypedAggregation<SurveyDetails> aggregation;
		if (columnName == null) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.group(CommonConstants.REMINDER_COUNT_COLUMN).count()
					.as("count"));
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(columnName).is(columnValue)),
					Aggregation.group(CommonConstants.REMINDER_COUNT_COLUMN).count().as("count"));
		}

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString(),
						Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to the number of reminder emails sent, getCountOfCustomersByReminderMails() finished.");
		return reminderCountSplit;
	}

	// This method returns the customers' count based upon their current stage i.e. number of
	// questions they have answered so far.

	@Override
	public Map<String, Long> getCountOfCustomersByStage(String columnName, long columnValue) {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStage() started.");
		TypedAggregation<SurveyDetails> aggregation;
		if (columnName == null) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.group(CommonConstants.STAGE_COLUMN).count()
					.as("count"));
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(columnName).is(columnValue)),
					Aggregation.group(CommonConstants.STAGE_COLUMN).count().as("count"));
		}
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStage() finished.");
		return stageCountSplit;
	}

	// Method to return number of surveys taken in a given month of a particular year.

	@Override
	public long getTotalSurveyCountByMonth(int year, int month) {
		LOG.info("Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() started.");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Date startDate = calendar.getTime();
		// Returns max value for date in the month set in Calendar instance.
		calendar.set(year, month, calendar.getActualMaximum(5));
		Date endDate = calendar.getTime();
		Query query = new Query(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate));
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate));
		LOG.info("Method to get count of total number of surveys taken in a given month and year, getTotalSurveyCountByMonth() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	@Override
	@SuppressWarnings("unchecked")
	public double getRatingForPastNdays(String columnName, long columnValue, int noOfDays, boolean aggregateAbusive) {
		LOG.info("Method getRatingOfAgentForPastNdays(), to calculate rating of agent started for columnName: " + columnName + " columnValue:"
				+ columnValue + " noOfDays:" + noOfDays + " aggregateAbusive:" + aggregateAbusive);
		Date startDate = null;
		/**
		 * if days is not set, take the start date as 1 jan 1970
		 */
		if (noOfDays == -1) {
			startDate = new Date(0l);
		}
		else {
			startDate = getNdaysBackDate(noOfDays);
		}

		Date endDate = Calendar.getInstance().getTime();

		Query query = new Query();

		/**
		 * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
		 * are to be fetched else fetch all the records
		 */
		if (!aggregateAbusive) {
			query.addCriteria(Criteria.where(CommonConstants.IS_ABUSIVE_COLUMN).is(aggregateAbusive));
		}

		query.addCriteria(Criteria
				.where(columnName)
				.is(columnValue)
				.andOperator(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate),
						Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate),
						Criteria.where(CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE)));

		TypedAggregation<SurveyDetails> aggregation = null;
		if (!aggregateAbusive) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(
					CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)), Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(
					startDate)), Aggregation.match(Criteria.where(columnName).is(columnValue)), Aggregation.match(Criteria.where(
					CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE)), Aggregation.match(Criteria.where(
					CommonConstants.IS_ABUSIVE_COLUMN).is(aggregateAbusive)), Aggregation.group(columnName).sum(CommonConstants.SCORE_COLUMN)
					.as("total_score"));
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(
					CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)), Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(
					startDate)), Aggregation.match(Criteria.where(columnName).is(columnValue)), Aggregation.match(Criteria.where(
					CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE)), Aggregation.group(columnName)
					.sum(CommonConstants.SCORE_COLUMN).as("total_score"));
		}

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		long reviewsCount = mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
		LOG.debug("Count of aggregated results :" + reviewsCount);
		double rating = 0;
		if (result != null && reviewsCount > 0) {
			List<DBObject> basicDBObject = (List<DBObject>) result.getRawResults().get("result");
			rating = (double) basicDBObject.get(0).get("total_score") / reviewsCount;
		}
		LOG.info("Method getRatingOfAgentForPastNdays(), to calculate rating of agent finished.");
		return rating;
	}

	// January is denoted with 0.
	public double getRatingByMonth(String columnName, long columnValue, int year, int month) {
		LOG.info("Method getRatingOfAgentByMonth(), to calculate rating of agent started.");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Date startDate = calendar.getTime();
		// Returns max value for date in the month set in Calendar instance.
		calendar.set(year, month, calendar.getActualMaximum(5));
		Date endDate = calendar.getTime();
		Query query = new Query(Criteria
				.where(columnName)
				.is(columnValue)
				.andOperator(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate),
						Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)));
		long count = mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
		if (count < 3) {
			LOG.info(columnName + " " + columnValue + " does not qualify for calculation of rating. Returning...");
			return -1;
		}
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(
				CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)), Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN)
				.gte(startDate)), Aggregation.match(Criteria.where(columnName).is(columnValue)), Aggregation.group(columnName)
				.sum(CommonConstants.SCORE_COLUMN).as("total_score"));

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		double rating = 0;
		if (result != null) {
			rating = ((long) result.getRawResults().get("total_score")) / count;
		}
		LOG.info("Method getRatingOfAgentByMonth(), to calculate rating of agent finished.");
		return rating;
	}

	// Method to get count of posts shared by customers on various social networking sites for
	// "agent/branch/region/company/all".
	// Returns posts count on that site.

	@Override
	public long getSocialPostsCount(String columnName, long columnValue, int numberOfDays) {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCount() started.");
		Date endDate = Calendar.getInstance().getTime();
		Date startDate = getNdaysBackDate(numberOfDays);
		Query query = new Query(Criteria.where("sharedOn").exists(true));
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate).lte(endDate));
		LOG.info("Method to count number of social posts by customers, getSocialPostsCount() finished.");
		return mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
	}

	// Method to get count of surveys initiated by customers and agents separately.
	// Columns can only be from : {agentId/branchId/regionId}

	@Override
	public Map<String, Long> getCountOfSurveyInitiators(String columnName, long columnValue) {
		LOG.info("Method to count number of surveys initiators, getCountOfSurveyInitiators() started.");
		TypedAggregation<SurveyDetails> aggregation;
		if (columnName == null) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.group(CommonConstants.INITIATED_BY_COLUMN).count()
					.as("count"));
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(columnName).is(columnValue)),
					Aggregation.group(CommonConstants.INITIATED_BY_COLUMN).count().as("count"));
		}
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> initiatorCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> initiatorCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : initiatorCount) {
				initiatorCountSplit.put(post.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of surveys initiators, getCountOfSurveyInitiators() finished.");
		return initiatorCountSplit;
	}

	/*
	 * Returns a list of feedbacks provided by customers. First sorted on score then on date (both
	 * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
	 * value for respective column. limitScore is the max score under which reviews have to be shown
	 */
	@Override
	public List<SurveyDetails> getFeedbacks(String columnName, long columnValue, int start, int rows, double startScore, double limitScore,
			boolean fetchAbusive, Date startDate, Date endDate) {
		LOG.info("Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() started.");

		Query query = new Query();
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}

		/**
		 * fetching only completed surveys
		 */
		query.addCriteria(Criteria.where(CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE));

		if (startDate != null && endDate != null) {
			query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)
					.andOperator(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)));
		}
		else if (startDate != null) {
			query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate));
		}
		else if (endDate != null) {
			query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate));
		}

		/**
		 * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
		 * are to be fetched else fetch all the records
		 */
		if (!fetchAbusive) {
			query.addCriteria(Criteria.where(CommonConstants.IS_ABUSIVE_COLUMN).is(fetchAbusive));
		}

		if (startScore > -1 && limitScore > -1) {
			query.addCriteria(new Criteria().andOperator(Criteria.where(CommonConstants.SCORE_COLUMN).gte(startScore),
					Criteria.where(CommonConstants.SCORE_COLUMN).lte(limitScore)));
		}

		if (start > -1) {
			query.skip(start);
		}
		if (rows > -1) {
			query.limit(rows);
		}

		query.with(new Sort(Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN));
		query.with(new Sort(Sort.Direction.DESC, CommonConstants.SCORE_COLUMN));
		List<SurveyDetails> surveysWithReviews = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);

		LOG.info("Method to fetch all the feedbacks from SURVEY_DETAILS collection, getFeedbacks() finished.");
		return surveysWithReviews;
	}

	@Override
	public long getFeedBacksCount(String columnName, long columnValue, double startScore, double limitScore, boolean fetchAbusive) {
		LOG.info("Method getFeedBacksCount started for columnName:" + columnName + " columnValue:" + columnValue + " startScore:" + startScore
				+ " limitScore:" + limitScore + " and fetchAbusive:" + fetchAbusive);
		Query query = new Query();
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		/**
		 * fetching only completed surveys
		 */
		query.addCriteria(Criteria.where(CommonConstants.STAGE_COLUMN).is(CommonConstants.SURVEY_STAGE_COMPLETE));

		/**
		 * adding isabusive criteria only if fetch abusive flag is false, i.e only non abusive posts
		 * are to be fetched else fetch all the records
		 */
		if (!fetchAbusive) {
			query.addCriteria(Criteria.where(CommonConstants.IS_ABUSIVE_COLUMN).is(fetchAbusive));
		}

		/**
		 * adding limit for score if specified
		 */
		if (startScore > -1 && limitScore > -1) {
			query.addCriteria(new Criteria().andOperator(Criteria.where(CommonConstants.SCORE_COLUMN).gte(startScore),
					Criteria.where(CommonConstants.SCORE_COLUMN).lte(limitScore)));
		}

		long feedBackCount = mongoTemplate.count(query, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method getFeedBacksCount executed successfully.Returning feedBackCount:" + feedBackCount);
		return feedBackCount;
	}

	/*
	 * Returns a list of survey which are not yet competed by customers.Sorted on date (
	 * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
	 * value for respective column. limitScore is the max score under which reviews have to be shown
	 */

	@Override
	public List<SurveyDetails> getIncompleteSurvey(String columnName, long columnValue, int start, int rows, double startScore, double limitScore,
			Date startDate, Date endDate) {
		LOG.info("Method to fetch all the incomplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() started.");
		Query query = new Query();
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columnValue));
		}
		if (startScore > 0 && limitScore > 0) {
			query.addCriteria(new Criteria().andOperator(Criteria.where(CommonConstants.SCORE_COLUMN).gte(startScore),
					Criteria.where(CommonConstants.SCORE_COLUMN).lte(limitScore)));
		}
		query.addCriteria(Criteria.where(CommonConstants.STAGE_COLUMN).ne(CommonConstants.SURVEY_STAGE_COMPLETE));
		if (start > -1) {
			query.skip(start);
		}
		if (rows > -1) {
			query.limit(rows);
		}
		if(startDate!=null){
			query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate));
		}
		if(endDate!=null){
			query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate));
		}
		query.with(new Sort(Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN));
		List<SurveyDetails> surveysWithReviews = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);

		LOG.info("Method to fetch all the incoplete survey from SURVEY_DETAILS collection, getIncompleteSurvey() finished.");
		return surveysWithReviews;
	}

	/*
	 * Method to increase reminder count by 1.
	 */
	@Override
	public void updateReminderCount(long agentId, String customerEmail) {
		LOG.info("Method to increase reminder count by 1, updateReminderCount() started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.inc(CommonConstants.REMINDER_COUNT_COLUMN, 1);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to increase reminder count by 1, updateReminderCount() finished.");
	}

	/*
	 * Method to get count of clicked surveys based upon criteria(Weekly/Monthly/Yearly)
	 */
	@Override
	public Map<String, Long> getClickedSurveyByCriteria(String columnName, long columnValue, String groupByCriteria) throws ParseException {
		LOG.info("Method to get");
		TypedAggregation<SurveyDetails> aggregation;
//		Date endDate = new Date();
		int numberOfPastDaysToConsider = 7;
		String criteriaColumn = "";
		switch (groupByCriteria) {
			case "weekly":
				numberOfPastDaysToConsider = 7;
				criteriaColumn = "dayOfMonth";
				break;
			case "monthly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				numberOfPastDaysToConsider = 30 + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				criteriaColumn = "week";
				break;
			case "yearly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				numberOfPastDaysToConsider = 365 + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				criteriaColumn = "month";
				break;
		}
		Date startDate = getNdaysBackDate(numberOfPastDaysToConsider);
		aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(CommonConstants.SURVEY_CLICKED_COLUMN).is(true)), Aggregation.match(Criteria.where(
				columnName).is(columnValue)), Aggregation.match(Criteria.where(
				CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)), Aggregation.project(CommonConstants.MODIFIED_ON_COLUMN)
				.andExpression(criteriaColumn + "(modifiedOn)").as("groupCol"), Aggregation.group("groupCol").count().as("count"));

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> clickedSurveys = new LinkedHashMap<>();
		if (result != null) {

			if (criteriaColumn.equals("dayOfMonth")) {
				Date currDate = new Date();
				int reductionInDate = 0;
				for (int i = 0; i < 7; i++) {
					currDate = getNdaysBackDate(reductionInDate++);
					clickedSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
				}
			}
			else if (criteriaColumn.equals("week")) {
				Date currDate = new Date();
				int reductionInDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
				for (int i = 0; i < 4; i++) {
					currDate = getNdaysBackDate(reductionInDate);
					clickedSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
					reductionInDate += 7;
				}
			}
			else if (criteriaColumn.equals("month")) {
				int currMonth = Calendar.getInstance().get(Calendar.MONTH);
				for (int i = 0; i < 12; i++) {
					clickedSurveys.put(getMonthAsString((++currMonth) % 12).toString(), 0l);
				}
			}
			Calendar calendar = Calendar.getInstance();
			@SuppressWarnings("unchecked") List<BasicDBObject> clicked = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject clickedSurvey : clicked) {
				if (criteriaColumn == "dayOfMonth") {
					for (String date : clickedSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(clickedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							clickedSurveys.put(date, Long.parseLong(clickedSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "week") {
					for (String date : clickedSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(clickedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()) + 1)
							clickedSurveys.put(date, Long.parseLong(clickedSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "month")
					for (String date : clickedSurveys.keySet()) {
						String dateFormat = "MMM";
						calendar.setTime(new SimpleDateFormat(dateFormat).parse(date));
						if (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(clickedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							clickedSurveys.put(date, Long.parseLong(clickedSurvey.get("count").toString()));
					}
			}
		}
		return clickedSurveys;
	}

	/*
	 * Method to get count of sent surveys based upon criteria(Weekly/Monthly/Yearly).
	 */
	@Override
	public Map<String, Long> getSentSurveyByCriteria(String columnName, long columnValue, String groupByCriteria) throws ParseException {
		LOG.info("Method to get count of sent surveys based upon criteria(Weekly/Monthly/Yearly) getSentSurveyByCriteria() started.");
		TypedAggregation<SurveyDetails> aggregation;
//		Date endDate = new Date();
		int numberOfPastDaysToConsider = 7;
		String criteriaColumn = "";
		switch (groupByCriteria) {
			case "weekly":
				numberOfPastDaysToConsider = 7;
				criteriaColumn = "dayOfMonth";
				break;
			case "monthly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				numberOfPastDaysToConsider = 30 + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				criteriaColumn = "week";
				break;
			case "yearly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				numberOfPastDaysToConsider = 365 + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				criteriaColumn = "month";
				break;
		}
		Date startDate = getNdaysBackDate(numberOfPastDaysToConsider);
		aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(CommonConstants.CREATED_ON).gte(startDate)), Aggregation.match(Criteria.where(columnName)
				.is(columnValue)), Aggregation.project(CommonConstants.CREATED_ON)
				.andExpression(criteriaColumn + "(" + CommonConstants.CREATED_ON + ")").as("groupCol"), Aggregation.group("groupCol").count()
				.as("count"));

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> sentSurveys = new LinkedHashMap<>();
		if (result != null) {

			if (criteriaColumn.equals("dayOfMonth")) {
				Date currDate = new Date();
				int reductionInDate = 0;
				for (int i = 0; i < 7; i++) {
					currDate = getNdaysBackDate(reductionInDate++);
					sentSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
				}
			}
			else if (criteriaColumn.equals("week")) {
				Date currDate = new Date();
				int reductionInDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
				for (int i = 0; i < 4; i++) {
					sentSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
					reductionInDate += 7;
					currDate = getNdaysBackDate(reductionInDate);
				}
			}
			else if (criteriaColumn.equals("month")) {
				int currMonth = Calendar.getInstance().get(Calendar.MONTH);
				for (int i = 0; i < 12; i++) {
					sentSurveys.put(getMonthAsString((++currMonth) % 12).toString(), 0l);
				}
			}
			Calendar calendar = Calendar.getInstance();
			@SuppressWarnings("unchecked") List<BasicDBObject> sent = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject sentSurvey : sent) {
				if (criteriaColumn == "dayOfMonth") {
					for (String date : sentSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							sentSurveys.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "week") {
					for (String date : sentSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()) + 1)
							sentSurveys.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "month")
					for (String date : sentSurveys.keySet()) {
						String dateFormat = "MMM";
						calendar.setTime(new SimpleDateFormat(dateFormat).parse(date));
						if (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString()))
							sentSurveys.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
			}
		}
		LOG.info("Method to get count of sent surveys based upon criteria(Weekly/Monthly/Yearly) getSentSurveyByCriteria() finished.");
		return sentSurveys;
	}

	/*
	 * Method to get count of completed surveys based upon criteria(Weekly/Monthly/Yearly).
	 */
	@Override
	public Map<String, Long> getCompletedSurveyByCriteria(String columnName, long columnValue, String groupByCriteria) throws ParseException {
		LOG.info("Method to get count of completed surveys based upon criteria(Weekly/Monthly/Yearly) getCompletedSurveyByCriteria() started.");
		TypedAggregation<SurveyDetails> aggregation;
//		Date endDate = new Date();
		int numberOfPastDaysToConsider = 7;
		String criteriaColumn = "";
		switch (groupByCriteria) {
			case "weekly":
				numberOfPastDaysToConsider = 7;
				criteriaColumn = "dayOfMonth";
				break;
			case "monthly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				numberOfPastDaysToConsider = 30 + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				criteriaColumn = "week";
				break;
			case "yearly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				numberOfPastDaysToConsider = 365 + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				criteriaColumn = "month";
				break;
		}
		Date startDate = getNdaysBackDate(numberOfPastDaysToConsider);
		aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(CommonConstants.STAGE_COLUMN).is(
				CommonConstants.SURVEY_STAGE_COMPLETE)), Aggregation.match(Criteria.where(columnName).is(columnValue)), Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(
				startDate)), Aggregation.project(CommonConstants.MODIFIED_ON_COLUMN)
				.andExpression(criteriaColumn + "(" + CommonConstants.MODIFIED_ON_COLUMN + ")").as("groupCol"), Aggregation.group("groupCol").count()
				.as("count"));

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> completedSurveys = new LinkedHashMap<>();
		if (result != null) {
			if (criteriaColumn.equals("dayOfMonth")) {
				Date currDate = new Date();
				int reductionInDate = 0;
				for (int i = 0; i < 7; i++) {
					currDate = getNdaysBackDate(reductionInDate++);
					completedSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
				}
			}
			else if (criteriaColumn.equals("week")) {
				Date currDate = new Date();
				int reductionInDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
				for (int i = 0; i < 4; i++) {
					completedSurveys.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
					reductionInDate += 7;
					currDate = getNdaysBackDate(reductionInDate);
				}
			}
			else if (criteriaColumn.equals("month")) {
				int currMonth = Calendar.getInstance().get(Calendar.MONTH);
				for (int i = 0; i < 12; i++) {
					completedSurveys.put(getMonthAsString((++currMonth) % 12).toString(), 0l);
				}
			}
			Calendar calendar = Calendar.getInstance();
			@SuppressWarnings("unchecked") List<BasicDBObject> completed = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject completedSurvey : completed) {
				if (criteriaColumn == "dayOfMonth")
					for (String date : completedSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(completedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							completedSurveys.put(date, Long.parseLong(completedSurvey.get("count").toString()));
					}
				if (criteriaColumn == "week") {
					for (String date : completedSurveys.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(completedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()) + 1)
							completedSurveys.put(date, Long.parseLong(completedSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "month")
					for (String date : completedSurveys.keySet()) {
						String dateFormat = "MMM";
						calendar.setTime(new SimpleDateFormat(dateFormat).parse(date));
						if (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(completedSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							completedSurveys.put(date, Long.parseLong(completedSurvey.get("count").toString()));
					}
			}
		}
		LOG.info("Method to get count of completed surveys based upon criteria(Weekly/Monthly/Yearly) getCompletedSurveyByCriteria() finished.");
		return completedSurveys;
	}

	/*
	 * Method to get count of social posts based upon criteria(Weekly/Monthly/Yearly).
	 */
	@Override
	public Map<String, Long> getSocialPostsCountByCriteria(String columnName, long columnValue, String groupByCriteria) throws ParseException {
		LOG.info("Method to get count of social posts based upon criteria(Weekly/Monthly/Yearly), getSocialPostsCountByCriteria() started.");
		TypedAggregation<SurveyDetails> aggregation;
//		Date endDate = new Date();
		int numberOfPastDaysToConsider = 7;
		String criteriaColumn = "";
		switch (groupByCriteria) {
			case "weekly":
//				endDate = getNdaysBackDate(7);
				numberOfPastDaysToConsider = 7;
				criteriaColumn = "dayOfMonth";
				break;
			case "monthly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
				numberOfPastDaysToConsider = 30 + Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
				criteriaColumn = "week";
				break;
			case "yearly":
//				endDate = getNdaysBackDate(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
				numberOfPastDaysToConsider = 365 + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				criteriaColumn = "month";
				break;
		}
		Date startDate = getNdaysBackDate(numberOfPastDaysToConsider);
		aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, Aggregation.match(Criteria.where(CommonConstants.SHARED_ON_COLUMN)
				.exists(true)), Aggregation.match(Criteria.where(columnName).is(columnValue)),
				Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)), Aggregation
						.project(CommonConstants.MODIFIED_ON_COLUMN).andExpression(criteriaColumn + "(" + CommonConstants.MODIFIED_ON_COLUMN + ")")
						.as("groupCol"), Aggregation.group("groupCol").count().as("count"));

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> socialPosts = new LinkedHashMap<>();
		if (result != null) {
			if (criteriaColumn.equals("dayOfMonth")) {
				Date currDate = new Date();
				int reductionInDate = 0;
				for (int i = 0; i < 7; i++) {
					currDate = getNdaysBackDate(reductionInDate++);
					socialPosts.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
				}
			}
			else if (criteriaColumn.equals("week")) {
				Date currDate = new Date();
				int reductionInDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
				for (int i = 0; i < 4; i++) {
					socialPosts.put(new SimpleDateFormat(CommonConstants.DATE_FORMAT).format(currDate).toString(), 0l);
					reductionInDate += 7;
					currDate = getNdaysBackDate(reductionInDate);
				}
			}
			else if (criteriaColumn.equals("month")) {
				int currMonth = Calendar.getInstance().get(Calendar.MONTH);
				for (int i = 0; i < 12; i++) {
					socialPosts.put(getMonthAsString((++currMonth) % 12).toString(), 0l);
				}
			}
			Calendar calendar = Calendar.getInstance();
			@SuppressWarnings("unchecked") List<BasicDBObject> sent = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject sentSurvey : sent) {
				if (criteriaColumn == "dayOfMonth")
					for (String date : socialPosts.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()))
							socialPosts.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
				if (criteriaColumn == "week") {
					for (String date : socialPosts.keySet()) {
						calendar.setTime(new SimpleDateFormat(CommonConstants.DATE_FORMAT).parse(date));
						if (calendar.get(Calendar.WEEK_OF_YEAR) == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN)
								.toString()) + 1)
							socialPosts.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
				}
				if (criteriaColumn == "month")
					for (String date : socialPosts.keySet()) {
						String dateFormat = "MMM";
						calendar.setTime(new SimpleDateFormat(dateFormat).parse(date));
						if (calendar.get(Calendar.MONTH) + 1 == Integer.parseInt(sentSurvey.get(CommonConstants.DEFAULT_MONGO_ID_COLUMN).toString()))
							socialPosts.put(date, Long.parseLong(sentSurvey.get("count").toString()));
					}
			}
		}
		LOG.info("Method to get count of social posts based upon criteria(Weekly/Monthly/Yearly), getSocialPostsCountByCriteria() finished.");
		return socialPosts;
	}

	@Override
	public List<SurveyDetails> getIncompleteSurveyCustomers(long companyId, int surveyReminderInterval, int maxReminders) {
		LOG.info("Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() started.");
		Date cutOffDate = getNdaysBackDate(surveyReminderInterval);
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId),
				Criteria.where(CommonConstants.LAST_REMINDER_FOR_INCOMPLETE_SURVEY).lte(cutOffDate),
				Criteria.where(CommonConstants.REMINDER_COUNT_COLUMN).lt(maxReminders)));
		List<SurveyDetails> surveys = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to get list of customers who have not yet completed their survey, getIncompleteSurveyCustomers() finished.");
		return surveys;
	}

	private Date getNdaysBackDate(int noOfDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, noOfDays * (-1));
		Date startDate = calendar.getTime();
		return startDate;
	}

	private String getMonthAsString(int monthInt) {
		String month = "Invalid Month";
		DateFormatSymbols dateFormatSymbols = new DateFormatSymbols();
		month = dateFormatSymbols.getMonths()[monthInt];
		return month.substring(0, 3);
	}

	@Override
	public void updateReminderCount(List<Long> agents, List<String> customers) {
		LOG.info("Method to increase reminder count by 1, updateReminderCount() started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).in(agents));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).in(customers));
		Update update = new Update();
		update.inc(CommonConstants.REMINDER_COUNT_COLUMN, 1);
		Date date = new Date();
		update.set(CommonConstants.MODIFIED_ON_COLUMN, date);
		update.set(CommonConstants.LAST_REMINDER_FOR_INCOMPLETE_SURVEY, date);
		update.push(CommonConstants.REMINDERS_FOR_INCOMPLETE_SURVEYS, date);
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to increase reminder count by 1, updateReminderCount() finished.");
	}

	@Override
	public List<SurveyDetails> getIncompleteSocialPostCustomersEmail(long companyId, int surveyReminderInterval, int maxReminders, float autopostScore) {
		LOG.info("Method to get list of customers who have not yet shared their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() started.");
		Date cutOffDate = getNdaysBackDate(surveyReminderInterval);
		Query query = new Query();
		query.addCriteria(new Criteria().andOperator(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId),
				Criteria.where(CommonConstants.LAST_REMINDER_FOR_SOCIAL_POST).lte(cutOffDate),
				Criteria.where(CommonConstants.SCORE_COLUMN).gte(autopostScore), Criteria.where("socialPostsReminder").lt(maxReminders)));
		List<SurveyDetails> surveys = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to get list of customers who have not yet completed their survey on all the social networking sites, getIncompleteSocialPostCustomersEmail() finished.");
		return surveys;
	}

	@Override
	public void updateSharedOn(List<String> socialSites, long agentId, String customerEmail) {
		LOG.info("updateSharedOn() started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.pushAll(CommonConstants.SHARED_ON_COLUMN, socialSites.toArray());
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("updateSharedOn() finished.");
	}

	@Override
	public void changeStatusOfSurvey(long agentId, String customerEmail, boolean editable) {
		LOG.info("Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set(CommonConstants.EDITABLE_SURVEY_COLUMN, editable);
		update.set(CommonConstants.STAGE_COLUMN, 0);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to update status of survey in SurveyDetails collection, changeStatusOfSurvey() finished.");
	}

	// JIRA SS-137 and 158 : EOC
}