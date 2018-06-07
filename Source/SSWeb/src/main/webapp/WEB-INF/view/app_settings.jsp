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
	<input type="hidden" id="crm-source"
		value="${appSettings.crm_info.crm_source}" />
</c:if>
<input type="hidden" id="cur-company-id"
	value="${user.company.companyId}" />
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.appsettings.key" />
			</div>
			<!-- Add user assignment dropdown -->
			<jsp:include page="user_assignment_dropdown.jsp"></jsp:include>
		</div>
	</div>
</div>
<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
	<!-- Check if user is realtech or ss admin -->
	<c:if test="${isRealTechOrSSAdmin}">
		<!-- Select which CRM jsp to include -->
		<c:if test="${not empty crmMappings }">
			<div class="st-crm-container">
				<div class="um-header crm-setting-hdr crm-settings-dropdown">
					<span id="crm-settings-dropdown-sel-text">${crmMappings[0].crmMaster.crmName }</span>
					Settings
				</div>
				<div id="crm-settings-dropdown-cont"
					class="hide crm-settings-dropdown-cont va-dd-wrapper">
					<c:forEach items="${crmMappings}" var="mapping">
						<c:choose>
							<c:when
								test="${mapping.crmMaster.crmName == 'Encompass' && profilemasterid != 1}">
								<%-- Skip if crm mapping encompass and not company admin --%>
							</c:when>

							<c:when
								test="${mapping.crmMaster.crmName == 'Lone Wolf' && profilemasterid == 4}">
							</c:when> 
							
							<c:when
								test="${mapping.crmMaster.crmName == 'FTP' && profilemasterid != 1}">
								<%-- Skip if crm mapping ftp and not company admin --%>
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
						<c:when
							test="${mapping.crmMaster.crmName == 'Encompass' && profilemasterid == 1}">
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

						<c:when
							test="${mapping.crmMaster.crmName == 'Lone Wolf' && profilemasterid != 4}">
							<div class="crm-setting-cont hide"
								data-crm-type="${mapping.crmMaster.crmName }">
								<jsp:include page="lone_wolf.jsp"></jsp:include>
							</div>
						</c:when> 
						
						<c:when
							test="${mapping.crmMaster.crmName == 'FTP' && profilemasterid == 1}">
							<div class="crm-setting-cont hide"
								data-crm-type="${mapping.crmMaster.crmName }">
								<jsp:include page="ftp.jsp"></jsp:include>
							</div>
						</c:when>
					</c:choose>
				</c:forEach>
			</div>
		</c:if>
		</c:if>
		<c:if test="${user.isOwner == 1}">
			<jsp:include page="untracked_user.jsp"></jsp:include>
		</c:if>
	</div>

</div>


<script>
	$(document).ready(
			function() {
				$(document).attr("title", "Apps");
				updateViewAsScroll();
				console.log("${mapping.crmMaster.crmName}");
				console.log("${profilemasterid}");

				//Remove the dropdown icon if only one option for app available
				if ($('#crm-settings-dropdown-cont').children(
						'.crm-settings-dropdown-item').length <= 1) {
					$('.crm-setting-hdr').removeClass('crm-settings-dropdown');
				}

				//check for crm source and show the corresponding app
				var crmSource = $('#crm-source').val();
				if (crmSource && crmSource.toUpperCase() == "DOTLOOP") {
					$('.crm-settings-dropdown-item[data-crm-type="Dotloop"]')
							.trigger('click');
				} else if (crmSource && crmSource.toUpperCase() == "LONEWOLF") {
					$('.crm-settings-dropdown-item[data-crm-type="Lone Wolf"]')
							.trigger('click');
				} else {
					$('#crm-settings-dropdown-cont').children(
							'.crm-settings-dropdown-item:first').trigger(
							'click');
				}
			});
</script>