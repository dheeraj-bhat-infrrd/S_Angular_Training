app.controller('newSignupController', ['$scope', '$location', '$rootScope', 'UserProfileService', 'CompanyProfileService','$window', function ($scope, $location, $rootScope, UserProfileService, CompanyProfileService,$window) {
	$rootScope.userId=userId;
	$rootScope.companyId=companyId;
	
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
	}else if($window.opener!=null){
		ParentScope = $window.opener.ScopeToShare;
		location.href = ParentScope.linkedinurl;
		$location.path('/linkedinloader').replace();
	}else{
		$location.path('/accountsignup').replace();
	}
	
	$scope.getErrorMessage = function(data){
		hideOverlay();
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
    $('#reg-phone').intlTelInput({ utilsScript: "../resources/js/utils.js" });
    $('#reg-phone').mask(phoneFormat, phoneRegEx);
    $('#signInForm').on('click', '.country-list', function() {
    	$scope.setPhoneNumber();
    });
    
    $scope.setPhoneNumber = function(){
    	var countryData = $("#reg-phone").intlTelInput("getSelectedCountryData");
        $('#reg-phone').mask(phoneFormatList[countryData.iso2.toUpperCase()], phoneRegEx);
        var number = $("#reg-phone").intlTelInput("getNumber");
        number = number.substring(countryData.dialCode.length + 1, number.length + 1);
        $scope.accountRegistration.phone = {};
        $scope.accountRegistration.phone.number = number;
        $scope.accountRegistration.phone.countryCode = "+"+countryData.dialCode; 
    }
    
    $scope.submitLogin = function () {
        if (vcRecaptchaService.getResponse() == "") { 
            showError("Please resolve the captcha and submit!");
            $scope.activate = 0;
        } else {
        	showOverlay();
        	$scope.accountRegistration.captchaResponse = vcRecaptchaService.getResponse();
        	$scope.setPhoneNumber();
        	LoginService.signup($scope.accountRegistration).then(function (response) {
	           	 $rootScope.userId=response.data.userId;
	           	 $rootScope.companyId=response.data.companyId;
	           	 hideOverlay();
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
        $scope.widgetId = widgetId;
    };
    
    $scope.cbExpiration = function () {
        console.info('Captcha expired. Resetting response object');
        vcRecaptchaService.reload($scope.widgetId);
        $scope.response = null;
    };
}]);


app.controller('linkedInController', ['$scope','$location','$rootScope','LinkedinService', 'UserProfileService','$window', function ($scope, $location,$rootScope,LinkedinService,UserProfileService,$window) {
	$window.ScopeToShare = $scope;
	
	$scope.linkedin = function (){
		LinkedinService.linkedin($rootScope.userId).then(function(response){
		$scope.linkedinurl=response.data;
		window.open("/newaccountsignup.do" ,"Authorization Page", "width=800,height=600,scrollbars=yes");
		},function(error){
			showError($scope.getErrorMessage(error.data));
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
    
//	if(angular.isUndefined($rootScope.userId))
//		$rootScope.userId = 1291;
	
	$('#reg-phone1').intlTelInput({utilsScript: "../resources/js/utils.js"});
    $('#reg-phone1').mask(phoneFormat,phoneRegEx);
    $('#reg-phone2').intlTelInput({utilsScript: "../resources/js/utils.js"});
    $('#reg-phone2').mask(phoneFormat, phoneRegEx);
    
    $scope.loadDropzone = function(){
    	if(!angular.isUndefined($rootScope.userProfile)){
		    $("div#profileImg").dropzone({ 
		    	url: "/registeraccount/uploaduserprofilelogo.do?userId="+$rootScope.userId,
		    	success: function(file, response){
		    		$rootScope.userProfile.profilePhotoUrl = response;
		    	},
				maxFiles: 1,
			    maxfilesexceeded: function(file) {
			        this.removeAllFiles();
			        this.addFile(file);
			    }, 
		    	init: function () {
		    		if($rootScope.userProfile.profilePhotoUrl != null){
			    		var fileName = $rootScope.userProfile.profilePhotoUrl.substring($rootScope.userProfile.profilePhotoUrl.lastIndexOf('/')+1);
			    		var mockFile = { name: fileName, size: 12345 };     
			    		this.options.addedfile.call(this, mockFile);
			    		this.options.thumbnail.call(this, mockFile, $rootScope.userProfile.profilePhotoUrl);
			    		mockFile.previewElement.classList.add('dz-success');
			    		mockFile.previewElement.classList.add('dz-complete');
			    		this.files[0] = mockFile;
		    		}
		    		
		    		this.on( "addedfile", function() { 
		                if ( this.files[1] != null ){ 
		                  this.removeFile( this.files[0] ); 
		                }
		    		});
		    	}
		    });
    	}
    }
    
	if(angular.isUndefined($rootScope.userProfile) || $rootScope.userProfile == null || $rootScope.userProfile == {}){
		UserProfileService.getUserProfile($rootScope.userId).then(function(response){ 
			$rootScope.userProfile = response.data;
			$scope.loadDropzone();
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	$scope.setPhone1 = function (){
		$rootScope.userProfile.phone1.number = $('#reg-phone1').val();
    	$rootScope.userProfile.phone1.countryCode = $('.dial-country-code').html();
	}
	
	$scope.setPhone2 = function (){
		$rootScope.userProfile.phone2.number = $('#reg-phone2').val();
    	$rootScope.userProfile.phone2.countryCode = $('.dial-country-code').html();
	}
	/*$scope.getPhone1 = function(){
		if($rootScope.phone!=""||$rootScope.phone!= undefined){
    		$scope.userProfile.phone1.number= $rootScope.phone;
    		$('#reg-phone1').val($rootScope.userProfile.phone1.number);
    		console.log($('#reg-phone1').val());
    	}
	}*/
    
    $scope.saveProfile = function () {
    	$location.path('/profiledetail').replace();
    	/*$('#reg-phone1').val($rootScope.userProfile.phone1.number);*/
    };
    
    $scope.saveProfileDetails = function () {
    	showOverlay();
    	//$rootScope.userProfile.phone1 = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};
    	UserProfileService.updateUserProfile($rootScope.userId, 'UPP', $rootScope.userProfile).then(function(response){ 
    		 hideOverlay();
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
}]);

app.controller('companyController', ['$scope', '$location', 'CompanyProfileService', '$rootScope', function ($scope, $location, CompanyProfileService, $rootScope) {
	
//	if(angular.isUndefined($rootScope.companyId))
//		$rootScope.companyId = 93;
	
	$scope.usa=true;
	$scope.canada=false;
	$scope.india=false;
	$('#reg-phone-office').intlTelInput({utilsScript: "../resources/js/utils.js"});
	$('#reg-phone-office').mask(phoneFormat, phoneRegEx);
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
	
	$scope.loadDropzone = function(){
		if(!angular.isUndefined($rootScope.companyProfile)){
			$("div#logoDrop").dropzone({ 
				url: "/registeraccount/uploadcompanylogo.do?companyId="+$rootScope.companyId, 
				success: function(file, response){
					$rootScope.companyProfile.companyLogo = response;
		    	},
				maxFiles: 1,
			    maxfilesexceeded: function(file) {
			        this.removeAllFiles();
			        this.addFile(file);
			    }, 
		    	init: function () {
		    		if($rootScope.companyProfile.companyLogo != null){
			    		var fileName = $rootScope.companyProfile.companyLogo.substring($rootScope.companyProfile.companyLogo.lastIndexOf('/')+1);
			    		var mockFile = { name: fileName, size: 12345 };     
			    		this.options.addedfile.call(this, mockFile);
			    		this.options.thumbnail.call(this, mockFile, $rootScope.companyProfile.companyLogo);
			    		mockFile.previewElement.classList.add('dz-success');
			    		mockFile.previewElement.classList.add('dz-complete');
			    		this.files[0] = mockFile;
		    		}
		    		
		    		this.on( "addedfile", function() { 
		                if ( this.files[1] != null ){ 
		                  this.removeFile( this.files[0] ); 
		                }
		    		});
				}
		    });
		}
    }
	
	if(angular.isUndefined($rootScope.companyProfile) || $rootScope.companyProfile == null || $rootScope.companyProfile == {}){
		CompanyProfileService.getCompanyProfile($rootScope.companyId).then(function(response){ 
			$rootScope.companyProfile = response.data;
			$scope.loadDropzone();
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	if(angular.isUndefined($rootScope.industries) || $rootScope.industries == null || $rootScope.industries == {}){
		CompanyProfileService.getVerticals().then(function(response){ 
			//$rootScope.data = response.data;
			$rootScope.data = [{ 	"verticalsMasterId": -1, 	"verticalName": "CUSTOM", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 1, 	"verticalName": "Mortgage", 	"priorityOrder": 1 }, { 	"verticalsMasterId": 2, 	"verticalName": "Real Estate", 	"priorityOrder": 2 }, { 	"verticalsMasterId": 3, 	"verticalName": "Accounting", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 4, 	"verticalName": "Aviation", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 5, 	"verticalName": "Alternative Medicine", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 6, 	"verticalName": "Apparel & Fashion", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 7, 	"verticalName": "Architecture & Planning", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 8, 	"verticalName": "Arts & Crafts", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 9, 	"verticalName": "Automotive", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 10, 	"verticalName": "Banking", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 11, 	"verticalName": "Broadcast Media", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 12, 	"verticalName": "Business Suppliers & Equipment", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 13, 	"verticalName": "Capital Markets", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 14, 	"verticalName": "Civic and Social Organization", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 15, 	"verticalName": "Civil Engineering", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 16, 	"verticalName": "Commercial Real Estate", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 17, 	"verticalName": "Computer Technology", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 18, 	"verticalName": "Construction", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 19, 	"verticalName": "Consumer Electronics", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 20, 	"verticalName": "Consumer Goods", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 21, 	"verticalName": "Consumer Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 22, 	"verticalName": "Cosmetics", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 23, 	"verticalName": "Defence & Space", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 24, 	"verticalName": "Design", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 25, 	"verticalName": "Education", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 26, 	"verticalName": "Electrical/Electronic Manufacturing", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 27, 	"verticalName": "Entertainment", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 28, 	"verticalName": "Environmental Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 29, 	"verticalName": "Events Services", 	"priorityOrder": 4 }, { 	"verticalsMasterId": 30, 	"verticalName": "Executive Office", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 31, 	"verticalName": "Facilities Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 32, 	"verticalName": "Farming", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 33, 	"verticalName": "Financial Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 34, 	"verticalName": "Fine Art", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 35, 	"verticalName": "Fishery", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 36, 	"verticalName": "Food & Beverages", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 37, 	"verticalName": "Food Production", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 38, 	"verticalName": "Fund-Raising", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 39, 	"verticalName": "Furniture", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 40, 	"verticalName": "Gambling & Casinos", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 41, 	"verticalName": "Graphic Design", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 42, 	"verticalName": "Health, Wellness & Fitness", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 43, 	"verticalName": "Higher Education", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 44, 	"verticalName": "Hospital & Health Care", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 45, 	"verticalName": "Hospitality", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 46, 	"verticalName": "Human Resources", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 47, 	"verticalName": "Import & Export", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 48, 	"verticalName": "Individual & Family Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 49, 	"verticalName": "Industrial Automation", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 50, 	"verticalName": "Information Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 51, 	"verticalName": "Information Technology & Services", 	"priorityOrder": 5 }, { 	"verticalsMasterId": 52, 	"verticalName": "Insurance", 	"priorityOrder": 3 }, { 	"verticalsMasterId": 53, 	"verticalName": "Internet", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 54, 	"verticalName": "Investment Banking", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 55, 	"verticalName": "Investment Management", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 56, 	"verticalName": "Law Enforcement", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 57, 	"verticalName": "Law Practice", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 58, 	"verticalName": "Legal Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 59, 	"verticalName": "Legislative Office", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 60, 	"verticalName": "Leisure, Travel & Tourism", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 61, 	"verticalName": "Logistics and Supply Chain", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 62, 	"verticalName": "Machinery", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 63, 	"verticalName": "Management Consulting", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 64, 	"verticalName": "Maritime", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 65, 	"verticalName": "Market Research", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 66, 	"verticalName": "Marketing & Advertisement", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 67, 	"verticalName": "Mechanical & Industrial Engineering", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 68, 	"verticalName": "Media Production", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 69, 	"verticalName": "Medical Devices", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 70, 	"verticalName": "Medical Practice", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 71, 	"verticalName": "Mental Health Care", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 72, 	"verticalName": "Military", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 73, 	"verticalName": "Mining & Metals", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 74, 	"verticalName": "Museums & Institutions", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 75, 	"verticalName": "Music", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 76, 	"verticalName": "Non-Profit Organization", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 77, 	"verticalName": "Oil & Energy", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 78, 	"verticalName": "Online Media", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 79, 	"verticalName": "Outsourcing/Offshoring", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 80, 	"verticalName": "Package/Freight Delivery", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 81, 	"verticalName": "Packaging & Containers", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 82, 	"verticalName": "Paper & Forest Products", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 83, 	"verticalName": "Performing Arts", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 84, 	"verticalName": "Pharmaceuticals", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 85, 	"verticalName": "Philanthropy", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 86, 	"verticalName": "Photography", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 87, 	"verticalName": "Plastics", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 88, 	"verticalName": "Political Organization", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 89, 	"verticalName": "Primary/Secondary Education", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 90, 	"verticalName": "Printing", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 91, 	"verticalName": "Professional Training & Coaching", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 92, 	"verticalName": "Program Development", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 93, 	"verticalName": "Public Relations & Communications", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 94, 	"verticalName": "Publishing", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 95, 	"verticalName": "Recreational Facilities & Services", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 96, 	"verticalName": "Religious Institutions", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 97, 	"verticalName": "Renewables & Environment", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 98, 	"verticalName": "Research", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 99, 	"verticalName": "Restaurants", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 100, 	"verticalName": "Retail", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 101, 	"verticalName": "Security & Investigations", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 102, 	"verticalName": "Sporting Goods", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 103, 	"verticalName": "Sports", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 104, 	"verticalName": "Staffing & Recruiting", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 105, 	"verticalName": "Telecommunications", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 106, 	"verticalName": "Think Tanks", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 107, 	"verticalName": "Translation And Localization", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 108, 	"verticalName": "Transportation/Trucking/Railroad", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 109, 	"verticalName": "Venture Capital & Private Equity", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 110, 	"verticalName": "Veterinary", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 111, 	"verticalName": "Warehousing", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 112, 	"verticalName": "Wholesale", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 113, 	"verticalName": "Wine And Spirits", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 114, 	"verticalName": "Wireless", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 115, 	"verticalName": "Writing and Editing", 	"priorityOrder": 0 }, { 	"verticalsMasterId": 116, 	"verticalName": "Other", 	"priorityOrder": 0 }];
			var custom = { "verticalsMasterId": -1, "verticalName": "CUSTOM", "priorityOrder": 0 };
			$rootScope.data.splice(custom, 1);
			$rootScope.industries=[];
			var verticals=[];
			var priority=[];
			for (i in $rootScope.data)
			{
			  if($rootScope.data[i].priorityOrder!=0){
					priority.push($rootScope.data[i]);
				}else{
					verticals.push($rootScope.data[i]);
				}
			}
			priority.sort(function(obj1, obj2) {
				return obj1.priorityOrder - obj2.priorityOrder;
			});
			priority.push({ divider: true});
			$rootScope.industries=priority.concat(verticals);
			$scope.ddSelectOptions=$rootScope.industries;
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	$scope.ddSelectOptions = $rootScope.industries;
	
    $scope.saveCompanyProfile = function () {
		$location.path('/companydetail').replace();
    };
    
    $scope.saveCompanyProfileDetails = function () {
    	showOverlay();
    	/*$rootScope.companyProfile.officePhone = {"countryCode" : "1", "number" : "1234567890", "extension" : "12"};*/
    	CompanyProfileService.updateCompanyProfile($rootScope.companyId, 'CPP', $rootScope.companyProfile).then(function(response){ 
    		 hideOverlay();
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
}]);


app.controller('paymentController', ['$scope','PaymentService', '$location','$rootScope', function ($scope,PaymentService, $location,$rootScope) {
	
	$scope.individual=true;
	$scope.business=false;
	$scope.enterprise=false;
	$scope.authorize = true;
	
	$scope.alertUser = function(){
		if(!$scope.authorize){
			alert("You have to authorize SocialSurvey to debit your credit card for the monthly subscription fees.");
		}
	}
	
	if(angular.isUndefined($scope.paymentPlans) || $scope.paymentPlans == null || $scope.paymentPlans == {}){
		PaymentService.getPaymentPlans().then(function(response){ 
			$scope.paymentPlans = response.data;
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	if(angular.isUndefined($scope.clientToken) || $scope.clientToken == null || $scope.clientToken == {}){
		PaymentService.getClientToken().then(function(response){ 
			$scope.clientToken = response.data;
			braintree.setup($scope.clientToken, 'dropin', {
				container : 'dropin'
			});
		}, function (error) {
		    showError($scope.getErrorMessage(error.data));
		});
	}
	
	$scope.back = function (){
    	$location.path('/companydetail').replace();
    }
	
	$scope.processPayment = function (){
		if($scope.individual || $scope.business){
			if(!$scope.authorize){
				alert("You have to authorize SocialSurvey to debit your credit card for the monthly subscription fees.");
			}else{
				// TODO Check if payment has been made by checking an entry is present in license detail..
				console.log("Check if payment has been made by checking an entry is present in license detail..");
				$location.path('/signupcomplete').replace();
			}
		}else{
			$location.path('/signupcomplete').replace();
		}
	}
	
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
	    $scope.authorize = false;
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
	    $scope.name=null;
	    $scope.email=null;
	    $scope.message=null;
	  };
}]);
