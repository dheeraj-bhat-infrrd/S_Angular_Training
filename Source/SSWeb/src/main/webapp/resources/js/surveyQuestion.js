var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;
var qno = 0;
var questions;
var questionDetails;
var agentId;
var agentName;
var customerResponse;
var customerEmail;
var mood;
var stage;
var isSmileTypeQuestion=true;
var swearWords = [];
var isAbusive;
var autoPost;
var autoPostScore;
var happyText;
var neutralText;
var sadText;
var rating;
var firstName;
var lastName;
var surveyUrl = "/rest/survey/";
var editable;
var yelpEnabled;
var googleEnabled;
var agentProfileLink;

$(document).on('click', '.sq-np-item-next', function() {
});

/*
 * Function to initiate survey. It hits controller to get list of all the
 * questions which are shown one after one to the customer.
 */
function initSurvey(firstName, lastName, email, agentId, agentName,
		grecaptcharesponse) {
	this.agentId = agentId;
	this.agentName = agentName;
	customerEmail = email;
	/*var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"customerEmail" : email,
		"g-recaptcha-response" : grecaptcharesponse,
		"relationship" : relationship
	};*/
	
	
	//$('input[relationship]').val(relationship);
	$('input[g-recaptcha-response]').val(grecaptcharesponse);
	
	if($('#cust-agent-verify').hasClass('bd-check-img-checked')){
		$('#overlay-toast').html("Verify that you have done business with the agent");
		showToast();
		return false;
	}
	
	$('#survey-request-form').submit();
	
	/*$.ajax({
		url : window.location.origin + surveyUrl + "triggersurvey",
		type : "GET",
		dataType : "TEXT",
		data : payload,
		success : function(data) {			
			$('#overlay-toast').html(data);
			$("#recaptcha_reload").click();
			showToast();
			clearForm();
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});*/
}

function initSurveyWithUrl(q) {
	console.log(window.location.origin);
	var success = false;
	var payload = {
		"q" : q
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "triggersurveywithurl",
		type : "GET",
		dataType : "JSON",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
			else {
				$('#overlay-toast').html(data.errMessage);
				$("#recaptcha_reload").click();
				showToast();
			}
		},
		complete : function(data) {
			if (success) {
				agentId = data.responseJSON.agentId;
				loadAgentPic(agentId);
				agentName = data.responseJSON.agentName;
				customerEmail = data.responseJSON.customerEmail;
				firstName = data.responseJSON.customerFirstName;
				lastName = data.responseJSON.customerLastName;
				paintSurveyPage(data);
			}
		},
		error : function(e) {
			showPageNotFoundError();
		}
	});
}

function showPageNotFoundError(){
	window.location = window.location.origin + surveyUrl + "notfound";
}

function loadAgentPic(agentId){
	var imageUrl;
	var success = false;
	var payload = {
		"agentId" : agentId
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "displaypiclocationofagent",
		type : "GET",
		dataType : "text",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				imageUrl = data.responseText;
				if(imageUrl!='' && imageUrl!=null)
					$("#agnt-img").css("background", "url("+imageUrl+") no-repeat center");
					$("#agnt-img").css("background-size", "contain");
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
		}
	});
}

function paintSurveyPage(jsonData) {
	$("#pst-srvy-div").hide();
	questions = jsonData.responseJSON.survey;
	stage = jsonData.responseJSON.stage;
	editable = Boolean(jsonData.responseJSON.editable);
	happyText = jsonData.responseJSON.happyText;
	neutralText = jsonData.responseJSON.neutralText;
	sadText = jsonData.responseJSON.sadText;
	autoPost = jsonData.responseJSON.autopostEnabled;
	autoPostScore = jsonData.responseJSON.autopostScore;
	yelpEnabled = Boolean(jsonData.responseJSON.yelpEnabled);
	googleEnabled = Boolean(jsonData.responseJSON.googleEnabled);
	agentProfileLink = jsonData.responseJSON.agentProfileLink;
	if (stage != undefined)
		qno = stage;
	paintSurveyPageFromJson();
}

/*
 * It gets the questions from array of questions and finds out the current
 * question based upon current index for question number. It also checks over
 * various conditions of the question and renders the page accordingly.
 */
function paintSurveyPageFromJson() {
	$("div[data-ques-type]").hide();
	if (qno == -1 && editable == false) {
		$("div[data-ques-type]").hide();
		$("div[data-ques-type='error']").show();
		$('#content-head').html('Survey');
		$('#content').html(
				"OOPS! It looks like you have already taken a survey for " + agentName + "."
				+"<br/><br/>"
				+"Are you trying to amend a prior response? If so click the link below and we will email you the access required<br/><br/>")
				.append("<div>Link to resend original Survey Responses so they can be amended</div>");
		
		var linkToResendSurvey = $('<div>').attr({
			"id" : "changeSurvey",
			"class" : "change-survey-btn"
		}).html("Retake survey");
		
		$('#content').append(linkToResendSurvey);
		
		$(document).on('click', '#changeSurvey', function() {
			retakeSurveyRequest();
		});
		return;
	}
	questionDetails = questions[qno];
	var question = questionDetails.question;
	
	question = question.replace(/\[name\]/gi, agentName);
	var questionType = questionDetails.questionType;
	var isRatingQuestion = questionDetails.isRatingQuestion;
	if (isRatingQuestion == 1) {
		$("#skip-ques").hide();
		$("#next-star").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-scale").addClass("btn-com-disabled");
	}
	if (questionType == "sb-range-star") {
		$("div[data-ques-type='stars']").show();
		$("#ques-text").html(question);
		$("#sq-stars").show();
		if(questionDetails.customerResponse!=undefined && !isNaN(parseInt(questionDetails.customerResponse))){
			increaseOpacityOfStars(parseInt(questionDetails.customerResponse));
			$("#next-star").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-smiles") {
		$("div[data-ques-type='smiley']").show();
		$("#ques-text-smiley").html(question);
		$("#sq-smiles").show();
		if(questionDetails.customerResponse!=undefined && !isNaN(parseInt(questionDetails.customerResponse))){
			increaseOpacityOfStars(parseInt(questionDetails.customerResponse));
			$("#next-smile").removeClass("btn-com-disabled");
		}
	} else if (questionType == "sb-range-scale") {
		$("div[data-ques-type='scale']").show();
		$("#ques-text-scale").html(question);
		$("#sq-stars").show();
	} else if (questionType == "sb-sel-mcq") {
		$("div[data-ques-type='mcq']").show();
		$("#mcq-ques-text").html(question);
		$("#skip-ques-mcq").show();
		$("#next-mcq").show();
		$("#next-mcq").removeClass("btn-com-disabled");
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
		if(questionDetails.customerResponse!=undefined)
			$("#text-area").html(questionDetails.customerResponse);
	} else if (questionType == "sb-master") {
		$("div[data-ques-type='smiley-text-final']").show();
		$("#text-area").hide();
		$("#smiles-final").show();
		$("#ques-text-textarea").html(question);
	}
	togglePrevAndNext();
	if (qno == questions.length - 1) {
		$("#next-mcq").addClass("btn-com-disabled");
		$("#next-smile").addClass("btn-com-disabled");
		$("#next-star").addClass("btn-com-disabled");
		$("#next-textarea-smiley").addClass("btn-com-disabled");
		$("#skip-ques-mcq").hide();
	}
	$(".sq-main-txt").html("Survey for " + agentName);
}

function togglePrevAndNext(){
	if (qno == 0) {
		$("#prev-star").addClass("btn-com-disabled");
		$("#prev-smile").addClass("btn-com-disabled");
		$("#prev-scale").addClass("btn-com-disabled");
		$("#prev-mcq").addClass("btn-com-disabled");
		$("#prev-textarea-smiley").addClass("btn-com-disabled");
	} else {
		$("#prev-star").removeClass("btn-com-disabled");
		$("#prev-smile").removeClass("btn-com-disabled");
		$("#prev-scale").removeClass("btn-com-disabled");
		$("#prev-mcq").removeClass("btn-com-disabled");
		$("#prev-textarea-smiley").removeClass("btn-com-disabled");
	}
}

function retakeSurveyRequest(){
	var payload = {
			"customerEmail" : customerEmail,
			"agentId" : agentId,
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName
	};
	callAjaxGetWithPayloadData(window.location.origin + surveyUrl + 'restartsurvey', '', payload, true);
	$('#overlay-toast').html('Mail sent to your registered email id for retaking the survey for '+agentName);
	showToast();
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
		url : window.location.origin + surveyUrl + "data/storeAnswer",
		type : "GET",
		data : payload,
		dataType : "JSON",
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				if (qno == (questions.length - 1)) {
					var parsed = data.responseJSON;
					for ( var x in parsed) {
						swearWords.push(parsed[x]);
					}
				}
			}
		},
		error : function(e) {
			console.error("error : ");
		}
	});
}

function updateCustomerResponse(feedback) {
	var success = false;
	isAbusive = false;
	var feedbackArr = feedback.split(" ");
	for (var i = 0; i < feedbackArr.length; i++) {
		if ($.inArray(feedbackArr[i], swearWords) != -1) {
			isAbusive = true;
		}
	}
	var payload = {
		"mood" : mood,
		"feedback" : feedback,
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"isAbusive" : isAbusive
	};
	questionDetails.customerResponse = customerResponse;
	$.ajax({
		url : window.location.origin + surveyUrl + "data/storeFeedback",
		type : "GET",
		data : payload,
		dataType : "TEXT",
		success : function(data) {
			if (data != undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				console.log(data);
			}
		},
		error : function(e) {
			console.error("error : "+e);
		}
	});
}

function showFeedbackPage(mood) {
	$("div[data-ques-type]").hide();
	$("div[data-ques-type='smiley-text-final']").show();
	$("#text-area").show();
	$("#text-area").val("");
	$("#smiles-final").hide();
	$("#next-textarea-smiley").html("Finish");
	$("#next-textarea-smiley").removeClass("btn-com-disabled");
	isSmileTypeQuestion = false;
	switch (mood) {
	case "Great":
		question = happyText;
		$("#ques-text-textarea").html(question);
		var currResponse = 0;
		var counter = 0;
		for(var i=0;i<questions.length;i++){
			var currQuestion = questions[i];
			if((currQuestion.questionType=='sb-range-smiles')||(currQuestion.questionType=='sb-range-scale')
					||(currQuestion.questionType=='sb-range-star')){
				if(!isNaN(parseInt(currQuestion.customerResponse))){
					counter++;
					currResponse += parseInt(currQuestion.customerResponse);
				}
			}
		}
		rating = currResponse/(counter);
		rating = parseFloat(rating).toFixed(2);
		if((rating >= autoPostScore) && (Boolean(autoPost) == true))
			$("#pst-srvy-div").show();
		break;
	case "OK":
		question = neutralText;
		$("#ques-text-textarea").html(question);
		break;
	case "Unpleasant":
		question = sadText;
		$("#ques-text-textarea").html(question);
		break;
	}
	$("#prev-textarea-smiley").removeClass("btn-com-disabled");
}

/*
 * This is used to render all the possible choices for an MCQ.
 */
function paintMcqAnswer(answer) {
	var divToPopulate;
	customerResponse = questionDetails.customerResponse;
	if (answer == customerResponse) {
		divToPopulate = "<div data-answer='" + answer
				+ "' class='sq-mcq-item clearfix'>"
				+ "<div class='sq-mcq-chk-wrapper float-left'>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-on'>"
				+ "</div>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-off hide'>"
				+ "</div>" + "</div>"
				+ "<div class='sq-mcq-ans-wrapper float-left'>" + answer
				+ "</div></div>";
	} else {
		divToPopulate = "<div data-answer='" + answer
				+ "' class='sq-mcq-item clearfix'>"
				+ "<div class='sq-mcq-chk-wrapper float-left'>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-on hide'>"
				+ "</div>"
				+ "<div class='float-left sq-mcq-chk st-mcq-chk-off'>"
				+ "</div>" + "</div>"
				+ "<div class='sq-mcq-ans-wrapper float-left'>" + answer
				+ "</div></div>";
	}
	return divToPopulate;
}

function paintListOptions(agentName) {
	var divToPopulate = "<option value='select'>--Select an Option--"
			+ "<option value='transacted'>Transacted with " + agentName
			+ "<option value='enquired'>Enquired with " + agentName;
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

function paintRangeScale() {
	if (questionDetails.questionType == "sb-range-scale") {
		var value = parseInt(questionDetails.customerResponse);
		if (value == $('.sq-pts-red').html()) {
			$('.pts-hover-1').addClass('showHoverTab');
		} else if (value == $('.sq-pts-org').html()) {
			$('.pts-hover-2').addClass('showHoverTab');
		} else if (value == $('.sq-pts-lgreen').html()) {
			$('.pts-hover-3').addClass('showHoverTab');
		} else if (value == $('.sq-pts-military').html()) {
			$('.pts-hover-4').addClass('showHoverTab');
		} else if (value == $('.sq-pts-dgreen').html()) {
			$('.pts-hover-5').addClass('showHoverTab');
		}
	}
}

function showMasterQuestionPage(){
	if (isSmileTypeQuestion) {
		showFeedbackPage(mood);
	} else {
		//if ($('#pst-srvy-div').is(':visible'))
		//	autoPost = $('#post-survey').is(":checked");
		var feedback = $("#text-area").val();
		if($('#shr-post-chk-box').hasClass('bd-check-img') && (rating >= autoPostScore) && (Boolean(autoPost) == true)){
			postToSocialMedia(feedback);
			$('#social-post-lnk').show();
			if(yelpEnabled && (mood=='Great'))
				$('#ylp-btn').show();
			else
				$('#ylp-btn').hide();
			if(googleEnabled && (mood=='Great'))
				$('#ggl-btn').show();
			else
				$('#ggl-btn').hide();
		}
		updateCustomerResponse(feedback);
		$("div[data-ques-type]").hide();
		$("div[data-ques-type='error']").show();
		$('#content-head').html('Survey Completed');
		$('#content').html("Congratulations! You have completed survey for "
			+ agentName+ ".\nThanks for your participation.");
	}
	return;
}

function postToSocialMedia(feedback){
	var success = false;
	var payload = {
		"agentId" : agentId,
		"firstName" : firstName,
		"lastName" : lastName,
		"agentName" : agentName,
		"rating" : rating,
		"customerEmail" : customerEmail,
		"feedback" : feedback,
		"agentProfileLink" : agentProfileLink
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "posttosocialnetwork",
		type : "GET",
		dataType : "TEXT",
		data : payload,
		success : function(data) {
				success = true;
		},
		complete : function(data) {
			if (success) {
				
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function updateSharedOn(socialSite, agentId, customerEmail){
	var success = false;
	var payload = {
		"agentId" : agentId,
		"customerEmail" : customerEmail,
		"socialSite" : socialSite
	};
	$.ajax({
		url : window.location.origin + surveyUrl + "updatesharedon",
		type : "GET",
		dataType : "TEXT",
		data : payload,
		success : function(data) {
				success = true;
		},
		complete : function(data) {
			if (success) {
				
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast();
		}
	});
}

function reduceOpacityOfStars(){
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < 5) {
			$(this).removeClass('sq-full-star-click');
			$(this).addClass('opacity-red');
		}
	});
}

function reduceOpacityOfSmiles(){
	$('#sq-smiles').find('.sq-smile').each(
		function(index) {
			if (index < 5) {
				$(this).removeClass('sq-full-smile-click');
				$(this).addClass('opacity-red');
			}
	});
}

function increaseOpacityOfStars(value){
	$('#sq-stars').find('.sq-star').each(function(index) {
		if (index < value) {
			$(this).addClass('sq-full-star-click');
			$(this).removeClass('opacity-red');
		}
	});
}

function increaseOpacityOfSmiles(value){
	$('#sq-smiles').find('.sq-smile').each(
		function(index) {
			if (index < value) {
				$(this).addClass('sq-full-smile-click');
				$(this).removeClass('opacity-red');
			}
	});
}

function clearForm(){
	$('#firstName').val('');
	$('#lastName').val('');
	$('#email').val('');
	$('#captcha-text').val('');
}

// Starting click events.

// Code to be executed on click of stars of rating question.

$('.sq-star').click(function() {
	$(this).parent().find('.sq-star').removeClass('sq-full-star');
	$(this).parent().find('.sq-star').removeClass('sq-full-star-click');
	var starVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < starVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-star-click');
		}
		else{
			if(!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-star").removeClass("btn-com-disabled");
	}
	storeCustomerAnswer(starVal);
});

$('.sq-star').hover(function() {
	var smileVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
		}
	});
}, function() {
	var smileVal = $(this).attr('star-no');
	$(this).parent().find('.sq-star').each(function(index) {
		if (index < smileVal) {
			if ($(this).hasClass('sq-full-star-click')) {
				$(this).removeClass('opacity-red');
			} else {
				$(this).addClass('opacity-red');
			}
		}
	});
});

// Code to be executed on click of next for all types of questions.

$('.sq-np-item-next')
		.click(
				function() {
					if (questionDetails.questionType == "sb-master") {
						showMasterQuestionPage();
					}

					if (questionDetails.questionType == "sb-sel-mcq"
							&& customerResponse != undefined) {
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-sel-desc") {
						customerResponse = $("#text-area").val();
						if (customerResponse == undefined) {
							customerResponse = "";
						}
						storeCustomerAnswer(customerResponse);
					} else if (questionDetails.questionType == "sb-range-star") {
						reduceOpacityOfStars();
						if ($('#next-star').hasClass("btn-com-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-smiles") {
						reduceOpacityOfSmiles();
						if ($('#next-smile').hasClass("btn-com-disabled")) {
							$('#overlay-toast')
									.html(
											'Please answer the question. You can not skip a rating question.');
							showToast();
							return;
						}
					} else if (questionDetails.questionType == "sb-range-scale") {
						if ($('#next-scale').hasClass("btn-com-disabled")) {
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
					$(".sq-smile").removeClass('sq-full-smile');
					qno++;
					paintSurveyPageFromJson();

					if (questionDetails.questionType == "sb-range-star") {
						var starVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(starVal)) {
							$("#next-star").removeClass("btn-com-disabled");
							$('#sq-stars').find('.sq-star').each(
									function(index) {
										if (index < starVal) {
											$(this).addClass('sq-full-star-click');
											$(this).removeClass('opacity-red');
										}
									});
						}
					}
					if (questionDetails.questionType == "sb-range-smiles") {
						var smileVal = parseInt(questionDetails.customerResponse);
						if (!isNaN(smileVal)) {
							$("#next-smile").removeClass("btn-com-disabled");
							$('#sq-smiles').find('.sq-smile').each(
									function(index) {
										if (index < smileVal) {
											$(this).addClass('sq-full-smile-click');
											$(this).removeClass('opacity-red');
										}
									});
						}
					}
					if (questionDetails.questionType == "sb-range-scale") {
						$("#next-scale").removeClass("btn-com-disabled");
						paintRangeScale();
					}
					if (questionDetails.questionType == "sb-sel-mcq") {
						if(questionDetails.customerResponse==undefined || questionDetails.customerResponse=="")
							customerResponse = "";
					}

				});

// Code to be executed on click of previous for star and smile questions.

$('.sq-np-item-prev').click(function() {
	$("#pst-srvy-div").hide();
	if (qno == 0) {
		return;
	}
	$("#next-textarea-smiley").html("Next");
	$(".sq-star").removeClass('sq-full-star');
	$(".sq-smile").removeClass('sq-full-smile');
	if (isSmileTypeQuestion)
		qno--;
	isSmileTypeQuestion = true;
	paintSurveyPageFromJson();
	if (questionDetails.questionType == "sb-range-star") {
		reduceOpacityOfStars();
		var starVal = parseInt(questionDetails.customerResponse);
		$('#sq-stars').find('.sq-star').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-star-click');
				$(this).removeClass('opacity-red');
			}
		});
	}
	paintRangeScale();
	if (questionDetails.questionType == "sb-range-smiles") {
		reduceOpacityOfSmiles();
		var starVal = parseInt(questionDetails.customerResponse);
		$('#sq-smiles').find('.sq-smile').each(function(index) {
			if (index < starVal) {
				$(this).addClass('sq-full-smile-click');
				$(this).removeClass('opacity-red');
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
	$("#next-star").removeClass("btn-com-disabled");
	$("#next-smile").removeClass("btn-com-disabled");
	$("#next-scale").removeClass("btn-com-disabled");
	$("#next-textarea-smiley").removeClass("btn-com-disabled");
});

/* Click event on grey smile. */
$('.sq-smile').click(function() {
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile');
	$(this).parent().find('.sq-smile').removeClass('sq-full-smile-click');
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
			$(this).addClass('sq-full-smile-click');
		}
		else{
			if(!$(this).hasClass('opacity-red'))
				$(this).addClass('opacity-red');
		}
	});
	if (qno != questions.length - 1) {
		$("#next-smile").removeClass("btn-com-disabled");
	}
	storeCustomerAnswer(smileVal);
	$("#next-star").removeClass("btn-com-disabled");
});

$('.sq-smile').hover(function() {
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			$(this).removeClass('opacity-red');
		}
	});
}, function() {
	var smileVal = $(this).attr('smile-no');
	$(this).parent().find('.sq-smile').each(function(index) {
		if (index < smileVal) {
			if ($(this).hasClass('sq-full-smile-click')) {
				$(this).removeClass('opacity-red');
			} else {
				$(this).addClass('opacity-red');
			}
		}
	});
});

$('.sq-happy-smile').click(function() {
	// Update customer's mood in db and ask for cutomer's kind words.
	mood = "Great";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$('.sq-happy-smile').removeClass('opacity-red');
	$('.sq-neutral-smile').addClass('opacity-red');
	$('.sq-sad-smile').addClass('opacity-red');
});
$('.sq-neutral-smile').click(function() {
	// Update customer's mood in db and ask for feedback that could have made
	// him happy.
	mood = "OK";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$('.sq-neutral-smile').removeClass('opacity-red');
	$('.sq-happy-smile').addClass('opacity-red');
	$('.sq-sad-smile').addClass('opacity-red');
});
$('.sq-sad-smile').click(function() {
	// Update customer's mood in db and ask what went wrong during the entire
	// course.
	mood = "Unpleasant";
	$('#next-textarea-smiley').removeClass("btn-com-disabled");
	isSmileTypeQuestion = true;
	$('.sq-sad-smile').removeClass('opacity-red');
	$('.sq-neutral-smile').addClass('opacity-red');
	$('.sq-happy-smile').addClass('opacity-red');
});

$('#start-btn').click(function() {
	firstName = $('#firstName').val().trim();
	lastName = $('#lastName').val().trim();
	var email = $('#email').val().trim();
	var grecaptcharesponse = $('#g-recaptcha-response').val();
	// var recaptcha_challenge_field = $('#recaptcha_challenge_field').val();
	
	if (!validateUserFirstName('firstName')) {
		$('#overlay-toast').html('Please enter valid First Name!');
		showToast();
		return;
	}
	if (!validateUserEmailId('email')) {
		$('#overlay-toast').html('Please enter valid Email Id!');
		showToast();
		return;
	}
	
	var agentEmail = $('#prof-container').attr("data-agent-email");
	if(agentEmail.toUpperCase() == email.toUpperCase()){
		$('#overlay-toast').html('Agents can not take survey for themselves!');
		showToast();
		return;
	}
	
	/*if (!validateUserEmailId('email')) {
		$('#overlay-toast').html('Please enter valid Email Id!');
		showToast();
		return;
	}*/
	
	
	var agentId = $('#prof-container').attr("data-agentId");
	var agentName = $('#prof-container').attr("data-agentName");
	var e = document.getElementById("cust-agnt-rel");
	//var relationship = e.options[e.selectedIndex].value;
	initSurvey(firstName, lastName, email, agentId, agentName,
			grecaptcharesponse);
	
	// Google analytics for reviews
	ga('send', {
		'hitType': 'event',
		'eventCategory': 'review',
		'eventAction': 'click',
		'eventLabel': 'Reviews',
		'eventValue': agentId
	});
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
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-org').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-2').addClass('showHoverTab');
	var answer = $('.sq-pts-org').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-lgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-3').addClass('showHoverTab');
	var answer = $('.sq-pts-lgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-military').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-4').addClass('showHoverTab');
	var answer = $('.sq-pts-military').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('.sq-pts-dgreen').click(function() {
	$('.sq-pts-item-hover').removeClass('showHoverTab');
	$('.pts-hover-5').addClass('showHoverTab');
	var answer = $('.sq-pts-dgreen').html();
	storeCustomerAnswer(answer);
	$("#next-scale").removeClass("btn-com-disabled");
});

$('#ylp-btn').click(function(e) {
	//e.stopImmediatePropagation();
	var yelpElement = document.getElementById('ylp-btn');
	shareOnYelp(agentId, window.location.origin+"/rest/survey/", yelpElement);
	updateSharedOn("yelp", agentId, customerEmail);
});

$('#ggl-btn').click(function(e) {
	//e.stopImmediatePropagation();
	var googleElement = document.getElementById('ggl-btn');
	shareOnGooglePlus(agentId, window.location.origin+"/rest/survey/", googleElement);
	updateSharedOn("google", agentId, customerEmail);
});

$('#shr-post-chk-box').click(function(){
	if($('#shr-post-chk-box').hasClass('bd-check-img-checked')){
		$('#shr-post-chk-box').removeClass('bd-check-img-checked');
		autoPost = true;
	}
	else{
		$('#shr-post-chk-box').addClass('bd-check-img-checked');
		autoPost = false;
	}
});
