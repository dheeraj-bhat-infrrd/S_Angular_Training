<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
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
<script type="text/javascript"
	src="${pageContext.request.contextPath}/resources/js/common.js"></script>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/bootstrap-theme.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/style.css">

<script type="text/javascript">
	function addRegion() {
		console.log("addRegion called..");
		var url = "./addregion.do";
		callAjaxFormSubmit(url, addRegionCallBack, "add-region-form");
		console.log("addRegion finished..");
	}
	function addRegionCallBack(data) {
		console.log(data);
	}
	function addBranch() {
		console.log("addBranch called..");
		var url = "./addbranch.do";
		callAjaxFormSubmit(url, addRegionCallBack, "add-branch-form");
		console.log("addBranch finished..");
	}
	function addBranchCallBack(data) {
		console.log(data);
	}
	$(document).ready(function() {
		$(document).attr("title", "Build Hierarcy");
		$("#btn-add-region").on('click', function() {
			console.log("inside click");
			addRegion();
		});
		$("#btn-add-branch").on('click', function() {
			console.log("inside click");
			addBranch();
		});
	});
</script>
</head>
<body>
	<div id="buildhierarchyMainWrapper" class="dashboardWrapper">
		<div id="headerContainer"></div>
		<div id="buildhierarchyWrapper" class="dashboardBodyWrapper">
			<div id="buildhierarchyBodyHeader" class="dashboardBodyHeader"></div>
			<c:if test="${isRegionAdditionAllowed}">
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
								<form id="add-region-form" class="form-horizontal">
									<div class="form-group">
										<label class="col-sm-3 control-label">New Region</label>
										<div class="col-sm-7">
											<input type="text" class="form-control" name="regionName"
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
										<div class="form-Button floatLeft" id="btn-add-region">Save</div>
										<div class="form-Button floatRight">Clear</div>
									</div>
								</form>
							</div>
						</div>
						<div class="floatRight RightContainer"></div>
					</div>
				</div>
			</c:if>
			<c:if test="${isBranchAdditionAllowed}">
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
								<form id="add-branch-form" class="form-horizontal">
									<div class="form-group">
										<label class="col-sm-3 control-label">New Branch</label>
										<div class="col-sm-7">
											<input type="text" class="form-control" name="branchName"
												id="newRegion" placeholder="New Branch Name" />
										</div>
									</div>
									<div class="form-group">
										<label class="col-sm-3 control-label">Select Region</label>
										<div class="col-sm-7">
											<select id="select-region" class="form-control"></select>
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
											<input type="text" class="form-control" name="branchAddress2"
												id="brachAddress2" placeholder="Address Line 2" />
										</div>
									</div>
								</form>
							</div>
							<div class="formFooter clearfix">
								<div class="form-Button floatLeft" id="btn-add-branch">Save</div>
								<div class="form-Button floatRight">Clear</div>
							</div>
						</div>
						<div class="floatRight RightContainer"></div>
					</div>
				</div>
			</c:if>
		</div>
	</div>
</body>
</html>
<!-- JIRA SS-37 BY RM06 EOC -->