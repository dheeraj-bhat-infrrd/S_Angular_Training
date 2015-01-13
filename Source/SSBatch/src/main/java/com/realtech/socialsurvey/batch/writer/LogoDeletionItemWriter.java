package com.realtech.socialsurvey.batch.writer;

import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.impl.CloudUploadServiceImpl;

public class LogoDeletionItemWriter implements ItemWriter<String>{
	
	@Autowired
	CloudUploadServiceImpl cloudService;
	
	@Autowired
	private EmailServices emailServices;
	
	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	private static final Logger LOG = LoggerFactory.getLogger(LogoDeletionItemWriter.class);
	
	private void sendFailureMail(Exception e) {

		LOG.debug("Sending failure mail to recpient : " + recipientMailId);
		String stackTrace = ExceptionUtils.getFullStackTrace(e);
		// replace all dollars in the stack trace with \$
		stackTrace = stackTrace.replace("$", "\\$");

		try {
			emailServices.sendFatalExceptionEmail(recipientMailId, stackTrace);
			LOG.debug("Failure mail sent to admin.");
		}
		catch (InvalidInputException | UndeliveredEmailException e1) {
			LOG.error("CustomItemProcessor : Exception caught when sending Fatal Exception mail. Message : " + e1.getMessage());
		}
	}

	@Override
	public void write(List<? extends String> items) throws Exception {
		LOG.info("Writer called to delete items from the Amazon S3 server");
		
		try{
			// Delete every item in the list from the Amazon S3 server.
			for(String item : items){
				LOG.info("Deleting the : " + item + " item from the Amazon S3 server");
				cloudService.deleteObjectFromBucket(item);
				LOG.info("Deleted the item : " + item);
			}
		}catch(FatalException e){
			LOG.error("FatalException caught when deleting an item");
			sendFailureMail(e);
		}
		
	}

}
