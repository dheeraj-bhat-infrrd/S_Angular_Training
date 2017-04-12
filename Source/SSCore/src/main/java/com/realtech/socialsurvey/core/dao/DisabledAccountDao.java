package com.realtech.socialsurvey.core.dao;

import java.util.Date;
import java.util.List;
import com.realtech.socialsurvey.core.entities.DisabledAccount;

/*
 * This interface contains methods which are required for queries and criteria on User table.
 */
public interface DisabledAccountDao extends GenericDao<DisabledAccount, Long> {

	public List<DisabledAccount> getAccountsToDisable(Date maxDisableDate);

	public List<DisabledAccount> getAccountsForPurge(int graceSpan);
	
}
