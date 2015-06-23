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
						<a href="https://www.facebook.com/sharer/sharer.php?u=${feedback.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-fb" title="Facebook"></span></a>
						<a href="https://twitter.com/home?status=${feedback.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-twit" title="Twitter"></span></a>
						<a href="https://www.linkedin.com/shareArticle?mini=true&url=${feedback.completeProfileUrl} &title=&summary=${feedback.score}-star response from ${feedback.customerFirstName} ${feedback.customerLastName} for ${feedback.agentName} at SocialSurvey - ${feedback.review} + &source=" target="_blank"><span class="float-left ppl-share-icns icn-lin" title="LinkedIn"></span></a>
                        <a href="https://plus.google.com/share?url=${feedback.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-gplus" title="Google+"></span></a>
						<c:if test="${not empty feedback.yelpProfileUrl}">
							<a href="${feedback.yelpProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-yelp" title="Yelp"></span></a>
						</c:if>
						<c:if test="${not empty feedback.zillowProfileUrl}">
							<a href="${feedback.zillowProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-zillow" title="Zillow"></span></a>
						</c:if>
						<c:if test="${not empty feedback.lendingTreeProfileUrl}">
							<a href="${feedback.lendingTreeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-lendingtree" title="LendingTree"></span></a>
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
<script src="${initParam.resourcesPath}/resources/js/timezones.js"></script>
<script>
$(document).ready(function(){
	$('.ppl-head-2').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
		var date = convertUTCToUserDate(new Date(dateSplit[0], dateSplit[1], dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
		$(this).html(date.toDateString());
	});
});
</script>