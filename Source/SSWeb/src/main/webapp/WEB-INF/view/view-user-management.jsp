<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
	<div id="ms-overlay-loader" style="display:none"></div>
	<div id="ms-confirm-popup" class="overlay-main" style="display: none;">
		<div id="ms-confirm-popup-body" class="ms-popup-body-cont">
			<div class="ol-content">
				<div id="ms-confirm-popup-txt-cont" class="ol-txt">
					<div class="linked-in-cc-popup-txt-body">
					    <div id="ms-confirm-popup-txt-hdr" class="linked-in-cc-popup-txt-hdr"></div>
					    <div id="ms-confirm-popup-txt"class="linked-in-cc-popup-txt">
					    	<div id="ms-confirm-failed" class="ms-popup-alert-txt"></div>
					    	<div id="ms-confirm-success" class="ms-popup-alert-txt"></div>
					    </div>
					</div>
				</div>
				<div class="linked-in-cc-popup-btn-cont">
					<div class="linked-in-cc-btn-cont">
						<div id="ms-confirm-popup-continue" class="ms-confirm-btn">Ok</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<div id="ms-confirmation-popup" class="overlay-main" style="display: none;">
		<div id="ms-confirmation-popup-body" class="ms-popup-body-cont">
			<div class="ol-content">
				<div id="ms-confirmation-popup-txt-cont" class="ol-txt">
					<div class="linked-in-cc-popup-txt-body">
					    <div id="ms-confirmation-popup-txt"class="linked-in-cc-popup-txt">
					    	<div id="ms-confirmation-message" class="ms-popup-alert-txt">Are you sure you want to do this?</div>
					    </div>
					</div>
				</div>
				<div class="linked-in-cc-popup-btn-cont">
					<div class="linked-in-cc-btn-cont">
						<div id="ms-confirmation-popup-cancel" class="ms-confirm-btn">Cancel</div>
					</div>
					<div class="linked-in-cc-btn-cont">
						<div id="ms-confirmation-popup-continue" class="ms-confirm-btn">Confirm</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	
	<input type="file" id="ms-prof-image" class="hidden">
	<input type="file" id="ms-logo-image" class="hidden">
	<div id="ms-assign-popup" class="overlay-main" style="display:none">
		<input type="hidden" id="ms-sel-region-id" value="">
		<input type="hidden" id="ms-sel-branch-id" value="">
		<input type="hidden" id="ms-sel-region-name" value="">
		<input type="hidden" id="ms-sel-branch-name" value="">
		<div id="ms-assign-popup-body" class="ms-popup-body-cont">
			<div id="ms-assign-popup-hdr" class="ms-popup-hdr">
				<div id="ms-assign-popup-hdr-txt" class="ms-assign-popup-hdr-txt">Assign to Region</div>
			</div>
			<div id="ms-assign-cont" class="ol-content" style="display: none;">
				<div id="ms-assign-popup-txt-cont" class="ol-txt">
					<div class="ms-popup-body">
					  <div class="ms-assign-popup-label">Select Region: </div>
					  <input id="ms-assign-popup-inp-reg" class="ms-assign-popup-inp" autocomplete="off" style="display: none;">
					  <input id="ms-assign-popup-inp-bra" class="ms-assign-popup-inp" autocomplete="off" style="display: none;">
					</div>
				</div>
			</div>
			
			<div id="ms-autopost-cont" class="ol-content" style="display: none;">
				<div id="ms-assign-popup-txt-cont" class="ol-txt">
					<div class="ms-popup-body">
						<div class="ms-minrat-star-cont" id="ms-minrat-star-cont">
							<div class="rating-image float-left  star-rating-0.00" title="0.5/5.0"></div>
		   					<div class="ms-minrat-star-text">0.0</div>
						</div>
						<div class="ms-minrat-drop-cont">
							<input class="ms-dropdown-inp dd-arrow-dn v-ed-click-item" id="ms-autopost" data-value=0.5 value="0.5" readonly>
							<div class="ms-rat-dropdown" style="display:none;">
								<div class="ms-rat-drop-item" style="border-top: 1px solid #ececec;">5</div>
								<div class="ms-rat-drop-item">4.5</div>
								<div class="ms-rat-drop-item">4.0</div>
								<div class="ms-rat-drop-item">3.5</div>
								<div class="ms-rat-drop-item">3.0</div>
								<div class="ms-rat-drop-item">2.5</div>
								<div class="ms-rat-drop-item">2.0</div>
								<div class="ms-rat-drop-item">1.5</div>
								<div class="ms-rat-drop-item">1.0</div>
								<div class="ms-rat-drop-item">0.5</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div id="ms-prof-pic-cont"  class="ms-prof-img-crop-cont" style="display:none">
				<div id="ms-prof-img-cropper" class="ss-prof-img-popup-cropper">
				
				</div>
			</div>
			
			<div class="linked-in-cc-popup-btn-cont">
					<div id="ms-assign-popup-cancel" class="ms-assign-btn">Cancel</div>
					<div id="ms-assign-popup-assign" class="ms-assign-btn">Assign</div>
			</div>
			
		</div>
	</div>
	
	<div class="hm-header-main-wrapper">
		<div class="container" style="padding: 0">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.header.usermanagement.key" />
				</div>
				<c:if test="${not empty realTechAdminId }">
					<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./hierarchyupload.do')">
						<spring:message code="label.header.Hierarchyupload.key" />
					</div>
				</c:if>
				<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./viewhierarchy.do');">
					<spring:message code="label.viewcompanyhierachy.key" />
				</div>
				<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./showusermangementpage.do')">
					<spring:message code="label.header.editteam.key" />
				</div>
				<c:if test="${canAdd}">
				<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
					<spring:message code="label.header.buildhierarchy.key" />
				</div>
				</c:if>
			</div>
		</div>
	</div>

	<div class="container v-um-container mng-tbl-pad-zero">
		<div class="v-um-header clearfix" style="margin-bottom: 15px">
			<div class="v-um-hdr-right ms-hdr-search">
				<input id="ms-search-users-key" class="ms-input" placeholder="Search User">
				<span id="ms-search-icn" class="ms-search-icn"></span>
				<div id="ms-clear-input-icn" class="ms-clear-input-icn hide" title="clear" style="display: block;"></div>
				<input type="hidden" id="users-count" value="${usersCount}">
			</div>
		</div>
		
		<input id="ms-user-data" type="hidden" data-selectedUsers=[] data-userStatus="default" data-selected="none" data-startIndex=0 data-userCount="${usersCount}" data-activeUsersCount=0 data-nameUsersCount=0 data-nextIndex=0	data-prevIndex=0 data-lastIndex=0 data-firstIndex=0 data-pageCount=1 data-PageNo=1>
		<div class="ms-hdr">
			<div class="float-left ms-dropdown ms-ba-dropdown">
				<div class="ms-dropdown-hdr">
					Select Action 
					<img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-ba-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-ba-chevron-up" style="display: none;">
				</div>
				<div class="float-left ms-options-cont ms-ba-options" style="display: none;">
					<div class="ms-option ms-reinvite"><div class="ms-reinvite-icn"></div>ReInvite</div>
					
					<c:choose>
						<c:when test="${canAssignRegion}">
							<div class="ms-option ms-assign-reg"><div class="ms-region-icn"></div>Assign to Region</div>
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to assign user to region."><div class="ms-region-icn"></div>Assign to Region</div>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${canAssignBranch}">
							<div class="ms-option ms-assign-bra"><div class="ms-branch-icn"></div>Assign to Branch</div>	
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to assign user to branch."><div class="ms-branch-icn"></div>Assign to Branch</div>	
						</c:otherwise>
					</c:choose>
					
					<div class="ms-option ms-smadmin"><div class="ms-social-monitor-icn"></div>Social Monitor Access</div>
					<div class="ms-option ms-auto-post-score"><div class="ms-auto-post-icn"></div>Auto post score</div>
					<div class="ms-option ms-prof-image"><div class="ms-prof-img-icn"></div>Upload Profile Image</div>
					
					<c:choose>
						<c:when test="${not parentLock.isLogoLocked}">
							<div class="ms-option ms-logo-image"><div class="ms-logo-img-icn"></div>Upload Logo</div>				
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="Logo is locked by admin."><div class="ms-logo-img-icn"></div>Upload Logo</div>			
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${canDelete}">
							<div class="ms-option ms-delete"><div class="ms-delete-icn"></div>Delete</div>		
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to delete user."><div class="ms-delete-icn"></div>Delete</div>		
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<div class="ms-sel-usr-text">
				
			</div>
			
			<div class="ms-filters-dropdown">
		        <div class="ms-filters-dropdown-hdr">
		            <div class="ms-filter-hdr-txt">Selected Filter</div>
		            <img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-fil-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-fil-chevron-up" style="display: none;">
		    	</div>
		        <div class="ms-filters-options" style="display: none;">
		        	<input type="hidden" value="${usersCount}" id="ms-user-count">
		            <div class="ms-filter ms-sel-all-option ms-sel-all">Select all ${usersCount} users</div>
		            <div class="ms-filter ms-sel-unverified">Select all Unverified users</div>
		            <div class="ms-filter ms-sel-verified">Select all Verified users</div>
		        </div>
		    </div>
		    
		    <div class="ms-page-user-count">
		        <div class="ms-page-user-count-txt ms-page-user-start-count">0</div> - 
		        <div class="ms-page-user-count-txt ms-page-user-end-count">0</div> of 
		        <div class="ms-page-user-count-txt ms-page-user-total-count">${usersCount}</div>
		    </div>
		    
		    <div class="float-left ms-dropdown ms-batch-size-dropdown">
		    	<input type="hidden" id="um-batch-size" value="10">
				<div class="ms-dropdown-hdr">
					Batch Size: <div class="ms-bs-value">10</div> 
					<img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-bs-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-bs-chevron-up" style="display: none;">
				</div>
				<div class="float-left ms-options-cont ms-batch-size-options" style="display: none;">
					<div class="ms-batchSize">10</div>
					<div class="ms-batchSize">25</div>
					<div class="ms-batchSize">50</div>
					<div class="ms-batchSize">100</div>
				</div>
			</div>
			
			<div class="ms-pagination-container">
		        <div class="ms-first ms-pagi-icn" data-active="false">&lt;&lt;</div>
		        <div class="ms-prev ms-pagi-icn" data-active="false">&lt;</div>
		        <input class="ms-page-no" value="1">/
		        <div class="ms-total-pages">100</div>
		        <div class="ms-next ms-pagi-icn" data-active="false">&gt;</div>
		        <div class="ms-last ms-pagi-icn" data-active="false">&gt;&gt;</div>
		    </div>
		</div>
		
		<div class="ms-sub-hdr">
			<div class="ms-page-sort">
			     	Sort by Name
				<div class="ms-sort ms-sort-asc" data-active="true">Ascending</div>|<div class="ms-sort ms-sort-desc" data-active="false">Descending</div>
			</div>
		</div>
			
		<div class="v-um-tbl-wrapper mng-tbl-pad-zero ms-user-list-container" id="user-list">
			<!-- Fill in the user list jsp -->
		</div>
			
		<div class="ms-sub-hdr">
			<div class="ms-page-sort">
			   	Sort by Name
			 	<div class="ms-sort ms-sort-asc" data-active="true">Ascending</div>|<div class="ms-sort ms-sort-desc" data-active="false">Descending</div>
			</div>
		</div>		
		<div class="ms-hdr">
			<div class="float-left ms-dropdown ms-ba-dropdown">
				<div class="ms-dropdown-hdr">
					Select Action 
					<img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-ba-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-ba-chevron-up" style="display: none;">
				</div>
				<div class="float-left ms-options-cont ms-ba-options" style="display: none;">
					<div class="ms-option ms-reinvite"><div class="ms-reinvite-icn"></div>ReInvite</div>
					
					<c:choose>
						<c:when test="${canDelete}">
							<div class="ms-option ms-delete"><div class="ms-delete-icn"></div>Delete</div>		
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to delete user."><div class="ms-delete-icn"></div>Delete</div>		
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${canAssignRegion}">
							<div class="ms-option ms-assign-reg"><div class="ms-region-icn"></div>Assign to Region</div>
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to assign user to region."><div class="ms-region-icn"></div>Assign to Region</div>
						</c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${canAssignBranch}">
							<div class="ms-option ms-assign-bra"><div class="ms-branch-icn"></div>Assign to Branch</div>	
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="No permissions to assign user to branch."><div class="ms-branch-icn"></div>Assign to Branch</div>	
						</c:otherwise>
					</c:choose>
					
					<div class="ms-option ms-smadmin"><div class="ms-social-monitor-icn"></div>Social Monitor Access</div>
					<div class="ms-option ms-auto-post-score"><div class="ms-auto-post-icn"></div>Auto post score</div>
					<div class="ms-option ms-prof-image"><div class="ms-prof-img-icn"></div>Upload Profile Image</div>
					
					<c:choose>
						<c:when test="${not parentLock.isLogoLocked}">
							<div class="ms-option ms-logo-image"><div class="ms-logo-img-icn"></div>Upload Logo</div>				
						</c:when>
						<c:otherwise>
							<div class="ms-option ms-option-locked" title="Logo is locked by admin."><div class="ms-logo-img-icn"></div>Upload Logo</div>			
						</c:otherwise>
					</c:choose>
				</div>
			</div>
			
			<div class="ms-sel-usr-text">
				
			</div>
			
			<div class="ms-filters-dropdown">
		        <div class="ms-filters-dropdown-hdr">
		            <div class="ms-filter-hdr-txt">Selected Filter</div>
		            <img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-fil-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-fil-chevron-up" style="display: none;">
		    	</div>
		        <div class="ms-filters-options" style="display: none;">
		        	<input type="hidden" value="${usersCount}" id="ms-user-count">
		            <div class="ms-filter ms-sel-all-option ms-sel-all">Select all ${usersCount} users</div>
		            <div class="ms-filter ms-sel-unverified">Select all Unverified users</div>
		            <div class="ms-filter ms-sel-verified">Select all Verified users</div>
		        </div>
		    </div>
		    
		    <div class="ms-page-user-count">
		        <div class="ms-page-user-count-txt ms-page-user-start-count">0</div> - 
		        <div class="ms-page-user-count-txt ms-page-user-end-count">0</div> of 
		        <div class="ms-page-user-count-txt ms-page-user-total-count">${usersCount}</div>
		    </div>
		    
		    <div class="float-left ms-dropdown ms-batch-size-dropdown">
		    	<input type="hidden" id="um-batch-size" value="10">
				<div class="ms-dropdown-hdr">
					Batch Size: <div class="ms-bs-value">10</div> 
					<img src="/resources/images/chevron-down.png" class="ms-dropdown-img ms-bs-chevron-down" style="display: block;">
					<img src="/resources/images/chevron-up.png" class="hide ms-dropdown-img ms-bs-chevron-up" style="display: none;">
				</div>
				<div class="float-left ms-options-cont ms-batch-size-options" style="display: none;">
					<div class="ms-batchSize">10</div>
					<div class="ms-batchSize">25</div>
					<div class="ms-batchSize">50</div>
					<div class="ms-batchSize">100</div>
				</div>
			</div>
			
			<div class="ms-pagination-container">
		        <div class="ms-first ms-pagi-icn" data-active="false">&lt;&lt;</div>
		        <div class="ms-prev ms-pagi-icn" data-active="false">&lt;</div>
		        <input class="ms-page-no" value="1">/
		        <div class="ms-total-pages">100</div>
		        <div class="ms-next ms-pagi-icn" data-active="false">&gt;</div>
		        <div class="ms-last ms-pagi-icn" data-active="false">&gt;&gt;</div>
		    </div>
		</div>
		
		<%-- <div id="paginate-buttons" style="width: 100%; margin: 0% 40% ; auto;">
			<div id="page-previous" class="float-left paginate-button"><spring:message code="label.previous.key" /></div>
			<div class="float-left" style="margin:0% 1.5%; 0% 1.5%">
							<input id="sel-paginate-manage-team" type="text" pattern="[0-9]*" class="sel-page" value="1"/>
							<span class="paginate-divider">/</span>
							<span id="stream-page-count" class="paginate-total-pages">0</span>
		   </div>
			<div id="page-next" class="float-left paginate-button"><spring:message code="label.next.key" /></div>
		</div> --%>
	</div>
	<div id="temp-message" class="hide"></div>
	
<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "User Management");
	
	console.log("${parentLock}");
	console.log("${parentLock.isLogoLocked}");
	
	var startIndex = 0;
	$('#ms-user-data').attr('data-startIndex',startIndex);
	
	fetchUsersForUserManagementForAdmin(startIndex);

	doStopAjaxRequestForUsersListMultiSelect = false;
	if ($('#server-message>div').hasClass("error-message")) {
		isUserManagementAuthorized = false;
		$('#server-message').show();
	}

	$(document).on('click', '.v-tbl-icn', function(e) {
		e.stopPropagation();
	});

	$(document).on('click', '.v-ed-txt-dd', function() {
		$(this).next('.v-ed-dd-wrapper').slideToggle(200);
	});

	$(document).on('click', '.v-ed-dd-item', function(e) {
		e.stopPropagation();
		$(this).parent().prev('.v-ed-txt-dd').val($(this).html());
		$(this).parent().slideToggle(200);
	});
	
	$('#ms-search-users-key').keyup(function() {
		var val = $(this).val();
		if(val == "undefined" || val.trim() == "") {
			$('#ms-clear-input-icn').hide();
		} else {
			$('#ms-clear-input-icn').show();
		}
	});
	
	$('#ms-clear-input-icn').click(function() {
		$('#ms-search-users-key').val('');
		$(this).hide();

		var startIndex = 0;
		$('#ms-user-data').attr('data-startIndex',startIndex);
		
		fetchUsersForUserManagementForAdmin(startIndex);
	});
});
</script>