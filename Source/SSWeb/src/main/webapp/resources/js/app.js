var phoneFormat = '(ddd) ddd-dddd';

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
        controller: "companydetailController"
    }).
    otherwise({
        redirect: '/accountsignup'
    });
}]);