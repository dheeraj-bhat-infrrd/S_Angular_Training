/**
 * 
 */
package com.realtech.socialsurvey.core.services.crmbatchtrackerhistory.impl;

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.CrmBatchTrackerHistory;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.crmbatchtrackerhistory.CRMBatchTrackerHistoryService;

/**
 * @author Dipak
 *
 */
public class CRMBatchTrackerHistoryServiceImpl implements
		CRMBatchTrackerHistoryService {
	
    private static final Logger LOG = LoggerFactory.getLogger( CRMBatchTrackerHistoryServiceImpl.class );

    @Autowired
    private GenericDao<CrmBatchTrackerHistory, Long> crmBatchTrackerHistoryDao;

	@Override
	@Transactional
	public void insertCrmBatchTrackerHistory(int countOfRecordsFetched, int crmBatchTrackerId)throws InvalidInputException {
		// TODO Auto-generated method stub
		
	    LOG.debug( "method insertCrmBatchTrackerHistory started" );
		if(crmBatchTrackerId==0)
		{ 
		LOG.error( "CrmBatchTracker Id must have valid Id" );
        throw new InvalidInputException( "Cannot Insert CrmBatchTraxkerHistory since crmBatchTracker Not Found" );
		}
			CrmBatchTrackerHistory crmBatchTrackerHistory=new CrmBatchTrackerHistory();
			crmBatchTrackerHistory.setCountOfRecordsFetched(countOfRecordsFetched);
			crmBatchTrackerHistory.setCrmBatchTrackerID(crmBatchTrackerId);
			crmBatchTrackerHistory.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			crmBatchTrackerHistory.setCreatedBy(CommonConstants.CRM_SOURCE_DOTLOOP);
			crmBatchTrackerHistory.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			crmBatchTrackerHistory.setModifiedBy(CommonConstants.CRM_SOURCE_DOTLOOP);
			crmBatchTrackerHistory.setStatus(CommonConstants.STATUS_ACTIVE);
			crmBatchTrackerHistoryDao.save(crmBatchTrackerHistory);
			
	    LOG.debug( "method insertCrmBatchTrackerHistory ended" );
		
	}
	
	

}
