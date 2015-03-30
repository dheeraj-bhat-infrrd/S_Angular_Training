<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- in highest roles comparison, 1 = companyAdmin, 2 = regionAdmin, 3 = branchAdmin, 4 = agent, 5 = no profile  -->
<c:if test="${not empty branches}">
	<c:forEach var="branch" items="${branches}">
		<tr id="tr-branch-row-${branch.branchId}" clicked="false" class="v-tbl-row v-tbl-row-sel v-tbl-row-brnch branch-row-${branch.regionId}">
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
		<tr class="v-tbl-row v-tbl-row-sel tr-branch-edit hide">
	      		<td colspan="7" id="td-branch-edit-${branch.branchId}" class="td-branch-edit">
	      			<!--edit form comes here  -->
	      		</td>
	       </tr>
	</c:forEach>
</c:if>