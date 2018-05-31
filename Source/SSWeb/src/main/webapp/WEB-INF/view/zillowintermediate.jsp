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
									<input type="hidden" id="screenNameTempHidden"></input>
									<input type="hidden" id="zillowNonLenderURI" value="${zillowNonLenderURI}" ></input>
									<input type="hidden" id="zillowLenderPath" value="${zillowLenderURI}" ></input>
									<div id="main-container" class="non-zillow-help-container">
			 							<div class="zillow-input-container clearfix">
											<div class="zillow-input-cont">
												<c:choose>
													  <c:when test="${profile.vertical == 'Mortgage'}">
													  	<div class="welcome-popup-hdr-wrapper clearfix">
																<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.lender.header.key"/></div>
																<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
														</div>
														<div class="welcome-popup-body-wrapper popup-body clearfix">
														  	<div class="popup-padding-bottom">
														  		<label class="zillow-input-label"><spring:message code="label.zillowconnect.key"/></label>	
														  	</div>
														    <div class="popup-padding-bottom">
															    <div class="float-left">
																	    <span><spring:message code="label.zillowconnect.nmls.header.key"/></span>
																		<input class="zillow-input" name="nmlsId" type="text" maxlength="8" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.nmls.key"/>' >
																</div>
																<div class="float-left help-link-container">
																	<a href="#" class="help-link main-container" title="Help" onclick="openHelpPopup('main-container')"></a>
																</div>
															</div>
														</div>
														<div>
															<div class="float-left ol-btn-wrapper overlay-continue-zillow ol-btn-wrapper-left-zillow" style="width: 20% !important;">
																<a href="#" id="overlay-continue-zillow" class="zillow-link" >By Screen Name</a>
															</div>
															<div class="float-right ol-btn-wrapper overlay-next-zillow ol-btn-wrapper-right-zillow" style="width: 18% !important;">
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
																		<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.estate.header.key" /></div>
																		<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
																	</div>
																  	<div class="welcome-popup-body-wrapper popup-body clearfix">
																  		<div class="popup-padding-bottom">
																	  		<div class="float-left">
																			  	<span><spring:message code="label.zillowconnect.screen.name.key"/></span>
																				<input class="zillow-input zillowProfileName" name="zillowProfileName" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' >																	
																			</div>
																			<div class="float-left help-link-container">
																				<a href="#" class="help-link main-container" title="Help" onclick="openHelpPopup('main-container')"></a>
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
																		<div class="popup-padding-bottom">
																			<spring:message code="label.zillowconnect.by.screen.name.note.key"/>
																		</div>
																	</div>
																</div>
																<div>
																	<div class="float-left ol-btn-wrapper overlay-disconnect-zillow-byscreen-name ol-btn-wrapper-left-zillow disconnect-zillow" style="width: 20% !important;">
																		<a href="#" id="overlay-disconnect-zillow-byscreen-name" class="zillow-link" >Disconnect Zillow</a>
																	</div>
																	
																	<div class="float-right ol-btn-wrapper overlay-save-zillow ol-btn-wrapper-right-zillow" style="width: 18% !important;">
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
																		<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.estate.header.key" /></div>
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
																	<div class="float-right ol-btn-wrapper overlay-continue-zillow ol-btn-wrapper-right-zillow" style="width: 20% !important;">
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
											<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.lender.header.key"/></div>
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
											<div class="float-left ol-btn-wrapper overlay-change-zillow ol-btn-wrapper-left-zillow" style="width: 20% !important;">
												<a href="#" id="overlay-change-zillow" class="zillow-link" >Change NMLS#</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-save-zillow-byscreen ol-btn-wrapper-right-zillow" style="width: 18% !important;">
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
												<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.lender.header.key"/></div>
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
														<input class="zillow-input zillowProfileName" name="zillowProfileNameNoScreenForNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' >																																				
													</div>
													<div class="float-left help-link-container">
														<a href="#" class="help-link" title="Help" onclick="openHelpPopup('no-screen-name-container')"></a>
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
												<div class="popup-padding-bottom">
													<spring:message code="label.zillowconnect.by.screen.name.note.key"/>
												</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-disconnect-noscreen ol-btn-wrapper-left-zillow disconnect-zillow" style="width: 20% !important;">												
												<a href="#" id="overlay-disconnect-noscreen" class="zillow-link" >Disconnect Zillow</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-save-noscreen ol-btn-wrapper-right-zillow" style="width: 18% !important;">
												<div id="overlay-next-noscreen" class="ol-btn cursor-pointer">
													Next
												</div>
											</div>
											<div class="float-right ol-btn-wrapper overlay-cancel-noscreen" style="width: 8% !important;">
												<a href="#" id="overlay-cancel-noscreen" class="zillow-link all-cancel" >Cancel</a>
											</div>
										</div>
									</div>
									<div id="no-screen-name-confirm-container" class="non-zillow-help-container clearfix" style="display:none;">
										<div>
											<div class="welcome-popup-hdr-wrapper clearfix">
												<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.lender.header.key"/></div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.screen.name.found.key"/></span>
												</div>
												<div class="zillow-exm-profile popup-padding-bottom">
													<a target="_blank" id="zillow-profile-lender-new-link" class="zillow-profile-link" href='<spring:message code="label.zillowconnect.lender.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }'>
												    	<spring:message code="label.zillowconnect.lender.link.key" />${ profile.socialMediaTokens.zillowToken.zillowScreenName }
												    </a>
												</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-disconnect-noscreen ol-btn-wrapper-left-zillow" style="width: 20% !important;">												
												<a href="#" id="overlay-disconnect-noscreen" class="zillow-link" >Disconnect Zillow</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-save-noscreen ol-btn-wrapper-right-zillow" style="width: 18% !important;">
												<div id="overlay-save-noscreen" class="ol-btn cursor-pointer">
													Yes, that's me
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
												<div class="float-left wc-hdr-txt"><spring:message code="label.zillowconnect.lender.header.key"/></div>
												<div class="float-right popup-close-icn zillow-popup-close-icn  wc-final-skip-close" style="cursor:pointer;"></div>
											</div>
											<div class="welcome-popup-body-wrapper popup-body clearfix">
												<div class="popup-padding-bottom">
													<span><spring:message code="label.zillowconnect.by.screen.name.only.key"/></span>
												</div>
												
												<div class="popup-padding-bottom clearfix">
													<div class="float-left">
														<span><spring:message code="label.zillowconnect.screen.name.key"/></span>											
														<input class="zillow-input zillowProfileName" name="zillowProfileNameForNoNMLS" type="text" autofocus="autofocus" placeholder='<spring:message code="label.zillowconnect.profileName.key"/>' >												
													</div>
													<div class="float-left help-link-container">
														<a href="#" class="help-link" title="Help" onclick="openHelpPopup('by-screen-name-container')"></a>
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
												<div class="popup-padding-bottom">
													<spring:message code="label.zillowconnect.by.screen.name.note.key"/>
												</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-disconnect-zillow-byscreen-name ol-btn-wrapper-left-zillow disconnect-zillow" style="width: 20% !important;">
												<a href="#" id="overlay-disconnect-zillow-byscreen-name" class="zillow-link" >Disconnect Zillow</a>
											</div>
											<div class="float-right ol-btn-wrapper overlay-save-zillow-byscreen-name ol-btn-wrapper-right-zillow" style="width: 18% !important;">
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
											<div class="float-left ol-btn-wrapper overlay-cancel-disconnect-zillow ol-btn-wrapper-left-zillow" style="width: 20% !important;">										
												<a href="#" id="overlay-cancel-disconnect-zillow" class="zillow-link all-cancel" >Cancel</a>
											</div>
											<div class="float-right ol-btn-wrapper overlay-deletereview-disconnect-zillow ol-btn-wrapper-right-zillow" style="width: 20% !important;">
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
												<div class="popup-padding-bottom">SocialSurvey can connect to your Zillow profile to automatically fetch up to the last 50 approved reviews in addition to providing a link to your Zillow profile on your SocialSurvey public page. Your Zillow profile page may also be used to ask customers who give you a GREAT review to share that review manually on you Zillow profile.</div>
												<div class="popup-padding-bottom">Zillow does not allow third parties, such as SocialSurvey, to post reviews automatically to Zillow profiles. Zillow reviews are not made available to be shared on SocialSurvey until they have been manually audited by Zillow staff which typically takes 10 business days.</div>
											</div>
										</div>
										<div>
											<div class="float-left ol-btn-wrapper overlay-contact-support ol-btn-wrapper-left-zillow" style="width: 20% !important;">
												<a href="#" id="overlay-contact-support" class="zillow-link" >Contact Support</a>
											</div>
											
											<div class="float-right ol-btn-wrapper overlay-contact-support-cancel ol-btn-wrapper-right-zillow" style="width: 20% !important;">
												<div id="overlay-contact-support-cancel" class="ol-btn cursor-pointer">
													Close
												</div>
											</div>
											<!-- <div class="float-right ol-btn-wrapper overlay-contact-support-cancel" style="width: 8% !important;">
												<a href="#" id="overlay-contact-support-cancel" class="zillow-link" >Cancel</a>
											</div> -->
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
    
    //SS-1224 - zillowScreenName update + SS-1125 - UI validation for empty input
    $('input[name="nmlsId"]').val($('#nmlsIdHidden').val());
    
    $('input[name="zillowProfileNameNoScreenForNMLS"]').val($('#screenNameHidden').val());
    $('input[name="zillowProfileNameForNoNMLS"]').val($('#screenNameHidden').val());
    $('input[name="zillowProfileName"]').val($('#screenNameHidden').val());   
    
    $('input[name="zillowProfileNameNoScreenForNMLS"]').on('input', function (e) {
		$('input[name="zillowProfileNameForNoNMLS"]').val($('input[name="zillowProfileNameNoScreenForNMLS"]').val());
		$('input[name="zillowProfileName"]').val($('input[name="zillowProfileNameNoScreenForNMLS"]').val());
    });
    
    $('input[name="zillowProfileNameForNoNMLS"]').on('input',function (e) {
		$('input[name="zillowProfileNameNoScreenForNMLS"]').val($('input[name="zillowProfileNameForNoNMLS"]').val());
		$('input[name="zillowProfileName"]').val($('input[name="zillowProfileNameForNoNMLS"]').val());
    });
    
    $('input[name="zillowProfileName"]').on('input',function (e) {
		$('input[name="zillowProfileNameNoScreenForNMLS"]').val($('input[name="zillowProfileName"]').val());
		$('input[name="zillowProfileNameForNoNMLS"]').val($('input[name="zillowProfileName"]').val());
    });
    
   
    $('.zillow-popup-close-icn').on('click', function(event) {
        $("#zillow-popup").hide();
        overlayRevert();
    });
    
    $('input[name="nmlsId"]').keydown(function (e) {
    	// Allow: backspace, delete, tab, escape, enter, ctrl+A and .
        if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) !== -1 ||
             // Allow: Ctrl+A
            (e.keyCode == 65 && e.ctrlKey === true) || 
             // Allow: home, end, left, right
            (e.keyCode >= 35 && e.keyCode <= 39)) {
                 // let it happen, don't do anything
                 return;
        }

        var charValue = String.fromCharCode(e.keyCode)
            , valid = /^[0-9]+$/.test(charValue);

        if (!valid) {
            e.preventDefault();
        }
        
        if($('input[name="nmlsId"]').val().trim().length > 0) {
        	showHideDisconnectZillowLink(true);
        } else {
        	showHideDisconnectZillowLink(false);
        }
    });
    
    $('input[name="zillowProfileNameForNoNMLS"]').on('input propertychange paste', function() {
    	if($('input[name="zillowProfileNameForNoNMLS"]').val().trim().length > 0) {
        	showHideDisconnectZillowLink(true);
        } else {
        	showHideDisconnectZillowLink(false);
        }
    });
    
    $('input[name="zillowProfileNameNoScreenForNMLS"]').on('input propertychange paste', function() {
    	if($('input[name="zillowProfileNameNoScreenForNMLS"]').val().trim().length > 0) {
        	showHideDisconnectZillowLink(true);
        } else {
        	showHideDisconnectZillowLink(false);
        }
    });
    
    $('input[name="zillowProfileName"]').on('input propertychange paste', function() {
    	if($('input[name="zillowProfileName"]').val().trim().length > 0) {
        	showHideDisconnectZillowLink(true);
        } else {
        	showHideDisconnectZillowLink(false);
        }
    });
    
    
    if($('input[name="zillowProfileNameForNoNMLS"]').val().trim().length > 0) {
    	showHideDisconnectZillowLink(true);
    } else {
    	showHideDisconnectZillowLink(false);
    }
    
    if($('input[name="zillowProfileNameNoScreenForNMLS"]').val().trim().length > 0) {
    	showHideDisconnectZillowLink(true);
    } else {
    	showHideDisconnectZillowLink(false);
    }
    
    
    
});

function showHideDisconnectZillowLink(ifShow) {
	if(ifShow)
		$('.disconnect-zillow').show();
	else
		$('.disconnect-zillow').hide();
}
</script>
