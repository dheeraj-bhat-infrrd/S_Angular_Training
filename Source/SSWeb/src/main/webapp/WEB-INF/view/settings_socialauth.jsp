<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Check if auto login -->
<c:choose>
	<c:when test="${isAutoLogin == 'true' }">
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
				onclick="disconnectSocialMedia('facebook', ${isAutoLogin})" title="Disconnect"></div>
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
				onclick="disconnectSocialMedia('twitter', ${isAutoLogin})" title="Disconnect"></div>
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
		<c:when test="${not empty linkedinLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia('linkedin', ${isAutoLogin})" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left ${socialDisabled}"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-linkedin soc-nw-adj ${socialDisabled}"
		onclick="openAuthPage(event,'linkedin', ${isAutoLogin}, this);" data-link="${linkedinLink}"></div>
	<div id="edt-prof-linkedin-lnk" class="float-left soc-nw-icn-link"
		data-social='linkedin'>${linkedinLink}</div>
</div>
<div class="soc-nw-icns-cont clearfix">
	<c:choose>
		<c:when test="${not empty googleLink}">
			<div class="social-media-disconnect float-left ${socialDisabled}"
				onclick="disconnectSocialMedia('google', ${isAutoLogin})" title="Disconnect"></div>
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
				onclick="disconnectSocialMedia('zillow')" title="Disconnect"></div>
		</c:when>
		<c:otherwise>
			<div class="social-media-disconnect social-media-disconnect-disabled float-left"></div>
		</c:otherwise>
	</c:choose>
	<div
		class="float-left soc-nw-icns cursor-pointer icn-wide-zillow soc-nw-adj"
		onclick="openAuthPageZillow(event,'.icn-wide-zillow');"></div>
	<div id="edt-prof-ggl-lnk" class="float-left soc-nw-icn-link"
		data-social='zillow'>${zillowLink}</div>
</div>