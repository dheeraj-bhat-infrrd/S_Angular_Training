package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.FatalException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.DisplayMessageConstants;
import com.realtech.socialsurvey.core.utils.EncryptionHelper;


@Component
public class CloudUploadServiceImpl implements FileUploadService
{

    private static final Logger LOG = LoggerFactory.getLogger( CloudUploadServiceImpl.class );
    private static final String CACHE_PUBLIC = "public";
    private static final String CACHE_MAX_AGE = "max-age=";
    private static final String CACHE_VALUE_SEPERATOR = ", ";

    @Autowired
    private EncryptionHelper encryptionHelper;

    @Autowired
    private GenericDao<FileUpload, Long> fileUploadDao;

    @Autowired
    private UploadUtils uploadUtils;

    @Value ( "${AMAZON_ENDPOINT}")
    private String endpoint;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String logoBucket;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String imageBucket;

    @Value ( "${AMAZON_BUCKET}")
    private String bucket;

    @Value ( "${AMAZON_SURVEY_CSV_BUCKET}")
    private String amazonSurveyCsvBucket;

    @Value ( "${AMAZON_ENV_PREFIX}")
    private String envPrefix;

    @Value ( "${AMAZON_ACCESS_KEY}")
    private String accessKey;

    @Value ( "${AMAZON_SECRET_KEY}")
    private String secretKey;

    @Value ( "${AMAZON_REPORTS_BUCKET}")
    private String reportBucket;

    @Value ( "${AMAZON_OLD_REPORTS_BUCKET}")
    private String oldReportBucket;

    @Value ( "${AMAZON_DIGEST_BUCKET}")
    private String digestBucket;

    @Value ( "${AMAZON_FTP_BUCKET}")
    private String ftpBucket;


    @Override
    public String uploadProfileImageFile( File file, String imageName, boolean preserveFileName ) throws InvalidInputException
    {
        LOG.info( "Method uploadProfileImageFile inside AmazonUploadServiceImpl called for image {}", imageName );
        try {
            return uploadImage( file, imageName, bucket + CommonConstants.FILE_SEPARATOR + imageBucket, preserveFileName );
        } catch ( InvalidInputException e ) {
            LOG.error( "IOException occured while reading file. Reason : " + e.getMessage(), e );
            throw new FatalException( "IOException occured while reading file. Reason : " + e.getMessage(), e );
        }
    }


    @Override
    public String uploadLogoImageFile( File file, String imageName, boolean preserveFileName ) throws InvalidInputException
    {
        LOG.info( "Method uploadLogoImageFile inside AmazonUploadServiceImpl called" );
        try {
            return uploadImage( file, imageName, bucket + CommonConstants.FILE_SEPARATOR + logoBucket, preserveFileName );
        } catch ( InvalidInputException e ) {
            LOG.error( "IOException occured while reading file. Reason : " + e.getMessage(), e );
            throw new FatalException( "IOException occured while reading file. Reason : " + e.getMessage(), e );
        }
    }


    @Override
    public String uploadLogo( MultipartFile fileLocal, String logoName ) throws InvalidInputException
    {
        LOG.info( "Method fileUploadHandler inside AmazonUploadServiceImpl called" );

        if ( !fileLocal.isEmpty() ) {
            try {
                File convFile = new File( CommonConstants.IMAGE_NAME );
                fileLocal.transferTo( convFile );

                return uploadImage( convFile, logoName, bucket + CommonConstants.FILE_SEPARATOR + logoBucket, false );
            } catch ( IOException e ) {
                LOG.error( "IOException occured while reading file. Reason : " + e.getMessage(), e );
                throw new FatalException( "IOException occured while reading file. Reason : " + e.getMessage(), e );
            }
        } else {
            LOG.error( "Method fileUploadHandler inside AmazonUploadServiceImpl failed to upload" );
            throw new InvalidInputException( "Upload failed: " + logoName + " because the file was empty",
                DisplayMessageConstants.INVALID_LOGO_FILE );
        }
    }


    @Override
    public String uploadOldReport( File file, String fileName ) throws NonFatalException
    {
        String bucketString = bucket + CommonConstants.FILE_SEPARATOR + oldReportBucket;

        uploadFileAtSpecifiedBucket( file, fileName, bucketString, false );

        return endpoint + CommonConstants.FILE_SEPARATOR + bucketString + CommonConstants.FILE_SEPARATOR + fileName;
    }


    @Override
    public void uploadReport( File file, String fileName ) throws NonFatalException
    {
        uploadFileAtSpecifiedBucket( file, fileName, bucket + CommonConstants.FILE_SEPARATOR + reportBucket, false );
    }


    @Override
    public void uploadFileAtDefautBucket( File file, String fileName ) throws NonFatalException
    {
        uploadFile( file, fileName, bucket, false );
    }


    @Override
    public void uploadFileAtDigestBucket( File file, String fileName ) throws NonFatalException
    {
        uploadFile( file, fileName, bucket + "/" + digestBucket, false );
    }


    @Override
    public String uploadFileAtFTPBucket( File file, String fileName ) throws NonFatalException, UnsupportedEncodingException
    {
        uploadFile( file, fileName, bucket + "/" + ftpBucket, false );
        return endpoint + CommonConstants.FILE_SEPARATOR + bucket + CommonConstants.FILE_SEPARATOR + ftpBucket
            + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( fileName, CommonConstants.UTF_8_ENCODING );
    }


    @Override
    public boolean deleteFileAtFTPBucket( String fileName ) throws NonFatalException
    {
        deleteObjectFromBucket( fileName, bucket + "/" + ftpBucket );
        return true;
    }


    @Override
    public void uploadFileAtSpecifiedBucket( File file, String fileName, String bucketName, boolean expireImmediately )
        throws NonFatalException
    {
        LOG.info( "Uploading file : " + fileName + " at bucket: " + bucketName );
        uploadFile( file, fileName, bucketName, expireImmediately );
    }


    private String uploadImage( File convFile, String logoName, String bucket, boolean preserveFileName )
        throws InvalidInputException
    {
        LOG.debug( "Uploading file. preserving file name: " + preserveFileName );
        uploadUtils.validateFile( convFile );

        String fileName = null;
        StringBuilder amazonFileName = null;

        if ( !preserveFileName ) {
            amazonFileName = new StringBuilder( envPrefix ).append( CommonConstants.SYMBOL_HYPHEN );

            amazonFileName.append( encryptionHelper.encryptSHA512( logoName + ( System.currentTimeMillis() ) ) );
            if ( logoName.endsWith( ".jpg" ) || logoName.endsWith( ".JPG" ) ) {
                amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "jpg" );
            } else if ( logoName.endsWith( ".jpeg" ) || logoName.endsWith( ".JPEG" ) ) {
                amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "jpeg" );
            } else if ( logoName.endsWith( ".png" ) || logoName.endsWith( ".PNG" ) ) {
                amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "png" );
            }
            fileName = amazonFileName.toString();
        } else {
            fileName = logoName;
        }

        try {
            uploadFile( convFile, fileName, bucket, false );
        } catch ( NonFatalException e ) {
            throw new InvalidInputException( "Could not upload file: " + e.getMessage(), e );
        }

        LOG.info( "Amazon file Name: " + fileName );
        return fileName;
    }


    private void uploadFile( File file, String fileName, String bucket, boolean expireImmediately ) throws NonFatalException
    {
        LOG.info( "Uploading file: " + fileName + " to Amazon S3" );
        if ( file == null || !file.exists() || fileName == null || fileName.isEmpty() ) {
            throw new InvalidInputException( "Either file or file name is not present" );
        }

        Calendar cal = Calendar.getInstance();
        cal.set( Calendar.DATE, 19 );
        cal.set( Calendar.MONTH, Calendar.JANUARY );
        cal.set( Calendar.YEAR, 2038 );
        cal.set( Calendar.HOUR_OF_DAY, 23 );
        cal.set( Calendar.MINUTE, 59 );
        cal.set( Calendar.SECOND, 59 );
        cal.set( Calendar.MILLISECOND, 0 );

        int maxAgeValue = (int) ( ( cal.getTimeInMillis() - System.currentTimeMillis() ) / 1000 );

        ObjectMetadata metadata = new ObjectMetadata();
        if ( expireImmediately ) {
            metadata.setCacheControl( CACHE_PUBLIC );
            metadata.setExpirationTime( new Date( System.currentTimeMillis() ) );
        } else {
            metadata.setCacheControl( CACHE_MAX_AGE + maxAgeValue + CACHE_VALUE_SEPERATOR + CACHE_PUBLIC );
            // TODO: set expiration date properly later
            metadata.setExpirationTime( cal.getTime() );
            metadata.setHeader( "Expires", new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z" ).format( cal.getTime() ) );
        }
        PutObjectRequest putObjectRequest = new PutObjectRequest( bucket, fileName, file );
        putObjectRequest.setMetadata( metadata );
        putObjectRequest.withCannedAcl( CannedAccessControlList.PublicRead );

        AmazonS3 s3Client = createAmazonClient( endpoint, bucket );

        PutObjectResult result = s3Client.putObject( putObjectRequest );

        LOG.info( "Amazon Upload Etag: " + result.getETag() );
        LOG.info( "Uploaded " + fileName + " to Amazon s3" );
    }


    /**
     * Method that returns a list of all the keys in a bucket.
     * 
     * @return
     */
    @Override
    public List<String> listAllOjectsInBucket( AmazonS3 s3Client )
    {
        List<String> fileList = new ArrayList<>();
        LOG.info( "Listing all objects in bucket : " + bucket );

        try {
            LOG.debug( "Creating the list objects request" );
            ListObjectsRequest request = new ListObjectsRequest();
            request.setBucketName( bucket );

            LOG.debug( "The environment prefix used is : " + envPrefix );
            request.setPrefix( envPrefix );
            ObjectListing listing = null;

            LOG.debug( "Preparing the arraylist of all the keys currently in the bucket : " + bucket );
            do {
                LOG.debug( "Fetching the list of objects to be added to the list" );
                listing = s3Client.listObjects( request );
                for ( S3ObjectSummary objectSummary : listing.getObjectSummaries() ) {
                    LOG.debug( "Adding key to the list. Key : " + objectSummary.getKey() );
                    fileList.add( objectSummary.getKey() );
                }
                LOG.debug( "Setting marker to fetch next batch of objects" );
                request.setMarker( listing.getNextMarker() );
            } while ( listing.isTruncated() );
            LOG.debug( "List successfully created" );
        } catch ( AmazonClientException e ) {
            LOG.error( "Amazon Client Exception caught while fetching list of keys : message : " + e.getMessage() );
            throw new FatalException(
                "Amazon Client Exception caught while fetching list of keys : message : " + e.getMessage(), e );
        }

        LOG.info( "Returning the list." );
        return fileList;
    }


    /**
     * Method to delete the object with a particular key from the S3 bucket.
     * 
     * @param key
     * @throws InvalidInputException
     */
    @Override
    public void deleteObjectFromBucket( String key, AmazonS3 s3Client ) throws InvalidInputException
    {
        if ( key == null || key.isEmpty() ) {
            LOG.error( "key parameter sent to deleteObjectFromBucket is null or empty!" );
            throw new InvalidInputException( "key parameter sent to deleteObjectFromBucket is null or empty!" );
        }

        try {
            LOG.info( "Amazon Client created. Now sending a request to delete the object with key : " + key );
            s3Client.deleteObject( bucket, key );
            LOG.info( "Object with key : " + key + " deleted from bucket : " + bucket );

        } catch ( AmazonClientException e ) {
            LOG.error( "AmazonClientException caught while deleting object with key : " + key );
            throw new FatalException( "AmazonClientException caught while deleting object with key : " + key );
        }
    }


    /**
     * Method to delete a file in a specified bucket
     * 
     * @param key
     * @param bucket
     * @throws InvalidInputException
     */
    @Override
    public void deleteObjectFromBucket( String key, String bucket ) throws InvalidInputException
    {
        LOG.debug( "method deleteObjectFromBucket() called" );
        if ( StringUtils.isEmpty( key ) ) {
            LOG.error( "file name is not specified" );
            throw new InvalidInputException( "file name is not specified" );
        } else if ( StringUtils.isEmpty( bucket ) ) {
            LOG.error( "target bucket is not specified" );
            throw new InvalidInputException( "target bucket is not specified" );
        }

        try {
            createAmazonClient( endpoint, bucket ).deleteObject( bucket, key );
        } catch ( AmazonClientException e ) {
            LOG.error( "AmazonClientException caught while deleting object with key : {}", key );
            throw new FatalException( "AmazonClientException caught while deleting object with key : " + key );
        }
        LOG.debug( "method deleteObjectFromBucket() finished" );
    }


    /**
     * Method to create AmazonS3 client
     */
    public AmazonS3 createAmazonClient( String endpoint, String bucket )
    {
        LOG.debug( "Creating Amazon S3 Client" );
        AWSCredentials credentials = new BasicAWSCredentials( accessKey, secretKey );
        Region region = Region.getRegion( Regions.US_WEST_1 );

        AmazonS3 s3Client = new AmazonS3Client( credentials );
        s3Client.setRegion( region );
        s3Client.setEndpoint( endpoint );

        if ( !s3Client.doesBucketExist( bucket ) ) {
            throw new FatalException( "Bucket for Logo upload does not exists" );
        }
        LOG.debug( "Returning Amazon S3 Client" );
        return s3Client;
    }


    @Transactional
    @Override
    public void updateFileUploadRecord( FileUpload fileUpload ) throws InvalidInputException
    {
        LOG.info( "Check if files need to be uploaded" );
        if ( fileUpload == null ) {
            throw new InvalidInputException( "File upload is null" );
        }
        fileUploadDao.update( fileUpload );
    }


    /*
     * method to upload a csv file filled with customer details and return the URI
     * 
     * @param MultiPartFile
     * @param String
     * @return String
     */
    @Override
    public String uploadFileAtSurveyCsvBucket( File file, String fileName ) throws NonFatalException
    {
        LOG.debug( "Method uploadFileAtSurveyCsvBucket called" );

        if ( file != null && StringUtils.isNotEmpty( fileName ) ) {
            try {

                // uploading in social survey's application bucket, inside "amazonSurveyCsvBucket" folder
                uploadFile( file, fileName, bucket + CommonConstants.FILE_SEPARATOR + amazonSurveyCsvBucket, false );

                return endpoint + CommonConstants.FILE_SEPARATOR + bucket + CommonConstants.FILE_SEPARATOR
                    + amazonSurveyCsvBucket + CommonConstants.FILE_SEPARATOR + URLEncoder.encode( fileName, "UTF-8" );

            } catch ( IOException e ) {
                LOG.error( "IOException occured while reading file. Reason : " + e.getMessage(), e );
                throw new FatalException( "IOException occured while reading file. Reason : " + e.getMessage(), e );
            }
        } else {
            LOG.error( "Method fuploadFileAtSurveyCsvBucket failed to upload" );
            throw new InvalidInputException( "Upload failed: because the file or file name was empty" );
        }
    }


}