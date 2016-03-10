package com.realtech.socialsurvey.core.dao.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.BranchDao;
import com.realtech.socialsurvey.core.dao.CompanyDao;
import com.realtech.socialsurvey.core.dao.OrganizationUnitSettingsDao;
import com.realtech.socialsurvey.core.entities.Branch;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.OrganizationUnitSettings;
import com.realtech.socialsurvey.core.exception.DatabaseException;
import com.realtech.socialsurvey.core.exception.InvalidInputException;

@Component("branch")
public class BranchDaoImpl extends GenericDaoImpl<Branch, Long> implements BranchDao {

	private static final Logger LOG = LoggerFactory.getLogger(BranchDaoImpl.class);

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private OrganizationUnitSettingsDao organizationUnitSettingsDao;


	/*
	 * Method to delete all the users of a company.
	 */
	@Override
	public void deleteBranchesByCompanyId(long companyId) {
		LOG.info("Method to delete all the branches by company id,deleteBranchesByCompanyId() started.");
		try {
			Query query = getSession().createQuery("delete from Branch where company.companyId=?");
			query.setParameter(0, companyId);
			query.executeUpdate();
		}
		catch (HibernateException hibernateException) {
			LOG.error("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
			throw new DatabaseException("Exception caught in deleteBranchesByCompanyId() ", hibernateException);
		}
		LOG.info("Method to delete all the branches by company id, deleteBranchesByCompanyId() finished.");
	}


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchForBranchIds( Set<Long> branchIds ) throws InvalidInputException
    {
        if ( branchIds == null || branchIds.isEmpty() )
            throw new InvalidInputException( "Branch ids passed cannot be null or empty" );
        LOG.info( "Method to get all the branches for branches ids,getBranchForBranchIds() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.in( CommonConstants.BRANCH_ID_COLUMN, branchIds ) );
        LOG.info( "Method to get all the branches for branches ids, getBranchForBranchIds() finished." );
        return criteria.list();
    }


    @SuppressWarnings ( "unchecked")
    @Override
    public List<Branch> getBranchesForCompany( long companyId, int isDefault,int start, int batch ) throws InvalidInputException
    {
        if ( companyId <= 0 )
            throw new InvalidInputException( "Invalid company id is passed in getBranchesForCompany()" );
        LOG.info( "Method to get all branches for company,getBranchesForCompany() started." );
        Criteria criteria = getSession().createCriteria( Branch.class );
        criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
        criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, isDefault ) );
        criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
        criteria.addOrder(Order.asc("branch"));
        if ( start > 0 )
            criteria.setFirstResult( start );
        if ( batch > 0 )
            criteria.setFetchSize( batch );

        List<Branch> branchList = criteria.list();
        if ( isDefault == CommonConstants.NO ) {
            Set<Long> branchIdList = new LinkedHashSet<Long>();
            Map<Long, Branch> branchIdObjMap = new TreeMap<>();
            if ( branchList != null && branchList.size() > 0 ) {
                for ( Branch branch : branchList ) {
                    branchIdList.add( branch.getBranchId() );
                    branchIdObjMap.put( branch.getBranchId(), branch );
                }
            }

            List<Branch> finalBranchList = new ArrayList<>();
            if ( branchIdList.size() > 0 ) {
                LOG.info( "Fetching branch settings for the fetched branches" );
                List<OrganizationUnitSettings> branchSettingList = organizationUnitSettingsDao
                    .fetchOrganizationUnitSettingsForMultipleIds( branchIdList,
                        MongoOrganizationUnitSettingDaoImpl.BRANCH_SETTINGS_COLLECTION );
                LOG.info( "Adding address, state city and zipcode info into branches" );
                if ( branchSettingList != null && branchSettingList.size() > 0 ) {
                    for ( OrganizationUnitSettings branchSettings : branchSettingList ) {
                        if ( branchIdObjMap.get( branchSettings.getIden() ) != null ) {
                            Branch branch = branchIdObjMap.get( branchSettings.getIden() );
                            branch.setAddress1( branchSettings.getContact_details().getAddress1() );
                            branch.setAddress2( branchSettings.getContact_details().getAddress2() );
                            branch.setCity( branchSettings.getContact_details().getCity() );
                            branch.setState( branchSettings.getContact_details().getState() );
                            branch.setZipcode( branchSettings.getContact_details().getZipcode() );

                            finalBranchList.add( branch );
                        }
                    }
                }
            }
            LOG.info( "Method to get all branches for company,getBranchesForCompany() ended." );
            if ( finalBranchList.size() > 0 )
                return finalBranchList;
            else
                return branchList;
        } else
            return branchList;
    }


    /**
     * Method to fetch all branch ids under company
     * @param companyId
     * @throws InvalidInputException
     * */
    @SuppressWarnings ( "unchecked")
    @Override
    @Transactional
    public List<Long> getBranchIdsUnderCompany( long companyId, int start, int batchSize ) throws InvalidInputException
    {
        if ( companyId <= 0 ) {
            throw new InvalidInputException( "Invalid company id passed in getBranchIdsUnderCompany method" );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() started." );
        Criteria criteria = null;
        try {
            criteria = getSession().createCriteria( Branch.class );
            criteria.setProjection( Projections.property( CommonConstants.BRANCH_ID_COLUMN ).as(
                CommonConstants.BRANCH_ID_COLUMN ) );
            criteria.add( Restrictions.eq( CommonConstants.COMPANY_COLUMN, companyDao.findById( Company.class, companyId ) ) );
            criteria.add( Restrictions.eq( CommonConstants.IS_DEFAULT_BY_SYSTEM, CommonConstants.NO ) );
            criteria.add( Restrictions.eq( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE ) );
            if ( start > 0 )
                criteria.setFirstResult( start );
            if ( batchSize > 0 )
                criteria.setMaxResults( batchSize );
        } catch ( HibernateException e ) {
            LOG.error( "HibernateException caught in getBranchIdsUnderCompany(). Reason: " + e.getMessage(), e );
            throw new DatabaseException( "HibernateException caught in getBranchIdsUnderCompany().", e );
        }
        LOG.info( "Method to get all branch ids under company id : " + companyId + ",getBranchIdsUnderCompany() ended." );
        return criteria.list();
    }
}
// JIRA SS-42 By RM-05 EOC
