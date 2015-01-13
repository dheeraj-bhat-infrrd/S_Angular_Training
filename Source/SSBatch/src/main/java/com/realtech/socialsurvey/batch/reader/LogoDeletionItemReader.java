package com.realtech.socialsurvey.batch.reader;
//SS-84 RM03
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.upload.impl.CloudUploadServiceImpl;

public class LogoDeletionItemReader implements ItemReader<String>,InitializingBean{
	
	private List<String> fileAbsolutePaths = null;
	
	private int cursor = CommonConstants.INITIAL_INDEX;
	
	@Autowired
	private EmailServices emailServices;
	
	@Value("${ADMIN_EMAIL_ID}")
	private String recipientMailId;
	
	@Autowired
	private CloudUploadServiceImpl cloudService;
	
	private static final Logger LOG = LoggerFactory.getLogger(LogoDeletionItemReader.class);
	
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
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		LOG.info("Reader called for item.");
		if(cursor < fileAbsolutePaths.size()){
			LOG.info("Returning the item to the processor : " + fileAbsolutePaths.get(cursor));
			return fileAbsolutePaths.get(cursor++);
		}		
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		//Fetches all the logos in the Amazon S3 bucket and loads it into the List.
		try{
			LOG.info("Fetching the list of logos from Amazon S3");
			fileAbsolutePaths = cloudService.listAllOjectsInBucket();
			LOG.info("Fetched the list of logos from Amazon S3");
		}catch(FatalException e){
			LOG.error("FatalException caught while fetching the list of logos from amazon server");
			sendFailureMail(e);
		}

	}

}
