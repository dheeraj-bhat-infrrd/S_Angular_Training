<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<c:if test ="${not empty users}">
	<c:forEach var="branchUser" items="${users}">
		<tr id="user-row-${branchUser.userId}" clicked="false" data-userid="${branchUser.userId}"
			class="v-tbl-row v-tbl-row-sel v-tbl-row-ind sel-r${regionId}-b${branchId}-u${branchUser.userId} user-row-${branchId}">
			<td class="v-tbl-line"><div class="v-line-ind"></div></td>
			<td class="v-tbl-name">${branchUser.displayName}</td>
			<td class="v-tbl-add"><c:if test="${not empty branchUser.emailId}">${branchUser.emailId}</c:if></td>
			<td class="v-tbl-role">
				<c:choose>
					<c:when test="${(branchUser.isRegionAdmin || branchUser.isBranchAdmin) && branchUser.isAgent}">
						<spring:message code="label.admin.key" />&#44;&nbsp;<spring:message code="label.user.key" />
					</c:when>
					<c:when test="${branchUser.isRegionAdmin || branchUser.isBranchAdmin}">
						<spring:message code="label.admin.key" />
					</c:when>
					<c:when test="${branchUser.isAgent}">
						<spring:message code="label.user.key" />
					</c:when>
				</c:choose>
			</td>
			<td class="v-tbl-btns">
				<div class="clearfix v-tbl-icn-wraper">
					<c:choose>
						<c:when test="${branchUser.canEdit && user.userId != branchUser.userId}">
							<div class="float-left v-tbl-icn v-icn-close user-del-icn" data-userid="${branchUser.userId}"></div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn-disabled v-icn-close"></div>
						</c:otherwise>
				   </c:choose>
                   <c:choose>
						<c:when test="${branchUser.canEdit}">
							<div class="float-left v-tbl-icn v-icn-edit user-edit-icn" clicked="false" data-userid="${branchUser.userId}"></div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn-disabled v-icn-edit"></div>
						</c:otherwise>
				   </c:choose>
               </div>
           </td>
           <td class="v-tbl-spacer"></td>
       </tr>
       <tr class="v-tbl-row v-tbl-row-sel user-edit-row hide">
			<td colspan="7" id="user-details-and-assignments-${branchUser.userId}" class="td-user-edit user-assignment-edit-div">
    			<!--edit form comes here for the user -->
    		</td>
       </tr>
    </c:forEach>
</c:if> 