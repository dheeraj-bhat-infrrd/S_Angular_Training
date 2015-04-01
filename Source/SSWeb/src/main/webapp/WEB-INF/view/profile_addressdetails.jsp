<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:if test="${not empty profileSettings && not empty profileSettings.contact_details}">
	<c:set value="${profileSettings.contact_details}" var="contactdetail"></c:set>
</c:if>

<div class="prof-user-addline1 prof-edditable prof-addr-center" >${contactdetail.name}</div>
<c:if test="${not empty contactdetail.address}">
	<div class="prof-user-addline2 prof-edditable prof-addr-center" >${contactdetail.address}</div>
</c:if>
<c:if test="${not empty contactdetail.country && not empty contactdetail.zipcode}">
	<div class="prof-user-addline3 prof-edditable prof-addr-center" >${contactdetail.country}, ${contactdetail.zipcode}</div>
</c:if>