/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.realtech.socialsurvey.compute.common.APIOperations;
import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.EmailConstants;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
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

		if (reportRequest.getReportType().equals(ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName())) {

			long startDate = reportRequest.getStartTime();
			long endDate = reportRequest.getEndTime();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			String startDateInGmt = sdf.format(new Date(startDate));
			String endDateInGmt = sdf.format(new Date(endDate));
			
			JsonObject jsonObject = getSolrResponse(ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName(), startDateInGmt, endDateInGmt);

			// Received Count
			List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth = new ArrayList<SurveyInvitationEmailCountMonth>();
			List<SurveyInvitationEmailCountMonth> rcvdresponse = null;
			int startIndex = 0;
			try {
				do {
					rcvdresponse = SSAPIOperations.getInstance().getReceivedCountsMonth(startDate, endDate, startIndex,
							ComputeConstants.BATCH_SIZE);
					agentEmailCountsMonth.addAll(rcvdresponse);
					startIndex += ComputeConstants.BATCH_SIZE;
				} while (rcvdresponse != null && rcvdresponse.size() == ComputeConstants.BATCH_SIZE);
			} catch (IOException e) {
				LOG.error("Failed getting received count for date range {},{} in batch {}", startDateInGmt,
						endDateInGmt, startIndex);
			}
			
			if(jsonObject != null) {
				// Attempted count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT);
				// Delivered count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED);
				// Differed count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED);
				// Blocked count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED);
				// Opened count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_OPENED);
				// Spamed count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_SPAMED);
				// Unsubscribed count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED);
				// Bounced count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED);
				// Link Clicked count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED);
				// Dropped Count
				getEmailCounts(jsonObject,agentEmailCountsMonth, ComputeConstants.SOLR_PIVOT_AGENT_DROPPED);
			} else {
				LOG.info("Solr returned null response for date range.");
			}
			
			if(agentEmailCountsMonth == null || agentEmailCountsMonth.size() <= 0) {
				LOG.info("No data found for date range.");
			} else {
				LOG.info("Saving agent email count data for time frame {}-{}",agentEmailCountsMonth.get(0).getMonth(),
						agentEmailCountsMonth.get(0).getYear());
				saveEmailCountMonthData(agentEmailCountsMonth);

				Calendar cal = Calendar.getInstance();
				if (agentEmailCountsMonth.get(0)
						.getMonth() == ((cal.get(Calendar.MONTH) == 0) ? 12 : cal.get(Calendar.MONTH))
						&& agentEmailCountsMonth.get(0)
								.getYear() == ((cal.get(Calendar.MONTH) == 0) ? cal.get(Calendar.YEAR) - 1
										: cal.get(Calendar.YEAR))) {
					LOG.info("Saved agent email count data for time frame {}-{}",
							agentEmailCountsMonth.get(0).getMonth(), agentEmailCountsMonth.get(0).getYear());
					processAlltimeData(agentEmailCountsMonth);
				}
				
			}
		}
	}
	
	private void saveEmailCountMonthData(List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth) {
		SSAPIOperations.getInstance().saveEmailCountMonthData(agentEmailCountsMonth);
	}

	private void processAlltimeData(List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth) {
		LOG.info("Data processin for all time started.");
		List<SurveyInvitationEmailCountMonth> invitationAllTimeData = new ArrayList<SurveyInvitationEmailCountMonth>();
		List<SurveyInvitationEmailCountMonth> responseList = null;
		int startIndex = 0;
		try {
			do {
				responseList = SSAPIOperations.getInstance().getAllTimeDataForSurveyInvitationMail(startIndex,ComputeConstants.BATCH_SIZE);
				invitationAllTimeData.addAll(responseList);
				startIndex += ComputeConstants.BATCH_SIZE;
			}while(responseList != null && !responseList.isEmpty() && responseList.size() == ComputeConstants.BATCH_SIZE);
		}catch(IOException ie) {
			LOG.error("Failed getting all time data in batch {}", startIndex);
		}
		
		responseList = new ArrayList<SurveyInvitationEmailCountMonth>();
		
		for(int i = 0; i < agentEmailCountsMonth.size(); i++) {
			SurveyInvitationEmailCountMonth newCountObj = agentEmailCountsMonth.get(i);
			SurveyInvitationEmailCountMonth oldCountObj = null;
			try {
				oldCountObj = invitationAllTimeData.get(i);
			}catch(IndexOutOfBoundsException e) {
				LOG.debug("Array size is less than {}",i);
			}
			if(oldCountObj != null) {
				newCountObj.setAttempted(newCountObj.getAttempted() + oldCountObj.getAttempted());
				newCountObj.setDelivered(newCountObj.getDelivered() + oldCountObj.getDelivered());
				newCountObj.setDiffered(newCountObj.getDiffered() + oldCountObj.getDiffered());
				newCountObj.setBlocked(newCountObj.getBlocked() + oldCountObj.getBlocked());
				newCountObj.setOpened(newCountObj.getOpened() + oldCountObj.getOpened());
				newCountObj.setSpamed(newCountObj.getSpamed() + oldCountObj.getSpamed());
				newCountObj.setUnsubscribed(newCountObj.getUnsubscribed() + oldCountObj.getUnsubscribed());
				newCountObj.setBounced(newCountObj.getBounced() + oldCountObj.getBounced());
				newCountObj.setLinkClicked(newCountObj.getLinkClicked() + oldCountObj.getLinkClicked());
				newCountObj.setReceived(newCountObj.getReceived() + oldCountObj.getReceived());
				newCountObj.setDropped(newCountObj.getDropped() + oldCountObj.getDropped());
			}
			newCountObj.setMonth(0);
			newCountObj.setYear(0);
			responseList.add(newCountObj);
		}
		
		LOG.info("Triggering api call to store all time data.");
		saveEmailCountMonthData(responseList);
	}

	private JsonObject getSolrResponse(String name, String startDateInGmt, String endDateInGmt) {
		boolean isFacet = true;
		String facetField = "agentId";
		List<String> facetPivots = new ArrayList<String>();
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_OPENED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_SPAMED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED);
		facetPivots.add(ComputeConstants.SOLR_PIVOT_AGENT_DROPPED);
		
		String fieldQuery = formulateFieldQuery(Arrays.asList( EmailConstants.EMAIL_TYPE_SURVEY_INVITATION_MAIL ),startDateInGmt, endDateInGmt);
		int facetLimit = -1;
		int facetMinCount = 1;
		JsonObject response = APIOperations.getInstance()
				.getEmailCounts( "*:*",fieldQuery, isFacet, facetField, facetPivots,facetLimit, facetMinCount );
		//agentId : [ 1 TO * ]
		JsonObject obj = null;
		if(response != null && response.getAsJsonObject("facet_counts") != null) {
			obj = response.getAsJsonObject("facet_counts").getAsJsonObject("facet_pivot");
		}
		
		if(obj != null) {
			return obj;
		}
		return null;
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
	 * @param jsonObject
	 * @return
	 */
	private void getEmailCounts(JsonObject jsonObject,
			List<SurveyInvitationEmailCountMonth> agentEmailCountsMonth, String pivotName) {
		Map<Long, Long> countMap = null;
		
		switch (pivotName) {
		case ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setAttempted(count);
				}
			}
			
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setDelivered(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setDiffered(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setBlocked(count);
				}
			}
			
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_OPENED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setOpened(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_SPAMED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setSpamed(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setUnsubscribed(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setBounced(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED:
			countMap = convertToMap(jsonObject, pivotName);
			if(countMap != null && !countMap.isEmpty()) {
				for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
					long count = 0;
					if(countMap.containsKey(mailCount.getAgentId())) {
						count = countMap.get(mailCount.getAgentId());
					}
					mailCount.setLinkClicked(count);
				}
			}
			break;
		case ComputeConstants.SOLR_PIVOT_AGENT_DROPPED:
				countMap = convertToMap(jsonObject, pivotName);
				if(countMap != null && !countMap.isEmpty()) {
					for(SurveyInvitationEmailCountMonth mailCount : agentEmailCountsMonth) {
						long count = 0;
						if(countMap.containsKey(mailCount.getAgentId())) {
							count = countMap.get(mailCount.getAgentId());
						}
						mailCount.setDropped(count);
					}
				}
				break;
			}
	}

	private Map<Long, Long> convertToMap(JsonObject jsonObject, String pivotName) {
		Map<Long, Long> countMap = new HashMap<Long, Long>();
		
		for (JsonElement jsonElement : jsonObject.getAsJsonArray(pivotName)) {
			JsonObject obj = jsonElement.getAsJsonObject();
			
			long countVal = obj.get("count").getAsLong();
			long agentId = obj.get("value").getAsLong();
			
			countMap.put(agentId, countVal);
		}
		return countMap;
	}
	
	public String formulateFieldQuery(List<String> mailTypes, String startDate, String endDate) {
		LOG.info("Formulating the field query for getting mails based on mailType , start and end date");
		StringBuilder mailTypeInQueryBuilder = new StringBuilder("(");
		for (String mailType : mailTypes) {
			mailTypeInQueryBuilder.append(" ").append(mailType);
		}
		mailTypeInQueryBuilder.append(")");

		StringBuilder fieldQuery = new StringBuilder("mailType:").append(mailTypeInQueryBuilder)
				.append(" AND emailAttemptedDate:[").append(startDate).append(" TO ").append(endDate).append("]");

		LOG.info("Field query: {}", fieldQuery);
		return fieldQuery.toString();
	}

}