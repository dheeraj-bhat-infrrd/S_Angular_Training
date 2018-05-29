package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.enums.FileUploadStatus;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.exception.FileUploadUpdationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class UpdateFileUploadStatusBolt extends BaseComputeBoltWithAck
{

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( UpdateFileUploadStatusBolt.class );


    @Override public void executeTuple( Tuple input ) {
        LOG.info( "Trying to update the fileUpdateStatus to processing..." );
        boolean isSuccess = false;

        // get the report request from the tuple
        ReportRequest reportRequest = ConversionUtils.deserialize( input.getString( 0 ), ReportRequest.class );
        try {
            Call<FileUploadResponse> requestCall = RetrofitApiBuilder.apiBuilderInstance()
                .getSSAPIIntergrationService().updateFileUploadStatus(reportRequest.getFileUploadId(),
                    FileUploadStatus.STATUS_UNDER_PROCESSING.getValue());
            Response<FileUploadResponse> response = requestCall.execute();
            RetrofitApiBuilder.apiBuilderInstance().validateFileUploadResponse( response );
            if ( LOG.isTraceEnabled() ) {
                LOG.trace( "Response in UpdateFileUploadStatusBolt is", response.body() );
            }
            LOG.debug("Response is {}", response.body());
            if (response.body().getRecordsUpdated() == 1) {
                isSuccess = true;
            }
        } catch ( FileUploadUpdationException ex) {
            LOG.error("Exception occurred while updating the status of fileUploadTable" + ex.getMessage());
            LOG.warn( "Message processing will NOT be retried. Message will be logged for inspection." );
            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
            failedMessagesService.insertPermanentlyFailedReportRequest(reportRequest, ex);
        }
        catch (APIIntegrationException | IllegalArgumentException | IOException ex) {
            LOG.error("Exception occurred while updating the status of fileUploadTable" + ex.getMessage());
            FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
            failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
        }

        LOG.info( "Emitting tuple with isSuccess {} ", isSuccess );
        _collector.emit(  input, Arrays.asList( isSuccess ) );
    }


    @Override public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null );
    }


    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare( new Fields("isSuccess"));
    }
}
