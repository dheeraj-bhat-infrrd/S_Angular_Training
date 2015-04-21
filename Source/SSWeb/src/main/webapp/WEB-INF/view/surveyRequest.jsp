<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user"
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title><spring:message code="label.survey.title.key" /></title>
<link rel="shortcut icon" href="${pageContext.request.contextPath}/favicon.ico" sizes="16x16">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/rangeslider.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-common-1.1.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style-resp-1.1.css">

</head>
<body>
	<div id="req-prof-container" data-agentid="${agentId}"
		data-agentName="${agentName}"
		class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
		<div class="container">
			<div class="sq-ques-wrapper">
				<div id="req-agnt-img" class="sq-top-img"></div>
				<div class="sq-quest-item">
					<div class="sq-ques">
						<i><span class="sq-ques-txt">Please fill in details of
								the customer</span></i>
					</div>
					<div class="sq-bord-bot-sm"></div>
					<div class="sq-rat-wrapper">
						<div
							class="sq-star-wrapper sq-i-container clearfix ques-wrapper-adj">
							<div class="clearfix sq-info-wrapper">
								<div class="sq-i-lbl float-left">First Name</div>
								<div class="sq-i-txt float-left">
									<div class="hide sq-img-adj icn-fname"></div>
									<input id="reqFirstName" class="sq-i-txt-fld">
								</div>
							</div>
							<div class="clearfix sq-info-wrapper">
								<div class="sq-i-lbl float-left">Last Name</div>
								<div class="sq-i-txt float-left">
									<div class="hide sq-img-adj icn-lname"></div>
									<input id="reqLastName" class="sq-i-txt-fld">
								</div>
							</div>
							<div class="clearfix sq-info-wrapper">
								<div class="sq-i-lbl float-left">Email</div>
								<div class="sq-i-txt float-left">
									<div class="hide sq-img-adj icn-email"></div>
									<input id="reqEmail" class="sq-i-txt-fld">
								</div>
							</div>
							<div class="clearfix sq-info-wrapper">
								<div class="sq-i-lbl float-left">Customer</div>
								<div class="sq-i-txt float-left">
									<select id="req-cust-agnt-rel" class="sq-i-txt-fld"></select>
								</div>
							</div>

							<div class="sq-btn-wrapper">
								<div id="req-start-btn" class="sq-btn-continue">Send
									Request</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- temp caching of images -->
		<div class="hide">
			<div class="float-left sq-mcq-chk hide st-mcq-chk-on hide"></div>
			<div class="float-left sq-mcq-chk hide st-mcq-chk-off"></div>
			<div class="sq-star sq-full-star hide"></div>
		</div>
	</div>
	<div style="display: none">
		<script
			src="https://www.google.com/recaptcha/api/challenge?k=6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-"></script>
	</div>
	<script
		src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script
		src="https://www.google.com/jsapi?autoload={'modules':[{'name':'visualization','version':'1','packages':['corechart']}]}"></script>
	<script src="${pageContext.request.contextPath}/resources/js/common.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/usermanagement.js"></script>
	<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/rangeslider.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/proList.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/rangeslider.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/surveyQuestion.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/progressbar.min.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/editprofile.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/dashboard.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/editprofile.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/hierarchy-management.js"></script>
	<script
		src="${pageContext.request.contextPath}/resources/js/googletracking.js"></script>
	<script>
		$(document).ready(function() {
			$("#req-cust-agnt-rel").html(paintListOptions());
debugger;
			$('#req-start-btn').click(function() {
				var success = false;
				var fname = $('#reqFirstName').val().trim();
				var lname = $('#reqLastName').val().trim();
				var customerEmail = $('#reqEmail').val().trim();
				var relationWithAgent = $('#req-cust-agnt-rel').val().trim();
				var payload = {
					"firstName" : fname,
					"lastName" : lname,
					"email" : customerEmail,
					"relation" : relationWithAgent
				};
				
				$.ajax({
					url : "./sendsurveyinvite.do",
					type : "GET",
					data : payload,
					dataType : "text",
					async : true,
					success : function(data){
						if (data.errCode == undefined)
							success = true;
					},
					complete: function(){
						if(success==true)
							window.open('./landing.do', '_self');
					},
					error : function(e) {
						redirectErrorpage();
					}
				});
			});

			adjustMinHeight();
			$(window).resize(adjustMinHeight);
			function adjustMinHeight() {
				var winH = $(window).height();
				if ($(window).width() < 768) {
					var minH = winH - 50 - 50 - 5 - 1;
				} else {
					var minH = winH - 80 - 78 - 78 - 1;
				}
				$('.min-height-container').css('min-height', minH + 'px');
			}

			function resizeFunc() {
				var winW = $(window).width();
				if (winW < 768) {
					var offset = winW - 114 - 20;
					$('.reg-cap-txt').css('max-width', offset + 'px');
				}
			}
			
			function paintListOptions() {
				var divToPopulate = "<option value='select'>--Select an Option--"
						+ "<option value='transacted'>Transacted with me"
						+ "<option value='enquired'>Enquired with me";
				return divToPopulate;
			}
		});
	</script>
	<jsp:include page="footer.jsp" />
</body>
</html>