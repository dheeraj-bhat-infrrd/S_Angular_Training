<!-- User details in user management page -->
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty searchedUser}">
	<c:set value="${searchedUser.userId}" var="userId"></c:set>
	<c:set value="${searchedUser.firstName}" var="firstName"></c:set>
	<c:set value="${searchedUser.lastName}" var="lastName"></c:set>
	<c:set value="${searchedUser.emailId}" var="emailId"></c:set>
	<c:set value="${searchedUser.status }" var="status"></c:set>
</c:if>
<div class="row" id="${userId}">
	<div class="um-top-row cleafix">
		<div class="clearfix um-top-form-wrapper">
			<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 um-panel-item">
				<div class="hm-item-row clearfix">
					<div class="um-item-row-left text-right">
						<spring:message code="label.firstname.key" />
					</div>
					<div class="um-item-row-icon"></div>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" id="um-fname" name="firstName" value="${firstName }" class="um-item-row-txt"
							placeholder='<spring:message code="label.firstname.key"/>'>
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
						<input type="text" id="um-lname" name="lastName" value="${lastName }" class="um-item-row-txt"
							placeholder='<spring:message code="label.lastname.key" />'>
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
							<div class="um-item-row-icon icn-save"></div>
						</c:otherwise>
					</c:choose>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" id="um-emailid" name="emailId" value="${emailId }" class="um-item-row-txt"
							placeholder='<spring:message code="label.emailid.key" />'>
					</div>
				</div>
				<div class="hm-item-row clearfix" id="um-assignto-con">
					<div class="um-item-row-left text-right">
						<spring:message code="label.assignto.key" />
					</div>
						<div class="um-item-row-icon icn-save"></div>
					<div class="hm-item-row-right um-item-row-right">
						<input type="text" class="um-item-row-txt" id="um-assignto"
							placeholder='<spring:message code="label.assignto.key" />'>
					</div>
				</div>
			</div>
		</div>
		<!-- Populate all the assigned branches to the user -->
		<c:choose>
			<c:when test="${not empty assignedBranches}">
				<div class="clearfix um-top-tag-wrapper margin-bottom-25" id="um-assigned-brach-container">
					<c:forEach items="${assignedBranches}" var="branch">
						<div class="um-tag-row float-left" id="branch-to-unassign-${branch.branchId}">
							<div class="um-tag-item-wrapper clearfix">
								<div class="um-tag-item-txt float-left">${branch.branch}</div>
								<div class="um-tag-item-icn float-left"></div>
							</div>
						</div>
					</c:forEach>
				</div>
			</c:when>
		</c:choose>
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
			<div id="icon-user-delete" class="um-top-status-text um-status-icon icn-person-remove float-left cursor-pointer"></div>
		</div>
	</c:if>
	<div class="um-bottom-row cleafix"></div>
</div>
<script>
	
	$('#icon-user-delete').click(function(){
		var userId = $(this).closest('.row').attr("id");
		console.log("user id to delete : " + userId);
		deleteUser(userId);
	});
	$('#icn-status-green').click(function(){
		var userId = $(this).closest('.row').attr("id");
		activateOrDeactivateUser(false,userId);
	});
	$('#icn-status-red').click(function(){
		var userId = $(this).closest('.row').attr("id");
		activateOrDeactivateUser(true,userId);
	});
	$('.um-tag-item-icn').click(function() {
		var branchIdToUnassign = $(this).parent().parent().attr("id");
		branchIdToUnassign = branchIdToUnassign.substr("branch-to-unassign-".length);
		var userId = $(this).closest('.row').attr("id");
		unassignUserFromBranch(userId, branchIdToUnassign);
	});
	
	$('#um-assignto').click(function(){
		$(this).parent().find('.um-branch-list').toggle();
	});
	
	$(document).ready(function() {
		var branchListHtml = $('#branch-list').html();
		console.log(branchListHtml);
		$('#um-assignto').parent().append(branchListHtml);
	});
</script>
