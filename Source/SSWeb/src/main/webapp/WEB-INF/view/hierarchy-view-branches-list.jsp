<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:if test="${not empty branches}">
	<c:forEach var="branch" items="${branches}">
		<tr id="tr-branch-row-${branch.branchId}" data-regionid="${branch.regionId}" data-branchid="${branch.branchId}" clicked="false" class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row sel-r${branch.regionId}-b${branch.branchId}">
		    <td class="v-tbl-line">
		        <div class="v-line-brnch"></div>
		    </td>
		    <td class="v-tbl-name">${branch.branchName}</td>
		    <td class="v-tbl-add"><c:if test="${not empty branch.address}">${branch.address}</c:if></td>
		    <td class="v-tbl-role"></td>
		    <td class="v-tbl-btns">
		        <div class="clearfix v-tbl-icn-wraper">
		            <div class="float-left v-tbl-icn v-icn-close" data-branchid="${branch.branchId}"></div>
		            <div class="float-left v-tbl-icn v-icn-edit branch-edit-icn" clicked="false" data-branchid="${branch.branchId}"></div>
		        </div>
		    </td>
		    <td class="v-tbl-spacer v-tbl-no-bd"></td>
		</tr>
		 <tr class="v-tbl-row v-tbl-row-sel tr-user-edit hide">
	      		<td colspan="7" id="td-reg-edit-${branchUser.userId}" class="td-branch-edit">
	      			<!--edit form comes here for the user -->
	      		</td>
	       </tr>
	</c:forEach>
</c:if>
<c:if test ="${not empty individuals}">
	<c:forEach var="regionUser" items="${individuals}">
		<tr id="tr-user-${regionUser.userId}" clicked="false" class="v-tbl-row v-tbl-row-sel edit-user v-tbl-row-ind sel-r${regionId}-u${regionUser.userId}" data-userid="${regionUser.userId}">
	           <td class="v-tbl-line">
	               <div class="v-line-ind v-line-comp-ind"></div>
	           </td>
	           <td class="v-tbl-name">${regionUser.displayName}</td>
	           <td class="v-tbl-add"><c:if test="${not empty regionUser.emailId}">${regionUser.emailId}</c:if></td>
	           <td class="v-tbl-role"></td>
	           <td class="v-tbl-btns">
	               <div class="clearfix v-tbl-icn-wraper">
	                   <div class="float-left v-tbl-icn v-icn-close" data-userid="${regionUser.userId}"></div>
	                   <div class="float-left v-tbl-icn v-icn-edit user-edit-icn" clicked="false" data-userid="${regionUser.userId}"></div>
	               </div>
	           </td>
	           <td class="v-tbl-spacer"></td>
	       </tr>
	       <tr class="v-tbl-row v-tbl-row-sel tr-user-edit user-edit-row hide">
	      		<td colspan="7" id="user-details-and-assignments-${regionUser.userId}" class="td-user-edit user-assignment-edit-div">
	      			<!--edit form comes here for the user -->
	      		</td>
	       </tr>
    </c:forEach>
</c:if> 