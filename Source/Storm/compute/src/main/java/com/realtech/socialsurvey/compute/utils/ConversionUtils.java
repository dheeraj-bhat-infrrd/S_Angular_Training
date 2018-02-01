package com.realtech.socialsurvey.compute.utils;

import static com.realtech.socialsurvey.compute.common.ComputeConstants.APPLICATION_PROPERTY_FILE;
import static com.realtech.socialsurvey.compute.common.ComputeConstants.FILEUPLOAD_DIRECTORY_LOCATION;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.realtech.socialsurvey.compute.common.LocalPropertyFileHandler;


/**
 * Utility class for conversions
 * @author nishit
 *
 */
public class ConversionUtils
{
    private static final DateTimeFormatter ISO_INSTANT_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    private static final Logger LOG = LoggerFactory.getLogger( ConversionUtils.class );
    // private constructor to avoid instantiation
    private ConversionUtils()
    {}


    /**
     * Deserialize json string
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T deserialize( String jsonString, Class<T> clazz )
    {
        Gson gson = new Gson();
        return gson.fromJson( jsonString, clazz );
    }


    /**
     * Deserialize json string
     * @param jsonString
     * @param type
     * @return
     */
    public static <T> T deserialize(String jsonString, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, type);
    }

    /**
     * Converts timestamp to DateTimeFormatter.ISO_INSTANT
     * @return
     */
    public static String convertCurrentEpochMillisToSolrTrieFormat()
    {
        return ISO_INSTANT_FORMATTER.format( Instant.ofEpochMilli( System.currentTimeMillis() ) );
    }


    /**
     * Converts provided timestamp in milliseconds to DateTimeFormatter.ISO_INSTANT 
     * @param timestampMillis
     * @return
     */
    public static String convertEpochSecondToSolrTrieFormat( long timestampMillis )
    {
        return ISO_INSTANT_FORMATTER.format( Instant.ofEpochSecond( timestampMillis ) );
    }

    /**
     * Converts the given timeZone to GMT
     * @param time
     * @return
     */
    public static String convertToGmt(String time) {
        ZonedDateTime dateTime = ZonedDateTime.parse(time);
        ZoneOffset offset = ZoneOffset.of("+00:00");
        ZonedDateTime gmtDateTime = dateTime.withZoneSameInstant(offset);
        return gmtDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public static String convertFromGmt(String gmtTime, String zoneId) {
        if( gmtTime != null && gmtTime != "" ) {
            ZonedDateTime gmtDateTime = ZonedDateTime.parse(gmtTime);
            ZoneOffset offset = ZoneOffset.of(zoneId);
            ZonedDateTime dateTime = gmtDateTime.withZoneSameInstant(offset);
            return dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } else return null;
    }

    public static String getZoneIdFromDateTime(String dateTime) {
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
        return  zonedDateTime.getZone().getId();
    }

    public static byte[] convertFileToBytes(File file) throws IOException {
        byte[] fileBytes = null;
        if(file != null){
            fileBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        }
        return fileBytes;
    }

    public static File convertBytesToFile(byte[] fileBytes, String fileName) throws IOException {
        String fileDirectoryLocation = LocalPropertyFileHandler.getInstance()
                .getProperty(APPLICATION_PROPERTY_FILE, FILEUPLOAD_DIRECTORY_LOCATION).orElse(null);

        Path path = Paths.get(fileDirectoryLocation + File.separator + fileName );
        Files.write(path, fileBytes);
        return path.toFile();
    }
}
