package com.realtech.socialsurvey.core.entities.widget;

public class WidgetConfigurationRequest
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
    private String seoTitle;
    private String seoKeywords;
    private String seoDescription;
    private String requestMessage;


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


    public void setButtonOneName( String button1Text )
    {
        this.buttonOneName = button1Text;
    }


    public String getButtonOneLink()
    {
        return buttonOneLink;
    }


    public void setButtonOneLink( String button1Link )
    {
        this.buttonOneLink = button1Link;
    }


    public String getButtonOneOpacity()
    {
        return buttonOneOpacity;
    }


    public void setButtonOneOpacity( String button1Opacity )
    {
        this.buttonOneOpacity = button1Opacity;
    }


    public String getButtonTwoName()
    {
        return buttonTwoName;
    }


    public void setButtonTwoName( String button2Text )
    {
        this.buttonTwoName = button2Text;
    }


    public String getButtonTwoLink()
    {
        return buttonTwoLink;
    }


    public void setButtonTwoLink( String button2Link )
    {
        this.buttonTwoLink = button2Link;
    }


    public String getButtonTwoOpacity()
    {
        return buttonTwoOpacity;
    }


    public void setButtonTwoOpacity( String button2Opacity )
    {
        this.buttonTwoOpacity = button2Opacity;
    }


    public String getReviewLoaderName()
    {
        return reviewLoaderName;
    }


    public void setReviewLoaderName( String loadMoreButtonText )
    {
        this.reviewLoaderName = loadMoreButtonText;
    }


    public String getReviewLoaderOpacity()
    {
        return reviewLoaderOpacity;
    }


    public void setReviewLoaderOpacity( String loadMoreButtonOpacity )
    {
        this.reviewLoaderOpacity = loadMoreButtonOpacity;
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


    public String getRequestMessage()
    {
        return requestMessage;
    }


    public void setRequestMessage( String requestMessage )
    {
        this.requestMessage = requestMessage;
    }


    @Override
    public String toString()
    {
        return "WidgetConfigurationRequest [font=" + font + ", backgroundColor=" + backgroundColor + ", ratingAndStarColor="
            + ratingAndStarColor + ", barGraphColor=" + barGraphColor + ", foregroundColor=" + foregroundColor + ", fontTheme="
            + fontTheme + ", embeddedFontTheme=" + embeddedFontTheme + ", buttonOneName=" + buttonOneName + ", buttonOneLink="
            + buttonOneLink + ", buttonOneOpacity=" + buttonOneOpacity + ", buttonTwoName=" + buttonTwoName + ", buttonTwoLink="
            + buttonTwoLink + ", buttonTwoOpacity=" + buttonTwoOpacity + ", reviewLoaderName=" + reviewLoaderName
            + ", reviewLoaderOpacity=" + reviewLoaderOpacity + ", maxReviewsOnLoadMore=" + maxReviewsOnLoadMore
            + ", initialNumberOfReviews=" + initialNumberOfReviews + ", hideBarGraph=" + hideBarGraph + ", hideOptions="
            + hideOptions + ", reviewSortOrder=" + reviewSortOrder + ", reviewSources=" + reviewSources
            + ", allowModestBranding=" + allowModestBranding + ", seoTitle=" + seoTitle + ", seoKeywords=" + seoKeywords
            + ", seoDescription=" + seoDescription + ", requestMessage=" + requestMessage + "]";
    }


}
