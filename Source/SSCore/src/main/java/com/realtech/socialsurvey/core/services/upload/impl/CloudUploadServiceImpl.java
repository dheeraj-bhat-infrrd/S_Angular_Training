package com.realtech.socialsurvey.core.services.upload.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.realtech.socialsurvey.core.commons.CommonConstants;
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
    private UploadUtils uploadUtils;

    @Value ( "${AMAZON_ENDPOINT}")
    private String endpoint;

    @Value ( "${AMAZON_LOGO_BUCKET}")
    private String logoBucket;

    @Value ( "${AMAZON_IMAGE_BUCKET}")
    private String imageBucket;

    @Value ( "${AMAZON_BUCKET}")
    private String bucket;

    @Value ( "${AMAZON_ENV_PREFIX}")
    private String envPrefix;

    @Value ( "${AMAZON_ACCESS_KEY}")
    private String accessKey;

    @Value ( "${AMAZON_SECRET_KEY}")
    private String secretKey;


    @Override
    public String fileUploadHandler( File file, String imageName ) throws InvalidInputException
    {
        LOG.info( "Method fileUploadHandler inside AmazonUploadServiceImpl called" );
        try {
            return uploadImage( file, imageName, bucket + CommonConstants.FILE_SEPARATOR + imageBucket );
        } catch ( InvalidInputException e ) {
            LOG.error( "IOException occured while reading file. Reason : " + e.getMessage(), e );
            throw new FatalException( "IOException occured while reading file. Reason : " + e.getMessage(), e );
        }
    }


    @Override
    public String fileUploadHandler( MultipartFile fileLocal, String logoName ) throws InvalidInputException
    {
        LOG.info( "Method fileUploadHandler inside AmazonUploadServiceImpl called" );

        if ( !fileLocal.isEmpty() ) {
            try {
                File convFile = new File( CommonConstants.IMAGE_NAME );
                fileLocal.transferTo( convFile );

                return uploadImage( convFile, logoName, bucket + CommonConstants.FILE_SEPARATOR + logoBucket );
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
    public void uploadFileAtDefautBucket( File file, String fileName ) throws NonFatalException
    {
        uploadFile( file, fileName, bucket );
    }


    private String uploadImage( File convFile, String logoName, String bucket ) throws InvalidInputException
    {
        uploadUtils.validateFile( convFile );

        StringBuilder amazonFileName = new StringBuilder( envPrefix ).append( CommonConstants.SYMBOL_HYPHEN );

        amazonFileName.append( encryptionHelper.encryptSHA512( logoName + ( System.currentTimeMillis() ) ) );
        if ( logoName.endsWith( ".jpg" ) || logoName.endsWith( ".JPG" ) ) {
            amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "jpg" );
        } else if ( logoName.endsWith( ".jpeg" ) || logoName.endsWith( ".JPEG" ) ) {
            amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "jpeg" );
        } else if ( logoName.endsWith( ".png" ) || logoName.endsWith( ".PNG" ) ) {
            amazonFileName.append( CommonConstants.SYMBOL_FULLSTOP + "png" );
        }

        try {
            uploadFile( convFile, amazonFileName.toString(), bucket );
        } catch ( NonFatalException e ) {
            throw new InvalidInputException( "Could not upload file: " + e.getMessage(), e );
        }

        LOG.info( "Amazon file Name: " + amazonFileName.toString() );
        return amazonFileName.toString();
    }


    private void uploadFile( File file, String fileName, String bucket ) throws NonFatalException
    {
        LOG.info( "Uploading file: " + fileName + " to Amazon S3" );
        if ( file == null || !file.exists() || fileName == null || fileName.isEmpty() ) {
            throw new InvalidInputException( "Either file or file name is not present" );
        }

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 19);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 2038);
        
        int maxAgeValue = (int) ( ( cal.getTimeInMillis() - System.currentTimeMillis() ) / 1000 ) ;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setCacheControl( CACHE_MAX_AGE + maxAgeValue + CACHE_VALUE_SEPERATOR + CACHE_PUBLIC );
        // TODO: set expiration date properly later
        metadata.setExpirationTime( cal.getTime() );
        PutObjectRequest putObjectRequest = new PutObjectRequest( bucket, fileName, file );
        putObjectRequest.setMetadata( metadata );
        putObjectRequest.withCannedAcl( CannedAccessControlList.PublicRead );

        AmazonS3 s3Client = createAmazonClient( endpoint, bucket );
        addExpirationTime( s3Client, (int) ( maxAgeValue / ( 24 * 60 * 60 ) ), bucket );

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
            throw new FatalException( "Amazon Client Exception caught while fetching list of keys : message : "
                + e.getMessage(), e );
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


    private void addExpirationTime( AmazonS3 s3Client, int days, String bucket )
    {

        BucketLifecycleConfiguration.Rule picExpirationRule = new BucketLifecycleConfiguration.Rule()
            .withId( "PicExpirationRule" ).withExpirationInDays(days).withStatus( BucketLifecycleConfiguration.ENABLED.toString() );

        BucketLifecycleConfiguration configuration = new BucketLifecycleConfiguration().withRules( Arrays
            .asList( picExpirationRule ) );

        s3Client.setBucketLifecycleConfiguration( bucket, configuration );

    }

}