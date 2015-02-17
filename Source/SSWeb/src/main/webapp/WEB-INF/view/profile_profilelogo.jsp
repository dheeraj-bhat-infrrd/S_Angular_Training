<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty profile && not empty profile.logo}">
	<c:set value="${profile.logo}" var="profilelogo"></c:set>
</c:if>
					<c:choose>
						<c:when test="${not empty profilelogo}">
							<div id="prof-logo-edit" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background: url(${profilelogo}) no-repeat center;"></div>
							<c:choose>
								<c:when	test="${lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="locked" class="prof-img-lock-item prof-img-lock prof-img-lock-locked"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file" disabled>
									</form>
								</c:when>
								<c:when	test="${not lock.isLogoLocked && not user.agent}">
									<div id="prof-logo-lock" data-state="unlocked" class="prof-img-lock-item prof-img-lock"></div>
									<form class="form_contact_image" enctype="multipart/form-data">
										<input type="file" id="prof-logo" class="con_img_inp_file">
									</form>
								</c:when>
							</c:choose>
						</c:when>
						<c:otherwise>
							<div id="prof-logo" class="prof-image-rp prof-image-edit pos-relative cursor-pointer" style="background-image:initial; background: no-repeat center;"></div>
						</c:otherwise>
					</c:choose>
