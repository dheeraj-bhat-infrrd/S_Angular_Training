<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>


<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>
<div class="hm-header-main-wrapper hm-hdr-bord-bot">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left hr-dsh-adj-lft"><spring:message code="label.usermanagement.header.key" /></div>
					<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./showaddsocialsurveyadmin.do');">Add Social Survey Admin</div>
			
		</div>
	</div>
</div>
&nbsp;

<div class="container v-hr-container">
	<form id="add-ss-admin-form">
		<div class="reg_form_wrapper_2">
			<div class="reg_form_row clearfix">
				<div class="float-left rfr_lbl"><spring:message code="label.adminname.key" /></div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-name"></div>
					<div class="rfr_txt_fld">
						<input class="rfr_input_fld" id="add-ss-admin-fname" data-non-empty="true"
							name="firstName" placeholder='<spring:message code="label.firstname.key"/>'>
					</div>
				</div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-name"></div>
					<div class="rfr_txt_fld">
						<input class="rfr_input_fld" data-non-empty="true" id="add-ss-admin-lname"
							name="lastName" placeholder='<spring:message code="label.lastname.key"/>'>
					</div>
				</div>
			</div>
			<div class="reg_form_row clearfix">
				<div class="float-left rfr_lbl"><spring:message code="label.adminemail.key" /></div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-email"></div>
					<div class="rfr_txt_fld">
						<input type="email" class="rfr_input_fld" data-non-empty="true" id="add-ss-admin-emailid"
							name="emailId" placeholder='<spring:message code="label.adminemail.key"/>'>
					</div>
				</div>
			</div>
			<div class="bd-hr-form-item clearfix">
					<div class="float-left bd-frm-left"></div>
				<div class="float-left hm-header-right text-center" id="add-ss-admin-form-submit"><spring:message code="label.create.key" /></div>
				<div class="float-left hm-header-right text-center" id="cancel-ss-admin-form-submit"  onclick="javascript:showMainContent('./adminusermanagement.do')"><spring:message code="label.cancel.key" /></div>				
			</div>
			
		</div>
	</form>
</div>
<div id="temp-message" class="hide"></div>