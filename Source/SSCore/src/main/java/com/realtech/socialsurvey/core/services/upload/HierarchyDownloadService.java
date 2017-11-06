
package com.realtech.socialsurvey.core.services.upload;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.entities.HierarchyUploadAggregate;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;


public interface HierarchyDownloadService
{

    public XSSFWorkbook generateHierarchyDownloadReport( HierarchyUpload hierarchyUpload, Company company )
        throws InvalidInputException, NoRecordsFetchedException;


    public HierarchyUploadAggregate fetchUpdatedHierarchyUploadStructure( Company company ) throws InvalidInputException;

}
