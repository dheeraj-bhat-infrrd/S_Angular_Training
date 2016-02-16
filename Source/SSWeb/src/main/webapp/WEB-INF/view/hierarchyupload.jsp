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
<%-- <div class="hm-header-main-wrapper clearfix">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="hm-header-row-left text-center lgn-adj">
				<spring:message code="label.hierarchyupload.key" />
			</div>
		</div>
	</div>
</div> --%>
<div class="clearfix">
	<div class="float-left rfr_lbl" style="margin-top:10px;">
		<spring:message code="label.xlsxfile.key" />
	</div>
	<div class="float-left rfr_txt" style="margin-top: 10px;">
		<div class="rfr_icn icn-logo"></div>
		<div class="icn-lname input-file-icn-left" id="input-file-icn-left"></div>
		<div class="rfr_txt_fld">
			<input type="text" class="rfr_input_fld" id="com-xlsx-file"
				name="XlsxFileName"
				placeholder='<spring:message code="label.xlsxfile.placeholder.key"/>'
				value="${XlsxFileName}">
		</div>
		<div>
			<input type="file" class="rfr_input_fld com-logo-comp-info"
				id="com-file" name="xlsxfile">
			<input type="hidden" id="fileUrl" name="fileUrl">
		</div>
		<div
			class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj"
			id="icn-xlsxfile"></div>
	</div>
	<div class="reg_form_row clearfix hierarchy-btn" >
		<div class="reg_btn" id="xlsx-file-verify">
			<spring:message code="label.verify.key" />
		</div>
	</div>
	<div id="hierarchy-upload" class="reg_form_row clearfix hierarchy-btn hide" >
		<div class="reg_btn" id="xlsx-file-upload">
			<spring:message code="label.upload.key" />
		</div>
	</div>
</div>

<div class="container hide" id="summary" style="margin-top: 10px;">

	<!-- Nav tabs -->
	<ul class="nav nav-tabs" role="tablist">
		<li class="active"><a href="#upload-summery-region"
			data-toggle="tab"> Regions </a></li>
		<li><a href="#upload-summery-branch" data-toggle="tab">
				Branches </a></li>
		<li><a href="#upload-summery-user" data-toggle="tab"> Users
		</a></li>

	</ul>

	<!-- Tab panes -->
	<div class="tab-content">
		<div class="tab-pane fade active in" id="upload-summery-region"
			style="overflow: auto; max-height: 500px;">
			<h2 style="color: #666;">Region Summary</h2>
			<div class="clearfix">
				<div id="region-summary" class="float-left"></div>
				<div id="region-errors" class="float-left"></div>
				<div id="region-warnings" class="float-left"></div>
			</div>
			<table class="table">
				<thead>
					<tr>
					<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Serial No.
							</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Region
							ID</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Region
							Name</th>
							
					</tr>
				</thead>
				<tbody id="region-upload"></tbody>
			</table>
		</div>
		<div class="tab-pane fade" id="upload-summery-branch"
			style="overflow: auto; max-height: 500px;">
			<h2 style="color: #666;">Branch Summary</h2>
			<div class="clearfix">
				<div id="branch-summary" class="float-left"></div>
				<div id="branch-errors" class="float-left"></div>
				<div id="branch-warnings" class="float-left"></div>
			</div>
			<table class="table">
				<thead>
					<tr>
					<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Serial No.
							</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Branch
							ID</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Branch
							Name</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Region
							ID</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Address
							1</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Address
							2</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">City</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">State</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Zip</th>
					</tr>
				</thead>
				<tbody id="branch-upload"></tbody>
			</table>
		</div>
		<div class="tab-pane fade" id="upload-summery-user"
			style="overflow: auto; max-height: 500px;">
			<h2 style="color: #666;">User Summary</h2>
			<div class="clearfix">
				<div id="user-summary" class="float-left"></div>
				<div id="user-errors" class="float-left"></div>
				<div id="user-warnings" class="float-left"></div>
			</div>
			<table class="table">
				<thead>
					<tr>
					<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Serial No.
							</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">User
							ID</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">First
							Name</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Last
							Name</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Title</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Office
							Assignment(s)</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Region
							Assignment(s)</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Office
							Admin Privilege</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Email</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Phone</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">Website</th>
						<th
							style="text-align: center; font-weight: 600 !important; font-size: 14px;">License</th>
					</tr>
				</thead>
				<tbody id="user-upload"></tbody>
			</table>
		</div>
	</div>

 <div id="hierarchy-upload" class="reg_form_row clearfix hierarchy-btn" style="float:none !important;">
		<div class="reg_btn" id="xlsx-file-upload">
			<spring:message code="label.upload.key" />
		</div>
	</div>
</div>






<script>
	$(document).ready(function() {
		hierarchyUpload.fileUpload();
	});
</script>