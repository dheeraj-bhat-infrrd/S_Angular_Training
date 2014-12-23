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
 <div class="overlay-loader hide"></div>
 <div class="overlay-payment hide">
 	<div id="payment-section" class="payment-section">
		<!-- Payment page comes here through ajax  -->
        <div id="acc-type-payment" class="acc-type-payment">
            <div class="login-txt text-center font-24 margin-bot-20">Payment Information</div>
            <div class="clearfix pu-acc-type-sel">
                <div class="pu-acc-type-txt float-left" id="pu-acc-type-txt">Account Type</div>
                <div class="pu-acc-type-val float-right" id="pu-acc-type-val">Company</div>
            </div>
            <div class="clearfix pu-acc-type-sel margin-bottom-25">
                <div class="pu-acc-type-txt float-left" id="pu-acc-amount-txt">Total Amount</div>
                <div class="pu-acc-type-val float-right" id="pu-acc-amount-val">$35.99</div>
            </div>
            <div id="payment-details-form" class="payment-details-form">
                <form id="" method="" action="">
                    <div id="payment-dropin"></div>
                </form>
            </div>
        </div>
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
                                            <div class="payment-tab-header"><spring:message code="label.accounttype.individual.key"/></div>
                                            <div class="payment-tab-price"><span class="payment-txt-price">$35<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
                                            <div class="payment-tab-line1"><strong>1</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
                                        </div>
                                    </div>
                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(1)"><spring:message code="label.accounttype.select.key"/></div>
                                </div>
                                <div class="float-left payment-option-tab">
                                    <div class="payment-tab-main">
                                        <div class="payment-text-wrapper">
                                            <div class="payment-tab-header"><spring:message code="label.accounttype.team.key"/></div>
                                            <div class="payment-tab-price"><span class="payment-txt-price">$45<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
                                            <div class="payment-tab-line1"><strong>30</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
                                        </div>
                                    </div>
                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(2)"><spring:message code="label.accounttype.select.key"/></div>
                                </div>
                                <div class="float-left payment-option-tab">
                                    <div class="payment-tab-main">
                                        <div class="payment-text-wrapper">
                                            <div class="payment-tab-header"><spring:message code="label.accounttype.company.key"/></div>
                                            <div class="payment-tab-price"><span class="payment-txt-price">$65<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
                                            <div class="payment-tab-line1"><strong>60</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
                                        </div>
                                    </div>
                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(3)"><spring:message code="label.accounttype.select.key"/></div>
                                </div>
                                <div class="float-left payment-option-tab padding-right-25">
                                    <div class="payment-tab-main">
                                        <div class="payment-text-wrapper">
                                            <div class="payment-tab-header"><spring:message code="label.accounttype.enterprise.key"/></div>
                                            <div class="payment-tab-price"><span class="payment-txt-price">$99<sup>99</sup></span> <span class="txt-thin"><spring:message code="label.permonth.key"/></span></div>
                                            <div class="payment-tab-line1"><strong>100</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
                                            <div class="payment-tab-line2">Lorem ipsum dore it leer Lorem ipsu leer Lorem ipsum dore it </div>
                                        </div>
                                    </div>
                                    <div class="btn-payment-sel" onclick="javascript:selectAccountType(4)"><spring:message code="label.accounttype.select.key"/></div>
                                </div>
                            </div>
                            <input type="hidden" name="accounttype" id="account-type" />
                        </form>                                                             
                    </div>
                    
                </div>
                <div class="footer-copyright text-center"><spring:message code="label.copyright.key" /> &copy; 
                    <spring:message code="label.copyrightposttext.key" />
                </div>                
            </div>
        </div>
    </div>
    <script	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
    <script src="https://js.braintreegateway.com/v2/braintree.js"></script>
    <script>
	    function selectAccountType(accountType) {
			console.log("selecting and saving account type");
			$('#account-type').val(accountType);
			var url = "./addaccounttype.do";
			
			/* show the progress icon */
	    	showOverlay();
			callAjaxFormSubmit(url, selectAccountTypeCallBack,
					"account-type-selection-form");
		}
	
		function selectAccountTypeCallBack(data) {
			console.log("callback for selectAccountType called");
			
			/* hide the progress icon */
	    	hideOverlay();
			$("#payment-section").html(data);
			showPayment();
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
            
            $('#payment-logo').click(function(){
                $('.overlay-payment').show();
                braintree.setup("eyJ2ZXJzaW9uIjoxLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJmNGMxYzJhZTk4ODU0ZmJiNjdhMDdkZjBjZjJhN2YxMzY1YWI4ODMwZGJlYWZjMjI5NDYxMmJjYjAwMjEyNWI5fGNyZWF0ZWRfYXQ9MjAxNC0xMi0yM1QwNjoyNTozMi40MjY4MjI0MjQrMDAwMFx1MDAyNm1lcmNoYW50X2lkPWRjcHNweTJicndkanIzcW5cdTAwMjZwdWJsaWNfa2V5PTl3d3J6cWszdnIzdDRuYzgiLCJjb25maWdVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpL3YxL2NvbmZpZ3VyYXRpb24iLCJjaGFsbGVuZ2VzIjpbXSwicGF5bWVudEFwcHMiOltdLCJjbGllbnRBcGlVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvZGNwc3B5MmJyd2RqcjNxbi9jbGllbnRfYXBpIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhdXRoVXJsIjoiaHR0cHM6Ly9hdXRoLnZlbm1vLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhbmFseXRpY3MiOnsidXJsIjoiaHR0cHM6Ly9jbGllbnQtYW5hbHl0aWNzLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb20ifSwidGhyZWVEU2VjdXJlRW5hYmxlZCI6ZmFsc2UsInBheXBhbEVuYWJsZWQiOnRydWUsInBheXBhbCI6eyJkaXNwbGF5TmFtZSI6IkFjbWUgV2lkZ2V0cywgTHRkLiAoU2FuZGJveCkiLCJjbGllbnRJZCI6bnVsbCwicHJpdmFjeVVybCI6Imh0dHA6Ly9leGFtcGxlLmNvbS9wcCIsInVzZXJBZ3JlZW1lbnRVcmwiOiJodHRwOi8vZXhhbXBsZS5jb20vdG9zIiwiYmFzZVVybCI6Imh0dHBzOi8vYXNzZXRzLmJyYWludHJlZWdhdGV3YXkuY29tIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9jaGVja291dC5wYXlwYWwuY29tIiwiZGlyZWN0QmFzZVVybCI6bnVsbCwiYWxsb3dIdHRwIjp0cnVlLCJlbnZpcm9ubWVudE5vTmV0d29yayI6dHJ1ZSwiZW52aXJvbm1lbnQiOiJvZmZsaW5lIiwibWVyY2hhbnRBY2NvdW50SWQiOiJzdGNoMm5mZGZ3c3p5dHc1IiwiY3VycmVuY3lJc29Db2RlIjoiVVNEIn0sImNvaW5iYXNlRW5hYmxlZCI6ZmFsc2V9", 'dropin', {
  container: 'payment-dropin'
});
            });
            
        });
    </script>
</body>
</html>