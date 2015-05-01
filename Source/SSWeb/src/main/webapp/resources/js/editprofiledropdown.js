// Profile View as
$('body').on('click','#profile-sel',function(e) {
	e.stopPropagation();
	$('#pe-dd-wrapper-profiles').slideToggle(200);
});
$('body').on('click','.pe-dd-item',function(e) {
	var newProfileId = $(this).data('profile-id');

	$('#profile-sel').html($(this).html());
	$('#pe-dd-wrapper-profiles').slideToggle(200);

	showMainContent('./showprofilepage.do?profileId=' + newProfileId);
});

$('body').click(function() {
	if ($('#pe-dd-wrapper-profiles').css('display') == "block") {
		$('#pe-dd-wrapper-profiles').toggle();
	}
});