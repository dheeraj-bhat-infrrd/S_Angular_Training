package com.realtech.socialsurvey.core.workbook.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.SurveyPreInitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.vo.SurveyPreInitiationList;
import com.realtech.socialsurvey.core.vo.UserList;


/**
 * @author RareMile
 *
 */
@Component
public class WorkbookData
{
    public final String EXCEL_FORMAT = "application/vnd.ms-excel";
    public final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    public final String EXCEL_FILE_EXTENSION = ".xlsx";


    public Map<String, List<Object>> getCorruptSurveyDataToBeWrittenInSheet( SurveyPreInitiationList surveyPreInitiationListVO )
    {
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( SurveyPreInitiation survey : surveyPreInitiationListVO.getSurveyPreInitiationList() ) {
            surveyDetailsToPopulate.add( survey.getAgentName() );
            surveyDetailsToPopulate.add( survey.getAgentEmailId() );
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getEngagementClosedTime() );
            surveyDetailsToPopulate.add( survey.getErrorCodeDescription() );
            data.put( ( ++counter ).toString(), surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( "Agent Name" );
        surveyDetailsToPopulate.add( "Transaction Email" );
        surveyDetailsToPopulate.add( "Customer First Name" );
        surveyDetailsToPopulate.add( "Customer Last Name" );
        surveyDetailsToPopulate.add( "Customer Email" );
        surveyDetailsToPopulate.add( "Date" );
        surveyDetailsToPopulate.add( "Reason" );
        data.put( "1", surveyDetailsToPopulate );
        return data;
    }


    public Map<String, List<Object>> getMappedSurveyDataToBeWrittenInSheet( UserList userList )
    {
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> userDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( User user : userList.getUsers() ) {
            userDetailsToPopulate.add( user.getFirstName() );
            userDetailsToPopulate.add( user.getMappedEmails() );
            data.put( ( ++counter ).toString(), userDetailsToPopulate );
            userDetailsToPopulate = new ArrayList<>();
        }
        userDetailsToPopulate.add( "Name" );
        userDetailsToPopulate.add( "Mapped Email Id(s)" );
        data.put( "1", userDetailsToPopulate );
        return data;
    }


    public Map<String, List<Object>> getUnmatchedOrProcessedSurveyDataToBeWrittenInSheet(
        SurveyPreInitiationList surveyPreInitiationListVO )
    {
        Map<String, List<Object>> data = new TreeMap<>();
        List<Object> surveyDetailsToPopulate = new ArrayList<>();
        Integer counter = 1;
        for ( SurveyPreInitiation survey : surveyPreInitiationListVO.getSurveyPreInitiationList() ) {
            surveyDetailsToPopulate.add( survey.getAgentName() );
            surveyDetailsToPopulate.add( survey.getAgentEmailId() );
            surveyDetailsToPopulate.add( survey.getCustomerFirstName() );
            surveyDetailsToPopulate.add( survey.getCustomerLastName() );
            surveyDetailsToPopulate.add( survey.getCustomerEmailId() );
            surveyDetailsToPopulate.add( survey.getEngagementClosedTime() );
            data.put( ( ++counter ).toString(), surveyDetailsToPopulate );
            surveyDetailsToPopulate = new ArrayList<>();
        }
        surveyDetailsToPopulate.add( "Agent Name" );
        surveyDetailsToPopulate.add( "Transaction Email" );
        surveyDetailsToPopulate.add( "Customer First Name" );
        surveyDetailsToPopulate.add( "Customer Last Name" );
        surveyDetailsToPopulate.add( "Customer Email" );
        surveyDetailsToPopulate.add( "Date" );
        data.put( "1", surveyDetailsToPopulate );
        return data;
    }

}
