package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.upload.ImageUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.PropertyFileReader;

@Component
public class ImageUploadServiceImpl implements ImageUploadService {

	private static final Logger LOG = LoggerFactory.getLogger(ImageUploadServiceImpl.class);

	@Autowired
	private PropertyFileReader propertyFileReader;

	@Override
	public String imageUploadHandler(MultipartFile fileLocal, String logoName) throws InvalidInputException {
		LOG.info("Method imageUploadHandler inside ImageUploadServiceImpl called");

		BufferedOutputStream stream = null;
		if (!fileLocal.isEmpty()) {
			try {
				byte[] bytes = fileLocal.getBytes();

				File convFile = new File(fileLocal.getOriginalFilename());
				fileLocal.transferTo(convFile);

				LOG.debug("Validating uploaded image");
				if (!imageFormat(convFile)) {
					throw new InvalidInputException("Upload failed: Not valid Format", DisplayMessageConstants.INVALID_LOGO_FORMAT);
				}
				if (!imageSize(convFile)) {
					throw new InvalidInputException("Upload Failed: MAX size exceeded", DisplayMessageConstants.INVALID_LOGO_SIZE);
				}
				if (!imageDimension(convFile)) {
					throw new InvalidInputException("Upload Failed: MAX dimensions exceeded", DisplayMessageConstants.INVALID_LOGO_DIMENSIONS);
				}

				// Creating the directory to store file
				LOG.debug("Creating the directory to store file");
				String rootPath = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.LOGO_HOME_DIRECTORY);
				File dir = new File(rootPath);
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				LOG.debug("Creating the file on server");
				String logoFormat = logoName.substring(logoName.lastIndexOf("."));
				String logoNameHash = logoName.hashCode() + "";
				File serverFile = new File(dir.getAbsolutePath() + File.separator + logoNameHash + logoFormat);
				stream = new BufferedOutputStream(new FileOutputStream(serverFile));
				stream.write(bytes);

				LOG.debug("Server File Location=" + serverFile.getAbsolutePath());
				LOG.info("Method imageUploadHandler inside ImageUploadServiceImpl completed successfully");
				return logoNameHash + logoFormat;
			}
			catch (IOException e) {
				LOG.error("IOException occured while reading file. Reason : " + e.getMessage(), e);
				throw new FatalException("IOException occured while reading file. Reason : " + e.getMessage(), e);
			}
			finally {
				try {
					if (stream != null) {
						stream.close();
					}
				}
				catch (IOException e) {
					LOG.error("IOException occured while closing the BufferedOutputStream. Reason : " + e.getMessage(), e);
				}
			}
		}
		else {
			LOG.error("Method imageUploadHandler inside ImageUploadServiceImpl failed to upload");
			throw new InvalidInputException("Upload failed: " + logoName + " because the file was empty", DisplayMessageConstants.INVALID_LOGO_FILE);
		}
	}

	/**
	 * Method to validate logo image size for a company
	 */
	private boolean imageSize(File logo) {
		LOG.debug("Validation imageSize method inside ImageUploadServiceImpl called");
		double maxPixels = Double.parseDouble(propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE,
				CommonConstants.MAX_LOGO_SIZE_BYTES));

		LOG.debug("Validation imageSize method inside ImageUploadServiceImpl completed successfully");
		return (logo.length() < maxPixels) ? true : false;
	}

	/**
	 * Method to validate logo image format for a company
	 */
	private boolean imageFormat(File logo) {
		LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				String formatName = reader.getFormatName();
				String[] listFormats = propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE, CommonConstants.LIST_LOGO_FORMATS)
						.split(",");
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
	private boolean imageDimension(File logo) {
		LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				int width = reader.getWidth(0);
				int height = reader.getHeight(0);

				double maxWidth = Double.parseDouble(propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE,
						CommonConstants.MAX_LOGO_WIDTH_PIXELS));
				double maxHeight = Double.parseDouble(propertyFileReader.getProperty(CommonConstants.CONFIG_PROPERTIES_FILE,
						CommonConstants.MAX_LOGO_HEIGHT_PIXELS));
				LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl completed successfully");
				return ((width < maxWidth) && (height < maxHeight)) ? true : false;
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
				// throw new FatalException("IOException occured while closing the ImageReader/ImageInputStream. Reason : " + e.getMessage(), e);
			}
		}
	}
}