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
	var body = "";
	if (iden == undefined || profileLevel == undefined || profileLevel == ""){
		body = "Incorrect parameters. Please check your selection.";
	} else {
		body = "&lt;iframe src=\"http://localhost:8080/rest/widget/" + profileLevel + "/" + iden + "\" height=\"100%\" style=\"border : 0\"&gt;&lt;/iframe&gt;";
	}
	$("#widget-code-area").html(body);
});
</script>