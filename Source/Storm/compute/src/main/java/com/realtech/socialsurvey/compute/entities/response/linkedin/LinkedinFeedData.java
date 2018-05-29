
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;

/**
 * @author manish
 *
 */
public class LinkedinFeedData implements Serializable
{
    private static final long serialVersionUID = 1L;

    private boolean isCommentable;

    private boolean isLikable;

    private boolean isLiked;
    
    private int numLikes;

    private long timestamp;

    private LinkedinFeedUpdateContent updateContent;

    private String updateKey;
    
    private String updateType;

   
    public boolean isCommentable()
    {
        return isCommentable;
    }


    public boolean isLikable()
    {
        return isLikable;
    }


    public boolean isLiked()
    {
        return isLiked;
    }


    public int getNumLikes()
    {
        return numLikes;
    }


   


    public LinkedinFeedUpdateContent getUpdateContent()
    {
        return updateContent;
    }


    public String getUpdateKey()
    {
        return updateKey;
    }


    public String getUpdateType()
    {
        return updateType;
    }


    public void setCommentable( boolean isCommentable )
    {
        this.isCommentable = isCommentable;
    }


    public void setLikable( boolean isLikable )
    {
        this.isLikable = isLikable;
    }


    public void setLiked( boolean isLiked )
    {
        this.isLiked = isLiked;
    }


    public void setNumLikes( int numLikes )
    {
        this.numLikes = numLikes;
    }


    public void setUpdateContent( LinkedinFeedUpdateContent updateContent )
    {
        this.updateContent = updateContent;
    }


    public void setUpdateKey( String updateKey )
    {
        this.updateKey = updateKey;
    }


    public void setUpdateType( String updateType )
    {
        this.updateType = updateType;
    }


    public long getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp( long timestamp )
    {
        this.timestamp = timestamp;
    }


    @Override
    public String toString()
    {
        return "LinkedinFeedData [isCommentable=" + isCommentable + ", isLikable=" + isLikable + ", isLiked=" + isLiked
            + ", numLikes=" + numLikes + ", timestamp=" + timestamp + ", updateContent=" + updateContent + ", updateKey="
            + updateKey + ", updateType=" + updateType + "]";
    }
}
