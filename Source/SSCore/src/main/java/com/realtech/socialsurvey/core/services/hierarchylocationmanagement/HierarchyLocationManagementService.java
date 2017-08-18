package com.realtech.socialsurvey.core.services.hierarchylocationmanagement;

import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.HierarchyRelocationTarget;
import com.realtech.socialsurvey.core.entities.Region;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.search.exception.SolrException;


public interface HierarchyLocationManagementService
{
    public void relocateRegion( Region regionTobeRelocated, HierarchyRelocationTarget targetLocation )
        throws InvalidInputException, SolrException;


    public void relocateBranch( Branch branchTobeRelocated, HierarchyRelocationTarget targetLocation )
        throws SolrException, InvalidInputException;


    public void relocateUser( User userTobeRelocated, HierarchyRelocationTarget targetLocation )
        throws SolrException, InvalidInputException;


    public void generateEntitiesAndStartRelocationForRegion( long regionId, long targetCompanyId )
        throws InvalidInputException, SolrException;


    public void generateEntitiesAndStartRelocationForBranch( long branchId, long targetCompanyId, long targetRegionId )
        throws InvalidInputException, SolrException;


    public void generateEntitiesAndStartRelocationForUser( long userId, long targetBranchId, int surveyRelcation ) throws InvalidInputException, SolrException;

}
