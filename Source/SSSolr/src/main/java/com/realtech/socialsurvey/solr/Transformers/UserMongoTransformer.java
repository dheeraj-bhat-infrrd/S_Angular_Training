package com.realtech.socialsurvey.solr.Transformers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;


public class UserMongoTransformer
{
    public Object transformRow( Map<String, Object> row )
    {
        //Set Review count
        if ( row.get( "reviewCount" ) != null ) {
            Long reviewCount = (Long) row.get( "reviewCount" );
            if ( reviewCount != null ) {
                row.put( "reviewCount", reviewCount );
            }
        }
        //profileUrl
        if ( row.get( "profileUrl" ) != null ) {
            String profileUrl = (String) row.get( "profileUrl" );
            row.put( "profileUrl", profileUrl );
        }
        //profileName
        if ( row.get( "profileName" ) != null ) {
            String profileName = (String) row.get( "profileName" );
            row.put( "profileName", profileName );
        }
        //profileImageUrl
        if ( row.get( "profileImageUrl" ) != null ) {
            String profileImageUrl = (String) row.get( "profileImageUrl" );
            row.put( "profileImageUrl", profileImageUrl );
        }
        //contact details
        BasicDBObject contactDetailsObject = (BasicDBObject) row.get( "contact_details" );
        System.out.println( "CONTACT DETAILS : " + contactDetailsObject );
        if ( contactDetailsObject != null ) {
            Document contactDetails = Document.parse( new Gson().toJson( contactDetailsObject ) );
            System.out.println( "contact_details : " + contactDetails );
            if ( contactDetails != null ) {
                //title
                System.out.println( "title : " + contactDetails.get( "title" ) );
                if ( contactDetails.get( "title" ) != null ) {
                    String title = (String) contactDetails.get( "title" );
                    row.put( "title", title );
                } else {
                    System.out.println( "Title is empty" );
                }
                //aboutMe
                System.out.println( "aboutMe : " + contactDetails.get( "about_me" ) );
                if ( contactDetails.get( "about_me" ) != null ) {
                    String aboutMe = (String) contactDetails.get( "about_me" );
                    row.put( "aboutMe", aboutMe );
                } else {
                    System.out.println( "aboutMe is empty" );
                }
            }
        }
        //get mongo fields
        /*@SuppressWarnings ( "resource") MongoClient mongoClient = new MongoClient( "localhost", 27017 );
        MongoDatabase db = mongoClient.getDatabase( "ss_db" );
        Long userId = (Long) row.get( "USER_ID" );
        if ( userId == null ) {
            System.out.println( "User ID is null" );
        }
        FindIterable<Document> iterable = db.getCollection( "AGENT_SETTINGS" ).find( new Document( "iden", userId ) );
        if ( iterable != null ) {
            Document agentSetting = iterable.first();
            if ( agentSetting != null ) {
                //reviewCount
                if ( agentSetting.get( "reviewCount" ) != null ) {
                    Long reviewCount = (Long) agentSetting.get( "reviewCount" );
                    if ( reviewCount != null ) {
                        row.put( "reviewCount", reviewCount );
                    }
                }
                Document contactDetails = (Document) agentSetting.get( "contact_details" );
                System.out.println( "contact_details : " + contactDetails );
                if ( contactDetails != null ) {
                    //title
                    System.out.println( "title : " + contactDetails.get( "title" ) );
                    if ( contactDetails.get( "title" ) != null ) {
                        String title = (String) contactDetails.get( "title" );
                        row.put( "title", title );
                    } else {
                        System.out.println( "Title is empty" );
                    }
                    //aboutMe
                    System.out.println( "aboutMe : " + contactDetails.get( "about_me" ) );
                    if ( contactDetails.get( "about_me" ) != null ) {
                        String aboutMe = (String) contactDetails.get( "about_me" );
                        row.put( "aboutMe", aboutMe );
                    } else {
                        System.out.println( "aboutMe is empty" );
                    }
                }
                //profileUrl
                if ( agentSetting.get( "profileUrl" ) != null ) {
                    String profileUrl = (String) agentSetting.get( "profileUrl" );
                    row.put( "profileUrl", profileUrl );
                }
                //profileName
                if ( agentSetting.get( "profileName" ) != null ) {
                    String profileName = (String) agentSetting.get( "profileName" );
                    row.put( "profileName", profileName );
                }
                //profileImageUrl
                if ( agentSetting.get( "profileImageUrl" ) != null ) {
                    String profileImageUrl = (String) agentSetting.get( "profileImageUrl" );
                    row.put( "profileImageUrl", profileImageUrl );
                }
            }
        }*/
        return row;
    }
}