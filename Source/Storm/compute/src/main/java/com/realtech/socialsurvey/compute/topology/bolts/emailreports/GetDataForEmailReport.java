/**
 * 
 */
package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

/**
 * @author Subhrajit
 *
 */
public class GetDataForEmailReport extends BaseComputeBoltWithAck {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger( GetDataForEmailReport.class );

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declare( new Fields( "isSuccess", "surveyMailList", "fileUploadId", "status", "reportRequest" ) );		
	}

	@Override
	public void executeTuple(Tuple input) {
		
		// get the report request from the tuple
        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );
        boolean success = false;
        
        if ( reportRequest.getReportType().equals( ReportType.SURVEY_INVITATION_EMAIL_REPORT.getName() ) ) {
            LOG.info("Executing query to fetch survey invitation mails data from table.");
            long fileUploadId = reportRequest.getFileUploadId();
            List<SurveyInvitationEmailCountMonth> reportResponse = null;
            String status = null;
            int month = 0;
            int year = 0;
            if(reportRequest.getStartTime() != 0) {
            	Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(reportRequest.getStartTime());
                month = cal.get(Calendar.MONTH)+1;
                year = cal.get(Calendar.YEAR);
            } 
            
            long companyId = reportRequest.getCompanyId();
            
            try {
                	reportResponse = SSAPIOperations.getInstance().getDataForEmailReport(month,year,companyId);
                   if ( reportResponse == null ) {
                        status = ReportStatus.BLANK.getValue();
                    } else if ( !reportResponse.isEmpty() && reportResponse.size() > 0) {
                        status = ReportStatus.PROCESSED.getValue();
                    }
                    success = true;
                    LOG.info( "Emitting tuple with success = {},  fileUploadId = {}, status = {} ", success, fileUploadId, status );
                    _collector.emit( input, Arrays.asList( success, reportResponse, fileUploadId, status,
                             reportRequest ) );

            } catch ( APIIntegrationException | IllegalArgumentException | IOException ex ) {
                success = true;
                LOG.error( "Exception occurred while fetching data from table ", ex );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
                LOG.info("Emitting tuple with success = {}, fileUploadId = {}, processingCompleted = {}",
                        success, fileUploadId, ReportStatus.FAILED.getValue() );
                _collector.emit( input, Arrays.asList( success, reportResponse, fileUploadId, ReportStatus.FAILED.getValue(),
                        reportRequest ) );
            }
        } else {
            LOG.info( "Emitting tuple with success = {}, fileUploadId = {}, status = {}", 
            		success, -1, null);
            
            _collector.emit( input, Arrays.asList( success, null, -1, null, null ) );
        }

		
	}

	@Override
	public List<Object> prepareTupleForFailure() {
		return Arrays.asList( false, null, -1, null, null );
	}

}
