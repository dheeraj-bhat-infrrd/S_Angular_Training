/**
 * Contains All related methods for registration
 */

function submitInvitationForm() {
	console.log("Method to submit Invitation form called");
	var success;
	if (!$('#message-header').hasClass("hide"))
		$('#message-header').addClass("hide");
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