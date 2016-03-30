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
	<table style="width: 100%">
		<tr style="margin-top: 10px; margin-left: 45px;">
			<td>
				<div class="float-left rfr_lbl">
					<spring:message code="label.uploadType.key" />
				</div>
				<div class="float-left bd-cust-rad-item clearfix">
					<div data-type="append"
						class="float-left bd-cust-rad-img bd-cust-rad-img-checked"></div>
					<div class="float-left bd-cust-rad-txt">Append Mode</div>
				</div>
				<div
					class="float-left bd-cust-rad-item bd-cust-rad-item-adj clearfix">
					<div data-type="replace" class="float-left bd-cust-rad-img"></div>
					<div class="float-left bd-cust-rad-txt">Replace Mode</div>
				</div>
				<div class="float-right" style="margin-right: 45px;">
					<div id="dsh-ind-rep-bnt"
						class="float-right dash-btn-dl-sd btn-wid-sm" style="width: 300px">
						<div class="dsh-dwnld-btn float-left cursor-pointer"
							style="width: 100%" onclick="downloadCompanyHierarchyReport()">Download
							Company Hierarchy Report</div>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="float-left">
					<div class="float-left rfr_lbl" style="margin-top: 10px;">
						<spring:message code="label.xlsxfile.key" />
						&nbsp;
					</div>
					<div class="float-left rfr_txt" style="margin-top: 10px;">
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
							<input type="file" class="rfr_input_fld com-logo-comp-info"
								id="com-file" name="xlsxfile"> <input type="hidden"
								id="fileUrl" name="fileUrl">
						</div>
						<div
							class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj"
							id="icn-xlsxfile"></div>
					</div>
					<div class="reg_form_row clearfix hierarchy-btn">
						<div class="reg_btn" id="xlsx-file-verify">
							<spring:message code="label.verify.key" />
						</div>
					</div>
					<div id="hierarchy-upload"
						class="reg_form_row clearfix hierarchy-btn">
						<div class="reg_btn disable" id="xlsx-file-upload">
							<spring:message code="label.import.key" />
						</div>
					</div>
				</div>
			</td>
		</tr>
	</table>
</div>
<div id="no-data"
	style="width: 120px; margin: auto; color: #009FE0; font-size: 30px;"
	class="hide">No Changes Made</div>

<div id="lastUploadRunTimestamp" class="clearfix hide"
	style="font-size: 14px; margin-right: 30px; margin-left: 45px; margin-bottom: 20px;">
</div>

<div id="uploadBatchStatus" class="hide"
	style="font-size: 14px; margin-right: 30px; margin-left: 45px; margin-bottom: 20px;"></div>

<div class="container hide" id="summary" style="margin-top: 10px;">
	<!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist">
		<li id="region-sum-btn" class="active"
			style="display: none !important"><a
			href="#upload-summary-region" data-toggle="tab"> Regions </a></li>
		<li id="branch-sum-btn" style="display: none !important"><a
			href="#upload-summary-branch" data-toggle="tab"> Offices </a></li>
		<li id="user-sum-btn" style="display: none !important"><a
			href="#upload-summary-user" data-toggle="tab"> Users </a></li>
	</ul>

	<!-- Tab panes -->
	<div class="tab-content">
		<div class="tab-pane fade active in" id="upload-summary-region"
			style="overflow: auto; max-height: 500px;">
			<!-- <h2 style="color: #666;">Region Summary</h2> -->
			<div class="clearfix"
				style="margin-top: 30px; width: 425px; margin-left: auto; margin-right: auto;">
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-red"></span><span
						style="margin-left: 5px;">Deleted Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-green"></span><span
						style="margin-left: 5px;">Added Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-blue"></span><span
						style="margin-left: 5px;">Modified Record</span>
				</div>
			</div>
			<div class="clearfix" style="margin-top: 10px;">
				<div id="region-added" class="float-left hide"
					style="margin-right: 20px;"></div>
				<div id="region-modified" class="float-left hide"
					style="margin-right: 20px;"></div>
				<div id="region-deleted" class="float-left hide"
					style="margin-right: 20px;"></div>
			</div>

			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Edit</th>
						<th style="width: 15px"></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Serial No.</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_REGION_REGION_ID%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_REGION_REGION_NAME%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ADDRESS_1%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ADDRESS_2%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_CITY%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_STATE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ZIP%></th>

					</tr>
				</thead>
				<tbody id="region-upload"></tbody>
			</table>
		</div>
		<div class="tab-pane fade" id="upload-summary-branch"
			style="overflow: auto; max-height: 500px;">
			<!-- <h2 style="color: #666;">Branch Summary</h2> -->
			<div class="clearfix"
				style="margin-top: 30px; width: 425px; margin-left: auto; margin-right: auto;">
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-red"></span><span
						style="margin-left: 5px;">Deleted Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-green"></span><span
						style="margin-left: 5px;">Added Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-blue"></span><span
						style="margin-left: 5px;">Modified Record</span>
				</div>
			</div>
			<div class="clearfix" style="margin-top: 10px;">
				<div id="branch-added" class="float-left"
					style="margin-right: 20px;"></div>
				<div id="branch-modified" class="float-left"
					style="margin-right: 20px;"></div>
				<div id="branch-deleted" class="float-left"
					style="margin-right: 20px;"></div>
			</div>
			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Edit</th>
						<th style="width: 15px"></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Serial No.</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_BRANCH_BRANCH_ID%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_BRANCH_BRANCH_NAME%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_REGION_REGION_ID%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ADDRESS_1%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ADDRESS_2%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_CITY%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_STATE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_ZIP%></th>
					</tr>
				</thead>
				<tbody id="branch-upload"></tbody>
			</table>
		</div>
		<div class="tab-pane fade" id="upload-summary-user"
			style="overflow: auto; max-height: 500px;">
			<!-- <h2 style="color: #666;">User Summary</h2> -->
			<div class="clearfix"
				style="margin-top: 30px; width: 425px; margin-left: auto; margin-right: auto;">
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-red"></span><span
						style="margin-left: 5px;">Deleted Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-green"></span><span
						style="margin-left: 5px;">Added Record</span>
				</div>
				<div class="float-left" style="margin-right: 20px;">
					<span class="lgn-col-item hier-col-blue"></span><span
						style="margin-left: 5px;">Modified Record</span>
				</div>
			</div>
			<div class="clearfix" style="margin-top: 10px;">
				<div id="user-added" class="float-left" style="margin-right: 20px;"></div>
				<div id="user-modified" class="float-left"
					style="margin-right: 20px;"></div>
				<div id="user-deleted" class="float-left"
					style="margin-right: 20px;"></div>
			</div>
			<table class="table" style="margin-top: 10px;">
				<thead>
					<tr>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Edit</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Send Invite</th>
						<th style="width: 15px"></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td">Serial No.</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_USER_ID%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_FIRST_NAME%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_LAST_NAME%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_TITLE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_OFFICE_ASSIGNMENTS%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_REGION_ASSIGNMENTS%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_OFFICE_ADMIN_PRIVILEGE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_REGION_ADMIN_PRIVILEGE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_EMAIL%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_PHONE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_WEBSITE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_LICENSE%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_LEGAL_DISCLAIMER%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_PHOTO%></th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;"
							class="hier-upload-td"><%=CommonConstants.CHR_USERS_ABOUT_ME_DESCRIPTION%></th>
					</tr>
				</thead>
				<tbody id="user-upload"></tbody>
			</table>
		</div>
	</div>
</div>

<script>
	$(document).ready(
			function() {
				hierarchyUpload.fileUpload();
				hierarchyUpload.hierarchyJson = {};
				hierarchyUpload.uploadType = "append";
				callAjaxPOSTWithTextDataUpload("./fetchUploadBatchStatus.do",
						hierarchyUpload.fetchUploadBatchStatusCallback, true,
						null);
			});
</script>