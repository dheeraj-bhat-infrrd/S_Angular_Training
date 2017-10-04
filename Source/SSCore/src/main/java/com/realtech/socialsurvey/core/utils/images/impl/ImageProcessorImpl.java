package com.realtech.socialsurvey.core.utils.images.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

	@Value("${RECTANGULAR_THUMBNAIL_IMG_PATH}")
    private String rectangularThumbnailImgPath;
	
	
	@Autowired
	private FileUploadService fileUploadService;
	
	
	private static final String THUMBNAIL_APPENDER = "-th.";
	private static final String RECTANGULAR_THUMBNAIL_APPENDER = "-rctTh.";
	
	
	@Override
	public Map<String,String> processImage(String imageFileName, String imageType)
			throws ImageProcessingException, InvalidInputException {
        if ( imageFileName == null || imageFileName.isEmpty() ) {
            LOG.error( "Image File Name is empty" );
            throw new InvalidInputException( "Image File Name is empty" );
        }
        if ( imageType == null || imageType.isEmpty() ) {
            LOG.error( "Image Type is empty" );
            throw new InvalidInputException( "Image Type is empty" );
        }
        
        Map<String, String> processedImgs = new HashMap<String, String>();
        
		LOG.info("Processing images for " + imageFileName);
		// get the image
		BufferedImage sourceImage = getImageFromCloud(imageFileName);
		// process thumbnail
		LOG.info("Processing image for thumbnail " + imageFileName);
		String extension = imageFileName.substring(imageFileName.lastIndexOf(".") + 1).toLowerCase();
		if(imageFileName.contains( "media.licdn.com" )){
		    extension = "jpg";
		}
		
		//generate Thumbnail image
		File processedImage = processImageForThumbnail(sourceImage, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, extension);
		// name the file that needs to be uploaded
		String thumbnailImageName = getThumbnailImageName(imageFileName, extension ,THUMBNAIL_APPENDER);
		String thumbnailFileName = writeImage(thumbnailImageName, processedImage, imageType);
		deleteTempFile(processedImage);
		
		
		
	    File processedRectangularImg = processImageAsRectangular(sourceImage, RECTANGULAR_THUMBNAIL_WIDTH, RECTANGULAR_THUMBNAIL_HEIGHT, extension);
		String linkedInThumbnailImageName = getThumbnailImageName(imageFileName, extension ,RECTANGULAR_THUMBNAIL_APPENDER);
        String linkedInThumbnailFileName = writeImage(linkedInThumbnailImageName, processedRectangularImg, imageType);
        deleteTempFile(processedRectangularImg);
		
        processedImgs.put( CommonConstants.SQUARE_THUMBNAIL, thumbnailFileName );
        processedImgs.put( CommonConstants.RECTANGULAR_THUMBNAIL, linkedInThumbnailFileName );
       
		return processedImgs;
	}
	
	

	@Override
	public File processImageForThumbnail(BufferedImage image, int width, int height, String imageExtension) throws ImageProcessingException,
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
    public File processImageAsRectangular(BufferedImage image, int width, int height, String imageExtension) throws ImageProcessingException,
            InvalidInputException {
	    LOG.info( "Method processImageAsRectangular started" );
        if (image == null || imageExtension == null || imageExtension.isEmpty()) {
            LOG.error("Could not find file to process");
            throw new InvalidInputException("Could not find file to process");
        }
        BufferedImage scaledImage = null;
        scaledImage = Scalr.resize(image, Method.SPEED, Mode.AUTOMATIC, width, height);
        File processedFile = null;
        
        BufferedImage newRectangularImage = null;
        try {
            newRectangularImage = getBackgroundImageForRectangularThumbnail();
        } catch ( IOException e ) {
            LOG.error("Error while getting black image for rectangular thumbnail", e);
            throw new ImageProcessingException("Error while processing image.", e);
        }
        
        int xCordiate = (newRectangularImage.getWidth() - scaledImage.getWidth())/2 ;
        int yCordinate = (newRectangularImage.getHeight() - scaledImage.getHeight())/2;
        
        Graphics2D graphics = newRectangularImage.createGraphics();
        graphics.drawImage(scaledImage, xCordiate , yCordinate , null);
        graphics.dispose();
        
        processedFile = new File(CommonConstants.TEMP_FOLDER + CommonConstants.FILE_SEPARATOR + String.valueOf(System.currentTimeMillis()) + "-"
                  + "." + imageExtension);
        try {
            LOG.debug( "File path of processed file : " + processedFile.getAbsolutePath() );
            processedFile.createNewFile();
            if (processedFile.exists()) {
                ImageIO.write(newRectangularImage, imageExtension, processedFile);
            }
        }
        catch (IOException e) {
            LOG.error("Error while processing image.", e);
            throw new ImageProcessingException("Error while processing image.", e);
        }
        
        LOG.info( "Method processImageAsRectangular finished" );

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


	/**
	 * 
	 * @param originalImageName
	 * @param extension
	 * @return
	 * @throws InvalidInputException
	 */
	private String getThumbnailImageName( String originalImageName, String extension , String thumbnailAppender ) throws InvalidInputException
    {
        LOG.debug( "Getting thumbnail name for " + originalImageName );
        String thumbnailImg = "";
        try {
            if(thumbnailImg.contains( "." + extension  )){
                thumbnailImg = originalImageName.substring( originalImageName.lastIndexOf( "/" ) + 1,
                    originalImageName.lastIndexOf( "." + extension ) )
                    + thumbnailAppender + extension;
            }else{
                thumbnailImg = originalImageName.substring( originalImageName.lastIndexOf( "/" ) + 1,
                    originalImageName.length() -1 )
                    + thumbnailAppender + extension;
            }
            
        } catch ( StringIndexOutOfBoundsException e ) {
            throw new InvalidInputException( "Error. Unable to generate thumbnail image name for fileName : "
                + originalImageName + ".", e );
        }
        return thumbnailImg;
    }

    
	
	
    /**
     * 
     * @param file
     */
	void deleteTempFile(File file) {
		LOG.debug("Deleting file: " + file.getAbsolutePath());
		FileUtils.deleteQuietly(file);
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private  BufferedImage getBackgroundImageForRectangularThumbnail() throws IOException{
	    LOG.info( "method getBackgroundImageForRectangularThumbnail started" );
	    BufferedImage image = ImageIO.read(getClass().getClassLoader().getResource(rectangularThumbnailImgPath));
	       LOG.info( "method getBackgroundImageForRectangularThumbnail finished" );
	    return image;
	}
	
	
}
