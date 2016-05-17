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

app.service('CompanyProfileService', [ '$http', function($http) {
	this.getCompanyProfile = function(companyId) {
		return $http.get('/registeraccount/getcompanyprofile.do?companyId='+companyId);
	}
	
	this.updateCompanyProfile = function(companyId, stage, dataToSend) {
		return $http.put('/registeraccount/updatecompanyprofile.do?companyId='+companyId+'&stage='+stage,JSON.stringify(dataToSend));
	}
	
	this.getVerticals = function(){
		return $http.get('/registeraccount/getverticals.do');
	}
}]);

app.service('LinkedinService',['$http',function($http){
	this.linkedin=function(userId){
		var url = $http.post('/registeraccount/agent/initlinkedinconnection.do',userId);
		console.log(url);
		return url;
	}
	
}]);
