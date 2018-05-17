package com.realtech.socialsurvey.compute.topology.bolts;

import com.realtech.socialsurvey.compute.common.SSAPIOperations;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.entities.response.SocialResponseObject;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.emailreports.GetDataForEmailReport;
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
    private static final Logger LOG = LoggerFactory.getLogger( GetDataForEmailReport.class );

    @Override public void executeTuple( Tuple input )
    {
        LOG.info( " Executing query to fetch data from SOCIAL_FEED_COLLECTION " );
        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );
        boolean success = false;

        long fileUploadId = reportRequest.getFileUploadId();
        Optional<List<SocialResponseObject>> response = null;
        long companyId = reportRequest.getCompanyId();
        int pageNum = 1;
        int pageSize = 10;
        String status = null;
        //TODO get the keywod from the request
        String keyword = "testing";

        try {
            do {
                response = SSAPIOperations.getInstance()
                    .getDataForSocialMonitorReport( companyId, keyword, reportRequest.getStartTime(), reportRequest.getEndTime(),
                        pageSize, ( pageNum - 1 ) * pageSize );
                if ( pageNum == 1 && ( !response.isPresent() || response.get().isEmpty() ) ) {
                    status = ReportStatus.BLANK.getValue();
                } else if ( response.isPresent() && !response.get().isEmpty() ) {
                    status = ReportStatus.PROCESSING.getValue();
                } else if ( pageNum > 1 && ( !response.isPresent() || response.get().isEmpty() ) )
                    status = ReportStatus.PROCESSED.getValue();
                success = true;
                LOG.info( "Emitting tuple with success = {},  fileUploadId = {}, status = {}, companyId = {}, enterAt = {}  ",
                    success, fileUploadId, status, companyId, pageNum-1 );
                _collector.emit( input, Arrays.asList( success, response.get(), fileUploadId, status, reportRequest, companyId, pageNum-1 ) );
                pageNum ++;
            }while ( response != null && response.isPresent() );

        } catch ( APIIntegrationException | IllegalArgumentException | IOException e ) {
            success = true;
            LOG.error( "Exception occurred while fetching socialfeed data  ", e );
            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
            failedMessagesService.insertTemporaryFailedReportRequest( reportRequest );
            LOG.info("Emitting tuple with success = {}, fileUploadId = {}, processingCompleted = {}",
                success, fileUploadId, ReportStatus.FAILED.getValue() );
            _collector.emit( input, Arrays.asList( success, response, fileUploadId, ReportStatus.FAILED.getValue(),
                reportRequest ) );
        }
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return null;
    }


    @Override public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer.declare( new Fields( "isSuccess", "socialFeed", "fileUploadId", "status", "reportRequest", "companyId", "enterAt" ) );
    }
}
