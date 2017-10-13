package com.realtech.socialsurvey.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.realtech.socialsurvey.core.entities.CompanyActiveUsersStats;
import com.realtech.socialsurvey.core.entities.CompanySurveyStatusStats;
import com.realtech.socialsurvey.core.entities.CompanyTransactionsSourceStats;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;
import com.realtech.socialsurvey.core.services.activitymanager.ActivityManagementService;


/**
 * 
 * @author rohit
 *
 */
@Controller
public class TransactionMonitorController
{

    private static final Logger LOG = LoggerFactory.getLogger( TransactionMonitorController.class );

    @Autowired
    private ActivityManagementService activityManagementService;


    /**
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/getcompanyinputtransactionsforpastndays")
    @ResponseBody
    public String getInputTransactionsForCompanyForPastNDays( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getCompanyTransactionsForPastNDays started." );

        String companyIdStr = request.getParameter( "companyId" );
        String noOfDaysStr = request.getParameter( "noOfDays" );
        Long companyId;
        Integer noOfDays;
        Gson gson = new Gson();

        try {
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            try {
                noOfDays = Integer.parseInt( noOfDaysStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            List<CompanyTransactionsSourceStats> companyTransactions = activityManagementService
                .getTransactionsCountForCompanyForPastNDays( companyId, noOfDays );

            LOG.info( "Method getCompanyTransactionsForPastNDays finished." );
            return gson.toJson( companyTransactions );
        } catch ( NonFatalException e ) {
            LOG.error( "Error in getCompanyTransactionsForPastNDays ", e );
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put( "responseText", e.getMessage() );
            return gson.toJson( responseMap );

        }
    }


    /**
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/getcompanysurveystatuscountforpastndays")
    @ResponseBody
    public String getCompanySurveyStatusCountForPastNDays( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getCompanySurveyStatusCountForPastNDays started." );

        String companyIdStr = request.getParameter( "companyId" );
        String noOfDaysStr = request.getParameter( "noOfDays" );
        Long companyId;
        Integer noOfDays;
        Gson gson = new Gson();

        try {
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            try {
                noOfDays = Integer.parseInt( noOfDaysStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            List<CompanySurveyStatusStats> companySurveyStats = activityManagementService
                .getSurveyStatusStatsForCompanyForPastNDays( companyId, noOfDays );

            LOG.info( "Method getCompanySurveyStatusCountForPastNDays finished." );
            return gson.toJson( companySurveyStats );
        } catch ( NonFatalException e ) {
            LOG.error( "Error in getCompanySurveyStatusCountForPastNDays ", e );
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put( "responseText", e.getMessage() );
            return gson.toJson( responseMap );
        }
    }


    /**
     * 
     * @param model
     * @param request
     * @return
     */
    @RequestMapping ( value = "/getcompanyactiveusercountforpastndays")
    @ResponseBody
    public String getActiveUserCountsForCompanyForPastNDays( Model model, HttpServletRequest request )
    {
        LOG.info( "Method getActiveUserCountsForCompanyForPastNDays started." );

        String companyIdStr = request.getParameter( "companyId" );
        String noOfDaysStr = request.getParameter( "noOfDays" );
        Long companyId;
        Integer noOfDays;
        Gson gson = new Gson();
        try {
            try {
                companyId = Long.parseLong( companyIdStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            try {
                noOfDays = Integer.parseInt( noOfDaysStr );
            } catch ( NumberFormatException e ) {
                throw new InvalidInputException( "Wrong company id passed " );
            }

            List<CompanyActiveUsersStats> companyActiveUserCountStats = activityManagementService
                .getActiveUserCountStatsForCompanyForPastNDays( companyId, noOfDays );

            LOG.info( "Method getActiveUserCountsForCompanyForPastNDays finished." );
            return gson.toJson( companyActiveUserCountStats );
        } catch ( NonFatalException e ) {
            LOG.error( "Error in getActiveUserCountsForCompanyForPastNDays ", e );
            Map<String, Object> responseMap = new HashMap<String, Object>();
            responseMap.put( "responseText", e.getMessage() );
            return gson.toJson( responseMap );
        }
    }
}
