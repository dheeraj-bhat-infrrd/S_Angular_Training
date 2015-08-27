/**
 * JIRA:SS-24 BY RM02
 * Holds the javascript functions to be used commonly in almost all files in the
 * application
 */


//Function to redirect to login page if session time out
function redirectToLoginPageOnSessionTimeOut(status) {
	window.location = window.location.origin + '/login.do?s=sessionerror';
}


/**
 * Generic function to be used for making ajax get calls
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxGET(url, callBackFunction, isAsync) {
	console.log("ajax get called for url :" + url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		dataType : "html",
		async : isAsync,
		success : callBackFunction,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

/**
 * Generic function to be used for making ajax get calls
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxPOST(url, callBackFunction, isAsync) {
	console.log("ajax post called for url :" + url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "POST",
		dataType : "html",
		async : isAsync,
		success : callBackFunction,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

/**
 * Generic function to be used for making ajax get calls with datatype text and formdata
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxPOSTWithTextData(url, callBackFunction, isAsync, formData) {
	console.log("ajax post called for url :" + url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "POST",
		dataType : "text",
		contentType : false,
		processData : false,
		cache : false,
		data : formData,
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
			},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

/**
 * Generic function to be used for making ajax get calls with datatype text and formdata
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxGETWithTextData(url, callBackFunction, isAsync, formData) {
	console.log("ajax post called for url :" + url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		dataType : "text",
		data : formData,
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}


/**
 * Generic function to be used for making form submission with ajax post
 * 
 * @param url
 * @param callBackFunction
 * @param formId
 */
function callAjaxFormSubmit(url, callBackFunction, formId) {
	console.log("ajax form submit called for url :" + url + " and formId : " + formId);
	var $form = $("#" + formId);
	var payLoad = $form.serialize();
	console.log("payload is --" + payLoad);
	$.ajax({
		url : url,
		type : "POST",
		data : payLoad,
		success : callBackFunction,
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

/**
 * Generic function to be used for making form submission with ajax post
 */
function redirectErrorpage(){
	window.open(window.location.origin + "/errorpage.do",'_self');
}

/**
 * Generic method to use for post ajax request with payload data
 * 
 * @param url
 * @param callBackFunction
 * @param payload
 */
function callAjaxPostWithPayloadData(url, callBackFunction, payload, isAsync){
	console.log("callAjaxPostWithPayloadData for payload--"+payload+" url--"+url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "POST",
		data : payload,
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

function callAjaxGetWithPayloadData(url, callBackFunction, payload,isAsync){
	console.log("callAjaxGetWithPayloadData for payload--"+payload+" url--"+url);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		data : payload,
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}

/*function changeRatingPattern(rating, ratingParent) {
	var counter = 0;
	ratingParent.children().each(function() {
		$(this).addClass("icn-no-star");
		$(this).removeClass("icn-half-star");
		$(this).removeClass("icn-full-star");

		if (rating >= counter) {
			if (rating - counter >= 1) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-full-star");
			} else if (rating - counter == 0.5) {
				$(this).removeClass("icn-no-star");
				$(this).addClass("icn-half-star");
			}
		}
		counter++;
	});
}*/

function changeRatingPattern(rating, ratingParent, isOverallRating, source) {
	var ratingIntVal = 0;

	if (ratingIntVal % 1 == 0) {
		ratingIntVal = parseInt(rating);
	} else {
		ratingIntVal = parseInt(rating) + 1;
	}

	if (ratingIntVal == 0) {
		ratingIntVal = 1;
	}

	var roundedFloatingVal = parseFloat(rating).toFixed(3);
	var ratingImgHtml = "";
	if(source != undefined && source == "Zillow"){
		ratingImgHtml = "<div class='rating-image float-left icn-zillow' title='Zillow'></div>";
	}else {
		ratingImgHtml = "<div class='rating-image float-left smiley-rat-" + ratingIntVal + "'></div>";		
	}
	
	var ratingValHtml = "<div class='rating-rounded float-left'>" + roundedFloatingVal + "</div>";
	if (isOverallRating) {
		ratingValHtml = "<div class='rating-rounded float-left'>" + roundedFloatingVal + " - </div>";
	}

	ratingParent.html('');
	ratingParent.append(ratingImgHtml).append(ratingValHtml);
}

/**
 * function for adding delay to a function call
 */
var delay = (function() {
	var timer = 0;
	return function(callback, ms) {
		clearTimeout(timer);
		timer = setTimeout(callback, ms);
	};
})();

function shareOnFacebook(firstName, lastName, agentName, review, score, agentId){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score,
			"agentId" : agentId
	};
	
	$.ajax({
		url : "./postonfacebook.do",
		type : "GET",
		dataType : "html",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				data = data.responseText;
				if(data=='true')
					$('#overlay-toast').html('No facebook account setup in hierarchy to share.');
				else
					$('#overlay-toast').html('Successfully shared on facebook.');
				showToast();
			}
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast(e.responseText);
		}
	});
}

function shareOnTwitter(firstName, lastName, agentName, review, score, agentId){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score,
			"agentId" : agentId
		};
		$.ajax({
			url : "./postontwitter.do",
			type : "GET",
			dataType : "html",
			data : payload,
			success : function(data) {
				if (data.errCode == undefined)
					success = true;
			},
			complete : function(data) {
				if (success) {
					data = data.responseText;
					if(data=='true')
						$('#overlay-toast').html('No twitter account setup in hierarchy to share.');
					else
						$('#overlay-toast').html('Successfully shared on twitter.');
					showToast();
				}
			},
			error : function(e) {
				if(e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast(e.responseText);
			}
		});
}

function shareOnLinkedin(firstName, lastName, agentName, review, score, agentId){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score,
			"agentId" : agentId
		};
		$.ajax({
			url : "./postonlinkedin.do",
			type : "GET",
			dataType : "html",
			data : payload,
			success : function(data) {
				if (data.errCode == undefined)
					success = true;
			},
			complete : function(data) {
				if (success) {
					data = data.responseText;
					if(data=='true')
						$('#overlay-toast').html('No linkedin account setup in hierarchy to share.');
					else
						$('#overlay-toast').html('Successfully shared on linkedin.');
					showToast(data);
				}
			},
			error : function(e) {
				if(e.status == 504) {
					redirectToLoginPageOnSessionTimeOut(e.status);
					return;
				}
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast(e.responseText);
			}
		});
}

function shareOnYelp(agentId, location, yelpElement){
	var success= false;
	var payload = {
			"agentId" : agentId
	};
	$.ajax({
		url : location + "getyelplinkrest",
		type : "GET",
		dataType : "json",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			data = data.responseJSON;
			if (success) {
				if(data.relativePath != undefined)
					if (yelpElement == undefined) {
						window.open(data.relativePath);
					} else {
						yelpElement.href = data.relativePath;
					}
				else {
					$('#overlay-toast').html('Please setup your Yelp account to share.');
					showToast();
				}
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast(e.responseText);
		}
	});
}

function shareOnGooglePlus(agentId, location, googleElement){
	var success = false;
	var payload = {
		"agentId" : agentId
	};
	$.ajax({
		url : location + "getgooglepluslinkrest",
		type : "GET",
		dataType : "json",
		data : payload,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				data = data.responseJSON;
				if (data.host != undefined && data.profileServer != undefined && data.relativePath != undefined) {
					if (googleElement == undefined) {
						window.open(data.host + data.profileServer + data.relativePath);
					}
					else {
						googleElement.href = data.host + data.profileServer + data.relativePath;
					}
				}
				else {
					$('#overlay-toast').html('Please setup your Google+ account to share.');
					showToast();
				}
			}
		},
		error : function(e) {
			if (e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast(e.responseText);
		}
	});
}

// Function to open forgot password page
function openForgotPasswordPage(){
	window.location.href = "./forgotpassword.do";
}

// Dashboard popup click functions
function openAuthPage(socialNetwork) {
	window.open("./socialauth.do?social=" + socialNetwork, "Authorization Page", "width=800,height=600,scrollbars=yes");
}
function openAuthPageRegistration(socialNetwork) {
	window.open("./socialauth.do?social=" + socialNetwork + "&flow=registration", "Authorization Page", "width=600,height=600,scrollbars=yes");
}
function openAuthPageDashboard(socialNetwork, columnName, columnValue) {
	window.open("./socialauth.do?social=" + socialNetwork + "&columnName="
			+ columnName + "&columnValue=" + columnValue, "Authorization Page",
			"width=800,height=600,scrollbars=yes");
}
function postOnSocialNetworkOnce(socialNetwork, firstName, lastName, agentName, rating, review) {
	window.open("./social/socialauthinsession?social=" + socialNetwork
			+ "&firstName=" + firstName + "&lastName=" + lastName
			+ "&agentName=" + agentName + "&rating=" + rating + "&review="
			+ review, "Authorization Page",
			"width=800,height=600,scrollbars=yes");
}

//Disconnect social media
function disconnectSocialMedia(socialMedia) {
	if($('div[data-social="'+socialMedia+'"]').text() == undefined || $('div[data-social="'+socialMedia+'"]').text() == ''){
		return;
	}
	
	var payload = {
		"socialMedia" : socialMedia	
	};
	
	callAjaxPostWithPayloadData("/disconnectsocialmedia.do", function(data) {
		if(data == "success"){
			$('div[data-social="'+socialMedia+'"]').html('');
			$('div[data-social="'+socialMedia+'"]').parent().find('.social-media-disconnect').addClass('social-media-disconnect-disabled').removeAttr("onclick").removeAttr("title");
			$('#overlay-toast').html('Successfully disconnected ' + socialMedia);
			showToast();
		} else {
			$('#overlay-toast').html('Some error occurred while disconnecting ' + socialMedia);
			showToast();
		}
	}, payload, true);	
}


function showProfileLinkInEditProfilePage(source, profileUrl){
	if(source=='facebook'){
		$('#edt-prof-fb-lnk').html(profileUrl);
	}
	else if(source=='twitter'){
		$('#edt-prof-twt-lnk').html(profileUrl);
	}
	else if(source=='linkedin'){
		$('#edt-prof-linkedin-lnk').html(profileUrl);
	}
	else if(source=='google'){
		$('#edt-prof-ggl-lnk').html(profileUrl);
	}
}

// Send Survey Agent
$(document).on('input', '#wc-review-table-inner[data-role="agent"] input', function() {
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div class="wc-review-tr clearfix">'
			+ '<div class="wc-review-tc1 float-left"><input class="wc-review-input wc-review-fname"></div>'
			+ '<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-lname"></div>'
			+ '<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-email"></div>'
			+ '<div class="wc-review-tc4 float-left"><div class="wc-review-rmv-icn hide"></div></div>'
		+ '</div>';
		parentDiv.after(htmlData);
		
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		
		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);
	}
});

//Send Survey Admin
$(document).on('input', '#wc-review-table-inner[data-role="admin"] input', function() {
	var parentDiv = $(this).parent().parent();
	if (parentDiv.is(':last-child')) {
		var htmlData = '<div class="wc-review-tr clearfix">'
			+ '<div class="wc-review-tc1 float-left pos-relative"><input data-name="agent-name" class="wc-review-input wc-review-agentname"></div>'
			+ '<div class="wc-review-tc2 float-left"><input class="wc-review-input wc-review-fname"></div>'
			+ '<div class="wc-review-tc3 float-left"><input class="wc-review-input wc-review-lname"></div>'
			+ '<div class="wc-review-tc4 float-left"><input class="wc-review-input wc-review-email"></div>'
			+ '<div class="wc-review-tc5 float-left"><div class="wc-review-rmv-icn hide"></div></div>'
		+ '</div>';
		parentDiv.after(htmlData);
		
		// enable remove button
		if ($('#wc-review-table-inner').children().length > 2) {
			$('.wc-review-rmv-icn').show();
		}
		
		// setting up perfect scrollbar
		setTimeout(function() {
			$('#wc-review-table').perfectScrollbar();
			$('#wc-review-table').perfectScrollbar('update');
		}, 1000);
	}
});

$(document).on('click', '.wc-review-rmv-icn', function() {
	var parentDiv = $('#wc-review-table-inner');
	
	// disable remove button
	if (parentDiv.children().length <= 3) {
		$('.wc-review-rmv-icn').hide();
	}
	$(this).parent().parent().remove();

	// setting up perfect scrollbar
	setTimeout(function() {
		$('#wc-review-table').perfectScrollbar();
		$('#wc-review-table').perfectScrollbar('update');
	}, 1000);
});

$(document).on('click', '#wc-send-survey', function() {
	var receiversList = [];
	var agentId = undefined;
	var columnName = undefined;
	var firstname = "";
	var lastname = "";
	var idx=0;
	var exit = false;
	$('#wc-review-table-inner').children().each(function() {
		if (!$(this).hasClass('wc-review-hdr')) {
			var dataName = $(this).find('input.wc-review-agentname').first().attr('data-name');
			if (dataName == 'agent-name') {
				agentId = $(this).find('input.wc-review-agentname').first().attr('agent-id');

				/*var name = $(this).find('input.wc-review-custname').first().val();
				if(name!=undefined){
				var name = $(this).find('input.wc-review-custname').first().val();
				if (name != undefined) {
					var nameParts = name.split(" ");
					if (nameParts.length == 1) {
						firstname = name;
					} else {
						for (var i = 0; i < nameParts.length-1; i++) {
							firstname = firstname + nameParts[i];
						}
						lastname = nameParts[nameParts.length - 1];
					}
				}*/
				if (idx == 0) {
					columnName = $(this).find('input.wc-review-agentname').first().attr('column-name');
					idx ++;
				}
			}
			
			firstname = $(this).find('input.wc-review-fname').first().val();
			lastname = $(this).find('input.wc-review-lname').first().val();
			
			var emailId = $(this).find('input.wc-review-email').first().val();
			
			if(firstname == "" && emailId != ""){
				$('#overlay-toast').html('Please enter Firstname for all the customer');
				showToast();
				exit = true;
				return false;
			} else if (agentId != undefined && firstname == ""){
				$('#overlay-toast').html('Please enter Firstname for all the customer');
				showToast();
				exit = true;
				return false;
			}
			if (emailRegex.test(emailId)) {
				var receiver = new Object();
				receiver.firstname = firstname;
				receiver.lastname = lastname;
				receiver.emailId = emailId;
				if (dataName == 'agent-name') {
					receiver.agentId = agentId;
					if(agentId == undefined){
						$('#overlay-toast').html('Please enter Agent name for all survey requests');
						showToast();
						exit = true;
						return false;					
					}
				}
				receiversList.push(receiver);
			} else if(firstname != ""){
				$('#overlay-toast').html('Please enter valid email for ' + firstname);
				showToast();
				exit = true;
				return false;
			}
		}
	});

	if(exit){
		exit = false;
		return false;
	}
	
	//Check if recievers list empty
	if(receiversList.length == 0){
		$('#overlay-toast').html('Add customers to send survey request!');
		showToast();
		exit = false;
		return false;
	}
	
	receiversList = JSON.stringify(receiversList);
	var payload = {
		"receiversList" : receiversList,
		"source" : 'agent'
	};
	if (columnName != undefined) {
		payload = {
			"receiversList" : receiversList,
			"source" : 'admin',
			"columnName" : columnName,
		};
	}

	//loadDisplayPicture();
	$(this).closest('.overlay-login').hide();
	showDisplayPic();
	
	callAjaxPostWithPayloadData("./sendmultiplesurveyinvites.do", function(data) {
		$('#overlay-toast').html('Survey request sent successfully!');
		showToast();
		$('body').removeClass('body-no-scroll');
	}, payload);
});

$(document).on('click', '#wc-skip-send-survey', function() {
	$('#overlay-send-survey').html('');
	$('body').removeClass('body-no-scroll');
});

function sendSurveyInvitation() {
	$('body').addClass('body-no-scroll');
	callAjaxGET("./sendsurveyinvitation.do", function(data) {
		$('#overlay-send-survey').html(data);
		if ($("#welcome-popup-invite").length) {
			$('#overlay-send-survey').removeClass("hide");
			$('#overlay-send-survey').show();
		}
	}, true);
}

function sendSurveyInvitationAdmin(columnName, columnValue) {
	$('body').addClass('body-no-scroll');
	var payload = {
			"columnName" : columnName,
			"columnValue" : columnValue
	};
	callAjaxGetWithPayloadData("./sendsurveyinvitationadmin.do", function(data) {
		$('#overlay-send-survey').html(data);
		if ($("#welcome-popup-invite").length) {
			$('#overlay-send-survey').removeClass("hide");
			$('#overlay-send-survey').show();
		}
	}, payload, true);
}

function linkedInDataImport() {
	$('body').addClass('body-no-scroll');
	callAjaxGET("./linkedindataimport.do", function(data) {
		$('#overlay-linkedin-import').html(data);
		if ($("#welocome-step1").length) {
			$('#overlay-linkedin-import').removeClass("hide");
			$('#overlay-linkedin-import').show();
		}
	}, true);
}

function returnValidWebAddress(url) {
	if (url && !url.match(/^http([s]?):\/\/.*/)) {
		url = 'http://' + url;
	}
	return url;
}

function linkify(inputText) {
    var replacedText, replacePattern1, replacePattern2, replacePattern3;

    //URLs starting with http://, https://, or ftp://
    replacePattern1 = /(\b(https?|ftp):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/gim;
    replacedText = inputText.replace(replacePattern1, '<a href="$1" target="_blank">$1</a>');

    //URLs starting with "www." (without // before it, or it'd re-link the ones done above).
    replacePattern2 = /(^|[^\/])(www\.[\S]+(\b|$))/gim;
    replacedText = replacedText.replace(replacePattern2, '$1<a href="http://$2" target="_blank">$2</a>');

    //Change email addresses to mailto:: links.
    replacePattern3 = /(([a-zA-Z0-9\-\_\.])+@[a-zA-Z\_]+?(\.[a-zA-Z]{2,6})+)/gim;
    replacedText = replacedText.replace(replacePattern3, '<a href="mailto:$1">$1</a>');

    return replacedText;
}

function initializeCityLookup(searchData, elementId) {
	$('#' + elementId).autocomplete({
		minLength : 0,
		source : searchData,
		focus : function(event, ui) {
			event.stopPropagation();
		},
		select : function(event, ui) {
			event.stopPropagation();
		},
		open : function() {
			$('.ui-autocomplete').perfectScrollbar({
				suppressScrollX : true
			});
			$('.ui-autocomplete').perfectScrollbar('update');
		}
	}).keydown(function(e) {
		if (e.keyCode != $.ui.keyCode.TAB)
			return;

		e.keyCode = $.ui.keyCode.DOWN;
		$(this).trigger(e);

		e.keyCode = $.ui.keyCode.ENTER;
		$(this).trigger(e);
	});
}

function getUniqueCitySearchData(data) {
	cityLookupList = JSON.parse(data);
	var searchData = [];
	for (var i = 0; i < cityLookupList.length; i++) {
		searchData[i] = toTitleCase(cityLookupList[i].cityname);
	}

	var uniqueSearchData = searchData.filter(function(itm, i, a) {
		return i == a.indexOf(itm);
	});
	return uniqueSearchData;
}

function showStateCityRow(parentId, elementId) {
	$('#'+parentId).show();
	var stateVal = $('#'+elementId).attr('data-value');
	if (!stateList) {
		callAjaxGET("./getusstatelist.do", function(data) {
			stateList = JSON.parse(data);
			for (var i = 0; i < stateList.length; i++) {
				if (stateVal == stateList[i].statecode) {
					$('#'+elementId).append(
							'<option data-stateid=' + stateList[i].id
									+ ' selected >' + stateList[i].statecode
									+ '</option>');
				} else {
					$('#'+elementId).append(
							'<option data-stateid=' + stateList[i].id + '>'
									+ stateList[i].statecode + '</option>');
				}
			}
		}, true);
	} else {

		if ($('#'+elementId).children('option').size() == 1) {
			for (var i = 0; i < stateList.length; i++) {
				if (stateVal == stateList[i].statecode) {
					$('#'+elementId).append(
							'<option data-stateid=' + stateList[i].id
									+ ' selected >' + stateList[i].statecode
									+ '</option>');
				} else {
					$('#'+elementId).append(
							'<option data-stateid=' + stateList[i].id + '>'
									+ stateList[i].statecode + '</option>');
				}
			}
		} else {
			if (stateVal != undefined && stateVal != "") {
				$('#'+elementId).val(stateVal);
			}
		}
	}
}

function hideStateCityRow(parentId, elementId) {
	$('#' + parentId).hide();
	$('#' + parentId + ' input').val('');
	$('#' + elementId).attr('data-value','');
	$('#' + elementId).val(function() {
		return $(this).find('option[disabled]').text();
	});
}

function getDateStrToUTC(dateStr) {
	var dateSplit = dateStr.split("-");
	var date = convertTimeStampToLocalTimeStamp(new Date(dateSplit[0],parseInt(dateSplit[1]) - 1,dateSplit[2],dateSplit[3],dateSplit[4],dateSplit[5],dateSplit[6]));
	return date;
}