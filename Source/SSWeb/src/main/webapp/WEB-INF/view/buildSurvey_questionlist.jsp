<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty surveyDetail }">
	<c:if test="${ not empty surveyDetail.questions}">
		<c:set value="${fn:length(surveyDetail.questions)}" var="length" />
		<c:set value="${surveyDetail.status}" var="status" />
		<c:set value="1" var="count" scope="page" />
	</c:if>
	<c:set value="${surveyDetail.countOfRatingQuestions}" var="ratingQuestionCount" scope="page" />
</c:if>
<c:set value="${surveyQuestionsJson}" var="npsQuestion" />

<c:choose>
	<c:when test="${not empty surveyDetail}">
		<c:forEach var="surveyQuestion" items="${surveyDetail.questions}">
			<c:choose>
				<c:when test="${surveyQuestion.isNPSQuestion == 1}">
					
				</c:when>
				<c:otherwise>
					<div
						class="bd-srv-tbl-row clearfix bd-srv-tbl-row-${surveyQuestion.questionId}"
						data-questionid="${surveyQuestion.questionId}"
						data-rating-question="${surveyQuestion.isRatingQuestion}">
						<div class="float-left srv-tbl-num">
							<span>${surveyQuestion.questionOrder}</span>
						</div>

						<!-- setting icon for question type-->
						<c:choose>
							<c:when
								test="${surveyQuestion.questionType == 'sb-range-smiles'}">
								<c:set value="smiley-icn-sm" var="questionicon" scope="page" />
							</c:when>
							<c:when test="${surveyQuestion.questionType == 'sb-range-star'}">
								<c:set value="rating-icn-sm" var="questionicon" scope="page" />
							</c:when>
							<c:when test="${surveyQuestion.questionType == 'sb-range-scale'}">
								<c:set value="scale-icn-sm" var="questionicon" scope="page" />
							</c:when>
							<c:when test="${surveyQuestion.questionType == 'sb-sel-mcq'}">
								<c:set value="mcq-icn-sm" var="questionicon" scope="page" />
							</c:when>
							<c:when test="${surveyQuestion.questionType == 'sb-sel-desc'}">
								<c:set value="desc-icn-sm" var="questionicon" scope="page" />
							</c:when>
						</c:choose>

						<div class="float-left srv-tbl-txt ${questionicon}">${surveyQuestion.question}</div>
						<div class="srv-tbl-btns clearfix float-right hide">
							<c:choose>
								<c:when test="${count == 1}">
									<div class="float-left srv-tbl-move-dn"></div>
								</c:when>
								<c:when test="${count == length}">
									<div class="float-left srv-tbl-move-up"></div>
								</c:when>
								<c:otherwise>
									<div class="float-left srv-tbl-move-dn"></div>
									<div class="float-left srv-tbl-move-up"></div>
								</c:otherwise>
							</c:choose>
							<div class="float-left srv-tbl-edit">
								<spring:message code="label.edit.key" />
							</div>
							<div class="float-left srv-tbl-rem">
								<spring:message code="label.remove.key" />
							</div>
						</div>
					</div>
					<c:set value="${count + 1}" var="count" scope="page" />
				</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<spring:message code="label.noquestionsadded.key"/>
	</c:otherwise>
</c:choose>

<script>
$(document).ready(function() {
	var status = "${status}";
	if( '${ratingQuestionCount}' != undefined ){
		ratingQuestionCount = '${ratingQuestionCount}';
	}
	if (status != "") {
		setTimeout(function() {
			showError(status);
		}, 300);
	} else {
		hideError();
	}
	
	var surveyDetails =JSON.parse('${npsQuestion}');
	var surveyQuestionsList = surveyDetails.questions;
	var npsQuestion=null;
	
	for(var i=0;i<surveyQuestionsList.length;i++){
		if(surveyQuestionsList[i].isNPSQuestion == 1){
			npsQuestion = surveyQuestionsList[i];
			break;
		}
	}
	
	if(npsQuestion == null){
		$('#nps-chkbox').addClass('bd-check-img-checked');
		$('#nps-ques').val(false);
		$('#nps-add-edit').hide();
		$("#overlay-toast").html('Enable NPS Question to add/edit the NPS question');
		$('#nps-question-form').attr('data-state','new');
		$('#nps-question-form').attr('data-status','new');
		$('#nps-question-form').attr('data-quesref','');
		$('#sb-nps-question-txt').val(defaultNpsQuestion);
		$('#sq-not-very-likely-nps').val(defaultNotVeryLikely);
		$('#sq-very-likely-nps').val(defaultVeryLikely);
		showToast('error');
	}else{
		$('#nps-add-edit').show();
		$('#nps-chkbox').removeClass('bd-check-img-checked');
		$('#nps-ques').val(true);
		$('#nps-question-form').attr('data-state','editable');
		$('#nps-question-form').attr('data-status','new');
		$('#nps-question-form').attr('data-quesref',npsQuestion.questionId);
		$('#sb-nps-question-txt').val(npsQuestion.question);
		$('#sq-not-very-likely-nps').val(npsQuestion.notAtAllLikely);
		$('#sq-very-likely-nps').val(npsQuestion.veryLikely);
		
		if(npsQuestion.isUserRankingQuestion == 1){
			if ($('#user-ranking-chkbox-nps').hasClass('bd-check-img-checked')) {		
				$('#user-ranking-chkbox-nps').removeClass('bd-check-img-checked');
				 $('#user-ranking-ques-nps').val(true);
			}
		}else{
			$('#user-ranking-chkbox-nps').addClass('bd-check-img-checked')
			 $('#user-ranking-ques-nps').val(false);
		}
		
		if(npsQuestion.considerForScore == 1){
			if ($('#avg-score-chkbox-nps').hasClass('bd-check-img-checked')) {		
				$('#avg-score-chkbox-nps').removeClass('bd-check-img-checked');
				 $('#avg-score-ques-nps').val(true);
			}
		}else{
			$('#avg-score-chkbox-nps').addClass('bd-check-img-checked')
			 $('#avg-score-ques-nps').val(false);
		}
	}
});
</script>