// Settings View as
$('body').on('click','#setting-sel',function(e){
	e.stopPropagation();
	$('#se-dd-wrapper-profiles').slideToggle(200);
});

$('body').on('click','.se-dd-item',function(e) {
	var newProfileId = $(this).data('profile-id');
	
	$('#setting-sel').html($(this).html());
	$('#se-dd-wrapper-profiles').slideToggle(200);
	
	showMainContent('./showcompanysettings.do?profileId=' + newProfileId);
});

$('body').click(function() {
	if ($('#se-dd-wrapper-profiles').css('display') == "block") {
		$('#se-dd-wrapper-profiles').toggle();
	}
});