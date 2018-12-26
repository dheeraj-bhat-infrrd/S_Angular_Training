package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class OpenGraphData implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String recommendation_type;

    public String getRecommendationType() { return this.recommendation_type; }

    public void setRecommendationType(String recommendation_type) { this.recommendation_type = recommendation_type; }

    @SerializedName( "review_text" )
    private String reviewText;

    public String getReviewText() { return this.reviewText; }

    public void setReviewText(String review_text) { this.reviewText = review_text; }

    @SerializedName( "is_hidden" )
    private boolean isHidden;

    public boolean getIsHidden() { return this.isHidden; }

    public void setIsHidden(boolean is_hidden) { this.isHidden = is_hidden; }

    private String language;

    public String getLanguage() { return this.language; }

    public void setLanguage(String language) { this.language = language; }

    private Seller seller;

    public Seller getSeller() { return this.seller; }

    public void setSeller(Seller seller) { this.seller = seller; }

    private Rating rating;

    public Rating getRating() { return this.rating; }

    public void setRating(Rating rating) { this.rating = rating; }
}
