package com.realtech.socialsurvey.core.dao;

import java.io.Serializable;
import java.util.List;
import com.realtech.socialsurvey.core.entities.UserInvite;

//JIRA: SS-8: By RM05: BOC

/*This interface contains methods which are to be used by various DAOs.
 * Every DAO implements this interface.
 */

public interface UserInviteDao<T, ID extends Serializable> extends GenericDao<T,ID>{
	public List<UserInvite> findByColumn(String encryptedUrlParameter);
}

//JIRA: SS-8: By RM05:EOC