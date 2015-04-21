// Profile View as
$('#profile-sel').click(function(e) {
	e.stopPropagation();
	$('#pe-dd-wrapper-profiles').slideToggle(200);
});

$('.pe-dd-item').click(function() {
	var newProfileId = $(this).data('profile-id');

	$('#profile-sel').html($(this).html());
	$('#pe-dd-wrapper-profiles').slideToggle(200);

	showMainContent('./showprofilepage.do?profileId=' + newProfileId);
});

$('body').click(function(e) {
	e.stopImmediatePropagation();
	if ($('#pe-dd-wrapper-profiles').css('display') == "block") {
		$('#pe-dd-wrapper-profiles').slideToggle(200);
	}
});