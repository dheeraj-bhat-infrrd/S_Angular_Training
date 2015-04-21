<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:choose>
<c:when test="${ messageFlag == 1 }">
	<div id="display-msg-div" class="message">'${messageBody}'</div>
</c:when>
<c:otherwise>
<c:choose>
<c:when test="${ paymentChange == 1 }">
	<div class="overlay-loader hide"></div>
	<div class="ov-payment-container">
		<div class="clearfix container ov-payment-shadow margin-top-25 margin-bottom-25 padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-md-6 col-xs-9">
    		<div>
    			
    		<style>
    			.update-card-details-txt{
    				display: block;
					width: 100% !important;
					min-width: 100px !important;
					margin-bottom: 10px;
					height: 40px;
					padding: 0 10px;
    			}
    			.card-det-adj{
    				margin-bottom: 30px;
    			}
    			.card-err-txt{
    				line-height: 24px;
					padding-left: 10px;
					font-size: 12px;
					color: #DE3838;
					margin-bottom: 10px;
    			}
    		</style>
</c:when>
<c:when test="${ paidUpgrade == 1 }"></c:when>
<c:otherwise>
	<!DOCTYPE">
	<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	    <meta name="viewport" content="width=device-width, initial-scale=1">
	    <title><spring:message code="label.makepayment.title.key" /></title>
	    <link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
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
	          	  <div class="pu-acc-type-txt float-left"><spring:message code="label.cardnumber.key"/></div>
	              <div class="pu-acc-type-val float-right">'${cardNumber}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left"><spring:message code="label.cardholder.key"/></div>
	              <div class="pu-acc-type-val float-right">'${cardHolderName}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left"><spring:message code="label.cardtype.key"/></div>
	              <div class="pu-acc-type-val float-right">'${cardType}'</div>
	          </div>
	          <div class="clearfix pu-acc-type-sel">
	          	  <div class="pu-acc-type-txt float-left"><spring:message code="label.issuingbank.key"/></div>
	              <div class="pu-acc-type-val float-right">'${issuingBank}'</div>
	          </div>
	          <br>
	          <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.newpaymentdetails.key"/></div>
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
	          		<c:when test="${ paymentChange == 1 }">
	          			<form id="checkout" method="POST">
	          			<div id="dropin" class="payment-dropin"></div>	          			
	          		</c:when>
	          		<c:when test="${ paidUpgrade == 1 }">
	          			<form id="checkout" method="POST">
	          			<div id="dropin" class="payment-dropin"></div>	          			
	          		</c:when>
	          		<c:otherwise>
	          			<form id="checkout" method="POST" action="./subscribe.do">
	          			<div id="dropin" class="payment-dropin"></div>	          			
	          		</c:otherwise>
	          		</c:choose>
				            <div class="clearfix">
				            	<c:choose>
						        <c:when test="${ paymentChange == 1 }">
						        	<input type="submit" class="btn-payment float-left" value='<spring:message code="label.update.key"/>' />
						        </c:when>
						        <c:when test="${ paidUpgrade == 1 }">
						        	<input type="submit" class="btn-payment float-left" value='<spring:message code="label.makepayment.key"/>' />
						        </c:when>
						        <c:otherwise>
					            	<input type="submit" class="btn-payment float-left" value='<spring:message code="label.makepayment.key"/>' />
					            </c:otherwise>
					            </c:choose>
					            <input type="button" id="cancel-payment" class="btn-payment float-right" value='<spring:message code="label.cancel.key"/>'/>
				            </div>
				        <c:choose>
				        <c:when test="${ paidUpgrade == 1 }">
				        	<input type="hidden" value="${accounttype}" name="accounttype">
				        </c:when>
				        <c:when test="${ paymentChange == 1 }"></c:when>
				        <c:otherwise>
			            <input type="hidden" value="${accounttype}" name="accounttype"></c:otherwise>
			            </c:choose>
			        </form>
	      	</div>
        </div>
    </div>  
    <c:if test="${ paymentChange == 1 || paidUpgrade == 1 }">
    		</div>
    	</div>
    </div>
    </c:if>
    <script type="text/javascript">
           
	   $(document).ready(function() {
		   console.log("pageInitialized : " + pageInitialized);
		   if(pageInitialized){
			   console.log("Not executing ready function!");
			   return;
		   }
		   console.log("Loading braintree");
		   var paymentChangeStatus = '<c:out value="${paymentChange}"/>';
           var paidUpgrade = '<c:out value="${paidUpgrade}"/>';
		   if(paymentChangeStatus == 1 && paidUpgrade!=1 ){
			   console.log("Setting up the payment form for payment change");
				braintree.setup('${clienttoken}', 'dropin', {
					container : 'dropin',
					paymentMethodNonceReceived : makeAjaxCallToUpgrade
				});
			   console.log("Braintree loaded");
		   }else if(paymentChangeStatus != 1 && paidUpgrade==1 ){
			   console.log("Setting up the payment form upgrading to paid plan");
				braintree.setup('${clienttoken}', 'dropin', {
					container : 'dropin',
					paymentMethodNonceReceived : makeAjaxCallToPlanUpgrade
				});
			   console.log("Braintree loaded");
		   }
		   else if(paymentChangeStatus != 1 && paidUpgrade!=1 ){
			   console.log("Setting up the payment form");
				braintree.setup('${clienttoken}', 'dropin', {
					container : 'dropin'
				});
			   console.log("Braintree loaded");
		   }
		   else{
			   console.log("Payment status flags not properly set!");
		   }
		   
		   pageInitialized = true;
	   });
	   
	   $("#cancel-payment").click(function() {
	   		$('body').removeClass('body-no-scroll');
	   		$('#st-settings-payment-off').show();
	   		$('#st-settings-payment-on').hide();
		   	hidePayment();
		   	pageInitialized=false;
	   });
	   
	   $('input').keypress(function(e){
	       	// detect enter
	       	if (e.which == 13){
	       		e.preventDefault();
	       		updateCardDetails();
	       	}
		});
	   
	   $(document).keyup(function(e) {
	       	if (e.which == 27){
				$("#cancel-payment").trigger('click');
	       	}
		});
	   
	   function makeAjaxCallToPlanUpgrade(event,nonce){
		   hidePayment();
		   showOverlay();
		   console.log("making ajax call with nonce: " + nonce);
		   console.log(event);
		   var data = "payment_method_nonce=" + nonce+"&accounttype=" + $('#account-type').val();
		   console.log(data);

		   var url = "./upgradeplan.do";
		   $.ajax({
			  url : url,
			  type : "POST",
			  data : data,
			  success : showMessage,
			  error : function(e) {
					redirectErrorpage();
				}
		   });
	   }
	   
	   function showMessage(data){
       	var jsonData = JSON.parse(data);
       	console.log("Data recieved : " + jsonData);
       	if(jsonData["success"] == 1){
       		console.log("Account upgrade successful. Redirecting to dashboard");
           	$('#overlay-toast').html(jsonData["message"]);
	    		console.log("Added toast message. Showing it now");
	    		showToast();
	    		console.log("Finished showing the toast");
           	setTimeout(function (){location.href = "./landing.do";},4000);
       	}
       	else{
       		console.error("Error occured while upgrading. ");
	        	$('.overlay-payment').hide();
	        	hideOverlay();
               $('#overlay-toast').html(jsonData["message"]);
	    		console.log("Added toast message. Showing it now");
	    		showToast();
	    		console.log("Finished showing the toast");
       	}
       }
	   
	   function makeAjaxCallToUpgrade(event,nonce){
		   console.log("making ajax call with nonce: " + nonce);
		   console.log(event);
		   showOverlay();
		   var data = "payment_method_nonce=" + nonce;
		   var url = "./paymentupgrade.do";
		   $.ajax({
			  url : url,
			  type : "POST",
			  data : data,
			  success : displayToast,
			  error : function(e) {
					redirectErrorpage();
				}
		   });
	   }
	   
	   function displayToast(data){
		    hidePayment();
			hideOverlay();
			pageInitialized=false;
		   	console.log("Message recieved. Hiding Payment popup");
			console.log("Removing no-scroll class from body");
	   		$('body').removeClass('body-no-scroll');
	   		$('#st-settings-payment-off').show();
		   	$('#st-settings-payment-on').hide();
	   		$('#overlay-toast').html(data);
	   		console.log("Added toast message. Showing it now");
	   		showToast();
	   		console.log("Finished showing the toast");
	   }	
	</script>
   
   <c:choose>
   <c:when test="${ paymentChange == 1 }"></c:when>
   <c:when test="${ paidUpgrade == 1 }"></c:when>
   <c:otherwise>
	    <script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	    <script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
   </c:otherwise>
   </c:choose>

</c:otherwise>
</c:choose>