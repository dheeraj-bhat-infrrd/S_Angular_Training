<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:if test="${not empty branches}">
	<c:forEach var="branch" items="${branches}">
		<c:choose>
			<c:when test="${not empty regionId}">
				<c:set var="regionIdVal" value="${regionId }"></c:set>
			</c:when>
			<c:otherwise>
				<c:set var="regionIdVal" value="${branch.region.regionId }"></c:set>
			</c:otherwise>
		</c:choose>
		<tr id="tr-branch-row-${branch.branchId}"
			data-regionid="${regionIdVal}" data-branchid="${branch.branchId}"
			clicked="false"
			class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row sel-r${regionIdVal}-b${branch.branchId}">
			<td class="v-tbl-line">
				<div class="v-line-brnch"></div>
				<i id="tr-spinner-${branch.branchId}"  class="fa fa-spinner fa-pulse fa-2x fa-fw" aria-hidden="true" style="display:none;margin:6px 15px;"></i> 
			</td>
			<%-- <td class="v-tbl-img">
           		<c:choose>
    				<c:when test="${not empty branch.profileImageUrl}">
        				<div  class="float-left profile-image-display" style="background: url(${branch.profileImageUrl}) 50% 50% / cover no-repeat;">
							<span></span>
						</div> 
    				</c:when>    
    				<c:otherwise>
        				<div id="" class="float-left profile-image-display" style="">
							<span id="">${fn:substring(branch.regionName, 0, 1)}</span>
						</div> 
    				</c:otherwise>
				</c:choose>
           	</td> --%>
			<td class="v-tbl-name">${branch.branch}</td>
			<td class="v-tbl-add"><c:if test="${not empty branch.address1}">${branch.address1}</c:if>&nbsp;<c:if
					test="${not empty branch.address2}">${branch.address2}</c:if></td>
			<td class="v-tbl-role"></td>
			<td class="v-tbl-btns">
				<div class="clearfix v-tbl-icn-wraper">
					<div class="float-left v-tbl-icn v-icn-close branch-del-icn vis-hidden"
						data-branchid="${branch.branchId}"></div>
					<div class="float-left v-tbl-icn v-icn-edit branch-edit-icn vis-hidden"
						clicked="false" data-branchid="${branch.branchId}"></div>
				</div>
			</td>
			<td class="v-tbl-spacer v-tbl-no-bd"></td>
		</tr>
		<tr class="v-tbl-row v-tbl-row-sel tr-branch-edit hide">
			<td colspan="7" id="td-branch-edit-${branch.branchId}"
				class="td-branch-edit">
				<!--edit form comes here for the user -->
			</td>
		</tr>
	</c:forEach>
</c:if>
<c:if test="${not empty individuals}">
	<c:forEach var="regionUser" items="${individuals}">
		<tr id="user-row-${regionUser.userId}" clicked="false"
			class="v-tbl-row v-tbl-row-sel edit-user v-tbl-row-ind sel-r${regionId}-u${regionUser.userId}"
			data-userid="${regionUser.userId}">
			<td class="v-tbl-line">
				<div class="v-line-ind v-line-region-ind"></div>
			</td>
			<%-- <td class="v-tbl-img">
           		<c:choose>
    				<c:when test="${not empty regionUser.profileImageUrl}">
        				<div  class="float-left profile-image-display" style="background: url(${regionUser.profileImageUrl}) 50% 50% / cover no-repeat;">
							<span></span>
						</div> 
    				</c:when>    
    				<c:otherwise>
        				<div id="" class="float-left profile-image-display" style="">
							<span id="">${fn:substring(regionUser.displayName, 0, 1)}</span>
						</div> 
    				</c:otherwise>
				</c:choose>
           	</td> --%>
			<td class="v-tbl-name">${regionUser.displayName}</td>
			<td class="v-tbl-add"><c:if
					test="${not empty regionUser.emailId}">${regionUser.emailId}</c:if></td>
			<td class="v-tbl-role"><c:choose>
					<c:when
						test="${regionUser.isRegionAdmin && regionUser.isAgent}">
						<spring:message code="label.regionadmin.key" />&#44;&nbsp;<spring:message
							code="label.user.key" />
					</c:when>
					<c:when
						test="${regionUser.isBranchAdmin && regionUser.isAgent}">
						<spring:message code="label.branchadmin.key" />&#44;&nbsp;<spring:message
							code="label.user.key" />
					</c:when>
					<c:when test="${regionUser.isRegionAdmin}">
					<spring:message code="label.regionadmin.key" />
					</c:when>
					<c:when test="${regionUser.isBranchAdmin}">
					<spring:message code="label.branchadmin.key" />
					</c:when>
					<c:when test="${regionUser.isAgent}">
						<spring:message code="label.user.key" />
					</c:when>
				</c:choose></td>
			<td class="v-tbl-btns">
				<div class="clearfix v-tbl-icn-wraper">
					<div class="float-left v-tbl-icn v-icn-close user-del-icn hidden"
						data-userid="${regionUser.userId}"></div>
					<div class="float-right v-tbl-icn v-icn-login user-login-icn"
						data-iden="${regionUser.userId}" title="login as"></div>
					<div class="float-right v-tbl-icn v-icn-edit user-edit-icn vis-hidden"
						clicked="false" data-userid="${regionUser.userId}"
						data-iscom-admin="${regionUser.isOwner}"></div>
				</div>
			</td>
			<td class="v-tbl-spacer"></td>
		</tr>
		<tr class="v-tbl-row v-tbl-row-sel tr-user-edit user-edit-row hide">
			<td colspan="7"
				id="user-details-and-assignments-${regionUser.userId}"
				class="td-user-edit user-assignment-edit-div">
				<!--edit form comes here for the user -->
			</td>
		</tr>
	</c:forEach>
</c:if>
<script>
	bindUserLoginEvent();
</script>