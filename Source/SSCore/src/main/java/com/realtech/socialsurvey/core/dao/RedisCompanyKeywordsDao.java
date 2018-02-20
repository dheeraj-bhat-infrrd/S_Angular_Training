package com.realtech.socialsurvey.core.dao;

import com.realtech.socialsurvey.core.entities.Keyword;

import java.util.List;

public interface RedisCompanyKeywordsDao {
    void addKeywords(long companyId, List<Keyword> keywords);
}
