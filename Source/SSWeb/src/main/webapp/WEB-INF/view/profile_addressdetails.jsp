<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty profile}">
	<c:set value="${profile.profilesMaster.profileId}" var="profilemasterid"></c:set>
</c:if>
<c:choose>
	<c:when test="${profilemasterid == 4 && not empty  profileSettings && not empty profileSettings.contact_details && empty profileSettings.contact_details.address1}">
		<c:if test="${not empty profileSettings.companyProfileData}">
			<c:set value="${profileSettings.companyProfileData}" var="contactdetail"></c:set>
		</c:if>
	</c:when>
	<c:otherwise>
		<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
			<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
		</c:if>
	</c:otherwise>
</c:choose>
<c:if test="${not empty contactdetail.address1}">
	<div class="prof-user-addline1 prof-addr-center">${contactdetail.address1}</div>
</c:if>
<c:if test="${not empty contactdetail.address2}">
	<div class="prof-user-addline2 prof-addr-center">${contactdetail.address2}</div>
</c:if>
<div class="prof-user-addline3 prof-addr-center">
	<c:if test="${not empty  contactdetail.city}">${contactdetail.city}, </c:if>
	<c:if test="${not empty  contactdetail.state}">${contactdetail.state} </c:if>
	<c:if test="${not empty contactdetail.zipcode}">${contactdetail.zipcode}</c:if>
</div>
