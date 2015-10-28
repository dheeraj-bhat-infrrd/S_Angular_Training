<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<div id="widget-container" class="prof-user-address prof-edit-icn">
	<textarea id="widget-code-area"></textarea>
</div>
<script>
$(document).ready(function() {
	var iden = "${ iden }";
	var profileLevel = "${ profileLevel }";
	var appBaseUrl = "${ applicationBaseUrl }";
	var body = "";
	if (iden == undefined || profileLevel == undefined || profileLevel == ""){
		body = "Incorrect parameters. Please check your selection.";
	} else {
		body = "&lt;iframe id = \"ss-widget-iframe\" src=\"" + appBaseUrl +  "rest/widget/" + profileLevel + "/" + iden + "\" frameborder=\"0\" width=\"100%\"  height=\"500px\" scrolling=\"no\" /&gt;";
	}
	$("#widget-code-area").html(body);
});
</script>