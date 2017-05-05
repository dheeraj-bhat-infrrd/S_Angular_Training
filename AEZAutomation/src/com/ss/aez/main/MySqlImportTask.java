/**
 * 
 */
package com.ss.aez.main;

import static com.ss.aez.util.PropertyReader.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;


/**
 * @author Subhrajit
 *
 */
public class MySqlImportTask {

	/**
	 * @throws Exception 
	 */
	public static void importToMysql() throws Exception {
		String loadData = "load data local infile \""+getValueForKey("MONGO.EXPORT.FILE.PATH")+"\" into table "+getValueForKey("MYSQL.TABLE.NAME")
						  +" columns terminated by ',' optionally enclosed by '\"' escaped by '\"' lines terminated by '\\n' ignore 1 lines;";
		String truncateData = "truncate table "+getValueForKey("MYSQL.TABLE.NAME")+";";
		
		Class.forName(getValueForKey("MYSQL.CLASS.NAME"));
		Connection connection = DriverManager.getConnection(getValueForKey("MYSQL.CONNECTION.URL")+"/"+getValueForKey("MYSQL.SCHEMA.NAME"),
								getValueForKey("MYSQL.USERNAME"),getValueForKey("MYSQL.PASSWORD"));
		Statement statement = connection.createStatement();
		statement.executeQuery(truncateData);
		statement.executeQuery(loadData);
	}

}
