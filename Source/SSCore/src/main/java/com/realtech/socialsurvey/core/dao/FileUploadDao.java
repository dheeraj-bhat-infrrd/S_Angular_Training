package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.FileUpload;

public interface FileUploadDao extends GenericDao<FileUpload, Long>
{

    List<FileUpload> findRecentActivityForReporting( long entityId, String entityType, int startIndex, int batchSize );

    long getRecentActivityCountForReporting( long entityId, String entityType );

    void changeShowOnUiStatus( FileUpload fileUpload );

}
