<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<body>
	<div class="hm-header-main-wrapper">
		<div class="container">
			<div class="hm-header-row hm-header-row-main clearfix">
				<div class="float-left hm-header-row-left text-center">
					<spring:message code="label.reviewsmonitor.key" />
				</div>
			</div>
		</div>		
	</div>
	<div class="vendasta-container">
		<iframe id="vendasta-iframe" height="600px" width="102%" > 
		//onload="javascript:testVendastaIframe()" >
			Vendasta Integration
		</iframe>		
		<div id="retry-cont" class="container retry-container hide">
			<div id="retry" class="review-retry cursor-pointer rvw-rty-small">Unable to connect<br/>Retry</div>
		</div>
	</div>
<script>
$(document).ready( function() { 
	loadVendastaIframe();
	
	$(document).on('click', '#retry', function() {
		$("retry-cont").hide();
		$("#vendasta-iframe").show();
		loadVendastaIframe();
	});
});
</script>
	