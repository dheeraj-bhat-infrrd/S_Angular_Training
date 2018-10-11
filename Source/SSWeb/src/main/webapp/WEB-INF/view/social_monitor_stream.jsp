<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<input id="companyId" value="${companyId}" type="hidden"/>
<div id="bulk-options-popup" class="hide bulk-action-popup">
	<button type="button" class="close bulk-options-dismiss" id="dismiss-bulk-options">&times;</button>
	<div id="stream-bulk-actions-popup-body" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 bulk-action-edit-container">
		<div class="bulk-action-hdr">Bulk Actions</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<div id="bulk-send-mail-post" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note stream-post-mail-note-active">Send Email</div>
			<div id="bulk-private-note-post" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note">Private Note</div>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<textarea class="form-control stream-post-textbox" rows="3" id="bulk-edit-txt-box" placeholder="Send an email message to offending user or take a private note"></textarea>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 stream-actions-btn-container">
			<div id="bulk-macro-dropdown" class="col-lg-5 col-md-5 col-sm-5 col-xs-5 macro-dropdown">
				<img src="${initParam.resourcesPath}/resources/images/flash.png" class="macro-dropdown-icn">
				 Apply Macro 
				<img id="bulk-mac-chevron-down" src="${initParam.resourcesPath}/resources/images/chevron-down.png" class="macro-dropdown-chevron">
				<img id="bulk-mac-chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide macro-dropdown-chevron">
			</div>
			<div id="bulk-edit-unflag" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn bulk-stream-action-unflag bulk-act-btn">
				Unflag
			</div>
			<div id="bulk-edit-flag" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn bulk-stream-action-flag bulk-act-btn">
				Flag
			</div>
			<div id="bulk-edit-esc" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn bulk-stream-action-esc bulk-act-btn">
				Escalate
			</div>
			<div id="bulk-edit-res" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn bulk-stream-action-res bulk-act-btn">
				Resolve
			</div>
			<div id="bulk-edit-sub" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn bulk-stream-action-submit bulk-act-btn">
				Submit
			</div>
			<div id="bulk-macro-options" class="hide float-left macro-options">
					
			</div>
		</div>		
	</div>
</div>

<div id="action-popup" class="hide bulk-action-popup">
	<button type="button" class="close bulk-options-dismiss" id="dismiss-bulk-action-popup" onclick="javascript:actionPopupRevert()">&times;</button>
	<div id="action-popup-body" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 bulk-action-edit-container">
		<div class="bulk-action-hdr">Bulk Actions</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<div id="action-send-mail-post" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note stream-post-mail-note-active">Send Email</div>
			<div id="action-private-note-post" class="col-lg-3 col-md-3 col-sm-3 col-xs-3 stream-post-mail-note">Private Note</div>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<textarea class="form-control stream-post-textbox" rows="3" id="action-edit-txt-box" placeholder="Send an email message to offending user or take a private note"></textarea>
		</div>
		<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 stream-actions-btn-container">
			<div id="action-cancel" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn act-stream-action-submit bulk-act-btn">
				Cancel
			</div>
			<div id="action-edit-unflag" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn act-stream-action-unflag bulk-act-btn hide">
				Unflag
			</div>
			<div id="action-edit-flag" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn act-stream-action-flag bulk-act-btn hide">
				Flag
			</div>
			<div id="action-edit-esc" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn act-stream-action-esc bulk-act-btn hide">
			Escalate
			</div>
			<div id="action-edit-res" class="col-lg-2 col-md-2 col-sm-2 col-xs-2 stream-actions-btn act-stream-action-res bulk-act-btn hide">
				Resolve
			</div>
		</div>		
	</div>
</div>

<div id="duplicate-post-popup" class="hide bulk-action-popup dup-post-popup">
	<div id="dup-dash" class="hide" ></div>
	<button type="button" class="close bulk-options-dismiss" id="dismiss-duplicate-post-popup">&times;</button>
	<div id="dup-post-popup-body-container" class="col-lg-6 col-md-6 col-sm-6 col-xs-6 bulk-action-edit-container" style="top: 20px;">
		<div class="bulk-action-hdr">Manage Duplicates</div>
		<div id="dup-post-popup-body">
			<%@ include file="social_monitor_duplicate_popup.jsp" %>
		</div>
	</div>
</div>
			
<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-hdr">
	
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft soc-mon-hdr-txt">
				<spring:message code="label.social.monitor.key" /> - <spring:message code="label.social.monitor.stream.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorpage.do')">
					<spring:message code="label.edit.monitors.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitorreportspage.do')">
					<spring:message code="label.social.monitor.reports.key" />
			</div>
			<div class="float-right hm-header-right text-center soc-mon-btn" onclick="javascript:showMainContent('./showsocialmonitormacropage.do')">
					<spring:message code="label.edit.macros.key" />
			</div>
		</div>
	</div>
</div>

<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="v-um-hdr-right v-um-hdr-search float-right soc-mon-search-bar">
				<input id="search-post" class="v-um-inp soc-mon-inp" placeholder="<spring:message code="label.social.monitor.search.key" />">
				<span id="soc-mon-stream-search-icn" class="um-search-icn"></span>
				<div id="soc-mon-stream-search-clr" class="um-clear-input-icn hide" title="clear"></div>
			</div>
			<div class="hm-header-left text-center float-left" style="margin-top: 12px;">
				<div id="stream-usr-selection" class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<jsp:include page="social_monitor_stream_user_dropdown.jsp"></jsp:include>
				</div>
				<div id="stream-seg-selection" class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<jsp:include page="social_monitor_stream_segment_dropdown.jsp"></jsp:include>	
				</div>
				<div id="stream-feed-selection" class="dash-btn-dl-sd-admin report-selector soc-mon-dropdown" >
					<jsp:include page="social_monitor_stream_feed_dropdown.jsp"></jsp:include>
				</div>
			</div>
		</div>
	</div>
</div>

<input type="hidden" id="selected-post-ids" data-post-ids='[]'>
<div class="dash-wrapper-main">
	<div class="dash-container container">
		<div id="stream-tabs" class="clearfix" data-status="NEW" data-trusted-source=false data-socMonOnLoad=true>
			<div id="soc-mon-stream-tab" class="soc-mon-tab soc-mon-stream-active" data-disabled=true><img id="stream-inactive" src="${initParam.resourcesPath}/resources/images/stream-gray.png"  class="hide soc-mon-icns"><img id="stream-active" src="${initParam.resourcesPath}/resources/images/stream-blue.png" class="soc-mon-icns">Stream</div>
			<div id="soc-mon-alerts-tab" class="soc-mon-tab" data-disabled=false><img id="alert-inactive" src="${initParam.resourcesPath}/resources/images/flag-gray.png" class="soc-mon-icns"><img id="alert-active" src="${initParam.resourcesPath}/resources/images/flag-yellow.png" class="hide soc-mon-icns">Alerts</div>
			<div id="soc-mon-escalated-tab" class="soc-mon-tab" data-disabled=false><img id="esc-inactive" src="${initParam.resourcesPath}/resources/images/escalated-gray.png" class="soc-mon-icns"><img id="esc-active" src="${initParam.resourcesPath}/resources/images/escalated-orange.png" class="hide soc-mon-icns">Escalations</div>
			<div id="soc-mon-resolved-tab" class="soc-mon-tab" data-disabled=false><img id="res-inactive" src="${initParam.resourcesPath}/resources/images/verified-gray.png" class="soc-mon-icns"><img id="res-active" src="${initParam.resourcesPath}/resources/images/verified-green.png" class="hide soc-mon-icns">Resolutions</div>
			<div id="soc-mon-trusted-tab" class="soc-mon-tab" data-disabled=false><img id="trust-inactive" src="${initParam.resourcesPath}/resources/images/verified-gray.png" class="soc-mon-icns"><img id="trust-active" src="${initParam.resourcesPath}/resources/images/verified-green.png" class="hide soc-mon-icns">Trusted Source</div>
		</div>
	</div>
</div>

<input type="hidden" id="action-type-sel" value=1>
<form id="macro-form-apply">
	<input type="hidden" id="macro-form-is-dup" class="macro-form-is-dup" name="macro-form-is-dup" value="false">
	<input type="hidden" id="macro-form-post-id" class="macro-form-post-id" name="macro-form-post-id" value="">
	<input type="hidden" id="macro-form-status" name="macro-form-status" class="macro-form-status" value="NEW">
	<input type="hidden" id="macro-form-text-act-type" class="macro-form-text-act-type" name="macro-form-text-act-type" value="SEND_EMAIL">
	<input type="hidden" id="macro-form-macro-id" class="macro-form-macro-id" name="macro-form-macro-id" value="">
	<input type="hidden" id="macro-form-text" class="macro-form-text" name="macro-form-text" value="">
</form>

<form id="bulk-actions-apply">
	<input type="hidden" id="form-is-dup" class="form-is-dup" name="form-is-dup" value="false">
	<input type="hidden" id="form-post-id" class="form-post-id" name="form-post-id" value="">
	<input type="hidden" id="form-status" name="form-status" class="form-status" value="NEW">
	<input type="hidden" id="form-text-act-type" class="form-text-act-type" name="form-text-act-type" value="SEND_EMAIL">
	<input type="hidden" id="form-macro-id" class="form-macro-id" name="form-macro-id" value="">
	<input type="hidden" id=form-post-textbox class="form-post-textbox" name="form-post-textbox" value="">
</form>

<div id="soc-mon-stream" class="dash-wrapper-main">
	<div id="stream-dash" class="hide" ></div>
	<div class="dash-container container">
		<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr">
			<div class="container" style="width:100%">
				<div class="hm-header-row clearfix">
					<div class="hm-header-left text-center float-left">
						<img id="stream-unchecked" src="${initParam.resourcesPath}/resources/images/check-no.png"  class="float-left stream-checkbox">
						<img id="stream-checked" src="${initParam.resourcesPath}/resources/images/check-yes.png"  class="hide float-left stream-checkbox">
						<div id="stream-bulk-actions" class="float-left stream-bulk-actions">
							<div class="bulk-actions-select">Select Action <img src="${initParam.resourcesPath}/resources/images/chevron-down.png" id="chevron-down" class="float-right bulk-actions-dropdown-img"><img id="chevron-up" src="${initParam.resourcesPath}/resources/images/chevron-up.png" class="hide float-right bulk-actions-dropdown-img"></div>
							<div id="stream-bulk-action-options" class="hide float-left bulk-actions-options">
								<div id="stream-bulk-edit" class="bulk-option">Edit</div>
								<div id="stream-bulk-unflag" class="bulk-option">Unflag</div>
								<div id="stream-bulk-flag" class="bulk-option">Flag</div>
								<div id="stream-bulk-esc" class="bulk-option">Escalate</div>
								<div id="stream-bulk-res" class="bulk-option">Resolve</div>	
							</div>
						</div>
					</div>
					<div id="stream-pagination" class="float-right soc-mon-pagination" data-startIndex=0 data-count=0>
						<div class="soc-mon-pag-text"><span id="stream-item-count" class="soc-mon-bold-text">0</span> items</div>
						<div id="stream-start-page" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-double-left.png" class="soc-mon-pag-icn"></div>
						<div id="stream-start-page-active" class="hide soc-mon-pag-active"><img src="${initParam.resourcesPath}/resources/images/chevron-double-left-blue.png" class="soc-mon-pag-icn"></div>
						<div id="stream-prev-page" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-left.png"  class="soc-mon-pag-icn"></div>
						<div id="stream-prev-page-active" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-left-blue.png" class="soc-mon-pag-icn"></div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-soc-mon" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="stream-page-count" class="paginate-total-pages">0</span>
						</div>
						<div id="stream-next-page" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-right.png" class="soc-mon-pag-icn"></div>
						<div id="stream-next-page-active" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-right-blue.png" class="soc-mon-pag-icn"></div>
						<div id="stream-end-page" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-double-right.png" class="soc-mon-pag-icn"></div>
						<div id="stream-end-page-active" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-double-right-blue.png" class="soc-mon-pag-icn"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div  id="empty-stream" class="hide dash-container container">
		<span class="incomplete-trans-span stream-alert">No posts found for the stream</span>
	</div>
	<div  id="stream-posts" class="dash-container container">
		
	</div>
	<div class="dash-container container">
		<div class="hm-header-main-wrapper hm-hdr-bord-bot soc-mon-sub-hdr" style="border-top: 1px solid #d6d6d6;">
			<div class="container" style="width:100%">
				<div class="hm-header-row clearfix">
					<div id="stream-pagination-bottom" class="float-right soc-mon-pagination" data-startIndex=0 data-count=0>
						<div class="soc-mon-pag-text"><span id="stream-item-count-bottom" class="soc-mon-bold-text">0</span> items</div>
						<div id="stream-start-page-bottom" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-double-left.png" class="soc-mon-pag-icn"></div>
						<div id="stream-start-page-active-bottom" class="hide soc-mon-pag-active"><img src="${initParam.resourcesPath}/resources/images/chevron-double-left-blue.png" class="soc-mon-pag-icn"></div>
						<div id="stream-prev-page-bottom" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-left.png"  class="soc-mon-pag-icn"></div>
						<div id="stream-prev-page-active-bottom" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-left-blue.png" class="soc-mon-pag-icn"></div>
						<div class="paginate-sel-box float-left">
							<input id="sel-page-soc-mon-bottom" type="text" pattern="[0-9]*" class="sel-page" value="0"/>
							<span class="paginate-divider">/</span>
							<span id="stream-page-count-bottom" class="paginate-total-pages">0</span>
						</div>
						<div id="stream-next-page-bottom" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-right.png" class="soc-mon-pag-icn"></div>
						<div id="stream-next-page-active-bottom" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-right-blue.png" class="soc-mon-pag-icn"></div>
						<div id="stream-end-page-bottom" class="soc-mon-pag"><img src="${initParam.resourcesPath}/resources/images/chevron-double-right.png" class="soc-mon-pag-icn"></div>
						<div id="stream-end-page-active-bottom" class="soc-mon-pag-active hide"><img src="${initParam.resourcesPath}/resources/images/chevron-double-right-blue.png" class="soc-mon-pag-icn"></div>
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
	getMacrosForStream();
	getStreamPosts(0,'NEW');
	getSegmentsByCompanyId();
});
</script>