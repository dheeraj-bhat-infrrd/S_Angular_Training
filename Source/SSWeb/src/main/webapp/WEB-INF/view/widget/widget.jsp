<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta charset="utf-8">
<c:if test="${not empty averageRating}">
	<fmt:formatNumber var="floatingAverageRating" type="number"
		value="${averageRating}" maxFractionDigits="2" minFractionDigits="3" />
	<fmt:formatNumber var="floatingAverageGoogleRating" type="number"
		value="${averageRating}" maxFractionDigits="1" minFractionDigits="1" />
	<fmt:formatNumber var="integerAverageRating" type="number"
		value="${averageRating}" maxFractionDigits="0" />
	<c:if test="${integerAverageRating == 6}">
		<c:set var="integerAverageRating" value="5"></c:set>
	</c:if>
	<c:if test="${integerAverageRating == 0}">
		<c:set var="integerAverageRating" value="1"></c:set>
	</c:if>
</c:if>
<c:choose>
	<c:when test="${ floatingAverageRating % 1 == 0 }">
		<fmt:formatNumber var="floatingAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="0" />
	</c:when>
	<c:otherwise>
		<fmt:formatNumber var="floatingAverageRating" type="number"
			value="${averageRating}" maxFractionDigits="1" minFractionDigits="1" />
	</c:otherwise>
</c:choose>
</head>
<body>
	<div itemprop="aggregateRating" itemscope
		itemtype="http://schema.org/AggregateRating"
		class="prof-rating clearfix">
		<div class="prof-rating-wrapper maring-0 clearfix float-left"
			id="rating-avg-comp">
			<div class='rating-rounded float-left'
				data-score="${floatingAverageRating}">
				<span itemprop="ratingValue">${floatingAverageRating}</span> -
			</div>
		</div>
		<div class="float-left review-count-left cursor-pointer"
			id="prof-company-review-count">
			<span itemprop="reviewCount">${reviewsCount}</span> Review(s)
		</div>
	</div>
	<c:choose>
		<c:when test="${profileLevel == 'INDIVIDUAL'}">
			<a href="/rest/survey/showsurveypage/${profile.iden}" target="_blank"><span
				class="prof-btn-survey float-left" id="read-write-share-btn">Write
					a Review</span></a>
		</c:when>
		<c:otherwise>
			<a
				href="/initfindapro.do?profileLevel=${profileLevel}&iden=${profile.iden}&searchCriteria=${profile.contact_details.name}"
				target="_blank"><span class="prof-btn-survey float-left"
				id="read-write-share-btn">Write a Review</span></a>
		</c:otherwise>
	</c:choose>

	<c:if test="${not empty surveys}">
		<div class="reviews">
			<c:forEach  items="${surveys }" var="reviewItem">
				<div class="review-item">
					<c:choose>
						<c:when test="${not empty reviewItem.customerLastName }">
							<c:set value="${reviewItem.customerFirstName} ${reviewItem.customerLastName}" var="author"></c:set>
						</c:when>
						<c:otherwise>
							<c:set value="${reviewItem.customerFirstName}" var="author"></c:set>
						</c:otherwise>
					</c:choose> 
					<div class="review-author">${author}</div>
					<div class="review-date">${reviewItem.modifiedOn}</div>
					<div class="review-body">${reviewItem.review}</div>
				</div>
			</c:forEach>
		</div>
	</c:if>
</body>
</html>