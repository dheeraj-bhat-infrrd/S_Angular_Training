package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;

public interface UsercountModificationNotificationDao extends GenericDao<UsercountModificationNotification, Long> {

	/**
	 * delete based on id and status
	 * @param userCountModificationNotification
	 * @param status
	 */
	public void deleteByIdAndStatus(UsercountModificationNotification userCountModificationNotification, int status);

}
