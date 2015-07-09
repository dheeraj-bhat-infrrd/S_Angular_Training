function showActiveUserLogoutOverlay() {
	$('#overlay-header').html("Active User Detected");
	$("#overlay-text").html("Please logout active user to proceed");
	$('#overlay-continue').addClass("hide");
	$('#overlay-cancel').html("Ok");
	
	$('#overlay-cancel').click(function(){
		hideActiveUserLogoutOverlay();
		landingFlow();
	});

	$('#overlay-main').show();
}

function hideActiveUserLogoutOverlay() {
	$('#overlay-main').hide();
	$("#overlay-header").html('');
	$("#overlay-text").html('');
	$('#overlay-continue').removeClass("hide");
	$('#overlay-cancel').html('');
	
	$('#overlay-cancel').unbind('click');
}