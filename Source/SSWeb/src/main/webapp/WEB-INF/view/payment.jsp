<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title><spring:message code="label.makepayment.title.key" /></title>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
</head>
    
<body>
    <div class="payment-details-wrapper">
        <form id="checkout" method="POST" action="./subscribe.do">
            <div id="dropin" class="payment-dropin"></div>
            <input type="hidden" value="${accounttype}" name="accounttype">
            <input type="submit" value='<spring:message code="label.makepayment.key" />'>
        </form>
    </div>  
   
    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
   
   <script type="text/javascript">
   $(document).ready(function() {
		braintree.setup('${clienttoken}', 'dropin', {
			container : 'dropin'
		});
   });
	
	</script>
</body>
</html>