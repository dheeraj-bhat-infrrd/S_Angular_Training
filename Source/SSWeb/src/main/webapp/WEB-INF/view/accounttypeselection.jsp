<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
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
			<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
			<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
			<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
			<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">
		</head>
		<body>
			<div class="overlay-payment overlay-main hide">
				<div id="payment-section" class="payment-section">
					<!-- Payment page comes here through ajax  -->
				</div>
			</div>
			<div class="hdr-wrapper">
				<div class="container hdr-container clearfix">
					<div class="float-left hdr-logo"></div>
					<div class="float-right clearfix hdr-btns-wrapper">
						<div class="float-left hdr-log-btn hdr-log-reg-btn"><spring:message code="label.signin.key" /></div>
						<div class="float-left hdr-reg-btn hdr-log-reg-btn"><spring:message code="label.joinus.key" /></div>
					</div>
				</div>
			</div>
	</c:otherwise>
</c:choose>

<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center">
				<c:choose>
					<c:when test="${upgrade == 1}"><spring:message code="label.upgradeplan.header.key" /></c:when>
					<c:otherwise><spring:message code="label.accounttypeselection.header.key" /></c:otherwise>	
				</c:choose>
			</div>
		</div>
	</div>
</div>

<div id="payment-form" class="acc-type-main-wrapper margin-top-25 margin-bottom-25">
	<div id="acc-type-sel-options" class="acc-type-container container">
	
		<form id="account-type-selection-form">
			<c:choose>
				<%-- Payment options for upgrade --%>
				<c:when test="${upgrade == 1}">
					<div class="acc-type-item text-center" data-status="disabled">
						<div class="act-header" id="account-type-1"><spring:message code="label.accounttype.individual.key"/></div>
						<div class="act-price">
							$35<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>1</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					
					<c:choose>
						<c:when test="${currentplan < 2}"><div class="acc-type-item text-center" onclick="confirmUpgradation(2)"></c:when>
						<c:otherwise><div class="acc-type-item text-center" data-status="disabled"></c:otherwise>
					</c:choose>
						<div class="act-header" id="account-type-2"><spring:message code="label.accounttype.team.key"/></div>
						<div class="act-price">
							$45<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>30</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					
					<c:choose>
						<c:when test="${currentplan < 3}"><div class="acc-type-item text-center" onclick="confirmUpgradation(3)"></c:when>
						<c:otherwise><div class="acc-type-item text-center" data-status="disabled"></c:otherwise>
					</c:choose>
						<div class="act-header" id="account-type-3"><spring:message code="label.accounttype.company.key"/></div>
						<div class="act-price">
							$65<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>60</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					
					<c:choose>
						<c:when test="${currentplan < 4}"><div class="acc-type-item text-center" onclick="confirmUpgradation(4)"></c:when>
						<c:otherwise><div class="acc-type-item text-center" data-status="disabled"></c:otherwise>
					</c:choose>
						<div class="act-header" id="account-type-4"><spring:message code="label.accounttype.enterprise.key"/></div>
						<div class="act-price">
							$99<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>100</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
				</c:when>
				
				<%-- Payment options for new user --%>
				<c:otherwise>
					<div class="acc-type-item text-center" onclick="selectAccountType(1, '$35')">
						<div class="act-header" id="account-type-1"><spring:message code="label.accounttype.individual.key"/></div>
						<div class="act-price">
							$35<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>1</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					<div class="acc-type-item text-center" onclick="selectAccountType(2, '$45')">
						<div class="act-header" id="account-type-2"><spring:message code="label.accounttype.team.key"/></div>
						<div class="act-price">
							$45<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>30</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					<div class="acc-type-item text-center" onclick="selectAccountType(3, '$65')">
						<div class="act-header" id="account-type-3"><spring:message code="label.accounttype.company.key"/></div>
						<div class="act-price">
							$65<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>60</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
					<div class="acc-type-item text-center" onclick="selectAccountType(4, '$99')">
						<div class="act-header" id="account-type-4"><spring:message code="label.accounttype.enterprise.key"/></div>
						<div class="act-price">
							$99<sup>99</sup><span><spring:message code="label.permonth.key"/></span>
						</div>
						<div class="act-txt-1"><strong>100</strong> <spring:message code="label.accounttype.useraccounts.key"/></div>
						<div class="act-txt-2">Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds Lor em ip sum do ie aje lanjds</div>
						<div class="act-txt-3"><spring:message code="label.accounttype.select.key"/></div>
					</div>
				</c:otherwise>	
			</c:choose>
			<input type="hidden" name="accounttype" id="account-type" />
		</form>
	
	</div>
</div>

<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
	<c:otherwise>
		<script	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
		<script	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
		<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
		<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
	</c:otherwise>
</c:choose>

<c:if test="${message != null}">
	<script>
		$(document).ready(function(){
			var message = '<c:out value="${message}"/>';
			console.log("Showing toast message : " + message);
			$('#overlay-toast').html(message);
			showToast(message);
		});
	</script>
</c:if>  

<script src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
function selectAccountType(accountType, paymentAmount) {
	if ($(this).attr('data-status') == 'disabled') {
		return;
	}
	
	console.log("selecting and saving account type");
	$('#account-type').val(accountType);

	// show the progress icon
	showOverlay();

	var url = "./addaccounttype.do";
	var $form = $("#account-type-selection-form");
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		type : "POST",
		data : payLoad,
		success : function(data) {
			selectAccountTypeCallBack(data, accountType, paymentAmount);
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

function selectAccountTypeCallBack(data,accountType,paymentAmount) {
	console.log("callback for selectAccountType called");
	hideOverlay();

	// Replace the contents of account selection with payment page with selected account type contents
	$("#payment-section").html(data);		
	showPayment();
	$('body').css('overflow', 'hidden');
	
	var selectedAccountType = $("#account-type-" + accountType).html();
	$("#pu-acc-type-val").html(selectedAccountType);
	$("#pu-acc-amount-val").html(paymentAmount);
	console.log("callback for selectAccountType finished");
}

function confirmUpgradation(accountType){
	if ($(this).attr('data-status') == 'disabled') {
		return;
	}

	console.log("Returning the upgrade confirmation page");
	data = "accounttype=" + String(accountType);
   	$('.overlay-payment').hide();
	$('.overlay-payment').html("");

	// show the progress icon
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

	// show the progress icon
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
		setTimeout(function (){location.href = "./landing.do";}, 4000);
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

$("#ol-btn-cancel").click(function() {
	$('.overlay-payment').hide();
});

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
	$('#overlay-main').hide();
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');

	$('body').css('overflow','auto');
}
</script>

<c:choose>
	<c:when test="${upgrade == 1}"></c:when>
	<c:otherwise>
		</body>
		</html>
	</c:otherwise>
</c:choose> 