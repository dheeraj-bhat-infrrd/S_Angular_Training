package com.realtech.socialsurvey.core.dao;

import java.util.Map;
import java.util.Set;
import com.realtech.socialsurvey.core.exception.InvalidInputException;


public interface ZillowHierarchyDao
{

    public Map<String, Set<Long>> getIdsUnderBranchConnectedToZillow( long branchId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderBranch( long branchId ) throws InvalidInputException;


    public Map<String, Set<Long>> getIdsUnderRegionConnectedToZillow( long regionId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderRegion( long regionId ) throws InvalidInputException;


    public Map<String, Set<Long>> getIdsUnderCompanyConnectedToZillow( long companyId ) throws InvalidInputException;


    public Map<String, Long> getZillowReviewCountAndTotalScoreForAllUnderCompany( long companyId ) throws InvalidInputException;

}
