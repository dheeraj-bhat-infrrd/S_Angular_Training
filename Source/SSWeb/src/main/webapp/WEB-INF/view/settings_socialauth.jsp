<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty facebookLink}">
			<div class="social-media-disconnect float-left"
				onclick="disconnectSocialMedia('facebook')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-fb soc-nw-adj"
		onclick="openAuthPage('facebook');"></div>
	<div id="edt-prof-fb-lnk" class="float-left soc-nw-icn-link"
		data-social='facebook'>${facebookLink}</div>
</div>
<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-gplus" onclick="openAuthPage('google');"></div> -->
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty twitterLink}">
			<div class="social-media-disconnect float-left"
				onclick="disconnectSocialMedia('twitter')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-twitter soc-nw-adj"
		onclick="openAuthPage('twitter');"></div>
	<div id="edt-prof-twt-lnk" class="float-left soc-nw-icn-link"
		data-social='twitter'>${twitterLink}</div>
</div>
<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-rss" onclick="openAuthPage('rss');"></div> -->
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty linkedinLink}">
			<div class="social-media-disconnect float-left"
				onclick="disconnectSocialMedia('linkedin')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-linkedin soc-nw-adj"
		onclick="openAuthPage('linkedin');"></div>
	<div id="edt-prof-linkedin-lnk" class="float-left soc-nw-icn-link"
		data-social='linkedin'>${linkedinLink}</div>
</div>
<!-- <div class="float-left soc-nw-icns cursor-pointer icn-wide-yelp" onclick="openAuthPage('yelp');"></div> -->
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty googleLink}">
			<div class="social-media-disconnect float-left"
				onclick="disconnectSocialMedia('google')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-gplus soc-nw-adj"
		onclick="openAuthPage('google');"></div>
	<div id="edt-prof-ggl-lnk" class="float-left soc-nw-icn-link"
		data-social='google'>${googleLink}</div>
</div>