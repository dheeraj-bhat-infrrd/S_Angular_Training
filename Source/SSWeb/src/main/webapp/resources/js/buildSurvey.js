// Populate Existing Survey Questions 
function commonActiveSurveyCallback(response){
	showInfo(response);
	loadActiveSurveyQuestions();
}

function loadActiveSurveyQuestions() {
	var url = "./getactivesurveyquestions.do";
	callAjaxGET(url, populateActiveSurveyQuestions, true);
}

function populateActiveSurveyQuestions(response) {
	var surveyDetail = $.parseJSON(response);
	var surveyQuestions = surveyDetail.questions;
	var htmlData = "";

	if (surveyQuestions != null) {
		var lengthQuestions = surveyQuestions.length;
		var countQues = 1;
		
		// For Each Question
		$.each(surveyQuestions, function(i, surveyQuestion) {
			// Question start
			htmlData = htmlData + '<div class="bd-srv-tbl-row clearfix" data-questionid="' + surveyQuestion.questionId + '">';

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
	$('body').addClass('body-no-scroll');
});

function revertQuestionOverlay() {
	$('#bd-srv-pu').hide();
	$('body').removeClass('body-no-scroll');
	loadActiveSurveyQuestions();
}

$('.bd-q-btn-done').click(function() {
	createPopupConfirm("Unsaved Changes", "Save", "Discard");
	
	var lastQuestion = currentQues - 1;
	$('body').on('click', '#overlay-continue', function(){
		// submit for adding question
		if (lastQuestion > 0 && $('#bs-question-' + lastQuestion).attr('data-state') == 'new'
			&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
			
			if ($('#sb-question-txt-' + lastQuestion).val() == '') {
				$("#overlay-toast").html('Please finish adding the Question');
				showToast();
			} else {
				var url = "./addquestiontosurvey.do?order=" + lastQuestion;
				callAjaxFormSubmit(url, function(data) {
					var map =  $.parseJSON(data);
					$("#overlay-toast").html(map.message);
					showToast();
					
					if (map.status == "success") {
						$('#bs-question-' + lastQuestion).attr('data-state', 'editable');
						$('#bs-question-' + lastQuestion).attr('data-quesref', map.questionId);

						revertQuestionOverlay();
					}
					$('#overlay-continue').unbind('click');
					overlayRevert();
					
				}, 'bs-question-' + lastQuestion);
			}
		}
		// submit for modifying question
		else if (lastQuestion > 0 && $('#bs-question-' + lastQuestion).attr('data-state') == 'editable'
			&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
			
			if ($('#sb-question-txt-' + lastQuestion).val() == '') {
				$("#overlay-toast").html('Please finish editing the Question');
				showToast();
			} else {
				var questionId = $('#bs-question-' + lastQuestion).attr('data-quesref');
				var url = "./updatequestionfromsurvey.do?order=" + lastQuestion + "&questionId=" + questionId;
				callAjaxFormSubmit(url, function(data) {
					var map =  $.parseJSON(data);
					$("#overlay-toast").html(map.message);
					showToast();
					
					if (map.status == "success") {
						revertQuestionOverlay();
					}
					$('#overlay-continue').unbind('click');
					overlayRevert();
				}, 'bs-question-' + lastQuestion);
			}
		}
		
		$('#overlay-continue').unbind('click');
	});
});

$(document).on('click', '.bd-q-pu-close', function() {
	$(this).parent().parent().remove();
});

// TODO Question edit
$(document).on('click', '.srv-tbl-edit', function() {
	var questionId = $(this).parent().parent().data('questionid');
	
    var editQuestion = '<div class="sb-edit-q-wrapper">'
		+ '<form id="bs-question-' + questionId + '" data-quesnum="' + questionId + '" data-quesref="">'
		+ '<div class="bd-q-pu-header clearfix">'
			+ '<div class="float-left bd-q-pu-header-lft">Edit Your Question Here</div>'
		+ '</div>'
		+ '<div class="bd-q-pu-txt-wrapper pos-relative">'
			+ '<input type="hidden" id="sb-question-type-' + questionId + '" name="sb-question-type-' + questionId + '"/>'
			+ '<input id="sb-question-txt-' + questionId + '" name="sb-question-txt-' + questionId + '" class="bd-q-pu-txt-edit" data-nextquest="false" data-qno="' + currentQues + '">'
			+ '<div class="bd-q-pu-close hide"></div>'
		+ '</div>'
		+ '<div class="bs-ans-wrapper hide" style="display: block;">'
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
							+ '<input name="sb-answers-' + questionId + '[]" class="float-left bd-mcq-txt">'
							+ '<div class="float-left bd-mcq-close hide"></div>'
						+ '</div>'
						+ '<div class="bd-mcq-row clearfix">'
							+ '<div class="float-left bd-mcq-lbl">Option</div>'
							+ '<input name="sb-answers-' + questionId + '[]" class="float-left bd-mcq-txt">'
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
			+ '</div>'
		+ '</form>'
		+ '</div>'
    	+ '<div class="bd-q-pu-done-wrapper clearfix">'
    		+ '<div data-quesnum="' + questionId + '" class="bd-q-edit-btn-done float-left">Done</div>'
    	+ '</div>'
    + '</div>';
    $(this).parent().parent().after(editQuestion);
});

$('.bd-q-edit-btn-done').click(function() {
	var quesNum = $(this).data('quesnum');

	if ($('#sb-question-txt-' + quesNum).val() == '') {
		$("#overlay-toast").html('Please finish editing the Question');
		showToast();
	} else {
		var questionId = $('#bs-question-' + quesNum).attr('data-quesref');
		var url = "./updatequestionfromsurvey.do?order=" + quesNum + "&questionId=" + questionId;
		callAjaxFormSubmit(url, function(data) {
			$("#overlay-toast").html(data);
			showToast();
			
			$(this).closest('.sb-edit-q-wrapper').remove();
		}, 'bs-question-' + quesNum);
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
});

$(document).on('click', '.bd-tab-com', function() {
	$(this).parent().find('.bd-ans-tab-item').removeClass('bd-ans-tab-sel');
	$(this).addClass('bd-ans-tab-sel');
	$(this).parent().parent().parent().find('.bd-ans-type-item').hide();
	$(this).parent().parent().parent().find('.bd-ans-type-com').show();

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('#sb-question-type-' + quesNum).val($(this).data('id'));
});

$(document).on('click', '.bd-ans-img-wrapper', function() {
	$(this).parent().parent().find('.bd-ans-img').addClass('bd-img-sel');
	$(this).find('.bd-ans-img').removeClass('bd-img-sel');

	var quesNum = $(this).closest('form').data('quesnum');
	$(this).closest('form').find('#sb-question-type-' + quesNum).val($(this).data('id'));
});

$(document).on('click', '.bd-com-chk', function() {
	if ($(this).hasClass('bd-com-unchk')) {
		$(this).removeClass('bd-com-unchk');
	} else {
		$(this).addClass('bd-com-unchk');
	}
});

// Add another question and submit previous
var currentQues = 1;
$(document).on("input", '.bd-q-pu-txt', function() {
	var form = $(this).closest('form');
	var quesOrder = form.data('quesnum') - 1;
	
	// Settings status
	$('#bs-question-' + form.data('quesnum')).attr('data-state', 'edited');
	
	// submit for adding new question
	if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'new'
			&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish adding the Question');
			showToast();
		} else {
			var url = "./addquestiontosurvey.do?order=" + quesOrder;
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();
				
				if (map.status == "success") {
					$('#bs-question-' + quesOrder).attr('data-state', 'editable');
					$('#bs-question-' + quesOrder).attr('data-quesref', map.questionId);
				}
			}, 'bs-question-' + quesOrder);
		}
	}
	// submit for modifying question
	else if (quesOrder > 0 && $('#bs-question-' + quesOrder).attr('data-state') == 'editable'
		&& $('#bs-question-' + quesOrder).attr('data-status') == 'edited') {
		
		if ($('#sb-question-txt-' + quesOrder).val() == '') {
			$("#overlay-toast").html('Please finish editing the Question');
			showToast();
		} else {
			var questionId = $('#bs-question-' + quesOrder).attr('data-quesref');
			var url = "./updatequestionfromsurvey.do?order=" + quesOrder + "&questionId=" + questionId;
			callAjaxFormSubmit(url, function(data) {
				var map =  $.parseJSON(data);
				$("#overlay-toast").html(map.message);
				showToast();
				
				if (map.status == "success") {
					
				}
			}, 'bs-question-' + quesOrder);
		}
	}
	
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

$('body').on('blur', '.bd-mcq-txt', function(){
	if ($(this).parent().is(':last-child')) {
		var addMcqTextOption = $(this).attr('name')[$(this).attr('name').length - 3];

		// Settings status
		$('#bs-question-' + addMcqTextOption).attr('data-state', 'edited');
		
		var htmlData = '<div class="bd-mcq-row clearfix">'
				+ '<div class="float-left bd-mcq-lbl">Option</div>'
				+ '<input name="sb-answers-' + addMcqTextOption + '[]" class="float-left bd-mcq-txt">'
				+ '<div class="float-left bd-mcq-close"></div>'
			+ '</div>';
		$(this).parent().after(htmlData);
	}
});

$('body').on('click', '.bd-mcq-close', function(){
	$(this).parent().remove();
});

// Remove Question from survey
$('body').on('click', '.srv-tbl-rem', function(){
	var questionId = $(this).parent().parent().data('questionid');
	var url = "./removequestionfromsurvey.do?questionId=" + questionId;
	
	createPopupConfirm("Delete Question ?", "Delete", "Cancel");
	$('body').on('click', '#overlay-continue', function(){
		callAjaxPOST(url, commonActiveSurveyCallback, true);
		
		overlayRevert();
		$('#overlay-continue').unbind('click');
	});
});

// Reorder Question in survey
$('body').on('click', '.srv-tbl-move-up', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "up");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

$('body').on('click', '.srv-tbl-move-dn', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().parent().data('questionid'));
	formData.append("reorderType", "down");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

// Overlay Popup
function createPopupConfirm(header, ok, cancel) {
	$('#overlay-header').html(header);
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
$('body').on('click', '#overlay-cancel', function(){
	$('#overlay-continue').unbind('click');
	overlayRevert();
});