<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="user" value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />
<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
	<c:when test="${paidUpgrade == 1}">
		<body>
			<div class="overlay-payment overlay-main hide">
				<div id="payment-section" class="payment-section">
					<!-- Payment page comes here through ajax  -->
				</div>
			</div>	
			<div class="overlay-loader hide"></div>
	</c:when>	
	<c:otherwise>
   		<!DOCTYPE">
		<html>
		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<meta name="viewport" content="width=device-width, initial-scale=1">
			<title><spring:message code="label.makepayment.title.key" /></title>
			<link rel="shortcut icon" href="/favicon.ico" sizes="16x16">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/bootstrap.min.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/rangeslider.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-common-1.1.css">
			<link rel="stylesheet" href="${initParam.resourcesPath}/resources/css/style-resp-1.1.css">
		</head>
		<body>
			<div class="overlay-payment overlay-main hide">
				<div id="payment-section" class="payment-section">
					<!-- Payment page comes here through ajax  -->
				</div>
			</div>
			<div class="overlay-loader hide"></div>
			<div class="hdr-wrapper">
				<div class="container hdr-container clearfix">
					<div class="float-left hdr-logo"></div>
					<div class="float-right clearfix hdr-btns-wrapper">
						<div id="header-user-info" class="header-user-info float-right clearfix">
					<div class="float-left user-info-initial">
						<span id="usr-initl">${fn:substring(user.firstName, 0, 1)}</span>
					</div>
	                <div class="float-left user-info-sing-out">
	                    <a class="" href="j_spring_security_logout"><spring:message code="label.logout.key" /></a>
	                </div>
					</div>
				</div>
			</div>
			</div>
	</c:otherwise>
</c:choose>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="hm-header-row-left text-center padding-10">
				<c:choose>
					<c:when test="${upgrade == 1}"><spring:message code="label.upgradeplan.header.key" /></c:when>
					<c:otherwise><spring:message code="label.accounttypeselection.header.key" /></c:otherwise>	
				</c:choose>
			</div>
		</div>
	</div>
</div>

<div id="payment-form" class="acc-type-main-wrapper margin-bottom-25 payment-container">
	<div class="container">
	<form id="account-type-selection-form">
		<div class="payment-hdr-block">
			<h2 class="payment-hdr-txt1">Plans built for individuals & teams.</h2>
			<p class="payment-hdr-txt2">Start 30 days free.</p>
		</div>
		<div class="payment-table">
			<table class="payment-pricing">
				<tbody>
					<tr>
						<th></th>
						<th><strong><span class="currency">$</span>29.95</strong> /
							Month<div class="payment-acc-type-txt">Individual</div></th>
						<th><strong><span class="currency">$</span>19.95</strong> /
							User / Month<div class="payment-acc-type-txt">Enterprise</div></th>
					</tr>
					<tr>
						<td>Special</td>
						<td>Free 30-Day Trial</td>
						<td>Free 30-Day Trial</td>
					</tr>
					<tr>
						<td>Purpose</td>
						<td>Single User</td>
						<td>Multi-User</td>
					</tr>
					<tr>
						<td>Users</td>
						<td>1</td>
						<td>Unlimited</td>
					</tr>
					<tr class="action">
						<td></td>
						<td>
							<c:choose>
							<c:when test="${ upgrade == 1 }">
								<span class="payment-button payment-button-disabled">Start 30-Day Trial</span>
							</c:when>
							<c:otherwise>
								<span class="payment-button" onclick="selectAccountType(1, '$29.95', ${skippayment});">Start 30-Day Trial</span>
							</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
							<c:when test="${ upgrade == 1 }">
								<span class="payment-button" onclick="makePaidUpgrade(4, '$19.95')">Upgrade</span>
							</c:when>
							<c:otherwise>
								<span class="payment-button" onclick="selectAccountType(4, '$19.95', ${skippayment});">Start 30-Day Trial</span>
							</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="payment-table">
			<table class="payment-pricing">
				<tbody>
					<tr>
						<th><strong>Basic Features</strong></th>
						<th><div class="payment-acc-type-txt">Individual</div></th>
						<th><div class="payment-acc-type-txt">Enterprise</div></th>
					</tr>
					<tr>
						<td>Free 30-Day Trial</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Users</td>
						<td>1</td>
						<td>Unlimited</td>
					</tr>
					<tr>
						<td>Find a Pro Public Search Page</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Individual Public Profile Page</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Company Public Profile Pages</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Mobile Ready Pages</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Automated SEO with Star Rating</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Feature Specific Reviews on Your Public Pages</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Pre-Written Surveys for Your Business Vertical</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Create your account by using your Linkedin ID</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Unlimited Surveys</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Unlimited Survey Questions</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>User Dashboard</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>User Scorecards</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Office, Region and Company Scorecards</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Pre-Written Surveys for Your Business Vertical</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Licensing and Compliance Integration</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>SSL Security</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="payment-table">
			<table class="payment-pricing">
				<tbody>
					<tr>
						<th><strong>Advanced Workflow</strong></th>
						<th><div class="payment-acc-type-txt">Individual</div></th>
						<th><div class="payment-acc-type-txt">Enterprise</div></th>
					</tr>
					<tr>
						<td>Create and Edit Survey Builder Tool</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Automatically Send Requests from Your Point of Sale System</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Syndicate and Share Happy Customer Comments</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Surveys are White Labeled</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Multiple Survey Question Types</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Collect Optional Customer Comments</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Edit Outbound Emails and Workflow</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="payment-table">
			<table class="payment-pricing">
				<tbody>
					<tr>
						<th><strong>Advanced Tools</strong></th>
						<th><div class="payment-acc-type-txt">Individual</div></th>
						<th><div class="payment-acc-type-txt">Enterprise</div></th>
					</tr>
					<tr>
						<td>Easily Import thousands of Users in Your Enterprise
							Hierarchy</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Import Leads</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Development API</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td>Minimum 250 Users / Coming in Late 2015</td>
					</tr>
					<tr>
						<td>Custom Development and Widgets</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td>Minimum 250 Users / Coming in Late 2015</td>
					</tr>
					<tr>
						<td>Manage User Permissions and Workflow Within Your Hierarchy</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Export Survey Results</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Support Ticket System</td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
					<tr>
						<td>Advanced Reporting and Company Admin Access</td>
						<td><span class="payment-icn payment-close-icn"></span></td>
						<td><span class="payment-icn payment-tick-icn"></span></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="payment-table">
			<table class="payment-pricing">
				<tbody>
					<tr>
						<th></th>
						<th><strong><span class="currency">$</span>29.95</strong> /
							Month<div class="payment-acc-type-txt">Individual</div></th>
						<th><strong><span class="currency">$</span>19.95</strong> /
							User / Month<div class="payment-acc-type-txt">Enterprise</div></th>
					</tr>
					<tr class="action">
						<td></td>
						<td>
							<c:choose>
							<c:when test="${ upgrade == 1 }">
								<span class="payment-button payment-button-disabled">Start 30-Day Trial</span>
							</c:when>
							<c:otherwise>
								<span class="payment-button" onclick="selectAccountType(1, '$29.95', ${skippayment})">Start 30-Day Trial</span>
							</c:otherwise>
							</c:choose>
						</td>
						<td>
							<c:choose>
							<c:when test="${ upgrade == 1 }">
								<span class="payment-button" onclick="makePaidUpgrade(4, '$19.95')">Upgrade</span>
							</c:when>
							<c:otherwise>
								<span class="payment-button" onclick="selectAccountType(4, '$19.95', ${skippayment})">Start 30-Day Trial</span>
							</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<input type="hidden" name="accounttype" id="account-type" />
		<input type="hidden" name="skipPayment" value="${skippayment}" />
	</form>
	</div>
</div>
<!-- Account type -->
<div class="hide">
	<div id="account-type-1"><spring:message code="label.accounttype.individual.key"/></div>
	<div id="account-type-4"><spring:message code="label.accounttype.enterprise.key"/></div>
</div>
<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
	<c:when test="${ paidUpgrade == 1 }"></c:when>
	<c:otherwise>
		<script	src="${initParam.resourcesPath}/resources/js/jquery-2.1.1.min.js"></script>
		<script	src="${initParam.resourcesPath}/resources/js/bootstrap.min.js"></script>
		<script src="${initParam.resourcesPath}/resources/js/common.js"></script>
		<script src="${initParam.resourcesPath}/resources/js/script.js"></script>
		<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
	</c:otherwise>
</c:choose>

<c:if test="${message != null}">
	<script>
		$(document).ready(function(){
			var message = '<c:out value="${message}"/>';
			showErrorMobileAndWeb(message);
		});
	</script>
</c:if>  

<script>
function selectAccountType(accountType, paymentAmount, skippayment) {
	// show the progress icon
	showOverlay();
	disableBodyScroll();
	$('body').addClass("disable-scroll");
	if ($(this).attr('data-status') == 'disabled') {
		return;
	}
	$('#account-type').val(accountType);

	var url = "./addaccounttype.do";
	var $form = $("#account-type-selection-form");
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		type : "POST",
		async : false,
		data : payLoad,
		success : function(data) {
			if(skippayment == undefined || Boolean(skippayment) != true){
				selectAccountTypeCallBack(data, accountType, paymentAmount);
			}else{
            	location.href="./landing.do";
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

function makePaidUpgrade(accountType,paymentAmount){
	/* show the progress icon */
    showOverlay();
    $('#account-type').val(accountType);
    
    var url = "./paymentpage.do";

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
        	if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
        }
    });
}

function selectAccountTypeCallBack(data,accountType,paymentAmount) {
	showOverlay();
	var paidUpgrade = '<c:out value="${upgrade}"/>';
    if(accountType == 5 && data == ""){
    	var url = "./subscribe.do";
        var $form = $("#account-type-selection-form");
        var payLoad = $form.serialize();
        $.ajax({
            url : url,
            type : "POST",
            data : payLoad,
            success : function(data) {
            	location.href="./landing.do";
            },
            error : function(e) {
            	if(e.status == 504) {
    				redirectToLoginPageOnSessionTimeOut(e.status);
    				return;
    			}
    			redirectErrorpage();
    		}
        });
    }
    else{
    	if(paidUpgrade == 1){
    		$("#payment-form").css("display","none");
    	}

        /* Replace the contents of account selection with payment page with selected account type contents*/
        $(".overlay-payment").html('<div class="payment-section">'+data+'</div>');		
        showPayment();
        hideOverlay();
        var selectedAccountType = $("#account-type-"+accountType).html();
        $("#pu-acc-type-val").html(selectedAccountType);
        $("#pu-acc-amount-val").html(paymentAmount);
    }
}

function confirmUpgradation(accountType){
	if ($(this).attr('data-status') == 'disabled') {
		return;
	}

	data = "accounttype=" + String(accountType);
   	$('.overlay-payment').hide();
	$('#payment-section').html("");

	// show the progress icon
	showOverlay();

	var url = "./upgradeconfirmation.do";
	$.ajax({
		url : url,
		type : "POST",
		data : data,
		success : function(data){
			$('.overlay-payment').html(data);		
			$('.overlay-payment').show();
			$('body').css('overflow', 'hidden');
		},
		complete: function(){
			hideOverlay();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

function upgradeAccountType(accountType) {
	data = "accounttype=" + String(accountType);
	var url = "./upgradeplan.do";

	// show the progress icon
	$('.overlay-payment').hide();
	showOverlay();
	$('body').css('overflow', 'auto');
	
	$.ajax({
		url : url,
		type : "POST",
		data : data,
		success : showMessage,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

function showMessage(data){
	var jsonData = JSON.parse(data);
	if(jsonData["success"] == 1){
		$('#overlay-toast').html(jsonData["message"]);
		showToast();
		setTimeout(function (){location.href = "./landing.do";}, 4000);
	}
	else{
		$('.overlay-payment').hide();
		hideOverlay();
		$('#overlay-toast').html(jsonData["message"]);
		showToast();
	}
}

$('.acc-type-item').hover(
	function(){
		if ($(this).attr('data-status') != 'disabled') {
			$(this).addClass('act-type-hover');
		}
	},
	function(){
		if ($(this).attr('data-status') != 'disabled') {
			$(this).removeClass('act-type-hover');
		}
	}
);

function overlayRevert() {
	$('.overlay-payment').hide();
	$('body').css('overflow', 'auto');
}
</script>

<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
	<c:when test="${ paidUpgrade == 1 }"></c:when>
	<c:otherwise>
		</body>
		</html>
	</c:otherwise>
</c:choose> 