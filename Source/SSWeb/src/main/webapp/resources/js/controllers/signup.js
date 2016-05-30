app.controller('newSignupController', [ '$scope', '$location', '$rootScope', 'UserProfileService', 'CompanyProfileService', '$window', function($scope, $location, $rootScope, UserProfileService, CompanyProfileService, $window) {
	$rootScope.userId = userId;
	$rootScope.companyId = companyId;

	if (!angular.isUndefined($rootScope.userId) && !angular.isUndefined($rootScope.companyId)) {
		var userStageDsiplayOrder = 0;
		var companyStageDsiplayOrder = 0;
		var landingStage = '';
		var registrationStages = JSON.parse('{"INIT":1, "LIN":2, "UPP":3, "CPP":4, "COM":5}');
		var registrationStagesRoute = JSON.parse('{"1":"/accountsignup", "2":"/linkedin", "3":"/profile", "4":"/company", "5":"/payment"}');

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
	} else if ($window.opener != null) {
		ParentScope = $window.opener.ScopeToShare;
		location.href = ParentScope.linkedinurl;
		$location.path('/linkedinloader').replace();
	} else {
		$location.path('/accountsignup').replace();
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
} ]);

app.controller('accountSignupController', [ '$scope', '$location', 'vcRecaptchaService', 'LoginService', '$rootScope', function($scope, $location, vcRecaptchaService, LoginService, $rootScope) {
	$scope.activate = 0;
	$scope.accountRegistration = {};
	$scope.response = null;
	$scope.widgetId = null;
	$scope.emailFormat = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	$scope.model = {
		key : '6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K'
	};
	$('#reg-phone').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	});
	$('#reg-phone').mask(phoneFormat, phoneRegEx);
	$("#reg-phone").on("countrychange", function(e, countryData) {
		$scope.maskPhoneNumber("reg-phone", countryData);
	});

	$scope.maskPhoneNumber = function(phoneId, countryData) {
		if (countryData.iso2 == 'us') {
			$('#' + phoneId).mask(phoneFormat, phoneRegEx);
		} else {
			$('#' + phoneId).mask(phoneFormatList[countryData.iso2.toUpperCase()], phoneRegEx);
		}
	}

	$scope.getPhoneNumber = function(phoneId) {
		var countryData = $('#' + phoneId).intlTelInput("getSelectedCountryData");
		var number = $('#' + phoneId).intlTelInput("getNumber");
		if (number.indexOf("+1") != -1) {
			number = number.substring(2, number.length + 1);
		} else {
			number = number.substring(countryData.dialCode.length + 1, number.length + 1);
		}
		return {
			"number" : number,
			"countryCode" : "+" + countryData.dialCode,
			"extension" : $('#' + phoneId).intlTelInput("getExtension")
		};
	}

	$scope.submitLogin = function() {
		if (vcRecaptchaService.getResponse() == "") {
			showError("Let's make sure you are a real person, please check the box beside I'm not a robot! ");
			$scope.activate = 0;
		} else {
			showOverlay();
			$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
			$scope.accountRegistration.phone = $scope.getPhoneNumber("reg-phone");
			LoginService.signup($scope.accountRegistration).then(function(response) {
				$rootScope.userId = response.data.userId;
				$rootScope.companyId = response.data.companyId;
				hideOverlay();
				$location.path('/linkedin').replace();
			}, function(error) {
				showError($scope.getErrorMessage(error.data));
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
	$window.ScopeToShare = $scope;

	$scope.linkedin = function() {
		LinkedinService.linkedin($rootScope.userId).then(function(response) {
			$scope.linkedinurl = response.data;
			window.open("/newaccountsignup.do", "Authorization Page", "width=800,height=600,scrollbars=yes");
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	};

	$scope.saveLinkedInStage = function() {
		UserProfileService.updateUserStage($rootScope.userId, 'LIN').then(function(response) {
			$location.path('/profile').replace();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}
} ]);

app.controller('signupcompleteController', [ '$scope', '$location', '$rootScope', 'LinkedinService', 'UserProfileService', '$window', function($scope, $location, $rootScope, LinkedinService, UserProfileService, $window) {

	// select parent Window
	var parentWindow;
	if (window.opener != null && !window.opener.closed) {
		parentWindow = window.opener;
	} else {
	}

	// close on error
	var error = "${error}";
	if (parseInt(error) == 1) {
		setTimeout(function() {
			window.close();
		}, 3000);
	}

	// close on success
	setTimeout(function() {
		window.close();
	}, 3000);
} ]);

app.controller('profileController', [ '$scope', '$http', '$location', 'UserProfileService', '$rootScope', function($scope, $http, $location, UserProfileService, $rootScope) {

	// if(angular.isUndefined($rootScope.userId))
	// $rootScope.userId = 1291;

	$('#reg-phone1').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	});
	$('#reg-phone1').mask(phoneFormat, phoneRegEx);
	$('#reg-phone2').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	});
	$('#reg-phone2').mask(phoneFormat, phoneRegEx);

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

					});
				}
			});
		}
	}

	if (angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}) {
		UserProfileService.getUserProfile($rootScope.userId).then(function(response) {
			$rootScope.userProfile = response.data;
			$scope.loadDropzone();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.setPhone1 = function() {
		$rootScope.userProfile.phone1.number = $('#reg-phone1').val();
		$rootScope.userProfile.phone1.countryCode = $('.dial-country-code').html();
	}

	$scope.setPhone2 = function() {
		$rootScope.userProfile.phone2.number = $('#reg-phone2').val();
		$rootScope.userProfile.phone2.countryCode = $('.dial-country-code').html();
	}
	/*
	 * $scope.getPhone1 = function(){ if($rootScope.phone!=""||$rootScope.phone!= undefined){ $scope.userProfile.phone1.number= $rootScope.phone; $('#reg-phone1').val($rootScope.userProfile.phone1.number); console.log($('#reg-phone1').val()); } }
	 */

	$scope.saveProfile = function() {
		$location.path('/profiledetail').replace();
		/* $('#reg-phone1').val($rootScope.userProfile.phone1.number); */
	};

	$scope.saveProfileDetails = function() {
		showOverlay();
		// $rootScope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
		UserProfileService.updateUserProfile($rootScope.userId, 'UPP', $rootScope.userProfile).then(function(response) {
			hideOverlay();
			$location.path('/company').replace();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	};

	$scope.backOnProfile = function() {
		$location.path('/linkedin').replace();
	}

	$scope.backOnProfileDetail = function() {
		$location.path('/profile').replace();
	}
} ]);

app.controller('companyController', [ '$scope', '$location', 'CompanyProfileService', '$rootScope', function($scope, $location, CompanyProfileService, $rootScope) {

	// if (angular.isUndefined($rootScope.companyId))
	// $rootScope.companyId = 93;

	$scope.usa = true;
	$scope.canada = false;
	$scope.india = false;
	$('#reg-phone-office').intlTelInput({
		utilsScript : "../resources/js/utils.js"
	});
	$('#reg-phone-office').mask(phoneFormat, phoneRegEx);
	$("#country").countrySelect();
	$scope.selectCountry = function() {
		var country_code = $('#country_code').val();
		if (country_code == "ca") {
			$scope.canada = true;
			$scope.india = false;
			$scope.usa = false;
		} else if (country_code == "in") {
			$scope.india = true;
			$scope.canada = false;
			$scope.usa = false;
		} else {
			$scope.usa = true;
			$scope.india = false;
			$scope.canada = false;
		}
	}

	$scope.loadDropzone = function() {
		if (!angular.isUndefined($rootScope.companyProfile)) {
			$("div#logoDrop").dropzone({
				url : "/registeraccount/uploadcompanylogo.do?companyId=" + $rootScope.companyId,
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
				}
			});
		}
	}

	if (angular.isUndefined($rootScope.companyProfile) || $rootScope.companyProfile == null || $rootScope.companyProfile == {}) {
		CompanyProfileService.getCompanyProfile($rootScope.companyId).then(function(response) {
			$rootScope.companyProfile = response.data;
			if ($rootScope.companyProfile.industry.verticalsMasterId < 0) {
				$rootScope.companyProfile.industry = {};
			}
			$scope.loadDropzone();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
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
		$location.path('/companydetail').replace();
	};

	$scope.saveCompanyProfileDetails = function() {
		showOverlay();
		/* $rootScope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"}; */
		CompanyProfileService.updateCompanyProfile($rootScope.companyId, 'CPP', $rootScope.companyProfile).then(function(response) {
			hideOverlay();
			$location.path('/payment').replace();
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	};

	$scope.backOnCompany = function() {
		$location.path('/profiledetail').replace();
	}

	$scope.backOnCompanyDetail = function() {
		$location.path('/company').replace();
	}
} ]);

app.controller('paymentController', [ '$scope', 'PaymentService', '$location', '$rootScope', function($scope, PaymentService, $location, $rootScope) {

	$scope.individual = true;
	$scope.business = false;
	$scope.enterprise = false;
	$scope.authorize = true;

	$scope.alertUser = function() {
		if (!$scope.authorize) {
			showPopUp("Authorize SocialSurvey", "You have to authorize SocialSurvey to debit your credit card for the monthly subscription fees.");
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
			braintree.setup($scope.clientToken, 'custom', {
				id : "paymentForm",
				hostedFields : {
					number : {
						selector : "#card-number"
					},
					cvv : {
						selector : "#cvv"
					},
					expirationMonth : {
						selector : "#expiration-month"
					},
					expirationYear : {
						selector : "#expiration-year"
					},
					postalCode : {
						selector : "#zip-code"
					}
				},
				onError : function(error) {

				}
			});
		}, function(error) {
			showError($scope.getErrorMessage(error.data));
		});
	}

	$scope.back = function() {
		$location.path('/companydetail').replace();
	}

	$scope.processPayment = function() {
		if ($scope.individual || $scope.business) {
			if (!$scope.authorize) {
				showPopUp("Authorize SocialSurvey", "Your authroization for payment is required.  Don't worry, you will not be charged until after your free trial ends on [DD/YY/YYYY].  You may cancel online anytime.");
			} else {
				// TODO Check if payment has been made by checking an entry is present in license detail..
				console.log("Check if payment has been made by checking an entry is present in license detail..");
				$location.path('/signupcomplete').replace();
			}
		} else {
			$location.path('/signupcomplete').replace();
		}
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
		$scope.name = null;
		$scope.email = null;
		$scope.message = null;
		$scope.authorize = true;
	};
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
}
