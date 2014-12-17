var is_chrome = navigator.userAgent.indexOf('Chrome') > -1;
var is_explorer = navigator.userAgent.indexOf('MSIE') > -1;
var is_firefox = navigator.userAgent.indexOf('Firefox') > -1;
var is_safari = navigator.userAgent.indexOf("Safari") > -1;
var is_Opera = navigator.userAgent.indexOf("Presto") > -1;

function validateForm(id) {
	var validate = true;
	var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
	var zipcodeRegex = /^\d{5}([\-]?\d{4})?$/;
	var phoneRegex = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
	
	$('#' + id).find('input').each(function() {
		if ($(this).data('email') == true) {
			if (emailRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
				 $('#jsError').hide();
			}else {
				validate = false;
				$('#jsError').show();
                $('#jsErrTxt').html('Please enter a valid emailId.');
				$(this).parent().addClass('input-error');
			}
		}
		
		if ($(this).data('zipcode') == true) {
			if (zipcodeRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
				 $('#jsError').hide();
			}else {
				validate = false;
				$('#jsError').show();
                $('#jsErrTxt').html('Please enter a valid zipcode.');
				$(this).parent().addClass('input-error');
			}
		}
		
		if ($(this).data('phone') == true) {
			if (phoneRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
				 $('#jsError').hide();
			}else {
				validate = false;
				$('#jsError').show();
                $('#jsErrTxt').html('Please enter a valid phone number.');
				$(this).parent().addClass('input-error');
			}
		}
		
		if ($(this).data('non-empty') == true) {
			if ($(this).val() == "") {
				$(this).parent().addClass('input-error');
                $('#jsError').show();
                $('#jsErrTxt').html('Please enter the required fields.');
				validate = false;
			} else {
				$(this).parent().removeClass('input-error');
                $('#jsError').hide();
			}
		}
	});

	if (!validate) {
		return false;
	} else {
		/* Form validated. */
		return true;
	}
}

function showOverlay(){
    $('.overlay-loader').show();
}

function hideOverlay(){
    $('.overlay-loader').hide();
}

detectBrowser();

function detectBrowser(){
    if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1){
        is_safari = true;
        $('.input-file-text').css('left','-51px');
    }else if(is_firefox){
        $('.input-file-text').css('left','-44px');
    }
}

function moveFileBtn(){
    if (navigator.userAgent.indexOf('Safari') != -1 && navigator.userAgent.indexOf('Chrome') == -1){
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 101) + 'px')
    }else if(is_firefox){
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px')
    }else{
        $('#com-logo').css('left',($('#input-file-icn-left').width() - 97) + 'px')
    }
}