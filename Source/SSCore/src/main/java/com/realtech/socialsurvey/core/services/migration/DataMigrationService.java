/**
 * 
 */
package com.realtech.socialsurvey.core.services.migration;

import java.io.IOException;

/**
 * @author Subhrajit
 * Service class for data migration activity.
 */
public interface DataMigrationService {
	
	/**
	 * Service method to migrate MCQ to NPS in mongo.
	 * @param companyId
	 * @param question
	 * @return
	 * @throws IOException 
	 */
	public void migrateMCQtoNPSMongo(long companyId, int questionId, String question) throws IOException;

}
