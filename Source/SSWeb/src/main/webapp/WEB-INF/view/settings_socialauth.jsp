<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- Check if auto login -->
<c:choose>
	<c:when test="${isAutoLogin == 'true' && allowOverrideForSocialMedia == 'false' }">
		<c:set var="socialDisabled" value="social-auth-disabled"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="isAutoLogin" value="false"></c:set>
	</c:otherwise>
</c:choose>

<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty facebookLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia(event,'facebook', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-fb soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'facebook', ${isAutoLogin}, this);" data-link="${facebookLink}"></div>
	<div id="edt-prof-fb-lnk" class="float-left soc-nw-icn-link"
		data-social='facebook'>${facebookLink}</div>
</div>
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty twitterLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia(event,'twitter', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-twitter soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'twitter', ${isAutoLogin}, this);" data-link="${twitterLink}"></div>
	<div id="edt-prof-twt-lnk" class="float-left soc-nw-icn-link"
		data-social='twitter'>${twitterLink}</div>
</div>
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${linkedinConnected}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia(event,'linkedin', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-linkedin soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'linkedin', ${isAutoLogin}, this);" data-link="${linkedinLink}"></div>
		
	
	<c:choose>
		<c:when test="${linkedinLinkNotFound}">
			<div id="edt-prof-linkedin-lnk" class="float-left soc-nw-icn-link"
				data-social='linkedin'><spring:message code="label.linkedin.profileurl.notfound" /></div>
		</c:when>
		<c:otherwise>
			<div id="edt-prof-linkedin-lnk" class="float-left soc-nw-icn-link"
				data-social='linkedin'>${linkedinLink}</div>
		</c:otherwise>
	</c:choose>	

	
</div>
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty googleLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia(event,'google', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-gplus soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'google', ${isAutoLogin}, this);" data-link="${googleLink}"></div>
	<div id="edt-prof-ggl-lnk" class="float-left soc-nw-icn-link"
		data-social='google'>${googleLink}</div>
</div>
<!-- Zillow link -->
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty zillowLink}">
			<div class="social-media-disconnect float-left"
				onclick="disconnectSocialMedia(event,'zillow')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-zillow soc-nw-adj"
		onclick="openAuthPageZillow(event,'.icn-wide-zillow');"></div>
	<div id="edt-prof-zillow-lnk" class="float-left soc-nw-icn-link"
		data-social='zillow'>${zillowLink}</div>
</div>
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty instagramLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia(event,'instagram', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-instagram soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'instagram', ${isAutoLogin}, this);" data-link="${instagramLink}"></div>
	<div id="edt-prof-insta-lnk" class="float-left soc-nw-icn-link"
		data-social='instagram'>${instagramLink}</div>
</div>