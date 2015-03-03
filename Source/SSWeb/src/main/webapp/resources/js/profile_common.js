/**
 * This js file contains functions commonly used in profile page and find a pro
 */

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