package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public interface HierarchyUploadDao
{

    public void reinsertHierarchyUploadObjectForACompany( HierarchyUpload hierarchyUpload ) throws InvalidInputException;


    public void updateStatusForParsedHierarchyUpload( long companyId, int status ) throws InvalidInputException;


    void reinsertParsedHierarchyUpload( ParsedHierarchyUpload parsedHierarchyUpload ) throws InvalidInputException;


    public List<ParsedHierarchyUpload> getActiveHierarchyUploads() throws NoRecordsFetchedException;
    

    public ParsedHierarchyUpload getParsedHierarchyUpload( long companyId )
        throws NoRecordsFetchedException, InvalidInputException;


    public HierarchyUpload getHierarchyUploadByCompany( long companyId ) throws InvalidInputException;

}
