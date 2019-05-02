<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="company" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="region" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="branch" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="individual" var="profileLevel"></c:set>
	</c:when>
</c:choose>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.showwidget.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>

<div id="temp-div"></div>
<div id="hm-main-content-wrapper"
	class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="um-header margin-top-25">
			<spring:message code="label.widgetheader.key" /> - <div id= widget-name" style="display: inline;">${entityName}</div>
		</div>
		<div class="clearfix st-score-wrapper" style="height: 600px">
			<div id="basic-widget-view" class="float-left wd-score-txt widget-display" ></div>
			<div class="clearfix float-right st-score-rt pos-relative widget-code" style="width: 40%;">
				<div style="padding:5px">
					<div class="st-widget-txt" style="font-weight: 600 !important;">
						<spring:message code="label.widgetdesc.key" />
					</div>
					<div id="widget-container" class="prof-user-address prof-edit-icn">
						<textarea id="widget-code-area"></textarea>
					</div>
					<div class="ol-btn-wrapper widget-copy widget-btn-cpy" >
						<div id="overlay-continue" class="ol-btn cursor-pointer"
							onclick="javascript:copyWidgetToClipboard('widget-code-area')">Copy
							to clipboard</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$(document)
			.ready(
					function() {
						$(document).attr("title", "Widgets");
						updateViewAsScroll();
						var iden = "${entityId}";
						var profileLevel = "${profileLevel}";
						var appBaseUrl = "${ applicationBaseUrl }";
						var body = "";
						if (iden == undefined || profileLevel == undefined
								|| profileLevel == "") {
							body = "Incorrect parameters. Please check your selection.";
						} else {
							body = "&lt;iframe id = \"ss-widget-iframe\" src=\""
									+ appBaseUrl
									+ "rest/widget/"
									+ profileLevel
									+ "/"
									+ iden
									+ "\" frameborder=\"0\" width=\"100%\" height=\"500px\" style=\"overflow-y: scroll;\" &gt&lt;/iframe&gt;";
							body += "&lt;script type=\"text/javascript\"&gt;$(document).ready(function(){ var myEventMethod = window.addEventListener ? \"addEventListener\" : \"attachEvent\"; var myEventListener = window[myEventMethod]; var myEventMessage = myEventMethod == \"attachEvent\" ? \"onmessage\" : \"message\"; myEventListener(myEventMessage, function (e) { if (e.data === parseInt(e.data)) document.getElementById('ss-widget-iframe').height = e.data + \"px\";    }, false);});&lt;/script&gt;";
						}

						var html = $("<div />").html(body).text();
						$("#basic-widget-view").append(html);
						$("#widget-code-area").html(body);
					});
	$('#ss-widget-iframe').css("border","0px");
	$('#ss-widget-iframe').css("border-color","#dcdcdc");
	$('#ss-widget-iframe').css("border-style","solid");


	$('#overlay-continue').click(function() {
		copyWidgetToClipboard("widget-code-area");
		$('#overlay-continue').unbind('click');
	});
</script>
