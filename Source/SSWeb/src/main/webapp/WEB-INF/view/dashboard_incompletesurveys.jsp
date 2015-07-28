<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:if test="${not empty incompleteSurveys}">
	<c:forEach var="survey" items="${incompleteSurveys}"  varStatus="loop">
		<div class="dash-lp-item clearfix"  data-iden="sur-pre-${survey.surveyPreIntitiationId }">
			<div class="float-left dash-lp-txt">
				${survey.customerFirstName} ${survey.customerLastName}
					<div class="font-11 opensanslight" data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-hh-mm-ss"
							value="${survey.modifiedOn}" />">
					</div>
			</div>
			<div
				data-custname="${survey.customerFirstName} ${survey.customerLastName}"
				data-agentid="${survey.agentId}" data-agentname="${agentName}"
				data-custemail="${survey.customerEmailId}"
				class="float-right dash-lp-rt-img cursor-pointer"
				title="Resend Survey"></div>
		</div>
	</c:forEach>
</c:if>
<script>
$(document).ready(function(){
	$('.opensanslight').each(function(index, currentElement) {
		var dateSplit = $(this).attr('data-modifiedon').split('-');
		var date = convertTimeStampToLocalTimeStamp(new Date(dateSplit[0], dateSplit[1]-1, dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
		$(this).html(date);
	});
});
</script>