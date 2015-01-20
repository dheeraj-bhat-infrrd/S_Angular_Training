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
    	<div id="acc-type-payment" class="acc-type-payment">
	          <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.paymentinformation.key"/></div>
	          <div class="clearfix pu-acc-type-sel">
	              <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt"><spring:message code="label.accounttype.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-type-val">
	              		<!-- Value is populated dynamically based on selected account type -->
	              </div>
	          </div>
	          <div class="clearfix pu-acc-type-sel margin-bottom-25">
	              <div class="pu-acc-type-txt float-left" id="pu-acc-amount-txt"><spring:message code="label.totalamount.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-amount-val">
	              		<!-- Value is populated dynamically based on selected account type -->
	              </div>
	          </div>
	          <div id="payment-details-form" class="payment-details-form">
			        <form id="checkout" method="POST" action="./subscribe.do">
			            <div id="dropin" class="payment-dropin"></div>
				            <div class="clearfix">
					            <input type="submit" class="btn-payment float-left" value='<spring:message code="label.makepayment.key" />'>
					            <input type="button" id="cancel-payment" class="btn-payment float-right" value='<spring:message code="label.cancel.key" />'>
				            </div>
			            <input type="hidden" value="${accounttype}" name="accounttype">
			        </form>
	      	</div>
        </div>
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
   
   $("#cancel-payment").click(function() {
	   hidePayment();
   })
	
	</script>
</body>
</html>