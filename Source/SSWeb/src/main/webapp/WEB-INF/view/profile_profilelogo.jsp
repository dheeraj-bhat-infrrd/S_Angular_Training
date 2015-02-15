<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
	<c:when test="${not empty displaylogo}">
		<div id="prof-logo" class="prof-image prof-image-edit pos-relative cursor-pointer" style="background: url(${displaylogo}) no-repeat center;">
			<div class="prof-download-contact clearfix cursor-pointer">
				<div id="prof-logo-upload" class="clearfix prof-dowload-width">
					<div class="float-left inc-dl"></div>
					<div class="float-left txt-download">
						<spring:message code="label.uploadlogo.key"></spring:message>
					</div>
				</div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<div id="prof-logo" class="prof-image prof-image-edit pos-relative cursor-pointer"	style="background-image:initial; background: no-repeat center;">
			<div class="prof-download-contact clearfix cursor-pointer">
				<div id="prof-logo-upload" class="clearfix prof-dowload-width">
					<div class="float-left inc-dl"></div>
					<div class="float-left txt-download">
						<spring:message code="label.uploadlogo.key"></spring:message>
					</div>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>
<form enctype="multipart/form-data">
	<input type="file" id="prof-logo-edit" style="display: block;">
</form>