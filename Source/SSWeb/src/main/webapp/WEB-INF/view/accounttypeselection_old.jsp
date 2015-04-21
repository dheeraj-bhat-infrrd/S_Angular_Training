<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:choose>
   	<c:when test="${ upgrade == 1}"></c:when>
   	<c:otherwise>
   		<!DOCTYPE">
		<html>
		<head>
		    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		    <meta name="viewport" content="width=device-width, initial-scale=1">
   	    	<title><spring:message code="label.makepayment.title.key" /></title>
   	    	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
		    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
		    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common.css">
		    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp.css">
		</head>  
		<div id="toast-container" class="toast-container">
		   <span id="overlay-toast" class="overlay-toast"></span>
	    </div>
		
		<body>
   	</c:otherwise>
</c:choose>
<c:choose>
<c:when test="${ upgrade ==1 }"></c:when>
<c:otherwise>
	<div class="overlay-payment overlay-main hide">
	 	<div id="payment-section" class="payment-section">
			<!-- Payment page comes here through ajax  -->
		</div>
	 </div>
</c:otherwise>
</c:choose>

 	<c:choose>
 	<c:when test="${ upgrade ==1 }">
 		<div class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
        <div class="container">
 	</c:when>
 	<c:otherwise>
 		<div class="login-main-wrapper padding-001 login-wrapper-min-height account-type-height">
        <div class="container login-container">
        <div class="row login-row">
 	</c:otherwise>	
    </c:choose>
        
                <div id="payment-form" class="payment-form login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-md-12 col-xs-12">
                    <c:choose>
                    <c:when test="${ upgrade == 1 }"></c:when>
                    <c:otherwise><div id="payment-logo" class="logo login-logo margin-bottom-25 margin-top-25"></div></c:otherwise>
                    </c:choose>
                    <div id="acc-type-sel-options" class="acc-type-sel-options">
                        <c:choose>
                        	<c:when test="${ upgrade == 1}">
                        	<div class="login-txt text-center font-24 margin-bot-20 margin-top-25">	
                        		<spring:message code="label.upgradeplan.header.key"/>
                        	</div>
                        	</c:when>
                        	<c:otherwise>
                        	<div class="login-txt text-center font-24 margin-bot-20">	
                        		<spring:message code="label.accounttypeselection.header.key"/>
                        	</div>
                        	</c:otherwise>    
                        </c:choose>
                        <form id="account-type-selection-form">
                            <c:choose>
                            <c:when test="${ upgrade ==1 }">
                            	<div class="clearfix payment-option-wrapper">
                            		<div class="float-left payment-option-tab padding-left-25">
	                                    <div class="payment-tab-main">
	                                        <div class="payment-text-wrapper">
	                                            <div class="payment-tab-header" id="account-type-1"><spring:message code="label.accounttype.individual.key"/></div>
	                                            <div class="payment-tab-price"><span class="payment-txt-price">$35<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
	                                            <div class="payment-tab-line1"><strong>1</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
	                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
	                                        </div>
	                                    </div>
	                                    <div class="btn-payment-sel btn-disabled"><spring:message code="label.accounttype.upgrade.button.key"/></div>
	                                </div>
		                            <div class="float-left payment-option-tab">
		                                    <div class="payment-tab-main">
		                                        <div class="payment-text-wrapper">
		                                            <div class="payment-tab-header" id="account-type-2"><spring:message code="label.accounttype.team.key"/></div>
		                                            <div class="payment-tab-price"><span class="payment-txt-price">$45<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
		                                            <div class="payment-tab-line1"><strong>30</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
		                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
		                                        </div>
		                                    </div>
		                                    <c:choose>
		                                    <c:when test="${ currentplan < 2}">
		                                       	<div class="btn-payment-sel" onclick="javascript:confirmUpgradation(2)"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:when>
		                                    <c:otherwise>
		                                    	<div class="btn-payment-sel btn-disabled"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:otherwise>
		                                    </c:choose>
		                            </div>		                            
		                            <div class="float-left payment-option-tab">
		                                    <div class="payment-tab-main">
		                                        <div class="payment-text-wrapper">
		                                            <div class="payment-tab-header" id="account-type-3"><spring:message code="label.accounttype.company.key"/></div>
		                                            <div class="payment-tab-price"><span class="payment-txt-price">$65<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
		                                            <div class="payment-tab-line1"><strong>60</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
		                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
		                                        </div>
		                                    </div>
											<c:choose>
		                                    <c:when test="${ currentplan < 3}">
		                                       	<div class="btn-payment-sel" onclick="javascript:confirmUpgradation(3)"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:when>
		                                    <c:otherwise>
		                                    	<div class="btn-payment-sel btn-disabled"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:otherwise>
		                                    </c:choose>	
		                            </div>
		                            	<div class="float-left payment-option-tab padding-right-25">
		                                    <div class="payment-tab-main">
		                                        <div class="payment-text-wrapper">
		                                            <div class="payment-tab-header" id="account-type-4"><spring:message code="label.accounttype.enterprise.key"/></div>
		                                            <div class="payment-tab-price"><span class="payment-txt-price">$99<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
		                                            <div class="payment-tab-line1"><strong>100</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
		                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
		                                        </div>
		                                    </div>
		                                    <c:choose>
		                                    <c:when test="${ currentplan < 4}">
		                                       	<div class="btn-payment-sel" onclick="javascript:confirmUpgradation(4)"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:when>
		                                    <c:otherwise>
		                                    	<div class="btn-payment-sel btn-disabled"><spring:message code="label.accounttype.upgrade.button.key"/></div>
		                                    </c:otherwise>
		                                    </c:choose>
		                                </div>
		                                <div class="float-left ol-btn-wrapper">
						                    <div id="ol-btn-cancel" class="ol-btn">Cancel</div>
						                </div>
		                            </div>
                            </c:when>
                            <c:otherwise>
                            	<div class="clearfix payment-option-wrapper">
	                                <div class="float-left payment-option-tab padding-left-25">
	                                    <div class="payment-tab-main">
	                                        <div class="payment-text-wrapper">
	                                            <div class="payment-tab-header" id="account-type-1"><spring:message code="label.accounttype.individual.key"/></div>
	                                            <div class="payment-tab-price"><span class="payment-txt-price">$35<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
	                                            <div class="payment-tab-line1"><strong>1</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
	                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
	                                        </div>
	                                    </div>
	                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(1,'$35')"><spring:message code="label.accounttype.select.key"/></div>
	                                </div>
	                                <div class="float-left payment-option-tab">
	                                    <div class="payment-tab-main">
	                                        <div class="payment-text-wrapper">
	                                            <div class="payment-tab-header" id="account-type-2"><spring:message code="label.accounttype.team.key"/></div>
	                                            <div class="payment-tab-price"><span class="payment-txt-price">$45<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
	                                            <div class="payment-tab-line1"><strong>30</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
	                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
	                                        </div>
	                                    </div>
	                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(2,'$45')"><spring:message code="label.accounttype.select.key"/></div>
	                                </div>
	                                <div class="float-left payment-option-tab">
	                                    <div class="payment-tab-main">
	                                        <div class="payment-text-wrapper">
	                                            <div class="payment-tab-header" id="account-type-3"><spring:message code="label.accounttype.company.key"/></div>
	                                            <div class="payment-tab-price"><span class="payment-txt-price">$65<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
	                                            <div class="payment-tab-line1"><strong>60</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
	                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
	                                        </div>
	                                    </div>
	                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(3,'$65')"><spring:message code="label.accounttype.select.key"/></div>
	                                </div>
	                                <div class="float-left payment-option-tab padding-right-25">
	                                    <div class="payment-tab-main">
	                                        <div class="payment-text-wrapper">
	                                            <div class="payment-tab-header" id="account-type-4"><spring:message code="label.accounttype.enterprise.key"/></div>
	                                            <div class="payment-tab-price"><span class="payment-txt-price">$99<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
	                                            <div class="payment-tab-line1"><strong>100</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
	                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
	                                        </div>
	                                    </div>
	                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(4,'$99')"><spring:message code="label.accounttype.select.key"/></div>
	                                </div>
                            </div>
                            </c:otherwise>
                            </c:choose>
                            <input type="hidden" name="accounttype" id="account-type" />
                        </form>                                                             
                    </div>
                    
                </div>
                <c:choose>
                <c:when test="${ upgrade == 1 }"></c:when>
                <c:otherwise>
                <div class="footer-copyright text-center">
                    <spring:message code="label.copyright.key"/> 
					&copy; 
					<spring:message code="label.footer.socialsurvey.key"/> 
					<span class="center-dot">.</span> 
					<spring:message code="label.allrightscopyright.key"/>
                </div>
                </c:otherwise>
                </c:choose>                
            </div>
        </div>
    </div>
    <c:choose>
    <c:when test="${ upgrade ==1  }"></c:when>
    <c:otherwise>
	    <script	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
		<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
		<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
	</c:otherwise>
	</c:choose>
	<c:if test="${ message != null}">
		<script>
				$(document).ready(function(){
					var message = '<c:out value="${message}"/>';
					console.log("Showing toast message : " + message);
					$('#overlay-toast').html(message);
					showToast(message);
					
				});

		</script>
	</c:if>  
    <script>
            	
        function selectAccountType(accountType,paymentAmount) {
            console.log("selecting and saving account type");
            $('#account-type').val(accountType);
            var url = "./addaccounttype.do";

            /* show the progress icon */
            showOverlay();

            var $form = $("#account-type-selection-form");
            var payLoad = $form.serialize();
            $.ajax({
                url : url,
                type : "POST",
                data : payLoad,
                success : function(data) {
                    selectAccountTypeCallBack(data,accountType,paymentAmount);
                },
                error : function(e) {
                    console.error("error : " + e);
                }
            });
        }
        
        
        function confirmUpgradation(accountType){
        	console.log("Returning the upgrade confirmation page")
        	data = "accounttype=" + String(accountType);
    	   	$('.overlay-payment').hide();
			$('.overlay-payment').html("");
			showOverlay();
        	var url = "./upgradeconfirmation.do";
        	$.ajax({
        		url : url,
        		type : "POST",
        		data : data,
        		success : function(data){
                    $('.overlay-payment').html(data);		
        			hideOverlay();
        			$('.overlay-payment').show();
        		},
        		error : redirectErrorpage
        	});        	
        }
        
        function upgradeAccountType(accountType) {
            console.log("selecting and upgrading account type");
        	data = "accounttype=" + String(accountType);
            var url = "./upgradeplan.do";
            /* show the progress icon */
            $('.overlay-payment').hide();
            showOverlay();
            $.ajax({
        		url : url,
        		type : "POST",
        		data : data,
        		success : showMessage,
        		error : redirectErrorpage
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

        function selectAccountTypeCallBack(data,accountType,paymentAmount) {
            console.log("callback for selectAccountType called");

            /* hide the progress icon */
            hideOverlay();

            /* Replace the contents of account selection with payment page with selected account type contents*/
            $("#payment-section").html(data);		
            showPayment();
            var selectedAccountType = $("#account-type-"+accountType).html();
            $("#pu-acc-type-val").html(selectedAccountType);
            $("#pu-acc-amount-val").html(paymentAmount);

            console.log("callback for selectAccountType finished");
        }
        $(document).ready(function(){
            adjustOnResize();
            $(window).resize(adjustOnResize);
            $('.login-row').resize(adjustOnResize);
            
            function adjustOnResize(){
                var winH = $(window).height();
                var winH2 = winH/2;
                var conH2 = $('.login-row').height()/2;
                var offset = winH2 - conH2;
                if(offset > 25){
                    $('.login-row').css('margin-top',offset+'px');
                }
            }            
        });
        
        $("#ol-btn-cancel").click(function() {
        	$('.overlay-payment').hide();
         })
    </script>
<c:choose>
<c:when test="${ upgrade == 1 }">
</c:when>
<c:otherwise>
	</body>
	</html>
</c:otherwise>
</c:choose> 