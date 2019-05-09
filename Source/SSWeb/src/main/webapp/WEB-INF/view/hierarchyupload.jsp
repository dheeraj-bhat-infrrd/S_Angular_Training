<%@page import="com.realtech.socialsurvey.core.commons.CommonConstants"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.hierarchyupload.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./hierarchyupload.do')">
				<spring:message code="label.header.Hierarchyupload.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./viewhierarchy.do');">
				<spring:message code="label.viewcompanyhierachy.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showusermangementpage.do')">
				<spring:message code="label.header.editteam.key" />
			</div>
			<div class="float-right hm-header-right text-center"
				onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
				<spring:message code="label.header.buildhierarchy.key" />
			</div>
		</div>
	</div>
</div>

<div id="xlsVerifyUplaod" class="clearfix disable">
	<table style="width: 100%; margin: 1%;">
		<tr style="margin-top: 10px; margin-left: 45px;">
		</tr>
		<tr>
			<td>
			<div style="margin: 10px;">
				<div id="mode-message" class="float-left rfr_lbl">
					<spring:message code="label.uploadType.key" />
				</div>
				<div id="add-up-rad" class="float-left bd-cust-rad-item" style="margin-top: 7px;">
					<div id="hierarchy-upload-append" data-type="append"
						class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
					<div class="float-left bd-cust-rad-txt">Add and Update</div>
				</div>
				<div id="rep-rad" class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix" style="margin-top: 7px;">
					<div id="hierarchy-upload-replace" data-type="replace" class="float-left bd-cust-rad-img"></div>
					<div class="float-left bd-cust-rad-txt">Overwrite</div>
				</div>
				<div class="clear-both">
					<div id="warning-toggle" class="warn-toggle">
						<div id="warn-toggle-chk-box" class="float-left bd-check-img bd-check-img-checked clear-both"></div>
						<div class="float-left listing-access-txt warn-resp" style="margin-bottom: 0px; margin-right: 300px;"><spring:message code="label.warn.toggle.key"/>
						</div>
					</div>
					<div id="verify-toggle" class="warn-toggle">
						<div id="verify-chk-box" class="float-left bd-check-img clear-both"></div>
						<div class="float-left listing-access-txt warn-resp ver-resp" style="margin-bottom: 0px; margin-right: 263px;"><spring:message code="label.verify.upload.key"/>
						</div>
					</div>
					<div class="float-left rfr_lbl" style="margin-top: 10px; clear: both;">
						<spring:message code="label.xlsxfile.key" />
						&nbsp;
					</div>
					<div class="float-left rfr_txt" style="margin-top: 10px; margin-bottom: 10px;">
						<div class="rfr_icn icn-logo"></div>
						<div class="icn-lname input-file-icn-left"
							id="input-file-icn-left"></div>
						<div class="rfr_txt_fld">
							<input type="text" class="rfr_input_fld" id="com-xlsx-file"
								name="XlsxFileName"
								placeholder='<spring:message code="label.xlsxfile.placeholder.key"/>'
								value="${XlsxFileName}">
						</div>
						<div>
							<input type="file" class="rfr_input_fld com-logo-comp-info" id="com-file" name="xlsxfile"> 
						</div>
						<div
							class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj"
							id="icn-xlsxfile"></div>
					</div>
					<div id="last-uploaded-date" class="uploaded-file-info">
					</div>
				</div>
				</div>
				<div id="hierarchy-upload"
					class="reg_form_row clearfix hierarchy-btn" style="float:none;">
					<div class="reg_btn disable" id="xlsx-file-upload" style=" margin-bottom: 10px;">
						<spring:message code="label.import.key" />
					</div>
					<div style=" text-align: center; opacity: 0.4;">Please check the Upload Type and Warning toggle before importing.</div>
				</div>
			</td>
		</tr>
	</table>
</div>

<div id="no-data"
	style="width: 120px; margin: auto; color: #2b69a9; font-size: 30px;"
	class="hide">No Changes Made</div>

<div id="lastUploadRun" class="last-run-info hide"></div>

<div id="uploadBatchStatus" class="hide"
	style="font-size: 14px; margin-right: 30px; margin-left: 40px; margin-bottom: 20px;"></div>

<div id="hierarchy-stats" class="clearfix" style="margin: 20px;"></div>

<div class="container hide" id="summary" style="margin-top: 10px;">
	<!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist">
		<li id="region-sum-btn" class="active" style="display: none !important"><a
			href="#upload-summary-region" data-toggle="tab"> Regions </a></li>
		<li id="branch-sum-btn" style="display: none !important"><a
			href="#upload-summary-branch" data-toggle="tab"> Offices </a></li>
		<li id="user-sum-btn" style="display: none !important"><a
			href="#upload-summary-user" data-toggle="tab"> Users </a></li>
	</ul>
	
	<!-- Tab panes -->
	<div class="tab-content">
		<div class="tab-pane fade active in" id="upload-summary-region" style="overflow: auto; max-height: 500px;">
			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message Type</th>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message</th>

					</tr>
				</thead>
				<tbody id="region-upload"></tbody>
			</table>
		</div>
		
		<div class="tab-pane fade" id="upload-summary-branch" style="overflow: auto; max-height: 500px;">
			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message Type</th>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message</th>
					</tr>
				</thead>
				<tbody id="branch-upload"></tbody>
			</table>
		</div>
		<div class="tab-pane fade" id="upload-summary-user" style="overflow: auto; max-height: 500px;">
			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message Type</th>
						<th style="text-align: center; font-weight: 600 !important; font-size: 14px;" class="hier-upload-td">Message</th>
					</tr>
				</thead>
				<tbody id="user-upload"></tbody>
			</table>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {hierarchyUpload.fileUpload();});
</script>