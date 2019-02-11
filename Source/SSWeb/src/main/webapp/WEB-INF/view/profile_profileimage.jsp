<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty profileSettings && not empty profileSettings.profileImageUrlThumbnail}">
	<c:set value="${profileSettings.profileImageUrlThumbnail}" var="profileimage"></c:set>
</c:if>
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

<c:choose>
	<c:when test="${profilemasterid == 1}">
		<c:set value="comp-default-img" var="defaultprofimageclass"></c:set>
	</c:when>
	<c:when test="${profilemasterid == 2}">
		<c:set value="region-default-img" var="defaultprofimageclass"></c:set>
	</c:when>
	<c:when test="${profilemasterid == 3}">
		<c:set value="office-default-img" var="defaultprofimageclass"></c:set>
	</c:when>
	<c:when test="${profilemasterid == 4}">
		<c:set value="pers-default-big" var="defaultprofimageclass"></c:set>
	</c:when>
</c:choose>

<input type="text" id="default-prof-image" class="hidden" value="${defaultprofimageclass}">
<c:choose>
	<c:when test="${not empty profileimage}">
	<div class="prof-img-height padding-top-one-third">
		<img id="prof-image-edit" class="prof-image prof-image-edit height-auto pos-relative" src="${profileimage}"  onload="checkProfImageForProfileEdit(this);" style="height: 195px;clear: both;"></img>
	</div>
	</c:when>
	<c:otherwise>
		<div id="prof-image-edit" class="prof-image prof-image-edit ${defaultprofimageclass} pos-relative" style="clear: both;"></div>
	</c:otherwise>
</c:choose>

<div id="prof-img-edit-cont" style="width: 70px; margin: 0 auto;">
	<div class="edit-prof-img-icn float-left">
	</div>
	<div class="prof-img-del-icn float-left">
	</div>
</div>

<div class="prof-rating-mobile-wrapper hide">
	<div class="st-rating-wrapper maring-0 clearfix"></div>
</div>

<input type="file" id="prof-image" class="hidden">

<script>
$(document).ready(function(){
	$(document).off('click', '.edit-prof-img-icn');
	$('.edit-prof-img-icn').off('click');
	$(document).on('click', '.edit-prof-img-icn', function(e){
		e.stopPropagation();
		$("#prof-image").trigger('click');
	})

	$(document).off('click', '.prof-img-del-icn');
	$('.prof-img-del-icn').off('click');
	$(document).on('click', '.prof-img-del-icn', function(e){
		e.stopPropagation();

		callAjaxPOST("./removeprofileimage.do", function(data){
			hideOverlay();
			
			callAjaxGET("./fetchprofileimage.do", function(data) {
				$('#prof-img-container').html(data);
				hideOverlay();
			}, true);
			
			$('#prof-message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, true);
	})
})

function checkProfImageForProfileEdit(ele){
	checkImgForProfile(ele);
}
</script>
