package com.realtech.socialsurvey.batch.writer;
//SS-84 RM03
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.amazonaws.services.s3.AmazonS3;
import com.realtech.socialsurvey.batch.commons.BatchCommon;
import com.realtech.socialsurvey.core.commons.CoreCommon;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;

public class LogoDeletionItemWriter implements ItemWriter<String>,InitializingBean{
	
	@Autowired
	private FileUploadService cloudService;
	
	@Autowired
	private BatchCommon commonServices;
	
	@Autowired
	private CoreCommon coreCommonServices;
	
	@Value("${AMAZON_ENDPOINT}")
	private String endpoint;

	@Value("${AMAZON_BUCKET}")
	private String bucket;
	
	private AmazonS3 s3Client;
	
	private static final Logger LOG = LoggerFactory.getLogger(LogoDeletionItemWriter.class);
	
	@Override
	public void write(List<? extends String> items) throws Exception {
		LOG.info("Writer called to delete items from the Amazon S3 server");
		
		try{
			// Delete every item in the list from the Amazon S3 server.
			for(String item : items){
				LOG.info("Deleting the : " + item + " item from the Amazon S3 server");
				cloudService.deleteObjectFromBucket(item,s3Client);
				LOG.info("Deleted the item : " + item);
			}
		}catch(FatalException e){
			LOG.error("FatalException caught when deleting an item");
			coreCommonServices.sendFailureMail(e);
		}
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		LOG.debug("Creating the S3 client");
		s3Client = cloudService.createAmazonClient(endpoint, bucket);
		LOG.debug("S3 client created");
		
	}

}
