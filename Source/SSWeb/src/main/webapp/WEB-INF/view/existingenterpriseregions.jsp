<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-sub-header clearfix">
    <div class="float-left hm-sub-header-left"><spring:message code="label.existingregion.key"/></div>
    <div class="float-right">
        <div class="clearfix hm-sub-search-wrapper">
            <div class="float-left">
                <input id = "search-region-txt" class="hm-sub-search-txt" placeholder='<spring:message code="label.searchregion.key"/>'> 
            </div>
            <div class="float-left icn-search cursor-pointer hm-sub-height-adjust"></div>
        </div>
    </div>
</div>
<div id="existing-regions">
	<c:choose>
		<c:when test = "${not empty regions}">
			<input type="hidden" id="enable-branches-form" value="true">
			<c:forEach var="region" items="${regions}">
				<div class="hm-sub-item clearfix">
				    <div class="float-left hm-sub-item-left region-element" data-regionid = "${region.regionId}">${region.region}</div>
				    <div class="float-right icn-remove cursor-pointer hm-item-height-adjust" onclick="deleteRegionPopup('${region.regionId}')"></div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<spring:message code="label.noregionexist.key"/>
		</c:otherwise>
	</c:choose>
</div>