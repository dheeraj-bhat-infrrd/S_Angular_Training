<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
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
<body>
 <div class="overlay-loader overlay hide"></div>
 <div class="overlay-payment overlay-main hide">
 	<div id="payment-section" class="payment-section">
		<!-- Payment page comes here through ajax  -->
	</div>
 </div>
	<div class="login-main-wrapper padding-001 login-wrapper-min-height account-type-height">
        <div class="container login-container">
            <div class="row login-row">
                <div id="payment-form" class="payment-form login-wrapper-resp padding-001 margin-top-25 margin-bottom-25 login-wrapper bg-fff margin-0-auto col-md-12 col-xs-12">
                    <div id="payment-logo" class="logo login-logo margin-bottom-25 margin-top-25"></div>
                    <div id="acc-type-sel-options" class="acc-type-sel-options">
                        <div class="login-txt text-center font-24 margin-bot-20"><spring:message code="label.accounttypeselection.header.key"/></div>
                        <form id="account-type-selection-form">
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
                            <input type="hidden" name="accounttype" id="account-type" />
                        </form>                                                             
                    </div>
                    
                </div>
                <div class="footer-copyright text-center">
                    <spring:message code="label.copyright.key"/> 
					&copy; 
					<spring:message code="label.footer.socialsurvey.key"/> 
					<span class="center-dot">.</span> 
					<spring:message code="label.allrightscopyright.key"/>
                </div>                
            </div>
        </div>
    </div>
    <script	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
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
    </script>
    <script src="https://js.braintreegateway.com/v2/braintree.js"></script>
</body>
</html>