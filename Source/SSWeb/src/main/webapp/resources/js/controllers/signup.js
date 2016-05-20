app.controller('newSignupController', ['$scope', '$location', '$rootScope', 'UserProfileService', 'CompanyProfileService', function ($scope, $location, $rootScope, UserProfileService, CompanyProfileService) {
	/*$rootScope.userId=userId;
	$rootScope.companyId=companyId;
	*/
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
	}else{
		$location.path('/accountsignup').replace();
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
    
    $scope.submitLogin = function () {
    	console.log(vcRecaptchaService.getResponse());
        if (vcRecaptchaService.getResponse() == "") { //if string is empty
            showError("Please resolve the captcha and submit!");
            $scope.activate = 0;
        } else {
        	$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
        	$scope.accountRegistration.phone.number = $('#reg-phone').val();
        	$scope.accountRegistration.phone.countryCode = $('.dial-country-code').html();
        	
        	LoginService.signup($scope.accountRegistration).then(function (response) {
           	 $rootScope.userId=response.data.userId;
           	 $rootScope.companyId=response.data.companyId;
           	 $location.path('/linkedin').replace();
           }, function (error) {
           	showError($scope.getErrorMessage(error.data));
           });
        }
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


app.controller('linkedInController', ['$scope','$location','$rootScope','LinkedinService', 'UserProfileService','$window', function ($scope, $location,$rootScope,LinkedinService,UserProfileService,$window) {
	$window.ScopeToShare = $scope;
	$scope.linkedin = function (){
		window.open("/newaccountsignup.do#/signupcomplete" ,"Authorization Page", "width=800,height=600,scrollbars=yes");
		LinkedinService.linkedin($rootScope.userId).then(function(response){
			/*window.open(response.data, "Authorization Page", "width=800,height=600,scrollbars=yes");*/
			
		$scope.linkedin=response.data;
				
			
		},function(error){
			showError($scope.getErrorMessage(error.data));
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
app.controller('signupcompleteController', ['$scope','$location','$rootScope','LinkedinService', 'UserProfileService','$window', function ($scope, $location,$rootScope,LinkedinService,UserProfileService,$window) {
	
			
			var waitMessage = "${message}";
			
				if($window.parent != null)
				{
				ParentScope = $window.opener.ScopeToShare;
				location.href = ParentScope.linkedin;
				}
				else {
				}
			
			
			// select parent Window
			var parentWindow;
			if (window.opener != null && !window.opener.closed) {
				parentWindow = window.opener;
			}
			else {
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
		
	
	
	
	
}]);




app.controller('profileController', ['$scope', '$http', '$location', 'UserProfileService', '$rootScope', function ($scope, $http, $location, UserProfileService, $rootScope) {
	
	/*$rootScope.userId = 301;*/


	
	if(angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}){
		UserProfileService.getUserProfile($rootScope.userId).then(function(response){ 
			$rootScope.userProfile = response.data;
			$('#reg-phone1').val($rootScope.userProfile.phone1.number);
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	$("div#profileImg").dropzone({ url: "/file/post" });
	
	$scope.setPhone1 = function (){
		$rootScope.userProfile.phone1.number = $('#reg-phone1').val();
    	$rootScope.userProfile.phone1.countryCode = $('.dial-country-code').html();
	}
	
	$scope.setPhone2 = function (){
		$rootScope.userProfile.phone2.number = $('#reg-phone2').val();
    	$rootScope.userProfile.phone2.countryCode = $('.dial-country-code').html();
	}
    
    $scope.saveProfile = function () {
    	$location.path('/profiledetail').replace();
    };
    
    $scope.saveProfileDetails = function () {
    	//$rootScope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	console.log($rootScope.userProfile);
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
	/*$scope.countrycode=='ax';*/
	$scope.usa=true;
	 $scope.canada=false;
	 $scope.india=false;
 
	/*$rootScope.companyId=196;*/
	

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
	
	$("div#logoDrop").dropzone({ url: "/file/post" });
    $scope.saveCompanyProfile = function () {
		$location.path('/companydetail').replace();
    };
    
    $scope.saveCompanyProfileDetails = function () {
    	/*$rootScope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};*/
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
    
	
	    
	$("#country").countrySelect();
	$scope.selectCountry=function(){
		var country_code=$('#country_code').val();
		 if(country_code =="ca"){
			 $scope.canada=true;
			 $scope.india=false;
			 $scope.usa=false;
		 }else if(country_code =="in"){
			 $scope.india=true;
			 $scope.canada=false;
			 $scope.usa=false;
		 }else{
			 $scope.usa=true;
			 $scope.india=false;
			 $scope.canada=false;
		 }
	}
	
	
	$('#reg-phone-office').intlTelInput({
	    utilsScript: "../resources/js/utils.js"
	});
	$('#reg-phone-office').mask($scope.phoneFormat, $scope.phoneRegEx);
}]);


app.controller('paymentController', ['$scope','$http', '$location','$rootScope', function ($scope,$http, $location,$rootScope) {
	
	$scope.individual=true;
	$scope.business=false;
	$scope.enterprise=false;
	$scope.upGrade = function() {
	    
	    if($scope.individual){
	    	$scope.individual=false;
	    	$scope.business=true;
	    	$scope.enterprise=false;
	    }else if($scope.business){
	    	$scope.individual=false;
	    	$scope.business=false;
	    	$scope.enterprise=true;
	    }
	  };
	  $scope.downGrade = function() {
		    
		     if($scope.business){
		    	$scope.individual=true;
		    	$scope.business=false;
		    	$scope.enterprise=false;
		    }else if($scope.enterprise){
		    	$scope.individual=false;
		    	$scope.business=true;
		    	$scope.enterprise=false;
		    }
		  };
		  $scope.creditcardRegEx = {
			        'translation': {
			            d: {
			                pattern: /[0-9*]/
			            }
			           
			        }
			    };
		  $scope.expiryRegEx = {
			        'translation': {
			            d: {
			                pattern: /[0-9*]/
			            }
			           
			        }
			    };
$('#creditcard').mask(creditcardFormat, $scope.creditcardRegEx);
$('#expiryDate').mask(expiryDateFormat, $scope.expiryRegEx);
}]);



	
