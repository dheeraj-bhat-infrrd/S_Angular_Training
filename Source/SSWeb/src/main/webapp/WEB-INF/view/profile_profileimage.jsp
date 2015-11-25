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
	<c:when test="${not empty profileimage}">
		<img id="prof-image-edit" class="prof-image prof-image-edit pos-relative cursor-pointer" src="${profileimage}"></img>
	</c:when>
	<c:otherwise>
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
		<div id="prof-image-edit" class="prof-image prof-image-edit ${defaultprofimageclass} pos-relative cursor-pointer"></div>
	</c:otherwise>
</c:choose>
<form class="form_contact_image" enctype="multipart/form-data">
	<input type='file' id="prof-image" class="con_img_inp_file" />
</form>
<div class="prof-rating-mobile-wrapper hide">
	<div class="st-rating-wrapper maring-0 clearfix"></div>
</div>