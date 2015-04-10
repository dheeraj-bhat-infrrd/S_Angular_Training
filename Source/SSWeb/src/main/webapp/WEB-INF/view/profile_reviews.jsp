<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:choose>
	<c:when test="${not empty reviewItems}">
		<c:forEach var="reviewItem" items="${reviewItems}">
			<div class="ppl-review-item">
				<div class="ppl-header-wrapper clearfix">
					<div class="float-left ppl-header-left">
						<div class="ppl-head-1">${reviewItem.customerFirstName} ${reviewItem.customerLastName}</div>
						<div class="ppl-head-2">
							<fmt:formatDate type="date" pattern="dd MMM, yyyy" value="${reviewItem.modifiedOn}" />
						</div>
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
						<div class="float-left ppl-share-icns icn-fb"></div>
						<div class="float-left ppl-share-icns icn-twit"></div>
						<div class="float-left ppl-share-icns icn-lin"></div>
						<div class="float-left ppl-share-icns icn-yelp"></div>
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