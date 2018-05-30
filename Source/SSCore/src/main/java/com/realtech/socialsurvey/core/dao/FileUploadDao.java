package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.FileUpload;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

public interface FileUploadDao extends GenericDao<FileUpload, Long>
{

    List<FileUpload> findRecentActivityForReporting( long entityId, String entityType, int startIndex, int batchSize );

    long getRecentActivityCountForReporting( long entityId, String entityType );

    void changeShowOnUiStatus( FileUpload fileUpload );

	/**
	 * Dao method to get the latest activity of entity type.
	 * @param entityId
	 * @return
	 */
	FileUpload getLatestActivityForReporting(Long entityId);

	int updateStatus(long filUploadId, int status) throws InvalidInputException;

    int updateStatusAndFileName( long fileUploadId, int status, String location ) throws InvalidInputException;
    
    /**
     * Method to get recent activity for social monitor reporting
     * @param entityId
     * @param entityType
     * @param startIndex
     * @param batchSize
     * @return
     */
    List<FileUpload> findRecentActivityForSocialMonitorReporting( long entityId, String entityType, int startIndex, int batchSize );
    
    /**
     * Method to get recent activity count for social monitor reporting
     * @param entityId
     * @param entityType
     * @return
     */
    public long getRecentActivityCountForSocialMonitorReporting(long entityId , String entityType);
}
