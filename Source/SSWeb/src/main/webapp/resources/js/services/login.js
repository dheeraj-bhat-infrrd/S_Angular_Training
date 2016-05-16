app.service('LoginService', [ '$http', function($http) {
	this.signup = function(dataToSend) {
		return $http.post('/registeraccount/initiateregistration.do',JSON.stringify(dataToSend));
	}
}]);

app.service('UserProfileService', [ '$http', function($http) {
	this.getUserProfile = function(userId) {
		return $http.get('/registeraccount/getuserprofile.do?userId='+userId);
	}
	
	this.updateUserProfile = function(userId, stage, dataToSend) {
		return $http.put('/registeraccount/updateuserprofile.do?userId='+userId+'&stage='+stage,JSON.stringify(dataToSend));
	}
}]);
