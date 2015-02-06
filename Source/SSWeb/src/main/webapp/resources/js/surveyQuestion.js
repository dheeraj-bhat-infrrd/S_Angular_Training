var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var qno = 0;
var data;
var questionDetails;
var agentId;
var customerResponse;
var customerEmail;

$(document).on('click', '.sq-np-item-next', function() {
});

/*
 * Function to initiate survey. It hits controller to get list of all the
 * questions which are shown one after one to the customer.
 */
function initSurvey(agentId, customerEmailId) {
	var success = false;
	this.agentId = agentId;
	customerEmail = customerEmailId;
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

/*
 * It gets the questions from array of questions and finds out the current
 * question based upon current index for question number. It also checks over
 * various conditions of the question and renders the page accordingly.
 */
function paintSurveyPageFromJson() {
	$("div[data-ques-type]").hide();
	questionDetails = data[qno];
	var question = questionDetails.question;
	var questionType = questionDetails.questionType;
	var isRatingQuestion = questionDetails.isRatingQuestion;
	if (questionType == "sb-range-star") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
	} else if (questionType == "sb-range-smiles") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
	} else if (questionType == "sb-range-scale") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
	} else if (questionType == "sb-sel-mcq") {
		$("div[data-ques-type='mcq']").show();
		$("#mcq-ques-text").html(question);
		$("#skip-ques-mcq").show();
		$("#next-mcq").show();
		$("#next-mcq").removeClass("sq-np-item-disabled");
		var options = "";
		for ( var option in questionDetails.answers) {
			options += paintMcqAnswer(questionDetails.answers[option].answerText);
		}
		$("#answer-options").html(options);
		bindMcqCheckButton();
	} else if (questionType == "sb-sel-desc") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
	}
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next").addClass("sq-np-item-disabled");
	}
	if (qno == 0) {
		$("#prev").addClass("sq-np-item-disabled");
		$("#prev-mcq").addClass("sq-np-item-disabled");
	} else {
		$("#prev").removeClass("sq-np-item-disabled");
	}
	if (qno == data.length - 1) {
		$("#next-mcq").addClass("sq-np-item-disabled");
		$("#skip-ques-mcq").hide();
	}
}

/*
 * This method is used to store the answer provided by the customer for a
 * specific question.
 */
function storeCustomerAnswer(customerResponse) {
	var success = false;
	var payload = {
		"answer" : customerResponse,
		"question" : questionDetails.question,
		"questionType" : questionDetails.questionType,
		"stage" : qno+1,
		"agentId" : agentId,
		"customerEmail" : customerEmail
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
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

/*
 * This is used to render all the possible choices for an MCQ.
 */
function paintMcqAnswer(answer) {
	var divToPopulate = "<div data-answer='" + answer
			+ "' class='sq-mcq-item clearfix'>"
			+ "<div class='sq-mcq-chk-wrapper float-left'>"
			+ "<div class='float-left sq-mcq-chk st-mcq-chk-on hide'>"
			+ "</div>" + "<div class='float-left sq-mcq-chk st-mcq-chk-off'>"
			+ "</div>" + "</div>"
			+ "<div class='sq-mcq-ans-wrapper float-left'>" + answer
			+ "</div></div>";
	return divToPopulate;
}

function bindMcqCheckButton() {
	$('.st-mcq-chk-on').click(function() {
		$(this).hide();
		$(this).parent().find('.st-mcq-chk-off').show();
		customerResponse = $(this).parent().attr('answer');
	});

	$('.st-mcq-chk-off').click(function() {
		$('.sq-mcq-wrapper').find('.st-mcq-chk-on').hide();
		$('.sq-mcq-wrapper').find('.st-mcq-chk-off').show();
		$(this).hide();
		$(this).parent().find('.st-mcq-chk-on').show();
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
	if (qno != data.length - 1) {
		$("#next").removeClass("sq-np-item-disabled");
	}
	storeCustomerAnswer(starVal);
});

// Code to be executed on click of next for star questions.

$('.sq-np-item-next').click(
		function() {
			if (questionDetails.question == "sb-sel-mcq"
					&& customerResponse != undefined)
				storeCustomerAnswer(customerResponse);
			$(".sq-star").removeClass('sq-full-star');
			qno++;
			paintSurveyPageFromJson();
		});

// Code to be executed on click of previous for star questions.

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