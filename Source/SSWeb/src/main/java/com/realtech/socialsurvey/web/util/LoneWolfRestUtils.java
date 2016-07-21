package com.realtech.socialsurvey.web.util;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class LoneWolfRestUtils
{

    public static final Logger LOG = LoggerFactory.getLogger( LoneWolfRestUtils.class );

    public final String GET = "GET";
    public final String MD5_EMPTY = "1B2M2Y8AsgTpgAmY7PhCfg==";
    public final String H_MAC_SHA256 = "HmacSHA256";
    public final String DATE_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSSX";

    public String generateAuthorizationHeaderFor( String resourceUri, String secretkey, String apiToken, String clientCode)
    {
        LOG.info( "Method generateAuthorizationHeaderFor started." );
        
        String authHeader = null;
        String dateStr = getCurrentTimestampIn( "UTC" );
        String signature = initSigntaure( GET, resourceUri, dateStr, MD5_EMPTY );
        String sig = getSHA256String( signature, secretkey );
        authHeader = initAuthorizationHeader( apiToken, clientCode, sig, dateStr);
        
        LOG.info( "Method generateAuthorizationHeaderFor finished." );
        return authHeader;
    }


    private String getCurrentTimestampIn( String timeZone )
    {
        Date today = new Date( System.currentTimeMillis() );
        SimpleDateFormat utcDateFormat = new SimpleDateFormat( DATE_FORMAT );
        utcDateFormat.setTimeZone( TimeZone.getTimeZone( timeZone ) );
        return utcDateFormat.format( today );
    }


    private String initSigntaure( String httpMethod, String resourceUri, String date, String md5Content )
    {
        String signature = "[HTTP Method]:[Resource URI]:[Date]:[Content-MD5]";
        signature = signature.replace( "[HTTP Method]", httpMethod );
        signature = signature.replace( "[Resource URI]", resourceUri );
        signature = signature.replace( "[Date]", date );
        signature = signature.replace( "[Content-MD5]", md5Content );
        return signature;
    }


    private String getSHA256String( String signature, String secretkey )
    {
        String result = "";
        try {
            final Charset utf8cs = Charset.forName( "US-ASCII" );
            final Mac sha256_HMAC = Mac.getInstance( H_MAC_SHA256 );
            final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec( utf8cs.encode( secretkey ).array(),
                H_MAC_SHA256 );
            sha256_HMAC.init( secret_key );
            final byte[] mac_data = sha256_HMAC.doFinal( utf8cs.encode( signature ).array() );
            for ( final byte element : mac_data ) {
                result += Integer.toString( ( element & 0xff ) + 0x100, 16 ).substring( 1 );
            }
        } catch ( Exception e ) {
            LOG.error( e.getMessage() );
        }
        return result;
    }
    
    private String initAuthorizationHeader( String apiToken, String clientCode, String signature, String dateStr)
    {
        String authHeader = "LoneWolfToken [API Token]:[Client Code]:[Signature]:[Date]";
        authHeader = authHeader.replace( "[API Token]", apiToken );
        authHeader = authHeader.replace( "[Client Code]", clientCode );
        authHeader = authHeader.replace( "[Signature]", signature );
        authHeader = authHeader.replace( "[Date]", dateStr );
        return authHeader;
    }

}
