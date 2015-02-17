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
								<c:when	test="${lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="locked" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="unlocked" class="lp-edit-locks float-left"></div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<div class="intro-body" id="intro-body-text">
						<c:choose>
							<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">${contactdetail.about_me}</c:when>
							<c:otherwise><spring:message code="label.aboutcompany.empty.key" /></c:otherwise>
						</c:choose>
					</div>
					<textarea class="sb-txtarea hide" id="intro-body-text-edit"></textarea>
