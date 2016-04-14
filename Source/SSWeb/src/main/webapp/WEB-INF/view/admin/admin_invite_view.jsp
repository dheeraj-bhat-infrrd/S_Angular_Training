<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div id="hm-header-main-wrapper" class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<spring:message code="label.sendinvite.header.key" />
			</div>
		</div>
	</div>
</div>
<div id="server-message" class="hide">
	<jsp:include page="../messageheader.jsp"></jsp:include>
</div>

<div class="container v-hr-container">
	<form id="send-invite-form">
		<div class="reg_form_wrapper_2">
			<div class="reg_form_row clearfix">
				<div class="float-left rfr_lbl"><spring:message code="label.customername.key" /></div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-name"></div>
					<div class="rfr_txt_fld">
						<input class="rfr_input_fld" id="user-fnmae" data-non-empty="true"
							name="firstName" value="${city}" placeholder='<spring:message code="label.firstname.key"/>'>
					</div>
				</div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-name"></div>
					<div class="rfr_txt_fld">
						<input class="rfr_input_fld" id="user-lname" data-non-empty="true"
							name="lastName" placeholder='<spring:message code="label.lastname.key"/>'>
					</div>
				</div>
			</div>
			<div class="reg_form_row clearfix">
				<div class="float-left rfr_lbl"><spring:message code="label.customeremail.key" /></div>
				<div class="float-left rfr_txt">
					<div class="rfr_icn icn-email"></div>
					<div class="rfr_txt_fld">
						<input type="email" class="rfr_input_fld" id="user-email" data-non-empty="true"
							name="emailId" placeholder='<spring:message code="label.customeremail.key"/>'>
					</div>
				</div>
			</div>
			<div class="reg_form_row clearfix">
				<div class="reg_btn" id="send-invite-form-submit"><spring:message code="label.done.key" /></div>
			</div>
		</div>
	</form>
</div>
<div id="temp-message" class="hide"></div>