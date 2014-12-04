<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA SS-31 BY RM02
	Page for selecting the account type(plan)
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.title.registerUser.key" /></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script src="https://js.braintreegateway.com/v2/braintree.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<script type="text/javascript">
	function selectAccountType(accountType) {
		console.log("selecting and saving account type");
		$('#accountType').val(accountType);
		var url = "./addaccounttype.do";
		callAjaxFormSubmit(url, selectAccountTypeCallBack,
				"accountTypeSelectionForm");
	}

	function selectAccountTypeCallBack(data) {
		console.log("callback for selectAccountType called");
		$("#paymentSection").html(data);
		console.log("callback for selectAccountType finished");
	}
</script>
</head>
<body>
	<div id="accountTypeSelectionMainWrapper" class="mainWrapper">
		<div class="overlay">
			<div class="formModalContainer">
				<div class="hide" id="messageHeader"></div>
				<div id="accountTypeSelectionContainer" class="formWrapper">
					<div id="formHeaderBar"></div>
					<div class="formBody" id="accountTypeSelectionBody">
						<div class="formBodyMainText">
							<spring:message code="label.accounttypeselection.header.key"></spring:message>
						</div>
						<div class="formContainer">
							<form id="accountTypeSelectionForm">
								<div class="accountTypeBoxWrapper">
									<div class="form-group formInputField">
										<input type="hidden" name="accounttype" id="accountType" />
										<div class="accountTypeBox" id="accountTypeIndividual">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.individual.key" />
											</div>
											<button type="button" class="formButton"
												id="typeIndividualButton"
												onclick="javascript:selectAccountType(1)">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeTeam">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.team.key" />
											</div>
											<button type="button" class="formButton" id="typeTeamButton"
												onclick="javascript:selectAccountType(2)">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeCompany">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.company.key" />
											</div>
											<button type="button" class="formButton"
												id="typeCompanyButton"
												onclick="javascript:selectAccountType(3)">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeEnterprise">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.enterprise.key" />
											</div>
											<button type="button" class="formButton"
												id="typeEnterpriseButton"
												onclick="javascript:selectAccountType(4)">
												<spring:message code="label.select.key" />
											</button>
										</div>
									</div>
									<div class="clearfix"></div>
								</div>
							</form>
						</div>
					</div>
				</div>
				<div id="paymentSection" class="paymentSection"></div>
				<div class="formPageFooter">
					<spring:message code="label.copyright.key"/>
				</div>
			</div>
		</div>
	</div>
</body>
</html>