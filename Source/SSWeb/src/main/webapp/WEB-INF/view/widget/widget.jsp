<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta charset="utf-8">
    <link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/perfect-scrollbar.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
	<c:if test="${not empty averageRating}">
    	<fmt:formatNumber var="floatingAverageRating" type="number" value="${averageRating}" maxFractionDigits="2" minFractionDigits="3"/>
    	<fmt:formatNumber var="floatingAverageGoogleRating" type="number" value="${averageRating}" maxFractionDigits="1" minFractionDigits="1"/>
    	<fmt:formatNumber var="integerAverageRating" type="number" value="${averageRating}" maxFractionDigits="0"/>
    	<c:if test="${integerAverageRating == 6}">
    		<c:set var="integerAverageRating" value="5"></c:set>
    	</c:if>
    	<c:if test="${integerAverageRating == 0}">
    		<c:set var="integerAverageRating" value="1"></c:set>
    	</c:if>
    </c:if>
    <c:choose>
		<c:when test="${ floatingAverageRating % 1 == 0 }">
			<fmt:formatNumber var="floatingAverageRating" type="number" value="${averageRating}" maxFractionDigits="0"/>
		</c:when>
		<c:otherwise>
			<fmt:formatNumber var="floatingAverageRating" type="number" value="${averageRating}" maxFractionDigits="1" minFractionDigits="1"/>
		</c:otherwise>
	</c:choose>
	</head>
<body>

					<div itemprop="aggregateRating" itemscope itemtype="http://schema.org/AggregateRating" class="prof-rating clearfix">
						<div class="prof-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp">
							<div class='rating-rounded float-left' data-score="${floatingAverageRating}"><span itemprop="ratingValue">${floatingAverageRating}</span> - </div>
						</div>
						<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"><span itemprop="reviewCount">${reviewsCount}</span> Review(s)</div>
					</div>
					
					
					<c:choose>
						<c:when test="${profileLevel == 'INDIVIDUAL'}">
							<a href="/rest/survey/showsurveypage/${profile.iden}" target="_blank"><span class="prof-btn-survey float-left" id="read-write-share-btn">Write a Review</span></a>
						</c:when>
						<c:otherwise>
							<a href="/initfindapro.do?profileLevel=${profileLevel}&iden=${profile.iden}&searchCriteria=${profile.contact_details.name}" target="_blank"><span class="prof-btn-survey float-left" id="read-write-share-btn">Write a Review</span></a>
						</c:otherwise>
					</c:choose>
					
					<div class="people-say-wrapper rt-content-main hide" id="reviews-container">
                    	<div id="prof-review-item" class="prof-reviews">
	                   		<!--  reviews get populated here -->
                    	</div>
                	</div>
                	<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/date.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
<script src="${initParam.resourcesPath}/resources/js/profile.js"></script>
<script src="${initParam.resourcesPath}/resources/js/googletracking.js"></script>
<script src="${initParam.resourcesPath}/resources/js/googlemaps.js"></script>
<script src="${initParam.resourcesPath}/resources/js/timezones.js"></script>
<script src="${initParam.resourcesPath}/resources/js/perfect-scrollbar.jquery.min.js"></script>
<script></script>
</body>
</html>