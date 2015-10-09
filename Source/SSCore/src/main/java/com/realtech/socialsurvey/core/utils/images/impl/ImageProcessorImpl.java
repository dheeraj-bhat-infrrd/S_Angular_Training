package com.realtech.socialsurvey.core.utils.images.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.ImagesCollection;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.images.ImageProcessor;

/**
 * Image processing using scalr
 */
public class ImageProcessorImpl implements ImageProcessor {

	private static Logger LOG = LoggerFactory.getLogger(ImageProcessorImpl.class);

	@Override
	public ImagesCollection processAndUpdateImageForAllDimensions(String imageFileName, ImagesCollection imagesCollection, String imageType)
			throws ImageProcessingException, InvalidInputException {
		LOG.info("Processing images for " + imageFileName);
		// get the image
		BufferedImage sourceImage = getImageFromCloud(imageFileName);
		// process thumbnail
		LOG.info("Processing image for thumbnail " + imageFileName);
		String extension = imageFileName.substring(imageFileName.lastIndexOf(".")).toLowerCase();
		File processedImage = processImage(sourceImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, extension);
		// name the file that needs to be uploaded
		
		return null;
	}

	@Override
	public File processImage(BufferedImage image, int width, int height, String imageExtension) throws ImageProcessingException,
			InvalidInputException {
		if (image == null || imageExtension == null || imageExtension.isEmpty()) {
			LOG.error("Could not find file to process");
			throw new InvalidInputException("Could not find file to process");
		}
		LOG.info("Processing image for width " + width + " and length " + height);
		BufferedImage scaledImage = null;
		File processedFile = null;
		scaledImage = Scalr.resize(image, Mode.AUTOMATIC, width, height);
		processedFile = new File(CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + String.valueOf(System.currentTimeMillis()) + "-"
				+ width + "-" + height + "." + imageExtension);
		try {
			ImageIO.write(scaledImage, imageExtension, processedFile);
		}
		catch (IOException e) {
			LOG.error("Error while processing image.", e);
			throw new ImageProcessingException("Error while processing image.", e);
		}
		return processedFile;
	}

	@Override
	public BufferedImage getImageFromCloud(String imageFileName) throws ImageProcessingException, InvalidInputException {
		LOG.info("Downloading file from cloud: " + imageFileName);
		if (imageFileName == null || imageFileName.isEmpty()) {
			LOG.error("No file name specified");
			throw new InvalidInputException("No source file name specified.");
		}
		BufferedImage bufferedImage = null;
		try {
			URL url = new URL(imageFileName);
			LOG.debug("Reading the file from url");
			bufferedImage = ImageIO.read(url);
		}
		catch (IOException e) {
			LOG.error("Error while processing the image.", e);
			throw new ImageProcessingException("Could not proces image: " + imageFileName, e);
		}
		return bufferedImage;
	}

	@Override
	public void writeImage(String destFileName, File image) throws ImageProcessingException {
		// TODO Auto-generated method stub

	}

}
