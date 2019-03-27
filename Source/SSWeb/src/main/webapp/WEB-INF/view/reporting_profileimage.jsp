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
<input type="file" id="rep-prof-image" class="hidden">

<c:choose>
	<c:when test="${not empty profileimage}">
		<img id="prof-image-edit" class="prof-image prof-image-edit pos-relative cursor-pointer rep-prof-pic" src="${profileimage}" onload="checkProfImage(this);"></img>
	</c:when>
	<c:otherwise>
		<div id="prof-image-edit" class="prof-image prof-image-edit ${defaultprofimageclass} pos-relative cursor-pointer rep-prof-pic"></div>
	</c:otherwise>
</c:choose>

<div class="rep-prof-pic-overlay">
    <img class="rep-prof-pic-edit-icon" src="${initParam.resourcesPath}/resources/images/edit-1.png">
    <img class="rep-prof-pic-del-icon" src="${initParam.resourcesPath}/resources/images/delete.png">
</div>

<script src="${initParam.resourcesPath}/resources/js/modernizr-custom.js"></script>
<script>
$(document).ready(function(){
	$(document).off('click','.rep-prof-pic-edit-icon');
	$('.rep-prof-pic-edit-icon').off('click');
	$(document).on('click', '.rep-prof-pic-edit-icon', function(e){
		e.stopPropagation();
		$("#rep-prof-image").trigger('click');
	})

	$(document).off('click','.rep-prof-pic-del-icon');
	$('.rep-prof-pic-del-icon').off('click');
	$(document).on('click', '.rep-prof-pic-del-icon', function(e){
		e.stopPropagation();

		callAjaxPOST("./removeprofileimage.do", function(data){
			hideOverlay();
			
			callAjaxGET("./fetchprofileimagefornewdashboard.do", function(data) {
				hideOverlay();
				$('.rep-prof-pic-circle').html(data);
			}, true);
			
			$('#prof-message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, true);
	})
});

function checkProfImage(ele){
	checkImgForProfile(ele);
}
</script>