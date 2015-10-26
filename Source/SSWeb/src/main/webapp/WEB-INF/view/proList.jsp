<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.prolist.title.key" /></title>
	<meta name="keywords"
		content="find-pro-first-name=${patternFirst}&find-pro-last-name=${patternLast}, professional, online, reputation, social, survey, reviews, rating">
	<meta name="description"
		content="${usersList.userFound } Professionals reviewed. Find find-pro-first-name=${patternFirst}&find-pro-last-name=${patternLast} professional reviews, ratings, reputation, and contact information on SocialSurvey.me">
	<link rel="canonical" href="https://socialsurvey.me/findapro.do?find-pro-first-name=${patternFirst}&find-pro-last-name=${patternLast}">
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body>
	<div class="overlay-loader hide"></div>
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
								<input id="fp-users-size" type="hidden" value="0">
								<input id="fp-profile-level-fetch-info" data-searchcriteria="${searchCriteria}"
									data-profile-level="${profileLevel}" data-iden="${iden}" type="hidden"/>
							</div>
							<div id="srch-num-list" class="ctnt-list-header-right float-right hide">
								<span id="srch-num" class="srch-num"><spring:message code="label.no.key" /></span>
								<spring:message code="label.profilelistfound.key" />
							</div>
						</div>
						
						<div id="ctnt-list-wrapper" class="ctnt-list-wrapper"></div>
												
						<div id="pro-paginate-btn" class="paginate-buttons-survey clearfix hide" data-start="0" data-total="0" data-batch="10">
							<div id="pro-prev" class="float-left sur-paginate-btn">&lt; Prev</div>
							<div class="paginate-sel-box float-left">
								<input id="sel-page-prolist" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
								<span class="paginate-divider">/</span>
								<span id="pro-total-pages" class="paginate-total-pages">0</span>
							</div>
							<div id="pro-next" class="float-right sur-paginate-btn">Next &gt;</div>
						</div>
					</div>
					<div class="ctnt-right-item col-lg-3 col-md-3 col-sm-3 col-xs-12 ads-container">
						<a href="http://mbshighway.com/sosur" target="_blank">
						<img class="mbs-highwayad" src="/resources/images/Sidebar_MBSHighway.png"></a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer_new.jsp"></jsp:include>
	<jsp:include page="scripts.jsp"></jsp:include>
	<script>
	$(document).ready(function() {
		startIndex = 0;
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