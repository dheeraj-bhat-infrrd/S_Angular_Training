<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profileSettings.vertical}" var="companyvertical"></c:set>
	<c:set value="${profileSettings.lockSettings}" var="lock"></c:set>
</c:if>

<div id="prof-name-container" class="lp-edit-wrapper clearfix prof-edditable-cont">
	<input id="prof-name" class="prof-name prof-name-txt prof-edditable" value="${contactdetail.name}">
	<div class="prof-edit-field-icn hide"></div>
</div>
<div class="prof-address">
	<c:if test="${profilemasterid != 1}">
		<div class="prof-edditable-cont">
			<input id="prof-title" class="prof-addline2 prof-edditable" value="${contactdetail.title}" placeholder='<spring:message code="label.profiletitle.placeholder.key"/>'>
			<div id="prof-title-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
			<div class="prof-edit-field-icn hide"></div>
		</div>
	</c:if>
	<div class="prof-edditable-cont">
		<input id="prof-vertical" class="prof-addline2 prof-edditable" value="${profileSettings.vertical}"
			placeholder='<spring:message code="label.profilevertical.placeholder.key"/>'>
		<div id="prof-vertical-lock" data-state="unlocked" data-control="user" class="hide float-left"></div>
		<div class="prof-edit-field-icn hide"></div>
	</div>
</div>
<div id="prof-rating-review-count" class="prof-rating clearfix">
	<div class="st-rating-wrapper maring-0 clearfix float-left"
		id="rating-avg-comp"></div>
	<div class="float-left review-count-left cursor-pointer"
		id="prof-company-review-count"></div>
</div>