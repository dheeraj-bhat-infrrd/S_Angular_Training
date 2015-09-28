
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}"
	var="user" />
<c:if test="${appSettings != null && appSettings.crm_info != null}">
	<input type="hidden" id="crm-source" value="${appSettings.crm_info.crm_source}"/>
</c:if>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.appsettings.key" />
			</div>
		</div>
	</div>
</div>
<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<!-- Select which CRM jsp to include -->
		<c:if test="${not empty crmMappings }">
			<div class="st-crm-container">
				<c:choose>
					<c:when test="${fn:length(crmMappings) gt 1}">
						<div class="um-header crm-setting-hdr crm-settings-dropdown">
							<span id="crm-settings-dropdown-sel-text">${crmMappings[0].crmMaster.crmName }</span>
							Settings
						</div>
						<div class="hide crm-settings-dropdown-cont va-dd-wrapper">
							<c:forEach items="${crmMappings }" var="mapping">
								<div class="crm-settings-dropdown-item"
									data-crm-type="${mapping.crmMaster.crmName }">${mapping.crmMaster.crmName }</div>
							</c:forEach>
						</div>
					</c:when>
					<c:otherwise>
						<div class="um-header crm-setting-hdr">
							${crmMappings[0].crmMaster.crmName } Settings</div>
					</c:otherwise>
				</c:choose>
				<c:forEach items="${crmMappings }" var="mapping" varStatus="loop">
					<c:choose>
						<c:when test="${loop.index gt 0}">
							<c:set var="hideClass" value="hide"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="hideClass" value=""></c:set>
						</c:otherwise>
					</c:choose>
					<div class="crm-setting-cont ${hideClass}"
						data-crm-type="${mapping.crmMaster.crmName }">
						<c:if test="${mapping.crmMaster.crmName == 'Encompass'}">
							<jsp:include page="encompass.jsp"></jsp:include>
						</c:if>
					</div>
					<div class="crm-setting-cont ${hideClass}"
						data-crm-type="${mapping.crmMaster.crmName }">
						<c:if test="${mapping.crmMaster.crmName == 'Dotloop'}">
							<jsp:include page="dotloop.jsp"></jsp:include>
						</c:if>
					</div>
				</c:forEach>
			</div>
		</c:if>
	</div>
</div>
<script>
	$(document).ready(function() {
		hideOverlay();
		$(document).attr("title", "Apps");

		$('#encompass-username').blur(function() {
			validateEncompassUserName(this.id);
		});
		$('#encompass-password').blur(function() {
			validateEncompassPassword(this.id);
		});
		$('#encompass-url').blur(function() {
			validateURL(this.id);
		});
		
		$('body').on('click', '#encompass-save', function() {
			if (validateEncompassInput('encompass-form-div')) {
				saveEncompassDetails("encompass-form");
			}
		});
		$('body').on('click', '#encompass-testconnection', function() {
			if (validateEncompassInput('encompass-form-div')) {
				testEncompassConnection("encompass-form");
			}
		});
		$('body').on('click',function(){
			$('.crm-settings-dropdown-cont').slideUp(200);
		});
		$('.crm-settings-dropdown').on('click',function(e){
			e.stopPropagation();
			$('.crm-settings-dropdown-cont').slideToggle(200);
		});
		$('.crm-settings-dropdown-item').on('click',function(e){
			var crmType = $(this).attr('data-crm-type');
			$('#crm-settings-dropdown-sel-text').text(crmType);
			$('.crm-setting-cont').hide();
			$('.crm-setting-cont[data-crm-type="'+crmType+'"]').show();
		});
		
		//check for crm source and show the corresponding app
		var crmSource = $('#crm-source').val();
		if(crmSource && crmSource.toUpperCase() == "DOTLOOP") {
			$('.crm-settings-dropdown-item[data-crm-type="Dotloop"]').click();
		}
		
		//dotloop function
		$('#dotloop-apikey').blur(function() {
			validateDotloopKey(this.id);
		});
		$('body').on('click', '#dotloop-save', function() {
			if (validateDotloopInput()) {
				saveDotloopDetails("dotloop-form");
			}
		});
		$('body').on('click', '#dotloop-testconnection', function() {
			if (validateDotloopInput()) {
				testDotloopConnection("dotloop-form");
			}
		});
	});
</script>