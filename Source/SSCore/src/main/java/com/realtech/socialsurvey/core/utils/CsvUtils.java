package com.realtech.socialsurvey.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


@Component
public class CsvUtils
{
    private static final Logger LOG = LoggerFactory.getLogger( CsvUtils.class );

    private static final char CSV_SEPERATOR = ',';
    private static final char NEWLINE = '\n';
    private static final String DEFAULT_FILENAME = "dataFile";


    private String ProvideCSVSafeString( String input )
    {
        return input.contains( "\"" ) ? input.replace( "\"", "\"\"" ) : input;
    }


    public void writeLineToCsv( Writer csvWriter, List<Object> values ) throws IOException, InvalidInputException
    {
        LOG.info( "writeLineToCsv started" );

        //check for file writer
        if ( csvWriter == null ) {
            throw new InvalidInputException( "file writer not found" );
        }

        //check for value list
        if ( values == null || values.isEmpty() ) {
            throw new InvalidInputException( "no data found to write to csv" );
        }

        //prepare a builder to create a data string 
        StringBuilder dataStringBuilder = new StringBuilder();

        for ( Object data : values ) {

            String dataString = null;

            //check if the data provided can be written as string
            try {
                dataString = data == null ? "" : (String) data;
            } catch ( Exception unableToCastToString ) {
                dataString = "";
            }
            //append to the data String
            dataStringBuilder.append( ProvideCSVSafeString( dataString ) ).append( CSV_SEPERATOR );
        }
        //delete the extra csv separator
        dataStringBuilder.deleteCharAt( dataStringBuilder.length() - 1 );

        //append new line
        dataStringBuilder.append( NEWLINE );

        //write to file
        csvWriter.write( dataStringBuilder.toString() );
        LOG.info( "writeLineToCsv finished" );
    }


    public File buildCsvFile( String filename, List<List<Object>> rows, String filePath )
        throws InvalidInputException, IOException
    {
        LOG.info( "buildCsvFile started" );
        //check for data to be inserted
        if ( rows == null || rows.isEmpty() ) {
            throw new InvalidInputException( "data to be entered is empty" );
        }

        //if file name is not given then use the default file name
        if ( filename == null || filename.isEmpty() ) {
            filename = DEFAULT_FILENAME;
        }

        //declare file and file writer references
        File csvFile = null;
        Writer csvWriter = null;
        Path tokenDirectory = null;

        try {

            try {
                tokenDirectory = Paths.get( filePath );
                if ( !Files.exists( tokenDirectory ) ) {
                    Files.createDirectory( tokenDirectory );
                }
            } catch ( IOException unableToFindFilePlaceHolder ) {
                LOG.error( "unable to get hold of user token directory" );
                throw unableToFindFilePlaceHolder;
            }

            //create a csv file in temporary folder
            StringBuilder pathBuilder = new StringBuilder( tokenDirectory + "\\" + filename + ".csv" );
            csvFile = new File( pathBuilder.toString() );

            //create the file
            csvFile.createNewFile();

            //check if the file is actually created
            if ( csvFile == null || !csvFile.exists() ) {
                throw new IOException( "unable to create a csv file" );
            }

            try {
                //initialize a writer for the file
                csvWriter = new FileWriter( csvFile );
            } catch ( IOException unableToOpenFileInWriteMode ) {
                LOG.error( "unable to create FileWriter object for the csv file" );
                throw unableToOpenFileInWriteMode;
            }

            //begin writing rows into the new file
            for ( List<Object> row : rows ) {
                //try to write the current row of data
                try {
                    writeLineToCsv( csvWriter, row );
                } catch ( InvalidInputException invalidRowValues ) {

                    //when unable to write the current line, move on to the next line 
                    LOG.error( "unable to parse the current list of data values,moving on to the next line" );

                } catch ( IOException unableToWriteLine ) {

                    //in case of IO errors stop further writes to the file and throw IO exception
                    LOG.error( "unable to write  the current list of data values to file" );
                    throw unableToWriteLine;

                }
            }

            //flush data to file and close the writer
            csvWriter.flush();
            csvWriter.close();

        } catch ( Exception unhandledException ) {
            LOG.error( "unable to build the csv file" );
        } finally {
            if ( csvWriter != null ) {
                csvWriter.close();
            }
        }

        LOG.info( "buildCsvFile finished" );
        return csvFile;
    }


    /**
     * @param csvFileURI
     * @return
     * @throws InvalidInputException
     * @throws IOException
     */
    public Map<Integer, List<String>> readFromCsv( String csvFileURI ) throws InvalidInputException, IOException
    {

        LOG.debug( "method readFromCsv started" );

        // check if the URI is present, if not then throw an exception
        if ( StringUtils.isEmpty( csvFileURI ) ) {
            LOG.error( "Source of the file is not present" );
            throw new InvalidInputException( "file source is not present." );
        }

        Map<Integer, List<String>> csvData = new HashMap<>();

        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( new URL( csvFileURI ).openStream() ) ) ) {

            String line = null;
            int i = 1;

            // read all the lines form the source and extract the fields
            while ( ( line = reader.readLine() ) != null ) {

                // process all the lines and retrieve the trimmed data
                csvData.put( i, Arrays.asList( line.trim().split( "\\s*" + String.valueOf( CSV_SEPERATOR ) + "\\s*" ) ) );
                i++;
            }

        } catch ( IOException readError ) {
            LOG.error( "Unable read CSV file." );
            throw new IOException( "Unable to read Csv from the source" );
        }

        return csvData;
    }
}