package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface JobLogDetailsDao extends GenericReportingDao<JobLogDetails, Long>
{

    /**
     * Returns the latest JobLog Detail
     * @return
     */
    public JobLogDetails getLatestJobLogDetails();
    
    /**
     * Returns JobLog based on last successful run
     * @return
     * @throws InvalidInputException
     */
    public JobLogDetails getJobLogDetailsOfLastSuccessfulRun() throws InvalidInputException;
}
