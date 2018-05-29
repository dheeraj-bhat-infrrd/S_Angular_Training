package com.realtech.socialsurvey.compute.topology.bolts;

import com.realtech.socialsurvey.compute.common.ComputeConstants;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.enums.ReportType;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


/**
 * @author Lavanya
 */

public class GetSocialFeedReport extends BaseComputeBoltWithAck {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( GetSocialFeedReport.class );

    @Override public void executeTuple( Tuple input )
    {
        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );
        boolean success = false;
        String reportType = reportRequest.getReportType();

        if(reportType.equals( ReportType.SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD.getName() ) ||
            reportType.equals( ReportType.SOCIAL_MONITOR_DATE_REPORT.getName() )) {

            LOG.info( " Executing query to fetch data from SOCIAL_FEED_COLLECTION " );
            long fileUploadId = reportRequest.getFileUploadId();
            Optional<List<SocialResponseObject>> response = null;
            long companyId = reportRequest.getCompanyId();
            int pageNum = 1;
            int pageSize = Integer.parseInt(LocalPropertyFileHandler.getInstance().getProperty( ComputeConstants.APPLICATION_PROPERTY_FILE,
                ComputeConstants.SOCIAL_MONITOR_REPORT_BATCH_SIZE ).orElseGet( () -> "100"));
            String status = null;
            String keyword = reportRequest.getKeyword();

            try {
                do {
                    if(reportType.equals( ReportType.SOCIAL_MONITOR_DATE_REPORT_FOR_KEYWORD.getName() )) {
                        response = SSAPIOperations.getInstance()
                            .getDataForSocialMonitorReport( companyId, keyword, reportRequest.getStartTime(), reportRequest.getEndTime(),
                                pageSize, ( pageNum - 1 ) * pageSize );
                    } else {
                        response = SSAPIOperations.getInstance()
                            .getDataForSocialMonitorReport( companyId, reportRequest.getStartTime(), reportRequest.getEndTime(),
                                pageSize, ( pageNum - 1 ) * pageSize );
                    }

                    if ( pageNum == 1 && ( !response.isPresent() || response.get().isEmpty() ) ) {
                        status = ReportStatus.BLANK.getValue();
                    } else if ( response.isPresent() && !response.get().isEmpty() ) {
                        status = ReportStatus.PROCESSING.getValue();
                    } else if ( pageNum > 1 && ( !response.isPresent() || response.get().isEmpty() ) )
                        status = ReportStatus.PROCESSED.getValue();
                    success = true;
                    LOG.info( "Emitting tuple with success = {}, fileUploadId = {}, status = {}, companyId = {}, enterAt = {}  ",
                        success, fileUploadId, status, companyId, pageNum );
                    _collector.emit( input, Arrays.asList( success, response.orElseGet(null), fileUploadId,
                        status, reportRequest, pageNum ) );
                    pageNum++;
                } while ( status.equals( ReportStatus.PROCESSING.getValue() ) );

            } catch ( APIIntegrationException | IllegalArgumentException | IOException e ) {
                success = true;
                LOG.error( "Exception occurred while fetching socialfeed data  ", e );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
                LOG.error( "Emitting tuple with success = {}, fileUploadId = {}, processingCompleted = {}", success,
                    fileUploadId, ReportStatus.FAILED.getValue() );
                _collector.emit( input,
                    Arrays.asList( success, response, fileUploadId, ReportStatus.FAILED.getValue(), reportRequest, -1 ) );
            }
        }
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, -1, null, null, -1 );
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer.declare( new Fields( "isSuccess", "socialFeed", "fileUploadId", "status", "reportRequest", "enterAt" ) );
    }
}
