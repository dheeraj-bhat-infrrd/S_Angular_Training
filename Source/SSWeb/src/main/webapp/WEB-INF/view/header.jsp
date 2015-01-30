<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<!DOCTYPE html>
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
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
</head>
<body>
    <div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
    </div>
	<div class="overlay-loader hide"></div>
	<div class="overlay-payment hide"></div>

	<div id="overlay-main" class="overlay-main hide">
		<div class="overlay-disable-wrapper">
			<div id="overlay-header" class="ol-header">
				<!-- Populated by javascript -->
			</div>
			<div class="ol-content">
				<div id="overlay-text" class="ol-txt">
					<!-- Populated by javascript -->
				</div>
				<div class="clearfix">
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-continue" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
					<div class="float-left ol-btn-wrapper">
						<div id="overlay-cancel" class="ol-btn cursor-pointer">
							<!-- Populated by javascript -->
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="header-slider-wrapper" class="header-slider-wrapper">
		<div class="header-slider">
			<div id="header-links-slider"
				class="header-links header-links-slider float-left clearfix">
				<div class="header-links-item">
					<a href="javascript:showMainContent('./dashboard.do')"><spring:message
							code="label.header.dashboard.key" /></a>
				</div>
				<c:if test="">
					<div class="header-links-item">
						<a
							href="javascript:showMainContent('./showbuildhierarchypage.do')"><spring:message
								code="label.header.company.key" /></a>
					</div>
				</c:if>
				<c:if test="${highestrole == 1}">
					<div class="header-links-item">
						<a href="#"><spring:message
								code="label.header.buildsurvey.key" /></a>
					</div>
				</c:if>
				<c:if test="${user.company.licenseDetails[0].accountsMaster.accountsMasterId != 1 && highestrole != 4}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./showusermangementpage.do')"><spring:message
								code="label.header.usermanagement.key" /></a>
					</div>
				</c:if>
				<c:if test="${highestrole == 1}">
				<!-- show the company settings only of the user has company admin as a role -->
				<div class="header-links-item">
					<a href="javascript:showMainContent('./showcompanysettings.do')">
						<spring:message code="label.settings.company.key" />
						</a>
					</div>
				</c:if>
				<c:if test="${user.company.licenseDetails[0].accountsMaster.accountsMasterId < 4}">
					<div class="header-links-item" id="upgrade-plan" onclick="upgradePlan();">
						<spring:message	code="label.header.upgrade.key" />
					</div>
				</c:if>
					<div class="header-links-item">
						<spring:message code="label.profilesetting.key" />
					</div>
					<div class="header-links-item">
						<spring:message code="label.accountsetting.key" />
					</div>
					<div class="header-links-item" >
						<a href="javascript:showMainContent('./showchangepasswordpage.do')">
						<spring:message code="label.changepassword.key"/></a>
					</div>
					<div class="header-links-item" >
						<a href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
					</div>
			</div>
		</div>
	</div>

	<div class="header-main-wrapper">
		<div class="container clearfix header-container">
			<div class="header-logo float-left" id="header-logo" style="cursor: pointer;"></div>
			<div id="header-links" class="header-links float-left clearfix">
				<div class="header-links-item">
					<a href="javascript:showMainContent('./dashboard.do')"><spring:message
							code="label.header.dashboard.key" /></a>
				</div>
				<c:if
					test="${(user.company.licenseDetails[0].accountsMaster.accountsMasterId == 4 || user.company.licenseDetails[0].accountsMaster.accountsMasterId == 3) && highestrole == 1}">
					<div class="header-links-item">
						<a
							href="javascript:showMainContent('./showbuildhierarchypage.do')"><spring:message
								code="label.header.company.key" /></a>
					</div>
				</c:if>
				<c:if test="${highestrole == 1}">
					<div class="header-links-item">
						<a href="#"><spring:message
								code="label.header.buildsurvey.key" /></a>
					</div>
				</c:if>
				<c:if test="${user.company.licenseDetails[0].accountsMaster.accountsMasterId != 1 && highestrole != 4}">
					<div class="header-links-item">
						<a href="javascript:showMainContent('./showusermangementpage.do')"><spring:message
								code="label.header.usermanagement.key" /></a>
					</div>
				</c:if>
			</div>
			<div class="header-user-info float-right clearfix">
				<div class="float-left user-info-initial">
					<span>${fn:substring(user.firstName, 0, 1)}</span>
					<div class="initial-dd-wrapper hide blue-arrow-bot">
						<c:if test="${highestrole == 1}">
							<!-- show the company settings only of the user has company admin as a role -->
							<div class="initial-dd-item" id="company-setting">
								<spring:message code="label.settings.company.key" />
							</div>
						</c:if>
						<div class="initial-dd-item" id="profile-setting">
							<spring:message code="label.profilesetting.key" />
						</div>
						<div class="initial-dd-item" id="account-setting">
							<spring:message code="label.accountsetting.key" />
						</div>
						<div class="initial-dd-item" id="change-password"onclick="showMainContent('./showchangepasswordpage.do')">
						<spring:message code="label.changepassword.key"/>
						</div>
						<c:if test="${user.company.licenseDetails[0].accountsMaster.accountsMasterId < 4}">
							<div class="initial-dd-item" id="upgrade-plan" onclick="upgradePlan();">
								<spring:message	code="label.header.upgrade.key" />
							</div>
						</c:if>
						<a class="initial-dd-item" href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
					</div>
				</div>


				<c:if test="${displaylogo != null}">
					<div class="float-left user-info-seperator"></div>
					<div class="float-left user-info-logo"
						style="background: url(${displaylogo}) no-repeat center; background-size: 100% auto;"></div>
				</c:if>

			</div>
			<div id="header-menu-icn"
				class="header-menu-icn icn-menu hide float-right"></div>
		</div>
	</div>
