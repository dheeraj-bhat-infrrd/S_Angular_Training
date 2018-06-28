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

<div id="transaction-monitor-container" class="dash-wrapper-main">
	<div id="transaction-monitor-graph-container" class="dash-container container trans-monitor-dash-container">	
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
				<div class="trans-monitor-graph-col-sys">
					<span class="trans-monitor-graph-span">Invitations-sent</span>
					<div id="sys-invite-sent-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-sys">
					<span class="trans-monitor-graph-span">Reminders Sent</span>
					<div id="sys-rem-sent-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-sys">
					<span class="trans-monitor-graph-span">Unprocessed Transactions</span>
					<div id="sys-unpro-trans-graph" class="trans-monitor-graph-div"></div>
				</div>
				<div class="trans-monitor-graph-col-sys">
					<span class="trans-monitor-graph-span">Surveys Completed</span>
					<div id="sys-sur-comp-graph" class="trans-monitor-graph-div"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	$(document).ready(function(){
		showSystemSurveyGraph(-1,14);
		getTransactionMonitorData('error',14);
	});
</script>