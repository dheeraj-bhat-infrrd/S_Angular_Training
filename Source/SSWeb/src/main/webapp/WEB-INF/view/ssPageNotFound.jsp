<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.prolist.title.key" /></title>
	<meta name="keywords" content="professional, online, reputation, social, survey, reviews, rating, SocialSurvey">
	<meta name="description" content="Find professional reviews, ratings, reputation, and contact information on SocialSurvey">
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
</head>
<body class="ss-not-found-page">
	<div class="overlay-loader hide"></div>
	<div class="body-wrapper">
		<div class="hdr-wrapper search-eng-hdr">
			<div class="container hdr-container clearfix">
				<div class="float-left hdr-logo"></div>
				<div class="float-right clearfix hdr-btns-wrapper">
					<div class="float-left hdr-log-btn hdr-log-reg-btn">
						<spring:message code="label.signin.key" />
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="ss-page-not-found">
		<div class="ss-404-img-cont">
			<img src="${initParam.resourcesPath}/resources/images/ss-404-not-found.png" alt="404 Not Found">
		</div>
		<div class="ss-page-not-found-txt">Page not found</div>
	</div>
	<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
	<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
</body>
</html>