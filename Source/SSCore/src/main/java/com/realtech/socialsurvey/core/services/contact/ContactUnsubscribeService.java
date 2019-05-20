/**
 * 
 */
package com.realtech.socialsurvey.core.services.contact;

/**
 * @author user345
 *
 */
public interface ContactUnsubscribeService
{
    /**
     * @param companyId
     * @param contactNumber
     * @param agentId
     * @param messageBody
     * @return
     */
    public String unsubscribeContact( Long companyId, String contactNumber, Long agentId, int modifiedBy, String messageBody );

    /**
     * @param companyId
     * @param contactNumber
     * @param messageBody
     * @return
     */
    public String resubscribeContact( Long companyId, String contactNumber, int modifiedBy, String messageBody );

    /**
     * @param companyId
     * @param contactNumber
     * @return
     */
    public boolean isUnsubscribed( long companyId, String contactNumber );
}
