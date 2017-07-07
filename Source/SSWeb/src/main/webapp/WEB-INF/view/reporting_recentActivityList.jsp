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
	
	function getStatusString(status){
		var statusString;
		switch(status){
		case 0: statusString='Pending';
			break;
		case 1: statusString='Download';
			break;
		case 2: statusString='Failed';
			break;
		default: statusString='Failed'
		}
		return statusString;
	}
	
	if(recentActivityCount > 0){
		$('#recent-activity-list').show();
		$('#empty-list-msg-div').hide();
		
		var tableHeaderData="<table class=\"v-um-tbl\" style=\"margin-bottom:15px\" >"
			+"<tr id=\"u-tbl-header\" class=\"u-tbl-header\">"
			+"<td class=\"v-tbl-recent-activity \">${requestedKey}</td>"
			+"<td class=\"v-tbl-recent-activity-wide\">${reportKey}</td>"
			+"<td class=\"v-tbl-recent-activity-wide\" \>${dateRangeKey}</td>"
			+"<td class=\"v-tbl-recent-activity \">${requestedByKey}</td>"
			+"<td class=\"v-tbl-recent-activity\">${statusKey}</td>"
			+"<td class=\"v-tbl-recent-activity \"></td>"
			+"</tr>";
		recentActivityList = getRecentActivityList(startIndex,batchSize);
		var tableData=''; 
		for(var i=0;i<recentActivityList.length;i++){
			
			var statusString = getStatusString(recentActivityList[i][6]);
			tableData += "<tr class=\"u-tbl-row user-row \">"
				+"<td class=\"v-tbl-recent-activity fetch-name hide\">"+i+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][0]+"</td>"
				+"<td class=\"v-tbl-recent-activity-wide fetch-email txt-bold tbl-blue-text\">"+recentActivityList[i][1]+"</td>"
				+"<td class=\"v-tbl-recent-activity-wide fetch-email txt-bold tbl-black-text\" "+(recentActivityList[i][2]==null?("style=\"text-align:center\">"+" "):(">"+recentActivityList[i][2]))+" - "+(recentActivityList[i][3]==null?" ":recentActivityList[i][3])+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold tbl-black-text\">"+recentActivityList[i][4]+" "+recentActivityList[i][5]+"</td>";
			
			if(recentActivityList[i][6]!=1){	
			tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \" style='font-size:13px !important'>"+statusString+"</td>"
				+"<td class=\"v-tbl-recent-activity fetch-name txt-bold \" ><a id=\"recent-act-delete-row\" class='txt-bold recent-act-delete-x' href='#'>X</td>"
				+"</tr>";
			}else{
				tableData +="<td class=\"v-tbl-recent-activity fetch-name txt-bold \"><a id=\"downloadLink\" class='txt-bold tbl-blue-text' href='#'>"+statusString+"</a></td>"
					+""
					+"</tr>";
			}
		}
		$('#recent-activity-list').html(tableHeaderData+tableData);
	}else{
		$('#recent-activity-list').hide();
		$('#empty-list-msg-div').show();
	}
	
	console.log("==========================\n\n",recentActivityList,"\n\n========================\n\n");
});
	
</script>