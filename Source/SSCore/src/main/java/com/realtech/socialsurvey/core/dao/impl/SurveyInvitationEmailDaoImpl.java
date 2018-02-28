/**
 * 
 */
package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.SurveyInvitationEmailDao;
import com.realtech.socialsurvey.core.entities.SurveyInvitationEmailCountMonth;

/**
 * @author Subhrajit
 *
 */
@Component
public class SurveyInvitationEmailDaoImpl extends GenericReportingDaoImpl<SurveyInvitationEmailCountMonth, Long> implements SurveyInvitationEmailDao {

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.core.dao.SurveyInvitationEmailDao#getSurveyInvitationEmailReportForMonth(long, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getSurveyInvitationEmailReportForMonth(long companyId, int month,
			int year) {
		String queryString = "select imc.agent_name,imc.agent_email,b.BRANCH,r.REGION,"
				+ "imc.received,imc.attempted_count,imc.delivered,imc.bounced,imc.deffered,"
				+ "imc.opened,imc.link_clicked,imc.dropped "
				+ "from invitation_mail_count_month imc "
				+ "inner join company c on imc.company_id=c.company_id "
				+ "inner join user_profile up on imc.agent_id=up.user_id and up.status = 1 "
				+ "left join branch b on up.branch_id = b.branch_id and b.IS_DEFAULT_BY_SYSTEM=0 "
				+ "left join region r on up.region_id = r.region_id and r.IS_DEFAULT_BY_SYSTEM=0 "
				+ "where imc.month=:month and imc.year=:year and imc.company_id=:companyId";
		
		SQLQuery query = getSession().createSQLQuery(queryString);
		query.setInteger("month", month)
		.setInteger("year", year)
		.setLong("companyId", companyId);
		
		return query.list();
	}

}
