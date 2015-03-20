// Populate Existing Survey Questions 
var bdQuestItemRevert = '<form id="bs-question-1" data-quesnum="1" data-quesref="" data-state="new" data-status="new"><div class="bd-q-pu-header bd-q-pu-header-adj clearfix"><div class="float-left bd-q-pu-header-lft">Create Your Survey Questions Here</div><div class="float-right bd-q-pu-header-rt cursor-pointer">Need Help?</div></div><div class="bd-q-pu-txt-wrapper pos-relative"><input type="hidden" id="sb-question-type-1" name="sb-question-type-1" data-state="new"/><input id="sb-question-txt-1" name="sb-question-txt-1" class="bd-q-pu-txt" data-nextquest="false" data-qno="1"><div class="bd-q-pu-close hide"></div></div><div class="bs-ans-wrapper hide"><div class="bd-and-header-txt">I want my customer reply using</div><div class="bd-ans-options-wrapper"><div class="bd-ans-header clearfix"><div class="bd-ans-hd-container clearfix float-left"><div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Range</div><div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item">Comment</div><div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item">Multiple Choice</div></div></div><div id="" class="bd-ans-type-rating bd-ans-type-item"><div class="bd-and-tier2">My Customers can answer using</div><div class="row clearfix bd-ans-type bd-ans-type-rating-adj"><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div data-id="sb-range-smiles" class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-smiley"></div><div class="bd-ans-img-txt">Smiley</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div data-id="sb-range-star" class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-star"></div><div class="bd-ans-img-txt">Stars</div></div></div><div class="col-lg-4 col-md-4 col-sm-4 col-xs-12"><div data-id="sb-range-scale" class="bd-ans-img-wrapper"><div class="bd-ans-img bd-ans-scale"></div><div class="bd-ans-img-txt">Scale</div></div></div></div></div><div id="" class="bd-ans-type-mcq bd-ans-type-item hide"><div class="bd-and-tier2">My Customers can answer from</div><div class="clearfix bd-ans-type bd-ans-type-mcq-adj"><div class="bd-mcq-row clearfix"><div class="float-left bd-mcq-lbl">Option</div><input name="sb-answers-1[]" class="float-left bd-mcq-txt"><div class="float-left bd-mcq-close hide"></div></div><div class="bd-mcq-row clearfix"><div class="float-left bd-mcq-lbl">Option</div><input name="sb-answers-1[]" class="float-left bd-mcq-txt"><div class="float-left bd-mcq-close hide"></div></div></div></div><div id="" class="bd-ans-type-com bd-ans-type-item hide"><div class="clearfix bd-com-wrapper"><div class="float-left bd-com-chk"></div><div class="float-left bd-com-txt">Textarea</div></div></div></div><div class="bd-q-status-wrapper text-center hide"><span class="bd-spinner">`</span><span class="bd-q-status-txt">Saving</span></div></div></form>';
function commonActiveSurveyCallback(response){
	showInfo(response);
	loadActiveSurveyQuestions();
	$('.err-nw-wrapper').delay(2000).fadeOut();
}

function loadActiveSurveyQuestions() {
	var url = "./getactivesurveyquestions.do";
	callAjaxGET(url, populateActiveSurveyQuestions, true);
}

function populateActiveSurveyQuestions(response) {
	if (response == "") {
		return;
	}
	
	var surveyDetail = $.parseJSON(response);
	var surveyQuestions = surveyDetail.questions;
	var htmlData = "";

	// setting status of survey
	if (surveyDetail.status != "") {
		setTimeout(function() {
			showError(surveyDetail.status);
		}, 3000);
	} else {
		hideError();
	}
	
	if (surveyQuestions != null) {
		var lengthQuestions = surveyQuestions.length;
		var countQues = 1;
		
		// For Each Question
		$.each(surveyQuestions, function(i, surveyQuestion) {
			// Question start
			htmlData = htmlData + '<div class="bd-srv-tbl-row clearfix bd-srv-tbl-row-' + surveyQuestion.questionId + '" data-questionid="' + surveyQuestion.questionId + '">';

			// Question order
			htmlData = htmlData + '<div class="float-left srv-tbl-num"><span>' + surveyQuestion.questionOrder + '</span></div>';
			
			// Question Text
			var questionTypeCode = surveyQuestion.questionType.trim();
			htmlData = htmlData + '<div class="float-left srv-tbl-txt" q-type="' + questionTypeCode + '">' + surveyQuestion.question + '</div>';

			// Buttons
			htmlData = htmlData + '<div class="srv-tbl-btns clearfix float-right">';
			if (countQues == 1) {
				htmlData = htmlData + '<div class="float-left srv-tbl-move-dn"></div>';
			}
			else if (countQues == lengthQuestions) {
				htmlData = htmlData + '<div class="float-left srv-tbl-move-up"></div>';
			}
			else {
				htmlData = htmlData
					+ '<div class="float-left srv-tbl-move-dn"></div>'
					+ '<div class="float-left srv-tbl-move-up"></div>';
			}
			htmlData = htmlData
				+ '<div class="float-right srv-tbl-rem">Remove</div>'
				+ '<div class="float-right srv-tbl-edit">Edit</div>';
			htmlData = htmlData + '</div>';
			
			// Question End
			htmlData = htmlData + '</div>';
			
			countQues++;
		});
		
		$('#bs-ques-wrapper').html(htmlData);
	} else {
		$('#bs-ques-wrapper').html('');
	}
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
$('#btn-add-question').click(function() {
	$('#bd-srv-pu').show();
	$(document).addClass('body-no-scroll');
});

function revertQuestionOverlay() {
	$('#bd-quest-item').html(bdQuestItemRevert);
	$('#bd-srv-pu').hide();
	$(document).removeClass('body-no-scroll');
	currentQues = 1;
}

$('.bd-q-btn-done').click(function(e) {
	e.stopPropagation();
	createPopupConfirm("Unsaved changes detected", "Do you want to save your changes ?", "Save", "Cancel");

	$('#overlay-continue').click(function(){
		var lastQuestion = currentQues - 1;
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
		
		loadActiveSurveyQuestions();
		$('#overlay-continue').unbind('click');
		$('#overlay-cancel').unbind('click');
		overlayRevert();
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
		var surveyQuestion = $.parseJSON(response);
		var questionId = surveyQuestion.questionId;
		var questionTypeCode = surveyQuestion.questionType.trim();
		var editQuestion = '<div class="sb-edit-q-wrapper">'
			+ '<form id="bs-question-' + questionId + '" data-quesnum="' + questionId + '">'
			+ '<div class="bd-q-pu-header clearfix">'
				+ '<div class="float-left bd-q-pu-header-lft">Edit Your Question Here</div>'
			+ '</div>'
			+ '<div class="bd-q-pu-txt-wrapper pos-relative">'
				+ '<input type="hidden" id="sb-question-type-' + questionId + '" name="sb-question-type-' + questionId + '" value="' + questionTypeCode + '"/>'
				+ '<input id="sb-question-txt-' + questionId + '" name="sb-question-txt-' + questionId 
					+ '" class="bd-q-pu-txt-edit" data-nextquest="false" value="' + surveyQuestion.question + '">'
				+ '<div class="bd-q-pu-close hide"></div>'
			+ '</div>'
			+ '<div class="bs-ans-wrapper hide" style="display: block;">'
				+ '<div class="bd-and-header-txt">I want my customer replying using</div>'
				+ '<div class="bd-ans-options-wrapper">'
					+ '<div class="bd-ans-header clearfix">'
						+ '<div class="bd-ans-hd-container clearfix float-left">';
						if (questionTypeCode == "sb-range-smiles" || questionTypeCode == "sb-range-star" || questionTypeCode == "sb-range-scale") {
							editQuestion = editQuestion
							+ '<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div>'
							+ '<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item">Comment</div>'
							+ '<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div>';
						}
						else if (questionTypeCode == "sb-sel-mcq" && surveyQuestion.answers.length > 0) {
							editQuestion = editQuestion
							+ '<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item">Rating</div>'
							+ '<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item">Comment</div>'
							+ '<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item bd-ans-tab-sel">Mutiple Choice</div>';
						}
						else if (questionTypeCode == "sb-sel-desc") {
							editQuestion = editQuestion
							+ '<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item">Rating</div>'
							+ '<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item bd-ans-tab-sel">Comment</div>'
							+ '<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div>';
						}
						editQuestion = editQuestion + '</div>'
					+ '</div>'
					+ '<div id="" class="bd-ans-type-rating bd-ans-type-item">'
						+ '<div class="bd-and-tier2">My Customers can answer using</div>'
						+ '<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">'
							+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
								+ '<div data-id="sb-range-smiles" class="bd-ans-img-wrapper">';
								if (questionTypeCode == "sb-range-smiles") {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-smiley bd-img-sel"></div>'
									+ '<div class="bd-ans-img-txt">Smiley</div>';
								} else {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-smiley"></div>'
									+ '<div class="bd-ans-img-txt">Smiley</div>';
								}
								editQuestion = editQuestion + '</div>'
							+ '</div>'
							+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
								+ '<div data-id="sb-range-star" class="bd-ans-img-wrapper">';
								if (questionTypeCode == "sb-range-star") {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-star bd-img-sel"></div>'
									+ '<div class="bd-ans-img-txt">Stars</div>';
								} else {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-star"></div>'
									+ '<div class="bd-ans-img-txt">Stars</div>';
								}
								editQuestion = editQuestion + '</div>'
							+ '</div>'
							+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
								+ '<div data-id="sb-range-scale" class="bd-ans-img-wrapper">';
								if (questionTypeCode == "sb-range-star") {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-scale bd-img-sel"></div>'
									+ '<div class="bd-ans-img-txt">Scale</div>';
								} else {
									editQuestion = editQuestion
									+ '<div class="bd-ans-img bd-ans-scale"></div>'
									+ '<div class="bd-ans-img-txt">Scale</div>';
								}
								editQuestion = editQuestion + '</div>'
							+ '</div>'
						+ '</div>'
					+ '</div>'
					+ '<div id="" class="bd-ans-type-mcq bd-ans-type-item hide">'
						+ '<div class="bd-and-tier2">My Customers can answer from</div>'
						+ '<div class="clearfix bd-ans-type bd-ans-type-mcq-adj">';
							if (questionTypeCode == "sb-sel-mcq" && surveyQuestion.answers.length > 0) {
								var countAns = 1;
								$.each(surveyQuestion.answers, function(i, answer) {
									editQuestion = editQuestion
									+ '<div class="bd-mcq-row clearfix">'
										+ '<div class="float-left bd-mcq-lbl">Option</div>'
										+ '<input name="sb-answers-' + questionId + '[]" class="float-left bd-mcq-txt" value="' + answer.answerText + '">'
										+ '<div class="float-left bd-mcq-close"></div>'
									+ '</div>';
									countAns ++;
								});
							} else {
								editQuestion = editQuestion
								+ '<div class="bd-mcq-row clearfix">'
									+ '<div class="float-left bd-mcq-lbl">Option</div>'
									+ '<input name="sb-answers-' + questionId + '[]" class="float-left bd-mcq-txt">'
									+ '<div class="float-left bd-mcq-close hide"></div>'
								+ '</div>'
								+ '<div class="bd-mcq-row clearfix">'
									+ '<div class="float-left bd-mcq-lbl">Option</div>'
									+ '<input name="sb-answers-' + questionId + '[]" class="float-left bd-mcq-txt">'
									+ '<div class="float-left bd-mcq-close hide"></div>'
								+ '</div>';
							}
						editQuestion = editQuestion + '</div>'
					+ '</div>'
					+ '<div id="" class="bd-ans-type-com bd-ans-type-item hide">'
						+ '<div class="clearfix bd-com-wrapper">'
							+ '<div class="float-left bd-com-chk"></div>'
							+ '<div class="float-left bd-com-txt">Textarea</div>'
						+ '</div>'
					+ '</div>'
				+ '</div>'
				+ '<div class="bd-q-status-wrapper text-center hide">'
					+ '<span class="bd-spinner">`</span>'
					+ '<span class="bd-q-status-txt">Saving</span>'
				+ '</div>'
			+ '</div>'
		+ '</form>'
		+ '</div>'
    	+ '<div class="bd-q-pu-done-wrapper clearfix">'
    		+ '<div data-quesnum="' + questionId + '" class="bd-q-btn-done-edit float-left">Done</div>'
    	+ '</div>'
    + '</div>';
						
	$('.bd-srv-tbl-row-' + questionId).after(editQuestion);
	}, true);
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
			$("#overlay-toast").html(map.message);
			showToast();
			
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
	
	// activating next question
	if ($(this).val().trim().length > 0) {
		$(this).parent().next('.bs-ans-wrapper').show();
		if ($(this).data('nextquest') == false) {
			currentQues ++;
			var newQuestTemplateWithTopTxt = '<div class="bd-quest-item hide">'
				+ '<form id="bs-question-' + currentQues + '" data-quesnum="' + currentQues + '" data-state="new" data-status="new" data-quesref="">'
				+ '<div class="bd-q-pu-header clearfix">'
					+ '<div class="float-left bd-q-pu-header-lft">I Would Like To Add Another Question</div>'
				+ '</div>'
				+ '<div class="bd-q-pu-txt-wrapper pos-relative">'
					+ '<input type="hidden" id="sb-question-type-' + currentQues + '" name="sb-question-type-' + currentQues + '" data-state="new"/>'
					+ '<input id="sb-question-txt-' + currentQues + '" name="sb-question-txt-' + currentQues + '" class="bd-q-pu-txt" data-nextquest="false" data-qno="' + currentQues + '">'
					+ '<div class="bd-q-pu-close hide"></div>'
				+ '</div>'
				+ '<div class="bs-ans-wrapper hide">'
					+ '<div class="bd-and-header-txt">I want my customer replying using</div>'
					+ '<div class="bd-ans-options-wrapper">'
						+ '<div class="bd-ans-header clearfix">'
							+ '<div class="bd-ans-hd-container clearfix float-left">'
								+ '<div data-id="sb-range" class="bd-tab-rat float-left bd-ans-tab-item bd-ans-tab-sel">Rating</div>'
								+ '<div data-id="sb-sel-desc" class="bd-tab-com float-left bd-ans-tab-item">Comment</div>'
								+ '<div data-id="sb-sel-mcq" class="bd-tab-mcq float-left bd-ans-tab-item">Mutiple Choice</div>'
							+ '</div>'
						+ '</div>'
						+ '<div id="" class="bd-ans-type-rating bd-ans-type-item">'
							+ '<div class="bd-and-tier2">My Customers can answer using</div>'
							+ '<div class="row clearfix bd-ans-type bd-ans-type-rating-adj">'
								+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
									+ '<div data-id="sb-range-smiles" class="bd-ans-img-wrapper">'
										+ '<div class="bd-ans-img bd-ans-smiley"></div>'
										+ '<div class="bd-ans-img-txt">Smiley</div>'
									+ '</div>'
								+ '</div>'
								+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
									+ '<div data-id="sb-range-star" class="bd-ans-img-wrapper">'
										+ '<div class="bd-ans-img bd-ans-star"></div>'
										+ '<div class="bd-ans-img-txt">Stars</div>'
									+ '</div>'
								+ '</div>'
								+ '<div class="col-lg-4 col-md-4 col-sm-4 col-xs-12">'
									+ '<div data-id="sb-range-scale" class="bd-ans-img-wrapper">'
										+ '<div class="bd-ans-img bd-ans-scale"></div>'
										+ '<div class="bd-ans-img-txt">Scale</div>'
									+ '</div>'
								+ '</div>'
							+ '</div>'
						+ '</div>'
						+ '<div id="" class="bd-ans-type-mcq bd-ans-type-item hide">'
							+ '<div class="bd-and-tier2">My Customers can answer from</div>'
							+ '<div class="clearfix bd-ans-type bd-ans-type-mcq-adj">'
								+ '<div class="bd-mcq-row clearfix">'
									+ '<div class="float-left bd-mcq-lbl">Option</div>'
									+ '<input name="sb-answers-' + currentQues + '[]" class="float-left bd-mcq-txt">'
									+ '<div class="float-left bd-mcq-close hide"></div>'
								+ '</div>'
								+ '<div class="bd-mcq-row clearfix">'
									+ '<div class="float-left bd-mcq-lbl">Option</div>'
									+ '<input name="sb-answers-' + currentQues + '[]" class="float-left bd-mcq-txt">'
									+ '<div class="float-left bd-mcq-close hide"></div>'
									+ '</div>'
								+ '</div>'
							+ '</div>'
							+ '<div id="" class="bd-ans-type-com bd-ans-type-item hide">'
								+ '<div class="clearfix bd-com-wrapper">'
									+ '<div class="float-left bd-com-chk"></div>'
									+ '<div class="float-left bd-com-txt">Textarea</div>'
								+ '</div>'
							+ '</div>'
						+ '</div>'
						+ '<div class="bd-q-status-wrapper text-center hide">'
							+ '<span class="bd-spinner">`</span>'
							+ '<span class="bd-q-status-txt">Saving</span>'
						+ '</div>'
					+ '</div>'
				+ '</form>'
				+ '</div>';
			$(this).parent().parent().after(newQuestTemplateWithTopTxt);
			$(this).parent().parent().next('.bd-quest-item').show();
			$(this).data('nextquest', 'true');
		}
	}
	
	/*if ($(this).data('qno') != '1') {
		$(this).next('.bd-q-pu-close').show();
	}*/
});

$(document).on('blur', '.bd-mcq-txt', function(){
	// changing status to edited
	var addMcqTextOption = $(this).attr('name')[$(this).attr('name').length - 3];
	showStatus('#bs-question-' + addMcqTextOption, 'Edited');
	$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
});

$(document).on('blur', '.bd-mcq-txt', function(){
	if ($(this).parent().is(':last-child')) {
		var addMcqTextOption = $(this).attr('name')[$(this).attr('name').length - 3];

		// changing status to edited
		showStatus('#bs-question-' + addMcqTextOption, 'Edited');
		$('#bs-question-' + addMcqTextOption).attr('data-status', 'edited');
		
		var htmlData = '<div class="bd-mcq-row clearfix">'
				+ '<div class="float-left bd-mcq-lbl">Option</div>'
				+ '<input name="sb-answers-' + addMcqTextOption + '[]" class="float-left bd-mcq-txt">'
				+ '<div class="float-left bd-mcq-close"></div>'
			+ '</div>';
		$(this).parent().after(htmlData);
	}
});

$(document).on('click', '.bd-mcq-close', function(){
	$(this).parent().remove();
	
	// changing status to edited
	var addMcqTextOption = $(this).prev().attr('name')[$(this).prev().attr('name').length - 3];
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