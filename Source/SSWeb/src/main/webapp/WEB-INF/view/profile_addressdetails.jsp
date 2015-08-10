<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:choose>
	<c:when test="${entityType == 'companyId'}">
		<c:set value="1" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'regionId'}">
		<c:set value="2" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'branchId'}">
		<c:set value="3" var="profilemasterid"></c:set>
	</c:when>
	<c:when test="${entityType == 'agentId'}">
		<c:set value="4" var="profilemasterid"></c:set>
	</c:when>
</c:choose>
<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>
<c:if test="${profilemasterid == 4 && empty contactdetail.address1 && not empty profileSettings.companyProfileData}">
	<c:set value="${profileSettings.companyProfileData}" var="contactdetail"></c:set>
</c:if>

<c:if test="${not empty contactdetail.address1}">
	<div class="prof-user-addline1 prof-addr-center">${contactdetail.address1}</div>
</c:if>
<c:if test="${not empty contactdetail.address2}">
	<div class="prof-user-addline2 prof-addr-center">${contactdetail.address2}</div>
</c:if>
<div class="prof-user-addline3 prof-addr-center">
	<c:if test="${not empty contactdetail.city}">${contactdetail.city}, </c:if>
	<c:if test="${not empty contactdetail.state}">${contactdetail.state} </c:if>
	<c:if test="${not empty contactdetail.zipcode}">${contactdetail.zipcode}</c:if>
</div>