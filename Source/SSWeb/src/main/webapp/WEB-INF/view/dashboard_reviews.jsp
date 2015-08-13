<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:choose>
	<c:when test="${not empty reviews}">
		<c:forEach var="feedback" varStatus="loop" items="${reviews}">
			<c:set value="ppl-review-item" var="reviewitemclass"></c:set>
			<div data-firstname="${feedback.customerFirstName}" data-lastname="${feedback.customerLastName}"
				data-agentid="${feedback.agentId}" data-agentname="${feedback.agentName}" data-customeremail="${feedback.customerEmail}"
				data-review="${feedback.review}" data-score="${feedback.score}" class="${reviewitemclass}">
				
				<div class="ppl-header-wrapper clearfix">
					<div class="float-left ppl-header-left">
						<div class="ppl-head-1">${feedback.customerFirstName} ${feedback.customerLastName}</div>
						<div class="ppl-head-2" data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-hh-mm-ss"
							value="${feedback.modifiedOn}" />">
						</div>
					</div>
					<div class="float-right ppl-header-right">
						<div class="st-rating-wrapper maring-0 clearfix review-ratings float-right" data-rating="${feedback.score}">
						</div>
						<div class="report-resend-icn-container clearfix float-right">
							<div class="report-abuse-txt report-txt">Report</div>
							|
							<div class="restart-survey-mail-txt report-txt">Resend</div>
						</div>
					</div>
				</div>
				<div class="ppl-content">${feedback.review}</div>
				<div class="ppl-share-wrapper clearfix">
					<div class="float-left blue-text ppl-share-shr-txt"><spring:message code="label.share.key" /></div>
					<div class="float-left icn-share icn-plus-open" style="display: block;"></div>
					<div class="float-left clearfix ppl-share-social hide" style="display: none;">
						<span class="float-left ppl-share-icns icn-fb" title="Facebook" data-link="https://www.facebook.com/sharer/sharer.php?u=${feedback.completeProfileUrl}"></span>
						<span class="float-left ppl-share-icns icn-twit" title="Twitter" data-link="https://twitter.com/home?status=${feedback.completeProfileUrl}"></span>
						<span class="float-left ppl-share-icns icn-lin" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=${feedback.completeProfileUrl} &title=&summary=${feedback.score}-star response from ${feedback.customerFirstName} ${feedback.customerLastName} for ${feedback.agentName} at SocialSurvey - ${feedback.review} + &source="></span>
                        <span class="float-left ppl-share-icns icn-gplus" title="Google+" data-link="https://plus.google.com/share?url=${feedback.completeProfileUrl}"></span>
						<c:if test="${not empty feedback.yelpProfileUrl}">
							<span class="float-left ppl-share-icns icn-yelp" title="Yelp" data-link="${feedback.yelpProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty feedback.zillowProfileUrl}">
							<span class="float-left ppl-share-icns icn-zillow" title="Zillow" data-link="${feedback.zillowProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty feedback.lendingTreeProfileUrl}">
							<span class="float-left ppl-share-icns icn-lendingtree" title="LendingTree" data-link="${feedback.lendingTreeProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty feedback.realtorProfileUrl}">
							<span class="float-left ppl-share-icns icn-realtor" title="Realtor" data-link="${feedback.realtorProfileUrl}"></span>
						</c:if>
					</div>
					<div class="float-left icn-share icn-remove icn-rem-size hide" style="display: none;"></div>
				</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<div class="dash-lp-header" id="incomplete-survey-header"><spring:message code="label.noincompletesurveys.key" /></div>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function(){
	$('.ppl-head-2').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
		var date = convertUserDateToLocale(new Date(dateSplit[0], dateSplit[1]-1, dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
		$(this).html(date.toDateString());
	});
	
	$('.icn-yelp').each(function(index, currentElement) {
		var url = $(this).parent().attr('href');
		$(this).parent().attr('href', returnValidWebAddress(url));
	});
	$('.icn-zillow').each(function(index, currentElement) {
		var url = $(this).parent().attr('href');
		$(this).parent().attr('href', returnValidWebAddress(url));
	});
	$('.icn-lendingtree').each(function(index, currentElement) {
		var url = $(this).parent().attr('href');
		$(this).parent().attr('href', returnValidWebAddress(url));
	});
	
	$('.ppl-share-icns').bind('click', function() {
		var link = $(this).attr('data-link');
		var title = $(this).attr('title');
		if (link == undefined || link == "") {
			return false;
		}
		window.open(link, 'Post to ' + title, 'width=800,height=600,scrollbars=yes');
	});
});
</script>