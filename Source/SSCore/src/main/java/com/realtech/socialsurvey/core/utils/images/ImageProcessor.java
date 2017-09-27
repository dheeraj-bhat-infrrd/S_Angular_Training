package com.realtech.socialsurvey.core.utils.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.utils.images.impl.ImageProcessingException;

/**
 * Handles processing of the image
 *
 */
public interface ImageProcessor {

	public static final int THUMBNAIL_WIDTH = 200;
	public static final int THUMBNAIL_HEIGHT = 200;
	
	/**
	 * Processes and updates the provided
	 * @param imageFileName
	 * @param imageType
	 * @return
	 * @throws ImageProcessingException
	 * @throws InvalidInputException
	 */
	public Map<String,String> processImage(String imageFileName, String imageType)  throws ImageProcessingException, InvalidInputException;
	
	/**
	 * Processed the image and return a buffered image
	 * @param image
	 * @param width
	 * @param length
	 * @param imageExtension
	 * @return
	 * @throws ImageProcessingException
	 * @throws InvalidInputException
	 */
	public File processImageForThumbnail(BufferedImage image, int width, int length, String imageExtension) throws ImageProcessingException, InvalidInputException;
	
	/**
	 * Gets the image file
	 * @param imageFileName
	 * @return
	 * @throws ImageProcessingException
	 * @throws InvalidInputException
	 */
	public BufferedImage getImageFromCloud(String imageFileName) throws ImageProcessingException, InvalidInputException;
	
	/**
	 * Writes image to the destination
	 * @param destFileName
	 * @param image
	 * @param imageType
	 * @return uploaded image destination
	 * @throws ImageProcessingException
	 * @throws InvalidInputException
	 */
	public String writeImage(String destFileName, File image, String imageType) throws ImageProcessingException, InvalidInputException;

   

    public File processImageAsRectangular( BufferedImage image,  int width, int height, String imageExtension ) throws ImageProcessingException,
        InvalidInputException;
	
	
}
