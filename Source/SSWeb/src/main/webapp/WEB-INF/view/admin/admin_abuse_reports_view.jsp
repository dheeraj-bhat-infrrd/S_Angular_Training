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
	<div class="container overflow-x-scroll abuse-text-hdr-window">
		<div class="abuse-review-row row">
			<div class="abuse-review-hdr-row col-lg-2 col-md-2 col-sm-2 col-xs-2">Agent Name</div>
			<div class="abuse-review-hdr-row col-lg-3 col-md-3 col-sm-3 col-xs-3">Survey</div>
			<div class="abuse-review-hdr-row col-lg-2 col-md-2 col-sm-2 col-xs-2">Reporter Name</div>
			<div class="abuse-review-hdr-row col-lg-2 col-md-2 col-sm-2 col-xs-2">Reporter Email</div>
			<div class="abuse-review-hdr-row col-lg-2 col-md-2 col-sm-2 col-xs-2">Report Reason</div>
			<div class="abuse-review-hdr-row col-lg-1 col-md-1 col-sm-1 col-xs-1">Edit</div>
		</div>
		<div id="admin-abs-sur-list">
			<!-- Get the Abusive Survey list from the JavaScript -->
		</div>
	</div>
</div>

<div id="temp-message" class="hide"></div>
<script>
$(document).ready(function() {
	abuseReportStartIndex = 0;
	showAbusiveReviews(abuseReportStartIndex, abuseReportBatch);
	
	attachScrollEventOnAbuseReports();
});
</script>