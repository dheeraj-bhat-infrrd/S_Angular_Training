package com.realtech.socialsurvey.core.services.settingsmanagement.impl;

import java.math.BigInteger;
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
import com.realtech.socialsurvey.core.enums.OrganizationUnit;
import com.realtech.socialsurvey.core.enums.SettingsForApplication;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsLocker;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsManager;
import com.realtech.socialsurvey.core.services.settingsmanagement.SettingsSetter;


@Component
public class SettingsManagerImpl implements SettingsManager
{

    private static final Logger LOG = LoggerFactory.getLogger( SettingsManagerImpl.class );

    @Autowired
    private SettingsSetterDao settingsSetterDao;
    @Autowired
    private SettingsSetter settingsSetter;
    @Autowired
    private SettingsLocker settingsLocker;


    @Override
    public Map<String, BigInteger> calculateSettingsScore( List<SettingsDetails> settingsDetailsList )
    {
        LOG.debug( "Inside method calcualteSettingsScore " );
        BigInteger lockScore = BigInteger.valueOf(0);
        BigInteger setScore = BigInteger.valueOf(0);
        Map<String, BigInteger> map = new HashMap<>();
        for ( SettingsDetails settingsDetails : settingsDetailsList ) {
            lockScore = new BigInteger(lockScore.toByteArray());
            lockScore.add( BigInteger.valueOf(settingsDetails.getLockSettingsHolder()));
            
            setScore  = new BigInteger(setScore.toByteArray());
            setScore.add( settingsDetails.getSetSettingsHolder());
        }
        map.put( CommonConstants.SETTING_SCORE, setScore );
        map.put( CommonConstants.LOCK_SCORE, lockScore );

        return map;
    }


    @Override
    @Transactional
    public List<SettingsDetails> getScoreForCompleteHeirarchy( long companyId, long branchId, long regionId )
    {
        LOG.debug( "Inside method getScoreForCompleteHeirarchy " );
        return settingsSetterDao.getScoresById( companyId, regionId, branchId );
    }


    public Map<SettingsForApplication, OrganizationUnit> getClosestSettingLevel( String currentSetAggregateValue,
        String currentLockAggregateValue ) throws InvalidSettingsStateException
    {
        LOG.debug( "Getting a map of all the settings with the closest level of setter" );

        currentLockAggregateValue = currentLockAggregateValue.split( "\\." ).length > 1 ? currentLockAggregateValue.split( "\\." )[0]
            : currentLockAggregateValue ;
        currentSetAggregateValue = currentSetAggregateValue.split( "\\." ).length > 1 ? currentSetAggregateValue.split( "\\." )[0]
            : currentSetAggregateValue ;

        int lockValueLength = currentLockAggregateValue.length();
        int loopTillValue = ( currentSetAggregateValue.length() > lockValueLength ? currentSetAggregateValue.length()
            : lockValueLength );

        int counter = loopTillValue;
        Map<SettingsForApplication, OrganizationUnit> settingsMap = new HashMap<SettingsForApplication, OrganizationUnit>();
        double setIndex = -1;

        int lockIndex = -1;
        while ( counter > 0 ) {
            setIndex = -1;
            lockIndex = -1;
            // check the locker value. If is not locked then get the lowest setter level
            if ( counter > ( loopTillValue - lockValueLength ) ) {
                int lockCounter = ( -1 ) * ( ( loopTillValue - lockValueLength ) - counter );
                // get values for both setting and lock
                lockIndex = Integer.parseInt( currentLockAggregateValue.substring( lockCounter - 1, lockCounter ) );
                if ( lockIndex == CommonConstants.LOCKED_BY_NONE ) {
                    setIndex = Double.parseDouble( currentSetAggregateValue.substring( counter - 1, counter ) );
                }

            } else {
                setIndex = Integer.parseInt( currentSetAggregateValue.substring( counter - 1, counter ) );
            }
            if ( lockIndex != -1 ) {
                if ( lockIndex != CommonConstants.LOCKED_BY_NONE ) {
                    settingsMap.put( SettingsForApplication.getSettingForApplicationFromIndex( loopTillValue-counter+1 ),
                        settingsLocker.getHighestLockerLevel( lockIndex ) );
                } else {
                    // the setting is not locked
                    if ( setIndex != -1 && setIndex != CommonConstants.SET_BY_NONE ) {
                        settingsMap.put( SettingsForApplication.getSettingForApplicationFromIndex( loopTillValue-counter+1 ),
                            settingsSetter.getLowestSetterLevel( setIndex ) );
                    } else {
                        settingsMap.put( SettingsForApplication.getSettingForApplicationFromIndex(loopTillValue-counter+1 ), null );
                    }
                }
            } else {
                // the setting is not locked
                if ( setIndex != -1 && setIndex != CommonConstants.SET_BY_NONE ) {
                    settingsMap.put( SettingsForApplication.getSettingForApplicationFromIndex( loopTillValue-counter+1 ),
                        settingsSetter.getLowestSetterLevel( setIndex ) );
                } else {
                    settingsMap.put( SettingsForApplication.getSettingForApplicationFromIndex( loopTillValue-counter+1 ), null );
                }
            }
            counter--;
        }
        return settingsMap;
    }
}
