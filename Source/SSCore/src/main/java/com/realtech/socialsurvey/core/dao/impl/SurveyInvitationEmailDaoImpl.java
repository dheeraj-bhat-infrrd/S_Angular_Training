/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.SurveyInvitationEmailDao;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;

/**
 * @author Subhrajit
 *
 */
@Component
public class SurveyInvitationEmailDaoImpl extends GenericReportingDaoImpl<SurveyInvitationEmailCountMonth, Long> implements SurveyInvitationEmailDao {
	
	private static final Logger LOG = LoggerFactory.getLogger(SurveyInvitationEmailDaoImpl.class);

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.core.dao.SurveyInvitationEmailDao#getSurveyInvitationEmailReportForMonth(long, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSurveyInvitationEmailReportForMonth(long companyId, int month,
			int year) {
		String queryString = "select imc.agent_name,imc.agent_email,b.BRANCH,r.REGION, "
				+ "imc.received,imc.attempted_count,imc.delivered,imc.bounced,imc.deffered, "
				+ "imc.opened,imc.link_clicked,imc.dropped,imc.month,imc.year "
				+ "from invitation_mail_count_month imc "
				+ "inner join company c on imc.company_id=c.company_id "
				+ "inner join user_profile up on imc.agent_id=up.user_id and up.status = 1 and up.profiles_master_id=4 "
				+ "left join branch b on up.branch_id = b.branch_id and b.IS_DEFAULT_BY_SYSTEM=0 "
				+ "left join region r on up.region_id = r.region_id and r.IS_DEFAULT_BY_SYSTEM=0 "
				+ "where imc.month=:month and imc.year=:year and imc.company_id=:companyId "
				+ "order by imc.agent_name";
		
		SQLQuery query = getSession().createSQLQuery(queryString);
		query.setInteger("month", month)
		.setInteger("year", year)
		.setLong("companyId", companyId);
		
		return query.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSurveyInvitationEmailReportForAllTime(long companyId, int month, int year) {
		String queryString = "select u.agent_name,u.agent_email,b.BRANCH,r.REGION,u.received,"
				+ "u.attempted_count,u.delivered,u.bounced,u.deffered,u.opened,u.link_clicked,"
				+ "u.dropped,u.month,u.year from  user_profile up "
				+ "inner join (select imc.agent_id,imc.agent_name,imc.agent_email,"
				+ "sum(imc.received) as received,sum(imc.attempted_count) as attempted_count,"
				+ "sum(imc.delivered) as delivered,sum(imc.bounced) as bounced,sum(imc.deffered) as deffered,"
				+ "sum(imc.opened) as opened,sum(imc.link_clicked) as link_clicked,sum(imc.dropped) as dropped,"
				+ "imc.month,imc.year from invitation_mail_count_month imc "
				+ "inner join company c on imc.company_id=c.company_id where imc.month in (0, :month ) "
				+ "and imc.year in (0, :year ) and imc.company_id=:companyId group by imc.agent_id "
				+ "order by imc.agent_name) as u on up.user_id=u.agent_id and up.STATUS=1 and "
				+ "up.PROFILES_MASTER_ID=4 left join branch b on up.branch_id = b.branch_id "
				+ "and b.IS_DEFAULT_BY_SYSTEM=0 left join region r on up.region_id = r.region_id "
				+ "and r.IS_DEFAULT_BY_SYSTEM=0 order by u.agent_name";
		
		SQLQuery query = getSession().createSQLQuery(queryString);
		query.setInteger("month", month)
		.setInteger("year", year)
		.setLong("companyId", companyId);
		
		return query.list();
	}

	@Override
	public void deleteOldDataForMonth(int month, int year) {
		String queryString = "delete from invitation_mail_count_month where month= :month and year= :year ";
		SQLQuery query = getSession().createSQLQuery(queryString);
		query.setInteger("month", month)
		.setInteger("year", year);
		query.executeUpdate();
		LOG.info("Deleted records for {}-{}",month,year);
	}

	@Override
	public long getSurveyInvitationEmailReportCountForMonth(int month, int year) {
		long count = 0;
		Criteria criteria = getSession().createCriteria(SurveyInvitationEmailCountMonth.class);
		criteria.add(Restrictions.eq("month", month))
		.add(Restrictions.eq("year", year));
		criteria.setProjection(Projections.rowCount());
		count = (Long)criteria.uniqueResult();
		return count;
	}
	
}
