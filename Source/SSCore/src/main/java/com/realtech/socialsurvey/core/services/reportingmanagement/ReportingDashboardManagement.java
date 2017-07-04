package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

public interface ReportingDashboardManagement
{
    public void generateReports( int reportId, Date startDate, Date endDate, Long entityId, String entityType,Company company,
        Long adminUserid ) throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException;

    public void createEntryForReportInFileUpload( int reportId, Date startDate, Date endDate,
        Long entityId, String entityType , Company company , Long adminUserId);

    public List<List<Object>> getSurveyStatsReport( Long entityId, String entityType );




}
