/**
 * 
 */
package com.ss.aez.main;

import static com.ss.aez.util.PropertyReader.*;
/**
 * @author Subhrajit
 *
 */
public class MongoExportTask {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void mongoExport() throws Exception { //  Change it for multiple company using in command.
		String query = getValueForKey("MONGO.BIN.PATH")+"/mongoexport -d "+getValueForKey("MONGO.DB.NAME")
						+" -c "+getValueForKey("MONGO.COLLECTION.NAME")+" --query '{\"companyId\": {$in : ["+getValueForKey("MONGO.COLLECTION.NAME")
						+"]}}' -o "+getValueForKey("MONGO.EXPORT.FILE.PATH")+" --csv --fields "+getValueForKey("MONGO.EXPORT.FIELDS");
		Runtime.getRuntime().exec(query);
	}
}
