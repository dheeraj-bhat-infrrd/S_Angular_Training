package com.realtech.socialsurvey.core.services.reportingmanagement;

import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface JobLogDetailsManagement
{

    public JobLogDetailsResponse getLastSuccessfulEtlTime() throws InvalidInputException;

	public boolean getIfEtlIsRunning() throws InvalidInputException;

	public JobLogDetailsResponse getLastRunForEntity(long entityId, String entityType) throws InvalidInputException;

	public long insertJobLog(long entityId, String entityType, String jobName, String status) throws InvalidInputException;

	public void recalEtl(long companyId, long jobLogId) throws InvalidInputException;
}
