<!-- User details in user management page -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty searchedUser}">
	<c:set value="${searchedUser.userId}" var="userId"></c:set>
	<c:set value="${searchedUser.firstName}" var="firstName"></c:set>
	<c:set value="${searchedUser.lastName}" var="lastName"></c:set>
	<c:set value="${searchedUser.emailId}" var="emailId"></c:set>
	<c:set value="${searchedUser.status }" var="status"></c:set>
</c:if>
<div class="row" data-id="${userId}" id="um-user-details-container">
	<div class="um-top-row cleafix">
		<div class="clearfix um-top-form-wrapper">
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
				<div class="hm-item-row clearfix">
					<div class="um-item-row-left text-right">
						<spring:message code="label.firstname.key" />
					</div>
					<div class="um-item-row-icon"></div>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" id="um-fname" name="firstName"
							value="${firstName }" class="um-item-row-txt mob-icn-fname"
							placeholder='<spring:message code="label.firstname.key"/>'>
						<div id="um-fname-error" class="input-error-2 error-msg"></div>
					</div>
				</div>
			</div>
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
				<div class="hm-item-row clearfix">
					<div class="um-item-row-left text-right">
						<spring:message code="label.lastname.key" />
					</div>
					<div class="um-item-row-icon"></div>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" id="um-lname" name="lastName"
							value="${lastName }" class="um-item-row-txt mob-icn-lname"
							placeholder='<spring:message code="label.lastname.key" />'>
						<div id="um-lname-error" class="input-error-2 error-msg"></div>
					</div>
				</div>
			</div>
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
				<div class="hm-item-row clearfix" id="um-emailid-con">
					<div class="um-item-row-left text-right">
						<spring:message code="label.emailid.key" />
					</div>
					<c:choose>
						<c:when test="${not empty searchedUser }">
							<div class="um-item-row-icon icn-tick"></div>
						</c:when>
						<c:otherwise>
							<div class="um-item-row-icon icn-save cursor-pointer"></div>
						</c:otherwise>
					</c:choose>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" id="um-emailid" name="emailId"
							value="${emailId }" class="um-item-row-txt mob-icn-email"
							placeholder='<spring:message code="label.emailid.key" />'>
						<div id="um-emailid-error" class="input-error-2 error-msg"></div>
					</div>

				</div>
				<c:if test="${accounttypeval > 2 && accounttypeval < 5 }">
					<div class="hm-item-row clearfix" id="um-assignto-con">
						<div class="um-item-row-left text-right">
							<spring:message code="label.assignto.key" />
						</div>
						<div class="um-item-row-icon icn-save cursor-pointer"></div>
						<div class="hm-item-row-right um-item-row-right pos-relative">
							<input type="text" class="um-item-row-txt mob-icn-assignTo" id="um-assignto"
								placeholder='<spring:message code="label.assignto.key" />'>
						</div>
					</div>
				</c:if>
			</div>
		</div>
        <div class="row clearfix hm-btn-wrapper um-btn-wrapper-fix margin-0-auto">
            <div id="um-add-user" class="float-left add-curve-btn cursor-pointer">Add New User</div>
            <div id="um-clear-user-form" class="float-left icn-clear cursor-pointer margin-right-0"></div>
        </div>
		<!-- Populate all the assigned branches to the user -->
		<c:if test="${accounttypeval ne 2 }">
			<c:choose>
				<c:when test="${not empty assignedBranches}">
					<div class="clearfix um-top-tag-wrapper margin-bottom-25"
						id="um-assigned-brach-container">
						<c:forEach items="${assignedBranches}" var="branch">
							<div class="um-tag-row float-left"
								id="branch-to-unassign-${branch.branchId}">
								<div class="um-tag-item-wrapper clearfix">
									<div class="um-tag-item-txt float-left">${branch.branch}</div>
									<div class="um-tag-item-icn float-left"></div>
								</div>
							</div>
						</c:forEach>
					</div>
				</c:when>
			</c:choose>
		</c:if>
	</div>
	<c:if test="${not empty searchedUser}">
		<div class="clearfix um-top-status-wrapper margin-bottom-25">
			<div class="um-top-status-title float-left">
				<spring:message code="label.status.key" />
			</div>
			<c:choose>
				<c:when test="${status eq 1 }">
					<div id="um-icn-status-text" class="um-top-status-text float-left">
						<spring:message code="label.active.key" />
					</div>
					<div id="icn-status-green"
						class="um-top-status-text um-status-icon icn-status-green float-left cursor-pointer"></div>
					<div id="icn-status-red"
						class="hide um-top-status-text um-status-icon icn-status-red float-left cursor-pointer"></div>
				</c:when>
				<c:when test="${status eq 2 }">
					<div id="um-icn-status-text" class="um-top-status-text float-left">
						<spring:message code="label.notverified.key" />
					</div>
				</c:when>
				<c:when test="${status eq 3 }">
					<div id="um-icn-status-text" class="um-top-status-text float-left">
						<spring:message code="label.inactive.key" />
					</div>
					<div id="icn-status-green"
						class="hide um-top-status-text um-status-icon icn-status-green float-left cursor-pointer"></div>
					<div id="icn-status-red"
						class="um-top-status-text um-status-icon icn-status-red float-left cursor-pointer"></div>
				</c:when>
			</c:choose>
			<div id="icon-user-delete"
				class="um-top-status-text um-status-icon icn-person-remove float-left cursor-pointer"></div>
		</div>
	</c:if>
	<div class="um-bottom-row cleafix"></div>
</div>
<style>
.error-msg {
	width: auto !important;
	margin-top: 5px !important;
}

.hm-item-row-right {
	min-height: 90px;
}

.hm-item-row {
	margin: 0 auto;
}
</style>
<script>
	
	/* $('#um-assignto').keyup(function() {
		$(this).removeAttr("brachid");
	}); */
	$('#icon-user-delete').click(function() {
		var userId = $(this).closest('.row').attr("data-id");
		confirmDeleteUser(userId);
	});
	$('#icn-status-green')
			.click(
					function() {
						var userId = $(this).closest('.row').attr("data-id");
						$('#overlay-main').show();
						$('#overlay-continue').html("Deactivate");
						$('#overlay-cancel').html("Cancel");
						$('#overlay-header').html("Deactivate User");
						$('#overlay-text').html(
								"Are you sure you want to deactivate user??");
						$('#overlay-continue').attr(
								"onclick",
								"activateOrDeactivateUser(" + false + ", "
										+ userId + ");");
					});
	$('#icn-status-red').click(
			function() {
				var userId = $(this).closest('.row').attr("data-id");
				$('#overlay-continue').html("Activate");
				$('#overlay-cancel').html("Cancel");
				$('#overlay-main').show();
				$('#overlay-header').html("Activate User");
				$('#overlay-text').html("Are you sure you want to activate user??");
				$('#overlay-continue').attr(
						"onclick",
						"activateOrDeactivateUser(" + true + ", " + userId
								+ ");");
			});
	$('.um-tag-item-icn').click(
			function() {
				var branchIdToUnassign = $(this).parent().parent().attr("id");
				branchIdToUnassign = branchIdToUnassign
						.substr("branch-to-unassign-".length);
				var userId = $(this).closest('.row').attr("data-id");
				$('#overlay-continue').html("Confirm");
				$('#overlay-cancel').html("Cancel");
				$('#overlay-main').show();
				$('#overlay-header').html("Remove user from branch");
				$('#overlay-text').html(
						"Are you sure you want to remove user from branch??");
				$('#overlay-continue').attr(
						"onclick",
						"unassignUserFromBranch(" + userId + ","
								+ branchIdToUnassign + ");");

			});

	$('#um-assignto').click(function() {
		$(this).parent().find('.um-branch-list').slideToggle(200);
	});

	$(document).ready(function() {
		/* var branchListHtml = $('#branch-list').html();
		if($('#branch-list > div > div').length == 0) {
			$('#um-assignto').prop("disabled",true);
			$('#um-assignto').attr("placeholder","Please add a branch");
		}
		$('#um-assignto').parent().append(branchListHtml); */
	});
</script>
