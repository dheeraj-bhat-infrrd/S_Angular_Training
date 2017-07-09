<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="requestedKey"><spring:message code="label.recent.activity.requested.key" /></c:set>
<c:set var="reportKey"><spring:message code="label.recent.activity.report.key" /></c:set>
<c:set var="dateRangeKey"><spring:message code="label.recent.activity.daterange.key" /></c:set>
<c:set var="requestedByKey"><spring:message code="label.recent.activity.requestedby.key" /></c:set>
<c:set var="statusKey"><spring:message code="label.recent.activity.status.key" /></c:set>
<div id="empty-list-msg-div" class="hide">
	<div style="text-align:center; margin:30% auto">
		<span class="incomplete-trans-span">There are No Recent Activities</span>
	</div>
</div>
<div id="recent-activity-list" class="hide">
	
</div>
<script>
var recentActivityList=null;
var batchSize = 10;
var startIndex=0;
$(document).ready(function(){
	var recentActivityCount = getRecentActivityCount();

	var tableHeaderData="<table class=\"v-um-tbl\" style=\"margin-bottom:15px\" >"
		+"<tr id=\"u-tbl-header\" class=\"u-tbl-header\">"
		+"<td class=\"v-tbl-recent-activity \">${requestedKey}</td>"
		+"<td class=\"v-tbl-recent-activity\">${reportKey}</td>"
		+"<td class=\"v-tbl-recent-activity\" \>${dateRangeKey}</td>"
		+"<td class=\"v-tbl-recent-activity \">${requestedByKey}</td>"
		+"<td class=\"v-tbl-recent-activity\" style='width:25%'>${statusKey}</td>"
		+"<td class=\"v-tbl-recent-activity \"></td>"
		+"</tr>";
	
	drawRecentActivity(startIndex, batchSize,tableHeaderData);
	showHidePaginateButtons(startIndex, recentActivityCount);
	
	if(recentActivityCount > 0){
		$('#recent-activity-list').show();
		$('#empty-list-msg-div').hide();
		
		
	}else{
		$('#recent-activity-list').hide();
		$('#empty-list-msg-div').show();
	}		
	
	console.log("==========================\n\n",recentActivityList,"\n\n========================\n\n");
});
	
</script>