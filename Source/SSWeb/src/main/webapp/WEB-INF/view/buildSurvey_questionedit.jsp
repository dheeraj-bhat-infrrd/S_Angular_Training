<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty surveyQuestion && not empty surveyQuestion.questionType}">
	<c:set value="${fn:trim(surveyQuestion.questionType)}" var="questionType" />
</c:if>

<c:set value="${surveyQuestion.isUserRankingQuestion }" var="isUserRankingQuestion" />
<c:set value="${surveyQuestion.considerForScore }" var="considerForScore" />
<c:set value="${surveyQuestion.notAtAllLikely }" var="notVeryLikely" />
<c:set value="${surveyQuestion.veryLikely }" var="veryLikely" />

<c:set value="false" var="isUserRankingQuestionVal" />
<c:if test="${isUserRankingQuestion == 1}">
	<c:set value="true" var="isUserRankingQuestionVal" />
</c:if>

<c:set value="false" var="considerForScoreVal" />
<c:if test="${considerForScore == 1}">
	<c:set value="true" var="considerForScoreVal" />
</c:if>


<div class="sb-edit-q-wrapper">
	<form id="bs-question-edit-${surveyQuestion.questionId}" data-quesnum="${surveyQuestion.questionId}">
		<div class="bd-q-pu-header bd-q-pu-header-margin-override clearfix">
			<div class="float-left bd-q-pu-header-lft"><spring:message code="label.editquestion.key" /></div>
		</div>
		<div class="bd-q-pu-txt-wrapper pos-relative">
			<input type="hidden" id="sb-question-edit-type-${surveyQuestion.questionId}" name="sb-question-type-${surveyQuestion.questionId}" value="${surveyQuestion.questionType}"/>
			<input id="sb-question-edit-txt-${surveyQuestion.questionId}" name="sb-question-txt-${surveyQuestion.questionId}" class="bd-q-pu-txt-edit" data-nextquest="false" value="${surveyQuestion.question}">
			<div class="bd-q-pu-close hide"></div>
		</div>
		<div class="bs-ans-wrapper hide" style="display: block">
			<div class="bd-and-header-txt"><spring:message code="label.customer.reply.key" /></div>
			<div class="bd-ans-options-wrapper">
				<div class="bd-ans-header clearfix">
					<div class="bd-ans-hd-container clearfix float-left">
						<c:choose>
							<c:when test="${questionType == 'sb-range-smiles' || questionType == 'sb-range-star' || questionType == 'sb-range-scale'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-range-0to10" class="bd-tab-rad float-left bd-ans-tab-item"><spring:message code="label.rating.0to10.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
							<c:when test="${questionType == 'sb-sel-mcq'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-range-0to10" class="bd-tab-rad float-left bd-ans-tab-item"><spring:message code="label.rating.0to10.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
							<c:when test="${questionType == 'sb-sel-desc'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-range-0to10" class="bd-tab-rad float-left bd-ans-tab-item"><spring:message code="label.rating.0to10.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
							<c:when test="${questionType == 'sb-range-0to10'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-range-0to10" class="bd-tab-rad float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.rating.0to10.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
						</c:choose>
					</div>
				</div>
				
				<c:set var="radioClass" scope="page" value=""/>
				<c:if test="${questionType != 'sb-range-0to10'}">
					<c:set var="radioClass" scope="page" value="hide"/>
				</c:if>

				<div class="bd-ans-type-radio bd-ans-type-item ${radioClass}">
					<div class="bd-and-tier2"><spring:message code="label.customer.customize.descriptions.key" /></div>
					<div class="clearfix bd-ans-type bd-ans-type-radio-adj">
						<div id="sq-radio-1to10-edit" class="sq-1to10-range" style="height:160px" selected-rating-radio="">
							<div class="edit-survey-radio-container">
								<div class="container-1to10 inner-container-1to10">
									<c:forEach begin="0" end="10" varStatus="loop">
										<c:choose>
											<c:when test="${loop.index == 0}">
												<div class="radio-div" style="margin-left: 10px">
													<div class="radio-outer-gray sq-radio"></div>
												</div>
											</c:when>
											<c:otherwise>
												<div class="radio-div">
													<div class="radio-outer-gray sq-radio"></div>
												</div>
											</c:otherwise>
										</c:choose>
									</c:forEach>
								</div>
								<div class="container-1to10">
									<c:forEach begin="0" end="10" varStatus="numLoop">
										<div class="span-1to10" style="text-align:right">${numLoop.index}</div>
									</c:forEach>
								</div>
								<div class="sq-1to10-range-val">
									<input id="sq-not-very-likely-edit" name="notVeryLikely" class="float-left sq-range-1to10-input sq-not-very-likely" value="${notVeryLikely}">
									<input id="sq-very-likely-edit" name="veryLikely" class="float-right sq-range-1to10-input text-align-right sq-very-likely" value="${veryLikely}">
								</div>
							</div>
						</div>
					</div>
					<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
						<div id="user-ranking-chkbox-wrapper-edit-nps" class="clearfix" style="width: 200px;">
							<div id="user-ranking-chkbox-edit-nps" class='float-left user-ranking-chkbox bd-check-img <c:if test="${ isUserRankingQuestion == 0}">bd-check-img-checked</c:if>'></div>
							<input type="hidden" id="user-ranking-ques-edit-nps" name="user-ranking-ques" value=${ isUserRankingQuestionVal }>
							<div class="float-left  listing-access-txt cursor-pointer">Considered for User Ranking</div>
						</div>
						<div id="avg-score-chkbox-wrapper-edit" class="clearfix" style="width: 210px;"> 
							<div id="avg-score-chkbox-edit" class='float-left avg-score-chkbox bd-check-img <c:if test="${ considerForScore == 0}">bd-check-img-checked</c:if>'></div>
							<input type="hidden" id="avg-score-ques-edit" name="considerForScore" value=${ considerForScoreVal }>
							<div class="float-left listing-access-txt cursor-pointer">Considered for Average Score</div>
						</div>
						<div id="nps-chkbox-wrapper-edit" class="clearfix">
							<input type="hidden" id="nps-ques-edit" name="nps-ques" value=false>
						</div>
					</div>
				</div>

				<c:set var="ratingclass" scope="page" value=""/>
				<c:if test="${questionType != 'sb-range-smiles' && questionType != 'sb-range-star' && questionType != 'sb-range-scale'}">
					<c:set var="ratingclass" scope="page" value="hide"/>
				</c:if>
				<div class="bd-ans-type-rating bd-ans-type-item ${ratingclass}">
					<div class="bd-and-tier2"><spring:message code="label.customer.answer.key" /></div>
					<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
							<div data-id="sb-range-smiles" class="bd-ans-img-wrapper">
								<c:choose>
									<c:when test="${questionType != 'sb-range-smiles'}">
										<div class="bd-ans-img bd-ans-smiley bd-img-sel"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.smiles.key" /></div>
									</c:when>
									<c:otherwise>
										<div class="bd-ans-img bd-ans-smiley"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.smiles.key" /></div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
							<div data-id="sb-range-star" class="bd-ans-img-wrapper">
								<c:choose>
									<c:when test="${questionType != 'sb-range-star'}">
										<div class="bd-ans-img bd-ans-star bd-img-sel"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.star.key" /></div>
									</c:when>
									<c:otherwise>
										<div class="bd-ans-img bd-ans-star"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.star.key" /></div>
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<%-- <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
							<div data-id="sb-range-scale" class="bd-ans-img-wrapper">
								<c:choose>
									<c:when test="${questionType != 'sb-range-scale'}">
										<div class="bd-ans-img bd-ans-scale bd-img-sel"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.scale.key" /></div>
									</c:when>
									<c:otherwise>
										<div class="bd-ans-img bd-ans-scale"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.scale.key" /></div>
									</c:otherwise>
								</c:choose>
							</div>
						</div> --%>
					</div>
					
					<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
						<div id="user-ranking-chkbox-wrapper-edit" class="clearfix" style="width: 200px;">
								
									<div id="user-ranking-chkbox-edit" class='float-left user-ranking-chkbox bd-check-img <c:if test="${ isUserRankingQuestion == 0}">bd-check-img-checked</c:if>'></div>
									<input type="hidden" id="user-ranking-ques-edit" name="user-ranking-ques" value=${ isUserRankingQuestionVal }>
								
								
								<div class="float-left  listing-access-txt cursor-pointer">Considered for User Ranking</div>
							</div>
  					</div>
					
				</div>
				
				<c:set var="mcqclass" scope="page" value=""/>
				<c:if test="${questionType != 'sb-sel-mcq'}">
					<c:set var="mcqclass" scope="page" value="hide"/>
				</c:if>
				
				<div class="bd-ans-type-mcq bd-ans-type-item ${mcqclass}">
					<div class="bd-and-tier2"><spring:message code="label.customer.answerfrom.key" /></div>
					<div class="clearfix bd-ans-type bd-ans-type-mcq-adj">
						<c:if test="${fn:length(surveyQuestion.answers) <= 2}">
							<c:set value="hide" var="closebuttonclass" />
						</c:if>
						<c:choose>
							<c:when test="${questionType == 'sb-sel-mcq'}">
								<c:forEach var="answer" items="${surveyQuestion.answers}">
									<div class="bd-mcq-row clearfix">
										<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
										<input name="sb-answers-${surveyQuestion.questionId}[]" class="float-left bd-mcq-txt" value="${answer.answerText}">
										<div class="float-left bd-mcq-close ${closebuttonclass}"></div>
									</div>
								</c:forEach>
							</c:when>
							<c:otherwise>
								<div class="bd-mcq-row clearfix">
									<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
									<input name="sb-answers-${surveyQuestion.questionId}[]" class="float-left bd-mcq-txt">
									<div class="float-left bd-mcq-close hide"></div>
								</div>
								<div class="bd-mcq-row clearfix">
									<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
									<input name="sb-answers-${surveyQuestion.questionId}[]" class="float-left bd-mcq-txt">
									<div class="float-left bd-mcq-close hide"></div>
								</div>
								<div class="bd-mcq-row clearfix">
									<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
									<input name="sb-answers-${surveyQuestion.questionId}[]" class="float-left bd-mcq-txt">
									<div class="float-left bd-mcq-close hide"></div>
								</div>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
				<c:set var="comclass" scope="page" value=""/>
				<c:if test="${questionType != 'sb-sel-desc'}">
					<c:set var="comclass" scope="page" value="hide"/>
				</c:if>
				<div class="bd-ans-type-com bd-ans-type-item ${comclass}">
					<div class="clearfix bd-com-wrapper">
						<!-- <div class="float-left bd-com-chk"></div> -->
						<div class="float-left bd-com-txt"><spring:message code="label.textarea.key" /></div>
					</div>
				</div>
			</div>
			<div class="bd-q-status-wrapper text-center hide">
				<span class="bd-spinner">`</span>
				<span class="bd-q-status-txt"></span>
			</div>
		</div>
	</form>
	<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
  		<div data-quesnum="${surveyQuestion.questionId}" class="bd-q-btn-done-edit float-left"><spring:message code="label.done.key" /></div>
  	</div>
</div>