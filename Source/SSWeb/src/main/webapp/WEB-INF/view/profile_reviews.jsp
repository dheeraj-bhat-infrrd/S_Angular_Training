<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty reviews}">
	<c:forEach var="reviewItem" varStatus="loop" items="${reviews}">
		<c:set value = "#.#" var = "scoreformat"></c:set>
		<c:set
			value="${ reviewItem.customerFirstName } ${ reviewItem.customerLastName }"
			var="customerName"></c:set>
		<c:set value="${fn:split(customerName, ' ')}" var="nameArray"></c:set>
		<c:choose>
			<c:when test="${ not empty nameArray[1] }">
				<c:set
					value="${ nameArray[0] } ${ nameArray[1].substring( 0, 1 ).toUpperCase() }."
					var="customerDisplayName"></c:set>
			</c:when>
			<c:otherwise>
				<c:set value="${ nameArray[0] }" var="customerDisplayName"></c:set>
			</c:otherwise>
		</c:choose>
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
		<div data-firstname="${reviewItem.customerFirstName}"
			data-lastname="${reviewItem.customerLastName}"
			data-review="${fn:escapeXml(reviewItem.review)}" data-score="${reviewItem.score}"
			data-agentname="${reviewItem.agentName}" class="ppl-review-item dsh-review-cont hide">

			<div class="ppl-header-wrapper clearfix">
			<div class="float-left ppl-header-right">
					<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-modified="false" 
						data-rating="${reviewItem.score}" data-source="${reviewItem.source }">
					</div>
				</div>
				<%-- <div class="float-left ppl-header-left" style="width:290px;">
					<div class="ppl-head-1 review-detail-profile">
					<span class="float-left"> &#8212; Reviewed by </span>
						<span class="float-left" style="margin-left:5px;font-weight:600 !important;">${reviewItem.customerFirstName} ${reviewItem.customerLastName}</span>
						<c:if test="${profilemasterid !=4}">
					<span class="float-left" style="margin-left:5px;">for<a class="cursor-pointer" style="color:#236CAF;font-weight: 600 !important;" href="${reviewItem.completeProfileUrl}" target="_blank"> ${reviewItem.agentName}</a></span>
					</c:if>
					</div>
					<span class="float-left" style="margin: 0 5px;line-height: 22px;">&#8212;</span>
					<div class="ppl-head-2 float-left" data-modified="false" 
						data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-H-mm-ss"
						value="${reviewItem.modifiedOn}" />"></div>
						
				</div> --%>
				<c:if test="${reviewItem.source =='encompass'}">
				<div class='verified-badge  verify-image float-right' title='Click here to know more'></div>
				</c:if>
				<c:if test="${reviewItem.source =='DOTLOOP'}">
				<div class='verified-badge  verify-image float-right' title='Click here to know more'></div>
				</c:if>
				<c:if test="${reviewItem.source =='Zillow'}">
				<div class='zillow-badge   verify-image float-right' title='Click here to know more'></div>
				</c:if>
				<div class="ppl-header-left review-sm-screen " >
					<div class="ppl-head-1 review-detail-profile">
					<span class="float-left"> &#8212; Reviewed by </span>
						<span class="float-left" style="margin-left:5px;font-weight:600 !important;">${reviewItem.customerFirstName} ${reviewItem.customerLastName}</span>
						<c:if test="${profilemasterid !=4}">
					<span class="float-left" style="margin-left:5px;">for<a class="cursor-pointer" style="color:#236CAF;font-weight: 600 !important;" href="${reviewItem.completeProfileUrl}" target="_blank"> ${reviewItem.agentName}</a></span>
					</c:if>
					</div>
					<span class="float-left" style="margin: 0 5px;line-height: 22px;">&#8212;</span>
					<div class="ppl-head-2 float-left" data-modified="false" 
						data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-H-mm-ss"
						value="${reviewItem.modifiedOn}" />"></div>
						
				</div>
				
			</div>
			<c:if test="${ not empty reviewItem.summary }">
				<div class="ppl-content">${reviewItem.summary}</div>
			</c:if>
			
			<div class="ppl-share-wrapper clearfix share-plus-height" style="visibility:hidden;">
				<%-- <div class="float-left blue-text ppl-share-shr-txt">
					<spring:message code="label.share.key" />
				</div> --%>
				<!-- <div class="float-left icn-share icn-plus-open"></div> -->
				<div class="float-left clearfix ppl-share-social ">
					<span id = "fb_${loop.index}"class="float-left ppl-share-icns icn-fb-rev" title="Facebook" onclick = "getImageandCaptionProfile(${loop.index});"
						data-link="https://www.facebook.com/dialog/feed?${reviewItem.faceBookShareUrl}&link=${fn:replace(reviewItem.completeProfileUrl, 'localhost', '127.0.0.1')}&description=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } for ${reviewItem.agentName} at SocialSurvey -${fn:escapeXml(reviewItem.review)} .&redirect_uri=https://www.facebook.com"></span>
					
					
					    <input type="hidden" id="twttxt_${loop.index}" class ="twitterText_loop" value ="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } for ${reviewItem.agentName} at SocialSurvey - ${fn:escapeXml(reviewItem.review)}"/>
						
						<span class="float-left ppl-share-icns icn-twit-rev" id ="twitt_${loop.index}" onclick="twitterProfileFn(${loop.index},this);" data-link="https://twitter.com/intent/tweet?text=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } for ${reviewItem.agentName} at SocialSurvey - ${fn:escapeXml(reviewItem.review)}&url=${reviewItem.completeProfileUrl}"></span>
						 <span
						class="float-left ppl-share-icns icn-lin-rev" title="LinkedIn"
						data-link="https://www.linkedin.com/shareArticle?mini=true&url=${reviewItem.completeProfileUrl} &title=&summary=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } for ${reviewItem.agentName} at SocialSurvey - ${fn:escapeXml(reviewItem.review)} + &source="></span>
					<span class="float-left" title="Google+">
						<button
							class="g-interactivepost float-left ppl-share-icns icn-gplus-rev"
							data-contenturl="${reviewItem.completeProfileUrl}"
							data-clientid="${reviewItem.googleApi}"
							data-cookiepolicy="single_host_origin"
							data-prefilltext="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } for ${reviewItem.agentName} at SocialSurvey - ${fn:escapeXml(reviewItem.review)}"
							data-calltoactionlabel="USE"
							data-calltoactionurl="${reviewItem.completeProfileUrl}">
							<span class="icon">&nbsp;</span> <span class="label">share</span>
						</button>
					</span>
				</div>
				<!-- <div class="float-left icn-share icn-remove icn-rem-size hide"></div> -->
			</div>
			<div class="ppl-content">${reviewItem.review}</div>
		</div>
	</c:forEach>
</c:if>
<script type="text/javascript" src="//apis.google.com/js/client:plusone.js" async="async"></script>
<script type="text/javascript" src="//apis.google.com/js/plusone.js" async="async"></script>