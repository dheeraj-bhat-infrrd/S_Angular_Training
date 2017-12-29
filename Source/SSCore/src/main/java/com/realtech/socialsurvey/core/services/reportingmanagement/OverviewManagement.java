package com.realtech.socialsurvey.core.services.reportingmanagement;

import java.util.Map;

import com.realtech.socialsurvey.core.entities.Digest;
import com.realtech.socialsurvey.core.entities.OverviewBranch;
import com.realtech.socialsurvey.core.entities.OverviewCompany;
import com.realtech.socialsurvey.core.entities.OverviewRegion;
import com.realtech.socialsurvey.core.entities.OverviewUser;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;


public interface OverviewManagement
{
    public OverviewUser fetchOverviewUserDetails( long entityId, String entityType ) throws NonFatalException;


    public OverviewBranch fetchOverviewBranchDetails( long entityId, String entityType ) throws NonFatalException;


    public OverviewRegion fetchOverviewRegionDetails( long entityId, String entityType ) throws NonFatalException;


    public OverviewCompany fetchOverviewCompanyDetails( long entityId, String entityType ) throws NonFatalException;


    public Map<String, Object> fetchOverviewDetailsBasedOnMonth( long entityId, String entityType, int month, int year )
        throws NonFatalException;


    Map<String, Object> fetchOverviewDetailsBasedOnYear( long entityId, String entityType, int year ) throws NonFatalException;


    Map<String, Object> fetchAllTimeOverview( long entityId, String entityType ) throws NonFatalException;


    Map<String, Object> fetchSpsAllTime( long entityId, String entityType ) throws NonFatalException;


    public Digest fetchDigestDataForAHierarchy( String profileLevel, long entityId, int month, int year )
        throws InvalidInputException;

}
