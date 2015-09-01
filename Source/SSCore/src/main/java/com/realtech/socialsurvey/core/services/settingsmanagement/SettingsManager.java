package com.realtech.socialsurvey.core.services.settingsmanagement;

import java.util.List;
import java.util.Map;

import com.realtech.socialsurvey.core.entities.SettingsDetails;


/**
 * Operates on setting of the settings
 *
 */
public interface SettingsManager
{

    public Map<String, Long> calculateSettingsScore( List<SettingsDetails> settingsDetails );


    public List<SettingsDetails> getScoreForCompleteHeirarchy( long companyId, long branchId, long regionId );
}
