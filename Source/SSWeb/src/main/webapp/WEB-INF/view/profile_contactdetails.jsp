<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings && not empty cannonicalusersettings.companySettings.contact_details}">
	<c:set value="${cannonicalusersettings.companySettings.contact_details.mail_ids}" var="mailIds"></c:set>
	<c:set value="${cannonicalusersettings.companySettings.contact_details.contact_numbers}" var="contactNumbers"></c:set>
	<c:set value="${cannonicalusersettings.companySettings.contact_details.web_addresses}" var="webAddresses"></c:set>
</c:if>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-mail"></div>
	<%-- <input class="float-left lp-con-row-item prof-edditable-sin" data-email="work" value="${mailIds.work}" readonly="readonly"> --%>
	<div class="float-left lp-con-row-item" data-email="work">${mailIds.work}</div>
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-web"></div>
	<div>
		<input class="float-left lp-con-row-item blue-text prof-edditable-sin" data-web-address="work" value="${webAddresses.work}"
			placeholder='<spring:message code="label.webaddress.placeholder.key"/>'>
	  	<div>
	  		<input type="button" value="lock" class="ep-lock ep-lock-btn ep-ulock-btn" style="height: 24px;line-height: 20px;">
	  	</div>
	</div>
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-phone"></div>
	<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="work" value="${contactNumbers.work}">
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-mbl"></div>
	<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="personal" value="${ contactNumbers.personal}"
		placeholder='<spring:message code="label.personalnumber.placeholder.key"/>'>
</div>
<div class="lp-con-row lp-row clearfix">
	<div class="float-left lp-con-icn icn-fax"></div>
	<input class="float-left lp-con-row-item prof-edditable-sin" data-phone-number="fax" value="${contactNumbers.fax}"
		placeholder='<spring:message code="label.fax.placeholder.key"/>'>
</div>