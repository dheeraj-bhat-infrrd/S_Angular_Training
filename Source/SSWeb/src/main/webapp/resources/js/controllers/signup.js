var phoneRegEx = {
        'translation': {
            d: {
                pattern: /[0-9*]/
            },
            /*x:{
	    	 pattern:/[A-Z*]/
	     }*/
        }
    };

app.controller('accountSignupController', ['$scope', '$http', '$location', 'vcRecaptchaService', 'LoginService','$rootScope', function ($scope, $http, $location, vcRecaptchaService, LoginService,$rootScope) {
	$scope.activate = 0;
	$scope.accountRegistration = {};
	$scope.response = null;
    $scope.widgetId = null;
    $scope.model = {key: '6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K'};
    $scope.accountRegisterIds = {};
    
    $scope.submitLogin = function () {
        if (vcRecaptchaService.getResponse() === "") { //if string is empty
            showError("Please resolve the captcha and submit!");
            $scope.activate = 0;
        } else {
        	$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
        	$scope.accountRegistration.phone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
        }
        LoginService.signup($scope.accountRegistration)
            .then(function (response) {
            	$scope.accountRegisterIds = response.data;
            	 $rootScope.userId=response.data.userId;
            	 $rootScope.comanyId=response.data.companyId;
            
            	$scope.register();
            }, function (error) {
            	showError(error);
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
    
    $('#reg-phone').mask(phoneFormat, phoneRegEx);
    
    $scope.register = function () {
    	$location.path('/linkedin').replace();
    };
}]);


app.controller('linkedInController', ['$scope','$http', '$location','$rootScope','LinkedinService', function ($scope,$http, $location,$rootScope,LinkedinService) {
	$scope.linkedin = function (){
		LinkedinService.linkedin($rootScope.userId).then(function(response){
			window.open(response.data, "Authorization Page", "width=800,height=600,scrollbars=yes");
		},function(error){
			console.log(error);
		});
	};
}]);


app.controller('profileController', ['$scope', '$http', '$location', 'UserProfileService', '$rootScope', function ($scope, $http, $location, UserProfileService, $rootScope) {
	if(angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}){
		UserProfileService.getUserProfile(1230).then(function(response){ 
			$rootScope.userProfile = response.data;
		}, function (error) {
		    console.log(error);
		    showError(error);
		});
	}
	
	var myDropzone = null;
	if ( angular.isElement('#my-awesome-dropzone')) {
	    myDropzone = new Dropzone("div#my-awesome-dropzone", {
	        url: "/file/post"
	    });
	}
    
    $scope.saveProfile = function () {
    	$location.path('/profiledetail').replace();
    };
    
    $scope.saveProfileDetails = function () {
    	$rootScope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	UserProfileService.updateUserProfile(1230, 'UPP', $rootScope.userProfile).then(function(response){ 
    		$location.path('/company').replace();
    	}, function (error) {
    	    console.log(error);
    	    showError(error);
    	});
    };
    
    $('#reg-phone1').intlTelInput({
        utilsScript: "../resources/js/utils.js"
    });
    $('#reg-phone1').mask(phoneFormat,phoneRegEx);
    
    $('#reg-phone2').intlTelInput({
        utilsScript: "../resources/js/utils.js"
    });
    $('#reg-phone2').mask(phoneFormat, phoneRegEx);
}]);

app.controller('companyController', ['$scope', '$http', '$location', 'CompanyProfileService', '$rootScope', function ($scope, $http, $location, CompanyProfileService, $rootScope) {
	$scope.countrycode=='ax';
	
	if(angular.isUndefined($rootScope.companyProfile) || $rootScope.companyProfile == null || $rootScope.companyProfile == {}){
		CompanyProfileService.getCompanyProfile(36).then(function(response){ 
			$rootScope.companyProfile = response.data;
		}, function (error) {
		    console.log(error);
		    showError(error);
		});
	}
	
	if(angular.isUndefined($rootScope.industries) || $rootScope.industries == null || $rootScope.industries == {}){
		CompanyProfileService.getVerticals().then(function(response){ 
			$rootScope.industries = response.data;
		}, function (error) {
		    console.log(error);
		    showError(error);
		});
	}
	
	var myDropzone = null;
	if ( angular.isElement('#my-awesome-dropzone')) {
	    myDropzone = new Dropzone("div#my-awesome-dropzone", {
	        url: "/file/post"
	    });
	}
    
    $scope.saveCompanyProfile = function () {
		$location.path('/companydetail').replace();
    };
    
    $scope.saveCompanyProfileDetails = function () {
    	$rootScope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	CompanyProfileService.updateCompanyProfile(36, 'CPP', $rootScope.companyProfile).then(function(response){ 
    		$location.path('/payment').replace();
    	}, function (error) {
    	    console.log(error);
    	    showError(error);
    	});
    };
    
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
	$('#reg-phone-office').mask(phoneFormat, phoneRegEx);
}]);