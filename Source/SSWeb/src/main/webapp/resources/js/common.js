/**
 * JIRA:SS-24 BY RM02
 * Holds the javascript functions to be used commonly in almost all files in the
 * application
 */


//Function to redirect to login page if session time out
function redirectToLoginPageOnSessionTimeOut(status) {
	//window.location = window.location.origin + '/login.do?s=sessionerror';
	window.location = getLocationOrigin() + '/login.do?s=sessionerror';
}


/**
 * Generic function to be used for making ajax get calls
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxGET(url, callBackFunction, isAsync) {
	if (isAsync == "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		dataType : "html",
		async : isAsync,
		cache : false,
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
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		dataType : "text",
		data : formData,
		async : isAsync,
		cache : false,
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
	var $form = $("#" + formId);
	var payLoad = $form.serialize();
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
	//window.open(window.location.origin + "/errorpage.do",'_self');
	window.open(getLocationOrigin() + "/errorpage.do",'_self');
}

/**
 * Generic method to use for post ajax request with payload data
 * 
 * @param url
 * @param callBackFunction
 * @param payload
 */
function callAjaxPostWithPayloadData(url, callBackFunction, payload, isAsync){
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
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "GET",
		data : payload,
		async : isAsync,
		cache : false,
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
		ratingImgHtml = "<div class='rating-image float-left smiley-rat-" + ratingIntVal + "' title='Social Survey'></div>";		
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
		cache : false,
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
			cache : false,
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
			cache : false,
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
		cache : false,
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
		cache : false,
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

function enableBodyScroll() {
	$('body').removeClass('body-no-scroll');
}

function disableBodyScroll() {
	$('body').addClass('body-no-scroll');
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

function scrollToTop() {
	var body = $("html, body");
	body.stop().animate({scrollTop:0}, '500', 'swing', function() { 
	});
}

function getLocationOrigin(){
	var origin = window.location.protocol + "//" + window.location.hostname + (window.location.port ? ':' + window.location.port: '');
	alert(origin);
	return origin;
}