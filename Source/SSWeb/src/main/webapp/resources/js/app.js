var phoneFormat = '(ddd) ddd-dddd';
var creditcardFormat ='dddd-dddd-dddd-dddd';
var expiryDateFormat = 'dd/dd';
var phoneRegEx = {
        'translation': {
            d: {pattern: /[0-9*]/},
            //x:{pattern:/[A-Z*]/}
        }
    };

var app = angular.module('SocialSurvey',['ngRoute','vcRecaptcha','ngDropdowns'])
.run(function($rootScope) {
    $rootScope.userId;
    $rootScope.comanyId;
});
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
       controller: "profileController"
    })
    .when("/company", {
        templateUrl: "../../resources/html/company.html",
        controller: "companyController"
    })
    .when("/companydetail", {
        templateUrl: "../../resources/html/company-detail.html",
        controller: "companyController"
    })
    .when("/payment",{
    	templateUrl:"../../resources/html/payment.html",
    	controller:"paymentController"
    })
    .when("/signupcomplete",{
    	templateUrl:"../../resources/html/signupcomplete.html",
    	controller:"signupcompleteController"
    })
    .when("/linkedinloader",{
    	templateUrl:"../../resources/html/linkedinloader.html",
    })
    .
    otherwise({
        redirect: '/accountsignup'
    });
}]);