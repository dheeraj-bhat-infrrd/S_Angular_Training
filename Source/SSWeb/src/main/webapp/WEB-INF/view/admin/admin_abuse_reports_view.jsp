<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
&nbsp;
<table  align="center">
	<thead>
		<tr>
			<th align="left" width="15%">Agent Name</th>
			<th width="3%"></th>
			<th align="left" width="30%">Survey</th>
			<th width="3%"></th>
			<th align="left" width="15%">Reporter Name</th>
			<th width="3%"></th>
			<th align="left" width="30%">Reporter Email</th>
		</tr>
	</thead>
</table>
<table id="admin-abs-sur-list" align="center">
	<!-- Get the Abusive Survey list from the JavaScript -->
</table>

<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	showAbusiveReviews(0,10);
});
</script>