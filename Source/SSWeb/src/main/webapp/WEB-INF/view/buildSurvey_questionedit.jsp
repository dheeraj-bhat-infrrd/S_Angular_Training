<%@ page language="java" contentType="text/html charset=ISO-8859-1" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:if test="${not empty surveyQuestion && not empty surveyQuestion.questionType}">
	<c:set value="${fn:trim(surveyQuestion.questionType)}" var="questionType" />
</c:if>

<div class="sb-edit-q-wrapper">
	<form id="bs-question-${surveyQuestion.questionId}" data-quesnum="${surveyQuestion.questionId}">
		<div class="bd-q-pu-header bd-q-pu-header-margin-override clearfix">
			<div class="float-left bd-q-pu-header-lft"><spring:message code="label.editquestion.key" /></div>
		</div>
		<div class="bd-q-pu-txt-wrapper pos-relative">
			<input type="hidden" id="sb-question-type-${surveyQuestion.questionId}" name="sb-question-type-${surveyQuestion.questionId}" value="${surveyQuestion.questionType}"/>
			<input id="sb-question-txt-${surveyQuestion.questionId}" name="sb-question-txt-${surveyQuestion.questionId}" class="bd-q-pu-txt-edit" data-nextquest="false" value="${surveyQuestion.question}">
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
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
							<c:when test="${questionType == 'sb-sel-mcq'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
							<c:when test="${questionType == 'sb-sel-desc'}">
								<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item"><spring:message code="label.rating.key" /></div>
								<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.comment.key" /></div>
								<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</c:when>
						</c:choose>
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
  	
