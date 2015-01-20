<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-sub-header clearfix">
     <div class="float-left hm-sub-header-left"><spring:message code="label.existingbranch.key"/></div>
     <div class="float-right">
         <div class="clearfix hm-sub-search-wrapper">
             <div class="float-left">
                 <input class="hm-sub-search-txt" placeholder='<spring:message code="label.searchbranch.key"/>'> 
             </div>
             <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
         </div>
     </div>
 </div>
<c:choose>
	<c:when test = "${not empty branches}">
		<c:forEach var="branch" items="${branches}">
			<div class="hm-sub-item clearfix">
			    <div class="float-left hm-sub-item-left branch-element" data-branchid = "${branch.branchId}" data-regionid = "${branch.region.regionId}" data-regionname = "${branch.region.region}">${branch.branch}</div>
			    <div class="float-right icn-remove cursor-pointer hm-item-height-adjust" onclick ="deleteBranchPopup('${branch.branchId}')"></div>
			</div>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<spring:message code="label.nobranchexist.key"/>
	</c:otherwise>
</c:choose>