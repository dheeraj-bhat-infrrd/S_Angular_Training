<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>
<c:choose>
	<c:when test="${columnName == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${columnName == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>
<c:choose>
	<c:when test="${profilemasterid == 4}">
		<c:choose>
			<c:when test="${not empty profileSettings.contact_details && not empty profileSettings.contact_details.industry}">
				<c:set value="${profileSettings.contact_details.industry}" var="verticalVal"></c:set>
			</c:when>
			<c:when test="${not empty profileSettings.vertical}">
				<c:set value="${profileSettings.vertical}" var="verticalVal"></c:set>
			</c:when>
		</c:choose>
	</c:when>
	<c:otherwise>
		<c:set value="${profileSettings.vertical}" var="verticalVal"></c:set>
	</c:otherwise>
</c:choose>

<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
	<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
	<div class="prof-edit-field-icn hide"></div>
</div>
<div class="prof-address">
	<div class="prof-edditable-cont">
		<input id="prof-title" class="prof-addline2 prof-edditable" value="${contactdetail.title}"
			placeholder='<spring:message code="label.profiletitle.placeholder.key"/>'>
		<div id="prof-title-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
		<div class="prof-edit-field-icn hide"></div>
	</div>
	<div class="prof-edditable-cont">
		<input id="prof-vertical" class="prof-addline2 prof-edditable" value="${verticalVal}"
			placeholder='<spring:message code="label.profilevertical.placeholder.key"/>'>
		<div id="prof-vertical-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
		<div class="prof-edit-field-icn hide"></div>
	</div>
	<div class="prof-edditable-cont">
		<input id="prof-location" class="prof-addline2 prof-edditable" value="${contactdetail.location}"
			placeholder='<spring:message code="label.location.placeholder.key"/>'>
		<div id="prof-location-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
		<div class="prof-edit-field-icn hide"></div>
	</div>
</div>
<div id="prof-rating-review-count" class="prof-rating clearfix">
	<div class="st-rating-wrapper maring-0 clearfix float-left" id="rating-avg-comp"></div>
	<div class="float-left review-count-left cursor-pointer" id="prof-company-review-count"></div>
</div>
<script>
	if (verticalsMasterList == undefined) {
		callAjaxGETWithTextData("/fetchverticalsmaster.do", function(data) {
			var parsedData = JSON.parse(data);
			if (parsedData.errCode == undefined) {
				verticalsMasterList = parsedData;
				initializeVerticalAutcomplete();
			}
		}, true, {});
	} else {
		initializeVerticalAutcomplete();		
	}
</script>