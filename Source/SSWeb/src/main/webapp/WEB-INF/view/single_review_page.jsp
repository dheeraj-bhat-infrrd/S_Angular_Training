<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:if test="${not empty reviewAggregate}">
	<c:if test="${reviewAggregate.surveyIdValid}">
		<c:choose>
			<c:when test="${reviewAggregate.profileLevel == 'COMPANY'}">
				<c:set value="sr-comp-default-img" var="defaultimageclass"></c:set>
			</c:when>
			<c:when test="${reviewAggregate.profileLevel == 'REGION'}">
				<c:set value="sr-reg-default-img" var="defaultimageclass"></c:set>
			</c:when>
			<c:when test="${reviewAggregate.profileLevel == 'BRANCH'}">
				<c:set value="sr-off-default-img" var="defaultimageclass"></c:set>
			</c:when>
			<c:when test="${reviewAggregate.profileLevel == 'INDIVIDUAL'}">
				<c:set value="sr-ind-default-img" var="defaultimageclass"></c:set>
			</c:when>
		</c:choose>
		<c:set value="${ reviewAggregate.review }" var = "singleReviewItem"></c:set>
		
		<c:if test="${ not empty singleReviewItem.customerLastName }">
			<c:set value="${ singleReviewItem.customerFirstName } ${ singleReviewItem.customerLastName }" var="customerName"></c:set>
		</c:if>
		
		<c:if test="${ empty singleReviewItem.customerLastName }">
			<c:set value="${ singleReviewItem.customerFirstName }" var="customerName"></c:set>
		</c:if>
		
		
		<c:set value="${fn:split(customerName, ' ')}" var="nameArray"></c:set>
		
		<c:choose>
			<c:when test="${ not empty nameArray[1] }">
				<c:set value="${ nameArray[0] } ${ nameArray[1].substring( 0, 1 ).toUpperCase() }" var="customerDisplayName"></c:set>
			</c:when>
			<c:otherwise>
				<c:set value="${ nameArray[0] }" var="customerDisplayName"></c:set>
			</c:otherwise>
		</c:choose>
		
		<c:set value="${fn:escapeXml(singleReviewItem.review)}" var="singleReview"></c:set>
		<c:set value="${ reviewAggregate.unitSettings }" var="unitProfile"></c:set>

		 <c:set value="${unitProfile.contact_details.name}" var = "unitName"></c:set>
		 <c:if test="${profile.hiddenSection and not empty singleReviewItem.agentName}">
			 <c:set var="unitName" value="${singleReviewItem.agentName}" />
			 <c:set var="unitNameTokens" value="${fn:split(unitName, ' ')}" />
			 <c:set var="unitName" value="${unitNameTokens[0]}" />
			 <c:if test="${not empty unitNameTokens[1]}">
			 	<c:set var="unitName" value="${unitName } ${fn:substring(unitNameTokens[1], 0, 1)}" />
			 </c:if>
		 </c:if>
		 <c:set value="${unitProfile.contact_details.firstName}" var = "agentFirstNameValue"></c:set>
		 <c:set value="${profile.logo}" var="profileLogo"></c:set>

		<div id="sr-review-info" data-customeremail="${singleReviewItem.customerEmail}" data-agentid="${singleReviewItem.agentId}" data-cust-first-name="${singleReviewItem.customerFirstName}" data-cust-last-name="${singleReviewItem.customerLastName}" data-agent-name="${singleReviewItem.agentName}" data-review="${singleReviewItem.review}" data-survey-mongo-id="${singleReviewItem._id}" data-score="${singleReviewItem.score}" data-facebookshareurl="${singleReviewItem.faceBookShareUrl}" data-googleapi="${singleReviewItem.googleApi}"></div>
		<div id="single-review-popup" class="single-review-popup-wrapper">
			<div class="single-review-popup-hdr-wrapper clearfix">
				<c:if test="${not empty unitProfile.profileImageUrlThumbnail && not empty fn:trim(unitProfile.profileImageUrlThumbnail)}">
					<div class="col-lg-2 col-md-2 col-sm-2 col-xs-2" style="padding-right: 0%;">
						<div class="sr-prof-pic-circle">
							<img class="agent-prof-image prof-image-edit" src="${unitProfile.profileImageUrlThumbnail}"></img>
						</div>
					</div>
				</c:if>
				<div class="col-lg-6 col-md-6 col-sm-6 col-xs-9 sr-popup-hdr-details">
					<span style="font-weight:bold !important; line-height:25px" class="capitalize">${unitName}</span>
					<span>${unitProfile.contact_details.title}</span>
				</div>
				<c:if test="${not empty profileLogo && profileLogo != null}">
					<div class="col-lg-4 col-md-4 col-sm-4 col-xs-0 sr-logo-cont">
						<img class="comp-logo-srp prof-image-edit" src="${profileLogo}"></img>
					</div>
				</c:if>
			</div>
			<div class="single-review-popup-body-wrapper clearfix">
				<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 sr-review-details">
					<span class="sr-reviewed-by">Reviewed by ${customerDisplayName}.</span>
					<div class="sr-margin-bottom-50">
						<span>Posted on </span> 
						<c:choose>
							<c:when test="${not empty singleReviewItem.surveyUpdatedDate and singleReviewItem.surveyUpdatedDate != singleReviewItem.surveyCompletedDate }">
							 	<fmt:formatDate type="date" pattern="MMMM d, YYYY"value="${singleReviewItem.surveyUpdatedDate}" /> 
							</c:when>
							<c:when test="${ not empty singleReviewItem.surveyCompletedDate}">
							    <fmt:formatDate type="date" pattern="MMMM d, YYYY" value="${singleReviewItem.surveyCompletedDate}" />
							</c:when>
							<c:otherwise>
								<fmt:formatDate type="date" pattern="MMMM d, YYYY" value="${singleReviewItem.modifiedOn}" />
							</c:otherwise>
						</c:choose>
						<span> for transaction completed in </span>
						<c:choose>
                        	<c:when test= "${not empty singleReviewItem.surveyTransactionDate}">
                            	<fmt:formatDate type="date" pattern="MMMM YYYY" value="${ singleReviewItem.surveyTransactionDate}" />
								<c:choose>
									<c:when test="${not empty singleReviewItem.city && not empty singleReviewItem.state}">
										<span> in ${singleReviewItem.city}, ${singleReviewItem.state}.</span>
									</c:when>
								</c:choose>
							</c:when>
                            <c:otherwise>
                             	<fmt:formatDate type="date" pattern="MMMM YYYY" value="${singleReviewItem.modifiedOn}" />
								<c:choose>
									<c:when test="${not empty singleReviewItem.city && not empty singleReviewItem.state}">
										<span> in ${singleReviewItem.city}, ${singleReviewItem.state}.</span>
									</c:when>
								</c:choose>
							</c:otherwise>
                        </c:choose>
					</div>
					<div>Share this Review</div>
					<div class="ppl-share-wrapper clearfix share-plus-height sr-share-wrapper" >
						<!-- social media share links populated from java script -->
						<div class="float-left clearfix ppl-share-social sr-share-social">
						</div>
					</div>		
				</div>
				<div class="col-lg-8 col-md-8 col-sm-8 col-xs-8 sr-review-content" >
					<div style="margin-bottom: 10px;">
						<div class="float-left ppl-header-right">
							<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-source="${singleReviewItem.source }" data-rating="${singleReviewItem.score}"></div>
						</div>
						<c:choose>
							<c:when test="${singleReviewItem.source == 'verifiedPartner'}">
								<div class='verified-partner-badge  verify-partner-image float-right'
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='encompass'}">
								<div class='verified-badge  verify-image float-right'
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='DOTLOOP'}">
								<div class='verified-badge  verify-image float-right'
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='API'}">
								<div class='verified-badge  verify-image float-right'
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='FTP'}">
								<div class='verified-badge  verify-image float-right' 
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='LONEWOLF'}">
								<div class='verified-badge  verify-image float-right' 
									title='Click here to know more'></div>
							</c:when>
							<c:when test="${singleReviewItem.source =='Zillow'}">
								<div class='zillow-badge  verify-image-zillow float-right'></div>
							</c:when>
							<c:otherwise>
								<div class='unverified-badge  verify-image-ss float-right'></div>
							</c:otherwise>
						</c:choose>
					</div>
					<div class="ppl-content sr-ppl-content" >
						${singleReview}
						<c:if test="${singleReviewItem.source=='Zillow' }">
                              <br><span><a class="view-zillow-link" href="${singleReviewItem.sourceId}"  target="_blank">View on zillow</a></span>
                        </c:if>
					</div>
					
				</div>
			</div>
			<div class="clearfix sr-wc-btn-row">
				<c:if test="${singleReviewItem.source != 'Zillow' }">
					<div class="float-left sr-report-review" id="report-review-wrapper">
						<div class="float-left dash-flag-retake ">
							<div class="clearfix">
								<div class="sr-icn-flag float-left report-abuse-txt sr-prof-report-abuse-txt cursor-pointer " title="Report"></div>
							</div>
						</div>
						<span class="sr-report-review-span">report this review</span>
					</div>
				</c:if>
				<c:if test="${ not empty agentFirstNameValue }">
					<c:choose>
						<c:when test="${singleReviewItem.source != 'Zillow' }">
							<div class="sr-wc-btn-col float-left">
								<div id="single-review-contact-btn" data-contact-link="${unitProfile.completeProfileUrl}" class="sr-wc-sub-send-btn float-left wc-final-submit">
									<span class="capitalize">View Profile</span>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div class="sr-wc-btn-col-zillow float-left">
								<div id="single-review-contact-btn" data-contact-link="${unitProfile.completeProfileUrl}" class="sr-wc-sub-send-btn float-left wc-final-submit">
									<span class="capitalize">View Profile</span>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</c:if>
			</div>
		</div>
	</c:if>
</c:if>