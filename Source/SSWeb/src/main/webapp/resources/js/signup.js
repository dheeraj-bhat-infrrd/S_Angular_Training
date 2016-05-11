var phoneFormat = '(ddd) ddd-dddd';
function showSigupContent(url) {
	
	closeMoblieScreenMenu();
	saveState(url);
	callAjaxGET(url, showMainContentCallBack, true);
}
var app = angular.module('SocialSurvey',['ngRoute','vcRecaptcha']);
app.config(['$routeProvider', function ($routeProvider) {

    $routeProvider
    .when("/accountsignup", {
        templateUrl: "../../resources/html/accountsignup.html",
        controller: "accountSignupController"
    })
    .when("/linkedin", {
        templateUrl: "../../resources/html/linkedIn.html",
        controller: "linkedInController"
    })
    .when("/profile", {
        templateUrl: "../../resources/html/profile.html",
       controller: "profileController"
    })
    .when("/profiledetail", {
        templateUrl: "../../resources/html/profile-detail.html",
       controller: "profiledetailController"
    })
    .when("/company", {
        templateUrl: "../../resources/html/company.html",
        controller: "companyController"
    })
    .when("/companydetail", {
        templateUrl: "../../resources/html/company-detail.html",
        controller: "companyController"
    }).
    otherwise({
        redirect: '/accountsignup'
    });
}]);
app.controller('linkedInController',['$http','$location',function($http,$location){
	var vm = this;
    vm.title = 'AngularJS for SocialSurvey';
}]);
app.controller('accountSignupController',['$scope','$http','$location','vcRecaptchaService',function($scope,$http,$location,vcRecaptchaService){
	
	console.log("this is your app's controller");
    $scope.response = null;
    $scope.widgetId = null;
	$scope.model = {
            key: '6Le2wQYTAAAAAAacBUn0Dia5zMMyHfMXhoOh5A7K'
        };
	
     $scope.setResponse = function (response) {
         console.info(response);
         $scope.response = response;
     };
     $scope.setWidgetId = function (widgetId) {
         console.info('Created widget ID: %s', widgetId);
         $scope.widgetId = widgetId;
     };
     $scope.cbExpiration = function() {
         console.info('Captcha expired. Resetting response object');
         vcRecaptchaService.reload($scope.widgetId);
         $scope.response = null;
      };
     $scope.submit = function () {
         var valid;
         /**
          * SERVER SIDE VALIDATION
          *
          * You need to implement your server side validation here.
          * Send the reCaptcha response to the server and use some of the server side APIs to validate it
          * See https://developers.google.com/recaptcha/docs/verify
          */
         console.log('sending the captcha response to the server', $scope.response);
         if (valid) {
             console.log('Success');
         } else {
             console.log('Failed validation');
             // In case of a failed validation you need to reload the captcha
             // because each response can be checked just once
             vcRecaptchaService.reload($scope.widgetId);
         }
     };

	
	$('#reg-phone').intlTelInput({
		utilsScript: "../resources/js/utils.js"
	});
	$('#reg-phone').mask(phoneFormat, {
		'translation' : {
			d : {
				pattern : /[0-9*]/
			},
	     /*x:{
	    	 pattern:/[A-Z*]/
	     }*/
		}
	});
	$scope.register =function(){
		if (validateCaptcha.equals(CommonConstants.YES_STRING)){
		 $location.path('/linkedin').replace();
		}
	};
	
}]);

app.controller('profileController',['$scope','$http','$location',function($scope,$http,$location){
	var myDropzone = new Dropzone("div#my-awesome-dropzone", { url: "/file/post"});
	$scope.profileAuthentication=function(fname,lname,title){
		
		console.log(fname,lname,title);
	};
}]);


app.controller('linkedInController',['$http','$location',function($http,$location){
	var vm = this;
    vm.title = 'AngularJS for SocialSurvey';
}]);

app.controller('profiledetailController',['$http','$location',function($http,$location){
	$('#reg-phone1').intlTelInput({
		utilsScript: "../resources/js/utils.js"
	});
	$('#reg-phone1').mask(phoneFormat, {
		'translation' : {
			d : {
				pattern : /[0-9*]/
			},
	     /*x:{
	    	 pattern:/[A-Z*]/
	     }*/
		}
	});
	$('#reg-phone2').intlTelInput({
		utilsScript: "../resources/js/utils.js"
	});
	$('#reg-phone2').mask(phoneFormat, {
		'translation' : {
			d : {
				pattern : /[0-9*]/
			},
	     /*x:{
	    	 pattern:/[A-Z*]/
	     }*/
		}
	});
}]);
