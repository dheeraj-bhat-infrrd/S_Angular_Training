<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Account masters 1=Individual, 2=Team, 3=Company,4=Enterprise,5=Free Account -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:set var="accountTypeId" value="${user.company.licenseDetails[0].accountsMaster.accountsMasterId}" />

<table class="v-um-tbl">
	<tr id="u-tbl-header" class="u-tbl-header" data-num-found="${numFound}">
		<td class="v-tbl-uname"><spring:message code="label.usermanagement.username.key" /></td>
		<td class="v-tbl-email"><spring:message code="label.emailid.key" /></td>
		<td class="v-tbl-rgn-adm text-center">

		<td class="v-tbl-ln-of text-center"><spring:message code="label.action.key" /></td>
	</tr>
	<c:choose>
		<c:when test="${not empty userslist}">
			<c:forEach var="userfromsearch" items="${userslist}">


				<tr class="u-tbl-row user-row" id="user-row-${userfromsearch.userId}">
					<td class="v-tbl-uname fetch-name" data-first-name="${userfromsearch.firstName}" data-last-name="${userfromsearch.lastName}"
						data-user-id="${userfromsearch.userId}">${userfromsearch.firstName} ${userfromsearch.lastName}</td>
					<td class="v-tbl-email fetch-email">${userfromsearch.emailId}</td>
					
					<td class="v-tbl-rgn-adm text-center"></td>
					<td class="v-tbl-btns v-tbl-btns-um">
						<div class="v-tbn-icn-dropdown hide"></div>
						<div class="clearfix v-tbl-icn-wraper v-um-tbl-icn-wraper">
							<div class="v-tbl-rem v-icn-rem-ssadmin margin-left-125" data-user-id="${userfromsearch.userId}" title="<spring:message code="label.remove.key" />">Delete</div>
					
					   </div>
				   </td>
				</tr>
				<%-- <tr class="u-tbl-row u-tbl-row-sel hide user-assignment-edit-row">
					<td id="user-details-and-assignments-${userfromsearch.userId}" class="u-tbl-edit-td user-assignment-edit-div" colspan="6">
						<!-- data populated from um-edit-row.jsp -->
					</td>
				</tr> --%>
				
			</c:forEach>
		</c:when>
		<c:otherwise>
			<tr class="u-tbl-row"><spring:message code="label.nousersfound.key" /></tr>
		</c:otherwise>
	</c:choose>
</table>
<script>
	bindAppUserLoginEvent();
</script>