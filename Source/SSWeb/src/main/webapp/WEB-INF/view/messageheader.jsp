<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty message.message || not empty param.message}">
<div class="display-message">
	<div class="error-wrapper clearfix">
        <div class="float-left ${message.type.name}" id="common-message-header"></div>
        <div class="float-left msg-txt-area">
            <div class="msg-area">
                <div id="display-msg-div" class="msg-con ${message.type.name}">
                    ${message.message} ${param.message}
                </div>
            </div>
        </div>
    </div>
</div>
</c:if>
<c:if test="${not empty invalidEmailAddressMessage || not empty invalidEmaiAppBaseUrllAddressMessage.message}">
<div class="display-message">
	<div class="error-wrapper clearfix">
        <div class="float-left ${invalidEmailAddressMessage.type.name}" id="common-ieam-message-header"></div>
        <div class="float-left msg-txt-area">
            <div class="msg-area">
                <div id="display-invalid-email-addr-msg-div" class="msg-con ${invalidEmailAddressMessage.type.name}">
                    ${invalidEmailAddressMessage.message}
                </div>
            </div>
        </div>
    </div>
</div>
</c:if>
<c:if test="${not empty alreadyExistEmailAddress || not empty alreadyExistEmailAddress.message}">
<div class="display-message">
	<div class="error-wrapper clearfix">
        <div class="float-left ${alreadyExistEmailAddress.type.name}" id="common-aeem-message-header"></div>
        <div class="float-left msg-txt-area">
            <div class="msg-area">
                <div id="display-already-exist-email-addr-msg-div" class="msg-con ${alreadyExistEmailAddress.type.name}">
                    ${alreadyExistEmailAddress.message}
                </div>
            </div>
        </div>
    </div>
</div>
</c:if>
<c:if test="${not empty invalidEmailAddress}">
	<div id="invalid-display-msg-div" class="hide">${invalidEmailAddress}</div>
</c:if>

<c:if test="${not empty forNewDashboard}">
	<input id="new-dash-flag" class="hidden" value="${forNewDashboard}">
</c:if>

<c:if test="${not empty forQuickEdits}">
	<input id="quick-edits-img-flag" class="hidden" value="${forQuickEdits}">
</c:if>

<c:if test="${not empty profileImageUrlThumbnailForQuickEdits}">
	<input id="quick-edits-profile-img-url" class="hidden" value="${profileImageUrlThumbnailForQuickEdits}">
</c:if>

<c:if test="${not empty logoUrlForQuickEdits}">
	<input id="quick-edits-logo-url" class="hidden" value="${logoUrlForQuickEdits}">
</c:if>