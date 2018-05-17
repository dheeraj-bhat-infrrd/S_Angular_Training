<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<input type="hidden" id="social_media_details" data-social-media-expired="${isSocialMediaExpired}" data-social-media-refresh="${isTokenRefreshRequired}" data-expired-social-media-list=>
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script src="${initParam.resourcesPath}/resources/js/intlTelInput.js"></script>
<script type="text/javascript" async src="//platform.twitter.com/widgets.js" async="async"></script>
<script src="${initParam.resourcesPath}/resources/js/jquery.cookie.js"></script>
<script type="text/javascript">
var hiddenSection = "false";
var vendastaAccess = "false";
var expiredSocialMediaList = JSON.parse('${expiredSocialMediaList}');
var isTokenRefreshRequired = '${isTokenRefreshRequired}';
var isSocialMediaExpired = '${isSocialMediaExpired}';
var enableTokenRefresh = '${enableTokenRefresh}';

$(document).ready(function() {
	callAjaxGetWithPayloadData("/ishiddensection.do", function(data) {
		hiddenSection = data;
		// Show popup if any active session found
		var activeSessionFound = "${activeSessionFound}";
		if (activeSessionFound == "true") {
			showActiveUserLogoutOverlay();
		} else {
			landingFlow();
		}
	});
		
	callAjaxGetWithPayloadData("/isvendastaaccessibleforthesession.do", function(data) {
		vendastaAccess = data;
		showOrHideListingsManager( vendastaAccess );
		showOrHideVendastaProductSettings( vendastaAccess );
	});
	
	$('#social_media_details').data('expired-social-media-list',expiredSocialMediaList);
	$('#social_media_details').data('social-media-expired',isSocialMediaExpired);
	$('#social_media_details').data('social-media-refresh',isTokenRefreshRequired);
});

function landingFlow() {
	var popupStatus = "${popupStatus}";
	var showLinkedInPopup = "${showLinkedInPopup}";
	var showSendSurveyPopup = "${showSendSurveyPopup}";
	var disableCookie="false";
	var cookieValue= $.cookie("doNotShowPopup");
	
	if(!navigator.cookieEnabled){
		var disableCookie="true";
	}
	
	if (showLinkedInPopup == "true" && popupStatus == "Y" && hiddenSection== "false") {
		linkedInDataImport();
	}
	else if (cookieValue !="true" && showSendSurveyPopup == "true" && popupStatus == "Y") {
		sendSurveyInvitation('');
	}
	
	// Skip / Next buttons 
	$('body').on('click', '.wc-skip-btn, .wc-sub-btn', function() {
		if ($(this).closest('.welcome-popup-wrapper').attr('data-page') == 'one') {
			callAjaxGET("./showlinkedindatacompare.do", function(data) {
				$('#welocome-step2').html(data);
				 loadSocialMediaUrlInPopup();
			}, false);
		}
		
		if ($(this).closest('.welcome-popup-wrapper').attr('data-page') == 'two') {
			callAjaxGET("./finalizeprofileimage.do", function(data) {
			}, false);
			
			$('#wc-address-submit').trigger('click');
		}
		
		var parent = $(this).closest('.welcome-popup-wrapper');
		parent.hide();
		parent.next('.welcome-popup-wrapper').show();
	});
	
	$('body').on('click', '.wc-final-skip-close', function(){ 
		var end=false;
		$('#overlay-send-survey').find('#wc-review-table-inner').children().each(function() {
			if (!$(this).hasClass('wc-review-hdr')) {
				$(this).children().each(function(){
					if (!$(this).hasClass('last')){
						var input=$(this).children(":input").val();
						if(input!=""){
							end=true;
							$('#overlay-header-survey').html("Warning");
							$('#overlay-text-survey').html("Closing this window without submitting will delete any data rows entered.Are you sure you want to close?")
							$('#overlay-continue-survey').html("Ok");
	                        $('#overlay-cancel-survey').html("Cancel");
							$('#overlay-main-survey').show();
							$('#overlay-continue-survey').off();
							$('#overlay-continue-survey').click(function() {
								$('#overlay-main-survey').hide();
								$('#overlay-send-survey').hide();
								enableBodyScroll();
							});
							$('#overlay-cancel-survey').off();
							$('#overlay-cancel-survey').click(function() {
								$('#overlay-main-survey').hide();
							});
						}
					}
					
				});
				
			}

		});
		if(!end){
			$('#overlay-send-survey').hide();
			enableBodyScroll();
		}
		
		
	});
	$('body').on('click', '.wc-final-skip', function(){
		$(this).closest('.overlay-login').hide().html('');
		enableBodyScroll();
	});
	
	$('body').on('click', '.lnk-final-skip.wc-final-skip', function(){
		loadDisplayPicture();
		showDisplayPic();
	});
	
	onpopstate = function(event) {
        if(location.hash.trim()!=''){
            historyCallback= true;
            refreshSupport=true;
        }
        retrieveState();
    };
    
	if(location.hash.trim()!='' ){
        historyCallback= true;
        refreshSupport=true;
        retrieveState();
        return;
    }
	
	showMainContent('./showreportingpage.do');
	if (enableTokenRefresh == "true" || enableTokenRefresh == true) {
		//update social media
		if (isSocialMediaExpired == "true" || isSocialMediaExpired == true) {
			if ($("#rep-fix-social-media").length > 0) {
				$("#rep-fix-social-media").removeClass("hide");
			}
		}
		if (isTokenRefreshRequired == "true" || isTokenRefreshRequired == true) {
			var columnName = "${entityType}";
			var columnValue = "${entityId}";
			for ( var expiredSocialMedia in expiredSocialMediaList) {
				openAuthPageFixSocialMedia( expiredSocialMediaList[expiredSocialMedia], columnName, columnValue, "false");
				}
			}
		}
	}
</script>
<jsp:include page="footer.jsp" />