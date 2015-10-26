<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${not empty complaintRegSettings }">
	<c:set value="${complaintRegSettings}" var="complaintRegSettings"></c:set>
</c:if>
<div class="hm-header-main-wrapper">
	<div class="container">
		<div class="hm-header-row clearfix">
			<div class="float-left hm-header-row-left">
				<spring:message code="label.title.complaintregsettings.key" />
			</div>
		</div>
	</div>
</div>

<div id="toast-container" class="toast-container">
	<span id="overlay-toast" class="overlay-toast"></span>
</div>


<div class="prof-main-content-wrapper margin-top-25 margin-bottom-25">
	<div class="container">
		<div class="complaint-cont">
			<form id="comp-reg-form" method="post">
				<!-- Mail Id Input -->
				<div class="bd-hr-form-item clearfix">
					<div class="float-left compl-input-text"><spring:message code="label.complaintreg.mail.text" /></div>
					<div class="float-left bd-frm-right"><input class="bd-frm-rt-txt compl-input" name="mailId"
						id="comp-mailId" value="${complaintRegSettings.mailId}" placeholder='<spring:message code="label.addmultipleemailids.key"/>'></div>
				</div>

				<div class="clearfix compl-type-sel-cont">
					<div class="float-left compl-option-sel">
						<input id="comp-enabled" type="checkbox" name="enabled" class="hide">
						<div class="float-left">
							<div id="compl-checkbox" class="bd-check-img float-right compl-checkbox bd-check-img-checked"></div>
						</div>
						<div class="float-left compl-box-txt">
							<spring:message code="label.complaintreg.capture.text" />
						</div>
					</div>
					<div class="float-left compl-option-sel">
						<input type="text" name="rating" id="comp-rating-post"
							class="st-item-row-txt cursor-pointer dd-arrow-dn comp-rating-post"
							autocomplete="off" value="${complaintRegSettings.rating}">
						<div class="st-dd-wrapper hide" id="st-dd-wrapper-min-post"></div>
					</div>
					<div class="float-left compl-option-sel">
						<div class="mood-text">
							<spring:message	code="label.complaintreg.or.text" />
						</div>
					</div>
					<div class="float-left compl-option-sel">
						<div id="comp-mood-unpleasant" class="sq-smile-icn-container compl-input-cont opacity-red" data-mood="unpleasant">
							<div class="sq-smile-icn-text sq-smile-sad-text compl-sml-txt">
								<spring:message code="label.complaint.sad.text" />
							</div>
						</div>
					</div>
					<div class="float-left compl-option-sel">
						<div class="mood-text">
							<spring:message	code="label.complaintreg.or.text" />
						</div>
					</div>
					<div class="float-left compl-option-sel">
						<div id="comp-mood-ok" class="sq-smile-icn-container compl-input-cont opacity-red" data-mood="ok">
							<div class="sq-smile-icn-text sq-smile-neutral-text compl-sml-txt">
								<spring:message code="label.complaint.ok.text" />
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="comp-mood" name="mood"/> 
				<div id="comp-reg-form-submit" class="bd-btn-save cursor-pointer compl-save-btn">Save</div>
			</form>
		</div>
	</div>
</div>

<script>
	$(document).ready(function() {
		$(document).attr("title", "Complaint Registration Settings");
		
		if (${complaintRegSettings.enabled}) {
			$('#compl-checkbox').removeClass('bd-check-img-checked');
			$('input[name="enabled"]').prop( "checked" , true);
			$('input[name="enabled"]').val("enable");
		}
		
		if(${not empty complaintRegSettings.mood}) {
			$('#comp-mood').val("${complaintRegSettings.mood}".toLowerCase());
			if ("${complaintRegSettings.mood}".toLowerCase() == "ok") {
				$('#comp-mood-ok').removeClass('opacity-red');
				$('#comp-mood-unpleasant').removeClass('opacity-red');
			}
			if ("${complaintRegSettings.mood}".toLowerCase() == "unpleasant") {
				$('#comp-mood-unpleasant').removeClass('opacity-red');
			}
		}
				
		autoAppendRatingDropdownComplaint('#st-dd-wrapper-min-post',
				"st-dd-item st-dd-item-min-post", 5, 0, 0.5);
		$('#comp-rating-post').off('click');
		$('#comp-rating-post').on('click', function() {
			if(!$('input[name="enabled"]').prop( "checked" ))
				return;
			$('#st-dd-wrapper-min-post').slideToggle(200);
		});
		$('.sq-smile-icn-container').off('click');
		$('.sq-smile-icn-container').on('click', function() {
			if(!$('input[name="enabled"]').prop( "checked" ))
				return;
			var mood = $(this).attr("data-mood");
			var currentMood = $('#comp-mood').val();
			
			//check for toggle state
			
			//set the mood
			if($(this).hasClass('opacity-red')) {
				$('#comp-mood').val(mood);
				if(mood.toLowerCase() == "ok") {
					$('.sq-smile-icn-container').removeClass('opacity-red');
				} else if (mood.toLowerCase() == "unpleasant") {
					$(this).removeClass('opacity-red');
				}
			} else {
				$('#comp-mood').val('');
				
				if(mood.toLowerCase() == "ok") {
					$('.sq-smile-icn-container').addClass('opacity-red');
				} else if (mood.toLowerCase() == "unpleasant") {
					if(currentMood == "ok") {
						$('.sq-smile-icn-container[data-mood="ok"]').addClass('opacity-red');
						$('#comp-mood').val(mood);
					} else if(currentMood == "unpleasant"){
						$(this).addClass('opacity-red');
					}
				}
			}
		});
		
	});
</script>