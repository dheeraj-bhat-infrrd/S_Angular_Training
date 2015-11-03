<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

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

<div class="float-right dash-main-right col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dsh-graph-wrapper">
		<div class="dsh-g-wrap dsh-g-wrap-1">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-1" class="dsh-graph-img"></div>
				<div id="srv-scr" class="dsh-graph-num">${socialScore}</div>
				<div class="dsh-graph-txt dsh-graph-txt-1"><spring:message code="label.surveyscore.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-2">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-2" class="dsh-graph-img"></div>
				<div id="srv-snt-cnt" class="dsh-graph-num">${surveyCount}</div>
				<div class="dsh-graph-txt dsh-graph-txt-2"><spring:message code="label.totalsurveys.key" /></div>
			</div>
		</div>
		<div class="dsh-g-wrap dsh-g-wrap-3">
			<div class="dsh-graph-item dsh-graph-item-3">
				<div id="dg-img-3" class="dsh-graph-img"></div>
				<div id="socl-post" class="dsh-graph-num">${socialPosts}</div>
				<div class="dsh-graph-txt dsh-graph-txt-3"><spring:message code="label.socialposts.key" /></div>
			</div>
		</div>
		
		<div class="dsh-g-wrap dsh-g-wrap-4">
			<div class="dsh-graph-item dsh-graph-item-1">
				<div id="dg-img-4" class="dsh-graph-img dsh-graph-img-4"></div>
				<div id="dsh-prsn-img" class="dsh-graph-num dsh-graph-num-4"></div>
				<div class="dsh-graph-txt dsh-graph-txt-4"><spring:message code="label.profilecomplete.key" /></div>
				<div id="badges" class="dsg-g-rbn dsg-g-rbn-${badges}"></div>
			</div>
		</div>
	</div>
</div>

<div class="float-left dash-main-left col-lg-6 col-md-6 col-sm-6 col-xs-12">
	<div class="dash-left-txt-wrapper">
		<div class="dsh-name-wrapper">
			<div id="name" class="dsh-txt-1">${name}</div>
			<c:if test="${not empty title}">
				<div id="designation" class="dsh-txt-2">${title}</div>
			</c:if>
			<c:if test="${not empty location || not empty vertical}">
				<div class="dsh-txt-3">
					<c:if test="${not empty location}">
						${location }
						<c:set var="isLocationTrue" value="yes"></c:set>	
					</c:if>
					<c:if test="${not empty vertical }">
						<c:if test="${isLocationTrue == 'yes'}"> | </c:if>
						${vertical}
					</c:if>
				</div>
			</c:if>
			<c:if test="${not empty company}">
				<div id="company" class="dsh-txt-3 hide">${company}</div>
			</c:if>
		</div>
		<div id="pro-cmplt-stars" class="dsh-star-wrapper clearfix">
			<%-- <div class="float-left dsh-star-item"></div>
			<div id="profile-completed" class="float-left dsh-rating-item">${fn:substringBefore(profileCompleteness * 5 / 100, '.')}/5</div> --%>
			<div id="dsh-btn1" class="dsh-btn-complete float-left"><spring:message code="label.sendsurvey.btn.key" /></div>
			<div id="dsh-btn2" class="dsh-btn-complete dsh-btn-orange float-left hide"></div>
			<div id="dsh-btn3" class="dsh-btn-complete dsh-btn-green float-left hide"></div>
			<%-- <c:choose>
				<c:when test="${profilemasterid == 4}">
					<div class="dsh-btn-complete float-left" onclick="sendSurveyInvitation();"><spring:message code="label.sendsurvey.btn.key" /></div>
				</c:when>
				<c:otherwise>
					<div class="dsh-btn-complete float-left" onclick="showMainContent('./showprofilepage.do');"><spring:message code="label.complete.profile.key" /></div>
				</c:otherwise>
			</c:choose> --%>
		</div>
	</div>
</div>

<script>
$(document).ready(function() {
	// Social Posts
	$('#dg-img-3').find('svg').remove();
	var socialPosts = "${socialPosts}";
	var circle1 = new ProgressBar.Circle('#dg-img-3', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseFloat(socialPosts) / maxSocialPosts) > 1)
		circle1.animate(1);
	else
		circle1.animate(parseFloat(socialPosts) / maxSocialPosts);

	// Survey Count
	$('#dg-img-2').find('svg').remove();
	var surveyCount = "${surveyCount}";
	var circle2 = new ProgressBar.Circle('#dg-img-2', {
		color : '#E97F30',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseInt(surveyCount) / maxSurveySent) > 1)
		circle2.animate(1);
	else
		circle2.animate(parseInt(surveyCount) / maxSurveySent);
	
	// Social Score
	$('#dg-img-1').find('svg').remove();
	var socialScore = "${socialScore}";
	var circle3 = new ProgressBar.Circle('#dg-img-1', {
		color : '#5CC7EF',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	if ((parseFloat(socialScore) / 5) > 1)
		circle3.animate(1);
	else
		circle3.animate(parseFloat(socialScore) / 5);

	// Profile completion
	$('#dg-img-4').find('svg').remove();
	var circle4 = new ProgressBar.Circle('#dg-img-4', {
		color : '#7AB400',
		fill : "rgba(249,249,251, 1)",
		duration : 1500,
		strokeWidth : 4,
		easing : 'easeInOut'
	});
	var profileCompleted = parseInt("${profileCompleteness}");
	if ((profileCompleted / 100) > 1)
		circle4.animate(1);
	else
		circle4.animate(profileCompleted / 100);
	
	// Change rating pattern
	var starVal = profileCompleted * 5 / 100;
	
	var ratingIntVal = 0;
	

	if (ratingIntVal % 1 == 0) {
		ratingIntVal = parseInt(starVal);
	} else {
		ratingIntVal = parseInt(starVal) + 1;
	}

	if (ratingIntVal == 0) {
		ratingIntVal = 1;
	}
	
	$("#pro-cmplt-stars").find('.dsh-star-item').addClass('smiley-bg-rat-'+ratingIntVal);
	/* $("#pro-cmplt-stars").find('.dsh-star-item').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('no-star');
			$(this).addClass('sq-full-star');
		}
	}); */
	
	/* $('#srv-scr').click(function(){
		showSurveyRequestPage();
	}); */
	
	var columnName = "${columnName}";
	var columnValue = "${columnValue}";
	showDashboardButtons(columnName, columnValue);
});
</script>