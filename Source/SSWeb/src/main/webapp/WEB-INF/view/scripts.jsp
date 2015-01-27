<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/usermanagement.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/changepassword.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/surveyBuilder.js"></script>
<script>
	$('#logout-section').click(function(e) {
		logoutuser();
	});
	
	$('#company-setting').click(function(e) {
		showMainContent('./showcompanysettings.do');
	});
	$('#header-logo').click(function(e){
		showMainContent('./dashboard.do');
	});
	</script>