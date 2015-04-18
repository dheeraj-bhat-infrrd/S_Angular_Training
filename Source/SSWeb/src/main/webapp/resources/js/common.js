/**
 * JIRA:SS-24 BY RM02
 * Holds the javascript functions to be used commonly in almost all files in the
 * application
 */

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
	console.log("ajax form submit called for url :" + url + " and formId : "
			+ formId);
	var $form = $("#"+formId);
	var payLoad = $form.serialize();
	console.log("payload is --"+payLoad);
	$.ajax({
		url : url,
		type : "POST",
		data : payLoad,
		success : callBackFunction,
		error : function(e) {
			redirectErrorpage();
		}
	});
}

/**
 * Generic function to be used for making form submission with ajax post
 */
function redirectErrorpage(){
	window.location = "errorpage.do";
}

/**
 * Generic method to use for post ajax request with payload data
 * 
 * @param url
 * @param callBackFunction
 * @param payload
 */
function callAjaxPostWithPayloadData(url, callBackFunction, payload,isAsync){
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

function changeRatingPattern(rating, ratingParent,isOverallRating) {
	
	var ratingIntVal = parseInt(rating) + 1;
	
	if(ratingIntVal >=5){
		ratingIntVal = 5;
	}
	
	var roundedFloatingVal = parseFloat(rating).toFixed(2);
	
	var ratingImgHtml = "<div class='rating-image float-left smiley-rat-"+ratingIntVal+"'></div>";
	var ratingValHtml = "<div class='rating-rounded float-left'>"+roundedFloatingVal+"</div>";
	
	if(isOverallRating){
		ratingValHtml = "<div class='rating-rounded float-left'>"+roundedFloatingVal+" - </div>";
		$('#prof-header-rating').addClass('smiley-rat-'+ratingIntVal);
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

function shareOnFacebook(firstName, lastName, agentName, review, score){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score
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
					showToast(data);
				}
			},
			error : function(e) {
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast(e.responseText);
			}
		});
}

function shareOnTwitter(firstName, lastName, agentName, review, score){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score
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
					showToast(data);
				}
			},
			error : function(e) {
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast(e.responseText);
			}
		});
}

function shareOnLinkedin(firstName, lastName, agentName, review, score){
	var success= false;
	var payload = {
			"firstName" : firstName,
			"lastName" : lastName,
			"agentName" : agentName,
			"review" : review,
			"score" : score
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
					showToast(data);
				}
			},
			error : function(e) {
				console.error("error : " + e.responseText);
				$('#overlay-toast').html(e.responseText);
				showToast(e.responseText);
			}
		});
}

function shareOnYelp(agentId, location){
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
				window.open(data.relativePath);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast(e.responseText);
		}
	});
}

function shareOnGooglePlus(agentId, location){
	var success= false;
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
				window.open(data.host + data.profileServer
						+ data.relativePath);
			}
		},
		error : function(e) {
			console.error("error : " + e.responseText);
			$('#overlay-toast').html(e.responseText);
			showToast(e.responseText);
		}
	});
}