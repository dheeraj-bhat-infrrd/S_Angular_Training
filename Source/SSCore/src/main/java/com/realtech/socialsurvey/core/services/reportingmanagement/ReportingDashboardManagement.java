package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.SurveyStatsReportBranch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

public interface ReportingDashboardManagement
{
    public void createEntryInFileUploadForReporting( int reportId, Date startDate, Date endDate, Long entityId, String entityType,Company company,
        Long adminUserid ) throws InvalidInputException, NoRecordsFetchedException, FileNotFoundException, IOException;

    public List<List<Object>> getSurveyStatsReport( Long entityId, String entityType );

    public List<List<Object>> getRecentActivityList( Long entityId, String entityType , int startIndex, int batchSize  ) throws InvalidInputException;

    public Long getRecentActivityCount( Long entityId, String entityType );

    String generateSurveyStatsForReporting( Long entityId, String entityType , Long userId ) throws UnsupportedEncodingException, NonFatalException;

    List<List<Object>> getUserAdoptionReport( Long entityId, String entityType );

    String generateUserAdoptionForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException;

    void deleteRecentActivity( Long fileUploadId );

    List<List<Object>> getCompanyUserReport( Long entityId, String entityType );

    String generateCompanyUserForReporting( Long entityId, String entityType, Long userId )
        throws UnsupportedEncodingException, NonFatalException;

	List<List<Object>> getSurveyResultsCompanyReport(Long entityId, String EntityType,Timestamp startDate, Timestamp endDate);
	
	String generateSurveyResultsCompanyForReporting( Long entityId, String entityType, Long userId, Timestamp startDate, Timestamp endDate )
		        throws UnsupportedEncodingException, NonFatalException;

	List<String> getSurveyResponseData(String surveyDetailsId);

    List<List<Object>> getSurveyTransactionReport( Long entityId, String entityType, Timestamp startDate, Timestamp endDate );

    String generateSurveyTransactionForReporting( Long entityId, String entityType, Long userId, Timestamp startDate,
        Timestamp endDate ) throws UnsupportedEncodingException, NonFatalException;
    
    List<List<Object>> getUserRankingThisYear(String entityType, Long entityId,int year,int startIndex,int batchSize);
    
    List<List<Object>> getUserRankingThisMonth(String entityType, Long entityId,int month,int year,int startIndex,int batchSize);
    
    List<List<Object>> getUserRankingPastMonth(String entityType, Long entityId,int month,int year,int startIndex,int batchSize);
    
    List<List<Object>> getUserRankingPastYear(String entityType, Long entityId,int year,int startIndex,int batchSize);

    Map<String, Object> fetchRankingCountThisYear(long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;

    Map<String, Object> fetchRankingCountThisMonth(long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;

    Map<String, Object> fetchRankingCountPastYear( long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;

    Map<String, Object> fetchRankingCountPastMonth( long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;

    Map<String, Object> fetchRankingRankCountThisYear( long userId, long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;

    Map<String, Object> fetchRankingRankCountThisMonth( long userId, long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;

    Map<String, Object> fetchRankingRankCountPastYear( long userId, long entityId, String entityType, int year, int BatchSize )
        throws NonFatalException;

    Map<String, Object> fetchRankingRankCountPastMonth( long userId, long entityId, String entityType, int year, int month,
        int BatchSize ) throws NonFatalException;

    Map<String, Object> fetchRankingRankCountPastYears( long userId, long entityId, String entityType, int BatchSize )
        throws NonFatalException;

    Map<String, Object> fetchRankingCountPastYears( long entityId, String entityType, int BatchSize ) throws NonFatalException;

    List<List<Object>> getUserRankingPastYears( String entityType, Long entityId, int startIndex, int batchSize );

    
}
