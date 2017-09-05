package com.reporting.arc.reports;

import static com.reporting.arc.utils.PropertyReader.getValueForKey;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.reporting.arc.utils.PropertyReader;

import java.util.Properties;


/**
 * @author Subhrajit
 *
 */
public class ReportGenerater
{

    public static void generateReport() throws Exception
    {

        Properties prop = new Properties();
        InputStream inStream = ReportGenerater.class.getClassLoader().getResourceAsStream( "reportqueries.properties" );
        ;
        prop.load( inStream );
        Map<String, ResultSet> resultMap = new HashMap<String, ResultSet>();

        Class.forName( getValueForKey( "MYSQL.CLASS.NAME" ) );
        //for local windows
        /*Connection connection = DriverManager.getConnection(
            getValueForKey( "MYSQL.CONNECTION.URL" ) + "/" + getValueForKey( "MYSQL.SCHEMA.NAME" ),
            getValueForKey( "MYSQL.USERNAME" ), getValueForKey( "MYSQL.PASSWORD" ) );
         * 
         */
        //for connecting to info
        Connection connection = DriverManager.getConnection(
            PropertyReader.getValueForKey( "db.url.property" ) + "/" + PropertyReader.getValueForKey( "MYSQL.SCHEMA.NAME" ),
            PropertyReader.getValueForKey( "db.user.property" ), PropertyReader.getValueForKey( "db.password.property" ) );

        for ( Entry<Object, Object> entry : prop.entrySet() ) {
            Statement statement = connection.createStatement();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            System.out.println("query to get : " + key + " : " + value  );
            ResultSet rs = statement.executeQuery( value );
            rs.last();
            int total = rs.getRow();
            System.out.println(total);
            rs.beforeFirst();
            System.out.println( rs.toString() );
            resultMap.put( key, rs );
        }
        WriteResultToExcel.writeMultipleResultToExcel( resultMap );
        connection.close();
    }
}