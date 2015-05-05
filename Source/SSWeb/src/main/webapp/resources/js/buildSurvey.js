// Populate Existing Survey Questions 
function commonActiveSurveyCallback(response){
	showInfo(response);
	loadActiveSurveyQuestions();
	$('.err-nw-wrapper').delay(2000).fadeOut();
}

function loadActiveSurveyQuestions() {
	var url = "./getactivesurveyquestions.do";
	callAjaxGET(url, function(data) {
		$('#bs-ques-wrapper').html(data);
		resizeAdjBuildSurvey();
	}, true);
}

function resizeAdjBuildSurvey(){
	var winW = $(window).width();
	if (winW < 768) {
		var txtW = winW - 118;
		$('.srv-tbl-txt').width(txtW);
	}
	else {}
}

// On Hover
$(document).on('click', '.bd-srv-tbl-row', function() {
	if ($(window).width() < 768) {
		if ($(this).find('.srv-tbl-rem').css('display') == 'none') {
			$(this).find('.srv-tbl-rem').show();
			$(this).find('.srv-tbl-edit').show();
            $(this).find('.srv-tbl-move-up').show();
            $(this).find('.srv-tbl-move-dn').show();
		} else {
			$(this).find('.srv-tbl-rem').hide();
			$(this).find('.srv-tbl-edit').hide();
            $(this).find('.srv-tbl-move-up').hide();
            $(this).find('.srv-tbl-move-dn').hide();
		}
	} else {
		// $(this).find('.srv-tbl-rem').hide();
		// $(this).find('.srv-tbl-edit').hide();
	}
});

$(document).on('mouseover', '.bd-srv-tbl-row', function() {
	if ($(window).width() > 768) {
		$(this).addClass('bd-srv-tbl-row-hover');
		$(this).find('.srv-tbl-rem').show();
		$(this).find('.srv-tbl-edit').show();
        $(this).find('.srv-tbl-move-up').show();
        $(this).find('.srv-tbl-move-dn').show();
	}
});

$(document).on('mouseout', '.bd-srv-tbl-row', function() {
	if ($(window).width() > 768) {
		$(this).removeClass('bd-srv-tbl-row-hover');
		$(this).find('.srv-tbl-rem').hide();
		$(this).find('.srv-tbl-edit').hide();
        $(this).find('.srv-tbl-move-up').hide();
        $(this).find('.srv-tbl-move-dn').hide();
	}
});

// Add Survey Question overlay
$(document).on('click', '#btn-add-question', function() {
	$('#bd-srv-pu').show();
	$(document).addClass('body-no-scroll');
});

function revertQuestionOverlay() {
	var url = "./revertquestionoverlay.do";
	callAjaxGET(url, function(data) {
		$('#bd-quest-wrapper').html(data);
	}, true);
	
	$('#bd-srv-pu').hide();
	$(document).removeClass('body-no-scroll');
	currentQues = 1;
}

$(document).on('click', '.bd-q-btn-done', function() {
	var lastQuestion = currentQues - 1;
	var count = 1;
	var editedStatus = true;
	while (count <= currentQues) {
		if ($('#bs-question-' + count).attr('data-status') == 'edited') {
			editedStatus = true;
			break;
		}
		else {
			editedStatus = false;
		}
		count++;
	}
	if (editedStatus == false) {
		revertQuestionOverlay();
		setTimeout(function() {
			loadActiveSurveyQuestions();
		}, 2000);
		return;
	}
	
	createPopupConfirm("Unsaved changes detected", "Do you want to save your changes ?", "Save", "Cancel");

	$('#overlay-continue').click(function(){
		var count = 1;
		while (count <= lastQuestion) {
			// submit for adding question
			if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'new'
				&& $('#bs-question-' + count).attr('data-status') == 'edited') {
				
				if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
					$("#overlay-toast").html('Please finish adding the Question');
					showToast();
				} else {
					var url = "./addquestiontosurvey.do?order=" + count;
					$('#bs-question-' + count).attr('data-state', 'editable');
					$('#bs-question-' + count).attr('data-status', 'new');
					callAjaxFormSubmit(url, function(data) {
						var map =  $.parseJSON(data);
						$("#overlay-toast").html(map.message);
						showToast();
						
						if (map.status == "success") {
							$('#bs-question-' + count).attr('data-quesref', map.questionId);
							revertQuestionOverlay();
						} else {
							$('#bs-question-' + count).attr('data-state', 'new');
							$('#bs-question-' + count).attr('data-status', 'edited');
						}
					}, 'bs-question-' + count);
				}
			}
			// submit for modifying question
			else if (count > 0 && $('#bs-question-' + count).attr('data-state') == 'editable'
				&& $('#bs-question-' + count).attr('data-status') == 'edited') {
				
				if ($('#sb-question-txt-' + count).val() == '' || $('#sb-question-type-' + count).val() == '') {
					$("#overlay-toast").html('Please finish editing the Question');
					showToast();
				} else {
					var questionId = $('#bs-question-' + count).attr('data-quesref');
					var url = "./updatequestionfromsurvey.do?order=" + count + "&questionId=" + questionId;
					callAjaxFormSubmit(url, function(data) {
						var map =  $.parseJSON(data);
						$("#overlay-toast").html(map.message);
						showToast();
						
						if (map.status == "success") {
							revertQuestionOverlay();
							$('#bs-question-' + count).attr('data-status', 'new');
						} else {
							$('#bs-question-' + count).attr('data-status', 'edited');
						}
					}, 'bs-question-' + count);
				}
			}
			count ++;
		}
		
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		setTimeout(function() {
			loadActiveSurveyQuestions();
		}, 2000);
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		
		revertQuestionOverlay();
		loadActiveSurveyQuestions();
	});
});

$(document).on('click', '.bd-q-pu-close', function() {
	$(this).parent().parent().remove();
});

// Question edit
$(document).on('click', '.srv-tbl-edit', function() {
	var questionId = $(this).parent().parent().data('questionid');
	var url = "./getsurveyquestion.do?questionId=" + questionId;

	callAjaxGET(url, function(response) {
		$('.sb-edit-q-wrapper').remove();
		$('.bd-q-pu-done-wrapper').remove();
		$('.bd-srv-tbl-row-' + questionId).after(response);
		revertQuestionOverlay();
	}, true);
});

$(document).on('input', '.bd-q-pu-txt-edit', function() {
	var quesNum = $(this).closest('form').data('quesnum');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
	showStatus('#bs-question-' + quesNum, 'Edited');
});

$(document).on('click', '.bd-q-btn-done-edit', function() {
	var questionId = $(this).data('quesnum');

	if ($('#sb-question-txt-' + questionId).val() == '' || $('#sb-question-type-' + questionId).val() == '') {
		$("#overlay-toast").html('Please finish editing the Question');
		showToast();
	} else {
		var url = "./updatequestionfromsurvey.do?order=" + questionId + "&questionId=" + questionId;
		showProgress('#bs-question-' + questionId);
		callAjaxFormSubmit(url, function(data) {
			var map =  $.parseJSON(data);
			showInfo(map.message);
			
			if (map.status == "success") {
				$('.bd-srv-tbl-row-' + questionId).next().remove();
				loadActiveSurveyQuestions();
			} else {
				showStatus('#bs-question-' + questionId, 'Retry Saving');
			}
		}, 'bs-question-' + questionId);
	}
});

// Select question type
$(document).on('click', '.bd-tab-rat', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-rating').show();
});

$(document).on('click', '.bd-tab-mcq', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-mcq').show();
	
	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('#sb-question-type-' + quesNum).val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-tab-com', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-com').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('#sb-question-type-' + quesNum).val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-ans-img-wrapper', function() {
	$(this).parent().parent().find('.bd-ans-img').addClass('bd-img-sel');
	$(this).find('.bd-ans-img').removeClass('bd-img-sel');

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('#sb-question-type-' + quesNum).val($(this).data('id'));
	showStatus('#bs-question-' + quesNum, 'Edited');
	$('#bs-question-' + quesNum).attr('data-status', 'edited');
});

$(document).on('click', '.bd-com-chk', function() {
	if ($(this).hasClass('bd-com-unchk')) {
		$(this).removeClass('bd-com-unchk');
	} else {
		$(this).addClass('bd-com-unchk');
	}
});

// Submit previous question
var currentQues = 1;
$(document).on("focus", '.bd-q-pu-txt', function() {
	var quesOrder = $(this).closest('form').data('quesnum') - 1;
	
	// submit for adding new question
	if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'new'
			&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish adding the Question');
			showToast('error');
		} else {
			var url = "./addquestiontosurvey.do?order=" + quesOrder;
			showProgress('#bs-question-' + quesOrder);
			$('#bs-question-' + quesOrder).attr('data-state', 'editable');
			$('#bs-question-' + quesOrder).attr('data-status', 'new');
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();
				
				if (map.status == "success") {
					$('#bs-question-' + quesOrder).attr('data-quesref', map.questionId);
					showStatus('#bs-question-' + quesOrder, 'Saved');
				} else {
					$('#bs-question-' + quesOrder).attr('data-state', 'new');
					$('#bs-question-' + quesOrder).attr('data-status', 'edited');
					showStatus('#bs-question-' + quesOrder, 'Retry Saving');
				}
			}, 'bs-question-' + quesOrder);
		}
	}
	// submit for modifying question
	else if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'editable'
		&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '' || $('#sb-question-type-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish editing the Question');
			showToast();
		} else {
			var questionId = $('#bs-question-' + quesOrder).attr('data-quesref');
			var url = "./updatequestionfromsurvey.do?order=" + quesOrder + "&questionId=" + questionId;
			showProgress('#bs-question-' + quesOrder);
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();
				
				if (map.status == "success") {
					showStatus('#bs-question-' + quesOrder, 'Saved');
					$('#bs-question-' + quesOrder).attr('data-status', 'new');
				} else {
					showStatus('#bs-question-' + quesOrder, 'Retry Saving');
					$('#bs-question-' + quesOrder).attr('data-status', 'edited');
				}
			}, 'bs-question-' + quesOrder);
		}
	}
});

$(document).on("input", '.bd-q-pu-txt', function() {
	var quesPresent = $(this).closest('form').data('quesnum');
	
	// Setting status
	showStatus('#bs-question-' + quesPresent, 'Edited');
	$('#bs-question-' + quesPresent).attr('data-status', 'edited');
	
	// populating next question
	if ($(this).val().trim().length > 0) {
		$(this).parent().next('.bs-ans-wrapper').show();

		if ($(this).data('nextquest') == false) {
			currentQues ++;
			
			var url = "./populatenewform.do?order=" + currentQues;
			$('#sb-question-txt-' + quesPresent).data('nextquest', 'true');
			callAjaxGET(url, function(data) {
				$('#bs-question-' + quesPresent).after(data);
				$('#bs-question-' + quesPresent).next('.bd-quest-item').show();
			}, true);
		}
	}
	
	/*if ($(this).data('qno') != '1') {
		$(this).next('.bd-q-pu-close').show();
	}*/
});

$(document).on('input', '.bd-mcq-txt', function(){
	// changing status to edited
	var name = $(this).attr('name');
	var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));
	
	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

$(document).on('blur', '.bd-mcq-txt', function(){
	if ($(this).parent().is(':last-child')) {
		var name = $(this).attr('name');
		var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

		// changing status to edited
		showStatus('#bs-question-' + addMcqTextOption, 'Edited');
		$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
		
		var htmlData = '<div class="bd-mcq-row clearfix">'
				+ '<div class="float-left bd-mcq-lbl">Option</div>'
				+ '<input name="sb-answers-' + addMcqTextOption + '[]" class="float-left bd-mcq-txt">'
				+ '<div class="float-left bd-mcq-close"></div>'
			+ '</div>';
		$(this).parent().after(htmlData);
		
		// enable remove button
		if ($(this).parent().parent().children().length > 2) {
			$('.bd-mcq-close').removeClass('hide');
		}
	}
});

$(document).on('click', '.bd-mcq-close', function(){
	var parentDiv = $(this).parent().parent();
	$(this).parent().remove();

	// disable remove button
	if (parentDiv.children().length <= 2) {
		$('.bd-mcq-close').addClass('hide');
	}
	
	// changing status to edited
	var name = $(this).attr('name');
	var addMcqTextOption = name.substring(name.lastIndexOf("-") + 1, name.lastIndexOf("["));

	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

// Remove Question from survey
$(document).on('click', '.srv-tbl-rem', function(e){
	e.stopPropagation();
	var questionId = $(this).parent().parent().data('questionid');
	var url = "./removequestionfromsurvey.do?questionId=" + questionId;
	
	createPopupConfirm("Delete Question", "Do you want to delete the question ?", "Delete", "Cancel");
	$('#overlay-continue').click(function(){
		overlayRevert();
		$('#overlay-continue').unbind('click');

		callAjaxPOST(url, commonActiveSurveyCallback, true);
	});
	$('#overlay-cancel').click(function(){
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
		
		loadActiveSurveyQuestions();
	});
});

// Reorder Question in survey
$(document).on('click', '.srv-tbl-move-up', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "up");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

$(document).on('click', '.srv-tbl-move-dn', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "down");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

// Overlay Popup
function createPopupConfirm(header, text, ok, cancel) {
	$('#overlay-header').html(header);
	$("#overlay-text").html(text);
	$('#overlay-continue').html(ok);
	$('#overlay-cancel').html(cancel);

	$('#overlay-main').show();
}
function overlayRevert() {
	$('#overlay-main').hide();
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').html('');
	$('#overlay-cancel').html('');
}

// Progress Bar
function hideProgress(formId) {
	$(formId).find('.bd-q-status-wrapper').hide();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html('');
}
function showProgress(formId) {
	$(formId).find('.bd-q-status-wrapper').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-spinner').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html('Saving');
}
function showStatus(formId, text) {
	$(formId).find('.bd-q-status-wrapper').show();
	$(formId).find('.bd-q-status-wrapper').find('.bd-spinner').hide();
	$(formId).find('.bd-q-status-wrapper').find('.bd-q-status-txt').html(text);
}