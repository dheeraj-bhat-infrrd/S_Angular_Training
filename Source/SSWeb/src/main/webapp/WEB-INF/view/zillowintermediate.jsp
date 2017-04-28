<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty accountSettings}">
	<c:set var = "profile" value = "${ accountSettings }"></c:set>
</c:if>
<c:if test="${empty accountSettings}">
	<c:if test="${ not empty profileSettings }">
		<c:set var = "profile" value = "${ profileSettings }"></c:set>
	</c:if>
</c:if>

<div class=" padding-001 ">
	<div class="container login-container">
		<div class="row login-row">
			
			<div class=" padding-001 margin-top-25 margin-bottom-25 bg-fff margin-0-auto col-xs-12 col-md-12 col-sm-12 col-lg-12">
			<div class="text-center font-24">
					<div style="padding: 0px 20px;" class="clearfix">
						<div style="margin-bottom: 10px; font-size: 13px; text-align: center; padding: 0px 10px;">
							<div>
								<form id="zillowForm">
									<input type="hidden" id="profileType" value="${profile.vertical}" ></input>
									<div id="main-container" class="non-zillow-help-container">
			 							<div class="zillow-input-container clearfix popupUrl-zillow">
											<div class="zillow-input-cont">
												<c:choose>
													  <c:when test="${profile.vertical == 'Mortgage'}">
													  	<div class="popup-header">- Zillow Lending Profile Connection -</div>
													  	<div class="popup-padding-bottom">
													  		<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>	
													  	</div>
													    <div class="popup-padding-bottom">
														    <span>
															    <spring:message code="label.zillowconnect.nmls.header.key" />
																<input class="zillow-input" name="nmlsId" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.nmls.key"/>' value = "${ profile.socialMediaTokens.zillowToken.lenderRef.nmlsId }">
																<a href="#" class="help-link" title="Help">?</a>
															</span>
														</div>
														
														<div>
															<div class="float-left ol-btn-wrapper overlay-cancel-zillow" style="width: 30% !important;">
																<div id="overlay-cancel-zillow" class="ol-btn cursor-pointer">
																	<!-- Populated by javascript -->
																</div>
															</div>
															<div class="float-left ol-btn-wrapper overlay-continue-zillow" style="width: 30% !important;">
																<div id="overlay-continue-zillow" class="ol-btn cursor-pointer">
																	<!-- Populated by javascript -->
																</div>
															</div>
															<div class="float-left ol-btn-wrapper overlay-next-zillow" style="width: 30% !important;">
																<div id="overlay-next-zillow" class="ol-btn cursor-pointer">
																	<!-- Populated by javascript -->
																</div>
															</div>
														</div>
													  </c:when>
													  <c:otherwise>
													  	<div>
														  	<div class="zillow-example-cont popupUrl">
														  		<div class="popup-header">- Zillow Real Estate Profile Connection -</div>
																<div class="zillow-exm-url popup-padding-bottom">
																	<span class="zillow-url"><spring:message code="label.zillow.exampleurl.key" /></span>
																</div>
																<div class="zillow-exm-profile popup-padding-bottom">
																	<span><spring:message code="label.zillow.exampleprofilename.text.key" /></span> 
																	<span class="zillow-exm-profilename"><spring:message code="label.zillow.exampleprofilename.key" /></span>
																</div>
															</div>
															<div class="popup-padding-bottom">
															  	<span><spring:message code="label.zillowconnect.link.key"/></span>
																<input class="zillow-input zillowProfileName" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">
																<span>/</span>
																<a href="#" class="help-link" title="Help">?</a>
															</div>
														</div>
														<div>
															<div class="float-left ol-btn-wrapper overlay-cancel-zillow">
																<div id="overlay-cancel-zillow" class="ol-btn cursor-pointer">
																	<!-- Populated by javascript -->
																</div>
															</div>
															<div class="float-left ol-btn-wrapper overlay-save-zillow">
																<div id="overlay-save-zillow" class="ol-btn cursor-pointer">
																	<!-- Populated by javascript -->
																</div>
															</div>
														</div>
													  </c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
									<div id="screen-name-found-container" class="non-zillow-help-container">
											<div class="zillow-input-container clearfix popupUrl-zillow">
												<div>
													<div class="popup-header">- Zillow Lending Profile Connection -</div>
													<div class="popup-padding-bottom">
														<span><spring:message code="label.zillowconnect.screen.name.found.key"/></span>
													</div>
													<div class="zillow-exm-profile popup-padding-bottom">
														<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
													</div>
												</div>
												<div>
													<div class="float-left ol-btn-wrapper overlay-change-zillow" style="width: 30% !important;">
														<div id="overlay-change-zillow" class="ol-btn cursor-pointer">
															<!-- Populated by javascript -->
														</div>
													</div>
													<div class="float-left ol-btn-wrapper overlay-disconnect-zillow" style="width: 30% !important;">
														<div id="overlay-disconnect-zillow" class="ol-btn cursor-pointer">
															<!-- Populated by javascript -->
														</div>
													</div>
													<div class="float-left ol-btn-wrapper overlay-save-zillow-byscreen" style="width: 30% !important;">
														<div id="overlay-save-zillow-byscreen" class="ol-btn cursor-pointer">
															<!-- Populated by javascript -->
														</div>
													</div>
												</div>
											</div>
									</div>
									<div id="no-screen-name-container" class="non-zillow-help-container">
										<div class="zillow-input-container clearfix popupUrl-zillow">
											<div>
												<div class="popup-header">- Zillow Lending Profile Connection -</div>
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.no.screen.name.key"/></span>
													<!-- <span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span> -->
												</div>
												
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
													<input class="zillow-input zillowProfileName" name="zillowProfileNameNoScreenForNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">	
													<a href="#" class="help-link" title="Help">?</a>											
												</div>							
												
												<div class="popup-padding-bottom">
													<div class="popup-padding-bottom">
														<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span>
													</div>
													<div>
														<span class="zillow-exm-profilename">
															<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
														</span>
													</div>
												</div>
												<div class="popup-padding-bottom">* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
													Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
												</div>
											</div>
											<div>
												<div class="float-left ol-btn-wrapper overlay-disconnect-noscreen" style="width: 30% !important;">
													<div id="overlay-disconnect-noscreen" class="ol-btn cursor-pointer">
														Disconnect Zillow
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-cancel-noscreen" style="width: 30% !important;">
													<div id="overlay-cancel-noscreen" class="ol-btn cursor-pointer">
														Cancel
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-save-noscreen" style="width: 30% !important;">
													<div id="overlay-save-noscreen" class="ol-btn cursor-pointer">
														Save
													</div>
												</div>
											</div>
										</div>
									</div>
									<div id="by-screen-name-container" class="non-zillow-help-container">
										<div class="zillow-input-container clearfix popupUrl-zillow">
											<div>
												<div class="popup-header">- Zillow Lending Profile Connection -</div>
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.by.screen.name.only.key"/></span>
												</div>
												
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
													<input class="zillow-input zillowProfileName" name="zillowProfileNameForNoNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">												
													<a href="#" class="help-link" title="Help">?</a>
												</div>				
												
												<div class="zillow-exm-profile popup-padding-bottom">
													<div class="popup-padding-bottom">
														<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span>
													</div>
													<div>
														<spring:message code="label.zillowconnect.link.key" /><span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span>
													</div>
												</div>
												<div class="popup-padding-bottom">* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
													Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
												</div>
											</div>
											<div>
												<div class="float-left ol-btn-wrapper overlay-disconnect-zillow-byscreen-name" style="width: 30% !important;">
													<div id="overlay-disconnect-zillow-byscreen-name" class="ol-btn cursor-pointer">
														Disconnect Zillow
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-cancel-zillow-byscreen-name" style="width: 30% !important;">
													<div id="overlay-cancel-zillow-byscreen-name" class="ol-btn cursor-pointer">
														Cancel
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-save-zillow-byscreen-name" style="width: 30% !important;">
													<div id="overlay-save-zillow-byscreen-name" class="ol-btn cursor-pointer">
														Save
													</div>
												</div>
											</div>
										</div>
									</div>
									<div id="disconnect-zillow-container" class="non-zillow-help-container">
										<div class="zillow-input-container clearfix popupUrl-zillow">
											<div>
												<div class="popup-header">- Disconnect Zillow Profile -</div>
												<div class="popup-padding-bottom">Disconnecting will prevent us from fetching any new reviews from Zillow and will remove the Zillow Profile link from your SocialSurvey public page.</div>
											</div>
											<div>
												<div class="float-left ol-btn-wrapper overlay-cancel-disconnect-zillow" style="width: 30% !important;">
													<div id="overlay-cancel-disconnect-zillow" class="ol-btn cursor-pointer">
														Cancel
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-keepreview-disconnect-zillow" style="width: 30% !important;">
													<div id="overlay-keepreview-disconnect-zillow" class="ol-btn cursor-pointer">
														Keep Zillow Reviews
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-deletereview-disconnect-zillow" style="width: 30% !important;">
													<div id="overlay-deletereview-disconnect-zillow" class="ol-btn cursor-pointer">
														Delete Zillow Reviews
													</div>
												</div>
											</div>
										</div>
									</div>
									<div id="zillow-help-container">
										<div class="zillow-input-container clearfix popupUrl-zillow">
											<div>
												<div class="popup-header">- About Zillow Profile Connection -</div>
												<div class="popup-padding-bottom">SocialSurvey can connect to your Zillow profile to automatically fetch up to the last 50 approved reviews in addition to provioding a link to your Zillow profile on your SocailSurvey public page. Your Zillow profile page may also be used to ask customers who give you a GREAT review to share that review manually on you Zillow profile.</div>
												<div class="popup-padding-bottom">Zillow does not allow third parties, such as SocialSurvey, to post reviews automatically to Zillow profiles. Zillow reviews are not made avaiable to be shared on SocialSurvey until they have been manually audited by Zillow staff which typically takes 10 business days.</div>
											</div>
											<div>
												<div class="float-left ol-btn-wrapper overlay-contact-support" style="width: 50% !important;">
													<div id="overlay-contact-support" class="ol-btn cursor-pointer">
														Contact Support
													</div>
												</div>
												<div class="float-left ol-btn-wrapper overlay-contact-support-close" style="width: 50% !important;">
													<div id="overlay-contact-support-close" class="ol-btn cursor-pointer">
														Close
													</div>
												</div>
											</div>
										</div>
									</div>
								</form>
								
							</div>
						</div>
					</div>
					<div style="font-size: 11px; text-align: center;"></div>
				</div>
				
			</div>
		</div>
	</div>
</div>

<script>
$( document ).ready(function() {     
    $('.icn-realtor, .icn-lendingtree, .icn-yelp, .icn-gplus, .icn-google-business, .icn-lin, .icn-twit, .icn-fb, .icn-wide-gplus, .icn-wide-linkedin, .icn-wide-twitter, .icn-wide-fb').on('click', function(event) {
    	$("#overlay-pop-up").removeClass("overlay-disable-wrapper-zillow");
    });
    
    $('.icn-zillow').on('click', function(event) {
        $("#overlay-pop-up").addClass("overlay-disable-wrapper-zillow");
    });
    
});
</script>
