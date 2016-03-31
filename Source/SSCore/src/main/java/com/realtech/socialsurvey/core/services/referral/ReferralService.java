package com.realtech.socialsurvey.core.services.referral;

import com.realtech.socialsurvey.core.entities.ReferralInvitiation;
import com.realtech.socialsurvey.core.entities.User;
import com.realtech.socialsurvey.core.exception.InvalidInputException;
import com.realtech.socialsurvey.core.exception.NonFatalException;

/**
 * Manages referral sign up
 *
 */
public interface ReferralService
{

    /**
     * Validates the referral code passed during registration
     * @param referralCode
     * @return flag to check if referral code is valid
     * @throws InvalidInputException
     */
    public boolean validateReferralCode(String referralCode) throws InvalidInputException;
    
    /**
     * Fetch the referral object from code
     * @param referralCode
     * @return
     * @throws InvalidInputException
     */
    public ReferralInvitiation getReferralInvitation(String referralCode) throws InvalidInputException;
    
    /**
     * Add referral mapping
     * @param user
     * @param referralCode
     * @throws NonFatalException
     */
    public void addRefferalMapping( User user, String referralCode ) throws NonFatalException;
}
