$('#reg-start-individual').click( function() {
    $(this).toggleClass("reg-individual-click");
  } );
$('#reg-start-business').click( function() {
    $(this).toggleClass("reg-business-click");
  } );
function showSigupContent(url) {
	
	closeMoblieScreenMenu();
	saveState(url);
	callAjaxGET(url, showMainContentCallBack, true);
}
var app = angular.module('SocialSurvey',['ngRoute']);
app.config(['$routeProvider', function ($routeProvider) {

    $routeProvider
    .when("/s", {
        templateUrl: "../../resources/html/linkedIn.html",
        controller: "linkedInController"
    })
    .when("/profile", {
        templateUrl: "../../resources/html/profile.html",
        controller: "profileController"
    })
    .when("/company", {
        templateUrl: "../../resources/html/company.html",
        controller: "companyController"
    }).
    otherwise({
        redirect: '/s'
    });
}]);
app.controller('linkedInController',['$http','$location',function($http,$location){
	var vm = this;
    vm.title = 'AngularJS for SocialSurvey';
}]);

/*app.controller('dropZoneCtrl',['$http','$location',function($http,$location){
	app.directive('dropZone', function() {
		  return function(scope, element, attrs) {
		    element.dropzone({ 
		        url: "/upload",
		        maxFilesize: 100,
		        paramName: "uploadfile",
		        maxThumbnailFilesize: 5
		    });
		  };		 
});
}]);*/


angular.module('app', []).controller('dropZoneCtrl',function($scope){
}).directive('dropZone', function() {
  return function(scope, element, attrs) {
    element.dropzone({ 
        url: "/upload",
        maxFilesize: 100,
        paramName: "uploadfile",
        maxThumbnailFilesize: 5
    });
  }
});
