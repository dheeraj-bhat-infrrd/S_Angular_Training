package com.realtech.socialsurvey.core.entities;

import java.util.Date;


public class ReviewReplyVO
{
    private String surveyId;
    private String replyId;
    private String replyText;
    private String replyByName;
    private String replyById;
    private Date createdOn;
    private Date modifiedOn;


    public String getSurveyId()
    {
        return surveyId;
    }


    public void setSurveyId( String surveyId )
    {
        this.surveyId = surveyId;
    }


    public String getReplyId()
    {
        return replyId;
    }


    public void setReplyId( String replyId )
    {
        this.replyId = replyId;
    }


    public String getReplyText()
    {
        return replyText;
    }


    public void setReplyText( String replyText )
    {
        this.replyText = replyText;
    }


    public String getReplyByName()
    {
        return replyByName;
    }


    public void setReplyByName( String replyByName )
    {
        this.replyByName = replyByName;
    }


    public String getReplyById()
    {
        return replyById;
    }


    public void setReplyById( String replyById )
    {
        this.replyById = replyById;
    }


    public Date getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn( Date createdOn )
    {
        this.createdOn = createdOn;
    }


    public Date getModifiedOn()
    {
        return modifiedOn;
    }


    public void setModifiedOn( Date modifiedOn )
    {
        this.modifiedOn = modifiedOn;
    }
    
    
    /*
     * Convenience method to get a new VO object back
     */
    public static ReviewReplyVO transformToVO(ReviewReply reply){
        ReviewReplyVO reviewReplyVO = new ReviewReplyVO();
        
        reviewReplyVO.setReplyId( reply.getReplyId() );
        reviewReplyVO.setReplyText( reply.getReplyText() );
        reviewReplyVO.setCreatedOn( reply.getCreatedOn() );
        reviewReplyVO.setModifiedOn( reply.getModifiedOn() );
        reviewReplyVO.setReplyByName( reply.getReplyByName() );
        reviewReplyVO.setReplyById( reply.getReplyById() );
        
        return reviewReplyVO;
    }
    
    @Override
    public String toString()
    {
        return "ReviewReplyVO [surveyId=" + surveyId + ", replyId=" + replyId + ", replyText=" + replyText + ", replyByName="
            + replyByName + ", replyById=" + replyById + ", createdOn=" + createdOn + ", modifiedOn=" + modifiedOn + "]";
    }
}
