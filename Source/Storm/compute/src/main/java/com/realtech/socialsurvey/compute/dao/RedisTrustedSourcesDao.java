package com.realtech.socialsurvey.compute.dao;

import java.io.Serializable;
import java.util.List;

import com.realtech.socialsurvey.compute.entities.SocialMonitorTrustedSource;

public interface RedisTrustedSourcesDao extends Serializable {

	public List<SocialMonitorTrustedSource> getCompanyTrustedSourcesForCompanyId(long companyIden);

}
