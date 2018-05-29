<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

	<div class="hm-header-main-wrapper">
		<div class="container">
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
				<div class="float-right hm-header-right text-center" onclick="javascript:showMainContent('./showbuildhierarchypage.do')">
					<spring:message code="label.header.buildhierarchy.key" />
				</div>
				
			</div>
		</div>
	</div>

	<div class="container v-um-container mng-tbl-pad-zero">
		<div class="v-um-header clearfix">
			<div class="v-um-hdr-left float-left"><spring:message code="label.usermanagement.head.browseruser.key" /></div>
			<div class="v-um-hdr-right v-um-hdr-search float-right">
				<input id="search-users-key" class="v-um-inp" placeholder="<spring:message code="label.searchuser.key" />">
				<span id="um-search-icn" class="um-search-icn"></span>
				<div id="um-clear-input-icn" class="um-clear-input-icn hide" title="clear"></div>
				<input type="hidden" id="users-count" value="${usersCount}">
			</div>
		</div>
		<div class="v-um-tbl-wrapper mng-tbl-pad-zero" id="user-list">
			<!-- Fill in the user list jsp -->
		</div>
		<div id="paginate-buttons" style="width: 100px; margin: 0 auto;">
			<div id="page-previous" class="float-left paginate-button"><spring:message code="label.previous.key" /></div>
			<div id="page-next" class="float-right paginate-button"><spring:message code="label.next.key" /></div>
		</div>
	</div>
	<div id="temp-message" class="hide"></div>
	
<script>
$(document).ready(function() {
	hideOverlay();
	$(document).attr("title", "User Management");
	initUserManagementPage();

	doStopAjaxRequestForUsersList = false;
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
	
	$('#search-users-key').keyup(function() {
		var val = $(this).val();
		if(val == "undefined" || val.trim() == "") {
			$('#um-clear-input-icn').hide();
		} else {
			$('#um-clear-input-icn').show();
		}
	});
	
	$('#um-clear-input-icn').click(function() {
		$('#search-users-key').val('');
		$(this).hide();
		initUserManagementPage();
	});
});
</script>