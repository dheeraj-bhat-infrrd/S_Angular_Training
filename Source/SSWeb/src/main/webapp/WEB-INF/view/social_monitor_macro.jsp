<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-hdr">
	
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft soc-mon-hdr-txt">
				<spring:message code="label.social.monitor.key" /> - <spring:message code="label.edit.macros.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.edit.monitors.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.social.monitor.reports.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorstreampage.do')">
					<spring:message code="label.view.stream.key" />
			</div>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="v-um-hdr-left v-um-hdr-search float-left soc-mon-search-bar">
				<input id="search-macro" class="v-um-inp soc-mon-inp" placeholder="<spring:message code="label.search.macros.key" />">
				<span id="soc-mon-macro-search-icn" class="um-search-icn"></span>
				<div id="soc-mon-macro-search-clr" class="um-clear-input-icn hide" title="clear"></div>
			</div>
			<div class="hm-header-left text-center float-left">
				<div id="macro-status-dropdown" class="float-left stream-bulk-actions macro-dropdown-options">
					<div class="bulk-actions-select">Active <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="macro-page-chevron-down" class="float-right bulk-actions-dropdown-img"><img id="macro-page-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img"></div>
					<div id="macro-status-options" class="hide float-left bulk-actions-options">
						<div id="macro-active" class="bulk-option">Active</div>
						<div id="macro-inactive" class="bulk-option">Inactive</div>
					</div>
				</div>
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn soc-mon-add-mon" onclick="javascript:showMainContent('./showsocialmonitoraddmacropage.do')">
				<spring:message code="label.add.macros.key" />
			</div>
		</div>
	</div>
</div>

<div id="soc-mon-macro" class="dash-wrapper-main" style="margin-bottom:40px">
	<div class="dash-container container">
		<div class="dash-stats-wrapper bord-bot-dc clearfix macro-hdr">
			<div class="macro-hdr-txt">Macros</div>
			<div class="macro-hdr-desc">A macro is a prepared response or action that is used to manage posts.</div>
		</div>
		<div class="bord-bot-dc clearfix">
			<div id="macro-active-container" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 macro-tabs macro-tabs-active">Active</div>
			<div id="macro-inactive-container" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 macro-tabs">Inactive</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix macro-list-div">
			<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 macro-list-hdr ">Name</div>
			<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-list-hdr">Usage  (last 7 days)</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix macro-list-div">
			<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 macro-list-item">Post has questionable language - Flag</div>
			<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-list-item-usage">4</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix macro-list-div">
			<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 macro-list-item">Request post must be Removed - Escalate</div>
			<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-list-item-usage">0</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix macro-list-div">
			<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 macro-list-item">Confirm post Removed - Resolve</div>
			<div class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-list-item-usage">0</div>
		</div>
	</div>
</div>