<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty message.message || not empty param.message}">
<div class="${message.type.name}">
	<div class="error-wrapper clearfix">
        <div class="float-left msg-err-icn"></div>
        <div class="float-left msg-err-txt-area">
            <div class="err-msg-area">
                <div class="err-msg-con">
                    <p>${message.message} ${param.message}</p>
                </div>
            </div>
        </div>
    </div>
</div>
</c:if>