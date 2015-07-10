package com.realtech.socialsurvey.core.services.reports.impl;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.realtech.socialsurvey.core.entities.Company;
import com.realtech.socialsurvey.core.entities.CompanyReportsSearch;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NoRecordsFetchedException;
import com.realtech.socialsurvey.core.services.reports.AdminReports;

public class AdminReportsImpl implements AdminReports {

	private static final Logger LOG = LoggerFactory.getLogger(AdminReportsImpl.class);

	@Override
	public List<Company> companyCreationReports(CompanyReportsSearch search) throws InvalidInputException, NoRecordsFetchedException {
		if(search != null){
			LOG.info("Report criteria: "+search.toString());
			
		}else{
			LOG.warn("No criteria set. Will get all the records");
		}
		return null;
	}
}
