<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script type="text/javascript" async src="//platform.twitter.com/widgets.js" async="async"></script>
<script src="${initParam.resourcesPath}/resources/js/jquery.cookie.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	// Show popup if any active session found
	var activeSessionFound = "${activeSessionFound}";
	if (activeSessionFound == "true") {
		showActiveUserLogoutOverlay();
	} else {
		landingFlow();
	}
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
	
	if (showLinkedInPopup == "true" && popupStatus == "Y") {
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
	
	showMainContent('./dashboard.do');
}
</script>
<jsp:include page="footer.jsp" />