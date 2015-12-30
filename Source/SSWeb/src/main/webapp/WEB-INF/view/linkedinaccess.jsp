<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title><spring:message code="label.linkedin.access.key" /></title>
	<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
	<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
</head>
<body>

<div class="hdr-wrapper">
	<div class="container hdr-container clearfix">
		<div class="float-left hdr-logo"></div>
		<div class="float-right clearfix hdr-btns-wrapper">
			<div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
			<div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
		</div>
	</div>
</div>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center"><spring:message code="label.linkedin.access.key" /></div>
		</div>
	</div>
</div>

<div id="" class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container reg_panel_container">
		<div class="reg_header text-center"><spring:message code="label.linkedin.request.key" /></div>
		<div class="reg_lkin_wrapper text-center">
			<div class="reg_lin_lin2"><spring:message code="label.allowaccess.message" /></div>
			<div class="reg_lin_btn_wrapper">
				<div id="allow-access" class="btn_reg_lin" onclick="openAuthPage('linkedin');"></div>
				<div id="skip-button" class="btn-reg-lin-skip"><spring:message code="label.skip.key"/></div>
			</div>
		</div>
	</div>
</div>

<script src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
<script>
	function openAuthPage(socialNetwork) {
		window.open("./socialauth.do?social=" + socialNetwork + "&flow=registration", "Authorization Page", "width=600,height=600,scrollbars=yes");
	}
	
	$("#skip-button").click(function() {
		location.href = "./landing.do";
	});
</script>

</body>
</html>