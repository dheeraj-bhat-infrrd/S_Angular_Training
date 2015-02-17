<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
</c:if>
<div id="prof-address-edit-container" class="prof-user-address prof-edit-icn">
	<input id="prof-name" class="pu-edit-fields" value="${contactdetail.name}">
	<input id="prof-address1" class="pu-edit-fields" value="${contactdetail.address1}">
	<input id="prof-address2" class="pu-edit-fields" value="${contactdetail.address2}">
	<input id="prof-country" class="pu-edit-fields" value="${contactdetail.country}">
	<input id="prof-zipcode" class="pu-edit-fields" value="${contactdetail.zipcode}">
</div>