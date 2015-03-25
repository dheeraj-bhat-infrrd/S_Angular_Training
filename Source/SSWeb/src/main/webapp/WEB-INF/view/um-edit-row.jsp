<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountType" value="${user.company.licenseDetails[0].accountsMaster.accountName}" />
<div class="v-um-edit-wrapper clearfix">
	<div class="v-edit-lft col-lg-6 col-md-6 col-sm-6 col-xs-12">
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">User Name</div>
			<div class="float-left v-ed-txt-sm">
				<input class="v-ed-txt-item" placeholder="First Name" value="${firstName}" readonly>
			</div>
			<div class="float-left v-ed-txt-sm v-ed-txt-sm-adj">
				<input class="v-ed-txt-item" placeholder="Last Name" value="${lastName}" readonly>
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl">Email Address</div>
			<div class="float-left v-ed-txt">
				<input class="v-ed-txt-item" placeholder="Email Address" value="${emailId}" readonly>
			</div>
		</div>
		<div class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl"><spring:message code="label.assignto.key"/></div>
			<div id="assign-to-selector" class="float-left v-ed-txt pos-relative">
				<c:choose>
				    <c:when test="${accountType == 'Enterprise'}">
						<input id="assign-to-txt" data-assignto="office" class="v-ed-txt-item v-ed-txt-dd ignore-clear" value='<spring:message code="label.office.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
								<div data-assign-to-option="office" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.office.key"/></div>
							<c:if test="${highestrole == 1 || highestrole == 2}">
								<div data-assign-to-option="region" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.region.key"/></div>
							</c:if>
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${accountType == 'Company'}">
						<input id="assign-to-txt" data-assignto="office" class="v-ed-txt-item v-ed-txt-dd ignore-clear" value='<spring:message code="label.office.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
								<div data-assign-to-option="office" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.office.key"/></div>
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.company.key"/></div>
							</c:if>
						</div>
					</c:when>
					<c:when test="${accountType == 'Team'}">
						<input id="assign-to-txt" data-assignto="company" class="v-ed-txt-item v-ed-txt-dd ignore-clear" value='<spring:message code="label.team.key"/>'>
						<div id="assign-to-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
							<c:if test="${highestrole == 1}">
								<div data-assign-to-option="company" class="clearfix v-ed-dd-item hm-dd-hover hm-assignto-options"><spring:message code="label.team.key"/></div>
							</c:if>
						</div>
					</c:when>
				</c:choose>
			</div>
		</div>
		<div id="bd-region-selector" class="v-edit-row clearfix hide">
			<div class="float-left v-ed-lbl"><spring:message code="label.selectregion.key"/></div>
			<div class="float-left v-ed-txt pos-relative" id="region-selector">
				<input id="selected-region-txt" class="v-ed-txt-item v-ed-txt-dd" placeholder='<spring:message code="label.regionselector.placeholder.key"/>'/>
				<input type="hidden" name="regionId" id="selected-region-id-hidden"/>
				<div id="regions-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
					<!-- regions list get populated here -->
				</div>
			</div>
		</div>
		<div id="bd-office-selector" class="v-edit-row clearfix">
			<div class="float-left v-ed-lbl"><spring:message code="label.selectoffice.key"/></div>
			<div class="float-left v-ed-txt pos-relative" id="office-selector">
				<input id="selected-office-txt" class="v-ed-txt-item v-ed-txt-dd" placeholder='<spring:message code="label.officeselector.placeholder.key"/>'/>
				<input type="hidden" name="officeId" id="selected-office-id-hidden"/>
				<div id="offices-droplist" class="clearfix hide v-ed-dd-wrapper dd-droplist">
					<!-- offices list get populated here -->
				</div>
			</div>
		</div>
		<div class="v-edit-row clearfix" id="admin-privilege-div">
			<div class="float-left v-ed-lbl"></div>
			<div class="float-left v-ed-txt pos-relative">
				<div class="bd-frm-check-wrapper clearfix">
					<div class="float-left bd-check-img"></div>
					<input type="hidden" name="isAdmin" value="true" id="is-admin-chk">
	            	<div class="float-left bd-check-txt"><spring:message code="label.grantadminprivileges.key"/></div>
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
				
				<c:choose>
					<c:when test="${not empty profiles}">
						<c:forEach var="profile" items="${profiles}">
							<tr class="v-edt-tbl-row" id="v-edt-tbl-row-${profile.profileId}" data-profile-id="${profile.profileId}">
								<td class="v-edt-tbl-assign-to">${profile.entityName}</td>
								<td class="v-edt-tbl-role">${profile.role}</td>
								<c:choose>
									<c:when test="${profile.status == 1}">
										<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-on" title="<spring:message code="label.active.key" />"></td>
									</c:when>
									<c:otherwise>
										<td class="v-edt-tbl-status v-edt-tbl-icn v-edt-tbl-switch tbl-switch-off" title="<spring:message code="label.inactive.key" />"></td>
									</c:otherwise>
								</c:choose>
								<!-- <td class="v-edt-tbl-rem v-edt-tbl-icn v-icn-rem-user"></td> -->
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<tr class="v-edt-tbl-row"><spring:message code="label.nouserprofilesfound.key" /></tr>
					</c:otherwise>
				</c:choose>
			</table>
		</div>
	</div>
	<div class="v-edt-btn-sav"><spring:message code="label.savechanges.key" /></div>
</div>