package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyPreInitiationDao;
import com.realtech.socialsurvey.core.entities.AgentRankingReport;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.integration.EngagementProcessingStatus;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("surveypreinitiation")
public class SurveyPreInitiationDaoImpl extends GenericDaoImpl<SurveyPreInitiation, Long> implements SurveyPreInitiationDao {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyPreInitiationDaoImpl.class);

	@Override
	public Timestamp getLastRunTime(String source) throws InvalidInputException {
		LOG.info("Get the max created time for source " + source);
		if (source == null || source.isEmpty()) {
			LOG.debug("Source is not provided.");
			throw new InvalidInputException("Souce is not provided.");
		}
		Timestamp lastRunTime = null;
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source));
			criteria.setProjection(Projections.max(CommonConstants.CREATED_ON));
			Object result = criteria.uniqueResult();
			if (result instanceof Timestamp) {
				lastRunTime = (Timestamp) result;
			}
		}
		catch (HibernateException ex) {
			LOG.error("Exception caught in getLastRunTime() ", ex);
			throw new DatabaseException("Exception caught in getLastRunTime() ", ex);
		}
		return lastRunTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EngagementProcessingStatus> getProcessedIds(String source, Timestamp timestamp) throws InvalidInputException {
		if (source == null || source.isEmpty()) {
			LOG.warn("Source is not present.");
			throw new InvalidInputException("Source is not present.");
		}
		LOG.info("Getting processed ids for source " + source + " after timestamp " + (timestamp != null ? String.valueOf(timestamp) : ""));
		List<EngagementProcessingStatus> processedRecords = null;
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.SURVEY_SOURCE_KEY_COLUMN, source));
			if (timestamp != null) {
				criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, timestamp));
			}
			criteria.setProjection(Projections.property(CommonConstants.SURVEY_SOURCE_ID_COLUMN));
			criteria.setProjection(Projections.property(CommonConstants.STATUS_COLUMN));
			processedRecords = (List<EngagementProcessingStatus>) criteria.list();
		}
		catch (HibernateException ex) {
			LOG.error("Exception caught in getProcessedIds() ", ex);
			throw new DatabaseException("Exception caught in getProcessedIds() ", ex);
		}
		return processedRecords;
	}

	// Method to get list of incomplete surveys to display in Dash board and profile page.
	@SuppressWarnings("unchecked")
	@Override
	public List<SurveyPreInitiation> getIncompleteSurvey(Timestamp startDate, Timestamp endDate, int start, int row, Set<Long> agentIds,
			boolean isCompanyAdmin, long companyId, boolean realtechAdmin) throws DatabaseException {
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try {
			if (startDate != null)
				criteria.add(Restrictions.ge(CommonConstants.MODIFIED_ON_COLUMN, startDate));
			if (endDate != null)
				criteria.add(Restrictions.le(CommonConstants.MODIFIED_ON_COLUMN, endDate));
			if (row > 0)
				criteria.setMaxResults(row);
			if (start > 0)
				criteria.setFirstResult(start);
			
			if (!realtechAdmin) {
				if (!isCompanyAdmin && agentIds.size() > 0)
					criteria.add(Restrictions.in(
							CommonConstants.AGENT_ID_COLUMN, agentIds));
				else {
					criteria.add(Restrictions.eq(
							CommonConstants.COMPANY_ID_COLUMN, companyId));
				}
			}
			// JIRA: SS-1357: Begin
			criteria.add(Restrictions.in(CommonConstants.STATUS_COLUMN, new Integer[]{CommonConstants.STATUS_SURVEYPREINITIATION_PROCESSED, CommonConstants.SURVEY_STATUS_INITIATED}));
			// JIRA: SS-1357: End
			criteria.addOrder(Order.desc(CommonConstants.MODIFIED_ON_COLUMN));
			return criteria.list();
		}
		catch (HibernateException e) {
			LOG.error("Exception caught in getIncompleteSurvey() ", e);
			throw new DatabaseException("Exception caught in getIncompleteSurvey() ", e);
		}
	}

	// Method to get incomplete survey list for sending reminder mail.
	@SuppressWarnings("unchecked")
	@Override
	public List<SurveyPreInitiation> getIncompleteSurveyForReminder(long companyId, int surveyReminderInterval, int maxReminders) {
		LOG.info("Method getIncompleteSurveyForReminder() started.");
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try {
			criteria.add(Restrictions.eq(CommonConstants.COMPANY_ID_COLUMN, companyId));
			criteria.add(Restrictions.le("lastReminderTime", new Timestamp(new Date().getTime() - surveyReminderInterval * 24 * 60 * 60 * 1000)));
			if(maxReminders > 0)
				criteria.add(Restrictions.lt("reminderCounts", maxReminders));
			LOG.info("Method getIncompleteSurveyForReminder() finished.");
			return criteria.list();
		}
		catch (HibernateException e) {
			LOG.error("Exception caught in getIncompleteSurveyForReminder() ", e);
			throw new DatabaseException("Exception caught in getIncompleteSurveyForReminder() ", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getIncompleteSurveysCount(Date startDate, Date endDate, Map<Long, AgentRankingReport> agentReportData) {
		LOG.info("Method getIncompleteSurveysCount() started");
		List<SurveyPreInitiation> surveys = new ArrayList<>();
		
		Criteria criteria = getSession().createCriteria(SurveyPreInitiation.class);
		try {
			if (startDate != null && endDate != null) {
				criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, new Timestamp(startDate.getTime())));
				criteria.add(Restrictions.le(CommonConstants.CREATED_ON, new Timestamp(endDate.getTime())));
			}
			else if (startDate != null && endDate == null)
				criteria.add(Restrictions.ge(CommonConstants.CREATED_ON, new Timestamp(startDate.getTime())));
			else if (startDate == null && endDate != null)
				criteria.add(Restrictions.le(CommonConstants.CREATED_ON, new Timestamp(endDate.getTime())));
			
			surveys = criteria.list();
		}
		catch (HibernateException e) {
			LOG.error("Exception caught in getIncomplgetIncompleteSurveysCounteteSurveyForReminder() ", e);
			throw new DatabaseException("Exception caught in getIncompleteSurveysCount() ", e);
		}
		
		for (SurveyPreInitiation survey : surveys) {
			if (survey.getStatus() != CommonConstants.SURVEY_STATUS_PRE_INITIATED) {
				continue;
			}
			
			AgentRankingReport agentRankingReport = null;
			if (agentReportData.containsKey(survey.getAgentId())) {
				agentRankingReport = agentReportData.get(survey.getAgentId());
			}
			else {
				agentRankingReport = new AgentRankingReport();
				agentRankingReport.setAgentId(survey.getAgentId());
				agentRankingReport.setAgentName(survey.getAgentName());
			}
			
			agentRankingReport.setIncompleteSurveys(agentRankingReport.getIncompleteSurveys() + 1);
			agentReportData.put(survey.getAgentId(), agentRankingReport);
		}
		LOG.info("Method getIncompleteSurveysCount() finished");
	}
	
	@Override
	public void deleteSurveysWithIds(Set<Long> incompleteSurveyIds) {
		LOG.info("Method deleteSurveysWithIds() started");
		//First get the list of surveys that will be deleted
		String deleteQuery = "delete from SurveyPreInitiation where surveyPreIntitiationId in (:incompleteSurveyIds)";
		Query query = getSession().createQuery(deleteQuery);
		query.setParameterList("incompleteSurveyIds", incompleteSurveyIds);
		query.executeUpdate();
	}
	
	   /**
     * Method to fetch preinitiated surveys by IDs
     * 
     * @param incompleteSurveyIds
     * @return
     */
	@SuppressWarnings ( "unchecked")
    @Override
	public List<SurveyPreInitiation> fetchSurveysByIds(Set<Long> incompleteSurveyIds) {
	    LOG.info( "Method fetchSurveysByIds() started" );
	    Criteria criteria = getSession().createCriteria( SurveyPreInitiation.class );
	    criteria.add( Restrictions.in( CommonConstants.SURVEY_PREINITIATION_ID_COLUMN, incompleteSurveyIds ) );
	    return criteria.list();
	}
	
	@SuppressWarnings ( "unchecked")
    @Override
	public long getIncompleteSurveyCount(long companyId, long agentId, int status, Timestamp startDate, Timestamp endDate, Set<Long> agentIds){
		LOG.info("getting incomplete survey count");
		StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(*) AS COUNT FROM SURVEY_PRE_INITIATION WHERE ");
		boolean whereFlag = false; // used if where is
		if(companyId > 0l){
			queryBuilder.append(" COMPANY_ID = :companyId");
			whereFlag = true;
		}
		// Change the incomplete count for status 1 and 2 - Important! Needs to be modified later to accomodate status list in input
		if(whereFlag){
			queryBuilder.append(" AND STATUS IN (1,2)");
		}else{
			queryBuilder.append(" STATUS IN (1,2)");
			whereFlag = true;
		}
		if(startDate != null){
			if(whereFlag){
				queryBuilder.append(" AND CREATED_ON >= :startDate");
			}else{
				queryBuilder.append(" CREATED_ON >= :startDate");
				whereFlag = true;
			}
		}
		if(endDate != null){
			if(whereFlag){
				queryBuilder.append(" AND CREATED_ON <= :endDate");
			}else{
				queryBuilder.append(" CREATED_ON <= :endDate");
				whereFlag = true;
			}
		}
		if(agentId > 0l){
			if(whereFlag){
				queryBuilder.append(" AND AGENT_ID = :agentId");
			}else{
				queryBuilder.append(" AGENT_ID = :agentId");
				whereFlag = true;
			}
		}else if(agentIds != null && agentIds.size() > 0){
			if(whereFlag){
				queryBuilder.append(" AND AGENT_ID IN (:agentIds)");
			}else{
				queryBuilder.append(" AGENT_ID IN (:agentIds)");
				whereFlag = true;
			}
		}
		Query query = null;
		query = getSession().createSQLQuery(queryBuilder.toString());
		if(companyId > 0l){
			query.setParameter("companyId", companyId);
		}
		//query.setParameter("status", status);
		if(startDate != null){
			query.setParameter("startDate", startDate);
		}
		if(endDate != null){
			query.setParameter("endDate", endDate);
		}
		if(agentId > 0l){
			query.setParameter("agentId", agentId);
		}else if(agentIds != null && agentIds.size() > 0){
			query.setParameterList("agentIds", agentIds);
		}
		List<BigInteger> results = query.list();
		long count = 0l;
		if(results != null && results.size() > 0){
			count= results.get(0).longValue();
		}
		return count;
	}
	
	@Override
	public Map<Integer, Integer> getIncompletSurveyAggregationCount(long companyId, long agentId, int status, Timestamp startDate, Timestamp endDate, Set<Long> agentIds, String aggregateBy) throws InvalidInputException{
		LOG.info("Getting incomplete survey aggregated count for company id : "+companyId+" \t status: "+status+"\t startDate "+startDate+"\t end date: "+endDate+"\t aggregatedBy: "+aggregateBy);
		Map<Integer, Integer> aggregateResult = null;
		StringBuilder queryBuilder = new StringBuilder();
		if(aggregateBy == null || aggregateBy.isEmpty()){
			LOG.error("Aggregate by is null");
			throw new InvalidInputException("Aggregate by is null");
		}
		boolean whereFlag = false; // used if where is 
		if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_WEEK)){
			queryBuilder.append("SELECT YEARWEEK(CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE ");
		}else if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_DAY)){
			queryBuilder.append("SELECT DATE(CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE ");
		}else if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_MONTH)){
			queryBuilder.append("SELECT EXTRACT(YEAR_MONTH FROM CREATED_ON) AS SENT_DATE, COUNT(SURVEY_PRE_INITIATION_ID) AS NUM_OF_SURVEYS FROM SURVEY_PRE_INITIATION WHERE ");
		}
		if(companyId > 0l){
			queryBuilder.append(" COMPANY_ID = :companyId");
			whereFlag = true;
		}
		// Change the incomplete count for status 1 and 2 - Important! Needs to be modified later to accomodate status list in input
		if(whereFlag){
			queryBuilder.append(" AND STATUS IN (1,2)");
		}else{
			queryBuilder.append(" STATUS IN (1,2)");
			whereFlag = true;
		}
		if(startDate != null){
			if(whereFlag){
				queryBuilder.append(" AND CREATED_ON >= :startDate");
			}else{
				queryBuilder.append(" CREATED_ON >= :startDate");
				whereFlag = true;
			}
		}
		if(endDate != null){
			if(whereFlag){
				queryBuilder.append(" AND CREATED_ON <= :endDate");
			}else{
				queryBuilder.append(" CREATED_ON <= :endDate");
				whereFlag = true;
			}
		}
		if(agentId > 0l){
			if(whereFlag){
				queryBuilder.append(" AND AGENT_ID = :agentId");
			}else{
				queryBuilder.append(" AGENT_ID = :agentId");
				whereFlag = true;
			}
		}else if(agentIds != null && agentIds.size() > 0){
			if(whereFlag){
				queryBuilder.append(" AND AGENT_ID IN (:agentIds)");
			}else{
				queryBuilder.append(" AGENT_ID IN (:agentIds)");
				whereFlag = true;
			}
		}
		if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_WEEK)){
			queryBuilder.append(" GROUP BY YEARWEEK(CREATED_ON) ORDER BY SENT_DATE");
		}else if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_DAY)){
			queryBuilder.append(" GROUP BY DATE(CREATED_ON) ORDER BY SENT_DATE");
		}else if(aggregateBy.equals(CommonConstants.AGGREGATE_BY_MONTH)){
			queryBuilder.append(" GROUP BY EXTRACT(YEAR_MONTH FROM CREATED_ON) ORDER BY SENT_DATE");
		}
		Query query = null;
		query = getSession().createSQLQuery(queryBuilder.toString());
		if(companyId > 0l){
			query.setParameter("companyId", companyId);
		}
		//query.setParameter("status", status);
		if(startDate != null){
			query.setParameter("startDate", startDate);
		}
		if(endDate != null){
			query.setParameter("endDate", endDate);
		}
		if(agentId > 0l){
			query.setParameter("agentId", agentId);
		}else if(agentIds != null && agentIds.size() > 0){
			query.setParameterList("agentIds", agentIds);
		}
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.list();
		if(results != null && results.size() > 0){
			aggregateResult = new HashMap<Integer, Integer>();
			for(Object[] result : results){
				aggregateResult.put((Integer)result[0], ((BigInteger)result[1]).intValue());
			}
		}
		return aggregateResult;
	}
}
