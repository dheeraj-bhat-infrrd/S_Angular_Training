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
	           <td class="v-tbl-add"><c:if test="${not empty region.address}">${region.address}</c:if></td>
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