<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="COMPANY" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="REGION" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="BRANCH" var="profileLevel"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="INDIVIDUAL" var="profileLevel"></c:set>
	</c:when>
</c:choose>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.shownewwidget.key" />
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
		<div class="um-header margin-top-25" style="font-size:20px !important">
			<spring:message code="label.newwidgetheader.key" /> - <div id="widget-name" style="display: inline;">${entityName}</div>
			<div id="widget-reload-conf" class="ol-btn cursor-pointer widget-rld-cur-conf"><spring:message code="label.widgetreloadconfig.key" /></div>
		</div>
		
		
		
		
		<div class="clearfix">
		
			<!-- new widget preview -->
			<div id="basic-widget-view" class="float-left wd-score-txt new-widget-display" style="border: 1px solid #dcdcdc; /*background: url(resources/images/SS-Widget-v3.5-mobile.png)no-repeat center;*/ background-color: #fff;">
			<jsp:include page="./widget/ss_widget.jsp"></jsp:include>
			</div>
			
			
			<!-- new widget configuration -->			
			<div class="clearfix float-right st-score-rt pos-relative widget-code" style="width: 59%;">
				
				<div class="widget-conf-cont" style="padding-top: 1%;">
				    <div class="widget-conf-txt float-left">Background Color</div>
					<div class="float-right" style="padding-top: 1%;"><input type="text" id="widget-bg-clr"></div>
				</div>
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Foreground Color</div>
					<div class="float-right" style="padding-top: 1%;"><input type="text" id="widget-fg-clr"></div>
				</div>
				<div class="widget-conf-cont" style="padding-bottom: 0%;">
				    <div class="widget-conf-txt float-left">Custom Bar Graph Color</div>
					<div class="float-right color-picker-adj" style="padding-top: 2%;"><input type="text" id="widget-bargraph-clr"></div>
					<div class="clear-both">( Default : SocialSurvey Rainbow )</div>
				</div>
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Ratings and Star Color</div>
					<div class="float-right" style="padding-top: 1%;"><input type="text" id="widget-rating-str-clr"></div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Font</div>
				    <div class="float-right" style="padding-top: 1%;">
					<div id="fontSelect" class="fontSelect"><div class="float-right" style="font-size: 11px; color: #5f5f5f; padding-right: 5px;">&#9660;</div>
					</div>
					</div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Font Theme</div>
				    <div class="float-right" style="padding-top: 1%;">
    					<input type="text" name="widget-font-theme" id="widget-font-theme" class="st-item-row-txt cursor-pointer dd-arrow-dn widget-select-bx" autocomplete="off" disabled="disabled" value="">
						<div class="st-dd-wrapper hide" style="width: 160px" id="st-dd-wrapper-widget-font-theme"></div>
				    </div>
				</div>
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Embedded Font Theme</div>
				    <div class="float-right" style="padding-top: 1%;">
				    	<input type="text" name="widget-embedded-font-theme" id="widget-embedded-font-theme" class="st-item-row-txt cursor-pointer dd-arrow-dn widget-select-bx" autocomplete="off" disabled="disabled" value="">
						<div class="st-dd-wrapper hide" style="width: 160px" id="st-dd-wrapper-widget-embedded-font-theme"></div>
				    </div>
				</div>
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Default Review Order</div>
				    <div class="float-right" style="padding-top: 1%;">
				    	<input type="text" name="widget-df-rev-ordr" id="widget-df-rev-ordr" class="st-item-row-txt cursor-pointer dd-arrow-dn widget-select-bx" autocomplete="off" disabled="disabled" value="">
						<div class="st-dd-wrapper hide" style="width: 160px" id="st-dd-wrapper-df-rev-ordr"></div>
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Button One Name</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="button-one-name" id="button-one-name" class="st-item-row-txt widget-select-bx" autocomplete="off" value="" placeholder="Button One Name">
				    </div>
				</div>
								
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Button Two Name</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="button-two-name" id="button-two-name" class="st-item-row-txt widget-select-bx" autocomplete="off" value="" placeholder="Button Two Name">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Review Loader Name</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="ld-mr-alt" id="ld-mr-alt" class="st-item-row-txt widget-select-bx" autocomplete="off" value="" placeholder="Review Loader Name">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Button One Opacity</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="btn-one-opc" id="btn-one-opc" class="st-item-row-txt widget-select-bx wid-sel-bxl-num" autocomplete="off" value="" maxlength="3">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Button Two Opacity</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="btn-two-opc" id="btn-two-opc" class="st-item-row-txt widget-select-bx wid-sel-bxl-num" autocomplete="off" value="" maxlength="3">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Review Loader Opacity</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="rvw-ldr-opc" id="rvw-ldr-opc" class="st-item-row-txt widget-select-bx wid-sel-bxl-num" autocomplete="off" value="" maxlength="3">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">Initial Review Count</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="init-rvw-cnt" id="init-rvw-cnt" class="st-item-row-txt widget-select-bx wid-sel-bxl-num" autocomplete="off" value="" maxlength="3">
				    </div>
				</div>
				
				<div class="widget-conf-cont">
				    <div class="widget-conf-txt float-left">On Load Review Count</div>
				    <div class="float-right" style="padding-top: 1%;">
						<input type="text" name="onld-rvw-cnt" id="onld-rvw-cnt" class="st-item-row-txt widget-select-bx wid-sel-bxl-num" autocomplete="off" value="" maxlength="3">
				    </div>
				</div>
				
				<br/>
				
				<div class="widget-conf-cont" style="padding: 3% 0%;">
				    <div class="widget-conf-txt">Button One Link:</div>
					<input type="text" name="button-one-link" id="button-one-link" class="st-item-row-txt widget-link-bx" autocomplete="off" value="" placeholder="Button One Link">
				</div>

				
				<div class="widget-conf-cont" style="padding: 3% 0%;">
				    <div class="widget-conf-txt">Button Two Link:</div>
					<input type="text" name="button-two-link" id="button-two-link" class="st-item-row-txt widget-link-bx" autocomplete="off" value="" placeholder="Button Two Link">
				</div>
				
				<div class="widget-conf-cont" style="min-height: 190px;">
					<div class="widget-conf-txt float-left">Default Filters:</div>
				   	<div id="widget-filter-select-all" class="ol-btn cursor-pointer float-right widget-btn-sel-al">Select All</div>
					<div class="clear-both" style="padding: 1% 0%;">
					    <div class="float-left">
					    	<div class="clear-both">
								<div id="flt-ss-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">SocialSurvey</div>
							</div>
					        <div class="clear-both">
								<div id="flt-ssv-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">SocialSurvey Verified</div>
							</div>
					        <div class="clear-both">
								<div id="flt-zw-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">Zillow</div>
							</div>
					    </div>
					   <!--  <div class="float-right">
					    	<div class="clear-both">
								<div id="flt-fb-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">Facebook</div>
							</div>
											
							<div class="clear-both">
								<div id="flt-ln-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">LinkedIn</div>
							</div>
											
							<div class="clear-both">
								<div id="flt-gl-chk-box" class="float-left wid-chk-adj bd-check-img"></div>
								<div class="widget-conf-txt float-left">Google</div>
							</div>
						</div> -->
					</div>
				</div>
							
				<div class="widget-conf-cont widget-txt-bx-adj-resp" style="padding: 3% 0%;">
				    <div class="widget-conf-txt">Add / Append Title Tag To Site ( Optional ):</div>
					<textarea name="title-tag-text" id="title-tag-text" class="st-item-row-txt widget-text-bx" autocomplete="off" placeholder="Title Tag Text"></textarea>
				</div>
				
				<div class="widget-conf-cont widget-txt-bx-adj-resp" style="padding: 3% 0%;">
				    <div class="widget-conf-txt float-left">Add / Append Keywords Tag To Site ( Optional ):</div>
					<textarea name="kw-tag-text" id="kw-tag-text" class="st-item-row-txt widget-text-bx" autocomplete="off" placeholder="Keywords Tag Text"></textarea>
				</div>
				
				<div class="widget-conf-cont widget-txt-bx-adj-resp" style="padding: 3% 0%; padding-bottom: 0%">
				    <div class="widget-conf-txt float-left">Add / Append Description Tag To Site ( Optional ):</div>
					<textarea name="dsc-tg-txt" id="dsc-tg-txt" class="st-item-row-txt widget-text-bx" autocomplete="off" placeholder="Description Tag Text"></textarea>
				</div>
				
				<div class="widget-conf-cont widget-chk-adj-resp">
   				    <div id="hide-bg-initly-chk-box" class="float-left wid-chk-adj bd-check-img" style="height: 40px"></div>
   				    <div class="widget-conf-txt float-left">Hide Bar Graph Initially</div>
				</div>
				
				<div class="widget-conf-cont widget-chk-adj-resp">
   				    <div id="hide-ot-initly-chk-box" class="float-left wid-chk-adj bd-check-img"  style="height: 40px"></div>
   				    <div class="widget-conf-txt float-left">Hide Options Tab Initially</div>
				</div>
				
				<div class="widget-conf-cont widget-chk-adj-resp">
   				    <div id="allw-mdst-brndng-chk-box" class="float-left wid-chk-adj bd-check-img" style="height: 40px"></div>
   				    <div class="widget-conf-txt float-left">Allow Modest Branding</div>
				</div>
				
				<div class="widget-conf-cont widget-conf-hist-resp" style="padding: 7% 0%">
				    <div class="widget-conf-txt float-left" style="padding: 4% 0%;">History</div>
				    <div class="float-right">
						<div id="widget-conf-history" class="st-item-row-txt cursor-pointer dd-arrow-dn widget-select-bx widget-hist-adj"></div>
						<div class="st-dd-wrapper widget-hist-adj hide" id="st-dd-wrapper-conf-history" style="height: auto;"></div>
				    </div>
				</div>
				
				<div class="widget-conf-cont widget-save-resp">
					<div class="float-left" style="width: 45%;">
						<div id="widget-conf-save" class="ol-btn cursor-pointer">Save</div>
					</div>
					<div class="float-right" style="width: 55%;">
						<div id="widget-conf-reset" class="ol-btn cursor-pointer">Reset</div>
					</div>
				    <div class="widget-error hide"></div>
				</div>
			</div>
		
			<!-- widget code display area -->
			<div class ="widget-code-ar">
				<div class="st-new-widget-txt" style="font-weight: 600 !important;">
					<spring:message code="label.widgetjsdesc.key" />
				</div>
				<div id="widget-js-container" class="prof-user-address prof-edit-icn">
					<pre class="prettyprint" id="widget-js-code-area"></pre>
				</div>
				<div class="ol-btn-wrapper widget-copy widget-btn-cpy" >
					<div id="overlay-continue-js" class="ol-btn cursor-pointer"
						onclick="javascript:copyWidgetToClipboard('widget-js-code-area')">Copy
						to clipboard</div>
				</div>
				<br/><br/><br/><br/>
				<div class="st-new-widget-txt" style="font-weight: 600 !important;">
					<spring:message code="label.widgetjsdesccust.key" />
				</div>
				<div id="widget-js-cust-container" class="prof-user-address prof-edit-icn">
					<pre class="prettyprint" id="widget-js-cust-code-area"></pre>
				</div>
				<div class="ol-btn-wrapper widget-copy widget-btn-cpy" >
					<div id="overlay-continue-js-cust" class="ol-btn cursor-pointer"
						onclick="javascript:copyWidgetToClipboard('widget-js-cust-code-area')">Copy
						to clipboard</div>
				</div>
				<br/><br/><br/><br/>
				<div class="st-new-widget-txt" style="font-weight: 600 !important;">
					<spring:message code="label.widgetjsidesc.key" />
				</div>
				<div id="widget-jsi-container" class="prof-user-address prof-edit-icn">
					<pre class="prettyprint" id="widget-jsi-code-area"></pre>
				</div>
				<div class="ol-btn-wrapper widget-copy widget-btn-cpy" >
					<div id="overlay-continue-jsi" class="ol-btn cursor-pointer"
						onclick="javascript:copyWidgetToClipboard('widget-jsi-code-area')">Copy
						to clipboard</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script>

var wAppBaseUrl = "${applicationBaseUrl}";
var wProfileLevel = "${profileLevel}";
var wProfileName = "${profileName}";
var wCompanyProfileName = "${companyProfileName}";
var resourcesUrl = "${resourcesUrl}";

var bonf = false;
var btnf = false;
var boof = false;
var btof = false;
var rlof = false;
var lmaf = false;
var ircf = false;
var orcf = false;

	$(document)
			.ready(
					
					function() {
						$(document).attr("title", "Widgets");
						updateViewAsScroll();
						socialSurveyJavascriptWidget.setup();
					});
</script>
