<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-fb"
		onclick="authenticate(event,'facebook');"></div>
	<div id="fb-profile-url" class="wc-icn-txt float-left">${facebookProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-twitter"
		onclick="authenticate(event,'twitter');"></div>
	<div id="twitter-profile-url" class="wc-icn-txt float-left">${twitterProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-linkedin"
		onclick="authenticate(event,'linkedin');"></div>
	<div id="linkedin-profile-url" class="wc-icn-txt float-left">${linkedinProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-gplus"
		onclick="authenticate(event,'google');"></div>
	<div id="ggl-profile-url" class="wc-icn-txt float-left">${googleProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-zillow"
		onclick="authenticateZillow(event);"></div>
	<div id="zillow-profile-url" class="wc-icn-txt float-left">${zillowProfileUrl}</div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn icn-wide-yelp float-left" onclick="showYelpInput();"></div>
	<input id="yelp-profile-url"
		class="wc-icn-txt float-left wc-form-input hide">
	<div id="yelp-profile-url-display" class="wc-icn-txt float-left"></div>
</div>
<div class="wc-social-icn-row clearfix">
	<div class="wc-social-icn float-left icn-wide-instagram"
		onclick="authenticate(event,'instagram');"></div>
	<div id="insta-profile-url" class="wc-icn-txt float-left">${instagramProfileUrl}</div>
</div>
<!-- <div class="wc-social-icn-row clearfix">
				<div class="wc-social-icn i-rss float-left"></div>
				<div class="wc-icn-txt float-left"></div>
			</div> -->