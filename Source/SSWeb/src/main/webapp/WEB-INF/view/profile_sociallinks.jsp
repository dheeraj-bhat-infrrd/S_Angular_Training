<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty profile && not empty profile.socialMediaTokens}">
	<c:set value="${profile.socialMediaTokens}" var="socialMediaTokens"></c:set>
</c:if>
<c:if test="${not empty socialMediaTokens}">
	<c:set value="${socialMediaTokens.facebookToken}" var="facebookToken"></c:set>
	<c:set value="${socialMediaTokens.twitterToken}" var="twitterToken"></c:set>
	<c:set value="${socialMediaTokens.linkedInToken}" var="linkedInToken"></c:set>
	<c:set value="${socialMediaTokens.yelpToken}" var="yelpToken"></c:set>
</c:if>
<div class="float-left social-item-icon icn-fb" data-link="${facebookToken.facebookPageLink}"></div>
<div class="float-left social-item-icon icn-twit" data-link="${twitterToken.twitterPageLink}"></div>
<div class="float-left social-item-icon icn-lin" data-link="${linkedInToken.linkedInPageLink}"></div>
<div class="float-left social-item-icon icn-yelp" data-link="${yelpToken.yelpPageLink}"></div>
<input id="social-token-text" type="text" class="social-token-text hide" placeholder='<spring:message code="label.socialpage.placeholder.key"/>'>