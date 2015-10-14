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
			imageProcessor.processAndUpdateImageForAllDimensions("https://don7n2as2v6aa.cloudfront.net/userprofilepics/P-ae12f4d2e10a5437b18dbc58c55170737b409c7dd5aa3a5121f77757f94d5acd71b277130acdb9a08b3ea8169734c834aaee0c036840e20915ca0873e8d0ae19.png", imageCollection, ImageProcessor.IMAGE_TYPE_PROFILE);
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
