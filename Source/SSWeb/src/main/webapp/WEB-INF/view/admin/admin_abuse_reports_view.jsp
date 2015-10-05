<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.abusereports.header.key" /></div>
		</div>
	</div>
</div>
&nbsp;
<div class="abuse-report-wrapper">
	<div class="container">
		<div class="abuse-review-row abuse-review-hdr-row row">
			<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">Agent Name</div>
			<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">Survey</div>
			<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">Reporter Name</div>
			<div class="abuse-report-col col-lg-3 col-md-3 col-sm-3 col-xs-3">Reporter Email</div>
		</div>
		<div id="admin-abs-sur-list">
			<!-- Get the Abusive Survey list from the JavaScript -->
		</div>
	</div>
</div>

<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	showAbusiveReviews(0,10);
});
</script>