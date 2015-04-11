// Profile View as
$('#profile-sel').click(function(){
	$('#pe-dd-wrapper-profiles').slideToggle(200);
});

$('.pe-dd-item').click(function(){
	var newProfileId = $(this).data('profile-id');
	
	$('#profile-sel').html($(this).html());
	$('#pe-dd-wrapper-profiles').slideToggle(200);
	
	showMainContent('./showprofilepage.do?profileId=' + newProfileId);
});