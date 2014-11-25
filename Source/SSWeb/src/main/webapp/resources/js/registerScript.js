/**
 * Contains All related methods for registration
 */

function validateInvitationForm() {
	console.log("validating Invitation form");
	
	var payLoad = $("#registerForm").serialize();
	
	$.ajax({
		url : "./corporateinvite.do",
		type : "POST",
		data: payLoad,
		success : function(data) {
			console.log(data);
		}
	});

}