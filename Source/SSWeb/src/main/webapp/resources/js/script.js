function validateForm(id) {
	var validate = true;
	var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
	var zipcodeRegex = /^\d{5}([\-]?\d{4})?$/;
	var phoneRegex = /^[(]{0,1}[0-9]{3}[)]{0,1}[-\s\.]{0,1}[0-9]{3}[-\s\.]{0,1}[0-9]{4}$/;
	
	$('#' + id).find('input').each(function() {
		if ($(this).data('non-empty') == true) {
			if ($(this).val() == "") {
				$(this).parent().addClass('input-error');
                $('#jsError').show();
                $('#jsErrTxt').html('Please enter the required fields.')
				validate = false;
			} else {
				$(this).parent().removeClass('input-error');
                $('#jsError').hide();
			}
		}

		if ($(this).data('email') == true) {
			if (emailRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
			}else {
				validate = false;
				$(this).parent().addClass('input-error');
			}
		}
		
		if ($(this).data('zipcode') == true) {
			if (zipcodeRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
			}else {
				validate = false;
				$(this).parent().addClass('input-error');
			}
		}
		
		if ($(this).data('phone') == true) {
			if (phoneRegex.test($(this).val()) == true) {
				$(this).parent().removeClass('input-error');
			}else {
				validate = false;
				$(this).parent().addClass('input-error');
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