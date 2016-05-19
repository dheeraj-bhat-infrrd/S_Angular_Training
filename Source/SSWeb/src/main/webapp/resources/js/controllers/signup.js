app.controller('newSignupController', ['$scope', '$location', '$rootScope', 'UserProfileService', 'CompanyProfileService', function ($scope, $location, $rootScope, UserProfileService, CompanyProfileService) {
	$rootScope.userId=userId;
	$rootScope.companyId=companyId;
	
//	$rootScope.userId=1256;
//	$rootScope.companyId=59;
	
	$scope.phoneRegEx = {
        'translation': {
            d: {
                pattern: /[0-9*]/
            },
            /*x:{
	    	 pattern:/[A-Z*]/
	     }*/
        }
    };
	
	$scope.phoneFormat = '(ddd) ddd-dddd';
	
	if(!angular.isUndefined($rootScope.userId) && !angular.isUndefined($rootScope.companyId)){
		var userStageDsiplayOrder = 0;
		var companyStageDsiplayOrder = 0;
		var landingStage = '';
		var registrationStages = JSON.parse('{"INIT":1, "LIN":2, "UPP":3, "CPP":4, "COM":5}');
		var registrationStagesRoute = JSON.parse('{"1":"/accountsignup", "2":"/linkedin", "3":"/profile", "4":"/company", "5":"/payment"}');
		
		UserProfileService.getUserStage($rootScope.userId).then(function(response){
			userStageDsiplayOrder = registrationStages[response.data];
			CompanyProfileService.getCompanyStage($rootScope.companyId).then(function(response){
				companyStageDsiplayOrder = registrationStages[response.data];
				if(userStageDsiplayOrder > companyStageDsiplayOrder){
					landingStage = registrationStagesRoute[userStageDsiplayOrder + 1];
				}else {
					landingStage = registrationStagesRoute[companyStageDsiplayOrder + 1];
				}
				$location.path(landingStage).replace();
			}, function (error){
				showError($scope.getErrorMessage(error.data));
			});
		}, function (error){
			showError($scope.getErrorMessage(error.data));
		});
	}
	
	$scope.getErrorMessage = function(data){
		var errorMessage = '';
		if(data.errors != null){
			angular.forEach(data.errors, function(value, key) {
				errorMessage = errorMessage + value + ", ";
			}, errorMessage);
			var lastspace = errorMessage.lastIndexOf(',');
	        if (lastspace != -1) {
				if (errorMessage.charAt(lastspace-1) == ',') {
					lastspace = lastspace - 1;
				}
				errorMessage = errorMessage.substr(0, lastspace);
	        }
		}else{
			errorMessage = data;
		}
		return errorMessage;
	}
}]);


app.controller('accountSignupController', ['$scope', '$location', 'vcRecaptchaService', 'LoginService','$rootScope', function ($scope, $location, vcRecaptchaService, LoginService,$rootScope) {
	$scope.activate = 0;
	$scope.accountRegistration = {};
	$scope.response = null;
    $scope.widgetId = null;
    $scope.model = {key: '6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K'};
    $scope.accountRegisterIds = {};
    $scope.countryCode=$('.dial-country-code').html();
    
    $scope.submitLogin = function () {
    	console.log(vcRecaptchaService.getResponse());
        if (vcRecaptchaService.getResponse() == "") { //if string is empty
            showError("Please resolve the captcha and submit!");
            $scope.activate = 0;
        } else {
        	$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
        	$scope.accountRegistration.phone = { "number" : "1234567890", "extension" : "12"};
        	$scope.accountRegistration.phone.countryCode=$('.dial-country-code').html();
        }
        LoginService.signup($scope.accountRegistration).then(function (response) {
        	 $rootScope.userId=response.data.userId;
        	 $rootScope.companyId=response.data.companyId;
        	 $location.path('/linkedin').replace();
        }, function (error) {
        	showError($scope.getErrorMessage(error.data));
        });
    };

    $scope.setResponse = function (response) {
        $scope.activate = 1;
        $scope.response = response;
    };
    
    $scope.setWidgetId = function (widgetId) {
        console.info('Created widget ID: %s', widgetId);
        $scope.widgetId = widgetId;
    };
    
    $scope.cbExpiration = function () {
        console.info('Captcha expired. Resetting response object');
        vcRecaptchaService.reload($scope.widgetId);
        $scope.response = null;
    };
    
    $('#reg-phone').intlTelInput({
        utilsScript: "../resources/js/utils.js"
    });
    
    $('#reg-phone').mask($scope.phoneFormat, $scope.phoneRegEx);
}]);


app.controller('linkedInController', ['$scope','$location','$rootScope','LinkedinService', 'UserProfileService', function ($scope, $location,$rootScope,LinkedinService,UserProfileService) {
	
	$scope.linkedin = function (){
		LinkedinService.linkedin($rootScope.userId).then(function(response){
			window.open(response.data, "Authorization Page", "width=800,height=600,scrollbars=yes");
		},function(error){
			/*var win = window.open(response.data, "Authorization Page", "width=800,height=600,scrollbars=yes");
			setTimeout(function () { win.close();}, 3000);*/
		});
	};
	
	$scope.saveLinkedInStage = function (){
		UserProfileService.updateUserStage($rootScope.userId, 'LIN').then(function(response){
			$location.path('/profile').replace();
		}, function(error){
			showError($scope.getErrorMessage(error.data));
		});
	}
}]);


app.controller('profileController', ['$scope', '$location', 'UserProfileService', '$rootScope', function ($scope, $location, UserProfileService, $rootScope) {
	
	if(angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}){
		UserProfileService.getUserProfile($rootScope.userId).then(function(response){ 
			$rootScope.userProfile = response.data;
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	/*var myDropzone = null;
	if ( angular.isElement('#my-awesome-dropzone')) {
	    myDropzone = new Dropzone("div#my-awesome-dropzone", {
	        url: "/file/post"
	    });
	}*/
	$("div#profileImg").dropzone({ url: "/file/post" });
    
    $scope.saveProfile = function () {
    	$location.path('/profiledetail').replace();
    };
    
    $scope.saveProfileDetails = function () {
    	$rootScope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	UserProfileService.updateUserProfile($rootScope.userId, 'UPP', $rootScope.userProfile).then(function(response){ 
    		$location.path('/company').replace();
    	}, function (error) {
    	    showError($scope.getErrorMessage(error.data));
    	});
    };
    
    $scope.backOnProfile = function (){
    	$location.path('/linkedin').replace();
    }
    
    $scope.backOnProfileDetail = function (){
    	$location.path('/profile').replace();
    }
    
    $('#reg-phone1').intlTelInput({
        utilsScript: "../resources/js/utils.js"
    });
    $('#reg-phone1').mask($scope.phoneFormat,$scope.phoneRegEx);
    
    $('#reg-phone2').intlTelInput({
        utilsScript: "../resources/js/utils.js"
    });
    $('#reg-phone2').mask($scope.phoneFormat, $scope.phoneRegEx);
}]);

app.controller('companyController', ['$scope', '$location', 'CompanyProfileService', '$rootScope', function ($scope, $location, CompanyProfileService, $rootScope) {
	$scope.countrycode=='ax';
	
	if(angular.isUndefined($rootScope.companyProfile) || $rootScope.companyProfile == null || $rootScope.companyProfile == {}){
		CompanyProfileService.getCompanyProfile($rootScope.companyId).then(function(response){ 
			$rootScope.companyProfile = response.data;
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	if(angular.isUndefined($rootScope.industries) || $rootScope.industries == null || $rootScope.industries == {}){
		CompanyProfileService.getVerticals().then(function(response){ 
			$rootScope.industries = response.data;
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	/*var myDropzone = null;
	if ( angular.isElement('div#my-awesome-dropzone')) {
	    myDropzone = new Dropzone("div#my-awesome-dropzone", {
	        url: "/file/post"
	    });
	}
    */
	
	$("div#logoDrop").dropzone({ url: "/file/post" });
    $scope.saveCompanyProfile = function () {
		$location.path('/companydetail').replace();
    };
    
    $scope.saveCompanyProfileDetails = function () {
    	$rootScope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	CompanyProfileService.updateCompanyProfile($rootScope.companyId, 'CPP', $rootScope.companyProfile).then(function(response){ 
    		$location.path('/payment').replace();
    	}, function (error) {
    	    showError($scope.getErrorMessage(error.data));
    	});
    };
    
    $scope.backOnCompany = function (){
    	$location.path('/profiledetail').replace();
    }
    
    $scope.backOnCompanyDetail = function (){
    	$location.path('/company').replace();
    }
    
	if( $scope.countrycode=='us'){
		$scope.State='State';
		$scope.ZIP='ZIP';
	}else if( $scope.countrycode=='ca'){
		$scope.State='Province';
		$scope.ZIP='Postal Code';
	}else{
		$scope.State='State';
		$scope.ZIP='ZIP';
	}
	    
	$("#country").countrySelect();
	
	$('#reg-phone-office').intlTelInput({
	    utilsScript: "../resources/js/utils.js"
	});
	$('#reg-phone-office').mask($scope.phoneFormat, $scope.phoneRegEx);
}]);

app.controller('paymentController', ['$scope','$http', '$location','$rootScope','PaymentService', function ($scope,$http, $location,$rootScope,PaymentService) {
	$scope.plan=="individual";
	$scope.togglePlan = function() {
	    var plans = $scope.plan;
	    if(plans=="individual"){
	    }
	};
}]);