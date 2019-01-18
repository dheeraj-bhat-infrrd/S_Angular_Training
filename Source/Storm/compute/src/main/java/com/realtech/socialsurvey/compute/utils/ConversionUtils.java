package com.realtech.socialsurvey.compute.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


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


    /**
     * Converts the given millies into EST
     * @param millies
     * @return
     */
    public static String convertToEst(long millies){
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millies),
            ZoneId.of("-05:00"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" MM/dd/yyyy H:m:s a");
        return zonedDateTime.format(formatter);
    }


    /**
     * Converts seconds to date object
     * @param seconds
     * @return
     */
    public static Date secondsToDateTime(long seconds){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String dateTimeString =  Instant.ofEpochSecond(seconds).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ));
        try {
            return format.parse( dateTimeString );
        } catch ( ParseException e ) {
            LOG.error( "Error while converting seconds {} to date", seconds );

        }
        return null;
    }

}
