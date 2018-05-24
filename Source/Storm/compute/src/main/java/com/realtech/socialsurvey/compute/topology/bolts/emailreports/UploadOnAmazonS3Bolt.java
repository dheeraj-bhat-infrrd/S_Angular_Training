package com.realtech.socialsurvey.compute.topology.bolts.emailreports;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.ReportRequest;
import com.realtech.socialsurvey.compute.enums.ReportStatus;
import com.realtech.socialsurvey.compute.exception.UploadOnAmazonException;
import com.realtech.socialsurvey.compute.services.FailedMessagesService;
import com.realtech.socialsurvey.compute.services.impl.FailedMessagesServiceImpl;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.FileUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.*;

public class UploadOnAmazonS3Bolt extends BaseComputeBoltWithAck
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(UploadOnAmazonS3Bolt.class );
    private static final  String FILE_SEPARATOR = "/";

    private String amazonAccessKey;
    private String amazonSecretKey;

    @Override
    public void prepare( @SuppressWarnings ( "rawtypes") Map stormConf, TopologyContext context, OutputCollector collector ) {
        super.prepare( stormConf, context, collector );
        amazonAccessKey = (String) stormConf.get( AMAZON_ACCESS_KEY );
        amazonSecretKey = (String) stormConf.get( AMAZON_SECRET_KEY );
    }

    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer ) {
        outputFieldsDeclarer.declare(new Fields(  "isSuccess", "fileName", "fileUploadId", "reportRequest", "status"));
    }

    @Override
    public void executeTuple( Tuple input ) {
        //upload file on s3
        boolean success = false;
        String fileName = input.getStringByField(  "fileName" );
        byte[] fileBytes = (byte[]) input.getValueByField("fileBytes");
        File file = null;
        String fileNameInS3 = null;
        ReportRequest reportRequest = (ReportRequest) input.getValueByField("reportRequest");
        String bucket = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, AMAZON_BUCKET ).orElse( null );
        String reportBucketPath = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, AMAZON_REPORTS_BUCKET ).orElse( null );
        String endpoints = LocalPropertyFileHandler.getInstance()
            .getProperty(APPLICATION_PROPERTY_FILE, AMAZON_ENDPOINT ).orElse( null );
        String status = input.getStringByField("status");
        boolean isSuccess = input.getBooleanByField( "isSuccess");

        if ( isSuccess  && status.equals(ReportStatus.PROCESSED.getValue() ) ){
            String reportBucket = bucket + "/" + reportBucketPath;
            LOG.debug( "Uploading file: {}  to Amazon S3", fileName );
            //convert byte stream to file
            try {
                file = FileUtils.convertBytesToFile(fileBytes, fileName);
                if (fileBytes == null || file == null || !file.exists() || fileName == null || fileName.isEmpty()) {
                    LOG.error("Either file or file name is not present");
                    status = ReportStatus.FAILED.getValue();
                } else {
                    ObjectMetadata metadata = new ObjectMetadata();
                    PutObjectRequest putObjectRequest = new PutObjectRequest(reportBucket, fileName, file);
                    putObjectRequest.setMetadata(metadata);
                    putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead);


                    fileNameInS3 = endpoints + FILE_SEPARATOR + reportBucket + FILE_SEPARATOR
                        + URLEncoder.encode(fileName, "UTF-8");
                    AmazonS3 s3Client = createAmazonClient(endpoints, reportBucket);
                    PutObjectResult result = s3Client.putObject(putObjectRequest);
                    LOG.debug("Amazon Upload Etag: " + result.getETag());
                    LOG.debug("Uploaded {} to Amazon s3 ", fileName);
                    success = true;
                }
            } catch ( UploadOnAmazonException | UnsupportedEncodingException ex ) {
                LOG.error( "Exception while uploading file {} , on s3", fileName, ex );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertPermanentlyFailedReportRequest(reportRequest, ex);
                success = true;
                status = ReportStatus.FAILED.getValue();
            } catch (IOException e) {
                LOG.error( "IO  exception while converting bytes to file {}", file.getName(), e );
                FailedMessagesService failedMessagesService = new FailedMessagesServiceImpl();
                failedMessagesService.insertTemporaryFailedReportRequest(reportRequest);
                success = true;
                status = ReportStatus.FAILED.getValue();
            }

        }
        else if(isSuccess && (status.equals(ReportStatus.FAILED.getValue()) || status.equals(ReportStatus.BLANK.getValue())))
            success = true;

        LOG.info("Emitting tuple with success = {}, fileName = {}, fileUploadId = {}, status = {}", success, fileNameInS3,
            input.getValueByField( "fileUploadId" ), status);
        _collector.emit(input, Arrays.asList(success, fileNameInS3, input.getValueByField( "fileUploadId" ),
            reportRequest, status));

        //if the file is successfully uploaded , delete from the local
        if(file != null && file.exists()) {
            if(file.delete()) LOG.debug(" {} has been successfully deleted ", fileName);
            else LOG.error(" Unable to delete {} " , fileName);
        }
    }

    /**
     * Method to create AmazonS3 client
     */
    private AmazonS3 createAmazonClient( String endpoint, String bucket ) {
        LOG.debug( "Creating Amazon S3 Client" );
        AWSCredentials credentials = new BasicAWSCredentials( amazonAccessKey, amazonSecretKey );
        Region region = Region.getRegion( Regions.US_WEST_1 );

        AmazonS3 s3Client = new AmazonS3Client( credentials );
        s3Client.setRegion( region );
        s3Client.setEndpoint( endpoint );

        if ( !s3Client.doesBucketExist( bucket ) ) {
            throw new UploadOnAmazonException( "Bucket for file upload does not exists" );
        }
        LOG.debug( "Returning Amazon S3 Client" );
        return s3Client;
    }

    @Override
    public List<Object> prepareTupleForFailure() {
        return Arrays.asList(false, null, -1, null, null);
    }

}
