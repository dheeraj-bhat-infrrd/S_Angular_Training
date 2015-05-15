<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="UTF-8"%>
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
						<%-- <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">
							<div data-id="sb-range-scale" class="bd-ans-img-wrapper">
								<div class="bd-ans-img bd-ans-scale"></div>
								<div class="bd-ans-img-txt"><spring:message code="label.scale.key" /></div>
							</div>
						</div> --%>
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