package com.realtech.socialsurvey.core.dao.impl;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.dao.OverviewCompanyDao;
import com.realtech.socialsurvey.core.entities.OverviewCompany;

@Component
public class OverviewCompanyDaoImpl extends GenericDaoImpl<OverviewCompany, String> implements OverviewCompanyDao
{

    private static final Logger LOG = LoggerFactory.getLogger(OverviewCompanyDaoImpl.class);

    @Override
    @Transactional
    public String getOverviewCompanyId( Long companyid )
    {
        LOG.info("Method to get OverviewCompanyId from CompanyId, getOverviewCompanyId() started." );

        Query query = getSession().createSQLQuery( "SELECT overview_company_id FROM overview_company WHERE company_id = :companyId " );
        query.setParameter( "companyId", companyid  );
        String OverviewCompanyId = (String) query.uniqueResult();
        
        LOG.info(
            "Method to get OverviewCompanyId from CompanyId, getOverviewCompanyId() finished." );
        return OverviewCompanyId;
    }

    @Override
    @Transactional
    public OverviewCompany findOverviewCompany( Class<OverviewCompany> entityClass, String overviewCompanyid )
    {
        return super.findById( entityClass, overviewCompanyid );
    }

}
