<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<form id="add-macro-form" data-state="new" data-status="new">
<input type="hidden" id="macro-id" name="macro-id" value="">
<input type="hidden" id="macro-usage" name="macro-usage" value=0>
<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft soc-mon-hdr-txt">
				<spring:message code="label.social.monitor.key" /> - <spring:message code="label.edit.macros.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorpage.do')">
					<spring:message code="label.edit.monitors.key" />
			</div>
			<%-- <div class="float-right hm-header-right text-center soc-mon-btn" onclick="">
					<spring:message code="label.social.monitor.reports.key" />
			</div> --%>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorstreampage.do')">
					<spring:message code="label.view.stream.key" />
			</div>
		</div>
	</div>
</div>

<div id="soc-mon-add-macro" class="dash-wrapper-main" style="margin-bottom:40px">
	<div class="dash-container container">
		<div class="dash-stats-wrapper bord-bot-dc clearfix" style="border:0">
			<div id="goto-macro-page" class="chevron-macro-page" onclick="javascript:showMainContent('./showsocialmonitormacropage.do')"><img src="${initParam.resourcesPath}/resources/images/chevron-left-large.png" class="float-left"></div>
			<div id="macro-name-hdr" class="macro-hdr-txt">Add Macro</div>
			<div id="macro-updated-date" class="macro-hdr-desc">A macro is a prepared response or action that is used to manage posts.</div>
		</div>
	</div>
	<div class="dash-container container">
		<div class="dash-stats-wrapper bord-bot-dc clearfix add-macro-form-container">
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt">Macro name*</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt-box">						
				<textarea class="form-control stream-post-textbox macro-name-txt-box" rows="1" id="macro-name" name="macro-name" placeholder="Enter a Macro Name"></textarea>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt">Description</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-form-txt-box">						
				<textarea class="form-control stream-post-textbox" rows="1" id="macro-description" name="macro-description" placeholder="Enter an optional description"></textarea>
			</div>
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix" style="padding-left:15px">
			<div class="sm-display-flex">
				<div class="macro-action-desc col-lg-6 col-md-6 col-sm-6 col-xs-6">
					<div class="macro-action-txt">Actions</div>
					<div class="macro-action-desc">Add actions to add a note, send an email or change the status of a post.</div>
				</div>
				<div class="macro-action-desc col-lg-6 col-md-6 col-sm-6 col-xs-6 sm-display-flex">
					<input type="hidden" id="macro-status" name="macro-status" value="true">
					<div class="macro-status-text-div">
						Macro status :
					</div>
					<div class="mac-radio-div">
						<div id="add-mac-active-radio" class="add-mac-radio macro-radio-outer">
							<div class="radio-inner"></div>
						</div>
						<div id="add-macro-status" class="macro-form-txt mac-status-text-span">
							Active
						</div>
					</div>
					<div class="mac-radio-div" style="margin-left: 20px;">
						<div id="add-mac-inactive-radio" class="add-mac-radio macro-radio-outer">
							<div class="radio-inner hide"></div>
						</div>
						<div id="add-macro-status" class="macro-form-txt mac-status-text-span">
							Inactive
						</div>
					</div>
				</div>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-add-action-container">
				<div id="add-macro-status" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-form-txt" style="font-size: 14px;  margin-bottom: 0;">
					Status :
				</div>
				<div id="add-macro-alerts" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-action-dropdown action-dropdown-margin">
					<input type="hidden" id="macro-alert" name="macro-alert" value="0">
					<div class="bulk-actions-select"><span id="macro-alert-text">Alerts</span> <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="macro-alerts-chevron-down" class="float-right"><img id="macro-alerts-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right"></div>
					<div id="add-macro-alerts-options" class="hide float-left add-macro-options">
						<div id="macro-unflag" class="add-macro-dropdown-option">Unflag</div>
						<div id="macro-flag" class="add-macro-dropdown-option">Flag</div>
						<div id="macro-esc" class="add-macro-dropdown-option">Escalate</div>
						<div id="macro-res" class="add-macro-dropdown-option">Resolve</div>
					</div>
				</div>
			</div>
			<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 macro-add-action-container">
				<div id="add-macro-action" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 macro-action-dropdown">
					<input type="hidden" id="macro-action-type" name="macro-action-type" value="PRIVATE_NOTE">
					<div class="bulk-actions-select"><span id="macro-action-type-text">Private Note</span><img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="macro-action-chevron-down" class="float-right"><img id="macro-action-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right"></div>
					<div id="add-macro-action-options" class="hide float-left add-macro-options">
						<div id="macro-pr-note" class="add-macro-dropdown-option">Private Note</div>
						<div id="macro-sn-mail" class="add-macro-dropdown-option">Send Email</div>
					</div>	
				</div>
				<div class="col-lg-9 col-md-9 col-sm-9 col-xs-9 action-textbox">						
						<textarea class="form-control stream-post-textbox" rows="1" id="macro-action-text" name="macro-action-text" placeholder=""></textarea>
				</div>
			</div>
			<!-- <div class="float-left hm-header-left text-center macro-add-action-btn" onclick="">
				Add Action
			</div> -->
		</div>
		<div class="dash-stats-wrapper bord-bot-dc clearfix no-border-style">
			<div id="add-macro-save-active" class="hide float-right hm-header-right text-center macro-add-btn" onclick="javascript:addMacro()">
					Save
			</div>
			<div id="add-macro-save-inactive" class="float-right hm-header-right text-center macro-add-btn-disabled">
					Save
			</div>
			<div class="float-right hm-header-right text-cente macro-add-btn" onclick="javascript:showMainContent('./showsocialmonitormacropage.do')">
					Cancel
			</div>
		</div>
	</div>
</div>
</form>