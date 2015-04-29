package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.realtech.socialsurvey.core.dao.UsercountModificationNotificationDao;
import com.realtech.socialsurvey.core.entities.UsercountModificationNotification;

@Component("userCountModificationNotification")
public class UsercountModificationNotificationDaoImpl extends GenericDaoImpl<UsercountModificationNotification, Long> implements
		UsercountModificationNotificationDao {

	private static final Logger LOG = LoggerFactory.getLogger(UsercountModificationNotificationDaoImpl.class);
	
	@Override
	public void deleteByIdAndStatus(UsercountModificationNotification userCountModificationNotification, int status) {
		LOG.info("Deleting user count modification with id "+userCountModificationNotification.getUsercountModificationNotificationId()+" and status "+status);
		Query query = getSession().getNamedQuery("UsercountModificationNotification.deleteByIdAndStatus");
		query.setParameter(0, userCountModificationNotification.getUsercountModificationNotificationId());
		query.setParameter(1, status);
		query.executeUpdate();
		LOG.info("Deleted the user count modification record "+userCountModificationNotification.getUsercountModificationNotificationId());
	}

}
