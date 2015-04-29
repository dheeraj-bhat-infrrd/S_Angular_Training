<jsp:include page="header.jsp" />
<div id="main-content"></div>
<jsp:include page="scripts.jsp"/>
<script src="${pageContext.request.contextPath}/resources/js/landing.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	callAjaxGET("./linkedindataimport.do", function(data) {
		$('#overlay-login').html(data);
		
		if ($("#welocome-step1").length) {
			$('#overlay-login').removeClass("hide");
		}
	}, true);
	
	showMainContent('./dashboard.do');
});
</script>
<jsp:include page="footer.jsp" />