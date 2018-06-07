package com.realtech.socialsurvey.core.utils.remoteaccess;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessConfig;
import com.realtech.socialsurvey.core.entities.remoteaccess.RemoteAccessResponse;
import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteAccessAuthentication;
import com.realtech.socialsurvey.core.enums.remoteaccess.RemoteFileDelivery;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.remoteaccess.RemoteAccessException;


@Service
public class RemoteAccessUtils
{

    public static final Logger LOG = LoggerFactory.getLogger( RemoteAccessUtils.class );

    public static final String SSH_CHANNEL_TYPE_EXEC = "exec";
    public static final String SSH_CHANNEL_TYPE_SHELL = "shell";
    public static final String SSH_CHANNEL_TYPE_SFTP = "sftp";

    public static final String JSCH_AUTHENTICATION_TYPE_PUBLIC_KEY = "publickey";
    public static final String JSCH_AUTHENTICATION_TYPE_KEYBOARD = "keyboard-interactive";
    public static final String JSCH_AUTHENTICATION_TYPE_PASS_WORD = "password";
    public static final String JSCH_AUTHENTICATION_KEY = "PreferredAuthentications";
    public static final String JSCH_HOST_KEY_CHECKING = "StrictHostKeyChecking";

    public static final int SCP_ACK_ERROR = 1;
    public static final int SCP_ACK_FATAL_ERROR = 2;

    // the header keeps changing but starts with 'C'
    public static final char SCP_FILE_DETAILS_HEADER = 'C';
    public static final char SCP_FILE_TIMESTAMP_DATA_HEADER = 'T';
    public static final String SCP_SEPERATOR = " ";
    public static final String SCP_EOL = "\n";
    public static final int SCP_ACK_CODE = 0;
    public static final int MILLISECS_IN_SEC = 1000;
    public static final long SCP_AYSNC_RESPONSE_WAIT = 10000;
    public static final String SCP_COMMAND = "scp";
    public static final String SCP_PRESERVE_TIMESTAMP_OPTION = "-p";
    public static final String SCP_RECEIVE_OPTION = "-f";
    public static final String SCP_SEND_OPTION = "-t";

    public static final String LS = "ls";
    public static final String LS_APPEND_SLASH_TO_DIRECTORY_OPTION = "-p";
    public static final String LS_INCLUDE_HIDDEN_OPTION = "-a";
    public static final String GREP = "grep";
    public static final String GREP_NOT_INCLUDE_OPTION = "-v";
    public static final String PIPE = "|";

    public static final int SSH_PORT = 22;

    private static final String MV = "mv";
    private static final String NO = "no";

    private static final String SUDO = "sudo";


    /**
     * 
     * @param remoteAccessConfig
     * @param command
     * @return
     * @throws RemoteAccessException
     * @throws InvalidInputException
     */
    public RemoteAccessResponse executeCommand( RemoteAccessConfig remoteAccessConfig, String command )
        throws RemoteAccessException, InvalidInputException
    {
        LOG.debug( "method executeCommand() started " );

        if ( StringUtils.isEmpty( command ) ) {
            LOG.warn( "command to be executed is not specified : " );
            throw new InvalidInputException( "command to be executed is not specified" );
        }

        // validate configuration for remote access
        validateConfiguration( remoteAccessConfig );

        Session session = null;
        ChannelExec executionChannel = null;
        InputStream in = null;
        RemoteAccessResponse response = null;
        String responseString = null;

        LOG.trace( "Connecting to server as user {} to  host {} and port {}", remoteAccessConfig.getUserName(),
            remoteAccessConfig.getHost(), remoteAccessConfig.getPort() );

        try {

            session = setupSession( remoteAccessConfig );

            //connect session
            session.connect();

            // create the execution channel over the session
            executionChannel = (ChannelExec) session.openChannel( SSH_CHANNEL_TYPE_EXEC );

            LOG.trace( "Executing command in remote server : {}", command );
            executionChannel.setCommand( command );


            // initialize the in stream for output
            in = executionChannel.getInputStream();

            // execute
            executionChannel.connect();


            // get the response for the command execution 
            responseString = IOUtils.toString( in );
            LOG.trace( "output of the executed command: {}", responseString );

            // Retrieve the exit status of the executed command
            LOG.trace( "status of execution: {}", executionChannel.getExitStatus() );


            // build response object
            response = new RemoteAccessResponse();
            response.setResponse( responseString );
            response.setStatus( executionChannel.getExitStatus() );


        } catch ( JSchException remoteAccessAgentError ) {
            LOG.warn( "Unable to to execute the specified command on the remote server: {}",
                remoteAccessAgentError.getMessage(), remoteAccessAgentError );
            throw new RemoteAccessException( "Unable to to execute the specified command on the remote server",
                remoteAccessAgentError );
        } catch ( IOException ioError ) {
            LOG.warn( "Remote server connection error: {}", ioError.getMessage(), ioError );
            throw new RemoteAccessException( "Remote server connection error", ioError );
        }


        finally {
            LOG.trace( "Disconnecting the ssh execution channel and terminating ssh connection" );
            if ( executionChannel != null ) {
                executionChannel.disconnect();
            }
            if ( session != null ) {
                session.disconnect();
            }
        }

        LOG.debug( "method executeCommand() finished" );
        return response;
    }


    /**
     * 
     * @param remoteAccessConfig
     * @return
     * @throws JSchException
     */
    private Session setupSession( RemoteAccessConfig remoteAccessConfig ) throws JSchException
    {
        LOG.trace( "Setting up remote session" );

        // remote access dependency agent
        JSch jschInstance = new JSch();

        // give the path to private key file or the password for authentication
        if ( remoteAccessConfig.getPreferredAuthentication().equals( RemoteAccessAuthentication.USING_PUBLIC_KEY ) ) {
            jschInstance.addIdentity( remoteAccessConfig.getKeyPath() );
        }

        // adding known hosts file
        if ( remoteAccessConfig.isAllowKnownHostsOnly() ) {
            jschInstance.setKnownHosts( remoteAccessConfig.getKnownHostsPath() );
        }

        // Set User and IP of the remote host and SSH port.
        Session session = jschInstance.getSession( remoteAccessConfig.getUserName(), remoteAccessConfig.getHost(),
            remoteAccessConfig.getPort() );

        Properties config = new Properties();

        if ( remoteAccessConfig.getPreferredAuthentication().equals( RemoteAccessAuthentication.USING_PUBLIC_KEY ) ) {
            config.put( JSCH_AUTHENTICATION_KEY, JSCH_AUTHENTICATION_TYPE_PUBLIC_KEY );
        } else if ( remoteAccessConfig.getPreferredAuthentication().equals( RemoteAccessAuthentication.USING_PASSWORD ) ) {
            config.put( JSCH_AUTHENTICATION_KEY, JSCH_AUTHENTICATION_TYPE_PASS_WORD );
            session.setPassword( remoteAccessConfig.getPassword() );
        }


        // By default StrictHostKeyChecking is set to yes as a security measure.
        if ( !remoteAccessConfig.isAllowKnownHostsOnly() ) {
            config.put( JSCH_HOST_KEY_CHECKING, NO );
        }

        session.setConfig( config );
        return session;
    }


    /**
     * 
     * @param remoteAccessConfig
     * @throws InvalidInputException
     */
    private void validateConfiguration( RemoteAccessConfig remoteAccessConfig ) throws InvalidInputException
    {
        LOG.trace( "Validating remote access configuration" );

        if ( remoteAccessConfig == null ) {
            LOG.warn( "Remote access configuration missing" );
            throw new InvalidInputException( "Remote access configuration not specified" );
        } else if ( StringUtils.isEmpty( remoteAccessConfig.getUserName() ) ) {
            LOG.warn( "Username for the remote server account not specified" );
            throw new InvalidInputException( "Username for the remote server account not specified" );
        } else if ( StringUtils.isEmpty( remoteAccessConfig.getHost() ) ) {
            LOG.warn( "Host address of the remote server not specified" );
            throw new InvalidInputException( "Host address of the remote server not specified" );
        } else if ( remoteAccessConfig.getPort() <= CommonConstants.INITIAL_INDEX ) {
            LOG.warn( "SSH port of the remote server not specified" );
            throw new InvalidInputException( "SSH port of the remote server not specified" );
        } else if ( remoteAccessConfig.getPreferredAuthentication() == null ) {
            LOG.warn( "Authentication method for connecting to remote server not specified" );
            throw new InvalidInputException( "Authentication method for connecting to remote server not specified" );
        } else if ( RemoteAccessAuthentication.USING_PUBLIC_KEY.equals( remoteAccessConfig.getPreferredAuthentication() )
            && StringUtils.isEmpty( remoteAccessConfig.getKeyPath() ) ) {
            LOG.warn( "Authentication key file path not specified" );
            throw new InvalidInputException( "Authentication key file path not specified" );
        } else if ( RemoteAccessAuthentication.USING_PASSWORD.equals( remoteAccessConfig.getPreferredAuthentication() )
            && StringUtils.isEmpty( remoteAccessConfig.getPassword() ) ) {
            LOG.warn( "Authentication password not specified" );
            throw new InvalidInputException( "Authentication password not specified" );
        } else if ( remoteAccessConfig.isAllowKnownHostsOnly()
            && StringUtils.isEmpty( remoteAccessConfig.getKnownHostsPath() ) ) {
            LOG.warn( "Known hosts file path not specified" );
            throw new InvalidInputException( "Known hosts file path not specified" );
        }
    }


    /**
     * 
     * @param remoteAccessConfig
     * @param localFile
     * @param sendOrReceive
     * @param preserveFileTimestamps
     * @param remoteFilePath
     * @return
     * @throws RemoteAccessException
     * @throws InvalidInputException
     */
    public RemoteAccessResponse transferFile( RemoteAccessConfig remoteAccessConfig, File localFile,
        RemoteFileDelivery sendOrReceive, boolean preserveFileTimestamps, String remoteFilePath )
        throws RemoteAccessException, InvalidInputException
    {
        LOG.debug( "method transferFile() started" );

        // validate configuration for remote access
        validateConfiguration( remoteAccessConfig );

        // validate the file, making sure it is ready to send or receive data
        validateFilesAndPathsUnderConsideration( localFile, sendOrReceive, remoteFilePath );

        Session session = null;
        ChannelExec executionChannel = null;
        InputStream in = null;
        RemoteAccessResponse response = null;
        String responseString = null;
        OutputStream out = null;
        long copiedFileSize = 0L;


        LOG.trace( "Connecting to server as user {} to  host {} and port {}", remoteAccessConfig.getUserName(),
            remoteAccessConfig.getHost(), remoteAccessConfig.getPort() );

        try ( FileOutputStream fos = new FileOutputStream( localFile );
            FileInputStream fis = new FileInputStream( localFile ) ) {

            session = setupSession( remoteAccessConfig );

            //connect session
            session.connect();


            // create the execution channel over the session
            executionChannel = (ChannelExec) session.openChannel( SSH_CHANNEL_TYPE_EXEC );


            String command = buildScpCommand( sendOrReceive, preserveFileTimestamps, remoteFilePath );


            LOG.trace( "Executing command in remote server : {}", command );
            executionChannel.setCommand( command );


            // initialize the in and out stream
            in = executionChannel.getInputStream();
            out = executionChannel.getOutputStream();

            // execute
            executionChannel.connect();

            if ( RemoteFileDelivery.RECEIVE.equals( sendOrReceive ) ) {

                if ( preserveFileTimestamps ) {
                    sendAck( out );
                    receiveAndProcessFileTimeStampsData( localFile, in );
                }

                sendAck( out );
                String[] fileDetails = receiveFileDetails( in ); // receiving file details header at index=0, file size at index=1 and file name at index=2
                long fileSizeInBytes = Long.parseLong( fileDetails[1] );

                sendAck( out );
                copiedFileSize = IOUtils.copyLarge( in, fos, CommonConstants.INITIAL_INDEX, fileSizeInBytes );
                fos.flush();

                // receive closing acknowledgement
                receiveAck( in );

            } else if ( RemoteFileDelivery.SEND.equals( sendOrReceive ) ) {


                receiveAck( in );


                if ( preserveFileTimestamps ) {
                    sendFileTimeStampsData( localFile, out );
                    receiveAck( in );
                }


                sendFileDetails( localFile, out );
                receiveAck( in );

                copiedFileSize = IOUtils.copyLarge( fis, out, CommonConstants.INITIAL_INDEX, localFile.length() );

                // send closing acknowledgement
                sendAck( out );

            }


            // get the response for the command execution 
            responseString = "Bytes transfered : " + copiedFileSize;
            LOG.trace( "output of the executed command: {}", responseString );

            // Retrieve the exit status of the executed command
            LOG.trace( "status of execution: {}", executionChannel.getExitStatus() );


            // build response object
            response = new RemoteAccessResponse();
            response.setResponse( responseString );
            response.setStatus( executionChannel.getExitStatus() );


        } catch ( JSchException remoteAccessAgentError ) {
            LOG.warn( "Unable to transfer file: {}", remoteAccessAgentError.getMessage(), remoteAccessAgentError );
            throw new RemoteAccessException( "Unable to transfer file", remoteAccessAgentError );
        } catch ( IOException ioError ) {
            LOG.warn( "Remote server connection error: {}", ioError.getMessage(), ioError );
            throw new RemoteAccessException( "Remote server connection error", ioError );
        }


        finally {
            LOG.trace( "Disconnecting the ssh execution channel and terminating ssh connection" );
            if ( executionChannel != null ) {
                executionChannel.disconnect();
            }
            if ( session != null ) {
                session.disconnect();
            }
        }


        LOG.debug( "method transferFile() finished" );
        return response;
    }


    /**
     * 
     * @param file
     * @param sendOrReceive
     * @param remoteFilePath
     * @throws InvalidInputException
     */
    private void validateFilesAndPathsUnderConsideration( File file, RemoteFileDelivery sendOrReceive, String remoteFilePath )
        throws InvalidInputException
    {
        LOG.trace( "Validating file and delivery direction" );

        if ( file == null ) {
            LOG.warn( "File under consideration is not specified" );
            throw new InvalidInputException( "File under consideration is not specified" );
        } else if ( !file.exists() ) {
            LOG.warn( "File under consideration is doesn't exist" );
            throw new InvalidInputException( "File under consideration is doesn't exist" );
        } else if ( !file.isFile() ) {
            LOG.warn( "File under consideration is not valid" );
            throw new InvalidInputException( "File under consideration is not valid" );
        } else if ( sendOrReceive == null ) {
            LOG.warn( "Not specified whether to send or receive file" );
            throw new InvalidInputException( "Not specified whether to send or receive file" );
        } else if ( RemoteFileDelivery.SEND.equals( sendOrReceive ) && !file.canRead() ) {
            LOG.warn( "File under consideration cannot be read" );
            throw new InvalidInputException( "File under consideration cannot be read" );
        } else if ( RemoteFileDelivery.RECEIVE.equals( sendOrReceive ) && !file.canWrite() ) {
            LOG.warn( "File under consideration cannot be written to" );
            throw new InvalidInputException( "File under consideration cannot be written to" );
        } else if ( StringUtils.isEmpty( file.getName() ) ) {
            LOG.warn( "File under consideration does not have name associated to it" );
            throw new InvalidInputException( "File under consideration does not have name associated to it" );
        } else if ( StringUtils.isEmpty( remoteFilePath ) ) {
            LOG.warn( "Remote file path is not specified" );
            throw new InvalidInputException( "Remote file path is not specified" );
        }

    }


    /**
     * 
     * @param sendOrReceive
     * @param preserveFileTimestamps
     * @param remoteFilePath
     * @return
     */
    private String buildScpCommand( RemoteFileDelivery sendOrReceive, boolean preserveFileTimestamps, String remoteFilePath )
    {
        StringBuilder command = new StringBuilder( SCP_COMMAND + SCP_SEPERATOR );

        if ( preserveFileTimestamps ) {
            command.append( SCP_PRESERVE_TIMESTAMP_OPTION + SCP_SEPERATOR );
        }

        if ( RemoteFileDelivery.RECEIVE.equals( sendOrReceive ) ) {
            command.append( SCP_RECEIVE_OPTION + SCP_SEPERATOR );
        } else {
            command.append( SCP_SEND_OPTION + SCP_SEPERATOR );
        }

        return command.append( remoteFilePath ).toString();
    }


    private void sendAck( OutputStream out ) throws IOException
    {
        LOG.trace( "sending ack code" );
        out.write( SCP_ACK_CODE );
        out.flush();
    }


    private void receiveAck( InputStream in ) throws IOException
    {
        LOG.trace( "receiving ack code" );
        if ( isDataAvailable( in ) && checkAck( in ) != SCP_ACK_CODE ) {
            LOG.warn( "Input stream  is blocked" );
            throw new IOException( "ACK not received, Input stream  is blocked" );
        }
    }


    private static int checkAck( InputStream in ) throws IOException
    {
        LOG.trace( "checking ack code" );
        int status = in.read();
        if ( status == SCP_ACK_ERROR || status == SCP_ACK_FATAL_ERROR ) {
            String errorMessage = getStringReader( in ).readLine();
            LOG.warn( "remote access ACK error: {}", errorMessage );
            throw new IOException( "remote access ACK error: " + errorMessage );
        }
        return status;
    }


    private boolean isDataAvailable( InputStream in ) throws IOException
    {
        LOG.trace( "checking data availability" );
        if ( in.available() > CommonConstants.INITIAL_INDEX ) {
            return true;
        } else {
            try {
                Thread.sleep( SCP_AYSNC_RESPONSE_WAIT );
            } catch ( InterruptedException asyncResponseError ) {
                LOG.warn( "Unable to wait for async remote response" );
                Thread.currentThread().interrupt();
            }
            return in.available() > CommonConstants.INITIAL_INDEX ? true : false;
        }
    }


    /**
     * 
     * @param localFile
     * @param in
     * @return
     * @throws IOException
     */
    private boolean receiveAndProcessFileTimeStampsData( File localFile, InputStream in ) throws IOException
    {
        LOG.trace( "processing file timestamps data" );
        if ( SCP_FILE_TIMESTAMP_DATA_HEADER != checkAck( in ) ) {
            throw new IOException( "FileDetails fetch error : Remote file timestamp data not received" );
        }

        String[] times = StringUtils.split( getStringReader( in ).readLine().trim(), SCP_SEPERATOR );
        // the third index has last accessed time, not needed
        return localFile.setLastModified( Long.parseLong( times[CommonConstants.INITIAL_INDEX] ) );
    }


    private void sendFileTimeStampsData( File localFile, OutputStream out ) throws IOException
    {
        LOG.trace( "sending file timestamps data" );
        long timeInMilliSecs = localFile.lastModified() / MILLISECS_IN_SEC;
        out.write( ( SCP_FILE_TIMESTAMP_DATA_HEADER + timeInMilliSecs + SCP_SEPERATOR + SCP_ACK_CODE + SCP_SEPERATOR
            + timeInMilliSecs + SCP_SEPERATOR + SCP_ACK_CODE + SCP_EOL ).getBytes() );
        out.flush();
    }


    private void sendFileDetails( File localFile, OutputStream out ) throws IOException
    {
        LOG.trace( "sending file details" );
        out.write(
            ( SCP_FILE_DETAILS_HEADER + SCP_SEPERATOR + localFile.length() + SCP_SEPERATOR + localFile.getName() + SCP_EOL )
                .getBytes() );
        out.flush();
    }


    private String[] receiveFileDetails( InputStream in ) throws IOException
    {
        LOG.trace( "receiving file details" );
        String fileDetailsString = getStringReader( in ).readLine();

        if ( StringUtils.isEmpty( fileDetailsString ) || fileDetailsString.indexOf( SCP_SEPERATOR ) == -1
            || SCP_FILE_DETAILS_HEADER != fileDetailsString.charAt( CommonConstants.INITIAL_INDEX ) ) {
            throw new IOException( "FileDetails fetch error : file size and name data not received" );
        }

        String[] fileDetails = new String[3];
        fileDetails[0] = StringUtils.substring( fileDetailsString, CommonConstants.INITIAL_INDEX,
            fileDetailsString.indexOf( SCP_SEPERATOR ) );

        fileDetails[1] = StringUtils.substring( fileDetailsString, fileDetailsString.indexOf( SCP_SEPERATOR ) + 1,
            StringUtils.ordinalIndexOf( fileDetailsString, SCP_SEPERATOR, 2 ) );

        fileDetails[2] = StringUtils.substring( fileDetailsString,
            StringUtils.ordinalIndexOf( fileDetailsString, SCP_SEPERATOR, 2 ) + 1, fileDetailsString.length() );


        return fileDetails;

    }


    private static synchronized BufferedReader getStringReader( InputStream in )
    {
        return new BufferedReader( new InputStreamReader( in ) );
    }


    public static String fetchCommandToGetFileNamesFromDirectory( String directoryPath, boolean includeHidden )
    {
        return LS + SCP_SEPERATOR + ( StringUtils.isEmpty( directoryPath ) ? "" : directoryPath + SCP_SEPERATOR )
            + ( includeHidden ? LS_INCLUDE_HIDDEN_OPTION + SCP_SEPERATOR : "" ) + LS_APPEND_SLASH_TO_DIRECTORY_OPTION + PIPE
            + GREP + SCP_SEPERATOR + GREP_NOT_INCLUDE_OPTION + SCP_SEPERATOR + CommonConstants.FILE_SEPARATOR;
    }


    public static String fetchCommandToMovefilesToDirectory( List<String> absoluteFilePaths, String targetDirectory,
        boolean withSudo ) throws InvalidInputException
    {
        if ( absoluteFilePaths != null && !absoluteFilePaths.isEmpty() && StringUtils.isNotEmpty( targetDirectory ) ) {
            return ( withSudo ? SUDO + SCP_SEPERATOR : StringUtils.EMPTY ) + MV + SCP_SEPERATOR
                + StringUtils.join( absoluteFilePaths, SCP_SEPERATOR ) + SCP_SEPERATOR + targetDirectory;
        } else {
            throw new InvalidInputException( "Invalid files or target directory" );
        }
    }
}