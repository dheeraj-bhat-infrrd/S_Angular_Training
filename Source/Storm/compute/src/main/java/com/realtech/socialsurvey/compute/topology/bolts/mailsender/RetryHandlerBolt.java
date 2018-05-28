package com.realtech.socialsurvey.compute.topology.bolts.mailsender;

import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.entities.EmailMessage;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;

public class RetryHandlerBolt extends BaseComputeBoltWithAck {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger( RetryHandlerBolt.class );

    @Override
    public void executeTuple(Tuple input) {
        boolean isSuccess = false;
        boolean success = input.getBooleanByField("success");
        EmailMessage emailMessage = (EmailMessage) input.getValueByField("emailMessage");
        boolean isTemporaryException = input.getBooleanByField("isTemporaryException");

        FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();

        if(success && emailMessage.isRetried()){
            //delete the entry from mongo
            int nRemoved = failedMessagesService.deleteFailedEmailMessage(emailMessage.getRandomUUID());
            if(nRemoved >= 1) {
                LOG.info("Email Message with randomUUID {} has been deleted successfully", emailMessage.getRandomUUID());
                isSuccess = true;
            } else LOG.error("Something went wrong while deleting email message with randomUUID {}", emailMessage.getRandomUUID());

        } else if(!success && isTemporaryException){
            //message sending failed once again, increase the retry count and update the mongo
            int updatedCount = failedMessagesService.updateFailedEmailMessageRetryCount(emailMessage.getRandomUUID());
            if(updatedCount >=1 ) {
                LOG.info("Email Message with randomUUID {} has been updated successfully", emailMessage.getRandomUUID());
                isSuccess = true;
            } else LOG.error("Something went wrong while updating email message retryCount having randomUUID {}", emailMessage.getRandomUUID());
        }
        _collector.emit(Arrays.asList(isSuccess));
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return Arrays.asList(false);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("isSuccess"));
    }
}
