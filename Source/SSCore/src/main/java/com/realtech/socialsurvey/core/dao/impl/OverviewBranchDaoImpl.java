package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.OverviewBranchDao;
import com.realtech.socialsurvey.core.entities.OverviewBranch;

@Component
public class OverviewBranchDaoImpl extends GenericReportingDaoImpl<OverviewBranch, String> implements OverviewBranchDao
{
    private static final Logger LOG = LoggerFactory.getLogger(OverviewBranchDaoImpl.class);

    @Override
    public String getOverviewBranchId( Long branchid )
    {
        LOG.info("Method to get OverviewBranchId from branchid, getOverviewBranchId() started." );

        Query query = getSession().createSQLQuery( "SELECT overview_branch_id FROM overview_branch WHERE branch_id = :branchid " );
        query.setParameter( "branchid", branchid  );
        String OverviewBranchId = (String) query.uniqueResult();
        
        LOG.info( "Method to get OverviewBranchId from branchid, getOverviewBranchId() finished." );
        return OverviewBranchId;
    }

    @Override
    public OverviewBranch findOverviewBranch( Class<OverviewBranch> entityClass, String overviewBranchid )throws IllegalArgumentException
    {
        return super.findById( entityClass, overviewBranchid );
    }

    
  
}
