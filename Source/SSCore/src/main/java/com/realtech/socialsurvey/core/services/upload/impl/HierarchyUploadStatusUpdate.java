package com.realtech.socialsurvey.core.services.upload.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.HierarchyUploadDao;
import com.realtech.socialsurvey.core.entities.ParsedHierarchyUpload;


@Component
public class HierarchyUploadStatusUpdate
{
    @Autowired
    private HierarchyUploadDao hierarchyUploadDao;

    private static final Logger LOG = LoggerFactory.getLogger( HierarchyUploadStatusUpdate.class );


    @Async
    public void updateParsedHierarchyUpload( long time, int interval, ParsedHierarchyUpload upload )
    {
        try {
            while ( upload.getStatus() == CommonConstants.HIERARCHY_UPLOAD_STATUS_IMPORTING
                || upload.getStatus() == CommonConstants.HIERARCHY_UPLOAD_STATUS_VERIFING ) {

                if ( hierarchyUploadDao.findParsedHierarchyUpload( upload.get_id() ) == null ) {
                    break;
                }

                hierarchyUploadDao.reinsertParsedHierarchyUpload( upload );
                LOG.debug( "updateParsedHierarchyUpload thread called." );
                Thread.sleep( interval );

            }
        } catch ( Exception errorWhileUpdating ) {
            LOG.error(
                "Error occured while updating the ParsedHierarchyUpload object, reason: " + errorWhileUpdating.getMessage() );
        }
    }

}
