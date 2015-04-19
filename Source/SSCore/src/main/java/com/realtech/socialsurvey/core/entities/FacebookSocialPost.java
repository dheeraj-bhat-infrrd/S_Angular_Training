package com.realtech.socialsurvey.core.entities;

import facebook4j.Post;

public class FacebookSocialPost extends SocialPost {

	private Post post;

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}