<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:if test="${not empty incompleteSurveys}">
	<c:forEach var="survey" items="${incompleteSurveys}">
		<div class="dash-lp-item clearfix" data-sur-iden="${survey.surveyPreIntitiationId}" 
			data-iden="sur-pre-${survey.surveyPreIntitiationId }">
			<div class="float-left sur-icn-checkbox sb-q-chk-no"></div>
			<div class="float-left dash-lp-txt">
				<div>Customer Name - ${survey.customerFirstName}
					${survey.customerLastName}</div>
				<div>Agent Name - ${survey.agentName}</div>
				<div>Reminder count - ${survey.reminderCounts}</div>
				<div class="font-11 opensanslight"
					data-modifiedon="<fmt:formatDate type="date" pattern="yyyy-MM-dd-hh-mm-ss"
							value="${survey.modifiedOn}" />">
				</div>
			</div>
			<%-- <div class="float-right dash-icn-close cursor-pointer"
				title="Cancel Survey Reminder"
				onclick="removeIncompleteSurveyRequest(${survey.surveyPreIntitiationId })"></div> --%>
			<div
				data-custname="${survey.customerFirstName} ${survey.customerLastName}"
				data-agentid="${survey.agentId}"
				data-agentname="${survey.agentName}"
				data-custemail="${survey.customerEmailId}"
				class="float-right dash-lp-rt-img cursor-pointer"
				title="Resend Survey"></div>
		</div>
	</c:forEach>
</c:if>
<script>
	$(document).ready(function() {
		$('.opensanslight').each(function(index, currentElement) {
			var dateSplit = $(this).attr('data-modifiedon').split('-');
			var date = convertTimeStampToLocalTimeStamp(new Date(dateSplit[0],dateSplit[1] - 1, dateSplit[2], dateSplit[3], dateSplit[4], dateSplit[5]));
			$(this).html(date);
		});
		$('.sur-icn-checkbox').each(function(index, currentElement) {
			var selectSurveyToDelete = $('#icn-sur-popup-cont').data('selected-survey-to-delete');
			if(selectSurveyToDelete == undefined) {
				return;
			} else {
				var surveyId = $(this).closest('.dash-lp-item').attr('data-sur-iden');
				var index = selectSurveyToDelete.indexOf(surveyId);
				if(index > -1) {
					$(this).addClass('sb-q-chk-yes').removeClass('sb-q-chk-no');
				}
			}
		});
		$('.sur-icn-checkbox').on('click',function(e){
			var selectSurveyToDelete = $('#icn-sur-popup-cont').data('selected-survey-to-delete');
			if(selectSurveyToDelete == undefined) {
				selectSurveyToDelete = new Array();
			}
			var surveyId = $(this).closest('.dash-lp-item').attr('data-sur-iden');
			var index = selectSurveyToDelete.indexOf(surveyId);
			if($(this).hasClass('sb-q-chk-no')){
				$(this).addClass('sb-q-chk-yes').removeClass('sb-q-chk-no');
				if(index < 0){
					selectSurveyToDelete.push(surveyId);
				}
			} else {
				$(this).addClass('sb-q-chk-no').removeClass('sb-q-chk-yes');
				if(index > -1) {
					selectSurveyToDelete.splice(index, 1);
				}
			}
			$('#icn-sur-popup-cont').data('selected-survey-to-delete',selectSurveyToDelete);
			if(selectSurveyToDelete != undefined && selectSurveyToDelete.length > 0 ) {
				$('#del-mult-sur-icn').addClass('del-mult-sur-icn-active');
			} else {
				$('#del-mult-sur-icn').removeClass('del-mult-sur-icn-active');
			}
		});
	});
</script>