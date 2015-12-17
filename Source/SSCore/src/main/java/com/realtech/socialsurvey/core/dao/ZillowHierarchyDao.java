package com.realtech.socialsurvey.core.dao;

import java.util.Map;
import java.util.Set;

import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface ZillowHierarchyDao
{

    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderBranch( long branchId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderRegion( long regionId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderCompany( long companyId ) throws InvalidInputException;


    public Set<Long> getRegionIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getBranchIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getBranchIdsUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getUserIdsUnderCompanyConnectedToZillow( long companyId, int startIndex, int batchSize )
        throws InvalidInputException;


    public Set<Long> getUserIdsUnderRegionConnectedToZillow( long regionId, int startIndex, int batchSize ) throws InvalidInputException;


    public Set<Long> getUserIdsUnderBranchConnectedToZillow( long branchId, int startIndex, int batchSize ) throws InvalidInputException;

}
