package com.realtech.socialsurvey.core.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
	public void updateGatewayAnswer(long agentId, String customerEmail, String mood, String review) {
		LOG.info("Method updateGatewayAnswer() to update review provided by customer started.");
		Query query = new Query();
		query.addCriteria(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId));
		query.addCriteria(Criteria.where(CommonConstants.CUSTOMER_EMAIL_COLUMN).is(customerEmail));
		Update update = new Update();
		update.set("stage", CommonConstants.SURVEY_STAGE_COMPLETE);
		update.set("mood", mood);
		update.set("review", review);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
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
		update.set(CommonConstants.SCORE_COLUMN, answer / noOfResponse);
		update.set(CommonConstants.MODIFIED_ON_COLUMN, new Date());
		mongoTemplate.updateMulti(query, update, SURVEY_DETAILS_COLLECTION);
		LOG.info("Method to calculate and update final score based upon rating questions finished.");
	}

	// ///////// Methods to get aggregated data from SURVEY_DETAILS collection starting.

	public long getSentSurveyCount() {
		LOG.info("Method to get count of total number of surveys sent so far, getTotalSurveyCount() started.");
		LOG.info("Method to get count of total number of surveys sent so far, getTotalSurveyCount() finished.");
		return mongoTemplate.count(null, SurveyDetails.class);
	}

	public long getCompletedSurveyCount() {
		LOG.info("Method to get count of total number of surveys taken so far, getTotalSurveyCount() started.");
		Query query = new Query(Criteria.where("stage").is(CommonConstants.SURVEY_STAGE_COMPLETE));
		LOG.info("Method to get count of total number of surveys taken so far, getTotalSurveyCount() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getIncompleteSurveyCount() {
		LOG.info("Method to get count of total number of surveys taken so far, getIncompleteSurveyCount() started.");
		LOG.info("Method to get count of total number of surveys taken so far, getIncompleteSurveyCount() finished.");
		return (getSentSurveyCount() - getCompletedSurveyCount());
	}

	public long getSentSurveyCountByAgent(long agentId) {
		LOG.info("Method to get count of total number of surveys sent so far by agent , getSentSurveyCountByAgent() started.");
		LOG.info("Method to get count of total number of surveys sent so far by agent, getSentSurveyCountByAgent() finished.");
		Query query = new Query(Criteria.where("agentId").is(agentId));
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getCompletedSurveyCountByAgent(long agentId) {
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByAgent() started.");
		Query query = new Query(Criteria.where("stage").is(CommonConstants.SURVEY_STAGE_COMPLETE));
		query.addCriteria(Criteria.where("agentId").is(agentId));
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByAgent() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getIncompleteSurveyCountByAgent(long agentId) {
		LOG.info("Method to get count of total number of incomplete surveys so far by agent, getIncompleteSurveyCountByAgent() started.");
		LOG.info("Method to get count of total number of incomplete surveys so far by agent, getIncompleteSurveyCountByAgent() finished.");
		return (getSentSurveyCountByAgent(agentId) - getCompletedSurveyCountByAgent(agentId));
	}

	public long getSentSurveyCountByBranch(long branchId) {
		LOG.info("Method to get count of total number of surveys sent so far by agent , getSentSurveyCountByBranch() started.");
		Query query = new Query(Criteria.where("branchId").is(branchId));
		LOG.info("Method to get count of total number of surveys sent so far by agent, getSentSurveyCountByBranch() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getCompletedSurveyCountByBranch(long branchId) {
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByBranch() started.");
		Query query = new Query(Criteria.where("stage").is(CommonConstants.SURVEY_STAGE_COMPLETE));
		query.addCriteria(Criteria.where("agentId").is(branchId));
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByBranch() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getIncompleteSurveyCountByBranch(long branchId) {
		LOG.info("Method to get count of total number of incomplete surveys so far in branch, getIncompleteSurveyCountByBranch() started.");
		LOG.info("Method to get count of total number of incomplete surveys so far in branch, getIncompleteSurveyCountByBranch() finished.");
		return (getSentSurveyCountByBranch(branchId) - getCompletedSurveyCountByBranch(branchId));
	}

	public long getSentSurveyCountByRegion(long regionId) {
		LOG.info("Method to get count of total number of surveys sent so far by agent , getCompletedSurveyCountByRegion() started.");
		Query query = new Query(Criteria.where("branchId").is(regionId));
		LOG.info("Method to get count of total number of surveys sent so far by agent, getCompletedSurveyCountByRegion() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getCompletedSurveyCountByRegion(long regionId) {
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByRegion() started.");
		Query query = new Query(Criteria.where("stage").is(CommonConstants.SURVEY_STAGE_COMPLETE));
		query.addCriteria(Criteria.where("agentId").is(regionId));
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByRegion() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getIncompleteSurveyCountByRegion(long regionId) {
		LOG.info("Method to get count of total number of incomplete surveys so far in region, getIncompleteSurveyCountByRegion() started.");
		LOG.info("Method to get count of total number of incomplete surveys so far in region, getIncompleteSurveyCountByRegion() finished.");
		return (getSentSurveyCountByBranch(regionId) - getCompletedSurveyCountByBranch(regionId));
	}

	public long getSentSurveyCountByCompany(long companyId) {
		LOG.info("Method to get count of total number of surveys sent so far by company, getSentSurveyCountByCompany() started.");
		Query query = new Query(Criteria.where("companyId").is(companyId));
		LOG.info("Method to get count of total number of surveys sent so far by company, getSentSurveyCountByCompany() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getCompletedSurveyCountByCompany(long companyId) {
		LOG.info("Method to get count of total number of surveys taken so far by company, getCompletedSurveyCountByRegion() started.");
		Query query = new Query(Criteria.where("stage").is(CommonConstants.SURVEY_STAGE_COMPLETE));
		query.addCriteria(Criteria.where("companyId").is(companyId));
		LOG.info("Method to get count of total number of surveys taken so far by agent, getCompletedSurveyCountByRegion() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	public long getIncompleteSurveyCountByCompany(long companyId) {
		LOG.info("Method to get count of total number of incomplete surveys so far in company, getIncompleteSurveyCountByCompany() started.");
		LOG.info("Method to get count of total number of incomplete surveys so far in company, getIncompleteSurveyCountByCompany() finished.");
		return (getSentSurveyCountByBranch(companyId) - getCompletedSurveyCountByBranch(companyId));
	}

	@Override
	public Map<String, Long> getCountOfCustomersByMood() {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMood() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.group("mood").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get("_id").toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMood() finished.");
		return moodSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByMoodForAgent(long agentId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForAgent() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId)), Aggregation.group("mood").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get("_id").toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForAgent() finished.");
		return moodSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByMoodForBranch(long branchId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForBranch() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.BRANCH_ID_COLUMN).is(branchId)), Aggregation.group("mood").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get("_id").toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForBranch() finished.");
		return moodSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByMoodForRegion(long regionId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForRegion() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.REGION_ID_COLUMN).is(regionId)), Aggregation.group("mood").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get("_id").toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForRegion() finished.");
		return moodSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByMoodForCompany(long companyId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForCompany() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId)), Aggregation.group("mood").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> moodSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> moodCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject o : moodCount) {
				moodSplit.put(o.get("_id").toString(), Long.parseLong(o.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByMoodForCompany() finished.");
		return moodSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMails() {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMails() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.group("reminderCount").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get("_id").toString(), Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMails() finished.");
		return reminderCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMailsForAgent(long agentId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForAgent() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId)), Aggregation.group("reminderCount").count()
						.as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get("_id").toString(), Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForAgent() finished.");
		return reminderCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMailsForBranch(long branchId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForBranch() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("branchId").is(branchId)), Aggregation.group("reminderCount").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get("_id").toString(), Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForBranch() finished.");
		return reminderCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMailsForRegion(long regionId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForRegion() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.REGION_ID_COLUMN).is(regionId)), Aggregation.group("reminderCount").count()
						.as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get("_id").toString(), Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForRegion() finished.");
		return reminderCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByReminderMailsForCompany(long companyId) {
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForCompany() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId)), Aggregation.group("reminderCount").count()
						.as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> reminderCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> reminderCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject reminder : reminderCount) {
				reminderCountSplit.put(reminder.get("_id").toString(), Long.parseLong(reminder.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to their mood, getCountOfCustomersByReminderMailsForCompany() finished.");
		return reminderCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByStage() {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStage() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("stage").ne(-1)), //
				Aggregation.group("stage").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get("_id").toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStage() finished.");
		return stageCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByStageForAgent(long agentId) {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForAgent() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("stage").ne(-1)), //
				Aggregation.match(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId)), Aggregation.group("stage").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get("_id").toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForAgent() finished.");
		return stageCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByStageForBranch(long branchId) {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForBranch() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("stage").ne(-1)), //
				Aggregation.match(Criteria.where(CommonConstants.BRANCH_ID_COLUMN).is(branchId)), Aggregation.group("stage").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get("_id").toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForBranch() finished.");
		return stageCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByStageForRegion(long regionId) {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForRegion() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("stage").ne(-1)), //
				Aggregation.match(Criteria.where(CommonConstants.REGION_ID_COLUMN).is(regionId)), Aggregation.group("stage").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get("_id").toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForRegion() finished.");
		return stageCountSplit;
	}

	@Override
	public Map<String, Long> getCountOfCustomersByStageForCompany(long companyId) {
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForCompany() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("stage").ne(-1)), //
				Aggregation.match(Criteria.where(CommonConstants.COMPANY_ID_COLUMN).is(companyId)), Aggregation.group("stage").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> stageCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> stageCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject stage : stageCount) {
				stageCountSplit.put(stage.get("_id").toString(), Long.parseLong(stage.get("count").toString()));
			}
		}
		LOG.info("Method to get customers according to stage of survey, getCountOfCustomersByStageForCompany() finished.");
		return stageCountSplit;
	}

	@Override
	public long getTotalSurveyCountByMonth(int year, int month) {
		LOG.info("Method to get count of total number of surveys taken so far, getTotalSurveyCount() started.");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Date startDate = calendar.getTime();
		// Returns max value for date in the month set in Calendar instance.
		calendar.set(year, month, calendar.getActualMaximum(5));
		Date endDate = calendar.getTime();
		Query query = new Query(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate));
		query.addCriteria(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate));
		LOG.info("Method to get count of total number of surveys taken so far, getTotalSurveyCount() finished.");
		return mongoTemplate.count(query, SurveyDetails.class);
	}

	@Override
	public double getRatingForPastNdays(String columnName, long columnValue, int noOfDays) {
		LOG.info("Method getRatingOfAgentForPastNdays(), to calculate rating of agent started.");
		Calendar calendar = Calendar.getInstance();
		Date endDate = calendar.getTime();
		calendar.add(5, noOfDays * (-1));
		Date startDate = calendar.getTime();
		if (noOfDays == -1) {
			calendar.setTimeInMillis(0);
			startDate = calendar.getTime();
		}
		Query query = new Query(Criteria
				.where(columnName)
				.is(columnValue)
				.andOperator(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate),
						Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)));
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(
				SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)), Aggregation.match(Criteria.where(
						CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)), Aggregation.match(Criteria.where(columnName).is(columnValue)),
				Aggregation.group(columnName).sum(CommonConstants.SCORE_COLUMN).as("total_score") //
		);

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		long a = mongoTemplate.count(query, SurveyDetails.class);
		double rating = 0;
		if (result != null && a > 0) {
			rating = ((long) result.getRawResults().get("total_score")) / a;
		}
		LOG.info("Method getRatingOfAgentForPastNdays(), to calculate rating of agent finished.");
		return rating;
	}

	// January is denoted with 0.
	public double getRatingOfAgentByMonth(long agentId, int year, int month) {
		LOG.info("Method getRatingOfAgentByMonth(), to calculate rating of agent started.");
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Date startDate = calendar.getTime();
		// Returns max value for date in the month set in Calendar instance.
		calendar.set(year, month, calendar.getActualMaximum(5));
		Date endDate = calendar.getTime();
		Query query = new Query(Criteria
				.where(CommonConstants.AGENT_ID_COLUMN)
				.is(agentId)
				.andOperator(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate),
						Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)));
		long count = mongoTemplate.count(query, SurveyDetails.class);
		if (count < 3) {
			LOG.info("Agent " + agentId + " does not qualify for calculation of rating. Returning...");
			return -1;
		}
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.MODIFIED_ON_COLUMN).lte(endDate)), Aggregation.match(Criteria.where(
						CommonConstants.MODIFIED_ON_COLUMN).gte(startDate)), Aggregation.match(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(
						agentId)), Aggregation.group(CommonConstants.AGENT_ID_COLUMN).sum(CommonConstants.SCORE_COLUMN).as("total_score") //
		);

		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		double rating = 0;
		if (result != null) {
			rating = ((long) result.getRawResults().get("total_score")) / count;
		}
		LOG.info("Method getRatingOfAgentByMonth(), to calculate rating of agent finished.");
		return rating;
	}

	@Override
	public Map<String, Long> getSocialPostsCount() {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCount() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.unwind(CommonConstants.SHARED_ON_COLUMN), //
				Aggregation.group(CommonConstants.SHARED_ON_COLUMN).count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> postCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> postsCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : postsCount) {
				postCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of social posts by customers, getSocialPostsCount() finished.");
		return postCountSplit;
	}

	@Override
	public Map<String, Long> getSocialPostsCountForAgent(long agentId) {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForAgent() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.AGENT_ID_COLUMN).is(agentId)), Aggregation.unwind(CommonConstants.SHARED_ON_COLUMN), //
				Aggregation.group(CommonConstants.SHARED_ON_COLUMN).count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> postCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> postsCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : postsCount) {
				postCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForAgent() finished.");
		return postCountSplit;
	}

	@Override
	public Map<String, Long> getSocialPostsCountForBranch(long branchId) {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForBranch() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.BRANCH_ID_COLUMN).is(branchId)),
				Aggregation.unwind(CommonConstants.SHARED_ON_COLUMN), //
				Aggregation.group(CommonConstants.SHARED_ON_COLUMN).count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> postCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> postsCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : postsCount) {
				postCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForBranch() finished.");
		return postCountSplit;
	}

	@Override
	public Map<String, Long> getSocialPostsCountForRegion(long regionId) {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForRegion() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where(CommonConstants.REGION_ID_COLUMN).is(regionId)),
				Aggregation.unwind(CommonConstants.SHARED_ON_COLUMN), //
				Aggregation.group("sharedOn").count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> postCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> postsCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : postsCount) {
				postCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForRegion() finished.");
		return postCountSplit;
	}

	@Override
	public Map<String, Long> getSocialPostsCountForCompany(long companyId) {
		LOG.info("Method to count number of social posts by customers, getSocialPostsCountForCompany() started.");
		TypedAggregation<SurveyDetails> aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
				Aggregation.match(Criteria.where("companyId").is(companyId)), Aggregation.unwind(CommonConstants.SHARED_ON_COLUMN), //
				Aggregation.group(CommonConstants.SHARED_ON_COLUMN).count().as("count") //
		);
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> postCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> postsCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : postsCount) {
				postCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of social posts by customers, getSoctiatorialPostsCountForCompany() finished.");
		return postCountSplit;
	}

	// Method to get get count of surveys initiated by customers and agents separately.
	// Columns can only be from : {agentId/branchId/regionId}

	@Override
	public Map<String, Long> getCountOfSurveyInitiators(String columnName, long columnValue) {
		LOG.info("Method to count number of surveys initiators, getCountOfSurveyInitiators() started.");
		TypedAggregation<SurveyDetails> aggregation;
		if (columnName == null) {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
					Aggregation.group("initiator").count().as("count") //
			);
		}
		else {
			aggregation = new TypedAggregation<SurveyDetails>(SurveyDetails.class, //
					Aggregation.match(Criteria.where(columnName).is(columnValue)), Aggregation.group("initiator").count().as("count") //
			);
		}
		AggregationResults<SurveyDetails> result = mongoTemplate.aggregate(aggregation, SURVEY_DETAILS_COLLECTION, SurveyDetails.class);
		Map<String, Long> initiatorCountSplit = new HashMap<>();
		if (result != null) {
			@SuppressWarnings("unchecked") List<BasicDBObject> initiatorCount = (List<BasicDBObject>) result.getRawResults().get("result");
			for (BasicDBObject post : initiatorCount) {
				initiatorCountSplit.put(post.get("_id").toString(), Long.parseLong(post.get("count").toString()));
			}
		}
		LOG.info("Method to count number of surveys initiators, getCountOfSurveyInitiators() finished.");
		return initiatorCountSplit;
	}

	/*
	 * Returns a list of feedbacks provided by customers. First sorted on score then on date (both
	 * descending). ColumnName can be "agentId/branchId/regionId/companyId". ColumnValue should be
	 * value for respective column.
	 */

	@Override
	public List<SurveyDetails> getAllFeedbacks(String columnName, long columNValue) {
		LOG.info("Method to fetch all the feedbacks from SURVEY_DETAILS collection, getAllFeedbacks() started.");
		Query query = new Query();
		if (columnName != null) {
			query.addCriteria(Criteria.where(columnName).is(columNValue));
		}
		query.with(new Sort(Sort.Direction.DESC, CommonConstants.SCORE_COLUMN));
		query.with(new Sort(Sort.Direction.DESC, CommonConstants.MODIFIED_ON_COLUMN));
		List<SurveyDetails> surveysWithReviews = mongoTemplate.find(query, SurveyDetails.class, SURVEY_DETAILS_COLLECTION);

		LOG.info("Method to fetch all the feedbacks from SURVEY_DETAILS collection, getAllFeedbacks() finished.");
		return surveysWithReviews;
	}
}