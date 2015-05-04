<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
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
						<input type="hidden" id="sb-question-type-1" name="sb-question-type-1" data-state="new"/>
						<input id="sb-question-txt-1" name="sb-question-txt-1" class="bd-q-pu-txt" data-nextquest="false" data-qno="1">
						<div class="bd-q-pu-close hide"></div>
					</div>
					<div class="bs-ans-wrapper hide">
						<div class="bd-and-header-txt"><spring:message code="label.customer.reply.key" /></div>
						<div class="bd-ans-options-wrapper">
							<div class="bd-ans-header clearfix">
								<div class="bd-ans-hd-container clearfix float-left">
									<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.rating.key" /></div>
									<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
									<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
								</div>
							</div>
							<div id="" class="bd-ans-type-rating bd-ans-type-item">
								<div class="bd-and-tier2"><spring:message code="label.customer.answer.key" /></div>
								<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
										<div data-id="sb-range-smiles" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-smiley"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.smiles.key" /></div>
										</div>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
										<div data-id="sb-range-star" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-star"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.star.key" /></div>
										</div>
									</div>
									<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
										<div data-id="sb-range-scale" class="bd-ans-img-wrapper">
											<div class="bd-ans-img bd-ans-scale"></div>
											<div class="bd-ans-img-txt"><spring:message code="label.scale.key" /></div>
										</div>
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
								</div>
							</div>
							<div id="" class="bd-ans-type-com bd-ans-type-item hide">
								<div class="clearfix bd-com-wrapper">
									<div class="float-left bd-com-chk"></div>
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
		<div id="btn-add-question" class="float-right bd-svry-right"><spring:message code="label.addnewquestion.key" /></div>
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
	</div>
</div>
<script src="${pageContext.request.contextPath}/resources/js/buildSurvey.js"></script>
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