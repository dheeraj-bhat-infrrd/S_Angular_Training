
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedinFeedCompanyStatusUpdate implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private LinkedinFeedShare share;

    public LinkedinFeedShare getShare() {
        return share;
    }

    public void setShare(LinkedinFeedShare share) {
        this.share = share;
    }

    @Override
    public String toString()
    {
        return "LinkedinFeedCompanyStatusUpdate [share=" + share + "]";
    }
}
