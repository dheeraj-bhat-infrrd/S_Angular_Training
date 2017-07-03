package com.realtech.socialsurvey.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.dao.GenerateReportListDao;
import com.realtech.socialsurvey.core.dao.OverviewUserDao;
import com.realtech.socialsurvey.core.entities.GenerateReportList;
import com.realtech.socialsurvey.core.entities.OverviewUser;

@Component
public class GenerateReportListDaoImpl extends GenericReportingDaoImpl<GenerateReportList, Long>implements GenerateReportListDao
{

}
