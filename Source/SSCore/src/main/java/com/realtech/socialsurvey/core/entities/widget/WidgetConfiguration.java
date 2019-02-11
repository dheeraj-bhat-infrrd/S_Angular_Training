package com.realtech.socialsurvey.core.entities.widget;

import java.util.List;


public class WidgetConfiguration
{
    private String font;
    private String backgroundColor;
    private String ratingAndStarColor;
    private String barGraphColor;
    private String foregroundColor;
    private String fontTheme;
    private String embeddedFontTheme;
    private String buttonOneName;
    private String buttonOneLink;
    private String buttonOneOpacity;
    private String buttonTwoName;
    private String buttonTwoLink;
    private String buttonTwoOpacity;
    private String reviewLoaderName;
    private String reviewLoaderOpacity;
    private String maxReviewsOnLoadMore;
    private String initialNumberOfReviews;
    private String hideBarGraph;
    private String hideOptions;
    private String reviewSortOrder;
    private String reviewSources;
    private String allowModestBranding;
    private List<WidgetHistory> history;
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private List<WidgetLockHistory> lockHistory;
    private long actionByProfileId;
    private String actionByProfileLevel;
    private long actionOn;
    private int lockFlag = 0;
    private String hideReviewBtn;
    private String hideContactBtn;
    private String maxWidgetBtnSize;
    public String enableMobView;

    public String getEnableMobView() {
		return enableMobView;
	}


	public void setEnableMobView(String enableMobView) {
		this.enableMobView = enableMobView;
	}


	public String getMaxWidgetBtnSize() {
		return maxWidgetBtnSize;
	}


	public void setMaxWidgetBtnSize(String maxWidgetBtnSize) {
		this.maxWidgetBtnSize = maxWidgetBtnSize;
	}


	public String getHideReviewBtn() {
		return hideReviewBtn;
	}


	public void setHideReviewBtn(String hideReviewBtn) {
		this.hideReviewBtn = hideReviewBtn;
	}


	public String getHideContactBtn() {
		return hideContactBtn;
	}


	public void setHideContactBtn(String hideContactBtn) {
		this.hideContactBtn = hideContactBtn;
	}


	public String getFont()
    {
        return font;
    }


    public void setFont( String font )
    {
        this.font = font;
    }


    public String getBackgroundColor()
    {
        return backgroundColor;
    }


    public void setBackgroundColor( String backgroundColor )
    {
        this.backgroundColor = backgroundColor;
    }


    public String getRatingAndStarColor()
    {
        return ratingAndStarColor;
    }


    public void setRatingAndStarColor( String ratingAndStarColor )
    {
        this.ratingAndStarColor = ratingAndStarColor;
    }


    public String getBarGraphColor()
    {
        return barGraphColor;
    }


    public void setBarGraphColor( String barGraphColor )
    {
        this.barGraphColor = barGraphColor;
    }


    public String getForegroundColor()
    {
        return foregroundColor;
    }


    public void setForegroundColor( String foregroundColor )
    {
        this.foregroundColor = foregroundColor;
    }


    public String getFontTheme()
    {
        return fontTheme;
    }


    public void setFontTheme( String fontTheme )
    {
        this.fontTheme = fontTheme;
    }


    public String getEmbeddedFontTheme()
    {
        return embeddedFontTheme;
    }


    public void setEmbeddedFontTheme( String embeddedFontTheme )
    {
        this.embeddedFontTheme = embeddedFontTheme;
    }


    public String getButtonOneName()
    {
        return buttonOneName;
    }


    public void setButtonOneName( String buttonOneName )
    {
        this.buttonOneName = buttonOneName;
    }


    public String getButtonOneLink()
    {
        return buttonOneLink;
    }


    public void setButtonOneLink( String buttonOneLink )
    {
        this.buttonOneLink = buttonOneLink;
    }


    public String getButtonOneOpacity()
    {
        return buttonOneOpacity;
    }


    public void setButtonOneOpacity( String buttonOneOpacity )
    {
        this.buttonOneOpacity = buttonOneOpacity;
    }


    public String getButtonTwoName()
    {
        return buttonTwoName;
    }


    public void setButtonTwoName( String buttonTwoName )
    {
        this.buttonTwoName = buttonTwoName;
    }


    public String getButtonTwoLink()
    {
        return buttonTwoLink;
    }


    public void setButtonTwoLink( String buttonTwoLink )
    {
        this.buttonTwoLink = buttonTwoLink;
    }


    public String getButtonTwoOpacity()
    {
        return buttonTwoOpacity;
    }


    public void setButtonTwoOpacity( String buttonTwoOpacity )
    {
        this.buttonTwoOpacity = buttonTwoOpacity;
    }


    public String getReviewLoaderName()
    {
        return reviewLoaderName;
    }


    public void setReviewLoaderName( String reviewLoaderName )
    {
        this.reviewLoaderName = reviewLoaderName;
    }


    public String getReviewLoaderOpacity()
    {
        return reviewLoaderOpacity;
    }


    public void setReviewLoaderOpacity( String reviewLoaderOpacity )
    {
        this.reviewLoaderOpacity = reviewLoaderOpacity;
    }


    public String getMaxReviewsOnLoadMore()
    {
        return maxReviewsOnLoadMore;
    }


    public void setMaxReviewsOnLoadMore( String maxReviewsOnLoadMore )
    {
        this.maxReviewsOnLoadMore = maxReviewsOnLoadMore;
    }


    public String getInitialNumberOfReviews()
    {
        return initialNumberOfReviews;
    }


    public void setInitialNumberOfReviews( String initialNumberOfReviews )
    {
        this.initialNumberOfReviews = initialNumberOfReviews;
    }


    public String getHideBarGraph()
    {
        return hideBarGraph;
    }


    public void setHideBarGraph( String hideBarGraph )
    {
        this.hideBarGraph = hideBarGraph;
    }


    public String getHideOptions()
    {
        return hideOptions;
    }


    public void setHideOptions( String hideOptions )
    {
        this.hideOptions = hideOptions;
    }


    public String getReviewSortOrder()
    {
        return reviewSortOrder;
    }


    public void setReviewSortOrder( String reviewSortOrder )
    {
        this.reviewSortOrder = reviewSortOrder;
    }


    public String getReviewSources()
    {
        return reviewSources;
    }


    public void setReviewSources( String reviewSources )
    {
        this.reviewSources = reviewSources;
    }


    public String getAllowModestBranding()
    {
        return allowModestBranding;
    }


    public void setAllowModestBranding( String allowModestBranding )
    {
        this.allowModestBranding = allowModestBranding;
    }


    public List<WidgetHistory> getHistory()
    {
        return history;
    }


    public void setHistory( List<WidgetHistory> history )
    {
        this.history = history;
    }


    public String getSeoTitle()
    {
        return seoTitle;
    }


    public void setSeoTitle( String seoTitle )
    {
        this.seoTitle = seoTitle;
    }


    public String getSeoKeywords()
    {
        return seoKeywords;
    }


    public void setSeoKeywords( String seoKeywords )
    {
        this.seoKeywords = seoKeywords;
    }


    public String getSeoDescription()
    {
        return seoDescription;
    }


    public void setSeoDescription( String seoDescription )
    {
        this.seoDescription = seoDescription;
    }

    public List<WidgetLockHistory> getLockHistory()
    {
        return lockHistory;
    }


    public void setLockHistory( List<WidgetLockHistory> lockHistory )
    {
        this.lockHistory = lockHistory;
    }


    public long getActionByProfileId()
    {
        return actionByProfileId;
    }


    public void setActionByProfileId( long actionByProfileId )
    {
        this.actionByProfileId = actionByProfileId;
    }


    public String getActionByProfileLevel()
    {
        return actionByProfileLevel;
    }


    public void setActionByProfileLevel( String actionByProfileLevel )
    {
        this.actionByProfileLevel = actionByProfileLevel;
    }


    public long getActionOn()
    {
        return actionOn;
    }


    public void setActionOn( long actionOn )
    {
        this.actionOn = actionOn;
    }

    public int getLockFlag()
    {
        return lockFlag;
    }


    public void setLockFlag( int lockFlag )
    {
        this.lockFlag = lockFlag;
    }


	@Override
	public String toString() {
		return "WidgetConfiguration [font=" + font + ", backgroundColor=" + backgroundColor + ", ratingAndStarColor="
				+ ratingAndStarColor + ", barGraphColor=" + barGraphColor + ", foregroundColor=" + foregroundColor
				+ ", fontTheme=" + fontTheme + ", embeddedFontTheme=" + embeddedFontTheme + ", buttonOneName="
				+ buttonOneName + ", buttonOneLink=" + buttonOneLink + ", buttonOneOpacity=" + buttonOneOpacity
				+ ", buttonTwoName=" + buttonTwoName + ", buttonTwoLink=" + buttonTwoLink + ", buttonTwoOpacity="
				+ buttonTwoOpacity + ", reviewLoaderName=" + reviewLoaderName + ", reviewLoaderOpacity="
				+ reviewLoaderOpacity + ", maxReviewsOnLoadMore=" + maxReviewsOnLoadMore + ", initialNumberOfReviews="
				+ initialNumberOfReviews + ", hideBarGraph=" + hideBarGraph + ", hideOptions=" + hideOptions
				+ ", reviewSortOrder=" + reviewSortOrder + ", reviewSources=" + reviewSources + ", allowModestBranding="
				+ allowModestBranding + ", history=" + history + ", seoTitle=" + seoTitle + ", seoKeywords="
				+ seoKeywords + ", seoDescription=" + seoDescription + ", lockHistory=" + lockHistory
				+ ", actionByProfileId=" + actionByProfileId + ", actionByProfileLevel=" + actionByProfileLevel
				+ ", actionOn=" + actionOn + ", lockFlag=" + lockFlag + ", hideReviewBtn=" + hideReviewBtn
				+ ", hideContactBtn=" + hideContactBtn + ", maxWidgetBtnSize=" + maxWidgetBtnSize + ", enableMobView="
				+ enableMobView + "]";
	}
    
}
