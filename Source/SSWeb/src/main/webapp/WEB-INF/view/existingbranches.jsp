<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-bottom-panel margin-bottom-25">
    <div class="hm-bottom-header">
        <div class="hm-sub-header clearfix">
            <div class="float-left hm-sub-header-left"><spring:message code="label.existingbranch.key"/></div>
            <div class="float-right mobile-search-panel">
                <div class="clearfix hm-sub-search-wrapper">
                    <div class="float-left">
                        <input class="hm-sub-search-txt" placeholder='<spring:message code="label.searchbranch.key"/>'> 
                    </div>
                    <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
                </div>
            </div>
        </div>
    </div>
    <c:choose>
    	<c:when test="${not empty branches}">
		    <div class="hm-bottom-panel-content clearfix">
		        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-right-30">
			        <c:forEach items="${branches}" step="2" var="branch">
			            <div class="hm-sub-item clearfix">
			                <div class="float-left hm-sub-item-left branch-element" data-branchid = "${branch.branchId}" data-regionid = "${branch.region.regionId}" data-regionname = "${branch.region.region}">${branch.branch}</div>
			                <div class="float-right icn-remove cursor-pointer hm-item-height-adjust" id="branch-"${branch.branchId} onclick ="deleteBranchPopup('${branch.branchId}')"></div>
			            </div>		            
			        </c:forEach>
		        </div>
		        <div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-bottom-panel-item padding-left-30">
		            <c:forEach items="${branches}" step="2" var="branch" begin="1">
			            <div class="hm-sub-item clearfix">
			                <div class="float-left hm-sub-item-left branch-element" data-branchid = "${branch.branchId}" data-regionid = "${branch.region.regionId}" data-regionname = "${branch.region.region}">${branch.branch}</div>
			                <div class="float-right icn-remove cursor-pointer hm-item-height-adjust" id="branch-"${branch.branchId} onclick ="deleteBranchPopup('${branch.branchId}')"></div>
			            </div>		            
			        </c:forEach>
		        </div>
		    </div>
    	</c:when>
    	<c:otherwise>
    		<spring:message code="label.nobranchexist.key"/>
   		</c:otherwise>
    </c:choose>
</div>