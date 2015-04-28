<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.logo}" var="profilelogo"></c:set>
	<c:set value="${profileSettings.profileImageUrl}" var="profileimage"></c:set>
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.linkedInProfileData}" var="linkedInData"></c:set>
</c:if>

<div class="welcome-popup-hdr-wrapper clearfix">
	<div class="float-left wc-hdr-txt"><spring:message code="label.aboutbusiness.key" /></div>
	<div class="float-right wc-hdr-step"><spring:message code="label.step.two.key" /></div>
</div>
<div class="welcome-popup-body-wrapper clearfix">
	<div class="wc-popup-body-hdr"><spring:message code="label.linkedin.imported.key" />
		<div class="float-right linkedin-import-hdr"></div>
	</div>
	<div class="wc-popup-body-cont">
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr"><spring:message code="label.photo.upload.key" /></div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-edit-photo-cont clearfix">
					<div class="wc-edit-photo-cont-col float-left">
						<div class="float-right">
							<div class="wc-linkedin-photo" style="background: url(${linkedInData.pictureUrls.values[0]}) no-repeat center; background-size: contain"></div>
							<div class="wc-linkedin-photo-txt">
								<spring:message code="label.photo.from.key" />
								<span class="wc-highlight"><spring:message code="label.linkedin.key" /></span>
							</div>
						</div>
					</div>
					<div class="wc-div-txt float-left"><spring:message code="label.or.key" /></div>
					<div class="wc-edit-photo-cont-col float-left">
						<div class="float-left">
							<c:choose>
								<c:when test="${not empty profileimage}">
									<div id="wc-photo-upload" class="wc-photo-upload cursor-pointer" style="background: url(${profileimage}) no-repeat center; background-size: contain"></div>
								</c:when>
								<c:otherwise>
									<div id="wc-photo-upload" class="wc-photo-upload cursor-pointer"></div>						
								</c:otherwise>
							</c:choose>
							<form class="hide" enctype="multipart/form-data">
								<input type='file' id="prof-image" />
							</form>
							<div id="prof-image-upload-btn" class="wc-submit-btn"><spring:message code="label.upload.key" /></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr">Business Name, Address and Logo</div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-form-container">
					<div class="wc-form-row clearfix">
						<div class="float-left wc-form-txt">Logo</div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" placeholder='<spring:message code="label.logo.placeholder.key"/>'>
							<input type="file" class="rfr_txt_fld com-logo-comp-info" id="com-logo" name="logo">
							<div class="float-right input-icon-internal icn-file file-pick-logo file-pick-logo-adj"></div>
						</div>
					</div>
					<div class="wc-form-row clearfix">
						<div class="float-left wc-form-txt">Address</div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" id="com-address1" data-non-empty="true" name="address1"
								value="${contactdetail.address1}" placeholder='<spring:message code="label.address1.key"/>'>
						</div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" id="com-address2" name="address2"
								value="${contactdetail.address2}" placeholder='<spring:message code="label.address2.key"/>'>
						</div>
					</div>
					<div class="wc-form-row clearfix">
						<div class="float-left wc-form-txt"></div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" id="com-country" data-non-empty="true" name="country"
								value="${contactdetail.country}" placeholder='<spring:message code="label.country.key"/>'>
						</div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" id="com-zipcode" data-non-empty="true" data-zipcode="true" name="zipcode"
								value="${contactdetail.zipcode}" placeholder='<spring:message code="label.zipcode.key"/>'>
						</div>
					</div>
					<div class="wc-form-row clearfix">
						<div class="float-left wc-form-txt">Phone No</div>
						<div class="float-left wc-form-input-cont">
							<input class="wc-form-input" id="com-contactno" data-non-empty="true" data-phone="true" name="contactno"
								value="${contactdetail.contact_numbers.work}" placeholder="<spring:message code="label.phoneno.key" />">
						</div>
					</div>
					<div class="wc-form-row clearfix">
						<div class="reg_btn">Update</div>
					</div>
				</div>
			</div>
		</div>
		<div class="wc-step2-body-row">
			<div class="wc-step2-body-row-hdr">Tell us about yourself</div>
			<div class="wc-step2-body-row-cont">
				<div class="wc-prof-details">
					<div class="wc-prof-hdr">About Scott Haris</div>
					<div class="wc-prof-details-row clearfix">
						<div class="wc-prof-input-cont float-left">
							<input class="wc-form-input" value="${linkedInData.industry}">
						</div>
						<div class="wc-prof-input-cont float-left">
							<input class="wc-form-input" value="${linkedInData.location.name}">
						</div>
						<div class="wc-linkedin-photo-txt float-right">Data imported from <span class="wc-highlight">Linkedin</span></div>
					</div>
					<div class="wc-prof-details-row clearfix">
						<textarea class="wc-about-prof-txt">${linkedInData.summary}</textarea>
					</div>
					
					<div class="wc-prof-details-row clearfix">
						<div class="wc-submit-btn float-right">Update</div>
					</div>
					
				</div>
			</div>
		</div>
	</div>
</div>
<div class="wc-btn-row clearfix" data-page="two">
	<div class="wc-btn-col float-left">
		<div class="wc-skip-btn float-right"><spring:message code="label.skipthisstep.key" /></div>
	</div>
	<div class="wc-btn-col float-left">
		<div class="wc-sub-btn float-left wc-next-btn"><spring:message code="label.nextstep.key" /></div>
	</div>
</div>

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.css">

<script src="${pageContext.request.contextPath}/resources/js/jcrop.js"></script>
<script src="${pageContext.request.contextPath}/resources/jcrop/jquery.Jcrop.min.js"></script>
<script>
$(document).on('click', '#wc-photo-upload', function() {
	$('#prof-image').trigger('click');
});

$(document).on('change', '#prof-image', function() {
	initiateJcrop(this);
});

function callBackOnProfileImageUpload(data) {
	callAjaxGET("./fetchuploadedprofileimage.do", function(profileImageUrl) {
		$('#wc-photo-upload').css("background", "url(" + profileImageUrl + ") no-repeat center");
		$('#wc-photo-upload').css("background-size","contain");

		hideOverlay();
	});

	$('#overlay-toast').html($('#display-msg-div').text().trim());
	showToast();
}
</script>