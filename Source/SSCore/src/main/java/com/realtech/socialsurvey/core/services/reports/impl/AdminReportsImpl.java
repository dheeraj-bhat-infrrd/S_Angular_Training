package com.realtech.socialsurvey.core.services.reports.impl;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyReportsSearch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.reports.AdminReports;


@Component
public class AdminReportsImpl implements AdminReports
{

    private static final Logger LOG = LoggerFactory.getLogger( AdminReportsImpl.class );

    @Autowired
    private CompanyDao companyDao;


    @Override
    @Transactional ( readOnly = true)
    public List<Company> companyCreationReports( CompanyReportsSearch search ) throws InvalidInputException,
        NoRecordsFetchedException
    {
        List<Company> companyList = new ArrayList<Company>();
        if ( search != null ) {

            LOG.info( "Report criteria: " + search.toString() );
            if ( search.getCompanyId() != 0 ) {
                Company company = companyDao.findById( Company.class, search.getCompanyId() );
                if ( company != null ) {
                    if ( search.getLowerEndTime() != null && search.getHigherEndTime() != null ) {
                        LOG.debug( "User has specified time filter as well " );
                        if ( search.getLowerEndTime().before( company.getCreatedOn() )
                            && search.getHigherEndTime().after( company.getCreatedOn() ) ) {
                            companyList.add( company );
                        }
                    } else {
                        companyList.add( company );
                    }
                    companyList.add( company );
                }
            } else if ( search.getCompanyIds() != null && !search.getCompanyIds().isEmpty() ) {
                for ( Long companyId : search.getCompanyIds() ) {
                    Company company = null;
                    if ( companyId != null ) {
                        company = companyDao.findById( Company.class, companyId );
                    }
                    if ( company != null ) {
                        if ( search.getLowerEndTime() != null && search.getHigherEndTime() != null ) {
                            LOG.debug( "User has specified time filter as well " );
                            if ( search.getLowerEndTime().before( company.getCreatedOn() )
                                && search.getHigherEndTime().after( company.getCreatedOn() ) ) {
                                companyList.add( company );
                            }
                        } else {
                            companyList.add( company );
                        }
                    }

                }
            } else if ( search.getLowerEndTime() != null ) {
                if ( search.getHigherEndTime() != null ) {
                    LOG.debug( "Search between time interval" );
                    companyList = companyDao.searchBetweenTimeIntervals( search.getLowerEndTime(), search.getHigherEndTime() );
                } else {
                    LOG.debug( "Search from start time to end" );
                    companyList = companyDao.searchBetweenTimeIntervals( search.getLowerEndTime(), null );
                }
            } else {
                LOG.debug( "No filter applied, hence searching all companies" );
                companyList = companyDao.findAll( Company.class );
            }
        } else {
            LOG.warn( "No criteria set. Will get all the records" );
            companyList = companyDao.findAll( Company.class );
        }
        return companyList;
    }
}
