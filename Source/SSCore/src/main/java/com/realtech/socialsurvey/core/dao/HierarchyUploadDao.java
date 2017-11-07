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
    
    
    /**
     * Method to save hierarchy upload in UPLOAD_HIERARCHY_DETAILS collection
     * @param hierarchyUpload
     * @throws InvalidInputException
     */
    public void saveUploadHierarchyDetails( HierarchyUpload hierarchyUpload ) throws InvalidInputException;
    
    
    /**
     * Method to get hieararchy upload object from UPLOAD_HIERARCHY_DETAILS collection
     * @param companyId
     * @return
     * @throws InvalidInputException
     */
    public HierarchyUpload getUploadHierarchyDetailsByCompany( long companyId ) throws InvalidInputException;

}
