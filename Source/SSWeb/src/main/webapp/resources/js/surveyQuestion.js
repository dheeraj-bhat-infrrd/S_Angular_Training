var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var qno = 0;
var data;
var questionDetails;
var agentId;
var agentName;
var customerResponse;
var customerEmail;
var mood;

$(document).on('click', '.sq-np-item-next', function() {
});

/*
 * Function to initiate survey. It hits controller to get list of all the
 * questions which are shown one after one to the customer.
 */
function initSurvey(firstName, lastName, email, agentId, agentName) {
	var success = false;
	this.agentId = agentId;
	this.agentName = agentName;
	customerEmail = email;
	var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"customerEmail" : email
	};
	$.ajax({
		// TODO provide mapping
		url : "./../triggersurvey",
		type : "GET",
		dataType : "JSON",
		data : payload,
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
		$("div[data-ques-type='smiley']").show();
		$("#ques-text-smiley").html(question);
		$("#sq-smiles").show();
	} else if (questionType == "sb-range-scale") {
		$("div[data-ques-type='scale']").show();
		$("#ques-text-scale").html(question);
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
		$("div[data-ques-type='smiley-text-final']").show();
		$("#ques-text-textarea").html(question);
		$("#text-area").show();
		$("#smiles-final").hide();
	} else if (questionType == "sb-master") {
		$("div[data-ques-type='smiley-text-final']").show();
		$("#text-area").hide();
		$("#smiles-final").show();
		$("#ques-text-textarea").html(question);
	}
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next-star").addClass("sq-np-item-disabled");
		$("#next-smile").addClass("sq-np-item-disabled");
		$("#next-scale").addClass("sq-np-item-disabled");
	}
	if (qno == 0) {
		$("#prev-star").addClass("sq-np-item-disabled");
		$("#prev-smile").addClass("sq-np-item-disabled");
		$("#prev-scale").addClass("sq-np-item-disabled");
		$("#prev-mcq").addClass("sq-np-item-disabled");
		$("#prev-textarea-smiley").addClass("sq-np-item-disabled");
	} else {
		$("#prev-star").removeClass("sq-np-item-disabled");
		$("#prev-smile").removeClass("sq-np-item-disabled");
		$("#prev-scale").removeClass("sq-np-item-disabled");
		$("#prev-mcq").removeClass("sq-np-item-disabled");
		$("#prev-textarea-smiley").removeClass("sq-np-item-disabled");
	}
	if (qno == data.length - 1) {
		$("#next-mcq").addClass("sq-np-item-disabled");
		$("#next-smile").addClass("sq-np-item-disabled");
		$("#next-star").addClass("sq-np-item-disabled");
		$("#next-textarea-smiley").addClass("sq-np-item-disabled");
		$("#skip-ques-mcq").hide();
	}
	$(".sq-main-txt").html("Survey for " + agentName);
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
		"stage" : qno + 1,
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

function updateCustomeResponse(feedback) {
	var payload = {
		"mood" : mood,
		"feedback" : feedback,
		"agentId" : agentId,
		"customerEmail" : customerEmail
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : "./../data/storeFeedback",
		type : "GET",
		data : payload
	});
}

function showFeedbackPage(mood) {
	$("div[data-ques-type]").hide();
	$("div[data-ques-type='smiley-text-final']").show();
	$("#text-area").show();
	$("#text-area").val("");
	$("#smiles-final").hide();
	switch (mood) {
	case "happy":
		question = "Please share your kind words about us!";
		$("#ques-text-textarea").html(question);
		break;
	case "neutral":
		question = "Please share your views to help us improve our quality!";
		$("#ques-text-textarea").html(question);
		break;
	case "sad":
		question = "Please let us know what went wrong so that you don't get disappointed next time!";
		$("#ques-text-textarea").html(question);
		break;
	}
	$("#prev-textarea-smiley").removeClass("sq-np-item-disabled");
	$("#next-textarea-smiley").addClass("sq-np-item-disabled");
	$("#submit").show();
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
	});

	$('.st-mcq-chk-off').click(function() {
		customerResponse = $(this).parent().parent().attr('data-answer');
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
		$("#next-star").removeClass("sq-np-item-disabled");
	}
	storeCustomerAnswer(starVal);
});

// Code to be executed on click of next for all types of questions.

$('.sq-np-item-next')
		.click(
				function() {
					if (questionDetails.questionType == "sb-sel-mcq"
							&& customerResponse != undefined) {
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-sel-desc"
							&& customerResponse != undefined) {
						customerResponse = $("#text-area").val();
						if (customerResponse == undefined) {
							customerResponse = "";
						}
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-range-star") {
						if ($('#next-star').hasClass("sq-np-item-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-smiles") {
						if ($('#next-smile').hasClass("sq-np-item-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-scale") {
						if ($('#next-scale').hasClass("sq-np-item-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-master") {
						return;
					}
					$(".sq-star").removeClass('sq-full-star');
					qno++;
					paintSurveyPageFromJson();

					if (questionDetails.questionType == "sb-range-star") {
						var starVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(starVal)) {
							$("#next-star").removeClass("sq-np-item-disabled");
							$('#sq-stars').find('.sq-star').each(
									function(index) {
										if (index < starVal) {
											$(this).addClass('sq-full-star');
										}
									});
						}
					}
					if (questionDetails.questionType == "sb-range-smiles") {
						var smileVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(smileVal)) {
							$("#next-smile").removeClass("sq-np-item-disabled");
							$('#sq-smiles').find('.sq-smile').each(
									function(index) {
										if (index < smileVal) {
											$(this).addClass('sq-full-smile');
										}
									});
						}
					}
					if (questionDetails.questionType == "sb-range-scale") {
						var value = parseInt(questionDetails.customerResponse);
						if (!isNaN(value)) {
							$("#next-scale").removeClass("sq-np-item-disabled");
							$('#range-slider-value').html(value);
						}
					}
					if (questionDetails.questionType == "sb-sel-mcq") {
						customerResponse = "";
					}

				});

// Code to be executed on click of previous for star and smile questions.

$('.sq-np-item-prev').click(function() {
	if (qno == 0) {
		return;
	}
	$(".sq-star").removeClass('sq-full-star');
	$(".sq-smile").removeClass('sq-full-smile');
	qno--;
	paintSurveyPageFromJson();
	if (questionDetails.questionType == "sb-range-star") {
		var starVal = parseInt(questionDetails.customerResponse);
		$('#sq-stars').find('.sq-star').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-star');
			}
		});
	}
	if (questionDetails.questionType == "sb-range-smiles") {
		var starVal = parseInt(questionDetails.customerResponse);
		$('#sq-smiles').find('.sq-smile').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-smile');
			}
		});
	}
	if (questionDetails.questionType == "sb-range-scale") {
		var value = parseInt(questionDetails.customerResponse);
		$('#range-slider-value').html(value);
	}
	if (questionDetails.questionType == "sb-sel-desc") {
		var val = questionDetails.customerResponse;
		if (val != undefined) {
			$("#text-area").val(val);
		}
	}
	$("#next-star").removeClass("sq-np-item-disabled");
	$("#next-smile").removeClass("sq-np-item-disabled");
	$("#next-scale").removeClass("sq-np-item-disabled");
	$("#next-textarea-smiley").removeClass("sq-np-item-disabled");
});

/* Click event on grey smile. */
$('.sq-smile').click(function() {
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile');
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).addClass('sq-full-smile');
		}
	});
	if (qno != data.length - 1) {
		$("#next-smile").removeClass("sq-np-item-disabled");
	}
	storeCustomerAnswer(smileVal);
	$("#next-star").removeClass("sq-np-item-disabled");
});

$('.sq-happy-smile').click(function() {
	// Update customer's mood in db and ask for cutomer's kind words.
	mood = "happy";
	showFeedbackPage(mood);
});
$('.sq-neutral-smile').click(function() {
	// Update customer's mood in db and ask for feedback that could have made
	// him happy.
	mood = "neutral";
	showFeedbackPage(mood);
});
$('.sq-sad-smile').click(function() {
	// Update customer's mood in db and ask what went wrong during the entire
	// course.
	mood = "sad";
	showFeedbackPage(mood);
});

$('#start-btn').click(function() {
	var firstName = $('#firstName').val().trim();
	var lastName = $('#lastName').val().trim();
	var email = $('#email').val().trim();
	var agentId = $('#prof-container').attr("data-agentId");
	var agentName = $('#prof-container').attr("data-agentName");
	initSurvey(firstName, lastName, email, agentId, agentName);
});

$('#submit')
		.click(
				function() {
					var feedback = $("#text-area").val();
					updateCustomeResponse(feedback);
					$('#overlay-toast')
							.html(
									'Congratulations! Your survey has been submitted successfully.');
					showToast();
				});

$('input[type="range"]').rangeslider({
	polyfill : false,

	// Default CSS classes
	rangeClass : 'rangeslider',
	fillClass : 'rangeslider__fill',
	handleClass : 'rangeslider__handle',

	onSlide : function(position, value) {
		// $('div[quest-no="' + survQuesNo + '"]').find(
		// '.sq-slider-val').html(value);
		$('#range-slider-value').html(value);
	},
	// Callback function
	onSlideEnd : function(position, value) {
		$('#range-slider-value').html(value);
		storeCustomerAnswer(value);
	},
});

$('.sq-pts-red').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-1').addClass('showHoverTab');
	var answer = $('.sq-pts-red').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("sq-np-item-disabled");
});

$('.sq-pts-org').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-2').addClass('showHoverTab');
	var answer = $('.sq-pts-org').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("sq-np-item-disabled");
});

$('.sq-pts-lgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-3').addClass('showHoverTab');
	var answer = $('.sq-pts-lgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("sq-np-item-disabled");
});

$('.sq-pts-military').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-4').addClass('showHoverTab');
	var answer = $('.sq-pts-military').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("sq-np-item-disabled");
});

$('.sq-pts-dgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-5').addClass('showHoverTab');
	var answer = $('.sq-pts-dgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("sq-np-item-disabled");
});
