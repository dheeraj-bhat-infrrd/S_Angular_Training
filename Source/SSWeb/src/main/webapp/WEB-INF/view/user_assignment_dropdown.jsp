<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:if test="${not empty assignments}">
	<div id="da-dd-wrapper"
		class="float-right header-right clearfix hr-dsh-adj-rt hdr-prof-sel hide">
		<div class="float-left hr-txt1">
			<spring:message code="label.viewas.key" />
		</div>
		<div id="dashboard-sel" class="float-left hr-txt2 cursor-pointer">${entityName}</div>
		<div id="da-dd-wrapper-profiles" class="va-dd-wrapper hide">
			<c:if test="${not empty assignments.agents}">
				<div class="dd-tl">User</div>
				<c:forEach var="agent" items="${assignments.agents}">
					<div class="da-dd-item" data-column-type="agentId"
						data-column-name="${agent.value}" data-column-value="${agent.key}">${agent.value}</div>
				</c:forEach>
			</c:if>
			<c:if test="${not empty assignments.companies}">
				<div class="dd-tl">Company</div>
				<c:forEach var="company" items="${assignments.companies}">
					<div class="da-dd-item" data-column-type="companyId"
						data-column-name="${company.value}"
						data-column-value="${company.key}">${company.value}</div>
				</c:forEach>
			</c:if>
			<c:if test="${not empty assignments.regions}">
				<div class="dd-tl">Region</div>
				<c:forEach var="region" items="${assignments.regions}">
					<div class="da-dd-item" data-column-type="regionId"
						data-column-name="${region.value}"
						data-column-value="${region.key}">${region.value}</div>
				</c:forEach>
			</c:if>
			<c:if test="${not empty assignments.branches}">
				<div class="dd-tl">Office</div>
				<c:forEach var="branch" items="${assignments.branches}">
					<div class="da-dd-item" data-column-type="branchId"
						data-column-name="${branch.value}"
						data-column-value="${branch.key}">${branch.value}</div>
				</c:forEach>
			</c:if>
		</div>
	</div>
</c:if>