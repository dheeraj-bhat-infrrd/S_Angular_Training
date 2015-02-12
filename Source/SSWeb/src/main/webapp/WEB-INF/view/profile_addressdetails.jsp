<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set
	value="${cannonicalusersettings.companySettings.contact_details }"
	var="contactDetails"></c:set>
<input id="prof-name" class="prof-name prof-edditable"
	value="${contactDetails.name }">
<div class="prof-rating">
	<div class="st-rating-wrapper maring-0 clearfix">
		<div class="rating-star icn-full-star"></div>
		<div class="rating-star icn-full-star"></div>
		<div class="rating-star icn-half-star"></div>
		<div class="rating-star icn-no-star"></div>
		<div class="rating-star icn-no-star"></div>
	</div>
</div>
<div class="prof-address">
	<input id="prof-address1" class="prof-addline1 prof-edditable"
		value="${contactDetails.address1 }"> <input id="prof-address2"
		class="prof-addline2 prof-edditable"
		value="${contactDetails.address2 }">
</div>
