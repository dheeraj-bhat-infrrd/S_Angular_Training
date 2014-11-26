/**
 * Contains All related methods for registration
 */

function validateInvitationForm() {
	console.log("validating Invitation form");
	var success;
	if (!$('#messageHeader').hasClass("hide"))
		$('#messageHeader').addClass("hide");
	var payLoad = $("#registerForm").serialize();

	if (!$('#registerForm').data('bootstrapValidator').isValid())
		return;
	$.ajax({
		url : "./corporateinvite.do",
		type : "POST",
		data : payLoad,
		success : function(data) {
			$('#messageHeader').html(data);
			if ($('#messageHeader').find('div').hasClass('success_message'))
				success = 1;
			$('#messageHeader').removeClass("hide");
		},
		complete : function() {
			if (success) {
				$('#registerForm')[0].reset();
				$('#registerForm').bootstrapValidator('resetForm', true);
			}
			Recaptcha.reload();
		}
	});

}