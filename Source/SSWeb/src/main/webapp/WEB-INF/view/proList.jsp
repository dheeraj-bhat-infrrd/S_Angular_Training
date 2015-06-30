<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.prolist.title.key" /></title>
	<meta name="keywords"
		content="[SearchQuery], professional, online, reputation, social, survey, reviews, rating">
	<meta name="description"
		content="[countResults] Professionals reviewed. Find [SearchQuery] professional reviews, ratings, reputation, and contact information on SocialSurvey.me">
	<link rel="canonical" href="https://socialsurvey.me/findapro.do?find-pro-first-name=[firstName]&find-pro-last-name=[lastName]">
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
	<div class="body-wrapper">
		<div class="hdr-wrapper">
			<div class="container hdr-container clearfix">
				<div class="float-left hdr-logo"></div>
				<div class="float-right clearfix hdr-btns-wrapper">
					<div class="float-left hdr-log-btn hdr-log-reg-btn">
						<spring:message code="label.signin.key" />
					</div>
					<div class="float-left hdr-reg-btn hdr-log-reg-btn">
						<spring:message code="label.joinus.key" />
					</div>
				</div>
			</div>
		</div>
		<div id="err-nw-wrapper" class="err-nw-wrapper">
			<span class="err-new-close"></span>
			<span id="err-nw-txt"></span>
		</div>
		<div class="hero-wrapper">
			<div class="hero-container container">
				<div class="hr-txt"><spring:message code="label.notrelevatprofile.key" /></div>
			</div>
		</div>
		
		<div class="fp-wrapper">
			<div class="fp-container container clearfix">
				<div class="row">
					<div class="fp-row-wrapper clearfix">
						<div class="float-left fp-left-item"><spring:message code="label.findapro.key" /></div>
						<div class="float-left fp-right-item">
							<form id="find-pro-form" method="GET" action="./findapro.do">
								<div class="fp-wrapper clearfix">
									<input id="find-pro-first-name" name="find-pro-first-name" value="${patternFirst}"
										class="fp-inp" placeholder="First Name">
									<input id="find-pro-last-name" name="find-pro-last-name" value="${patternLast}"
										class="fp-inp" placeholder="Last Name">
									<input id="find-pro-submit" type="button" class="fp-inp pro-btn" value="Search">
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div class="ctnt-wrapper">
			<div class="ctnt-container container">
				<div class="row">
					<div class="ctnt-left-item col-lg-9 col-md-9 col-sm-9 col-xs-12">
						<div class="ctnt-list-header clearfix">
							<div class="ctnt-list-header-left float-left">
								<spring:message code="label.profilefoundfor.key" />
									<c:choose>
										<c:when test="${not empty searchCriteria}">
											<span class="srch-name">${searchCriteria}</span>
										</c:when>
										<c:otherwise>
											<span class="srch-name">${patternFirst} ${patternLast}</span>
											<input id="fp-first-name-pattern" type="hidden" value="${patternFirst}">
											<input id="fp-last-name-pattern" type="hidden" value="${patternLast}">
										</c:otherwise>
									</c:choose>
								<input id="fp-users-size" type="hidden">
								<input id="fp-profile-level-fetch-info" data-searchcriteria="${searchCriteria}"
									data-profile-level="${profileLevel}" data-iden="${iden}" type="hidden"/>
							</div>
							<div class="ctnt-list-header-right float-right">
								<span id="srch-num" class="srch-num"><spring:message code="label.no.key" /></span>
								<spring:message code="label.profilelistfound.key" />
							</div>
						</div>
						
						<div id="ctnt-list-wrapper" class="ctnt-list-wrapper"></div>
					</div>
					<div class="ctnt-right-item col-lg-3 col-md-3 col-sm-3 col-xs-12 ads-container" style="background: rgb(225, 225, 225); margin-top: 50px;"></div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer_new.jsp"></jsp:include>
	<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script-1.1.js"></script>
    <script src="${initParam.resourcesPath}/resources/js/script.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/profile_common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/application.js"></script>
	<script>
	$(document).ready(function() {
		$('#fp-users-size').val(0);
		
		fetchUsers(startIndex);
		adjustTextContainerWidthOnResize();
		
		$(window).resize(function() {
			if ($(window).width() < 768) {
				adjustTextContainerWidthOnResize();
			}
		});
	});
	</script>
</body>
</html>