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
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/rangeslider.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
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
			value="${averageRating}" maxFractionDigits="3" minFractionDigits="3" />
	</c:otherwise>
</c:choose>
<style type="text/css">
.review-item {
    padding: 10px 0;
    border-bottom: 1px solid #dcdcdc;
    line-height: 20px;
}

.review-item:last-child {
    border-bottom: none;
}

.review-author {
    text-transform: capitalize;
    font-family: 'opensanssemibold';
    font-size: 14px;
}
</style>
</head>
<body>
	<div class="prof-rating-wrapper maring-0 clearfix" id="rating-avg-comp">
		<div class='rating-rounded float-left'
			data-score="${floatingAverageRating}">
			<div class="float-left">
				<div id="wdg-rating-cont"
					class="st-rating-wrapper maring-0 clearfix">
					<div class="rating-star icn-no-star"></div>
					<div class="rating-star icn-no-star"></div>
					<div class="rating-star icn-no-star"></div>
					<div class="rating-star icn-no-star"></div>
					<div class="rating-star icn-no-star"></div>
				</div>
			</div>
			<div class="float-left">
				<span id="rating">${floatingAverageRating}</span>
			</div>
		</div>
	</div>
	<div class="clearfix">
		<div class="float-left review-count-left cursor-pointer"
			id="prof-company-review-count">
			<span>${reviewsCount}</span> Review(s)
		</div>
		<div class="float-left">
			<c:choose>
				<c:when test="${profileLevel == 'INDIVIDUAL'}">
					<a href="/rest/survey/showsurveypage/${profile.iden}"
						target="_blank"><span class="prof-btn-survey float-left"
						id="read-write-share-btn">Write a Review</span></a>
				</c:when>
				<c:otherwise>
					<a
						href="/initfindapro.do?profileLevel=${profileLevel}&iden=${profile.iden}&searchCriteria=${profile.contact_details.name}"
						target="_blank"><span class="prof-btn-survey float-left">Write
							a Review</span></a>
				</c:otherwise>
			</c:choose>
		</div>
	</div>

	<c:if test="${not empty surveys}">
		<div class="reviews">
			<c:forEach items="${surveys }" var="reviewItem">
				<div class="review-item">
					<c:choose>
						<c:when test="${not empty reviewItem.customerLastName }">
							<c:set
								value="${reviewItem.customerFirstName} ${fn:substring(reviewItem.customerLastName, 0, 1)}."
								var="author"></c:set>
						</c:when>
						<c:otherwise>
							<c:set value="${reviewItem.customerFirstName}" var="author"></c:set>
						</c:otherwise>
					</c:choose>
					<div class="review-author">${author}</div>
					<div class="review-date"><fmt:formatDate value="${reviewItem.modifiedOn}" pattern="M dd, yyyyy"/></div>
					<div class="review-body">${reviewItem.review}</div>
				</div>
			</c:forEach>
		</div>
	</c:if>

	<script
		src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script type="text/javascript">
		function changeWidgetRatingPattern(rating, ratingParent) {
			var counter = 0;
			ratingParent.children().each(function() {
				$(this).addClass("icn-no-star");
				$(this).removeClass("icn-half-star");
				$(this).removeClass("icn-full-star");

				if (rating >= counter) {
					if (rating - counter >= 1) {
						$(this).removeClass("icn-no-star");
						$(this).addClass("icn-full-star");
					} else if (rating - counter == 0.5) {
						$(this).removeClass("icn-no-star");
						$(this).addClass("icn-half-star");
					}
				}
				counter++;
			});
		}
		var rating = document.getElementById("rating").innerText;
		changeWidgetRatingPattern(rating, $('#wdg-rating-cont'));
	</script>
</body>
</html>