2<%@ page language="java" contentType="text/html; charset=UTF-8"
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
<div id="json-response" class="hierarchy-table clearfix"></div>

<script>
	$(document).ready(function() {
		hierarchyUpload.fileUpload();
	});
</script>