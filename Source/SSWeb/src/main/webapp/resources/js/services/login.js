app.service(' loginService', ['$http', function($http) { 
	 this.signup = function (dataToSend) {
         return $http.post('/registeraccount/initiateregistration.do', dataToSend);
     };

}]);