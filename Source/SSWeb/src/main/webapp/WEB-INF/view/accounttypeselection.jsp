<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA : SS-24 by RM-02
	Page for selecting the account type(plan)
-->
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.title.registerUser.key" /></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
<script type="text/javascript">
	function selectAccountType(accountType) {
		console.log("selecting and saving account type");
		$("#accountType").val(accountType);
		document.accountTypeSelectionform.submit();
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
							<form role="form" id="accountTypeSelectionForm" method="post"
								action="addaccounttype.do">
								<div class="accountTypeBoxWrapper">
									<div class="form-group formInputField">
										<input type="hidden" name="accounttype" id="accountType" />
										<div class="accountTypeBox" id="accountTypeIndividual">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.individual.key" />
											</div>
											<button class="formButton" id="typeIndividualButton"
												onclick="javascript:selectAccountType('individual')">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeTeam">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.team.key" />
											</div>
											<button class="formButton" id="typeTeamButton"
												onclick="javascript:selectAccountType('team')">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeCompany">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.company.key" />
											</div>
											<button class="formButton" id="typeCompanyButton"
												onclick="javascript:selectAccountType('company')">
												<spring:message code="label.select.key" />
											</button>
										</div>
										<div class="accountTypeBox" id="accountTypeEnterprise"
											onclick="javascript:selectAccountType('enterprise')">
											<div class="accountTypeBoxHeader">
												<spring:message code="label.accounttype.enterprise.key" />
											</div>
											<button class="formButton" id="typeEnterpriseButton">
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
				<div class="formPageFooter">Copyright © 2014 Social Survey.
					All rights reserved.</div>
			</div>
		</div>
	</div>
</body>
</html>