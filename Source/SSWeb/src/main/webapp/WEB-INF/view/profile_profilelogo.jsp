<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:set value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" var="accountMasterId"/>
<c:if test="${not empty profileSettings && not empty profileSettings.logo}">
	<c:set value="${profileSettings.logo}" var="profilelogo"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
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
			<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && profilemasterid != 4}">
				<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock"></div>
				<form class="form_contact_image" enctype="multipart/form-data">
					<input type="file" id="prof-logo" class="con_img_inp_file">
				</form>
			</c:when>
		</c:choose>
	</c:when>
	<c:otherwise>
		<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer"
			style="background-image:initial; 50% 50% no-repeat; background: no-repeat center; background-size: cover;"></div>
		<form class="form_contact_image" enctype="multipart/form-data">
			<input type="file" id="prof-logo" class="con_img_inp_file">
		</form>
	</c:otherwise>
</c:choose>