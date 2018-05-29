package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.RetrofitApiBuilder;
import com.realtech.socialsurvey.compute.entities.FileUploadResponse;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.enums.FileUploadStatus;
import com.realtech.socialsurvey.compute.exception.APIIntegrationException;
import com.realtech.socialsurvey.compute.exception.FileUploadUpdationException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

import retrofit2.Call;
import retrofit2.Response;

public class UpdateFileUploadStatusAndFileNameBolt extends BaseComputeBoltWithAck {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( UpdateFileUploadStatusAndFileNameBolt.class );

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("isSuccess"));
    }

    @Override
    public void executeTuple(Tuple input) {

        boolean success = input.getBooleanByField( "isSuccess" );
        boolean isSuccess = false;

        if(success) {
            Call<FileUploadResponse> requestCall;
            long fileUploadId = input.getLongByField("fileUploadId");
            ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");
            String status = input.getStringByField("status");
            int  fileUploadStatus = -1;
            switch (status){
                case "PROCESSED" : fileUploadStatus = FileUploadStatus.STATUS_COMPLETED.getValue(); break;
                case "FAILED" : fileUploadStatus = FileUploadStatus.STATUS_FAILED.getValue(); break;
                case "BLANK" : fileUploadStatus = FileUploadStatus.STATUS_BLANK.getValue(); break;
                default: LOG.warn("Invalid status found!!! Needs to be handled."); fileUploadStatus = FileUploadStatus.STATUS_FAILED.getValue();
            }

            try {
                String fileLocationInS3 = input.getStringByField("fileName");

                requestCall = RetrofitApiBuilder.apiBuilderInstance()
                        .getSSAPIIntergrationService().updateFileUploadStatusAndLocation(fileUploadId,
                                fileUploadStatus, fileLocationInS3 == null ? "" : fileLocationInS3);
                Response<FileUploadResponse> response = requestCall.execute();
                RetrofitApiBuilder.apiBuilderInstance().validateFileUploadResponse(response);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Updation response in UpdateFileUploadStatusAndFileNameBolt is {}", response.body());
                }
                LOG.info("Response is {} for  report completed update with fileUploadId {}", response.body(), fileUploadId);
                if (response.body().getRecordsUpdated() == 1) {
                    isSuccess = true;
                } else {
                    requestCall = RetrofitApiBuilder.apiBuilderInstance()
                            .getSSAPIIntergrationService().updateFileUploadStatus(fileUploadId, FileUploadStatus.STATUS_FAILED.getValue());
                    response = requestCall.execute();
                    RetrofitApiBuilder.apiBuilderInstance().validateFileUploadResponse(response);
                    LOG.info("Response is {} for a report failed update with fileUploadId {}", response.body(), fileUploadId);
                }
            }
            catch ( FileUploadUpdationException ex) {
                LOG.error("Exception occurred while updating the status of fileUploadTable " + ex.getMessage());
                LOG.warn( "Message processing will NOT be retried. Message will be logged for inspection." );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertPermanentlyFailedReportRequest(reportRequest, ex);
            }
            catch (IOException | APIIntegrationException ex) {
                LOG.error("Exception occurred while updating the status of fileUploadTable " + ex.getMessage());
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
            }
        }
        LOG.info( "Emitting tuple with isSuccess = {} ", isSuccess );
        _collector.emit(input, Arrays.asList(isSuccess) );
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return Arrays.asList(false);    }
}
