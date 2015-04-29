<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script src="${pageContext.request.contextPath}/resources/js/landing.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var showLinkedInPopup = "${showLinkedInPopup}";
	if (showLinkedInPopup == "true") {
		callAjaxGET("./linkedindataimport.do", function(data) {
			$('#overlay-linkedin-import').html(data);
			if ($("#welocome-step1").length) {
				$('#overlay-linkedin-import').removeClass("hide");
			}
		}, true);
	}
	else {
		$('#overlay-send-survey').removeClass("hide");
	}
	
	showMainContent('./dashboard.do');
});
</script>
<jsp:include page="footer.jsp" />