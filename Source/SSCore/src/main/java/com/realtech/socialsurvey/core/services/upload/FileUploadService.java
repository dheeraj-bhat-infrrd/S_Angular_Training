package com.realtech.socialsurvey.core.services.upload;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


/**
 * Holds methods to upload file to the application server
 */
public interface FileUploadService
{

    /**
     * uploads image to server path specified
     * 
     * @throws InvalidInputException
     */
    public String uploadLogo( MultipartFile fileLocal, String logoName ) throws InvalidInputException;


    /**
     * Uploads profile image
     * @param file
     * @param imageName
     * @param preserveFileName sets the file name that is being sent in the parameter
     * @return
     * @throws InvalidInputException
     */
    public String uploadProfileImageFile( File file, String imageName, boolean preserveFileName ) throws InvalidInputException;


    /**
     * Method that returns a list of all the keys in a bucket.
     * @return
     */
    public List<String> listAllOjectsInBucket( AmazonS3 s3Client );


    /**
     * Method to delete the object with a particular key from the S3 bucket.
     * @param key
     * @throws InvalidInputException
     */
    public void deleteObjectFromBucket( String key, AmazonS3 s3Client ) throws InvalidInputException;


    /**
     * Method to create AmazonS3 client
     */
    public AmazonS3 createAmazonClient( String endpoint, String bucket );


    public void uploadFileAtDefautBucket( File file, String fileName ) throws NonFatalException;


    /**
     * Uploads the given file in the specified bucket
     * @param file
     * @param fileName
     * @param bucketName
     * @param expireImmediately
     * @throws NonFatalException
     */
    public void uploadFileAtSpecifiedBucket( File file, String fileName, String bucketName, boolean expireImmediately )
        throws NonFatalException;


    /**
     * Uploads logo file
     * @param file
     * @param imageName
     * @param preserveFileName sets the file name that is being sent in the parameter
     * @return
     * @throws InvalidInputException
     */
    public String uploadLogoImageFile( File file, String imageName, boolean preserveFileName ) throws InvalidInputException;

    /**
     * Uploads file to the old report directory and returns the URL of the uploaded file
     * 
     * @param file
     * @param fileName
     * @return
     * @throws NonFatalException
     */
    public String uploadOldReport( File file, String fileName ) throws NonFatalException;
    

    public void uploadReport( File file, String fileName ) throws NonFatalException;


    public void updateFileUploadRecord( FileUpload fileUpload ) throws InvalidInputException, Exception;

    
    public String uploadFileAtSurveyCsvBucket( File tempFile, String fileName ) throws NonFatalException;


    public void uploadFileAtDigestBucket( File file, String fileName ) throws NonFatalException;


    public String uploadFileAtFTPBucket( File file, String fileName ) throws NonFatalException, UnsupportedEncodingException;


    public void deleteObjectFromBucket( String key, String bucket ) throws InvalidInputException;


    public boolean deleteFileAtFTPBucket( String fileName ) throws NonFatalException;
}
