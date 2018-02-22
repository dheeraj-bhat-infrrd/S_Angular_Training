/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.dao.impl.SolrEmailCountDaoImpl;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

/**
 * @author Subhrajit
 *
 */
public class AggregateSolrQueryBolt extends BaseComputeBoltWithAck {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(AggregateSolrQueryBolt.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.
	 * topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#
	 * executeTuple(org.apache.storm.tuple.Tuple)
	 */
	@Override
	public void executeTuple(Tuple input) {
		LOG.info("Executing query to fetch survey invitation mails from solr");

		// get the report request from the tuple
		ReportRequest reportRequest = ConversionUtils.deserialize(input.getString(0), ReportRequest.class);
		QueryResponse response = null;

		if (reportRequest.getReportType().equals(ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName())) {

			String startDateInGmt = reportRequest.getStartDateExpectedTimeZone();
			String endDateInGmt = reportRequest.getEndDateExpectedTimeZone();

			response = new SolrEmailCountDaoImpl().getEmailCountForDateRange(
					ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName(), startDateInGmt, endDateInGmt);

			// Received Count
			List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth = new ArrayList<SurveyInvitationEmailCountMonth>();
			try {
				agentEmailCountsMonth
						.addAll(SSAPIOperations.getInstance().getReceivedCountsMonth(startDateInGmt, endDateInGmt));
			} catch (IOException e1) {
				LOG.error("Exception while fetching the transaction received count.", e1);
			}
			// Attempted count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT);
			// Delivered count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED);
			// Differed count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED);
			// Blocked count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED);
			// Opened count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_OPENED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_OPENED);
			// Spamed count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_SPAMED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_SPAMED);
			// Unsubscribed count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED);
			// Bounced count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED);
			// Link Clicked count
			getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED),
					agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED);
			
			if(agentEmailCountsMonth == null || agentEmailCountsMonth.size() <= 0) {
				LOG.info("No data found for date range.");
			} else {
				SSAPIOperations.getInstance().saveEmailCountMonthData(agentEmailCountsMonth);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#
	 * prepareTupleForFailure()
	 */
	@Override
	public List<Object> prepareTupleForFailure() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Method to get the count for a pivot.
	 * 
	 * @param pivotFields
	 * @return
	 */
	private void getEmailCounts(List<PivotField> pivotFields,
			List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth, String pivotName) {
		Map<Integer, SurveyInvitationEmailCountMonth> countMap = convertToMap(pivotFields, pivotName);
		for (SurveyInvitationEmailCountMonth emailCount : agentEmailCountsMonth) {
			SurveyInvitationEmailCountMonth emailCountMap = countMap.get(emailCount.getAgentId());
			if (emailCountMap != null) {
				switch (pivotName) {
				case ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT:
					emailCount.setAttempted(emailCountMap.getAttempted());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED:
					emailCount.setDelivered(emailCountMap.getDelivered());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED:
					emailCount.setDiffered(emailCountMap.getDiffered());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED:
					emailCount.setBlocked(emailCountMap.getBlocked());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_OPENED:
					emailCount.setOpened(emailCountMap.getOpened());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_SPAMED:
					emailCount.setSpamed(emailCountMap.getSpamed());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED:
					emailCount.setUnsubscribed(emailCountMap.getUnsubscribed());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED:
					emailCount.setBounced(emailCountMap.getBounced());
					break;
				case ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED:
					emailCount.setLinkClicked(emailCountMap.getLinkClicked());
					break;
				}
			}
		}
	}

	private Map<Integer, SurveyInvitationEmailCountMonth> convertToMap(List<PivotField> pivotFields, String pivotName) {
		Map<Integer, SurveyInvitationEmailCountMonth> countMap = new HashMap<Integer, SurveyInvitationEmailCountMonth>();
		for (PivotField pivotFiled : pivotFields) {
			SurveyInvitationEmailCountMonth count = new SurveyInvitationEmailCountMonth();

			switch (pivotName) {
			case ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT:
				count.setAttempted(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED:
				count.setDelivered(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED:
				count.setDiffered(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED:
				count.setBlocked(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_OPENED:
				count.setOpened(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_SPAMED:
				count.setSpamed(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED:
				count.setUnsubscribed(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED:
				count.setBounced(pivotFiled.getCount());
				break;
			case ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED:
				count.setLinkClicked(pivotFiled.getCount());
				break;
			}
			countMap.put(Integer.parseInt(pivotFiled.getValue().toString()), count);
		}
		return countMap;
	}

}
