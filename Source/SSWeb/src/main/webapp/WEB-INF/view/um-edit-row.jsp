<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="v-um-edit-wrapper clearfix">
	<div class="v-edit-lft col-lg-6 col-md-6 col-sm-6 col-xs-12">
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">User Name</div>
			<div class="float-left v-ed-txt-sm">
				<input class="v-ed-txt-item" placeholder="First Name" value="${firstName}">
			</div>
			<div class="float-left v-ed-txt-sm v-ed-txt-sm-adj">
				<input class="v-ed-txt-item" placeholder="Last Name" value="${lastName}">
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">Email Address</div>
			<div class="float-left v-ed-txt">
				<input class="v-ed-txt-item" placeholder="Email Address" value="${emailId}">
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">Assign To</div>
			<div class="float-left v-ed-txt pos-relative">
				<input class="v-ed-txt-item v-ed-txt-dd" placeholder="Email Address">
				<div class="clearfix hide v-ed-dd-wrapper">
					<div class="clearfix v-ed-dd-item">One</div>
					<div class="clearfix v-ed-dd-item">Two</div>
					<div class="clearfix v-ed-dd-item">Three</div>
				</div>
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">Select Office</div>
			<div class="float-left v-ed-txt pos-relative">
				<input class="v-ed-txt-item v-ed-txt-dd" placeholder="">
				<div class="clearfix hide v-ed-dd-wrapper">
					<div class="clearfix v-ed-dd-item">One</div>
					<div class="clearfix v-ed-dd-item">Two</div>
					<div class="clearfix v-ed-dd-item">Three</div>
				</div>
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">Select Role</div>
			<div class="float-left v-ed-txt pos-relative">
				<input class="v-ed-txt-item v-ed-txt-dd" placeholder="">
				<div class="clearfix hide v-ed-dd-wrapper">
					<div class="clearfix v-ed-dd-item">One</div>
					<div class="clearfix v-ed-dd-item">Two</div>
					<div class="clearfix v-ed-dd-item">Three</div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="v-edit-rt col-lg-6 col-md-6 col-sm-6 col-xs-12">
		<div class="v-edt-tbl-wrapper">
			<table class="v-edt-tbl">
				<tr class="v-edt-tbl-header">
					<td class="v-edt-tbl-assign-to"><spring:message code="label.assignedto.key" /></td>
					<td class="v-edt-tbl-role"><spring:message code="label.role.key" /></td>
					<td class="v-edt-tbl-status"><spring:message code="label.status.key" /></td>
					<!-- <td class="v-edt-tbl-rem"></td> -->
				</tr>
				
				<c:forEach var="profile" items="${profiles}">
					<tr class="v-edt-tbl-row">
						<td class="v-edt-tbl-assign-to">${profile.entityName}</td>
						<td class="v-edt-tbl-role">${profile.role}</td>
						<c:choose>
							<c:when test="${profile.status == 1}">
								<c:set var="profilestatusclass" value="tbl-switch-on"/>
								<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-on" title="<spring:message code="label.active.key" />"></td>
							</c:when>
							<c:otherwise>
								<c:set var="profilestatusclass" value="tbl-switch-off"/>
								<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-off" title="<spring:message code="label.inactive.key" />"></td>
							</c:otherwise>
						</c:choose>
						<!-- <td class="v-edt-tbl-rem v-edt-tbl-icn v-icn-rem-user"></td> -->
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>
	<div class="v-edt-btn-sav"><spring:message code="label.savechanges.key" /></div>
</div>