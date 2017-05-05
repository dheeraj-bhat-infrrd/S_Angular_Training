package com.ss.aez.report;

import static com.ss.aez.util.PropertyReader.getValueForKey;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * @author Subhrajit
 *
 */
public class ReportGenerater {

	public static void generateReport() throws Exception {
		
		Properties prop = new Properties();
		InputStream inStream = new BufferedInputStream(new FileInputStream("resources/reportqueries.properties"));
		prop.load(inStream);
		Map<String,ResultSet> resultMap = new HashMap<String,ResultSet>();
		
		Class.forName(getValueForKey("MYSQL.CLASS.NAME"));
		Connection connection = DriverManager.getConnection(getValueForKey("MYSQL.CONNECTION.URL")+"/"+getValueForKey("MYSQL.SCHEMA.NAME"),
				getValueForKey("MYSQL.USERNAME"),getValueForKey("MYSQL.PASSWORD"));
		
		for(Entry<Object, Object> entry : prop.entrySet()){
			Statement statement = connection.createStatement();
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			ResultSet rs = statement.executeQuery(value);
			resultMap.put(key, rs);
		}
		WriteResultToExcel.writeMultipleResultToExcel(resultMap);
		connection.close();
	}
}