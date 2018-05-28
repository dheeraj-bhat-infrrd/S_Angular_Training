
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedinFeedUpdateContent implements Serializable
{
    private static final long serialVersionUID = 1L;
    private LinkedinFeedCompany company;
    private LinkedinFeedCompanyStatusUpdate companyStatusUpdate;

    public LinkedinFeedCompany getCompany() {
        return company;
    }

    public void setCompany(LinkedinFeedCompany company) {
        this.company = company;
    }

    public LinkedinFeedCompanyStatusUpdate getCompanyStatusUpdate() {
        return companyStatusUpdate;
    }

    public void setCompanyStatusUpdate(LinkedinFeedCompanyStatusUpdate companyStatusUpdate) {
        this.companyStatusUpdate = companyStatusUpdate;
    }

    @Override
    public String toString()
    {
        return "LinkedinFeedUpdateContent [company=" + company + ", companyStatusUpdate=" + companyStatusUpdate + "]";
    }
}
