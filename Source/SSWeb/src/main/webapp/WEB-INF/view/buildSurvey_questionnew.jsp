<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="bd-quest-item hide">
	<form id="bs-question-${order}" data-quesnum="${order}" data-state="new" data-status="new" data-quesref="">
		<div class="bd-q-pu-header clearfix">
			<div class="float-left bd-q-pu-header-lft"><spring:message code="label.addanotherquestion.key" /></div>
		</div>
		<div class="bd-q-pu-txt-wrapper pos-relative">
			<input type="hidden" id="sb-question-type-${order}" name="sb-question-type-${order}" data-state="new"/>
			<input id="sb-question-txt-${order}" name="sb-question-txt-${order}" class="bd-q-pu-txt" data-nextquest="false" data-qno="${order}">
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
						<div id="user-ranking-chkbox-wrapper-new" class="clearfix" style="width: 200px;">
							<div id="user-ranking-chkbox-new" class="float-left user-ranking-chkbox bd-check-img"></div>
							<input type="hidden" id="user-ranking-ques-new" name="user-ranking-ques" value=true>
							<div class="float-left  listing-access-txt cursor-pointer">Consider for User Ranking</div>
						</div>
  					</div>
					
				</div>
				
				<div class="bd-ans-type-radio bd-ans-type-item hide">
					<div class="bd-and-tier2"><spring:message code="label.customer.customize.descriptions.key" /></div>
					<div class="clearfix bd-ans-type bd-ans-type-radio-adj">
						<div id="sq-radio-1to10-new" class="sq-1to10-range" style="height:160px" selected-rating-radio="">
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
									<input id="sq-not-very-likely-new" name="notVeryLikely" class="float-left sq-range-1to10-input sq-not-very-likely" value="Not at all likely">
									<input id="sq-very-likely-new" name="veryLikely" class="float-right sq-range-1to10-input text-align-right sq-very-likely" value="Very Likely">
								</div>
							</div>
						</div>
					</div>
					<div class="bd-q-pu-done-wrapper bd-q-pu-done-wrapper-override clearfix">
						<div id="user-ranking-chkbox-wrapper-new-nps" class="clearfix" style="width: 200px;">
							<div id="user-ranking-chkbox-new-nps" class="float-left user-ranking-chkbox bd-check-img"></div>
							<div class="float-left  listing-access-txt cursor-pointer">Consider for User Ranking</div>
						</div>
						<div id="avg-score-chkbox-wrapper-new" class="clearfix" style="width: 210px;">
							<div id="avg-score-chkbox-new" class="float-left avg-score bd-check-img"></div>
							<input type="hidden" id="avg-score-ques-new" name="considerForScore" value=true>
							<div class="float-left  listing-access-txt cursor-pointer">Consider for Average Score</div>
						</div>
						<div id="nps-chkbox-wrapper-new" class="clearfix">
							<input type="hidden" id="nps-ques-new" name="nps-ques" value=false>
						</div>
  					</div>
				</div>
				
				<div id="" class="bd-ans-type-mcq bd-ans-type-item hide">
					<div class="bd-and-tier2"><spring:message code="label.customer.answerfrom.key" /></div>
					<div class="clearfix bd-ans-type bd-ans-type-mcq-adj">
						<div class="bd-mcq-row clearfix">
							<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
							<input name="sb-answers-${order}[]" class="float-left bd-mcq-txt">
							<div class="float-left bd-mcq-close hide"></div>
						</div>
						<div class="bd-mcq-row clearfix">
							<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
							<input name="sb-answers-${order}[]" class="float-left bd-mcq-txt">
							<div class="float-left bd-mcq-close hide"></div>
						</div>
						<div class="bd-mcq-row clearfix">
							<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
							<input name="sb-answers-${order}[]" class="float-left bd-mcq-txt">
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
				<span class="bd-q-status-txt"></span>
			</div>
		</div>
	</form>
</div>