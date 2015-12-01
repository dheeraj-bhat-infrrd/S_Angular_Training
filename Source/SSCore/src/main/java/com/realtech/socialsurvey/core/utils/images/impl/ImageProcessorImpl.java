package com.realtech.socialsurvey.core.utils.images.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.images.ImageProcessor;

/**
 * Image processing using scalr
 */
@Component
public class ImageProcessorImpl implements ImageProcessor {

	private static Logger LOG = LoggerFactory.getLogger(ImageProcessorImpl.class);

	@Value("${CDN_PATH}")
	private String amazonEndpoint;

	@Value("${AMAZON_IMAGE_BUCKET}")
	private String amazonImageBucket;

	@Value("${AMAZON_LOGO_BUCKET}")
	private String amazonLogoBucket;

	@Autowired
	private FileUploadService fileUploadService;

	@Override
	public String processImage(String imageFileName, String imageType)
			throws ImageProcessingException, InvalidInputException {
        if ( imageFileName == null || imageFileName.isEmpty() ) {
            LOG.error( "Image File Name is empty" );
            throw new InvalidInputException( "Image File Name is empty" );
        }
        if ( imageType == null || imageType.isEmpty() ) {
            LOG.error( "Image Type is empty" );
            throw new InvalidInputException( "Image Type is empty" );
        }
		LOG.info("Processing images for " + imageFileName);
		// get the image
		BufferedImage sourceImage = getImageFromCloud(imageFileName);
		// process thumbnail
		LOG.info("Processing image for thumbnail " + imageFileName);
		String extension = imageFileName.substring(imageFileName.lastIndexOf(".") + 1).toLowerCase();
		File processedImage = processImage(sourceImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, extension);
		// name the file that needs to be uploaded
		String thumbnailImageName = getThumbnailImageName(imageFileName, extension);
		String uploadedFileName = writeImage(thumbnailImageName, processedImage, imageType);
		deleteTempFile(processedImage);
		return uploadedFileName;
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
		File f = new File(".");
		LOG.debug("File path: "+f.getAbsolutePath());
		File processedFile = null;
		scaledImage = Scalr.resize(image, Method.SPEED, Mode.AUTOMATIC, width, height);
		processedFile = new File(CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + String.valueOf(System.currentTimeMillis()) + "-"
				+ width + "-" + height + "." + imageExtension);
		try {
            LOG.debug( "File path of processed file : " + processedFile.getAbsolutePath() );
			processedFile.createNewFile();
			if (processedFile.exists()) {
				ImageIO.write(scaledImage, imageExtension, processedFile);
			}
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
	public String writeImage(String destFileName, File image, String imageType) throws ImageProcessingException, InvalidInputException {
		if (image == null || destFileName == null || destFileName.isEmpty() || imageType == null || imageType.isEmpty()) {
			LOG.error("Invalid details provided to upload image to S3");
			throw new InvalidInputException("Invalid details provided to upload image to S3");
		}
		LOG.info("Uploading " + destFileName + " to cloud");
		String cloudFrontUrl = null;
		if (imageType.equals(CommonConstants.IMAGE_TYPE_PROFILE)) {
			LOG.debug("Uploading profile pic");
			fileUploadService.uploadProfileImageFile(image, destFileName, true);
			cloudFrontUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonImageBucket + CommonConstants.FILE_SEPARATOR + destFileName;
		}
		else if (imageType.equals(CommonConstants.IMAGE_TYPE_LOGO)) {
			LOG.debug("Uploading logo");
			fileUploadService.uploadLogoImageFile(image, destFileName, true);
			cloudFrontUrl = amazonEndpoint + CommonConstants.FILE_SEPARATOR + amazonLogoBucket + CommonConstants.FILE_SEPARATOR + destFileName;
		}
		LOG.debug("Returning image name: " + cloudFrontUrl);
		return cloudFrontUrl;
	}

	private String getThumbnailImageName(String originalImageName, String extension) {
		LOG.debug("Getting thumbnail name for " + originalImageName);
		return originalImageName.substring(originalImageName.lastIndexOf("/")+1, originalImageName.lastIndexOf("." + extension)) + "-t." + extension;
	}

	private void deleteTempFile(File file) {
		LOG.debug("Deleting file: " + file.getAbsolutePath());
		FileUtils.deleteQuietly(file);
	}

}
