package com.realtech.socialsurvey.core.starter;

import java.net.MalformedURLException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.PostUpdate;
import facebook4j.auth.AccessToken;

/**
 * Class to post manually to social media
 */
public class PostManuallyToSocialMedia {

	private static final Logger LOG = LoggerFactory.getLogger(PostManuallyToSocialMedia.class);

	private String facebookClientId = "866992583360371";
	private String facebookAppSecret = "fcc9f6f47e762e078a5c75880d3fa1c6";
	private String facebookRedirectUri = "/facebookauth.do";
	private String facebookScope = "publish_actions,user_posts,manage_pages";
	private String serverBaseUrl = "https://socialsurvey.me";

	public void postToFacebook(String rating, String customerDisplayName, String agentName, String completeProfileUrl, String feedback,
			String facebookToken) {
		String message = rating + "-Star Survey Response from " + customerDisplayName + " for " + agentName + " on Social Survey - view at "
				+ completeProfileUrl + "\n Feedback : " + feedback;
		LOG.debug("Message to post: " + message);
		Facebook facebook = getFacebookInstance();
		facebook.setOAuthAccessToken(new AccessToken(facebookToken, null));
		try{
			PostUpdate postUpdate = new PostUpdate( message );
            postUpdate.setCaption( completeProfileUrl );
            try {
                postUpdate.setLink( new URL( completeProfileUrl ) );
            } catch ( MalformedURLException e1 ) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            facebook.postFeed( postUpdate );
		}catch(RuntimeException e){
			LOG.error("Exception: "+e.getMessage(), e);
		}
		catch (FacebookException e) {
			LOG.error("Exception: "+e.getMessage(), e);
		}
	}

	public Facebook getFacebookInstance() {
		facebook4j.conf.ConfigurationBuilder confBuilder = new facebook4j.conf.ConfigurationBuilder();
		confBuilder.setOAuthAppId(facebookClientId);
		confBuilder.setOAuthAppSecret(facebookAppSecret);
		confBuilder.setOAuthCallbackURL(serverBaseUrl + facebookRedirectUri);
		confBuilder.setOAuthPermissions(facebookScope);
		facebook4j.conf.Configuration configuration = confBuilder.build();

		return new FacebookFactory(configuration).getInstance();
	}
	
	public static void main(String[] args){
		PostManuallyToSocialMedia post = new PostManuallyToSocialMedia();
		post.postToFacebook("5.0", "Don", "Nishit Kannan", "https://socialsurvey.me/pages/nishit-kannan", "Test Post", "");
	}

}
