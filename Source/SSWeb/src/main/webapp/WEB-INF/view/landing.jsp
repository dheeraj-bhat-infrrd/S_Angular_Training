<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script src="${pageContext.request.contextPath}/resources/js/landing.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var showLinkedInPopup = "${showLinkedInPopup}";
	var showSendSurveyPopup = "${showSendSurveyPopup}";
	
	if (showLinkedInPopup == "true") {
		linkedInDataImport();
	}
	else if (showSendSurveyPopup == "true") {
		sendSurveyInvitation();
	}
	
	showMainContent('./dashboard.do');
});
</script>
<jsp:include page="footer.jsp" />