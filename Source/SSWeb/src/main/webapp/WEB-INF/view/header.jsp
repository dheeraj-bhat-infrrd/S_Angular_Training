<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.login.title.key" /></title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
<body>
	<div id="header-slider-wrapper" class="header-slider-wrapper">
		<div class="header-slider">
			<div id="header-links-slider"
				class="header-links header-links-slider float-left clearfix">
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.dashboard.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('./showbuildhierarchypage.do')"><spring:message
							code="label.header.company.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.buildsurvey.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.usermanagement.key" /></a>
				</div>
			</div>
		</div>
	</div>

	<div class="header-main-wrapper">
		<div class="container clearfix header-container">
			<div class="header-logo float-left"></div>
			<div id="header-links" class="header-links float-left clearfix">
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.dashboard.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('./showbuildhierarchypage.do')"><spring:message
							code="label.header.company.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.buildsurvey.key" /></a>
				</div>
				<div class="header-links-item">
					<a href="javascript:showMainContent('')"><spring:message
							code="label.header.usermanagement.key" /></a>
				</div>
			</div>
			<div class="header-user-info float-right clearfix">
				<div class="float-left user-info-initial">
                    <span>${fn:substring(user.loginName, 0, 1)}</span>
                    <div class="initial-dd-wrapper hide blue-arrow-bot">
                        <div class="initial-dd-item">Profile Settings</div>
                        <div class="initial-dd-item">Account Settings</div>
                        <div class="initial-dd-item">Change Password</div>
                        <div class="initial-dd-item">Log Out</div>
                    </div>
                </div>
<!--
				<div class="float-left user-info-seperator"></div>
				<div class="float-left user-info-logo"></div>
				<div class="float-left user-info-initial">${fn:substring(user.loginName, 0, 1)}</div>
-->
				<c:if test="${displaylogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo" style="background: url(${pageContext.request.contextPath}/resources/logos/${displaylogo}) no-repeat center; background-size: 100% auto;"></div>
				</c:if>
			</div>			
			<div id="header-menu-icn" class="header-menu-icn icn-menu hide float-right"></div>
		</div>
	</div>