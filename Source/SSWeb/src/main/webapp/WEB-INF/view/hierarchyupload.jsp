<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div class="hm-header-main-wrapper clearfix">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="hm-header-row-left text-center lgn-adj">
				<spring:message code="label.hierarchyupload.key" />
			</div>
		</div>
	</div>
</div>
<div class="clearfix">
	<div class="float-left rfr_lbl">
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
	<div class="reg_form_row clearfix hierarchy-btn" >
		<div class="reg_btn" id="xlsx-file-upload">
			<spring:message code="label.upload.key" />
		</div>
	</div>
</div>
<table id="json-response"></table>

<script>
	$(document).ready(function() {
						var set = false;
						$('#com-file').change(
								function() {
									var fileAdd = $(this).val().split('\\');
									$('#com-xlsx-file').val(
											fileAdd[fileAdd.length - 1]);
								});

						/* $('#xlsx-file-upload').click(
								function() {
									if (fileValidate("#com-file")) {
										set = true;
									}
									if (set == true) {
										var formData = new FormData();
										formData.append("logo", $('#com-file')
												.prop("files")[0]);
										formData
												.append("logo_name", $(
														'#com-file').prop(
														"files")[0].name);
										callAjaxPOSTWithTextDataLogo("./uploadxlxsfile.do",
												uploadXlxsSuccessCallback, true, formData); 
									}
									
									
								}); */
								$('#xlsx-file-verify').click(
										function() {
											if (fileValidate("#com-file")) {
												set = true;
											}
											if (set == true) {
												showInfo("Valid File Format");
											}
											
											
										});
								$('#xlsx-file-upload').click(
										function() {
											if (set == true) {
												var formData = new FormData();
												formData.append("logo", $('#com-file')
														.prop("files")[0]);
												formData
														.append("logo_name", $(
																'#com-file').prop(
																"files")[0].name);
												callAjaxPOSTWithTextDataLogo("./uploadxlxsfile.do",
														uploadXlxsSuccessCallback, true, formData); 
											}
											set=false;
											
										});

						function uploadXlxsSuccessCallback(response) {
							
							$.each($.parseJSON(response), function(key, value) {
								  console.log(key+ ':' + value);
								});
							
							
							if (!response) {
								$('#com-file').val('');
								$('#com-xlsx-file').val('');
								showError(response);
							} else {
								$.each($.parseJSON(response), function(key, value) {
									 $('<tr><td> '+key+':</td><td id="'+key+'">'+value+'</td><tr>').appendTo('#json-response');
									
									});
							
								}
							
						}
						$('#icn-xlsxfile').click(function() {
							$('#com-file').trigger('click');
						});

						/* $("#com-file").on("change", function() {
							if(fileValidate("#com-file")){
							 	set=true;
							}
							if(set==true){
						var formData = new FormData();
								formData.append("logo", $('#com-file').prop("files")[0]);
								formData.append("logo_name", $('#com-file').prop("files")[0].name);
							}
						}); */
						function fileValidate(fileformat) {
							var fileExtensions = ".xlsx";
							if ($(fileformat).attr("type") == "file") {
								var FileName = $(fileformat).val();
								if (FileName.length > 0) {
									var blnValid = false;
									if (FileName.substr(
											FileName.length
													- fileExtensions.length,
											fileExtensions.length)
											.toLowerCase() == fileExtensions
											.toLowerCase()) {
										blnValid = true;
									}
								}
								if (!blnValid) {
									var msg = "Please upload xlsx file";
									showErrorMobileAndWeb(msg);
									$(fileformat).val = "";

									return false;
								}
							}
							return true;
						}
					});
</script>