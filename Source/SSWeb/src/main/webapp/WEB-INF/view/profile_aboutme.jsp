<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
</c:if>
					<div class="main-con-header main-con-header-adj clearfix">
						<div class="float-left">
							<spring:message code="label.about.key" /> ${contactdetail.name}
						</div>
						<div class="float-left">
							<c:choose>
								<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">
									${contactdetail.about_me}
								</c:when>
								<c:otherwise>
									<spring:message code="label.aboutcompany.empty.key" />
									<input type="hidden" id="aboutme-status" value="new"/>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<div class="intro-body" id="intro-body-text">
						<c:choose>
							<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">
								${contactdetail.about_me}
								<input type="hidden" id="aboutme-status" value="edited"/>
							</c:when>
							<c:otherwise><spring:message code="label.aboutcompany.empty.key" /></c:otherwise>
						</c:choose>
					</div>
					<textarea class="sb-txtarea hide" id="intro-body-text-edit"></textarea>
