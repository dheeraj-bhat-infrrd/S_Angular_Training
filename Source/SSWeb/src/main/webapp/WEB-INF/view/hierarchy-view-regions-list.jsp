<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:if test ="${not empty regions}">
	<c:forEach var="region" items="${regions}">
		<tr id="tr-region-${region.regionId}" clicked="false" class="v-tbl-row v-tbl-row-sel region-row" data-regionid="${region.regionId}">
	           <td class="v-tbl-line">
	               <div class="v-line-rgn"></div>
	           </td>
	           <td class="v-tbl-name">${region.regionName}</td>
	           <td class="v-tbl-add"><c:if test="${not empty region.address1}">${region.address1}</c:if>&nbsp;<c:if test="${not empty region.address2}">${region.address2}</c:if></td>
	           <td class="v-tbl-role"></td>
	           <td class="v-tbl-btns">
	               <div class="clearfix v-tbl-icn-wraper">
	                   <div class="float-left v-tbl-icn v-icn-close" data-regionid="${region.regionId}"></div>
	                   <div class="float-left v-tbl-icn v-icn-edit region-edit-icn" clicked="false" data-regionid="${region.regionId}"></div>
	               </div>
	           </td>
	           <td class="v-tbl-spacer"></td>
	       </tr>
	       <tr class="v-tbl-row v-tbl-row-sel tr-region-edit hide">
	      		<td colspan="7" id="td-region-edit-${region.regionId}" class="td-region-edit">
	      			<!--edit form comes here  -->
	      		</td>
	       </tr>
    </c:forEach>
</c:if> 
<c:if test ="${not empty branches}">
	<c:forEach var="branch" items="${branches}">
		<tr id="tr-branch-${branch.branchId}" clicked="false" class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row sel-b${branch.branchId}" data-branchid="${branch.branchId}">
	           <td class="v-tbl-line">
	               <div class="v-line-brnch v-line-comp-brnch"></div>
	           </td>
	           <td class="v-tbl-name">${branch.branchName}</td>
	           <td class="v-tbl-add"><c:if test="${not empty branch.address1}">${branch.address1}</c:if>&nbsp;<c:if test="${not empty branch.address2}">${branch.address2}</c:if></td>
	           <td class="v-tbl-role"></td>
	           <td class="v-tbl-btns">
	               <div class="clearfix v-tbl-icn-wraper">
	                   <div class="float-left v-tbl-icn v-icn-close" data-branchid="${branch.branchId}"></div>
	                   <div class="float-left v-tbl-icn v-icn-edit branch-edit-icn" clicked="false" data-branchid="${branch.branchId}"></div>
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
		<tr id="tr-user-${compUser.userId}" clicked="false" class="v-tbl-row v-tbl-row-sel v-tbl-row-ind sel-u${compUser.userId}" data-userid="${compUser.userId}">
	           <td class="v-tbl-line">
	               <div class="v-line-ind v-line-comp-ind"></div>
	           </td>
	           <td class="v-tbl-name">${compUser.displayName}</td>
	           <td class="v-tbl-add"><c:if test="${not empty compUser.emailId}">${compUser.emailId}</c:if></td>
	           <td class="v-tbl-role"></td>
	           <td class="v-tbl-btns">
	               <div class="clearfix v-tbl-icn-wraper">
	                   <div class="float-left v-tbl-icn v-icn-close" data-userid="${compUser.userId}"></div>
	                   <div class="float-left v-tbl-icn v-icn-edit user-edit-icn" clicked="false" data-userid="${compUser.userId}"></div>
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