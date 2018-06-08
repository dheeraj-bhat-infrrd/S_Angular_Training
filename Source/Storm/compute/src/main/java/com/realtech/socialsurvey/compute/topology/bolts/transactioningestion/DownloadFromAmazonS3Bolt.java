package com.realtech.socialsurvey.compute.topology.bolts.transactioningestion;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;
import com.realtech.socialsurvey.compute.entities.TransactionIngestionMessage;
import com.realtech.socialsurvey.compute.topology.bolts.BaseComputeBoltWithAck;
import com.realtech.socialsurvey.compute.utils.ConversionUtils;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.*;


/**
 * 
 * @author sandra
 *
 */
public class DownloadFromAmazonS3Bolt extends BaseComputeBoltWithAck
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger( DownloadFromAmazonS3Bolt.class );


    @Override
    public void declareOutputFields( OutputFieldsDeclarer outputFieldsDeclarer )
    {
        outputFieldsDeclarer.declare( new Fields( "isSuccess", "targetPath", "transactionIngestionMessage" ) );
    }


    @Override
    public void executeTuple( Tuple input )
    {
        LOG.info( "Fetching emited transaction ingetion object in DownloadFromAmazonS3Bolt" );
        //set success
        boolean success = false;
        Path targetPath = null;
        String localDirPath = LocalPropertyFileHandler.getInstance()
            .getProperty( APPLICATION_PROPERTY_FILE, LOCAL_DIR_LOCATION ).orElse( null );

        //get the transaction ingestion object from tuple 
        TransactionIngestionMessage transactionIngestionMessage = ConversionUtils.deserialize( input.getString( 0 ),
            TransactionIngestionMessage.class );
        if ( transactionIngestionMessage.getS3FileLocation() != null
            || !transactionIngestionMessage.getS3FileLocation().isEmpty() ) {
            try {
                targetPath = download( transactionIngestionMessage.getS3FileLocation(), localDirPath );
                success = true;
            } catch ( IOException e ) {
                LOG.error( "Failed to save file on local from location {}", transactionIngestionMessage.getS3FileLocation() );
            }
        }
        LOG.info( "Emitting tuple with success = {} , targetPath = {}, transactionIngestionMessage = {}", success, targetPath,
            transactionIngestionMessage );
        _collector.emit( input, Arrays.asList( success, targetPath, transactionIngestionMessage ) );

    }


    private static Path download( String sourceURL, String targetDirectory ) throws IOException
    {
        URL url = new URL( sourceURL );
        String fileName = sourceURL.substring( sourceURL.lastIndexOf( '/' ) + 1, sourceURL.length() );
        Path targetPath = new File( targetDirectory + File.separator + fileName ).toPath();

        // Open the file, creating it if it doesn't exist
        try ( final BufferedWriter out = Files.newBufferedWriter( targetPath, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
            StandardOpenOption.APPEND ) ) {
            // Write to out here, perhaps outputting `str`?
        }
        Files.copy( url.openStream(), targetPath, StandardCopyOption.REPLACE_EXISTING );

        return targetPath;
    }


    @Override
    public List<Object> prepareTupleForFailure()
    {
        return Arrays.asList( false, null, null );
    }

}
