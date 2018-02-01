<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft soc-mon-hdr-txt">
				<spring:message code="label.social.monitor.key" /> - <spring:message code="label.social.monitor.stream.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.edit.monitors.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.social.monitor.reports.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.edit.macros.key" />
			</div>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="v-um-hdr-right v-um-hdr-search float-right soc-mon-search-bar">
				<input id="search-monitors-key" class="v-um-inp soc-mon-inp" placeholder="<spring:message code="label.social.monitor.search.key" />">
				<span id="soc-mon-search-icn" class="um-search-icn"></span>
				<div id="soc-mon-search-clr" class="um-clear-input-icn hide" title="clear"></div>
			</div>
			<div class="hm-header-left text-center float-left" style="margin-top: 12px;">
				<div class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<select id="" class="float-left dash-download-sel-item report-selector-choice">
  						<option value= >User1</option>
					</select>	
				</div>
				<div class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<select id="" class="float-left dash-download-sel-item report-selector-choice">
  						<option value= >Segment1</option>
					</select>	
				</div>
				<div class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<select id="" class="float-left dash-download-sel-item report-selector-choice">
  						<option value= >Feed1</option>
					</select>	
				</div>
			</div>
		</div>
	</div>
</div>

<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div class="clearfix">
			<div id="soc-mon-stream-tab" class="soc-mon-tab soc-mon-stream-active"><img id="stream-inactive" src="${initParam.resourcesPath}/resources/images/stream-gray.png"  class="hide soc-mon-icns"><img id="stream-active" src="${initParam.resourcesPath}/resources/images/stream-blue.png" class="soc-mon-icns">Stream</div>
			<div id="soc-mon-alerts-tab" class="soc-mon-tab"><img id="alert-inactive" src="${initParam.resourcesPath}/resources/images/flag-gray.png" class="soc-mon-icns"><img id="alert-active" src="${initParam.resourcesPath}/resources/images/flag-yellow.png" class="hide soc-mon-icns">Alerts</div>
			<div id="soc-mon-escalated-tab" class="soc-mon-tab"><img id="esc-inactive" src="${initParam.resourcesPath}/resources/images/escalated-gray.png" class="soc-mon-icns"><img id="esc-active" src="${initParam.resourcesPath}/resources/images/escalated-orange.png" class="hide soc-mon-icns">Escalations</div>
			<div id="soc-mon-resolved-tab" class="soc-mon-tab"><img id="res-inactive" src="${initParam.resourcesPath}/resources/images/verified-gray.png" class="soc-mon-icns"><img id="res-active" src="${initParam.resourcesPath}/resources/images/verified-green.png" class="hide soc-mon-icns">Resolutions</div>
		</div>
	</div>
</div>

<div id="soc-mon-stream" class="dash-wrapper-main">
	<div class="dash-container container">
		<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
			<div class="container">
				<div class="hm-header-row clearfix">
					<div class="hm-header-left text-center float-left">
						<img id="stream-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="float-left stream-checkbox">
						<img id="stream-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="hide float-left stream-checkbox">
						<div id="stream-bulk-actions" class="float-left stream-bulk-actions">
							<div class="bulk-actions-select">Bulk Actions <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="chevron-down" class="float-right bulk-actions-dropdown-img"><img id="chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img"></div>
							<div id="stream-bulk-action-options" class="hide float-left bulk-actions-options">
								<div id="stream-bulk-unflag" class="bulk-option">Unflag</div>
								<div id="stream-bulk-flag" class="bulk-option">Flag</div>
								<div id="stream-bulk-esc" class="bulk-option">Escalate</div>
								<div id="stream-bulk-res" class="bulk-option">Resolve</div>	
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
/* var  is_dashboard_loaded = window.is_dashboard_loaded; */
$(document).ready(function() {
	$(document).attr("title", "Social Monitor");
});
</script>