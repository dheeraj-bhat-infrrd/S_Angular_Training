<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty cannonicalusersettings && not empty cannonicalusersettings.companySettings && not empty cannonicalusersettings.companySettings.contact_details}">
	<c:set value="${cannonicalusersettings.companySettings.contact_details}" var="contactDetails"></c:set>
</c:if>
<div class="main-con-header"><spring:message code="label.about.key" /> ${contactDetails.name}</div>
<div class="intro-body" id="intro-body-text">
	<c:choose>
		<c:when	test="${not empty contactDetails.about_me && not empty fn:trim(contactDetails.about_me)}">
			${contactDetails.about_me}
		</c:when>
		<c:otherwise><spring:message code="label.aboutcompany.empty.key" /></c:otherwise>
	</c:choose>
</div>
<textarea class="sb-txtarea hide" id="intro-body-text-edit"></textarea>