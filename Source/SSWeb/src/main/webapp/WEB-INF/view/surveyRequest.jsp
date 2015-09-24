<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="user"
	value="${sessionScope.SPRING_SECURITY_CONTEXT.authentication.principal}" />

	<div id="req-prof-container" data-agentid="${agentId}"
		data-agentName="${agentName}" data-agent-email="${agentEmail}"
		class="prof-main-content-wrapper margin-top-25 margin-bottom-25 min-height-container">
		<div class="">
			<div class="sq-ques-wrapper">
				<!-- <div id="req-agnt-img" class="sq-top-img"></div> -->
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
	<script>
		$(document).ready(function() {
			$("#req-cust-agnt-rel").html(paintListOptions());
			$('#req-start-btn').click(function() {
				var success = false;
				var fname = $('#reqFirstName').val().trim();
				var lname = $('#reqLastName').val().trim();
				var customerEmail = $('#reqEmail').val().trim();
				var relationWithAgent = $('#req-cust-agnt-rel').val().trim();
				
				var agentEmail = $('#req-prof-container').attr("data-agent-email");
				if(agentEmail.toUpperCase() == customerEmail.toUpperCase()){
					$('#overlay-toast').html('Users can not take survey for themselves!');
					showToast();
					return;
				}
				
				var payload = {
					"firstName" : fname,
					"lastName" : lname,
					"email" : customerEmail,
					"relation" : relationWithAgent
				};
				
				$.ajax({
					url : "./sendsurveyinvite.do",
					type : "GET",
					cache : false,
					data : payload,
					dataType : "text",
					async : true,
					success : function(data){
						if (data.errCode == undefined)
							success = true;
					},
					complete: function(){
						if(success==true){
							
							$('#srv-req-pop').removeClass('survey-request-popup-container');
							$('#srv-req-pop').addClass('hide');					
						}
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