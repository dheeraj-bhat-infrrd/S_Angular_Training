package com.realtech.socialsurvey.core.starter;

import java.util.List;
import java.util.Map;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import com.realtech.socialsurvey.core.commons.CommonConstants;
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
		Map<Long, String> images = null;
		// get unprocessed company profile images
		images = getUnprocessedProfileImages(CommonConstants.COMPANY_SETTINGS_COLLECTION);
		String fileName = null;
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_PROFILE);
					updateImage(id, fileName, CommonConstants.COMPANY_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_PROFILE);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}
		// get unprocessed region profile images
		images = getUnprocessedProfileImages(CommonConstants.REGION_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_PROFILE);
					updateImage(id, fileName, CommonConstants.REGION_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_PROFILE);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}
		// get unprocessed branch profile images
		images = getUnprocessedProfileImages(CommonConstants.BRANCH_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_PROFILE);
					updateImage(id, fileName, CommonConstants.BRANCH_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_PROFILE);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}
		// get unprocessed agent profile images
		images = getUnprocessedProfileImages(CommonConstants.AGENT_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_PROFILE);
					updateImage(id, fileName, CommonConstants.AGENT_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_PROFILE);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}

		// get unprocessed company logo images
		images = getUnprocessedLogoImages(CommonConstants.COMPANY_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_LOGO);
					updateImage(id, fileName, CommonConstants.COMPANY_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_LOGO);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}
		// get unprocessed region logo images
		images = getUnprocessedLogoImages(CommonConstants.REGION_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_LOGO);
					updateImage(id, fileName, CommonConstants.REGION_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_LOGO);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}
		// get unprocessed branch logo images
		images = getUnprocessedLogoImages(CommonConstants.BRANCH_SETTINGS_COLLECTION);
		if (images != null) {
			for (long id : images.keySet()) {
				try {
					fileName = imageProcessor.processImage(images.get(id), ImageProcessor.IMAGE_TYPE_LOGO);
					updateImage(id, fileName, CommonConstants.BRANCH_SETTINGS_COLLECTION, ImageProcessor.IMAGE_TYPE_LOGO);
				}
				catch (ImageProcessingException | InvalidInputException e) {
					LOG.error("Skipping... Could not process image: " + id + " : " + images.get(id), e);
				}
			}
		}

		try {
			imageProcessor
					.processImage(
							"https://don7n2as2v6aa.cloudfront.net/userprofilepics/P-ae12f4d2e10a5437b18dbc58c55170737b409c7dd5aa3a5121f77757f94d5acd71b277130acdb9a08b3ea8169734c834aaee0c036840e20915ca0873e8d0ae19.png",
							ImageProcessor.IMAGE_TYPE_PROFILE);
		}
		catch (ImageProcessingException | InvalidInputException e) {
			LOG.error("Could not process image", e);
		}
		LOG.info("Finished processing of images");
	}

	private void initializeDependencies(JobDataMap jobMap) {
		imageProcessor = (ImageProcessor) jobMap.get("imageProcessor");
	}

	private Map<Long, String> getUnprocessedProfileImages(String collection) {
		return null;
	}

	private Map<Long, String> getUnprocessedLogoImages(String collection) {
		return null;
	}

	private void updateImage(long iden, String fileName, String collectionName, String imageType) {}
}
