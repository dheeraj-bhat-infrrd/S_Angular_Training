<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.transaction.monitor.key" /></div>			
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container trans-monitor-dash-container">	
		<div class="dash-stats-wrapper bord-bot-dc clearfix trans-monitor-wrapper">
			<div class="trans-monitor-sub-header-sys">
				<div class="trans-monitor-sub-header-box-sys"></div>
				<span class="trans-monitor-sub-header-span">System</span>
			</div>
			<div class="trans-monitor-graphs-wrapper">
				<div class="trans-monitor-graph-col-sys">
					<span class="trans-monitor-graph-span">Automated Transactions</span>
					<div id="sys-auto-trans-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-danger">
					<span class="trans-monitor-graph-span">Invitations-sent</span>
					<div id="sys-invite-sent-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-warn">
					<span class="trans-monitor-graph-span">Unprocessed Transactions</span>
					<div id="sys-unpro-trans-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-gray">
					<span class="trans-monitor-graph-span">Reminders Sent</span>
					<div id="sys-rem-sent-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-normal">
					<span class="trans-monitor-graph-span">Surveys Completed</span>
					<div id="sys-sur-comp-graph" class="trans-monitor-graph-div"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function(){
		var graphData = [
		                 {"companyId":0,"transactionDate":"Nov 19, 2017","surveyInvitationSentCount":0,"transactionReceivedCount":0,"surveycompletedCount":5,"surveyReminderSentCount":10,"corruptedCount":0,"duplicateCount":0,"oldRecordCount":0,"ignoredCount":0,"mismatchedCount":0,"notAllowedCount":0},
		                 {"companyId":0,"transactionDate":"Nov 20, 2017","surveyInvitationSentCount":35,"transactionReceivedCount":44,"surveycompletedCount":13,"surveyReminderSentCount":0,"corruptedCount":0,"duplicateCount":0,"oldRecordCount":0,"ignoredCount":0,"mismatchedCount":0,"notAllowedCount":0},
		                 {"companyId":0,"transactionDate":"Nov 22, 2017","surveyInvitationSentCount":37,"transactionReceivedCount":44,"surveycompletedCount":13,"surveyReminderSentCount":0,"corruptedCount":0,"duplicateCount":0,"oldRecordCount":0,"ignoredCount":0,"mismatchedCount":0,"notAllowedCount":0}
		                 ];
		paintTransactionMonitorGraph(graphData,'sys-auto-trans-graph');
	});
</script>