package com.realtech.socialsurvey.core.starter;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.entities.ImageDetails;
import com.realtech.socialsurvey.core.entities.ImagesCollection;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.images.ImageProcessor;
import com.realtech.socialsurvey.core.utils.images.impl.ImageProcessingException;

public class ImageProcessingStarter extends QuartzJobBean {

	public static final Logger LOG = LoggerFactory.getLogger(ImageProcessingStarter.class); 
	
	private ImageProcessor imageProcessor;
	
	@Override
	protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		LOG.info("Starting processing of images");
		initializeDependencies(jobExecutionContext.getMergedJobDataMap());
		ImagesCollection imageCollection = new ImagesCollection();
		imageCollection.setThumbnailImage(new ImageDetails());
		try {
			imageProcessor.processAndUpdateImageForAllDimensions("https://s3-us-west-1.amazonaws.com/agent-survey/dev/userprofilepics/d-83bcd094541e74bc4c4c13b8f5ba6d332b28e343ed4f10be95e2a277e9aff18c99a16be051319e919e3abde93a08b4c085f014e71edf59a5de209cc8656d8334.jpg", imageCollection, ImageProcessor.IMAGE_TYPE_PROFILE);
		}
		catch (ImageProcessingException | InvalidInputException e) {
			LOG.error("Could not process image", e);
		}
		LOG.info("Finished processing of images");
	}

	private void initializeDependencies(JobDataMap jobMap){
		imageProcessor = (ImageProcessor) jobMap.get("imageProcessor");
	}
}
