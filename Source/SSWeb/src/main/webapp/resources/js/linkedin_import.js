function authenticate(socialNetwork) {
	openAuthPage(socialNetwork);
	payload = {
		'socialNetwork' : socialNetwork
	};
}

// update yelp profile url
function showYelpInput() {
	$('#yelp-profile-url-display').addClass('hide');
	
	$('#yelp-profile-url').removeClass('hide');
	$('#yelp-profile-url').focus();
}

$(document).on('blur', '#yelp-profile-url', function() {
	var yelpLink = $('#yelp-profile-url').val();

	// check if link edited or not
	if (yelpLink == $('#yelp-profile-url-display').html().trim()) {
		$('#yelp-profile-url-display').removeClass('hide');
		$('#yelp-profile-url').addClass('hide');
		return;
	}
	
	var payload = {
		"yelplink" : yelpLink
	};
	if (isValidUrl(yelpLink)) {
		callAjaxPostWithPayloadData("./updateyelplink.do", function(data) {
			$('#yelp-profile-url-display').html(yelpLink);
			$('#yelp-profile-url-display').removeClass('hide');
	        
			$('#yelp-profile-url').addClass('hide');
			
			$('#message-header').html(data);
			$('#overlay-toast').html($('#display-msg-div').text().trim());
			showToast();
		}, payload);
	} else {
		$('#overlay-toast').html("Enter a valid url");
		showToast();
	}
});

// show social profile urls
function showLinkedInProfileUrl(data) {
	if (data == undefined || data == null) {
		return;
	}
	
	$('#wl-import-btn').remove();
	$('#wl-import-btn-msg').removeClass('hide');
	$('#wc-connect-link').html('LinkedIn Profile <u><a href=' + data + ' target="_blank">' + data + '</a></u>');
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