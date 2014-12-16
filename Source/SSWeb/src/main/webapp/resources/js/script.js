function validateForm(id) {
	var validate = true;
	var emailRegex = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/;
	$('#' + id).find('input').each(function() {
		if ($(this).data('non-empty') == true) {
			if ($(this).val() == "") {
				$(this).parent().addClass('input-error');
				validate = false;
			} else {
				$(this).parent().removeClass('input-error');
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
	});

	if (!validate) {
		return false;
	} else {
		/* Form validated. */
		return true;
	}
}