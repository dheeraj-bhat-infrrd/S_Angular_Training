<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:choose>
	<c:when test="${not empty reviewItems}">
		<c:forEach var="reviewItem" varStatus="loop" items="${reviewItems}">
			<c:set value="ppl-review-item" var="reviewitemclass"></c:set>
			<c:if test="${loop.last}">
				<c:set value="ppl-review-item-last" var="reviewitemclass"></c:set>
			</c:if>
			<div data-firstname="${reviewItem.customerFirstName}" data-lastname="${reviewItem.customerLastName}"
				data-review="${reviewItem.review}" data-score="${reviewItem.score}"
				data-agentname="${reviewItem.agentName}" class="${reviewitemclass}">
				
				<div class="ppl-header-wrapper clearfix">
					<div class="float-left ppl-header-left">
						<div class="ppl-head-1">${reviewItem.customerFirstName} ${reviewItem.customerLastName}</div>
						<div class="ppl-head-2" data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-hh-mm-ss"
							value="${reviewItem.modifiedOn}" />"></div>
					</div>
					<div class="float-right ppl-header-right">
						<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating="${reviewItem.score}">
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-half-star"></div>
							<div class="rating-star icn-no-star"></div>
							<div class="rating-star icn-no-star"></div>
						</div>
					</div>
				</div>
				<div class="ppl-content">${reviewItem.review}</div>
				<div class="ppl-share-wrapper clearfix">
					<div class="float-left blue-text ppl-share-shr-txt"><spring:message code="label.share.key"/></div>
					<div class="float-left icn-share icn-plus-open"></div>
					<div class="float-left clearfix ppl-share-social hide">
						<a href="https://www.facebook.com/sharer/sharer.php?u=${reviewItem.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-fb fb-shr" title="Facebook"></span></a>
						<a href="https://twitter.com/home?status=${reviewItem.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-twit twt-shr" title="Twitter"></span></a>
						<a href="https://www.linkedin.com/shareArticle?mini=true&url=${reviewItem.completeProfileUrl} &title=&summary=${reviewItem.score}-star response from ${reviewItem.customerFirstName} ${reviewItem.customerLastName} for ${reviewItem.agentName} at SocialSurvey - ${reviewItem.review} + &source=" target="_blank"><span class="float-left ppl-share-icns icn-lin lnkdn-shr" title="LinkedIn"></span></a>
                        <a href="https://plus.google.com/share?url=${reviewItem.completeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-gplus yelp-shr" title="Google+"></span></a>
						<c:if test="${not empty reviewItem.yelpProfileUrl}">
							<a href="${reviewItem.yelpProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-yelp yelp-shr" title="Yelp"></span></a>
						</c:if>
						<c:if test="${not empty reviewItem.zillowProfileUrl}">
							<a href="${reviewItem.zillowProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-zillow yelp-shr" title="Zillow"></span></a>
						</c:if>
						<c:if test="${not empty reviewItem.lendingTreeProfileUrl}">
							<a href="${reviewItem.lendingTreeProfileUrl}" target="_blank"><span class="float-left ppl-share-icns icn-lendingtree yelp-shr" title="LendingTree"></span></a>
						</c:if>
					</div>
					<div class="float-left icn-share icn-remove icn-rem-size hide"></div>
				</div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<spring:message code="label.noreviews.key"/>
	</c:otherwise>
</c:choose>
<script>
$(document).ready(function(){
	$('.ppl-head-2').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
	//	var date = convertUTCToUserDate(new Date(dateSplit[0], dateSplit[1], dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
		var date = convertUserDateToLocale(new Date(dateSplit[0], dateSplit[1], dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
		$(this).html(date.toDateString());
	});
});
</script>