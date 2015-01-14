<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.changepassword.key" />
			</div>
		</div>
	</div>
</div>

<div id="hm-main-content-wrapper"
	class="hm-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="hm-content clearfix padding-001">
			<div class="hm-top-panel padding-001" id="company-branch">
				<div class="hm-top-panel-header">
					<!--Use this container to input all the messages from server-->
					<jsp:include page="messageheader.jsp" />
				</div>
				<div class="create-branch-dd cp-container">
					<form id="change-password-form" method="POST"
						action="./changepassword.do">
						<div class="hm-top-panel-content border-bottom-0 clearfix">
							<div class="clearfix">
								<div
									class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
									<div class="hm-item-row cp-item-row clearfix">
										<div class="hm-item-row-left cp-item-row-left text-right">
											<spring:message code="label.currentpassword.key" />
										</div>
										<div class="hm-item-row-right">
											<input type="password" name="oldpassword" id="current-pwd"
												data-non-empty="true"
												class="hm-item-row-txt cp-item-row-txt"
												placeholder='Current Password'>
											<div id="" class="input-error-2 error-msg"></div>
										</div>
									</div>
								</div>
								<div class="clearfix">
									<div
										class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
										<div class="hm-item-row cp-item-row clearfix">
											<div class="hm-item-row-left cp-item-row-left text-right">
												<spring:message code="label.newpassword.key" />
											</div>
											<div class="hm-item-row-right">
												<input type="password" name="newpassword" id="new-pwd"
													data-non-empty="true"
													class="hm-item-row-txt cp-item-row-txt"
													placeholder='New Password'>
												<div id="" class="input-error-2 error-msg"></div>
											</div>
										</div>
									</div>
									<div
										class="col-lg-6 col-md-6 col-sm-6 col-xs-12 hm-top-panel-item">
										<div class="hm-item-row cp-item-row clearfix">
											<div class="hm-item-row-left cp-item-row-left text-right">
												<spring:message code="label.confirmnewpassword.key" />
											</div>
											<div class="hm-item-row-right">
												<input type="password" name="confirmnewpassword"
													id="confnw-pwd" data-non-empty="true"
													class="hm-item-row-txt cp-item-row-txt"
													placeholder='Confirm New Password'>
												<div id="" class="input-error-2 error-msg"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div
								class="hm-btn-outer margin-bottom-25 margin-top-5 cp-btn-save clearfix hm-item-row-right-btn-save">
								<div
									class="clearfix hm-btn-wrapper hm-btn-wrapper-fix margin-0-auto">
									<div class="btn-payment-sel cp-btn-save" id="save-pwd">
										<spring:message code="label.save.key" />
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>
<script
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
<script>
	var isChangePasswordFormValid;
	$(document).ready(function() {
		isChangePasswordFormValid = false;
		function submitChangepassword() {
			console.log("submitting change password");
			$('#change-password-form').submit();

		}
		function updatechangepassword(formid) {
			var url = "./changepassword.do";
			callAjaxFormSubmit(url, updatechangepasswordCallBack, formid);
		}
		function updatechangepasswordCallBack(response) {
			$("#main-content").html(response);
		}
		$('#save-pwd').click(function(e) {
			if(validateChangePasswordForm()){
				updatechangepassword('change-password-form');
			}
		});
		$('input').keypress(function(e) {
			// detect enter
			if (e.which == 13) {
				e.preventDefault();
				if(validateChangePasswordForm()){
					updatechangepassword('change-password-form');
				}
			}
		});

		$('#current-pwd').blur(function() {
			validateCurrentPassword(this.id);
		});

		$('#new-pwd').blur(function() {
			validateNewPassword(this.id);
		});

		$('#confnw-pwd').blur(function() {
			validateConfirmNewPassword('new-pwd', this.id);
		});
		function validateChangePasswordForm(){
			$("#serverSideerror").hide();
			isChangePasswordFormValid=true;
        	var isFocussed = false;
        	var isSmallScreen = false;
        	if($(window).width()<768){
        		isSmallScreen = true;
        	}
			if (!validateCurrentPassword('current-pwd')) {
				isChangePasswordFormValid = false;
				if (!isFocussed) {
					$('#current-pwd').focus();
					isFocussed = true;
				}
				if (isSmallScreen) {
					return isChangePasswordFormValid;
				}
			}
			if (!validateNewPassword('new-pwd')) {
				isChangePasswordFormValid = false;
				if (!isFocussed) {
					$('#new-pwd').focus();
					isFocussed = true;
				}
				if (isSmallScreen) {
					return isChangePasswordFormValid;
				}
			}
			if (!validateConfirmNewPassword('new-pwd', 'confnw-pwd')) {
				isChangePasswordFormValid = false;
				if (!isFocussed) {
					$('#confnw-pwd').focus();
					isFocussed = true;
				}
				if (isSmallScreen) {
					return isChangePasswordFormValid;
				}
			}
			return isChangePasswordFormValid;
		}

	});
</script>