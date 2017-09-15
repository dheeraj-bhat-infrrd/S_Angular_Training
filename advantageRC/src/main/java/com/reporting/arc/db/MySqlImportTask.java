/**
 * 
 */
package com.reporting.arc.db;

import com.reporting.arc.utils.PropertyReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


/**
 * @author Subhrajit
 *
 */
public class MySqlImportTask
{

    /**
     * @throws Exception 
     */
    public static void importToMysql() throws Exception
    {
        String loadData = "load data local infile \"" + PropertyReader.getValueForKey( "MONGO.EXPORT.FILE.PATH" )
            + "\" into table " + PropertyReader.getValueForKey( "MYSQL.TABLE.NAME" )
            + " columns terminated by ',' optionally enclosed by '\"' escaped by '\"' lines terminated by '\\n' ignore 1 lines (_id,customerFirstName,customerLastName,customerEmail,surveyPreIntitiationId,surveyTransactionDate,surveyCompletedDate,createdOn,modifiedOn,companyId,agentId,agentName,sourceId,source,reminderCount,retakeSurvey,surveyClicked,mood,agreedToShare,showSurveyOnUI,underResolution,score,stage,isAbusive,isAbuseRepByUser);";
        String truncateData = "DELETE FROM " + PropertyReader.getValueForKey( "MYSQL.TABLE.NAME" ) + ";";

        Class.forName( PropertyReader.getValueForKey( "MYSQL.CLASS.NAME" ) );
       //for local on windows 
       /*Connection connection = DriverManager.getConnection(
            PropertyReader.getValueForKey( "MYSQL.CONNECTION.URL" ) + "/" + PropertyReader.getValueForKey( "MYSQL.SCHEMA.NAME" ),
            PropertyReader.getValueForKey( "MYSQL.USERNAME" ), PropertyReader.getValueForKey( "MYSQL.PASSWORD" ) );
        * 
        */
        //for connecting to info
        Connection connection = DriverManager.getConnection(
            PropertyReader.getValueForKey( "db.url.property" ) + "/" + PropertyReader.getValueForKey( "MYSQL.SCHEMA.NAME" ),
            PropertyReader.getValueForKey( "db.user.property" ), PropertyReader.getValueForKey( "db.password.property" ) );
        Statement statement = connection.createStatement();
        statement.executeUpdate( truncateData );
        statement.executeQuery( loadData );
    }

}
