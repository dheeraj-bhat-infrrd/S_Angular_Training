<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set var="start" value='${startIndex}'></c:set>
<c:if test="${not empty reviews}">
	<c:forEach var="reviewItem" varStatus="loop" items="${reviews}">
		<c:choose>
			<c:when test="${ not empty profileUrl}">
				<c:set value="${profileUrl}" var="completeProfileUrl"></c:set>
			</c:when>
			<c:otherwise>
				<c:set value="${reviewItem.completeProfileUrl}" var="completeProfileUrl"></c:set>
			</c:otherwise>
		</c:choose>
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
        <c:set value="${fn:escapeXml(reviewItem.review)}" var="review"></c:set>
		<div data-agentid="${reviewItem.agentId}" survey-mongo-id="${reviewItem._id}"
			data-review="${fn:escapeXml(reviewItem.review)}" data-score="${reviewItem.score}" 
			 class="ppl-review-item dsh-review-cont hide">

			<div class="ppl-header-wrapper clearfix">
			<div class="float-left ppl-header-right">
					<div class="st-rating-wrapper maring-0 clearfix review-ratings" data-modified="false" 
						data-rating="${reviewItem.score}" data-source="${reviewItem.source }">
					</div>
				</div>
				<c:choose>
					<c:when test="${reviewItem.source == 'verifiedPartner'}">
						<div class='verified-partner-badge  verify-partner-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='encompass'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='DOTLOOP'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='API'}">
						<div class='verified-badge  verify-image float-right'
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='FTP'}">
						<div class='verified-badge  verify-image float-right' 
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='LONEWOLF'}">
						<div class='verified-badge  verify-image float-right' 
							title='Click here to know more'></div>
					</c:when>
					<c:when test="${reviewItem.source =='Zillow'}">
						<div class='zillow-badge  verify-image-zillow float-right'></div>
					</c:when>
					<c:when test="${reviewItem.source =='facebook'}">
						<div class='fb-verified-image verify-image-fb float-right'></div>
					</c:when>
					<c:when test="${reviewItem.source =='google'}">
						<div class='google-verified-image verify-image-google float-right'></div>
					</c:when>
					<c:otherwise>
						<div class='unverified-badge  verify-image-ss float-right'></div>
					</c:otherwise>
				</c:choose>
				<div class="ppl-header-left review-sm-screen " >
				<c:choose>
						<c:when test="${not empty reviewItem.surveyUpdatedDate and reviewItem.surveyUpdatedDate != reviewItem.surveyCompletedDate }">
						 	<div class="ppl-head-2 review-detail-profile float-left"> Survey updated on </div> 
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${reviewItem.surveyUpdatedDate}" />"> 
							</div>
						</c:when>
						<c:when test="${ not empty reviewItem.surveyCompletedDate}">
						    <div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div>
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${reviewItem.surveyCompletedDate}" />"> 
							</div>
						</c:when>
						<c:otherwise>
							<div class="ppl-head-2 review-detail-profile float-left"> Survey completed on </div> 				
							<div class="ppl-head-2 review-detail-profile float-left" style="margin-left: 5px;"
								data-modified="false"
								data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-d-YYYY"
						value="${reviewItem.modifiedOn}" />">
							</div>
						</c:otherwise>
					</c:choose>
					<div class="ppl-head-1 review-detail-profile" style="clear:both">
					<span class="float-left">  Reviewed by </span>
						<span class="float-left" style="margin-left:5px;font-weight:600 !important;">${customerDisplayName}</span>
						<c:if test="${profilemasterid !=4}">
							<c:if test="${not empty reviewItem.agentName}">
							 <span class="float-left" style="margin-left: 5px;">for
							 <c:if test="${hiddenSection}">
								 <c:set var="agentName" value="${reviewItem.agentName}" />
								 <c:set var="agentNameTokens" value="${fn:split(agentName, ' ')}" />
								 <c:set var="agentName" value="${agentNameTokens[0]}" />
								 <c:if test="${not empty agentNameTokens[1]}">
								 	<c:set var="agentName" value="${agentName } ${fn:substring(agentNameTokens[1], 0, 1)}" />
								 </c:if>
								${agentName}
							 </c:if>
							 <c:if test="${!hiddenSection}">
							   <a class="cursor-pointer" style="color: #236CAF; font-weight: 600 !important;" href="${reviewItem.completeProfileUrl}" target="_blank">
								${reviewItem.agentName}</a>
								</c:if></span>
							</c:if>
						</c:if>
						<c:choose>
							<c:when test="${ not empty reviewItem.summary }">
								<div class="ppl-content" style="clear:both;padding-top:0px !important;">${reviewItem.summary}</div>
							</c:when>
							<c:otherwise>
							<c:choose>
							<c:when
								test="${not (empty reviewItem.surveyGeoLocation and empty reviewItem.surveyType)}">
								<div class="ppl-content" style="clear:both;padding-top:0px !important;">${reviewItem.surveyGeoLocation}
									<span>${reviewItem.surveyType}</span>
								</div>
							</c:when>
							
							<c:otherwise>
							    <c:if test="${reviewItem.source ne 'customer'}">
                                    <div style="clear:both"><spring:message code="label.completedTransaction.key"/>
                                           <c:choose>
                                        <c:when test= "${not empty reviewItem.surveyTransactionDate}">
                                            <span class="completedOn" data-modified="false" data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-YYYY"
                            value="${ reviewItem.surveyTransactionDate}" />"></span>
														<c:choose>
															<c:when test="${not empty reviewItem.city && not empty reviewItem.state}">
																<span> in ${reviewItem.city}, ${reviewItem.state}.</span>
															</c:when>
														</c:choose>
													</c:when>
                                        <c:otherwise>
                                            <span class="completedOn" data-modified="false" data-modifiedon="<fmt:formatDate type="date" pattern="MMMM-YYYY"
                            value="${reviewItem.modifiedOn}" />"></span>
														<c:choose>
															<c:when test="${not empty reviewItem.city && not empty reviewItem.state}">
																<span> in ${reviewItem.city}, ${reviewItem.state}.</span>
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
			
			<c:set value="${reviewItem.reviewReply}" var="reviewReply"></c:set>
			<c:choose>
				<c:when test="${fn:length(review)>250}">
					<div class="ppl-content review-height">
						<span class="review-complete-txt">${review}</span>

						<c:if test="${reviewItem.source=='Zillow' }">
                          <br><span><a class="view-zillow-link hide" href="${reviewItem.sourceId}"  target="_blank">View on zillow</a></span>
						</c:if>
						<c:if test="${reviewItem.source =='facebook'}">
							<br><span><a class="view-fb-link hide" href="${reviewItem.facebookProfileUrl}"  target="_blank">View on Facebook</a></span>
						</c:if>
						<c:if test="${reviewItem.source =='google'}">
							<br><span><a class="view-goo-link hide" href="${reviewItem.googleBusinessProfileUrl}"  target="_blank">View on Google</a></span>
						</c:if>
						<span class="review-less-text">${fn:substring(review, 0, 250)}</span>
							<span class="review-more-button">read full review</span>
						  
						<!-- Reviews Reply Start -->
						<!-- Remove the following if condition, not the inside div, to enable multiple replies -->	
						<c:if test="${reviewItem.source !='Zillow' && reviewItem.source !='facebook' && reviewItem.source !='google'}">					
						<c:if test="${ empty reviewReply}">
							<c:if test="${ minReplyScore <= reviewItem.score && allowReply}">
								    <div><span class="show-reply-input"><b>Reply</b></span></div>
								        <div class="review-reply-main hide">
									    <input type="text" class="review-reply-input" placeholder="Write a reply"/>
									    <button class="review-reply-btn cursor-pointer">Reply</button>
								    </div>
							 </c:if>

							<div class="review-reply-section hide">
								<!-- <h5><span><b>Replies</b></span></h5> -->							
								<div class="review-reply-container"></div>
							</div>
						</c:if>

						<c:if test="${not empty reviewReply}">
							<!-- Remove the following div with children to enable multiple replies -->
							<c:if test="${ minReplyScore <= reviewItem.score && allowReply}">
								    <div><span class="show-reply-input hide"><b>Reply</b></span></div>
								        <div class="review-reply-main hide">
									    <input type="text" class="review-reply-input" placeholder="Write a reply"/>
									    <button class="review-reply-btn cursor-pointer">Reply</button>
								    </div>
							 </c:if>

							<div class="review-reply-section">
								<!-- <h5><span><b>Replies</b></span></h5> -->							
								<div class="review-reply-container">
									
										<c:forEach var="reply" varStatus="loop" items="${reviewReply}">   
											<div class="review-reply-box" data-reply-id="${reply.replyId}">
												<div class="review-reply-box-container">
													<span class="review-reply-owner"><b>${reply.replyByName}</b></span>
													<span class="review-reply-text">${reply.replyText}</span>
													<div class="review-reply-edit-box hide">
														<input type="text" class="review-reply-edit-text" value="" />
														<button class="save-reply-btn manage-edit-box-btn">Save</button>
														<button class="cancel-reply-btn manage-edit-box-btn">Cancel</button>
													</div>
												</div>

												<c:if test="${ minReplyScore <= reviewItem.score && allowReply }">
													<div class="manage-review-reply-box">
													    <c:if test ="${reply.replyById == currentSessionUserId}">
														    <span class="edit-reply manage-review-reply cursor-pointer">Edit</span>
														</c:if>    
														<c:if test ="${(reply.replyById == currentSessionUserId) || (profilemasterid <= reply.profileMasterId)}">
														    <span class="delete-reply manage-review-reply cursor-pointer">Delete</span>
														</c:if>
													</div>
												</c:if>

											</div>                 
										</c:forEach>							
								
								</div>
							</div>
						</c:if>
						</c:if>
						<!-- Reviews Reply End -->

					</div>
				</c:when>
				<c:otherwise>
					<div class="ppl-content review-height">
					    <span>${review}</span>
					    
						<!-- Reviews Reply Start -->
						<!-- Remove the following if condition, not the inside div, to enable multiple replies -->
						<c:if test="${reviewItem.source !='Zillow' && reviewItem.source !='facebook' && reviewItem.source !='google'}">						
						<c:if test="${ empty reviewReply}">
							<c:if test="${ minReplyScore <= reviewItem.score && allowReply}">
								    <div><span class="show-reply-input"><b>Reply</b></span></div>
								        <div class="review-reply-main hide">
									    <input type="text" class="review-reply-input" placeholder="Write a reply"/>
									    <button class="review-reply-btn cursor-pointer">Reply</button>
								    </div>
							 </c:if>

							<div class="review-reply-section hide">
								<!-- <h5><span><b>Replies</b></span></h5> -->							
								<div class="review-reply-container"></div>
							</div>
						</c:if>

						<c:if test="${not empty reviewReply}">
							<!-- Remove the following div with children to enable multiple replies -->
							<c:if test="${ minReplyScore <= reviewItem.score && allowReply}">
								    <div><span class="show-reply-input hide"><b>Reply</b></span></div>
								        <div class="review-reply-main hide">
									    <input type="text" class="review-reply-input" placeholder="Write a reply"/>
									    <button class="review-reply-btn cursor-pointer">Reply</button>
								    </div>
							 </c:if>

							<div class="review-reply-section">
								<!-- <h5><span><b>Replies</b></span></h5> -->							
								<div class="review-reply-container">
									
										<c:forEach var="reply" varStatus="loop" items="${reviewReply}">   
											<div class="review-reply-box" data-reply-id="${reply.replyId}">
												<div class="review-reply-box-container">
													<span class="review-reply-owner"><b>${reply.replyByName}</b></span>
													<span class="review-reply-text">${reply.replyText}</span>
													<div class="review-reply-edit-box hide">
														<input type="text" class="review-reply-edit-text" value="" />
														<button class="save-reply-btn manage-edit-box-btn">Save</button>
														<button class="cancel-reply-btn manage-edit-box-btn">Cancel</button>
													</div>
												</div>

												<c:if test="${ minReplyScore <= reviewItem.score && allowReply }">
													<div class="manage-review-reply-box">
													    <c:if test ="${reply.replyById == currentSessionUserId}">
														    <span class="edit-reply manage-review-reply cursor-pointer">Edit</span>
														</c:if>    
														<c:if test ="${(reply.replyById == currentSessionUserId) || (profilemasterid <= reply.profileMasterId)}">
														    <span class="delete-reply manage-review-reply cursor-pointer">Delete</span>
														</c:if>
													</div>
												</c:if>

											</div>                 
										</c:forEach>							
								
								</div>
							</div>
						</c:if>
						</c:if>
						<!-- Reviews Reply End -->

                            <c:if test="${reviewItem.source=='Zillow' }">
                              <br><span><a class="view-zillow-link" href="${reviewItem.sourceId}"  target="_blank">View on zillow</a></span>
                            </c:if>
                            <c:if test="${reviewItem.source =='facebook'}">
								<br><span><a class="view-fb-link" href="${reviewItem.facebookProfileUrl}"  target="_blank">View on Facebook</a></span>
							</c:if>
							<c:if test="${reviewItem.source =='google'}">
								<br><span><a class="view-goo-link" href="${reviewItem.googleBusinessProfileUrl}"  target="_blank">View on Google</a></span>
							</c:if>
					</div>
				</c:otherwise>
			</c:choose>
			<div class="ppl-share-wrapper clearfix share-plus-height" >
				<div class="float-left clearfix ppl-share-social ">
				<c:if test="${not empty reviewItem.agentName}">
					<c:set var="includeAgentName" value="for ${reviewItem.agentName} "></c:set>
				</c:if>
					<span id = "fb_${loop.index}"class="float-left ppl-share-icns icn-fb-rev" title="Facebook" 
						data-link="https://www.facebook.com/dialog/share?${reviewItem.faceBookShareUrl}&href=${fn:replace(completeProfileUrl, 'localhost', '127.0.0.1')}/${reviewItem._id}&quote=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey -${fn:escapeXml(reviewItem.review)}&redirect_uri=https://www.facebook.com"></span>
					
                            <c:choose>
                                <c:when test="${fn:length(reviewItem.review) > 180}">
                                    <c:set var="twitterReview" value="${fn:substring(reviewItem.review,0,176)}..."></c:set>
                                </c:when>
                                <c:otherwise>
                                    <c:set var="twitterReview" value="${reviewItem.review}"></c:set>
                                </c:otherwise>
                            </c:choose>
					        <input type="hidden" id="twttxt_${loop.index}" class ="twitterText_loop" value ="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(twitterReview)}"/>

						    <span class="float-left ppl-share-icns icn-twit-rev" id ="twitt_${loop.index}" onclick="twitterProfileFn(${loop.index},this);" data-link="https://twitter.com/intent/tweet?text=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(twitterReview)}&url=${completeProfileUrl}/${reviewItem._id}"></span>
						 <input type="hidden" class="linkedInSummary" value="<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(reviewItem.review)}" >
						 <span
						class="float-left ppl-share-icns icn-lin-rev" title="LinkedIn"
						data-link="https://www.linkedin.com/shareArticle?mini=true&url=${completeProfileUrl}/${reviewItem._id}&title=&summary=<fmt:formatNumber type="number" pattern="${ scoreformat }" value="${reviewItem.score}" maxFractionDigits="1" minFractionDigits="1" />-star response from ${ customerDisplayName } ${includeAgentName}at SocialSurvey - ${fn:escapeXml(reviewItem.review)}&reviewid=${reviewItem._id}&source="></span>
					<span class="float-left ppl-share-icns permalink icn-permalink-rev" title="Permalink" onclick="copyIndividualReviewUrlToClipboard(${start})">
                       	<input id="permalink_url_${start}" type="hidden" value="${completeProfileUrl}/${reviewItem._id}"/>
                    </span>
				</div>
				<div class="float-right dash-flag-retake ">
					<div class="clearfix">
						<div class="icn-flag float-left report-abuse-txt cursor-pointer "
							title="Report"></div>
					</div>
				</div>
			</div>
		</div>
		<c:set var="start" value="${start + 1}"/>
	</c:forEach>
</c:if>
<script type="text/javascript" src="//apis.google.com/js/client:plusone.js" async="async"></script>
<script type="text/javascript" src="//apis.google.com/js/plusone.js" async="async"></script>