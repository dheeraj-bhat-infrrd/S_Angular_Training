package com.realtech.socialsurvey.core.dao;

import java.util.List;

import com.realtech.socialsurvey.core.entities.ZillowTempPost;

public interface ZillowTempPostDao extends GenericDao<ZillowTempPost, Integer>
{

   public ZillowTempPost saveOrUpdateZillowTempPost( ZillowTempPost zillowTempPost );

   public void removeProcessedZillowTempPosts( List<Long> processedZillowTempPostIds );

}
