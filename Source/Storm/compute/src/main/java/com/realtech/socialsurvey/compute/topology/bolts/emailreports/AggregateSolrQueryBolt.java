/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.sql.SQLException;
import java.util.List;

import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.dao.SQLEmailCountDao;
import com.realtech.socialsurvey.compute.dao.impl.SQLEmailCountDaoImpl;
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

	/* (non-Javadoc)
	 * @see org.apache.storm.topology.IComponent#declareOutputFields(org.apache.storm.topology.OutputFieldsDeclarer)
	 */
	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#executeTuple(org.apache.storm.tuple.Tuple)
	 */
	@Override
	public void executeTuple(Tuple input) {
		LOG.info("Executing query to fetch survey invitation mails from solr");
		boolean success = false;

		// get the report request from the tuple
		ReportRequest reportRequest = ConversionUtils.deserialize(input.getString(0), ReportRequest.class);
		QueryResponse response = null;
		SQLEmailCountDao sqlEmailCountDao = new SQLEmailCountDaoImpl();

		if (reportRequest.getReportType().equals(ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName())) {

			LOG.info("Conversion to GMT timezone stared");
			// convert the startDate and endDate to GMT
			/*
			 * String startDateInGmt = getStartDateTimeInGmt(
			 * reportRequest.getStartDateExpectedTimeZone() ); String endDateInGmt =
			 * getEndDateTimeInGmt( reportRequest.getEndDateExpectedTimeZone() );
			 */

			String startDateInGmt = reportRequest.getStartDateExpectedTimeZone();
			String endDateInGmt = reportRequest.getEndDateExpectedTimeZone();

			response = new SolrEmailCountDaoImpl().getEmailCountForDateRange(ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName(),
					startDateInGmt, endDateInGmt);

			SurveyInvitationEmailCountMonth emailCountMonth = new SurveyInvitationEmailCountMonth();
			//Attempted count
			emailCountMonth.setAttempted(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_EMAIL_ATTEMPT)));
			//Delivered count
			emailCountMonth.setDelivered(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_DELIVERED)));
			//Differed count
			emailCountMonth.setDiffered(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_DIFFERED)));
			//Blocked count
			emailCountMonth.setBlocked(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_BLOCKED)));
			//Opened count
			emailCountMonth.setOpened(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_OPENED)));
			//Spamed count
			emailCountMonth.setSpamed(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_SPAMED)));
			//Unsubscribed count
			emailCountMonth.setUnsubscribed(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_UNSUBSCRIBED)));
			//Bounced count
			emailCountMonth.setBounced(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_BOUNCED)));
			//Link Clicked count
			emailCountMonth.setLinkClicked(getEmailCounts(response.getFacetPivot().get(ComputeConstants.SOLR_PIVOT_AGENT_LINK_CLICKED)));
			//Received Count
			try {
				emailCountMonth.setReceived(sqlEmailCountDao.getReceivedCount(startDateInGmt, endDateInGmt));
			} catch (SQLException e) {
				LOG.error("SQL exception while getting received count.",e);
				success = false;
			}
			
			try {
				sqlEmailCountDao.save(emailCountMonth);
				success = true;
			} catch (SQLException e) {
				LOG.error("SQL exception while saving email count data to db.",e);
				success = false;
			}
			
		}

	}

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck#prepareTupleForFailure()
	 */
	@Override
	public List<Object> prepareTupleForFailure() {
		// TODO Auto-generated method stub
		return null;
	}
	
    /**
     * Method to get the count for a pivot.
     * @param pivotFields
     * @return
     */
    public int getEmailCounts(List<PivotField> pivotFields) {
		int count = 0;
		for(PivotField pivotFiled : pivotFields) {
			count += pivotFiled.getCount();
		}
		return count;
	}
	

}
