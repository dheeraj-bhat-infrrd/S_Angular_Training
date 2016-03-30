package com.realtech.socialsurvey.core.services.referral;

import com.realtech.socialsurvey.core.exception.InvalidInputException;

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
}
