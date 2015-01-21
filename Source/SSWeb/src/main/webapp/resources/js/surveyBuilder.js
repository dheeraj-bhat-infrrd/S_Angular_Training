$(document).ready(function(){
	
	newQuestionEnableDisable();
	
	$('.sb-sel-icn-inact').click(function(){
		$('.sb-sel-icn-act').hide();
		$('.sb-sel-icn-inact').show();
		$(this).hide();
		$(this).parent().find('.sb-sel-icn-act').show();
		
		newQuestionEnableDisable();
	});
	
	$('.sb-sel-icn-act').click(function(){
		$('.sb-sel-icn-act').hide();
		$('.sb-sel-icn-inact').show();
		$(this).hide();
		$(this).parent().find('.sb-sel-icn-inact').show();
		
		newQuestionEnableDisable();
	});

	$('.sb-ct-exp').click(function(){
		$(this).hide();
		$('.sb-ct-close').show();
		$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideDown(350);
	});
	
	$('.sb-ct-close').click(function(){
		$(this).hide();
		$('.sb-ct-exp').show();
		$(this).parent().prev('.sb-q-item-txt-or').find('.sb-template-q-wrapper').slideUp(350);
	});
	
	$('#btn-new-survey').click(function(){
		$('.sb-tab-item').removeClass('sb-tab-active');
		$(this).addClass('sb-tab-active');
		$('.sb-content').hide();
		$('.new-survery-content').show();
	});
	
	$('#btn-choose-survey').click(function(){
		$('.sb-tab-item').removeClass('sb-tab-active');
		$(this).addClass('sb-tab-active');
		$('.sb-content').hide();
		$('.choose-survery-content').show();
	});
	
	$('.sb-sel-item-range-txt, .sb-sel-item-range-icn').click(function(){
		$('.sb-dd-wrapper').slideToggle(200);
	});
	
	$('.sb-ratings-sel-item').click(function(){
		$('.sb-ratings-sel-item').removeClass('blue-text');
		$(this).addClass('blue-text');
		$('.sb-sel-icn-act').hide();
		$('.sb-sel-icn-inact').show();
		$('.sb-sel-icn-inact-range').hide();
		$('.sb-sel-icn-act-range').show();
	});
	
	$('.sb-q-chk-no').click(function(){
		$(this).hide();
		$(this).parent().find('.sb-q-chk-yes').show();
	});
	
	$('.sb-q-chk-yes').click(function(){
		$(this).hide();
		$(this).parent().find('.sb-q-chk-no').show();
	});
	
	var selectedRating = "";
	$('.sb-btn-edit').click(function(){
		$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html());
		
		if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html());
			$('.sb-ans-mc-item').hide();
			$('.q-ans-obj-txt').show();
		} else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating") {
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').show();
			$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').hide();
		}
		
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').hide();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').show();
		$(this).next('.sb-btn-save').show();
		$(this).hide();
	});
	
	$('.sb-btn-save').click(function(){
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').val());
		
		if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "objective"){
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-1-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-2-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-3-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-4-txt').val());
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5').html($(this).parent().prev('.sb-q-item-txt').find('.sb-ans-mc-wrapper').find('.q-ans-obj-5-txt').val());
			$('.sb-ans-mc-item').show();
			$('.q-ans-obj-txt').hide();
		} else if($(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').attr('q-type') == "rating") {
			$(this).parent().prev('.sb-q-item-txt').find('.sb-ans-rat-wrapper').hide();
			$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-2').show();
		}
		
		if(selectedRating == "smiles"){
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.sb-icn-smiles').show();
		} else if(selectedRating == "star") {
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.icn-full-star').show();
		} else if(selectedRating == "scale") {
			$('.sb-q-txt-2').find('.sb-stars').hide();
			$('.sb-q-txt-2').find('.sb-icn-scale').show();
		}
		
		$(this).parent().prev('.sb-q-item-txt').find('.sb-q-txt-1').show();
		$(this).parent().prev('.sb-q-item-txt').find('.sb-txt-ar').hide();
		$(this).prev('.sb-btn-edit').show();
		$(this).hide();
	});
	
	$('body').on('click','.sb-dd-item-ans',function(){
		selectedRating = $(this).attr('type');
		$('.sb-dd-item-ans').removeClass('blue-text');
		$(this).addClass('blue-text');
	});
});

function newQuestionEnableDisable() {
	if($('#sb-sel-range-on').is(':hidden') && $('#sb-sel-desc-on').is(':hidden') && $('#sb-sel-mult-on').is(':hidden')) {
	    $('#sb-question-txt').attr('disabled', true);

	    $('#sb-question-add').attr('disabled', true);
	    $('#sb-question-add').addClass('btn-disabled');
	    
	    $('#sb-question-done').attr('disabled', true);
	    $('#sb-question-done').addClass('btn-disabled');
	} else {
	    $('#sb-question-txt').removeAttr("disabled");

	    $('#sb-question-add').removeAttr("disabled");
	    $('#sb-question-add').removeClass('btn-disabled');
	    
	    $('#sb-question-done').removeAttr("disabled");
	    $('#sb-question-done').removeClass('btn-disabled');
	}
}