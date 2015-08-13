<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<c:if
	test="${not empty profileSettings && not empty profileSettings.positions}">
	<c:set value="${profileSettings.positions}" var="positions"></c:set>
</c:if>
<div id="prof-position-edit-container"
	class="prof-user-address prof-edit-icn">
	<c:choose>
		<c:when test="${not empty positions}">
			<c:forEach items="${positions}" var="positionItem">
				<div class="pos-cont margin-top-10 text-left">
					<div class="checkbox-input-cont"><div class="checkbox-input checkbox-iscurrent" data-checked="${positionItem.isCurrent}"></div>Current Employer</div>
					<input name="companyName" class="lp-pos-row-1 pos-input lp-row clearfix" value="${positionItem.name}" placeholder="Company Name">
					<input name="title" class="lp-pos-row-2 pos-input lp-row clearfix" value="${positionItem.title}" placeholder="Job Title">
					<input name="startTime" class="lp-pos-row-2 pos-input lp-row clearfix" value="${positionItem.startTime}" placeholder="Start Date">
					<c:choose>
						<c:when test="${positionItem.isCurrent}">
							<input name="endTime" class="lp-pos-row-2 pos-input lp-row clearfix hide" value="${positionItem.endTime}" placeholder="End Date">
						</c:when>
						<c:otherwise>
							<input name="endTime" class="lp-pos-row-2 pos-input lp-row clearfix" value="${positionItem.endTime}" placeholder="End Date">
						</c:otherwise>
					</c:choose>
					<div class="pos-remove-icn"></div>
				</div>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="pos-cont margin-top-10 text-left">
				<div class="checkbox-input-cont">
					<div class="checkbox-input checkbox-iscurrent"
						data-checked="false"></div>
					Current Employer
				</div>
				<input name="companyName" class="pos-input" placeholder='Company Name'>
				<input name="title" class="pos-input" placeholder='Job Title'>
				<input name="startTime" class="pos-input" placeholder='Start Date'>
				<input name="endTime" class="pos-input" placeholder='End Date'>
				<div class="pos-remove-icn"></div>
			</div>
		</c:otherwise>
	</c:choose>
	<div class="text-right add-pos-link float-right clear-both">Add another</div>
</div>
