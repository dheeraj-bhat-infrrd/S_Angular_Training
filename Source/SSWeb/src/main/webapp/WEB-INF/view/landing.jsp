<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script>
	$(document).ready(function() {
		$('.header-links-item').on('click',function(){
			 window.location.href = $(this).find('a').attr('href');
		});
		loadDisplayPicture();
	});
</script>
<script src="${initParam.resourcesPath}/resources/js/landing.js"></script>
<script src="${initParam.resourcesPath}/resources/js/historySupport.js"></script>
<script src="${initParam.resourcesPath}/resources/js/activesession.js"></script>
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
	
	if (showLinkedInPopup == "true" && popupStatus == "Y") {
		linkedInDataImport();
	}
	else if (showSendSurveyPopup == "true" && popupStatus == "Y") {
		sendSurveyInvitation();
	}
	
	// Skip / Next buttons 
	$('body').on('click', '.wc-skip-btn, .wc-sub-btn', function() {
		if ($(this).closest('.welcome-popup-wrapper').attr('data-page') == 'one') {
			callAjaxGET("./showlinkedindatacompare.do", function(data) {
				$('#welocome-step2').html(data);
			}, false);
		}
		
		if ($(this).closest('.welcome-popup-wrapper').attr('data-page') == 'two') {
			callAjaxGET("./finalizeprofileimage.do", function(data) {
				console.log(data);
			}, false);
			
			$('#wc-address-submit').trigger('click');
		}
		
		var parent = $(this).closest('.welcome-popup-wrapper');
		parent.hide();
		parent.next('.welcome-popup-wrapper').show();
	});

	$('body').on('click', '.wc-final-skip', function(){
		loadDisplayPicture();
		$(this).closest('.overlay-login').hide();
		showDisplayPic();
	});
	
	onpopstate = function(event) {
        console.log('history modified');
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