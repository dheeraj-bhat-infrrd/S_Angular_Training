/*
 *Contains all JavaScript for login purpose
 */

/**
 * 
 */
function loginUser() {
	console.log("authenticate user");
	var success = 0;
	if (!$('#messageHeader').hasClass("hide"))
		$('#messageHeader').addClass("hide");
	var payLoad = $("#loginForm").serialize();
	var redirectTo = '';

	$.ajax({
		url : "./userlogin.do",
		type : "POST",
		data : payLoad,
		success : function(data) {
			console.log(data);
			$('#messageHeader').html(data);
			if ($('#messageHeader').find('div').hasClass('success_message'))
				success = 1;
			$('#messageHeader').removeClass("hide");
			redirectTo = $("#redirectUrl").val();

		},
		complete : function(data) {
			if (success) {
				console.log("Login Successful");
				location.href = "./" + redirectTo;
			}
		}
	});

}
