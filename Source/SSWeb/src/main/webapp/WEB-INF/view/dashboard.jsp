<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.dashboard.title.key"></spring:message></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script type="text/javascript">
	function showBuildHierarchyPage() {
		console.log("showBuildHierarchyPage called");
		var url = "./showbuildhierarchypage.do";
		callAjaxGET(url, showBuildHierarchyPageCallBack, true);
		console.log("showBuildHierarchyPage finished");
	}

	function showBuildHierarchyPageCallBack(data) {
		console.log(data);
		$("#buildhierarchy").html(data);
	}
</script>
</head>
<body>
	<h1>Welcome</h1>
	<br />
	<a href='javascript:showBuildHierarchyPage()'>Build your hierarchy</a>
	<div id="buildhierarchy"></div>
</body>
</html>