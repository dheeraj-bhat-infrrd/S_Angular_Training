<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty complaintRegSettings }">
	<c:set value="${complaintRegSettings}" var="complaintRegSettings"></c:set>
</c:if>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.complaintregsettings.key" />
			</div>
		</div>
	</div>
</div>

<form action="./updatecomplaintregsettings.do" method="post">
	<!-- Mail Id Input -->
	<div>
	 	<spring:message code="label.complaintreg.mail.text" />
		<input type="text" name="mailId" value="${complaintRegSettings.mailId}">
	</div>

	<!-- set the min rating -->
	<div>
		<spring:message code="label.complaintreg.rating.text" />
		<select name="rating">
			<option value="5">5</option>
			<option value="4.5">4.5</option>
			<option value="4">4</option>
			<option value="3.5">3.5</option>
			<option value="3" selected>3</option>			
			<option value="2.5">2.5</option>	
			<option value="2">2</option>		
			<option value="1.5">1.5</option>
			<option value="1">1</option>
			<option value="0.5">0.5</option>
		</select>
		</div>

		<!-- Mood selection -->
		<div>
			<spring:message code="label.complaintreg.mood.text" />
			<select  name="mood">
				<option value="OK" id="mood-OK" selected><spring:message code="label.smile.neutral.text" /></option>
				<option value="Unpleasent" id="mood-Unpleasent"><spring:message code="label.smile.sad.text" /></option>
			</select>
		</div>
		<div>
			<input type="submit" value="Save"/>
		</div>
</form>

<div id="toast-container" class="toast-container">
	   <span id="overlay-toast" class="overlay-toast"></span>
</div>

<!-- window to display reviews flagged as under resolution  -->
<div>
	<div>
		<spring:message code="label.complaintres.review.text" />
	</div>
	<div><spring:message code="label.complaintres.reviewdesc.text" /></div>
	
	<c:if test="${not empty complaintRegSettings.mailId }">
		<div><spring:message code="label.complaintres.criteria.text" /></div>
		<div><spring:message code="label.complaintreg.rating.text" /> : ${complaintRegSettings.rating}</div>
		<div><spring:message code="label.complaintreg.mood.text" /> : ${complaintRegSettings.mood}</div>
	</c:if>
	<div id="sur-under-res-list">
		<!-- Javascript will display surveys under Complaint resolution -->
	</div>
</div>

<script>
$(document).ready(function() {
	showSurveysUnderResolution(0,10);
});
</script>