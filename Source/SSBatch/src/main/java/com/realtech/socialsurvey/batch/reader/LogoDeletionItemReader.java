package com.realtech.socialsurvey.batch.reader;
//SS-84 RM03
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.services.s3.AmazonS3;
import com.realtech.socialsurvey.batch.commons.BatchCommon;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.services.upload.impl.CloudUploadServiceImpl;

public class LogoDeletionItemReader implements ItemReader<String>,InitializingBean{
	
	private List<String> logoFileNames = null;
	
	private int cursor = CommonConstants.INITIAL_INDEX;
	
	@Autowired
	private BatchCommon commonServices;
	
	@Autowired
	private CloudUploadServiceImpl cloudService;
	
	@Value("${AMAZON_ENDPOINT}")
	private String endpoint;

	@Value("${AMAZON_BUCKET}")
	private String bucket;
	
	private AmazonS3 s3Client;
	
	private static final Logger LOG = LoggerFactory.getLogger(LogoDeletionItemReader.class);
	
	@Override
	public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		
		LOG.info("Reader called for item.");
		if(cursor < logoFileNames.size()){
			LOG.info("Returning the item to the processor : " + logoFileNames.get(cursor));
			return logoFileNames.get(cursor++);
		}		
		LOG.info("Item queue completely read. Returning null!");
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		//Creating the S3 client
		LOG.debug("Creating the S3 client");
		s3Client = cloudService.createAmazonClient(endpoint, bucket);
		LOG.debug("S3 client created");
		
		//Fetches all the logos in the Amazon S3 bucket and loads it into the List.
		try{
			LOG.info("Fetching the list of logos from Amazon S3");
			logoFileNames = cloudService.listAllOjectsInBucket(s3Client);
			LOG.info("Fetched the list of logos from Amazon S3");
		}catch(FatalException e){
			LOG.error("FatalException caught while fetching the list of logos from amazon server");
			commonServices.sendFailureMail(e);
		}

	}

}
