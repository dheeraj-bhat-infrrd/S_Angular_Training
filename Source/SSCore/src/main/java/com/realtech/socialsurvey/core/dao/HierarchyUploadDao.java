package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.HierarchyUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface HierarchyUploadDao
{

    /**
     * Method to save hierarchy upload in mongo
     * @param hierarchyUpload
     * @throws InvalidInputException 
     */
    public void saveHierarchyUploadObject( HierarchyUpload hierarchyUpload ) throws InvalidInputException;


    /**
     * Method to fetch hierarchy upload for company
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public HierarchyUpload getHierarchyUploadByCompany( long companyId ) throws InvalidInputException;

}
