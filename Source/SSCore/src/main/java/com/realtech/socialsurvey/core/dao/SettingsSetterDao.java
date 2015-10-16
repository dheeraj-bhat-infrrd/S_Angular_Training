package com.realtech.socialsurvey.core.dao;

import java.util.List;
import com.realtech.socialsurvey.core.entities.SettingsDetails;


/*
 * This interface contains methods which are required for queries and criteria on Branch table.
 */
public interface SettingsSetterDao extends GenericDao<SettingsDetails, Long>
{

    public List<SettingsDetails> getScoresById( long companyId, long regionId, long branchId );

}
