app.service('loginService', [ '$http', function($http) {
	this.signup = function(dataToSend) {
		console.log(dataToSend);
		return $http.post('/registeraccount/initiateregistration.do',JSON.stringify(dataToSend));
	}
}]);
	