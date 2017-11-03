/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.SurveyResultsReportBranchDao;
import com.realtech.socialsurvey.core.entities.SurveyResponseTable;
import com.realtech.socialsurvey.core.entities.SurveyResultsReportBranch;
import com.realtech.socialsurvey.core.exception.DatabaseException;

/**
 * @author Subhrajit
 *
 */
@Repository
public class SurveyResultsReportBranchDaoImpl extends GenericReportingDaoImpl<SurveyResultsReportBranch, String>
		implements SurveyResultsReportBranchDao {

	private static final Logger LOG = LoggerFactory.getLogger(SurveyResultsReportBranchDaoImpl.class);

	/*
	 * The limit is applied to the surveyResults table and a left outer join to
	 * survey response so the surveys like zillow who dont have a response are
	 * not missed
	 */
	private static final String GET_SURVEY_RESULT_ALL_TIME_BY_BRANCH_ID_QUERY = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,ab.SURVEY_SENT_DATE,"
			+ "ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,ab.CUSTOMER_COMMENTS,"
			+ "ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,ab.CLICK_THROUGH_FOR_BRANCH "
			+ "from (select srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"
			+ "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"
			+ "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "
			+ "from survey_results_report_branch srcr where srcr.BRANCH_ID = ? and srcr.IS_DELETED = 0 limit ?,?) as ab "
			+ "left outer join survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID "
			+ "order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID ";

	private static final String GET_SURVEY_RESULTS_BY_START_DATE_QUERY = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"
			+ "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"
			+ "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"
			+ "ab.CLICK_THROUGH_FOR_BRANCH from (select "
			+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"
			+ "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"
			+ "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "
			+ "from survey_results_report_branch srcr where srcr.BRANCH_ID = ? and srcr.SURVEY_COMPLETED_DATE >= ? and srcr.IS_DELETED = 0 limit ?,?) as ab left outer join "
			+ "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";

	private static final String GET_SURVEY_RESULTS_BY_END_DATE_QUERY = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"
			+ "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"
			+ "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"
			+ "ab.CLICK_THROUGH_FOR_BRANCH from (select "
			+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"
			+ "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"
			+ "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "
			+ "from survey_results_report_branch srcr where srcr.BRANCH_ID = ? and srcr.SURVEY_COMPLETED_DATE <= ? and srcr.IS_DELETED = 0 limit ?,?) as ab left outer join "
			+ "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";

	private static final String GET_SURVEY_RESULTS_BY_START_AND_END_DATE_QUERY = "select ab.SURVEY_DETAILS_ID,sr.answer,ab.USER_FIRST_NAME,ab.USER_LAST_NAME,ab.CUSTOMER_FIRST_NAME,ab.CUSTOMER_LAST_NAME,"
			+ "ab.SURVEY_SENT_DATE,ab.SURVEY_COMPLETED_DATE,ab.TIME_INTERVAL,ab.SURVEY_SOURCE,ab.SURVEY_SOURCE_ID,ab.SURVEY_SCORE,ab.GATEWAY,"
			+ "ab.CUSTOMER_COMMENTS,ab.AGREED_TO_SHARE,ab.BRANCH_NAME,ab.CLICK_THROUGH_FOR_COMPANY,ab.CLICK_THROUGH_FOR_AGENT,ab.CLICK_THROUGH_FOR_REGION,"
			+ "ab.CLICK_THROUGH_FOR_BRANCH from (select "
			+ "srcr.SURVEY_DETAILS_ID,srcr.USER_FIRST_NAME,srcr.USER_LAST_NAME,srcr.CUSTOMER_FIRST_NAME,srcr.CUSTOMER_LAST_NAME,srcr.SURVEY_SENT_DATE,"
			+ "srcr.SURVEY_COMPLETED_DATE,srcr.TIME_INTERVAL,srcr.SURVEY_SOURCE,srcr.SURVEY_SOURCE_ID,srcr.SURVEY_SCORE,srcr.GATEWAY,srcr.CUSTOMER_COMMENTS,"
			+ "srcr.AGREED_TO_SHARE,srcr.BRANCH_NAME,srcr.CLICK_THROUGH_FOR_COMPANY,srcr.CLICK_THROUGH_FOR_AGENT,srcr.CLICK_THROUGH_FOR_REGION,srcr.CLICK_THROUGH_FOR_BRANCH "
			+ "from survey_results_report_branch srcr where srcr.BRANCH_ID = ? and srcr.SURVEY_COMPLETED_DATE >= ? and srcr.SURVEY_COMPLETED_DATE <= ? and srcr.IS_DELETED = 0 limit ?,?) as ab left outer join "
			+ "survey_response sr on ab.SURVEY_DETAILS_ID = sr.SURVEY_DETAILS_ID order by sr.SURVEY_DETAILS_ID, sr.QUESTION_ID";

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(value = "transactionManagerForReporting")
	public Map<String, SurveyResultsReportBranch> getSurveyResultForBranchId(long branchId, Timestamp startDate,
			Timestamp endDate, int startIndex, int batchSize) {
		LOG.debug("Method getSurveyResultForCompanyId started for CompanyId : {}", branchId);
		Query query = null;
		try {
			if (startDate != null && endDate != null) {
				query = getSession().createSQLQuery(GET_SURVEY_RESULTS_BY_START_AND_END_DATE_QUERY);
				query.setParameter(1, startDate);
				query.setParameter(2, endDate);
				query.setParameter(3, startIndex);
				query.setParameter(4, batchSize);
			} else if (startDate != null && endDate == null) {
				query = getSession().createSQLQuery(GET_SURVEY_RESULTS_BY_START_DATE_QUERY);
				query.setParameter(1, startDate);
				query.setParameter(2, startIndex);
				query.setParameter(3, batchSize);
			} else if (startDate == null && endDate != null) {
				query = getSession().createSQLQuery(GET_SURVEY_RESULTS_BY_END_DATE_QUERY);
				query.setParameter(1, endDate);
				query.setParameter(2, startIndex);
				query.setParameter(3, batchSize);
			} else if (startDate == null && endDate == null) {
				query = getSession().createSQLQuery(GET_SURVEY_RESULT_ALL_TIME_BY_BRANCH_ID_QUERY);
				query.setParameter(1, startIndex);
				query.setParameter(2, batchSize);
			}

			query.setParameter(0, branchId);
			LOG.debug("QUERY : " + query.getQueryString());
			List<Object[]> rows = (List<Object[]>) query.list();
			Map<String, SurveyResultsReportBranch> surveyResultMap = new HashMap<String, SurveyResultsReportBranch>();

			// map the answer to the survey details id
			for (Object[] row : rows) {
				SurveyResponseTable surveyResponseTable = new SurveyResponseTable();
				surveyResponseTable.setAnswer(String.valueOf(row[1]));
				List<SurveyResponseTable> surveyResponseList = new ArrayList<>();
				SurveyResultsReportBranch surveyResultsReportBranch = null;
				String surveyDetailsId = String.valueOf(row[0]);

				if (surveyResultMap.get(surveyDetailsId) != null) {
					surveyResultsReportBranch = surveyResultMap.get(surveyDetailsId);
					surveyResponseList = surveyResultsReportBranch.getSurveyResponseList();
					surveyResponseList.add(surveyResponseTable);
					surveyResultsReportBranch.setSurveyResponseList(surveyResponseList);

				} else {
					surveyResultsReportBranch = new SurveyResultsReportBranch();
					surveyResponseList.add(surveyResponseTable);
					surveyResultsReportBranch.setSurveyResponseList(surveyResponseList);
					surveyResultsReportBranch.setSurveyDetailsId(String.valueOf(row[0]));
					surveyResultsReportBranch.setUserFirstName(String.valueOf(row[2]));
					surveyResultsReportBranch.setUserLastName(String.valueOf(row[3]));
					surveyResultsReportBranch.setCustomerFirstName(String.valueOf(row[4]));
					surveyResultsReportBranch.setCustomerLastName(String.valueOf(row[5]));
					surveyResultsReportBranch.setSurveySentDate((Timestamp) (row[6]));
					surveyResultsReportBranch.setSurveyCompletedDate((Timestamp) (row[7]));
					surveyResultsReportBranch.setTimeInterval((Integer) (row[8]));
					surveyResultsReportBranch.setSurveySource(String.valueOf(row[9]));
					surveyResultsReportBranch.setSurveySourceId(String.valueOf(row[10]));
					surveyResultsReportBranch.setSurveyScore(((BigDecimal) (row[11])).doubleValue());
					surveyResultsReportBranch.setGateway(String.valueOf(row[12]));
					surveyResultsReportBranch.setCustomerComments(String.valueOf(row[13]));
					surveyResultsReportBranch.setAgreedToShare(String.valueOf(row[14]));
					surveyResultsReportBranch.setBranchName(String.valueOf(row[15]));
					surveyResultsReportBranch.setClickTroughForCompany(String.valueOf(row[16]));
					surveyResultsReportBranch.setClickTroughForAgent(String.valueOf(row[17]));
					surveyResultsReportBranch.setClickTroughForRegion(String.valueOf(row[18]));
					surveyResultsReportBranch.setClickTroughForBranch(String.valueOf(row[19]));
				}
				surveyResultMap.put(surveyDetailsId, surveyResultsReportBranch);
			}
			return surveyResultMap;
		} catch (Exception hibernateException) {
			LOG.error("Exception caught in getSurveyResultForBranchId() ", hibernateException);
			throw new DatabaseException("Exception caught in getSurveyResultForBranchId() ", hibernateException);
		}
	}
}