<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />


<c:if test ="${not empty regions}">
	<c:forEach var="region" items="${regions}">
		<tr id="tr-region-${region.regionId}" clicked="false" class="v-tbl-row v-tbl-row-sel region-row" data-regionid="${region.regionId}">
           <td class="v-tbl-line"><div class="v-line-rgn"></div></td>
           <td class="v-tbl-name">${region.region}</td>
           <td class="v-tbl-add"><c:if
							test="${not empty region.address1}">${region.address1}</c:if>&nbsp;<c:if
							test="${not empty region.address2}">${region.address2}</c:if></td>
           <td class="v-tbl-role"></td>
           <td class="v-tbl-btns v-tbl-btns-hr">
               <div class="v-tbn-icn-dropdown hide"></div>
				<div class="clearfix v-tbl-icn-wraper v-hr-tbl-icn-wraper">
	               <div class="float-left v-tbl-top-spacer"></div>
                   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-wid" 
                   		data-iden="${region.regionId}" data-profile="region"
                  	 	title="<spring:message code="label.widget.key" />">Widget</div> 
	               <div class="float-left v-tbl-top-spacer"></div>  
	               <c:if test="${canDelete}">
                   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-close region-del-icn" data-regionid="${region.regionId}">Delete</div>
                   </c:if>
                   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-edit region-edit-icn" clicked="false" data-regionid="${region.regionId}">Edit</div>
               	   <div class="float-left v-tbl-top-spacer"></div>
               </div>
           </td>
           <td class="v-tbl-spacer"></td>
       </tr>
       <tr class="v-tbl-row v-tbl-row-sel tr-region-edit hide">
      		<td colspan="7" id="td-region-edit-${region.regionId}" class="td-region-edit">
      		</td>
       </tr>
    </c:forEach>
</c:if> 
<c:if test ="${not empty branches}">
	<c:forEach var="branch" items="${branches}">
		<tr id="tr-branch-row-${branch.branchId}" clicked="false" data-branchid="${branch.branchId}"
			class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row sel-b${branch.branchId}">
           <td class="v-tbl-line"><div class="v-line-brnch v-line-comp-brnch"></div></td>
           <td class="v-tbl-name">${branch.branch}</td>
           <td class="v-tbl-add"><c:if
							test="${not empty branch.address1}">${branch.address1}</c:if>&nbsp;<c:if
							test="${not empty branch.address2}">${branch.address2}</c:if></td>
           <td class="v-tbl-role"></td>
           <td class="v-tbl-btns v-tbl-btns-hr">
                <div class="v-tbn-icn-dropdown hide"></div>
				<div class="clearfix v-tbl-icn-wraper v-hr-tbl-icn-wraper">
					<div class="float-left v-tbl-top-spacer"></div>
					<div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-wid" 
						data-iden="${branch.branchId}" data-profile="branch"
						title="<spring:message code="label.widget.key" />">Widget</div>
					<div class="float-left v-tbl-top-spacer"></div>
					 <c:when test="${canDelete}">
					<div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-close branch-del-icn" data-branchid="${branch.branchId}">Delete</div>
					</c:when>
                   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-edit branch-edit-icn" clicked="false" data-branchid="${branch.branchId}">Edit</div>
              <div class="float-left v-tbl-top-spacer"></div>
               </div>
           </td>
           <td class="v-tbl-spacer"></td>
       </tr>
       <tr class="v-tbl-row v-tbl-row-sel tr-branch-edit hide">
      		<td colspan="7" id="td-branch-edit-${branch.branchId}" class="td-branch-edit">
      			<!--edit form for branch comes here  -->
      		</td>
       </tr>
    </c:forEach>
</c:if> 
<c:if test ="${not empty individuals}">
	<c:forEach var="compUser" items="${individuals}">
	
	<!-- If status is 2, then user has not acted on invitation -->
				<c:set var="regstatustickclass" value="" />
				<c:set var="userstatustickclass" value="v-icn-verified" />
				<c:if test="${compUser.status == 2}">
					<c:set var="regstatustickclass" value="v-icn-femail" />
					<c:set var="userstatustickclass" value="v-icn-notverified" />
				</c:if>

				<!-- if admin can edit -->
				<c:choose>
					<c:when test="${compUser.canEdit}">
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
	
	
	
		<tr id="user-row-${compUser.userId}" clicked="false" data-userid="${compUser.userId}"
			class="v-tbl-row v-tbl-row-sel v-tbl-row-ind sel-u${compUser.userId}">
           <td class="v-tbl-line"><div class="v-line-ind v-line-comp-ind"></div></td>
           <td class="v-tbl-name">${compUser.displayName}</td>
           <td class="v-tbl-add"><c:if test="${not empty compUser.emailId}">${compUser.emailId}</c:if></td>
           <td class="v-tbl-role">
				<c:choose>
					<c:when test="${(compUser.isRegionAdmin || compUser.isBranchAdmin) && compUser.isAgent}">
						<spring:message code="label.admin.key" />&#44;&nbsp;<spring:message code="label.user.key" />
					</c:when>
					<c:when test="${compUser.isRegionAdmin || compUser.isBranchAdmin}">
						<spring:message code="label.admin.key" />
					</c:when>
					<c:when test="${compUser.isAgent}">
						<spring:message code="label.user.key" />
					</c:when>
				</c:choose>
			</td>
           	<td class="v-tbl-btns v-tbl-btns-hr">
              	<div class="v-tbn-icn-dropdown hide"></div>
				<div class="clearfix v-tbl-icn-wraper v-hr-tbl-icn-wraper">
                <c:choose>
						<c:when test="${not empty regstatustickclass}">
							<div class="float-left v-tbl-icn-sm v-tbl-icn  ${admincaneditclass} ${regstatustickclass}"
								title="<spring:message code="label.resendmail.key" />"></div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn  ${admincaneditclass}"></div>
						</c:otherwise>
					</c:choose>
					<c:choose>
					<c:when test="${compUser.isAgent}">
					  <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-wid ${admincaneditclass}" 
					  	   data-iden="${compUser.userId}" data-profile="individual"
						   title="<spring:message code="label.widget.key" />">Widget</div> 
					</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-top-spacer"></div>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${compUser.status == 2}">
							<div class="float-left v-tbl-icn v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.notverified.key" />"></div>
						</c:when>
						<c:otherwise>
							<div class=" float-left v-tbl-icn v-tbl-icn ${userstatustickclass}" title="<spring:message code="label.verified.key" />"></div>
						</c:otherwise>
					</c:choose>  
                   <c:choose>
						<c:when test="${canDelete and user.userId != compUser.userId}">
						   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-close user-del-icn" data-userid="${compUser.userId}">Delete</div>
						</c:when>
						<c:otherwise>
							<div class="float-left v-tbl-icn-disabled v-icn-close"></div>
						</c:otherwise>
				   </c:choose>
                   <c:choose>
						<c:when test="${compUser.canEdit}">
		                   <div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-edit user-edit-icn" clicked="false" data-userid="${compUser.userId}">Edit</div>
						</c:when>
						<c:otherwise>
		                    <div class="float-left v-tbl-icn-disabled v-icn-edit"></div>
						</c:otherwise>
				   </c:choose>
				   <c:if test="${user.userId != compUser.userId}">
				   		<div class="float-left v-tbl-icn-sm v-tbl-icn v-icn-login user-login-icn" data-iden="${compUser.userId}" title="login as">Login</div>
				   </c:if>
			   </div>
           </td>
           <td class="v-tbl-spacer"></td>
       </tr>
       <tr id="user-edit-row-${compUser.userId}" class="v-tbl-row v-tbl-row-sel tr-user-edit user-edit-row hide">
      		<td id="user-details-and-assignments-${compUser.userId}" colspan="7" class="td-user-edit user-assignment-edit-div">
      			<!--edit form comes here for the user -->
      		</td>
       </tr>
	</c:forEach>
</c:if> 
