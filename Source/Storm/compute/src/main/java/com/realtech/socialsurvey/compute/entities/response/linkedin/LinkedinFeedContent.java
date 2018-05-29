
package com.realtech.socialsurvey.compute.entities.response.linkedin;

import java.io.Serializable;


/**
 * @author manish
 *
 */
public class LinkedinFeedContent implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String description;

    private String eyebrowUrl;

    private String shortenedUrl;

    private String submittedImageUrl;

    private String submittedUrl;

    private String thumbnailUrl;

    private String title;


    public String getDescription()
    {
        return description;
    }


    public void setDescription( String description )
    {
        this.description = description;
    }


    public String getEyebrowUrl()
    {
        return eyebrowUrl;
    }


    public void setEyebrowUrl( String eyebrowUrl )
    {
        this.eyebrowUrl = eyebrowUrl;
    }


    public String getShortenedUrl()
    {
        return shortenedUrl;
    }


    public void setShortenedUrl( String shortenedUrl )
    {
        this.shortenedUrl = shortenedUrl;
    }


    public String getSubmittedImageUrl()
    {
        return submittedImageUrl;
    }


    public void setSubmittedImageUrl( String submittedImageUrl )
    {
        this.submittedImageUrl = submittedImageUrl;
    }


    public String getSubmittedUrl()
    {
        return submittedUrl;
    }


    public void setSubmittedUrl( String submittedUrl )
    {
        this.submittedUrl = submittedUrl;
    }


    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }


    public void setThumbnailUrl( String thumbnailUrl )
    {
        this.thumbnailUrl = thumbnailUrl;
    }


    public String getTitle()
    {
        return title;
    }


    public void setTitle( String title )
    {
        this.title = title;
    }


    @Override
    public String toString()
    {
        return "LinkedinFeedContent [description=" + description + ", eyebrowUrl=" + eyebrowUrl + ", shortenedUrl="
            + shortenedUrl + ", submittedImageUrl=" + submittedImageUrl + ", submittedUrl=" + submittedUrl + ", thumbnailUrl="
            + thumbnailUrl + ", title=" + title + "]";
    }
}
