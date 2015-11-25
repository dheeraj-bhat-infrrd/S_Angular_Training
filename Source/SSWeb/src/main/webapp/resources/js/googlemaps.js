/**
 * JS to fetch API Key for google maps
 * 
 */

var apikey;

$(document).ready(function() {
	
	if (typeof apikey === 'undefined') {
		//fetchGoogleMapApi();
		callAjaxGET("/fetchgooglemapapikey.do", function(data) {
			apikey = data;
		}, true,'');
	}
});

function fetchGoogleMapApi() {
	$.ajax({
		url : window.location.origin + "/fetchgooglemapapikey.do",
		type : "GET",
		dataType : "html",
		async : false,
		success : function(data) {
			apikey = data;
		},
		error : function(e) {
			redirectErrorpage();
		}
	});
}