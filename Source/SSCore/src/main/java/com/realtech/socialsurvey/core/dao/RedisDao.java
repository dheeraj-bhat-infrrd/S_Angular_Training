package com.realtech.socialsurvey.core.dao;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.Keyword;
import com.realtech.socialsurvey.core.entities.SocialMonitorTrustedSource;

public interface RedisDao {
    void addKeywords(long companyId, List<Keyword> keywords);

    public Map<String, Long> getFacebookLock();

    public Map<String, Long> getTwitterLock();
    
    void updateCompanyIdsForSM(long companyId, boolean isSocialMonitorEnabled);

	void addTruestedSources(long companyId, List<SocialMonitorTrustedSource> truestedSources);
}
