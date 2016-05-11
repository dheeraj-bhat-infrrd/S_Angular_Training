app.service(' loginService', ['$http', function($http) { 
	 this.signup = function (dataToSend) {
         return $http.post('/saveinfo', dataToSend);
     };

}]);