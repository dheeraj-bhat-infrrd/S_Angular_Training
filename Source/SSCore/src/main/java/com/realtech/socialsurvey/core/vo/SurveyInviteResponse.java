package com.realtech.socialsurvey.core.vo;

/**
 * @author manish
 *
 */
public class SurveyInviteResponse
{
    private String status;
    private int surveySentCount;
    
    public SurveyInviteResponse() { }
    
    public SurveyInviteResponse(String status, int surveySentCount)
    {
        this.status = status;
        this.surveySentCount =surveySentCount;
    }


    public String getStatus()
    {
        return status;
    }


    public void setStatus( String status )
    {
        this.status = status;
    }


    public int getSurveySentCount()
    {
        return surveySentCount;
    }

    public void setSurveySentCount( int surveySentCount )
    {
        this.surveySentCount = surveySentCount;
    }

    @Override
    public String toString()
    {
        return "SurveyInviteResponse [status=" + status + ", surveySentCount=" + surveySentCount + "]";
    }

}
