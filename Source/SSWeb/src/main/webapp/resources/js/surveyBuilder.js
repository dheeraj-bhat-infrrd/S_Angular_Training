$(document).ready(function(){
	
	loadActiveSurvey();
	
	// New Survey Tab
	$('#btn-new-survey').click(function(){
		$('.sb-tab-item').removeClass('sb-tab-active');
		$(this).addClass('sb-tab-active');
		$('.sb-content').hide();
		$('.new-survery-content').show();
	});
	
	$('.sb-sel-icn-inact').click(function(){
		$('.sb-sel-icn-act').hide();
        $('.sb-sel-icn-inact').show();
        $(this).hide();
        $(this).parent().find('.sb-sel-icn-act').show();
        if($(this).attr('type') == 'mcq'){
            $('.sb-mcq-ans-wrapper').show();
        } else {
            $('.sb-mcq-ans-wrapper').hide();
        }
        
        // Changes
        var thisId = $(this).attr('id');
        if(thisId == "sb-range") {
            $('#sb-question-type').val('sb-range-smiles');
        } else {
            $('#sb-question-type').val(thisId);
        }
	});
	
    $('.sb-sel-item-range-txt, .sb-sel-item-range-icn').click(function(e){
        e.stopPropagation();
        $('.sb-dd-wrapper').slideToggle(200);
    });
	
    $('.sb-ratings-sel-item').click(function(e){
        e.stopPropagation();
        $('.sb-ratings-sel-item').removeClass('blue-text');
        $(this).addClass('blue-text');
        $('.sb-sel-icn-act').hide();
        $('.sb-sel-icn-inact').show();
        $('.sb-sel-icn-inact-range').hide();
        $('.sb-sel-icn-act-range').show();
        $('.sb-dd-wrapper').slideToggle(200);
        
        // Changes
        $('#sb-question-type').val($(this).attr('id'));
    });
	
	$('.sb-q-chk-no').click(function(){
		$(this).hide();
		$(this).parent().find('.sb-q-chk-yes').show();
	});
	
	$('.sb-q-chk-yes').click(function(){
		$(this).hide();
		$(this).parent().find('.sb-q-chk-no').show();
	});
	
	$('body').on('click', '.sb-dd-item-ans', function(){
		selectedRating = $(this).attr('type');
		$('.sb-dd-item-ans').removeClass('blue-text');
		$(this).addClass('blue-text');
	});
	
	$('body').click(function(){
        if($('.sb-dd-wrapper').css('display') == "block"){
            $('.sb-dd-wrapper').slideToggle(200);
        }
    });

	var selectedRating = "";
	$('body').on('click', '.sb-btn-edit', function(){
		$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html());
		
		if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html());
			$('.sb-ans-mc-item').hide();
			$('.q-ans-obj-txt').show();
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
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val());
			$('.sb-ans-mc-item').show();
			$('.q-ans-obj-txt').hide();
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
	
	// Choose Template Tab
	$('#btn-choose-template').click(function(){
		$('.sb-tab-item').removeClass('sb-tab-active');
		$(this).addClass('sb-tab-active');
		$('.sb-content').hide();
		$('.choose-survery-content').show();
	});
	
	$('.sb-ct-exp').click(function(){
		$(this).hide();
		$('.sb-ct-close').show();
		$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideDown(350);
	});
	
	$('.sb-ct-close').click(function(){
		$(this).hide();
		$('.sb-ct-exp').show();
		$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideUp(350);
	});
});

// Submit new Question
$('#sb-question-add').click(function(){
	if($('#sb-new-question-form').val() != '') {
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

// Load active survey
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

			// Question Text
			htmlData = htmlData
			+ '<div class="sb-q-txt-1" q-type="rating">' + surveyQuestion.question + '</div>'
			+ '<textarea class="sb-q-txt-1 sb-txt-ar"></textarea>';
			
			// Question types
			console.log(surveyQuestion.questionType);
			if (surveyQuestion.questionType === 'sb-range-smiles') {
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
			else if (surveyQuestion.questionType === 'sb-range-star') {
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
			else if (surveyQuestion.questionType === 'sb-range-scale') {
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
			else if (surveyQuestion.questionType === 'sb-sel-desc') {
				// No data
			}
			else if (surveyQuestion.questionType === 'sb-sel-mult' && surveyQuestion.answers.size > 0) {
				htmlData = htmlData + '<div class="sb-ans-mc-wrapper">';
				
				$.each(surveyQuestion.answers, function(i, answer) {
					htmlData = htmlData
					+ '<div class="sb-ans-mc-item q-ans-obj-1">' + answer.answerText + '</div>'
					+ '<input class="q-ans-obj-txt q-ans-obj-1-txt">';
				});
				
				htmlData = htmlData	+ '</div>';
			}
			
			// Question End
			htmlData = htmlData	+ '</div>';

			// Buttons
			htmlData = htmlData
			+ '<div class="float-right sb-q-item-btns clearfix">'
				+ '<div class="float-left sb-q-btn sb-btn-reorder"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-delete"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-edit"></div>'
				+ '<div class="float-left sb-q-btn sb-btn-save hide"></div>'
			+ '</div>';
			
			// Question End
			htmlData = htmlData + '</div>';
		});
		
		$("#sb-ques-wrapper").html(htmlData);
	} else {
		$("#sb-ques-wrapper").html('');
	}
}