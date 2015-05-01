package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.MessageUtils;

@Component
public final class UploadUtils {
	private static final Logger LOG = LoggerFactory.getLogger(MessageUtils.class);

	@Value("${MAX_LOGO_WIDTH_PIXELS}")
	double maxWidth;

	@Value("${MAX_LOGO_HEIGHT_PIXELS}")
	double maxHeight;

	@Value("${MAX_LOGO_SIZE_BYTES}")
	double maxBytes;

	@Value("${LIST_LOGO_FORMATS}")
	String imageFormats;

	/**
	 * Method to validate File
	 */
	public void validateFile(File convFile) throws InvalidInputException {
		LOG.debug("Validating uploaded image");
		if (!imageFormat(convFile)) {
			throw new InvalidInputException("Upload failed: Not valid Format", DisplayMessageConstants.INVALID_LOGO_FORMAT
					+ "(" + imageFormats + ")");
		}
		if (!imageSize(convFile)) {
			throw new InvalidInputException("Upload Failed: MAX size exceeded", DisplayMessageConstants.INVALID_LOGO_SIZE
					+ "(" + maxBytes + "bytes)");
		}
		if (!imageDimension(convFile)) {
			throw new InvalidInputException("Upload Failed: MAX dimensions exceeded", DisplayMessageConstants.INVALID_LOGO_DIMENSIONS
					+ "(" + maxWidth + " x " + maxHeight + " pixels)");
		}
		LOG.debug("Validated uploaded image");
	}
	
	/**
	 * Method to validate logo image size for a company
	 */
	public boolean imageSize(File logo) {
		LOG.debug("Validation imageSize method inside ImageUploadServiceImpl called");
		return (logo.length() < maxBytes) ? true : false;
	}

	/**
	 * Method to validate logo image format for a company
	 */
	public boolean imageFormat(File logo) {
		LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				// Find the format of the image and converting to upper to match with allowed extensions
				String formatName = reader.getFormatName().toUpperCase();
				LOG.debug("Format of the file: "+formatName);
				String[] listFormats = imageFormats.split(",");
				Set<String> setFormats = new HashSet<String>(Arrays.asList(listFormats));

				if (setFormats.contains(formatName)) {
					LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl completed successfully");
					return true;
				}
			}
			LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl completed successfully");
			return false;
		}
		catch (IOException e) {
			LOG.error("IOException occured while reading from ImageInputStream. Reason : " + e.getMessage(), e);
			throw new FatalException("IOException occured while reading from ImageInputStream. Reason : " + e.getMessage(), e);
		}
		finally {
			try {
				if (reader != null) {
					reader.dispose();
				}
				if (imageStream != null) {
					imageStream.close();
				}
			}
			catch (IOException e) {
				LOG.error("IOException occured while closing the ImageReader/ImageInputStream. Reason : " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method to validate logo image dimension for a company
	 */
	public boolean imageDimension(File logo) {
		LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		if(maxWidth == -1 && maxHeight == -1) {
			return true;
		}
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				int width = reader.getWidth(0);
				int height = reader.getHeight(0);

				LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl completed successfully");
				return ((width <= maxWidth) && (height <= maxHeight)) ? true : false;
			}
			LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl completed successfully");
			return false;
		}
		catch (IOException e) {
			LOG.error("IOException occured while reading from ImageInputStream. Reason : " + e.getMessage(), e);
			throw new FatalException("IOException occured while reading from ImageInputStream. Reason : " + e.getMessage(), e);
		}
		finally {
			try {
				if (reader != null) {
					reader.dispose();
				}
				if (imageStream != null) {
					imageStream.close();
				}
			}
			catch (IOException e) {
				LOG.error("IOException occured while closing the ImageReader/ImageInputStream. Reason : " + e.getMessage(), e);
			}
		}
	}
}