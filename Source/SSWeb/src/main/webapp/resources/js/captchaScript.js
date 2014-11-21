Recaptcha.create('6LdlHOsSAAAAAM8ypy8W2KXvgMtY2dFsiQT3HVq-', 'recaptcha', {
	theme : 'white',
	callback : captchaLoaded
});

function captchaLoaded() {
	$('#registerForm')
			.on(
					'added.field.bv',
					function(e, data) {
						// The field "recaptcha_response_field" has just been
						// added
						if (data.field === 'recaptcha_response_field') {
							// Find the icon
							var $parent = data.element.parents('.form-group'), $icon = $parent
									.find('.form-control-feedback[data-bv-icon-for="'
											+ data.field + '"]');

							// Move icon to other position
							$icon.insertAfter('#recaptcha');
						}
					}).bootstrapValidator({
				fields : {
					firstName : {
						validators : {
							notEmpty : {
								message : 'First name can\'t be empty'
							}
						}
					},
					lastName : {
						validators : {
							notEmpty : {
								message : 'Last name can\'t be empty'
							}
						}
					},
					emailId : {
						validators : {
							notEmpty : {
								message : 'Email Id can\'t be empty'
							}
						}
					}
				}
			}).bootstrapValidator('addField', 'recaptcha_response_field', {
				validators : {
					notEmpty : {
						message : 'Captcha is required'
					}
				}
			});
}