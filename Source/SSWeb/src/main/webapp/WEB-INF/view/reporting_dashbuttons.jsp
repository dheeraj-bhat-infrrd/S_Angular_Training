<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="hiddenSection" value="${hiddenSection}"></c:set>

<!-- Check if auto login -->
<c:choose>
	<c:when test="${isAutoLogin == 'true' && allowOverrideForSocialMedia == 'false' }">
		<c:set var="isAutoLogin" value="true"></c:set>
	</c:when>
	<c:otherwise>
		<c:set var="isAutoLogin" value="false"></c:set>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>

<div class="float-left dash-main-left col-lg-12 col-md-12 col-sm-12 col-xs-16">
	<div class="dash-left-txt-wrapper" style="padding: 20px 0; padding-left: 20px;">
		<c:if test="${hiddenSection && profilemasterid!=4}">
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn0" class="dsh-btn-complete dsh-btn-red float-left <c:if test="${not isSocialMediaExpired}">hide</c:if> ">Reconnect Social Media</div>
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide"></div>
			<div id="dsh-btn3" class="dsh-btn-complete dsh-btn-green float-left hide"></div>
		</div>
		</c:if>
		<c:if test="${!hiddenSection}">
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix" data-profilecompleteness="${profileCompleteness}" data-autologin="${isAutoLogin}">
			<div id="dsh-btn0" class="dsh-btn-complete dsh-btn-red float-left  <c:if test="${not isSocialMediaExpired}">hide</c:if> ">Reconnect Social Media</div>
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide"></div>
			<div id="dsh-btn3" class="dsh-btn-complete dsh-btn-green float-left hide"></div>
		</div>
		</c:if>
	</div>
</div>