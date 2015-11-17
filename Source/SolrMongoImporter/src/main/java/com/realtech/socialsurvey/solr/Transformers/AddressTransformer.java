package com.realtech.socialsurvey.solr.Transformers;

import java.util.Map;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;


public class AddressTransformer
{
    public Object transformRow( Map<String, Object> row )
    {
        BasicDBObject contactDetailsObject = (BasicDBObject) row.get( "contact_details" );
        System.out.println("CONTACT DETAILS : " + contactDetailsObject);
        if ( contactDetailsObject != null ) {
            Document contactDetails = Document.parse( new Gson().toJson( contactDetailsObject ) );
            System.out.println( "contact_details : " + contactDetails );
            if ( contactDetails != null ) {
                //Get address 1
                System.out.println( "address 1 : " + contactDetails.get( "address1" ) );
                if ( contactDetails.get( "address1" ) != null ) {
                    String address1 = (String) contactDetails.get( "address1" );
                    row.put( "address1", address1 );
                } else {
                    System.out.println( "address1 is empty" );
                }

                //Get address 2
                System.out.println( "address 2 : " + contactDetails.get( "address2" ) );
                if ( contactDetails.get( "address2" ) != null ) {
                    String address2 = (String) contactDetails.get( "address2" );
                    row.put( "address2", address2 );
                } else {
                    System.out.println( "address2 is empty" );
                }
            }
        }
        return row;
    }
}
