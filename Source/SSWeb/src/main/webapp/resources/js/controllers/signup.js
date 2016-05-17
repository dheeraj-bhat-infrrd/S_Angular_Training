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
	$scope.linkedin=function(){
		console.log("!!!!!!!!!!"+$rootScope.userId);
		var dataTosend={
				"usedId":$rootScope.userId
		};
	LinkedinService.linkedin($rootScope.userId)
	.then(function(response){
		window.open(response.data, "Authorization Page", "width=800,height=600,scrollbars=yes");
	},function(error){
		
	});
	};
	
}]);


app.controller('profileController', ['$scope', '$http', '$location', 'UserProfileService', function ($scope, $http, $location, UserProfileService) {
	if(angular.isUndefined($scope.userProfile) || $scope.userProfile == null || $scope.userProfile == {}){
		UserProfileService.getUserProfile(1230).then(function(response){ 
			$scope.userProfile = response.data;
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
    	$scope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	UserProfileService.updateUserProfile(1230, 'UPP', $scope.userProfile).then(function(response){ 
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

app.controller('companyController', ['$scope', '$http', '$location', 'CompanyProfileService', function ($scope, $http, $location, CompanyProfileService) {
	$scope.countrycode=='ax';
	
	if(angular.isUndefined($scope.companyProfile) || $scope.companyProfile == null || $scope.companyProfile == {}){
		CompanyProfileService.getCompanyProfile(36).then(function(response){ 
			$scope.companyProfile = response.data;
		}, function (error) {
		    console.log(error);
		    showError(error);
		});
	}
	
	if(angular.isUndefined($scope.industries) || $scope.industries == null || $scope.industries == {}){
		CompanyProfileService.getVerticals().then(function(response){ 
			$scope.industries = response.data;
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
    	$scope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	CompanyProfileService.updateCompanyProfile(36, 'CPP', $scope.companyProfile).then(function(response){ 
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