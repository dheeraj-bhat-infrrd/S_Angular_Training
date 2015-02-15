<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
</c:if>
<input id="prof-name" class="prof-user-addline1 prof-edditable" value="${contactdetail.name}">
<input id="prof-address1" class="prof-user-addline1 prof-edditable" value="${contactdetail.address1}">
<input id="prof-address2" class="prof-user-addline2 prof-edditable" value="${contactdetail.address2}">
<input id="prof-country" class="prof-user-addline2 prof-edditable" value="${contactdetail.country}">
<input id="prof-zipcode" class="prof-user-addline2 prof-edditable" value="${contactdetail.zipcode}">