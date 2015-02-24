<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<c:set value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" var="user" />
<c:if test="${not empty profile && not empty profile.contact_details}">
	<c:set value="${profile.contact_details}" var="contactdetail"></c:set>
	<c:set value="${profile.lockSettings}" var="lock"></c:set>
</c:if>
					<div class="main-con-header main-con-header-adj clearfix">
						<div class="float-left">
							<spring:message code="label.about.key" /> ${contactdetail.name}
						</div>
						<div class="float-left">
							<c:choose>
								<c:when	test="${parentLock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${parentLock.isAboutMeLocked && user.agent}">
									<div id="aboutme-lock" data-state="locked" data-control="parent" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="hide lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left lp-edit-locks-locked"></div>
								</c:when>
								<c:when	test="${not parentLock.isAboutMeLocked && not lock.isAboutMeLocked && not user.agent}">
									<div id="aboutme-lock" data-state="unlocked" data-control="user" class="lp-edit-locks float-left"></div>
								</c:when>
							</c:choose>
						</div>
					</div>
					<c:choose>
						<c:when	test="${not empty contactdetail.about_me && not empty fn:trim(contactdetail.about_me)}">
							<div class="pe-whitespace intro-body" id="intro-body-text">${fn:trim(contactdetail.about_me)}</div>
							<textarea class="pe-whitespace sb-txtarea hide" id="intro-body-text-edit">${fn:trim(contactdetail.about_me)}</textarea>
						</c:when>
						<c:otherwise>
							<div class="intro-body" id="intro-body-text">
								<spring:message code="label.aboutcompany.empty.key" />
							</div>
							<input type="hidden" id="aboutme-status" value="new"/>
							<textarea class="pe-whitespace sb-txtarea hide" id="intro-body-text-edit"></textarea>
						</c:otherwise>
					</c:choose>
