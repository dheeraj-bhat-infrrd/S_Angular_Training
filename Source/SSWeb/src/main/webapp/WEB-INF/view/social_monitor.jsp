<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form id="add-monitor-form" data-state="new" data-status="new">
<div id="add-mon-popup" class="hide bulk-action-popup">
	<button type="button" class="close bulk-options-dismiss dismiss-add-mon-popup" id="dismiss-add-mon-popup" onclick="javascript:hideAddMonitorPopup()">&times;</button>
		<div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 bulk-action-edit-container">
			<div class="bulk-action-hdr">Add Monitor</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt">KeyPhrase*</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt-box">						
				<textarea class="form-control stream-post-textbox macro-name-txt-box" rows="1" id="monitor-keyphrase" name="monitor-keyphrase" placeholder="Enter a KeyPhrase"></textarea>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt">MonitorType</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-1">
				<div id="add-mon-type-dropdown" class="float-left add-mon-type-dropdown">
					<div id="add-mon-type-select" class="bulk-actions-select" data-mon-type=2><div class="float-left" id="add-mon-type-sel-txt">Keyword Monitor</div> 
						<img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="add-mon-type-chevron-down" class="float-right bulk-actions-dropdown-img">
						<img id="add-mon-type-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img">
					</div>
					<div id="add-mon-type-options" class="hide float-left add-mon-type-options">
						<div id="add-mon-type-km" class="bulk-mon-option add-mon-type-opt">
							<img id="add-keyword-mon-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="hide float-left mon-type-checkbox add-mon-type-uncheckbox">
							<img id="add-keyword-mon-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="float-left mon-type-checkbox add-mon-type-checkbox">
							Keyword Monitor
						</div>
						<!-- <div id="add-mon-type-ga" class="bulk-mon-option add-mon-type-opt">
							<img id="add-google-alerts-mon-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="hide float-left mon-type-checkbox add-mon-type-uncheckbox">
							<img id="add-google-alerts-mon-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="float-left mon-type-checkbox add-mon-type-checkbox">
							Google Alerts
						</div> -->
					</div>
				</div>
				<input type="hidden" id="monitor-type" name="monitor-type" value=0>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 stream-actions-btn-container">
				<div id="add-mon-save-active" class="hide float-right hm-header-right text-center macro-add-btn add-mon-save-btn" onclick="javascript:addMonitor()">
					Save
				</div>
				<div id="add-mon-save-inactive" class="float-right hm-header-right text-center macro-add-btn-disabled add-mon-save-btn">
					Save
				</div>
				<div id="add-mon-cancel" class="float-right dismiss-add-mon-popup hm-header-right text-cente macro-add-btn add-mon-cancel-btn" onclick="javascript:hideAddMonitorPopup()">
					Cancel
				</div>
			</div>		
		</div>
</div>
</form>


<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft soc-mon-hdr-txt">
				<spring:message code="label.social.monitor.key" /> - <spring:message code="label.edit.monitors.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorstreampage.do')">
					<spring:message code="label.view.stream.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorreportspage.do')">
					<spring:message code="label.social.monitor.reports.key" />
			</div>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="v-um-hdr-left v-um-hdr-search float-left soc-mon-search-bar">
				<input id="search-monitors-key" class="v-um-inp soc-mon-inp" placeholder="<spring:message code="label.social.monitor.search.key" />">
				<span id="soc-mon-search-icn" class="um-search-icn"></span>
				<div id="soc-mon-search-clr" class="um-clear-input-icn hide" title="clear"></div>
			</div>
			<div class="hm-header-left text-center float-left">
				<div id="mon-type-dropdown" class="float-left stream-bulk-actions macro-dropdown-options">
					<div class="mon-type-select">Monitor Type(s) <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="mon-type-chevron-down" class="float-right bulk-actions-dropdown-img"><img id="mon-type-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img"></div>
					<div id="mon-type-options" class="hide float-left bulk-actions-options mon-type-select-options">
						<div id="mon-type-keyword-mon" class="bulk-mon-option mon-type-option">
							<img id="keyword-mon-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="hide float-left mon-type-checkbox">
							<img id="keyword-mon-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="float-left mon-type-checkbox">
							Keyword Monitor
						</div>
						<!-- <div id="mon-type-google-alerts" class="bulk-mon-option mon-type-option">
							<img id="google-alerts-mon-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="hide float-left mon-type-checkbox">
							<img id="google-alerts-mon-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="float-left mon-type-checkbox">
							Google Alerts
						</div> -->
					</div>
				</div>
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn soc-mon-add-mon" onclick="javascript:showAddMonitorPopup()">
				<spring:message code="label.add.monitors.key" />
			</div>
		</div>
	</div>
</div>

<div id="soc-mon-edit-mon" class="dash-wrapper-main" style="margin-bottom: 20px;">
	<div class="dash-container container zero-padding">
			<div class="container zero-padding" style="width:100%">
				<div class="dash-stats-wrapper clearfix"  >
					<div id="monitor-bulk-actions" class="float-left stream-bulk-actions monitors-bulk-select">
						<div class="bulk-actions-select">Select Action <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="monitor-chevron-down" class="float-right bulk-actions-dropdown-img"><img id="monitor-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img"></div>
						<div id="monitor-bulk-action-options" class="hide float-left bulk-actions-options monitors-bulk-options">
							<div id="monitor-bulk-delete" class="bulk-option">Delete</div>
						</div>
					</div>
				</div>
			</div>
	</div>
	
	<div id="monitor-list-container" class="dash-container container mon-type-container zero-padding">
		<div id="empty-monitors" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 clearfix monitors-empty-div hide">
			<span class="incomplete-trans-span monitors-empty-span">No monitors found</span>
		</div>
	</div>
	
</div>

<script>
	$(document).ready(function(){
		getMonitors();
	});
</script>