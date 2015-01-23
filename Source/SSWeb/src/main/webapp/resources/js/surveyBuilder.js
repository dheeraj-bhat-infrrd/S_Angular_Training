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
	$('#mcq-ans-container').append('<input class="sb-inparea" placeholder="Enter option">');
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
$('body').on('click', '#sb-question-add, #sb-question-done', function(){
	if($('#sb-question-txt').val() == '') {
		$("#overlay-toast").html('Please Enter the Question details');
		showToast();
	} else if($('.sb-inparea').val() != '') {
		$("#overlay-toast").html('Please Enter the Answer Options');
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

// Checkboxes for delete
$('body').on('click', '.sb-q-chk-no', function(){
	$(this).hide();
	$(this).parent().find('.sb-q-chk-yes').show();
});

$('body').on('click', '.sb-q-chk-yes', function(){
	$(this).hide();
	$(this).parent().find('.sb-q-chk-no').show();
});

// Edit question
$('body').on('click', '.sb-dd-item-ans', function(){
	selectedRating = $(this).attr('type');
	$('.sb-dd-item-ans').removeClass('blue-text');
	$(this).addClass('blue-text');
});

var selectedRating = "";
$('body').on('click', '.sb-btn-edit', function(){
	$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html());
	
	if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
		var length = $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').attr('length');
		var count = 1;
		while (count <= length) {
			var ans = '.q-ans-obj-' + count;
			var ansText = '.q-ans-obj-' + count + '-txt';
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ansText).val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ans).html());
			$('.sb-ans-mc-item').hide();
			$('.q-ans-obj-txt').show();
			count ++;
		}
	} else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating") {
		$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').show();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').hide();
	}
	
	$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').hide();
	$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').show();
	$(this).next('.sb-btn-save').show();
	$(this).hide();
});

$('body').on('click', '.sb-btn-save', function(){
	$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val());
	
	if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
		var length = $(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').attr('length');
		var count = 1;
		while (count <= length) {
			var ansText = '.q-ans-obj-' + count + '-txt';
			var ans = '.q-ans-obj-' + count;
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ans).html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find(ansText).val());
			$('.sb-ans-mc-item').show();
			$('.q-ans-obj-txt').hide();
			count ++;
		}
	} else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating") {
		$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').hide();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').show();
	}
	
	if(selectedRating == "smiles"){
		$('.sb-q-txt-2').find('.sb-stars').hide();
		$('.sb-q-txt-2').find('.sb-icn-smiles').show();
	} else if(selectedRating == "star") {
		$('.sb-q-txt-2').find('.sb-stars').hide();
		$('.sb-q-txt-2').find('.icn-full-star').show();
	} else if(selectedRating == "scale") {
		$('.sb-q-txt-2').find('.sb-stars').hide();
		$('.sb-q-txt-2').find('.sb-icn-scale').show();
	}
	
	$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').show();
	$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').hide();
	$(this).prev('.sb-btn-edit').show();
	$(this).hide();
});

// Update Question
$('body').on('click', '.sb-btn-save', function(){
	var questionId = $(this).parent().attr('data-questionid');
	var url = "./updatequestionfromsurvey.do?questionId=" + questionId;
	
	//TODO
	callAjaxPOST(url, commonActiveSurveyCallback, true);
});

// Delete Question
$('body').on('click', '.sb-btn-delete', function(){
	var questionId = $(this).parent().attr('data-questionid');
	var url = "./removequestionfromsurvey.do?questionId=" + questionId;
	callAjaxPOST(url, commonActiveSurveyCallback, true);
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
	console.log(response);
	var surveyQuestions =  $.parseJSON(response);
	var htmlData = "";
	if (surveyQuestions != null) {
		
		// Row Header
		htmlData = htmlData
		+ '<div class="sb-item-row sb-item-row-header clearfix">'
			+ '<div class="float-left sb-q-item-no"></div>'
			+ '<div class="float-left sb-q-item-chk">'
				+ '<div class="sb-q-chk sb-q-chk-no sb-icn-pos-adj"></div>'
				+ '<div class="sb-q-chk sb-q-chk-yes sb-icn-pos-adj hide"></div>'
			+ '</div>'
			+ '<div class="float-left sb-q-item-txt text-center pos-relative">'
				+ '<span class="sb-q-header-txt">Survey Questions</span>'
				+ '<div class="sb-q-header-icons-rem">Remove</div>'
			+ '</div>'
			+ '<div class="float-right sb-q-item-btns blue-text cursor-pointer view-all-lnk">View All</div>'
		+ '</div>';

		// For Each Question
		$.each(surveyQuestions, function(i, surveyQuestion) {
			// Question start
			htmlData = htmlData + '<div class="sb-item-row clearfix">';

			// Question order
			htmlData = htmlData
			+ '<div class="float-left sb-q-item-no">(' + surveyQuestion.questionOrder	+ ')</div>';
			
			// Check boxes
			htmlData = htmlData
			+ '<div class="float-left sb-q-item-chk">'
				+ '<div class="sb-q-chk sb-q-chk-no"></div>'
				+ '<div class="sb-q-chk sb-q-chk-yes hide"></div>'
			+ '</div>';
			
			// Question Header
			htmlData = htmlData	+ '<div class="float-left sb-q-item-txt">';

			var questionTypeCode = surveyQuestion.questionType.trim();
			var qType = "";
			if (questionTypeCode == "sb-range-smiles" || questionTypeCode == "sb-range-star" || questionTypeCode == "sb-range-scale") {
				qType = "rating";
			}
			else if (questionTypeCode == "sb-sel-mcq") {
				qType = "objective";
			}
			else if (questionTypeCode == "sb-sel-desc") {
				qType = "descriptive";
			}

			// Question Text
			htmlData = htmlData
			+ '<div class="sb-q-txt-1" q-type="' + qType + '">' + surveyQuestion.question + '</div>'
			+ '<textarea class="sb-q-txt-1 sb-txt-ar"></textarea>';
			
			// Question types
			if (questionTypeCode == "sb-range-smiles") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
					+ '<div class="float-left sb-stars icn-full-star"></div>'
					+ '<div class="float-left sb-stars sb-icn-smiles hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale hide"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Smiles</div>'
					+ '<div type="star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans">Star</div>'
					+ '<div type="scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-range-star") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
					+ '<div class="float-left sb-stars icn-full-star hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-smiles"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale hide"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans">Smiles</div>'
					+ '<div type="star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Star</div>'
					+ '<div type="scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-range-scale") {
				htmlData = htmlData + '<div class="sb-q-txt-2 clearfix">'
					+ '<div class="float-left sb-stars icn-full-star hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-smiles hide"></div>'
					+ '<div class="float-left sb-stars sb-icn-scale"></div>'
				+ '</div>';
				
				htmlData = htmlData + '<div class="sb-ans-rat-wrapper"><div class="sb-dd-wrapper-or">'
					+ '<div type="smiles" class="sb-icn-smiles sb-dd-item sb-dd-item-or sb-dd-item-ans">Smiles</div>'
					+ '<div type="star" class="sb-icn-star sb-dd-item sb-dd-item-or sb-dd-item-ans">Star</div>'
					+ '<div type="scale" class="sb-icn-scale sb-dd-item sb-dd-item-or sb-dd-item-ans blue-text">Scale</div>'
				+ '</div></div>';
			}
			else if (questionTypeCode == "sb-sel-mcq" && surveyQuestion.answers.length > 0) {
				var length = surveyQuestion.answers.length;
				htmlData = htmlData + '<div class="sb-ans-mc-wrapper" length="' + length + '">';
				
				var count = 1;
				$.each(surveyQuestion.answers, function(i, answer) {
					htmlData = htmlData
					+ '<div class="sb-ans-mc-item q-ans-obj-' + count + '">' + answer.answerText + '</div>'
					+ '<input class="q-ans-obj-txt q-ans-obj-' + count + '-txt">';
					count ++;
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
			+ '<div class="float-right sb-q-item-btns clearfix" data-questionid="' + surveyQuestion.questionId + '">'
				+ '<div class="float-left sb-q-btn sb-btn-reorder"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-delete"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-edit"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-save hide"></div>'
			+ '</div>';
			
			// Question End
			htmlData = htmlData + '</div>';
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

// Load active Templates
function loadActiveTemplates() {
	
}