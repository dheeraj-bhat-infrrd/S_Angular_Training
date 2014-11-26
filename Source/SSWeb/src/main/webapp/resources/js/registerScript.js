/**
 * Contains All related methods for registration
 */

function validateInvitationForm() {
	console.log("validating Invitation form");
	if (!$('#messageHeader').hasClass("hide"))
		$('#messageHeader').addClass("hide");
	var payLoad = $("#registerForm").serialize();

	$.ajax({
		url : "./corporateinvite.do",
		type : "POST",
		data : payLoad,
		success : function(data) {
			$('#messageHeader').html(data);
			$('#messageHeader').removeClass("hide");
		},
		complete : function() {
			$('#registerForm')[0].reset();
			$('#recaptcha_reload_btn').click();
			$('#registerForm').bootstrapValidator('resetForm', true);
		}
	});

}