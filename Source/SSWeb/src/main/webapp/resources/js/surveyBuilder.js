// New Survey Tab
$('body').on('click', '#btn-new-survey', function(){
	$('.sb-tab-item').removeClass('sb-tab-active');
	$(this).addClass('sb-tab-active');
	$('.sb-content').hide();
	$('.new-survery-content').show();

	loadActiveSurvey();
});

// Choose Question type
$('body').on('click', '.sb-sel-icn-inact', function(){
	$('.sb-sel-icn-act').hide();
	$('.sb-sel-icn-inact').show();
	$(this).hide();
	$(this).parent().find('.sb-sel-icn-act').show();
	if($(this).attr('type') == 'mcq'){
		$('.sb-mcq-ans-wrapper').show();
	} else {
		$('.sb-mcq-ans-wrapper').hide();
	}
	
	var thisId = $(this).attr('id');
	if(thisId == "sb-range") {
		$('#sb-question-type').val('sb-range-smiles');
	} else {
		$('#sb-question-type').val(thisId);
	}
});

// Choose Rating type
$('body').click(function(){
	if($('.sb-dd-wrapper').css('display') == "block"){
		$('.sb-dd-wrapper').slideToggle(200);
	}
});

$('body').on('click', '.sb-sel-item-range-txt, .sb-sel-item-range-icn', function(e){
	e.stopPropagation();
	$('.sb-dd-wrapper').slideToggle(200);
});

$('body').on('click', '.sb-ratings-sel-item', function(e){
	e.stopPropagation();
	$('.sb-ratings-sel-item').removeClass('blue-text');
	$(this).addClass('blue-text');
	$('.sb-sel-icn-act').hide();
	$('.sb-sel-icn-inact').show();
	$('.sb-sel-icn-inact-range').hide();
	$('.sb-sel-icn-act-range').show();
	$('.sb-dd-wrapper').slideToggle(200);
	
	$('#sb-question-type').val($(this).attr('id'));
});

// Adding new option for MCQ
$('body').on('click', '.icn-sb-ad-btn', function(){
	$('#mcq-ans-container').append('<input name="sb-answers[]" class="sb-inparea" placeholder="Enter option">');
	var choiceLen = $('.sb-inparea').length;
	if(choiceLen > 2){
		$('.icn-sb-rem-btn').show();
	}
});

$('body').on('click', '.icn-sb-rem-btn', function(){
	var choiceLen = $('.sb-inparea').length;
	if(choiceLen > 2){
		$('.sb-inparea').each(function(){
			if($('.sb-inparea').index(this) > choiceLen - 2){
				$(this).remove();
			}
		});
	}
});

// Add Question
$('body').on('click', '#sb-question-add', function(){
	if ($('#sb-question-txt').val() == '') {
		$("#overlay-toast").html('Please Enter the Question details');
		showToast();
	} else {
		var url = "./addquestiontosurvey.do";
		callAjaxFormSubmit(url, addQuestionCallback, 'sb-new-question-form');
	}
});

function addQuestionCallback(response) {
	$("#overlay-toast").html(response);
	showToast();
	
	$('#sb-new-question-form :input').val('');
	loadActiveSurvey();
}

// Edit/Update question
var selectedRating = "";
var questionData;

$('body').on('click', '.sb-btn-edit', function(){
	$('.sb-ans-mc-wrapper').css('padding','0px');
	
	questionData = [];
	var item = {};

	var questionTxt = $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html();
	$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val(questionTxt);
	item ["questionTxt"] = questionTxt;
	
	var questionType = $(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type');
	item ["questionType"] = questionType;
	
	if (questionType == "sb-sel-mcq"){
		var ansLength = $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').attr('length');
		item ["ansLength"] = ansLength;
		
		var count = 1;
		var answers = [];
		while (count <= ansLength) {
			var ans = '.q-ans-obj-' + count;
			var ansEdit = '.q-ans-obj-' + count + '-txt';
			
			var answer = {};
			var answerText = $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ans).html();
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ansEdit).val(answerText);
			answer ["answerText"] = answerText;
			
			$('.sb-ans-mc-item').hide();
			$('.q-ans-obj-txt').show();
			
			answers.push(answer);
			count ++;
		}
		item ["answers"] = answers;
	}
	else if (questionType == "sb-range-smiles" || questionType == "sb-range-star" || questionType == "sb-range-scale") {
		$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').show();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').hide();
	}
	
	$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').hide();
	$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').show();
	$(this).next('.sb-btn-save').show();
	$(this).parent().find('.sb-btn-delete').hide();
	$(this).parent().find('.sb-btn-cancel').show();
	$(this).hide();
	
	questionData.push(item);
	questionData = JSON.stringify(questionData);
	console.log(questionData);
});

$('body').on('click', '.sb-btn-cancel', function(){
	$('.sb-ans-mc-wrapper').css('padding','0 10px');
	
	var surveyQuestion =  $.parseJSON(questionData)[0];
	if (surveyQuestion != null) {
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html(surveyQuestion.questionTxt);
		
		var questionType = surveyQuestion.questionType;
		if (questionType == "sb-sel-mcq"){
			var count = 1;
			$.each(surveyQuestion.answers, function(i, answer) {
				var ans = '.q-ans-obj-' + count;
				
				$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ans).html(answer.answerText);
				$('.sb-ans-mc-item').show();
				$('.q-ans-obj-txt').hide();
				count ++;
			});
		}
		else if (questionType == "sb-range-smiles" || questionType == "sb-range-star" || questionType == "sb-range-scale") {
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').hide();
			$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').show();
		}
		
		if (questionType == "sb-range-smiles"){
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.sb-icn-smiles').show();
		}
		else if (questionType == "sb-range-star") {
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.icn-full-star').show();
		}
		else if (questionType == "sb-range-scale") {
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.sb-icn-scale').show();
		}
		
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').show();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').hide();

		$(this).parent().find('.sb-btn-delete').show();
		$(this).parent().find('.sb-btn-cancel').hide();
		$(this).parent().find('.sb-btn-edit').show();
		$(this).parent().find('.sb-btn-save').hide();
	}	
	questionData = [];
});

$('body').on('click', '.sb-btn-save', function(){
	var questionId = $(this).parent().attr('data-questionid');
	var url = "./updatequestionfromsurvey.do?questionId=" + questionId;
	var formId = $(this).closest("form").attr('id');

	callAjaxFormSubmit(url, commonActiveSurveyCallback, formId);
	questionData = [];
});

$('body').on('click', '.sb-dd-item-ans', function(){
	selectedRating = $(this).attr('type');
	$('.sb-dd-item-ans').removeClass('blue-text');
	$(this).addClass('blue-text');
	
	$(this).parent().parent().parent().find('#sb-question-edit-type').val(selectedRating);
});

// Reorder Question
$('body').on('click', '.sb-btn-reorder-up', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().attr('data-questionid'));
	formData.append("reorderType", "up");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

$('body').on('click', '.sb-btn-reorder-down', function(){
	var formData = new FormData();
	formData.append("questionId", $(this).parent().attr('data-questionid'));
	formData.append("reorderType", "down");

	callAjaxPOSTWithTextData("./reorderQuestion.do", commonActiveSurveyCallback, true, formData);
});

// Delete Question(s)
$('body').on('click', '.sb-btn-delete', function(){
	var questionId = $(this).parent().attr('data-questionid');
	var url = "./removequestionfromsurvey.do?questionId=" + questionId;
	
	createPopupConfirm("Delete Question");
	$('body').on('click', '#overlay-continue', function(){
		callAjaxPOST(url, commonActiveSurveyCallback, true);
		
		overlayRevert();
		$('#overlay-continue').unbind('click');
	});
});

$('body').on('click', '#remove-selected', function(){
	createPopupConfirm("Delete Questions");
	$('body').on('click', '#overlay-continue', function(){
		var questionIds = [];
		var count = 0;
		$('.sb-q-chk-on').each(function(){
			if(!$(this).is(':visible')) {
				questionIds[count] = $(this).parent().attr('data-questionid');
				count++;
			}
		});
		var formData = new FormData();
		formData.append("questionIds", questionIds);

		callAjaxPOSTWithTextData("./removequestionsfromsurvey.do", commonActiveSurveyCallback, true, formData);

		overlayRevert();
		$('#overlay-continue').unbind('click');
	});
});

$('body').on('click', '.sb-q-chk-no', function(){
	$(this).hide();
	$(this).parent().find('.sb-q-chk-yes').show();
});

$('body').on('click', '.sb-q-chk-yes', function(){
	$(this).hide();
	$(this).parent().find('.sb-q-chk-no').show();
});

$('body').on('click', '#select-all-off', function() {
	$('.sb-q-chk-on').hide();
	$('.sb-q-chk-off').show();
});

$('body').on('click', '#select-all-on', function() {
	$('.sb-q-chk-on').show();
	$('.sb-q-chk-off').hide();
});

// Common Load active survey
function commonActiveSurveyCallback(response){
	$("#overlay-toast").html(response);
	showToast();

	loadActiveSurvey();
}

function loadActiveSurvey() {
	var url = "./getactivesurveydetails.do";
	callAjaxGET(url, loadActiveSurveyCallback, true);
}

function loadActiveSurveyCallback(response) {
	var surveyQuestions =  $.parseJSON(response);
	var htmlData = "";
	if (surveyQuestions != null) {
		
		// Row Header
		htmlData = htmlData
		+ '<div class="sb-item-row sb-item-row-header clearfix">'
			+ '<div class="float-left sb-q-item-no"></div>'
			+ '<div class="float-left sb-q-item-chk">'
				+ '<div id="select-all-off" class="sb-q-chk sb-q-chk-no sb-icn-pos-adj"></div>'
				+ '<div id="select-all-on" class="sb-q-chk sb-q-chk-yes sb-icn-pos-adj hide"></div>'
			+ '</div>'
			+ '<div class="float-left sb-q-item-txt text-center pos-relative">'
				+ '<span class="sb-q-header-txt">Survey Questions</span>'
				+ '<div id="remove-selected" class="sb-q-header-icons-rem">Remove</div>'
			+ '</div>'
		+ '</div>';

		// For Each Question
		var lengthQuestions = surveyQuestions.length;
		var countQues = 1;
		$.each(surveyQuestions, function(i, surveyQuestion) {
			// Question start
			htmlData = htmlData + '<div class="sb-item-row clearfix">';
			htmlData = htmlData + '<form id="sb-ques-edit-' + countQues + '">';

			// Question order
			htmlData = htmlData
			+ '<div class="float-left sb-q-item-no">(' + surveyQuestion.questionOrder + ')</div>';
			
			// Check boxes
			htmlData = htmlData
			+ '<div class="float-left sb-q-item-chk" data-questionid="' + surveyQuestion.questionId + '">'
				+ '<div class="sb-q-chk sb-q-chk-no sb-q-chk-on"></div>'
				+ '<div class="sb-q-chk sb-q-chk-yes sb-q-chk-off hide"></div>'
			+ '</div>';
			
			// Question Header
			htmlData = htmlData	+ '<div class="float-left sb-q-item-txt">';

			var questionTypeCode = surveyQuestion.questionType.trim();

			// Question Text
			htmlData = htmlData
			+ '<div class="sb-q-txt-1" q-type="' + questionTypeCode + '">' + surveyQuestion.question + '</div>'
			+ '<input type="hidden" id="sb-question-edit-type" name="sb-question-edit-type" value="' + questionTypeCode + '">'
			+ '<textarea id="sb-question-edit-txt" name="sb-question-edit-txt" class="sb-q-txt-1 sb-txt-ar"></textarea>';
			
			// Question types
			if (questionTypeCode == "sb-range-smiles") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
					+ '<div class="float-left sb-stars sb-icn-smiles"></div>'
					+ '<div class="float-left sb-stars icn-full-star hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale hide"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="sb-range-smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Smiles</div>'
					+ '<div type="sb-range-star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans">Star</div>'
					+ '<div type="sb-range-scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-range-star") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
				+ '<div class="float-left sb-stars sb-icn-smiles hide"></div>'
					+ '<div class="float-left sb-stars icn-full-star"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale hide"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="sb-range-smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans">Smiles</div>'
					+ '<div type="sb-range-star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Star</div>'
					+ '<div type="sb-range-scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-range-scale") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
					+ '<div class="float-left sb-stars sb-icn-smiles hide"></div>'
					+ '<div class="float-left sb-stars icn-full-star hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="sb-range-smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans">Smiles</div>'
					+ '<div type="sb-range-star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans">Star</div>'
					+ '<div type="sb-range-scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-sel-mcq" && surveyQuestion.answers.length > 0) {
				var lengthAns = surveyQuestion.answers.length;
				htmlData = htmlData + '<div class="sb-ans-mc-wrapper" length="' + lengthAns + '">';
				
				var countAns = 1;
				$.each(surveyQuestion.answers, function(i, answer) {
					htmlData = htmlData
					+ '<div class="sb-ans-mc-item q-ans-obj-' + countAns + '">' + answer.answerText + '</div>'
					+ '<input id="sb-edit-answers-text[]" name="sb-edit-answers-text[]" class="q-ans-obj-txt q-ans-obj-' + countAns + '-txt">'
					+ '<input type="hidden" id="sb-edit-answers-id[]" name="sb-edit-answers-id[]" value="' + answer.answerId + '">';
					countAns ++;
				});
				
				htmlData = htmlData	+ '</div>';
			}
			else if (questionTypeCode == "sb-sel-desc") {
				// No data required
			}
			
			// Question End
			htmlData = htmlData	+ '</div>';

			// Buttons
			htmlData = htmlData
			+ '<div class="float-right sb-q-item-btns clearfix" data-questionid="' + surveyQuestion.questionId + '">';
			
			if (countQues == 1) {
				htmlData = htmlData
					+ '<div class="float-left sb-q-btn sb-btn-reorder-up hide"></div>'
					+ '<div class="float-left sb-q-btn sb-btn-reorder-down"></div>';
			} else if (countQues == lengthQuestions) {
				htmlData = htmlData
				+ '<div class="float-left sb-q-btn sb-btn-reorder-up"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-reorder-down hide"></div>';
			} else {
				htmlData = htmlData
				+ '<div class="float-left sb-q-btn sb-btn-reorder-up"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-reorder-down"></div>';
			}
			
			htmlData = htmlData
				+ '<div class="float-left sb-q-btn sb-btn-delete"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-cancel hide"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-edit"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-save hide"></div>'
			+ '</div>';
			
			// Question End
			htmlData = htmlData	+ '</form>';
			htmlData = htmlData + '</div>';
			
			countQues++;
		});
		
		$('#sb-ques-wrapper').html(htmlData);
	} else {
		$('#sb-ques-wrapper').html('');
	}
}


// Choose Template Tab
$('body').on('click', '#btn-choose-template', function(){
	$('.sb-tab-item').removeClass('sb-tab-active');
	$(this).addClass('sb-tab-active');
	$('.sb-content').hide();
	$('.choose-survery-content').show();

	loadActiveTemplates();
});

// Expand/Hide template questions
$('body').on('click', '.sb-ct-exp', function(){
	$(this).hide();
	$('.sb-ct-close').show();
	$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideDown(350);
});

$('body').on('click', '.sb-ct-close', function(){
	$(this).hide();
	$('.sb-ct-exp').show();
	$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideUp(350);
});

// choose template clone
$('body').on('click', '.sb-btn-choose', function(){
	$('#overlay-text').html('Active Survey will be removed');
	createPopupConfirm("Copy Template");

	var templateId = $(this).attr('data-surveyid');
	
	$('body').on('click', '#overlay-continue', function(){
		var url = "./activatesurveyfromtemplate.do?templateId=" + templateId;
		callAjaxPOST(url, cloneSurveyTemplateCallback, true);

		overlayRevert();
		$('#overlay-continue').unbind('click');
		$('#btn-new-survey').click();
		loadActiveSurvey();
	});
});

function cloneSurveyTemplateCallback() {
	$("#overlay-toast").html(response);
	showToast();
}

// Load active Templates
function loadActiveTemplates() {
	var url = "./getactivesurveytemplates.do";
	callAjaxGET(url, loadActiveTemplateCallback, true);
}

function loadActiveTemplateCallback(response) {
	var surveyTemplates =  $.parseJSON(response);
	var htmlData = "";
	if (surveyTemplates != null) {
		htmlData = htmlData + '<div class="sb-item-row sb-item-row-header clearfix">';
			+ '<div class="float-left sb-q-item-no"></div>'
			+ '<div class="float-left sb-q-item-chk"></div>'
			+ '<div class="float-left sb-q-item-txt text-center pos-relative">'
				+ '<span class="sb-q-header-txt">Select Template</span>'
			+ '</div>'
		+ '</div>';
		
		var countQues = 1;
		$.each(surveyTemplates, function(i, surveyTemplate) {
			htmlData = htmlData + '<div class="sb-item-row clearfix">';
			
			// Template No
			htmlData = htmlData + '<div class="float-left sb-q-item-no">(' + countQues + ')</div>'
				+ '<div class="float-left sb-q-item-txt sb-q-item-txt-or">';
			
			// Template Name
			htmlData = htmlData + '<div class="sb-q-txt-1" q-type="rating">' + surveyTemplate.surveyName + '</div>'
				+ '<div class="sb-template-q-wrapper hide">'
				+ '<ul class="sb-ul">';
			
			// Questions
			$.each(surveyTemplate.questions, function(i, surveyQuestion) {
				var questionTypeCode = surveyQuestion.questionType.trim();
				
				htmlData = htmlData + '<li class="sb-q-template-item">'
					+ '<div class="sb-q-txt-1" q-type="' + questionTypeCode + '">' + surveyQuestion.question + '</div>';
				
				// Answers
				if (questionTypeCode == "sb-range-smiles") {
					htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
						+ '<div class="float-left sb-stars sb-icn-smiles"></div>'
					+ '</div>';
				}
				else if (questionTypeCode == "sb-range-star") {
					htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
						+ '<div class="float-left sb-stars icn-full-star"></div>'
					+ '</div>';
				}
				else if (questionTypeCode == "sb-range-scale") {
					htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
						+ '<div class="float-left sb-stars sb-icn-scale"></div>'
					+ '</div>';
				}
				else if (questionTypeCode == "sb-sel-mcq" && surveyQuestion.answers.length > 0) {
					htmlData = htmlData + '<div class="sb-ans-mc-wrapper">';
				
					$.each(surveyQuestion.answers, function(i, answer) {
						htmlData = htmlData + '<div class="sb-ans-mc-item">Answer 1</div>';
					});
					htmlData = htmlData	+ '</div>';
				}
				else if (questionTypeCode == "sb-sel-desc") {
					// No data required
				}
				
				htmlData = htmlData + '</li>';
			});
				
			htmlData = htmlData + '</ul>'
				+ '<div class="sb-btn-choose" data-surveyid="' + surveyTemplate.surveyId + '">Copy Template</div>'
				+ '</div>'
			+ '</div>';
			
			// Show Button
			htmlData = htmlData + '<div class="float-left sb-q-item-chk">'
				+ '<div class="sb-ct-exp cursor-pointer"></div>'
				+ '<div class="sb-ct-close cursor-pointer hide"></div>'
			+ '</div>';
			
			htmlData = htmlData + '</div>';
		});
		
		$('#sb-template-wrapper').html(htmlData);
	} else {
		$('#sb-template-wrapper').html('');
	}
}


// Popup
function createPopupConfirm(header) {
	$('#overlay-header').html(header);
	$('#overlay-continue').html('Continue');
	$('#overlay-cancel').html('Cancel');

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