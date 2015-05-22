package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;

/*
 * This interface contains methods which are required for Solr import
 */
public interface SolrImportDao extends BranchDao {

	public List<Region> fetchRegionsPage(int offset, int pageSize) throws NoRecordsFetchedException;

	public List<Branch> fetchBranchesPage(int offset, int pageSize) throws NoRecordsFetchedException;

	public List<User> fetchUsersPage(int offset, int pageSize) throws NoRecordsFetchedException;
}
