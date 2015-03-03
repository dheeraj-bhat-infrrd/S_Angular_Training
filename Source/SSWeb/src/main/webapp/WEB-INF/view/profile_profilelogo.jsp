<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile && not empty profile.logo}">
	<c:set value="${profile.logo}" var="profilelogo"></c:set>
	<c:set value="${profile.lockSettings}" var="lock"></c:set>
</c:if>
					<c:choose>
						<c:when test="${not empty profilelogo}">
							<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background: url(${profilelogo}) center; 50% 50% no-repeat; background-size: cover;"></div>
							<c:choose>
								<c:when	test="${parentLock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${parentLock.isLogoLocked && user.agent}">
									<div id="prof-logo-lock" data-state="locked" data-control="parent" class="hide prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="hide prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
								<c:when	test="${not parentLock.isLogoLocked && not lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" data-control="user" class="prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<div id="prof-logo" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background-image:initial; 50% 50% no-repeat; background: no-repeat center; background-size: cover;"></div>
							<form class="form_contact_image" enctype="multipart/form-data">
								<input type="file" id="prof-logo" class="con_img_inp_file">
							</form>
						</c:otherwise>
					</c:choose>
