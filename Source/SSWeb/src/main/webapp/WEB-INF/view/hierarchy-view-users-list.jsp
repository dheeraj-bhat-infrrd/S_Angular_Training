<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:choose>
	<c:when test="${user.companyAdmin}">
		<c:set value="1" var="sessionprofilemasterid"></c:set>
	</c:when>
	<c:when test="${user.regionAdmin}">
		<c:set value="2" var="sessionprofilemasterid"></c:set>
	</c:when>
	<c:when test="${user.branchAdmin}">
		<c:set value="3" var="sessionprofilemasterid"></c:set>
	</c:when>
	<c:when test="${user.agent}">
		<c:set value="4" var="sessionprofilemasterid"></c:set>
	</c:when>
</c:choose>
<c:if test ="${not empty users}">
	<c:forEach var="branchUser" items="${users}">
		<c:choose>
			<c:when test="${branchUser.isOwner == 1}">
				<c:set value="1" var="currentprofilemasterid"></c:set>
			</c:when>
			<c:when test="${branchUser.isRegionAdmin}">
				<c:set value="2" var="currentprofilemasterid"></c:set>
			</c:when>
			<c:when test="${branchUser.isBranchAdmin}">
				<c:set value="3" var="currentprofilemasterid"></c:set>
			</c:when>
			<c:when test="${branchUser.isAgent}">
				<c:set value="4" var="currentprofilemasterid"></c:set>
			</c:when>
		</c:choose>
		<!-- If status is 2, then user has not acted on invitation -->
				<c:set var="regstatustickclass" value="" />
				<c:set var="userstatustickclass" value="v-icn-verified" />
				<c:if test="${branchUser.status == 2}">
					<c:set var="regstatustickclass" value="v-icn-femail" />
					<c:set var="userstatustickclass" value="v-icn-notverified" />
				</c:if>

				<!-- if admin can edit -->
				<c:choose>
					<c:when test="${branchUser.canEdit}">
						<c:set var="admincaneditclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincaneditclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>
				
				<c:choose>
					<c:when test="${canDelete}">
						<c:set var="admincandeleteclass" value="v-tbl-icn" />
					</c:when>
					<c:otherwise>
						<c:set var="admincandeleteclass" value="v-tbl-icn-disabled" />
					</c:otherwise>
				</c:choose>
		<tr id="user-row-${branchUser.userId}" clicked="false" data-userid="${branchUser.userId}"
			class="v-tbl-row v-tbl-row-sel v-tbl-row-ind sel-r${regionId}-b${branchId}-u${branchUser.userId} user-row-${branchId}">
			<td class="v-tbl-line"><div class="v-line-ind"></div></td>
			<%-- <td class="v-tbl-img">
           		<c:choose>
    				<c:when test="${not empty branchUser.profileImageUrl}">
        				<div  class="float-left profile-image-display" style="background: url(${branchUser.profileImageUrl}) 50% 50% / cover no-repeat;">
							<span></span>
						</div> 
    				</c:when>    
    				<c:otherwise>
        				<div id="" class="float-left profile-image-display" style="">
							<span id="">${fn:substring(branchUser.displayName, 0, 1)}</span>
						</div> 
    				</c:otherwise>
				</c:choose>
			</td> --%>
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
			<td class="v-tbl-btns v-tbl-btns-hr">
				<div class="v-tbn-icn-dropdown hide"></div>
				<div class="clearfix v-tbl-icn-wraper v-hr-tbl-icn-wraper">
					<c:choose>
						<c:when test="${not empty regstatustickclass}">
							<div class="float-left v-tbl-icn v-tbl-icn-sm  ${admincaneditclass} ${regstatustickclass}"
								title="<spring:message code="label.resendmail.key" />">Resend</div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn  ${admincaneditclass}"></div>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${branchUser.isAgent}">
							<div class="float-left v-tbl-icn v-tbl-icn-sm v-icn-wid ${admincaneditclass}" data-iden="${branchUser.userId}" data-profile="individual"
								title="<spring:message code="label.widget.key" />">Widget</div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-top-spacer"></div>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${regionUser.status == 2}">
							<div class="float-left v-tbl-icn v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.notverified.key" />"></div>
						</c:when>
						<c:otherwise>
							<div class=" float-left v-tbl-icn  v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.verified.key" />"></div>
						</c:otherwise>
					</c:choose>  
					<c:choose>
						<c:when test="${canDelete && user.userId != branchUser.userId}">
							<div class="float-left v-tbl-icn v-tbl-icn-sm v-icn-close user-del-icn" data-userid="${branchUser.userId}">Delete</div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn-disabled v-icn-close"></div>
						</c:otherwise>
				   </c:choose>
                   <c:choose>
						<c:when test="${branchUser.canEdit}">
							<div class="float-left v-tbl-icn v-tbl-icn-sm v-icn-edit user-edit-icn" clicked="false" data-userid="${branchUser.userId}">Edit</div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn-disabled v-icn-edit"></div>
						</c:otherwise>
				   </c:choose>
				   <c:if test="${user.userId != branchUser.userId and sessionprofilemasterid < currentprofilemasterid}">
				   		<div class="float-left v-tbl-icn v-tbl-icn-sm v-icn-login user-login-icn" data-iden="${branchUser.userId}" title="login as">Login</div>
				   </c:if>
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