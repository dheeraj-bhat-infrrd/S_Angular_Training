package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.sql.Timestamp;
import java.util.Date;

public interface ReportingDashboardManagement
{
    public void generateReports( int reportId, Date startDate, Date endDate, Date currentDate,
        String firstName, String lastName, Long entityId, String entityType );

    public void createEntryinGenerateReportList( int reportId, Date startDate, Date endDate,
        Date currentDate, String firstName, String lastName, Long entityId, String entityType );


}
