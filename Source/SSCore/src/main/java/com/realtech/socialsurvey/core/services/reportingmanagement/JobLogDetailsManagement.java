package com.realtech.socialsurvey.core.services.reportingmanagement;

import com.realtech.socialsurvey.core.entities.JobLogDetailsResponse;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface JobLogDetailsManagement
{

    public JobLogDetailsResponse getLastSuccessfulEtlTime() throws InvalidInputException;
}
