app.controller('newSignupController', [ '$cookies', '$scope', '$location', '$rootScope', 'UserProfileService', 'CompanyProfileService', '$window', function($cookies, $scope, $location, $rootScope, UserProfileService, CompanyProfileService, $window) {
	leftposition();
	// this will set the expiration to 1 day
	var now = new Date(), exp = new Date();
	exp.setDate(exp.getDate() + 1);

	$scope.putUserIdCompanyIdInCookie = function(userId, companyId) {
		$cookies.put("userId", userId, {
			'expires' : exp
		});
		$cookies.put("companyId", companyId, {
			'expires' : exp
		});
		$rootScope.userId = $cookies.get("userId");
		$rootScope.companyId = $cookies.get("companyId");
	}

	$scope.clearCookie = function() {
		$cookies.remove("userId");
		$cookies.remove("companyId");
	}

	$rootScope.redirect = false;
	$scope.emailFormat = "^[_A-Za-z0-9-\\+\\.]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	if (planId != "") {
		$cookies.put("planId", planId, {
			'expires' : exp
		});
	} else {
		planId = $cookies.get("planId");
	}

	if (userId != "" && companyId != "") {
		$scope.putUserIdCompanyIdInCookie(userId, companyId);
	} else {
		$rootScope.userId = $cookies.get("userId");
		$rootScope.companyId = $cookies.get("companyId");
	}
	if (newUser == "true") {
		$scope.clearCookie();
		$location.path('/accountsignup').replace();
	} else if (isLinkedin == "true") {
		$rootScope.redirect = true;
		$location.path('/linkedin').replace();
	} else if (setPassword == "true") {
		$rootScope.firstName = firstName;
		$rootScope.lastName = lastName;
		$location.path('/password').replace();
	} else {
		var userStageDsiplayOrder = 0;
		var companyStageDsiplayOrder = 0;
		var landingStage = '';
		var registrationStages = JSON.parse('{"INIT":1, "LIN":2, "UPP":3, "CPP":4, "PAY":5, "COM":6}');
		var registrationStagesRoute = JSON.parse('{"1":"/accountsignup", "2":"/linkedin", "3":"/profile", "4":"/company", "5":"/payment", "6":"/signupcomplete"}');
		if (angular.isDefined($rootScope.userId) && angular.isDefined($rootScope.companyId)) {
			UserProfileService.getUserStage($rootScope.userId).then(function(response) {
				userStageDsiplayOrder = registrationStages[response.data];
				CompanyProfileService.getCompanyStage($rootScope.companyId).then(function(response) {
					companyStageDsiplayOrder = registrationStages[response.data];
					if (userStageDsiplayOrder > companyStageDsiplayOrder) {
						landingStage = registrationStagesRoute[userStageDsiplayOrder + 1];
					} else {
						landingStage = registrationStagesRoute[companyStageDsiplayOrder + 1];
					}
					$location.path(landingStage).replace();
				}, function(error) {
					showError($scope.getErrorMessage(error.data));
				});
			}, function(error) {
				showError($scope.getErrorMessage(error.data));
			});
		} else {
			$location.path('/accountsignup').replace();
		}
	}

	$scope.getErrorMessage = function(data) {
		hideOverlay();
		var errorMessage = '';
		if (data.errors != null) {
			angular.forEach(data.errors, function(value, key) {
				errorMessage = errorMessage + value + ", ";
			}, errorMessage);
			var lastspace = errorMessage.lastIndexOf(',');
			if (lastspace != -1) {
				if (errorMessage.charAt(lastspace - 1) == ',') {
					lastspace = lastspace - 1;
				}
				errorMessage = errorMessage.substr(0, lastspace);
			}
		} else {
			errorMessage = data;
		}
		return errorMessage;
	}

	$scope.setPhone = function(phoneId, phone) {
		if (phone != null) {
			$('#' + phoneId).intlTelInput("setCountry", phone.countryAbbr);
			$('#' + phoneId).intlTelInput("setNumber", phone.formattedPhoneNumber);
		}
	}

	$scope.saveLinkedInStage = function() {
		UserProfileService.updateUserStage($rootScope.userId, 'LIN').then(function(response) {
			$rootScope.redirect = false;
			$location.path('/profile').replace();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}
} ]);

app.controller('accountSignupController', [ '$cookies', '$scope', '$location', 'vcRecaptchaService', 'LoginService', '$rootScope', function($cookies, $scope, $location, vcRecaptchaService, LoginService, $rootScope) {
	leftposition();
	$scope.activate = 0;
	$scope.accountRegistration = {};
	$scope.response = null;
	$scope.widgetId = null;
	$scope.isEmailIdExist = false;

	$scope.model = {
		key : '6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K'
	};
	$('#reg-phone').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	});

	$('#reg-phone').mask(phoneFormatWithExtension, usPhoneRegEx);
	$("#reg-phone").on("countrychange", function(e, countryData) {
		maskPhoneNumber("reg-phone", countryData.iso2);
	});

	$scope.submitLogin = function() {
		if ($scope.signInForm.$valid) {
			showOverlay();
			$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
			$scope.accountRegistration.phone = getPhoneNumber("reg-phone");
			$scope.accountRegistration.planId = planId;
			LoginService.signup($scope.accountRegistration).then(function(response) {
				$rootScope.userId = response.data.userId;
				$rootScope.companyId = response.data.companyId;
				$scope.putUserIdCompanyIdInCookie($rootScope.userId, $rootScope.companyId);
				hideOverlay();
				$location.path('/linkedin').replace();
			}, function(error) {
				if (error.data.indexOf("already exists") > -1 && error.data.indexOf("User with Email") > -1) {
					$scope.isEmailIdExist = true;
					hideOverlay();
				} else {
					showError($scope.getErrorMessage(error.data));
				}
			});
		}
	};

	$scope.setResponse = function(response) {
		$scope.activate = 1;
		$scope.response = response;
	};

	$scope.setWidgetId = function(widgetId) {
		$scope.widgetId = widgetId;
	};

	$scope.cbExpiration = function() {
		console.info('Captcha expired. Resetting response object');
		vcRecaptchaService.reload($scope.widgetId);
		$scope.response = null;
	};
} ]);

app.controller('linkedInController', [ '$scope', '$location', '$rootScope', 'LinkedinService', 'UserProfileService', '$window', function($scope, $location, $rootScope, LinkedinService, UserProfileService, $window) {
	leftposition();
	$window.ScopeToShare = $scope;
	if ($rootScope.redirect) {
		if (linkedinResponse != null) {
			if (linkedinResponse == "ok") {
				$scope.saveLinkedInStage();
			} else if (linkedinResponse != null) {
				showError($scope.getErrorMessage("Please try again to connect to LinkedIn or to continue, click on Next"));
			}
		}
	}

	$scope.linkedin = function() {
		LinkedinService.linkedin($rootScope.userId).then(function(response) {
			$scope.linkedinurl = response.data;
			location.href = $scope.linkedinurl;
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	};
} ]);

app.controller('signupcompleteController', [ '$cookies', '$scope', '$rootScope', function($cookies, $scope, $rootScope) {
	leftposition();
	$scope.clearCookie();
	$cookies.remove("planId");

	$scope.login = function() {
		showOverlay();
		window.location = "/registeraccount/newloginas.do?userId=" + $rootScope.userId;
	}

} ]);

app.controller('profileController', [ '$scope', '$http', '$location', 'UserProfileService', '$rootScope', function($scope, $http, $location, UserProfileService, $rootScope) {
	leftposition();
	$('#reg-phone1').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	}).done(function() {
		if (angular.isDefined($rootScope.userProfile)) {
			$scope.setPhone("reg-phone1", $rootScope.userProfile.phone1);
		}
	});
	$('#reg-phone1').mask(phoneFormatWithExtension, usPhoneRegEx);
	$("#reg-phone1").on("countrychange", function(e, countryData) {
		maskPhoneNumber("reg-phone1", countryData.iso2);
	});
	$('#reg-phone2').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	}).done(function() {
		if (angular.isDefined($rootScope.userProfile)) {
			$scope.setPhone("reg-phone2", $rootScope.userProfile.phone2);
		}
	});
	$('#reg-phone2').mask(phoneFormatWithExtension, usPhoneRegEx);
	$("#reg-phone2").on("countrychange", function(e, countryData) {
		maskPhoneNumber("reg-phone2", countryData.iso2);
	});
	$scope.isValidWebAddress = true;

	$scope.validatePhone1 = function() {
		return angular.isDefined($rootScope.userProfile) && angular.isDefined($rootScope.userProfile.phone1) && $rootScope.userProfile.phone1 != null && $rootScope.userProfile.phone1.formattedPhoneNumber != null && $rootScope.userProfile.phone1.formattedPhoneNumber != "";
	}

	$scope.isValidPhone1 = $scope.validatePhone1();

	$scope.loadDropzone = function() {
		if (!angular.isUndefined($rootScope.userProfile)) {
			$("div#profileImg").dropzone({
				url : "/registeraccount/uploaduserprofilelogo.do?userId=" + $rootScope.userId,
				success : function(file, response) {
					$rootScope.userProfile.profilePhotoUrl = response;
				},
				maxFiles : 1,
				maxfilesexceeded : function(file) {
					this.removeAllFiles();
					this.addFile(file);
				},
				init : function() {
					if ($rootScope.userProfile.profilePhotoUrl != null) {
						var fileName = $rootScope.userProfile.profilePhotoUrl.substring($rootScope.userProfile.profilePhotoUrl.lastIndexOf('/') + 1);
						var mockFile = {
							name : fileName,
							size : 12345
						};
						this.options.addedfile.call(this, mockFile);
						this.options.thumbnail.call(this, mockFile, $rootScope.userProfile.profilePhotoUrl);
						mockFile.previewElement.classList.add('dz-success');
						mockFile.previewElement.classList.add('dz-complete');
						this.files[0] = mockFile;
					}

					this.on("addedfile", function() {
						if (this.files[1] != null) {
							this.removeFile(this.files[0]);
						}
					});
					this.on("removedfile", function(file) {
						UserProfileService.removelogo($rootScope.userId).then(function(response) {
							$rootScope.userProfile.profilePhotoUrl = null;
						}, function(error) {
							showError($scope.getErrorMessage(error.data));
						});
					});
				}
			});
		}
	}

	if (angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}) {
		UserProfileService.getUserProfile($rootScope.userId).then(function(response) {
			$rootScope.userProfile = response.data;
			$scope.setPhone("reg-phone1", $rootScope.userProfile.phone1);
			$scope.setPhone("reg-phone2", $rootScope.userProfile.phone2);
			$scope.isValidPhone1 = $scope.validatePhone1();
			$scope.loadDropzone();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.saveProfile = function() {
		if (!$scope.profileForm.$invalid) {
			$location.path('/profiledetail').replace();
		}
	};

	$scope.saveProfileDetails = function() {
		showOverlay();
		$rootScope.userProfile.phone1 = getPhoneNumber("reg-phone1");
		$rootScope.userProfile.phone2 = getPhoneNumber("reg-phone2");
		if ($rootScope.userProfile.website != null && $rootScope.userProfile.website != "") {
			UserProfileService.validateWebAddress($rootScope.userProfile.website).then(function(response) {
				$scope.isValidWebAddress = true;
				$scope.isValidPhone1 = $scope.validatePhone1();
				if ($scope.isValidWebAddress && $scope.isValidPhone1) {
					$scope.updateUserProfile();
				} else {
					hideOverlay();
				}
			}, function(error) {
				hideOverlay();
				$scope.isValidWebAddress = false;
			});
		} else {
			$scope.isValidPhone1 = $scope.validatePhone1();
			if ($scope.isValidPhone1) {
				$scope.updateUserProfile();
			} else {
				hideOverlay();
			}
		}
	};

	$scope.updateUserProfile = function() {
		UserProfileService.updateUserProfile($rootScope.userId, 'UPP', $rootScope.userProfile).then(function(response) {
			hideOverlay();
			$location.path('/company').replace();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.backOnProfile = function() {
		$location.path('/linkedin').replace();
	}

	$scope.backOnProfileDetail = function() {
		$location.path('/profile').replace();
	}
} ]);

app.controller('companyController', [ '$scope', '$location', 'CompanyProfileService', '$rootScope', function($scope, $location, CompanyProfileService, $rootScope) {
	leftposition();
	$scope.usa = true;
	$scope.canada = false;
	$scope.others = false;
	$('#reg-phone-office').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	}).done(function() {
		if (angular.isDefined($rootScope.companyProfile)) {
			$scope.setPhone("reg-phone-office", $rootScope.companyProfile.officePhone);
		}
	});
	$('#reg-phone-office').mask(phoneFormatWithExtension, usPhoneRegEx);
	$('#reg-phone-office').on("countrychange", function(e, countryData) {
		maskPhoneNumber("reg-phone-office", countryData.iso2);
	});
	$("#country").countrySelect();

	$scope.validateOfficePhone = function() {
		return angular.isDefined($rootScope.companyProfile) && angular.isDefined($rootScope.companyProfile.officePhone) && $rootScope.companyProfile.officePhone != null && $rootScope.companyProfile.officePhone.formattedPhoneNumber != null && $rootScope.companyProfile.officePhone.formattedPhoneNumber != "";
	}

	$scope.isValidOfficePhone = $scope.validateOfficePhone();

	$scope.selectCountry = function() {
		$rootScope.companyProfile.address = "";
		$rootScope.companyProfile.city = "";
		$rootScope.companyProfile.state = "";
		$rootScope.companyProfile.zip = "";
		$scope.companydetailsubmittedcanada = false;
		$scope.companydetailsubmitted = false;
		$scope.companydetailsubmittedusa = false;
		var country_code = $("#country").countrySelect("getSelectedCountryData").iso2;
		$scope.initFormByCountry(country_code);
	}

	$scope.initFormByCountry = function(country_code) {
		if (country_code == "ca") {
			$scope.canada = true;
			$scope.others = false;
			$scope.usa = false;
			$('#textarea').css("height", "40px");
		} else if (country_code == "us") {
			$scope.others = false;
			$scope.canada = false;
			$scope.usa = true;
			$('#textarea').css("height", "40px");
		} else {
			$scope.others = true;
			$scope.canada = false;
			$scope.usa = false;
			$('#textarea').css("height", "80px");
		}
	}

	$scope.loadDropzone = function() {
		if (!angular.isUndefined($rootScope.companyProfile)) {
			$("div#logoDrop").dropzone({
				url : "/registeraccount/uploadcompanylogo.do?companyId=" + $rootScope.companyId + "&userId=" + $rootScope.userId,
				success : function(file, response) {
					$rootScope.companyProfile.companyLogo = response;
				},
				maxFiles : 1,
				maxfilesexceeded : function(file) {
					this.removeAllFiles();
					this.addFile(file);
				},
				init : function() {
					if ($rootScope.companyProfile.companyLogo != null) {
						var fileName = $rootScope.companyProfile.companyLogo.substring($rootScope.companyProfile.companyLogo.lastIndexOf('/') + 1);
						var mockFile = {
							name : fileName,
							size : 12345
						};
						this.options.addedfile.call(this, mockFile);
						this.options.thumbnail.call(this, mockFile, $rootScope.companyProfile.companyLogo);
						mockFile.previewElement.classList.add('dz-success');
						mockFile.previewElement.classList.add('dz-complete');
						this.files[0] = mockFile;
					}

					this.on("addedfile", function() {
						if (this.files[1] != null) {
							this.removeFile(this.files[0]);
						}
					});
					this.on("removedfile", function(file) {
						CompanyProfileService.removelogo($rootScope.companyId, $rootScope.userId).then(function(response) {
							$rootScope.companyProfile.companyLogo = null;
						}, function(error) {
							showError($scope.getErrorMessage(error.data));
						});
					});
				}
			});
		}
	}

	if (angular.isUndefined($rootScope.companyProfile) || $rootScope.companyProfile == null || $rootScope.companyProfile == {}) {
		CompanyProfileService.getCompanyProfile($rootScope.companyId).then(function(response) {
			$rootScope.companyProfile = response.data;
			$scope.initCountry();
			$scope.setPhone("reg-phone-office", $rootScope.companyProfile.officePhone);
			$scope.isValidOfficePhone = $scope.validateOfficePhone();
			if ($rootScope.companyProfile.industry.verticalsMasterId < 0) {
				$rootScope.companyProfile.industry = {};
			}
			$scope.loadDropzone();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	if (angular.isUndefined($scope.usStates) || $scope.usStates == null || $scope.usStates == []) {
		CompanyProfileService.getUsStates().then(function(response) {
			$scope.usStates = response.data;
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.initCountry = function() {
		if (angular.isDefined($rootScope.companyProfile) && $rootScope.companyProfile.location.country.code != null) {
			$scope.initFormByCountry($rootScope.companyProfile.location.country.code);
			$("#country").countrySelect("selectCountry", $rootScope.companyProfile.location.country.code);
		}
	}

	if (angular.isUndefined($rootScope.industries) || $rootScope.industries == null || $rootScope.industries == {}) {
		CompanyProfileService.getVerticals().then(function(response) {
			$rootScope.data = response.data;
			var custom = {
				"verticalsMasterId" : -1,
				"verticalName" : "CUSTOM",
				"priorityOrder" : 0
			};
			$rootScope.data.splice(custom, 1);
			$rootScope.industries = [];
			var verticals = [];
			var priority = [];
			for (i in $rootScope.data) {
				if ($rootScope.data[i].priorityOrder != 0) {
					priority.push($rootScope.data[i]);
				} else {
					verticals.push($rootScope.data[i]);
				}
			}
			priority.sort(function(obj1, obj2) {
				return obj1.priorityOrder - obj2.priorityOrder;
			});
			priority.push({
				divider : true
			});
			$rootScope.industries = priority.concat(verticals);
			$scope.ddSelectOptions = $rootScope.industries;
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.ddSelectOptions = $rootScope.industries;

	$scope.saveCompanyProfile = function() {
		if (!$scope.companyForm.$invalid && $rootScope.companyProfile.industry.verticalsMasterId > 0) {
			$location.path('/companydetail').replace();
		}
	};

	$scope.saveCompanyProfileDetails = function() {
		$rootScope.companyProfile.officePhone = getPhoneNumber("reg-phone-office");
		var countryData = $("#country").countrySelect("getSelectedCountryData");
		$rootScope.companyProfile.location.country.code = countryData.iso2;
		$rootScope.companyProfile.location.name = countryData.name;
		if ($scope.canada) {
			$scope.companydetailsubmittedcanada = true;
			$scope.companydetailsubmitted = true;
			$scope.companydetailsubmittedusa = false;
		} else if ($scope.usa) {
			$scope.companydetailsubmittedusa = true;
			$scope.companydetailsubmitted = true;
			$scope.companydetailsubmittedcanada = false;
		} else {
			$scope.companydetailsubmittedusa = false;
			$scope.companydetailsubmittedcanada = false;
			$scope.companydetailsubmitted = true;
		}
		if ($scope.validateCompanyDetailsForm()) {
			showOverlay();
			CompanyProfileService.updateCompanyProfile($rootScope.companyId, $rootScope.userId, 'CPP', $rootScope.companyProfile).then(function(response) {
				hideOverlay();
				$location.path('/payment').replace();
			}, function(error) {
				showError($scope.getErrorMessage(error.data));
			});
		}
	};

	$scope.validateCompanyDetailsForm = function() {
		$scope.isValidOfficePhone = $scope.validateOfficePhone();
		if ($scope.canada) {
			return ($rootScope.companyProfile.address && $rootScope.companyProfile.city && $rootScope.companyProfile.state && $rootScope.companyProfile.zip && $scope.isValidOfficePhone);
		} else if ($scope.usa) {
			return ($rootScope.companyProfile.address && $rootScope.companyProfile.city && $rootScope.companyProfile.state && $rootScope.companyProfile.zip && $scope.isValidOfficePhone);
		} else {
			return ($rootScope.companyProfile.address && $scope.isValidOfficePhone);
		}
	};

	$scope.backOnCompany = function() {
		$location.path('/profiledetail').replace();
	}

	$scope.backOnCompanyDetail = function() {
		$location.path('/company').replace();
	}
} ]);

app.controller('paymentController', [ '$scope', 'PaymentService', '$location', '$rootScope', '$filter', 'PasswordService', function($scope, PaymentService, $location, $rootScope, $filter, PasswordService) {
	leftposition();
	$scope.individual = true;
	$scope.business = false;
	$scope.enterprise = false;

	if (planId == 1) {
		$scope.individual = true;
		$scope.business = false;
		$scope.enterprise = false;
	} else if (planId == 2) {
		$scope.individual = false;
		$scope.business = true;
		$scope.enterprise = false;
	} else if (planId == 3) {
		$scope.individual = false;
		$scope.business = false;
		$scope.enterprise = true;
	}

	$scope.authorize = true;
	$scope.payment = {};
	$scope.expirationMonths = [ {
		"id" : "1",
		"value" : "1 - Jan"
	}, {
		"id" : "2",
		"value" : "2 - Feb"
	}, {
		"id" : "3",
		"value" : "3 - Mar"
	}, {
		"id" : "4",
		"value" : "4 - Apr"
	}, {
		"id" : "5",
		"value" : "5 - May"
	}, {
		"id" : "6",
		"value" : "6 - Jun"
	}, {
		"id" : "7",
		"value" : "7 - Jul"
	}, {
		"id" : "8",
		"value" : "8 - Aug"
	}, {
		"id" : "9",
		"value" : "9 - Sept"
	}, {
		"id" : "10",
		"value" : "10 - Oct"
	}, {
		"id" : "11",
		"value" : "11 - Nov"
	}, {
		"id" : "12",
		"value" : "12 - Dec"
	} ];
	$('#card-number').mask("dddd dddd dddd dddd", digitRegEx);
	$('#cvv').mask("ddd", digitRegEx);
	$('#zip-code').mask("ddddd-dddd", digitRegEx);

	$scope.today = new Date();
	var currentYear = $scope.today.getFullYear();
	var validTillYear = currentYear + 19;
	$scope.expirationYears = [];
	for (var year = currentYear; year <= validTillYear; year++) {
		$scope.expirationYears.push({
			"id" : year + "",
			"value" : year + ""
		});
	}

	var trialEndDate = new Date();
	trialEndDate.setMonth(trialEndDate.getMonth() + 1);

	$scope.alertUser = function() {
		if (!$scope.authorize) {
			showPopUp("Authorize SocialSurvey", "Your authroization for payment is required.  Don't worry, you will not be charged until after your free trial ends on " + formattedDate(trialEndDate) + ".  You may cancel online anytime.");
		}
	}

	$scope.onChangeCreditCardNumber = function() {
		var numberValidation = cardValidator.number($scope.payment.cardNumber);
		if (numberValidation.card != null) {
			if (numberValidation.card.code.size == 3) {
				$('#cvv').mask("ddd", digitRegEx);
				$('#cvv').attr("maxlength", 3);
			} else if (numberValidation.card.code.size == 4) {
				$('#cvv').mask("dddd", digitRegEx);
				$('#cvv').attr("maxlength", 4);
			}
		}
	}

	if (angular.isUndefined($scope.paymentPlans) || $scope.paymentPlans == null || $scope.paymentPlans == {}) {
		PaymentService.getPaymentPlans().then(function(response) {
			$scope.paymentPlans = response.data;
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	if (angular.isUndefined($scope.clientToken) || $scope.clientToken == null || $scope.clientToken == {}) {
		PaymentService.getClientToken().then(function(response) {
			$scope.clientToken = response.data;
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.validateCardDetails = function() {
		var validCardDetails = true;
		var numberValidation = cardValidator.number($scope.payment.cardNumber);
		var expiryMonValidation = cardValidator.expirationMonth($scope.payment.expirationMonth);
		var expiryYearValidation = cardValidator.expirationYear($scope.payment.expirationYear);
		var postalCodeValidation = cardValidator.postalCode($scope.payment.zipCode);
		var cvvValidation1 = cardValidator.cvv($scope.payment.cvv, 3);
		var cvvValidation2 = cardValidator.cvv($scope.payment.cvv, 4);

		if (angular.isUndefined($scope.payment.cardHolderName) || $scope.payment.cardHolderName == "" || $scope.payment.cardHolderName == null) {
			validCardDetails = false;
		}

		if (angular.isUndefined($scope.payment.cardNumber) || !numberValidation.isValid || ($scope.payment.cardNumber.indexOf('3') != 0 && $scope.payment.cardNumber.indexOf('4') != 0 && $scope.payment.cardNumber.indexOf('5') != 0 && $scope.payment.cardNumber.indexOf('6') != 0)) {
			validCardDetails = false;
		}

		if (angular.isUndefined($scope.payment.expirationMonth) || !expiryMonValidation.isValid || (expiryYearValidation.isCurrentYear && $scope.payment.expirationMonth < $scope.today.getMonth() + 1)) {
			validCardDetails = false;
		}

		if (angular.isUndefined($scope.payment.expirationYear) || !expiryYearValidation.isValid || (expiryYearValidation.isCurrentYear && $scope.payment.expirationMonth < $scope.today.getMonth() + 1)) {
			validCardDetails = false;
		}

		if (numberValidation.card != null) {
			if (numberValidation.card.code.size == 3) {
				if (!cvvValidation1.isValid) {
					validCardDetails = false;
				}
			} else if (numberValidation.card.code.size == 4) {
				if (!cvvValidation2.isValid) {
					validCardDetails = false;
				}
			}
		} else if (angular.isUndefined($scope.payment.cvv) || (!cvvValidation1.isValid && !cvvValidation2.isValid)) {
			validCardDetails = false;
		}

		if (angular.isUndefined($scope.payment.zipCode) || !postalCodeValidation.isValid || ($scope.payment.zipCode.length != 5 && $scope.payment.zipCode.length != 10)) {
			validCardDetails = false;
		}

		return validCardDetails;
	}

	$scope.checkCvvValidations = function() {

	}

	$scope.setUpTokenizeBraintreeAndMakePayment = function() {
		var client = new braintree.api.Client({
			clientToken : $scope.clientToken
		});
		client.tokenizeCard({
			number : $scope.payment.cardNumber,
			cardholderName : $scope.payment.cardHolderName,
			expirationMonth : $scope.payment.expirationMonth,
			expirationYear : $scope.payment.expirationYear,
			cvv : $scope.payment.cvv,
			billingAddress : {
				postalCode : $scope.payment.zipCode
			}
		}, function(err, nonce) {
			if (angular.isDefined($scope.selectedPlan) && err == null && nonce != null) {
				var dataToSend = {
					"nonce" : nonce,
					"cardHolderName" : $scope.payment.cardHolderName
				};
				$scope.makePayment($rootScope.companyId, $scope.selectedPlan.planId, dataToSend);
			}
		});
	}

	$scope.back = function() {
		$location.path('/companydetail').replace();
	}

	$scope.processPayment = function() {
		showOverlay();
		if ($scope.individual) {
			$scope.selectedPlan = $filter('filter')($scope.paymentPlans, function(plan) {
				return plan.planName == "Individual";
			})[0];
		} else if ($scope.business) {
			$scope.selectedPlan = $filter('filter')($scope.paymentPlans, function(plan) {
				return plan.planName == "Business";
			})[0];
		} else if ($scope.enterprise) {
			$scope.selectedPlan = $filter('filter')($scope.paymentPlans, function(plan) {
				return plan.planName == "Enterprise";
			})[0];
		}

		if ($scope.individual || $scope.business) {
			var numberValidation = cardValidator.number($scope.payment.cardNumber);
			if (numberValidation.card != null)
				$scope.cvvSize = numberValidation.card.code.size;
			if ($scope.validateCardDetails() && $scope.paymentForm.$valid) {
				if (!$scope.authorize) {
					hideOverlay();
					showPopUp("Authorize SocialSurvey", "Your authroization for payment is required.  Don't worry, you will not be charged until after your free trial ends on " + formattedDate(trialEndDate) + ".  You may cancel online anytime.");
				} else {
					$scope.setUpTokenizeBraintreeAndMakePayment();
				}
			} else {
				hideOverlay();
			}
		} else {
			if ($scope.enterpriseForm.$valid && angular.isDefined($scope.selectedPlan)) {
				var dataToSend = {
					"name" : $scope.payment.name,
					"email" : $scope.payment.email,
					"message" : $scope.payment.message
				};
				$scope.makePayment($rootScope.companyId, $scope.selectedPlan.planId, dataToSend);
			} else {
				hideOverlay();
			}
		}
	}

	$scope.makePayment = function(companyId, planId, dataToSend) {
		PaymentService.makePayment($rootScope.companyId, $scope.selectedPlan.planId, dataToSend).then(function(response) {
			hideOverlay();
			PasswordService.isPasswordAlreadySet($rootScope.userId).then(function(response) {
				$rootScope.completeHeader = "Sign-up Complete";
				$rootScope.welcomeButton = "Get Started";
				$rootScope.download = true;
				$rootScope.signUpCompleteMessage = "Congratulations for starting your FREE trial. We want you to get the most value from this tool. There are still a few tasks to complete before sending survey requests to customers.";
				$rootScope.setPasswordMessage = "Be sure to check your email. We have sent a verification link to you that will need to be clicked to set your account password.";
				if ($scope.selectedPlan.planId == 3) {
					$rootScope.signUpCompleteMessage = "Thankyou for your interest in an Enterprise account. A member of our team will contact you within 1 business day.";
					$rootScope.completeHeader = "Request Sent";
					$rootScope.welcomeButton = "Close";
					$rootScope.signUpCompleteMessageContact = "You may also contact us at 1-888-701-4512 or getstarted@socialsurvey.com";
					$rootScope.download = false;
				}
				if (response.data) {
					$rootScope.setPasswordMessage = "";
				}
				$location.path('/signupcomplete').replace();
			}, function(error) {
				showError($scope.getErrorMessage(error.data));
			});
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.upGrade = function() {
		if ($scope.individual) {
			$scope.individual = false;
			$scope.business = true;
			$scope.enterprise = false;
		} else if ($scope.business) {
			$scope.individual = false;
			$scope.business = false;
			$scope.enterprise = true;
		}
		$scope.authorize = true;
		$scope.payment = {};
		$scope.paymentsubmitted = false;
	};

	$scope.downGrade = function() {
		if ($scope.business) {
			$scope.individual = true;
			$scope.business = false;
			$scope.enterprise = false;
		} else if ($scope.enterprise) {
			$scope.individual = false;
			$scope.business = true;
			$scope.enterprise = false;
		}
		$scope.authorize = true;
		$scope.payment = {};
		$scope.paymentsubmitted = false;
		$scope.entersubmitted = false;
	};
} ]);

app.controller('passwordController', [ '$scope', '$location', '$rootScope', 'PasswordService', function($scope, $location, $rootScope, PasswordService) {
	leftposition();
	$scope.firstName = $rootScope.firstName;
	$scope.lastName = $rootScope.lastName;

	$scope.savePassword = function() {
		if ($scope.passwordForm.$valid) {
			PasswordService.savePassword($rootScope.userId, $scope.password).then(function(response) {
				window.location = "/registeraccount/newloginas.do?userId=" + $rootScope.userId + "&planId=" + planId;
			}, function(error) {
				showError($scope.getErrorMessage(error.data));
			});
		}
	}
} ]);

function showPopUp(header, message) {
	$('#overlay-header').html(header);
	$("#overlay-text").html(message);
	$('#overlay-continue').html("Ok");
	$('#overlay-continue').attr("onclick", "");
	$('#overlay-continue').click(function() {
		$('#overlay-main').hide();
		$('#overlay-continue').unbind('click');
	});
	$('#overlay-main').show();
};

function formattedDate(date) {
	var d = new Date(date || Date.now()), month = '' + (d.getMonth() + 1), day = '' + d.getDate(), year = d.getFullYear();
	if (month.length < 2)
		month = '0' + month;
	if (day.length < 2)
		day = '0' + day;
	return [ month, day, year ].join('/');
};
function leftposition() {
	var rightcontainer = $('.reg-right-container').width();
	var leftcontainer = $('.reg-left-container').width();
	var rightcentermain = $('.reg-right-center').width();
	var rightcenterlinkedin = $('.reg-right-center-linkedin').width();
	var rightcenterprofile = $('.reg-right-center-profile').width();
	var rightcenter;
	var centerelement;
	if (rightcentermain != null) {
		rightcenter = rightcentermain;
		centerelement = '.reg-right-center';
	} else if (rightcenterlinkedin != null) {
		rightcenter = rightcenterlinkedin;
		centerelement = '.reg-right-center-linkedin';
	} else if (rightcenterprofile != null) {
		rightcenter = rightcenterprofile;
		centerelement = '.reg-right-center-profile';
	}
	var left = ((rightcontainer - (leftcontainer + rightcenter)) / 2) + leftcontainer;

	$(centerelement).css('left', left + 'px');
};
