package com.realtech.socialsurvey.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SurveyCsvUploadDao;
import com.realtech.socialsurvey.core.entities.SurveyCsvInfo;


@Repository
public class SurveyCsvUploadDaoImpl implements SurveyCsvUploadDao
{
    
    @Autowired
    private MongoTemplate mongoTemplate; 

    @Override
    public void createEntryForSurveyCsvUpload( SurveyCsvInfo csvInfo )
    {
        mongoTemplate.insert( csvInfo, CommonConstants.SURVEY_CSV_UPLOAD_COLLECTION );
    }

}
