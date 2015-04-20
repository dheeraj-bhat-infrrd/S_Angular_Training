/**
 * JS to fetch API Key for google maps
 * 
 */

var apikey;

$(document).ready(function() {
	
	if (typeof apikey === 'undefined') {
		$.ajax({
			url : window.location.origin + "/fetchgooglemapapikey.do",
			type : "GET",
			dataType : "html",
			async : true,
			success : function(data) {
				apikey = data;
			},
			error : function(e) {
				redirectErrorpage();
			}
		});
	}
});