
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

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

<c:set
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}"
	var="user" />
<c:if test="${appSettings != null && appSettings.crm_info != null}">
	<input type="hidden" id="crm-source" value="${appSettings.crm_info.crm_source}"/>
</c:if>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left"><spring:message code="label.title.appsettings.key" /></div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>
<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<!-- Select which CRM jsp to include -->
		<c:if test="${not empty crmMappings }">
			<div class="st-crm-container">
				<div class="um-header crm-setting-hdr crm-settings-dropdown">
					<span id="crm-settings-dropdown-sel-text">${crmMappings[0].crmMaster.crmName }</span>
					Settings
				</div>
				<div id="crm-settings-dropdown-cont" class="hide crm-settings-dropdown-cont va-dd-wrapper">
					<c:forEach items="${crmMappings}" var="mapping">
						<c:choose>
							<c:when
								test="${mapping.crmMaster.crmName == 'Encompass' && profilemasterid != 1}">
									<%-- Skip if crm mapping encompass and not company admin --%>
								</c:when>
							<c:otherwise>
								<div class="crm-settings-dropdown-item"
									data-crm-type="${mapping.crmMaster.crmName }">${mapping.crmMaster.crmName }</div>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</div>
				<c:forEach items="${crmMappings }" var="mapping" varStatus="loop">
					<c:choose>
						<c:when test="${mapping.crmMaster.crmName == 'Encompass' && profilemasterid == 1}">
							<div class="crm-setting-cont hide"
								data-crm-type="${mapping.crmMaster.crmName }">
									<jsp:include page="encompass.jsp"></jsp:include>
							</div>
						</c:when>
						<c:when test="${mapping.crmMaster.crmName == 'Dotloop'}">
							<div class="crm-setting-cont hide"
								data-crm-type="${mapping.crmMaster.crmName }">
								<jsp:include page="dotloop.jsp"></jsp:include>
							</div>
						</c:when>
					</c:choose>
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>
<script>
	$(document).ready(function() {
		hideOverlay();
		$(document).attr("title", "Apps");

		$('.va-dd-wrapper').perfectScrollbar({
			suppressScrollX : true
		});
		$('.va-dd-wrapper').perfectScrollbar('update');
		
		if ($("#da-dd-wrapper-profiles").children('.da-dd-item').length <= 1) {
			$('#da-dd-wrapper').remove();
		} else {
			$('#da-dd-wrapper').show();
		}

		//Remove the dropdown icon if only one option for app available
		if($('#crm-settings-dropdown-cont').children('.crm-settings-dropdown-item').length <= 1) {
			$('.crm-setting-hdr').removeClass('crm-settings-dropdown');
		}
		
		//check for crm source and show the corresponding app
		var crmSource = $('#crm-source').val();
		if(crmSource && crmSource.toUpperCase() == "DOTLOOP") {
			$('.crm-settings-dropdown-item[data-crm-type="Dotloop"]').click();
		} else {
			$('#crm-settings-dropdown-cont').children('.crm-settings-dropdown-item:first').click();
		}
	});
</script>