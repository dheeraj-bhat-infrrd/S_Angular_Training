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
			console.error("error : " + e);
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
			console.error("error : " + e);
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
			console.error("error : " + e);
		}
	});
}

function getMainContent(url,whereToDisplay) {
	
}