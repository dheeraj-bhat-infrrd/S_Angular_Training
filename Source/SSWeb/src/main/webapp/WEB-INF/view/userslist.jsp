<!-- User table list in user management page -->
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty usersList }">
	<c:forEach items="${usersList }" var="user">
		<tr id="um-user-${user.userId }" class="um-user-row">
			<td class="col-username um-table-content">${user.firstName} <span>${user.lastName }</span></td>
			<td class="col-email um-table-content">${user.emailId }</td>
			<td class="col-loanoff um-table-content clearfix">
				<div class="float-left tm-table-tick-icn icn-right-tick"></div>
			</td>
			<td class="col-status um-table-content clearfix">
			<!-- Fill the details to show the user status -->
			<c:choose>
				<c:when test="${user.status eq 1 }">
					<div class="tm-table-status-icn icn-green-col float-left"></div>
					<div class="tm-table-status-icn icn-green-brown float-left hide"></div>
				</c:when>
				<c:when test="${user.status eq 3 }">
					<div class="tm-table-status-icn icn-green-col float-left hide"></div>
					<div class="tm-table-status-icn icn-green-brown float-left"></div>
				</c:when>
			</c:choose>
			</td>
			<td class="col-remove um-table-content clearfix">
				<div class="tm-table-remove-icn icn-remove-user float-left"></div>
			</td>
		</tr>
	</c:forEach>
</c:if>
<script>
$('.um-user-row').click(function(){
	console.log("user row clicked");
	var userId = this.id;
	userId = userId.substr("um-user-".length);
	paintUserDetailsForm(userId);
});
$(document).ready(function(){
	var hasMoreUsers = ${hasMoreUsers};
	if(!hasMoreUsers){
		doStopAjaxRequestForUsersList = true;
	}
});
</script>