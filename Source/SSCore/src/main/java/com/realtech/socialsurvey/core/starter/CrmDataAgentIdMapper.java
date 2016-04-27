package com.realtech.socialsurvey.core.starter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.batchtracker.BatchTrackerService;
import com.realtech.socialsurvey.core.services.mail.EmailServices;
import com.realtech.socialsurvey.core.services.mail.UndeliveredEmailException;
import com.realtech.socialsurvey.core.services.organizationmanagement.UserManagementService;
import com.realtech.socialsurvey.core.services.surveybuilder.SurveyHandler;


public class CrmDataAgentIdMapper extends QuartzJobBean
{

    public static final Logger LOG = LoggerFactory.getLogger( CrmDataAgentIdMapper.class );

    private UserManagementService userManagementService;


    @Override
    protected void executeInternal( JobExecutionContext jobExecutionContext ) throws JobExecutionException
    {
        // initialize the dependencies
        initializeDependencies( jobExecutionContext.getMergedJobDataMap() );
        LOG.info( "Executing CrmDataAgentIdMapper" );
        userManagementService.crmDataAgentIdMApper();
    }


    @SuppressWarnings ( "unchecked")


    private void initializeDependencies( JobDataMap jobMap )
    {
        userManagementService = (UserManagementService) jobMap.get( "userManagementService" );
    }
}
