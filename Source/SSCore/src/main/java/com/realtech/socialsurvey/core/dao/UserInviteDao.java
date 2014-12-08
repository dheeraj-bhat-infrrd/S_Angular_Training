package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.UserInvite;

//JIRA: SS-8: By RM05: BOC

/*
 * This interface contains methods for USER_INVITE TABLE related operations.
 */

public interface UserInviteDao extends GenericDao<UserInvite, Integer>{
	public List<UserInvite> findByUrlParameter(String encryptedUrlParameter);
}

//JIRA: SS-8: By RM05:EOC