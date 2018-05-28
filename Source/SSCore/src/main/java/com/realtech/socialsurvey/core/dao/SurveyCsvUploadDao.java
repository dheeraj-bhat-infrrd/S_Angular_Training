package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;

public interface SurveyCsvUploadDao
{
    public void createEntryForSurveyCsvUpload( SurveyCsvInfo csvInfo );

    public void updateStatusForSurveyCsvUpload( String _id, int status );

    public List<SurveyCsvInfo> getActiveSurveyCsvUploads();

    public void updateSurveyCsvUpload( SurveyCsvInfo csvInfo );

    public boolean doesFileUploadExist( String fileName, String uploaderEmail );
}
