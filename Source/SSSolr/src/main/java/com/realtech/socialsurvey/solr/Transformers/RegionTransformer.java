package com.realtech.socialsurvey.solr.Transformers;

import java.util.Map;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class RegionTransformer
{
    public Object transformRow( Map<String, Object> row ){
        System.out.println("Entered RegionTransformer");
      //Get branch ID
        @SuppressWarnings ( "resource") MongoClient mongoClient = new MongoClient( "localhost", 27017 );
        MongoDatabase db = mongoClient.getDatabase( "ss_db" );
        Long regionId = (Long) row.get( "REGION_ID" );
        if ( regionId == null ) {
            System.out.println( "Region ID is null" );
        }
        FindIterable<Document> iterable = db.getCollection( "REGION_SETTINGS" ).find( new Document( "iden", regionId ) );
        if ( iterable != null ) {
            Document regionSetting = iterable.first();
            if ( regionSetting != null ) {
                //Get contact details
                Document contactDetails = (Document) regionSetting.get( "contact_details" );
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
        }
        return row;
    }
}
