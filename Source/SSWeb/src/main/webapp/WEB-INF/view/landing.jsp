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
<script type="text/javascript">
$(document).ready(function() {
	var popupStatus = "${popupStatus}";
	var showLinkedInPopup = "${showLinkedInPopup}";
	var showSendSurveyPopup = "${showSendSurveyPopup}";
	
	if (showLinkedInPopup == "true" && popupStatus == "Y") {
		linkedInDataImport();
	}
	else if (showSendSurveyPopup == "true" && popupStatus == "Y") {
		sendSurveyInvitation();
	}
	
	showMainContent('./dashboard.do');
});
</script>
<jsp:include page="footer.jsp" />