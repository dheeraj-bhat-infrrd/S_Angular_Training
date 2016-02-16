package com.realtech.socialsurvey.core.services.upload;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;



public interface HierarchyDownloadService
{

    public XSSFWorkbook generateHierarchyDownloadReport( HierarchyUpload hierarchyUpload, Company company )
        throws InvalidInputException, NoRecordsFetchedException;


    public HierarchyUpload fetchUpdatedHierarchyStructure( Company company ) throws InvalidInputException;

}
