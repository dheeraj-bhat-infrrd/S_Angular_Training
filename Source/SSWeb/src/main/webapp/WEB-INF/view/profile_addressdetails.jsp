<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set value="${cannonicalusersettings.companySettings.contact_details}" var="contactdetail"></c:set>

<input id="prof-name" class="prof-user-addline1 prof-edditable" value="${contactdetail.name}">
<input id="prof-address1" class="prof-user-addline1 prof-edditable" value="${contactdetail.address1}">
<input id="prof-address2" class="prof-user-addline2 prof-edditable" value="${contactdetail.address2}">
<input id="prof-country" class="prof-user-addline2 prof-edditable" value="${contactdetail.country}">
<input id="prof-zipcode" class="prof-user-addline2 prof-edditable" value="${contactdetail.zipcode}">
