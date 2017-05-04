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


							
								<form id="zillowForm">
									<input type="hidden" id="profileType" value="${profile.vertical}" ></input>
									<input type="hidden" id="nmlsIdHidden" value="${profile.socialMediaTokens.zillowToken.lenderRef.nmlsId}" ></input>
									<input type="hidden" id="screenNameHidden" value="${profile.socialMediaTokens.zillowToken.zillowScreenName}" ></input>
									<input type="hidden" id="zillowNonLenderURI" value="${zillowNonLenderURI}" ></input>
									<input type="hidden" id="zillowLenderPath" value="${zillowLenderURI}" ></input>
									<div id="main-container" class="non-zillow-help-container">
			 							<div class="zillow-input-container clearfix">
											<div class="zillow-input-cont">
												<c:choose>
													  <c:when test="${profile.vertical == 'Mortgage'}">
													  	<div class="welcome-popup-hdr-wrapper clearfix">
																<div class="float-left wc-hdr-txt">Zillow Lending Profile Lookup</div>
																<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
														</div>
														<div class="welcome-popup-body-wrapper popup-body clearfix">
														  	<div class="popup-padding-bottom">
														  		<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>	
														  	</div>
														    <div class="popup-padding-bottom">
															    <div class="float-left">
																	    <span><spring:message code="label.zillowconnect.nmls.header.key"/></span>
																		<input class="zillow-input" name="nmlsId" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.nmls.key"/>' value = "${ profile.socialMediaTokens.zillowToken.lenderRef.nmlsId }">
																</div>
																<div class="float-left help-link-container">
																	<a href="#" class="help-link" title="Help"></a>
																</div>
															</div>
														</div>
														<div>
															<div class="float-left ol-btn-wrapper overlay-continue-zillow" style="width: 20% !important;">
																<a href="#" id="overlay-continue-zillow" class="zillow-link" >By Screen Name</a>
															</div>
															<div class="float-right ol-btn-wrapper overlay-next-zillow" style="width: 18% !important;">
																<div id="overlay-next-zillow" class="ol-btn cursor-pointer">
																	Next
																</div>
															</div>
															<div class="float-right ol-btn-wrapper overlay-cancel-zillow" style="width: 8% !important;">
																<a href="#" id="overlay-cancel-zillow" class="zillow-link all-cancel" >Cancel</a>
															</div>
														</div>
													  </c:when>
													  
													  <c:otherwise>
													  		<div id="no-screen-name-saved">
															  	<div>
															  		<div class="welcome-popup-hdr-wrapper clearfix">
																		<div class="float-left wc-hdr-txt">Zillow Real Estate Profile Connection</div>
																		<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
																	</div>
																  	<div class="welcome-popup-body-wrapper popup-body clearfix">
																  		<div class="popup-padding-bottom">
																	  		<div class="float-left">
																			  	<span><spring:message code="label.zillowconnect.screen.name.key"/></span>
																				<input class="zillow-input zillowProfileName" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">																	
																			</div>
																			<div class="float-left help-link-container">
																				<a href="#" class="help-link" title="Help"></a>
																			</div>
																		</div>
																		
																		<div class="zillow-exm-url popup-padding-bottom popup-padding-top clearfix">
																			<div>
																				Your screen name is part of your Zillow profile URL. For example:
																			</div>
																			<div>
																				<span><spring:message code="label.zillow.exampleurl.key" /><span class="zillow-url"><spring:message code="label.zillowconnect.profileName.key"/></span>/</span>
																			</div>
																		</div>
																		<div class="popup-padding-bottom">* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
																			Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
																		</div>
																	</div>
																</div>
																<div>
																	<div class="float-left ol-btn-wrapper overlay-disconnect-zillow-byscreen-name" style="width: 20% !important;">
																		<a href="#" id="overlay-disconnect-zillow-byscreen-name" class="zillow-link" >Disconnect Zillow</a>
																	</div>
																	
																	<div class="float-right ol-btn-wrapper overlay-save-zillow" style="width: 18% !important;">
																		<div id="overlay-screen-name-to-saved-zillow" class="ol-btn cursor-pointer">
																			Save
																		</div>
																	</div>
																	<div class="float-right ol-btn-wrapper overlay-save-cancel" style="width: 8% !important;">
																		<a href="#" id="overlay-save-cancel" class="zillow-link all-cancel" >Cancel</a>
																	</div>
																</div>
															</div>
															<div id="screen-name-saved">
																<div class="welcome-popup-hdr-wrapper clearfix">
																		<div class="float-left wc-hdr-txt">Zillow Real Estate Profile Connection</div>
																		<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
																</div>
																<div class="welcome-popup-body-wrapper popup-body clearfix">
																  	<div class="popup-padding-bottom">
																  		<label class="zillow-input-label">Please click the link to verify this is your Zillow profile page:</label>	
																  	</div>
																    <div class="popup-padding-bottom">
																	    <a target="_blank" id="zillow-profile-link" class="zillow-profile-link" href='<spring:message code="label.zillowconnect.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }'>
																	    	<spring:message code="label.zillowconnect.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }
																	    </a>
																	</div>
																</div>
																<div>
																	<div class="float-right ol-btn-wrapper overlay-continue-zillow" style="width: 20% !important;">
																		<div id="overlay-save-zillow-screen-name" class="ol-btn cursor-pointer">
																			Yes, that's me
																		</div>
																	</div>
																	<div class="float-right ol-btn-wrapper overlay-cancel-zillow" style="width: 8% !important;">
																		<a href="#" id="overlay-change-zillow-screen-name" class="zillow-link" >Change</a>
																	</div>
																</div>
															</div>
													  </c:otherwise>
												</c:choose>
											</div>
										</div>
									</div>
									<div id="screen-name-found-container" class="clearfix">
										<div class="welcome-popup-hdr-wrapper clearfix">
											<div class="float-left wc-hdr-txt">Zillow Lending Profile Connection</div>
											<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
										</div>
										<div class="welcome-popup-body-wrapper popup-body clearfix">
											<div class="popup-padding-bottom">
												<span><spring:message code="label.zillowconnect.screen.name.found.key"/></span>
											</div>
											<div class="zillow-exm-profile popup-padding-bottom">
												<a target="_blank" id="zillow-profile-lender-link" class="zillow-profile-link" href='<spring:message code="label.zillowconnect.lender.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }'>
											    	<spring:message code="label.zillowconnect.lender.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }
											    </a>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-change-zillow" style="width: 20% !important;">
												<a href="#" id="overlay-change-zillow" class="zillow-link" >Change NMLS#</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-save-zillow-byscreen" style="width: 18% !important;">
												<div id="overlay-save-zillow-byscreen" class="ol-btn cursor-pointer">
													Yes, that's me
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-disconnect-zillow" style="width: 20% !important;">
												<a href="#" id="overlay-disconnect-zillow" class="zillow-link" >Disconnect Zillow</a>
											</div>
										</div>
									</div>
									<div id="no-screen-name-container" class="non-zillow-help-container clearfix">
										<div>
											<div class="welcome-popup-hdr-wrapper clearfix">
												<div class="float-left wc-hdr-txt">Zillow Lending Profile Connection</div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.no.screen.name.key"/></span>
													<!-- <span class="zillowProfileNameSpan">${ profile.socialMediaTokens.zillowToken.zillowScreenName }</span> -->
												</div>
												
												<div class="popup-padding-bottom">
													<div class="float-left">
														<span><spring:message code="label.zillowconnect.screen.name.key"/></span>
														<input class="zillow-input zillowProfileName" name="zillowProfileNameNoScreenForNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">																																				
													</div>
													<div class="float-left help-link-container">
														<a href="#" class="help-link" title="Help"></a>
													</div>										
												</div>							
												
												<div class="popup-padding-bottom popup-padding-top clearfix">
													<div>
														<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span>
													</div>
													<div>
														<span class="">
															<spring:message code="label.zillowconnect.lender.link.key" /><span class="zillow-url"><spring:message code="label.zillowconnect.profileName.key"/></span>/
														</span>
													</div>
												</div>
												<div class="popup-padding-bottom">* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
													Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
												</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-disconnect-noscreen" style="width: 20% !important;">												
												<a href="#" id="overlay-disconnect-noscreen" class="zillow-link" >Disconnect Zillow</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-save-noscreen" style="width: 18% !important;">
												<div id="overlay-save-noscreen" class="ol-btn cursor-pointer">
													Save
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-cancel-noscreen" style="width: 8% !important;">
												<a href="#" id="overlay-cancel-noscreen" class="zillow-link all-cancel" >Cancel</a>
											</div>
										</div>
									</div>
									<div id="by-screen-name-container" class="non-zillow-help-container clearfix">
										<div>
											<div class="welcome-popup-hdr-wrapper clearfix">
												<div class="float-left wc-hdr-txt">Zillow Lending Profile Connection</div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.by.screen.name.only.key"/></span>
												</div>
												
												<div class="popup-padding-bottom clearfix">
													<div class="float-left">
														<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
														<input class="zillow-input zillowProfileName" name="zillowProfileNameForNoNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' value = "${ profile.socialMediaTokens.zillowToken.zillowScreenName }">												
													</div>
													<div class="float-left help-link-container">
														<a href="#" class="help-link" title="Help"></a>
													</div>
												</div>				
												
												<div class="popup-padding-bottom clearfix">
													<div>
														<span><spring:message code="label.zillow.exampleprofilename.sample.key" /></span>
													</div>
													<div>
														<span>
															<spring:message code="label.zillowconnect.lender.link.key" /><span class="zillow-url"><spring:message code="label.zillowconnect.profileName.key"/></span>/
														</span>
													</div>
												</div>
												<div class="popup-padding-bottom">* You can find your screen name while logged into Zillow by hovering over "My Zillow" and clicking the "Profile" button. 
													Once you're on your profile, click on "Edit your profile" then you will be directed to the page where you can view and edit your "Screen name."
												</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-disconnect-zillow-byscreen-name" style="width: 20% !important;">
												<a href="#" id="overlay-disconnect-zillow-byscreen-name" class="zillow-link" >Disconnect Zillow</a>
											</div>
											<div class="float-right ol-btn-wrapper overlay-save-zillow-byscreen-name" style="width: 18% !important;">
												<div id="overlay-save-zillow-byscreen-name" class="ol-btn cursor-pointer">
													Save
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-cancel-zillow-byscreen-name" style="width: 8% !important;">
												<a href="#" id="overlay-cancel-zillow-byscreen-name" class="zillow-link all-cancel" >Cancel</a>
											</div>
										</div>
									</div>
									<div id="disconnect-zillow-container" class="clearfix">
										<div>
											<div class="welcome-popup-hdr-wrapper clearfix">
												<div class="float-left wc-hdr-txt">Disconnect Zillow Profile</div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">Disconnecting will prevent us from fetching any new reviews from Zillow and will remove the Zillow Profile link from your SocialSurvey public page.</div>					
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-cancel-disconnect-zillow" style="width: 20% !important;">										
												<a href="#" id="overlay-cancel-disconnect-zillow" class="zillow-link all-cancel" >Cancel</a>
											</div>
											<div class="float-right ol-btn-wrapper overlay-deletereview-disconnect-zillow" style="width: 20% !important;">
												<div id="overlay-deletereview-disconnect-zillow" class="ol-btn cursor-pointer">
													Delete Zillow Reviews
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-keepreview-disconnect-zillow" style="width: 20% !important;">
												<div id="overlay-keepreview-disconnect-zillow" class="ol-btn cursor-pointer">
													Keep Zillow Reviews
												</div>
											</div>
										</div>
									</div>
									<div id="zillow-help-container" class="clearfix">
										<div>
											<div class="welcome-popup-hdr-wrapper clearfix">
												<div class="float-left wc-hdr-txt">About Zillow Profile Connection</div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">SocialSurvey can connect to your Zillow profile to automatically fetch up to the last 50 approved reviews in addition to provioding a link to your Zillow profile on your SocailSurvey public page. Your Zillow profile page may also be used to ask customers who give you a GREAT review to share that review manually on you Zillow profile.</div>
												<div class="popup-padding-bottom">Zillow does not allow third parties, such as SocialSurvey, to post reviews automatically to Zillow profiles. Zillow reviews are not made avaiable to be shared on SocialSurvey until they have been manually audited by Zillow staff which typically takes 10 business days.</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-contact-support" style="width: 20% !important;">
												<a href="#" id="overlay-contact-support" class="zillow-link" >Contact Support</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-contact-support-close" style="width: 18% !important;">
												<div id="overlay-contact-support-close" class="ol-btn cursor-pointer">
													Close
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-contact-support-cancel" style="width: 8% !important;">
												<a href="#" id="overlay-contact-support-cancel" class="zillow-link all-cancel" >Cancel</a>
											</div>
										</div>
									</div>
								</form>
								
						
						

<script>
$( document ).ready(function() {     
    /*$('.icn-realtor, .icn-lendingtree, .icn-yelp, .icn-gplus, .icn-google-business, .icn-lin, .icn-twit, .icn-fb, .icn-wide-gplus, .icn-wide-linkedin, .icn-wide-twitter, .icn-wide-fb').on('click', function(event) {
    	$("#overlay-pop-up").removeClass("overlay-disable-wrapper-zillow");
    });
    
    $('.icn-zillow').on('click', function(event) {
        $("#overlay-pop-up").addClass("overlay-disable-wrapper-zillow");
    });*/
    
   
    $('.zillow-popup-close-icn').on('click', function(event) {
        $("#zillow-popup").hide();
        overlayRevert();
    });
    
});
</script>
