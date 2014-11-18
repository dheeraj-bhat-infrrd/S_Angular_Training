package com.realtech.socialsurvey.core.dao.impl;

import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.ParentDao;
import com.realtech.socialsurvey.core.entities.AccountsMaster;

public class AccountsMasterDaoImpl extends 
	GenericDaoImpl<AccountsMaster, Integer>
	implements ParentDao<AccountsMaster, Integer>{
	 @Transactional
	    public AccountsMaster save(AccountsMaster accountMaster) {
	        return super.save(accountMaster);
	    } 
}