package com.realtech.socialsurvey.core.services.upload.impl;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
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
	 * Method to validate image
	 */
	public void validateFile(File convFile) throws InvalidInputException {
        LOG.debug( "Validating uploaded image" );
        String imageFormat = isImageFormatValid( convFile );
        if ( imageFormat != "true" ) {
            throw new InvalidInputException( "Upload failed: " + imageFormat + " is not a valid Format",
                DisplayMessageConstants.INVALID_LOGO_FORMAT + "(" + imageFormats + ")" );
        }
        String imageSize = isImageSizeValid( convFile );
        if ( imageSize != "true" ) {
            throw new InvalidInputException( "Upload Failed: MAX size exceeded. Size of image is:" + imageSize,
                DisplayMessageConstants.INVALID_LOGO_SIZE + "(" + maxBytes + "bytes)" );
        }
        String imageDimension = isImageDimensionValid( convFile );
        if ( imageDimension != "true" ) {
            throw new InvalidInputException( "Upload Failed: MAX dimensions exceeded. Dimensions: " + imageDimension,
                DisplayMessageConstants.INVALID_LOGO_DIMENSIONS + "(" + maxWidth + " x " + maxHeight + " pixels)" );
        }
        LOG.debug( "Validated uploaded image" );
	}

	/**
	 * Method to validate image size
	 */
	public String isImageSizeValid(File logo) {
		LOG.debug("Validation imageSize method inside ImageUploadServiceImpl called");
		if (maxBytes == -1 ) {
            return "true";
        }
        return ( logo.length() < maxBytes ) ? "true" : Long.toString( logo.length() );
	}

	/**
	 * Method to validate image format
	 */
	public String isImageFormatValid(File logo) {
		LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			String formatName = null;
			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				// Find the format of the image and match with allowed extensions
				formatName = reader.getFormatName().toUpperCase();
				LOG.debug("Format of the file: " + formatName);
				String[] listFormats = imageFormats.split(",");
				Set<String> setFormats = new HashSet<String>(Arrays.asList(listFormats));

				if (setFormats.contains(formatName)) {
					LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl completed successfully");
					return "true";
				}
			}
			
			LOG.debug("Validation imageFormat method inside ImageUploadServiceImpl completed successfully");
			return formatName;
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
	 * Method to validate image dimension
	 */
	public String isImageDimensionValid(File logo) {
		LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl called");
		ImageInputStream imageStream = null;
		ImageReader reader = null;
		
		if (maxWidth == -1 && maxHeight == -1) {
			return "true";
		}
		try {
			imageStream = ImageIO.createImageInputStream(logo);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(imageStream);

			int width = 0,height = 0;
			if (readers.hasNext()) {
				reader = readers.next();
				reader.setInput(imageStream, true, true);

				width = reader.getWidth(0);
				height = reader.getHeight(0);

				LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl completed successfully");
                return ( ( width <= maxWidth ) && ( height <= maxHeight ) ) ? "true" : width + "X" + height;
			}
			LOG.debug("Validation imageDimension method inside ImageUploadServiceImpl completed successfully");
            return width + "X" + height;
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
	 * Method to resize image to given dimensions
	 */
	public static void resizeImage(String inputImagePath, String outputImagePath, int scaledWidth, int scaledHeight) throws IOException {
		LOG.debug("Method resizeImage() called from UploadUtils");
		// reads input image
		File inputFile = new File(inputImagePath);
		BufferedImage inputImage = ImageIO.read(inputFile);

		// creates output image
		BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, inputImage.getType());

		// scales the input image to the output image
		Graphics2D g2d = outputImage.createGraphics();
		g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
		g2d.dispose();

		// extracts extension of output file
		String formatName = outputImagePath.substring(outputImagePath.lastIndexOf(".") + 1);

		// writes to output file
		ImageIO.write(outputImage, formatName, new File(outputImagePath));
		LOG.debug("Method resizeImage() called from UploadUtils");
	}

	/**
	 * Method to crop image to given co-ordinates
	 */
	public static BufferedImage cropImage(BufferedImage img, int cropWidth, int cropHeight, int cropStartX, int cropStartY) {
		LOG.debug("Method cropImage() called from UploadUtils");

		Rectangle clip;
		BufferedImage clipped = null;
		boolean isClipAreaAdjusted = false;
		Dimension size = new Dimension(cropWidth, cropHeight);

		// Checking for negative X Co-ordinate
		if (cropStartX < 0) {
			cropStartX = 0;
			isClipAreaAdjusted = true;
		}
		// Checking for negative Y Co-ordinate
		if (cropStartY < 0) {
			cropStartY = 0;
			isClipAreaAdjusted = true;
		}

		// Checking if the clip area lies outside rectangle
		if ((size.width + cropStartX) <= img.getWidth() && (size.height + cropStartY) <= img.getHeight()) {
			// Setting up a clip rectangle when clip area lies within the image.
			clip = new Rectangle(size);
			clip.x = cropStartX;
			clip.y = cropStartY;
		}
		else {
			// Checking if the width of the clip area lies outside the image.
			// If so, making the image width boundary as the clip width.
			if ((size.width + cropStartX) > img.getWidth())
				size.width = img.getWidth() - cropStartX;

			// Checking if the height of the clip area lies outside the image.
			// If so, making the image height boundary as the clip height.
			if ((size.height + cropStartY) > img.getHeight())
				size.height = img.getHeight() - cropStartY;

			// Setting up the clip are based on our clip area size adjustment
			clip = new Rectangle(size);
			clip.x = cropStartX;
			clip.y = cropStartY;

			isClipAreaAdjusted = true;
		}

		if (isClipAreaAdjusted)
			LOG.info("Crop Area Lied Outside The Image. Adjusted The Clip Rectangle\n");

		try {
			int w = clip.width;
			int h = clip.height;

			clipped = img.getSubimage(clip.x, clip.y, w, h);
			LOG.info("Image Cropped. New Image Dimension: " + clipped.getWidth() + "w X " + clipped.getHeight() + "h");
		}
		catch (RasterFormatException rfe) {
			LOG.error("Raster format error: " + rfe.getMessage());
			return null;
		}

		LOG.debug("Method cropImage() finished from UploadUtils");
		return clipped;
	}
}