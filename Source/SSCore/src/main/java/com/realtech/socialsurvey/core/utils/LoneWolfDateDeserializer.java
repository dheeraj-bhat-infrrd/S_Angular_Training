package com.realtech.socialsurvey.core.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;


public class LoneWolfDateDeserializer extends JsonDeserializer<Date>
{
    @Override
    public Date deserialize( JsonParser jsonParser, DeserializationContext deserializationContext )
        throws IOException, JsonProcessingException
    {
        String datestr = jsonParser.readValueAs( String.class );
        SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        Date date = null;
        try {
            date = (Date) df.parse( datestr );
        } catch ( ParseException e ) {
            e.printStackTrace();
        }
        return date;
    }
}
