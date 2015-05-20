function showLinkedInProfileUrl(data) {
	if (data == undefined || data == null) {
		return;
	}
	
	$('#wl-import-btn').remove();
	$('#wl-import-btn-msg').removeClass('hide');
	$('#wc-connect-link').html('LinkedIn Profile <u><a href=' + data + ' target="_blank">' + data + '</a></u>');
}

function authenticate(socialNetwork) {
	openAuthPage(socialNetwork);
	payload = {
		'socialNetwork' : socialNetwork
	};
}

function showProfileLink(source, profileUrl){
	if(source=='facebook'){
		$('#fb-profile-url').html(profileUrl);
	}
	else if(source=='twitter'){
		$('#twitter-profile-url').html(profileUrl);
	}
	else if(source=='linkedin'){
		$('#linkedin-profile-url').html(profileUrl);
	}
	else if(source=='google'){
		$('#ggl-profile-url').html(profileUrl);
	}
}