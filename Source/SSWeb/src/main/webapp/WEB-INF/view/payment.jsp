<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title><spring:message code="label.makepayment.title.key" /></title>
</head>
<body>
	<div id="messageHeader"><jsp:include page="messageheader.jsp"></jsp:include></div>
	<form id="checkout" method="post" action="./dummypayment.do">
		<div id="dropin" style="width: 400px;"></div>
		<input type="submit"
			value='<spring:message code="label.makepayment.key" />!'>
	</form>
	<!-- <script src="https://js.braintreegateway.com/v2/braintree.js"></script> -->
	<!-- <script type="text/javascript">
		braintree.setup('${clienttoken}', 'dropin', {
			container : 'dropin'
		});
	</script> -->
</body>
</html>