<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page isELIgnored="false"%>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.header.usermanagement.key" />
			</div>
		</div>
	</div>
</div>
<div id="server-message" class="hide">
	<jsp:include page="usermanagementmessageheader.jsp"></jsp:include>
</div>
<div id="message-header" class="hide"></div>
<div id="hm-main-content-wrapper" data-admin-id="${user.userId }"
	class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="um-top-container">
			<div class="um-header clearfix">
				<div class="float-left">
					<spring:message code="label.userdetails.key" />
				</div>
				
			</div>
			<div class="clearfix um-panel-content" id="user-details-container">
				<!--User details content will be populated here through userdetails.jsp page-->
			</div>
		</div>
		<div class="um-bottom-container">
			<div class="um-header">
				<div class="float-left"><spring:message code="label.usermanagement.head.browseruser.key" /></div>
				<div class="float-right">
					<input type="text" placeholder="Search User" class="um-search-user" onkeyup="searchUsersByNameEmailLoginId(this.value)">
				</div>
			</div>
			<div class="clearfix um-panel-content um-bottom-content"
				id="um-user-list-container">
				
				<table class="um-table" id="um-user-list">
					<thead>
						<tr>
							<td class="col-username"><spring:message
									code="label.usermanagement.username.key" /></td>
							<td class="col-email"><spring:message
									code="label.emailid.key" /></td>
							<td class="col-loanoff"><spring:message
									code="label.agent.key" /></td>
							<td class="col-status"></td>
							<td class="col-remove"></td>
						</tr>
					</thead>
					<tbody>
						<!-- User table list is populated here through userlist.jsp page -->
					</tbody>
				</table>
				<div id="um-view-more-users" class="um-view-more" onclick="paginateUsersList();"><span>View more</span></div>
			</div>
			
			<%-- <div id="branch-list" class="hide">
				<c:if test="${not empty branches }">
					<div class="um-branch-list hide">
					<c:forEach items="${branches }" var="branch">
						<div class="um-dd-wrapper cursor-pointer" id="branch-${branch.branchId }" onclick="selectBranch(this);">${branch.branch }</div>
					</c:forEach>
					</div>
				</c:if>
			</div> --%>
			
		</div>
	</div>
</div>
<style>
	.um-dd-wrapper {
	    width: 300px;
	    background-color: #fff;
	    padding: 8px;
	    border: 1px solid #dcdcdc;
	}
	.um-branch-list{
		position: absolute;
	}
</style>
<script>
	$(document).ready(function(){
		doStopAjaxRequestForUsersList = false;
		if($('#server-message>div').hasClass("error-message")){
			isUserManagementAuthorized = false;
			$('#server-message').show();
			//var errorMessage = $('#server-message p').text();
		}
		initUserManagementPage();		
	});
</script>