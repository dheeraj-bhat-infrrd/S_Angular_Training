/**
 * 
 */
package com.reporting.arc.db;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.reporting.arc.utils.PropertyReader;


/**
 * @author Subhrajit
 *
 */
public class MongoExportTask
{

    /**
     * @param args
     * @throws Exception 
     */
    public static void mongoExport() throws Exception
    { //  Change it for multiple company using in command.
        System.out.println( "Connected" );
        
        //for windows
        /* 
         String query = PropertyReader.getValueForKey("MONGO.BIN.PATH")+"/mongoexport -d "+PropertyReader.getValueForKey("MONGO.DB.NAME")
         
        				+" -c "+PropertyReader.getValueForKey("MONGO.COLLECTION.NAME")+" --query \"{companyId: {$in : ["+PropertyReader.getValueForKey("MONGO.COMPANY.ID")
        				+"]}}\" -o "+PropertyReader.getValueForKey("MONGO.EXPORT.FILE.PATH")+" --csv --fields "+PropertyReader.getValueForKey("MONGO.EXPORT.FIELDS"); 
       */
       //for linux
        String query = "mongoexport --host "+PropertyReader.getValueForKey( "mongo.host.property" )+" -d "+PropertyReader.getValueForKey("MONGO.DB.NAME")
        +" -c "+PropertyReader.getValueForKey("MONGO.COLLECTION.NAME")+" --query '{\"companyId\": {$in : ["+PropertyReader.getValueForKey("MONGO.COMPANY.ID")
        +"]}}' -o "+PropertyReader.getValueForKey("MONGO.EXPORT.FILE.PATH")+" --csv --fields "+PropertyReader.getValueForKey("MONGO.EXPORT.FIELDS"); 
        Runtime.getRuntime().exec( query );
        System.out.println( "Mongo export done" );

    }
}
