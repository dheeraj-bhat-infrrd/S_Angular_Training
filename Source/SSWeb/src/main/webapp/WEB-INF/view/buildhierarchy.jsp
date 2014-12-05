<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!-- JIRA SS-37 BY RM06 BOC -->
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title><spring:message code="label.hierarchy.title.key"></spring:message></title>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/jquery-2.1.1.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/bootstrapValidator.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
	<div id="buildhierarchyMainWrapper" class="dashboardWrapper">
		<div id="headerContainer"></div>
		<div id="buildhierarchyWrapper" class="dashboardBodyWrapper">
			<div id="buildhierarchyBodyHeader" class="dashboardBodyHeader"></div>
			<div id="regionWrapper" class="hierarchySettingsWrapper">
				<div id="regionHeader" class="hierarchySettingsHeader clearfix">
					<div class="floatLeft">
						<span>Create Region</span> <a>Dont have region</a>
					</div>
					<div class="floatRight"></div>
				</div>
				<div id="regionModalContainer"
					class="hierarchySettingsModalContainer clearfix">
					<div class="floatLeft leftContainer">
						<div id="addRegionFormContainer" class="formContainer">
							<form id="addRegionForm" class="form-horizontal">
								<div class="form-group">
									<label class="col-sm-3 control-label">New Region</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="newRegion"
											id="newRegion" placeholder="New Region Name" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">Address Line 1</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="regionAddress1"
											id="regionAddress1" placeholder="Address Line 1" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">Address Line 2</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="regionAddress2"
											id="regionAddress2" placeholder="Address Line 2" />
									</div>
								</div>
								<div class="formFooter clearfix">
									<div class="form-Button floatLeft">Save</div>
									<div class="form-Button floatRight">Clear</div>
								</div>
							</form>
						</div>
					</div>
					<div class="floatRight RightContainer"></div>
				</div>
			</div>
			<div id="branchWrapper" class="hierarchySettingsWrapper">
				<div id="branchHeader" class="hierarchySettingsHeader clearfix">
					<div class="floatLeft">
						<span>Create Branch</span> <a>Dont have branch</a>
					</div>
					<div class="floatRight"></div>
				</div>
				<div id="regionModalContainer"
					class="hierarchySettingsModalContainer clearfix">
					<div class="floatLeft leftContainer">
						<div id="addRegionFormContainer" class="formContainer">
							<form id="addRegionForm" class="form-horizontal">
								<div class="form-group">
									<label class="col-sm-3 control-label">New Branch</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="newRegion"
											id="newRegion" placeholder="New Branch Name" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">Select Region</label>
									<div class="col-sm-7">
										<select id="selectRegion" class="form-control"></select>
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">Address Line 1</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="branchAddress1"
											id="branchAddress1" placeholder="Address Line 1" />
									</div>
								</div>
								<div class="form-group">
									<label class="col-sm-3 control-label">Address Line 2</label>
									<div class="col-sm-7">
										<input type="text" class="form-control" name="brachAddress2"
											id="brachAddress2" placeholder="Address Line 2" />
									</div>
								</div>
							</form>
						</div>
						<div class="formFooter clearfix">
							<div class="form-Button floatLeft">Save</div>
							<div class="form-Button floatRight">Clear</div>
						</div>
					</div>
					<div class="floatRight RightContainer"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
<!-- JIRA SS-37 BY RM06 EOC -->