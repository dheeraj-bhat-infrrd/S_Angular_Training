/**
 * 
 */
package com.realtech.socialsurvey.compute.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.realtech.socialsurvey.compute.dao.SQLEmailCountDao;
import com.realtech.socialsurvey.compute.entity.SurveyInvitationEmailCountMonth;

/**
 * @author Subhrajit
 *
 */
public class SQLEmailCountDaoImpl implements SQLEmailCountDao {
	
	private Connection getConnection() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306","root","root");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return con;
	}

	/* (non-Javadoc)
	 * @see com.realtech.socialsurvey.compute.dao.SQLEmailCountDao#getReceivedCount(java.lang.String, java.lang.String)
	 */
	@Override
	public int getReceivedCount(String startDate, String endDate) throws SQLException {
		int count = 0;
		String query = "select count(SURVEY_PRE_INITIATION_ID) from survey_pre_initiation where CREATED_ON_EST "
				+ "between ? and ? group by AGENT_ID;";
		
		Connection con = getConnection();
		PreparedStatement ps = con.prepareStatement(query);
		ps.setString(1, startDate);
		ps.setString(2, endDate);
		
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			count = rs.getInt(1);
		}
		
		return count;
	}

	@Override
	public void save(SurveyInvitationEmailCountMonth emailCountMonth) throws SQLException {
		String query = "insert into invitation_mail_count_month(agent_id,company_id,month,year,attempted_count,delivered,"
				+ "deffered,blocked,opened,spam,unsubscribed,bounced,link_clicked) vaues(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement ps = getConnection().prepareStatement(query);
		ps.setInt(1, emailCountMonth.getAgentId());
		ps.setInt(2, emailCountMonth.getCompanyId());
		ps.setInt(3, emailCountMonth.getMonth());
		ps.setInt(4, emailCountMonth.getYear());
		ps.setInt(5, emailCountMonth.getAttempted());
		ps.setInt(6, emailCountMonth.getDelivered());
		ps.setInt(7, emailCountMonth.getDiffered());
		ps.setInt(8, emailCountMonth.getBlocked());
		ps.setInt(9, emailCountMonth.getOpened());
		ps.setInt(10, emailCountMonth.getSpamed());
		ps.setInt(11, emailCountMonth.getUnsubscribed());
		ps.setInt(12, emailCountMonth.getBounced());
		ps.setInt(13, emailCountMonth.getLinkClicked());

		ps.execute();
	}

}
