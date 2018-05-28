package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.JobLogDetails;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

/**
 * @author user
 *
 */
public interface JobLogDetailsDao extends GenericReportingDao<JobLogDetails, Long>
{

    /**
     * Returns JobLog based on last successful run
     * @return
     * @throws InvalidInputException
     */
    public JobLogDetails getJobLogDetailsOfLastSuccessfulRun() throws InvalidInputException;

	/**
	 * @return
	 * @throws InvalidInputException
	 */
	public JobLogDetails getJobLogDetailsOfLatestRun() throws InvalidInputException;

	/**
	 * @param jobLogDetails
	 * @return
	 * @throws InvalidInputException
	 */
	public long insertJobLog(JobLogDetails jobLogDetails) throws InvalidInputException;

	/**
	 * @param entityId
	 * @param entityType
	 * @param jobName
	 * @return
	 * @throws InvalidInputException
	 */
	public JobLogDetails getJobLogDetailsOfLatestRunForEntity(long entityId, String entityType, String jobName)
			throws InvalidInputException;

	/**
	 * @return
	 */
	public JobLogDetails getLastCentrelisedRun();


	public void updateJobLog(JobLogDetails jobLogDetails) throws InvalidInputException; }