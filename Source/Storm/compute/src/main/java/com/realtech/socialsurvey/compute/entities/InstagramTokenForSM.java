package com.realtech.socialsurvey.compute.entities;

/**
 * @author Lavanya
 */
public class InstagramTokenForSM {

    private static final long serialVersionUID = 1L;
    private String id;
    private String pageLink;
    private String accessToken;
    private String accessTokenToPost;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPageLink() {
            return pageLink;
        }

        public void setPageLink(String pageLink) {
            this.pageLink = pageLink;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getAccessTokenToPost() {
            return accessTokenToPost;
        }

        public void setAccessTokenToPost(String accessTokenToPost) {
            this.accessTokenToPost = accessTokenToPost;
        }


        @Override
        public String toString() {
            return "InstagramTokenForSM{" +
                    "id='" + id + '\'' +
                    ", pageLink='" + pageLink + '\'' +
                    ", accessToken='" + accessToken + '\'' +
                    ", accessTokenToPost='" + accessTokenToPost + '\'' +
                    '}';
        }
}
