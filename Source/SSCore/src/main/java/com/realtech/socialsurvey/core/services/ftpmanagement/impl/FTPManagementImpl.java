package com.realtech.socialsurvey.core.services.ftpmanagement.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.realtech.socialsurvey.core.api.builder.SSApiBatchIntegrationBuilder;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.TransactionSourceFtp;
import com.realtech.socialsurvey.core.entities.ftp.FtpUploadRequest;
import com.realtech.socialsurvey.core.entities.integration.stream.FailedStreamMessage;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessConfig;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessResponse;
import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteAccessAuthentication;
import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteFileDelivery;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.exception.remoteaccess.RemoteAccessException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiConnectException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiException;
import com.realtech.socialsurvey.core.integration.stream.StreamApiIntegrationBuilder;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.ftpmanagement.FTPManagement;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.stream.StreamMessagesService;
import com.realtech.socialsurvey.core.services.upload.FileUploadService;
import com.realtech.socialsurvey.core.utils.remoteaccess.RemoteAccessUtils;

import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@Service
public class FTPManagementImpl implements FTPManagement
{
    private static final Logger LOG = LoggerFactory.getLogger( FTPManagementImpl.class );

    @Autowired
    private BatchTrackerService batchTrackerService;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;

    @Autowired
    private SSApiBatchIntegrationBuilder ssApiBatchIntergrationBuilder;

    @Autowired
    private StreamApiIntegrationBuilder streamApiIntergrationBuilder;

    @Autowired
    private RemoteAccessUtils remoteAccessUtils;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private EmailServices emailServices;

    @Autowired
    private StreamMessagesService streamMessagesService;


    @Value ( "${FTP_SERVEY_KEY_PATH}")
    private String rsaKeyPath;

    @Value ( "${FTP_BATCH_TEMP_FOLDER}")
    private String ftpBatchFolder;

    @Value ( "${FTP_SERVER_ONE_HOST}")
    private String ftpServerOneHost;

    @Value ( "${FTP_SERVER_ONE_USERNAME}")
    private String ftpServerOneUsername;

    @Value ( "${FTP_SERVER_TWO_HOST}")
    private String ftpServerTwoHost;

    @Value ( "${FTP_SERVER_TWO_USERNAME}")
    private String ftpServerTwoUsername;

    @Value ( "${FTP_SERVER_ONE_PROCESSED_ROOT}")
    private String ftpServerOneProcessedRoot;

    @Value ( "${FTP_SERVER_TWO_PROCESSED_ROOT}")
    private String ftpServerTwoProcessedRoot;

    @Value ( "${FTP_SERVER_ONE_OS}")
    private String ftpServerOneOs;

    @Value ( "${FTP_SERVER_TWO_OS}")
    private String ftpServerTwoOs;

    public static final int FTP_FILE_UPLOADER_START_INDEX = 0;
    public static final int FTP_FILE_UPLOADER_BATCH_SIZE = 10;
    public static final String PROCESSED_FILE_NAME_SEPERATOR = "_";
    public static final String FTP_PROCESSED_FILE_NAME_ROOT = "FTP_";
    public static final String FTP_HTML_LINE_FEED = "<br/>";


    /**
     * 
     */
    @Override
    public void startFTPFileProcessing()
    {
        try {
            // update last start time
            batchTrackerService.getLastRunEndTimeAndUpdateLastStartTimeByBatchType(
                CommonConstants.BATCH_TYPE_FTP_FILE_UPLOADER, CommonConstants.BATCH_NAME_FTP_FILE_UPLOADER );

            LOG.info( "Starting startFTPFileProcessing" );

            List<TransactionSourceFtp> activeFtpConnections = null;
            int startIndex = FTP_FILE_UPLOADER_START_INDEX;
            int batchSize = FTP_FILE_UPLOADER_BATCH_SIZE;

            do {

                // get the list of active FTP configurations
                activeFtpConnections = getActiveFtpConnectionsInBatch( startIndex, batchSize );

                if ( activeFtpConnections != null ) {
                    for ( TransactionSourceFtp activeFtpConnection : activeFtpConnections ) {
                        checkOutAndProcessFtpConnection( activeFtpConnection );
                    }
                } else {
                    LOG.warn( "Unable to parse the active ftp connections list" );
                    throw new InvalidInputException( "Unable to parse the active ftp connections list" );
                }

                startIndex += batchSize;

            } while ( !activeFtpConnections.isEmpty() && activeFtpConnections.size() >= batchSize );


            // updating last run time for batch in database
            batchTrackerService.updateLastRunEndTimeByBatchType( CommonConstants.BATCH_TYPE_FTP_FILE_UPLOADER );
            LOG.info( "Completed startFTPFileProcessing" );
        } catch ( Exception unhandledError ) {
            LOG.error( "Error in startFTPFileProcessing", unhandledError );
            try {
                // update batch tracker with error message
                batchTrackerService.updateErrorForBatchTrackerByBatchType( CommonConstants.BATCH_TYPE_FTP_FILE_UPLOADER,
                    unhandledError.getMessage() );
                // send report bug mail to administrator
                batchTrackerService.sendMailToAdminRegardingBatchError( CommonConstants.BATCH_NAME_FTP_FILE_UPLOADER,
                    System.currentTimeMillis(), unhandledError );
            } catch ( NoRecordsFetchedException | InvalidInputException errorWhileHandlingError ) {
                LOG.error( "Error while updating error message in startFTPFileProcessing" );
            } catch ( UndeliveredEmailException unableToDeliverMail ) {
                LOG.error( "Error while sending report excption mail to admin" );
            }
        }
    }


    /**
     * 
     * @param startIndex
     * @param batchSize
     * @return
     */
    private List<TransactionSourceFtp> getActiveFtpConnectionsInBatch( int startIndex, int batchSize )
    {
        LOG.debug( "method getActiveFtpConnectionsInBatch() running" );

        // make an SSApi call to get active FTP connections
        Response ftpConnectionListResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .getFtpConnections( CommonConstants.STATUS_ACTIVE_MONGO, startIndex, batchSize );

        // get the response body
        String ftpConnectionListString = StringEscapeUtils.unescapeJava( ftpConnectionListResponse != null
            ? new String( ( (TypedByteArray) ftpConnectionListResponse.getBody() ).getBytes() )
            : null );

        return new Gson().fromJson( StringUtils.strip( ftpConnectionListString, "\"" ),
            new TypeToken<List<TransactionSourceFtp>>() {}.getType() );
    }


    /**
     * 
     */
    @Override
    public List<TransactionSourceFtp> getFtpConnections( String status, int startIndex, int batchSize,
        boolean doHideSensitiveInfo )
    {
        LOG.info( "method getFtpConnections() called" );
        List<TransactionSourceFtp> ftpConnections = organizationUnitSettingsDao.getFtpConnectionsForCompany( status, startIndex,
            batchSize );

        if ( doHideSensitiveInfo ) {
            for ( TransactionSourceFtp transactionSourceFtp : ftpConnections ) {
                transactionSourceFtp.setUsername( null );
                transactionSourceFtp.setPassword( null );
                transactionSourceFtp.setFileHeaderMapper( null );
            }
        }

        LOG.info( "method getFtpConnections() finished" );
        return ftpConnections;
    }


    /**
     * 
     * @param activeFtpConnection
     * @return
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    private boolean checkOutAndProcessFtpConnection( TransactionSourceFtp activeFtpConnection )
        throws InvalidInputException, UndeliveredEmailException
    {
        LOG.trace( "looking for files in the ftp server" );

        long companyId = 0;
        long ftpId = 0;

        try {
            validateFtpConnection( activeFtpConnection );

            companyId = activeFtpConnection.getCompanyId();
            ftpId = activeFtpConnection.getFtpId();
            String host = null;
            String userName = null;
            String processedrootFolder = null;
            String os = null;

            //determine host, user name and processed root folder for the FTP server
            if ( CommonConstants.FTP_SERVER_ONE.equals( activeFtpConnection.getFtpServerLabel() ) ) {
                host = ftpServerOneHost;
                userName = ftpServerOneUsername;
                processedrootFolder = ftpServerOneProcessedRoot;
                os = ftpServerOneOs;
            } else if ( CommonConstants.FTP_SERVER_TWO.equals( activeFtpConnection.getFtpServerLabel() ) ) {
                host = ftpServerTwoHost;
                userName = ftpServerTwoUsername;
                processedrootFolder = ftpServerTwoProcessedRoot;
                os = ftpServerTwoOs;
            }

            // remove file separator at the end if it exists
            String directoryPath = activeFtpConnection.getFtpDirectoryPath().endsWith( CommonConstants.FILE_SEPARATOR )
                ? StringUtils.substringBeforeLast( activeFtpConnection.getFtpDirectoryPath(), CommonConstants.FILE_SEPARATOR )
                : activeFtpConnection.getFtpDirectoryPath();

            // build the processed folder path for this company     
            String processedFolder = processedrootFolder + CommonConstants.FILE_SEPARATOR
                + StringUtils.substringAfterLast( directoryPath, CommonConstants.FILE_SEPARATOR );


            List<String> fileNames = mineForActiveFtpUploads( host, userName, directoryPath );

            if ( fileNames == null || fileNames.isEmpty() ) {
                LOG.info( "No files found to upload from FTP server, aborting" );
                return false;
            }

            for ( String fileName : fileNames ) {
                extractAndQueueFtpFile( activeFtpConnection.getCompanyId(), host, userName, directoryPath, processedFolder,
                    ftpId, fileName, os );
            }


        } catch ( InvalidInputException | IOException invalidFtpInfo ) {
            LOG.error( "Error while processing ftp connection", invalidFtpInfo.getMessage(), invalidFtpInfo );

            Set<String> recipients = Collections.emptySet();
            TransactionSourceFtp ftpConnection = organizationUnitSettingsDao.fetchFileHeaderMapper( companyId, ftpId );
            if ( ftpConnection != null && StringUtils.isNotEmpty( ftpConnection.getEmailId() ) ) {
                recipients = new HashSet<>();
                recipients.addAll(
                    Arrays.asList( ftpConnection.getEmailId().trim().split( CommonConstants.COMMA_SEPERATOR_PATTERN ) ) );
            }

            emailServices.sendFtpProcessingErrorMailForCompany( recipients, companyId,
                "Error while processing ftp connection, please contact the administrator",
                StringUtils.join( invalidFtpInfo.getStackTrace(), FTP_HTML_LINE_FEED ), true, false );
            return false;
        }
        return true;
    }


    /**
     * 
     * @param companyId
     * @param host
     * @param userName
     * @param directoryPath
     * @param processedFolder
     * @param ftpId
     * @param fileName
     * @param os 
     * @return
     * @throws InvalidInputException
     * @throws IOException
     * @throws UndeliveredEmailException
     */
    private boolean extractAndQueueFtpFile( long companyId, String host, String userName, String directoryPath,
        String processedFolder, long ftpId, String fileName, String os )
        throws InvalidInputException, IOException, UndeliveredEmailException
    {
        LOG.debug( "method extractAndQueueFtpFile() called" );

        validateRemoteFileInformation( companyId, host, userName, directoryPath, processedFolder, ftpId, fileName );

        File localFile = null;
        RemoteAccessResponse fileTransferResponse = null;
        RemoteAccessResponse moveFileResponse = null;

        Date currentDate = new Date();

        String processedDateString = dateStringForFileName( currentDate );

        // assign new file name to store in s3
        String processedFileName = FTP_PROCESSED_FILE_NAME_ROOT
            + handleSpaces( fileName.substring( CommonConstants.INITIAL_INDEX, fileName.indexOf( '.' ) ) )
            + PROCESSED_FILE_NAME_SEPERATOR + currentDate.getTime() + fileName.substring( fileName.indexOf( '.' ) );

        // assign new file name to original file in the FTP server
        String processedFileNameToBeMoved = FTP_PROCESSED_FILE_NAME_ROOT
            + handleSpaces( fileName.substring( CommonConstants.INITIAL_INDEX, fileName.indexOf( '.' ) ) )
            + PROCESSED_FILE_NAME_SEPERATOR + processedDateString + fileName.substring( fileName.indexOf( '.' ) );

        String s3Url = null;
        boolean uploadedToS3 = false;
        boolean movedToProcessed = false;

        try {

            RemoteAccessConfig serverConfig = getRemoteFtpConfiguration( host, userName );

            // create the TEMP folder if it does not exist
            if ( !new File( ftpBatchFolder ).isDirectory() ) {
                new File( ftpBatchFolder ).mkdir();
            }

            // create local file place holder for the remote file
            localFile = new File( ftpBatchFolder + CommonConstants.FILE_SEPARATOR + processedFileName );
            if ( !localFile.createNewFile() ) {
                LOG.warn( "Unable to create temporary file for FTP batch or the file already exists" );
                throw new IOException( "Unable to create temporary file for FTP batch or the file already exists" );
            }

            // download the file from remote server
            fileTransferResponse = remoteAccessUtils.transferFile( serverConfig, localFile, RemoteFileDelivery.RECEIVE, true,
                directoryPath + CommonConstants.FILE_SEPARATOR + processOriginalFileName( fileName, os ) );
            if ( fileTransferResponse == null || fileTransferResponse.getStatus() > 0 ) {
                LOG.warn( "Unable to retreive the file fromm the FTP server" );
                throw new NonFatalException( "Unable to retreive the file from the FTP server" );
            } else {
                LOG.debug( fileTransferResponse.getResponse() );
            }

            // upload file to s3
            s3Url = fileUploadService.uploadFileAtFTPBucket( localFile, processedFileName );
            uploadedToS3 = true;


            // move processed file to 'processed' folder in the remote server
            moveFileResponse = remoteAccessUtils.executeCommand( serverConfig,
                RemoteAccessUtils.fetchCommandToMovefilesToDirectory(
                    Arrays.asList( directoryPath + CommonConstants.FILE_SEPARATOR + processOriginalFileName( fileName, os ) ),
                    processedFolder + CommonConstants.FILE_SEPARATOR + processedFileNameToBeMoved, true ) );
            if ( moveFileResponse == null || moveFileResponse.getStatus() > 0 ) {
                LOG.warn( "Unable to move the file in the FTP server to Processed folder" );
                throw new NonFatalException( "Unable to move the file in the FTP server to Processed folder" );
            } else {
                LOG.debug( moveFileResponse.getResponse() );
                movedToProcessed = true;
            }

            // post FTP upload request to stream
            postFtpUploadToStream( companyId, fileName, s3Url, ftpId, true );


        } catch ( NonFatalException error ) {
            if ( uploadedToS3 && !movedToProcessed ) {
                deleteUploadedFtpFile( processedFileName );
            }

            Set<String> recipients = Collections.emptySet();
            TransactionSourceFtp ftpConnection = organizationUnitSettingsDao.fetchFileHeaderMapper( companyId, ftpId );
            if ( ftpConnection != null && StringUtils.isNotEmpty( ftpConnection.getEmailId() ) ) {
                recipients = new HashSet<>();
                recipients.addAll(
                    Arrays.asList( ftpConnection.getEmailId().trim().split( CommonConstants.COMMA_SEPERATOR_PATTERN ) ) );
            }

            emailServices.sendFtpProcessingErrorMailForCompany( recipients, companyId,
                "Unable to process FTP file: '" + fileName + "' on Server: '" + host + "' at '" + directoryPath + "'",
                StringUtils.join( error.getStackTrace(), FTP_HTML_LINE_FEED ), true, false );
            return false;
        } finally {
            if ( localFile != null && localFile.exists() ) {
                Files.deleteIfExists( Paths.get( localFile.toURI() ) );
            }
        }

        return true;
    }


    private String handleSpaces( String fileName )
    {
        return fileName.replace( " ", PROCESSED_FILE_NAME_SEPERATOR );
    }


    private String processOriginalFileName( String fileName, String os )
    {
        return StringUtils.equals( CommonConstants.OS_LINUX, os ) ? fileName.replace( " ", "\\ " ) : fileName.trim();
    }


    /**
     * 
     * @param companyId
     * @param fileName
     * @param s3Url
     * @param ftpId
     * @param sendErrorMailToAdmin
     * @return
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    private boolean postFtpUploadToStream( long companyId, String fileName, String s3Url, long ftpId,
        boolean sendErrorMailToAdmin ) throws InvalidInputException, UndeliveredEmailException
    {
        LOG.debug( "method postFtpUploadToStream() called" );
        FtpUploadRequest ftpUploadRequest = getFtpUploadRequest( companyId, s3Url, ftpId );

        try {
            streamApiIntergrationBuilder.getStreamApi().sendsurveyTransactionRequest( ftpUploadRequest );
        } catch ( StreamApiException | StreamApiConnectException streamApiError ) {
            LOG.warn( "Unable to post ftp file upload message to kafka", streamApiError );

            FailedStreamMessage<FtpUploadRequest> failedStreamMessage = getFailedStreamMessageForFtp( companyId,
                ftpUploadRequest );
            failedStreamMessage.setId( saveFailedStreamMessages( failedStreamMessage ) );


            if ( sendErrorMailToAdmin ) {

                Set<String> recipients = Collections.emptySet();
                TransactionSourceFtp ftpConnection = organizationUnitSettingsDao.fetchFileHeaderMapper( companyId, ftpId );
                if ( ftpConnection != null && StringUtils.isNotEmpty( ftpConnection.getEmailId() ) ) {
                    recipients = new HashSet<>();
                    recipients.addAll(
                        Arrays.asList( ftpConnection.getEmailId().trim().split( CommonConstants.COMMA_SEPERATOR_PATTERN ) ) );
                }

                emailServices.sendFtpProcessingErrorMailForCompany( recipients, companyId,
                    "Unable to post ftp file upload message to kafka for file: '" + fileName
                        + "' with failed Stream message ID: '" + failedStreamMessage.getId() + "'",
                    StringUtils.join( streamApiError.getStackTrace(), FTP_HTML_LINE_FEED ), true, false );
            }
            return false;
        }
        return true;
    }


    /**
     * 
     * @param failedStreamMessage
     * @return
     */
    private String saveFailedStreamMessages( FailedStreamMessage<FtpUploadRequest> failedStreamMessage )
    {
        LOG.debug( "method saveFailedStreamMessages() running" );

        // make an SSApi call to save failed stream message data
        Response saveFailedFtpResponse = ssApiBatchIntergrationBuilder.getIntegrationApi()
            .saveFailedFtpStreamMessage( failedStreamMessage );

        if ( saveFailedFtpResponse != null && saveFailedFtpResponse.getStatus() >= 200
            && saveFailedFtpResponse.getStatus() < 300 ) {
            LOG.debug( "Successfully saved failed stream message" );
        } else {
            LOG.warn( "Unable to save failed stream message" );
            return null;
        }

        // get the response body
        String failedFtpIdString = StringEscapeUtils
            .unescapeJava( new String( ( (TypedByteArray) saveFailedFtpResponse.getBody() ).getBytes() ) );

        return new Gson().fromJson( StringUtils.strip( failedFtpIdString, "\"" ), new TypeToken<String>() {}.getType() );
    }


    /**
     * 
     * @param processedFileName
     */
    private void deleteUploadedFtpFile( String processedFileName )
    {
        LOG.trace( "method deleteUploadedFtpFile() running" );
        try {
            fileUploadService.deleteFileAtFTPBucket( processedFileName );
        } catch ( NonFatalException s3DeletionError ) {
            LOG.warn( "Unable to delete file : {} in s3 FTP bucket", processedFileName );
        }
    }


    /**
     * 
     * @param host
     * @param userName
     * @param directoryPath
     * @return
     * @throws InvalidInputException
     * @throws IOException
     */
    private List<String> mineForActiveFtpUploads( String host, String userName, String directoryPath )
        throws InvalidInputException, IOException
    {
        LOG.debug( "method mineForActiveFtpUploads() called" );

        validateRemoteFtpServerInfo( host, userName, directoryPath );

        RemoteAccessConfig serverConfig = getRemoteFtpConfiguration( host, userName );
        RemoteAccessResponse commandExecutionResponse = null;

        try {
            // get the list FTP files in the remote server to the be processed 
            String command = RemoteAccessUtils.fetchCommandToGetFileNamesFromDirectory( directoryPath, false );

            LOG.debug( "executing command on the remote server : {}", command );
            commandExecutionResponse = remoteAccessUtils.executeCommand( serverConfig, command );

            String response = StringUtils.EMPTY;

            if ( commandExecutionResponse != null ) {

                response = commandExecutionResponse.getResponse() == null ? "" : commandExecutionResponse.getResponse();

                if ( commandExecutionResponse.getStatus() > 1 ) {
                    LOG.debug( "command execution failed : {}", command );
                    LOG.debug( "Remote execution output: {}", response );
                    throw new IOException( "command execution failed" );
                }

            }

            return response == StringUtils.EMPTY ? new ArrayList<String>()
                : Arrays.asList( StringUtils.split( response, RemoteAccessUtils.SCP_EOL ) );

        } catch ( RemoteAccessException | IOException remoteAccessException ) {
            LOG.warn( "Unable to get a list of files from ftp server: {}, reason: {}", host, remoteAccessException.getMessage(),
                remoteAccessException );
            throw new IOException( "Unable to get a list of files from ftp server", remoteAccessException );
        }

    }


    /**
     * 
     * @param host
     * @param userName
     * @param directoryPath
     * @return
     * @throws InvalidInputException
     */
    private boolean validateRemoteFtpServerInfo( String host, String userName, String directoryPath )
        throws InvalidInputException
    {
        if ( StringUtils.isEmpty( host ) ) {
            LOG.warn( "Ftp host is not specified" );
            throw new InvalidInputException( "Ftp host is not specified" );
        } else if ( StringUtils.isEmpty( userName ) ) {
            LOG.warn( "Ftp server username is not specified" );
            throw new InvalidInputException( "Ftp server username is not specified" );
        } else if ( StringUtils.isEmpty( directoryPath ) ) {
            LOG.warn( "Ftp directory path is not specified" );
            throw new InvalidInputException( "Ftp directory path is not specified" );
        }
        return true;
    }


    /**
     * 
     * @param ftpConnection
     * @return
     * @throws InvalidInputException
     */
    private boolean validateFtpConnection( TransactionSourceFtp ftpConnection ) throws InvalidInputException
    {
        LOG.trace( "validating FTP Information" );
        if ( ftpConnection == null ) {
            LOG.warn( "FTP information not specified" );
            throw new InvalidInputException( "FTP information not specified" );
        } else if ( ftpConnection.getCompanyId() <= 0 ) {
            LOG.warn( "FTP company information not specified" );
            throw new InvalidInputException( "FTP company information not specified" );
        } else if ( StringUtils.isEmpty( ftpConnection.getFtpServerLabel() ) ) {
            LOG.warn( "FTP server information not specified" );
            throw new InvalidInputException( "FTP server information not specified" );
        } else if ( StringUtils.isEmpty( ftpConnection.getFtpDirectoryPath() ) ) {
            LOG.warn( "FTP file location information not specified" );
            throw new InvalidInputException( "FTP file location information not specified" );
        } else if ( !CommonConstants.FTP_SERVER_ONE.equals( ftpConnection.getFtpServerLabel() )
            && !CommonConstants.FTP_SERVER_TWO.equals( ftpConnection.getFtpServerLabel() ) ) {
            LOG.warn( "FTP server information is invalid" );
            throw new InvalidInputException( "FTP server information is invalid" );
        } else if ( ftpConnection.getFtpId() <= 0 ) {
            LOG.warn( "FTP connection information is invalid" );
            throw new InvalidInputException( "FTP connection information is invalid" );
        } else if ( StringUtils.isEmpty( ftpConnection.getFtpSource() ) ) {
            LOG.warn( "FTP source information is invalid" );
            throw new InvalidInputException( "FTP source information is invalid" );
        }
        return true;
    }


    /**
     * 
     * @param companyId
     * @param host
     * @param userName
     * @param directoryPath
     * @param processedFolder
     * @param ftpId
     * @param fileName
     * @return
     * @throws InvalidInputException
     */
    private boolean validateRemoteFileInformation( long companyId, String host, String userName, String directoryPath,
        String processedFolder, long ftpId, String fileName ) throws InvalidInputException
    {
        LOG.trace( "Validating remote ftp file information" );
        validateRemoteFtpServerInfo( host, userName, directoryPath );

        if ( companyId <= 0 ) {
            LOG.warn( "Invalid company Identifier" );
            throw new InvalidInputException( "Invalid company Identifier" );
        } else if ( StringUtils.isEmpty( processedFolder ) ) {
            LOG.warn( "Remote processed folder not specified" );
            throw new InvalidInputException( "Remote processed folder not specified" );
        } else if ( StringUtils.isEmpty( fileName ) ) {
            LOG.warn( "Remote file name not specified" );
            throw new InvalidInputException( "Remote file name not specified" );
        } else if ( ftpId <= 0 ) {
            LOG.warn( "FTP connection Identifier is invalid" );
            throw new InvalidInputException( "FTP connection Identifier is invalid" );
        }
        return true;
    }


    /**
     * 
     * @param host
     * @param userName
     * @return
     */
    private RemoteAccessConfig getRemoteFtpConfiguration( String host, String userName )
    {
        RemoteAccessConfig serverConfig = new RemoteAccessConfig();
        serverConfig.setHost( host );
        serverConfig.setKeyPath( rsaKeyPath );
        serverConfig.setPort( RemoteAccessUtils.SSH_PORT );
        serverConfig.setPreferredAuthentication( RemoteAccessAuthentication.USING_PUBLIC_KEY );
        serverConfig.setUserName( userName );
        return serverConfig;
    }


    /**
     * 
     * @param companyId
     * @param s3Url
     * @param ftpId
     * @return
     */
    private FtpUploadRequest getFtpUploadRequest( long companyId, String s3Url, long ftpId )
    {
        FtpUploadRequest ftpUploadRequest = new FtpUploadRequest();
        ftpUploadRequest.setCompanyId( companyId );
        ftpUploadRequest.setS3FileLocation( s3Url );
        ftpUploadRequest.setFtpId( ftpId );
        return ftpUploadRequest;
    }


    /**
     * 
     * @param companyId
     * @param ftpUploadRequest
     * @return
     */
    private FailedStreamMessage<FtpUploadRequest> getFailedStreamMessageForFtp( long companyId,
        FtpUploadRequest ftpUploadRequest )
    {
        FailedStreamMessage<FtpUploadRequest> failedStreamMessage = new FailedStreamMessage<>();
        failedStreamMessage.setCompanyId( companyId );
        failedStreamMessage.setFailedDate( System.currentTimeMillis() );
        failedStreamMessage.setMessageClass( FtpUploadRequest.class.getName() );
        failedStreamMessage.setMessage( ftpUploadRequest );
        return failedStreamMessage;
    }


    /**
     * 
     * @param failedFtpUpload
     * @param errorMessage
     * @return
     * @throws InvalidInputException
     * @throws UndeliveredEmailException
     */
    @Override
    public String processFailedStormMessage( FtpUploadRequest failedFtpUpload, String errorMessage, boolean sendOnlyToSocialSurveyAdmin )
        throws InvalidInputException, UndeliveredEmailException
    {
        if ( failedFtpUpload == null ) {
            LOG.warn( "failed ftp upload request not specified" );
            throw new InvalidInputException( "failed ftp upload request not specified" );
        } else if ( StringUtils.isEmpty( errorMessage ) ) {
            LOG.warn( "failed ftp upload error message not specified" );
            throw new InvalidInputException( "failed ftp upload error message not specified" );
        }

        FailedStreamMessage<FtpUploadRequest> failedStreamMessage = new FailedStreamMessage<>();
        failedStreamMessage.setCompanyId( failedFtpUpload.getCompanyId() );
        failedStreamMessage.setFailedDate( System.currentTimeMillis() );
        failedStreamMessage.setMessageClass( FtpUploadRequest.class.getName() );
        failedStreamMessage.setMessage( failedFtpUpload );

        // saving failed stream message to mongoDB FAILED_STREAM_MESSAGES COLLECTION
        streamMessagesService.saveFailedStreamMessage( failedStreamMessage );

        String details = "Error occured for the uploaded file: " + failedFtpUpload.getS3FileLocation()
            + ", saved error message ID: " + failedStreamMessage.getId();


        Set<String> recipients = Collections.emptySet();
        TransactionSourceFtp ftpConnection = organizationUnitSettingsDao.fetchFileHeaderMapper( failedFtpUpload.getCompanyId(),
            failedFtpUpload.getFtpId() );
        if ( ftpConnection != null && StringUtils.isNotEmpty( ftpConnection.getEmailId() ) ) {
            recipients = new HashSet<>();
            recipients
                .addAll( Arrays.asList( ftpConnection.getEmailId().trim().split( CommonConstants.COMMA_SEPERATOR_PATTERN ) ) );
        }

        // sending email to administrator
        emailServices.sendFtpProcessingErrorMailForCompany( recipients, failedFtpUpload.getCompanyId(), errorMessage, details,
            false, sendOnlyToSocialSurveyAdmin );

        return failedStreamMessage.getId();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public boolean updateRetryFailedForFailedFtpRequest( String id ) throws InvalidInputException
    {
        LOG.debug( "method updateRetryFailedForFailedFtpRequest() called" );
        FailedStreamMessage<FtpUploadRequest> failedStreamMessage = streamMessagesService.getFailedStreamMsg( id );

        if ( failedStreamMessage == null ) {
            LOG.warn( "the ID specified is not valid" );
            throw new InvalidInputException( "the failed message id is invalid" );
        }

        streamMessagesService.updateRetryFailedForStreamMsg( id );

        LOG.debug( "method updateRetryFailedForFailedFtpRequest() finished" );
        return true;
    }


    private String dateStringForFileName( Date date )
    {
        return new SimpleDateFormat( "yyyy-MM-dd__HH-mm-ss" ).format( date );
    }
}
