<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left i-fb"
		onclick="authenticate('facebook');"></div>
	<div id="fb-profile-url" class="wc-icn-txt float-left">${facebookProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left i-twt"
		onclick="authenticate('twitter');"></div>
	<div id="twitter-profile-url" class="wc-icn-txt float-left">${twitterProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left i-ln"
		onclick="authenticate('linkedin');"></div>
	<div id="linkedin-profile-url" class="wc-icn-txt float-left">${linkedinProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left i-gplus"
		onclick="authenticate('google');"></div>
	<div id="ggl-profile-url" class="wc-icn-txt float-left">${googleProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn i-yelp float-left" onclick="showYelpInput();"></div>
	<input id="yelp-profile-url"
		class="wc-icn-txt float-left wc-form-input hide">
	<div id="yelp-profile-url-display" class="wc-icn-txt float-left"></div>
</div>
<!-- <div class="wc-social-icn-row clearfix">
				<div class="wc-social-icn i-rss float-left"></div>
				<div class="wc-icn-txt float-left"></div>
			</div> -->