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
							},
							regexp: {
		                        regexp: /^[a-z]+$/i,
		                        message: 'Can only contain alphabets'
		                    }
						}
					},
					lastName : {
						validators : {
							regexp: {
		                        regexp: /^[a-z\s]+$/i,
		                        message: 'Can only contain alphabets'
		                    }
						}
					},
					emailId : {
						validators : {
							notEmpty : {
								message : 'Email Id can\'t be empty'
							},
							regexp: {
		                        regexp: /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]\.[0-9]\.[0-9]\.[0-9]\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]+))$/,
		                        message: 'Email address not valid'
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