<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:choose>
	<c:when test="${not empty reviews}">
		<c:forEach var="feedback" items="${reviews}">
			<div data-firstname="${feedback.customerFirstName}" data-lastname="${feedback.customerLastName}" data-agentid="${feedback.agentId}"
				data-agentname="${feedback.agentName}" data-review="${feedback.review}" data-score="${feedback.score}" class="ppl-review-item">
				
				<div class="ppl-header-wrapper clearfix">
					<div class="float-left ppl-header-left">
						<div class="ppl-head-1">${feedback.customerFirstName} ${feedback.customerLastName}</div>
						<div class="ppl-head-2">${feedback.modifiedOn}</div>
					</div>
					<div class="float-right ppl-header-right">
						<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-rating="${feedback.score}">
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-full-star"></div>
							<div class="rating-star icn-no-star"></div>
						</div>
					</div>
				</div>
				<div class="ppl-content">${feedback.review}</div>
				<div class="ppl-share-wrapper clearfix">
					<div class="float-left blue-text ppl-share-shr-txt"><spring:message code="label.share.key" /></div>
					<div class="float-left icn-share icn-plus-open" style="display: block;"></div>
					<div class="float-left clearfix ppl-share-social hide" style="display: none;">
						<div class="float-left ppl-share-icns icn-fb"></div>
						<div class="float-left ppl-share-icns icn-twit"></div>
						<div class="float-left ppl-share-icns icn-lin"></div>
						<div class="float-left ppl-share-icns icn-yelp"></div>
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