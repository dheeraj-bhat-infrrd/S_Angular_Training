<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>
<c:if test="${not empty profileSettings && not empty profileSettings.logo}">
	<c:set value="${profileSettings.logo}" var="profilelogo"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>

<%-- <c:if test="${accountMasterId != 1 && profilemasterid == 4 && not lock.isLogoLocked}">
	<c:set value="${profileSettings.companyProfileData.companyLogo}" var="profilelogo"></c:set>
</c:if> --%>

<c:choose>
	<c:when test="${not empty profilelogo}">
		<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer"
			style="background: url(${profilelogo}) no-repeat center; 50% 50% no-repeat; background-size: contain;"></div>
		<c:choose>
			<c:when test="${accountMasterId == 1 || accountMasterId == 5}">
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when	test="${parentLock.isLogoLocked && profilemasterid != 4}">
				<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
				</form>
			</c:when>
			<c:when	test="${parentLock.isLogoLocked && profilemasterid == 4}">
				<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
				</form>
			</c:when>
			<c:when	test="${not parentLock.isLogoLocked && profilemasterid == 4}">
				<div id="prof-logo-lock" data-state="unlocked" data-control="user" class=""></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when	test="${not parentLock.isLogoLocked && lock.isLogoLocked && profilemasterid != 4}">
				<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && profilemasterid != 4 && isLogoSetByEntity}">
				<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && profilemasterid != 4 && not isLogoSetByEntity}">
				<div id="prof-logo-lock" data-state="unlocked" data-control="user" class=""></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div id="prof-logo-edit" class="prof-logo-edit prof-image-rp prof-image-edit pos-relative cursor-pointer"></div>
		<c:choose>
			<c:when test="${accountMasterId == 1 || accountMasterId == 5}">
				<div id="prof-logo-lock" data-state="locked" data-control="user" class="prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when test="${accountMasterId == 4 && profilemasterid == 1}">
				<div id="prof-logo-lock" data-state="locked" data-control="user" class="prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
			<c:when test="${accountMasterId == 4 && profilemasterid != 1}">
				<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-locked"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
				</form>
			</c:when>
		</c:choose>
	</c:otherwise>
</c:choose>