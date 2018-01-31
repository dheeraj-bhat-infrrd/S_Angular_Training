package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface JobLogDetailsDao extends GenericReportingDao<JobLogDetails, Long>
{

    /**
     * Returns JobLog based on last successful run
     * @return
     * @throws InvalidInputException
     */
    public JobLogDetails getJobLogDetailsOfLastSuccessfulRun() throws InvalidInputException;

	public JobLogDetails getJobLogDetailsOfLatestRun() throws InvalidInputException;

	public long insertJobLog(JobLogDetails jobLogDetails) throws InvalidInputException;

	JobLogDetails getJobLogDetailsOfLatestRunForEntity(long entityId, String entityType, String jobName)
			throws InvalidInputException;
}
