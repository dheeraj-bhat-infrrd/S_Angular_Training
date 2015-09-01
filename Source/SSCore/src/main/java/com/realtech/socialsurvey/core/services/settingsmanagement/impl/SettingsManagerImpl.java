package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.SettingsSetterDao;
import com.realtech.socialsurvey.core.entities.SettingsDetails;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;


@Component
public class SettingsManagerImpl implements SettingsManager
{

    private static final Logger LOG = LoggerFactory.getLogger( SettingsManagerImpl.class );

    @Autowired
    private SettingsSetterDao settingsSetterDao;


    @Override
    public Map<String, Long> calculateSettingsScore( List<SettingsDetails> settingsDetailsList )
    {
        LOG.debug( "Inside method calcualteSettingsScore " );
        long lockScore = 0;
        long setScore = 0;
        Map<String, Long> map = new HashMap<String, Long>();
        for ( SettingsDetails settingsDetails : settingsDetailsList ) {
            lockScore = lockScore + settingsDetails.getLockSettingsHolder();
            setScore = setScore + settingsDetails.getSetSettingsHolder();
        }
        map.put( CommonConstants.SETTING_SCORE, setScore );
        map.put( CommonConstants.LOCK_SCORE, lockScore );

        return map;
    }


    @Override
    @Transactional
    public List<SettingsDetails> getScoreForCompleteHeirarchy( long companyId, long branchId, long regionId )
    {
        LOG.info( "Inside method getScoreForCompleteHeirarchy " );
        return settingsSetterDao.getScoresById( companyId, regionId, branchId );
    }
}
