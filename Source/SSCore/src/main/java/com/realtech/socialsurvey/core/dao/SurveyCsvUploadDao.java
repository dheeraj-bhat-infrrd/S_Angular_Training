package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;

public interface SurveyCsvUploadDao
{
    public void createEntryForSurveyCsvUpload( SurveyCsvInfo csvInfo ); 
}
