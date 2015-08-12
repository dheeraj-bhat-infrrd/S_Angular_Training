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
						<span class="float-left ppl-share-icns icn-fb" title="Facebook" data-link="https://www.facebook.com/sharer/sharer.php?u=${reviewItem.completeProfileUrl}"></span>
						<span class="float-left ppl-share-icns icn-twit" title="Twitter" data-link="https://twitter.com/home?status=${reviewItem.completeProfileUrl}"></span>
						<span class="float-left ppl-share-icns icn-lin" title="LinkedIn" data-link="https://www.linkedin.com/shareArticle?mini=true&url=${reviewItem.completeProfileUrl} &title=&summary=${reviewItem.score}-star response from ${reviewItem.customerFirstName} ${reviewItem.customerLastName} for ${reviewItem.agentName} at SocialSurvey - ${reviewItem.review} + &source="></span>
                        <span class="float-left ppl-share-icns icn-gplus" title="Google+" data-link="https://plus.google.com/share?url=${reviewItem.completeProfileUrl}"></span>
						<c:if test="${not empty reviewItem.yelpProfileUrl}">
							<span class="float-left ppl-share-icns icn-yelp" title="Yelp" data-link="${reviewItem.yelpProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty reviewItem.zillowProfileUrl}">
							<span class="float-left ppl-share-icns icn-zillow" title="Zillow" data-link="${reviewItem.zillowProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty reviewItem.lendingTreeProfileUrl}">
							<span class="float-left ppl-share-icns icn-lendingtree" title="LendingTree" data-link="${reviewItem.lendingTreeProfileUrl}"></span>
						</c:if>
						<c:if test="${not empty reviewItem.realtorProfileUrl}">
							<span class="float-left ppl-share-icns icn-realtor" title="Realtor" data-link="${reviewItem.realtorProfileUrl}"></span>
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
	
	$('.icn-realtor').each(function(index, currentElement) {
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