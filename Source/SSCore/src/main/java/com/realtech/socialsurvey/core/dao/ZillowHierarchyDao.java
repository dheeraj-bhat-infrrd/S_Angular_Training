package com.realtech.socialsurvey.core.dao;

import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface ZillowHierarchyDao
{

    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderBranch( long branchId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderRegion( long regionId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderCompany( long companyId ) throws InvalidInputException;


    public Set<Long> getRegionsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getBranchesUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getBranchesUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getUsersUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getUsersUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize ) throws InvalidInputException;


    public Set<Long> getUsersUnderBranchConnectedToZillow( long branchId, int startIndex, int batchSize ) throws InvalidInputException;

}
