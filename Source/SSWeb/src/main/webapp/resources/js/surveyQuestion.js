var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var qno = 0;
var data;
var questionDetails;
var agentId;

$(document).on('click', '.sq-np-item-next', function() {

});

function initSurvey(agentId, customerEmailId) {
	var success = false;
	$.ajax({
		// TODO provide mapping
		url : "./../data/" + agentId + "/" + customerEmailId,
		type : "GET",
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				paintSurveyPage(data);
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

function paintSurveyPage(jsonData) {
	data = jsonData.responseJSON;
	paintSurveyPageFromJson();
}

function paintSurveyPageFromJson() {
	questionDetails = data[qno];
	var question = questionDetails.question;
	var questionType = questionDetails.questionType;
	var isRatingQuestion = questionDetails.isRatingQuestion;
	$("#ques-text").html(question);
	if (questionType == "sb-range-star")
		$("#sq-stars").show();
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next").addClass("sq-np-item-disabled");
	}
	if(qno==0){
		$("#prev").addClass("sq-np-item-disabled");
	}
	else{
		$("#prev").removeClass("sq-np-item-disabled");
	}
}

function storeCustomerAnswer(customerResponse) {
	var success = false;
	var payload = {
		"answer" : customerResponse,
		"question" : questionDetails.question
	};
	questionDetails.customerResponse=customerResponse;
	$.ajax({
		// TODO provide mapping
		url : "./../data/storeAnswer",
		type : "GET",
		data : payload,
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				// paintSurveyPage(data);
			}
		},
		error : function(e) {
			console.error("error : " + e);
		}
	});
}

// Starting click events.
// Code to be executed on click of stars of rating question.

$('.sq-star').click(function() {
	$(this).parent().find('.sq-star').removeClass('sq-full-star');
	var starVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < starVal) {
			$(this).addClass('sq-full-star');
		}
	});
	if(qno!=data.length-1){
		$("#next").removeClass("sq-np-item-disabled");
	}
	storeCustomerAnswer(starVal);
});

// Code to be executed on click of next button.

$('.sq-np-item-next').click(function() {
	$(".sq-star").removeClass('sq-full-star');
	qno++;
	paintSurveyPageFromJson();
});

$('.sq-np-item-prev').click(function() {
	$(".sq-star").removeClass('sq-full-star');
	qno--;
	paintSurveyPageFromJson();
	var starVal = parseInt(questionDetails.customerResponse);
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < starVal) {
			$(this).addClass('sq-full-star');
		}
	});
	$("#next").removeClass("sq-np-item-disabled");
});