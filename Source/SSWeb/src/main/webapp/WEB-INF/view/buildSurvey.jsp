<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row hm-header-row-main clearfix">
			<div class="float-left hm-header-row-left text-center"><spring:message code="label.buildsurvey.header.key" /></div>
		</div>
	</div>
</div>

<div id="bd-srv-pu" class="bd-srv-pu hide">
	<div class="container bd-q-container">
		<div id="bd-quest-wrapper" class="bd-q-wrapper">
			<div class="bd-quest-item">
				<form id="bs-question-1" data-quesnum="1" data-quesref="" data-state="new" data-status="new">
					<div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
						<div class="float-left bd-q-pu-header-lft"><spring:message code="label.create.surveyquestions.key" /></div>
						<div class="float-right bd-q-pu-header-rt cursor-pointer"><spring:message code="label.needhelp.key" /></div>
					</div>
					<div class="bd-q-pu-txt-wrapper pos-relative">
						<input type="hidden" id="sb-question-type-1" name="sb-question-type-1" data-state="new" value="sb-range-smiles"/>
						<input id="sb-question-txt-1" name="sb-question-txt-1" class="bd-q-pu-txt" data-nextquest="false" data-qno="1">
						<div class="bd-q-pu-close hide"></div>
					</div>
					<div class="bs-ans-wrapper hide">
						<div class="bd-and-header-txt"><spring:message code="label.customer.reply.key" /></div>
						<div class="bd-ans-options-wrapper">
							<div class="bd-ans-header clearfix">
								<div class="bd-ans-hd-container clearfix float-left">
									<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.rating.key" /></div>
									<div data-id="sb-range-0to10" class="bd-tab-rad float-left bd-ans-tab-item"><spring:message code="label.rating.0to10.key" /></div>
									<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
									<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
								</div>
							</div>
							<div id="" class="bd-ans-type-rating bd-ans-type-item">
								<div class="bd-and-tier2"><spring:message code="label.customer.answer.key" /></div>
								<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
										<div data-id="sb-range-smiles" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-smiley"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.smiles.key" /></div>
										</div>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6">
										<div data-id="sb-range-star" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-star"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.star.key" /></div>
										</div>
									</div>
									<%-- <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
										<div data-id="sb-range-scale" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-scale"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.scale.key" /></div>
										</div>
									</div> --%>
								</div>
								
								<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
									<div id="user-ranking-chkbox-wrapper" class="clearfix" style="width: 200px;">
										<div id="user-ranking-chkbox" class="float-left user-ranking-chkbox bd-check-img"></div>
										<input type="hidden" id="user-ranking-ques" name="user-ranking-ques" value=true>
										<div class="float-left  listing-access-txt cursor-pointer">Consider for User Ranking</div>
									</div>
  								</div>
							</div>

							<div class="bd-ans-type-radio bd-ans-type-item hide">
								<div class="bd-and-tier2"><spring:message code="label.customer.customize.descriptions.key" /></div>
								<div class="clearfix bd-ans-type bd-ans-type-radio-adj">
									<div id="sq-radio-1to10" class="sq-1to10-range" style="height: 160px" selected-rating-radio="">
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
													<div class="span-1to10" style="text-align: right">${numLoop.index}</div>
												</c:forEach>
											</div>
											<div class="sq-1to10-range-val">
												<input id="sq-not-very-likely" name="notVeryLikely" class="float-left sq-range-1to10-input sq-not-very-likely" value="Not at all likely">
												<input id="sq-very-likely" name="veryLikely" class="float-right sq-range-1to10-input text-align-right sq-very-likely" value="Very Likely">
											</div>
										</div>
									</div>
								</div>
								<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
									<div id="user-ranking-chkbox-nps-wrapper" class="clearfix" style="width: 200px;">
										<div id="user-ranking-nps-chkbox" class="float-left user-ranking-chkbox bd-check-img"></div>
										<div class="float-left  listing-access-txt cursor-pointer">Consider for User Ranking</div>
									</div>
									<div id="avg-score-chkbox-wrapper" class="clearfix" style="width: 210px;">
										<div id="avg-score-chkbox" class="float-left avg-score bd-check-img"></div>
										<input type="hidden" id="avg-score-ques" name="considerForScore" value=true>
										<div class="float-left  listing-access-txt cursor-pointer">Consider for Average Score</div>
									</div>
									<div id="nps-chkbox-wrapper-1" class="clearfix">
										<input type="hidden" id="nps-ques-1" name="nps-ques" value=false>
									</div>
								</div>
							</div>

							<div id="" class="bd-ans-type-mcq bd-ans-type-item hide">
								<div class="bd-and-tier2"><spring:message code="label.customer.answerfrom.key" /></div>
								<div class="clearfix bd-ans-type bd-ans-type-mcq-adj">
									<div class="bd-mcq-row clearfix">
										<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
										<input name="sb-answers-1[]" class="float-left bd-mcq-txt">
										<div class="float-left bd-mcq-close hide"></div>
									</div>
									<div class="bd-mcq-row clearfix">
										<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
										<input name="sb-answers-1[]" class="float-left bd-mcq-txt">
										<div class="float-left bd-mcq-close hide"></div>
									</div>
									<div class="bd-mcq-row clearfix">
										<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
										<input name="sb-answers-1[]" class="float-left bd-mcq-txt">
										<div class="float-left bd-mcq-close hide"></div>
									</div>
								</div>
							</div>
							<div id="" class="bd-ans-type-com bd-ans-type-item hide">
								<div class="clearfix bd-com-wrapper">
									<div class="float-left bd-com-txt"><spring:message code="label.textarea.key" /></div>
								</div>
							</div>
						</div>
						<div class="bd-q-status-wrapper text-center hide">
							<span class="bd-spinner">`</span>
							<span class="bd-q-status-txt">Saving</span>
						</div>
					</div>
				</form>
			</div>
			<div class="bd-q-pu-done-wrapper clearfix">
				<div class="bd-q-btn-done float-left"><spring:message code="label.done.key" /></div>
			</div>
		</div>
	</div>
</div>

<div class="container bd-svry-container">
	<div class="bd-svry-header clearfix">
		<div class="float-left bd-svry-left"><spring:message code="label.surveyquestions.key" /></div>
		<%-- <div id="btn-add-question" class="float-right bd-svry-right"><spring:message code="label.addnewquestion.key" /></div> --%>
	</div>
	
	<div class="bd-srv-q-tbl">
		<div class="bd-srv-tbl-header"><spring:message code="label.build.customerfeedback.key" /></div>
		
		<div id="bs-ques-wrapper" class="bd-srv-rows">
			<div class="bd-srv-tbl-row clearfix hide">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="srv-tbl-btns clearfix float-right">
					<div class="float-left srv-tbl-move-dn"></div>
					<div class="float-left srv-tbl-move-up"></div>
					<div class="float-left srv-tbl-edit">Edit</div>
					<div class="float-left srv-tbl-rem">Remove</div>
				</div>
			</div>
			<div class="bd-srv-tbl-row clearfix hide">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="srv-tbl-btns clearfix float-right">
					<div class="float-left srv-tbl-move-dn"></div>
					<div class="float-left srv-tbl-move-up"></div>
					<div class="float-left srv-tbl-edit">Edit</div>
					<div class="float-left srv-tbl-rem">Remove</div>
				</div>
			</div>
		</div>
		<div class="btn-add-ques-bottom-row clearfix">
			<div id="btn-add-question" class="float-right bd-svry-right"><spring:message code="label.addnewquestion.key" /></div>
		</div>
	</div>
	<div id="npsQuestion" data-quesref="" class="bd-srv-q-tbl" style="display:grid">
		<div class="bd-srv-tbl-header">
			<spring:message code="label.build.nps.question.key" />
		</div>
		<form id="nps-question-form" data-quesnum="1" data-quesref="" data-state="new" data-status="edited">
		<div id="nps-chkbox-wrapper" class="clearfix" style="width: 210px;">
			<div id="nps-chkbox" class="float-left avg-score bd-check-img bd-check-img-checked"></div>
			<input type="hidden" id="nps-ques" name="nps-ques" value=false>
			<div class="float-left  listing-access-txt cursor-pointer">Enable NPS Question</div>
		</div>
		
		<div id="nps-add-edit" class="bd-ans-type-radio bd-ans-type-item hide">
			<div class="bd-q-pu-header clearfix">
				<div class="float-left bd-q-pu-header-lft">Enter/Edit Your NPS Questions Here</div>
			</div>
			<div class="col-xs-10 col-sm-10 col-md-10 col-lg-10" style="margin:0 auto">
				<div class="bd-q-pu-txt-wrapper pos-relative">
					<input type="hidden" id="sb-nps-question" name="sb-question-type-999" value="sb-range-0to10"/> 
					<input id="sb-nps-question-txt" name="sb-question-txt-999" class="bd-q-pu-txt-nps" value="<spring:message code="label.default.nps.question.key" />">
					<div class="bd-q-pu-close hide"></div>
				</div>
			</div>
			<div class="bd-and-tier2 col-xs-12 col-sm-12 col-md-12 col-lg-12"><spring:message code="label.customer.customize.descriptions.key" /></div>
			<div class="clearfix bd-ans-type bd-ans-type-radio-adj">
				<div id="sq-nps-radio-1to10" class="sq-1to10-range" style="height: 160px" selected-rating-radio="">
					<div class="edit-survey-radio-container">
						<div class="container-1to10 inner-container-1to10">
							<c:forEach begin="0" end="10" varStatus="loop">
								<c:choose>
									<c:when test="${loop.index == 0}">
										<div class="radio-div" style="margin-left: 10px">
											<div class="radio-outer-orange sq-radio"></div>
										</div>
									</c:when>
									<c:when test="${loop.index >0 && loop.index <=6 }">
										<div class="radio-div">
											<div class="radio-outer-orange sq-radio"></div>
										</div>
									</c:when>
									<c:when test="${loop.index >=7 && loop.index <=8 }">
										<div class="radio-div">
											<div class="radio-outer-yellow sq-radio"></div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="radio-div">
											<div class="radio-outer-green sq-radio"></div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</div>
						<div class="container-1to10">
							<c:forEach begin="0" end="10" varStatus="numLoop">
								<c:choose>
									<c:when test="${numLoop.index >=0 && numLoop.index<=6}">
										<div class="span-1to10 span-1to10-orange" style="text-align: right">${numLoop.index}</div>
									</c:when>
									<c:when test="${numLoop.index >=7 && numLoop.index<=8}">
										<div class="span-1to10 span-1to10-yellow" style="text-align: right">${numLoop.index}</div>
									</c:when>
									<c:otherwise>
										<div class="span-1to10 span-1to10-green" style="text-align: right">${numLoop.index}</div>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</div>
						<div class="sq-1to10-range-val">
							<input id="sq-not-very-likely-nps" name="notVeryLikely" class="float-left sq-range-1to10-input sq-not-very-likely" value="<spring:message code="label.default.not.very.likely.key" />">
							<input id="sq-very-likely-nps" name="veryLikely" class="float-right sq-range-1to10-input text-align-right sq-very-likely" value="<spring:message code="label.default.very.likely.key" />">
						</div>
					</div>
				</div>
			</div>
			<div class="bd-nps-checkbox-wrapper bd-q-pu-done-wrapper-override clearfix">
				<div id="user-ranking-chkbox-wrapper-nps" class="clearfix" style="width: 200px;">
					<div id="user-ranking-chkbox-nps" class="float-left user-ranking-chkbox bd-check-img"></div>
					<input type="hidden" id="user-ranking-ques-nps" name="user-ranking-ques" value=true>
					<div class="float-left  listing-access-txt cursor-pointer">Consider for User Ranking</div>
				</div>
				<div id="avg-score-chkbox-wrapper-nps" class="clearfix" style="width: 210px;">
					<div id="avg-score-chkbox-nps" class="float-left avg-score bd-check-img"></div>
					<input type="hidden" id="avg-score-ques-nps" name="considerForScore" value=true>
					<div class="float-left  listing-access-txt cursor-pointer">Consider for Average Score</div>
				</div>
			</div>
			<div class="bd-nps-checkbox-wrapper clearfix">
				<div class="bd-nps-btn-done float-left"><spring:message code="label.done.key" /></div>
			</div>
		</div>
		</form>
	</div>
</div>
<script>
	$(document).ready(function() {
		hideOverlay();
		$(document).attr("title", "Build Survey");

		$('#bs-ques-wrapper').html('');
		loadActiveSurveyQuestions();

		resizeAdjBuildSurvey();
		$(window).resize(resizeAdjBuildSurvey);
	});
</script>