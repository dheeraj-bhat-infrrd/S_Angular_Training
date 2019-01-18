package com.realtech.socialsurvey.compute.entities.response;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class TwitterFeedData implements Serializable
{
    private static final long serialVersionUID = 1L;
    private long createdAt;
    private long id;
    private String text;
    private int displayTextRangeStart = -1;
    private int displayTextRangeEnd = -1;
    private String source;
    private boolean isTruncated;
    private long inReplyToStatusId;
    private long inReplyToUserId;
    private boolean isFavorited;
    private boolean isRetweeted;
    private int favoriteCount;
    private String inReplyToScreenName;
    private long retweetCount;
    private boolean isPossiblySensitive;
    private String lang;
    private List<SocialFeedMediaEntity> mediaEntities;
    
    private int remaining;
    private int limit;
    private int resetTimeInSeconds;

    private long[] contributorsIDs;

    private String userName = null;
    private String[] withheldInCountries = null;


    public long getCreatedAt()
    {
        return createdAt;
    }


    public void setCreatedAt( long createdAt )
    {
        this.createdAt = createdAt;
    }


    public long getId()
    {
        return id;
    }


    public void setId( long id )
    {
        this.id = id;
    }


    public String getText()
    {
        return text;
    }


    public void setText( String text )
    {
        this.text = text;
    }


    public int getDisplayTextRangeStart()
    {
        return displayTextRangeStart;
    }


    public void setDisplayTextRangeStart( int displayTextRangeStart )
    {
        this.displayTextRangeStart = displayTextRangeStart;
    }


    public int getDisplayTextRangeEnd()
    {
        return displayTextRangeEnd;
    }


    public void setDisplayTextRangeEnd( int displayTextRangeEnd )
    {
        this.displayTextRangeEnd = displayTextRangeEnd;
    }


    public String getSource()
    {
        return source;
    }


    public void setSource( String source )
    {
        this.source = source;
    }


    public boolean isTruncated()
    {
        return isTruncated;
    }


    public void setTruncated( boolean isTruncated )
    {
        this.isTruncated = isTruncated;
    }


    public long getInReplyToStatusId()
    {
        return inReplyToStatusId;
    }


    public void setInReplyToStatusId( long inReplyToStatusId )
    {
        this.inReplyToStatusId = inReplyToStatusId;
    }


    public long getInReplyToUserId()
    {
        return inReplyToUserId;
    }


    public void setInReplyToUserId( long inReplyToUserId )
    {
        this.inReplyToUserId = inReplyToUserId;
    }


    public boolean isFavorited()
    {
        return isFavorited;
    }


    public void setFavorited( boolean isFavorited )
    {
        this.isFavorited = isFavorited;
    }


    public boolean isRetweeted()
    {
        return isRetweeted;
    }


    public void setRetweeted( boolean isRetweeted )
    {
        this.isRetweeted = isRetweeted;
    }


    public int getFavoriteCount()
    {
        return favoriteCount;
    }


    public void setFavoriteCount( int favoriteCount )
    {
        this.favoriteCount = favoriteCount;
    }


    public String getInReplyToScreenName()
    {
        return inReplyToScreenName;
    }


    public void setInReplyToScreenName( String inReplyToScreenName )
    {
        this.inReplyToScreenName = inReplyToScreenName;
    }


    public long getRetweetCount()
    {
        return retweetCount;
    }


    public void setRetweetCount( long retweetCount )
    {
        this.retweetCount = retweetCount;
    }


    public boolean isPossiblySensitive()
    {
        return isPossiblySensitive;
    }


    public void setPossiblySensitive( boolean isPossiblySensitive )
    {
        this.isPossiblySensitive = isPossiblySensitive;
    }


    public String getLang()
    {
        return lang;
    }


    public void setLang( String lang )
    {
        this.lang = lang;
    }


    public long[] getContributorsIDs()
    {
        return contributorsIDs;
    }


    public void setContributorsIDs( long[] contributorsIDs )
    {
        this.contributorsIDs = contributorsIDs;
    }


    public String getUserName()
    {
        return userName;
    }


    public void setUserName( String userName )
    {
        this.userName = userName;
    }


    public String[] getWithheldInCountries()
    {
        return withheldInCountries;
    }


    public void setWithheldInCountries( String[] withheldInCountries )
    {
        this.withheldInCountries = withheldInCountries;
    }


    public int getRemaining()
    {
        return remaining;
    }


    public void setRemaining( int remaining )
    {
        this.remaining = remaining;
    }


    public int getLimit()
    {
        return limit;
    }


    public void setLimit( int limit )
    {
        this.limit = limit;
    }


    public int getResetTimeInSeconds()
    {
        return resetTimeInSeconds;
    }


    public void setResetTimeInSeconds( int resetTimeInSeconds )
    {
        this.resetTimeInSeconds = resetTimeInSeconds;
    }


    public List<SocialFeedMediaEntity> getMediaEntities()
    {
        return mediaEntities;
    }


    public void setMediaEntities( List<SocialFeedMediaEntity> mediaEntities )
    {
        this.mediaEntities = mediaEntities;
    }

    @Override
    public String toString()
    {
        return "TwitterFeedData [createdAt=" + createdAt + ", id=" + id + ", text=" + text + ", displayTextRangeStart="
            + displayTextRangeStart + ", displayTextRangeEnd=" + displayTextRangeEnd + ", source=" + source + ", isTruncated="
            + isTruncated + ", inReplyToStatusId=" + inReplyToStatusId + ", inReplyToUserId=" + inReplyToUserId
            + ", isFavorited=" + isFavorited + ", isRetweeted=" + isRetweeted + ", favoriteCount=" + favoriteCount
            + ", inReplyToScreenName=" + inReplyToScreenName + ", retweetCount=" + retweetCount + ", isPossiblySensitive="
            + isPossiblySensitive + ", lang=" + lang + ", mediaEntities=" + mediaEntities + ", remaining=" + remaining
            + ", limit=" + limit + ", resetTimeInSeconds=" + resetTimeInSeconds + ", contributorsIDs="
            + Arrays.toString( contributorsIDs ) + ", userName=" + userName + ", withheldInCountries="
            + Arrays.toString( withheldInCountries ) + "]";
    }
}
