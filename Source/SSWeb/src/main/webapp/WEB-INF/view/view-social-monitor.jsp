<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user"
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<!DOCTYPE">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
</head>

<body>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.socialmonitor.key" />
				</div>
			</div>
		</div>
	</div>
	<div class="container v-um-container">
		<div class="v-um-header clearfix">
			<div class="v-um-hdr-right v-um-hdr-search float-left">
				<input id="post-search-query" name="post-search-query" class="v-um-inp" placeholder="<spring:message code="label.searchpost.key" />">
				<span id="sm-search-icn" class="um-search-icn"  onclick="postsSearch();"></span>
			</div>
			<div class="dash-btn-dl-sd-admin clear-none resp-float">
				<select id="download-survey-reports" class="float-left dash-download-sel-item">
					<option value=3 data-report="social-monitor"><spring:message code="label.downloadsurveydata.three.key" /></option>
				</select>
				<input id="dsh-start-date" class="dsh-date-picker" placeholder="<spring:message code="label.startdate.key" />">
				<span>-</span>
				<input id="dsh-end-date" class="dsh-date-picker" placeholder="<spring:message code="label.enddate.key" />">
				<div id="dsh-dwnld-report-btn" class="dash-down-go-button float-right cursor-pointer">
					<spring:message code="label.downloadsurveydata.key.click" />
				</div>
			</div>
		</div>
		<div class="v-um-tbl-wrapper" id="social-post-list">
			<div id="ppl-post-cont" class="rt-content-main bord-bot-dc clearfix">
				<div class="float-left panel-tweet-wrapper">
					<div id="prof-posts" class="tweet-panel tweet-panel-left sm-tweet-panel">
						<!--  latest posts get populated here -->
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="temp-message" class="hide"></div>

	<script>
		$(document).ready(function() {
			hideOverlay();
			bindDatePickerforSurveyDownload();
			$(document).attr("title", "Social Monitor");
			var currentProfileName = "${columnName}";
			var currentProfileValue = "${columnValue}";
			setColDetails(currentProfileName, currentProfileValue);
			showPostsSolr( true, "${entityId}" );
			if ($('#server-message>div').hasClass("error-message")) {
				$('#server-message').show();
			}
		});
		
		function postsSearch(){
			showSearchedPostsSolr(true, "${ entityId }", $("#post-search-query").val());
		}
	</script>
</body>
</html>