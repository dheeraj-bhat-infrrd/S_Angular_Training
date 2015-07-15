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
<c:if test="${not empty invalidEmailAddress}">
	<div id="invalid-display-msg-div" class="hide">${invalidEmailAddress}</div>
</c:if>
