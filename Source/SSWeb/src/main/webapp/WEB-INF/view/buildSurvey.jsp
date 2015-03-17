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
		<div class="bd-q-wrapper">
			
			<div class="bd-quest-item">
				<div class="bd-q-pu-header bd-q-pu-header-adj clearfix">
					<div class="float-left bd-q-pu-header-lft"><spring:message code="label.create.surveyquestions.key" /></div>
					<div class="float-right bd-q-pu-header-rt cursor-pointer"><spring:message code="label.needhelp.key" /></div>
				</div>
				<div class="bd-q-pu-txt-wrapper pos-relative">
					<input class="bd-q-pu-txt" data-nextquest="false" data-qno="1">
					<div class="bd-q-pu-close hide"></div>
				</div>
				<div class="bs-ans-wrapper hide">
					<div class="bd-and-header-txt"><spring:message code="label.customer.reply.key" /></div>
					<div class="bd-ans-options-wrapper">
						<div class="bd-ans-header clearfix">
							<div class="bd-ans-hd-container clearfix float-left">
								<div id="" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel"><spring:message code="label.rating.key" /></div>
								<div id="" class="bd-tab-com float-left bd-ans-tab-item"><spring:message code="label.comment.key" /></div>
								<div id="" class="bd-tab-mcq float-left bd-ans-tab-item"><spring:message code="label.multiplechoice.key" /></div>
							</div>
						</div>
						<div id="" class="bd-ans-type-rating bd-ans-type-item">
							<div class="bd-and-tier2"><spring:message code="label.customer.answer.key" /></div>
							<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
									<div class="bd-ans-img-wrapper">
										<div class="bd-ans-img bd-ans-smiley"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.smiles.key" /></div>
									</div>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
									<div class="bd-ans-img-wrapper">
										<div class="bd-ans-img bd-ans-star"></div>
										<div class="bd-ans-img-txt"><spring:message code="label.star.key" /></div>
									</div>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
									<div class="bd-ans-img-wrapper">
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
									<input class="float-left bd-mcq-txt">
									<div class="float-left bd-mcq-close"></div>
								</div>
								<div class="bd-mcq-row clearfix">
									<div class="float-left bd-mcq-lbl"><spring:message code="label.option.key" /></div>
									<input class="float-left bd-mcq-txt">
									<div class="float-left bd-mcq-close"></div>
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
				</div>
			</div>
			
			<!--<div class="bd-quest-item hide">
				<div class="bd-q-pu-header clearfix">
					<div class="float-left bd-q-pu-header-lft">I Would Like To Add Another Question</div>
				</div>
				<div class="bd-q-pu-txt-wrapper pos-relative">
					<input class="bd-q-pu-txt" data-qno="2">
					<div class="bd-q-pu-close hide"></div>
				</div>
				<div class="bs-ans-wrapper hide">
				</div>
			</div>-->
			
			<div class="bd-q-pu-done-wrapper clearfix">
				<div class="bd-q-btn-cancel float-left"><spring:message code="label.cancel.key" /></div>
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
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
			<div class="bd-srv-tbl-row clearfix">
				<div class="float-left srv-tbl-num"><span>1</span></div>
				<div class="float-left srv-tbl-chk srv-tbl-chk-on srv-tbl-chk-off"></div>
				<div class="float-left srv-tbl-txt">Lorem ipsum sample question Lorem ipsum sample question</div>
				<div class="float-right srv-tbl-rem">Remove</div>
				<div class="float-right srv-tbl-edit">Edit</div>
			</div>
		</div>
	</div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/buildSurvey.js"></script>
<script>
$(document).ready(function() {
	$(document).attr("title", "Build Survey");
	
	loadActiveSurveyQuestions();
	
	resizeAdj();
	$(window).resize(resizeAdj);
	function resizeAdj(){
		var winW = $(window).width();
		if (winW < 768) {
			var txtW = winW - 118;
			$('.srv-tbl-txt').width(txtW);
		}
		else {}
	}
});
</script>