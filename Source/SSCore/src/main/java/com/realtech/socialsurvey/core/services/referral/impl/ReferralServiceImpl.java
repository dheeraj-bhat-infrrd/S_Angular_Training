package com.realtech.socialsurvey.core.services.referral.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.realtech.socialsurvey.core.commons.CommonConstants;
import com.realtech.socialsurvey.core.dao.GenericDao;
import com.realtech.socialsurvey.core.entities.ReferralInvitiation;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.services.referral.ReferralService;


@Component
public class ReferralServiceImpl implements ReferralService
{

    private static final Logger LOG = LoggerFactory.getLogger( ReferralServiceImpl.class );

    @Autowired
    private GenericDao<ReferralInvitiation, Long> referralDao;


    @Override
    public boolean validateReferralCode( String referralCode ) throws InvalidInputException
    {
        LOG.info( "Validating referral code: " + referralCode );
        // checking for active profile
        if ( referralCode == null || referralCode.isEmpty() ) {
            LOG.warn( "Referral code is not present." );
            throw new InvalidInputException( "Referral code is not present." );
        }
        
        Map<String, Object> queries = new HashMap<>();
        queries.put( ReferralInvitiation.REFERRAL_ID_COLUMN, referralCode );
        queries.put( CommonConstants.STATUS_COLUMN, CommonConstants.STATUS_ACTIVE );
        List<ReferralInvitiation> referralCodes = referralDao.findByKeyValue( ReferralInvitiation.class, queries );
        if(referralCodes == null || referralCodes.isEmpty()){
            LOG.info( "Could not find referral code "+referralCode );
            return false;
        }else{
            LOG.info( "Fond referral code "+referralCode );
            return true;
        }
    }

}
