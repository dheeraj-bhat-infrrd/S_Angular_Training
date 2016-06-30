<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script src="${initParam.resourcesPath}/resources/js/intlTelInput.js"></script>
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
		var end=false;
		$('#overlay-send-survey').find('#wc-review-table-inner').children().each(function() {
			if (!$(this).hasClass('wc-review-hdr')) {
				$(this).children().each(function(){
					if (!$(this).hasClass('wc-review-tc5')){
						var input=$(this).children(":input").val();
						console.log(input);
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