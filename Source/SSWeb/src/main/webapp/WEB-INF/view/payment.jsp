<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:choose>
<c:when test="${ paymentChange == 1 }">
	<div class="margin-top-25 margin-bottom-25 padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-md-12 col-xs-12">
    <div class="container">
</c:when>
<c:otherwise>
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
</c:otherwise>
</c:choose>
    <div class="payment-details-wrapper">
    	<div id="acc-type-payment" class="acc-type-payment">
    	<c:choose>
    	<c:when test="${ paymentChange == 1 }">
    		  <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.paymentupgrade.key"/></div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt"><spring:message code="label.cardnumber.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-type-val">'${cardNumber}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt"><spring:message code="label.cardholder.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-type-val">'${cardHolderName}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt"><spring:message code="label.cardtype.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-type-val">'${cardType}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt"><spring:message code="label.issuingbank.key"/></div>
	              <div class="pu-acc-type-val float-right" id="pu-acc-type-val">'${issuingBank}'</div>
	          </div>
    	</c:when>
    	<c:otherwise>
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
	    </c:otherwise>
	    </c:choose>
	          <div id="payment-details-form" class="payment-details-form">
	          		<c:choose>
	          		<c:when test="${ paymentChange == 1 }"><form id="checkout" method="POST" action="./paymentupgrade.do"></c:when>
	          		<c:otherwise><form id="checkout" method="POST" action="./subscribe.do"></c:otherwise>
	          		</c:choose>
			            <div id="dropin" class="payment-dropin"></div>
				            <div class="clearfix">
					            <input type="submit" class="btn-payment float-left" value='<spring:message code="label.makepayment.key" />'>
					            <input type="button" id="cancel-payment" class="btn-payment float-right" value='<spring:message code="label.cancel.key" />'>
				            </div>
				        <c:choose>
				        <c:when test="${ paymentChange == 1 }"></c:when>
				        <c:otherwise>
			            <input type="hidden" value="${accounttype}" name="accounttype"></c:otherwise>
			            </c:choose>
			        </form>
	      	</div>
        </div>
    </div>  
    <c:if test="${ paymentChange == 1 }">
    </div>
    </div>
    </c:if>
    <script type="text/javascript">
   $(document).ready(function() {
	   console.log("Loading braintree");
		braintree.setup('${clienttoken}', 'dropin', {
			container : 'dropin'
		});
   });
   
   $("#cancel-payment").click(function() {
	   hidePayment();
   })
	
	</script>
   
   <c:choose>
   <c:when test="${ paymentChange == 1 }"></c:when>
   <c:otherwise>
	    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
   </c:otherwise>
   </c:choose>
</body>
</html>