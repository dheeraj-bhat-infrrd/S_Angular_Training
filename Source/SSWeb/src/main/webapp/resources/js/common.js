/**
 * JIRA:SS-24 BY RM02
 * Holds the javascript functions to be used commonly in almost all files in the
 * application
 */

var lastAjaxRequestToDelete = null;

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
function callAjaxGET(url, callBackFunction, isAsync,disableEle) {
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
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
		complete: function(){
			hideOverlay();
						/*$(document).data('requestRunning', false);
			*/
			enable(disableEle);
			
			
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
 * Generic function to be used for making ajax get calls
 * 
 * @param url
 * @param callBackFunction
 * @param isAsync
 */
function callAjaxPOST(url, callBackFunction, isAsync,disableEle) {
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
		url : url,
		type : "POST",
		dataType : "html",
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
			enable(disableEle);
		
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

function callAjaxPOSTWithTextDataLogo(url, callBackFunction, isAsync, formData) {

	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	$.ajax({
				url : url,
				type : "POST",
				dataType : "text",
				timeout : 30000,
				contentType : false,
				processData : false,
				cache : false,
				data : formData,
				async : isAsync,
				success : callBackFunction,
				complete : function() {
					hideOverlay();
				},
				error : function(x, t, m) {
					if (t === "timeout") {
						logoSuccess=true;
						showErrorMobileAndWeb(' please try uploading the logo again');
						$('#overlay-toast').text("Please try uploading the logo again");
						showToast();
						
					} else {
						redirectErrorpage();
					}
				}

			/*
			 * function(e) { if(e.status == 504) {
			 * redirectToLoginPageOnSessionTimeOut(e.status); return; }
			 * redirectErrorpage(); }
			 */
			});
}

function callAjaxPOSTWithTextDataUpload(url, callBackFunction, isAsync, formData) {

	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	return $.ajax({
				url : url,
				type : "POST",
				dataType : "text",
				contentType : false,
				processData : false,
				cache : false,
				data : formData,
				async : isAsync,
				success : callBackFunction,
				error : function(e) {
					if(e.status == 500) {
						showError("Hierarchy upload failed. Please try again.");
					}
				},
				complete : function() {
					hideOverlay();
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
var disableIcon=false;
function callAjaxGETWithTextData(url, callBackFunction, isAsync, formData, disableEle) {
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
	
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
			
			enable(disableEle);
			
			
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
 * function to disable elements

 * @param disableEle element to be disabled while ajax call is made
 */
function disable(disableEle) {

	if (disableEle && disableEle.trim()!='') {
		$(disableEle).data('requestRunning', true);
		disableIcon = true;
	}
}

/**
 * function to enable elements
 * @param disableEle element to be enabled after ajax call is made
 */
function enable(disableEle) {
	if (disableEle && disableEle.trim()!='') {
		$(disableEle).data("requestRunning", false);
		disableIcon = false;
	}
}

/**
 * Generic function to be used for making form submission with ajax post
 * 
 * @param url
 * @param callBackFunction
 * @param formId
 */
function callAjaxFormSubmit(url, callBackFunction, formId,disableEle) {
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	var $form = $("#" + formId);
	var payLoad = $form.serialize();
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payLoad,
		success : callBackFunction,
		complete: function(data){
			enable(disableEle);
			
			
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
function callAjaxPostWithPayloadData(url, callBackFunction, payload, isAsync,disableEle){
	
	
	if(disableIcon){
		return;
	}
	
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	
	$.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "POST",
		data : payload,
		async : isAsync,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
			enable(disableEle);
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

function callAjaxGetWithPayloadData(url, callBackFunction, payload,isAsync,disableEle){
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
	if (typeof isAsync === "undefined") {
		isAsync = true;
	}
	return $.ajax({
		url : url,
		headers: {          
            Accept : "text/plain; charset=utf-8"   
		},
		type : "GET",
		data : payload,
		async : isAsync,
		cache : false,
		success : callBackFunction,
		complete: function(){
			hideOverlay();
			hideDashOverlay('#mid-dash');
			hideDashOverlay('#top-dash');
			hideDashOverlay('#latest-post-ep');
			enable(disableEle);
		},
		error : function(e) {
			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			if(e.status == 0) {
				return;
			}
			redirectErrorpage();
		}
	});
}

function changeRatingPattern(rating, ratingParent, isOverallRating, source, isProfilePage) {
	var ratingIntVal = 0;
	var roundedFloatingVal = parseFloat(rating).toFixed(2);
	var ratingFloat= parseFloat(roundedFloatingVal).toFixed(2);
	var ratingInt= parseInt(ratingFloat*4);
	ratingIntVal=(ratingInt/4).toFixed(2);
	if(source != undefined && source == "Zillow"){
		ratingImgHtml = "<div class='rating-image cursor-pointer  float-left star-rating-green-"+ ratingIntVal +"' title='"+roundedFloatingVal+"/5.0' ></div>";
	}else if(source =="encompass" || source == "DOTLOOP"){
		ratingImgHtml = "<div class='rating-image  cursor-pointer float-left star-rating-"+ ratingIntVal +"' title='"+roundedFloatingVal+"/5.0'></div>";
		
	}
		else {
		ratingImgHtml = "<div class='rating-image cursor-pointer float-left  star-rating-"+ratingIntVal+"' title='"+roundedFloatingVal+"/5.0'></div>";		
	}
	
	var ratingValHtml = "<div class='rating-rounded float-left' style='font-size:15px;line-height:22px;' >"+ roundedFloatingVal + "</div>";
	if (isOverallRating) {
		ratingValHtml = "<div class='rating-rounded float-left'>" + roundedFloatingVal + " - </div>";
	}
	if (isProfilePage) {
		ratingValHtml = "<div class='rating-rounded float-left'> <span itemprop='ratingValue'>"+ roundedFloatingVal + "</span> - </div>";
	}
	

	ratingParent.html('');
	ratingParent.append(ratingImgHtml);
	ratingParent.append(ratingValHtml);
};

function proRatingPattern(rating, ratingParent, isOverallRating, source) {
	var ratingIntVal = 0;
	var roundedFloatingVal = parseFloat(rating).toFixed(1);
	var ratingFloat =parseFloat(roundedFloatingVal).toFixed(2);
	var ratingInt= parseInt(ratingFloat*4);
	ratingIntVal=(ratingInt/4).toFixed(2);
	if(roundedFloatingVal!= 0.0){
		var ratingValHtml = "<div class='rating-rounded float-left' style='font-weight:600 !important'>&#8212; " + roundedFloatingVal + "</div>";
		if (isOverallRating) {
			ratingValHtml = "<div class='rating-rounded float-left' style='font-weight:600 !important'>&#8212; " + roundedFloatingVal + " - </div>";
		}
	}
	
	if(source != undefined && source == "Zillow"){
		ratingImgHtml = "<div class='rating-image  cursor-pointer float-left star-rating-green-"+ ratingIntVal +"' title='"+roundedFloatingVal+"/5.0' style='margin-left: 10px;'></div>";
	}else if(source =="encompass" || source == "DOTLOOP"){
		ratingImgHtml = "<div class='rating-image  cursor-pointer float-left star-rating-"+ ratingIntVal +"' title='"+roundedFloatingVal+"/5.0' style='margin-left: 10px;' ></div>";
		
	}
		else {
		ratingImgHtml = "<div class='rating-image cursor-pointer float-left  star-rating-"+ratingIntVal+"' title='"+roundedFloatingVal+"/5.0' style='margin-left: 10px;'></div>";		
	}

	ratingParent.html('');
	ratingParent.append(ratingValHtml);
	ratingParent.append(ratingImgHtml);
	
};
$(document).on('click','.verified-badge',function(e){
	e.stopPropagation();
	window.open("https://socialsurvey.zendesk.com/hc/en-us/articles/216454118-Added-Verified-Customer-badge");
});

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



function callAjaxGETAndAbortLastRequest(url,  callBackFunction, isAsync,disableEle) {
	if ( $(disableEle).data('requestRunning') ) {
		return;
    }
	
	disable(disableEle);
	
	if (isAsync == "undefined") {
		isAsync = true;
	}
	lastAjaxRequestToDelete = $.ajax({
		url : url,
		type : "GET",
		dataType : "html",
		async : isAsync,
		cache : false,
		beforeSend : function()    {           
	        if(lastAjaxRequestToDelete != null) {
	        	lastAjaxRequestToDelete.abort();
	        }
	    },
		success : callBackFunction,
		complete: function(){
			hideOverlay();
						/*$(document).data('requestRunning', false);
			*/
			enable(disableEle);
			
			
		},

		error : function(jqXHR , e) {
			if (jqXHR.statusText == "abort" || e == "abort")
                return;

			if(e.status == 504) {
				redirectToLoginPageOnSessionTimeOut(e.status);
				return;
			}
			redirectErrorpage();
		}
	});
}
// Function to open forgot password page
function openForgotPasswordPage(){
	window.location.href = "./forgotpassword.do";
}

// Dashboard popup click functions
function openAuthPage(event,socialNetwork, isAutoLogin, element) {
	event.stopPropagation();
	if(isAutoLogin) {
		$('#overlay-toast').html('Insufficient permission to connect to ' + socialNetwork);
		showToast();
		return;
	}
	
	var dataLink = $(element).attr('data-link');
	
	confirmSocialAuth(socialNetwork, function() {
		window.open("./socialauth.do?social=" + socialNetwork, "Authorization Page", "width=800,height=600,scrollbars=yes");
	}, dataLink);
}
function openAuthPageZillow(event,disableEle) {
	event.stopPropagation();
	callAjaxGET("/socialauth.do?social=zillow", function(data) {
		createZillowProfileUrlPopup( data);
	}, true,disableEle);
}

function openAuthPageRegistration(socialNetwork) {
	window.open("./socialauth.do?social=" + socialNetwork + "&flow=registration", "Authorization Page", "width=600,height=600,scrollbars=yes");
}
function openAuthPageDashboard(socialNetwork, columnName, columnValue) {
	//check if user is autologged in
	if($('#pro-cmplt-stars').attr('data-autologin') == 'true') {
		$('#overlay-toast').html('Insufficient permission to connect to ' + socialNetwork);
		showToast();
		return;
	}
	
	confirmSocialAuth(socialNetwork, function() {
		window.open("./socialauth.do?social=" + socialNetwork + "&columnName="
				+ columnName + "&columnValue=" + columnValue, "Authorization Page",
				"width=800,height=600,scrollbars=yes");
	});
}

function openAuthPageFixSocialMedia(socialNetwork, columnName, columnValue, isManual) {
	//check if user is autologged in
	if($('#pro-cmplt-stars').attr('data-autologin') == 'true') {
		$('#overlay-toast').html('Insufficient permission to connect to ' + socialNetwork);
		showToast();
		return;
	}
	
		window.open("./socialauth.do?social=" + socialNetwork + "&columnName="
				+ columnName + "&columnValue=" + columnValue + "&isFixSocialMedia=true&isManual="+isManual, socialNetwork + " Authorization Page",
				"width=800,height=600,scrollbars=yes");
	
}


function openAuthPageDashboardZillow(disableEle){
	callAjaxGET("/socialauth.do?social=zillow", function(data) {
		createZillowProfileUrlPopup( data);
	}, true,disableEle);
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

function showStateCityRow(parentId, stateId, cityId) {
    $('#' + parentId).show();
    var stateVal = $('#' + stateId).attr('data-value');
    if (!stateList) {
        callAjaxGET("./getusstatelist.do", function(data) {
            stateList = JSON.parse(data);
            appendStateListToDropDown(stateId, stateList, cityId);
        }, true);
    } else {

        if ($('#' + stateId).children('option').size() == 1) {
        	appendStateListToDropDown(stateId, stateList, cityId);
        } else {
            if (stateVal != undefined && stateVal != "") {
                $('#' + stateId).val(stateVal);
            }
        }
    }
}

function appendStateListToDropDown(stateId, stateList, cityId) {
	var stateVal = $('#' + stateId).attr('data-value');
	for (var i = 0; i < stateList.length; i++) {
        if (stateVal == stateList[i].statecode) {
            $('#' + stateId).append(
                '<option data-stateid=' + stateList[i].id + ' selected >' + stateList[i].statecode + '</option>');
        } else {
            $('#' + stateId).append(
                '<option data-stateid=' + stateList[i].id + '>' + stateList[i].statecode + '</option>');
        }
    }
	attachChangeEventStateDropDown(stateId, cityId);
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
	return origin;
}

/**
 * function to initiate survey review
 */
function initSurveyReview(userId) {
	var payload = {
		"userId" : userId
	};
	$.ajax({
		url : "./../rest/survey/redirecttodetailspage",
		type : "GET",
		data : payload,
		datatype : "html",
		async : false,
		success : function(data) {
			if (data.errCode == undefined)
				success = true;
		},
		complete : function(data) {
			if (success) {
				window.open(data.responseText);
			}
		}
	});
}

//index js functions
//Function to validate login user name
function validateUserId(elementId) {
	if ($('#' + elementId).val() != "") {
		if (emailRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			$('#overlay-toast').html('Please enter a valid user name.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter user name.');
		showToast();
		return false;
	}
}

// Function to validate login password
function validateLoginPassword(elementId) {
	if ($('#' + elementId).val() != "") {
		return true;
	} else {
		$('#overlay-toast').html('Please enter password.');
		showToast();
		return false;
	}
}

// Function to validate the login form
function validateLoginForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	if (!validateUserId('login-user-id')) {
		$('#login-user-id').focus();
		return false;
	}
	if (!validateLoginPassword('login-pwd')) {
		$('#login-pwd').focus();
		return false;
	}
	return true;
}

// Function to validate registration form
function validateRegistrationForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	// Validate form input elements
	if (!validateRegFirstName('reg-fname')) {
		$('#reg-fname').focus();
		return false;
	}
	if (!validateRegLastName('reg-lname')) {
		$('#reg-lname').focus();
		return false;

	}
	if (!validateRegEmailId('reg-email')) {
		$('#reg-email').focus();
		return false;

	}
	
	if (!validateRegPassword('reg-pwd')) {
		$('#reg-pwd').focus();
		return false;
	}
	if (!validateRegConfirmPassword('reg-pwd', 'reg-conf-pwd')) {
		$('#reg-conf-pwd').focus();
		return false;
	}
	return true;
}

function validatePreRegistrationForm(id) {
	// hide the server error
	$("#serverSideerror").hide();
	// Validate form input elements
	if (!validateRegFirstName('reg-fname')) {
		$('#reg-fname').focus();
		return false;
	}
	if (!validateRegLastName('reg-lname')) {
		$('#reg-lname').focus();
		return false;
	}
	if (!validateRegEmailId('reg-email')) {
		$('#reg-email').focus();
		return false;
	}
	return true;
}

// Function to validate email id in a form
function validateRegEmailId(elementId) {
	if ($('#' + elementId).val() != "") {
		if (emailRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			// $('#overlay-toast').html('Please enter a valid email id.');
			// showToast();
			showRegErr('Please enter a valid email address');
			return false;
		}
	} else {
		// $('#overlay-toast').html('Please enter email id.');
		// showToast();
		showRegErr('Please enter email address');
		return false;
	}
}

// Function to validate the first name
function validateRegFirstName(elementId) {
	if ($('#' + elementId).val() != "") {
		if (nameRegex.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showRegErr('Please enter a valid first name');
			return false;
		}
	} else {
		showRegErr('Please enter first name');
		return false;
	}
}

// Function to validate the last name
function validateRegLastName(elementId) {

		if ($('#' + elementId).val() == ""||lastNameRegEx.test($('#' + elementId).val()) == true) {
			return true;
		} else {
			showRegErr('Please enter a valid last name.');
			return false;
		}
	
}

// function to validate a password in form
function validateRegPassword(elementId) {
	var password = $('#' + elementId).val();
	if (password != "") {
		// check if password length is proper
		if (password.length < minPwdLength || password.length > maxPwdLength) {
			$('#overlay-toast').html('Password must be between 6-15 characters.');
			showToast();
			return false;
		} else if (passwordRegex.test(password) == true) {
			return true;
		} else {
			$('#overlay-toast').html(
					'Password must contain one special character.');
			showToast();
			return false;
		}
	} else {
		$('#overlay-toast').html('Please enter password.');
		showToast();
		return false;
	}
}

// Function to match password and confirm password
function validateRegConfirmPassword(pwdId, confirmPwdId) {
	/* === Validate passwords === */
	if ($('#' + confirmPwdId).val() != "") {
		if ($('#' + pwdId).val() != $('#' + confirmPwdId).val()) {
			$('#overlay-toast').html('Passwords do not match.');
			showToast();
			return false;
		} else {
			return true;
		}
	} else {
		$('#overlay-toast').html('Please enter confirm password.');
		showToast();
		return false;
	}

}

//Show loader icon on pagination of infinite scroll
function showLoaderOnPagination(element) {
	element.find(".loader-icn-sm").remove();
	element.append("<div class='loader-icn-sm'></div>");
}

//Hide loader icon on pagination of infinite scroll
function hideLoaderOnPagination(element) {
	element.find(".loader-icn-sm").remove();
}

function getWindowWidth() {
	return window.innerWidth || document.documentElement.clientWidth
			|| document.body.clientWidth;
}

function getWindowHeight() {
	return window.innerHeight || document.documentElement.clientHeight
			|| document.body.clientHeight;
}

function attachChangeEventStateDropDown(stateId, cityId) {
    $('#' + stateId).off('change');
    $('#' + stateId).on('change', function(e) {
        var stateIdVal = $(this).find(":selected").attr('data-stateid');
        updateCityAutcomplete(stateIdVal, cityId, stateId);
    });
    var stateIdVal = $('#' + stateId).find(":selected").attr('data-stateid');
    if (stateIdVal != undefined && stateIdVal != '') {
    	updateCityAutcomplete(stateIdVal, cityId, stateId);
    }
}

function updateCityAutcomplete(stateIdVal, cityId, stateId) {
	callAjaxGET("./getzipcodesbystateid.do?stateId=" + stateIdVal, function(data) {
        var uniqueSearchData = getUniqueCitySearchData(data);
        initializeCityLookup(uniqueSearchData, cityId);
        attachFocusEventCity(stateId, cityId);
    }, true);
}

function attachFocusEventCity(stateId, cityId) {
	$('#'+cityId).unbind('focus');
  	$('#'+cityId).bind('focus', function(){ 
  		if($('#'+stateId).val() &&  $('#'+stateId).val() != ""){
  			$(this).trigger('keydown');
  			//$(this).autocomplete("search");		
  		}
  	});
}

function attachAutocompleteCountry(countryId, countryCodeId, stateId, stateCityRowId, cityId, phoneNumberId) {
	
	//check for the existing value of country code and set defualt to us if not set
	var countryCode = "US";

	if ($('#' + countryCodeId).val() != undefined && $('#' + countryCodeId).val() != "") {
	    countryCode = $('#' + countryCodeId).val();
	}

	if (countryCode == "US") {
	    showStateCityRow(stateCityRowId, stateId, cityId);
	    if ($('#' + countryId).val() == null || $('#' + countryId).val() == "") {
	        $('#' + countryId).val("United States");
	        $('#' + countryCodeId).val(countryCode);
	    }
	    selectedCountryRegEx = "^" + "\\b\\d{5}\\b(?:[- ]{1}\\d{4})?" + "$";
	    selectedCountryRegEx = new RegExp(selectedCountryRegEx);
	} else {
	    hideStateCityRow(stateCityRowId, stateId);
	}
	
	//attach autocomplete event
	$("#"+countryId).autocomplete({
		minLength: 1,
		source: countryData,
		delay : 0,
		autoFocus : true,
		open : function(event, ui) {
			$( "#"+countryCodeId ).val("");
		},
		select: function(event, ui) {
			$("#"+countryId).val(ui.item.label);
			$("#"+countryCodeId).val(ui.item.code);
			for (var i = 0; i < postCodeRegex.length; i++) {
				if (postCodeRegex[i].code == ui.item.code) {
					selectedCountryRegEx = "^" + postCodeRegex[i].regex + "$";
					selectedCountryRegEx = new RegExp(selectedCountryRegEx);
					break;
				}
			}
			if(ui.item.code == "US"){
				showStateCityRow(stateCityRowId, stateId, cityId);
			}else{
				hideStateCityRow(stateCityRowId, stateId);
			}
			
			if(phoneNumberId) {
				//update phone number masking
				var phoneNumberElement = $('#'+phoneNumberId);
				phoneNumberElement.unmask();
				phoneFormat = phoneFormatList[ui.item.code];
				currentPhoneRegEx = phoneFormat;
				phoneNumberElement.mask(phoneFormat, {'translation': {d: {pattern: /[0-9*]/}}});
			}
			return false;
		},
		close: function(event, ui) {},
		create: function(event, ui) {
	        $('.ui-helper-hidden-accessible').remove();
		}
	}).autocomplete("instance")._renderItem = function(ul, item) {
		return $("<li>").append(item.label).appendTo(ul);
	};
}

//function to check the type of file on logo upload
var logoFileExtensions = [".jpg", ".jpeg", ".png"]; 
function logoValidate(logo) {
	if ($(logo).attr("type")=="file"){
    	 var sFileName = $(logo).val();
         if (sFileName.length > 0) {
            var blnValid = false;
            for (var j = 0; j < logoFileExtensions.length; j++) {
                var sCurExtension = logoFileExtensions[j];
                if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
                    blnValid = true;
                    break;
                }
            }
             
            if (!blnValid) {
            	var msg="Please upload files of type jpeg, png or jpg";
            	showErrorMobileAndWeb(msg);
                $(logo).val="";
                
                return false;
            }
        }
    }
	
    return true;
    
}
/*var fileExtensions=".xlsx";
function fileValidate(fileformat){
	if($(fileformat).attr("type")=="file"){
		var FileName=$(fileFormat).val();
		if(FileName.length>0){
			var blnValid= false;
			if (FileName.substr(FileName.length - fileExtensions.length, fileExtensions.length).toLowerCase() == fileExtensions.toLowerCase()) {
                blnValid = true;
                break;
            }
		}
		 if (!blnValid) {
         	var msg="Please upload xlsx file";
         	showErrorMobileAndWeb(msg);
             $(logo).val="";
             
             return false;
         }
	}
	return true;
}*/


//update GA tracking Id
var gaTrackingId;

function updateGoogleTrackingId() {
	// Google Analytics tracking ID
	if (typeof gaTrackingId === 'undefined') {
		callAjaxGET("/fetchgatrackingid.do", function(data) {
			gaTrackingId = data;
			
			// Google Analytics tracking code
			(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
				(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
				m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
				})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

				ga('create', gaTrackingId, 'auto');
				ga('send', 'pageview');
		}, true);
	}
}

/**
 * Functions related to timezone and date
 * 
 */

var offset = new Date().getTimezoneOffset() * 60 * 1000;
function convertUTCToUserDate(date) {
	return new Date(Date.UTC(date.getFullYear(), date.getMonth(), date
			.getDate(), date.getHours(), date.getMinutes(), date.getSeconds()));
}

function convertUserDateToUTC(date) {
	return new Date(date.getUTCFullYear(), date.getUTCMonth(), date
			.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date
			.getUTCSeconds());
}

function convertUserDateToLocale(date) {
	var convertedTimestamp = date.getTime() - offset;
	return new Date(convertedTimestamp);
}
function convertUserDateToLocalWeekFormt(date) {
	var days = [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat' ];
	var convertedTimestamp = date.getTime() - offset;
	var date3 = new Date(convertedTimestamp);
	var dayOftheweek = days[date3.getDay()];
	var month = ((date3.getMonth() + 1) < 10) ? "0" + (date3.getMonth() + 1)
			: (date3.getMonth() + 1);
	var day = (date3.getDate() < 10) ? "0" + (date3.getDate()) : (date3
			.getDate());
	var minutes = (date3.getMinutes() < 10) ? "0" + (date3.getMinutes())
			: (date3.getMinutes());
	var hours = (date3.getHours() < 10) ? "0" + (date3.getHours()) : (date3
			.getHours());
	var sec = (date3.getSeconds() < 10) ? "0" + (date3.getSeconds()) : (date3
			.getSeconds());
	var date4 = dayOftheweek + " " + month + " " + day + " "
			+ date3.getFullYear() + " " + hours + ":" + minutes + ":" + sec;
	return date4;
}

function convertUserDateToWeekFormt(date) {
	var days = [ 'Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat' ];
	var months = [ 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug',
			'Sep', 'Oct', 'Nov', 'Dec' ];
	var date3 = new Date(date.getTime());
	var dayOftheweek = days[date3.getDay()];
	var month = months[date3.getMonth()];
	var day = (date3.getDate() < 10) ? "0" + (date3.getDate()) : (date3
			.getDate());
	var minutes = (date3.getMinutes() < 10) ? "0" + (date3.getMinutes())
			: (date3.getMinutes());
	var hours = date3.getHours();
	var ampm = hours >= 12 ? 'pm' : 'am';
	hours = hours % 12;
	hours = hours ? hours : 12;
	var date4 = dayOftheweek + ", " + month + " " + day + ", "
			+ date3.getFullYear() + " at " + hours + ":" + minutes + " " + ampm;
	return date4;
}

function convertTimeStampToLocalTimeStamp(generalTimestamp) {
	var convertedTimestamp = generalTimestamp.getTime() - offset;
	var date3 = new Date(convertedTimestamp);
	var month = ((date3.getMonth() + 1) < 10) ? "0" + (date3.getMonth() + 1)
			: (date3.getMonth() + 1);
	var day = (date3.getDate() < 10) ? "0" + (date3.getDate()) : (date3
			.getDate());
	var minutes = (date3.getMinutes() < 10) ? "0" + (date3.getMinutes())
			: (date3.getMinutes());
	var hours = (date3.getHours() < 10) ? "0" + (date3.getHours()) : (date3
			.getHours());
	var sec = (date3.getSeconds() < 10) ? "0" + (date3.getSeconds()) : (date3
			.getSeconds());
	var date4 = month + '-' + day + '-' + date3.getFullYear() + " " + hours
			+ ":" + minutes + ":" + sec + "." + date3.getMilliseconds();
	return date4;
}

/**
 * Find first day of the week (Monday)
 * 
 * @param week number
 * @param year number
 * 
 * @returns date
 * 
 */
function getDateFromWeekAndYear(year, weekNumber) {
	var jan10 = new Date(year, 0, 10, 12, 0, 0);
	var jan4 = new Date(year, 0, 4, 12, 0, 0);
	var mon1 = jan4.getTime() - jan10.getDay() * 86400000;
	var date = new Date(mon1 + ((weekNumber - 1) * 7 + 6) * 86400000);
	date = date.toString("MMM d, yyyy");
	return date;
}
//Function for Html encoding
function escapeHtml(stringInput) {
var div = document.createElement('div');
div.appendChild(document.createTextNode(stringInput));
return div.innerHTML;
}

function paintAvgRatingForPpf(avgRating) {
	if (avgRating != undefined) {
		changeRatingPattern(avgRating, $("#rating-avg-comp"), true);
	}
}