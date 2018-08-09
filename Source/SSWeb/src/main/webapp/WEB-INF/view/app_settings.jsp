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
	
<div id="mismatch-new-popup-main" class="mismatch-new-popup-main hide">
	<div id="mismatch-popup-new" class="mismatch-popup-new">
		<div class="mismatch-popup-new-header col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<div id="mismatch-new-mail" class="mis-new-mail-id mismatch-new-mail col-lg-12 col-md-12 col-sm-12 col-xs-12">
				invalid@domain.com
			</div>
			<div id="mismatch-new-text" class="mismatch-new-text col-lg-12 col-md-12 col-sm-12 col-xs-12">
				could not be automatically be associated with a user in your account.
			</div>
		</div>
		<div class="mismatch-popup-new-body col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<input type="hidden" id="mismatch-trans-mail" value="">
			<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12" style="padding:0">
				<div class="mismatch-popup-new-sub-header col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<div class="mismatch-sub-header-left col-lg-12 col-md-12 col-sm-12 col-xs-12">
				
					</div>
				</div>
				<div class="mismatch-new-body-trans col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<p class="mismatch-new-trans-txt">You have <span id="mismatch-new-trans-num">0</span> Transactions in this status</p>
					<div class="mismatch-new-trans-div col-lg-12 col-md-12 col-sm-12 col-xs-12">
						<div class="mismatch-new-trans-bar col-lg-12 col-md-12 col-sm-12 col-xs-12"><span class="mismatch-trans-arrow-up mismatch-new-trans-arrow cursor-pointer">&#9650;</span></div>
						<div id="mismatch-new-trans-list" class="mismatch-new-trans-list col-lg-12 col-md-12 col-sm-12 col-xs-12" data-startIndex=0 data-batchSize=6 data-total=0>
							<div id="mismatch-new-trans" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-trans">
								<div class="col-lg-7 col-md-7 col-xs-7 col-sm-7 mismatch-trans-text">
									<p class="mismatch-trans-p">Andrew Elliot</p>
									<p>KW-8989898898</p>
								</div>
								<div class="col-lg-5 col-md-5 col-xs-5 col-sm-5 mismatch-trans-date">Jan 17, 2018</div>
							</div>
							<div id="mismatch-new-trans-1" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-trans">
								<div class="col-lg-7 col-md-7 col-xs-7 col-sm-7 mismatch-trans-text">
									<p class="mismatch-trans-p">Andrew Elliot</p>
									<p>KW-8989898898</p>
								</div>
								<div class="col-lg-5 col-md-5 col-xs-5 col-sm-5 mismatch-trans-date">Jan 17, 2018</div>
							</div>
							<div id="mismatch-new-trans-2" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-trans">
								<div class="col-lg-7 col-md-7 col-xs-7 col-sm-7 mismatch-trans-text">
									<p class="mismatch-trans-p">Andrew Elliot</p>
									<p>KW-8989898898</p>
								</div>
								<div class="col-lg-5 col-md-5 col-xs-5 col-sm-5 mismatch-trans-date">Jan 17, 2018</div>
							</div>
						</div>
						<div class="mismatch-new-trans-bar col-lg-12 col-md-12 col-sm-12 col-xs-12"><span class="mismatch-trans-arrow-down mismatch-new-trans-arrow cursor-pointer hide">&#9660;</span></div>
					</div>
				</div>
			</div>
			<div class="mismatch-popup-body-right col-lg-8 col-md-8 col-sm-8 col-xs-12" style="padding:0">
				<div class="mismatch-popup-new-sub-header col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<div id="mismatch-sub-header-txt" class="mismatch-sub-header-txt col-lg-12 col-md-12 col-sm-12 col-xs-12">
						What can we do for you?
					</div>
				</div>
				<div id="mismatch-new-body-options" class="mismatch-new-body-options col-lg-12 col-md-12 col-sm-12 col-xs-12">
					<div id="mismatch-new-assign-cont" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-assign-cont">
						<p class="mismatch-option-text"><span class="mismatch-new-bold">Add</span> <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> as an <span class="mismatch-new-bold">alias</span> for a current user and <span class="mismatch-new-bold">send</span> surveys for all transactions</p>
						<div class="mismatch-new-options-cont" style="display:  flex;">
							<div class="mismatch-add-new-dropdown-div">
								<input id="mismatch-new-eid" class="mismatch-new-assign-inp ui-autocomplete-input"  autocomplete="off">
							</div>
							<div id="mismatch-new-assign-btn" class="mismatch-new-assign-btn cursor-pointer">Assign</div>
						</div>
					</div>
					<div id="mismatch-new-user-cont" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-user-cont">
						<div class="mismatch-new-or">or</div>
						<p class="mismatch-option-text">Create <span class="mismatch-new-bold">new user</span> account for <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> and <span class="mismatch-new-bold">send</span> surveys for all transactions</p>
						<div id="mismatch-new-user-btn" class="mismatch-new-user-btn mismatch-new-assign-btn cursor-pointer">Add User</div>
					</div>
					<div id="mismatch-new-ignore-cont" class="col-lg-12 col-md-12 col-sm-12 col-xs-12 mismatch-new-user-cont">
						<div class="mismatch-new-or">or</div>
						<p class="mismatch-option-text"><span class="mismatch-new-bold">Archive</span> these transactions and automatically <span class="mismatch-new-bold">ignore</span> <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> going forward</p>
						<div id="mismatch-new-ignore-btn" class="mismatch-new-ignore-btn mismatch-new-assign-btn cursor-pointer">Always Ignore</div>
					</div>
				</div>
				<div id="mismatch-new-alias-div" class="mismatch-new-body-options col-lg-12 col-md-12 col-sm-12 col-xs-12 hide">
					<p class="mismatch-new-alias-txt">Are you sure you want to:</p>
					<p class="mismatch-new-alias-verify">&#8226; Add <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> as an <span class="mismatch-new-bold">alias</span> for <span class="mismatch-new-assign-mail">Selected User</span> and automatically use this assignment for transactions from now on.</p>
					<p class="mismatch-new-alias-verify">&#8226; <span class="mismatch-new-bold">Send</span> surveys for these transactions (on the left) on behalf of <span class="mismatch-new-assign-mail">Selected User</span>.</p>
				</div>
				<div id="mismatch-new-add-div" class="mismatch-new-body-options col-lg-12 col-md-12 col-sm-12 col-xs-12 hide">
					<p class="mismatch-new-alias-txt">Are you sure you want to:</p>
					<p class="mismatch-new-alias-verify">&#8226; Create a <span class="mismatch-new-bold">new user</span> account for <span class="mismatch-new-assign-mail">Selected User</span> using <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> and send an invite for them to complete registration</p>
					<p class="mismatch-new-alias-verify">&#8226; <span class="mismatch-new-bold">Send</span> surveys for these transactions (on the left) on behalf of <span class="mismatch-new-assign-mail">Selected User</span>.</p>
				</div>
				<div id="mismatch-new-archive-div" class="mismatch-new-body-options col-lg-12 col-md-12 col-sm-12 col-xs-12 hide">
					<p class="mismatch-new-alias-txt">Are you sure you want to:</p>
					<p class="mismatch-new-alias-verify">&#8226; <span class="mismatch-new-bold">Archive</span> these transactions (on the left).</p>
					<p class="mismatch-new-alias-verify">&#8226; Automatically <span class="mismatch-new-bold">ignore</span> <span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span> from now on.</p>
				</div>
				<div id="mismatch-new-add-form-div" class="mismatch-new-body-options col-lg-12 col-md-12 col-sm-12 col-xs-12 hide">
					<form id="mismatch-new-add-user-form">'
						<input type="hidden" class="hide" name="userSelectionType" value="single">
						<input type="hidden" class="hide" name="selectedUserEmail" id="mismatch-sel-user-email">
						<p class="mismatch-add-form-txt"><span class="mismatch-new-bold">We will require the first and last name of the user to associate with </span><span class="mismatch-new-assign-mail mis-new-mail-id">invalid@domain.com</span></p>
						<div class="mismatch-inp-cont mis-drop-cont"><span class="mis-assign-to-lbl mismatch-inp-lbl">First Name</span><input id="mis-first-name" type="text" name="firstName" class="mismatch-inp" value=""/></div>
						<div class="mismatch-inp-cont mis-drop-cont"><span class="mis-assign-to-lbl mismatch-inp-lbl">Last Name</span><input id="mis-last-name" type="text" name="lastName" class="mismatch-inp" value=""/></div>
						<p class="mismatch-new-add-form-drops"><span class="mismatch-new-bold">Please select the business unit to which this new user belongs in your organizations hierarchy</span></p>
						<div class="mismatch-inp-cont mis-drop-cont">
							<span class="mismatch-inp-lbl mis-assign-to-lbl">Assign To</span>
							<div id="assign-to-selector-mis" class="float-left pos-relative"  data-profile="individual">
						        <input id="assign-to-txt-mis" data-assignto="company" value='<spring:message code="label.company.key"/>' class="mismatch-inp"/>
						        <div id="assign-to-droplist-mis" class=" mis-assigto-dd hide">
						        	<div data-assign-to-option="office" class="cursor-pointer mis-assignto-options mis-assignto-opts"><spring:message code="label.office.key"/></div>
						            <c:if test="${highestrole == 1 || highestrole == 2}">
						            	<div data-assign-to-option="region" class="cursor-pointer mis-assignto-options mis-assignto-opts"><spring:message code="label.region.key"/></div>
						            </c:if>
						            <c:if test="${highestrole == 1}">
						            	<div data-assign-to-option="company" class="cursor-pointer mis-assignto-options mis-assignto-opts"><spring:message code="label.company.key"/></div>
						            </c:if>
						        </div>
			        		</div>
						</div>
						<div id="bd-region-selector-mis" class="mismatch-inp-cont hide">
						    <div class="float-left mismatch-inp-lbl mis-assign-to-lbl"><spring:message code="label.selectregion.key"/></div>
						    <div class="float-left pos-relative" id="region-selector">
						        <input id="selected-region-txt-mis" class="mismatch-inp" placeholder='<spring:message code="label.regionselector.placeholder.key"/>'/>
						        <input type="hidden" name="regionId" id="selected-region-id-hidden-mis"/>
						        <div id="regions-droplist-mis" class="hide">
						            <!-- regions list get populated here -->
						        </div>
						    </div>
						</div>
						<div id="bd-office-selector-mis" class="mismatch-inp-cont hide">
						    <div class="float-left mismatch-inp-lbl mis-assign-to-lbl"><spring:message code="label.selectoffice.key"/></div>
						    <div class="float-left pos-relative" id="office-selector">
						        <input id="selected-office-txt-mis" class="mismatch-inp" placeholder='<spring:message code="label.officeselector.placeholder.key"/>'/>
						        <input type="hidden" name="officeId" id="selected-office-id-hidden-mis"/>
						        <div id="offices-droplist-mis" class="hide">
						            <!-- offices list get populated here  -->
						        </div>
						    </div>
						</div>
					</form>
				</div>	
			</div>
		</div>
		<div class="mismatch-popup-new-footer col-lg-12 col-md-12 col-sm-12 col-xs-12">
			<div id="mismatch-new-back" class="mismatch-new-back float-left cursor-pointer">&lt;&lt; Back</div>
			<div id="mismatch-new-confirm" class="mismatch-new-back mismatch-new-confirm float-right cursor-pointer hide">Confirm</div>
		</div>
	</div>
</div>

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