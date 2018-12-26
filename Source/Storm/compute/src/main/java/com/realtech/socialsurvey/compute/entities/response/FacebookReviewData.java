package com.realtech.socialsurvey.compute.entities.response;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


/**
 * @author Lavanya
 */

public class FacebookReviewData implements Serializable
{
    private static final long serialVersionUID = 1L;

    @SerializedName( "open_graph_story" )
    private OpenGraphStory openGraphStory;

    public OpenGraphStory getOpenGraphStory() { return this.openGraphStory; }

    public void setOpenGraphStory(OpenGraphStory openGraphStory) { this.openGraphStory = openGraphStory; }

    @SerializedName( "created_time" )
    private long createdTime;

    public long getCreatedTime() { return this.createdTime; }

    public void setCreatedTime(long created_time) { this.createdTime = created_time; }

    @SerializedName( "has_rating" )
    private boolean hasRating;

    public boolean getHasRating() { return this.hasRating; }

    public void setHasRating(boolean has_rating) { this.hasRating = has_rating; }

    @SerializedName( "has_review" )
    private boolean hasReview;

    public boolean getHasReview() { return this.hasReview; }

    public void setHasReview(boolean has_review) { this.hasReview = has_review; }

    private int rating;

    public int getRating() { return this.rating; }

    public void setRating(int rating) { this.rating = rating; }

    @SerializedName( "recommendation_type" )
    private String recommendationType;

    public String getRecommendationType() { return this.recommendationType; }

    public void setRecommendationType(String recommendation_type) { this.recommendationType = recommendation_type; }

    @SerializedName( "review_text" )
    private String reviewText;

    public String getReviewText() { return this.reviewText; }

    public void setReviewText(String review_text) { this.reviewText = review_text; }

    private Reviewer reviewer;

    public Reviewer getReviewer() { return this.reviewer; }

    public void setReviewer(Reviewer reviewer) { this.reviewer = reviewer; }
}
