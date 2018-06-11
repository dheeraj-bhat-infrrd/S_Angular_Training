<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="hiddenSection" value="${hiddenSection}"></c:set>
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
<c:set var="start" value='${startIndex}'></c:set>
<c:if test="${not empty reviews}">
	<c:forEach var="feedback" varStatus="loop" items="${reviews}">
		<c:choose>
			<c:when test="${ not empty profileUrl}">
				<c:set value="${profileUrl}" var="completeProfileUrl"></c:set>
			</c:when>
			<c:otherwise>
				<c:set value="${feedback.completeProfileUrl}" var="completeProfileUrl"></c:set>
			</c:otherwise>
		</c:choose>
		<c:set value="#.#" var="scoreformat"></c:set>
		<c:set
			value="${ feedback.customerFirstName } ${ feedback.customerLastName }"
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
		<div data-agentid="${feedback.agentId}" data-review="${fn:escapeXml(feedback.review)}" data-score="${feedback.score}" survey-mongo-id="${feedback._id}" class="ppl-review-item dsh-review-cont hide">
			<div class="ppl-header-wrapper clearfix">
			<div class="float-left ppl-header-right">
					<div class="st-rating-wrapper maring-0 clearfix review-ratings float-right" data-modified="false" data-rating="${feedback.score}" data-source="${feedback.source }">
					</div>
				</div>
				<c:choose>
					<c:when test="${feedback.source =='encompass'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${feedback.source =='DOTLOOP'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${feedback.source =='API'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${feedback.source =='FTP'}">
						<div class='verified-badge  verify-image float-right' 
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${feedback.source =='LONEWOLF'}">
						<div class='verified-badge  verify-image float-right' 
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${feedback.source =='Zillow'}">
						<div class='zillow-badge  verify-image-zillow float-right'></div>
					</c:when>
					<c:otherwise>
						<div class='unverified-badge  verify-image-ss float-right'></div>
					</c:otherwise>
				</c:choose>
				<div class=" ppl-header-left review-sm-screen ">

				<c:set value="${fn:escapeXml(feedback.review)}" var="review"></c:set>

					<c:choose>
					<c:when test="${ not empty feedback.surveyUpdatedDate and feedback.surveyUpdatedDate != feedback.surveyCompletedDate }">
					        <div class="ppl-head-2 review-detail-profile float-left"> Survey updated on </div>
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${feedback.surveyUpdatedDate}" />">
							</div>
						</c:when>
						<c:when test="${ not empty feedback.surveyCompletedDate}">
						    <div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div>
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${feedback.surveyCompletedDate}" />">
							</div>
						</c:when>
						<c:otherwise>
							<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div> 					
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${feedback.modifiedOn}" />">
							</div>
						</c:otherwise>
					</c:choose>
					<div class="ppl-head-1 " style="clear:both; text-align:left;">
					<span class="float-left"> Reviewed by </span>
					<c:choose>
						<c:when
							test="${fn:toLowerCase(feedback.customerLastName) eq 'null'}">
							<span class="float-left"
								style="margin-left: 5px; font-weight: 600 !important;">
								${feedback.customerFirstName} </span>
						</c:when>
						<c:otherwise>
							<span class="float-left"
								style="margin-left: 5px; font-weight: 600 !important;">
								${feedback.customerFirstName} ${feedback.customerLastName}</span>
						</c:otherwise>
					</c:choose>
					<c:if test="${profilemasterid !=4}">
					<c:if test="${not empty feedback.agentName }">
						<span class="float-left " style="margin-left: 5px;">for
						<c:if test="${hiddenSection}">
							 <c:set var="agentName" value="${feedback.agentName}" />
								 <c:set var="agentNameTokens" value="${fn:split(agentName, ' ')}" />
								 <c:set var="agentName" value="${agentNameTokens[0]}" />
								 <c:if test="${not empty agentNameTokens[1]}">
								 	<c:set var="agentName" value="${agentName } ${fn:substring(agentNameTokens[1], 0, 1)}" />
								 </c:if>
								${agentName}
						</c:if>
					  <c:if test="${!hiddenSection}">
						<a style="color: #236CAF; font-weight: 600 !important;"
							href="${feedback.completeProfileUrl}" target="_blank">
								${feedback.agentName}</a>
				     </c:if>
								</span>
					</c:if>
					</c:if>

						<c:choose>
							<c:when test="${ not empty feedback.summary }">
								<div class="ppl-content" style="clear:both;padding-top:0px !important;">${feedback.summary}</div>
							</c:when>
							<c:otherwise>
							<c:choose>
							<c:when
								test="${not (empty feedback.surveyGeoLocation and empty feedback.surveyType)}">
								<div class="ppl-content " style="clear:both;padding-top:0px !important;">${feedback.surveyGeoLocation}
									<span>${feedback.surveyType}</span>
								</div>
							</c:when>
							<c:otherwise>
							    <c:if test="${feedback.source ne 'customer'}">
                                    <div style="clear:both"><spring:message code="label.completedTransaction.key"/>
                                           <c:choose>
                                        <c:when test= "${not empty feedback.surveyTransactionDate}">
                                            <span class="completedOn" data-modified="false" data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-YYYY"
                            value="${feedback.surveyTransactionDate}" />"></span>
														<c:choose>
															<c:when test="${not empty feedback.city && not empty feedback.state}">
																<span> in ${feedback.city}, ${feedback.state}.</span>
															</c:when>
														</c:choose>
													</c:when>
                                        <c:otherwise>
                                            <span class="completedOn" data-modified="false" data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-YYYY"
                            value="${feedback.modifiedOn}" />"></span>
														<c:choose>
															<c:when test="${not empty feedback.city && not empty feedback.state}">
																<span> in ${feedback.city}, ${feedback.state}.</span>
															</c:when>
														</c:choose>
													</c:otherwise>
                                        </c:choose>
                                    </div>
                                </c:if>
							</c:otherwise>
						</c:choose>
							</c:otherwise>
						</c:choose>
				</div>
			</div>
			</div>
			<c:choose>
				<c:when test="${fn:length(review)>250}">
					<div class="ppl-content review-height">
						<span class="review-complete-txt">${review}</span>
						<c:if test="${feedback.source=='Zillow' }">
                          <br><span><a class="view-zillow-link hide" href="${feedback.sourceId}"  target="_blank">View on zillow</a></span>
						</c:if>
						<span class="review-less-text">${fn:substring(review, 0, 250)}</span>
							<span class="review-more-button">read full review</span>
					</div>
				</c:when>
				<c:otherwise>
					<div class="ppl-content review-height">
					    <span>${review}</span>
                    <c:if test="${feedback.source=='Zillow' }">
                      <br><span><a class="view-zillow-link" href="${feedback.sourceId}"  target="_blank">View on zillow</a></span>
                    </c:if>
                    </div>
				</c:otherwise>
			</c:choose>

			<div class="ppl-share-wrapper clearfix share-plus-height" >
				<div class="float-left clearfix ppl-share-social hide" style="display: block;">
					<c:if test="${not empty feedback.agentName}">
						<c:set var="includeAgentName" value="for ${feedback.agentName} "></c:set>
					</c:if>
					<span id ="fb_${loop.index}"class="float-left ppl-share-icns icn-fb-rev" title="Facebook" data-link="https://www.facebook.com/dialog/share?${feedback.faceBookShareUrl}&href=${fn:replace(completeProfileUrl, 'localhost', '127.0.0.1')}/${feedback._id}&quote=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}&redirect_uri=https://www.facebook.com"></span>
					
					<input type="hidden" id="twttxt_${loop.index}" class ="twitterText_loop" value ="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}"/>
					<span class="float-left ppl-share-icns icn-twit-rev" id ="twitt_${loop.index}" onclick="twitterDashboardFn(${loop.index},this);" data-link="https://twitter.com/intent/tweet?text=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}&url=${completeProfileUrl}/${feedback._id}"></span>
					 	<input type="hidden" class="linkedInSummary" value="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}" >
					 	<span
						class="float-left ppl-share-icns icn-lin-rev" title="LinkedIn"
						data-link="https://www.linkedin.com/shareArticle?mini=true&url=${completeProfileUrl}/${feedback._id}&title=&summary=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}&reviewid=${feedback._id}&source="></span>
                       <span class="float-left" title="Google+">
                       <button 
                           class="g-interactivepost float-left ppl-share-icns icn-gplus-rev"
                           data-contenturl="${completeProfileUrl}/${feedback._id}"
                           data-clientid="${feedback.googleApi}"
                           data-cookiepolicy="single_host_origin"
                           data-prefilltext="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${feedback.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(feedback.review)}"
                           data-calltoactionlabel="USE"
                           data-calltoactionurl="${completeProfileUrl}/${feedback._id}">
                          <span class="icon">&nbsp;</span>
                          <span class="label">share</span>
                      </button>
                       </span>
                       <span class="float-left ppl-share-icns permalink icn-permalink-rev" title="Permalink" onclick="copyIndividualReviewUrlToClipboard(${start})">
                       	<input id="permalink_url_${start}" type="hidden" value="${completeProfileUrl}/${feedback._id}"/>
                   		</span>
				</div>
				<c:if test="${feedback.source != 'Zillow'}">
				<div class="float-right dash-flag-retake">
					<div class="clearfix">
						<div class="icn-flag float-left report-abuse-txt cursor-pointer "
							title="Report"></div>
								<div class="restart-survey-mail-txt report-txt retake-icn float-left" title="Retake"></div>
					</div>
				</div>
				</c:if>
				<c:if test="${feedback.source == 'Zillow'}">
				<div class="float-right dash-flag-retake" >
					<div class="clearfix">
						<div class="icn-flag float-left report-abuse-txt cursor-pointer "
							title="Report"></div>
					</div>
				</div>
				</c:if>
				
				
			</div>
			
		</div>
		<c:set var="start" value="${start + 1}"/>
	</c:forEach>
</c:if>
<script type="text/javascript" src="//apis.google.com/js/client:plusone.js" async="async"></script>
<script type="text/javascript" src="//apis.google.com/js/plusone.js" async="async"></script>
