package com.realtech.socialsurvey.core.services.upload;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


@Component
public interface HierarchyUploadService
{

    public boolean processHierarchyUploadXlsx( ParsedHierarchyUpload parsedHierarchyUpload ) throws InvalidInputException;


    public List<ParsedHierarchyUpload> findInitiatedHierarchyUploads() throws NoRecordsFetchedException;


    public ParsedHierarchyUpload insertUploadHierarchyXlsxDetails( User user, String fileLocalName, String uploadedFileName,
        Date uploadedDate, boolean isInAppendMode ) throws InvalidInputException;


    public boolean updateStatusForParsedHierarchyUpload( long companyId, int hierarchyUploadStatusInitiated )
        throws InvalidInputException;


    public ParsedHierarchyUpload getParsedHierarchyUpload( long companyId )
        throws NoRecordsFetchedException, InvalidInputException;


    public boolean reinsertParsedHierarchyUpload( ParsedHierarchyUpload upload ) throws InvalidInputException;
}
